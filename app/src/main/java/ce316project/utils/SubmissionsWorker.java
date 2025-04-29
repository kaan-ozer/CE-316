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
import java.util.Map;
import java.util.HashMap;

import ce316project.entities.CompilationResult;
import ce316project.entities.Configuration;
import ce316project.entities.ExecutionResult;
import ce316project.entities.Student;
import ce316project.utils.Status;

public class SubmissionsWorker {

    private List<Student> students;
    private Configuration config;
    private String referencePath;
    private ExecutionResult executionResult;
    private CompilationResult compilationResult;
    private Status status;

    public SubmissionsWorker(List<Student> students, Configuration config) {
        this.students = students;
        this.config = config;
        this.status = Status.INITIAL;
    }
    
    public SubmissionsWorker(List<Student> students, Configuration config, String referencePath) {
        this.students = students;
        this.config = config;
        this.referencePath = referencePath;
        this.status = Status.INITIAL;
    }

    /**
     * Compiles all student submissions using thread pool
     */
    public void compile(String compilerPath) {
        this.status = Status.COMPILING;
        
        ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        );

        for(Student student : students) {
            executor.submit(() -> {
                student.setStatus(Status.COMPILING);
                CompilationResult result = compileSubmission(student, compilerPath);
                student.setCompilationResult(result);
                if (result.isSuccess()) {
                    student.setStatus(Status.READY);
                } else {
                    student.setStatus(Status.ERROR);
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
        
        // Check if all students are either READY or ERROR.
        // To make sure every thread did compiling and ready for execution.
        boolean allCompiled = true;
        for(Student student : students) {
            if (student.getStatus() != Status.READY && student.getStatus() != Status.ERROR) {
                allCompiled = false;
                break;
            }
        }
        
        this.status = allCompiled ? Status.READY : Status.ERROR;
    }

    private CompilationResult compileSubmission(Student student, String compilerPath) {
        Path submissionDir = Paths.get(student.getDirectoryPath());
        Instant start = Instant.now();
        boolean success = false;
        String output = "";
        String outputPath = "";

        try {
            List<File> sourceFiles = Files.walk(submissionDir)
                .filter(path -> path.toString().endsWith(config.getSourceExtension()))
                .map(Path::toFile)
                .collect(Collectors.toList());

            if (sourceFiles.isEmpty()) {
                return new CompilationResult(
                    false,
                    "",
                    "No source files found",
                    Duration.between(start, Instant.now())
                );
            }

            List<String> command = buildCompilerCommand(submissionDir, sourceFiles, compilerPath);
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(submissionDir.toFile());
            pb.redirectErrorStream(true);

            Process process = pb.start();
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                output = reader.lines().collect(Collectors.joining("\n"));
            }

            int exitCode = process.waitFor();
            success = exitCode == 0;
            
            if (success) {
                Path outputFile = findOutputFile(submissionDir);
                outputPath = outputFile.toString();
            }
        } catch (Exception e) {
            output = "Compilation error: " + e.getMessage();
        }

        return new CompilationResult(
            success, 
            outputPath,
            output, 
            Duration.between(start, Instant.now())
        );
    }

    private List<String> buildCompilerCommand(Path submissionDir, List<File> sourceFiles, String compilerPath) {
        List<String> command = new ArrayList<>();
        command.add(compilerPath != null ? compilerPath : config.getCompilerPath());

        if(config.getCompilerCommand() != null && !config.getCompilerCommand().isEmpty()) {
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
            .orElseThrow(() -> new IOException("Output file not found"));
    }

    /**
     * Executes all successfully compiled student submissions
     */
    public void execute(String executionPath) {
        this.status = Status.EXECUTING;
        
        ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors()
        );

        for(Student student : students) {
            executor.submit(() -> {
                if (student.getStatus() == Status.READY) {
                    student.setStatus(Status.EXECUTING);
                    ExecutionResult result = executeSubmission(student, executionPath);
                    student.setExecutionResult(result);
                    
                    if (result.getExitCode() == 0) {
                        student.setStatus(Status.COMPLETED);
                        
                        // Compare with reference if available
                        if (referencePath != null && !referencePath.isEmpty()) {
                            compareWithReference(student, referencePath);
                        }
                    } else {
                        student.setStatus(Status.ERROR);
                        student.setSubmissionPassed(false);
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
        
        // Check if all students are either COMPLETED or ERROR
        boolean allExecuted = true;
        for(Student student : students) {
            if (student.getStatus() != Status.COMPLETED && student.getStatus() != Status.ERROR) {
                allExecuted = false;
                break;
            }
        }
        
        this.status = allExecuted ? Status.COMPLETED : Status.ERROR;
    }
    
    private ExecutionResult executeSubmission(Student student, String executionPath) {
        Instant start = Instant.now();
        int exitCode = -1;
        String stdOut = "";
        String stdError = "";
        
        try {
            CompilationResult compilationResult = student.getCompilationResult();
            if (compilationResult == null || !compilationResult.isSuccess()) {
                return new ExecutionResult(
                    -1, 
                    "", 
                    "Cannot execute: compilation failed", 
                    Duration.between(start, Instant.now())
                );
            }
            
            List<String> command = new ArrayList<>();
            if (executionPath != null && !executionPath.isEmpty()) {
                command.add(executionPath);
            } else if (config.getRunCommand() != null && !config.getRunCommand().isEmpty()) {
                command.add(config.getRunCommand());
            }
            command.add(compilationResult.getOutputPath());
            
            if (config.getRunParameters() != null) {
                command.addAll(config.getRunParameters());
            }
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(Paths.get(student.getDirectoryPath()).toFile());
            
            Process process = pb.start();
            
            try (BufferedReader outReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
                 BufferedReader errReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream()))) {
                
                stdOut = outReader.lines().collect(Collectors.joining("\n"));
                stdError = errReader.lines().collect(Collectors.joining("\n"));
            }
            
            exitCode = process.waitFor();
            
        } catch (Exception e) {
            stdError = "Execution error: " + e.getMessage();
        }
        
        return new ExecutionResult(
            exitCode, 
            stdOut, 
            stdError, 
            Duration.between(start, Instant.now())
        );
    }
    
    /**
     * Compares student output with reference answer and sets submission passed status
     */
    private void compareWithReference(Student student, String referencePath) {
        try {
            ExecutionResult executionResult = student.getExecutionResult();
            if (executionResult == null || executionResult.getExitCode() != 0) {
                student.setSubmissionPassed(false);
                return;
            }
            
            String studentOutput = executionResult.getStdOutput().trim();
            Map<String, String> referenceAnswer = loadReferenceAnswer(referencePath);
            
            if (referenceAnswer == null || !referenceAnswer.containsKey("answer")) {
                student.setSubmissionPassed(false);
                return;
            }
            
            String expectedOutput = referenceAnswer.get("answer").trim();
            boolean matched = studentOutput.equals(expectedOutput);
            
            // Set the submission passed status
            student.setSubmissionPassed(matched);
            
        } catch (Exception e) {
            student.setSubmissionPassed(false);
        }
    }
    
    /**
     * Loads reference answer from JSON file
     */
    private Map<String, String> loadReferenceAnswer(String referencePath) {
        try {
            Path path = Paths.get(referencePath);
            String content = Files.readString(path);
            // Basic JSON parsing for the simple format {"answer": "Hello World"}
            content = content.trim();
            if (content.startsWith("{") && content.endsWith("}")) {
                content = content.substring(1, content.length() - 1).trim();
                Map<String, String> result = new HashMap<>();
                String[] parts = content.split(":", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim().replace("\"", "");
                    String value = parts[1].trim().replace("\"", "");
                    if (value.endsWith(",")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    result.put(key, value);
                    return result;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Runs the entire submission process (compile, execute, and compare)
     */
    public void run() {
        // Get the compiler and execution paths from config
        String compilerPath = config.getCompilerPath();
        String executionPath = config.getRunCommand();
        
        // Mark all students as received
        for (Student student : students) {
            student.setStatus(Status.RECEIVED);
        }
        this.status = Status.RECEIVED;
        
        // Compile submissions
        compile(compilerPath);
        
        // If compilation was successful, execute submissions
        if (this.status == Status.READY) {
            execute(executionPath);
        }
    }
    
    // Getter methods for SubmissionsWorker state
    public Status getStatus() {
        return status;
    }
    
    public CompilationResult getCompilationResult() {
        return compilationResult;
    }
    
    public ExecutionResult getExecutionResult() {
        return executionResult;
    }

    /**
     * Backward compatibility method for existing code
     */
    public void compileSubmissions() {
        compile(config.getCompilerPath());
    }
}
