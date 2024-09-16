package Advisor.Refunds;

import Advisor.Home.TravelAdvisorHome;
import DB.DBConnectivity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class Refunds extends javax.swing.JFrame {
    private JTextField blankNumberText;
    private JPanel mainPanel;
    private JButton checkValidityButton;
    private JButton backButton;
    private JTextField dateText;
    private JButton nextButton;
    private static int ID;
    private static String username;
    private int saleID;
    private int paymentDate;
    private String paymentType;
    private int employeeID;
    private int customerID;
    private int currencyCode;
    private int commissionID;
    private int ticketID;
    private Integer refundID;
    private int blankNumber;
    private boolean blankFound;
    private int currentDate;
    private float amount;


    /**
     Represents a GUI for processing refunds for a specific sale based on the blank number
     @param ID the ID of the sale
     @param username the username of the user processing the refund
     */
    public Refunds(int ID, String username) {
        setContentPane(mainPanel);
        setSize(1000, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        // ActionListener for the "Check Validity" button
        checkValidityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Get the input values from the text fields
                String blankString = blankNumberText.getText();
                blankString = blankString.replace(" ","");
                String currentDateString = dateText.getText();


                // Check if any of the text fields are empty
                if(blankString.isEmpty() || currentDateString.isEmpty()){
                    JOptionPane.showMessageDialog(mainPanel,"Blank text field and date text field both require text");
                }

                // Try to connect to the database and execute the query
                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "SELECT Sale.Sale_ID, Sale.Amount, Sale.Payment_Date, " +
                            " Sale.PaymentType, Sale.Employee_ID, Sale.Currency_Code," +
                            " Sale.Customer_ID, Sale.Commission_ID, Sale.TicketID, Sale.Refund_ID, Sale.BlankNumber  " +
                            "FROM Sale \n" +
                            "WHERE Sale.BlankNumber  = '" + blankString + "'";
                    System.out.println(query);
                    ResultSet rs = st.executeQuery(query);


                    // Check if the query returned any results
                    if(!rs.next()){
                        blankFound = false;
                        JOptionPane.showMessageDialog(mainPanel,"blank number not found");
                    }
                    else{
                        // Get the values from the query result
                        saleID = rs.getInt("Sale_ID");
                        amount = rs.getFloat("Amount");
                        paymentDate = rs.getInt("Payment_Date");
                        paymentType = rs.getString("PaymentType");
                        employeeID = rs.getInt("Employee_ID");
                        currencyCode = rs.getInt("Currency_Code");
                        customerID = rs.getInt("Customer_ID");
                        commissionID = rs.getInt("Commission_ID");
                        ticketID = rs.getInt("TicketID");
                        refundID = rs.getInt("Refund_ID");
                        blankNumber = rs.getInt("BlankNumber");
                        blankFound = true;

                    }



                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }

                System.out.println(refundID + " REFUND");

                currentDateString = currentDateString.replace("/","");
                currentDate = Integer.parseInt(currentDateString);

                // Format the payment and refund request dates to dd/MM/yy format
                String formattedBought = formatDate(paymentDate);
                String formattedRequest = formatDate(currentDate);


                // Check if the sale has already been refunded
                if(refundID > 0){
                    JOptionPane.showMessageDialog(mainPanel,"This sale has already been refunded");
                }

                // Check if the refund request date is valid
                else if(!validateDate(paymentDate, currentDate) && blankFound){

                    JOptionPane.showMessageDialog(mainPanel,"The date of the refund request " + formattedRequest + " has" +
                            " surpassed the date of the sale " + formattedBought + " by at least one year ");
                }
                else if(blankFound){
                    JOptionPane.showMessageDialog(mainPanel, "Refund is eligible for blankNumber: " + blankNumber);
                }



            }

            });


        // Adding a KeyListener to the dateText field to allow only digits and slashes to be entered
        dateText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '/') {
                    e.consume();
                }
            }
        });


        // Adding a KeyListener to the blankNumberText field to allow only digits to be entered
        blankNumberText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });

        // Adding an ActionListener to the nextButton to proceed to the next step if the refund is eligible
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(blankFound && validateDate(paymentDate, currentDate) & refundID == 0){
                    dispose();
                    RefundsPayBack refundsPayBack = new RefundsPayBack(ID,username,customerID,currentDate,paymentType,blankNumber, ticketID, saleID,commissionID,amount);
                    refundsPayBack.show();
                }
                else{
                    JOptionPane.showMessageDialog(mainPanel, "some of the details were not valid. Please check again");
                }
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to cancel?", "Cancel Confirmation", JOptionPane.YES_NO_OPTION);
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
     * Formats a payment date from an integer into a string in the format "dd/MM/yy".
     * @param paymentDate the payment date as an integer (in the format "yyMMdd")
     * @return the formatted payment date as a string (in the format "dd/MM/yy")
     */
    public static String formatDate(int paymentDate) {
        String dateStr = String.valueOf(paymentDate);
        String year = dateStr.substring(0, 2);
        String month = dateStr.substring(2, 4);
        String day = dateStr.substring(4);
        return day + "/" + month + "/" + year;
    }



    /**
     * Validates that a refund request date is later than the date the item was bought.
     * @param dateBought the date the item was bought as an integer (in the format "yyMMdd")
     * @param dateRefundRequest the date the refund was requested as an integer (in the format "yyMMdd")
     * @return true if the refund request date is valid, false otherwise
     */
    public boolean validateDate(int dateBought, int dateRefundRequest){

        if(String.valueOf(dateBought).length() == String.valueOf(dateRefundRequest).length()){
            if(dateRefundRequest - 10000 <= dateBought ){
                return true;
            }
            return false;
        }

        return false;
    }

    /**
     * Checks if a string is a valid date in the format "yy/MM/dd".
     * @param inputString the string to check
     * @return true if the string is a valid date, false otherwise
     */
    public static boolean isDateString(String inputString) {
        try {
            String[] parts = inputString.split("/");
            if (parts.length != 3) {
                return false;
            }
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            if (year < 0 || month < 1 || month > 12 || day < 1 || day > 31) {
                return false;
            }
            LocalDate date = LocalDate.of(year, month, day);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Main method for running the Refunds class and displaying the refund interface.
     * @param args the command line arguments (not used)
     */
    public static void main(String[] args){
        Refunds refunds = new Refunds(ID,username);
        refunds.show();
    }
}
