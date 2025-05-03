package ce316project.utils;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Utility class for loading resources from the application's resources directory.
 */
public class ResourceLoader {
    
    /**
     * Gets a URL to the specified resource.
     * 
     * @param resourcePath The relative path to the resource
     * @return URL to the resource, or null if the resource does not exist
     */
    public static URL getResourceURL(String resourcePath) {
        return ResourceLoader.class.getClassLoader().getResource(resourcePath);
    }
    
    /**
     * Extracts a resource to a temporary file on the file system.
     * This can be useful for resources that need to be accessed as external files.
     * 
     * @param resourcePath The relative path to the resource
     * @return Path to the extracted file, or null if extraction failed
     */
    public static Path extractResource(String resourcePath) {
        URL resourceUrl = getResourceURL(resourcePath);
        if (resourceUrl == null) {
            return null;
        }
        
        try {
            String fileName = Paths.get(resourceUrl.getPath()).getFileName().toString();
            Path tempFile = Files.createTempFile("resource-", "-" + fileName);
            Files.copy(resourceUrl.openStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            tempFile.toFile().deleteOnExit(); // Clean up on JVM exit
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Checks if a resource exists.
     * 
     * @param resourcePath The relative path to the resource
     * @return true if the resource exists, false otherwise
     */
    public static boolean resourceExists(String resourcePath) {
        return getResourceURL(resourcePath) != null;
    }
} 