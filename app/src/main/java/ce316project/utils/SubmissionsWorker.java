package ce316project.utils;

import java.util.List;

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

    private CompilationResult compileSubmission(Student student)
    {
        boolean success = false;





        return new CompilationResult(success, null, null, null);
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
