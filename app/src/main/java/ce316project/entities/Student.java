package ce316project.entities;

public class Student {
    private String studentId;
    private String directoryPath;
    private SubmissionThread submissionThread;

    public Student(String studentId, String directoryPath)
    {
        this.studentId = studentId;
        this.directoryPath = directoryPath;
    }
}
