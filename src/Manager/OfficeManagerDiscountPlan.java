package Manager;

import Authentication.Login;
import DB.DBConnectivity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 The OfficeManagerDiscountPlan class represents the user interface for managing the discount plan of the office manager.
 This class extends the javax.swing.JFrame class to create the frame for the discount plan interface.
 */
public class OfficeManagerDiscountPlan extends javax.swing.JFrame {
    private JButton logOutButton;
    private JButton homeButton;
    private JButton discountPlanButton;
    private JButton blanksButton;
    private JButton stockButton;
    private JButton ticketStockTurnOverButton;
    private JTable DiscountPlanTable;
    private JPanel DiscountPlan;
    private JScrollPane DiscountPlanScroll;
    private JTextField setDiscountRate;
    private JTextField lowerRange;
    private JTextField midRange;
    private JButton submitFixedDiscountRateButton;
    private JButton submitFlexableDiscountRatesButton;
    private JTextField UpperRange;
    private JTextField lowerRangeRate;
    private JTextField midRangeRate;
    private JTextField upperRangeRate;
    private JButton saleReportButton;
    private JLabel usernameLabel;
    private JTextField enterCustomerIDFixed;
    private JButton setButton;
    private static int ID;
    private static String username;
    private boolean hasDiscount;



    /**
     Constructor for OfficeManagerDiscountPlan class.
     @param ID The ID of the office manager
     @param username The username of the office manager
     */
    public OfficeManagerDiscountPlan(int ID, String username) {

        this.username = username;
        this.ID = ID;
        usernameLabel.setText("Manager: "+ username);

        setContentPane(DiscountPlan);
        setSize(1000, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OfficeManagerHome officeManagerPage = new OfficeManagerHome(ID, username);
                officeManagerPage.setVisible(true);
                dispose();

            }
        });


        stockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OfficeManagerStock officeManagerStock = new OfficeManagerStock(ID, username);
                officeManagerStock.setVisible(true);
                dispose();

            }
        });

        blanksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OfficeManagerBlanks officeManagerBlanks = new OfficeManagerBlanks(ID, username);
                officeManagerBlanks.setVisible(true);
                dispose();

            }
        });


        discountPlanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OfficeManagerDiscountPlan discountPlanButton = new OfficeManagerDiscountPlan(ID, username);
                discountPlanButton.setVisible(true);
                dispose();

            }
        });

        ticketStockTurnOverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OfficeManagerTicketStockTurnOverReport ticketStockTurnOverButton = new OfficeManagerTicketStockTurnOverReport(ID, username);
                ticketStockTurnOverButton.setVisible(true);
                dispose();

            }
        });


        submitFixedDiscountRateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String fixedRateText = setDiscountRate.getText();
                int customerID = Integer.parseInt(enterCustomerIDFixed.getText());
                // Check if the fixedRateText is a valid decimal number
                if (!fixedRateText.matches("-?\\d+(\\.\\d+)?")) {
                    JOptionPane.showMessageDialog(null, "Invalid rate, rate should be set as a decimal", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double fixedRate = Double.parseDouble(fixedRateText);

                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    // fixed for this need ot be based on the sales_this_month shoudl be grater than >3 and 5<

                    String insert = "UPDATE FixedDiscount " +
                            "SET Rate = ? " +
                            "WHERE CustomerID = ? " +
                            "AND CustomerID IN ( " +
                            "SELECT Customer_ID " +
                            "FROM CustomerAccount " +
                            "WHERE sales_this_month > 3 AND sales_this_month < 5" +
                            ");";

                    //crate astin query that takes the enetred CustomerID and and enerted fixeddisscsountrate, and looks in the CustomerAccoutn table, and look at the macthing Customer_ID Coulmn and chek if and if tbhe sales_this_month > 3 AND sales_this_month < 5, and then enetrs the enerted fixedRate  and the enerted CustomerID, inot the FixedDiscount Tabble
                    //fields in the fixedDiscount are, rate_ID,CustomerID,rate

                    PreparedStatement pstm = con.prepareStatement(insert);
                    pstm.setDouble(1,fixedRate);
                    pstm.setInt(2, customerID);
                    int rowsAffect = pstm.executeUpdate();

                    if(rowsAffect == 0){
                        String query = "INSERT INTO FixedDiscount SELECT "+
                                "(SELECT COALESCE (MAX(Fixed_ID),0) + 1 FROM FixedDiscount),'"+customerID+"','"+fixedRate+"'";
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.executeUpdate();

                        String valued = "UPDATE CustomerAccount " +
                                "SET AccountType = 'valued'," +
                                "DiscountType = 'fixed' " +
                                "WHERE Customer_ID = "+customerID+" " +
                                ";";
                        //crate astin query that takes the enetred CustomerID and and enerted fixeddisscsountrate, and looks in the CustomerAccoutn table, and look at the macthing Customer_ID Coulmn and chek if and if tbhe sales_this_month > 3 AND sales_this_month < 5, and then enetrs the enerted fixedRate  and the enerted CustomerID, inot the FixedDiscount Tabble
                        //fields in the fixedDiscount are, rate_ID,CustomerID,rate
                        PreparedStatement pst = con.prepareStatement(valued);
                        int rowsAffected = pst.executeUpdate();
                    }


                    pstm.close();

                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        submitFlexableDiscountRatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int lowerRate = Integer.parseInt(lowerRangeRate.getText());
                int midRate = Integer.parseInt(midRangeRate.getText());
                int upperRate = Integer.parseInt(upperRangeRate.getText());

                int lowRange = Integer.parseInt(lowerRange.getText());
                int MidRange = Integer.parseInt(midRange.getText());
                int upperRange = Integer.parseInt(UpperRange.getText());




                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    //Statement st = con.createStatement();

                    String query = "WITH MonthlySales AS (" +
                            "  SELECT Customer_ID, SUM(amount) AS sales_total" +
                            "  FROM Sale" +
                            "  WHERE EXTRACT(MONTH FROM Current_month) = EXTRACT(MONTH FROM CURRENT_DATE)" +
                            "  AND EXTRACT(YEAR FROM Current_month) = EXTRACT(YEAR FROM CURRENT_DATE)" +
                            "  GROUP BY Customer_ID" +
                            ")," +
                            "UPDATE FlexibleDiscountPlan" +
                            " SET Rate = CASE" +
                            "             WHEN ms.sales_total BETWEEN 1 AND " + lowRange + " THEN " + lowerRate +
                            "             WHEN ms.sales_total BETWEEN " + (lowRange + 1) + " AND " + midRange + " THEN " + midRate +
                            "             ELSE " + upperRate +
                            "           END" +
                            " FROM FlexibleDiscountPlan fdp" +
                            " JOIN CustomerAccount ca ON fdp.CustomerID = ca.Customer_ID" +
                            " JOIN MonthlySales ms ON ca.Customer_ID = ms.Customer_ID" +
                            " WHERE ms.sales_total > 3;";

                    PreparedStatement pstmt = con.prepareStatement(query);
                    pstmt.setInt(1,lowerRate );
                    pstmt.setInt(2,midRate );
                    pstmt.setInt(3,upperRate );

                    pstmt.setInt(4,lowRange );
                    pstmt.setInt(5,MidRange );
                    pstmt.setInt(6,upperRange );
                    pstmt.executeQuery();


                } catch (ClassNotFoundException ex) {ex.printStackTrace();


                } catch (SQLException ex) {
                    ex.printStackTrace();
                }




            }
        });
        saleReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OfficeSaleReports saleReportsButton = new OfficeSaleReports(ID, username);
                saleReportsButton.setVisible(true);
                dispose();
            }
        });
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Login login = new Login();
                login.show();
            }
        });
    }

    public static void main(String[] args){
        OfficeManagerDiscountPlan DiscountPlan = new OfficeManagerDiscountPlan(ID,username);
        DiscountPlan.show();
    }
}
