public class Driver {

    /* Try one of these options:
     * 
     * 1. Use the MySQL connector jar directly in your project
     * 2. What worked on my end (macOS) is running this through the terminal/cmd
     * Since VSCode does not support running Java files with external libraries directly,
     * you need to compile and run it with the classpath set to include the MySQL connector jar:
     * 
     * javac -cp ".:mysql-connector-j-9.3.0.jar" *.java && java -cp ".:mysql-connector-j-9.3.0.jar" Driver
     * OR
     * run.sh (./run.sh for macOS/Linux)
     * 
     * 3. If still not working, try this:
     * 
     * javac -cp ".:mysql-connector-j-9.3.0.jar" *.java
     * java -cp ".:mysql-connector-j-9.3.0.jar" Driver
     */
    
    public static void main(String[] args) {
        //new BranchModel();
        //new ProductModel();
        new MainMenuGUI();
    }
}
