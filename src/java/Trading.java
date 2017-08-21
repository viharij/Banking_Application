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
public class Trading {

    //database connections static declarations
    private static Connection conn = null;
    static Statement stat = null;
    static ResultSet rs = null;

    public Trading() {

    }

    public static void update() {

    }

    public void sell(String symbol, double price, int share, int accountSeller, String accountBuyer, int currentBuyershare) {
        double sellprice = price * share;

        try {
            String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/juturus7543";
            conn = DriverManager.getConnection(DB_URL, "juturus7543", "1445693");
            stat = conn.createStatement();

//            //fetch the number of shares in both buyer and seller
//            rs=stat.executeQuery("select Shares from pending_stock where "
//                    + "Account_ID ='"+accountSeller + "' and company ='"+symbol+"'");
//            int currentSellerShare = 0;
//            if(rs.next())
//                currentSellerShare =rs.getInt("Shares"); 
            int currentSellerShare = share;
            //check this and retain the remaining seller shares in the pending stock table  
            if (currentSellerShare > currentBuyershare) {
                //**************Seller details*****************************

//            stat.executeUpdate("update pending_stock set shares=shares-"+currentSellerShare+
//                              " where Account_ID='"+accountSeller+"'and company ='"+symbol+"'");
                stat.executeUpdate("insert into pending_stock values("
                        + accountSeller + ",'" + symbol + "'," + (currentSellerShare - currentBuyershare) + ","
                        + price + ",'sell'," + "NOW())");

//            rs=stat.executeQuery("select balance from pending_stock where Account_ID='"+accountSeller+"' and company='"+symbol+"'");
                double sellerfinalbal = 0;
//            if(rs.next())
//            {
//                sellerfinalbal=rs.getInt("balance");
//            }

                //seller user_orders details
                stat.executeUpdate("insert into user_orders values("
                        + accountSeller + ",'" + symbol + "'," + price + ","
                        + share + ",'Sold this number of shares'," + "NOW())");

                //update the BrokerageAccount table with the present seller balance for seller record
                stat.executeUpdate("update brokerageaccount set balance=balance+'" + sellprice + "' where AccountID='" + accountSeller + "'");

                //***************************Buyer Details*************************************
                //buyer user_order details
//            double buyerfinalbal=0.0;
////            rs=stat.executeQuery("select balance from pending_stock where Account_ID='"+accountBuyer+"' and company='"+symbol+"'");
////            if(rs.next()){
////            
////             buyerfinalbal=rs.getInt("balance");
////            }
//            buyerfinalbal = sellprice*share;
                stat.executeUpdate("insert into user_orders values("
                        + accountBuyer + ",'" + symbol + "'," + price + ","
                        + share + "," + "'purchased this number of shares '," + "NOW())");

                //update the BrokerageAccount table with the present buyer balance for buyer record
                stat.executeUpdate("update brokerageaccount set balance=balance-'" + sellprice + "' where AccountID='" + accountBuyer + "'");

                //deleting buyer record as transaction is done but it will be recored in user_orders
                stat.executeUpdate("delete from pending_stock where Account_ID='" + accountBuyer + "'and company ='" + symbol + "' and LOWER(`status`)='buy'");

                //Updating the Stock_details table to retrieve the stastical information
                rs = stat.executeQuery("Select * from stock_details where company='" + symbol + "'");

                //update the recent trade price with this price
                stat.executeUpdate("update stock_details set RecentTradedPrice='" + price + "'where company='" + symbol + "'");

                //updating stock_details table
                Stock_detailsUpdate su = new Stock_detailsUpdate();
                su.Update(price, share, symbol);
                System.out.println("Stock_Details is updated too");

            } else if (currentSellerShare == currentBuyershare) {
                //insert buyer and seller record in the user_order by updating this balance and delete from pending orders table

                stat = conn.createStatement();

                //buyer record insert
                stat.executeUpdate("insert into user_orders values('"
                        + accountBuyer + "','" + symbol + "'," + price + ","
                        + share + ",'Purchased has happend for this many shares'," + "NOW())");
                //seller record insert in user_orders
                stat.executeUpdate("insert into user_orders values('"
                        + accountSeller + "','" + symbol + "'," + price + ","
                        + share + ",'Sold this number of shares'," + "NOW())");

                //delete the buyer record from the pending order table
                stat.executeUpdate("delete from pending_stock where Account_ID='" + accountBuyer + "'and company ='" + symbol + "' and LOWER(`status`)='buy'");

                //update balances in the brokerage account table for both seller and buyer records
                stat.executeUpdate("update brokerageaccount set balance=balance-'" + sellprice + "' where AccountID='" + accountBuyer + "'");

                //update the BrokerageAccount table with the present seller balance for seller record
                stat.executeUpdate("update brokerageaccount set balance=balance+'" + sellprice + "' where AccountID='" + accountSeller + "'");

                //updating stock_details table
                Stock_detailsUpdate su = new Stock_detailsUpdate();
                su.Update(price, share, symbol);
                System.out.println("Stock_Details is updated too");

            } //if Seller  has less number of shares than the buyer
            else {
                //UPDATE BUYER INFORMATION

//             stat.executeUpdate("update stock_details set balance=balance+30,shares=shares-90 where Account_ID='10002' and company ='FB'");
                stat.executeUpdate("update pending_stock set shares=shares-" + share
                        + " where Account_ID='" + accountBuyer + "'and company ='" + symbol + "'");
//              
                //insert into user_order for transactions
//              rs=stat.executeQuery("select balance from pending_stock where Account_ID='"+accountBuyer+"' and company='"+symbol+"'");
//                double finalbal=0.0;
//                if(rs.next())
//                    finalbal = rs.getInt("balance");

                //Buyer user_orders details
                stat.executeUpdate("insert into user_orders values('"
                        + accountBuyer + "','" + symbol + "'," + price + ","
                        + share + ",'Purchased has happend for this many shares'," + "NOW())");

                //update the BrokerageAccount table with the present buyer balance for buyer record
                stat.executeUpdate("update brokerageaccount set balance=balance-'" + sellprice + "' where AccountID='" + accountBuyer + "'");

                /**
                 * ************Seller Record********************
                 */
                //insert first in user_orders then delete from pending stock
                stat.executeUpdate("insert into user_orders values('"
                        + accountSeller + "','" + symbol + "'," + price + ","
                        + share + ",'Sold is done'," + "NOW())");

                //update the BrokerageAccount table with the present seller balance for seller record
                stat.executeUpdate("update brokerageaccount set balance=balance+'" + sellprice + "' where AccountID='" + accountSeller + "'");

                //delete seller record as the purchase will be done completely.
                stat.executeUpdate("delete from pending_stock where Account_ID='" + accountSeller + "'and company ='" + symbol + "' and LOWER(`status`)='sell'");

                //update the recent trade price with this price
                stat.executeUpdate("update stock_details set RecentTradedPrice='" + price + "'where company='" + symbol + "'");

                //updating stock_details table
                Stock_detailsUpdate su = new Stock_detailsUpdate();
                su.Update(price, share, symbol);
                System.out.println("Stock_Details is updated too");

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void buy(String symbol, double price, int share, String accountSeller, int accountBuyer, int currentSellershare) {
        double buyprice = price * share;

        try {
            String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/juturus7543";
            conn = DriverManager.getConnection(DB_URL, "juturus7543", "1445693");
            stat = conn.createStatement();

            int currentBuyerShare = share;
            //check this and retain the remaining seller shares in the pending stock table  
            if (currentBuyerShare > currentSellershare) {
                //**************Buyer details*****************************

//            stat.executeUpdate("update pending_stock set shares=shares-"+currentSellerShare+
//                              " where Account_ID='"+accountSeller+"'and company ='"+symbol+"'");
                stat.executeUpdate("insert into pending_stock values("
                        + accountBuyer + ",'" + symbol + "'," + (currentBuyerShare - currentSellershare) + ","
                        + price + ",'buy'," + "NOW())");

//            rs=stat.executeQuery("select balance from pending_stock where Account_ID='"+accountSeller+"' and company='"+symbol+"'");
                double sellerfinalbal = 0;
//            if(rs.next())
//            {
//                sellerfinalbal=rs.getInt("balance");
//            }

                //Buyer user_orders details
                stat.executeUpdate("insert into user_orders values("
                        + accountBuyer + ",'" + symbol + "'," + price + ","
                        + share + ",'Purchased'," + "NOW())");

                //update the BrokerageAccount table with the present seller balance for seller record
                stat.executeUpdate("update brokerageaccount set balance=balance-'" + buyprice + "' where AccountID='" + accountBuyer + "'");

                //***************************Seller Details*************************************
                //buyer user_order details
//            double buyerfinalbal=0.0;
////            rs=stat.executeQuery("select balance from pending_stock where Account_ID='"+accountBuyer+"' and company='"+symbol+"'");
////            if(rs.next()){
////            
////             buyerfinalbal=rs.getInt("balance");
////            }
//            buyerfinalbal = sellprice*share;
                stat.executeUpdate("insert into user_orders values("
                        + accountSeller + ",'" + symbol + "'," + price + ","
                        + share + "," + "'Sold this number of shares '," + "NOW())");

                //update the BrokerageAccount table with the present buyer balance for buyer record
                stat.executeUpdate("update brokerageaccount set balance=balance+'" + buyprice + "' where AccountID='" + accountSeller + "'");

                //deleting buyer record as transaction is done but it will be recored in user_orders
                stat.executeUpdate("delete from pending_stock where Account_ID='" + accountSeller + "'and company ='" + symbol + "' and LOWER(`status`)='sell'");

                //update the recent trade price with this price
                stat.executeUpdate("update stock_details set RecentTradedPrice='" + price + "'where company='" + symbol + "'");

                //updating stock_details table
                Stock_detailsUpdate su = new Stock_detailsUpdate();
                su.Update(price, share, symbol);
                System.out.println("Stock_Details is updated too");

            } else if (currentSellershare == currentBuyerShare) {
                //insert buyer and seller record in the user_order by updating this balance and delete from pending orders table

                stat = conn.createStatement();

                //seller record insert
                stat.executeUpdate("insert into user_orders values('"
                        + accountSeller + "','" + symbol + "'," + price + ","
                        + share + ",'Sold has happend for this many shares'," + "NOW())");
                //buyer record insert in user_orders
                stat.executeUpdate("insert into user_orders values('"
                        + accountBuyer + "','" + symbol + "'," + price + ","
                        + share + ",'Purchased this number of shares'," + "NOW())");

                //delete the Seller record from the pending order table
                stat.executeUpdate("delete from pending_stock where Account_ID='" + accountSeller + "' and company ='" + symbol + "' and LOWER(`status`)='sell'");

                //update balances in the brokerage account table for both seller and buyer records
                stat.executeUpdate("update brokerageaccount set balance=balance-'" + buyprice + "' where AccountID='" + accountBuyer + "'");

                //update the BrokerageAccount table with the present seller balance for seller record
                stat.executeUpdate("update brokerageaccount set balance=balance+'" + buyprice + "' where AccountID='" + accountSeller + "'");

                //updating stock_details table
                Stock_detailsUpdate su = new Stock_detailsUpdate();
                su.Update(price, share, symbol);

                System.out.println("Stock_Details is updated too");

            } //if buyer  has less number of shares than the seller
            else {
                //UPDATE seller INFORMATION

//             stat.executeUpdate("update stock_details set balance=balance+30,shares=shares-90 where Account_ID='10002' and company ='FB'");
                stat.executeUpdate("update pending_stock set shares=shares-" + share
                        + " where Account_ID='" + accountSeller + "'and company ='" + symbol + "'");
//              
                //insert into user_order for transactions
//              rs=stat.executeQuery("select balance from pending_stock where Account_ID='"+accountBuyer+"' and company='"+symbol+"'");
//                double finalbal=0.0;
//                if(rs.next())
//                    finalbal = rs.getInt("balance");

                //Seller user_orders details
                stat.executeUpdate("insert into user_orders values('"
                        + accountSeller + "','" + symbol + "'," + price + ","
                        + share + ",'Sold has happend for this many shares'," + "NOW())");

                //update the BrokerageAccount table with the present seller balance for buyer record
                stat.executeUpdate("update brokerageaccount set balance=balance+'" + buyprice + "' where AccountID='" + accountSeller + "'");

                /**
                 * ************Buyer Record********************
                 */
                //insert first in user_orders then delete from pending stock
                stat.executeUpdate("insert into user_orders values('"
                        + accountBuyer + "','" + symbol + "'," + price + ","
                        + share + ",'Purchase is done'," + "NOW())");

                //update the BrokerageAccount table with the present buyer balance for buyer record
                stat.executeUpdate("update brokerageaccount set balance=balance-'" + buyprice + "' where AccountID='" + accountBuyer + "'");

                //delete buyer record as the purchase will be done completely.
                stat.executeUpdate("delete from pending_stock where Account_ID='" + accountBuyer + "'and company ='" + symbol + "' and LOWER(`status`)='buy'");

                //updating stock_details table
                Stock_detailsUpdate su = new Stock_detailsUpdate();
                su.Update(price, share, symbol);
                System.out.println("Stock_Details is updated too");

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void sellMarket(String symbol, int price, int share, String accountSeller, String accountBuyer, int currentBuyershare) {
        double sellprice = price * share;
        try {
            String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/juturus7543";
            conn = DriverManager.getConnection(DB_URL, "juturus7543", "1445693");
            stat = conn.createStatement();

            int currentSellerShare = share;
            //check this and retain the remaining seller shares in the pending stock table  
            if (currentSellerShare > currentBuyershare) {
                //**************Seller details*****************************

                stat.executeUpdate("insert into pending_stock values("
                        + accountSeller + ",'" + symbol + "'," + (currentSellerShare - currentBuyershare) + ","
                        + "0" + ",'sell'," + "NOW())");

                double sellerfinalbal = 0;

                //seller user_orders details
                stat.executeUpdate("insert into user_orders values("
                        + accountSeller + ",'" + symbol + "'," + price + ","
                        + share + ",'Sold this number of shares'," + "NOW())");

                //update the BrokerageAccount table with the present seller balance for seller record
                stat.executeUpdate("update brokerageaccount set balance=balance+'" + sellprice + "' where AccountID='" + accountSeller + "'");

                //***************************Buyer Details*************************************
                stat.executeUpdate("insert into user_orders values("
                        + accountBuyer + ",'" + symbol + "'," + price + ","
                        + share + "," + "'purchased this number of shares '," + "NOW())");

                //update the BrokerageAccount table with the present buyer balance for buyer record
                stat.executeUpdate("update brokerageaccount set balance=balance-'" + sellprice + "' where AccountID='" + accountBuyer + "'");

                //deleting buyer record as transaction is done but it will be recored in user_orders
                stat.executeUpdate("delete from pending_stock where Account_ID='" + accountBuyer + "'and company ='" + symbol + "' and LOWER(`status`)='buy'");

                //Updating the Stock_details table to retrieve the stastical information
                rs = stat.executeQuery("Select * from stock_details where company='" + symbol + "'");

                //update the recent trade price with this price
                stat.executeUpdate("update stock_details set RecentTradedPrice='" + price + "'where company='" + symbol + "'");

                //updating stock_details table
                Stock_detailsUpdate su = new Stock_detailsUpdate();
                su.Update(price, share, symbol);
                System.out.println("Stock_Details is updated too");

            } else if (currentSellerShare == currentBuyershare) {
                //insert buyer and seller record in the user_order by updating this balance and delete from pending orders table

                stat = conn.createStatement();

                //buyer record insert
                stat.executeUpdate("insert into user_orders values('"
                        + accountBuyer + "','" + symbol + "'," + price + ","
                        + share + ",'Purchased has happend for this many shares'," + "NOW())");
                //seller record insert in user_orders
                stat.executeUpdate("insert into user_orders values('"
                        + accountSeller + "','" + symbol + "'," + price + ","
                        + share + ",'Sold this number of shares'," + "NOW())");

                //delete the buyer record from the pending order table
                stat.executeUpdate("delete from pending_stock where Account_ID='" + accountBuyer + "'and company ='" + symbol + "' and LOWER(`status`)='buy'");

                //update balances in the brokerage account table for both seller and buyer records
                stat.executeUpdate("update brokerageaccount set balance=balance-'" + sellprice + "' where AccountID='" + accountBuyer + "'");

                //update the BrokerageAccount table with the present seller balance for seller record
                stat.executeUpdate("update brokerageaccount set balance=balance+'" + sellprice + "' where AccountID='" + accountSeller + "'");

                //updating stock_details table
                Stock_detailsUpdate su = new Stock_detailsUpdate();
                su.Update(price, share, symbol);
                System.out.println("Stock_Details is updated too");

            } //if Seller  has less number of shares than the buyer
            else {
                //UPDATE BUYER INFORMATION          

                stat.executeUpdate("update pending_stock set shares=shares-" + share
                        + " where Account_ID='" + accountBuyer + "'and company ='" + symbol + "'");

                stat.executeUpdate("insert into user_orders values('"
                        + accountBuyer + "','" + symbol + "'," + price + ","
                        + share + ",'Purchased has happend for this many shares'," + "NOW())");

                //update the BrokerageAccount table with the present buyer balance for buyer record
                stat.executeUpdate("update brokerageaccount set balance=balance-'" + sellprice + "' where AccountID='" + accountBuyer + "'");

                /**
                 * ************Seller Record********************
                 */
                //insert first in user_orders then delete from pending stock
                stat.executeUpdate("insert into user_orders values('"
                        + accountSeller + "','" + symbol + "'," + price + ","
                        + share + ",'Sold is done'," + "NOW())");

                //update the BrokerageAccount table with the present seller balance for seller record
                stat.executeUpdate("update brokerageaccount set balance=balance+'" + sellprice + "' where AccountID='" + accountSeller + "'");

                //delete seller record as the purchase will be done completely.
                stat.executeUpdate("delete from pending_stock where Account_ID='" + accountSeller + "'and company ='" + symbol + "' and LOWER(`status`)='sell'");

                //update the recent trade price with this price
                stat.executeUpdate("update stock_details set RecentTradedPrice='" + price + "'where company='" + symbol + "'");

                //updating stock_details table
                Stock_detailsUpdate su = new Stock_detailsUpdate();
                su.Update(price, share, symbol);
                System.out.println("Stock_Details is updated too");

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public void buyMarket(String symbol, int price, int share, String accountSeller, String accountBuyer, int currentSellershare) {

        double buyprice = price * share;

        try {
            String DB_URL = "jdbc:mysql://mis-sql.uhcl.edu/juturus7543";
            conn = DriverManager.getConnection(DB_URL, "juturus7543", "1445693");
            stat = conn.createStatement();

            int currentBuyerShare = share;
            //check this and retain the remaining seller shares in the pending stock table  
            if (currentBuyerShare > currentSellershare) {
                //**************Buyer details*****************************
                stat.executeUpdate("insert into pending_stock values("
                        + accountBuyer + ",'" + symbol + "'," + (currentBuyerShare - currentSellershare) + ","
                        + price + ",'buy'," + "NOW())");


                double sellerfinalbal = 0;

                //Buyer user_orders details
                stat.executeUpdate("insert into user_orders values("
                        + accountBuyer + ",'" + symbol + "'," + price + ","
                        + share + ",'Purchased'," + "NOW())");

                //update the BrokerageAccount table with the present seller balance for seller record
                stat.executeUpdate("update brokerageaccount set balance=balance-'" + buyprice + "' where AccountID='" + accountBuyer + "'");

                //***************************Seller Details*************************************
                //seller user_order details
                stat.executeUpdate("insert into user_orders values("
                        + accountSeller + ",'" + symbol + "'," + price + ","
                        + share + "," + "'Sold this number of shares '," + "NOW())");

                //update the BrokerageAccount table with the present seller balance for seller record
                stat.executeUpdate("update brokerageaccount set balance=balance+'" + buyprice + "' where AccountID='" + accountSeller + "'");

                //deleting seller record as transaction is done but it will be recored in user_orders
                stat.executeUpdate("delete from pending_stock where Account_ID='" + accountSeller + "'and company ='" + symbol + "' and LOWER(`status`)='sell'");

                //update the recent trade price with this price
                stat.executeUpdate("update stock_details set RecentTradedPrice='" + price + "'where company='" + symbol + "'");

                //updating stock_details table
                Stock_detailsUpdate su = new Stock_detailsUpdate();
                su.Update(price, share, symbol);
                System.out.println("Stock_Details is updated too");

            } else if (currentSellershare == currentBuyerShare) {
                //insert buyer and seller record in the user_order by updating this balance and delete from pending orders table

                stat = conn.createStatement();

                //seller record insert
                stat.executeUpdate("insert into user_orders values('"
                        + accountSeller + "','" + symbol + "'," + price + ","
                        + share + ",'Sold has happend for this many shares'," + "NOW())");
                //buyer record insert in user_orders
                stat.executeUpdate("insert into user_orders values('"
                        + accountBuyer + "','" + symbol + "'," + price + ","
                        + share + ",'Purchased this number of shares'," + "NOW())");

                //delete the Seller record from the pending order table
                stat.executeUpdate("delete from pending_stock where Account_ID='" + accountSeller + "' and company ='" + symbol + "' and LOWER(`status`)='sell'");

                //update balances in the brokerage account table for both seller and buyer records
                //buyer record
                stat.executeUpdate("update brokerageaccount set balance=balance-'" + buyprice + "' where AccountID='" + accountBuyer + "'");

                //update the BrokerageAccount table with the present seller balance for seller record
                stat.executeUpdate("update brokerageaccount set balance=balance+'" + buyprice + "' where AccountID='" + accountSeller + "'");

                //updating stock_details table
                Stock_detailsUpdate su = new Stock_detailsUpdate();
                su.Update(price, share, symbol);

                System.out.println("Stock_Details is updated too");

            } //if buyer  has less number of shares than the seller
            else {
                //UPDATE seller INFORMATION


                stat.executeUpdate("update pending_stock set shares=shares-" + share
                        + " where Account_ID='" + accountSeller + "'and company ='" + symbol + "'");
           //Seller user_orders details
                stat.executeUpdate("insert into user_orders values('"
                        + accountSeller + "','" + symbol + "'," + price + ","
                        + share + ",'Sold has happend for this many shares'," + "NOW())");

                //update the BrokerageAccount table with the present seller balance for buyer record
                stat.executeUpdate("update brokerageaccount set balance=balance+'" + buyprice + "' where AccountID='" + accountSeller + "'");

                /**
                 * ************Buyer Record********************
                 */
                //insert first in user_orders then delete from pending stock
                stat.executeUpdate("insert into user_orders values('"
                        + accountBuyer + "','" + symbol + "'," + price + ","
                        + share + ",'Purchase is done'," + "NOW())");

                //update the BrokerageAccount table with the present buyer balance for buyer record
                stat.executeUpdate("update brokerageaccount set balance=balance-'" + buyprice + "' where AccountID='" + accountBuyer + "'");

                //delete buyer record as the purchase will be done completely.
                stat.executeUpdate("delete from pending_stock where Account_ID='" + accountBuyer + "'and company ='" + symbol + "' and LOWER(`status`)='buy'");

                //updating stock_details table
                Stock_detailsUpdate su = new Stock_detailsUpdate();
                su.Update(price, share, symbol);
                System.out.println("Stock_Details is updated too");

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

}
