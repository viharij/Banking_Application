/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vihar
 */
public class DAO {
    
    private static final String DB_URL="jdbc:mysql://mis-sql.uhcl.edu/juturus7543";
    
     //database connections static declarations
    private static Connection conn = null;
    private static Statement stat = null;
    
    
    private DAO() {
        
    }
     
    public static Connection getConnection()
    {
        if(conn == null) {
        try {
            conn = DriverManager.getConnection(DB_URL, "juturus7543", "1445693");
        } catch (SQLException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    return conn;
    }
    
//    public static Statement getStatement(Connection con)
//    {
//        
//        try {
//            stat=con.createStatement();
//        } catch (SQLException ex) {
//            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return stat;
//    }
    
}
