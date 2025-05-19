package ce316project;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ce316project.entities.Configuration;
import ce316project.entities.Student;
import ce316project.utils.SubmissionsWorker;
import ce316project.utils.ZipExtractor;

public class Test {

    public static void main(String[] args) {

        List<Student> students = new ArrayList<>();
        ZipExtractor zipExtractor = new ZipExtractor("C:\\Users\\Mert\\Desktop\\PythonTestCE316");
        Map<String, Path> studentEntries = zipExtractor.extractZipsConcurrently();

        for (Map.Entry<String, Path> entry : studentEntries.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().toString());
            Student student = new Student(
                    entry.getKey(),
                    entry.getValue().toString()
            );
            students.add(student);
        }

        Configuration config = new Configuration(
                "C Config",
                ".exe",
                "C",
                List.of("gcc", "-o", "{output}", "{sources}"),
                List.of(   ""),
                "",
                ".c"
        );

        Configuration pyConfig = new Configuration(
                "PythonConfig",
                ".py", // No executable extension
                "Python",
                Collections.emptyList(), // No compiler parameters
                List.of("python", "-u"), // Run parameters (unbuffered output)
                "python", // Interpreter path
                ".py" // Source extension
        );

        SubmissionsWorker submissionsWorker = new SubmissionsWorker(students, pyConfig);

        submissionsWorker.compileSubmissions();
        submissionsWorker.executeSubmissions();

        for (int i = 0; i < students.size(); i++) {
            System.out.println(students.get(i).getExecutionResult().getStdOutput());
            System.out.println(students.get(i).getExecutionResult().getStdError());

        }

    }

}
