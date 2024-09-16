package Advisor.IndividualReport;

import Advisor.Home.TravelAdvisorHome;
import DB.DBConnectivity;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



/**

 The IndividualReport class represents the GUI for the individual report page, which includes buttons and fields
 for producing different types of reports for advisors and sales.
 */
public class IndividualReport extends javax.swing.JFrame {
    private JButton logOutButton;
    private JButton domesticSalesReportButton;
    private JButton homeButton;
    private JButton stocksButton;
    private JButton ticketStockTurnOverButton;
    private JButton blanksButton;
    private JButton discountPlanButton;
    private JButton interlineSalesReportButton;
    private JButton advisorIndividualReportButton;
    private JPanel individualReportPage;
    private JButton produceAdvisorIndivudialReportButton;
    private JComboBox chooseAdvisorName;
    private JComboBox selectSaleType;
    private JButton viewProducedReportButton;
    private JButton notificationButton;
    private JTextField dateForReport;
    private JTextField dateToReport;
    private JButton viewDateButton;

    private static int ID;
    private static String username;
    private String saleType;




    /**
     Constructs a new IndividualReport object with the specified user ID and username.
     @param ID the user ID for the current user
     @param username the username for the current user
     */
    public IndividualReport(int ID, String username) {


        this.username = username;
        this.ID = ID;

        setContentPane(individualReportPage);
        setSize(1000, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);


        String FileName = "Advisor Individual Report";

        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                TravelAdvisorHome advisorHome = new TravelAdvisorHome(ID, username);
                advisorHome.setVisible(true);

            }
        });

        selectSaleType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectSaleType.addItem("Interline");
                selectSaleType.addItem("Domestic");
            }
        });


        //Adding action listener for the button to produce advisor individual report
        produceAdvisorIndivudialReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saleType = (String) selectSaleType.getSelectedItem();

                //Try with resources block for establishing database connection
                try (Connection con = DBConnectivity.getConnection()) {
                    //Parse the entered dates from the JTextField and remove any slashes (if any)
                    int dateForReportInitail = Integer.parseInt(dateForReport.getText().replace("/",""));
                    int dateForReportTo = Integer.parseInt(dateToReport.getText().replace("/",""));

                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");

                    //MySQL query to retrieve data
                    String query = "SELECT " +
                            "  Sale.Employee_ID, " +
                            "  Blank.BlankNumber, " +
                            "  Sale.Payment_Date, " +
                            "  ROUND(SUM(Sale.Amount * Currency_Code.Exchange_Rate), 2) AS TotalSaleAmount, " +
                            "  ROUND(SUM(CASE WHEN Commission.blankType = Blank.Type THEN Sale.Amount * Commission.Rate * Currency_Code.Exchange_Rate ELSE 0 END), 2) AS TotalCommissionAmount, " +
                            "  ROUND(SUM(Sale.Amount * Currency_Code.Exchange_Rate) - SUM(CASE WHEN Commission.blankType = Blank.Type THEN Sale.Amount * Commission.Rate * Currency_Code.Exchange_Rate ELSE 0 END), 2) AS NetSaleAmount " +
                            "FROM " +
                            "  Sale " +
                            "  JOIN Commission ON Sale.Commission_ID = Commission.Commission_ID " +
                            "  JOIN Blank ON Sale.BlankNumber = Blank.BlankNumber " +
                            "  JOIN Currency_Code ON Sale.Currency_Code = Currency_Code.Currency_Code " +
                            "WHERE " +
                            "  Sale.Employee_ID = ? " +
                            "  AND Blank.Type = ? " +
                            "  AND DATE(Sale.Payment_Date) BETWEEN ? AND ? " +
                            "GROUP BY " +
                            "  Sale.Employee_ID, " +
                            "  Blank.BlankNumber;";


                    //Creating PreparedStatement object for the MySQL query
                    PreparedStatement preparedStatement = con.prepareStatement(query);
                    preparedStatement.setString(1, String.valueOf(ID));
                    preparedStatement.setString(2, saleType);
                    preparedStatement.setInt(3, dateForReportInitail);
                    preparedStatement.setInt(4, dateForReportTo);



                    //MySQL query to retrieve additional data
                    String additionalQuery = "SELECT ROUND(SUM(NetSaleAmount), 2) AS TotalNetSaleAmount, ROUND(SUM(TotalCommissionAmount), 2) AS TotalOverallCommissionEarned FROM (" +
                            "SELECT Sale.Employee_ID, " +
                            "       ROUND(SUM(Sale.Amount * Currency_Code.Exchange_Rate), 2) AS TotalSaleAmount, " +
                            "       ROUND(SUM(CASE WHEN Commission.blankType = Blank.Type THEN Sale.Amount * Commission.Rate * Currency_Code.Exchange_Rate ELSE 0 END), 2) AS TotalCommissionAmount, " +
                            "       ROUND(SUM(Sale.Amount * Currency_Code.Exchange_Rate) - SUM(CASE WHEN Commission.blankType = Blank.Type THEN Sale.Amount * Commission.Rate * Currency_Code.Exchange_Rate ELSE 0 END), 2) AS NetSaleAmount " +
                            "FROM Sale " +
                            "JOIN Commission ON Sale.Commission_ID = Commission.Commission_ID " +
                            "JOIN Blank ON Sale.BlankNumber = Blank.BlankNumber " +
                            "JOIN Currency_Code ON Sale.Currency_Code = Currency_Code.Currency_Code " +
                            "WHERE Sale.Employee_ID = ? AND Blank.Type = ? AND DATE(Sale.Payment_Date) BETWEEN ? AND ? " +
                            "GROUP BY Sale.Employee_ID" +
                            ") AS subquery;";


                    PreparedStatement preparedStatementA = con.prepareStatement(additionalQuery);
                    preparedStatementA.setString(1, String.valueOf(ID));
                    preparedStatementA.setString(2, saleType);
                    preparedStatementA.setInt(3, dateForReportInitail);
                    preparedStatementA.setInt(4, dateForReportTo);


                    // Execute the first query and store the results in a ResultSet object
                    ResultSet rs = preparedStatement.executeQuery();
                    ResultSet rsAdditional = preparedStatementA.executeQuery();

                    // Specify the path to the PDF file and delete it if it already exists
                    Path pdfPath = Paths.get("/Users/alexelemele/Downloads/AirViaTicketSalesSystem/data/RefundEmail.pdf");
                    if (Files.exists(pdfPath)) {
                        Files.delete(pdfPath);
                    }


                    // Create a new PDF document and open it
                    Document PDFdoc = new Document(PageSize.A4.rotate());
                    PdfWriter.getInstance(PDFdoc, new FileOutputStream("/Users/alexelemele/Downloads/AirViaTicketSalesSystem/data/RefundEmail.pdf"));
                    PDFdoc.open();

                    // Add a header to the PDF document
                    PDFdoc.addHeader("AirVia Ltd",saleType + " Sales Report");

                    // Create a new table with 6 columns to hold the query results
                    PdfPTable queryTable = new PdfPTable(6);

                    // Specify the column names for the table
                    String[] columnNames = {"Employee_ID", "Blank Number", "Payment Date", "Total Sale Amount", "Total Commission Amount", "Net Sale Amount"};

                    // Specify the width of each column in the table
                    float[] columnWidths = {2f, 2f, 1f,1.5f, 1.8f, 1f};
                    queryTable.setWidths(columnWidths);
                    queryTable.setWidthPercentage(100);

                    // Add the column headers to the table
                    for (String columnName : columnNames) {
                        PdfPCell header = new PdfPCell(new Phrase(columnName));
                        header.setMinimumHeight(20);
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        queryTable.addCell(header);
                    }

                    PdfPCell table_cell;

                    // Retrieve the values for each column in the row
                    // Add each value to a cell and add the cell to the table
                    while (rs.next()) {

                        String Employee_ID = rs.getString("Employee_ID");
                        table_cell = new PdfPCell(new Phrase(Employee_ID));
                        queryTable.addCell(table_cell);

                        String BlankNumber = rs.getString("BlankNumber");
                        table_cell = new PdfPCell(new Phrase(BlankNumber));
                        queryTable.addCell(table_cell);

                        String Payment_Date = rs.getString("Payment_Date");
                        table_cell = new PdfPCell(new Phrase(Payment_Date));
                        queryTable.addCell(table_cell);

                        String TotalSaleAmount = rs.getString("TotalSaleAmount");
                        table_cell = new PdfPCell(new Phrase(TotalSaleAmount));
                        queryTable.addCell(table_cell);

                        String TotalCommissionAmount = rs.getString("TotalCommissionAmount");
                        table_cell = new PdfPCell(new Phrase(TotalCommissionAmount));
                        queryTable.addCell(table_cell);

                        String NetSaleAmount = rs.getString("NetSaleAmount");
                        table_cell = new PdfPCell(new Phrase(NetSaleAmount));
                        queryTable.addCell(table_cell);


                    }


                    PdfPTable additionalQueryTable = new PdfPTable(2); // create a new PDF table with 2 columns


                    String[] columnName2 = {"Total Net Sale Amount", "Total Earned Commission"}; // define column names as an array


                    float[] columnWidths2 = {0.5f, 0.5f}; // define the column widths
                    additionalQueryTable.setWidths(columnWidths2); // set the column widths for the table
                    additionalQueryTable.setWidthPercentage(50); // set the table's width to be 50% of the page

                    // loop through the column names and add each as a header cell to the table
                    for (String columnName : columnName2) {
                        PdfPCell header = new PdfPCell(new Phrase(columnName)); // create a new PDF cell with the column name as a Phrase
                        header.setMinimumHeight(20); // set a minimum height for the header cell
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY); // set the background color of the header cell to light gray
                        additionalQueryTable.addCell(header); // add the header cell to the table
                    }

                    PdfPCell table2_cell;
                    if (rsAdditional.next()) {

                        // check if there are any additional results in the result set
                        String TotalNetSaleAmount = rsAdditional.getString("TotalNetSaleAmount");

                        table2_cell = new PdfPCell(new Phrase(TotalNetSaleAmount));
                        additionalQueryTable.addCell(table2_cell);

                        // get the "TotalOverallCommissionEarned" value from the result set and add it as a cell to the table
                        String TotalOverallCommissionEarned = rsAdditional.getString("TotalOverallCommissionEarned");
                        table2_cell = new PdfPCell(new Phrase(TotalOverallCommissionEarned));
                        additionalQueryTable.addCell(table2_cell);


                    }


                    rsAdditional.close(); // close the result set
                    PDFdoc.add(queryTable); // add the first table (queryTable) to the PDF document
                    PDFdoc.add(additionalQueryTable); // add the second table (additionalQueryTable) to the PDF document
                    PDFdoc.close(); // close the PDF document

                    preparedStatement.close(); // close the prepared statement for the first query
                    preparedStatementA.close(); // close the prepared statement for the second query


                } catch (SQLException | DocumentException | IOException | ClassNotFoundException exception) {
                    exception.printStackTrace();
                }
            }

        });


        viewProducedReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File PDFdoc = new File("data/RefundEmail.pdf");
                    Desktop.getDesktop().open(PDFdoc);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

    }



    public static void main(String[] args) {
        IndividualReport individualReport = new IndividualReport(ID,username);
        individualReport.show();
    }
}



