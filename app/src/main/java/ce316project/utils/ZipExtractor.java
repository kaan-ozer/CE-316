package ce316project.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipExtractor {
    
    private File zipsDirectory;
    private Path outputDir;
    private AtomicInteger submissionCount = new AtomicInteger(0);

    public ZipExtractor(String zipsPath) {
        this.zipsDirectory = new File(zipsPath);
    }

    public int extractZipsConcurrently()
    {
        File[] zipFiles = zipsDirectory.listFiles((dir,name) -> name.endsWith(".zip"));

        if(zipFiles == null || zipFiles.length == 0)
        {
            System.out.println("No zip files found in directory.");
            return 0;
        }

        int threadCount = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (File zipFile : zipFiles) {
            executor.submit(() -> {
                Path outputDir = zipsDirectory.toPath()
                                .resolve("Submissions");

                try {
                    Files.createDirectories(outputDir);
                    extractZip(zipFile, outputDir);
                    submissionCount.incrementAndGet();
                    System.out.println("Extracted: " + zipFile.getName() + " on thread: " + Thread.currentThread().getName());
                } catch (IOException e) {
                    System.err.println("Failed to extract " + zipFile.getName() + ": " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for tasks to complete.");
        }
        return submissionCount.get();
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

    public File getZipsDirectory() {
        return zipsDirectory;
    }

}
