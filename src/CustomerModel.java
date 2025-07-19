import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomerModel extends JPanel {
    // UI Texts
    final public String opening = "Customer Record Management";
    final public String b1Text = "View All Customers";
    final public String b2Text = "View Customer Details";
    final public String b3Text = "View Customer Purchases";
    final public String prevText = "Back";

    // UI Settings
    final Font font = new Font("Arial", Font.PLAIN, 20);
    final Dimension buttonSize = new Dimension(350, 50);

    private DisplayData displayData = new DisplayData();
    private JComboBox<String> customerComboBox;
    final private JPanel cardPanel;

    public CustomerModel(JPanel cardPanel) {

        this.cardPanel = cardPanel;
        //setTitle("Customer Record");
        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //setSize(800, 400);
        //setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        showMainMenu();
        //setVisible(true);
    }

    private void showMainMenu() {
        this.removeAll();
        
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
            ResultSet rs = getAllCustomers();
            if (rs != null) {
                displayData.displayData(this, rs, "All Customers", ev -> showMainMenu());
            } else {
                JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btn2.addActionListener(e -> showCustomerSelection("details"));
        btn3.addActionListener(e -> showCustomerSelection("purchases"));
        prevBtn.addActionListener(e ->  {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, "mainMenu");
        });

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

        this.add(mainPanel);
        this.revalidate();
        this.repaint();
    }

    private void showCustomerSelection(String action) {
        this.removeAll();
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Select a Customer:", SwingConstants.CENTER);
        titleLabel.setFont(font);
        
        customerComboBox = new JComboBox<>(displayData.getComboBoxData(
            "SELECT CONCAT(first_name, ' ', last_name) AS customer_name FROM Customer ORDER BY first_name"));
        customerComboBox.setFont(font);
        
        JButton submitBtn = new JButton("Submit");
        submitBtn.setFont(font);
        submitBtn.setPreferredSize(buttonSize);
        
        JButton backBtn = new JButton(prevText);
        backBtn.setFont(font);
        backBtn.setBackground(Color.decode("#880808"));
        backBtn.setForeground(Color.WHITE);
        backBtn.setOpaque(true);
        backBtn.setBorderPainted(false);
        
        submitBtn.addActionListener(e -> {
            String selectedCustomer = (String) customerComboBox.getSelectedItem();
            if (selectedCustomer != null && !selectedCustomer.isEmpty()) {
                if (action.equals("details")) {
                    ResultSet rs = getCustomerDetails(selectedCustomer);
                    if (rs != null) {
                        displayData.displayData(this, rs, "Customer Details", ev -> showCustomerSelection("details"));
                    }
                } else if (action.equals("purchases")) {
                    ResultSet rs = getCustomerPurchases(selectedCustomer);
                    if (rs != null) {
                        displayData.displayData(this, rs, "Customer Purchases", ev -> showCustomerSelection("purchases"));
                    }
                }
            }
        });
        
        backBtn.addActionListener(e -> showMainMenu());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        gbc.gridy = 0;
        panel.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        panel.add(customerComboBox, gbc);
        
        gbc.gridy = 2;
        panel.add(submitBtn, gbc);
        
        gbc.gridy = 3;
        panel.add(backBtn, gbc);
        
        this.add(panel);
        this.revalidate();
        this.repaint();
    }

    private ResultSet getAllCustomers() {
        return executeQuery("SELECT customer_id, first_name, last_name, email, gender, date_registered FROM Customer");
    }

    private ResultSet getCustomerDetails(String customerName) {
        String[] names = customerName.split(" ");
        String firstName = names[0];
        String lastName = names.length > 1 ? names[1] : "";
        
        String query = "SELECT customer_id, first_name, last_name, email, gender, date_registered " +
                       "FROM Customer WHERE first_name = ? AND last_name = ?";
        
        try {
            Connection conn =  DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ResultSet getCustomerPurchases(String customerName) {
        String[] names = customerName.split(" ");
        String firstName = names[0];
        String lastName = names.length > 1 ? names[1] : "";
        
        String query = "SELECT p.product_name, p.price, s.sale_date, s.quantity " +
                       "FROM Sale s " +
                       "JOIN Product p ON s.product_id = p.product_id " +
                       "JOIN Customer c ON s.customer_id = c.customer_id " +
                       "WHERE c.first_name = ? AND c.last_name = ? " +
                       "ORDER BY s.sale_date DESC";
        
        try {
            Connection conn =  DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            return stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ResultSet executeQuery(String query) {
        try {
            Connection conn =  DBConnection.getConnection();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}