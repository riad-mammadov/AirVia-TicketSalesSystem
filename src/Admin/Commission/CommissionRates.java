package Admin.Commission;

import Admin.Blanks.SystemStock;
import Admin.CustomerDetails.CustomerDetails;
import Admin.Home.SystemAdminHome;
import Admin.UserDetails.UserDetails;
import Admin.UserDetails.CreateUser;
import Authentication.EnterDate;
import ButtonUtil.HoverButton;
import DB.DBConnectivity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class CommissionRates extends javax.swing.JFrame {
    private JTable commissionTable;
    private JPanel mainPanel;
    private JButton homeButton;
    private JButton createUserButton;
    private JButton manageSystemStockButton;
    private JButton manageCommissionRatesButton;
    private JButton manageCustomerDetailsButton;
    private JButton manageUserDetailsButton;
    private static String username;
    private static int ID;
    private int selectedID;
    private JButton deleteCommissionRateButton;
    private JComboBox typeFilter;
    private JTextField fromDateText;
    private JTextField toDateFilter;
    private JButton deactivateCommissionRateButton;
    private JButton searchCommissionButton;
    private JButton allocateCommissionRateButton;
    private JButton activateCommissionRateButton;


    public CommissionRates(int ID, String username){

        this.username= username;
        this.ID= ID;
        setContentPane(mainPanel);
        setSize(1500, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        HoverButton.setButtonProperties(homeButton);

        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SystemAdminHome homeButton = new SystemAdminHome(ID,username, EnterDate.getDateToday());
                homeButton.setVisible(true);


            }
        });
        HoverButton.setButtonProperties(manageUserDetailsButton);

        manageUserDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                UserDetails manageUserDetailsButton = new UserDetails(ID,username);
                manageUserDetailsButton.setVisible(true);


            }
        });


        HoverButton.setButtonProperties(manageCustomerDetailsButton);

        manageCustomerDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CustomerDetails manageCustomerDetailsButton = new CustomerDetails(ID,username);
                manageCustomerDetailsButton.setVisible(true);



            }
        });

        HoverButton.setButtonProperties(manageCommissionRatesButton);

        manageCommissionRatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CommissionRates manageCommissionRatesButton = new CommissionRates(ID,username);
                manageCommissionRatesButton.setVisible(true);


            }
        });

        HoverButton.setButtonProperties(manageSystemStockButton);

        manageSystemStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SystemStock manageSystemStockButton = new SystemStock(ID,username);
                manageSystemStockButton.setVisible(true);



            }
        });

        HoverButton.setButtonProperties(createUserButton);

        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CreateUser createUserButton = new CreateUser(ID,username);
                createUserButton.setVisible(true);


            }
        });


        typeFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) commissionTable.getModel();
                model.setRowCount(0);
                String selected = (String) typeFilter.getSelectedItem();
                if(e.getSource() == typeFilter){
                    switch (selected) {
                        case "Interline" -> {
                            displayCommissionTable("'Interline'");
                        }
                        case "Domestic" -> {
                            displayCommissionTable("'Domestic'");
                        }
                        case "select all commission rates" ->{
                            displayCommissionTable("SELECT DISTINCT Commission.blankType FROM Commission");
                        }

                    }

                }

            }

        });

        HoverButton.setButtonProperties(deactivateCommissionRateButton);

        deactivateCommissionRateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = commissionTable.getSelectedRow(); // get the index of the selected row

                String message = "Are you sure you want to deactivate this commission rate?.";


                // show a JOptionPane with YES_NO_OPTION and the warning message
                int option = JOptionPane.showConfirmDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);


                if (selectedRow != -1) {
                    // check if the user clicked Yes
                    if (option == JOptionPane.YES_OPTION) {
                        // user clicked Yes, continue with the action


                        // get the value of the first column in the selected row
                        Object value = commissionTable.getModel().getValueAt(selectedRow, 0);

                        // cast the value to the appropriate type (e.g. String or Integer)
                        String stringID = (String) value;

                        selectedID = Integer.parseInt(stringID);

                        try (Connection con = DBConnectivity.getConnection()) {
                            assert con != null;
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Statement st = con.createStatement();
                            String query = "UPDATE Commission " +
                                    "SET Commission.deactivated = 0 " +
                                    "WHERE Commission_ID = '" + selectedID + "'";;
                            ;
                            System.out.println(query);
                            int rs = st.executeUpdate(query);

                            st.close();
                        } catch (SQLException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        // user clicked No, cancel the action
                        // ...
                    }
                }else {
                    // no row is selected, show an error message or do nothing
                    JOptionPane.showMessageDialog(null, "Please select a row from the table.");
                }
            }
        });

        HoverButton.setButtonProperties(deleteCommissionRateButton);


        deleteCommissionRateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = commissionTable.getSelectedRow(); // get the index of the selected row

                String message = "Are you sure you want to delete this commission rate?.";


                // show a JOptionPane with YES_NO_OPTION and the warning message
                int option = JOptionPane.showConfirmDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);


                if (selectedRow != -1) {
                    // check if the user clicked Yes
                    if (option == JOptionPane.YES_OPTION) {
                        // user clicked Yes, continue with the action


                        // get the value of the first column in the selected row
                        Object value = commissionTable.getModel().getValueAt(selectedRow, 0);

                        // cast the value to the appropriate type (e.g. String or Integer)
                        String stringID = (String) value;

                        selectedID = Integer.parseInt(stringID);

                        try (Connection con = DBConnectivity.getConnection()) {
                            assert con != null;
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Statement st = con.createStatement();
                            String query = "DELETE FROM Commission " +
                                    " WHERE Commission_ID = '" + selectedID + "'";
                            ;
                            System.out.println(query);
                            int rs = st.executeUpdate(query);
                        } catch (SQLException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        // user clicked No, cancel the action
                        // ...
                    }
                }else {
                    // no row is selected, show an error message or do nothing
                    JOptionPane.showMessageDialog(null, "Please select a row from the table.");
                }
            }
        });
        HoverButton.setButtonProperties(allocateCommissionRateButton);

        allocateCommissionRateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SetCommissionRate setCommissionRate = new SetCommissionRate(ID,username);
                setCommissionRate.show();
            }
        });
        HoverButton.setButtonProperties(activateCommissionRateButton);

        activateCommissionRateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = commissionTable.getSelectedRow(); // get the index of the selected row

                String message = "Are you sure you want to deactivate this commission rate?.";


                // show a JOptionPane with YES_NO_OPTION and the warning message
                int option = JOptionPane.showConfirmDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);


                if (selectedRow != -1) {
                    // check if the user clicked Yes
                    if (option == JOptionPane.YES_OPTION) {
                        // user clicked Yes, continue with the action


                        // get the value of the first column in the selected row
                        Object value = commissionTable.getModel().getValueAt(selectedRow, 0);

                        // cast the value to the appropriate type (e.g. String or Integer)
                        String stringID = (String) value;

                        selectedID = Integer.parseInt(stringID);

                        try (Connection con = DBConnectivity.getConnection()) {
                            assert con != null;
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Statement st = con.createStatement();
                            String query = "UPDATE Commission " +
                                    "SET Commission.deactivated = 1 " +
                                    "WHERE Commission_ID = '" + selectedID + "'";;
                            ;
                            System.out.println(query);
                            int rs = st.executeUpdate(query);
                        } catch (SQLException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        // user clicked No, cancel the action
                        // ...
                    }
                }else {
                    // no row is selected, show an error message or do nothing
                    JOptionPane.showMessageDialog(null, "Please select a row from the table.");
                }
            }
        });

        HoverButton.setButtonProperties(searchCommissionButton);

        searchCommissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) commissionTable.getModel();


                try(Connection con = DBConnectivity.getConnection()) {
                    int fromDateConverted = Integer.parseInt(fromDateText.getText().replace("/",""));
                    int toDateConverted = Integer.parseInt(toDateFilter.getText().replace("/",""));
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "SELECT Commission.Commission_ID, Commission.blankType, Commission.Rate," +
                            " Commission.Employee_ID, Commission.from_Date, Commission.to_Date, Commission.deactivated, Employee.First_name, Employee.Last_name \n" +
                            "FROM Commission \n" +
                            "INNER JOIN Employee " +
                            "ON Employee.Employee_ID = Commission.Employee_ID " +
                            "WHERE Commission.from_Date >= '"+fromDateConverted+"' AND Commission.to_Date <= '"+ toDateConverted+"'  ";
                    System.out.println(query);
                    ResultSet rs = st.executeQuery(query);
                    ResultSetMetaData rsmd = rs.getMetaData();

                    int cols = rsmd.getColumnCount();
                    String[] colName = new String[cols];
                    for(int i = 0; i < cols; i++){
                        colName[i] = rsmd.getColumnName(i+1);
                    }
                    model.setColumnIdentifiers(colName);
                    String commissionID,blankType,rate,employeeID,fromDate,toDate,firstName,lastName,deactivated;
                    while(rs.next()){
                        commissionID = rs.getString(1);
                        blankType = rs.getString(2);
                        rate = rs.getString(3);
                        employeeID = rs.getString(4);
                        fromDate = rs.getString(5);
                        toDate = rs.getString(6);
                        deactivated = rs.getString(7);
                        firstName = rs.getString(8);
                        lastName = rs.getString(9);
                        String[] row = {commissionID,blankType,rate,employeeID,fromDate,toDate,deactivated, firstName,lastName};
                        model.addRow(row);
                    }
                    st.close();

                } catch (ClassNotFoundException | SQLException ex) {
                    ex.printStackTrace();
                }


            }
        });
    }

    /**
     Displays the commission table based on the given blankConstraint.
     @param blankConstraint the blank type constraint to filter the results
     */
    private void displayCommissionTable(String blankConstraint) {

        // Get the default table model of the commission table
        DefaultTableModel model = (DefaultTableModel) commissionTable.getModel();

        // Set the model to the commission table
        commissionTable.setModel(model);

        // Clear the rows of the model
        model.setRowCount(0);
        try(Connection con = DBConnectivity.getConnection()) {
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement st = con.createStatement();
            String query = "SELECT Commission.Commission_ID, Commission.blankType, Commission.Rate," +
                    " Commission.Employee_ID, Commission.from_Date, Commission.to_Date, Commission.deactivated, Employee.First_name, Employee.Last_name \n" +
                    "FROM Commission \n" +
                    "INNER JOIN Employee " +
                    "ON Employee.Employee_ID = Commission.Employee_ID " +
                    "WHERE Commission.blankType IN ("+blankConstraint+") ";
            System.out.println(query);
            ResultSet rs = st.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();

            int cols = rsmd.getColumnCount();
            String[] colName = new String[cols];
            for(int i = 0; i < cols; i++){
                colName[i] = rsmd.getColumnName(i+1);
            }
            model.setColumnIdentifiers(colName);
            String commissionID,blankType,rate,employeeID,fromDate,toDate,firstName,lastName,deactivated;
            while(rs.next()){
                commissionID = rs.getString(1);
                blankType = rs.getString(2);
                rate = rs.getString(3);
                employeeID = rs.getString(4);
                fromDate = rs.getString(5);
                toDate = rs.getString(6);
                deactivated = rs.getString(7);
                firstName = rs.getString(8);
                lastName = rs.getString(9);
                String[] row = {commissionID,blankType,rate,employeeID,fromDate,toDate,deactivated, firstName,lastName};
                model.addRow(row);
            }
            st.close();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CommissionRates commissionRates = new CommissionRates(ID,username);
        commissionRates.show();
    }
}
