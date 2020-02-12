


package model.entity.price.country.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import model.entity.price.country.FuturePriceDay;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_FuturePriceDaySQL{

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
  private static final String SQL_INSERT = "insert into FuturePriceDay (reportDate,country,contract,commodity,source,bourse,openingValue,topValue,minimumValue,closingValue,volumes,priceUnit,weightUnit,comment,updatedBy,updatedAt) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  private static final String SQL_UPDATE = "update FuturePriceDay set bourse=?,openingValue=?,topValue=?,minimumValue=?,closingValue=?,volumes=?,priceUnit=?,weightUnit=?,comment=?,updatedBy=?,updatedAt=? where reportDate=? and country=? and contract=? and commodity=? and source=?";
  private static final String SQL_DELETE = "delete from FuturePriceDay where reportDate=? and country=? and contract=? and commodity=? and source=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "fpd.reportDate,fpd.country,fpd.contract,fpd.commodity,fpd.source,fpd.bourse,fpd.openingValue,fpd.topValue,fpd.minimumValue,fpd.closingValue,fpd.volumes,fpd.priceUnit,fpd.weightUnit,fpd.comment,fpd.updatedBy,fpd.updatedAt";
  public static final String SQL_TABLE = "FuturePriceDay fpd";
  public static final String SQL_ALIAS = "fpd";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(FuturePriceDay obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,FuturePriceDay obj) throws SQLException{
   
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
   public static void setInsertValues(PreparedStatement  ps,FuturePriceDay obj)throws SQLException{
      int col =0;
         DBUtil.setLong(ps, ++col,obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
              DBUtil.setString(ps, ++col,obj.getContract());
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col,obj.getSource());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(FuturePriceDay obj) throws SQLException{
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
  public static boolean update( Connection con ,FuturePriceDay obj) throws SQLException{
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
    public static void setUpdateValues(PreparedStatement  ps,FuturePriceDay obj)throws SQLException{
    int col= setValues(ps,obj,0);
        DBUtil.setLong(ps, ++col, obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
              DBUtil.setString(ps, ++col, obj.getContract());
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col, obj.getSource());      
  }
  
  
  
     
     public static boolean save(FuturePriceDay obj  ) throws SQLException{
       FuturePriceDay   _obj =  getObj( 
        obj.getReportDate()  , 
        obj.getCountry()  , 
        obj.getContract()  , 
        obj.getCommodity()  , 
        obj.getSource()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
      public static boolean save(List<FuturePriceDay >objs  ) throws SQLException{
       Connection con = null;
       PreparedStatement ps_update =null,ps_insert=null;
      try{
      con = getConnection();
      con.setAutoCommit(false);
              ps_update =con.prepareStatement(SQL_UPDATE);
   
       ps_insert =con.prepareStatement(SQL_INSERT);
     
      
      for(FuturePriceDay obj:objs){
      
       FuturePriceDay   _obj =  getObj(con, 
        obj.getReportDate()  , 
        obj.getCountry()  , 
        obj.getContract()  , 
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
contract , model.constant.Commodity
commodity , java.lang.String
source ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, reportDate , country , contract , commodity , source);
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
contract , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_FuturePriceDaySQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
     setDelete(   ps ,reportDate , country , contract , commodity , source
    ) ;
        ps.executeUpdate();    
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
  public static boolean delete(
     List<FuturePriceDay >objs
     ) throws SQLException{
     Connection con = null; PreparedStatement ps = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
       ps = con.prepareStatement(SQL_DELETE);
      
      for(FuturePriceDay obj:objs){
      int col= setDelete(ps, 
           obj.getReportDate()  , 
             obj.getCountry()
              ,   obj.getContract()  , 
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
contract , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
     int col = 0;
       DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, country.getCountry());
              DBUtil.setString(ps, ++col, contract);
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);  
    return col;
    }
  
  
  
  
  
  
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static FuturePriceDay getObj(
          long
reportDate , model.constant.Country
country , java.lang.String
contract , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          reportDate , country , contract , commodity , source); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static FuturePriceDay getObj(Connection conn,
          long
reportDate , model.constant.Country
country , java.lang.String
contract , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
      List<FuturePriceDay>  list = getObjs(conn,
          reportDate , country , contract , commodity , source
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (FuturePriceDay)list.get(0);  
      }
   } 
     
   public static List<FuturePriceDay> getObjs( Connection conn,
      long
reportDate , model.constant.Country
country , java.lang.String
contract , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
    List<FuturePriceDay> list = new ArrayList<FuturePriceDay>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "REPORTDATE = ?  and  ";
          
          sql+=  "COUNTRY = ?  and  ";
          
          sql+=  "CONTRACT = ?  and  ";
          
          sql+=  "COMMODITY = ?  and  ";
          
          sql+=  "SOURCE = ?  ";
           
      LogService.sql(Base_FuturePriceDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, country.getCountry());
              DBUtil.setString(ps, ++col, contract);
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       FuturePriceDay  obj = new FuturePriceDay();
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
  FuturePriceDay obj,
  
	int col) throws SQLException{
        
         long  reportDate=rs.getLong(++col);
          
    model.constant.Country    country = model.constant.Country.getCountry(rs.getString(++col));
             
     
         String contract= rs.getString(++col) ;
         
    model.constant.Commodity    commodity = model.constant.Commodity.getCommodity(rs.getString(++col));
             
     
         String source= rs.getString(++col) ;
         
    model.constant.Bourse    bourse = model.constant.Bourse.getBourse(rs.getString(++col));
             
    
            double openingValue =  rs.getDouble( ++col);
          
    
            double topValue =  rs.getDouble( ++col);
          
    
            double minimumValue =  rs.getDouble( ++col);
          
    
            double closingValue =  rs.getDouble( ++col);
          
    
         long  volumes=rs.getLong(++col);
          
    model.constant.PriceUnit    priceUnit = model.constant.PriceUnit.getPriceUnit(rs.getString(++col));
             
    model.constant.WeightUnit    weightUnit = model.constant.WeightUnit.getWeightUnit(rs.getString(++col));
             
     
         String comment= rs.getString(++col) ;
         
     
         String updatedBy= rs.getString(++col) ;
         
    java.util.Date updatedAt =DBUtil.getDate(rs, ++col);
    
				obj.setReportDate	(reportDate);
				
    
				obj.setCountry	(country);
				
    
				obj.setContract	(contract);
				
    
				obj.setBourse	(bourse);
				
    
				obj.setOpeningValue	(openingValue);
				
    
				obj.setTopValue	(topValue);
				
    
				obj.setMinimumValue	(minimumValue);
				
    
				obj.setClosingValue	(closingValue);
				
    
				obj.setVolumes	(volumes);
				
    
				obj.setPriceUnit	(priceUnit);
				
    
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

  public static int setValues(PreparedStatement ps, FuturePriceDay obj, int col) throws SQLException{
     
        
            DBUtil.setString(ps, ++col, obj.getBourse().getBourse());
             
     DBUtil.setDouble(ps, ++col, obj.getOpeningValue());
     DBUtil.setDouble(ps, ++col, obj.getTopValue());
     DBUtil.setDouble(ps, ++col, obj.getMinimumValue());
     DBUtil.setDouble(ps, ++col, obj.getClosingValue());
     DBUtil.setLong(ps, ++col, obj.getVolumes());
    
            DBUtil.setString(ps, ++col, obj.getPriceUnit().getPriceUnit());
             
    
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


