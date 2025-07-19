import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Properties; 

public class DBConnection {
    private static String url = "jdbc:mysql://localhost:3306/DBclothing";
    private static String username = "root";
    private static String password = "";
    private static boolean configured = false;
    private static final String CONFIG_FILE = "db_config.properties";
    
    static {
        loadConfig();
    }
    
    // Get database connection
    public static Connection getConnection() throws SQLException {
        if (!configured) {
            showConfigDialog();
        }
        return DriverManager.getConnection(url, username, password);
    }
    
    private static void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(configFile)) {
                props.load(in);
                url = props.getProperty("db.url", url);
                username = props.getProperty("db.username", username);
                password = props.getProperty("db.password", password);
                configured = true;
            } catch (IOException e) {
                System.err.println("Error loading configuration: " + e.getMessage());
            }
        }
    }
    
    private static void saveConfig() {
        Properties props = new Properties();
        props.setProperty("db.url", url);
        props.setProperty("db.username", username);
        props.setProperty("db.password", password);
        
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Database Configuration");
            configured = true;
        } catch (IOException e) {
            System.err.println("Error saving configuration: " + e.getMessage());
        }
    }
    
    public static void showConfigDialog() {
        JDialog dialog = new JDialog((Frame)null, "Database Configuration", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(null);
        
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField urlField = new JTextField(url);
        JTextField usernameField = new JTextField(username);
        JPasswordField passwordField = new JPasswordField(password);
        
        formPanel.add(new JLabel("Database URL:"));
        formPanel.add(urlField);
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save & Connect");
        saveButton.addActionListener(e -> {
            url = urlField.getText().trim();
            username = usernameField.getText().trim();
            password = new String(passwordField.getPassword());
            saveConfig();
            dialog.dispose();
        });
        buttonPanel.add(saveButton);
        
        dialog.add(new JLabel("Please enter your database connection details:", JLabel.CENTER), BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    public static boolean isConfigured() {
        return configured;
    }
}