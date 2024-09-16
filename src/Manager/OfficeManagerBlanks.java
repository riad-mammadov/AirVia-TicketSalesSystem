package Manager;

import DB.DBConnectivity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 The OfficeManagerBlanks class represents the GUI for the office manager's blank management page.
 This page displays information about the blank stock and allows the manager to view and submit
 reports on blank usage.
 @author [Alex Elemele]
 @version [13.04.2023]
 */
public class OfficeManagerBlanks extends javax.swing.JFrame{
    private JPanel Blanks;
    private JButton logOutButton;
    private JButton homeButton;
    private JButton stockButton;
    private JButton blanksButton;
    private JButton discountPlanButton;
    private JButton ticketStockTurnOverButton;
    private JButton submitBlankUsageReportButton;
    private JTable blanksTable;
    private JScrollPane blankTableScroll;
    private JButton showBlanksButton;
    private JButton viewBlankUsageReportButton;
    private JButton saleReportsButton;
    private JLabel usernameLabel;
    private JComboBox selectFilter;


    private static int ID;
    private static String username;




    /**
     * Constructor for the OfficeManagerBlanks class. Initializes the GUI and sets up the initial view
     * of the page.
     * @param ID the ID of the current user
     * @param username the username of the current user
     */
    public OfficeManagerBlanks(int ID, String username) {
        blanksTable.setPreferredScrollableViewportSize(new Dimension(500, 500));
        blankTableScroll.setPreferredSize(new Dimension(500, 500));

        this.username = username;
        this.ID = ID;
        usernameLabel.setText("Manager: "+ username);
        setContentPane(Blanks);
        setSize(1500, 1000);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);


        /*       NEED TO SORT ADD THE FILTER SYSTEM, MAKE IT THE A METHOD SO IT CAN BE CALLAED MUILT TOMES WITHOUT REAPIGN ALL THE CODE
        */


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


/*
        viewBlankUsageReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File logFile = new File("/Users/aadilghani/Desktop/AirViaTicketSalesSystem/src/LogFile Report.txt");
                    Desktop.getDesktop().open(logFile);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });

 */


/*

        viewBlankUsageReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                /*
                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();

                    // Create a query to select the card number for the given customer ID from the Card_Details table
                    String query = "";
                    System.out.println(query);
                    ResultSet rs = st.executeQuery(query);

                    st.close();

                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }



                try {
                    Log log = new Log("src/LogFile Report.txt");

                    // Logging the refund information
                    log.logger.info("Assigned Blank Number:"  + blankNumber +  "Assigned to :" + Employee_ID);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }



            }
        });


 */
        /*
        viewBlankUsageReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();

                    // Create a query to select the BlankNumber, Type, and Employee_ID from the Blank table
                    String query = "SELECT BlankNumber, Type, Employee_ID FROM Blank";
                    System.out.println(query);
                    ResultSet rs = st.executeQuery(query);

                    // Create a Log instance to write to the log file
                    Log log = new Log("src/LogFile Report.txt");

                    // Process the ResultSet and log the information
                    while (rs.next()) {
                        int blankNumber = rs.getInt("BlankNumber");
                        String blankType = rs.getString("Type");
                        int employeeID = rs.getInt("Employee_ID");

                        // Logging the blank information
                        log.logger.info("Blank Number: " + blankNumber + ", Type: " + blankType + ", Assigned to Employee_ID: " + employeeID);
                    }

                    st.close();
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });


         */





        showBlanksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) blanksTable.getModel();
                model.setRowCount(0);

                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query;
                    String selectedOption = (String) selectFilter.getSelectedItem();

                    switch (selectedOption) {
                        case "Sold Blanks":
                            query = "SELECT Blank.BlankNumber, Blank.Type, Blank.Employee_ID " +
                                    "FROM Blank " +
                                    "WHERE Blank.isSold = '1';";
                            model.setColumnIdentifiers(new String[]{"BlankNumber", "Type", "Employee_ID"});
                            break;
                        case "Assigned Blanks":
                            query = "SELECT Blank.BlankNumber, Blank.Type, Blank.Employee_ID, Blank.date_assign " +
                                    "FROM Blank " +
                                    "WHERE Blank.isAssigned = '1';";
                            model.setColumnIdentifiers(new String[]{"BlankNumber", "Type", "Employee_ID", "date_assign"});
                            break;
                        case "Unassigned Blanks":
                            query = "SELECT Blank.BlankNumber, Blank.Type " +
                                    "FROM Blank " +
                                    "WHERE Blank.isAssigned = '0';";
                            model.setColumnIdentifiers(new String[]{"BlankNumber", "Type"});
                        case "Blank Types":
                            query = "SELECT Blank.BlankNumber, Blank.Type " +
                                    "FROM Blank;";
                            model.setColumnIdentifiers(new String[]{"BlankNumber", "Type"});
                            break;
                        default:
                            return;
                    }

                    ResultSet rs = st.executeQuery(query);

                    while (rs.next()) {
                        String[] row;
                        switch (selectedOption) {
                            case "Sold Blanks":
                                row = new String[]{rs.getString("BlankNumber"), rs.getString("Type"), rs.getString("Employee_ID")};
                                break;
                            case "Assigned Blanks":
                                row = new String[]{rs.getString("BlankNumber"), rs.getString("Type"), rs.getString("Employee_ID"), rs.getString("date_assign")};
                                break;
                            case "Unassigned Blanks":
                                row = new String[]{rs.getString("BlankNumber"), rs.getString("Type")};
                                break;
                            case "Blank Types":
                                row = new String[]{rs.getString("BlankNumber"), rs.getString("Type")};
                                break;
                            default:
                                return;
                        }
                        model.addRow(row);
                    }
                    st.close();

                } catch (ClassNotFoundException | SQLException ex) {
                    ex.printStackTrace();
                }
            }

        });
        saleReportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OfficeSaleReports saleReportsButton = new OfficeSaleReports(ID, username);
                saleReportsButton.setVisible(true);
                dispose();
            }
        });

    }



    public static void main(String[] args){
        OfficeManagerBlanks Blanks = new OfficeManagerBlanks(ID,username);
        Blanks.show();


    }

}
