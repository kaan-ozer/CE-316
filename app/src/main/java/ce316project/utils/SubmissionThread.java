package ce316project.utils;

import ce316project.entities.CompilationResult;
import ce316project.entities.ExecutionResult;

public class SubmissionThread {
    private ExecutionResult executionResult;
    private CompilationResult compilationResult;
    private Status status;

    public SubmissionThread(ExecutionResult executionResult, CompilationResult compilationResult, Status status) {
        this.executionResult = executionResult;
        this.compilationResult = compilationResult;
        this.status = status;
    }
    
    /* 
    public CompilationResult compile()
    {
        
    }

    public ExecutionResult execute()
    {
        
    }
    */

}
