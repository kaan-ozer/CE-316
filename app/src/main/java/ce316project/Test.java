package ce316project;

import java.nio.file.Path;
import java.util.Map;

import ce316project.entities.Student;
import ce316project.utils.ZipExtractor;

public class Test {

    public static void main(String[] args) {

        ZipExtractor zipExtractor = new ZipExtractor("C:\\Users\\Mert\\Desktop\\TestProjectCE316");
        Map<String,Path> studentEntries = zipExtractor.extractZipsConcurrently();
        
        for(Map.Entry<String,Path> entry : studentEntries.entrySet())
        {
            System.out.println("id"+entry.getKey());
            System.out.println("path"+entry.getValue());
        }
    }
    
}
