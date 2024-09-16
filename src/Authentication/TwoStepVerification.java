package Authentication;

import Advisor.Home.TravelAdvisorHome;
import DB.DBConnectivity;
import Manager.OfficeManagerHome;
import SMTP.Mail;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;



/**

 The TwoStepVerification class is responsible for generating a 6-digit verification code, sending it via email to
 the user's email address, and verifying that the code entered by the user matches the code generated. The class
 also determines the role of the user based on their Employee ID and directs them to the appropriate home screen.
 @author [Alex Elemele]
 @version 1.0
 @since [13.04.2023]
 */
public class TwoStepVerification  extends javax.swing.JFrame  {
    private int sixDigitCode;
    private String role;
    private JTextField verifCodeText;
    private JButton continueButton;
    private JButton resendCodeButton;
    private JPanel mainPanel;
    private static int ID;
    private static String username;
    private String employeeEmail;


    /**
     * Constructs a new instance of the TwoStepVerification class.
     * @param ID the Employee ID of the user.
     * @param username the username of the user.
     */
    public TwoStepVerification(int ID, String username) {
        this.ID = ID;
        this.username = username;

        setContentPane(mainPanel);
        setSize(1000,600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        sixDigitCode = generateVerificationCode();

        try (Connection con = DBConnectivity.getConnection()) {
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement st = con.createStatement();
            String query = "SELECT Employee.Email, Employee.role " +
                        "FROM Employee " +
                    "WHERE Employee.Employee_ID = '" + ID + "' ";
            System.out.println(query);
            ResultSet rs = st.executeQuery(query);

            if(rs.next()){
                employeeEmail = rs.getString("Email");
                role = rs.getString("role");
            }

            st.close();
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        Mail mail = new Mail();
        mail.setupServerProperties();

        try {
            mail.draftEmail(employeeEmail,"Dear User, please use the following verification code to log into the AirVia system:" +
                    " " + sixDigitCode);
        } catch (MessagingException | IOException ex) {
            ex.printStackTrace();
        }
        try {
            mail.sendEmail();
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }




        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int inputtedCode = Integer.parseInt(verifCodeText.getText());
                if(sixDigitCode == inputtedCode){
                    JOptionPane.showMessageDialog(mainPanel, "Verification process successful");

                    switch (role) {
                        case "officeManager" -> {
                            dispose();
                            OfficeManagerHome officeHome = new OfficeManagerHome(ID, username);
                            officeHome.setVisible(true);
                            officeHome.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                        }
                        case "advisor" -> {
                            dispose();
                            TravelAdvisorHome advisorHome = new TravelAdvisorHome(ID, username);
                            advisorHome.setVisible(true);
                            advisorHome.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                        }
                        case "admin" -> {
                            dispose();
                            EnterDate enterDate = new EnterDate(ID,username);
                            enterDate.setVisible(true);
                            enterDate.show();
                        }
                    }
                }
                else{
                    JOptionPane.showMessageDialog(mainPanel, "Code not valid. Please ensure you enter the correct code");
                }

            }
        });

        resendCodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sixDigitCode = generateVerificationCode();
                try {
                    mail.draftEmail(employeeEmail,"Dear User, please use the following verification code to log into the AirVia system:" +
                            " " + sixDigitCode);
                } catch (MessagingException | IOException ex) {
                    ex.printStackTrace();
                }
                try {
                    mail.sendEmail();
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }


            }
        });
        verifCodeText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if (!Character.isDigit(c)){
                    e.consume();
                }
            }
        });
    }

    public static int generateVerificationCode() {
        Random random = new Random();
        int min = 100000; // minimum 6-digit number
        int max = 999999; // maximum 6-digit number
        return random.nextInt(max - min + 1) + min;

    }

    public static void main(String[]args){
        TwoStepVerification twoStepVerification = new TwoStepVerification(ID,username);
        twoStepVerification.show();
    }
}
