package Manager;

import Authentication.Login;
import DB.DBConnectivity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;



/**
 This class represents the user interface for managing stock of blanks.
 It allows the office manager to view the current stock of blanks, assign and reassign blanks to travel advisors,
 generate a stock report and view a report of open blanks.
 */
public class OfficeManagerStock extends javax.swing.JFrame {


    private JPanel Stock;
    private JButton logOutButton;
    private JTable stockTable;
    private JButton homeButton;
    private JButton discountPlanButton;
    private JButton blanksButton;
    private JButton stockButton;
    private JButton ticketStockTurnOverButton;
    private JComboBox assignTravelAdvisor;
    private JComboBox assignBlank;
    private JButton submitAssignBlanks;
    private JButton submitReassignBlanksButton;
    private JComboBox blankTypeTable;
    private JButton showStockButton;
    private JScrollPane stockTableScroll;
    private JComboBox ReassignAdvisor;
    private JComboBox reasssignBlank;
    private JTextField lowerRange;
    private JTextField upperRange;
    private JTextField assignDate;
    private JComboBox SelectBlankType;
    private JTextField EnterReassignDate;
    private JComboBox SelectReassignBlankType;
    private JTextField lowerRangeReassign;
    private JTextField upperRangeReassign;
    private JLabel usernameLabel;
    private JButton saleReportsButton;
    private JButton openBlankReportButton;
    private static int ID;
    private static String username;



    /**
     Validates the user input for assigning or reassigning blanks.
     Checks if the lower and upper batch numbers are exactly 9 digits and the lower batch is not higher than the upper batch.
     @return true if the input is valid, false otherwise
     */
    private boolean validateInput() {
        String lowerInput = lowerRange.getText();
        String upperInput = upperRange.getText();

        if (lowerInput.length() != 9 || upperInput.length() != 9) {
            JOptionPane.showMessageDialog(null, "Blank number must be exactly 9 digits");
            return false;
        }

        if (Long.parseLong(lowerInput) >= Long.parseLong(upperInput)) {
            JOptionPane.showMessageDialog(null, "Lower batch shouldn't be higher than the upper batch");
            return false;
        }

        JOptionPane.showMessageDialog(null, "Input is valid");
        return true;
    }





    /**
     Appends a given message to the log file.
     @param message the message to be appended to the log file
     */
    private void appendToLogFile(String message) {
        File logFile = new File("/Users/aadilghani/Desktop/AirViaTicketSalesSystem/src/LogFile Report.txt");

        try (FileWriter fw = new FileWriter(logFile, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     Creates a new instance of the OfficeManagerStock class.
     @param ID the ID of the office manager
     @param username the username of the office manager
     */
    public OfficeManagerStock(int ID, String username) {

        stockTable.setPreferredScrollableViewportSize(new Dimension(500, 500));
        stockTableScroll.setPreferredSize(new Dimension(500, 500));
        this.username = username;
        this.ID = ID;
        usernameLabel.setText("Manager: "+ username);


        setContentPane(Stock);
        setSize(1000, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        // add this to assign and reassign somehow

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(currentDate);






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



        showStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    //String query = "SELECT  Blank.BlankNumber FROM Blank";
                    String query = "SELECT Blank.BlankNumber, Employee.Employee_ID, Employee.role " +
                            "FROM Blank " +
                            "JOIN Employee ON Employee.Employee_ID = Blank.Employee_ID " +
                            "WHERE Employee.role = 'officemanager'";



                    ResultSet rs = st.executeQuery(query);
                    ResultSetMetaData rsmd = rs.getMetaData();
                    DefaultTableModel model = (DefaultTableModel) stockTable.getModel();

                    int cols = rsmd.getColumnCount();
                    String[] colName = new String[cols];
                    for (int i = 0; i < cols; i++) {
                        colName[i] = rsmd.getColumnName(i + 1);
                    }
                    model.setColumnIdentifiers(colName);
                    String blankNumber;
                    while (rs.next()) {
                        blankNumber = rs.getString(1);


                        String[] row = {blankNumber};
                        model.addRow(row);


                    }
                    st.close();

                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }


        });


        assignTravelAdvisor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "SELECT  Employee.Employee_ID FROM Employee where Employee.role = 'advisor'";

                    ResultSet rs = st.executeQuery(query);

                    while (rs.next()) {
                        int Id = rs.getInt("Employee_ID");
                        assignTravelAdvisor.addItem(Id);
                    }

                    st.close();

                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });



        ReassignAdvisor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "SELECT  Employee.Employee_ID FROM Employee where Employee.role = 'advisor'";


                    ResultSet rs = st.executeQuery(query);

                    while (rs.next()) {
                        //assignTravelAdvisor.addItem(rs.getString("BlankNumber"));
                        int id = rs.getInt("Employee_ID");
                        ReassignAdvisor.addItem(id);
                    }

                    st.close();

                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }


            }
        });






        submitAssignBlanks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                validateInput();
                String lowerRangeText = lowerRange.getText();
                String upperRangeText = upperRange.getText();
                int assignAdvisorID = (int) assignTravelAdvisor.getSelectedItem();

                int lowerBound = Integer.parseInt(lowerRangeText);
                int upperBound = Integer.parseInt(upperRangeText);

                String blankType = (String) SelectBlankType.getSelectedItem();

                try {
                    int assignDateBlank = Integer.parseInt(assignDate.getText().replace("/", ""));

                    try (Connection con = DBConnectivity.getConnection()) {
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");

                        // Check if the blanks are initially assigned to the manager

                        //String checkManagerQuery = "SELECT * FROM Blank WHERE BlankNumber BETWEEN ? AND ? AND Manager_ID = ?";
                        String checkManagerQuery = "SELECT Blank.BlankNumber, Employee.Employee_ID, Employee.role " +
                                "FROM Blank " +
                                "JOIN Employee ON Employee.Employee_ID = Blank.Employee_ID " +
                                "WHERE Employee.role = 'officemanager' AND BlankNumber BETWEEN ? AND ? " ;

                        PreparedStatement checkManagerStatement = con.prepareStatement(checkManagerQuery);
                        //checkManagerStatement.setInt(1, lowerBound);
                        //checkManagerStatement.setInt(2, upperB1ound);
                        //checkManagerStatement.setString(3, String.valueOf(ID));
                        checkManagerStatement.setInt(1,lowerBound);
                        checkManagerStatement.setInt(2,upperBound);
                        ResultSet resultSet = checkManagerStatement.executeQuery();

                        int validBlanksCount = 0;
                        while (resultSet.next()) {
                            validBlanksCount++;
                        }

                        System.out.println((upperBound - lowerBound + 1) + " UPPLOW");
                        System.out.println(validBlanksCount + "Blsnkcount");
                        System.out.println(upperBound + " UPPER");
                        System.out.println(lowerBound + " LOWER");
                        if (validBlanksCount == (upperBound - lowerBound + 1)) {
                            // Proceed with the assignment

                            String query = "UPDATE Blank SET Employee_ID = ?, date_assign = ?, Type = ?, isAssigned = ? WHERE BlankNumber BETWEEN ? AND ?";
                            PreparedStatement preparedStatement = con.prepareStatement(query);
                            preparedStatement.setInt(1, assignAdvisorID);
                            preparedStatement.setInt(2, assignDateBlank);
                            preparedStatement.setString(3, blankType);
                            preparedStatement.setInt(4, 1);
                            preparedStatement.setInt(5, lowerBound);
                            preparedStatement.setInt(6, upperBound);
                            //preparedStatement.setString(7, String.valueOf(ID));
                            int affectedRows = preparedStatement.executeUpdate();

                            if (affectedRows > 0) {
                                JOptionPane.showMessageDialog(null, "Database has been affected.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "No rows were affected.", "Information", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            // Show an error message when not all blanks belong to the manager
                            JOptionPane.showMessageDialog(null, "These blanks are not assigned to you.", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        submitReassignBlanksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                validateInput();
                String lowerRangeReassignTextText = lowerRangeReassign.getText();
                String upperRangeReassignTextText = upperRangeReassign.getText();
                int ReassignAdvisorid = (int) ReassignAdvisor.getSelectedItem();

                int lowerBoundReassign = Integer.parseInt(lowerRangeReassignTextText);
                int upperBoundReassign = Integer.parseInt(upperRangeReassignTextText);

                String blankType = (String) SelectReassignBlankType.getSelectedItem();

                try {
                    int reassignDateBlank = Integer.parseInt(EnterReassignDate.getText().replace("/", ""));

                    try (Connection con = DBConnectivity.getConnection()) {
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");

                        // Check if the blanks are initially assigned to an advisor, not sold, and not assigned to the same advisor
                        String checkAssignedQuery = "SELECT * FROM Blank WHERE BlankNumber BETWEEN ? AND ? AND Employee_ID IS NOT NULL AND Employee_ID != ? AND isSold = 0";
                        PreparedStatement checkAssignedStatement = con.prepareStatement(checkAssignedQuery);
                        checkAssignedStatement.setInt(1, lowerBoundReassign);
                        checkAssignedStatement.setInt(2, upperBoundReassign);
                        checkAssignedStatement.setInt(3, ReassignAdvisorid);
                        ResultSet resultSet = checkAssignedStatement.executeQuery();

                        int validBlanksCount = 0;
                        while (resultSet.next()) {
                            validBlanksCount++;
                        }

                        if (validBlanksCount == (upperBoundReassign - lowerBoundReassign + 1)) {
                            // Proceed with the reassignment
                            String query = "UPDATE Blank SET Employee_ID = ?, date_assign = ?, Blank.Type = ?  WHERE BlankNumber BETWEEN ? AND ? AND isSold = 0";
                            PreparedStatement preparedStatement = con.prepareStatement(query);
                            preparedStatement.setInt(1, ReassignAdvisorid);
                            preparedStatement.setInt(2, reassignDateBlank);
                            preparedStatement.setString(3, blankType);
                            preparedStatement.setInt(4, lowerBoundReassign);
                            preparedStatement.setInt(5, upperBoundReassign);
                            int affectedRows = preparedStatement.executeUpdate();

                            if (affectedRows > 0) {
                                JOptionPane.showMessageDialog(null, "Database has been affected.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "No rows were affected.", "Information", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } else {
                            // Show an error message when not all blanks meet the conditions
                            JOptionPane.showMessageDialog(null, "These blanks have not been assigned to an advisor, have been sold, or are already assigned to the same advisor.", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        SelectBlankType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectBlankType.addItem("Interline");
                SelectBlankType.addItem("Domestic");
            }
        });


        blankTypeTable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               // blankTypeTable.addItem();
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
        OfficeManagerStock Stock = new OfficeManagerStock(ID,username);
        Stock.show();
  }
}
