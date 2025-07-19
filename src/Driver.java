import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.text.View;


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
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Test the connection before starting the app
            try {
                DBConnection.getConnection().close();
                SwingUtilities.invokeLater(() -> new ClothingStore());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Could not connect to database. Please check your settings.\n\nError: " + e.getMessage(),
                    "Database Connection Error", JOptionPane.ERROR_MESSAGE);
                DBConnection.showConfigDialog();
                try {
                    // Try again after reconfiguration
                    DBConnection.getConnection().close();
                    SwingUtilities.invokeLater(() -> new ClothingStore());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, 
                        "Still unable to connect to database. Please restart the application.",
                        "Database Connection Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        } catch (ClassNotFoundException e) {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String scriptName = isWindows ? "run.bat" : "run.sh";
            
            System.out.println("\nAttempting to run " + scriptName + " automatically...\n");
            
            try {
                File script = new File(scriptName);
                if (!script.exists()) {
                    System.out.println("Error: " + scriptName + " not found!");
                    return;
                }
                if (!isWindows) {
                    Runtime.getRuntime().exec("chmod +x " + scriptName).waitFor();
                }
                String command = isWindows ? scriptName : "." + File.separator + scriptName;
                Process process = Runtime.getRuntime().exec(command);
                
                System.exit(0);
            } catch (Exception ex) {
                System.out.println("Failed to run script automatically. Please run manually:\n");
                System.out.println(isWindows ? "run.bat" : "./run.sh");
            }
        }
    }
}
