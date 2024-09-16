package Authentication;

import DB.DBConnectivity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;



/**
 The ChangePassword class provides a GUI for an employee to change their password.
 */
public class ChangePassword extends javax.swing.JFrame  {
    private JPanel mainPanel;
    private JButton cancelButton;
    private JButton changePasswordButton;
    private JPasswordField enterPasswordText;
    private JPasswordField confirmPasswordText;

    private static int ID;
    private static String username;



    /**
     * Constructor for the ChangePassword class.
     * @param ID the ID of the employee
     * @param username the username of the employee
     */
    public ChangePassword(int ID, String username) {
        this.ID = ID;
        this.username = username;
        setContentPane(mainPanel);
        setSize(700,700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);


        // Add an action listener for the changePasswordButton
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String passEntered = String.valueOf(enterPasswordText.getPassword());
                String passConfirmed = String.valueOf(confirmPasswordText.getPassword());
                if(passEntered.equals(passConfirmed)){
                    System.out.println(passConfirmed);
                    try (Connection con = DBConnectivity.getConnection()) {
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Statement st = con.createStatement();

                        String query =
                                "UPDATE Employee SET Employee.pass_word = '" + passConfirmed + "', " +
                                "Employee.is_new = 0 " +
                                "WHERE Employee_ID = '" + ID + "'; ";
                        System.out.println(query);
                        int rs = st.executeUpdate(query);
                    } catch (SQLException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                    dispose();
                    Login login = new Login();
                    login.show();
                }
                else{
                    JOptionPane.showMessageDialog(mainPanel,"The passwords do not match");
                }
            }
        });


        // Add an action listener for the cancelButton
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Login login = new Login();
                login.show();

            }
        });
    }
}
