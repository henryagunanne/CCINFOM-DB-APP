// Simplified the imports

import javax.swing.*;
import java.awt.*;
// import java.awt.event.*;
import java.sql.*;

/*import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;*/

// RUN JAVA BUTTON NOT THE CODE RUNNER IN VSCODE!!!!
public class BranchModel extends JFrame {
    // Change these values to your database credentials if necessary. Credentials are based on my MacOS
    final private String DRIVER = "com.mysql.cj.jdbc.Driver";
    final private String URL = "jdbc:mysql://localhost:3306/DBclothing";
    final private String USERNAME = "root";
    final private String PASSWORD = "imagentumr1@";

    final public String opening = "What do you want to do?";
    final public String b1Text = "Check Branch Records";
    final public String b2Text = "Branch & Active Sales Reps";
    final public String b3Text = "Transfer Inventory";
    final public String prevText = "Back";

    final Font font = new Font("Arial", Font.PLAIN, 20);
    final Dimension buttonSize = new Dimension(350, 50); // width, height

    private JComboBox<String> sourceBranch, destBranch, productBox;
    private JTextField quantityField;
    private DisplayData displayData = new DisplayData();

    public BranchModel() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL Driver not found!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        setTitle("Branch Record");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);
        showMainMenu();
        setVisible(true);
    }

    private void showMainMenu() {
        getContentPane().removeAll();
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(opening, SwingConstants.CENTER);
        titleLabel.setFont(font);

        JButton btn1 = new JButton(b1Text);
        JButton btn2 = new JButton(b2Text);
        JButton btn3 = new JButton(b3Text);

        btn1.setFont(font);
        btn2.setFont(font);
        btn3.setFont(font);

        btn1.setPreferredSize(buttonSize);
        btn2.setPreferredSize(buttonSize);
        btn3.setPreferredSize(buttonSize);

        JButton prevBtn = new JButton(prevText);
        prevBtn.setFont(font);
        prevBtn.setBackground(Color.decode("#880808"));
        prevBtn.setForeground(Color.WHITE);
        prevBtn.setOpaque(true);
        prevBtn.setBorderPainted(false);

        btn1.addActionListener(e -> {
            ResultSet rs = getBranchResultSet();
            if (rs != null) {
                displayData.displayData(this, rs, "Branch Records", ev -> showMainMenu());
            } else {
                JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btn2.addActionListener(e -> {
            ResultSet rs = BranchAndActiveSalesRep();
            if (rs != null) {
                displayData.displayData(this, rs, "Branch & Active Sales Reps", ev -> showMainMenu());
            } else {
                JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btn3.addActionListener(e -> showStockTransfer());
        prevBtn.addActionListener(e -> new MainMenuGUI());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        mainPanel.add(btn1, gbc);
        
        gbc.gridy = 2;
        mainPanel.add(btn2, gbc);
        
        gbc.gridy = 3;
        mainPanel.add(btn3, gbc);

        gbc.gridy = 4;
        mainPanel.add(prevBtn, gbc);

        getContentPane().add(mainPanel);
        revalidate();
        repaint();
    }

    private void showStockTransfer() {
        sourceBranch = new JComboBox<>(displayData.getComboBoxData("SELECT branch_name FROM Branch ORDER BY branch_name"));
        destBranch = new JComboBox<>(displayData.getComboBoxData("SELECT branch_name FROM Branch ORDER BY branch_name"));
        productBox = new JComboBox<>(displayData.getComboBoxData("SELECT product_name FROM Product ORDER BY product_name"));
        quantityField = new JTextField();
        
        displayData.showStockTransfer(this, sourceBranch, destBranch, productBox, quantityField,
            e -> {
                try {

                    String src = (String) sourceBranch.getSelectedItem();
                    String dst = (String) destBranch.getSelectedItem();
                    String product = (String) productBox.getSelectedItem();
                    int qty = Integer.parseInt(quantityField.getText());
                    
                    if (src.equals(dst)) {
                        JOptionPane.showMessageDialog(this, "Source and Destination branches cannot be the same.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    if (qty <= 0) throw new NumberFormatException();

                    if (transferStock(src, dst, product, qty)) {
                        JOptionPane.showMessageDialog(this, "Stock transferred successfully!");
                        showMainMenu();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid positive quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            },
            e -> showMainMenu()
        );
    }

    private ResultSet getBranchResultSet() {
        return executeQuery("SELECT * FROM Branch");
    }

    private ResultSet BranchAndActiveSalesRep() {
        return executeQuery("SELECT b.branch_name AS BranchName, b.location AS Location, " +
                "s.name AS SalesRep, s.active_status AS ActiveStatus, " +
                "b.contact_number AS ContactBranch " +
                "FROM Branch b " +
                "LEFT JOIN SalesRep s ON b.branch_code = s.branch_code AND s.active_status = 'TRUE' " +
                "ORDER BY s.name");
    }

    private ResultSet executeQuery(String query) {
        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    

    private boolean transferStock(String sourceBranchName, String destBranchName, String productName, int quantity) {
        String getBranchIdQuery = "SELECT branch_code FROM Branch WHERE branch_name = ?";
        String getProductIdQuery = "SELECT product_id FROM Product WHERE product_name = ?";
        String checkStockQuery = "SELECT quantity FROM Inventory WHERE branch_code = ? AND product_id = ?";
        String updateSourceQuery = "UPDATE Inventory SET quantity = quantity - ? WHERE branch_code = ? AND product_id = ?";
        String updateDestQuery = "INSERT INTO Inventory (branch_code, product_id, quantity) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE quantity = quantity + ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement getSourceIdStmt = conn.prepareStatement(getBranchIdQuery);
                 PreparedStatement getDestIdStmt = conn.prepareStatement(getBranchIdQuery);
                 PreparedStatement getProdIdStmt = conn.prepareStatement(getProductIdQuery)) {

                getSourceIdStmt.setString(1, sourceBranchName);
                String sourceBranchId = null;
                try (ResultSet rs = getSourceIdStmt.executeQuery()) { if (rs.next()) sourceBranchId = rs.getString(1); }

                getDestIdStmt.setString(1, destBranchName);
                String destBranchId = null;
                try (ResultSet rs = getDestIdStmt.executeQuery()) { if (rs.next()) destBranchId = rs.getString(1); }

                getProdIdStmt.setString(1, productName);
                int productId = -1;
                try (ResultSet rs = getProdIdStmt.executeQuery()) { if (rs.next()) productId = rs.getInt(1); }

                if (sourceBranchId == null || destBranchId == null || productId == -1) {
                    JOptionPane.showMessageDialog(this, "Invalid branch or product selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                // Check stock
                try (PreparedStatement checkStockStmt = conn.prepareStatement(checkStockQuery)) {
                    checkStockStmt.setString(1, sourceBranchId);
                    checkStockStmt.setInt(2, productId);
                    try (ResultSet rs = checkStockStmt.executeQuery()) {
                        if (!rs.next() || rs.getInt("quantity") < quantity) {
                            JOptionPane.showMessageDialog(this, "Insufficient stock in the source branch.", "Error", JOptionPane.ERROR_MESSAGE);
                            conn.rollback();
                            return false;
                        }
                    }
                }

                // Perform update
                try (PreparedStatement updateSourceStmt = conn.prepareStatement(updateSourceQuery);
                     PreparedStatement updateDestStmt = conn.prepareStatement(updateDestQuery)) {

                    updateSourceStmt.setInt(1, quantity);
                    updateSourceStmt.setString(2, sourceBranchId);
                    updateSourceStmt.setInt(3, productId);
                    updateSourceStmt.executeUpdate();

                    updateDestStmt.setString(1, destBranchId);
                    updateDestStmt.setInt(2, productId);
                    updateDestStmt.setInt(3, quantity);
                    updateDestStmt.setInt(4, quantity);
                    updateDestStmt.executeUpdate();

                    conn.commit(); // Commit transaction
                    return true;
                }

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred during the transfer: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
