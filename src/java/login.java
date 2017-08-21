
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedBean;
import javax.inject.Named;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author vihar
 */
@Named(value = "login")
@ManagedBean
@SessionScoped
public class login implements Serializable {

    private String id;
    private String password;
     private BrokerageAccount theLoginAccount;
   

    public  String login() {
        String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/juturus7543";
        boolean loginflag = true;
        int accountlock = 0;

        //load the driver
        try {
            Class.forName("com.mysql.jdbc.Driver");

        } catch (Exception e) {
            return ("Internal Error! Please try again later");
        }

        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        try {

            conn = DriverManager.getConnection(DB_URL, "juturus7543", "1445693");

            stat = conn.createStatement();

            //do the query 
            rs = stat.executeQuery("Select * from BrokerageAccount where LoginID='"
                    + id + "'");
//            System.out.println("after execute query");
            if (rs.next()) {
                //check the password
                String pass = rs.getString(5);
                while (loginflag) {

                    if (password.equals(pass)) {
                        //login successful! Enter  the online account
                        loginflag = false;
                        theLoginAccount = new BrokerageAccount(rs.getInt(1), rs.getString(3), rs.getDouble(6));
                        stat.executeUpdate("update brokerageaccount set WrongAttempt=0 where LoginID='" + id + "'");
                        return ("welcome");
                    } else {
                        rs = stat.executeQuery("select WrongAttempt from brokerageaccount where LoginID='" + id + "'");
                        if (rs.next()) {
                            accountlock = rs.getInt(1);
                            accountlock++;
                            stat.executeUpdate("update brokerageaccount set WrongAttempt='" + accountlock + "'where LoginID='" + id + "'");
                        }
                    }
                    if (accountlock >= 2) {

                        loginflag = false;
                        return ("passwordretrieval");

                    }

                }

            } else {

                //return ("No userID");
                return("loginNotOK");
            }
        } catch (SQLException e) {

            return ("internalError");
        } finally {
            try {
                rs.close();
                stat.close();
                conn.close();

            } catch (Exception e) {
                e.printStackTrace();
                
            }

        }
        return("No logic");
    }
    
    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
  

    public BrokerageAccount getTheLoginAccount() {
        return theLoginAccount;
    }

    public void setTheLoginAccount(BrokerageAccount theLoginAccount) {
        this.theLoginAccount = theLoginAccount;
    }


}
