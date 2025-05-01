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
    private List<Student> students; //TODO: this will be handled later.
    private Report report; //  it is probably ''result outputs'' //TODO: this will be handled later.
    private String referencePath; //  it is probably the reference of the ''expected outputs''
    private Date creationDate;
    
    public Project(String projectId, String projectName, Configuration config, List<Student> students, Report report,
            String referencePath) {
        this.projectName = projectName;
        this.config = config;
        this.students = students;
        this.report = report;
        this.referencePath = referencePath;
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

    public void runSubmissions(String submissionsDirectory)
    {
        SubmissionsWorker submissionsWorker = new SubmissionsWorker(students, config);
        submissionsWorker.executeSubmissions();
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

    public String getReferencePath() {
        return referencePath;
    }

    public void setReferencePath(String referencePath) {
        this.referencePath = referencePath;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}
