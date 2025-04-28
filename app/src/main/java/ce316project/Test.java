package ce316project;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ce316project.entities.Configuration;
import ce316project.entities.Student;
import ce316project.utils.SubmissionsWorker;
import ce316project.utils.ZipExtractor;

public class Test {

    public static void main(String[] args) {

        List<Student> students = new ArrayList<>();
        ZipExtractor zipExtractor = new ZipExtractor("C:\\Users\\Mert\\Desktop\\TestProjectCE316");
        Map<String,Path> studentEntries = zipExtractor.extractZipsConcurrently();

        for(Map.Entry<String,Path> entry : studentEntries.entrySet())
        {
            System.out.println("id"+entry.getKey());
            System.out.println("path"+entry.getValue());
            Student student = new Student(
                entry.getKey(), 
                entry.getValue().toString()
            );
            students.add(student);
        }

        Configuration config = new Configuration(
            "C Config",
            ".c",
            "C",
            "",
            List.of("-o","{output}","{sources}"),
            "./output",
            List.of(),
            "C:\\TDM-GCC-64\\bin\\gcc.exe"
        );

        SubmissionsWorker submissionsWorker = new SubmissionsWorker(students, config);
        submissionsWorker.compileSubmissions();

        System.out.println(students.get(0).getCompilationResult().getCompilerOutput());
        
        


    }
    
}
