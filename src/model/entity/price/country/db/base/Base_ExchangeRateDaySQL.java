


package model.entity.price.country.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import model.entity.price.country.ExchangeRateDay;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_ExchangeRateDaySQL{

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
  private static final String SQL_INSERT = "insert into ExchangeRateDay (reportDate,fromCurreny,toCurreny,source,value,comment,updatedBy,updatedAt) values (?,?,?,?,?,?,?,?)";
  private static final String SQL_UPDATE = "update ExchangeRateDay set value=?,comment=?,updatedBy=?,updatedAt=? where reportDate=? and fromCurreny=? and toCurreny=? and source=?";
  private static final String SQL_DELETE = "delete from ExchangeRateDay where reportDate=? and fromCurreny=? and toCurreny=? and source=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "erd.reportDate,erd.fromCurreny,erd.toCurreny,erd.source,erd.value,erd.comment,erd.updatedBy,erd.updatedAt";
  public static final String SQL_TABLE = "ExchangeRateDay erd";
  public static final String SQL_ALIAS = "erd";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(ExchangeRateDay obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,ExchangeRateDay obj) throws SQLException{
   
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
   public static void setInsertValues(PreparedStatement  ps,ExchangeRateDay obj)throws SQLException{
      int col =0;
         DBUtil.setLong(ps, ++col,obj.getReportDate()); DBUtil.setString(ps, ++col,obj.getFromCurreny()); DBUtil.setString(ps, ++col,obj.getToCurreny()); DBUtil.setString(ps, ++col,obj.getSource());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(ExchangeRateDay obj) throws SQLException{
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
  public static boolean update( Connection con ,ExchangeRateDay obj) throws SQLException{
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
    public static void setUpdateValues(PreparedStatement  ps,ExchangeRateDay obj)throws SQLException{
    int col= setValues(ps,obj,0);
        DBUtil.setLong(ps, ++col, obj.getReportDate()); DBUtil.setString(ps, ++col, obj.getFromCurreny()); DBUtil.setString(ps, ++col, obj.getToCurreny()); DBUtil.setString(ps, ++col, obj.getSource());      
  }
  
  
  
     
     public static boolean save(ExchangeRateDay obj  ) throws SQLException{
       ExchangeRateDay   _obj =  getObj( 
        obj.getReportDate()  , 
        obj.getFromCurreny()  , 
        obj.getToCurreny()  , 
        obj.getSource()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
      public static boolean save(List<ExchangeRateDay >objs  ) throws SQLException{
       Connection con = null;
       PreparedStatement ps_update =null,ps_insert=null;
      try{
      con = getConnection();
      con.setAutoCommit(false);
              ps_update =con.prepareStatement(SQL_UPDATE);
   
       ps_insert =con.prepareStatement(SQL_INSERT);
     
      
      for(ExchangeRateDay obj:objs){
      
       ExchangeRateDay   _obj =  getObj(con, 
        obj.getReportDate()  , 
        obj.getFromCurreny()  , 
        obj.getToCurreny()  , 
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
reportDate , java.lang.String
fromCurreny , java.lang.String
toCurreny , java.lang.String
source ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, reportDate , fromCurreny , toCurreny , source);
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
reportDate , java.lang.String
fromCurreny , java.lang.String
toCurreny , java.lang.String
source
    ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_ExchangeRateDaySQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
     setDelete(   ps ,reportDate , fromCurreny , toCurreny , source
    ) ;
        ps.executeUpdate();    
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
  public static boolean delete(
     List<ExchangeRateDay >objs
     ) throws SQLException{
     Connection con = null; PreparedStatement ps = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
       ps = con.prepareStatement(SQL_DELETE);
      
      for(ExchangeRateDay obj:objs){
      int col= setDelete(ps, 
           obj.getReportDate()  ,   obj.getFromCurreny()  ,   obj.getToCurreny()  ,   obj.getSource() );
        
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
reportDate , java.lang.String
fromCurreny , java.lang.String
toCurreny , java.lang.String
source
    ) throws SQLException{
     int col = 0;
       DBUtil.setLong(ps, ++col, reportDate); DBUtil.setString(ps, ++col, fromCurreny); DBUtil.setString(ps, ++col, toCurreny); DBUtil.setString(ps, ++col, source);  
    return col;
    }
  
  
  
  
  
  
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static ExchangeRateDay getObj(
          long
reportDate , java.lang.String
fromCurreny , java.lang.String
toCurreny , java.lang.String
source
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          reportDate , fromCurreny , toCurreny , source); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static ExchangeRateDay getObj(Connection conn,
          long
reportDate , java.lang.String
fromCurreny , java.lang.String
toCurreny , java.lang.String
source
   ) throws SQLException{
      List<ExchangeRateDay>  list = getObjs(conn,
          reportDate , fromCurreny , toCurreny , source
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (ExchangeRateDay)list.get(0);  
      }
   } 
     
   public static List<ExchangeRateDay> getObjs( Connection conn,
      long
reportDate , java.lang.String
fromCurreny , java.lang.String
toCurreny , java.lang.String
source
   ) throws SQLException{
    List<ExchangeRateDay> list = new ArrayList<ExchangeRateDay>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "REPORTDATE = ?  and  ";
          
          sql+=  "FROMCURRENY = ?  and  ";
          
          sql+=  "TOCURRENY = ?  and  ";
          
          sql+=  "SOURCE = ?  ";
           
      LogService.sql(Base_ExchangeRateDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setLong(ps, ++col, reportDate); DBUtil.setString(ps, ++col, fromCurreny); DBUtil.setString(ps, ++col, toCurreny); DBUtil.setString(ps, ++col, source);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       ExchangeRateDay  obj = new ExchangeRateDay();
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
  ExchangeRateDay obj,
  
	int col) throws SQLException{
        
         long  reportDate=rs.getLong(++col);
          
     
         String fromCurreny= rs.getString(++col) ;
         
     
         String toCurreny= rs.getString(++col) ;
         
     
         String source= rs.getString(++col) ;
         
    
            double value =  rs.getDouble( ++col);
          
     
         String comment= rs.getString(++col) ;
         
     
         String updatedBy= rs.getString(++col) ;
         
    java.util.Date updatedAt =DBUtil.getDate(rs, ++col);
    
				obj.setReportDate	(reportDate);
				
    
				obj.setFromCurreny	(fromCurreny);
				
    
				obj.setToCurreny	(toCurreny);
				
    
				obj.setValue	(value);
				
    
				obj.setSource	(source);
				
    
				obj.setComment	(comment);
				
    
				obj.setUpdatedBy	(updatedBy);
				
    
				obj.setUpdatedAt	(updatedAt);
				

    
   
     return col;
  }  
  
  /**
   * code auto write. ready only file. don't change any code. 
   */  

  public static int setValues(PreparedStatement ps, ExchangeRateDay obj, int col) throws SQLException{
     
         DBUtil.setDouble(ps, ++col, obj.getValue());
     DBUtil.setString(ps, ++col,obj.getComment());
     DBUtil.setString(ps, ++col,obj.getUpdatedBy());
    DBUtil.setTimestamp(ps, ++col, obj.getUpdatedAt());

     
     return col;
  }  
  
  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }
}


