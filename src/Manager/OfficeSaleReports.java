
package Manager;

import DB.DBConnectivity;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;




/**
 This class represents the sales report page for the office manager.
 The page provides functionality for generating and viewing sales reports
 for individual advisors, as well as global sales reports for a given date range.
 The page also allows the user to select the type of sales report they wish to generate.
 Additionally, the user can view the last generated PDF sales report by clicking the "view last report" button.
 */
public class OfficeSaleReports extends javax.swing.JFrame {
    private JButton logOutButton;
    private JButton homeButton;
    private JButton stocksButton;
    private JButton ticketStockTurnOverButton;
    private JButton blanksButton;
    private JButton discountPlanButton;
    private JButton advisorIndividualReportButton;
    private JPanel individualReportPage;
    private JButton produceReport;
    private JComboBox chooseAdvisorName;
    private JComboBox selectSaleType;
    private JButton viewProducedReportButton;
    private JButton notificationButton;
    private JTextField dateForReport;
    private JButton viewDateButton;
    private JTextField lowerDateRange;
    private JTextField upperDateRange;
    private JComboBox selectIndividualGlobal;
    private JLabel usernameLabel;
    private PdfPTable lastGeneratedPdfFile;


    private static int ID;
    private static String username;
    private int lowerDate;
    private int upperDate;
    private int advisorname;
    private String BlankType;
/*
    private void startRefundNotificationWatcher() {
        Thread refundWatcherThread = new Thread(() -> {
            while (true) {
                String message = refundMessage();
                if (!message.isEmpty()) {
                    SwingUtilities.invokeLater(() -> {
                        refundMessage();
                    });
                }
                try {
                    // Check for new records every minute (60000 ms)
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        refundWatcherThread.setDaemon(true);
        refundWatcherThread.start();
    }

 */


/*
    private boolean validateInput() {
        String lowerInput = lowerDateRange.getText();
        String upperInput = upperDateRange.getText();

        try {
            long lowerDate = Long.parseLong(lowerInput.substring(6));
            long upperDate = Long.parseLong(upperInput.substring(6));

            if (lowerDate <= 0 || upperDate <= 0 || lowerDate > 999999 || upperDate > 99999999) {
                JOptionPane.showMessageDialog(null, "Invalid date range provided");
                return false;
            }

            long lowerRange = Long.parseLong(lowerInput);
            long upperRange = Long.parseLong(upperInput);

            if (lowerRange > upperRange) {
                JOptionPane.showMessageDialog(null, "Lower date range must be less than or equal to upper date range");
                return false;
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid date range provided");
            return false;
        }

        JOptionPane.showMessageDialog(null, "Input is valid");
        return true;
    }

 */


    private List<Integer> refundMessage() {
        List<Integer> refundIDs = new ArrayList<>();

        try (Connection con = DBConnectivity.getConnection()) {
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement st = con.createStatement();
            String query = "SELECT Refund_ID FROM Refund WHERE refund_status = true";
            ResultSet rs = st.executeQuery(query);

            while (rs.next()) {
                int refundID = rs.getInt("Refund_ID");
                refundIDs.add(refundID);
            }

        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return refundIDs;
    }


    private PdfPTable createIndividualInterlineReports(int advisorID, int lowerDateRange, int upperDateRange) throws SQLException, FileNotFoundException, DocumentException, ClassNotFoundException {
        //blank Number,commsiaion rate applied the percentage,form of payemtn that was used, and if it was chas the sale price shoudl be placed next to it
        // total overall sum for that advisor, total commsion earedn and total neyt sale earned

        double overallNetSaleAmount = 0;
        double overallCommissionEarned = 0;
        BlankType = (String) selectSaleType.getSelectedItem();

        PdfPTable table = null;
        try (Connection con = DBConnectivity.getConnection()) {
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");

            String sql = "SELECT Sale.BlankNumber," +
                    "Sale.Sale_ID," +
                    "Sale.Amount AS TotalSaleAmount, " +
                    "Commission.Rate AS CommissionRateApplied, " +
                    "ROUND(CASE WHEN Commission.blankType = Blank.Type " +
                    "THEN Sale.Amount * Commission.Rate " +
                    "ELSE 0 " +
                    "END, 2) AS TotalCommissionAmount, " +
                    "ROUND(Sale.Amount - " +
                    "CASE WHEN Commission.blankType = Blank.Type " +
                    "THEN Sale.Amount * Commission.Rate " +
                    "ELSE 0 " +
                    "END, 2) AS NetSaleAmount " +
                    "FROM Sale " +
                    "JOIN Commission " +
                    "ON Sale.Commission_ID = Commission.Commission_ID " +
                    "JOIN Blank " +
                    "ON Sale.BlankNumber = Blank.BlankNumber " +
                    "WHERE Sale.Employee_ID = ? AND DATE(Sale.Payment_Date) BETWEEN ? AND ? AND Blank.Type = ?";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, advisorID);
            pstmt.setInt(2, lowerDateRange);
            pstmt.setInt(3, upperDateRange);
            pstmt.setString(4, BlankType);
//            pstmt.setInt(4, upperDateRange);
            lowerDate = lowerDateRange;
            upperDate = upperDateRange;
            advisorname = advisorID;


            ResultSet rs = pstmt.executeQuery();

            table = new PdfPTable(6);

            String[] headers = {"Blank Number", "Sale ID", "Total Sale Amount", "Commission Rate Applied", "Total Commission Amount", "Net Sale Amount"};
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            BaseColor headerBackgroundColor = new BaseColor(204, 204, 204);

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(headerBackgroundColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }


            while (rs.next()) {

                for (int i = 1; i <= 6; i++) {
                    table.addCell(rs.getString(i));
                }
                overallNetSaleAmount += rs.getDouble("NetSaleAmount");
                overallCommissionEarned += rs.getDouble("TotalCommissionAmount");
            }

            PdfPTable summaryTable = new PdfPTable(2);

            String[] summaryHeaders = {"Overall Net Sale Amount", "Overall Commission Earned"};
            Font summaryHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            BaseColor summaryHeaderBackgroundColor = new BaseColor(204, 204, 204);

            for (String header : summaryHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(header, summaryHeaderFont));
                cell.setBackgroundColor(summaryHeaderBackgroundColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                summaryTable.addCell(cell);
            }

            summaryTable.addCell(Double.toString(overallNetSaleAmount));
            summaryTable.addCell(Double.toString(overallCommissionEarned));

            Path pdfpath = Paths.get("/Users/aadilghani/Desktop/AirViaTicketSalesSystem/individual_interline_report.pdf");
            if (Files.exists(pdfpath)) {
                Files.delete(pdfpath);
            }

            Document document = new Document(PageSize.A4);

            if (BlankType.equals("Interline")) {
                PdfWriter.getInstance(document, new FileOutputStream("data/testPDF.pdf"));
                document.open();
                document.add(new Paragraph("Individual Interline Report"));
                document.add(table);
                document.add(summaryTable);
                document.close();


            }
//            else if (BlankType.equals("Interline")) {
//                PdfWriter.getInstance(document, new FileOutputStream("/Users/aadilghani/Desktop/AirViaTicketSalesSystem/individual_interline_report.pdf"));
//                document.open();
//                document.add(new Paragraph("Individual Interline Report"));
//                document.add(table);
//                document.add(summaryTable);
//                document.close();
//
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;

    }


    private PdfPTable createIndividualDomesticReports(int advisorID, int lowerDateRange, int upperDateRange) throws SQLException, FileNotFoundException, DocumentException, ClassNotFoundException {
        //blank Number,commsiaion rate applied the percentage,form of payemtn that was used, and if it was chas the sale price shoudl be placed next to it
        // total overall sum for that advisor, total commsion earedn and total neyt sale earned

        double overallNetSaleAmount = 0;
        double overallCommissionEarned = 0;
        BlankType = (String) selectSaleType.getSelectedItem();

        PdfPTable table = null;
        try (Connection con = DBConnectivity.getConnection()) {
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");

            String sql = "SELECT Sale.BlankNumber," +
                    "Sale.Sale_ID," +
                    "Sale.Amount AS TotalSaleAmount, " +
                    "Commission.Rate AS CommissionRateApplied, " +
                    "ROUND(CASE WHEN Commission.blankType = Blank.Type " +
                    "THEN Sale.Amount * Commission.Rate " +
                    "ELSE 0 " +
                    "END, 2) AS TotalCommissionAmount, " +
                    "ROUND(Sale.Amount - " +
                    "CASE WHEN Commission.blankType = Blank.Type " +
                    "THEN Sale.Amount * Commission.Rate " +
                    "ELSE 0 " +
                    "END, 2) AS NetSaleAmount " +
                    "FROM Sale " +
                    "JOIN Commission " +
                    "ON Sale.Commission_ID = Commission.Commission_ID " +
                    "JOIN Blank " +
                    "ON Sale.BlankNumber = Blank.BlankNumber " +
                    "WHERE Sale.Employee_ID = ? AND DATE(Sale.Payment_Date) BETWEEN ? AND ? AND Blank.Type = ?";


            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, advisorID);
            pstmt.setInt(2, lowerDateRange);
            pstmt.setInt(3, upperDateRange);
            pstmt.setString(4, BlankType);
//            pstmt.setInt(4, upperDateRange);
            lowerDate = lowerDateRange;
            upperDate = upperDateRange;
            advisorname = advisorID;


            ResultSet rs = pstmt.executeQuery();

            table = new PdfPTable(6);

            String[] headers = {"Blank Number", "Sale ID", "Total Sale Amount", "Commission Rate Applied", "Total Commission Amount", "Net Sale Amount"};
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            BaseColor headerBackgroundColor = new BaseColor(204, 204, 204);

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(headerBackgroundColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }


            while (rs.next()) {

                for (int i = 1; i <= 6; i++) {
                    table.addCell(rs.getString(i));
                }
                overallNetSaleAmount += rs.getDouble("NetSaleAmount");
                overallCommissionEarned += rs.getDouble("TotalCommissionAmount");
            }

            PdfPTable summaryTable = new PdfPTable(2);

            String[] summaryHeaders = {"Overall Net Sale Amount", "Overall Commission Earned"};
            Font summaryHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            BaseColor summaryHeaderBackgroundColor = new BaseColor(204, 204, 204);

            for (String header : summaryHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(header, summaryHeaderFont));
                cell.setBackgroundColor(summaryHeaderBackgroundColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                summaryTable.addCell(cell);
            }

            summaryTable.addCell(Double.toString(overallNetSaleAmount));
            summaryTable.addCell(Double.toString(overallCommissionEarned));

            Path pdfpath = Paths.get("data/testPDF.pdf");
            if (Files.exists(pdfpath)) {
                Files.delete(pdfpath);
            }

            Document document = new Document(PageSize.A4);

            if (BlankType.equals("Domestic")) {
                PdfWriter.getInstance(document, new FileOutputStream("data/testPDF.pdf"));
                document.open();
                document.add(new Paragraph("Individual Domestic Report"));
                document.add(table);
                document.add(summaryTable);
                document.close();


            }
//            else if (BlankType.equals("Interline")) {
//                PdfWriter.getInstance(document, new FileOutputStream("/Users/aadilghani/Desktop/AirViaTicketSalesSystem/data/individual_domestic_report.pdf"));
//                document.open();
//                document.add(new Paragraph("Individual Interline Report"));
//                document.add(table);
//                document.add(summaryTable);
//                document.close();
//
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return table;

    }


    private PdfPTable createGlobalTables(int lowerDateRange, int upperDateRange) throws SQLException, FileNotFoundException, DocumentException, ClassNotFoundException {

        //and cilue the tyoe of [ayent, and the ammoutn next to it

        String BlankType = (String) selectSaleType.getSelectedItem();
        double totalGlobalCommission = 0;
        double totalGlobalNetSale = 0;

        PdfPTable table = null;
        try (Connection con = DBConnectivity.getConnection()) {
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");

            String sql = "SELECT Sale.Employee_ID, SUM(Sale.Amount) AS TotalSaleAmount, " +
                    "ROUND(SUM(CASE WHEN Commission.blankType = Blank.Type THEN Sale.Amount * Commission.Rate ELSE 0 END), 2) AS TotalCommissionAmount, " +
                    "ROUND(SUM(Sale.Amount) - SUM(CASE WHEN Commission.blankType = Blank.Type THEN Sale.Amount * Commission.Rate ELSE 0 END), 2) AS NetSaleAmount " +
                    "FROM Sale " +
                    "JOIN Commission ON Sale.Commission_ID = Commission.Commission_ID " +
                    "JOIN Blank ON Sale.BlankNumber = Blank.BlankNumber " +
                    "WHERE DATE(Sale.Payment_Date) BETWEEN ? AND ? AND Blank.Type = ?" +
                    "GROUP BY Sale.Employee_ID";


            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, lowerDateRange);
            pstmt.setInt(2, upperDateRange);
            pstmt.setString(3, BlankType);


            ResultSet rs = pstmt.executeQuery();

            table = new PdfPTable(4);

            String[] headers = {"Employee ID", "Total Sale Amount", "Total Commission Amount", "Net Sale Amount"};
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            BaseColor headerBackgroundColor = new BaseColor(204, 204, 204);

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(headerBackgroundColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }


            while (rs.next()) {
                for (int i = 1; i <= 4; i++) {
                    table.addCell(rs.getString(i));
                }
                totalGlobalCommission += rs.getDouble("TotalCommissionAmount");
                totalGlobalNetSale += rs.getDouble("NetSaleAmount");
            }

            PdfPTable summaryTable = new PdfPTable(2);

            String[] summaryHeaders = {"Total Global Commission", "Total Global Net Sale"};
            Font summaryHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            BaseColor summaryHeaderBackgroundColor = new BaseColor(204, 204, 204);

            for (String header : summaryHeaders) {
                PdfPCell cell = new PdfPCell(new Phrase(header, summaryHeaderFont));
                cell.setBackgroundColor(summaryHeaderBackgroundColor);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                summaryTable.addCell(cell);
            }

            summaryTable.addCell(Double.toString(totalGlobalCommission));
            summaryTable.addCell(Double.toString(totalGlobalNetSale));

            if (BlankType.equals("Interline")) {
                Path pdfpath = Paths.get("data/testPDF.pdf");
                if (Files.exists(pdfpath)) {
                    Files.delete(pdfpath);
                }
            } else if (BlankType.equals("Domestic")) {
                Path pdfpath = Paths.get("data/testPDF.pdf");
                if (Files.exists(pdfpath)) {
                    Files.delete(pdfpath);
                }
            }

            Document document = new Document(PageSize.A4);

            if (BlankType.equals("Domestic")) {
                PdfWriter.getInstance(document, new FileOutputStream("data/testPDF.pdf"));
                document.open();
                document.add(new Paragraph("Global Domestic Report"));
                document.add(table);
                document.add(summaryTable);
                document.close();

            } else if (BlankType.equals("Interline")) {
                PdfWriter.getInstance(document, new FileOutputStream("data/testPDF.pdf"));
                document.open();
                document.add(new Paragraph("Global Interline Report"));
                document.add(table);
                document.add(summaryTable);
                document.close();
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

        return table;

    }


    /*

    private JPanel createRefundApprovalPanel(int customerID) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("CustomerID: " + customerID + " has to pay");
        panel.add(label, BorderLayout.NORTH);


        try (Connection con = DBConnectivity.getConnection()) {
            assert con != null;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Statement st = con.createStatement();
            String query = "SELECT Email, Firstname , Surname FROM CustomerAccount WHERE Customer_ID  = '" + customerID + "'";
            ResultSet rs = st.executeQuery(query);
            System.out.println(query);

            while (rs.next()) {

            }
            st.close();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();




        }
        return panel;
    }


        private void showPopup () {
            JComponent glassPane = new JPanel();
            glassPane.setLayout(null);
            glassPane.setOpaque(false);

            JPanel popup = new JPanel();
            popup.setBounds(getWidth() - 200, 0, 200, 400);
            popup.setBackground(Color.LIGHT_GRAY);
            popup.setLayout(new BoxLayout(popup, BoxLayout.Y_AXIS));

            List<Integer> refundIDs = refundMessage();
            for (int refundID : refundIDs) {
                popup.add(createRefundApprovalPanel(refundID));
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


     */





    /**
     * Constructs an instance of OfficeSaleReports with the given ID and username.
     * Initializes the page with default values and sets the page visible.
     * @param ID the ID of the user
     * @param username the username of the user
     */
    public OfficeSaleReports( int ID,  String username) {


        this.username = username;
        this.ID = ID;
        usernameLabel.setText("Manager: " + username);

        setContentPane(individualReportPage);
        setSize(1000, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        String FileName = "Advisor Individual Report";



        //startRefundNotificationWatcher();


        notificationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //showPopup();
            }
        });


        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                OfficeManagerHome officeManagerPage = new OfficeManagerHome(ID, username);
                officeManagerPage.setVisible(true);
                dispose();

            }
        });
        stocksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                OfficeManagerStock officeManagerStock = new OfficeManagerStock(ID, username);
                officeManagerStock.setVisible(true);
                dispose();

            }
        });
        blanksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                OfficeManagerBlanks officeManagerBlanks = new OfficeManagerBlanks(ID, username);
                officeManagerBlanks.setVisible(true);
                dispose();

            }
        });
        discountPlanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                OfficeManagerDiscountPlan discountPlanButton = new OfficeManagerDiscountPlan(ID, username);
                discountPlanButton.setVisible(true);
                dispose();

            }
        });
        ticketStockTurnOverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                OfficeManagerTicketStockTurnOverReport ticketStockTurnoverReportButton = new OfficeManagerTicketStockTurnOverReport(ID, username);
                ticketStockTurnoverReportButton.setVisible(true);
                dispose();

            }
        });


        advisorIndividualReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OfficeSaleReports advisorIndividualReportButton = new OfficeSaleReports(ID, username);
                advisorIndividualReportButton.setVisible(true);
                dispose();

            }
        });
        chooseAdvisorName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                try (Connection con = DBConnectivity.getConnection()) {
                    assert con != null;
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Statement st = con.createStatement();
                    String query = "SELECT  Employee.Employee_ID FROM Employee where Employee.role = 'advisor'";

                    ResultSet rs = st.executeQuery(query);
                    while (rs.next()) {
                        String Id = rs.getString("Employee_ID");
                        chooseAdvisorName.addItem(Id);
                    }
                    st.close();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            }
        });

        selectSaleType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectSaleType.addItem("Interline");
                selectSaleType.addItem("Domestic");
            }
        });

        produceReport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                //validateInput();
                String lowerRangeText = lowerDateRange.getText();
                String upperRangeText = upperDateRange.getText();
                int lowerBound = Integer.parseInt(lowerRangeText);
                int upperBound = Integer.parseInt(upperRangeText);

                String IndividualGlobal = (String) selectIndividualGlobal.getSelectedItem();
                String BlankType = (String) selectSaleType.getSelectedItem();


                if (BlankType.equals("Interline") && selectIndividualGlobal.getSelectedItem().toString().equals("Individual")) {
                    int advisorName = Integer.parseInt(chooseAdvisorName.getSelectedItem().toString());
                    try {
                        createIndividualInterlineReports(advisorName, lowerBound, upperBound);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (DocumentException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }

                else if (BlankType.equals("Domestic") && selectIndividualGlobal.getSelectedItem().toString().equals("Global")) {
                    int advisorName = Integer.parseInt(chooseAdvisorName.getSelectedItem().toString());
                    try {
                        createIndividualDomesticReports(advisorName, lowerBound, upperBound);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (DocumentException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }


                else if (BlankType.equals("Interline") && selectIndividualGlobal.getSelectedItem().toString().equals("Global")) {
                    try {
                        createGlobalTables( lowerBound, upperBound);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (DocumentException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }


                else if (BlankType.equals("Domestic") && selectIndividualGlobal.getSelectedItem().toString().equals("Individual")) {
                    try {
                        createGlobalTables(lowerBound, upperBound);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (DocumentException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });


        selectIndividualGlobal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectIndividualGlobal.addItem("Individual");
                selectIndividualGlobal.addItem("Global");
            }
        });


        viewProducedReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                if(BlankType.equals("Interline") && selectIndividualGlobal.getSelectedItem().toString().equals("Individual")){
                    try {
                        createIndividualInterlineReports(advisorname,lowerDate,upperDate);
                        File PDFdoc = new File("data/testPDF.pdf");
                        Desktop.getDesktop().open(PDFdoc);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (DocumentException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                else if(BlankType.equals("Domestic") && selectIndividualGlobal.getSelectedItem().toString().equals("Individual")){
                    try {
                        createIndividualDomesticReports(advisorname,lowerDate,upperDate);
                        File PDFdoc = new File("data/testPDF.pdf");
                        Desktop.getDesktop().open(PDFdoc);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (DocumentException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }

                else if(BlankType.equals("Domestic") && selectIndividualGlobal.getSelectedItem().toString().equals("Global")){
                    try {
                        createIndividualDomesticReports(advisorname,lowerDate,upperDate);
                        File PDFdoc = new File("data/testPDF.pdf");
                        Desktop.getDesktop().open(PDFdoc);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (DocumentException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }

                else if(BlankType.equals("Interline") && selectIndividualGlobal.getSelectedItem().toString().equals("Global")){
                    try {
                        createIndividualDomesticReports(advisorname,lowerDate,upperDate);
                        File PDFdoc = new File("data/testPDF.pdf");
                        Desktop.getDesktop().open(PDFdoc);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (DocumentException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }


            }

//                if (IndividualGlobal.equals("Individual")) {
//                    try {
//                        createIndividualReports(advisorID, lowerBound, upperBound);
//                    } catch (SQLException | FileNotFoundException | DocumentException | ClassNotFoundException ex) {
//                        ex.printStackTrace();
//                    }
//                } else {
//                    try {
//                        createGlobalTables(lowerBound, upperBound);
//                    } catch (SQLException | FileNotFoundException | DocumentException | ClassNotFoundException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            }
        });
    }









    public static void main(String[] args) {
        OfficeSaleReports individualReport = new OfficeSaleReports(ID, username);
        individualReport.show();
    }
}






