package ce316project.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import ce316project.entities.CompilationResult;
import ce316project.entities.Configuration;
import ce316project.entities.ExecutionResult;
import ce316project.entities.Student;

public class SubmissionsWorker {

    private List<Student> students;
    private Configuration config;

    public SubmissionsWorker(List<Student> students, Configuration config) {
        this.students = students;
        this.config = config;
    }

    public void compileSubmissions()
    {
        boolean isInterpreted = (config.getCompilerCommand() == null || config.getCompilerCommand().isEmpty())
        && (config.getCompilerParameters() == null || config.getCompilerParameters().isEmpty());

        if(isInterpreted) {
            for(Student student : students) {
                student.setCompilationResult(new CompilationResult(
                    true, "",
                     "Interpreted Language - no compilation required", 
                    Duration.ZERO
                ));
            }
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        );

        for(Student student : students) {
            executor.submit(() -> {
                CompilationResult result = compileSubmission(student);
                student.setCompilationResult(result);
            });
        }

        executor.shutdown();
        while(!executor.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private CompilationResult compileSubmission(Student student)
    {
        Path submissionDir = Paths.get(student.getDirectoryPath());
        Instant start = Instant.now();
        boolean success = false;
        String output = "";
        String outputPath = "";

        try {
            List<File> sourceFiles = Files.walk(submissionDir)
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .filter(f -> f.getName().endsWith(config.getSourceExtension()))
                .collect(Collectors.toList());
            
            if(sourceFiles.isEmpty()) {
                return new CompilationResult(false, "No Source File Found", "", Duration.between(start, Instant.now()));
            }

            List<String> command = buildCompilerCommand(student.getStudentId(),submissionDir, sourceFiles);

            Process process = new ProcessBuilder()
                .directory(submissionDir.toFile())
                .command(command)
                .redirectErrorStream(true)
                .start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                outputBuilder.append(line).append("\n");
            }
            output = outputBuilder.toString().trim();

            int exitCode = process.waitFor();
            success = exitCode == 0;

            if(success) {
                outputPath = findOutputFile(submissionDir).toString();
            }

        } catch (IOException | InterruptedException e) {
            output = "Compilation failed: " + e.getMessage();
            success = false; // Status.ERROR
        }

        return new CompilationResult(
            success, 
            outputPath,
            output, 
            Duration.between(start, Instant.now())
        );
    }

    private List<String> buildCompilerCommand(String studentId, Path submissionDir, List<File> sourceFiles)
    {
        List<String> command = new ArrayList<>();

        if(config.getCompilerCommand() != null && !config.getCompilerPath().isEmpty()) {
            command.add(config.getCompilerPath());
        } else if(config.getCompilerCommand() != null && !config.getCompilerCommand().isEmpty()) {
            command.add(config.getCompilerCommand());
        }

        String outputFileName = studentId + "_output";

        for (String param : config.getCompilerParameters()) {
            if (param.equals("{output}")) {
                command.add(outputFileName);
            } 
            else if (param.equals("{sources}")) {
                sourceFiles.forEach(f -> command.add(f.getName()));
            } 
            else {
                command.add(param);
            }
        }
        return command;
    }

    private Path findOutputFile(Path submissionDir) throws IOException {
        return Files.find(submissionDir, 1, (path,attrs) -> path
            .toString()
            .endsWith(config.getExecutableExtension()))
            .findFirst()
            .orElseThrow(() -> new IOException("Output file not found"));
    }

    public void compareSubmissions(String referencePath) // NOT sure writing referencePath as a parameter is a good idea. Mert should check whether is suitable or not.
    {
        //TODO read txt file from referencePath and compare with output of each student.
        StringBuilder result = new StringBuilder();

        try {
            Files.lines(Paths.get(referencePath)).forEach(line -> result.append(line).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Converting Ilker's file to string.
        String fileContent = result.toString();
        System.out.println(fileContent);

        for (Student student : students ) {
            if(student.getStatus() == Status.COMPLETED){
                if(student.getExecutionResult().equals(fileContent)){
                    student.setStatus(Status.PASSED);
                }
                else {
                    student.setStatus(Status.FAILED);
                }
            }
            else {
                //Student Got broken codes so maybe status set to be failed in future.
                //student.setStatus(Status.FAILED);
            }
        }

    }
  
    private ExecutionResult executeSubmission(Student student)
    {
        ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        );

        for (Student student : students) {
            if(student.getCompilationResult() != null && student.getCompilationResult().isSuccess()) {
            executor.submit(() -> {
                ExecutionResult result = executeSubmission(student);
                student.setExecutionResult(result);
            });
            } else {
                ExecutionResult failedResult = new ExecutionResult(
                1, 
                "", 
                "Execution skipped: Compilation failed or not attempted", 
                    Duration.ZERO
                );
                student.setExecutionResult(failedResult);
            }
        }

        executor.shutdown();
        
        try {
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private ExecutionResult executeSubmission(Student student)
    {
        Instant start = Instant.now();
        int exitCode = 1;
        String stdOut = "";
        String stdError = "";
        Duration executionDuration;

        try {
            CompilationResult compResult = student.getCompilationResult();
            if (compResult == null || !compResult.isSuccess()) {
                throw new IOException("Compilation failed or not attempted");
            }
            
            Path executablePath = Paths.get(student.getDirectoryPath());
            List<String> command = buildExecutionCommand(student.getStudentId(), executablePath);

            Process process = new ProcessBuilder()
                .directory(Paths.get(student.getDirectoryPath()).toFile())
                .command(command)
                .redirectErrorStream(false)
                .start();
            
            BufferedReader stdOutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdErrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            StringBuilder stdOutBuilder = new StringBuilder();
            StringBuilder stdErrBuilder = new StringBuilder();
            
            Thread outThread = new Thread(() -> {
                try {
                    String line;
                    while((line = stdOutReader.readLine()) != null) {
                        stdOutBuilder.append(line).append("\n");
                    }
                } catch (IOException e) {
                    stdErrBuilder.append("stdout read error: ").append(e.getMessage());
                }
            });

            Thread errThread = new Thread(() -> {
                try {
                    String line;
                    while((line = stdErrReader.readLine()) != null) {
                        stdErrBuilder.append(line).append("\n");
                    }
                } catch (IOException e) {
                    stdErrBuilder.append("stdout read error: ").append(e.getMessage());
                }
            });

            outThread.start();
            errThread.start();

            exitCode = process.waitFor();

            outThread.join();
            errThread.join();

            stdOutReader.close();
            stdErrReader.close();
            process.getInputStream().close();
            process.getErrorStream().close();
            process.getOutputStream().close();

            stdOut = stdOutBuilder.toString().trim();
            stdError = stdErrBuilder.toString().trim();
             
        } catch (IOException | InterruptedException e) {
            stdError = "Execution failed: "+ e.getMessage();
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        } finally {
            executionDuration = Duration.between(start, Instant.now());
        }

        return new ExecutionResult(exitCode, stdOut, stdError, executionDuration);
    }

    private List<String> buildExecutionCommand(String studentId, Path compileOutputDir) throws IOException {
        boolean isInterpreted = (config.getCompilerCommand() == null || config.getCompilerCommand().isEmpty()) 
            && (config.getCompilerParameters() == null || config.getCompilerParameters().isEmpty());
        
        if(isInterpreted) {

            Path sourceFile = findSourceFile(compileOutputDir);
            List<String> command = new ArrayList<>();

            command.add(config.getCompilerPath());

            if(!config.getRunParameters().isEmpty()) {
                command.addAll(config.getRunParameters());
            }

            command.add(sourceFile.toString());
            return command;
        }

        String executableFileName = studentId + "_output" + config.getExecutableExtension();
        Path executablePath = compileOutputDir.resolve(executableFileName);

        if(!Files.exists(executablePath)) {
            throw new IOException("Compile output not found: " + executablePath);
        }

        String outputBase = executableFileName.replace(config.getExecutableExtension(), "");
        List<String> command = new ArrayList<>();

        if(config.getRunParameters().isEmpty()) {
            command.add(executablePath.toString());
        } else {
            for(String param : config.getRunParameters()) {
                String processedParam = param
                    .replace("{Output}", outputBase) // If param equals to target it replace else it directly pass it.
                    .replace("{OutputFull}", executablePath.toString());
                command.add(processedParam);
            }
        }

        if (command.isEmpty()) {
            command.add(executablePath.toString());
        }

        return command;
    }

    private Path findSourceFile(Path submissionDir) throws IOException {
    return Files.find(submissionDir, 1, 
        (path, attrs) -> path.toString().endsWith(config.getSourceExtension())
    ).findFirst().orElseThrow(() -> 
        new IOException("No source file found with extension: " + config.getSourceExtension())
    );
}

    
}
