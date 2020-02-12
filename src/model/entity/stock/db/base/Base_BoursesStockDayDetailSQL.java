


package model.entity.stock.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import model.entity.stock.BoursesStockDayDetail;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_BoursesStockDayDetailSQL{

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
  private static final String SQL_INSERT = "insert into BoursesStockDayDetail (reportDate,country,bourse,wHId,wHName,annual,grade,producingArea,commodity,source,value,predictedValue,weightUnit,comment,updatedBy,updatedAt) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  private static final String SQL_UPDATE = "update BoursesStockDayDetail set value=?,predictedValue=?,weightUnit=?,comment=?,updatedBy=?,updatedAt=? where reportDate=? and country=? and bourse=? and wHId=? and wHName=? and annual=? and grade=? and producingArea=? and commodity=? and source=?";
  private static final String SQL_DELETE = "delete from BoursesStockDayDetail where reportDate=? and country=? and bourse=? and wHId=? and wHName=? and annual=? and grade=? and producingArea=? and commodity=? and source=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "bsm.reportDate,bsm.country,bsm.bourse,bsm.wHId,bsm.wHName,bsm.annual,bsm.grade,bsm.producingArea,bsm.commodity,bsm.source,bsm.value,bsm.predictedValue,bsm.weightUnit,bsm.comment,bsm.updatedBy,bsm.updatedAt";
  public static final String SQL_TABLE = "BoursesStockDayDetail bsm";
  public static final String SQL_ALIAS = "bsm";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(BoursesStockDayDetail obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,BoursesStockDayDetail obj) throws SQLException{
   
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
   public static void setInsertValues(PreparedStatement  ps,BoursesStockDayDetail obj)throws SQLException{
      int col =0;
         DBUtil.setLong(ps, ++col,obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
             
            DBUtil.setString(ps, ++col, obj.getBourse().getBourse());
              DBUtil.setString(ps, ++col,obj.getWHId()); DBUtil.setString(ps, ++col,obj.getWHName()); DBUtil.setString(ps, ++col,obj.getAnnual()); DBUtil.setString(ps, ++col,obj.getGrade()); DBUtil.setString(ps, ++col,obj.getProducingArea());
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col,obj.getSource());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(BoursesStockDayDetail obj) throws SQLException{
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
  public static boolean update( Connection con ,BoursesStockDayDetail obj) throws SQLException{
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
    public static void setUpdateValues(PreparedStatement  ps,BoursesStockDayDetail obj)throws SQLException{
    int col= setValues(ps,obj,0);
        DBUtil.setLong(ps, ++col, obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
             
            DBUtil.setString(ps, ++col, obj.getBourse().getBourse());
              DBUtil.setString(ps, ++col, obj.getWHId()); DBUtil.setString(ps, ++col, obj.getWHName()); DBUtil.setString(ps, ++col, obj.getAnnual()); DBUtil.setString(ps, ++col, obj.getGrade()); DBUtil.setString(ps, ++col, obj.getProducingArea());
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col, obj.getSource());      
  }
  
  
  
     
     public static boolean save(BoursesStockDayDetail obj  ) throws SQLException{
       BoursesStockDayDetail   _obj =  getObj( 
        obj.getReportDate()  , 
        obj.getCountry()  , 
        obj.getBourse()  , 
        obj.getWHId()  , 
        obj.getWHName()  , 
        obj.getAnnual()  , 
        obj.getGrade()  , 
        obj.getProducingArea()  , 
        obj.getCommodity()  , 
        obj.getSource()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
      public static boolean save(List<BoursesStockDayDetail >objs  ) throws SQLException{
       Connection con = null;
       PreparedStatement ps_update =null,ps_insert=null;
      try{
      con = getConnection();
      con.setAutoCommit(false);
              ps_update =con.prepareStatement(SQL_UPDATE);
   
       ps_insert =con.prepareStatement(SQL_INSERT);
     
      
      for(BoursesStockDayDetail obj:objs){
      
       BoursesStockDayDetail   _obj =  getObj(con, 
        obj.getReportDate()  , 
        obj.getCountry()  , 
        obj.getBourse()  , 
        obj.getWHId()  , 
        obj.getWHName()  , 
        obj.getAnnual()  , 
        obj.getGrade()  , 
        obj.getProducingArea()  , 
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
country , model.constant.Bourse
bourse , java.lang.String
wHId , java.lang.String
wHName , java.lang.String
annual , java.lang.String
grade , java.lang.String
producingArea , model.constant.Commodity
commodity , java.lang.String
source ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, reportDate , country , bourse , wHId , wHName , annual , grade , producingArea , commodity , source);
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
country , model.constant.Bourse
bourse , java.lang.String
wHId , java.lang.String
wHName , java.lang.String
annual , java.lang.String
grade , java.lang.String
producingArea , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_BoursesStockDayDetailSQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
     setDelete(   ps ,reportDate , country , bourse , wHId , wHName , annual , grade , producingArea , commodity , source
    ) ;
        ps.executeUpdate();    
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
  public static boolean delete(
     List<BoursesStockDayDetail >objs
     ) throws SQLException{
     Connection con = null; PreparedStatement ps = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
       ps = con.prepareStatement(SQL_DELETE);
      
      for(BoursesStockDayDetail obj:objs){
      int col= setDelete(ps, 
           obj.getReportDate()  , 
             obj.getCountry()
              , 
             obj.getBourse()
              ,   obj.getWHId()  ,   obj.getWHName()  ,   obj.getAnnual()  ,   obj.getGrade()  ,   obj.getProducingArea()  , 
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
country , model.constant.Bourse
bourse , java.lang.String
wHId , java.lang.String
wHName , java.lang.String
annual , java.lang.String
grade , java.lang.String
producingArea , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
     int col = 0;
       DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, country.getCountry());
             
            DBUtil.setString(ps, ++col, bourse.getBourse());
              DBUtil.setString(ps, ++col, wHId); DBUtil.setString(ps, ++col, wHName); DBUtil.setString(ps, ++col, annual); DBUtil.setString(ps, ++col, grade); DBUtil.setString(ps, ++col, producingArea);
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);  
    return col;
    }
  
  
  
  
  
  
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static BoursesStockDayDetail getObj(
          long
reportDate , model.constant.Country
country , model.constant.Bourse
bourse , java.lang.String
wHId , java.lang.String
wHName , java.lang.String
annual , java.lang.String
grade , java.lang.String
producingArea , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          reportDate , country , bourse , wHId , wHName , annual , grade , producingArea , commodity , source); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static BoursesStockDayDetail getObj(Connection conn,
          long
reportDate , model.constant.Country
country , model.constant.Bourse
bourse , java.lang.String
wHId , java.lang.String
wHName , java.lang.String
annual , java.lang.String
grade , java.lang.String
producingArea , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
      List<BoursesStockDayDetail>  list = getObjs(conn,
          reportDate , country , bourse , wHId , wHName , annual , grade , producingArea , commodity , source
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (BoursesStockDayDetail)list.get(0);  
      }
   } 
     
   public static List<BoursesStockDayDetail> getObjs( Connection conn,
      long
reportDate , model.constant.Country
country , model.constant.Bourse
bourse , java.lang.String
wHId , java.lang.String
wHName , java.lang.String
annual , java.lang.String
grade , java.lang.String
producingArea , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
    List<BoursesStockDayDetail> list = new ArrayList<BoursesStockDayDetail>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "REPORTDATE = ?  and  ";
          
          sql+=  "COUNTRY = ?  and  ";
          
          sql+=  "BOURSE = ?  and  ";
          
          sql+=  "WHID = ?  and  ";
          
          sql+=  "WHNAME = ?  and  ";
          
          sql+=  "ANNUAL = ?  and  ";
          
          sql+=  "GRADE = ?  and  ";
          
          sql+=  "PRODUCINGAREA = ?  and  ";
          
          sql+=  "COMMODITY = ?  and  ";
          
          sql+=  "SOURCE = ?  ";
           
      LogService.sql(Base_BoursesStockDayDetailSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, country.getCountry());
             
            DBUtil.setString(ps, ++col, bourse.getBourse());
              DBUtil.setString(ps, ++col, wHId); DBUtil.setString(ps, ++col, wHName); DBUtil.setString(ps, ++col, annual); DBUtil.setString(ps, ++col, grade); DBUtil.setString(ps, ++col, producingArea);
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       BoursesStockDayDetail  obj = new BoursesStockDayDetail();
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
  BoursesStockDayDetail obj,
  
	int col) throws SQLException{
        
         long  reportDate=rs.getLong(++col);
          
    model.constant.Country    country = model.constant.Country.getCountry(rs.getString(++col));
             
    model.constant.Bourse    bourse = model.constant.Bourse.getBourse(rs.getString(++col));
             
     
         String wHId= rs.getString(++col) ;
         
     
         String wHName= rs.getString(++col) ;
         
     
         String annual= rs.getString(++col) ;
         
     
         String grade= rs.getString(++col) ;
         
     
         String producingArea= rs.getString(++col) ;
         
    model.constant.Commodity    commodity = model.constant.Commodity.getCommodity(rs.getString(++col));
             
     
         String source= rs.getString(++col) ;
         
    
            double value =  rs.getDouble( ++col);
          
    
            double predictedValue =  rs.getDouble( ++col);
          
    model.constant.WeightUnit    weightUnit = model.constant.WeightUnit.getWeightUnit(rs.getString(++col));
             
     
         String comment= rs.getString(++col) ;
         
     
         String updatedBy= rs.getString(++col) ;
         
    java.util.Date updatedAt =DBUtil.getDate(rs, ++col);
    
				obj.setReportDate	(reportDate);
				
    
				obj.setCountry	(country);
				
    
				obj.setBourse	(bourse);
				
    
				obj.setWHId	(wHId);
				
    
				obj.setWHName	(wHName);
				
    
				obj.setAnnual	(annual);
				
    
				obj.setGrade	(grade);
				
    
				obj.setProducingArea	(producingArea);
				
    
				obj.setValue	(value);
				
    
				obj.setPredictedValue	(predictedValue);
				
    
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

  public static int setValues(PreparedStatement ps, BoursesStockDayDetail obj, int col) throws SQLException{
     
         DBUtil.setDouble(ps, ++col, obj.getValue());
     DBUtil.setDouble(ps, ++col, obj.getPredictedValue());
    
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


