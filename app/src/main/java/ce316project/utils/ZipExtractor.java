package ce316project.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipExtractor {
    
    private File zipsDirectory;
    private Path outputDir;

    public ZipExtractor(String zipsPath) {
        this.zipsDirectory = new File(zipsPath);
    }

    private void extractZip(File zipFile, Path outputDir) throws IOException
    {
       try(ZipFile zip = new ZipFile(zipFile)) {
        Enumeration<? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path entryPath = outputDir.resolve(entry.getName()).normalize();

                if(!entryPath.startsWith(outputDir)) {
                    throw new IOException("Entry is outside of the target directory: " + entry.getName());
                }

                if(entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (InputStream inputStream = zip.getInputStream(entry)) {
                        Files.copy(inputStream, entryPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
       }
    
    }
}
