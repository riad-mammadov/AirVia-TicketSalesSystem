package Advisor.Sales;

import Advisor.Home.TravelAdvisorHome;
import DB.DBConnectivity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;


/**
 SalesSearchCustomer is a JFrame class that allows a sales representative to search for a customer by
 first name, last name, email, and phone number. The user can also enter a date for a ticket sale.
 If the customer is found in the database, the SalesSelectTicket screen is displayed.
 If the customer is not found, the user is given the option to create a new customer.
 If the user chooses to create a new customer, the SalesSelectTicket screen is displayed with the new customer's ID.
 If the user chooses not to create a new customer, the screen remains open and the user can search for another customer.
 */
public class SalesSearchCustomer extends javax.swing.JFrame {
    private JTextField firstNameTextField;
    private JTextField lastNameTextField;
    private JTextField emailAddressTextField;
    private JTextField phoneNumberTextField;
    private JButton checkDetailsButton;
    private JButton homeButton;
    private JPanel mainPanel;
    private JPanel checkCustomerpanel;
    private JTextField dateText;
    private static int ID;
    private static String username;
    private int customerID;



    /**
     * Constructor for SalesSearchCustomer.
     * @param ID the ID of the logged-in user.
     * @param username the username of the logged-in user.
     */
    public SalesSearchCustomer(int ID, String username) {
        this.ID = ID;
        this.username = username;
        setContentPane(mainPanel);
        setSize(1000,600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        checkDetailsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = firstNameTextField.getText();
                String lastName = lastNameTextField.getText();
                String email = emailAddressTextField.getText();
                String phoneNumber = phoneNumberTextField.getText();



                try(Connection con = DBConnectivity.getConnection()){
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "SELECT Customer_ID\n" +
                            "FROM CustomerAccount \n" +
                            "WHERE Firstname = '"+firstName+"' AND Surname = '"+lastName+"'\n" +
                            "AND Email = '"+email+"' AND PhoneNumber = '"+phoneNumber+"'";
                    System.out.println(query);
                    ResultSet rs = st.executeQuery(query);

                    while (rs.next()) {
                        // get the value of a column by name
                        customerID = rs.getInt("Customer_ID");

                        // do something with the values
                        System.out.println("id of alex = " + customerID);
                    }

                    // resets result set in order to complete customer check
                    rs = st.executeQuery(query);

                    // check if all fields have text in them. If yes it evaluates the information
                    if(requireText(firstNameTextField) &&
                    requireText(lastNameTextField) &&
                    requireText(emailAddressTextField) &&
                    requireText(phoneNumberTextField)) {

                        if (!rs.next()) {
                            int dialogResult = JOptionPane.showConfirmDialog(mainPanel, "The Customer not was found./n" +
                                    " Do you want to continue to create a new customer/n" +
                                    "with this information?", "Confirmation", JOptionPane.YES_NO_OPTION);

                            if (dialogResult == JOptionPane.YES_OPTION) {
                                // User clicked the "Yes" button
                                // Do something here
                                Statement newCustomer = con.createStatement();
                                String queryCustomer = "INSERT INTO CustomerAccount SELECT" +
                                        " (SELECT COALESCE(MAX(Customer_ID), 0) + 1 FROM CustomerAccount), " +
                                        " '"+firstName+"','"+lastName+"','"+email+"','"+phoneNumber+"', " +
                                        "'"+" "+"','regular','null','January',0 ";
                                System.out.println(query);
                                int rowsInserted = newCustomer.executeUpdate(queryCustomer);


                                Statement stNewCustomer = con.createStatement();
                                String queryNewCustomer = "SELECT Customer_ID " +
                                        "FROM CustomerAccount \n" +
                                        "WHERE Firstname = '"+firstName+"' AND Surname = '"+lastName+"'\n" +
                                        "AND Email = '"+email+"' AND PhoneNumber = '"+phoneNumber+"'";
                                System.out.println(query);
                                ResultSet rsNewCustomer = stNewCustomer.executeQuery(queryNewCustomer);

                                if(rsNewCustomer.next()) {
                                    // get the value of a column by name
                                    customerID = rsNewCustomer.getInt("Customer_ID");

                                    // do something with the values
                                    System.out.println("id of alex = " + customerID);
                                }

                                st.close();



                                if(dateText.getText().isEmpty()){
                                    JOptionPane.showMessageDialog(mainPanel,"Please enter a date in dd/mm/yy format");
                                }

                                String dateString = dateText.getText().replace("/","");
                                int dateInt = Integer.parseInt(dateString);

                                dispose();
                                SalesSelectTicket salesSellTicket = new SalesSelectTicket(ID,username,customerID,dateInt);
                                salesSellTicket.show();

                            } else {
                                // do nothing
                            }
                        } else {
                            int dialogResult = JOptionPane.showConfirmDialog(mainPanel, "The Customer was found./n" +
                                    " Do you want to continue to sell the ticket to this customer?", "Confirmation", JOptionPane.YES_NO_OPTION);

                            if (dialogResult == JOptionPane.YES_OPTION) {
                                // User clicked the "Yes" button
                                // Do something here

                                if(dateText.getText().equals("")){
                                    JOptionPane.showMessageDialog(mainPanel,"Please enter a date in dd/mm/yy format");
                                }

                                String dateString = dateText.getText().replace("/","");
                                int dateInt = Integer.parseInt(dateString);

                                dispose();
                                SalesSelectTicket salesSellTicket = new SalesSelectTicket(ID,username,customerID,dateInt);
                                salesSellTicket.show();

                            } else {
                                // do nothing
                            }
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(mainPanel,"Each field requires Text");
                    }
                    st.close();

                } catch (ClassNotFoundException | SQLException ex) {
                    ex.printStackTrace();
                }


                }

        });
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                TravelAdvisorHome advisorHome = new TravelAdvisorHome(ID,username);
                advisorHome.show();

            }
        });
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
    }


    /**
     Checks if the specified JTextField contains text.
     @param c the JTextField to check for text
     @return true if the JTextField contains text, false otherwise
     */
    public boolean requireText(JTextField c){
        String text = c.getText();
        return !text.isEmpty();
    }


    public static void main(String[]args ){
        SalesSearchCustomer advisorSales = new SalesSearchCustomer(ID,username);
        advisorSales.show();
        advisorSales.setVisible(true);
    }
}
