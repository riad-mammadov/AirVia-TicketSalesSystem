package Authentication;

import DB.DBConnectivity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



/**
 The Login class represents a user login page where the user enters their username and password.
 Upon successful login, the user is directed to either a two-step verification page or a change password page.
 @author [Alex Elemele]
 @version 1.0
 @since 2023-04-13
 */
public class Login extends javax.swing.JFrame {
    public String role;
    private JTextField usernameText;
    private JLabel UsernameLabel;
    private JLabel PasswordLabel;
    private JButton loginButton;
    private JButton resetButton;
    private JPasswordField passwordField;
    private JPanel loginBackground;

    private String username;
    private String password;
    private int ID;
    private int is_new;


    /**
     * Constructor for Login class that sets the content pane and visibility of the page
     */
    public Login() {
        // MAKES THE PAGE VISIBLEu
        setContentPane(loginBackground);
        setSize(600,600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        // ACTION ON RESET BUTTON
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usernameText.setText("");
                passwordField.setText("");
            }
        });

        // ACTION ON LOGIN BUTTON
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                username = usernameText.getText();
                password = String.valueOf(passwordField.getPassword());


                // TRY TO CONNECT TO DATABASE
                try(Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    PreparedStatement stm = con.prepareStatement("Select Employee_ID, role , is_new from Employee where pass_word = ? and username = ? ");
                    stm.setString(1, password);
                    stm.setString(2, username);
                    System.out.println(username);
                    System.out.println(password);
                    System.out.println(is_new);
                    ResultSet rs = stm.executeQuery();
                    if (rs.next()) {
                        role = rs.getString("role");
                        ID = rs.getInt("Employee_ID");
                        is_new = rs.getInt("is_new");
                        if(is_new == 0) {
                            dispose();
                            TwoStepVerification twoStepVerification = new TwoStepVerification(ID, username);
                            twoStepVerification.show();
                        }

                        else if (is_new == 1){
                            dispose();
                            ChangePassword changePassword = new ChangePassword(ID,username);
                            changePassword.show();
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(loginBackground,"Invalid password or username");
                    }
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Gets the username of the logged-in user
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the ID of the logged-in user
     * @return the ID of the user
     */
    public int getID() {
        return ID;
    }

    /**
     * Main method to instantiate Login class and show the page
     */
    public static void main(String[]args){
        Login log = new Login();
        log.show();
    }
}
