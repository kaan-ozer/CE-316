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
                .filter(f -> f.getName().endsWith(config.getSourceExtention()))
                .collect(Collectors.toList());
            
            if(sourceFiles.isEmpty()) {
                return new CompilationResult(false, "No Source File Found", "", Duration.between(start, Instant.now()));
            }

            List<String> command = buildCompilerCommand(submissionDir, sourceFiles);

            Process process = new ProcessBuilder()
                .directory(submissionDir.toFile())
                .command(command)
                .redirectErrorStream(true)
                .start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder outpBuilder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                outpBuilder.append(line).append("\n");
            }
            output = outpBuilder.toString().trim();

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

    private List<String> buildCompilerCommand(Path submissionDir, List<File> sourceFiles)
    {
        List<String> command = new ArrayList<>();
        command.add(config.getCompilerPath());

        if(config.getCompilerCommand() != null && !config.getCompilerCommand().isEmpty())
        {
            command.add(config.getCompilerCommand());
        }

        String outputFile = submissionDir.resolve("output" + config.getExecutableExtension()).toString();

        for (String param : config.getCompilerParameters()) {
            if (param.equals("{output}")) {
                command.add(outputFile);
            } 
            else if (param.equals("{sources}")) {
                sourceFiles.forEach(f -> command.add(f.toString()));
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
            .orElseThrow(() -> new IOException("Output file nor found"));
    }

    /* 
    private ExecutionResult executeSubmission(Student student)
    {
        int exitCode;
        String stdOut, stdError;


        return new ExecutionResult(exitCode, stdOut, stdError, null);
    }
    
    public ExecutionResult execute()
    {
        
    }
    */

}
