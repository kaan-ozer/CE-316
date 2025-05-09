package ce316project;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.time.Duration;

import org.junit.Test;

import ce316project.entities.CompilationResult;
import ce316project.entities.ExecutionResult;
import ce316project.entities.Student;
import ce316project.utils.Status;

public class StudentTest {

    @Test
    public void testInitialStatus() {
        Student student = new Student("123","/MockPath");
        assertEquals(Status.INITIAL, student.getStatus());
    }

    @Test
    public void testSetCompilationResult() {
        Student student = new Student("123", "/MockPath");
        CompilationResult result = new CompilationResult(true, "out", "log", Duration.ofMillis(100));
        student.setCompilationResult(result);

        assertSame(result, student.getCompilationResult());
    }

    @Test
    public void testSetExecutionResult() {
        Student student = new Student("123", "/MockPath");
        ExecutionResult result = new ExecutionResult(0, "stdOut", "stdErr", Duration.ofMillis(100));
        student.setExecutionResult(result);

        assertSame(result, student.getExecutionResult());
    }

    @Test
    public void testStatusTransation() {
        Student student = new Student("123", "/MockPath");
        student.setStatus(Status.COMPILING);
        assertEquals(Status.COMPILING, student.getStatus());
    }
    
}
