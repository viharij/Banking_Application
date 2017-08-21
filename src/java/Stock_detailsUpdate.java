/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


/**
 *
 * @author vihar
 */
public class Stock_detailsUpdate 
{
    
    private static Connection conn = null;
    static Statement stat = null;
    static ResultSet rs = null;
    
    public void Update(double price,int share,String symbol)
    {
      conn = DAO.getConnection();
        try {
            stat = conn.createStatement();
            rs=stat.executeQuery("select * from stock_details where company='"+symbol+"'");
                   
            if(rs.next())
            {
            if(rs.getInt("LowestSellPrice")>price)
            {
                
                stat.executeUpdate("update stock_details set LowestSellPrice="+price+",SellingShares='"+share
                        +"'where company='"+symbol+"'");
            
            }
            if(rs.getInt("HighestBuyPrice")<price)
            {
            stat.executeUpdate("update stock_details set HighestBuyPrice="+price+",BuyingShares='"+share+
                    "'where company='"+symbol+"'");
            }
            stat.executeUpdate("update stock_details set RecentTradedPrice="+price+"where company='"+symbol+"'");
            }
        }
        catch(Exception e)
        {
        e.printStackTrace();
        }
        finally
        {
            try
            {
            
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        
        }
        
    
      }
    
    
}
