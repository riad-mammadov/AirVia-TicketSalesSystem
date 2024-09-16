package Advisor.Sales;

import Advisor.Home.TravelAdvisorHome;
import DB.DBConnectivity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;



/**
 A class representing the sales payment window of the airline ticket booking system.
 The window allows the user to apply commissions, check for applicable discounts, select a payment type and period, and finalize the sale.
 */
public class SalesPayment extends javax.swing.JFrame {
    private JButton applyCommissionButton;
    private JComboBox<String> commissionFilterr;
    private JButton checkDiscountButton;
    private JComboBox paymentFilter;
    private JButton continueButton;
    private JButton voidTicketButton;
    private JPanel mainPanel;
    private JLabel beforeComissionLabel;
    private JLabel priceBeforeText;
    private JLabel availableCommissionRateLabel;
    private JLabel calculatedPriceLabel;
    private JLabel totalPriceLabel;
    private JLabel checkApplicableDiscountLabel;
    private JLabel discountPlanLabel;
    private JLabel discountPlanText;
    private JLabel priceDiscountAppliedText;
    private JLabel discountPriceText;
    private JLabel paymentTypeLabel;
    private JComboBox paymentPeriodDropDown;
    private JLabel paymentPeriodLabel;
    private JPanel commissionFilterPanel;
    private JButton exchangeButton;
    private JLabel pricePercentageLabel;
    private JLabel usdPriceLabel;
    private JTextField enterCurrencyCodeTextField;
    private String blankType;
    private static int ID;
    private static String username;
    private static int customerID;
    private static int flightID;
    private static int blankNumber;
    private float price;
    private int commissionRows;
    private float priceAfterCommission;
    private boolean customerHasDiscount;
    private String accountType;
    private float flexibleApplied;
    private float fixedApplied;
    private String paymentOption;
    private String paymentPeriod;
    private String paymentType;
    private static int date;
    private static int currencyID;
    private float exchangeRate;
    private float priceInUSD;
    private int commission_ID;



    /**
     Constructs a SalesPayment object with the specified parameters and initializes the GUI components.
     @param ID the ID of the logged-in user
     @param username the username of the logged-in user
     @param customerID the ID of the customer purchasing the ticket
     @param flightID the ID of the flight being purchased
     @param blankNumber the number of the ticket being purchased
     @param date the date of the sale
     */
    public SalesPayment(int ID, String username, int customerID, int flightID, int blankNumber, int date) {
        System.out.println("This is flight ID: " + flightID);
        System.out.println("This is blank ID: " + blankNumber);
        System.out.println("This is customer ID: " + customerID);
        this.ID = ID;
        this.username = username;
        this.customerID = customerID;
        this.flightID = flightID;
        this.blankNumber = blankNumber;
        this.date = date;
        setContentPane(mainPanel);
        setSize(1000,600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        try(Connection con = DBConnectivity.getConnection()){
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");

            Statement stFlight = con.createStatement();
            String queryFlight = "SELECT Flight.price \n " +
                    "FROM Flight\n " +
                    "WHERE Flight.number = '"+flightID+"' ";
            ResultSet rsFlight = stFlight.executeQuery(queryFlight);

            if (rsFlight.next()) { // check if the ResultSet contains any rows
                price = rsFlight.getFloat("price");
                priceBeforeText.setText(String.valueOf(price));
                System.out.println(price + " THE FLOAT VALUE");

                // Do something with the price variable
            }
            stFlight.close();



        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }



        applyCommissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                float selectedCommission = Float.parseFloat(Objects.requireNonNull(commissionFilterr.getSelectedItem()).toString());
                System.out.println("SELECTED COMMISSION IS " + selectedCommission);
                priceAfterCommission = (priceInUSD * selectedCommission) + priceInUSD;
                totalPriceLabel.setText("The total price is: " +  priceAfterCommission);

            }
        });

        voidTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog(mainPanel,"Are you sure you want to void this Ticket?");

                if (dialogResult == JOptionPane.YES_OPTION) {
                    // User clicked the "Yes" button
                    // Do something here
                    JOptionPane.showMessageDialog(mainPanel,"The Ticket has been voided");
                    dispose();
                    TravelAdvisorHome home = new TravelAdvisorHome(ID,username);
                    home.getContentPane().show();

                } else {
                    // do nothing
                }

            }
        });


        checkDiscountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try(Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    // create a statement object to query customer's discount
                    Statement stCustDiscount = con.createStatement();
                    String queryCustDiscount = "SELECT AccountType , DiscountType  " +
                            "FROM CustomerAccount " +
                            "WHERE CustomerAccount.Customer_ID = '" + customerID + "' ";

                    // execute the query to get the customer's discount information
                    ResultSet rsCustDiscount = stCustDiscount.executeQuery(queryCustDiscount);

                    // check if the customer has a discount
                    if(rsCustDiscount.next()){
                        if(rsCustDiscount.getString("AccountType").equals("regular")){
                            // customer has no discount
                            discountPlanText.setText("No discount plan applicable");
                            priceDiscountAppliedText.setText("No discount percentage applied");
                            customerHasDiscount = false;
                        }
                        else if(rsCustDiscount.getString("AccountType").equals("valued")){
                            customerHasDiscount = true;
                            if(rsCustDiscount.getString("DiscountType").equals("fixed")) {
                                // customer has a fixed discount plan
                                accountType = "fixed";
                                Statement stCustFixed = con.createStatement();
                                String queryCustFixed = "SELECT Rate  " +
                                        "FROM FixedDiscount " +
                                        "WHERE FixedDiscount.CustomerID = '" + customerID + "' ";

                                // execute the query to get the fixed discount rate
                                ResultSet rsCustFixed = stCustDiscount.executeQuery(queryCustFixed);
                                if(rsCustFixed.next()){
                                    float fixedDiscount = Float.parseFloat(rsCustFixed.getString("Rate"));

                                    // calculate the price after applying fixed discount
                                    fixedApplied = priceAfterCommission - (priceAfterCommission * fixedDiscount);
                                    discountPlanText.setText("Fixed");
                                    discountPriceText.setText(String.valueOf(fixedApplied));
                                    priceDiscountAppliedText.setText("Total price after a flexible Discount of " + fixedDiscount * 100 + "%");
                                }
                            }

                            else if(rsCustDiscount.getString("AccountType").equals("flexible")){

                                // customer has a flexible discount plan
                                accountType = "valued";
                                customerHasDiscount = true;
                                Statement stCustFlexible = con.createStatement();
                                String queryCustFlexible = "SELECT Rate  " +
                                        "FROM FlexibleDiscount " +
                                        "WHERE FlexibleDiscount.CustomerID = '" + customerID + "' ";

                                // execute the query to get the flexible discount rate
                                ResultSet rsCustFlexible = stCustDiscount.executeQuery(queryCustFlexible);
                                if(rsCustFlexible.next()){
                                    float flexibleDiscount = Float.parseFloat(rsCustFlexible.getString("Rate"));

                                    // calculate the price after applying flexible discount
                                    flexibleApplied = priceAfterCommission - (priceAfterCommission * flexibleDiscount);
                                    discountPlanText.setText("Flexible");
                                    discountPriceText.setText(String.valueOf(flexibleApplied));
                                    priceDiscountAppliedText.setText("Total price after a flexible Discount of " + flexibleDiscount * 10 + "%");
                                }

                            }

                        }
                    }

                    stCustDiscount.close();
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }


            }
        });


        commissionFilterr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try(Connection con = DBConnectivity.getConnection()){
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    Statement stBlank = con.createStatement();
                    String queryBlank = "SELECT Blank.Type\n " +
                            "FROM Blank\n " +
                            "WHERE Blank.BlankNumber = '"+blankNumber+"' ";
                    ResultSet rsBlank = stBlank.executeQuery(queryBlank);
                    if(rsBlank.next()) { // Check if the ResultSet has any data
                        blankType = rsBlank.getString(1); // Retrieve the value of the first column
                        availableCommissionRateLabel.setText("Available commission rates for " + blankType);

                    }

                    Statement stRows = con.createStatement();
                    String queryRows = "SELECT COUNT(*) FROM (SELECT Rate, blankType " +
                            "FROM Commission " +
                            "WHERE Commission.blankType = '"+blankType+"' ) AS subquery";
                    System.out.println(queryRows);
                    ResultSet rsRows = stRows.executeQuery(queryRows);

                    if(rsRows.next()) {
                        commissionRows = rsRows.getInt(1);
                    }

                    commissionFilterr.setMaximumRowCount(commissionRows);


                    Statement st = con.createStatement();
                    String query = "SELECT Rate, blankType, Commission_ID " +
                            "FROM Commission " +
                            "WHERE Commission.blankType = '"+blankType+"' " +
                            "AND Commission.Employee_ID = '"+ID+"' " +
                            "AND '"+date+"' BETWEEN Commission.from_Date AND Commission.to_Date";
                    System.out.println(query);
                    ResultSet rs = st.executeQuery(query);


                    Set<String> rateSet = new HashSet<String>(); // Use a Set to store unique values

                    while(rs.next()){
                        String option = rs.getString("Rate");
                        commission_ID = rs.getInt("Commission_ID");
                        rateSet.add(option); // Add the value to the Set
                    }
                    for (String option : rateSet) { // Loop through the Set and add unique values to the combo box
                        commissionFilterr.addItem(option);
                    }
                    st.close();

                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });


        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paymentPeriod = paymentPeriodDropDown.getSelectedItem().toString();
                paymentType =  paymentFilter.getSelectedItem().toString();

                int dialog = JOptionPane.showConfirmDialog(mainPanel,"Are you sure you want to continue and selected correct payment options?");
                if (dialog == JOptionPane.YES_OPTION) {
                    // User clicked the "Yes" button
                    // Do something here

                    if(paymentPeriodDropDown.getSelectedItem().toString().equals("pay now")) {
                        System.out.println("false");

                        if (customerHasDiscount) {
                            if (accountType.equals("fixed")) {
                                dispose();
                                SaleSummaryPage salesCashPayNow = new SaleSummaryPage(ID, username, customerID, fixedApplied, flightID, paymentPeriod, commission_ID, paymentType, blankNumber, blankType, date, currencyID);
                            } else if (accountType.equals("flexible")) {
                                dispose();
                                SaleSummaryPage salesCashPayNow = new SaleSummaryPage(ID, username, customerID, flexibleApplied, flightID, paymentPeriod, commission_ID, paymentType, blankNumber, blankType, date, currencyID);
                            }

                        } else {
                            dispose();
                            SaleSummaryPage salesCashPayNow = new SaleSummaryPage(ID, username, customerID, priceAfterCommission, flightID, paymentPeriod, commission_ID,  paymentType, blankNumber, blankType, date, currencyID);
                        }
                    }
                    else if(paymentPeriodDropDown.getSelectedItem().toString().equals("pay later")){
                        System.out.println("true");
                        dispose();
                        SaleSummaryPage salesPayLater = new SaleSummaryPage(ID, username, customerID, priceAfterCommission, flightID, paymentPeriod, commission_ID, paymentType, blankNumber, blankType, date, currencyID);
                    }
                }


            }
        });


        exchangeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "SELECT Currency_Code.Currency_Code ,Currency_Code.Exchange_Rate " +
                            "FROM Currency_Code " +
                            "WHERE Currency_Code.Currency_name = '"+enterCurrencyCodeTextField.getText()+"'  ";
                    ResultSet rs = st.executeQuery(query);

                    if(rs.next()){
                        exchangeRate = rs.getFloat("Exchange_Rate");
                        currencyID = rs.getInt("Currency_Code");
                    }
                    System.out.println(enterCurrencyCodeTextField.getText() + "CURRENCY CODE");
                    System.out.println(exchangeRate+ "exchange rate");
                    System.out.println(price + "price");

                    priceInUSD = price * exchangeRate;
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
                System.out.println(priceInUSD);
                usdPriceLabel.setText(String.valueOf(priceInUSD));
                pricePercentageLabel.setText("Price to pay in local currency (exchange Rate " + exchangeRate + " applied)");
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

    public static void main(String[] args){
        SalesPayment salesPayment = new SalesPayment(ID,username,customerID,flightID,blankNumber, date);
        salesPayment.show();
    }
}
