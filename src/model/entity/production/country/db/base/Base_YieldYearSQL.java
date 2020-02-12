


package model.entity.production.country.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import model.entity.production.country.YieldYear;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_YieldYearSQL{

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
  private static final String SQL_INSERT = "insert into YieldYear (year,reportDate,country,reportStatus,commodity,source,value,weightUnit,comment,updatedBy,updatedAt) values (?,?,?,?,?,?,?,?,?,?,?)";
  private static final String SQL_UPDATE = "update YieldYear set value=?,weightUnit=?,comment=?,updatedBy=?,updatedAt=? where year=? and reportDate=? and country=? and reportStatus=? and commodity=? and source=?";
  private static final String SQL_DELETE = "delete from YieldYear where year=? and reportDate=? and country=? and reportStatus=? and commodity=? and source=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "yy.year,yy.reportDate,yy.country,yy.reportStatus,yy.commodity,yy.source,yy.value,yy.weightUnit,yy.comment,yy.updatedBy,yy.updatedAt";
  public static final String SQL_TABLE = "YieldYear yy";
  public static final String SQL_ALIAS = "yy";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(YieldYear obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,YieldYear obj) throws SQLException{
   
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
   public static void setInsertValues(PreparedStatement  ps,YieldYear obj)throws SQLException{
      int col =0;
         DBUtil.setString(ps, ++col,obj.getYear()); DBUtil.setLong(ps, ++col,obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
              DBUtil.setInt(ps, ++col, obj.getReportStatus());
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col,obj.getSource());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(YieldYear obj) throws SQLException{
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
  public static boolean update( Connection con ,YieldYear obj) throws SQLException{
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
    public static void setUpdateValues(PreparedStatement  ps,YieldYear obj)throws SQLException{
    int col= setValues(ps,obj,0);
        DBUtil.setString(ps, ++col, obj.getYear()); DBUtil.setLong(ps, ++col, obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
              DBUtil.setInt(ps, ++col, obj.getReportStatus());
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col, obj.getSource());      
  }
  
  
  
     
     public static boolean save(YieldYear obj  ) throws SQLException{
       YieldYear   _obj =  getObj( 
        obj.getYear()  , 
        obj.getReportDate()  , 
        obj.getCountry()  , 
        obj.getReportStatus()  , 
        obj.getCommodity()  , 
        obj.getSource()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
      public static boolean save(List<YieldYear >objs  ) throws SQLException{
       Connection con = null;
       PreparedStatement ps_update =null,ps_insert=null;
      try{
      con = getConnection();
      con.setAutoCommit(false);
              ps_update =con.prepareStatement(SQL_UPDATE);
   
       ps_insert =con.prepareStatement(SQL_INSERT);
     
      
      for(YieldYear obj:objs){
      
       YieldYear   _obj =  getObj(con, 
        obj.getYear()  , 
        obj.getReportDate()  , 
        obj.getCountry()  , 
        obj.getReportStatus()  , 
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
     java.lang.String
year , long
reportDate , model.constant.Country
country , int
reportStatus , model.constant.Commodity
commodity , java.lang.String
source ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, year , reportDate , country , reportStatus , commodity , source);
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
  
    public static boolean delete(Connection con,java.lang.String
year , long
reportDate , model.constant.Country
country , int
reportStatus , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_YieldYearSQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
     setDelete(   ps ,year , reportDate , country , reportStatus , commodity , source
    ) ;
        ps.executeUpdate();    
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
  public static boolean delete(
     List<YieldYear >objs
     ) throws SQLException{
     Connection con = null; PreparedStatement ps = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
       ps = con.prepareStatement(SQL_DELETE);
      
      for(YieldYear obj:objs){
      int col= setDelete(ps, 
          obj.getYear()  ,    obj.getReportDate()  , 
             obj.getCountry()
              ,    obj.getReportStatus()   , 
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
  
  
  
   public static int setDelete( PreparedStatement ps ,java.lang.String
year , long
reportDate , model.constant.Country
country , int
reportStatus , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
     int col = 0;
       DBUtil.setString(ps, ++col, year); DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, country.getCountry());
              DBUtil.setInt(ps, ++col, reportStatus);
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);  
    return col;
    }
  
  
  
  
  
  
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static YieldYear getObj(
          java.lang.String
year , long
reportDate , model.constant.Country
country , int
reportStatus , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          year , reportDate , country , reportStatus , commodity , source); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static YieldYear getObj(Connection conn,
          java.lang.String
year , long
reportDate , model.constant.Country
country , int
reportStatus , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
      List<YieldYear>  list = getObjs(conn,
          year , reportDate , country , reportStatus , commodity , source
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (YieldYear)list.get(0);  
      }
   } 
     
   public static List<YieldYear> getObjs( Connection conn,
      java.lang.String
year , long
reportDate , model.constant.Country
country , int
reportStatus , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
    List<YieldYear> list = new ArrayList<YieldYear>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "YEAR = ?  and  ";
          
          sql+=  "REPORTDATE = ?  and  ";
          
          sql+=  "COUNTRY = ?  and  ";
          
          sql+=  "REPORTSTATUS = ?  and  ";
          
          sql+=  "COMMODITY = ?  and  ";
          
          sql+=  "SOURCE = ?  ";
           
      LogService.sql(Base_YieldYearSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setString(ps, ++col, year); DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, country.getCountry());
              DBUtil.setInt(ps, ++col, reportStatus);
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       YieldYear  obj = new YieldYear();
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
  YieldYear obj,
  
	int col) throws SQLException{
         
         String year= rs.getString(++col) ;
         
    
         long  reportDate=rs.getLong(++col);
          
    model.constant.Country    country = model.constant.Country.getCountry(rs.getString(++col));
             
    
         int reportStatus=  rs.getInt(++col);
         
    model.constant.Commodity    commodity = model.constant.Commodity.getCommodity(rs.getString(++col));
             
     
         String source= rs.getString(++col) ;
         
    
            double value =  rs.getDouble( ++col);
          
    model.constant.WeightUnit    weightUnit = model.constant.WeightUnit.getWeightUnit(rs.getString(++col));
             
     
         String comment= rs.getString(++col) ;
         
     
         String updatedBy= rs.getString(++col) ;
         
    java.util.Date updatedAt =DBUtil.getDate(rs, ++col);
    
				obj.setYear	(year);
				
    
				obj.setReportDate	(reportDate);
				
    
				obj.setCountry	(country);
				
    
				obj.setValue	(value);
				
    
				obj.setWeightUnit	(weightUnit);
				
    
				obj.setReportStatus	(reportStatus);
				
    
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

  public static int setValues(PreparedStatement ps, YieldYear obj, int col) throws SQLException{
     
         DBUtil.setDouble(ps, ++col, obj.getValue());
    
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


