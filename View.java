import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class View extends JFrame{


    public View(){
        super("Clothing Store");

        JPanel cardPanel = new JPanel(new CardLayout());
        cardPanel.add(new MainMenuGUI(cardPanel), "mainMenu");
        cardPanel.add(new ProductModel(cardPanel), "products");
        cardPanel.add(new CustomerModel(cardPanel), "customers");
        cardPanel.add(new SalesRepModel(cardPanel), "salesRep");
        cardPanel.add(new BranchModel(cardPanel), "branch");
        cardPanel.add(new SalesTransactionModel(this, cardPanel), "salesTransaction");
       
        this.add(cardPanel, BorderLayout.CENTER);
        

        // Show the main menu first
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, "mainMenu");

        setBackground(Color.WHITE);
        //setLayout(new BorderLayout());
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        setVisible(true);
    }

}
