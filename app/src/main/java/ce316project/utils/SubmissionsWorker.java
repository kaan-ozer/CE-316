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
