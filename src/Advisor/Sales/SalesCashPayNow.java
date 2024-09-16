package Advisor.Sales;

import Advisor.Home.TravelAdvisorHome;
import DB.DBConnectivity;
import SMTP.Mail;
import com.itextpdf.text.Document;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



/**
 The SalesCashPayNow class represents the GUI for processing a cash payment for a sale in the Travel Advisor application.
 It includes methods for calculating the amount to be returned, voiding a ticket, and completing a sale.
 @author [Alex Elemele]
 @version 1.0
 @since [13.04.2023]
 */
public class SalesCashPayNow extends javax.swing.JFrame {

    private static int commission_ID;
    private JButton voidTicketButton;
    private JButton completeSaleButton;
    private JTextField amountGivenText;
    private JPanel mainPanel;
    private JLabel amountToBePaidText;
    private JLabel amountTobePaidLabel;
    private JButton calculateButton;
    private JLabel amountToBeReturnedText;
    private static int ID;
    private static String username;
    private static int customerID;
    private static float price;
    private static int blankNumber;
    private static String blankType;
    private static String paymentPeriod;
    private static String paymentType;
    private static int ticketID;
    private static int date;
    private static int currencyID;
    private static Document document;
    private String customerEmail;



    /**
     * Constructs a new SalesCashPayNow object with the specified parameters.
     * @param ID The ID of the sale
     * @param username The username of the user making the sale
     * @param customerID The ID of the customer
     * @param price The price of the sale
     * @param blankNumber The number of the blank being sold
     * @param blankType The type of the blank being sold
     * @param paymentPeriod The payment period for the sale
     * @param commission_ID The ID of the commission
     * @param paymentType The payment type for the sale
     * @param ticketID The ID of the ticket
     * @param date The date of the sale
     * @param currencyID The ID of the currency used for the sale
     * @param document The document associated with the sale
     */
    public SalesCashPayNow(int ID, String username, int customerID,
                           float price, int blankNumber, String blankType,
                           String paymentPeriod, int commission_ID, String paymentType, int ticketID, int date, int currencyID, Document document) {
        setContentPane(mainPanel);
        setSize(1000,600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        this.ID = ID;
        this.username = username;
        this.customerID = customerID;
        this.price = price;
        this.blankNumber = blankNumber;
        this.blankType = blankType;
        this.paymentPeriod = paymentPeriod;
        this.paymentType = paymentType;
        this.ticketID = ticketID;
        this.date = date;
        this.currencyID = currencyID;
        this.document = document;
        this.commission_ID = commission_ID;

        amountToBePaidText.setText(String.valueOf(price));


        // Action listener for the complete sale button
        completeSaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Try-with-resources block to ensure that the connection is properly closed after execution
                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    // SQL query to update the Blank table to indicate that the blank is sold
                    String query = " INSERT INTO Sale SELECT" +
                            "(SELECT COALESCE(MAX(Sale_ID), 0) + 1 FROM Sale), '"+price+"','"+paymentPeriod+"'," +
                            " null,'"+date+"'," +
                            "'"+paymentType+"', '"+ID+"','"+currencyID+"'," +
                            "'"+customerID+"','"+commission_ID+"','"+ticketID+"','"+blankNumber+"', null, 1 ";
                    System.out.println(query);
                    int insert = st.executeUpdate(query);

                    st.close();
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

                JOptionPane.showMessageDialog(mainPanel,"Payment was successful and has been recorded");

                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "UPDATE Blank " +
                            "SET Blank.isSold = '" + 1 + "' " +
                            "WHERE Blank.blankNumber = '" + blankNumber + "' ";
                    System.out.println(query);
                    int rs = st.executeUpdate(query);

                    st.close();
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

                dispose();
                TravelAdvisorHome advisorHome = new TravelAdvisorHome(ID,username);
                advisorHome.show();

                // selects email id
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

                // adding one to sales this month of the customer
                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "UPDATE CustomerAccount " +
                            "SET sales_this_month = sales_this_month + 1 " +
                            "WHERE CustomerAccount.Customer_ID = '" + customerID + "' ";
                    System.out.println(query);
                    int rowsUpdated = st.executeUpdate(query);

                    st.close();
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

                Mail mail = new Mail();
                mail.setupServerProperties();
                try {
                    mail.draftEmail(customerEmail,"Dear Customer for AirVia, this" +
                            "is your receipt for your most recent flight purchase", "/Users/alexelemele/Documents/testPDF.pdf");
                } catch (MessagingException | IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    mail.sendEmail();
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }

            }


        });

        /**
         * Calculates the amount of change to be returned to the customer and updates the corresponding label.
         */

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String amountGivenString = amountGivenText.getText();
                float amountGivenInt = Float.parseFloat(amountGivenString);
                float amountToBeReturned = amountGivenInt - price;
                if(amountToBeReturned < 0){
                    JOptionPane.showMessageDialog(mainPanel,"Customer has not given enough");
                }
                else{
                    amountToBeReturnedText.setText(String.valueOf(amountToBeReturned));
                }

            }
        });
        voidTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                TravelAdvisorHome travelAdvisorHome = new TravelAdvisorHome(ID,username);
            }
        });
    }


    public static void main (String[]args){
        SalesCashPayNow salesCashPayNow = new SalesCashPayNow(ID, username,customerID,price,blankNumber,
                blankType,paymentPeriod, commission_ID, paymentType,ticketID,date,currencyID, document);
        salesCashPayNow.show();
    }
}
