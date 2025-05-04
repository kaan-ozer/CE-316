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
        boolean isInterpreted = (config.getCompilerParameters() == null || config.getCompilerParameters().isEmpty())
        && (config.getCompilerParameters() == null || config.getCompilerParameters().isEmpty());

        if(isInterpreted) {
            for(Student student : students) {
                student.setCompilationResult(new CompilationResult(
                    true, "",
                     "Interpreted Language - no compilation required", 
                    Duration.ZERO
                ));
                student.setStatus(Status.READY);
            }
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        );

        for(Student student : students) {
            executor.submit(() -> {
                CompilationResult result = compileSubmission(student);
                synchronized (student) {
                    student.setCompilationResult(result);
                    if (result.isSuccess()) {
                        student.setStatus(Status.READY);
                    } else {
                        student.setStatus(Status.ERROR);
                    }
                }
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

        synchronized (student) {
            student.setStatus(Status.COMPILING);
        }

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
            success = false;
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

        if(config.getCompilerPath() != null && !config.getCompilerPath().isEmpty() && !config.getCompilerPath().equals("")) {
            command.set(0,config.getCompilerPath());
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

    public void compareSubmissions(String expectedOutputPath) {
        Path expectedFilePath = Paths.get(expectedOutputPath);

        if (!Files.exists(expectedFilePath) || Files.isDirectory(expectedFilePath)) {
            return;
        }

        String expectedOutput;
        try {
            expectedOutput = Files.readString(expectedFilePath).trim(); // remove trailing \n etc.
        } catch (IOException e) {
            return;
        }

        for (Student student : students) {
            synchronized (student) {
                if (student.getExecutionResult() != null) {
                    String actualOutput = student.getExecutionResult().getStdOutput().trim();
                    if (actualOutput.equals(expectedOutput)) {
                        student.setStatus(Status.PASSED);
                    } else {
                        student.setStatus(Status.FAILED);
                    }
                }
            }
        }
    }

  
    public void executeSubmissions()
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
                synchronized (student) {
                    student.setExecutionResult(failedResult);
                    student.setStatus(Status.ERROR);
                }
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

        synchronized (student) {
            student.setStatus(Status.EXECUTING);
        }

        try {
            CompilationResult compResult = student.getCompilationResult();
            if (compResult == null || !compResult.isSuccess()) {
                student.setStatus(Status.ERROR);
                throw new IOException("Compilation failed or not attempted");
            }
            
            Path executablePath = Paths.get(student.getDirectoryPath());
            List<String> command = buildExecutionCommand(executablePath);

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
                    student.setStatus(Status.ERROR);
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
                    student.setStatus(Status.ERROR);
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
            student.setStatus(Status.ERROR);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        } finally {
            executionDuration = Duration.between(start, Instant.now());
        }

        ExecutionResult result = new ExecutionResult(exitCode, stdOut, stdError, executionDuration);
        synchronized (student) {
            student.setExecutionResult(result);
            if(result.getExitCode() == 0) {
                student.setStatus(Status.COMPLETED);
            } else {
                student.setStatus(Status.ERROR);
            }
        }
        return result;
    }

    private List<String> buildExecutionCommand(Path compileOutputDir) throws IOException {
        List<String> command = new ArrayList<>();

        boolean isInterpreted = (config.getCompilerParameters() == null || config.getCompilerParameters().isEmpty());

        if (isInterpreted) {
            Path sourceFile = findSourceFile(compileOutputDir);

            if (!config.getRunParameters().isEmpty()) {
                command.addAll(config.getRunParameters());
            }

            if(config.getCompilerPath() != null && !config.getCompilerPath().isEmpty() && !config.getCompilerPath().equals("")) {
                command.set(0,config.getCompilerPath());
            } 

            command.add(sourceFile.toString());
            return command;
        }

        String ext = config.getExecutableExtension();

        List<Path> candidateFiles = Files.list(compileOutputDir)
                .filter(p -> p.toString().endsWith(ext))
                .collect(Collectors.toList());

        if (candidateFiles.isEmpty()) {
            throw new IOException("No executable file with extension " + ext + " found in " + compileOutputDir);
        }

        Path executablePath = candidateFiles.get(0);

        for (String param : config.getRunParameters()) {
                String processed = param
                        .replace("{Output}", executablePath.getFileName().toString().replace(ext, ""))
                        .replace("{output}", executablePath.getFileName().toString().replace(ext, ""))
                        .replace("{outputfull}", executablePath.toAbsolutePath().toString())
                        .replace("{OutputFull}", executablePath.toAbsolutePath().toString());
                command.add(processed);
        }
        if (command.isEmpty()) {
            command.add(executablePath.toAbsolutePath().toString());
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
