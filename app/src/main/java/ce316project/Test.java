package ce316project;

import ce316project.utils.ZipExtractor;

public class Test {

    public static void main(String[] args) {

        ZipExtractor zipExtractor = new ZipExtractor("C:\\Users\\Mert\\Desktop\\TestProjectCE316");
        zipExtractor.extractZipsConcurrently();
    }
    
}
