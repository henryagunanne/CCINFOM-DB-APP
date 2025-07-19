import javax.swing.*;
import java.awt.*;

public class ClothingStoreApp extends JFrame {
    private JPanel contentPanel;
    private MainMenuPanel mainMenuPanel;
    private ProductPanel productPanel;
    private BranchPanel branchPanel;
    private SalesRepPanel salesRepPanel;
    private SalesTransactionPanel salesTransactionPanel;
    private CustomerPanel customerPanel;
    private ReportsPanel reportsPanel;
    
    public ClothingStoreApp() {
        super("Clothing Store Management System");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(Color.WHITE);
        
        // Initialize the content panel with CardLayout
        contentPanel = new JPanel(new CardLayout());
        
        // Create panels
        mainMenuPanel = new MainMenuPanel(this);
        productPanel = new ProductPanel(this);
        branchPanel = new BranchPanel(this);
        salesRepPanel = new SalesRepPanel(this);
        salesTransactionPanel = new SalesTransactionPanel(this);
        customerPanel = new CustomerPanel(this);
        reportsPanel = new ReportsPanel(this);
        
        // Add panels to the content panel
        contentPanel.add(mainMenuPanel, "MainMenu");
        contentPanel.add(productPanel, "Product");
        contentPanel.add(branchPanel, "Branch");
        contentPanel.add(salesRepPanel, "SalesRep");
        contentPanel.add(salesTransactionPanel, "SalesTransaction");
        contentPanel.add(customerPanel, "Customer");
        contentPanel.add(reportsPanel, "Reports");
        add(contentPanel);
        
        showPanel("MainMenu");
        
        setVisible(true);
    }
    
    public void showPanel(String panelName) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, panelName);
    }
}