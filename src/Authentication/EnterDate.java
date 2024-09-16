package Authentication;

import Admin.Home.SystemAdminHome;
import Advisor.Home.TravelAdvisorHome;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 The EnterDate class is a JFrame that prompts the user to enter today's date in the format dd/MM/yyyy.
 The entered date is stored as an integer in the static field dateToday.
 The class also has buttons for the user to log out and continue to the system home page.
 */
public class EnterDate extends javax.swing.JFrame {
    private JTextField dateTodayText;
    private JButton continueButton;
    private JButton logOutButton;
    private JPanel mainPanel;

    /**
     * Getter method for the dateToday field.
     * @return The integer value of the date entered by the user.
     */
    public static int getDateToday() {
        return dateToday;
    }

    private static int dateToday;



    /**
     * The constructor initializes the EnterDate JFrame and sets up action listeners for the continue and log out buttons.
     * @param ID The ID of the user logged in to the system
     * @param username The username of the user logged in to the system
     */
    public EnterDate(int ID, String username) {
        setContentPane(mainPanel);
        setSize(1000,600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Login login = new Login();
                login.show();
            }
        });
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = dateTodayText.getText().replace("/","");
                dateToday = Integer.parseInt(date);
                dispose();
                SystemAdminHome home = new SystemAdminHome(ID,username,dateToday);

            }
        });
    }
}
