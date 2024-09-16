package Admin.Blanks;

import Admin.Commission.CommissionRates;
import Admin.CustomerDetails.CustomerDetails;
import Admin.Home.SystemAdminHome;
import Admin.UserDetails.CreateUser;
import Admin.UserDetails.UserDetails;
import Authentication.EnterDate;
import Authentication.Login;
import ButtonUtil.HoverButton;
import DB.DBConnectivity;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;
import java.util.List;


public class SystemStock extends javax.swing.JFrame {
    private JButton logOutButton;
    private JButton createUserButton;
    private JButton homeButton;
    private JButton manageUserDetailsButton;
    private JButton manageSystemStockButton;
    private JButton manageCommissionRatesButton;
    private JButton manageCustomerDetailsButton;
    private JPanel systemStockPage;

    private JButton ShowBlanks;
    private JTable blankTable;
    private JScrollPane blankScrollPane;
    private JComboBox selectOfficeManagerID;
    private JComboBox selectFilter;
    private JButton submitAssignBlank;
    private JTextField lowerRange;
    private JTextField upperRange;
    private JTextField assignDate;
    private JComboBox selectblankPrefix;
    private JComboBox SelectblankType;
    private JLabel usernameLabel;
    private JTextField newLowBlankRange;
    private JTextField newUpperBlankRange;
    private JButton submitBlankAdditionButton;
    private JComboBox selectBlankFilter;
    private JButton deleteBlankButton;
    private JComboBox blankPrefixFilter;


    private static int ID;
    private static String username;
    private static int dateToday;
    private int blankNumber;
    private String blankType;
    private int blankPrefix;


    private boolean validateInput() {
        String lowerInput = lowerRange.getText();
        String upperInput = upperRange.getText();
        List<String> validPrefixes = Arrays.asList("444", "440", "420", "201", "101", "451", "452");

        if (lowerInput.length() != 9 && upperInput.length() != 9) {
            JOptionPane.showMessageDialog(null, "Blank number must be exactly 9 digits");
            return false;
        }

        String lowerPrefix = lowerInput.substring(0, 3);
        String upperPrefix = upperInput.substring(0, 3);

        if (!validPrefixes.contains(lowerPrefix) || !validPrefixes.contains(upperPrefix)) {
            JOptionPane.showMessageDialog(null, "Invalid blank type provided");
            return false;
        }

        if (!lowerPrefix.equals(upperPrefix)) {
            JOptionPane.showMessageDialog(null, "Ensure blank types are the same");
            return false;
        }

        if (Long.parseLong(lowerInput) >= Long.parseLong(upperInput)) {
            JOptionPane.showMessageDialog(null, "Lower batch shouldn't be higher than the upper batch");
            return false;
        }

        JOptionPane.showMessageDialog(null, "Input is valid");
        return true;
    }










    public SystemStock(int ID, String username) {

        blankTable.setPreferredScrollableViewportSize(new Dimension(550, 500));
        blankScrollPane.setPreferredSize(new Dimension(500, 500));

        this.username = username;
        this.ID = ID;
        this.dateToday = dateToday;
        usernameLabel.setText("Manager: " + username);
        setContentPane(systemStockPage);
        setSize(1000, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        // testing the git push

        SelectblankType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SelectblankType.addItem("Interline");
                SelectblankType.addItem("Domestic");
            }
        });


        HoverButton.setButtonProperties(ShowBlanks);
        ShowBlanks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel model = (DefaultTableModel) blankTable.getModel();
                model.setRowCount(0);

                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query;
                    String selectedOption = (String) selectFilter.getSelectedItem();

                    switch (selectedOption) {
                        case "Sold Blanks":
                            query = "SELECT Blank.BlankNumber, Blank.Type, Blank.Employee_ID " +
                                    "FROM Blank " +
                                    "WHERE Blank.isSold = '1';";
                            model.setColumnIdentifiers(new String[]{"BlankNumber", "Type", "Employee_ID"});
                            break;
                        case "Assigned Blanks":
                            query = "SELECT Blank.BlankNumber, Blank.Type, Blank.Employee_ID, Blank.date_assign " +
                                    "FROM Blank " +
                                    "WHERE Blank.isAssigned = '1';";
                            model.setColumnIdentifiers(new String[]{"BlankNumber", "Type", "Employee_ID", "date_assign"});
                            break;
                        case "Unassigned Blanks":
                            query = "SELECT Blank.BlankNumber, Blank.Type " +
                                    "FROM Blank " +
                                    "WHERE Blank.isAssigned = '0';";
                            model.setColumnIdentifiers(new String[]{"BlankNumber", "Type"});
                        case "Blank Types":
                            query = "SELECT Blank.BlankNumber, Blank.Type " +
                                    "FROM Blank;";
                            model.setColumnIdentifiers(new String[]{"BlankNumber", "Type"});
                            break;
                        default:
                            return;
                    }

                    ResultSet rs = st.executeQuery(query);

                    while (rs.next()) {
                        String[] row;
                        switch (selectedOption) {
                            case "Sold Blanks":
                                row = new String[]{rs.getString("BlankNumber"), rs.getString("Type"), rs.getString("Employee_ID")};
                                break;
                            case "Assigned Blanks":
                                row = new String[]{rs.getString("BlankNumber"), rs.getString("Type"), rs.getString("Employee_ID"), rs.getString("date_assign")};
                                break;
                            case "Unassigned Blanks":
                                row = new String[]{rs.getString("BlankNumber"), rs.getString("Type")};
                                break;
                            case "Blank Types":
                                row = new String[]{rs.getString("BlankNumber"), rs.getString("Type")};
                                break;
                            default:
                                return;
                        }
                        model.addRow(row);
                    }
                    st.close();

                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });


        selectOfficeManagerID.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "SELECT  Employee.Employee_ID FROM Employee where Employee.role = 'officeManager'";

                    ResultSet rs = st.executeQuery(query);
                    //selectOfficeManagerID.removeAllItems();

                    while (rs.next()) {
                        int Id = Integer.parseInt(rs.getString("Employee_ID"));
                        selectOfficeManagerID.addItem(Id);
                    }

                    st.close();

                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            }
        });

        HoverButton.setButtonProperties(submitAssignBlank);


/*
        submitAssignBlank.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                validateInput();
                String lowerRangeText = lowerRange.getText();
                String upperRangeText = upperRange.getText();
                //int managerID = Integer.parseInt(selectOfficeManagerID.getSelectedItem().toString());

                int lowerBound = Integer.parseInt(lowerRangeText);
                int upperBound = Integer.parseInt(upperRangeText);

                String blankType = (String) SelectblankType.getSelectedItem();
                String blankPrefix = (String) selectblankPrefix.getSelectedItem();

                try {
                    int assignDateBlank = Integer.parseInt(assignDate.getText().replace("/", ""));

                    try (Connection con = DBConnectivity.getConnection()) {
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");

                        String getMaxBatchIdQuery = "SELECT MAX(batch_id) as max_batch_id FROM Blank";
                        Statement getMaxBatchIdStatement = con.createStatement();
                        ResultSet resultSet = getMaxBatchIdStatement.executeQuery(getMaxBatchIdQuery);
                        int newBatchId = 1;
                        if (resultSet.next()) {
                            newBatchId = resultSet.getInt("max_batch_id") + 1;
                        }



                        String query = "INSERT INTO Blank (BlankNumber,date_assign, Type, blank_prefix, isSold, isAssigned,batch_id) VALUES (?, ?, ?, ?, ?, ?,?)";


                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        for (int i = lowerBound; i <= upperBound; i++) {
                            preparedStatement.setInt(1, i);
                            //preparedStatement.setInt(2, managerID);
                            preparedStatement.setInt(2, assignDateBlank);
                            preparedStatement.setString(3, blankType);
                            preparedStatement.setString(4, blankPrefix);
                            preparedStatement.setInt(5, 0);
                            preparedStatement.setInt(6, 1);
                            preparedStatement.setInt(7, newBatchId);
                            preparedStatement.executeUpdate();
                        }

                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
        });


 */

        HoverButton.setButtonProperties(homeButton);

        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SystemAdminHome homeButton = new SystemAdminHome(ID, username, EnterDate.getDateToday());
                homeButton.setVisible(true);
                dispose();

            }
        });
        HoverButton.setButtonProperties(manageUserDetailsButton);
        manageUserDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserDetails manageUserDetailsButton = new UserDetails(ID, username);
                manageUserDetailsButton.setVisible(true);
                dispose();

            }
        });
        HoverButton.setButtonProperties(manageCustomerDetailsButton);
        manageCustomerDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CustomerDetails manageCustomerDetailsButton = new CustomerDetails(ID, username);
                manageCustomerDetailsButton.setVisible(true);
                dispose();


            }
        });
        manageCommissionRatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                CommissionRates manageCommissionRatesButton = new CommissionRates(ID, username);
                manageCommissionRatesButton.setVisible(true);
                dispose();

            }
        });
        manageSystemStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SystemStock manageSystemStockButton = new SystemStock(ID, username);
                manageSystemStockButton.setVisible(true);
                dispose();


            }
        });
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CreateUser createUserButton = new CreateUser(ID, username);
                createUserButton.setVisible(true);
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

        /*
        submitBlankAdditionButton.addActionListener(new ActionListener() {

                                                        @Override
                                                        public void actionPerformed(ActionEvent e) {

                                                            int lowerRangeNew = Integer.parseInt(newLowBlankRange.getText());
                                                            int upperRangeNew = Integer.parseInt(newUpperBlankRange.getText());
                                                            blankType = Objects.requireNonNull(selectBlankFilter.getSelectedItem()).toString();
                                                            blankPrefix = (int) selectblankPrefix.getSelectedItem();

                                                            try (Connection con = DBConnectivity.getConnection()) {
                                                                assert con != null;
                                                                Class.forName("com.mysql.cj.jdbc.Driver");

                                                                // Prepare the INSERT query for the Blank table
                                                                String insertQuery = "INSERT INTO Blank (Blank.BlankNumber,Blank.Type, Blank.blank_prefix,Blank.isSold,Blank.isAssigned) VALUES (?, ?, ?,?,?)";

                                                                // Prepare the statement to execute the INSERT query
                                                                PreparedStatement preparedStatement = con.prepareStatement(insertQuery);

                                                                // Iterate through the range of BlankNumbers to insert
                                                                for (int blankNumber = lowerRangeNew; blankNumber <= upperRangeNew; blankNumber++) {
                                                                    preparedStatement.setInt(1, blankNumber);
                                                                    preparedStatement.setString(2, blankType);
                                                                    preparedStatement.setInt(3, blankPrefix);
                                                                    preparedStatement.setInt(4, 0);
                                                                    preparedStatement.setInt(5, 0);

                                                                    System.out.println(blankPrefix + " BLANK PREFIX");
                                                                    System.out.println(blankType + "BLANK TYPE ");



                                                                    // Execute the INSERT query
                                                                    preparedStatement.executeUpdate();


                                                                }


                                                            } catch (SQLException ex) {
                                                                ex.printStackTrace();
                                                            } catch (ClassNotFoundException ex) {
                                                                ex.printStackTrace();
                                                            }

                                                        }
                                                    });


         */


        HoverButton.setButtonProperties(deleteBlankButton);

        deleteBlankButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRowBlank = blankTable.getSelectedRow();

                try {
                    blankNumber = Integer.parseInt(blankTable.getValueAt(selectedRowBlank, 0).toString());
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }

                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();

                    String query =
                            " UPDATE Blank " +
                                    "                SET Employee_ID = null " +
                                    "                where BlankNumber = '" + blankNumber + "';";
                    System.out.println(query);
                    int rs = st.executeUpdate(query);
                } catch (SQLException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                }


                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "DELETE FROM Blank " +
                            " WHERE BlankNumber = '" + blankNumber + "'";
                    ;
                    System.out.println(query);
                    int rs = st.executeUpdate(query);
                } catch (SQLException | ClassNotFoundException ex) {
                    JOptionPane.showMessageDialog(systemStockPage, "Blank is assigned or has been sold");
                }
                JOptionPane.showMessageDialog(systemStockPage, "Blank deleted");
            }
        });
        SelectblankType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) SelectblankType.getSelectedItem();
                if (e.getSource() == blankType) {
                    switch (selected) {
                        case "Interline" -> {
                            blankType = "Interline";
                        }
                        case "Domestic" -> {
                            blankType = "Domestic";
                        }
                    }

                }

            }
        });

        /*
        submitAssignBlank.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                validateInput();
                String lowerRangeText = lowerRange.getText();
                String upperRangeText = upperRange.getText();

                int lowerBound = Integer.parseInt(lowerRangeText);
                int upperBound = Integer.parseInt(upperRangeText);

                String blankType = (String) SelectblankType.getSelectedItem();
                String blankPrefix = (String) selectblankPrefix.getSelectedItem();

                try {
                    int assignDateBlank = Integer.parseInt(assignDate.getText().replace("/", ""));

                    try (Connection con = DBConnectivity.getConnection()) {
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");

                        String getMaxBatchIdQuery = "SELECT MAX(batch_id) as max_batch_id FROM Blank";
                        Statement getMaxBatchIdStatement = con.createStatement();
                        ResultSet resultSet = getMaxBatchIdStatement.executeQuery(getMaxBatchIdQuery);
                        int newBatchId = 1;
                        if (resultSet.next()) {
                            newBatchId = resultSet.getInt("max_batch_id") + 1;
                        }

                        String query = "UPDATE Blank SETl BlankNumber = ?, date_assign = ? , Type = ? , blank_prefix = ? , isSold = ?, isAssigned = ?, batch_id";

                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        for (int i = lowerBound; i <= upperBound; i++) {
                            preparedStatement.setInt(1, i);
                            preparedStatement.setInt(2, assignDateBlank);
                            preparedStatement.setString(3, blankType);
                            preparedStatement.setString(4, blankPrefix);
                            preparedStatement.setInt(5, 0);
                            preparedStatement.setInt(6, 1);
                            preparedStatement.setInt(7, newBatchId);
                            preparedStatement.executeUpdate();

                            // Increment the batch ID after each insert
                            newBatchId++;
                        }

                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

    });

         */
        submitAssignBlank.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                validateInput();
                String lowerRangeText = lowerRange.getText();
                String upperRangeText = upperRange.getText();
                int AssignManagerID = (int) selectOfficeManagerID.getSelectedItem();

                int lowerBoundAssign = Integer.parseInt(lowerRangeText);
                int upperBoundAssign = Integer.parseInt(upperRangeText);

                String blankType = (String) SelectblankType.getSelectedItem();

                try {
                    int AssignDateBlank = Integer.parseInt(assignDate.getText().replace("/", ""));

                    try (Connection con = DBConnectivity.getConnection()) {
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        String query = "UPDATE Blank SET Employee_ID = ?, date_assign = ?, Type = ?, isAssigned = ?  WHERE BlankNumber BETWEEN ? AND ? ";
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.setInt(1, AssignManagerID);
                        preparedStatement.setInt(2, AssignDateBlank);
                        preparedStatement.setString(3, blankType);
                        preparedStatement.setInt(4, 0);
                        preparedStatement.setInt(5, lowerBoundAssign);
                        preparedStatement.setInt(6, upperBoundAssign);
                        preparedStatement.executeUpdate();

                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        /*

        blankPrefixFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                blankPrefixFilter.addItem("444");
                blankPrefixFilter.addItem("440");
                blankPrefixFilter.addItem("420");
                blankPrefixFilter.addItem("201");
                blankPrefixFilter.addItem("101");

            }
        });

         */

        submitBlankAdditionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String LowBlankRangeInsert = newLowBlankRange.getText();
                String newUpperBlankRangeInsert = newUpperBlankRange.getText();
                String insertBlankType = (String) selectBlankFilter.getSelectedItem();

                if (LowBlankRangeInsert.length() != 9 || newUpperBlankRangeInsert.length() != 9) {
                    JOptionPane.showMessageDialog(null, "Blank Number must be 9 digits long");
                    return;
                }

                if (!LowBlankRangeInsert.substring(0, 3).equals(newUpperBlankRangeInsert.substring(0, 3))) {
                    JOptionPane.showMessageDialog(null, "Blank types aren't matching, try a different BlankNumber");
                    return;
                }

                int lowerBoundAssignInsert = Integer.parseInt(LowBlankRangeInsert);
                int upperBoundAssignInsert = Integer.parseInt(newUpperBlankRangeInsert);

                String query = "INSERT INTO Blank (BlankNumber, Type, isAssigned, isSold) VALUES (?, ?, ?, ?)";
                int totalRowsAffected = 0;

                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    PreparedStatement preparedStatement = con.prepareStatement(query);

                    for (int i = lowerBoundAssignInsert; i <= upperBoundAssignInsert; i++) {
                        preparedStatement.setInt(1, i); // BlankNumber
                        preparedStatement.setString(2, insertBlankType); // Type
                        preparedStatement.setInt(3, 0); // isAssigned
                        preparedStatement.setInt(4, 0); // isSold

                        int rowsAffected = preparedStatement.executeUpdate();
                        totalRowsAffected += rowsAffected;
                    }

                    if (totalRowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Database updated successfully! Total rows affected: " + totalRowsAffected);
                    } else {
                        JOptionPane.showMessageDialog(null, "No rows were affected. Please try again.");
                    }

                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

        /*
                //chanegs to the blank numbers not being added, worn gcolums
                int LowBlankRangeInsert = Integer.parseInt(newLowBlankRange.getText());
                int newUpperBlankRangeInsert = Integer.parseInt(newUpperBlankRange.getText());
                String insertBlankType = (String) selectBlankFilter.getSelectedItem();
                //nt blankPrefixInsert = (int) blankPrefixFilter.getSelectedItem();

                int lowerBoundAssignInsert = Integer.parseInt(String.valueOf(LowBlankRangeInsert));
                int upperBoundAssignInsert = Integer.parseInt(String.valueOf(newUpperBlankRangeInsert));

                String query = "UPDATE  Blank SET Type = ?, isAssigned = ? WHERE BlankNumber BETWEEN ? AND ?";
                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.setString(1, insertBlankType);
                    preparedStatement.setInt(2, 0);
                   // preparedStatement.setInt(3, blankPrefixInsert);
                    preparedStatement.setInt(3, lowerBoundAssignInsert);
                    preparedStatement.setInt(4, upperBoundAssignInsert);
                    preparedStatement.executeUpdate();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

*





                /* INSERT WITH THE  COULMN/DATA WEAY
                try {
                    //int AssignDateBlank = Integer.parseInt(assignDate.getText().replace("/", ""));

                    try (Connection con = DBConnectivity.getConnection()) {
                        assert con != null;
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        String query = "UPDATE Blank SET Type = ?, isAssigned = ?,blank_prefix = ?  WHERE BlankNumber BETWEEN ? AND ? ";
                        PreparedStatement preparedStatement = con.prepareStatement(query);
                        preparedStatement.setString(1, blankType);
                        preparedStatement.setInt(2, 0);
                        preparedStatement.setInt(3, blankPrefixInsert);
                        preparedStatement.setInt(4, lowerBoundAssignInsert);
                        preparedStatement.setInt(5, upperBoundAssignInsert);
                        preparedStatement.executeUpdate();

                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

 */






    public static void main(String[] args) {
        SystemStock systemStock = new SystemStock(ID,username);
        systemStock.show();
    }

}