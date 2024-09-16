package Advisor.Sales;

import Advisor.Home.TravelAdvisorHome;
import DB.DBConnectivity;
import SMTP.Mail;
import com.itextpdf.text.Document;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 The SalesCardPayNow class provides functionality for the sales representative to complete a payment for a travel booking using a credit card.
 It includes methods for validating the credit card details, exchanging currency, and completing the payment.
 @author [Alex Elemele]
 @version 1.0
 @since 2023-04-13
 */
public class SalesCardPayNow extends javax.swing.JFrame {
    private static int commission_ID;
    private float priceInUSD;
    private JButton checkCardDetailsButton;
    private JButton cancelPaymentButton;
    private JButton completePaymentButton;
    private JTextField customerName;
    private JTextField creditCardnumber;
    private JLabel pricePercentageLabel;
    private JPanel mainPanel;
    private JTextField textField1;
    private JTextField enterCurrencyCodeTextField;
    private JButton exchangeButton;
    private JLabel usdPriceLabel;
    private static int ID;
    private static String username;
    private static int customerID;
    private static float price;
    private static int blankNumber;
    private static String blankType;
    private static String paymentPeriod;
    private static String paymentType;
    private float exchangeRate;
    private boolean cardValid;
    private static int ticketID;
    private static int date;
    private static int currencyID;
    private static Document document;
    private String customerEmail;
    private String cardNumber;




    /**
     * Constructs a new SalesCardPayNow object.
     *
     * @param ID            the ID of the booking
     * @param username      the username of the sales representative
     * @param customerID    the ID of the customer making the booking
     * @param price         the price of the booking
     * @param blankNumber   the number of blank used for the booking
     * @param blankType     the type of blank used for the booking
     * @param paymentPeriod the payment period for the booking
     * @param commission_ID the ID of the commission associated with the booking
     * @param paymentType   the type of payment being used for the booking
     * @param ticketID      the ID of the ticket for the booking
     * @param date          the date of the booking
     * @param currencyID    the ID of the currency being used for the booking
     * @param document      the document associated with the booking
     */
    public SalesCardPayNow(int ID, String username, int customerID,
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

        usdPriceLabel.setText(String.valueOf(price));



        checkCardDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                cardNumber = creditCardnumber.getText();
                cardNumber = cardNumber.replace(" ","");
                System.out.println(cardNumber + "CARDNUMBER");
                cardValid = validateCreditCardNumber(cardNumber);

                if(cardValid && !(creditCardnumber.getText().equals("")) ){
                    JOptionPane.showMessageDialog(mainPanel,"Credit Card number is valid");
                }
                else {
                    JOptionPane.showMessageDialog(mainPanel,"Credit Card number is not valid");
                }


            }
        });
        cancelPaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        completePaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cardValid) {
                    try (Connection con = DBConnectivity.getConnection()) {
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Statement st = con.createStatement();
                        String query = " INSERT INTO Sale SELECT" +
                                "(SELECT COALESCE(MAX(Sale_ID), 0) + 1 FROM Sale), '"+priceInUSD+"','"+paymentPeriod+"'," +
                                " null,'"+date+"'," +
                                "'"+paymentType+"', '"+ID+"','"+ SalesCardPayNow.currencyID +"'," +
                                "'"+customerID+"', '"+commission_ID+"','"+ticketID+"','"+blankNumber+"', null, 1 ";
                        System.out.println(query);
                        int insert = st.executeUpdate(query);

                    } catch (SQLException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(mainPanel,"Payment was successful and has been recorded");
                } else {
                    JOptionPane.showMessageDialog(mainPanel,"Credit Card information was not valid");

                }

                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "UPDATE Blank " +
                            "SET Blank.isSold = '" + 1 + "' " +
                            "WHERE Blank.blankNumber = '" + blankNumber + "' ";
                    System.out.println(query);
                    int rs = st.executeUpdate(query);

                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

                dispose();
                TravelAdvisorHome advisorHome = new TravelAdvisorHome(ID,username);
                advisorHome.show();

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



                BigInteger bigNum = new BigInteger(cardNumber);

                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = " INSERT INTO Card_Details VALUES" +
                            "('"+ bigNum +"', '"+customerID+"')";
                    System.out.println(query);
                    int insert = st.executeUpdate(query);
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

        creditCardnumber.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
        cancelPaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(mainPanel, "Are you sure you want to cancel?", "Cancel Confirmation", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // User clicked "Yes"
                    // Perform cancellation action
                    dispose();
                    TravelAdvisorHome travelAdvisorHome = new TravelAdvisorHome(ID,username);
                } else {
                    // User clicked "No"
                    // Do nothing or perform alternative action
                }

            }
        });
    }


    /**

     This method validates a credit card number according to the Luhn algorithm
     @param input the credit card number as a String
     @return true if the credit card number is valid, false otherwise
     */
    private static boolean validateCreditCardNumber(String input){

        //convert input to int
        int[] creditCardInt = new int[input.length()];

        // place all elements of the string into the int arrays
        for(int i = 0; i < input.length(); i++){
            creditCardInt[i] = Integer.parseInt(input.substring(i,i+1));

        }
        //Starting from the right, double each other digit, if greater than 9 mod 10 + 1 to the remainder
        for(int i = creditCardInt.length - 2; i >= 0; i = i - 2){

            int tempValue = creditCardInt[i];
            tempValue = tempValue * 2;
            if(tempValue > 9){
                tempValue =  tempValue % 10 + 1;
            }

            creditCardInt[i] = tempValue;

        }
        // Add up all digits
        int total = 0;
        for(int i = 0; i < creditCardInt.length; i++){
            total += creditCardInt[i];
        }

        if(total % 10 == 0){
            return true;
        }else{
            return false;
        }
    }

    public static void main(String[]args){
        SalesCardPayNow salesCardPayNow = new SalesCardPayNow(ID,username,customerID,
         price,blankNumber, blankType,paymentPeriod, commission_ID, paymentType,ticketID,date, currencyID, document);
    }

}

