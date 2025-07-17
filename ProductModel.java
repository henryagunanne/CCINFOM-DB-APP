// Simplified the imports

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/*import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;*/

public class ProductModel extends JFrame{
    final private String DRIVER = "com.mysql.cj.jdbc.Driver";
    final private String URL = "jdbc:mysql://localhost:3306/DBclothing";
    final private String USERNAME = "root";
    final private String PASSWORD = "AGUnanne1";

    final public String opening = "Product Records Management";
    final public String b1Text = "View Product Records";
    final public String b2Text = "Product Purchase History";
    final public String b3Text = "Process a Return";
    final public String b4Text = "Restock a Product";
    final public String prevText = "Back";

    final Font font = new Font("Arial", Font.PLAIN, 20);
    final Font titleFont = new Font("Arial", Font.BOLD, 25);
    final Dimension buttonSize = new Dimension(350, 50);

    private DisplayData displayData = new DisplayData();

    public ProductModel(){
        super("Product Records");
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL Driver not found!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        // super("Product Records");
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        productMenu();

        setVisible(true);
    }

    private void productMenu(){
        this.getContentPane().removeAll();

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLbl = new JLabel(opening, SwingConstants.CENTER);
        titleLbl.setFont(titleFont);

        JButton btn1 = new JButton(b1Text);
        JButton btn2 = new JButton(b2Text);
        JButton btn3 = new JButton(b3Text);
        JButton btn4 = new JButton(b4Text);

        btn1.setFont(font);
        btn2.setFont(font);
        btn3.setFont(font);
        btn4.setFont(font);

        btn1.setPreferredSize(buttonSize);
        btn2.setPreferredSize(buttonSize);
        btn3.setPreferredSize(buttonSize);
        btn4.setPreferredSize(buttonSize);

        JButton prevBtn = new JButton(prevText);
        prevBtn.setFont(font);
        prevBtn.setBackground(Color.decode("#880808"));
        prevBtn.setForeground(Color.WHITE);
        prevBtn.setOpaque(true);
        prevBtn.setBorderPainted(false);

        btn1.addActionListener(e -> {
            ResultSet rs = productRecords();
            if (rs != null) {
                displayData.displayData(this, rs, "Product Records", ev -> productMenu());
            } else {
                JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btn2.addActionListener(e -> showPurchaseHistory());
        btn3.addActionListener(e -> displayProcessReturn());
        btn4.addActionListener(e -> displayRestockProduct());
        prevBtn.addActionListener(e -> new MainMenuGUI());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        mainPanel.add(titleLbl, gbc);

        gbc.gridy = 2;
        mainPanel.add(btn1, gbc);

        gbc.gridy = 3;
        mainPanel.add(btn2, gbc);

        gbc.gridy = 4;
        mainPanel.add(btn3, gbc);

        gbc.gridy = 5;
        mainPanel.add(btn4, gbc);

        gbc.gridy = 7;
        mainPanel.add(prevBtn, gbc);


        this.getContentPane().add(mainPanel);
        this.revalidate();
        this.repaint();
    }

    private ResultSet executeQuery(String query) {
        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            // JOptionPane.showMessageDialog(this, "DB Connection Failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private ResultSet productRecords(){
        return executeQuery("SELECT * FROM Product");
    }


    private void showPurchaseHistory(){
        JComboBox<String> products = new JComboBox<>(displayData.getComboBoxData("SELECT product_name FROM Product ORDER BY product_name"));

        this.getContentPane().removeAll();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Select product to view purchases", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(110, 300, 110, 300));

        String selectedProduct = (String) products.getSelectedItem();

        JButton proceedBtn = new JButton("proceed");
        proceedBtn.setFont(font);
        proceedBtn.setBackground(Color.decode("#008000"));
        proceedBtn.setForeground(Color.WHITE);
        proceedBtn.setOpaque(true);
        proceedBtn.setBorderPainted(false);

        proceedBtn.addActionListener(e -> {
            if (productPurchaseHistory(selectedProduct) != null || selectedProduct != null) {
                displayData.displayData(this, productPurchaseHistory(selectedProduct), "Product History", ev -> showPurchaseHistory());
            } else {
                JOptionPane.showMessageDialog(this, "Database connection failed", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        
        centerPanel.add(products);
        centerPanel.add(proceedBtn);
        panel.add(centerPanel, BorderLayout.CENTER);
        

        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.setBackground(Color.WHITE);

        JButton backBtn = new JButton(prevText);
        backBtn.setFont(font);
        backBtn.setBackground(Color.decode("#880808"));
        backBtn.setForeground(Color.WHITE);
        backBtn.setOpaque(true);
        backBtn.setBorderPainted(false);
        
        southPanel.add(backBtn);
        panel.add(southPanel, BorderLayout.SOUTH);

        backBtn.addActionListener(e -> productMenu());

        this.getContentPane().add(panel);
        this.revalidate();
        this.repaint();
    }


    private ResultSet productPurchaseHistory(String product){
     
        String getCustomerQuery = "SELECT c.customer_id, c.last_name, c.first_name, i.unit_price, i.quantity_ordered, s.total_amount " +
                                    "FROM Customer c JOIN Sales s ON s.customer_id = c.customer_id " +
                                    "JOIN SalesItems i ON i.sales_id = s.sales_id " +
                                    "JOIN Product p ON p.product_id = i.product_id " +
                                    "WHERE p.product_name = ?";

        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(getCustomerQuery);
            stmt.setString(1, product);
            return stmt.executeQuery();  // caller must handle closing
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void displayProcessReturn(){
        JComboBox<String> branchCode  = new JComboBox<>(displayData.getComboBoxData("SELECT branch_code FROM Branch ORDER BY branch_code"));
        JComboBox<String> saleDate  = new JComboBox<>(displayData.getComboBoxData("SELECT sale_date FROM Sales ORDER BY sale_date ASC"));
        JComboBox<String> customerName = new JComboBox<>();
        JLabel customerId = new JLabel(" ");
        JComboBox<String> returnItem = new JComboBox<>();
    
        // Initial population
        updateCustomerNameCombo(customerName, (String) saleDate.getSelectedItem(), (String) branchCode.getSelectedItem());

        String initialName = (String) customerName.getSelectedItem();
        updateCustomerIdLabel(customerId, initialName);

        updateReturnItemsCombo(returnItem, (String) saleDate.getSelectedItem(), (String) branchCode.getSelectedItem(), customerId.getText());


        // Update customerName and returnItem combos when date or branch is changed
        ActionListener updateListener = e -> {
            String selectedDate = (String) saleDate.getSelectedItem();
            String selectedBranch = (String) branchCode.getSelectedItem();
            updateCustomerNameCombo(customerName,selectedDate, selectedBranch);

            String updatedName = (String) customerName.getSelectedItem();
            updateCustomerIdLabel(customerId, updatedName);
        
            updateReturnItemsCombo(returnItem, selectedDate, selectedBranch, customerId.getText());
        };

        saleDate.addActionListener(updateListener);
        branchCode.addActionListener(updateListener);

        // Update customerId when customerName changes
        customerName.addActionListener(e -> {
            String name = (String) customerName.getSelectedItem();
            updateCustomerIdLabel(customerId, name);
            updateReturnItemsCombo(returnItem, (String) saleDate.getSelectedItem(), (String) branchCode.getSelectedItem(), customerId.getText());
        });


        JTextField quantityField = new JTextField();
        JTextField reasonField = new JTextField();

        displayData.showProcessReturn(this, branchCode, saleDate, customerName, customerId, returnItem, quantityField, reasonField, 
            e -> { 
                try {
                    String strBranchCode = (String) branchCode.getSelectedItem();
                    String strSaleDate = (String) saleDate.getSelectedItem();
                    String strReturnItem = (String) returnItem.getSelectedItem();
                    String reason = (String) reasonField.getText();
                    int qty = Integer.parseInt(quantityField.getText());

                    if(strSaleDate == null){
                        JOptionPane.showMessageDialog(this, "Missing selections", "Notice", JOptionPane.WARNING_MESSAGE);
                    }

                    if(qty <= 0){
                        JOptionPane.showMessageDialog(this, "Quantity has to be atleast 1", "Error", JOptionPane.ERROR_MESSAGE);
                        throw new NumberFormatException();
                    }

                    if(processReturn(strBranchCode, strSaleDate, strReturnItem, reason, qty)){
                        JOptionPane.showMessageDialog(this, "Item returned successfully!");
                        productMenu();
                    }
                }catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid quantity value");
                }
                
            }, e -> productMenu());
    }

    // Helper method for return items comboBox update on user selection
    private void updateCustomerNameCombo(JComboBox<String> customerName, String saleDate, String branchCode){
        if (saleDate != null && branchCode != null) {
            String customerNameQuery = "SELECT CONCAT(c.last_name, ' ', c.first_name) AS CustomerName " +
                                        "FROM Customer c JOIN Sales s ON s.customer_id = c.customer_id " +
                                        "WHERE s.sale_date = '" + saleDate + "' AND s.branch_code = '" + branchCode + "'";

            String[] customers = displayData.getComboBoxData(customerNameQuery);
            customerName.setModel(new DefaultComboBoxModel<>(customers));
        }else { 
            customerName.setModel(new DefaultComboBoxModel<>());
        }
    }


    // Helper method to update customer ID label
    private void updateCustomerIdLabel(JLabel customerIdLabel, String fullName) {
        if (fullName != null && !fullName.isEmpty()) {
            try (ResultSet rs = executeQuery("SELECT customer_id FROM Customer WHERE CONCAT(first_name, ' ', last_name) = '" + fullName + "'")) {
                if (rs.next()) {
                    customerIdLabel.setText(rs.getString("customer_id"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            customerIdLabel.setText("");
        }
    }

    // Helper method for return items comboBox update on user selection
    private void updateReturnItemsCombo(JComboBox<String> returnItem, String saleDate, String branchCode, String customerId) {
        if (saleDate != null && branchCode != null) {
            String query = "SELECT DISTINCT p.product_name FROM Product p " +
                           "JOIN SalesItems i ON i.product_id = p.product_id " +
                           "JOIN Sales s ON s.sales_id = i.sales_id " +
                           "WHERE s.sale_date = '" + saleDate + "' AND s.branch_code = '" + 
                           branchCode + "' AND s.customer_id = '" + customerId + "'";

            String[] items = displayData.getComboBoxData(query);
            returnItem.setModel(new DefaultComboBoxModel<>(items));
        } else {
            returnItem.setModel(new DefaultComboBoxModel<>());
        }
    }
    

    private boolean processReturn(String branchCode, String saleDate, String productName, String reason, int quantity){
        String getSaleIdQuery = "SELECT s.sales_id FROM Sales s JOIN SalesItems i ON i.sales_id = s.sales_id WHERE sale_date = ? AND product_id = ? AND branch_code = ?";
        String getProductIdQuery = "SELECT product_id FROM Product WHERE product_name = ?";
        String checkQuantityQuery = "SELECT quantity_ordered FROM SalesItems WHERE sales_id = ? AND product_id = ?";
        String updateSalesQuery = "UPDATE Sales SET total_amount = total_amount - ? WHERE sales_id = ? AND sale_date = ? and branch_code = ?";
        String deleteSalesQuery = "DELETE s FROM Sales s JOIN SalesItems i ON i.sales_id = s.sales_id WHERE s.sales_id = ? AND s.sale_date = ? AND i.product_id = ?";
        String updateSalesItemsQuery = "UPDATE SalesItems SET quantity_ordered = quantity_ordered - ? WHERE sales_id = ? AND product_id = ?";
        String deleteSalesItemsQuery = "DELETE FROM SalesItems WHERE sales_id = ? AND product_id = ?";
        String updateReturnsQuery = "INSERT INTO Returns VALUES(?, ?, ?)";
        String updateReturnItemsQuery = "INSERT INTO ReturnItems VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE quantity_returned = quantity_returned + ?";


        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            conn.setAutoCommit(false); // Start transaction
            
            try (PreparedStatement getSaleIdStmt = conn.prepareStatement(getSaleIdQuery);
                 PreparedStatement getProductIdStmt = conn.prepareStatement(getProductIdQuery);) {

                getProductIdStmt.setString(1, productName);
                int productId = -1;
                try (ResultSet rs = getProductIdStmt.executeQuery()) { 
                    if (rs.next()) {
                        productId = rs.getInt("product_id");
                    }
                }

                getSaleIdStmt.setString(1, saleDate);
                getSaleIdStmt.setInt(2, productId);
                getSaleIdStmt.setString(3, branchCode);
                int saleId = -1;
                try (ResultSet rs = getSaleIdStmt.executeQuery()) {
                    if (rs.next()){
                        saleId = rs.getInt("sales_id");
                    }
                }

                if (saleId == -1 || productId == -1) {
                    JOptionPane.showMessageDialog(this, "Invalid date or product selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                // check quantity to be returned
                int quantity_ordered;
                try (PreparedStatement checkQuantityStmt = conn.prepareStatement(checkQuantityQuery)) {

                    checkQuantityStmt.setInt(1, saleId);
                    checkQuantityStmt.setInt(2, productId);
                    try (ResultSet rs = checkQuantityStmt.executeQuery()) {
                        if (!rs.next()) {
                            JOptionPane.showMessageDialog(this, "Sale item not found.", "Error", JOptionPane.ERROR_MESSAGE);
                            conn.rollback();
                            return false;
                        }
                        quantity_ordered = rs.getInt("quantity_ordered");
                    
                        if (quantity_ordered < quantity) {
                            JOptionPane.showMessageDialog(this, "You cannot return more than the ordered quantity of " + quantity_ordered, "Error", JOptionPane.ERROR_MESSAGE);
                            conn.rollback();
                            return false;
                        }
                    }
                }


                // update tables
                try (PreparedStatement updateSalesStmt = conn.prepareStatement(updateSalesQuery);
                     PreparedStatement deleteSalesStmt = conn.prepareStatement(deleteSalesQuery);
                     PreparedStatement updateSalesItemsStmt = conn.prepareStatement(updateSalesItemsQuery);
                     PreparedStatement deleteSalesItemsStmt = conn.prepareStatement(deleteSalesItemsQuery);
                     PreparedStatement updateReturnsStmt = conn.prepareStatement(updateReturnsQuery);
                     PreparedStatement updateReturnItemsStmt = conn.prepareStatement(updateReturnItemsQuery)) {

                    String unitPrice = "Select unit_price FROM SalesItems WHERE sales_id = '" + saleId + "'" + " AND product_id = '" + productId + "'";
                    String strReturnId = "SELECT * FROM Returns ORDER BY return_id DESC LIMIT 1;";
                    String strReturnItemId = "SELECT * FROM ReturnItems ORDER BY return_item_id DESC LIMIT 1;";

                    LocalDate today = LocalDate.now();
                    String dateStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    double returnedPrice = -1;
                    int returnId = -1;
                    int returnItemId = -1;
            
                    try(ResultSet rsPrice = executeQuery(unitPrice);
                        ResultSet rsReturnId = executeQuery(strReturnId);
                        ResultSet rsReturnItemId = executeQuery(strReturnItemId)){
                        
                        if (rsPrice.next() && rsReturnId.next() && rsReturnItemId.next()) {
                            returnedPrice = rsPrice.getDouble("unit_price") * quantity;
                            returnId = rsReturnId.getInt("return_id") + 1;
                            returnItemId = rsReturnItemId.getInt("return_item_id") + 1;
                        } 

                        if (quantity < quantity_ordered && quantity > 0){

                            updateSalesStmt.setDouble(1, returnedPrice);
                            updateSalesStmt.setInt(2, saleId);
                            updateSalesStmt.setString(3, saleDate);
                            updateSalesStmt.setString(4, branchCode);
                            updateSalesStmt.executeUpdate();

                            updateSalesItemsStmt.setInt(1, quantity);
                            updateSalesItemsStmt.setInt(2, saleId);
                            updateSalesItemsStmt.setInt(3, productId);
                            updateSalesItemsStmt.executeUpdate();
                    
                            updateReturnsStmt.setInt(1, returnId);
                            updateReturnsStmt.setString(2, dateStr);
                            updateReturnsStmt.setString(3, reason);
                            updateReturnsStmt.executeUpdate();
                            
                            updateReturnItemsStmt.setInt(1, returnItemId);
                            updateReturnItemsStmt.setInt(2, returnId);
                            updateReturnItemsStmt.setInt(3, productId);
                            updateReturnItemsStmt.setInt(4, quantity);
                            updateReturnItemsStmt.setInt(5, quantity);
                            updateReturnItemsStmt.executeUpdate();
                            
                            conn.commit(); // Commit transaction
                            return true;

                        }else if (quantity_ordered == quantity){
                            deleteSalesStmt.setInt(1, saleId);
                            deleteSalesStmt.setString(2, saleDate);
                            deleteSalesStmt.setInt(3, productId);
                            deleteSalesStmt.executeUpdate();
    
                            deleteSalesItemsStmt.setInt(1, saleId);
                            deleteSalesItemsStmt.setInt(2, productId);
                            deleteSalesItemsStmt.executeUpdate();

                            updateReturnsStmt.setInt(1, returnId + 1);
                            updateReturnsStmt.setString(2, dateStr);
                            updateReturnsStmt.setString(3, reason);
                            updateReturnsStmt.executeUpdate();
                            
                            updateReturnItemsStmt.setInt(1, returnItemId + 1);
                            updateReturnItemsStmt.setInt(2, returnId);
                            updateReturnItemsStmt.setInt(3, productId);
                            updateReturnItemsStmt.setInt(4, quantity);
                            updateReturnItemsStmt.setInt(5, quantity);
                            updateReturnItemsStmt.executeUpdate();

                            conn.commit(); // Commit transaction
                            return true;
                        }
                    }
                }
            }catch (SQLException e) {
                conn.rollback(); // Rollback on error
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred while processing return: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    private void displayRestockProduct(){

        JComboBox<String> branchName = new JComboBox<>(displayData.getComboBoxData("SELECT branch_name FROM Branch ORDER BY branch_name"));
        JComboBox<String> productName = new JComboBox<>(displayData.getComboBoxData("SELECT product_name FROM Product ORDER BY product_name"));
        JComboBox<String> supplier = new JComboBox<>(displayData.getComboBoxData("SELECT supplier_name FROM Supplier ORDER BY supplier_name")); 
        JTextField quantityField = new JTextField();
        JLabel costLabel = new JLabel("₱0.00");  
        
        
        quantityField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCost(); }
            public void removeUpdate(DocumentEvent e) { updateCost(); }
            public void changedUpdate(DocumentEvent e) { updateCost(); }
        
            private void updateCost() {
                try {
                    String product = (String) productName.getSelectedItem();
                    String unitPriceQuery = "SELECT unit_price FROM Product WHERE product_name = '" + product + "'";
                    
                    double unitPrice = 0;
                    try (ResultSet rs = executeQuery(unitPriceQuery)) {
                        if (rs.next()) {
                            unitPrice = rs.getDouble("unit_price");
                        }
                    }

                    int quantity = Integer.parseInt(quantityField.getText());
                    double cost = unitPrice * 0.7 * quantity;
                    costLabel.setText(String.format("₱%.2f", cost));
                } catch (NumberFormatException ex) {
                    costLabel.setText("₱0.00"); // Reset or show error if input is invalid
                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        productName.addActionListener(e -> {
            // Force the cost to update when product changes
            quantityField.postActionEvent();
        });

        displayData.showRestockProducts(this, branchName, productName, supplier, quantityField, costLabel, e -> {
                
                try { 
                    
                    String branch = (String) branchName.getSelectedItem();
                    String product = (String) productName.getSelectedItem();
                    String strSupplier = (String) supplier.getSelectedItem();
                    int quantity = Integer.parseInt(quantityField.getText());

                    String strCost= costLabel.getText();
                    double cost = Double.parseDouble(strCost.substring(1));

                    if(restockProduct(branch, product, strSupplier, quantity, cost)){
                        JOptionPane.showMessageDialog(this, "Product restocked successfully!");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid positive quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                    productMenu();
                }
            }, e -> productMenu());
    }


    private boolean restockProduct(String branchName, String productName, String supplier, int quantity, double cost){
        String getBranchIdQuery = "SELECT branch_code FROM branch WHERE branch_name = ?";
        String getProductIdQuery = "SELECT product_id FROM product WHERE product_name = ?";
        String getSupplierIdQuery = "SELECT supplier_id FROM Supplier WHERE supplier_name = ?";
        String updateInventoryQuery = "UPDATE Inventory SET quantity = quantity + ? WHERE branch_code = ? AND product_id = ?";
        String updateRestockQuery = "INSERT INTO Restock VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement getBranchIdStmt = conn.prepareStatement(getBranchIdQuery);
                 PreparedStatement getProductIdStmt = conn.prepareStatement(getProductIdQuery);
                 PreparedStatement getSupplierIdStmt = conn.prepareStatement(getSupplierIdQuery)) {

                getBranchIdStmt.setString(1, branchName);
                String branchId = null;
                try (ResultSet rs = getBranchIdStmt.executeQuery()) {
                    if (rs.next()){
                        branchId = rs.getString(1);
                    }
                }

                getProductIdStmt.setString(1, productName);
                int productId = -1;
                try (ResultSet rs = getProductIdStmt.executeQuery()) {
                    if (rs.next()){
                        productId = rs.getInt(1);
                    }
                }

                getSupplierIdStmt.setString(1, supplier);
                int supplierId = -1;
                try (ResultSet rs = getSupplierIdStmt.executeQuery()) {
                    if (rs.next()){
                        supplierId = rs.getInt(1);
                    }
                }

                if(branchId == null || productId == -1 || supplierId == -1){
                    JOptionPane.showMessageDialog(this, "Invalid branch, product, or supplier selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                
                try(PreparedStatement updateInventoryStmt = conn.prepareStatement(updateInventoryQuery);
                    PreparedStatement updateRestockStmt = conn.prepareStatement(updateRestockQuery)){

                    String strRestockId = "SELECT * FROM Restock ORDER BY restock_id DESC LIMIT 1;";
                     
                    int restockId = -1;
                    try(ResultSet rs = executeQuery(strRestockId)){
                        if(rs.next()){
                            restockId = rs.getInt("restock_id") + 1;
                        }
                    }

                    LocalDate today = LocalDate.now();
                    String dateStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    updateInventoryStmt.setInt(1, quantity);
                    updateInventoryStmt.setString(2, branchId);
                    updateInventoryStmt.setInt(3, productId);
                    updateInventoryStmt.executeUpdate();

                    updateRestockStmt.setInt(1, restockId);
                    updateRestockStmt.setInt(2, productId);
                    updateRestockStmt.setInt(3, supplierId);
                    updateRestockStmt.setInt(4, quantity);
                    updateRestockStmt.setDouble(5, cost);
                    updateRestockStmt.setString(6, dateStr);
                    updateRestockStmt.executeUpdate();
                    
                    conn.commit(); // Commit transaction
                    return true;
                }

            } catch (SQLException e) {
                conn.rollback(); // Rollback on error
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error occurred during the restock: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
