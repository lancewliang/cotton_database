


package model.entity.gov.country.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import model.entity.gov.country.GovDay;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_GovDaySQL{

  /**
   * code auto write. ready only file. don't change any code. 
   */
  protected static javax.sql.DataSource TTDB;
  static{
    TTDB = DB.getDBPoolADMIN();
  }
  
  /**
    * code auto write. ready only file. don't change any code. 
    */
  private static final String SQL_INSERT = "insert into GovDay (reportDate,country,commodity,source,buyValue,sellValue,totalBuyValue,totalSellValue,weightUnit,comment,updatedBy,updatedAt) values (?,?,?,?,?,?,?,?,?,?,?,?)";
  private static final String SQL_UPDATE = "update GovDay set buyValue=?,sellValue=?,totalBuyValue=?,totalSellValue=?,weightUnit=?,comment=?,updatedBy=?,updatedAt=? where reportDate=? and country=? and commodity=? and source=?";
  private static final String SQL_DELETE = "delete from GovDay where reportDate=? and country=? and commodity=? and source=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "erd.reportDate,erd.country,erd.commodity,erd.source,erd.buyValue,erd.sellValue,erd.totalBuyValue,erd.totalSellValue,erd.weightUnit,erd.comment,erd.updatedBy,erd.updatedAt";
  public static final String SQL_TABLE = "GovDay erd";
  public static final String SQL_ALIAS = "erd";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(GovDay obj) throws SQLException{
    Connection con = null;
    boolean ret = false;
    try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = insert(con, obj);
      con.commit();
    }catch(Exception e){
      try {
        con.rollback(); 
      } catch (Exception ex) {
      }
      throw new SQLException(e);
    }finally{     
     try {
        con.setAutoCommit(true);
      } catch (Exception e) {
      }
      DBUtil.cleanup(null, con);
    }
    return ret;
 }
    
 public  static boolean insert(Connection con,GovDay obj) throws SQLException{
   
     PreparedStatement  ps = null;
    try{     
       ps = con.prepareStatement(SQL_INSERT);
       
      setInsertValues(ps, obj);
   
      ps.executeUpdate();    
    }finally{     
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
   public static void setInsertValues(PreparedStatement  ps,GovDay obj)throws SQLException{
      int col =0;
         DBUtil.setLong(ps, ++col,obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
             
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col,obj.getSource());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(GovDay obj) throws SQLException{
    Connection con = null;
    boolean ret = false;
    try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = update(con, obj);
        con.commit();
     }catch(Exception e){
      try {
        con.rollback(); 
      } catch (Exception ex) {
      }
      throw new SQLException(e);
    }finally{     
     try {
        con.setAutoCommit(true);
      } catch (Exception e) {
      }
      DBUtil.cleanup(null, con);
    }
    return ret;
   }  
  public static boolean update( Connection con ,GovDay obj) throws SQLException{
    PreparedStatement ps = null;
    try{ 
      ps= con.prepareStatement(SQL_UPDATE);
      
     setUpdateValues(ps,obj);
      ps.executeUpdate();
    
    }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
    public static void setUpdateValues(PreparedStatement  ps,GovDay obj)throws SQLException{
    int col= setValues(ps,obj,0);
        DBUtil.setLong(ps, ++col, obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
             
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col, obj.getSource());      
  }
  
  
  
     
     public static boolean save(GovDay obj  ) throws SQLException{
       GovDay   _obj =  getObj( 
        obj.getReportDate()  , 
        obj.getCountry()  , 
        obj.getCommodity()  , 
        obj.getSource()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
      public static boolean save(List<GovDay >objs  ) throws SQLException{
       Connection con = null;
       PreparedStatement ps_update =null,ps_insert=null;
      try{
      con = getConnection();
      con.setAutoCommit(false);
              ps_update =con.prepareStatement(SQL_UPDATE);
   
       ps_insert =con.prepareStatement(SQL_INSERT);
     
      
      for(GovDay obj:objs){
      
       GovDay   _obj =  getObj(con, 
        obj.getReportDate()  , 
        obj.getCountry()  , 
        obj.getCommodity()  , 
        obj.getSource()  );
           if(_obj==null){
              
               setInsertValues(ps_insert, obj);
                  ps_insert.addBatch();
       
           }else{
               
         setUpdateValues(ps_update,obj);
         ps_update.addBatch();
 
         }
       }
       
        ps_insert.executeBatch(); 
       ps_update.executeBatch();
        con.commit();
        return true;
         }catch(Exception e){
      try {
        con.rollback(); 
      } catch (Exception ex) {
      }
      throw new SQLException(e);
         }finally{     
            try {
            con.setAutoCommit(true);
          } catch (Exception e) {
        }     DBUtil.cleanup(ps_insert, null);
            DBUtil.cleanup(ps_update, null);
      DBUtil.cleanup(null, con);
      }
       
    }
     
    /**
   * code auto write. ready only file. don't change any code.
   */      
    
  
   public static boolean delete(
     long
reportDate , model.constant.Country
country , model.constant.Commodity
commodity , java.lang.String
source ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, reportDate , country , commodity , source);
          con.commit();
      }catch(Exception e){
      try {
        con.rollback(); 
      } catch (Exception ex) {
      }
      throw new SQLException(e);
    }finally{     
     try {
        con.setAutoCommit(true);
      } catch (Exception e) {
      }
      DBUtil.cleanup(null, con);
      }
    return ret;
   }
  
    public static boolean delete(Connection con,long
reportDate , model.constant.Country
country , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_GovDaySQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
     setDelete(   ps ,reportDate , country , commodity , source
    ) ;
        ps.executeUpdate();    
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
  public static boolean delete(
     List<GovDay >objs
     ) throws SQLException{
     Connection con = null; PreparedStatement ps = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
       ps = con.prepareStatement(SQL_DELETE);
      
      for(GovDay obj:objs){
      int col= setDelete(ps, 
           obj.getReportDate()  , 
             obj.getCountry()
              , 
             obj.getCommodity()
              ,   obj.getSource() );
        
          ps.addBatch();
       }  ps.executeBatch(); 
          con.commit();
      }catch(Exception e){
      try {
        con.rollback(); 
      } catch (Exception ex) {
      }
      throw new SQLException(e);
    }finally{     
     try {
        con.setAutoCommit(true);
      } catch (Exception e) {
      }
      DBUtil.cleanup(ps, con);
      }
    return ret;
   }
  
  
  
   public static int setDelete( PreparedStatement ps ,long
reportDate , model.constant.Country
country , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
     int col = 0;
       DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, country.getCountry());
             
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);  
    return col;
    }
  
  
  
  
  
  
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static GovDay getObj(
          long
reportDate , model.constant.Country
country , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          reportDate , country , commodity , source); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static GovDay getObj(Connection conn,
          long
reportDate , model.constant.Country
country , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
      List<GovDay>  list = getObjs(conn,
          reportDate , country , commodity , source
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (GovDay)list.get(0);  
      }
   } 
     
   public static List<GovDay> getObjs( Connection conn,
      long
reportDate , model.constant.Country
country , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
    List<GovDay> list = new ArrayList<GovDay>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "REPORTDATE = ?  and  ";
          
          sql+=  "COUNTRY = ?  and  ";
          
          sql+=  "COMMODITY = ?  and  ";
          
          sql+=  "SOURCE = ?  ";
           
      LogService.sql(Base_GovDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, country.getCountry());
             
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       GovDay  obj = new GovDay();
        getValues( rs,obj, 0);
       
    
        list.add(obj);
      }
      return list;
    }finally{
      DBUtil.cleanup(rs, ps, null);
    }
  }

  /**
   * code auto write. ready only file. don't change any code. 
   */  
  public static int getValues(ResultSet rs, 
  GovDay obj,
  
	int col) throws SQLException{
        
         long  reportDate=rs.getLong(++col);
          
    model.constant.Country    country = model.constant.Country.getCountry(rs.getString(++col));
             
    model.constant.Commodity    commodity = model.constant.Commodity.getCommodity(rs.getString(++col));
             
     
         String source= rs.getString(++col) ;
         
    
            double buyValue =  rs.getDouble( ++col);
          
    
            double sellValue =  rs.getDouble( ++col);
          
    
            double totalBuyValue =  rs.getDouble( ++col);
          
    
            double totalSellValue =  rs.getDouble( ++col);
          
    model.constant.WeightUnit    weightUnit = model.constant.WeightUnit.getWeightUnit(rs.getString(++col));
             
     
         String comment= rs.getString(++col) ;
         
     
         String updatedBy= rs.getString(++col) ;
         
    java.util.Date updatedAt =DBUtil.getDate(rs, ++col);
    
				obj.setReportDate	(reportDate);
				
    
				obj.setCountry	(country);
				
    
				obj.setBuyValue	(buyValue);
				
    
				obj.setSellValue	(sellValue);
				
    
				obj.setTotalBuyValue	(totalBuyValue);
				
    
				obj.setTotalSellValue	(totalSellValue);
				
    
				obj.setWeightUnit	(weightUnit);
				
    
				obj.setCommodity	(commodity);
				
    
				obj.setSource	(source);
				
    
				obj.setComment	(comment);
				
    
				obj.setUpdatedBy	(updatedBy);
				
    
				obj.setUpdatedAt	(updatedAt);
				

    
   
     return col;
  }  
  
  /**
   * code auto write. ready only file. don't change any code. 
   */  

  public static int setValues(PreparedStatement ps, GovDay obj, int col) throws SQLException{
     
         DBUtil.setDouble(ps, ++col, obj.getBuyValue());
     DBUtil.setDouble(ps, ++col, obj.getSellValue());
     DBUtil.setDouble(ps, ++col, obj.getTotalBuyValue());
     DBUtil.setDouble(ps, ++col, obj.getTotalSellValue());
    
            DBUtil.setString(ps, ++col, obj.getWeightUnit().getWeightUnit());
             
     DBUtil.setString(ps, ++col,obj.getComment());
     DBUtil.setString(ps, ++col,obj.getUpdatedBy());
    DBUtil.setTimestamp(ps, ++col, obj.getUpdatedAt());

     
     return col;
  }  
  
  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }
}

