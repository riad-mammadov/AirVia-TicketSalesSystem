package Advisor.Blanks;

import Advisor.Home.TravelAdvisorHome;
import DB.DBConnectivity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdvisorStock extends javax.swing.JFrame {
    private JTable stockTable;
    private JPanel stockPage;
    private JButton HomeButton;
    private JButton showBlanksButton;
    private JScrollPane stockTableScroll;
    private JPanel blankTypePanel;
    private JButton voidBlankButton;
    private static int ID;
    private static String username;
    private JComboBox blankType;



    /**
     Creates a new instance of the AdvisorStock class with the given ID and username.
     The constructor sets up the GUI, sets the dimensions, adds the components to the container,
     and sets the blankType JComboBox options.
     @param ID the ID of the advisor
     @param username the username of the advisor
     */
    public AdvisorStock(int ID, String username) {
        this.ID = ID;
        this.username = username;

        // Set the preferred size and dimensions of the table scroll and table viewport.
        stockTableScroll.setPreferredSize(new Dimension(500,500));
        stockTable.setPreferredScrollableViewportSize(new Dimension(500,500));
        // Set the content pane, size, default close operation, and visibility of the frame.
        setContentPane(stockPage);
        setSize(1000,600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        // Set the options of the blankType JComboBox.
        String[] options = new String[]{"All Blanks","MCO","Interline","Domestic"};
        blankType = new JComboBox<>(options);
        blankTypePanel.add(blankType);


        showBlanksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayBlankTable("SELECT DISTINCT Blank.Type FROM Blank");
            }
        });

        HomeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                TravelAdvisorHome advisorHome = new TravelAdvisorHome(ID,username);
                advisorHome.show();

            }
        });

        blankType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
                model.setRowCount(0);
                String selected = (String) blankType.getSelectedItem();
                if(e.getSource() == blankType){
                    switch (selected) {
                        case "Interline" -> {
                            displayBlankTable("'Interline'");
                        }
                        case "Domestic" -> {
                         displayBlankTable("'Domestic'");
                        }
                        case "MCO" ->{
                            displayBlankTable("'MCO'");
                        }
                        case "All Blanks" ->{
                            displayBlankTable("SELECT DISTINCT Blank.Type FROM Blank");
                        }

                    }

                }

            }
        });
        voidBlankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }


    /**
     This method displays a blank table in the stockTable using the given blank constraint.
     @param blankConstraint A String representing the blank constraint
     */
    public void displayBlankTable(String blankConstraint){
        // Get the DefaultTableModel object associated with the stockTable
        DefaultTableModel model = (DefaultTableModel) stockTable.getModel();
        // Set the number of rows in the table to 0
        model.setRowCount(0);
        try(Connection con = DBConnectivity.getConnection()) {
            // Ensure that the connection to the database is established
            assert con != null;
            // Load the MySQL JDBC driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement st = con.createStatement();
            // Construct the SQL query to retrieve the relevant data from the database
            String query = "SELECT Employee.First_name, Employee.Last_name, Blank.blankNumber, Blank.Type \n" +
                    "FROM Blank \n" +
                    "INNER JOIN Employee \n" +
                    "ON Blank.Employee_ID = Employee.Employee_ID " +
                    "WHERE Employee.Employee_ID = '"+ID+"' AND Blank.Type IN ("+blankConstraint+") ";
            // Execute the SQL query and retrieve the result set
            ResultSet rs = st.executeQuery(query);
            // Get the metadata for the result set
            ResultSetMetaData rsmd = rs.getMetaData();

            // Get the number of columns in the result set
            int cols = rsmd.getColumnCount();
            // Create an array to store the column names
            String[] colName = new String[cols];
            // Get the column names from the metadata and store them in the colName array
            for(int i = 0; i < cols; i++){
                colName[i] = rsmd.getColumnName(i+1);
            }
            // Set the column identifiers for the table model
            model.setColumnIdentifiers(colName);
            // Initialize variables to store the data retrieved from the result set
            String first_name,last_name,blankNumber,blankType;

            // Iterate through the result set and add each row to the table model
            while(rs.next()){
                first_name = rs.getString(1);
                last_name = rs.getString(2);
                blankNumber = rs.getString(3);
                blankType = rs.getString(4);
                String[] row = {first_name,last_name,blankNumber,blankType};
                model.addRow(row);
            }
            st.close();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }

    }


    public static void main(String[] args){
        AdvisorStock advisorStock = new AdvisorStock(ID,username);
        advisorStock.show();
        advisorStock.setVisible(true);

    }
}
