/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * 
 */
@ManagedBean
@RequestScoped
public class Registration {

   private String ssn;
   private String name;
   private String Login;
   private String Password;
   private String securityquestion1;
   private String securityquestion2;
   private String answer1;
   private String answer2;
   private double balance;
//   private static int nextAccountNumber = 100001;
   private static String accountNumber;

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return Login;
    }

    public void setLogin(String Login) {
        this.Login = Login;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getSecurityquestion1() {
        return securityquestion1;
    }

    public void setSecurityquestion1(String securityquestion1) {
        this.securityquestion1 = securityquestion1;
    }

    public String getSecurityquestion2() {
        return securityquestion2;
    }

    public void setSecurityquestion2(String securityquestion2) {
        this.securityquestion2 = securityquestion2;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

   

    public static String getAccountNumber() {
        return accountNumber;
    }

    public static void setAccountNumber(String accountNumber) {
        Registration.accountNumber = accountNumber;
    }

   
    public String register()
    {
        

         
        Connection connection = null;
        Statement stat = null;
        ResultSet rs = null;
        
        
        try
        {
            //load the driver
            Class.forName("com.mysql.jdbc.Driver");
            
            final String DATABASE_URL = "jdbc:mysql://mis-sql.uhcl.edu/juturus7543";
            
            //connect to the database with user name and password
            connection = DriverManager.getConnection(DATABASE_URL, 
                    "juturus7543", "1445693");   
            stat = connection.createStatement();
            //to search an onlineaccount based on id or ssn
            rs = stat.executeQuery("Select * from BrokerageAccount where AccountID='"
                    + accountNumber + "' or ssn= '" + ssn + "'");
            
            if(rs.next())
            {
                 return("Either you have an online account already "
                        + "or your online ID is not available to register");
            }
            else
            {
                
                int r = stat.executeUpdate("insert into brokerageaccount (SSN,Name,LoginID,Password,Balance"
                        + ",Question1,Answer1,Question2,Answer2,WrongAttempt) values('"
                        + ssn + "','" + name + "','" + Login + "','" + Password + "','" + balance + "','"
                        + securityquestion1 + "','" + answer1 + "','" + securityquestion2 + "','" + answer2 + "',0)");
                return ("Registration Successful! Please "
                         + "return to login your account.");

            }   
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return ("Internal Error! Please try again later.");
             
        } 
        catch (ClassNotFoundException ex) {
           Logger.getLogger(Registration.class.getName()).log(Level.SEVERE, null, ex);
           return ("Internal Error! Please try again later.");
       }
        finally
        {
            try
            {
                rs.close();
                stat.close();
                connection.close();
                
            }
            catch (Exception e)
            {
                 
                e.printStackTrace();
            }
        }
    
         
        }
      public String resetPassword()
    {
         boolean flag = false;
         
         Connection conn=null;
         Statement stat=null;
         ResultSet rs=null;
         
        try {
            //conn to the database 
            conn = DAO.getConnection();
            stat = conn.createStatement();
//            rs = stat.executeQuery("Select * from BrokerageAccount where LoginID='" + Login + "'");

//            while (flag != true) {
//                if (rs.next()) {
//                    System.out.println(rs.getString(7));
//                    answer1 = input.nextLine();
//                    System.out.println(rs.getString(9));
//                    answer2 = input.nextLine();
//
//                    if (answer1.equals(rs.getString(8)) && answer2.equals(rs.getString(10))) {
//                        System.out.println("Password is " + rs.getString(5));
//                        flag = true;
//                        login();
//                    }
//
//                }
//            }
        }
            catch (SQLException e)
            {
                      
                e.printStackTrace();
                return("internalError");
             }
             finally
             {
                try
                {
                    stat.close();
                    conn.close();
                     
                }
                catch (Exception e)
                {                 
                    e.printStackTrace();
                }
             }
        return null;
             
    } 
     
}
