package Advisor.Home;

import javax.mail.MessagingException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import Advisor.Blanks.AdvisorStock;
import Advisor.IndividualReport.IndividualReport;
import Advisor.Refunds.Refunds;
import Advisor.Sales.OutstandingPayment;
import Advisor.Sales.SalesSearchCustomer;
import Authentication.Login;
import SMTP.Mail;


/**

 This class represents the home page for the travel advisor, which displays various options for them to select from.
 It contains buttons for navigating to the stock page, ticket sales page, refunds page, individual report page, and outstanding payment page.
 The advisor's username is also displayed on the page.
 */
public class TravelAdvisorHome extends javax.swing.JFrame {
    private JButton homeButton;
    private JButton stockButton;
    private JButton ticketSalesButton;
    private JButton refundsButton;
    private JButton logOutButton;
    private JPanel travelAdvisorPage;
    private JPanel logoPanel;
    private JButton individualReportButton;
    private JLabel usernameLabel;
    private JButton outstandingPaymentButton;
    private JButton giveFeedbackButton;
    private ImageIcon logoImage;
    private JLabel logoLabel;
    private static int ID;
    private static int dateToday;
    private static String username;



    /**
     Constructs a TravelAdvisorHome object with the specified advisor ID and username.
     Sets the advisor's username on the page, and adds the AirVia logo to the logo panel.
     Sets the size, content pane, and visibility of the frame.
     @param ID the advisor's ID
     @param username the advisor's username
     */
    public TravelAdvisorHome(int ID, String username){
        this.username = username;
        this.ID = ID;
        this.dateToday = dateToday;
        usernameLabel.setText("advisor: "+ username);
        logoImage = new ImageIcon("data/AirViaLogo.png");
        logoLabel = new JLabel(logoImage);
        logoImage.getImage().getScaledInstance(500,500,Image.SCALE_DEFAULT);
        travelAdvisorPage.setPreferredSize(new Dimension(500,500));
        logoPanel.add(logoLabel);
        setContentPane(travelAdvisorPage);
        setSize(1000,600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);



        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Login login = new Login();
                login.show();

            }
        });
        stockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                AdvisorStock stock = new AdvisorStock(ID,username);
                stock.show();
            }
        });

        ticketSalesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SalesSearchCustomer advisorSales = new SalesSearchCustomer(ID,username);
                advisorSales.show();

            }
        });

        refundsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Refunds refunds = new Refunds(ID,username);
                refunds.show();
            }
        });
        individualReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                IndividualReport individualReport = new IndividualReport(ID,username);
                individualReport.show();
            }
        });
        outstandingPaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OutstandingPayment outstandingPayment = new OutstandingPayment(ID,username);
            }
        });
        giveFeedbackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPopup();

            }
        });
    }

    private void showPopup() {
        JComponent glassPane = new JPanel();
        glassPane.setLayout(null);
        glassPane.setOpaque(false);

        JPanel popup = new JPanel();
        popup.setBounds(getWidth() - 200, 0, 200, 400);
        popup.setBackground(Color.LIGHT_GRAY);
        popup.setLayout(new BoxLayout(popup, BoxLayout.Y_AXIS));

        JTextField textField = new JTextField();
        textField.setBounds(10, 10, 180, 30); // set the position and size of the text field
        popup.add(textField); // add the text field to the popup panel

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                if (text.contains("outofbounds")) {
                    textField.setText("");
                    popup.add(new JLabel("outofbounds"));
                    popup.revalidate();
                    popup.repaint();
                } else {
                    // handle submit action here

                    Mail mail = new Mail();
                    mail.setupServerProperties();

                    try {
                        mail.draftEmail("alexobz09@gmail.com",text);
                    } catch (MessagingException | IOException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        mail.sendEmail();
                    } catch (MessagingException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        popup.add(submitButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                glassPane.setVisible(false);
            }
        });
        popup.add(closeButton);

        JScrollPane scrollPane = new JScrollPane(popup);
        scrollPane.setBounds(getWidth() - 200, 0, 200, 400);
        glassPane.add(scrollPane);

        setGlassPane(glassPane);
        glassPane.setVisible(true);
    }







    public static void main(String[] args){
        TravelAdvisorHome advisorHome = new TravelAdvisorHome(ID, username);
        advisorHome.show();

    }



}
