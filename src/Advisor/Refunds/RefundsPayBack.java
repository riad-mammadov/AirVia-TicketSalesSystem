package Advisor.Refunds;

import Advisor.Home.TravelAdvisorHome;
import DB.DBConnectivity;
import SMTP.Mail;
import com.itextpdf.text.*;
import SMTP.Log;


import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * The RefundsPayBack class represents a GUI that allows a travel advisor to process refunds
 * for a customer. The class extends javax.swing.JFrame.
 */
public class RefundsPayBack extends javax.swing.JFrame {
    // Declare private fields
    private JPanel mainPanel;
    private JButton processRefundButton;
    private JLabel cardNumberText;
    private JButton checkButton;
    private JButton cancelButton;
    private JLabel customerIDText;
    private JLabel amountReturnText;
    private int cardNumber;
    private static Document logfile;
    private int saleID;
    private int commissionID;
    private int ID;
    private static String username;
    private static int customerID;
    private static int currentDate;
    private static String paymentType;
    private static int blankNumber;
    private static int ticketID;
    private static float price;
    private String customerEmail;


    /**
     * Constructs a new RefundsPayBack object with the specified parameters.
     * @param ID The ID of the travel advisor.
     * @param username The username of the travel advisor.
     * @param customerID The ID of the customer.
     * @param currentDate The current date.
     * @param paymentType The payment type.
     * @param blankNumber The blank number.
     * @param ticketID The ID of the ticket.
     * @param saleID The ID of the sale.
     * @param commissionID The ID of the commission.
     * @param price The price.
     */
    public RefundsPayBack(int ID, String username, int customerID, int currentDate,
                          String paymentType, int blankNumber, int ticketID, int saleID, int commissionID, float price) {

        // Initialize fields with the provided parameters
        this.ID = ID;
        this.username = username;
        this.customerID = customerID;
        this.currentDate = currentDate;
        this.paymentType = paymentType;
        this.blankNumber = blankNumber;
        this.ticketID = ticketID;
        this.saleID = saleID;
        this.commissionID = commissionID;
        this.price = price;

        // Set the content pane, size, default close operation, and visibility of the JFrame
        setContentPane(mainPanel);
        setSize(1000, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        // Set the text of the customerIDText and amountReturnText labels to the customer ID and price, respectively
        customerIDText.setText(String.valueOf(customerID));
        amountReturnText.setText(String.valueOf(price));

        try (Connection con = DBConnectivity.getConnection()) {
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement st = con.createStatement();

            // Create a query to select the card number for the given customer ID from the Card_Details table
            String query = " SELECT Card_Number FROM" +
                    " Card_Details " +
                    "WHERE Customer_ID = '"+ customerID +"' ";
            System.out.println(query);
            ResultSet rs = st.executeQuery(query);

            if(rs.next()){
                cardNumber = rs.getInt("Card_Number");
            }
            st.close();

        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }


        // Add an ActionListener to the checkButton
        checkButton.addActionListener(new ActionListener() {
            @Override
            // Set the text of the cardNumberText label to the card number
            public void actionPerformed(ActionEvent e) {
                cardNumberText.setText(String.valueOf(cardNumber));
            }
        });

        processRefundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Attempting to establish a connection to the database
                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;

                    // Loading the JDBC driver
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    // Creating a statement object
                    Statement st = con.createStatement();

                    // Defining a SQL query to insert a new refund into the Refund table
                    String query = "BEGIN;" +
                            "SELECT * FROM Refund WHERE Refund_ID = (SELECT COALESCE(MAX(Refund_ID), 0) FROM Refund) FOR UPDATE;" +
                            "INSERT INTO Refund SELECT" +
                            "(SELECT COALESCE(MAX(Refund_ID), 0) + 1 FROM Refund), '"+currentDate+"','"+customerID+"'," +
                            "'"+saleID+"'," +
                            "'"+ID+"', '"+commissionID+"', 1;" +
                            "COMMIT;";

                    // Executing the query and storing the result
                    int insert = st.executeUpdate(query);

                    // Closing the statement object
                    st.close();
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }


                // Attempting to create a new Log object with a specified file path
                try {
                    Log log = new Log("data/logfile.txt");

                    // Logging the refund information
                    log.logger.info("Refund of blankNumber: " + blankNumber + " to Customer: " + customerID + " on date:" +
                            " " + currentDate);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();

                    // Defining a SQL query to update the Refund_ID of a sale in the Sale table
                    String query = "BEGIN; " +
                            "SELECT * FROM Sale WHERE Sale.BlanlNumber = '"+blankNumber+"' FOR UPDATE; " +
                            "UPDATE Sale " +
                            "SET Sale.Refund_ID = 1 " +
                            "WHERE Sale.BlankNumber = '"+blankNumber+"';" +
                            "COMMIT; ";

                    int rs = st.executeUpdate(query);
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

                // Displaying a message dialog to indicate that the refund was successful
                JOptionPane.showMessageDialog(mainPanel,"Refund successfull");



                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "SELECT CustomerAccount.Email " +
                            "FROM CustomerAccount " +
                            "WHERE CustomerAccount.Customer_ID = '" + customerID + "' ";
                    System.out.println(query);
                    ResultSet rs = st.executeQuery(query);

                    if(rs.next()){
                        customerEmail = rs.getString("Email");
                    }

                    st.close();
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

                Mail mail = new Mail();
                mail.setupServerProperties();

                try {
                    mail.draftEmail(customerEmail,"Dear Customer " + customerID + ", this is to confirm that" +
                            "your refund for blankNumber: " + blankNumber + " was successfull and a amount of " + price);
                } catch (MessagingException | IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    mail.sendEmail();
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }

                dispose();
                TravelAdvisorHome advisorHome = new TravelAdvisorHome(ID,username);
                advisorHome.show();

            }


        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel the refund?", "Cancel Confirmation", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // User clicked "Yes"
                    // Perform cancellation action
                    dispose();
                    TravelAdvisorHome advisorHome = new TravelAdvisorHome(ID,username);
                    advisorHome.show();
                } else {
                    // User clicked "No"
                    // Do nothing or perform alternative action
                }


            }
        });
    }
}
