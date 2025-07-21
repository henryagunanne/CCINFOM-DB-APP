import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Date;
import java.util.Vector;

public class RestockPanel extends JPanel {
    private ClothingStoreApp mainApp;
    private JComboBox<String> supplierComboBox;
    private JComboBox<String> productComboBox;
    private JComboBox<String> branchComboBox;
    private JTextField quantityField;
    private JTextField costPriceField;
    private JTable restockTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton submitButton;
    private JButton backButton;
    
    // Store supplier IDs and product IDs
    private Vector<Integer> supplierIds = new Vector<>();
    private Vector<Integer> productIds = new Vector<>();
    
    // Store restock items before submission
    private Vector<RestockItem> restockItems = new Vector<>();
    
    public RestockPanel(ClothingStoreApp app) {
        this.mainApp = app;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        initComponents();
        loadSuppliers();
        loadProducts();
    }
    
    private void initComponents() {
        // Title
        JLabel titleLabel = new JLabel("Restock Products", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Branch selection
        gbc.gridx = 0; 
        gbc.gridy = 0;
        formPanel.add(new JLabel("Branch:"), gbc);
        
        gbc.gridx = 1; 
        gbc.gridy = 0;
        branchComboBox = new JComboBox<>();
        formPanel.add(branchComboBox, gbc);
        
        // Supplier selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Select Supplier:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        supplierComboBox = new JComboBox<>();
        formPanel.add(supplierComboBox, gbc);
        
        // Product selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Select Product:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        productComboBox = new JComboBox<>();
        formPanel.add(productComboBox, gbc);
        
        // Quantity
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Quantity:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        quantityField = new JTextField(10);
        formPanel.add(quantityField, gbc);
        
        // Cost Price
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Cost Price:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        costPriceField = new JTextField(10);
        formPanel.add(costPriceField, gbc);
        
        // Add button
        gbc.gridx = 1;
        gbc.gridy = 5;
        addButton = new JButton("Add to Restock List");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addRestockItem();
            }
        });
        formPanel.add(addButton, gbc);
        
        // Table for restock items
        String[] columnNames = {"Supplier", "Product", "Quantity", "Cost Price"};
        tableModel = new DefaultTableModel(columnNames, 0);
        restockTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(restockTable);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        submitButton = new JButton("Submit Restock");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitRestock();
            }
        });
        
        backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainApp.showPanel("MainMenu");
            }
        });
        
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);

        // Load branches in constructor
        loadBranches();
    }

    private void loadBranches() {
        branchComboBox.removeAllItems();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT branch_name FROM Branch")) {
            while (rs.next()) {
                branchComboBox.addItem(rs.getString("branch_name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading branches: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSuppliers() {
        supplierComboBox.removeAllItems();
        supplierIds.clear();
        
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT supplier_id, supplier_name FROM Supplier ORDER BY supplier_name");
            
            while (rs.next()) {
                int supplierId = rs.getInt("supplier_id");
                String supplierName = rs.getString("supplier_name");
                supplierComboBox.addItem(supplierName);
                supplierIds.add(supplierId);
            }
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading suppliers: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadProducts() {
        productComboBox.removeAllItems();
        productIds.clear();
        
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT product_id, product_name, size, color FROM Product WHERE discontinued = 'FALSE' ORDER BY product_name");
            
            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String productName = rs.getString("product_name");
                String size = rs.getString("size");
                String color = rs.getString("color");
                String displayText = productName + " - " + size + " - " + color;
                
                productComboBox.addItem(displayText);
                productIds.add(productId);
            }
            
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addRestockItem() {
        if (supplierComboBox.getSelectedIndex() == -1 || productComboBox.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select both supplier and product", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            double costPrice = Double.parseDouble(costPriceField.getText().trim());
            
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (costPrice <= 0) {
                JOptionPane.showMessageDialog(this, "Cost price must be greater than zero", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int supplierIndex = supplierComboBox.getSelectedIndex();
            int productIndex = productComboBox.getSelectedIndex();
            
            int supplierId = supplierIds.get(supplierIndex);
            int productId = productIds.get(productIndex);
            String supplierName = supplierComboBox.getSelectedItem().toString();
            String productName = productComboBox.getSelectedItem().toString();
            
            // Create restock item
            RestockItem item = new RestockItem(supplierId, productId, quantity, costPrice);
            restockItems.add(item);
            
            // Add to table
            Vector<Object> row = new Vector<>();
            row.add(supplierName);
            row.add(productName);
            row.add(quantity);
            row.add(costPrice);
            tableModel.addRow(row);
            
            // Clear input fields
            quantityField.setText("");
            costPriceField.setText("");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity and cost price", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void submitRestock() {
        if (restockItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No restock items added", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Get selected branch code
            String branchName = (String) branchComboBox.getSelectedItem();
            String branchCode = getBranchCode(conn, branchName);
            
            try {
                // Get next restock_id
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT MAX(restock_id) FROM Restock");
                int nextRestockId = 6043; // Default starting ID if table is empty
                if (rs.next() && rs.getObject(1) != null) {
                    nextRestockId = rs.getInt(1) + 1;
                }
                
                // Current date
                java.sql.Date currentDate = new java.sql.Date(new Date().getTime());
                
                // Insert restock records
                PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO Restock (restock_id, product_id, supplier_id, quantity_added, cost_price, restock_date) VALUES (?, ?, ?, ?, ?, ?)"
                );
                
                // Update inventory
                PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE Inventory SET quantity = quantity + ? WHERE product_id = ? AND branch_code = ?"
                );
                
                for (RestockItem item : restockItems) {
                    // Insert restock record
                    pstmt.setInt(1, nextRestockId);
                    pstmt.setInt(2, item.productId);
                    pstmt.setInt(3, item.supplierId);
                    pstmt.setInt(4, item.quantity);
                    pstmt.setDouble(5, item.costPrice);
                    pstmt.setDate(6, currentDate);
                    pstmt.executeUpdate();

                    // Update inventory for specific branch
                    updateInventory(conn, branchCode, item.productId, item.quantity);

                    nextRestockId++;
                }
                
                conn.commit();
                JOptionPane.showMessageDialog(this, "Restock completed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                // Clear table and items
                tableModel.setRowCount(0);
                restockItems.clear();
                
                pstmt.close();
                updateStmt.close();
                
            } catch (SQLException e) {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Error during restock: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                conn.setAutoCommit(true);
                conn.close();
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Get branch code from name
    private String getBranchCode(Connection conn, String branchName) throws SQLException {
        String sql = "SELECT branch_code FROM Branch WHERE branch_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, branchName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("branch_code");
            }
            throw new SQLException("Branch not found: " + branchName);
        }
    }

    // Update inventory method
    private void updateInventory(Connection conn, String branchCode, int productId, int quantity) throws SQLException {
        String sql = "UPDATE Inventory SET quantity = quantity + ? " +
                     "WHERE branch_code = ? AND product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setString(2, branchCode);
            stmt.setInt(3, productId);
            int updated = stmt.executeUpdate();
            
            if (updated == 0) {
                // Insert if record doesn't exist
                String insertSql = "INSERT INTO Inventory (branch_code, product_id, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, branchCode);
                    insertStmt.setInt(2, productId);
                    insertStmt.setInt(3, quantity);
                    insertStmt.executeUpdate();
                }
            }
        }
    }
    
    // Inner class to store restock items
    private class RestockItem {
        int supplierId;
        int productId;
        int quantity;
        double costPrice;
        
        public RestockItem(int supplierId, int productId, int quantity, double costPrice) {
            this.supplierId = supplierId;
            this.productId = productId;
            this.quantity = quantity;
            this.costPrice = costPrice;
        }
    }
}
