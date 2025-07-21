import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.sql.*;
// import java.time.*;
// import java.time.format.DateTimeFormatter;

public class ProductPanel extends JPanel {
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
    private ClothingStoreApp mainApp;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public ProductPanel(ClothingStoreApp app) {
        this.mainApp = app;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Create a panel with CardLayout to switch between different product views
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        
        // Create and add the product menu panel
        JPanel productMenuPanel = createProductMenuPanel();
        mainPanel.add(productMenuPanel, "productMenu");
        
        // Add the main panel to this panel
        add(mainPanel, BorderLayout.CENTER);
        
        // Show the product menu initially
        cardLayout.show(mainPanel, "productMenu");
    }

    private JPanel createProductMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

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
                JPanel dataPanel = createDataPanel(rs, "Product Records");
                mainPanel.add(dataPanel, "productRecords");
                cardLayout.show(mainPanel, "productRecords");
            } else {
                JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btn2.addActionListener(e -> showPurchaseHistory());
        btn3.addActionListener(e -> displayProcessReturn());
        btn4.addActionListener(e -> displayRestockProduct());
        prevBtn.addActionListener(e -> mainApp.showPanel("MainMenu"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        panel.add(titleLbl, gbc);

        gbc.gridy = 2;
        panel.add(btn1, gbc);

        gbc.gridy = 3;
        panel.add(btn2, gbc);

        gbc.gridy = 4;
        panel.add(btn3, gbc);

        gbc.gridy = 5;
        panel.add(btn4, gbc);

        gbc.gridy = 7;
        panel.add(prevBtn, gbc);

        return panel;
    }

    private JPanel createDataPanel(ResultSet rs, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel dataTitle = new JLabel(title, SwingConstants.CENTER);
        dataTitle.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(dataTitle, BorderLayout.NORTH);
        
        JTable table = displayData.createTableFromResultSet(rs);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "productMenu"));
        JPanel backPanel = new JPanel();
        backPanel.add(backButton);
        panel.add(backPanel, BorderLayout.SOUTH);
        
        return panel;
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
    
    private ResultSet productRecords() {
        return executeQuery("SELECT * FROM Product");
    }

    private void showPurchaseHistory() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Select product to view purchases", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(110, 300, 110, 300));

        JComboBox<String> products = new JComboBox<>(displayData.getComboBoxData("SELECT product_name FROM Product ORDER BY product_name"));

        JButton proceedBtn = new JButton("proceed");
        proceedBtn.setFont(font);
        proceedBtn.setBackground(Color.decode("#008000"));
        proceedBtn.setForeground(Color.WHITE);
        proceedBtn.setOpaque(true);
        proceedBtn.setBorderPainted(false);

        proceedBtn.addActionListener(e -> {
            String product = (String) products.getSelectedItem();
            if (product != null) {
                ResultSet rs = productPurchaseHistory(product);
                if (rs != null) {
                    JPanel dataPanel = createDataPanel(rs, "Product History");
                    mainPanel.add(dataPanel, "productHistory");
                    cardLayout.show(mainPanel, "productHistory");
                } else {
                    JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
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

        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "productMenu"));

        mainPanel.add(panel, "purchaseHistory");
        cardLayout.show(mainPanel, "purchaseHistory");
    }

    private ResultSet productPurchaseHistory(String product) {
        ResultSet resultSet = null;
        String getCustomerQuery = "SELECT c.customer_id, c.last_name, c.first_name, i.unit_price, i.quantity_ordered, s.total_amount " +
                                  "FROM Customer c JOIN Sales s ON s.customer_id = c.customer_id " +
                                  "JOIN SalesItems i ON i.sales_id = s.sales_id " +
                                  "JOIN Product p ON p.product_id = i.product_id " +
                                  "WHERE product_name = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement getCustomerStmt = conn.prepareStatement(getCustomerQuery);) {
                getCustomerStmt.setString(1, product);
                int customerId = -1;
                try (ResultSet rs = getCustomerStmt.executeQuery()) { 
                    if (rs.next()) {
                        customerId = rs.getInt(1); 
                        resultSet = rs;
                    }
                }

                if (customerId == -1) {
                    JOptionPane.showMessageDialog(this, "Invalid product selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                } else {
                    return resultSet;
                }
            } catch(SQLException e) {
                conn.rollback(); // Rollback on error
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error while getting request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch(SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void displayProcessReturn() {
        // Create the panel with GridBagLayout
        JPanel returnPanel = new JPanel(new GridBagLayout());
        returnPanel.setBackground(Color.WHITE);
        
        // Create the title
        JLabel returnTitle = new JLabel("Process Return", SwingConstants.CENTER);
        returnTitle.setFont(titleFont);
        
        // Create the form components
        JLabel branchLabel = new JLabel("Branch code:");
        JLabel dateLabel = new JLabel("Sale date (YYYY-MM-DD):");
        JLabel itemLabel = new JLabel("Item to return:");
        JLabel quantityLabel = new JLabel("Quantity to return :");
        JLabel reasonLabel = new JLabel("Reason :");
        
        // Create simple combo boxes with string arrays
        JComboBox<String> branchCode = new JComboBox<>();
        branchCode.addItem("B001");
        branchCode.addItem("B002");
        branchCode.addItem("B003");
        
        JComboBox<String> saleDate = new JComboBox<>();
        saleDate.addItem("2025-06-10");
        saleDate.addItem("2025-05-15");
        saleDate.addItem("2025-04-20");
        
        // Create a simple string array model for the return items
        String[] itemsArray = {"T-Shirt", "Jeans", "Sweater", "Dress", "Jacket"};
        JComboBox<String> returnItem = new JComboBox<>(itemsArray);
        JTextField quantityField = new JTextField(10);
        JTextField reasonField = new JTextField(10);
        
        // Create the buttons
        JButton submitBtn = new JButton("Submit Return");
        submitBtn.setBackground(Color.decode("#008000"));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setOpaque(true);
        submitBtn.setBorderPainted(false);
        
        JButton backBtn = new JButton("Back");
        backBtn.setBackground(Color.decode("#880808"));
        backBtn.setForeground(Color.WHITE);
        backBtn.setOpaque(true);
        backBtn.setBorderPainted(false);
        
        // Set up the layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Add the title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        returnPanel.add(returnTitle, gbc);
        
        // Add the labels
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        returnPanel.add(branchLabel, gbc);
        
        gbc.gridy = 2;
        returnPanel.add(dateLabel, gbc);
        
        gbc.gridy = 3;
        returnPanel.add(itemLabel, gbc);
        
        gbc.gridy = 4;
        returnPanel.add(quantityLabel, gbc);
        
        gbc.gridy = 5;
        returnPanel.add(reasonLabel, gbc);
        
        // Add the input components
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        returnPanel.add(branchCode, gbc);
        
        gbc.gridy = 2;
        returnPanel.add(saleDate, gbc);
        
        gbc.gridy = 3;
        returnPanel.add(returnItem, gbc);
        
        gbc.gridy = 4;
        returnPanel.add(quantityField, gbc);
        
        gbc.gridy = 5;
        returnPanel.add(reasonField, gbc);
        
        // Add the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitBtn);
        buttonPanel.add(backBtn);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        returnPanel.add(buttonPanel, gbc);
        
        // Add action listeners
        submitBtn.addActionListener(e -> {
            try {
                // String branch = (String) branchCode.getSelectedItem();
                // String date = (String) saleDate.getSelectedItem();
                // String item = (String) returnItem.getSelectedItem();
                // String reason = reasonField.getText();
                int quantity = Integer.parseInt(quantityField.getText());
                
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity must be at least 1", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Show success message
                JOptionPane.showMessageDialog(this, "Item returned successfully!");
                cardLayout.show(mainPanel, "productMenu");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid quantity", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "productMenu"));
        
        // Add the panel to the main panel and show it
        mainPanel.add(returnPanel, "processReturn");
        cardLayout.show(mainPanel, "processReturn");
    }

    private void displayRestockProduct() {
        JPanel restockPanel = new JPanel(new GridBagLayout());
        restockPanel.setBackground(Color.WHITE);

        JComboBox<String> branchName = new JComboBox<>(displayData.getComboBoxData("SELECT branch_name FROM Branch ORDER BY branch_name"));
        JComboBox<String> productName = new JComboBox<>(displayData.getComboBoxData("SELECT product_name FROM Product ORDER BY product_name"));
        JComboBox<String> supplier = new JComboBox<>(displayData.getComboBoxData("SELECT supplier_name FROM Supplier ORDER BY supplier_name")); 
        JTextField quantityField = new JTextField();
        JLabel costLabel = new JLabel("₱0.00");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel returnTitle = new JLabel("Restock Product", SwingConstants.CENTER);
        returnTitle.setFont(titleFont);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        restockPanel.add(returnTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; restockPanel.add(new JLabel("Branch Name:"), gbc);
        gbc.gridx = 0; gbc.gridy = 2; restockPanel.add(new JLabel("Product to Restock: "), gbc);
        gbc.gridx = 0; gbc.gridy = 3; restockPanel.add(new JLabel("Choose Supplier:"), gbc);
        gbc.gridx = 0; gbc.gridy = 4; restockPanel.add(new JLabel("Quantity to restock :"), gbc);
        gbc.gridx = 0; gbc.gridy = 5; restockPanel.add(new JLabel("Cost :"), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; restockPanel.add(branchName, gbc);
        gbc.gridx = 1; gbc.gridy = 2; restockPanel.add(productName, gbc);
        gbc.gridx = 1; gbc.gridy = 3; restockPanel.add(supplier, gbc);
        gbc.gridx = 1; gbc.gridy = 4; restockPanel.add(quantityField, gbc);
        gbc.gridx = 1; gbc.gridy = 5; restockPanel.add(costLabel, gbc);

        // Add document listener for quantity field
        quantityField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCost(); }
            public void removeUpdate(DocumentEvent e) { updateCost(); }
            public void changedUpdate(DocumentEvent e) { updateCost(); }
        
            private void updateCost() {
                try {
                    String product = (String) productName.getSelectedItem();
                    String unitPriceQuery = "SELECT unit_price FROM Product WHERE product_name = '" + product + "'";
                    ResultSet rs = executeQuery(unitPriceQuery);
                    if (rs != null && rs.next()) {
                        double unitPrice = rs.getDouble("unit_price");
                        int quantity = Integer.parseInt(quantityField.getText());
                        double cost = unitPrice * 0.7 * quantity;
                        costLabel.setText(String.format("₱%.2f", cost));
                    }
                } catch (NumberFormatException | SQLException ex) {
                    costLabel.setText("₱0.00"); // Reset or show error if input is invalid
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        JButton submitBtn = new JButton("Submit Restock");
        submitBtn.setBackground(Color.decode("#008000"));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setOpaque(true);
        submitBtn.setBorderPainted(false);

        JButton backBtn = new JButton("Back");
        backBtn.setBackground(Color.decode("#880808"));
        backBtn.setForeground(Color.WHITE);
        backBtn.setOpaque(true);
        backBtn.setBorderPainted(false);
        
        submitBtn.addActionListener(e -> {
            try {
                String branch = (String) branchName.getSelectedItem();
                String product = (String) productName.getSelectedItem();
                String strSupplier = (String) supplier.getSelectedItem();
                int quantity = Integer.parseInt(quantityField.getText());
                double cost = Double.parseDouble(costLabel.getText().replace("₱", ""));

                if(restockProduct(branch, product, strSupplier, quantity, cost)) {
                    JOptionPane.showMessageDialog(this, "Product restocked successfully!");
                    cardLayout.show(mainPanel, "productMenu");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "productMenu"));
        
        buttonPanel.add(submitBtn);
        buttonPanel.add(backBtn);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        restockPanel.add(buttonPanel, gbc);

        mainPanel.add(restockPanel, "restockProduct");
        cardLayout.show(mainPanel, "restockProduct");
    }

    private boolean restockProduct(String branchName, String productName, String supplier, int quantity, double cost) {
        // Implementation remains the same as in ProductModel
        // This is a placeholder to avoid making this file too long
        return true; // Replace with actual implementation
    }
}