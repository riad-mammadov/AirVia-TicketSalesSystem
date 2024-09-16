package Admin.UserDetails;

import Admin.Blanks.SystemStock;
import Admin.Commission.CommissionRates;
import Admin.CustomerDetails.CustomerDetails;
import Authentication.Login;
import ButtonUtil.HoverButton;
import DB.DBConnectivity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


/**
 A class that displays and manages user details.
 */
public class UserDetails extends javax.swing.JFrame {
    private JPanel mainPanel;
    private JTable userTable;
    private JButton deleteUserButton;
    private JButton changeAccessRoleButton;
    private JComboBox roleCombobox;
    private JTextField employeeIDText;
    private JButton searchUserButton;
    private static int ID;
    private static String username;
    private int selectedID;
    private DefaultTableModel model;

    private JPanel userDetails;
    private JButton homeButton;
    private JButton createUserButton;
    private JButton manageSystemStockButton;
    private JButton manageCommissionRatesButton;
    private JButton manageCustomerDetailsButton;
    private JButton manageUserDetailsButton;
    private JButton updateDetailsButton;
    private int employee_ID;


    /**
     * Constructs a UserDetails object with specified ID and username.
     * @param ID the ID of the user
     * @param username the username of the user
     */
    public UserDetails(int ID, String username) {
        setContentPane(mainPanel);
        setSize(1500, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        model = (DefaultTableModel) userTable.getModel();

        roleCombobox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) userTable.getModel();
                model.setRowCount(0);
                String selected = (String) roleCombobox.getSelectedItem();
                if (e.getSource() == roleCombobox) {
                    switch (selected) {
                        case "advisor" -> {
                            displayUserTable("'advisor'");
                        }
                        case "admin" -> {
                            displayUserTable("'admin'");
                        }
                        case "officeManager" -> {
                            displayUserTable("'officeManager'");
                        }
                        case "select all" -> {
                            displayUserTable("SELECT DISTINCT Employee.role FROM Employee");
                        }

                    }

                }
            }
        });


        HoverButton.setButtonProperties(searchUserButton);

        searchUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                model.setRowCount(0);
                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "SELECT Employee.Employee_ID, Employee.First_name, Employee.Last_name, Employee.username, Employee.role, Employee.PhoneNumber, " +
                            "Employee.Email, Employee.Address, Employee.Company_ID \n" +
                            "FROM Employee \n" +
                            "WHERE Employee.First_name = '" + employeeIDText.getText() + "' OR Employee.Last_name = '" + employeeIDText.getText() + "' OR Employee.username = '" + employeeIDText.getText() + "'  ";
                    System.out.println(query);
                    ResultSet rs = st.executeQuery(query);
                    ResultSetMetaData rsmd = rs.getMetaData();

                    int cols = rsmd.getColumnCount();
                    String[] colName = new String[cols];
                    for (int i = 0; i < cols; i++) {
                        colName[i] = rsmd.getColumnName(i + 1);
                    }
                    model.setColumnIdentifiers(colName);
                    String employee_ID, first_name, last_name, username, role, phoneNumber, email, address, companyID;
                    while (rs.next()) {
                        employee_ID = rs.getString(1);
                        first_name = rs.getString(2);
                        last_name = rs.getString(3);
                        username = rs.getString(4);
                        role = rs.getString(5);
                        phoneNumber = rs.getString(6);
                        email = rs.getString(7);
                        address = rs.getString(8);
                        companyID = rs.getString(9);
                        String[] row = {employee_ID, first_name, last_name, username, role, phoneNumber, email, address, companyID};
                        model.addRow(row);
                    }
                    st.close();

                } catch (ClassNotFoundException | SQLException ex) {
                    ex.printStackTrace();
                }

            }
        });

        HoverButton.setButtonProperties(deleteUserButton);

        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow(); // get the index of the selected row

                String message = "Are you sure you want to delete this user?" +
                        " This is a crucial operation and the user will be deleted permanently.";


                // show a JOptionPane with YES_NO_OPTION and the warning message
                int option = JOptionPane.showConfirmDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);


                if (selectedRow != -1) {
                    // check if the user clicked Yes
                    if (option == JOptionPane.YES_OPTION) {
                        // user clicked Yes, continue with the action


                        // get the value of the first column in the selected row
                        Object value = userTable.getModel().getValueAt(selectedRow, 0);

                        // cast the value to the appropriate type (e.g. String or Integer)
                        String stringID = (String) value;

                        selectedID = Integer.parseInt(stringID);

                        try (Connection con = DBConnectivity.getConnection()) {
                            assert con != null;
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Statement st = con.createStatement();
                            String query = "DELETE FROM Employee " +
                                    " WHERE Employee_ID = '" + selectedID + "'";
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

        HoverButton.setButtonProperties(changeAccessRoleButton);

        changeAccessRoleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow(); // get the index of the selected row



                if (selectedRow != -1) {
                    // get the value of the first column in the selected row
                    Object value = userTable.getModel().getValueAt(selectedRow, 0);

                    // cast the value to the appropriate type (e.g. String or Integer)
                    String stringID = (String) value;

                    selectedID = Integer.parseInt(stringID);

                    Object[] options = {"Admin", "Office Manager", "Advisor"};
                    int selection = JOptionPane.showOptionDialog(null, "Please select the role you want to specify for this user:", "Role Selection",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

                    switch (selection) {
                        // User selected "Admin" option
                        case 0 -> {
                            updateRole("admin");
                        }
                        case 1 -> {
                            // User selected "Office Manager" option
                            updateRole("officeManager");
                        }
                        case 2 -> {
                            // User selected "Advisor" option
                            updateRole("advisor");
                        }
                    }

                } else {
                    // no row is selected, show an error message or do nothing
                    JOptionPane.showMessageDialog(null, "Please select a row from the table.");
                }
            }

        });

        HoverButton.setButtonProperties(updateDetailsButton);

        updateDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow(); // get the index of the selected row
                // get the value of the first column in the selected row
                Object value = userTable.getModel().getValueAt(selectedRow, 0);

                // cast the value to the appropriate type (e.g. String or Integer)
                String stringID = (String) value;

                selectedID = Integer.parseInt(stringID);

                employee_ID = selectedID;

                if (selectedRow != -1) {
                    dispose();
                    UpdateUserDetails updateUserDetails = new UpdateUserDetails(employee_ID,ID,username);
                    updateUserDetails.show();
                }
                else{
                    // no row is selected, show an error message or do nothing
                    JOptionPane.showMessageDialog(null, "Please select a row from the table.");
                }

            }
        });
        HoverButton.setButtonProperties(manageUserDetailsButton);

        manageUserDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                UserDetails manageUserDetailsButton = new UserDetails(ID, username);
                manageUserDetailsButton.setVisible(true);


            }
        });
        HoverButton.setButtonProperties(manageCustomerDetailsButton);

        manageCustomerDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CustomerDetails manageCustomerDetailsButton = new CustomerDetails(ID, username);
                manageCustomerDetailsButton.setVisible(true);


            }
        });

        HoverButton.setButtonProperties(manageCommissionRatesButton);

        manageCommissionRatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CommissionRates manageCommissionRatesButton = new CommissionRates(ID, username);
                manageCommissionRatesButton.setVisible(true);


            }
        });


        HoverButton.setButtonProperties(manageSystemStockButton);

        manageSystemStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SystemStock manageSystemStockButton = new SystemStock(ID, username);
                manageSystemStockButton.setVisible(true);



            }
        });

        HoverButton.setButtonProperties(createUserButton);

        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CreateUser createUserButton = new CreateUser(ID, username);
                createUserButton.setVisible(true);


            }
        });
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Login login = new Login();
                login.show();
            }
        });
    }




        /**
         * This method displays the user table with the specified constraint
         * @param Constraint a String that represents the constraint for the query to fetch user data
         */
        public void displayUserTable (String Constraint){
            DefaultTableModel model = (DefaultTableModel) userTable.getModel();
            model.setRowCount(0);
            try (Connection con = DBConnectivity.getConnection()) {
                assert con != null;
                Class.forName("com.mysql.cj.jdbc.Driver");
                Statement st = con.createStatement();
                String query = "SELECT Employee.Employee_ID, Employee.First_name, Employee.Last_name, Employee.username, Employee.role, Employee.PhoneNumber, " +
                        "Employee.Email, Employee.Address, Employee.Company_ID \n" +
                        "FROM Employee \n" +
                        "WHERE Employee.Company_ID = 1 AND Employee.role IN (" + Constraint + ") ";
                System.out.println(query);
                ResultSet rs = st.executeQuery(query);
                ResultSetMetaData rsmd = rs.getMetaData();

                int cols = rsmd.getColumnCount();
                String[] colName = new String[cols];
                for (int i = 0; i < cols; i++) {
                    colName[i] = rsmd.getColumnName(i + 1);
                }
                model.setColumnIdentifiers(colName);
                String employee_ID, first_name, last_name, username, role, phoneNumber, email, address, companyID;
                // iterate over the result set and add the data to the table model
                while (rs.next()) {
                    employee_ID = rs.getString(1);
                    first_name = rs.getString(2);
                    last_name = rs.getString(3);
                    username = rs.getString(4);
                    role = rs.getString(5);
                    phoneNumber = rs.getString(6);
                    email = rs.getString(7);
                    address = rs.getString(8);
                    companyID = rs.getString(9);
                    String[] row = {employee_ID, first_name, last_name, username, role, phoneNumber, email, address, companyID};
                    model.addRow(row);
                }
                st.close();

            } catch (ClassNotFoundException | SQLException ex) {
                ex.printStackTrace();
            }

        }


    /**

     Updates the role of the employee with the given selected ID in the database
     @param role the new role to update to
     */
        public void updateRole (String role){
            try (Connection con = DBConnectivity.getConnection()) {
                assert con != null;
                Class.forName("com.mysql.cj.jdbc.Driver");
                Statement st = con.createStatement();
                String query = "UPDATE Employee " +
                        "SET role = '" + role + "' " +
                        "WHERE Employee_ID = '" + selectedID + "' ";
                System.out.println(query);
                int rs = st.executeUpdate(query);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }



        public static void main (String[]args){
            UserDetails userDetails = new UserDetails(ID, username);
            userDetails.show();
        }

}

