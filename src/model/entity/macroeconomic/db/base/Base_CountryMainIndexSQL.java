


package model.entity.macroeconomic.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import model.entity.macroeconomic.CountryMainIndex;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_CountryMainIndexSQL{

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
  private static final String SQL_INSERT = "insert into CountryMainIndex (reportDate,reportHour,source,country,title,currency,importance,inference,forecastValue,actualValue,previousValue,remark,mark,description,comment,updatedBy,updatedAt) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  private static final String SQL_UPDATE = "update CountryMainIndex set currency=?,importance=?,inference=?,forecastValue=?,actualValue=?,previousValue=?,remark=?,mark=?,description=?,comment=?,updatedBy=?,updatedAt=? where reportDate=? and reportHour=? and source=? and country=? and title=?";
  private static final String SQL_DELETE = "delete from CountryMainIndex where reportDate=? and reportHour=? and source=? and country=? and title=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "cmi.reportDate,cmi.reportHour,cmi.source,cmi.country,cmi.title,cmi.currency,cmi.importance,cmi.inference,cmi.forecastValue,cmi.actualValue,cmi.previousValue,cmi.remark,cmi.mark,cmi.description,cmi.comment,cmi.updatedBy,cmi.updatedAt";
  public static final String SQL_TABLE = "CountryMainIndex cmi";
  public static final String SQL_ALIAS = "cmi";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(CountryMainIndex obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,CountryMainIndex obj) throws SQLException{
   
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
   public static void setInsertValues(PreparedStatement  ps,CountryMainIndex obj)throws SQLException{
      int col =0;
         DBUtil.setLong(ps, ++col,obj.getReportDate()); DBUtil.setInt(ps, ++col, obj.getReportHour()); DBUtil.setString(ps, ++col,obj.getSource());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
              DBUtil.setString(ps, ++col,obj.getTitle());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(CountryMainIndex obj) throws SQLException{
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
  public static boolean update( Connection con ,CountryMainIndex obj) throws SQLException{
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
    public static void setUpdateValues(PreparedStatement  ps,CountryMainIndex obj)throws SQLException{
    int col= setValues(ps,obj,0);
        DBUtil.setLong(ps, ++col, obj.getReportDate()); DBUtil.setInt(ps, ++col, obj.getReportHour()); DBUtil.setString(ps, ++col, obj.getSource());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
              DBUtil.setString(ps, ++col, obj.getTitle());      
  }
  
  
  
     
     public static boolean save(CountryMainIndex obj  ) throws SQLException{
       CountryMainIndex   _obj =  getObj( 
        obj.getReportDate()  , 
        obj.getReportHour()  , 
        obj.getSource()  , 
        obj.getCountry()  , 
        obj.getTitle()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
      public static boolean save(List<CountryMainIndex >objs  ) throws SQLException{
       Connection con = null;
       PreparedStatement ps_update =null,ps_insert=null;
      try{
      con = getConnection();
      con.setAutoCommit(false);
              ps_update =con.prepareStatement(SQL_UPDATE);
   
       ps_insert =con.prepareStatement(SQL_INSERT);
     
      
      for(CountryMainIndex obj:objs){
      
       CountryMainIndex   _obj =  getObj(con, 
        obj.getReportDate()  , 
        obj.getReportHour()  , 
        obj.getSource()  , 
        obj.getCountry()  , 
        obj.getTitle()  );
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
reportDate , int
reportHour , java.lang.String
source , model.constant.Country
country , java.lang.String
title ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, reportDate , reportHour , source , country , title);
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
reportDate , int
reportHour , java.lang.String
source , model.constant.Country
country , java.lang.String
title
    ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_CountryMainIndexSQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
     setDelete(   ps ,reportDate , reportHour , source , country , title
    ) ;
        ps.executeUpdate();    
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
  public static boolean delete(
     List<CountryMainIndex >objs
     ) throws SQLException{
     Connection con = null; PreparedStatement ps = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
       ps = con.prepareStatement(SQL_DELETE);
      
      for(CountryMainIndex obj:objs){
      int col= setDelete(ps, 
           obj.getReportDate()  ,    obj.getReportHour()   ,   obj.getSource()  , 
             obj.getCountry()
              ,   obj.getTitle() );
        
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
reportDate , int
reportHour , java.lang.String
source , model.constant.Country
country , java.lang.String
title
    ) throws SQLException{
     int col = 0;
       DBUtil.setLong(ps, ++col, reportDate); DBUtil.setInt(ps, ++col, reportHour); DBUtil.setString(ps, ++col, source);
            DBUtil.setString(ps, ++col, country.getCountry());
              DBUtil.setString(ps, ++col, title);  
    return col;
    }
  
  
  
  
  
  
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static CountryMainIndex getObj(
          long
reportDate , int
reportHour , java.lang.String
source , model.constant.Country
country , java.lang.String
title
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          reportDate , reportHour , source , country , title); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static CountryMainIndex getObj(Connection conn,
          long
reportDate , int
reportHour , java.lang.String
source , model.constant.Country
country , java.lang.String
title
   ) throws SQLException{
      List<CountryMainIndex>  list = getObjs(conn,
          reportDate , reportHour , source , country , title
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (CountryMainIndex)list.get(0);  
      }
   } 
     
   public static List<CountryMainIndex> getObjs( Connection conn,
      long
reportDate , int
reportHour , java.lang.String
source , model.constant.Country
country , java.lang.String
title
   ) throws SQLException{
    List<CountryMainIndex> list = new ArrayList<CountryMainIndex>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "REPORTDATE = ?  and  ";
          
          sql+=  "REPORTHOUR = ?  and  ";
          
          sql+=  "SOURCE = ?  and  ";
          
          sql+=  "COUNTRY = ?  and  ";
          
          sql+=  "TITLE = ?  ";
           
      LogService.sql(Base_CountryMainIndexSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setLong(ps, ++col, reportDate); DBUtil.setInt(ps, ++col, reportHour); DBUtil.setString(ps, ++col, source);
            DBUtil.setString(ps, ++col, country.getCountry());
              DBUtil.setString(ps, ++col, title);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       CountryMainIndex  obj = new CountryMainIndex();
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
  CountryMainIndex obj,
  
	int col) throws SQLException{
        
         long  reportDate=rs.getLong(++col);
          
    
         int reportHour=  rs.getInt(++col);
         
     
         String source= rs.getString(++col) ;
         
    model.constant.Country    country = model.constant.Country.getCountry(rs.getString(++col));
             
     
         String title= rs.getString(++col) ;
         
     
         String currency= rs.getString(++col) ;
         
    
         int importance=  rs.getInt(++col);
         
     
         String inference= rs.getString(++col) ;
         
     
         String forecastValue= rs.getString(++col) ;
         
     
         String actualValue= rs.getString(++col) ;
         
     
         String previousValue= rs.getString(++col) ;
         
     
         String remark= rs.getString(++col) ;
         
     
         String mark= rs.getString(++col) ;
         
     
         String description= rs.getString(++col) ;
         
     
         String comment= rs.getString(++col) ;
         
     
         String updatedBy= rs.getString(++col) ;
         
    java.util.Date updatedAt =DBUtil.getDate(rs, ++col);
    
				obj.setReportDate	(reportDate);
				
    
				obj.setReportHour	(reportHour);
				
    
				obj.setSource	(source);
				
    
				obj.setCountry	(country);
				
    
				obj.setTitle	(title);
				
    
				obj.setCurrency	(currency);
				
    
				obj.setImportance	(importance);
				
    
				obj.setInference	(inference);
				
    
				obj.setForecastValue	(forecastValue);
				
    
				obj.setActualValue	(actualValue);
				
    
				obj.setPreviousValue	(previousValue);
				
    
				obj.setRemark	(remark);
				
    
				obj.setMark	(mark);
				
    
				obj.setDescription	(description);
				
    
				obj.setComment	(comment);
				
    
				obj.setUpdatedBy	(updatedBy);
				
    
				obj.setUpdatedAt	(updatedAt);
				

    
   
     return col;
  }  
  
  /**
   * code auto write. ready only file. don't change any code. 
   */  

  public static int setValues(PreparedStatement ps, CountryMainIndex obj, int col) throws SQLException{
     
         DBUtil.setString(ps, ++col,obj.getCurrency());
     DBUtil.setInt(ps, ++col,obj.getImportance());
     DBUtil.setString(ps, ++col,obj.getInference());
     DBUtil.setString(ps, ++col,obj.getForecastValue());
     DBUtil.setString(ps, ++col,obj.getActualValue());
     DBUtil.setString(ps, ++col,obj.getPreviousValue());
     DBUtil.setString(ps, ++col,obj.getRemark());
     DBUtil.setString(ps, ++col,obj.getMark());
     DBUtil.setString(ps, ++col,obj.getDescription());
     DBUtil.setString(ps, ++col,obj.getComment());
     DBUtil.setString(ps, ++col,obj.getUpdatedBy());
    DBUtil.setTimestamp(ps, ++col, obj.getUpdatedAt());

     
     return col;
  }  
  
  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }
}


