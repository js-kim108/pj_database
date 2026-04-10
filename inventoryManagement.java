import java.sql.Connection;
import java.sql.DriverManagement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InventoryManagement{
  public Connection getConnection() throws SQLException, ClassNotFoundException{
    Connection conn = null;
    Class.forName("org.sqlite.JDBC");

    conn = DriverMaagement.getConnection("jdbc:sqlite:finalPJ.db");
    conn.setAutoCommit(false);
    return conn;
  }
public static void main(String[] args){
  Connection conn = getConnection(); 
  Statement statement = conn.createStatement();
  statement.executeUpdate("CREATE TABLE IF NOT EXIST Items("
                         + "ID INTEGER, " 
                         + "name VARCHAR(100) NOT NULL, "
                         + "quantity quantity INT NOT NULL CHECK (quantity >= 0), "
	                       + "type VARCHAR(20), ", 
	                       + "price DECIMAL(10, 2), "
	                       + "PRIMARY KEY(ID))");
  
  statement.executeUpdate("CREATE TABLE IF NOT EXIST Employee("
                         + "ID INT NOT NULL CHECK(ID between 10000 AND 99999), " 
                         + "name VARCHAR(100) NOT NULL, "
	                       + "position VARCHAR(10) NOT NULL, ", 
	                       + "PRIMARY KEY(ID))");
  
  statement.executeUpdate("CREATE TABLE IF NOT EXIST Purchased("
                         + "ID INT NOT NULL CHECK (ID >= 0), "
	                       + "itemID FOREIGN KEY itemID REFERENCES items(ID), "
                      	 + "purchased_date VARCHAR(8), "
                      	 + "quantity INT NOT NULL CHECK (quantity >= 0), "
                      	 + "price DECIMAL(10,2), "
                      	 + "expiry_date VARCHAR(8), "
                      	 + "employeeID FOREIGN KEY employeeID REFERENCES employee(ID))");
  
  statement.executeUpdate("CREATE TABLE IF NOT EXIST Used("
                         + "ID FOREIGN KEY ID REFERENCES items(ID), "
	                       + "quantity INT NOT NULL CHECK (quantity >= 0), "
	                       + "date  DATE NOT NULL)");
  
  statement.executeUpdate("CREATE TABLE IF NOT EXIST Discarded("
                         + "ID FOREIGN KEY ID REFERENCES items(ID), "
	                       + "quantity INT NOT NULL CHECK (quantity >= 0), "
	                       + "date  DATE NOT NULL)"
                         + "reason VARCHAR(50) NOT NULL");
        
}
  
