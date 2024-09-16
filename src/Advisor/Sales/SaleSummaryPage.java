package Advisor.Sales;

import Advisor.Home.TravelAdvisorHome;
import DB.DBConnectivity;
import SMTP.Mail;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import javax.mail.MessagingException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;


/**
 The SaleSummaryPage class represents a JFrame that displays a summary of a ticket sale.
 */
public class SaleSummaryPage extends javax.swing.JFrame {
    private static int commission_ID;
    private JPanel mainPanel;
    private JButton voidTicketButton;
    private JButton payButton;
    private JLabel CustomerNamesLabel;
    private JLabel blankNumberLabel;
    private JLabel blankTypeLabel;
    private JLabel flightNumberLabel;
    private JLabel airlineLabel;
    private JLabel departureLabel;
    private JLabel arrivalLabel;
    private JLabel departureDateLabel;
    private JLabel departureTimeLabel;
    private JLabel arrivalTimeLabel;
    private JLabel paymentPeriodLabel;
    private JLabel paymentMethodLabel;
    private JLabel totalPriceLabel;
    private JLabel customerIDLabel;
    private static int ID;
    private static String username;
    private static float price;
    private static int flightID;
    private static String paymentPeriod;
    private static String paymentType;
    private static int blankNumber;
    private static String blankType;
    private static int customerID;
    private String firstName;
    private String surname;
    private String flightDeparture;
    private String flightArrival;
    private String flightDepTime;
    private String flightArrtime;
    private String airline;
    private String flightDate;
    private int ticketID;
    private static int date;
    private static int currencyID;
    private static Document document;
    private static String customerEmail;





    /**
     Constructor for the SaleSummaryPage class.
     @param ID the ID of the sale
     @param username the username of the user making the sale
     @param customerID the ID of the customer purchasing the ticket
     @param price the price of the ticket
     @param flightID the ID of the flight
     @param paymentPeriod the payment period of the ticket
     @param commission_ID the ID of the commission
     @param paymentType the payment type of the ticket
     @param blankNumber the number of the blank used to print the ticket
     @param blankType the type of the blank used to print the ticket
     @param date the date of the sale
     @param currencyID the ID of the currency used to make the sale
     */
    public SaleSummaryPage(int ID, String username, int customerID, float price,
                           int flightID, String paymentPeriod, int commission_ID, String paymentType, int blankNumber, String blankType, int date, int currencyID){
        this.ID = ID;
        this.username = username;
        this.price = price;
        this.flightID = flightID;
        this.paymentPeriod = paymentPeriod;
        this.paymentType = paymentType;
        this.blankNumber = blankNumber;
        this.blankType = blankType;
        this.customerID = customerID;
        this.date = date;
        this.commission_ID = commission_ID;
        setContentPane(mainPanel);
        setSize(1000,600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        customerIDLabel.setText(Integer.toString(customerID));
        blankNumberLabel.setText(Integer.toString(blankNumber));
        blankTypeLabel.setText(blankType);
        flightNumberLabel.setText(Integer.toString(flightID));
        paymentPeriodLabel.setText(paymentPeriod);
        paymentMethodLabel.setText(paymentType);
        totalPriceLabel.setText(Float.toString(price));


        // retrieves customer details
        try(Connection con = DBConnectivity.getConnection()){
            Class.forName("com.mysql.cj.jdbc.Driver");

            Statement stCustName = con.createStatement();
            String queryCustName = "SELECT Firstname,Surname  " +
                    "FROM CustomerAccount " +
                    "WHERE CustomerAccount.Customer_ID = '" + customerID + "' ";
            ResultSet rsCustName = stCustName.executeQuery(queryCustName);

            if(rsCustName.next()) {
                firstName = rsCustName.getString(1);
                surname = rsCustName.getString(2);
            }
            CustomerNamesLabel.setText(firstName + " " + surname);

            stCustName.close();

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

       //retrieves flight details
        try(Connection con = DBConnectivity.getConnection()) {
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement st = con.createStatement();
            String query = "SELECT Flight.departure, Flight.destination, Flight.depTime, Flight.arrTime, " +
                    " Flight.Airline, Flight.F_Date " +
                    "FROM Flight \n" +
                    "WHERE Flight.number  = '"+flightID+"' ";
            System.out.println(query);
            ResultSet rs = st.executeQuery(query);

            if(rs.next()) {
                flightDeparture = rs.getString(1);
                flightArrival = rs.getString(2);
                flightDepTime = rs.getString(3);
                flightArrtime = rs.getString(4);
                airline = rs.getString(5);
                flightDate = rs.getString(6);
            }


        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        departureLabel.setText(flightDeparture);
        arrivalLabel.setText(flightArrival);
        departureTimeLabel.setText(flightDepTime);
        arrivalTimeLabel.setText(flightArrtime);
        airlineLabel.setText(airline);
        departureDateLabel.setText(flightDate);

        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(paymentPeriod.equals("pay now")) {

                    int dialog = JOptionPane.showConfirmDialog(mainPanel, "Do you want to continue and complete the payment?");
                    if (dialog == JOptionPane.YES_OPTION) {
                        // User clicked the "Yes" button
                        try (Connection con = DBConnectivity.getConnection()) {
                            assert con != null;
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Statement st = con.createStatement();
                            String query = "INSERT INTO Ticket SELECT " +
                                    "(SELECT COALESCE(MAX(TicketID), 0) + 1 FROM Ticket), '" + blankNumber + "', '" + flightID + "'";
                            System.out.println(query);
                            int rowsInserted = st.executeUpdate(query);

                            st.close();
                        } catch (SQLException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }


                        try (Connection con = DBConnectivity.getConnection()) {
                            assert con != null;
                            Class.forName("com.mysql.cj.jdbc.Driver");
                            Statement st = con.createStatement();
                            String query = "SELECT TicketID FROM Ticket " +
                                    "WHERE blankNumber = '" + blankNumber + "'";
                            ResultSet rs = st.executeQuery(query);

                            if (rs.next()) {
                                ticketID = rs.getInt("TicketID");
                                System.out.println("ticketID is: " + ticketID);
                            }


                            st.close();
                        } catch (SQLException | ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }

                        try {
                            document = new Document();
                            PdfWriter.getInstance(document, new FileOutputStream("data/testPDF.pdf"));

                            document.open();

                            // Create a header with "AirVia Ltd" in bold font
                            Paragraph header = new Paragraph("AirVia Ltd", FontFactory.getFont(FontFactory.HELVETICA_BOLD));
                            header.setAlignment(Element.ALIGN_CENTER);
                            document.add(header);

                            Paragraph message = new Paragraph("Dear " + firstName + " " + surname + "," +
                                    " this is your receipt for flight number " + flightID + " from " + flightDeparture + " to " + flightArrival + ".");
                            message.setAlignment(Element.ALIGN_CENTER);
                            message.add(new Phrase(Chunk.NEWLINE));
                            document.add(message);

                            Paragraph customerInfo = new Paragraph();
                            customerInfo.setAlignment(Element.ALIGN_CENTER);
                            customerInfo.add(new Phrase("Customer ID:          " + customerID + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("First and Last name:          " + firstName + " " + surname + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("blankNumber:          " + blankNumber + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("blankType:          " + blankType + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("flightNumber:          " + flightID + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("date of sale:          " + date + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("Airline:          " + airline + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("departure Airport:          " + flightDeparture + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("arrival Airport:          " + flightArrival + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("Date of departure:          " + flightDate + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("Departure time:          " + flightDepTime + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("Arrival time:          " + flightArrtime + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("payment Period:          " + paymentPeriod + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("payment Method:         " + paymentType + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("total price:          " + price + "\n"));
                            customerInfo.add(new Phrase(Chunk.NEWLINE));
                            customerInfo.add(new Phrase("In case you have other enquiries, please contact us under +447713956305 "));
                            document.add(customerInfo);

                            document.close();


                        } catch (DocumentException | FileNotFoundException ex) {
                            ex.printStackTrace();
                        }


                        if (paymentType.equals("card")) {
                            dispose();
                            SalesCardPayNow salesCardPayNow = new SalesCardPayNow(ID, username, customerID, price, blankNumber,
                                    blankType, paymentPeriod, commission_ID ,paymentType, ticketID, date, currencyID, document);
                        } else if (paymentType.equals("cash")) {
                            dispose();
                            SalesCashPayNow salesCashPayNow = new SalesCashPayNow(ID, username, customerID, price, blankNumber,
                                    blankType, paymentPeriod, commission_ID,  paymentType, ticketID, date, currencyID, document);
                        }

                    } else {


                    }
                }

                else if(paymentPeriod.equals("pay later")){
                    int dateDue = calculatePayLaterDate(date);


                    try (Connection con = DBConnectivity.getConnection()) {
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Statement st = con.createStatement();
                        String query = "INSERT INTO Ticket SELECT " +
                                "(SELECT COALESCE(MAX(TicketID), 0) + 1 FROM Ticket), '" + blankNumber + "', '" + flightID + "'";
                        System.out.println(query);
                        int rowsInserted = st.executeUpdate(query);

                        st.close();
                    } catch (SQLException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }


                    try (Connection con = DBConnectivity.getConnection()) {
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Statement st = con.createStatement();
                        String query = "SELECT TicketID FROM Ticket " +
                                "WHERE blankNumber = '" + blankNumber + "'";
                        ResultSet rs = st.executeQuery(query);

                        if (rs.next()) {
                            ticketID = rs.getInt("TicketID");
                            System.out.println("ticketID is: " + ticketID);
                        }


                        st.close();
                    } catch (SQLException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }

                    try {
                        document = new Document();
                        PdfWriter.getInstance(document, new FileOutputStream("data/testPDF.pdf"));

                        document.open();

                        // Create a header with "AirVia Ltd" in bold font
                        Paragraph header = new Paragraph("AirVia Ltd", FontFactory.getFont(FontFactory.HELVETICA_BOLD));
                        header.setAlignment(Element.ALIGN_CENTER);
                        document.add(header);

                        Paragraph message = new Paragraph("Dear " + firstName + " " + surname + "," +
                                " these are the details for flight " + flightID + " from " + flightDeparture + " to " + flightArrival + "." +
                                " Please ensure that you pay the mentioned amount within the 30 day period");
                        message.setAlignment(Element.ALIGN_CENTER);
                        message.add(new Phrase(Chunk.NEWLINE));
                        document.add(message);

                        Paragraph customerInfo = new Paragraph();
                        customerInfo.setAlignment(Element.ALIGN_CENTER);
                        customerInfo.add(new Phrase("Customer ID:          " + customerID + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("First and Last name:          " + firstName + " " + surname + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("blankNumber:          " + blankNumber + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("blankType:          " + blankType + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("date of pay later sale:          " + date + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("flightNumber:          " + flightID + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("Airline:          " + airline + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("departure Airport:          " + flightDeparture + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("arrival Airport:          " + flightArrival + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("Date of departure:          " + flightDate + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("Departure time:          " + flightDepTime + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("Arrival time:          " + flightArrtime + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("payment Period:          " + paymentPeriod + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("payment Method:         " + paymentType + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("total price:          " + price + "\n"));
                        customerInfo.add(new Phrase(Chunk.NEWLINE));
                        customerInfo.add(new Phrase("In case you have other enquiries, please contact us under +447713956305 "));
                        document.add(customerInfo);

                        document.close();


                    } catch (DocumentException | FileNotFoundException ex) {
                        ex.printStackTrace();
                    }

                    // insert information into sale table
                    try (Connection con = DBConnectivity.getConnection()) {
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Statement st = con.createStatement();
                        String query = " INSERT INTO Sale SELECT" +
                                "(SELECT COALESCE(MAX(Sale_ID), 0) + 1 FROM Sale), '"+price+"','"+paymentPeriod+"'," +
                                " '"+dateDue+"','"+date+"'," +
                                "'"+paymentType+"', '"+ID+"','"+currencyID+"'," +
                                "'"+customerID+"','"+ commission_ID +"','"+ticketID+"','"+blankNumber+"', null, 0 ";
                        System.out.println(query);
                        int insert = st.executeUpdate(query);

                        st.close();
                    } catch (SQLException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }


                    // retrieve customer email
                    try (Connection con = DBConnectivity.getConnection()) {
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Statement st = con.createStatement();
                        String query = "SELECT CustomerAccount.Email " +
                                "FROM CustomerAccount " +
                                "WHERE CustomerAccount.Customer_ID = '" + customerID + "' ";
                        System.out.println(query);
                        ResultSet rs = st.executeQuery(query);

                        if(rs.next()){
                            customerEmail = rs.getString("Email");
                        }

                        st.close();
                    } catch (SQLException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }

                    Mail mail = new Mail();
                    mail.setupServerProperties();

                    try {
                        mail.draftEmail(customerEmail,"Dear Customer for AirVia, these " +
                                "are the details for your outstanding payment. Please execute the payment before :" +
                                " " + dateDue, "/Users/alexelemele/Documents/testPDF.pdf");
                    } catch (MessagingException | IOException ex) {
                        ex.printStackTrace();
                    }

                    try {
                        mail.sendEmail();
                    } catch (MessagingException ex) {
                        ex.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(mainPanel,"Customer has been emailed on late payment details");
                    dispose();
                    TravelAdvisorHome advisorHome = new TravelAdvisorHome(ID,username);
                    advisorHome.show();
                }


                }

        });

        voidTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                TravelAdvisorHome advisorHome = new TravelAdvisorHome(ID, username);

            }
        });
    }



    /**
     A utility method to calculate the payment due date for pay later transactions.
     @param dateSaleMade the date on which the sale was made in the format "DDMMYY"
     @return the payment due date in the format "DDMMYY"
     */
    public static int calculatePayLaterDate(int dateSaleMade){

        String str = String.valueOf(dateSaleMade);
        int dateDue = 0;
        if (str.substring(2, 4).equals("12")) {
            System.out.println(true);
            str = str.replace(str.substring(2,4),"01");
            dateDue = Integer.parseInt(str) + 10000 - 2;
        }
        else {
            dateDue = dateSaleMade + 100 - 2;
        }

        return dateDue;

    }


    public static void main(String[]args){
        SaleSummaryPage saleSummaryPage = new SaleSummaryPage(ID,  username,customerID,  price,
        flightID, paymentPeriod, commission_ID, paymentType, blankNumber, blankType,date, currencyID);
        saleSummaryPage.show();

    }
}
