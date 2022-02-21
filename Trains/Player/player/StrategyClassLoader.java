package player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A basic class loader for loading single-class .class files
 */
public class StrategyClassLoader extends ClassLoader {

    /**
     * Loads class from the file path
     * @param filePath path to the .class file
     * @return the Class corresponding to the class in the specified file.
     * @throws IOException if there is any issue reading bytes from the file.
     */
    public Class<?> loadClassFromFile(String filePath) throws IOException {
        byte[] fileAsBytes = Files.readAllBytes(Paths.get(filePath));
        return this.defineClass(null, fileAsBytes, 0, fileAsBytes.length);
    }
}
