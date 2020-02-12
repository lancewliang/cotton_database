


package model.entity.price.country.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import model.entity.price.country.PortPriceDay;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_PortPriceDaySQL{

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
  private static final String SQL_INSERT = "insert into PortPriceDay (reportDate,country,fromCountry,standard,term,portPriceType,commodity,source,value1,priceUnit1,weightUnit1,value2,priceUnit2,weightUnit2,comment,updatedBy,updatedAt) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  private static final String SQL_UPDATE = "update PortPriceDay set value1=?,priceUnit1=?,weightUnit1=?,value2=?,priceUnit2=?,weightUnit2=?,comment=?,updatedBy=?,updatedAt=? where reportDate=? and country=? and fromCountry=? and standard=? and term=? and portPriceType=? and commodity=? and source=?";
  private static final String SQL_DELETE = "delete from PortPriceDay where reportDate=? and country=? and fromCountry=? and standard=? and term=? and portPriceType=? and commodity=? and source=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "sad.reportDate,sad.country,sad.fromCountry,sad.standard,sad.term,sad.portPriceType,sad.commodity,sad.source,sad.value1,sad.priceUnit1,sad.weightUnit1,sad.value2,sad.priceUnit2,sad.weightUnit2,sad.comment,sad.updatedBy,sad.updatedAt";
  public static final String SQL_TABLE = "PortPriceDay sad";
  public static final String SQL_ALIAS = "sad";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(PortPriceDay obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,PortPriceDay obj) throws SQLException{
   
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
   public static void setInsertValues(PreparedStatement  ps,PortPriceDay obj)throws SQLException{
      int col =0;
         DBUtil.setLong(ps, ++col,obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
             
            DBUtil.setString(ps, ++col, obj.getFromCountry().getCountry());
              DBUtil.setString(ps, ++col,obj.getStandard()); DBUtil.setString(ps, ++col,obj.getTerm());
            DBUtil.setString(ps, ++col, obj.getPortPriceType().getPortPriceType());
             
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col,obj.getSource());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(PortPriceDay obj) throws SQLException{
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
  public static boolean update( Connection con ,PortPriceDay obj) throws SQLException{
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
    public static void setUpdateValues(PreparedStatement  ps,PortPriceDay obj)throws SQLException{
    int col= setValues(ps,obj,0);
        DBUtil.setLong(ps, ++col, obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
             
            DBUtil.setString(ps, ++col, obj.getFromCountry().getCountry());
              DBUtil.setString(ps, ++col, obj.getStandard()); DBUtil.setString(ps, ++col, obj.getTerm());
            DBUtil.setString(ps, ++col, obj.getPortPriceType().getPortPriceType());
             
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col, obj.getSource());      
  }
  
  
  
     
     public static boolean save(PortPriceDay obj  ) throws SQLException{
       PortPriceDay   _obj =  getObj( 
        obj.getReportDate()  , 
        obj.getCountry()  , 
        obj.getFromCountry()  , 
        obj.getStandard()  , 
        obj.getTerm()  , 
        obj.getPortPriceType()  , 
        obj.getCommodity()  , 
        obj.getSource()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
      public static boolean save(List<PortPriceDay >objs  ) throws SQLException{
       Connection con = null;
       PreparedStatement ps_update =null,ps_insert=null;
      try{
      con = getConnection();
      con.setAutoCommit(false);
              ps_update =con.prepareStatement(SQL_UPDATE);
   
       ps_insert =con.prepareStatement(SQL_INSERT);
     
      
      for(PortPriceDay obj:objs){
      
       PortPriceDay   _obj =  getObj(con, 
        obj.getReportDate()  , 
        obj.getCountry()  , 
        obj.getFromCountry()  , 
        obj.getStandard()  , 
        obj.getTerm()  , 
        obj.getPortPriceType()  , 
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
country , model.constant.Country
fromCountry , java.lang.String
standard , java.lang.String
term , model.constant.PortPriceType
portPriceType , model.constant.Commodity
commodity , java.lang.String
source ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, reportDate , country , fromCountry , standard , term , portPriceType , commodity , source);
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
country , model.constant.Country
fromCountry , java.lang.String
standard , java.lang.String
term , model.constant.PortPriceType
portPriceType , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_PortPriceDaySQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
     setDelete(   ps ,reportDate , country , fromCountry , standard , term , portPriceType , commodity , source
    ) ;
        ps.executeUpdate();    
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
  public static boolean delete(
     List<PortPriceDay >objs
     ) throws SQLException{
     Connection con = null; PreparedStatement ps = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
       ps = con.prepareStatement(SQL_DELETE);
      
      for(PortPriceDay obj:objs){
      int col= setDelete(ps, 
           obj.getReportDate()  , 
             obj.getCountry()
              , 
             obj.getFromCountry()
              ,   obj.getStandard()  ,   obj.getTerm()  , 
             obj.getPortPriceType()
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
country , model.constant.Country
fromCountry , java.lang.String
standard , java.lang.String
term , model.constant.PortPriceType
portPriceType , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
     int col = 0;
       DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, country.getCountry());
             
            DBUtil.setString(ps, ++col, fromCountry.getCountry());
              DBUtil.setString(ps, ++col, standard); DBUtil.setString(ps, ++col, term);
            DBUtil.setString(ps, ++col, portPriceType.getPortPriceType());
             
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);  
    return col;
    }
  
  
  
  
  
  
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static PortPriceDay getObj(
          long
reportDate , model.constant.Country
country , model.constant.Country
fromCountry , java.lang.String
standard , java.lang.String
term , model.constant.PortPriceType
portPriceType , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          reportDate , country , fromCountry , standard , term , portPriceType , commodity , source); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static PortPriceDay getObj(Connection conn,
          long
reportDate , model.constant.Country
country , model.constant.Country
fromCountry , java.lang.String
standard , java.lang.String
term , model.constant.PortPriceType
portPriceType , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
      List<PortPriceDay>  list = getObjs(conn,
          reportDate , country , fromCountry , standard , term , portPriceType , commodity , source
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (PortPriceDay)list.get(0);  
      }
   } 
     
   public static List<PortPriceDay> getObjs( Connection conn,
      long
reportDate , model.constant.Country
country , model.constant.Country
fromCountry , java.lang.String
standard , java.lang.String
term , model.constant.PortPriceType
portPriceType , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
    List<PortPriceDay> list = new ArrayList<PortPriceDay>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "REPORTDATE = ?  and  ";
          
          sql+=  "COUNTRY = ?  and  ";
          
          sql+=  "FROMCOUNTRY = ?  and  ";
          
          sql+=  "STANDARD = ?  and  ";
          
          sql+=  "TERM = ?  and  ";
          
          sql+=  "PORTPRICETYPE = ?  and  ";
          
          sql+=  "COMMODITY = ?  and  ";
          
          sql+=  "SOURCE = ?  ";
           
      LogService.sql(Base_PortPriceDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, country.getCountry());
             
            DBUtil.setString(ps, ++col, fromCountry.getCountry());
              DBUtil.setString(ps, ++col, standard); DBUtil.setString(ps, ++col, term);
            DBUtil.setString(ps, ++col, portPriceType.getPortPriceType());
             
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       PortPriceDay  obj = new PortPriceDay();
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
  PortPriceDay obj,
  
	int col) throws SQLException{
        
         long  reportDate=rs.getLong(++col);
          
    model.constant.Country    country = model.constant.Country.getCountry(rs.getString(++col));
             
    model.constant.Country    fromCountry = model.constant.Country.getCountry(rs.getString(++col));
             
     
         String standard= rs.getString(++col) ;
         
     
         String term= rs.getString(++col) ;
         
    model.constant.PortPriceType    portPriceType = model.constant.PortPriceType.getPortPriceType(rs.getString(++col));
             
    model.constant.Commodity    commodity = model.constant.Commodity.getCommodity(rs.getString(++col));
             
     
         String source= rs.getString(++col) ;
         
    
            double value1 =  rs.getDouble( ++col);
          
    model.constant.PriceUnit    priceUnit1 = model.constant.PriceUnit.getPriceUnit(rs.getString(++col));
             
    model.constant.WeightUnit    weightUnit1 = model.constant.WeightUnit.getWeightUnit(rs.getString(++col));
             
    
            double value2 =  rs.getDouble( ++col);
          
    model.constant.PriceUnit    priceUnit2 = model.constant.PriceUnit.getPriceUnit(rs.getString(++col));
             
    model.constant.WeightUnit    weightUnit2 = model.constant.WeightUnit.getWeightUnit(rs.getString(++col));
             
     
         String comment= rs.getString(++col) ;
         
     
         String updatedBy= rs.getString(++col) ;
         
    java.util.Date updatedAt =DBUtil.getDate(rs, ++col);
    
				obj.setReportDate	(reportDate);
				
    
				obj.setCountry	(country);
				
    
				obj.setFromCountry	(fromCountry);
				
    
				obj.setStandard	(standard);
				
    
				obj.setTerm	(term);
				
    
				obj.setPortPriceType	(portPriceType);
				
    
				obj.setValue1	(value1);
				
    
				obj.setPriceUnit1	(priceUnit1);
				
    
				obj.setWeightUnit1	(weightUnit1);
				
    
				obj.setValue2	(value2);
				
    
				obj.setPriceUnit2	(priceUnit2);
				
    
				obj.setWeightUnit2	(weightUnit2);
				
    
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

  public static int setValues(PreparedStatement ps, PortPriceDay obj, int col) throws SQLException{

    DBUtil.setDouble(ps, ++col, obj.getValue1());

    DBUtil.setString(ps, ++col, obj.getPriceUnit1() != null ? obj.getPriceUnit1().getPriceUnit() : null);

    DBUtil.setString(ps, ++col, obj.getWeightUnit1() != null ? obj.getWeightUnit1().getWeightUnit() : null);

    DBUtil.setDouble(ps, ++col, obj.getValue2());

    DBUtil.setString(ps, ++col, obj.getPriceUnit2() != null ? obj.getPriceUnit2().getPriceUnit() : null);

    DBUtil.setString(ps, ++col, obj.getWeightUnit2() != null ? obj.getWeightUnit2().getWeightUnit() : null);
             
     DBUtil.setString(ps, ++col,obj.getComment());
     DBUtil.setString(ps, ++col,obj.getUpdatedBy());
    DBUtil.setTimestamp(ps, ++col, obj.getUpdatedAt());

     
     return col;
  }  
  
  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }
}


