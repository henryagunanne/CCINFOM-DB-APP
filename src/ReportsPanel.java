import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.*;
import java.util.*;
 
public class ReportsPanel extends JPanel {

    final public String opening = "Reports Management";
    final public String b1Text = "Monthly Sales Report";
    final public String b2Text = "Sales per Sales Representative Report";
    final public String b3Text = "Product Performance Report";
    final public String b4Text = "Revenue per Branch Report";
    final public String prevText = "Back";

    final Font font = new Font("Arial", Font.PLAIN, 20);
    final Font titleFont = new Font("Arial", Font.BOLD, 25);
    final Dimension buttonSize = new Dimension(350, 50);

    private DisplayData displayData = new DisplayData();
    final private JPanel cardPanel;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public ReportsPanel(JPanel cardPanel) {
        this.cardPanel = cardPanel;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Create a panel with CardLayout to switch between different report views
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        
        // Create and add the reports menu panel
        JPanel reportsMenuPanel = createReportsMenuPanel();
        mainPanel.add(reportsMenuPanel, "reportsMenu");
        
        // Add the main panel to this panel
        this.add(mainPanel, BorderLayout.CENTER);
        
        // Show the reports menu initially
        cardLayout.show(mainPanel, "reportsMenu");
    }

    private JPanel createReportsMenuPanel() {
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

        btn1.addActionListener(e -> showMonthlySalesReport());
        btn2.addActionListener(e -> showSalesRepReport());
        btn3.addActionListener(e -> showProductPerformanceReport());
        btn4.addActionListener(e -> showRevenueBranchReport());
        prevBtn.addActionListener(e -> {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, "mainMenu");
        });

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
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "reportsMenu"));
        JPanel backPanel = new JPanel();
        backPanel.add(backButton);
        panel.add(backPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private ResultSet executeQuery(String query) {
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Monthly Sales Report
    private void showMonthlySalesReport() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Monthly Sales Report", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // Month selection
        JLabel monthLabel = new JLabel("Select Month:");
        String[] months = {"January", "February", "March", "April", "May", "June", 
                          "July", "August", "September", "October", "November", "December"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        // Year selection
        JLabel yearLabel = new JLabel("Select Year:");
        Integer[] years = {2023, 2024, 2025};
        JComboBox<Integer> yearCombo = new JComboBox<>(years);
        yearCombo.setSelectedItem(LocalDate.now().getYear());

        JButton generateBtn = new JButton("Generate Report");
        generateBtn.setFont(font);
        generateBtn.setBackground(Color.decode("#008000"));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setOpaque(true);
        generateBtn.setBorderPainted(false);

        generateBtn.addActionListener(e -> {
            int month = monthCombo.getSelectedIndex() + 1;
            int year = (Integer) yearCombo.getSelectedItem();
            
            ResultSet rs = generateMonthlySalesReport(month, year);
            if (rs != null) {
                JPanel dataPanel = createDataPanel(rs, 
                    "Monthly Sales Report - " + monthCombo.getSelectedItem() + " " + year);
                mainPanel.add(dataPanel, "monthlySalesData");
                cardLayout.show(mainPanel, "monthlySalesData");
            } else {
                JOptionPane.showMessageDialog(this, "No data available for the selected period.", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        centerPanel.add(monthLabel);
        centerPanel.add(monthCombo);
        centerPanel.add(yearLabel);
        centerPanel.add(yearCombo);
        centerPanel.add(new JLabel(""));
        centerPanel.add(generateBtn);
        
        panel.add(centerPanel, BorderLayout.CENTER);

        JButton backBtn = new JButton(prevText);
        backBtn.setFont(font);
        backBtn.setBackground(Color.decode("#880808"));
        backBtn.setForeground(Color.WHITE);
        backBtn.setOpaque(true);
        backBtn.setBorderPainted(false);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "reportsMenu"));
        
        JPanel southPanel = new JPanel();
        southPanel.add(backBtn);
        panel.add(southPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "monthlySalesReport");
        cardLayout.show(mainPanel, "monthlySalesReport");
    }

    private ResultSet generateMonthlySalesReport(int month, int year) {
        // In a real implementation, this would query the database
        // For now, we'll return sample data
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            // Create a temporary table with sample data
            stmt.execute("CREATE TEMPORARY TABLE IF NOT EXISTS temp_monthly_sales (" +
                         "day INT, category VARCHAR(50), product VARCHAR(50), quantity INT, amount DECIMAL(10,2))");
            
            // Clear any existing data
            stmt.execute("DELETE FROM temp_monthly_sales");
            
            // Insert sample data
            stmt.execute("INSERT INTO temp_monthly_sales VALUES " +
                         "(1, 'Tops', 'T-Shirt', 5, 125.00), " +
                         "(1, 'Bottoms', 'Jeans', 3, 210.00), " +
                         "(2, 'Tops', 'Sweater', 2, 150.00), " +
                         "(2, 'Outerwear', 'Jacket', 1, 200.00), " +
                         "(3, 'Dresses', 'Summer Dress', 4, 320.00)");
            
            // Query the temporary table
            return stmt.executeQuery("SELECT day, category, SUM(quantity) as total_quantity, " +
                                    "SUM(amount) as total_amount FROM temp_monthly_sales " +
                                    "GROUP BY day, category ORDER BY day, category");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Sales per Sales Representative Report
    private void showSalesRepReport() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Sales per Sales Representative Report", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // Month selection
        JLabel monthLabel = new JLabel("Select Month:");
        String[] months = {"January", "February", "March", "April", "May", "June", 
                          "July", "August", "September", "October", "November", "December"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        JButton generateBtn = new JButton("Generate Report");
        generateBtn.setFont(font);
        generateBtn.setBackground(Color.decode("#008000"));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setOpaque(true);
        generateBtn.setBorderPainted(false);

        generateBtn.addActionListener(e -> {
            int month = monthCombo.getSelectedIndex() + 1;
            
            ResultSet rs = generateSalesRepReport(month);
            if (rs != null) {
                JPanel dataPanel = createDataPanel(rs, 
                    "Sales per Rep Report - " + monthCombo.getSelectedItem());
                mainPanel.add(dataPanel, "salesRepData");
                cardLayout.show(mainPanel, "salesRepData");
            } else {
                JOptionPane.showMessageDialog(this, "No data available for the selected period.", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        centerPanel.add(monthLabel);
        centerPanel.add(monthCombo);
        centerPanel.add(new JLabel(""));
        centerPanel.add(generateBtn);
        
        panel.add(centerPanel, BorderLayout.CENTER);

        JButton backBtn = new JButton(prevText);
        backBtn.setFont(font);
        backBtn.setBackground(Color.decode("#880808"));
        backBtn.setForeground(Color.WHITE);
        backBtn.setOpaque(true);
        backBtn.setBorderPainted(false);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "reportsMenu"));
        
        JPanel southPanel = new JPanel();
        southPanel.add(backBtn);
        panel.add(southPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "salesRepReport");
        cardLayout.show(mainPanel, "salesRepReport");
    }

    private ResultSet generateSalesRepReport(int month) {
        // In a real implementation, this would query the database
        // For now, we'll return sample data
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            // Create a temporary table with sample data
            stmt.execute("CREATE TEMPORARY TABLE IF NOT EXISTS temp_sales_rep (" +
                         "sales_rep VARCHAR(50), branch VARCHAR(50), total_sales DECIMAL(10,2))");
            
            // Clear any existing data
            stmt.execute("DELETE FROM temp_sales_rep");
            
            // Insert sample data
            stmt.execute("INSERT INTO temp_sales_rep VALUES " +
                         "('John Smith', 'Main Branch', 5250.00), " +
                         "('Jane Doe', 'Main Branch', 3750.00), " +
                         "('Mike Johnson', 'Downtown', 4200.00), " +
                         "('Sarah Williams', 'Downtown', 3900.00), " +
                         "('Robert Brown', 'Uptown', 4800.00)");
            
            // Query the temporary table
            return stmt.executeQuery("SELECT sales_rep, branch, total_sales FROM temp_sales_rep " +
                                    "ORDER BY branch, total_sales DESC");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Product Performance Report
    private void showProductPerformanceReport() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Product Performance Report", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // Quarter selection
        JLabel quarterLabel = new JLabel("Select Quarter:");
        String[] quarters = {"Q1 (Jan-Mar)", "Q2 (Apr-Jun)", "Q3 (Jul-Sep)", "Q4 (Oct-Dec)"};
        JComboBox<String> quarterCombo = new JComboBox<>(quarters);
        
        // Calculate current quarter
        int currentMonth = LocalDate.now().getMonthValue();
        int currentQuarter = (currentMonth - 1) / 3;
        quarterCombo.setSelectedIndex(currentQuarter);

        JButton generateBtn = new JButton("Generate Report");
        generateBtn.setFont(font);
        generateBtn.setBackground(Color.decode("#008000"));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setOpaque(true);
        generateBtn.setBorderPainted(false);

        generateBtn.addActionListener(e -> {
            int quarter = quarterCombo.getSelectedIndex() + 1;
            
            ResultSet rs = generateProductPerformanceReport(quarter);
            if (rs != null) {
                JPanel dataPanel = createDataPanel(rs, 
                    "Product Performance Report - " + quarterCombo.getSelectedItem());
                mainPanel.add(dataPanel, "productPerfData");
                cardLayout.show(mainPanel, "productPerfData");
            } else {
                JOptionPane.showMessageDialog(this, "No data available for the selected period.", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        centerPanel.add(quarterLabel);
        centerPanel.add(quarterCombo);
        centerPanel.add(new JLabel(""));
        centerPanel.add(generateBtn);
        
        panel.add(centerPanel, BorderLayout.CENTER);

        JButton backBtn = new JButton(prevText);
        backBtn.setFont(font);
        backBtn.setBackground(Color.decode("#880808"));
        backBtn.setForeground(Color.WHITE);
        backBtn.setOpaque(true);
        backBtn.setBorderPainted(false);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "reportsMenu"));
        
        JPanel southPanel = new JPanel();
        southPanel.add(backBtn);
        panel.add(southPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "productPerfReport");
        cardLayout.show(mainPanel, "productPerfReport");
    }

    private ResultSet generateProductPerformanceReport(int quarter) {
        // In a real implementation, this would query the database
        // For now, we'll return sample data
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            // Create a temporary table with sample data
            stmt.execute("CREATE TEMPORARY TABLE IF NOT EXISTS temp_product_perf (" +
                         "product VARCHAR(50), quantity_sold INT, revenue DECIMAL(10,2), avg_price DECIMAL(10,2))");
            
            // Clear any existing data
            stmt.execute("DELETE FROM temp_product_perf");
            
            // Insert sample data
            stmt.execute("INSERT INTO temp_product_perf VALUES " +
                         "('T-Shirt', 120, 3000.00, 25.00), " +
                         "('Jeans', 85, 5950.00, 70.00), " +
                         "('Sweater', 65, 4875.00, 75.00), " +
                         "('Jacket', 40, 8000.00, 200.00), " +
                         "('Dress', 70, 5600.00, 80.00)");
            
            // Query the temporary table
            return stmt.executeQuery("SELECT product, quantity_sold, revenue, avg_price FROM temp_product_perf " +
                                    "ORDER BY revenue DESC");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Revenue per Branch Report
    private void showRevenueBranchReport() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Revenue per Branch Report", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        // Month selection
        JLabel monthLabel = new JLabel("Select Month:");
        String[] months = {"January", "February", "March", "April", "May", "June", 
                          "July", "August", "September", "October", "November", "December"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setSelectedIndex(LocalDate.now().getMonthValue() - 1);

        // Year selection
        JLabel yearLabel = new JLabel("Select Year:");
        Integer[] years = {2023, 2024, 2025};
        JComboBox<Integer> yearCombo = new JComboBox<>(years);
        yearCombo.setSelectedItem(LocalDate.now().getYear());

        JButton generateBtn = new JButton("Generate Report");
        generateBtn.setFont(font);
        generateBtn.setBackground(Color.decode("#008000"));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.setOpaque(true);
        generateBtn.setBorderPainted(false);

        generateBtn.addActionListener(e -> {
            int month = monthCombo.getSelectedIndex() + 1;
            int year = (Integer) yearCombo.getSelectedItem();
            
            ResultSet rs = generateRevenueBranchReport(month, year);
            if (rs != null) {
                JPanel dataPanel = createDataPanel(rs, 
                    "Revenue per Branch - " + monthCombo.getSelectedItem() + " " + year);
                mainPanel.add(dataPanel, "revenueBranchData");
                cardLayout.show(mainPanel, "revenueBranchData");
            } else {
                JOptionPane.showMessageDialog(this, "No data available for the selected period.", 
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        centerPanel.add(monthLabel);
        centerPanel.add(monthCombo);
        centerPanel.add(yearLabel);
        centerPanel.add(yearCombo);
        centerPanel.add(new JLabel(""));
        centerPanel.add(generateBtn);
        
        panel.add(centerPanel, BorderLayout.CENTER);

        JButton backBtn = new JButton(prevText);
        backBtn.setFont(font);
        backBtn.setBackground(Color.decode("#880808"));
        backBtn.setForeground(Color.WHITE);
        backBtn.setOpaque(true);
        backBtn.setBorderPainted(false);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "reportsMenu"));
        
        JPanel southPanel = new JPanel();
        southPanel.add(backBtn);
        panel.add(southPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "revenueBranchReport");
        cardLayout.show(mainPanel, "revenueBranchReport");
    }

    private ResultSet generateRevenueBranchReport(int month, int year) {
        // In a real implementation, this would query the database
        // For now, we'll return sample data
        try {
            Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            // Create a temporary table with sample data
            stmt.execute("CREATE TEMPORARY TABLE IF NOT EXISTS temp_branch_revenue (" +
                         "branch VARCHAR(50), location VARCHAR(50), total_revenue DECIMAL(10,2))");
            
            // Clear any existing data
            stmt.execute("DELETE FROM temp_branch_revenue");
            
            // Insert sample data
            stmt.execute("INSERT INTO temp_branch_revenue VALUES " +
                         "('Main Branch', 'Downtown', 25750.00), " +
                         "('Uptown Store', 'Uptown', 18900.00), " +
                         "('Mall Location', 'Westfield Mall', 22300.00), " +
                         "('Airport Shop', 'International Airport', 12500.00)");
            
            // Query the temporary table
            return stmt.executeQuery("SELECT branch, location, total_revenue FROM temp_branch_revenue " +
                                    "ORDER BY total_revenue DESC");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}