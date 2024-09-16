package Admin.Home;


import Admin.Blanks.SystemStock;
import Admin.Commission.CommissionRates;
import Admin.CustomerDetails.CustomerDetails;
import Admin.UserDetails.CreateUser;
import Admin.UserDetails.UserDetails;
import Authentication.Login;
import ButtonUtil.HoverButton;
import DB.DBConnectivity;
import SMTP.Mail;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 The SystemAdminHome class represents the home page for the system administrator, which allows the administrator
 to manage user details, customer details, commission rates, system stock, and backups. It also provides the option
 to view alerts, give feedback, and logout of the system. This class extends the javax.swing.JFrame class and
 implements the ActionListener interface to handle button click events.
 */
public class SystemAdminHome extends javax.swing.JFrame {
    private JButton homeButton;
    private JButton manageUserDetailsButton;
    private JButton customerDetailsButton;
    private JButton manageCommissionRatesButton;
    private JButton manageSystemStockButton;
    private JButton restoreButton;
    private JButton backUpButton;
    private JButton createUserButton;
    private JButton logOutButton;
    private JPanel adminPage;
    private JButton showAlertsButton;
    private JButton giveFeedbackButton;
    private static int ID;
    private static String username;
    String location = null;
    String filename;

    private String dbHost;
    private String dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    private File backupFile;
    private static int dateToday;
    private String email;
    private String firstname;
    private String surname;



    /**
     Creates a new instance of SystemAdminHome with the specified ID, username, and dateToday.
     @param ID the ID of the system administrator
     @param username the username of the system administrator
     @param dateToday the current date in the format yymmdd
     */
    public SystemAdminHome(int ID, String username, int dateToday){
        this.ID = ID;
        this.username = username;
        this.dateToday = dateToday;
        setContentPane(adminPage);
        setSize(1000,600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        dbHost = "smcse-stuproj00.city.ac.uk";
        dbPort = "3306";
        dbUser = "in2018g01_a";
        dbPassword = "G3pm6gib";
        dbName = "in2018g01";

        List<Integer> possibleAlerts = payLaterMessage();
        if (possibleAlerts.isEmpty()){
            // do nothing
        }
        else{
            showPopup();
        }


        HoverButton.setButtonProperties(logOutButton);
        logOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Login login = new Login();
                login.setVisible(true);


            }
        });



        HoverButton.setButtonProperties(homeButton);
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SystemAdminHome homeButton = new SystemAdminHome(ID,username, dateToday);
                homeButton.setVisible(true);
                dispose();

            }
        });


        HoverButton.setButtonProperties(manageUserDetailsButton);
        manageUserDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserDetails manageUserDetailsButton = new UserDetails(ID,username);
                manageUserDetailsButton.setVisible(true);
                dispose();

            }
        });


        HoverButton.setButtonProperties(customerDetailsButton);
        customerDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerDetails manageCustomerDetailsButton = new CustomerDetails(ID,username);
                manageCustomerDetailsButton.setVisible(true);
                dispose();


            }
        });

        HoverButton.setButtonProperties(manageCommissionRatesButton);
        manageCommissionRatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                CommissionRates manageCommissionRatesButton = new CommissionRates(ID,username);
                manageCommissionRatesButton.setVisible(true);
                dispose();

            }
        });

        HoverButton.setButtonProperties(manageSystemStockButton);
        manageSystemStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SystemStock manageSystemStockButton = new SystemStock(ID,username);
                manageSystemStockButton.setVisible(true);
                dispose();


            }
        });
        HoverButton.setButtonProperties(createUserButton);
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateUser createUserButton = new CreateUser(ID,username);
                createUserButton.setVisible(true);
                dispose();

            }
        });
        HoverButton.setButtonProperties(restoreButton);
        restoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(null, "Restoring the database will overwrite any existing data. Are you sure you want to continue?", "Confirm Database Restore", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    JFileChooser fileChooser = new JFileChooser();
                    int result2 = fileChooser.showOpenDialog(null);
                    if (result2 == JFileChooser.APPROVE_OPTION) {
                        File backupFile = fileChooser.getSelectedFile();
                        try {
                            String[] restoreCmd = new String[]{"mysql", "--user=" + dbUser, "--password=" + dbPassword, dbName, "-e", "source " + backupFile.getAbsolutePath()};
                            Process runtimeProcess = Runtime.getRuntime().exec(restoreCmd);
                            int processComplete = runtimeProcess.waitFor();
                            if (processComplete == 0) {
                                JOptionPane.showMessageDialog(null, "Database restored from " + backupFile.getAbsolutePath());
                            } else {
                                JOptionPane.showMessageDialog(null, "Error restoring database.");
                            }
                        } catch (IOException | InterruptedException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Error restoring database: " + ex.getMessage());
                        }
                    }
                }

            }
        });
        HoverButton.setButtonProperties(backUpButton);
        backUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    // Get the selected file and directory
                    backupFile = fileChooser.getSelectedFile();
                    File backupDir = backupFile.getParentFile();

                    // Get today's date in the format yyyy-MM-dd
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String dateStr = dateFormat.format(new Date());

                    // Construct the backup file name with today's date
                    String backupFileName = "backup" + "_"+ dbName + "_" + dateStr + ".sql";
                    backupFile = new File(backupDir, backupFileName);

                    try {
                        // Build the command to execute mysqldump
                        ProcessBuilder processBuilder = new ProcessBuilder(
                                "/usr/local/mysql-8.0.32-macos13-arm64/bin/mysqldump",
                                "-h" + dbHost,
                                "-P" + dbPort,
                                "-u" + dbUser,
                                "-p" + dbPassword,
                                dbName,
                                "--result-file=" + backupFile.getAbsolutePath()
                        );

                        // Execute the command
                        processBuilder.start();

                        JOptionPane.showMessageDialog(null, "Database backed up to " + backupFile.getAbsolutePath());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error backing up database: " + ex.getMessage());
                    }
                }
            }

        });

        HoverButton.setButtonProperties(showAlertsButton);
        showAlertsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { showPopup();
            }
        });
        giveFeedbackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPopupFeedBack();
            }
        });
    }

    /**
     Retrieves a list of customer IDs who have a pay later message and have exceeded the expiry date.
     @return A list of customer IDs as integers.
     */
    private List<Integer> payLaterMessage() {
        List<Integer> customerIDs = new ArrayList<>();

        try (Connection con = DBConnectivity.getConnection()) {
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement st = con.createStatement();
            String query = "SELECT Sale.Customer_ID FROM Sale WHERE Sale.paylater = 'pay later'" +
                    "AND Sale.Expiry_Date < '"+dateToday+"' ";
            ResultSet rs = st.executeQuery(query);
            System.out.println(query);

            while (rs.next()) {
                int customerID = rs.getInt("Customer_ID");
                customerIDs.add(customerID);
            }
            st.close();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }

        return customerIDs;
    }


    /**
     Creates a panel for refund approval with customer information and an alert button.
     @param customerID the ID of the customer who needs to pay the refund
     @return the panel containing the customer information and alert button
     */
    private JPanel createRefundApprovalPanel(int customerID) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("CustomerID: " + customerID + " has to pay");
        panel.add(label, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton alertButton = new JButton("Alert Customer");

        try (Connection con = DBConnectivity.getConnection()) {
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement st = con.createStatement();
            String query = "SELECT Email, Firstname , Surname FROM CustomerAccount WHERE Customer_ID  = '"+customerID+"'";
            ResultSet rs = st.executeQuery(query);
            System.out.println(query);

            while (rs.next()) {
                email = rs.getString("Email");
                firstname =  rs.getString("Firstname");
                surname =  rs.getString("Firstname");
            }
            st.close();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }

        // Add action listeners for approve and reject buttons
        alertButton.addActionListener(e -> {
            // Send alert logic
            Mail mail = new Mail();
            mail.setupServerProperties();

            try {
                mail.draftEmail(email,"Dear " + firstname + " " + surname + "," +
                        "\n" +
                        "I hope this email finds you well. I am writing to remind you that the payment for your account with us is currently past due. We would like to kindly request that you settle the outstanding balance as soon as possible.\n" +
                        "\n" +
                        "We understand that unforeseen circumstances can arise, which may cause delays in payment. However, it is important to keep your account current to avoid any disruption in the services that we provide.\n" +
                        "\n" +
                        "Please refer to your latest invoice for the amount due and the payment options available to you. If you have any questions or concerns, please do not hesitate to contact us under +447713956305.\n" +
                        "\n" +
                        "We value your business and look forward to continuing our relationship. Thank you for your prompt attention to this matter.\n" +
                        "\n" +
                        "Best regards,\n" +
                        "\n" +
                        "AirVia Ltd");
            } catch (MessagingException | IOException ex) {
                ex.printStackTrace();
            }
            try {
                mail.sendEmail();
            } catch (MessagingException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(adminPage, " the customer has been successfully emailed");
        });

        buttonsPanel.add(alertButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);

        return panel;

    }


    /**
     Displays a popup window with a list of customer IDs for refund approval.
     The popup contains a scrollable panel with refund approval panels for each customer ID.
     The popup also includes a "Close" button that hides the popup when clicked.
     */
    private void showPopup() {
        JComponent glassPane = new JPanel();
        glassPane.setLayout(null);
        glassPane.setOpaque(false);

        JPanel popup = new JPanel();
        popup.setBounds(getWidth() - 200, 0, 200, 400);
        popup.setBackground(Color.LIGHT_GRAY);
        popup.setLayout(new BoxLayout(popup, BoxLayout.Y_AXIS));

        List<Integer> customerIDs = payLaterMessage();
        for (int customerID : customerIDs) {
            popup.add(createRefundApprovalPanel(customerID));
            System.out.println("added");
        }

        JScrollPane scrollPane = new JScrollPane(popup);
        scrollPane.setBounds(getWidth() - 200, 0, 200, 400);
        glassPane.add(scrollPane);

        JButton closePopupButton = new JButton("Close");
        closePopupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                glassPane.setVisible(false);
            }
        });
        closePopupButton.setBounds(getWidth() - 200, 400, 200, 30);
        glassPane.add(closePopupButton);

        setGlassPane(glassPane);
        glassPane.setVisible(true);
    }


    /**
     Shows a popup window for user feedback.
     The popup window contains a text field and a submit button.
     When the submit button is clicked, the text entered in the text field is sent as an email to the designated recipient.
     If the text contains the string "outofbounds", a label with the same text is added to the popup window.
     */
    private void showPopupFeedBack() {
        JComponent glassPane = new JPanel();
        glassPane.setLayout(null);
        glassPane.setOpaque(false);

        JPanel popup = new JPanel();
        popup.setBounds(getWidth() - 200, 0, 200, 400);
        popup.setBackground(Color.LIGHT_GRAY);
        popup.setLayout(new BoxLayout(popup, BoxLayout.Y_AXIS));

        JTextField textField = new JTextField();
        textField.setBounds(10, 10, 180, 30); // set the position and size of the text field
        popup.add(textField); // add the text field to the popup panel

        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                if (text.contains("outofbounds")) {
                    textField.setText("");
                    popup.add(new JLabel("outofbounds"));
                    popup.revalidate();
                    popup.repaint();
                } else {
                    // handle submit action here

                    Mail mail = new Mail();
                    mail.setupServerProperties();

                    try {
                        mail.draftEmail("alexobz09@gmail.com",text);
                    } catch (MessagingException | IOException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        mail.sendEmail();
                    } catch (MessagingException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        popup.add(submitButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                glassPane.setVisible(false);
            }
        });
        popup.add(closeButton);

        JScrollPane scrollPane = new JScrollPane(popup);
        scrollPane.setBounds(getWidth() - 200, 0, 200, 400);
        glassPane.add(scrollPane);

        setGlassPane(glassPane);
        glassPane.setVisible(true);
    }




    public static void main(String[] args){
        SystemAdminHome adminHome = new  SystemAdminHome(ID, username, dateToday);
        adminHome.show();
    }
}