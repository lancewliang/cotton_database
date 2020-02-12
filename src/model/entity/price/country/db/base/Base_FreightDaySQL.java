


package model.entity.price.country.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import model.entity.price.country.FreightDay;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_FreightDaySQL{

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
  private static final String SQL_INSERT = "insert into FreightDay (reportDate,from,to,source,value1,value2,priceUnit,comment,updatedBy,updatedAt) values (?,?,?,?,?,?,?,?,?,?)";
  private static final String SQL_UPDATE = "update FreightDay set value1=?,value2=?,priceUnit=?,comment=?,updatedBy=?,updatedAt=? where reportDate=? and from=? and to=? and source=?";
  private static final String SQL_DELETE = "delete from FreightDay where reportDate=? and from=? and to=? and source=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "fd.reportDate,fd.from,fd.to,fd.source,fd.value1,fd.value2,fd.priceUnit,fd.comment,fd.updatedBy,fd.updatedAt";
  public static final String SQL_TABLE = "FreightDay fd";
  public static final String SQL_ALIAS = "fd";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(FreightDay obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,FreightDay obj) throws SQLException{
   
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
   public static void setInsertValues(PreparedStatement  ps,FreightDay obj)throws SQLException{
      int col =0;
         DBUtil.setLong(ps, ++col,obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getFrom().getCountry());
             
            DBUtil.setString(ps, ++col, obj.getTo().getCountry());
              DBUtil.setString(ps, ++col,obj.getSource());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(FreightDay obj) throws SQLException{
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
  public static boolean update( Connection con ,FreightDay obj) throws SQLException{
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
    public static void setUpdateValues(PreparedStatement  ps,FreightDay obj)throws SQLException{
    int col= setValues(ps,obj,0);
        DBUtil.setLong(ps, ++col, obj.getReportDate());
            DBUtil.setString(ps, ++col, obj.getFrom().getCountry());
             
            DBUtil.setString(ps, ++col, obj.getTo().getCountry());
              DBUtil.setString(ps, ++col, obj.getSource());      
  }
  
  
  
     
     public static boolean save(FreightDay obj  ) throws SQLException{
       FreightDay   _obj =  getObj( 
        obj.getReportDate()  , 
        obj.getFrom()  , 
        obj.getTo()  , 
        obj.getSource()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
      public static boolean save(List<FreightDay >objs  ) throws SQLException{
       Connection con = null;
       PreparedStatement ps_update =null,ps_insert=null;
      try{
      con = getConnection();
      con.setAutoCommit(false);
              ps_update =con.prepareStatement(SQL_UPDATE);
   
       ps_insert =con.prepareStatement(SQL_INSERT);
     
      
      for(FreightDay obj:objs){
      
       FreightDay   _obj =  getObj(con, 
        obj.getReportDate()  , 
        obj.getFrom()  , 
        obj.getTo()  , 
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
from , model.constant.Country
to , java.lang.String
source ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, reportDate , from , to , source);
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
from , model.constant.Country
to , java.lang.String
source
    ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_FreightDaySQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
     setDelete(   ps ,reportDate , from , to , source
    ) ;
        ps.executeUpdate();    
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
  public static boolean delete(
     List<FreightDay >objs
     ) throws SQLException{
     Connection con = null; PreparedStatement ps = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
       ps = con.prepareStatement(SQL_DELETE);
      
      for(FreightDay obj:objs){
      int col= setDelete(ps, 
           obj.getReportDate()  , 
             obj.getFrom()
              , 
             obj.getTo()
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
from , model.constant.Country
to , java.lang.String
source
    ) throws SQLException{
     int col = 0;
       DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, from.getCountry());
             
            DBUtil.setString(ps, ++col, to.getCountry());
              DBUtil.setString(ps, ++col, source);  
    return col;
    }
  
  
  
  
  
  
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static FreightDay getObj(
          long
reportDate , model.constant.Country
from , model.constant.Country
to , java.lang.String
source
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          reportDate , from , to , source); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static FreightDay getObj(Connection conn,
          long
reportDate , model.constant.Country
from , model.constant.Country
to , java.lang.String
source
   ) throws SQLException{
      List<FreightDay>  list = getObjs(conn,
          reportDate , from , to , source
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (FreightDay)list.get(0);  
      }
   } 
     
   public static List<FreightDay> getObjs( Connection conn,
      long
reportDate , model.constant.Country
from , model.constant.Country
to , java.lang.String
source
   ) throws SQLException{
    List<FreightDay> list = new ArrayList<FreightDay>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "REPORTDATE = ?  and  ";
          
          sql+=  "FROM = ?  and  ";
          
          sql+=  "TO = ?  and  ";
          
          sql+=  "SOURCE = ?  ";
           
      LogService.sql(Base_FreightDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setLong(ps, ++col, reportDate);
            DBUtil.setString(ps, ++col, from.getCountry());
             
            DBUtil.setString(ps, ++col, to.getCountry());
              DBUtil.setString(ps, ++col, source);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       FreightDay  obj = new FreightDay();
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
  FreightDay obj,
  
	int col) throws SQLException{
        
         long  reportDate=rs.getLong(++col);
          
    model.constant.Country    from = model.constant.Country.getCountry(rs.getString(++col));
             
    model.constant.Country    to = model.constant.Country.getCountry(rs.getString(++col));
             
     
         String source= rs.getString(++col) ;
         
    
            double value1 =  rs.getDouble( ++col);
          
    
            double value2 =  rs.getDouble( ++col);
          
    model.constant.PriceUnit    priceUnit = model.constant.PriceUnit.getPriceUnit(rs.getString(++col));
             
     
         String comment= rs.getString(++col) ;
         
     
         String updatedBy= rs.getString(++col) ;
         
    java.util.Date updatedAt =DBUtil.getDate(rs, ++col);
    
				obj.setReportDate	(reportDate);
				
    
				obj.setFrom	(from);
				
    
				obj.setTo	(to);
				
    
				obj.setValue1	(value1);
				
    
				obj.setValue2	(value2);
				
    
				obj.setPriceUnit	(priceUnit);
				
    
				obj.setSource	(source);
				
    
				obj.setComment	(comment);
				
    
				obj.setUpdatedBy	(updatedBy);
				
    
				obj.setUpdatedAt	(updatedAt);
				

    
   
     return col;
  }  
  
  /**
   * code auto write. ready only file. don't change any code. 
   */  

  public static int setValues(PreparedStatement ps, FreightDay obj, int col) throws SQLException{
     
         DBUtil.setDouble(ps, ++col, obj.getValue1());
     DBUtil.setDouble(ps, ++col, obj.getValue2());
    
            DBUtil.setString(ps, ++col, obj.getPriceUnit().getPriceUnit());
             
     DBUtil.setString(ps, ++col,obj.getComment());
     DBUtil.setString(ps, ++col,obj.getUpdatedBy());
    DBUtil.setTimestamp(ps, ++col, obj.getUpdatedAt());

     
     return col;
  }  
  
  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }
}


