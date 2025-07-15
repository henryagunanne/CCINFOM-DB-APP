
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MainMenuGUI extends JFrame{
    final public String opening = "Clothing Store Record Management System";
    final public String b1Text = "Product Record Management";
    final public String b2Text = "Customer Record Management";
    final public String b3Text = "Sales Rep Record Management";
    final public String b4Text = "Branch Record Management";

    final Font font = new Font("Arial", Font.PLAIN, 20);
    final Font titleFont = new Font("Arial", Font.BOLD, 25);
    final Dimension buttonSize = new Dimension(350, 50);

    public MainMenuGUI(){
        super("Clothing Store");
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        mainMenu();

        setVisible(true);
    }


    private void mainMenu(){
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

        btn1.addActionListener(e -> new ProductModel());
        //TODO: edit the action listeners for the buttons below
        // btn2.addActionListener(e -> );
        // btn3.addActionListener(e -> )
        btn4.addActionListener(e -> new BranchModel());

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

        this.getContentPane().add(mainPanel);
        this.revalidate();
        this.repaint();
    }
}
