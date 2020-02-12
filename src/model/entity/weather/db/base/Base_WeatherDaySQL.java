


package model.entity.weather.db.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entity.weather.WeatherDay;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;
import tutami.fw.DB;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_WeatherDaySQL{

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
  private static final String SQL_INSERT = "insert into WeatherDay (reportDate,weatherRegion,source,high,low,precip,precipUnit,snow,snowUnit,forecast,avgHigh,avgLow,comment,updatedBy,updatedAt) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  private static final String SQL_UPDATE = "update WeatherDay set high=?,low=?,precip=?,precipUnit=?,snow=?,snowUnit=?,forecast=?,avgHigh=?,avgLow=?,comment=?,updatedBy=?,updatedAt=? where reportDate=? and weatherRegion=? and source=?";
  private static final String SQL_DELETE = "delete from WeatherDay where reportDate=? and weatherRegion=? and source=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "sd.reportDate,sd.weatherRegion,sd.source,sd.high,sd.low,sd.precip,sd.precipUnit,sd.snow,sd.snowUnit,sd.forecast,sd.avgHigh,sd.avgLow,sd.comment,sd.updatedBy,sd.updatedAt";
  public static final String SQL_TABLE = "WeatherDay sd";
  public static final String SQL_ALIAS = "sd";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(WeatherDay obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,WeatherDay obj) throws SQLException{
   
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
   public static void setInsertValues(PreparedStatement  ps,WeatherDay obj)throws SQLException{
      int col =0;
         DBUtil.setLong(ps, ++col,obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getWeatherRegion().getWeatherRegion());
              DBUtil.setString(ps, ++col,obj.getSource());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(WeatherDay obj) throws SQLException{
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
  public static boolean update( Connection con ,WeatherDay obj) throws SQLException{
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
    public static void setUpdateValues(PreparedStatement  ps,WeatherDay obj)throws SQLException{
    int col= setValues(ps,obj,0);
        DBUtil.setLong(ps, ++col, obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getWeatherRegion().getWeatherRegion());
              DBUtil.setString(ps, ++col, obj.getSource());      
  }
  
  
  
     
     public static boolean save(WeatherDay obj  ) throws SQLException{
       WeatherDay   _obj =  getObj( 
        obj.getReportDate()  , 
        obj.getWeatherRegion()  , 
        obj.getSource()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
      public static boolean save(List<WeatherDay >objs  ) throws SQLException{
       Connection con = null;
       PreparedStatement ps_update =null,ps_insert=null;
      try{
      con = getConnection();
      con.setAutoCommit(false);
              ps_update =con.prepareStatement(SQL_UPDATE);
   
       ps_insert =con.prepareStatement(SQL_INSERT);
     
      
      for(WeatherDay obj:objs){
      
       WeatherDay   _obj =  getObj(con, 
        obj.getReportDate()  , 
        obj.getWeatherRegion()  , 
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
reportDate , model.constant.WeatherRegion
weatherRegion , java.lang.String
source ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, reportDate , weatherRegion , source);
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
reportDate , model.constant.WeatherRegion
weatherRegion , java.lang.String
source
    ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_WeatherDaySQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
     setDelete(   ps ,reportDate , weatherRegion , source
    ) ;
        ps.executeUpdate();    
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
  public static boolean delete(
     List<WeatherDay >objs
     ) throws SQLException{
     Connection con = null; PreparedStatement ps = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
       ps = con.prepareStatement(SQL_DELETE);
      
      for(WeatherDay obj:objs){
      int col= setDelete(ps, 
           obj.getReportDate()  , 
             obj.getWeatherRegion()
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
reportDate , model.constant.WeatherRegion
weatherRegion , java.lang.String
source
    ) throws SQLException{
     int col = 0;
       DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, weatherRegion.getWeatherRegion());
              DBUtil.setString(ps, ++col, source);  
    return col;
    }
  
  
  
  
  
  
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static WeatherDay getObj(
          long
reportDate , model.constant.WeatherRegion
weatherRegion , java.lang.String
source
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          reportDate , weatherRegion , source); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static WeatherDay getObj(Connection conn,
          long
reportDate , model.constant.WeatherRegion
weatherRegion , java.lang.String
source
   ) throws SQLException{
      List<WeatherDay>  list = getObjs(conn,
          reportDate , weatherRegion , source
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (WeatherDay)list.get(0);  
      }
   } 
     
   public static List<WeatherDay> getObjs( Connection conn,
      long
reportDate , model.constant.WeatherRegion
weatherRegion , java.lang.String
source
   ) throws SQLException{
    List<WeatherDay> list = new ArrayList<WeatherDay>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "REPORTDATE = ?  and  ";
          
          sql+=  "WEATHERREGION = ?  and  ";
          
          sql+=  "SOURCE = ?  ";
           
      LogService.sql(Base_WeatherDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, weatherRegion.getWeatherRegion());
              DBUtil.setString(ps, ++col, source);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       WeatherDay  obj = new WeatherDay();
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
  WeatherDay obj,
  
	int col) throws SQLException{
        
         long  reportDate=rs.getLong(++col);
          
    model.constant.WeatherRegion    weatherRegion = model.constant.WeatherRegion.getWeatherRegion(rs.getString(++col));
             
     
         String source= rs.getString(++col) ;
         
    
         int high=  rs.getInt(++col);
         
    
         int low=  rs.getInt(++col);
         
    
            double precip =  rs.getDouble( ++col);
          
    model.constant.LengthUnit    precipUnit = model.constant.LengthUnit.getLengthUnit(rs.getString(++col));
             
    
            double snow =  rs.getDouble( ++col);
          
    model.constant.LengthUnit    snowUnit = model.constant.LengthUnit.getLengthUnit(rs.getString(++col));
             
     
         String forecast= rs.getString(++col) ;
         
    
         int avgHigh=  rs.getInt(++col);
         
    
         int avgLow=  rs.getInt(++col);
         
     
         String comment= rs.getString(++col) ;
         
     
         String updatedBy= rs.getString(++col) ;
         
    java.util.Date updatedAt =DBUtil.getDate(rs, ++col);
    
				obj.setReportDate	(reportDate);
				
    
				obj.setWeatherRegion	(weatherRegion);
				
    
				obj.setHigh	(high);
				
    
				obj.setLow	(low);
				
    
				obj.setPrecip	(precip);
				
    
				obj.setPrecipUnit	(precipUnit);
				
    
				obj.setSnow	(snow);
				
    
				obj.setSnowUnit	(snowUnit);
				
    
				obj.setForecast	(forecast);
				
    
				obj.setAvgHigh	(avgHigh);
				
    
				obj.setAvgLow	(avgLow);
				
    
				obj.setSource	(source);
				
    
				obj.setComment	(comment);
				
    
				obj.setUpdatedBy	(updatedBy);
				
    
				obj.setUpdatedAt	(updatedAt);
				

    
   
     return col;
  }  
  
  /**
   * code auto write. ready only file. don't change any code. 
   */  

  public static int setValues(PreparedStatement ps, WeatherDay obj, int col) throws SQLException{
     
         DBUtil.setInt(ps, ++col,obj.getHigh());
     DBUtil.setInt(ps, ++col,obj.getLow());
     DBUtil.setDouble(ps, ++col, obj.getPrecip());
    
            DBUtil.setString(ps, ++col, obj.getPrecipUnit().getLengthUnit());
             
     DBUtil.setDouble(ps, ++col, obj.getSnow());
    
            DBUtil.setString(ps, ++col, obj.getSnowUnit().getLengthUnit());
             
     DBUtil.setString(ps, ++col,obj.getForecast());
     DBUtil.setInt(ps, ++col,obj.getAvgHigh());
     DBUtil.setInt(ps, ++col,obj.getAvgLow());
     DBUtil.setString(ps, ++col,obj.getComment());
     DBUtil.setString(ps, ++col,obj.getUpdatedBy());
    DBUtil.setTimestamp(ps, ++col, obj.getUpdatedAt());

     
     return col;
  }  
  
  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }
}


