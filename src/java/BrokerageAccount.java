/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.text.DecimalFormat;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author vihar
 */
@ManagedBean
@RequestScoped

public class BrokerageAccount {

    public String BrokerageAccount() 
    {
        //load the driver
        try {
            Class.forName("com.mysql.jdbc.Driver");

        } catch (Exception e) {
            return ("Internal Error! Please try again later");
        }
        return ("internaleError");
        
    }

    //if its not static whenever we create object will be same number.This is class level./
    private String ssn;
    private String accountNumber;
    private double balance;
    private String Symbol;
    private int price;
    private int shares;
    private String LM;
    private String order;

//    private Mainpage mainpage = null;
    private int AccountID =0;
//    private String company;

     
  
    //database connections static declarations
    static Connection conn = null;
    static Statement stat = null;
    static Statement stat1 = null;
    static ResultSet rs = null;
    static ResultSet rs1 = null;

    //Array List for buyers and sellers
    //new signup function
    public BrokerageAccount(int a, String s, double b) {
        AccountID = a;
        ssn=s;
        if (b < 0.0) {
            balance = 0;
//            System.out.println("you can only sell but cant buy");
        } else {
            balance = b;
        }

    }

//
    public String getTradeDetails() {

        if (order.equalsIgnoreCase("sell") && LM.equalsIgnoreCase("limit")) {

            try {

                String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/juturus7543";
                conn = DriverManager.getConnection(DB_URL, "juturus7543", "1445693");
                stat = conn.createStatement();

                rs = stat.executeQuery("select shares from pending_stock where "
                        + "Account_ID ='" + AccountID + "' and company ='" + Symbol + "'and status='" + order + "'");
                double currentSellerShare = 0.0;
                boolean hasrecord = false;

                if (rs.next()) {
                    currentSellerShare = rs.getDouble("shares");
                    hasrecord = true;
                    return("you have record");

                } //no record is found means no pending order then it will check is anyone available to buy.
                else {
                    //checking for any buyer with same details
                    rs = stat.executeQuery("select * from pending_stock WHERE LOWER(`status`)='buy"
                            + "' and company ='" + Symbol + "' and price>= " + price + " and Account_ID!= '" + AccountID + "'order by price desc");

                    //when he/she is not in the stock_details at all they have to be entered as pending in our case update status as sell
                  
                        if (rs.next()) {
//               
                                System.out.println("/****Selling the shares for limit price*******/");
                                int currentBuyerShare = rs.getInt("shares");
                                Trading td = new Trading();

                                td.sell(Symbol, price, shares, AccountID, rs.getString("Account_ID"), currentBuyerShare);
                                System.out.println("Trade was successful");
                                return("success");
                            }
                        else {
                            
                         stat.executeUpdate("insert into pending_stock values("
                                    + AccountID + ",'" + Symbol + "'," + shares + ","
                                    + price + "," + "'sell'," + "NOW())");
                            System.out.println("You have entered PendingStock queue");
                            return ("pending");
                        }
                        
                }
                 

            } catch (SQLException e) {
                e.printStackTrace();

            } finally {
                try {
                    rs.close();
                    stat.close();
                    conn.close();

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

        }
        
        if (order.equalsIgnoreCase("buy") && LM.equalsIgnoreCase("limit")) {

            try {

                String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/juturus7543";
                conn = DriverManager.getConnection(DB_URL, "juturus7543", "1445693");
                stat = conn.createStatement();

                rs = stat.executeQuery("select shares from pending_stock where "
                        + "Account_ID ='" + AccountID + "' and company ='" + Symbol + "'and status='" + order + "'");
                double currentBuyerrShare = 0.0;
                boolean hasrecord = false;

                if (rs.next()) {
                    currentBuyerrShare = rs.getDouble("shares");
                    hasrecord = true;
                    return("you have record");

                } //no record is found means no pending order then it will check is anyone available to buy.
                else {
                    //checking for any buyer with same details
                    rs = stat.executeQuery("select * from pending_stock WHERE LOWER(`status`)='sell"
                            + "' and company ='" + Symbol + "' and price<= " + price + " and Account_ID!= '" + AccountID + "'order by price desc");

                    //when he/she is not in the stock_details at all they have to be entered as pending in our case update status as sell
                  
                        if (rs.next()) {
//               
                                System.out.println("/****Buying the shares for limit price*******/");
                                int currentSellerShare = rs.getInt("shares");
                                Trading td = new Trading();

                                td.buy(Symbol, price, shares, rs.getString("Account_ID"),AccountID,currentSellerShare);
                                System.out.println("Trade was successful");
                                return("success");
                            }
                        else {
                            
                         stat.executeUpdate("insert into pending_stock values("
                                    + AccountID + ",'" + Symbol + "'," + shares + ","
                                    + price + "," + "'buy'," + "NOW())");
                            System.out.println("You have entered PendingStock queue");
                            return ("pending");
                        }
                        
                }
                 

            } catch (SQLException e) {
                e.printStackTrace();

            } finally {
                try {
                    rs.close();
                    stat.close();
                    conn.close();

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

        }
    
    return("No trade");
    }
    
    public void displayorder(String AccountID) {
        try {

            //connect to database
            String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/juturus7543";
            conn = DriverManager.getConnection(DB_URL, "juturus7543", "1445693");
            stat = conn.createStatement();

            rs = stat.executeQuery("select * from user_orders  where Account_ID = '" + AccountID + "' order by RegisterTime desc");
            System.out.printf("Account_ID\tBalance\tCompany\tPrice\tshares\tstatus\tTimeStamp\n");
            while (rs.next()) {

                System.out.println(rs.getInt(1) + "\t" + rs.getString(2) + "\t"
                        + rs.getDouble(3) + "\t" + rs.getInt(4) + "\t" + rs.getString(5) + "\t" + rs.getString(6));
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            try {
                conn.close();
                stat.close();
                rs.close();

            } catch (Exception e) {

            }

        }

    }


    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String Symbol) {
        this.Symbol = Symbol;
    }
    
    
      public int getShares() {
        return shares;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getLM() {
        return LM;
    }

    public void setLM(String LM) {
        this.LM = LM;
    }

//    public String getSsn() {
//        return ssn;
//    }
//
//    public void setSsn(String ssn) {
//        this.ssn = ssn;
//    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    

}
