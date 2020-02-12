


package model.entity.wasde.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import model.entity.wasde.obj.WorldSupplyDemandMonthlyHistory;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_WorldSupplyDemandMonthlyHistorySQL{

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
  private static final String SQL_INSERT = "insert into WorldSupplyDemandMonthlyHistory (country,year,reportDate,reportStatus,commodity,source,beginStock,production,imports,uses,exports,loss,endStock,weightUnit,comment,updatedBy,updatedAt) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
  private static final String SQL_UPDATE = "update WorldSupplyDemandMonthlyHistory set beginStock=?,production=?,imports=?,uses=?,exports=?,loss=?,endStock=?,weightUnit=?,comment=?,updatedBy=?,updatedAt=? where country=? and year=? and reportDate=? and reportStatus=? and commodity=? and source=?";
  private static final String SQL_DELETE = "delete from WorldSupplyDemandMonthlyHistory where country=? and year=? and reportDate=? and reportStatus=? and commodity=? and source=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "wsd.country,wsd.year,wsd.reportDate,wsd.reportStatus,wsd.commodity,wsd.source,wsd.beginStock,wsd.production,wsd.imports,wsd.uses,wsd.exports,wsd.loss,wsd.endStock,wsd.weightUnit,wsd.comment,wsd.updatedBy,wsd.updatedAt";
  public static final String SQL_TABLE = "WorldSupplyDemandMonthlyHistory wsd";
  public static final String SQL_ALIAS = "wsd";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(WorldSupplyDemandMonthlyHistory obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,WorldSupplyDemandMonthlyHistory obj) throws SQLException{
   
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
   public static void setInsertValues(PreparedStatement  ps,WorldSupplyDemandMonthlyHistory obj)throws SQLException{
      int col =0;
        
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
              DBUtil.setString(ps, ++col,obj.getYear()); DBUtil.setLong(ps, ++col,obj.getReportDate()); DBUtil.setInt(ps, ++col, obj.getReportStatus());
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col,obj.getSource());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(WorldSupplyDemandMonthlyHistory obj) throws SQLException{
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
  public static boolean update( Connection con ,WorldSupplyDemandMonthlyHistory obj) throws SQLException{
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
    public static void setUpdateValues(PreparedStatement  ps,WorldSupplyDemandMonthlyHistory obj)throws SQLException{
    int col= setValues(ps,obj,0);
       
            DBUtil.setString(ps, ++col, obj.getCountry().getCountry());
              DBUtil.setString(ps, ++col, obj.getYear()); DBUtil.setLong(ps, ++col, obj.getReportDate()); DBUtil.setInt(ps, ++col, obj.getReportStatus());
            DBUtil.setString(ps, ++col, obj.getCommodity().getCommodity());
              DBUtil.setString(ps, ++col, obj.getSource());      
  }
  
  
  
     
     public static boolean save(WorldSupplyDemandMonthlyHistory obj  ) throws SQLException{
       WorldSupplyDemandMonthlyHistory   _obj =  getObj( 
        obj.getCountry()  , 
        obj.getYear()  , 
        obj.getReportDate()  , 
        obj.getReportStatus()  , 
        obj.getCommodity()  , 
        obj.getSource()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
      public static boolean save(List<WorldSupplyDemandMonthlyHistory >objs  ) throws SQLException{
       Connection con = null;
       PreparedStatement ps_update =null,ps_insert=null;
      try{
      con = getConnection();
      con.setAutoCommit(false);
              ps_update =con.prepareStatement(SQL_UPDATE);
   
       ps_insert =con.prepareStatement(SQL_INSERT);
     
      
      for(WorldSupplyDemandMonthlyHistory obj:objs){
      
       WorldSupplyDemandMonthlyHistory   _obj =  getObj(con, 
        obj.getCountry()  , 
        obj.getYear()  , 
        obj.getReportDate()  , 
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
     model.constant.Country
country , java.lang.String
year , long
reportDate , int
reportStatus , model.constant.Commodity
commodity , java.lang.String
source ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, country , year , reportDate , reportStatus , commodity , source);
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
  
    public static boolean delete(Connection con,model.constant.Country
country , java.lang.String
year , long
reportDate , int
reportStatus , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_WorldSupplyDemandMonthlyHistorySQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
     setDelete(   ps ,country , year , reportDate , reportStatus , commodity , source
    ) ;
        ps.executeUpdate();    
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
  public static boolean delete(
     List<WorldSupplyDemandMonthlyHistory >objs
     ) throws SQLException{
     Connection con = null; PreparedStatement ps = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
       ps = con.prepareStatement(SQL_DELETE);
      
      for(WorldSupplyDemandMonthlyHistory obj:objs){
      int col= setDelete(ps, 
        
             obj.getCountry()
              ,   obj.getYear()  ,    obj.getReportDate()  ,    obj.getReportStatus()   , 
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
  
  
  
   public static int setDelete( PreparedStatement ps ,model.constant.Country
country , java.lang.String
year , long
reportDate , int
reportStatus , model.constant.Commodity
commodity , java.lang.String
source
    ) throws SQLException{
     int col = 0;
      
            DBUtil.setString(ps, ++col, country.getCountry());
              DBUtil.setString(ps, ++col, year); DBUtil.setLong(ps, ++col, reportDate); DBUtil.setInt(ps, ++col, reportStatus);
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);  
    return col;
    }
  
  
  
  
  
  
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static WorldSupplyDemandMonthlyHistory getObj(
          model.constant.Country
country , java.lang.String
year , long
reportDate , int
reportStatus , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          country , year , reportDate , reportStatus , commodity , source); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static WorldSupplyDemandMonthlyHistory getObj(Connection conn,
          model.constant.Country
country , java.lang.String
year , long
reportDate , int
reportStatus , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
      List<WorldSupplyDemandMonthlyHistory>  list = getObjs(conn,
          country , year , reportDate , reportStatus , commodity , source
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (WorldSupplyDemandMonthlyHistory)list.get(0);  
      }
   } 
     
   public static List<WorldSupplyDemandMonthlyHistory> getObjs( Connection conn,
      model.constant.Country
country , java.lang.String
year , long
reportDate , int
reportStatus , model.constant.Commodity
commodity , java.lang.String
source
   ) throws SQLException{
    List<WorldSupplyDemandMonthlyHistory> list = new ArrayList<WorldSupplyDemandMonthlyHistory>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "COUNTRY = ?  and  ";
          
          sql+=  "YEAR = ?  and  ";
          
          sql+=  "REPORTDATE = ?  and  ";
          
          sql+=  "REPORTSTATUS = ?  and  ";
          
          sql+=  "COMMODITY = ?  and  ";
          
          sql+=  "SOURCE = ?  ";
           
      LogService.sql(Base_WorldSupplyDemandMonthlyHistorySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
        
            DBUtil.setString(ps, ++col, country.getCountry());
              DBUtil.setString(ps, ++col, year); DBUtil.setLong(ps, ++col, reportDate); DBUtil.setInt(ps, ++col, reportStatus);
            DBUtil.setString(ps, ++col, commodity.getCommodity());
              DBUtil.setString(ps, ++col, source);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       WorldSupplyDemandMonthlyHistory  obj = new WorldSupplyDemandMonthlyHistory();
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
  WorldSupplyDemandMonthlyHistory obj,
  
	int col) throws SQLException{
        model.constant.Country    country = model.constant.Country.getCountry(rs.getString(++col));
             
     
         String year= rs.getString(++col) ;
         
    
         long  reportDate=rs.getLong(++col);
          
    
         int reportStatus=  rs.getInt(++col);
         
    model.constant.Commodity    commodity = model.constant.Commodity.getCommodity(rs.getString(++col));
             
     
         String source= rs.getString(++col) ;
         
    
            double beginStock =  rs.getDouble( ++col);
          
    
            double production =  rs.getDouble( ++col);
          
    
            double imports =  rs.getDouble( ++col);
          
    
            double uses =  rs.getDouble( ++col);
          
    
            double exports =  rs.getDouble( ++col);
          
    
            double loss =  rs.getDouble( ++col);
          
    
            double endStock =  rs.getDouble( ++col);
          
    model.constant.WeightUnit    weightUnit = model.constant.WeightUnit.getWeightUnit(rs.getString(++col));
             
     
         String comment= rs.getString(++col) ;
         
     
         String updatedBy= rs.getString(++col) ;
         
    java.util.Date updatedAt =DBUtil.getDate(rs, ++col);
    
				obj.setCountry	(country);
				
    
				obj.setYear	(year);
				
    
				obj.setReportDate	(reportDate);
				
    
				obj.setReportStatus	(reportStatus);
				
    
				obj.setBeginStock	(beginStock);
				
    
				obj.setProduction	(production);
				
    
				obj.setImports	(imports);
				
    
				obj.setUses	(uses);
				
    
				obj.setExports	(exports);
				
    
				obj.setLoss	(loss);
				
    
				obj.setEndStock	(endStock);
				
    
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

  public static int setValues(PreparedStatement ps, WorldSupplyDemandMonthlyHistory obj, int col) throws SQLException{
     
         DBUtil.setDouble(ps, ++col, obj.getBeginStock());
     DBUtil.setDouble(ps, ++col, obj.getProduction());
     DBUtil.setDouble(ps, ++col, obj.getImports());
     DBUtil.setDouble(ps, ++col, obj.getUses());
     DBUtil.setDouble(ps, ++col, obj.getExports());
     DBUtil.setDouble(ps, ++col, obj.getLoss());
     DBUtil.setDouble(ps, ++col, obj.getEndStock());
    
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


