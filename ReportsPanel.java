import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;

public class ReportsPanel extends JPanel {
    private final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private final String URL = "jdbc:mysql://localhost:3306/DBclothing";
    private final String USERNAME = "root";
    private final String PASSWORD = "imagentumr1@";
    
    private ClothingStoreApp mainApp;
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    public ReportsPanel(ClothingStoreApp app) {
        this.mainApp = app;
        setLayout(new BorderLayout());
        
        mainPanel.add(createReportsMenu(), "MENU");
        add(mainPanel, BorderLayout.CENTER);
        cardLayout.show(mainPanel, "MENU");
    }

    private JPanel createReportsMenu() {
        JPanel panel = new JPanel(new GridLayout(5, 1));
        
        JButton monthlySalesBtn = new JButton("Monthly Sales Report");
        monthlySalesBtn.addActionListener(e -> showReportParams("MONTHLY_SALES"));
        
        JButton salesRepBtn = new JButton("Sales per Sales Rep");
        salesRepBtn.addActionListener(e -> showReportParams("SALES_REP"));
        
        JButton productPerfBtn = new JButton("Product Performance");
        productPerfBtn.addActionListener(e -> showReportParams("PRODUCT_PERF"));
        
        JButton branchRevenueBtn = new JButton("Branch Revenue");
        branchRevenueBtn.addActionListener(e -> showReportParams("BRANCH_REV"));
        
        JButton backBtn = new JButton("Back to Main Menu");
        backBtn.addActionListener(e -> mainApp.showPanel("MainMenu"));
        
        panel.add(monthlySalesBtn);
        panel.add(salesRepBtn);
        panel.add(productPerfBtn);
        panel.add(branchRevenueBtn);
        panel.add(backBtn);
        
        return panel;
    }

    private void showReportParams(String reportType) {
        JPanel panel = new JPanel(new GridLayout(3, 2));
        
        JComboBox<String> monthCombo = new JComboBox<>(new String[]{
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        
        JComboBox<Integer> yearCombo = new JComboBox<>();
        int currentYear = YearMonth.now().getYear();
        for (int year = currentYear - 5; year <= currentYear; year++) {
            yearCombo.addItem(year);
        }
        yearCombo.setSelectedItem(currentYear);
        
        JButton generateBtn = new JButton("Generate Report");
        generateBtn.addActionListener(e -> {
            int month = monthCombo.getSelectedIndex() + 1;
            int year = (int) yearCombo.getSelectedItem();
            generateReport(reportType, month, year);
        });
        
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        
        panel.add(new JLabel("Month:"));
        panel.add(monthCombo);
        panel.add(new JLabel("Year:"));
        panel.add(yearCombo);
        panel.add(generateBtn);
        panel.add(backBtn);
        
        JPanel container = new JPanel(new BorderLayout());
        container.add(panel, BorderLayout.CENTER);
        
        mainPanel.add(container, "PARAMS_" + reportType);
        cardLayout.show(mainPanel, "PARAMS_" + reportType);
    }

    private void generateReport(String reportType, int month, int year) {
        try {
            String reportTitle = "";
            String sql = "";
            
            switch (reportType) {
                case "MONTHLY_SALES":
                    reportTitle = "Monthly Sales Report - " + month + "/" + year;
                    sql = "SELECT p.category, SUM(si.quantity_ordered) AS total_quantity, " +
                          "SUM(si.quantity_ordered * si.unit_price) AS total_sales " +
                          "FROM Sales s " +
                          "JOIN SalesItems si ON s.sales_id = si.sales_id " +
                          "JOIN Product p ON si.product_id = p.product_id " +
                          "WHERE MONTH(s.sale_date) = ? AND YEAR(s.sale_date) = ? " +
                          "GROUP BY p.category";
                    break;
                    
                case "SALES_REP":
                    reportTitle = "Sales Rep Report - " + month + "/" + year;
                    sql = "SELECT sr.name, b.branch_name, SUM(s.total_amount) AS total_sales " +
                          "FROM Sales s " +
                          "JOIN SalesRep sr ON s.sales_rep_id = sr.sales_rep_id " +
                          "JOIN Branch b ON s.branch_code = b.branch_code " +
                          "WHERE MONTH(s.sale_date) = ? AND YEAR(s.sale_date) = ? " +
                          "GROUP BY sr.name, b.branch_name";
                    break;
                    
                case "PRODUCT_PERF":
                    reportTitle = "Product Performance - " + month + "/" + year;
                    sql = "SELECT p.product_name, SUM(si.quantity_ordered) AS total_quantity, " +
                          "SUM(si.quantity_ordered * si.unit_price) AS total_sales, " +
                          "AVG(si.unit_price) AS avg_price " +
                          "FROM SalesItems si " +
                          "JOIN Sales s ON si.sales_id = s.sales_id " +
                          "JOIN Product p ON si.product_id = p.product_id " +
                          "WHERE QUARTER(s.sale_date) = ? AND YEAR(s.sale_date) = ? " +
                          "GROUP BY p.product_name";
                    break;
                    
                case "BRANCH_REV":
                    reportTitle = "Branch Revenue - " + month + "/" + year;
                    sql = "SELECT b.branch_name, SUM(s.total_amount) AS total_revenue " +
                          "FROM Sales s " +
                          "JOIN Branch b ON s.branch_code = b.branch_code " +
                          "WHERE MONTH(s.sale_date) = ? AND YEAR(s.sale_date) = ? " +
                          "GROUP BY b.branch_name";
                    break;
            }
            
            try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                if (reportType.equals("PRODUCT_PERF")) {
                    int quarter = (month + 2) / 3;
                    stmt.setInt(1, quarter);
                    stmt.setInt(2, year);
                } else {
                    stmt.setInt(1, month);
                    stmt.setInt(2, year);
                }
                
                ResultSet rs = stmt.executeQuery();
                displayReport(rs, reportTitle);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + ex.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayReport(ResultSet rs, String title) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        
        // table model
        DefaultTableModel model = new DefaultTableModel();
        for (int i = 1; i <= columnCount; i++) {
            model.addColumn(metaData.getColumnLabel(i));
        }
        
        // add data
        while (rs.next()) {
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i-1] = rs.getObject(i);
            }
            model.addRow(row);
        }
        
        // create table
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.add(new JLabel(title, JLabel.CENTER), BorderLayout.NORTH);
        reportPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton backBtn = new JButton("Back to Reports");
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        reportPanel.add(backBtn, BorderLayout.SOUTH);
        
        mainPanel.add(reportPanel, "REPORT_" + title);
        cardLayout.show(mainPanel, "REPORT_" + title);
    }
}
