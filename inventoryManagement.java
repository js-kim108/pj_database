import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.sql.PreparedStatement;
import java.io.File;
import java.io.IOException;

/**
 * Inventory Management for Restaurant
 *
 * @author Jisu Kim
 * @version Mar.30, 2026
 */

public class InventoryManagement{
    public static Scanner scan = new Scanner(System.in);
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        File inventory = new File("inventory_setup.txt");
        File employeeSetUp = new File("employee_setup.txt");
        Scanner scanFile = new Scanner(inventory);
        Scanner scanEmployeeFile = new Scanner(employeeSetUp);
        
        Connection conn = null;
        
        try {
            Class.forName("org.sqlite.JDBC");
        
            conn = DriverManager.getConnection("jdbc:sqlite:finalPJ.db");
            conn.setAutoCommit(false);
            Statement statement = conn.createStatement();
    
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Items("
                    + "ID INTEGER NOT NULL, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "quantity INT NOT NULL CHECK (quantity >= 0), "
                    + "type VARCHAR(20), "
                    + "price DECIMAL(10, 2), "
                    + "PRIMARY KEY(ID))");
    
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Employee("
                    + "ID INT NOT NULL CHECK(ID between 10000 AND 99999), "
                    + "name VARCHAR(100) NOT NULL, "
                    + "position VARCHAR(10) NOT NULL, "
                    + "PRIMARY KEY(ID))");
    
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Purchased("
                    + "ID INT NOT NULL CHECK (ID >= 0),"
                    + "itemID INT NOT NULL,"
                    + "purchased_date VARCHAR(8),"
                    + "quantity INT NOT NULL CHECK (quantity >= 0),"
                    + "price DECIMAL(10,2),"
                    + "expiry_date DATE NOT NULL,"
                    + "employeeID INT NOT NULL,"
                    + "PRIMARY KEY (ID),"
                    + "FOREIGN KEY (itemID) REFERENCES Items(ID),"
                    + "FOREIGN KEY (employeeID) REFERENCES Employee(ID))");
    
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Used("
                    + "ID INT NOT NULL,"
                    + "quantity INT NOT NULL CHECK (quantity >= 0),"
                    + "date DATE NOT NULL,"
                    + "FOREIGN KEY (ID) REFERENCES Items(ID))");
    
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Discarded("
                    + "ID INT NOT NULL,"
                    + "quantity INT NOT NULL CHECK (quantity >= 0),"
                    + "date DATE NOT NULL,"
                    + "reason VARCHAR(50) NOT NULL,"
                    + "FOREIGN KEY (ID) REFERENCES Items(ID))");
            conn.commit();
            
            while(scanFile.hasNext()){
                insertItemsString(conn, scanFile.nextLine());
            }
            while(scanEmployeeFile.hasNext()){
                insertEmployeeString(conn, scanEmployeeFile.nextLine());
            }
            
            // show all tables at beginning
            getAllItems(conn);
            getAllEmployee(conn);
            getAllPurchased(conn);
            getAllDiscarded(conn);
            getAllUsed(conn);
            
            
            //add values to each tables:
            insertItemsString(conn, "55123, Carrot, 40, Vegetable, 0.99");
            insertItemsString(conn, "62341, Pork Belly, 25, Meat, 6.99");
            insertItemsString(conn, "78912, Cod Fillet, 15, Seafood, 7.49");
            insertItemsString(conn, "91234, Brown Rice, 80, Grain, 3.29");
    
            insertEmployeeString(conn, "56789, Emma Davis, Waiter");
            insertEmployeeString(conn, "72345, Carlos Lopez, Chef");
            insertEmployeeString(conn, "81234, Mia Wilson, Manager");
            insertEmployeeString(conn, "93456, Noah Brown, Waiter");
    
            insertPurchasedString(conn, "20001, 10234, 2024-01-10, 20, 39.80, 2024-03-10, 34521");
            insertPurchasedString(conn, "20002, 23891, 2024-01-11, 15, 82.35, 2024-02-11, 67890");
            insertPurchasedString(conn, "20003, 45672, 2024-01-12, 10, 89.90, 2024-04-12, 23456");
            insertPurchasedString(conn, "20004, 10234, 2024-01-13, 25, 49.75, 2024-03-13, 89012");
    
            insertUsedString(conn, "10234, 5, 2024-01-15");
            insertUsedString(conn, "23891, 3, 2024-01-16");
            insertUsedString(conn, "45672, 2, 2024-01-17");
            insertUsedString(conn, "11098, 4, 2024-01-18");
    
            insertDiscardedString(conn, "67543, 2, 2024-01-20, Expired");
            insertDiscardedString(conn, "32187, 1, 2024-01-21, Damaged");
            insertDiscardedString(conn, "89456, 3, 2024-01-22, Spoiled");
            insertDiscardedString(conn, "54321, 1, 2024-01-23, Contaminated");
            
            
            // show changed tables
            getAllItems(conn);
            getAllEmployee(conn);
            getAllPurchased(conn);
            getAllDiscarded(conn);
            getAllUsed(conn);
            
            
            // show select join tables
            getOrdersByEmployee(conn);
            getRecentOrderByItem(conn);
    
        } catch(SQLException | ClassNotFoundException e){
            e.printStackTrace();
            try {
                if(conn != null){
                    conn.rollback();
                    System.out.println("Something went wrong. Please try again.");
                }
            } catch(SQLException ex){
                ex.printStackTrace();
            }
        } finally {
            try {
                if(conn != null){
                    conn.close();
                    System.out.println("Connection closed.");
                }
                scanFile.close();
                scanEmployeeFile.close();
            } catch(SQLException e){
                e.printStackTrace();
            }
        }
    }

    
    // SELECT * FROM each tables
    public static void getAllItems(Connection conn) throws SQLException{
        String sql = "SELECT * FROM Items";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
        
            if(!rs.next()){
                System.out.println("No items found.");
                return;
            }
            System.out.println("\nItems: ");
            System.out.println("ID | Name | Quantity | Type | Price");
            System.out.println("------------------------------------");
            do {
                int ID = rs.getInt("ID");
                String name = rs.getString("name");
                int quantity = rs.getInt("quantity");
                String type = rs.getString("type");
                double price = rs.getDouble("price");
                System.out.println(ID + " | " + name + " | " + quantity + " | " + type + " | " + price);
            } while(rs.next());
        }
    }
    
    public static void getAllEmployee(Connection conn) throws SQLException{
        String sql = "SELECT * FROM Employee";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
    
            if(!rs.next()){
                System.out.println("No employees found.");
                return;
            }
            System.out.println("\nEmployee: ");
            System.out.println("ID | Name | Position");
            System.out.println("--------------------");
            do {
                int ID = rs.getInt("ID");
                String name = rs.getString("name");
                String position = rs.getString("position");
                System.out.println(ID + " | " + name + " | " + position);
            } while(rs.next());
        }
    }
    
    public static void getAllPurchased(Connection conn) throws SQLException{
        String sql = "SELECT * FROM Purchased";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
    
            if(!rs.next()){
                System.out.println("No purchased records found.");
                return;
            }
            System.out.println("\n Purchased: ");
            System.out.println("ID | ItemID | Purchased Date | Quantity | Price | Expiry Date | EmployeeID");
            System.out.println("--------------------------------------------------------------------------");
            do {
                int ID = rs.getInt("ID");
                int itemID = rs.getInt("itemID");
                String purchased_date = rs.getString("purchased_date");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                String expiry_date = rs.getString("expiry_date");
                int employeeID = rs.getInt("employeeID");
                System.out.println(ID + " | " + itemID + " | " + purchased_date + " | " + quantity + " | " + price + " | " + expiry_date + " | " + employeeID);
            } while(rs.next());
        }
    }
    
    public static void getAllUsed(Connection conn) throws SQLException{
        String sql = "SELECT * FROM Used";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
    
            if(!rs.next()){
                System.out.println("No used records found.");
                return;
            }
            System.out.println("\nUsed: ");
            System.out.println("ID | Quantity | Date");
            System.out.println("--------------------");
            do {
                int ID = rs.getInt("ID");
                int quantity = rs.getInt("quantity");
                String date = rs.getString("date");
                System.out.println(ID + " | " + quantity + " | " + date);
            } while(rs.next());
        }
    }
    
    public static void getAllDiscarded(Connection conn) throws SQLException{
        String sql = "SELECT * FROM Discarded";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
    
            if(!rs.next()){
                System.out.println("No discarded records found.");
                return;
            }
            System.out.println("\nDiscarded: ");
            System.out.println("ID | Quantity | Date | Reason");
            System.out.println("------------------------------");
            do {
                int ID = rs.getInt("ID");
                int quantity = rs.getInt("quantity");
                String date = rs.getString("date");
                String reason = rs.getString("reason");
                System.out.println(ID + " | " + quantity + " | " + date + " | " + reason);
            } while(rs.next());
        }
    }
    
    // get employee 
    public static void getOrdersByEmployee(Connection conn) throws SQLException{
        String sql = "SELECT Employee.ID, Employee.name, SUM(Purchased.quantity) as total_ordered "
                   + "FROM Purchased "
                   + "JOIN Employee ON Purchased.employeeID = Employee.ID "
                   + "GROUP BY Employee.ID";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            ResultSet rs = pstmt.executeQuery();
            if(!rs.next()){
                System.out.println("No records found.");
                return;
            }
            System.out.println("\nOrder record by each employee:");
            System.out.println("EmployeeID | Name | Total Items Ordered");
            System.out.println("----------------------------------------");
            do {
                int ID = rs.getInt("ID");
                String name = rs.getString("name");
                int totalOrdered = rs.getInt("total_ordered");
                System.out.println(ID + " | " + name + " | " + totalOrdered);
            } while(rs.next());
        }
    }
    
    //get recent order date for each items
    public static void getRecentOrderByItem(Connection conn) throws SQLException{
        String sql = "SELECT Items.ID, Items.name, MAX(Purchased.purchased_date) as recent_order_date "
                   + "FROM Purchased "
                   + "JOIN Items ON Purchased.itemID = Items.ID "
                   + "GROUP BY Items.ID "
                   + "ORDER BY recent_order_date DESC";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            ResultSet rs = pstmt.executeQuery();
            if(!rs.next()){
                System.out.println("No records found.");
                return;
            }
            System.out.println("\nRecent orders for each items:");
            System.out.println("ItemID | Item Name | Most Recent Order Date");
            System.out.println("--------------------------------------------");
            do {
                int ID = rs.getInt("ID");
                String name = rs.getString("name");
                String recentDate = rs.getString("recent_order_date");
                System.out.println(ID + " | " + name + " | " + recentDate);
            } while(rs.next());
        }
    }
    
    
    // insert values to table using manual user input
    public static void insertItems(Connection conn) throws SQLException{
        System.out.println("Add Items. Please follow format below");
        System.out.println("ID, name, quantity, type, price");
        String input = scan.nextLine();
        insertItemsString(conn, input);
    }
    
    public static void insertEmployee(Connection conn) throws SQLException{
        System.out.println("Add Employee. Please follow format below");
        System.out.println("ID, name, position");
        String input = scan.nextLine();
        insertEmployeeString(conn, input);
    }
    
    public static void insertPurchased(Connection conn) throws SQLException{
        System.out.println("Add Purchased record. Please follow format below");
        System.out.println("ID, itemID, purchased_date, quantity, price, expiry_date, employeeID");
        String input = scan.nextLine();
        insertPurchasedString(conn, input);
    }
    
    public static void insertUsed(Connection conn) throws SQLException{
        System.out.println("Add Used record. Please follow format below");
        System.out.println("ID, quantity, date");
        String input = scan.nextLine();
        insertUsedString(conn, input);
    }
    
    public static void insertDiscarded(Connection conn) throws SQLException{
        System.out.println("Add Discarded record. Please follow format below");
        System.out.println("ID, quantity, date, reason");
        String input = scan.nextLine();
        insertDiscardedString(conn, input);
    }
    
    
    //insert values to tables with given string
    public static void insertItemsString(Connection conn, String input) throws SQLException{
        String[] values = input.split(", ");
        
        if(values.length != 5){
            System.out.println("Invalid format. Please check each values seperated by one comma followed by one space.");
            return;
        }
        
        int ID = Integer.parseInt(values[0]);
        String name = values[1];
        int quantity = Integer.parseInt(values[2]);
        String type = values[3];
        double price = Double.parseDouble(values[4]);
        
        String sql = "INSERT INTO Items (ID, name, quantity, type, price) VALUES (?, ?, ?, ?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ID);
            pstmt.setString(2, name);
            pstmt.setInt(3, quantity);
            pstmt.setString(4, type);
            pstmt.setDouble(5, price);
            pstmt.executeUpdate();
            conn.commit();
            System.out.println("Successfully inserted.");
            
        }
    }
    
    public static void insertEmployeeString(Connection conn, String input) throws SQLException{
        String[] values = input.split(", ");
        
        if(values.length != 3){
            System.out.println("Invalid format. Please check each values seperated by one comma followed by one space.");
            return;
        }
        
        int ID = Integer.parseInt(values[0]);
        String name = values[1];
        String position = values[2];
        
        String sql = "INSERT INTO Employee (ID, name, position) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ID);
            pstmt.setString(2, name);
            pstmt.setString(3, position);
            pstmt.executeUpdate();
            conn.commit();
            System.out.println("Successfully inserted.");
        }
    }
    
    public static void insertPurchasedString(Connection conn, String input) throws SQLException{
        String[] values = input.split(", ");
        if(values.length != 7){
            System.out.println("Invalid format. Please check each values seperated by one comma followed by one space.");
            return;
        }
        int ID = Integer.parseInt(values[0]);
        int itemID = Integer.parseInt(values[1]);
        String purchased_date = values[2];
        int quantity = Integer.parseInt(values[3]);
        double price = Double.parseDouble(values[4]);
        String expiry_date = values[5];
        int employeeID = Integer.parseInt(values[6]);
        String insertSql = "INSERT INTO Purchased (ID, itemID, purchased_date, quantity, price, expiry_date, employeeID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement pstmt = conn.prepareStatement(insertSql)){
            pstmt.setInt(1, ID);
            pstmt.setInt(2, itemID);
            pstmt.setString(3, purchased_date);
            pstmt.setInt(4, quantity);
            pstmt.setDouble(5, price);
            pstmt.setString(6, expiry_date);
            pstmt.setInt(7, employeeID);
            pstmt.executeUpdate();
        }
        String updateQuantitySql = "UPDATE Items SET quantity = quantity + ? WHERE ID = ?";
        try(PreparedStatement updateStatement = conn.prepareStatement(updateQuantitySql)){
            updateStatement.setInt(1, quantity);
            updateStatement.setInt(2, itemID);
            updateStatement.executeUpdate();
        }
        String updatePriceSql = "UPDATE Items SET price = ? WHERE ID = ?";
        try(PreparedStatement updateStatement = conn.prepareStatement(updatePriceSql)){
            updateStatement.setDouble(1, price);
            updateStatement.setInt(2, itemID);
            updateStatement.executeUpdate();
        }
        conn.commit();
        System.out.println("Successfully inserted.");
    }
    
    public static void insertUsedString(Connection conn, String input) throws SQLException{
        String[] values = input.split(", ");
        if(values.length != 3){
            System.out.println("Invalid format. Please check each values seperated by one comma followed by one space.");
            return;
        }
        int ID = Integer.parseInt(values[0]);
        int quantity = Integer.parseInt(values[1]);
        String date = values[2];
    
        String checkIDSql = "SELECT quantity FROM Items WHERE ID = ?";
        try(PreparedStatement checkStmt = conn.prepareStatement(checkIDSql)){
            checkStmt.setInt(1, ID);
            ResultSet rs = checkStmt.executeQuery();
            if(!rs.next()){
                System.out.println("Item ID " + ID + " not found.");
                return;
            }
            int currentQuantity = rs.getInt("quantity");
            if(currentQuantity < quantity){
                System.out.println("Not enough quantity. Available: " + currentQuantity);
                return;
            }
        }
    
        String sql = "INSERT INTO Used (ID, quantity, date) VALUES (?, ?, ?)";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, ID);
            pstmt.setInt(2, quantity);
            pstmt.setString(3, date);
            pstmt.executeUpdate();
        }
        String updateSql = "UPDATE Items SET quantity = quantity - ? WHERE ID = ?";
        try(PreparedStatement updateStatement = conn.prepareStatement(updateSql)){
            updateStatement.setInt(1, quantity);
            updateStatement.setInt(2, ID);
            updateStatement.executeUpdate();
        }
        conn.commit();
        System.out.println("Successfully inserted.");
    }
    
    public static void insertDiscardedString(Connection conn, String input) throws SQLException{
        String[] values = input.split(", ");
        if(values.length != 4){
            System.out.println("Invalid format. Please check each values seperated by one comma followed by one space.");
            return;
        }
        int ID = Integer.parseInt(values[0]);
        int quantity = Integer.parseInt(values[1]);
        String date = values[2];
        String reason = values[3];
    
        // Check if item ID exists
        String checkIDSql = "SELECT quantity FROM Items WHERE ID = ?";
        try(PreparedStatement checkStmt = conn.prepareStatement(checkIDSql)){
            checkStmt.setInt(1, ID);
            ResultSet rs = checkStmt.executeQuery();
            if(!rs.next()){
                System.out.println("Item ID " + ID + " not found.");
                return;
            }
            // Check if enough quantity available
            int currentQuantity = rs.getInt("quantity");
            if(currentQuantity < quantity){
                System.out.println("Not enough quantity. Available: " + currentQuantity);
                return;
            }
        }
    
        String sql = "INSERT INTO Discarded (ID, quantity, date, reason) VALUES (?, ?, ?, ?)";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, ID);
            pstmt.setInt(2, quantity);
            pstmt.setString(3, date);
            pstmt.setString(4, reason);
            pstmt.executeUpdate();
        }
        String updateSql = "UPDATE Items SET quantity = quantity - ? WHERE ID = ?";
        try(PreparedStatement updateStatement = conn.prepareStatement(updateSql)){
            updateStatement.setInt(1, quantity);
            updateStatement.setInt(2, ID);
            updateStatement.executeUpdate();
        }
        conn.commit();
        System.out.println("Successfully inserted.");
    }
}
