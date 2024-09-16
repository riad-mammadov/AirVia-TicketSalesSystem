package Admin.UserDetails;

import ButtonUtil.HoverButton;
import DB.DBConnectivity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 This class represents the update user details frame where the employee can update their own details.
 It retrieves the employee's details from the database and displays them in text fields.
 The employee can then edit the fields and click the update button to update their details in the database.
 */
public class UpdateUserDetails extends javax.swing.JFrame {


    private JTextField firstNameText;
    private JTextField lastNameText;
    private JTextField usernameText;
    private JTextField phoneNumberText;
    private JTextField emailText;
    private JTextField addressText;
    private JLabel employeeIDLabel;
    private JLabel roleLabel;
    private JLabel companyIDLabel;
    private JPanel mainPanel;
    private JButton updateDetailsButton;
    private JButton cancelButton;
    private  static int employee_id;
    private static int ID;
    private static  String username;
    private String first_name;
    private String last_name;
    private String role;
    private String phoneNumber;
    private String usernameEmployee;
    private String email;
    private String address;
    private String companyID;



    /**
     Constructs a new UpdateUserDetails frame for the specified employee.
     @param employee_id the ID of the employee whose details are being updated
     @param ID the ID of the currently logged-in user
     @param username the username of the currently logged-in user
     */
    public UpdateUserDetails(int employee_id, int ID, String username) {
        this.employee_id = employee_id;
        this.ID = ID;
        this.username = username;
        setContentPane(mainPanel);
        setSize(1500, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);


        try (Connection con = DBConnectivity.getConnection()) {
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement st = con.createStatement();
            String query = "SELECT Employee.First_name, Employee.Last_name, Employee.username, Employee.role, Employee.PhoneNumber, " +
                    "Employee.Email, Employee.Address, Employee.Company_ID \n" +
                    "FROM Employee \n" +
                    "WHERE Employee.Employee_ID = '"+employee_id+"' ";
            System.out.println(query);
            ResultSet rs = st.executeQuery(query);


            if(rs.next()){
                first_name = rs.getString(1);
                last_name = rs.getString(2);
                usernameEmployee = rs.getString(3);
                role = rs.getString(4);
                phoneNumber = rs.getString(5);
                email = rs.getString(6);
                address = rs.getString(7);
                companyID = rs.getString(8);

            }
            employeeIDLabel.setText(String.valueOf(employee_id));
            firstNameText.setText(first_name);
            lastNameText.setText(last_name);
            usernameText.setText(usernameEmployee);
            roleLabel.setText(role);
            phoneNumberText.setText(phoneNumber);
            emailText.setText(email);
            addressText.setText(address);
            companyIDLabel.setText(companyID);


        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        HoverButton.setButtonProperties(cancelButton);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                UserDetails userDetails = new UserDetails(ID, username);
                userDetails.show();
            }
        });

        HoverButton.setButtonProperties(updateDetailsButton);

        updateDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String message = "Are you sure you want to update this user?";


                // show a JOptionPane with YES_NO_OPTION and the warning message
                int option = JOptionPane.showConfirmDialog(null, message, "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);


                // check if the user clicked Yes
                if (option == JOptionPane.YES_OPTION) {
                    try (Connection con = DBConnectivity.getConnection()) {
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Statement st = con.createStatement();

                        String query = "BEGIN;" +
                                "SELECT * FROM Employee WHERE Employee_ID = '"+employee_id+"' FOR UPDATE;" +
                                "UPDATE Employee " +
                                "SET First_name = '" + firstNameText.getText() + "', " +
                                "Last_name = '" + lastNameText.getText() + "', " +
                                "username = '" + usernameText.getText() + "', " +
                                "PhoneNumber = '" + phoneNumberText.getText() + "', " +
                                "Email = '" + emailText.getText() + "', " +
                                "Address = '" + addressText.getText() + "' " +
                                "WHERE Employee_ID = '" + employee_id + "'; " +
                                "COMMIT; ";
                        System.out.println(query);
                        int rs = st.executeUpdate(query);
                    } catch (SQLException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }

                    dispose();
                    UserDetails userDetails = new UserDetails(ID, username);
                    userDetails.show();
                } else{
                    // do nothing
                }

            }
        });
    }

        public static void main(String[] args ){
        UpdateUserDetails updateUserDetails = new UpdateUserDetails(employee_id,ID,username);
        updateUserDetails.show();
    }
}
