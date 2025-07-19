import java.io.File;

/* Try one of these options:
 * 
 * 1. Use the MySQL connector jar directly in your project
 * 2. What worked on my end (macOS) is running this through the terminal/cmd
 * Since VSCode does not support running Java files with external libraries directly,
 * you need to compile and run it with the classpath set to include the MySQL connector jar.
 * If your MySQL connector jar is in the same directory as your Java files, you can run:
 * 
 * javac -cp ".:mysql-connector-j-9.3.0.jar" *.java && java -cp ".:mysql-connector-j-9.3.0.jar" Driver
 * OR
 * run.sh (./run.sh for macOS/Linux)
 * 
 * 3. If still not working, or if your .jar file is in the lib folder, try this:
 * 
 * javac -cp ".:mysql-connector-j-9.3.0.jar" *.java
 * java -cp ".:mysql-connector-j-9.3.0.jar" Driver
 */
import java.io.File;
import java.io.IOException;

public class Driver { 
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            new ClothingStoreApp();
        } catch (ClassNotFoundException e) {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String scriptName = isWindows ? "run.bat" : "run.sh";
            
            System.out.println("\nAttempting to run " + scriptName + " automatically...\n");
            
            try {
                ProcessBuilder pb = new ProcessBuilder(scriptName);
                pb.inheritIO(); // Forward output to current console
                Process process = pb.start();
                process.waitFor();
                System.exit(0);
            } catch (IOException | InterruptedException ex) {
                System.out.println("Failed to run script automatically. Please run manually:\n");
                System.out.println(isWindows ? "run.bat" : "./run.sh");
            }
        }
    }
}
