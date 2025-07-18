import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SalesRepModel extends JFrame {
    final private String DRIVER = "com.mysql.cj.jdbc.Driver";
    final private String URL = "jdbc:mysql://localhost:3306/DBclothing";
    final private String USERNAME = "root";
    final private String PASSWORD = "AGUnanne1";

    final public String opening = "Sales Representative Records Management";
    final public String b1Text = "View Sales Representatives";
    final public String b2Text = "View Completed Sales by Rep";
    final public String prevText = "Back";

    final Font font = new Font("Arial", Font.PLAIN, 20);
    final Font titleFont = new Font("Arial", Font.BOLD, 25);
    final Dimension buttonSize = new Dimension(350, 50);

    private DisplayData displayData = new DisplayData();

    public SalesRepModel() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL Driver not found!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        setTitle("Sales Representative Records");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        titleLabel.setFont(titleFont);

        JButton btn1 = new JButton(b1Text);
        JButton btn2 = new JButton(b2Text);

        btn1.setFont(font);
        btn2.setFont(font);

        btn1.setPreferredSize(buttonSize);
        btn2.setPreferredSize(buttonSize);

        JButton prevBtn = new JButton(prevText);
        prevBtn.setFont(font);
        prevBtn.setBackground(Color.decode("#880808"));
        prevBtn.setForeground(Color.WHITE);
        prevBtn.setOpaque(true);
        prevBtn.setBorderPainted(false);

        btn1.addActionListener(e -> {
            ResultSet rs = getSalesReps();
            if (rs != null) {
                displayData.displayData(this, rs, "Sales Representatives", ev -> showMainMenu());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to load sales representatives.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btn2.addActionListener(e -> showSelectSalesRepForSales());

        prevBtn.addActionListener(e -> dispose());

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
        mainPanel.add(prevBtn, gbc);

        getContentPane().add(mainPanel);
        revalidate();
        repaint();
    }

    private void showSelectSalesRepForSales() {
        JComboBox<String> salesRepComboBox = new JComboBox<>(displayData.getComboBoxData("SELECT name FROM salesrep ORDER BY name"));

        getContentPane().removeAll();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Select Sales Representative", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(110, 300, 110, 300));

        JButton proceedBtn = new JButton("Proceed");
        proceedBtn.setFont(font);
        proceedBtn.setBackground(Color.decode("#008000"));
        proceedBtn.setForeground(Color.WHITE);
        proceedBtn.setOpaque(true);
        proceedBtn.setBorderPainted(false);

        proceedBtn.addActionListener(e -> {
            String selectedRep = (String) salesRepComboBox.getSelectedItem();
            if (selectedRep != null) {
                ResultSet rs = getCompletedSalesByRep(selectedRep);
                if (rs != null) {
                    displayData.displayData(this, rs, "Completed Sales for " + selectedRep, ev -> showSelectSalesRepForSales());
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to load sales.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        centerPanel.add(salesRepComboBox);
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

        backBtn.addActionListener(e -> showMainMenu());

        getContentPane().add(panel);
        revalidate();
        repaint();
    }

    private ResultSet getSalesReps() {
        return executeQuery("SELECT * FROM salesrep");
    }

    private ResultSet getCompletedSalesByRep(String salesRepName) {
        String query = "SELECT s.sales_id, s.sale_date, s.total_amount, c.first_name, c.last_name " +
                      "FROM sales s " +
                      "JOIN customer c ON s.customer_id = c.customer_id " +
                      "JOIN salesrep sr ON s.sales_rep_id = sr.sales_rep_id " +
                      "WHERE sr.name = '" + salesRepName + "'";
        return executeQuery(query);
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
}
