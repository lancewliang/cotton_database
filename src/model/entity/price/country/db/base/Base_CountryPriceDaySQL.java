


package model.entity.price.country.db.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.constant.dao.Unit;
import model.entity.price.country.CountryPriceDay;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;
import tutami.fw.DB;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_CountryPriceDaySQL{

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
  private static final String SQL_INSERT = "insert into CountryPriceDay (reportDate,country,state,standard,commodity,source,value,priceUnit,unitType,unit,comment,updatedBy,updatedAt) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
  private static final String SQL_UPDATE = "update CountryPriceDay set value=?,priceUnit=?,unitType=?,unit=?,comment=?,updatedBy=?,updatedAt=? where reportDate=? and country=? and state=? and standard=? and commodity=? and source=?";
  private static final String SQL_DELETE = "delete from CountryPriceDay where reportDate=? and country=? and state=? and standard=? and commodity=? and source=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "sad.reportDate,sad.country,sad.state,sad.standard,sad.commodity,sad.source,sad.value,sad.priceUnit,sad.unitType,sad.unit,sad.comment,sad.updatedBy,sad.updatedAt";
  public static final String SQL_TABLE = "CountryPriceDay sad";
  public static final String SQL_ALIAS = "sad";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(CountryPriceDay obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,CountryPriceDay obj) throws SQLException{
   
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
   public static void setInsertValues(PreparedStatement  ps,CountryPriceDay obj)throws SQLException{
      int col =0;
         DBUtil.setLong(ps, ++col,obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
              DBUtil.setString(ps, ++col,obj.getState()); DBUtil.setString(ps, ++col,obj.getStandard());
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col,obj.getSource());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(CountryPriceDay obj) throws SQLException{
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
  public static boolean update( Connection con ,CountryPriceDay obj) throws SQLException{
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
    public static void setUpdateValues(PreparedStatement  ps,CountryPriceDay obj)throws SQLException{
    int col= setValues(ps,obj,0);
        DBUtil.setLong(ps, ++col, obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
              DBUtil.setString(ps, ++col, obj.getState()); DBUtil.setString(ps, ++col, obj.getStandard());
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col, obj.getSource());      
  }
  
  
  
     
     public static boolean save(CountryPriceDay obj  ) throws SQLException{
       CountryPriceDay   _obj =  getObj( 
        obj.getReportDate()  , 
        obj.getCountry()  , 
        obj.getState()  , 
        obj.getStandard()  , 
        obj.getCommodity()  , 
        obj.getSource()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
      public static boolean save(List<CountryPriceDay >objs  ) throws SQLException{
       Connection con = null;
       PreparedStatement ps_update =null,ps_insert=null;
      try{
      con = getConnection();
      con.setAutoCommit(false);
              ps_update =con.prepareStatement(SQL_UPDATE);
   
       ps_insert =con.prepareStatement(SQL_INSERT);
     
      
      for(CountryPriceDay obj:objs){
      
       CountryPriceDay   _obj =  getObj(con, 
        obj.getReportDate()  , 
        obj.getCountry()  , 
        obj.getState()  , 
        obj.getStandard()  , 
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
country , java.lang.String
state , java.lang.String
standard , model.constant.Commodity
commodity , java.lang.String
source ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, reportDate , country , state , standard , commodity , source);
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
country , java.lang.String
state , java.lang.String
standard , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_CountryPriceDaySQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
     setDelete(   ps ,reportDate , country , state , standard , commodity , source
    ) ;
        ps.executeUpdate();    
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
  public static boolean delete(
     List<CountryPriceDay >objs
     ) throws SQLException{
     Connection con = null; PreparedStatement ps = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
       ps = con.prepareStatement(SQL_DELETE);
      
      for(CountryPriceDay obj:objs){
      int col= setDelete(ps, 
           obj.getReportDate()  , 
             obj.getCountry()
              ,   obj.getState()  ,   obj.getStandard()  , 
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
country , java.lang.String
state , java.lang.String
standard , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
     int col = 0;
       DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, country.getCountry());
              DBUtil.setString(ps, ++col, state); DBUtil.setString(ps, ++col, standard);
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);  
    return col;
    }
  
  
  
  
  
  
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static CountryPriceDay getObj(
          long
reportDate , model.constant.Country
country , java.lang.String
state , java.lang.String
standard , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          reportDate , country , state , standard , commodity , source); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static CountryPriceDay getObj(Connection conn,
          long
reportDate , model.constant.Country
country , java.lang.String
state , java.lang.String
standard , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
      List<CountryPriceDay>  list = getObjs(conn,
          reportDate , country , state , standard , commodity , source
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (CountryPriceDay)list.get(0);  
      }
   } 
     
   public static List<CountryPriceDay> getObjs( Connection conn,
      long
reportDate , model.constant.Country
country , java.lang.String
state , java.lang.String
standard , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
    List<CountryPriceDay> list = new ArrayList<CountryPriceDay>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "REPORTDATE = ?  and  ";
          
          sql+=  "COUNTRY = ?  and  ";
          
          sql+=  "STATE = ?  and  ";
          
          sql+=  "STANDARD = ?  and  ";
          
          sql+=  "COMMODITY = ?  and  ";
          
          sql+=  "SOURCE = ?  ";
           
      LogService.sql(Base_CountryPriceDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, country.getCountry());
              DBUtil.setString(ps, ++col, state); DBUtil.setString(ps, ++col, standard);
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       CountryPriceDay  obj = new CountryPriceDay();
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
  CountryPriceDay obj,
  
	int col) throws SQLException{
        
         long  reportDate=rs.getLong(++col);
          
    model.constant.Country    country = model.constant.Country.getCountry(rs.getString(++col));
             
     
         String state= rs.getString(++col) ;
         
     
         String standard= rs.getString(++col) ;
         
    model.constant.Commodity    commodity = model.constant.Commodity.getCommodity(rs.getString(++col));
             
     
         String source= rs.getString(++col) ;
         
    
            double value =  rs.getDouble( ++col);
          
    model.constant.PriceUnit    priceUnit = model.constant.PriceUnit.getPriceUnit(rs.getString(++col));
             
    model.constant.UnitType    unitType = model.constant.UnitType.getUnitType(rs.getString(++col));
             
    Unit unit = unitType.getUnit(rs.getString(++col));
     
         String comment= rs.getString(++col) ;
         
     
         String updatedBy= rs.getString(++col) ;
         
    java.util.Date updatedAt =DBUtil.getDate(rs, ++col);
    
				obj.setReportDate	(reportDate);
				
    
				obj.setCountry	(country);
				
    
				obj.setState	(state);
				
    
				obj.setStandard	(standard);
				
    
				obj.setValue	(value);
				
    
				obj.setPriceUnit	(priceUnit);
				
    
				obj.setUnitType	(unitType);
				
    
				obj.setUnit	(unit);
				
    
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

  public static int setValues(PreparedStatement ps, CountryPriceDay obj, int col) throws SQLException{
     
         DBUtil.setDouble(ps, ++col, obj.getValue());
    
            DBUtil.setString(ps, ++col, obj.getPriceUnit().getPriceUnit());
             
    
            DBUtil.setString(ps, ++col, obj.getUnitType().getUnitType());

            DBUtil.setString(ps, ++col, obj.getUnit ().getUnit ());
           
    
     DBUtil.setString(ps, ++col,obj.getComment());
     DBUtil.setString(ps, ++col,obj.getUpdatedBy());
    DBUtil.setTimestamp(ps, ++col, obj.getUpdatedAt());

     
     return col;
  }  
  
  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }
}


