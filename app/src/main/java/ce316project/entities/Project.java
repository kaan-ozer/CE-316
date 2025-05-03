package ce316project.entities;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ce316project.utils.SubmissionsWorker;
import ce316project.utils.ZipExtractor;

public class Project {
    private String projectName;
    private Configuration config;
    private List<Student> students;
    private Report report;
    private String submissionsPath;     // NEW
    private String expectedOutputPath;   // NEW
    private Date creationDate;

    // no-arg ctor for Genson
    public Project() {
    }

    // full‐args ctor
    public Project(String projectName,
                   Configuration config,
                   List<Student> students,
                   Report report,
                   String submissionsPath,
                   String expectedOutputPath) {
        this.projectName = projectName;
        this.config = config;
        this.students = students;
        this.report = report;
        this.submissionsPath = submissionsPath;
        this.expectedOutputPath = expectedOutputPath;
    }





    public void prepareSubmissions(String submissionsDirectory)
    {
        ZipExtractor zipExtractor = new ZipExtractor(submissionsDirectory);
        Map<String,Path> studentEntries = zipExtractor.extractZipsConcurrently();

        for(Map.Entry<String,Path> entry : studentEntries.entrySet())
        {
            Student student = new Student(
                    entry.getKey(),
                    entry.getValue().toString()
            );
            students.add(student);
        }
    }

    public void compileSubmissions()
    {
        SubmissionsWorker submissionsWorker = new SubmissionsWorker(students, config);
        submissionsWorker.compileSubmissions();

    }

    public void runSubmissions(String expectedOutputPath) {
        SubmissionsWorker worker = new SubmissionsWorker(students, config);
        worker.executeSubmissions();
        worker.compareSubmissions(expectedOutputPath);
    }


    /**
     * If you ever want to do compare separately:
     */
    public void compareSubmissions(String expectedOutputPath) {
        SubmissionsWorker worker = new SubmissionsWorker(students, config);
        worker.compareSubmissions(expectedOutputPath);
    }


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getSubmissionsPath()    { return submissionsPath; }
    public void   setSubmissionsPath(String p)    { this.submissionsPath = p; }
    public String getExpectedOutputPath() { return expectedOutputPath; }
    public void   setExpectedOutputPath(String p) { this.expectedOutputPath = p; }

}
