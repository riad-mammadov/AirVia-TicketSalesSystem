package Advisor.Sales;

import Advisor.Home.TravelAdvisorHome;
import DB.DBConnectivity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;



/**
 The SalesSelectTicket class extends javax.swing.JFrame and provides a graphical user interface for a sales agent to
 select a flight and ticket to sell. The agent can search for flights based on the departure and arrival cities, and
 view available tickets for each flight. Once the agent has selected a flight and ticket, they can continue to the
 SalesPayment page to complete the sale.
 */
public class SalesSelectTicket extends javax.swing.JFrame {
    private JTextField departureText;
    private JTextField arrivalText;
    private JComboBox selectBlankType;
    private JTable blankTable;
    private JButton searchFlightsButton;
    private JTable flightTable;
    private JPanel titelpanel;
    private JPanel flightsInfoPanel;
    private JPanel mainPanel;
    private JButton showBlanksButton;
    private JButton continueButton;
    private JTextField textField1;
    private JButton homeButton;
    private JPanel filterPanel;
    private JComboBox selectBlank;
    private static int ID;
    private static String username;
    private static int customerID;
    private static int flightID;
    private static int blankNumberForSale;
    private static int date;



    /**
     Constructor for the SalesSelectTicket class. Initializes the ID, username, customerID, and date fields and sets
     up the graphical user interface.
     @param ID the ID of the sales agent
     @param username the username of the sales agent
     @param customerID the ID of the customer
     @param date the date of the sale
     */
    public SalesSelectTicket(int ID, String username, int customerID, int date) {
        SalesSelectTicket.ID = ID;
        SalesSelectTicket.username = username;
        SalesSelectTicket.customerID = customerID;
        SalesSelectTicket.date = date;

        setContentPane(mainPanel);
        setSize(1000,600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);



        searchFlightsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String departure = departureText.getText();
                String arrival = arrivalText.getText();
                DefaultTableModel model = (DefaultTableModel) flightTable.getModel();
                model.setRowCount(0);


                // getting flight details
                try(Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "SELECT Flight.number, Flight.departure, Flight.destination, Flight.depTime, Flight.arrTime,\n" +
                            " Flight.price, Flight.Airline, Flight.F_Date\n" +
                            "FROM Flight \n" +
                            "WHERE Flight.departure = '"+departure+"' AND Flight.destination = '"+arrival+"' ";
                    System.out.println(query);
                    ResultSet rs = st.executeQuery(query);

                    ResultSetMetaData rsmd = rs.getMetaData();

                    int cols = rsmd.getColumnCount();
                    String[] colName = new String[cols];
                    for(int i = 0; i < cols; i++){
                        colName[i] = rsmd.getColumnName(i+1);
                    }
                    model.setColumnIdentifiers(colName);
                    String flightNumber,  flightDeparture, flightArrival, flightDepTime, flightArrtime,
                            price, airline, flightDate;

                    while(rs.next()){
                        flightNumber = rs.getString(1);
                        flightDeparture  = rs.getString(2);
                        flightArrival = rs.getString(3);
                        flightDepTime = rs.getString(4);
                        flightArrtime  = rs.getString(5);
                        price = rs.getString(6);
                        airline  = rs.getString(7);
                        flightDate = rs.getString(8);
                        String[] row = {flightNumber,  flightDeparture, flightArrival, flightDepTime, flightArrtime,
                                price, airline, flightDate};
                        model.addRow(row);
                    }
                    st.close();

                } catch (ClassNotFoundException | SQLException ex) {
                    ex.printStackTrace();
                }


            }
        });

        selectBlankType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) blankTable.getModel();
                model.setRowCount(0);
                String selected = (String) selectBlankType.getSelectedItem();
                if(e.getSource() == selectBlankType){
                    switch (selected) {
                        case "Interline" -> {
                            displayBlankTable("'Interline'");
                        }
                        case "Domestic" -> {
                            displayBlankTable("'Domestic'");
                        }
                        case "MCO" -> {
                            displayBlankTable("'MCO'");
                        }
                        case "All Blanks" ->{
                            displayBlankTable("SELECT DISTINCT Blank.Type FROM Blank");
                        }

                    }

                }
            }
        });

        showBlanksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayBlankTable("SELECT DISTINCT Blank.Type FROM Blank");
            }
        });

        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowBlank = blankTable.getSelectedRow();
                int selectedRowFlight = flightTable.getSelectedRow();

                try {
                    flightID = Integer.parseInt(flightTable.getValueAt(selectedRowFlight, 0).toString());
                    blankNumberForSale = Integer.parseInt(blankTable.getValueAt(selectedRowBlank, 0).toString());
                }
                catch(Exception exception){
                    JOptionPane.showMessageDialog(mainPanel,"Please select both Flight and desired Blank. If there are no available /n" +
                            "Flights please let the customer know");
                }

                if (selectedRowBlank != -1 && selectedRowFlight != -1) {
                    // A row has been selected
                    // Perform your desired action here
                    // PaymentsPage
                    dispose();
                    SalesPayment salesPayment = new SalesPayment(ID,username,customerID,flightID,blankNumberForSale,date);
                    salesPayment.show();
                } else {
                    // No row has been selected
                    // Handle this case as needed
                    JOptionPane.showMessageDialog(mainPanel,"Please select both Flight and desired Blank. If there are no available /n" +
                            "Flights please let the customer know");
                }
            }
        });
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                TravelAdvisorHome travelAdvisorHome = new TravelAdvisorHome(ID,username);
            }
        });
    }




    /**

     Displays a table of available blank tickets based on the given blank constraint.
     The table will display the blank number and type of each available blank ticket.
     The method first sets the row count of the DefaultTableModel to 0 to clear any existing data.
     It then retrieves data from the Blank table in the database based on the given blank constraint.
     The retrieved data is then used to populate the table.
     @param blankConstraint a String representing the blank constraint to be used in the SQL query.
     This parameter is used to filter the blank tickets displayed in the table.
     It should be a comma-separated list of blank types.
     @throws SQLException if a database access error occurs.
     @throws ClassNotFoundException if the MySQL JDBC driver cannot be found.
     */
    public void displayBlankTable(String blankConstraint){
        DefaultTableModel model = (DefaultTableModel) blankTable.getModel();
        model.setRowCount(0);
        try(Connection con = DBConnectivity.getConnection()) {
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement st = con.createStatement();
            String query = "SELECT Blank.blankNumber, Blank.Type \n" +
                    "FROM Blank \n" +
                    "WHERE Blank.Employee_ID = '"+ID+"' AND Blank.isSold = 0 AND Blank.Type IN ("+blankConstraint+") ";
            System.out.println(query);
            ResultSet rs = st.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();

            int cols = rsmd.getColumnCount();
            String[] colName = new String[cols];
            for(int i = 0; i < cols; i++) {
                colName[i] = rsmd.getColumnName(i+1);
            }
            model.setColumnIdentifiers(colName);
            String blankNumber,blankType;
            while(rs.next()){
                blankNumber = rs.getString(1);
                blankType  = rs.getString(2);
                String[] row = {blankNumber,blankType};
                model.addRow(row);
            }
            st.close();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }

    }

    public static void main (String[] args){
        SalesSelectTicket salesSellTicket = new SalesSelectTicket(ID, username,customerID,date);
        salesSellTicket.show();
    }
}
