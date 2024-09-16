package Admin.Commission;

import ButtonUtil.HoverButton;
import DB.DBConnectivity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;


/**
 The SetCommissionRate class represents the GUI for setting commission rates for advisors.
 */
public class SetCommissionRate extends javax.swing.JFrame {
    private JTable advisorTable;
    private JPanel mainPanel;
    private JTextField advisorNameText;
    private JButton searchAdvisorButton;
    private JButton setCommissionButton;
    private JButton backButton;
    private JTextField blankTypeText;
    private JTextField rateText;
    private JTextField fromText;
    private JTextField toText;
    private DefaultTableModel model;
    private JButton showAdvisorsButton;
    private JLabel employeeIDLabel;
    private boolean fromValid;
    private boolean toValid;
    private int ID;
    private String username;
    private String advisorID;


    /**
     * Constructor for the SetCommissionRate class.
     * @param ID the user ID
     * @param username the username of the user
     */
    public SetCommissionRate(int ID, String username) {
        this.ID = ID;
        this.username = username;
        setContentPane(mainPanel);
        setSize(1500, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        model = (DefaultTableModel) advisorTable.getModel();

        // Set button properties using the HoverButton class
        HoverButton.setButtonProperties(searchAdvisorButton);

        // Action listener for the search advisor button
        searchAdvisorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setRowCount(0);
                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "SELECT Employee.Employee_ID, Employee.First_name, Employee.Last_name, Employee.username, Employee.role, Employee.PhoneNumber, " +
                            "Employee.Email, Employee.Address, Employee.Company_ID \n" +
                            "FROM Employee \n" +
                            "WHERE Employee.role = 'advisor' AND Employee.First_name = '" + advisorNameText.getText() + "' OR Employee.Last_name = '" + advisorNameText.getText() + "' OR Employee.username = '" + advisorNameText.getText() + "'  ";
                    System.out.println(query);
                    ResultSet rs = st.executeQuery(query);
                    ResultSetMetaData rsmd = rs.getMetaData();

                    int cols = rsmd.getColumnCount();
                    String[] colName = new String[cols];
                    for (int i = 0; i < cols; i++) {
                        colName[i] = rsmd.getColumnName(i + 1);
                    }
                    model.setColumnIdentifiers(colName);
                    String employee_ID, first_name, last_name, username, role, phoneNumber, email, address, companyID;
                    while (rs.next()) {
                        employee_ID = rs.getString(1);
                        first_name = rs.getString(2);
                        last_name = rs.getString(3);
                        username = rs.getString(4);
                        role = rs.getString(5);
                        phoneNumber = rs.getString(6);
                        email = rs.getString(7);
                        address = rs.getString(8);
                        companyID = rs.getString(9);
                        String[] row = {employee_ID, first_name, last_name, username, role, phoneNumber, email, address, companyID};
                        model.addRow(row);
                    }
                    st.close();

                } catch (ClassNotFoundException | SQLException ex) {
                    ex.printStackTrace();
                }

            }


        });

        // Setting button properties for the showAdvisorsButton
        HoverButton.setButtonProperties(showAdvisorsButton);

        // Adding ActionListener to the showAdvisorsButton
        showAdvisorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) advisorTable.getModel();
                model.setRowCount(0);
                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    // Query to retrieve advisor information from the Employee table
                    String query = "SELECT Employee.Employee_ID, Employee.First_name, Employee.Last_name, Employee.username, Employee.role, Employee.PhoneNumber, " +
                            "Employee.Email, Employee.Address, Employee.Company_ID \n" +
                            "FROM Employee \n" +
                            "WHERE Employee.role IN ('advisor') ";
                    System.out.println(query);
                    ResultSet rs = st.executeQuery(query);
                    ResultSetMetaData rsmd = rs.getMetaData();

                    int cols = rsmd.getColumnCount();
                    String[] colName = new String[cols];
                    for (int i = 0; i < cols; i++) {
                        colName[i] = rsmd.getColumnName(i + 1);
                    }
                    // Setting the column identifiers for the model
                    model.setColumnIdentifiers(colName);
                    String employee_ID, first_name, last_name, username, role, phoneNumber, email, address, companyID;
                    while (rs.next()) {
                        employee_ID = rs.getString(1);
                        first_name = rs.getString(2);
                        last_name = rs.getString(3);
                        username = rs.getString(4);
                        role = rs.getString(5);
                        phoneNumber = rs.getString(6);
                        email = rs.getString(7);
                        address = rs.getString(8);
                        companyID = rs.getString(9);
                        String[] row = {employee_ID, first_name, last_name, username, role, phoneNumber, email, address, companyID};
                        model.addRow(row);
                    }
                    st.close();

                } catch (ClassNotFoundException | SQLException ex) {
                    ex.printStackTrace();
                }

            }
        });
        HoverButton.setButtonProperties(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CommissionRates commissionRates = new CommissionRates(ID,username);
                commissionRates.show();
            }
        });

        advisorTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int row = advisorTable.getSelectedRow();
                advisorID = advisorTable.getModel().getValueAt(row, 0).toString();
                employeeIDLabel.setText(advisorID);

            }
        });
        HoverButton.setButtonProperties(setCommissionButton);

        setCommissionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fromDate = fromText.getText().replace("/","");
                String toDate = toText.getText().replace("/","");

                if(blankTypeText.getText().equals("Interline") || blankTypeText.getText().equals("Domestic") ){
                    // do nothing
                }
                else{
                    JOptionPane.showMessageDialog(null, "Type in Interline or Domestic");
                }

                if(!(fromText.getText().equals("")) && !(toText.getText().equals(""))){
                    try (Connection con = DBConnectivity.getConnection()) {
                        int fromToInt = Integer.parseInt(fromDate);
                        int toToInt = Integer.parseInt(toDate);
                        int advisor = Integer.parseInt(advisorID);
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Statement st = con.createStatement();
                        String query = "INSERT INTO Commission SELECT " +
                                "(SELECT COALESCE(MAX(Commission_ID), 0) + 1 FROM Commission), '"+blankTypeText.getText()+"', '"+ Float.parseFloat(rateText.getText())+"', '"+advisor+"', '"+fromToInt+"' , '"+toToInt+"' , 0";
                        System.out.println(query);
                        int rowsInserted = st.executeUpdate(query);

                        st.close();
                    } catch (SQLException | ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(mainPanel,"Commission successfully set to advisor: " + advisorID);

                    dispose();
                    CommissionRates commissionRates = new CommissionRates(ID,username);
                    commissionRates.show();

                }
                else{
                    JOptionPane.showMessageDialog(mainPanel,"Please enter the date in yy/mm/dd format.");
                }
            }
        });
        rateText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != '.') {
                    e.consume();
                }
            }
        });
    }
}
