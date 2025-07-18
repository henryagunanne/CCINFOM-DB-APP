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
public class Driver {
    public static void main(String[] args) {
        try {
            // Try to load the MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // If successful, start the application
            new MainMenuGUI();
        } catch (ClassNotFoundException e) {
            // Driver not found, try to run the appropriate script
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String scriptName = isWindows ? "run.bat" : "run.sh";
            
            System.out.println("\nAttempting to run " + scriptName + " automatically...\n");
            
            try {
                // Make sure the script exists
                File script = new File(scriptName);
                if (!script.exists()) {
                    System.out.println("Error: " + scriptName + " not found!");
                    return;
                }
                
                // Make the script executable (for Unix/Mac)
                if (!isWindows) {
                    Runtime.getRuntime().exec("chmod +x " + scriptName).waitFor();
                }
                
                // Run the script
                String command = isWindows ? scriptName : "." + File.separator + scriptName;
                Process process = Runtime.getRuntime().exec(command);
                
                // Exit this JVM as the script will start a new one
                System.exit(0);
            } catch (Exception ex) {
                System.out.println("Failed to run script automatically. Please run manually:\n");
                System.out.println(isWindows ? "run.bat" : "./run.sh");
            }
        }
    }
}
