import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SalesTransactionModel extends JFrame {
    final private String DRIVER = "com.mysql.cj.jdbc.Driver";
    final private String URL = "jdbc:mysql://localhost:3306/DBclothing";
    final private String USERNAME = "root";
    final private String PASSWORD = "imagentumr1@";

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private List<SaleItem> saleItems;
    private double totalAmount;
    private int customerId;
    private int salesRepId;
    private String branchCode;
    private String paymentType;
    
    // UI Components
    private JComboBox<String> customerCombo;
    private JComboBox<String> salesRepCombo;
    private JComboBox<String> branchCombo;
    private JComboBox<String> productCombo;
    private JTextField quantityField;
    private JTable itemsTable;
    private JComboBox<String> paymentCombo;
    private JLabel totalLabel;

    public SalesTransactionModel() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL Driver not found!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        setTitle("New Sale");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        saleItems = new ArrayList<>();
        totalAmount = 0.0;

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // panels for each step
        cardPanel.add(createCustomerPanel(), "CUSTOMER");
        cardPanel.add(createSalesRepBranchPanel(), "SALES_REP_BRANCH");
        cardPanel.add(createProductsPanel(), "PRODUCTS");
        cardPanel.add(createPaymentPanel(), "PAYMENT");
        cardPanel.add(createConfirmationPanel(), "CONFIRMATION");

        add(cardPanel);
        cardLayout.show(cardPanel, "CUSTOMER");
        setVisible(true);
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Customer Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        
        customerCombo = new JComboBox<>(new DisplayData().getComboBoxData(
            "SELECT CONCAT(customer_id, ' - ', first_name, ' ', last_name) FROM customer"
        ));
        customerCombo.setFont(new Font("Arial", Font.PLAIN, 18));
        
        centerPanel.add(new JLabel("Customer:"));
        centerPanel.add(customerCombo);
        centerPanel.add(new JLabel("Is Member?"));
        
        JCheckBox memberCheck = new JCheckBox("Member");
        memberCheck.setFont(new Font("Arial", Font.PLAIN, 18));
        memberCheck.setEnabled(false);
        centerPanel.add(memberCheck);
        
        panel.add(centerPanel, BorderLayout.CENTER);

        // check if member
        customerCombo.addActionListener(e -> {
            String selected = (String) customerCombo.getSelectedItem();
            if (selected != null) {
                int id = Integer.parseInt(selected.split(" - ")[0]);
                memberCheck.setSelected(isMember(id));
            }
        });

        JButton nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 18));
        nextButton.addActionListener(e -> cardLayout.show(cardPanel, "SALES_REP_BRANCH"));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private boolean isMember(int customerId) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT COUNT(*) FROM member WHERE customer_id = ?")) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private JPanel createSalesRepBranchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Sales Representative & Branch", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        
        salesRepCombo = new JComboBox<>(new DisplayData().getComboBoxData(
            "SELECT CONCAT(sales_rep_id, ' - ', name) FROM salesrep"
        ));
        salesRepCombo.setFont(new Font("Arial", Font.PLAIN, 18));
        
        branchCombo = new JComboBox<>(new DisplayData().getComboBoxData(
            "SELECT CONCAT(branch_code, ' - ', branch_name) FROM branch"
        ));
        branchCombo.setFont(new Font("Arial", Font.PLAIN, 18));
        
        centerPanel.add(new JLabel("Sales Representative:"));
        centerPanel.add(salesRepCombo);
        centerPanel.add(new JLabel("Branch:"));
        centerPanel.add(branchCombo);
        
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton prevButton = new JButton("Previous");
        prevButton.setFont(new Font("Arial", Font.PLAIN, 18));
        prevButton.addActionListener(e -> cardLayout.show(cardPanel, "CUSTOMER"));
        
        JButton nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 18));
        nextButton.addActionListener(e -> cardLayout.show(cardPanel, "PRODUCTS"));
        
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createProductsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Add Products to Sale", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        inputPanel.setBackground(Color.WHITE);
        
        productCombo = new JComboBox<>(new DisplayData().getComboBoxData(
            "SELECT product_name FROM product"
        ));
        productCombo.setFont(new Font("Arial", Font.PLAIN, 18));
        
        quantityField = new JTextField();
        quantityField.setFont(new Font("Arial", Font.PLAIN, 18));
        
        JButton addButton = new JButton("Add Product");
        addButton.setFont(new Font("Arial", Font.PLAIN, 18));
        addButton.addActionListener(e -> addProductToSale());
        
        inputPanel.add(new JLabel("Product:"));
        inputPanel.add(productCombo);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(addButton);
        
        panel.add(inputPanel, BorderLayout.NORTH);

        // table for items added
        String[] columns = {"Product", "Quantity", "Unit Price", "Subtotal"};
        Object[][] data = {};
        itemsTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        totalLabel = new JLabel("Total: ₱0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(totalLabel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton prevButton = new JButton("Previous");
        prevButton.setFont(new Font("Arial", Font.PLAIN, 18));
        prevButton.addActionListener(e -> cardLayout.show(cardPanel, "SALES_REP_BRANCH"));
        
        JButton nextButton = new JButton("Proceed to Payment");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 18));
        nextButton.addActionListener(e -> cardLayout.show(cardPanel, "PAYMENT"));
        
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private void addProductToSale() {
        try {
            String productName = (String) productCombo.getSelectedItem();
            int quantity = Integer.parseInt(quantityField.getText());
            
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double unitPrice = getProductPrice(productName);
            double subtotal = unitPrice * quantity;
            
            saleItems.add(new SaleItem(productName, quantity, unitPrice));
            totalAmount += subtotal;
            updateItemsTable();
            
            quantityField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private double getProductPrice(String productName) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT unit_price FROM product WHERE product_name = ?")) {
            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getDouble("unit_price") : 0.0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    
    private void updateItemsTable() {
        Object[][] data = new Object[saleItems.size()][4];
        for (int i = 0; i < saleItems.size(); i++) {
            SaleItem item = saleItems.get(i);
            data[i][0] = item.productName;
            data[i][1] = item.quantity;
            data[i][2] = String.format("₱%.2f", item.unitPrice);
            data[i][3] = String.format("₱%.2f", item.unitPrice * item.quantity);
        }
        itemsTable.setModel(new javax.swing.table.DefaultTableModel(
            data,
            new String[]{"Product", "Quantity", "Unit Price", "Subtotal"}
        ));
        totalLabel.setText(String.format("Total: ₱%.2f", totalAmount));
    }

    private JPanel createPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Payment Information", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        
        paymentCombo = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "Online Payment"});
        paymentCombo.setFont(new Font("Arial", Font.PLAIN, 18));
        
        centerPanel.add(new JLabel("Payment Type:"));
        centerPanel.add(paymentCombo);
        centerPanel.add(new JLabel("Total Amount:"));
        
        JLabel totalDisplay = new JLabel(String.format("₱%.2f", totalAmount));
        totalDisplay.setFont(new Font("Arial", Font.BOLD, 18));
        centerPanel.add(totalDisplay);
        
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton prevButton = new JButton("Previous");
        prevButton.setFont(new Font("Arial", Font.PLAIN, 18));
        prevButton.addActionListener(e -> cardLayout.show(cardPanel, "PRODUCTS"));
        
        JButton nextButton = new JButton("Complete Sale");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 18));
        nextButton.addActionListener(e -> {
            paymentType = (String) paymentCombo.getSelectedItem();
            cardLayout.show(cardPanel, "CONFIRMATION");
            completeSale();
        });
        
        buttonPanel.add(prevButton);
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private void completeSale() {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            conn.setAutoCommit(false);
            
            // create sale record
            int salesId = createSaleRecord(conn);
            
            // create sale items
            for (SaleItem item : saleItems) {
                createSaleItem(conn, salesId, item);
            }
            
            conn.commit();
            JOptionPane.showMessageDialog(this, "Sale completed successfully! Sales ID: " + salesId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error completing sale: " + e.getMessage(), 
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private int createSaleRecord(Connection conn) throws SQLException {
        String customerStr = (String) customerCombo.getSelectedItem();
        customerId = Integer.parseInt(customerStr.split(" - ")[0]);
        
        String salesRepStr = (String) salesRepCombo.getSelectedItem();
        salesRepId = Integer.parseInt(salesRepStr.split(" - ")[0]);
        
        String branchStr = (String) branchCombo.getSelectedItem();
        branchCode = branchStr.split(" - ")[0];
        
        String sql = "INSERT INTO sales (customer_id, sales_rep_id, branch_code, sale_date, payment_type, total_amount) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, salesRepId);
            stmt.setString(3, branchCode);
            stmt.setDate(4, Date.valueOf(LocalDate.now()));
            stmt.setString(5, paymentType);
            stmt.setDouble(6, totalAmount);
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;
        }
    }
    
    private void createSaleItem(Connection conn, int salesId, SaleItem item) throws SQLException {
        String sql = "INSERT INTO salesitems (sale_id, product_id, quantity_ordered, unit_price) " +
                     "VALUES (?, (SELECT product_id FROM product WHERE product_name = ?), ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salesId);
            stmt.setString(2, item.productName);
            stmt.setInt(3, item.quantity);
            stmt.setDouble(4, item.unitPrice);
            stmt.executeUpdate();
        }
    }

    private JPanel createConfirmationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Sale Completed Successfully", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel thanksLabel = new JLabel("Thank you for your purchase!", SwingConstants.CENTER);
        thanksLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        panel.add(thanksLabel, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 18));
        closeButton.addActionListener(e -> dispose());
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    // inner class for item in the sale
    private static class SaleItem {
        String productName;
        int quantity;
        double unitPrice;

        SaleItem(String productName, int quantity, double unitPrice) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }
}
