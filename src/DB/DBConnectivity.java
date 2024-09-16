package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectivity {

    /**
     This method returns a Connection object for the database with the given URL, username and password.
     @return Connection object representing the database connection
     */
    // TRIES CONNECTION WITH DATABASE
    public static Connection getConnection() {
        try {
            System.out.println("trying");
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://smcse-stuproj00.city.ac.uk:3306/in2018g01", "in2018g01_d", "U4m4nYtm");
            con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            System.out.println("Connection success");
            return con;

            // EXCEPTION MESSAGE IF DATABASE CONNECTION FAILS
        } catch (SQLException sqle) {
            System.out.println(sqle.getMessage() + " :Exception message");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
