package ce316project.entities;

import java.util.Date;
import java.util.List;

import ce316project.utils.ZipExtractor;

public class Project {

    
    private String projectId;
    private String projectName;
    private Configuration config;
    private List<Student> students;
    private Report report;
    private String referencePath;
    private Date creationDate;
    
    public Project(String projectId, String projectName, Configuration config, List<Student> students, Report report,
            String referencePath) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.config = config;
        this.students = students;
        this.report = report;
        this.referencePath = referencePath;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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

    public void prepareSubmissions(String submissionsDirectory)
    {
        ZipExtractor zipExtractor = new ZipExtractor(submissionsDirectory);
        zipExtractor.extractZipsConcurrently();
        


    }

    public void runSubmissions(String submissionsDirectory)
    {
        // Prepare Submissions
        


        // Compiler Submissions



        // Run Submissions


    }

}
