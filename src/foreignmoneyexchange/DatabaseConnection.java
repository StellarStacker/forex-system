/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package foreignmoneyexchange;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 269058
 */
public class DatabaseConnection {
    Connection connection;
    Statement statment;
    public DatabaseConnection() throws SQLException{
         try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ForeignMoneyExchange.class.getName()).log(Level.SEVERE, null, ex);
        }
        connection = DriverManager.getConnection("jdbc:sqlite:Z:\\OOSE\\forex-system\\database\\forex.db");
        System.out.println("Database connection succeeded");
    }
    
    public Connection getConnection(){
        return this.connection;
    }
    
    public static void main(String[] args) throws SQLException{
       DatabaseConnection databaseConnection =  new DatabaseConnection();
    }
           
    
}
