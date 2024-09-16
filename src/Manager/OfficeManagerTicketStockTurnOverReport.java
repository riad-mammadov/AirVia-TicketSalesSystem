package Manager;

import Authentication.Login;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



/**
 The OfficeManagerTicketStockTurnOverReport class represents the graphical user interface for the ticket stock turnover report
 that can be accessed by the office manager user. This class extends the javax.swing.JFrame class to create the window for the report.
 */
public class OfficeManagerTicketStockTurnOverReport extends javax.swing.JFrame {
    private JButton logOutButton;
    private JButton homeButton;
    private JButton stockButton;
    private JButton ticketStockTurnOverButton;
    private JButton blanksButton;
    private JButton discountPlanButton;
    private JPanel TurnOverReport;
    private JButton saleReportsButton;
    private JLabel usernameLabel;
    private static int ID;
    private static String username;


    /**
     * The constructor for the OfficeManagerTicketStockTurnOverReport class.
     * @param ID The user ID of the logged in user
     * @param username The username of the logged in user
     */
    public OfficeManagerTicketStockTurnOverReport(int ID, String username){
        this.ID = ID;
        this.username = username;
        usernameLabel.setText("Manager: "+ username);


        setContentPane(TurnOverReport);
        setSize(1000,600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);


        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OfficeManagerHome officeManagerPage = new OfficeManagerHome(ID,username);
                officeManagerPage.setVisible(true);
                dispose();

            }
        });

        stockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OfficeManagerStock officeManagerStock = new OfficeManagerStock(ID,username);
                officeManagerStock.setVisible(true);
                dispose();

            }
        });

        blanksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OfficeManagerBlanks officeManagerBlanks = new OfficeManagerBlanks(ID,username);
                officeManagerBlanks.setVisible(true);
                dispose();

            }
        });


        discountPlanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OfficeManagerDiscountPlan discountPlanButton = new OfficeManagerDiscountPlan(ID,username);
                discountPlanButton.setVisible(true);
                dispose();

            }
        });

        ticketStockTurnOverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OfficeManagerTicketStockTurnOverReport ticketStockTurnOverButton = new OfficeManagerTicketStockTurnOverReport(ID,username);
                ticketStockTurnOverButton.setVisible(true);
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
        OfficeManagerTicketStockTurnOverReport TurnOverReport = new OfficeManagerTicketStockTurnOverReport(ID, username);
        TurnOverReport.show();
    }
}
