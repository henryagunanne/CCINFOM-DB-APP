
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ProductModel extends JFrame{
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
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setSize(800, 400);
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
                JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
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
        ResultSet resultSet = null;
        String getCustomerQuery = "SELECT c.customer_id, c.last_name, c.first_name, i.unit_price, i.quantity_ordered, s.total_amount" +
                                    "FROM Customer c JOIN Sales s ON s.customer_id = c.customer_id" +
                                    "JOIN SalesItems i ON i.sales_id = s.sales_id" +
                                    "JOIN Product p ON p.product_id = i.product_id" +
                                    "WHERE product_name = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            conn.setAutoCommit(false); // Start transaction

            try (PreparedStatement getCustomerStmt = conn.prepareStatement(getCustomerQuery);){

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
                }else{
                    return resultSet;
                }

            }catch(SQLException e){
                conn.rollback(); // Rollback on error
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "An error while getting request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }


    private boolean processReturn(String saleDate, String productName, String reason, int quantity){
        String getSaleIdQuery = "SELECT sale_id FROM Sales WHERE sale_date = ?";
        String getProductIdQuery = "SELECT product_id FROM product WHERE name = ?";
        String checkQuantityQuery = "SELECT quantity_ordered FROM SalesItems WHERE sale_id = ? AND product_id = ?";;
        String updateSalesQuery = "UPDATE Sales SET total_amount = total_amount - ? WHERE sale_id = ? AND sale_date = ?";
        String DeleteSalesQuery = "DELETE FROM Sales WHERE sale_id = ? AND sale_date = ?";
        String updateSaleItemsQuery = "UPDATE SalesItems SET quantity_ordered = quantity_ordered - ? WHERE sale_id = ? AND product_id = ?";
        String DeleteSaleItemsQuery = "DELETE FROM SalesItems WHERE sale_id = ? AND product_id = ?";
        String updateReturnsQuery = "INSERT INTO Returns VALUES(?, ?, ?, ?)";
        String updateReturnItemsQuery = "INSERT INTO ReturnItems VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE quantity_returned = quantity + ?";


        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            conn.setAutoCommit(false); // Start transaction


        }catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
