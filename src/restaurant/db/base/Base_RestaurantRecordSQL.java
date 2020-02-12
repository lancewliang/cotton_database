


package restaurant.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import restaurant.obj.RestaurantRecord;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_RestaurantRecordSQL{

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
  private static final String SQL_INSERT = "insert into RestaurantRecord (keyID,source,reportDate,recent_order_num,month_sales,minimum_order_amount,rating_count,delivery_fee) values (?,?,?,?,?,?,?,?)";
  private static final String SQL_UPDATE = "update RestaurantRecord set recent_order_num=?,month_sales=?,minimum_order_amount=?,rating_count=?,delivery_fee=? where keyID=? and source=? and reportDate=?";
  private static final String SQL_DELETE = "delete from RestaurantRecord where keyID=? and source=? and reportDate=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "sdr.keyID,sdr.source,sdr.reportDate,sdr.recent_order_num,sdr.month_sales,sdr.minimum_order_amount,sdr.rating_count,sdr.delivery_fee";
  public static final String SQL_TABLE = "RestaurantRecord sdr";
  public static final String SQL_ALIAS = "sdr";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(RestaurantRecord obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,RestaurantRecord obj) throws SQLException{
   
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
   public static void setInsertValues(PreparedStatement  ps,RestaurantRecord obj)throws SQLException{
      int col =0;
         DBUtil.setString(ps, ++col,obj.getKeyID()); DBUtil.setString(ps, ++col,obj.getSource()); DBUtil.setLong(ps, ++col,obj.getReportDate());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(RestaurantRecord obj) throws SQLException{
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
  public static boolean update( Connection con ,RestaurantRecord obj) throws SQLException{
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
    public static void setUpdateValues(PreparedStatement  ps,RestaurantRecord obj)throws SQLException{
    int col= setValues(ps,obj,0);
        DBUtil.setString(ps, ++col, obj.getKeyID()); DBUtil.setString(ps, ++col, obj.getSource()); DBUtil.setLong(ps, ++col, obj.getReportDate());      
  }
  
  
  
     
     public static boolean save(RestaurantRecord obj  ) throws SQLException{
       RestaurantRecord   _obj =  getObj( 
        obj.getKeyID()  , 
        obj.getSource()  , 
        obj.getReportDate()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
      public static boolean save(List<RestaurantRecord >objs  ) throws SQLException{
       Connection con = null;
       PreparedStatement ps_update =null,ps_insert=null;
      try{
      con = getConnection();
      con.setAutoCommit(false);
              ps_update =con.prepareStatement(SQL_UPDATE);
   
       ps_insert =con.prepareStatement(SQL_INSERT);
     
      
      for(RestaurantRecord obj:objs){
      
       RestaurantRecord   _obj =  getObj(con, 
        obj.getKeyID()  , 
        obj.getSource()  , 
        obj.getReportDate()  );
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
keyID , java.lang.String
source , long
reportDate ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, keyID , source , reportDate);
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
keyID , java.lang.String
source , long
reportDate
    ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_RestaurantRecordSQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
     setDelete(   ps ,keyID , source , reportDate
    ) ;
        ps.executeUpdate();    
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
  public static boolean delete(
     List<RestaurantRecord >objs
     ) throws SQLException{
     Connection con = null; PreparedStatement ps = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
       ps = con.prepareStatement(SQL_DELETE);
      
      for(RestaurantRecord obj:objs){
      int col= setDelete(ps, 
          obj.getKeyID()  ,   obj.getSource()  ,    obj.getReportDate() );
        
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
keyID , java.lang.String
source , long
reportDate
    ) throws SQLException{
     int col = 0;
       DBUtil.setString(ps, ++col, keyID); DBUtil.setString(ps, ++col, source); DBUtil.setLong(ps, ++col, reportDate);  
    return col;
    }
  
  
  
  
  
  
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static RestaurantRecord getObj(
          java.lang.String
keyID , java.lang.String
source , long
reportDate
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          keyID , source , reportDate); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static RestaurantRecord getObj(Connection conn,
          java.lang.String
keyID , java.lang.String
source , long
reportDate
   ) throws SQLException{
      List<RestaurantRecord>  list = getObjs(conn,
          keyID , source , reportDate
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (RestaurantRecord)list.get(0);  
      }
   } 
     
   public static List<RestaurantRecord> getObjs( Connection conn,
      java.lang.String
keyID , java.lang.String
source , long
reportDate
   ) throws SQLException{
    List<RestaurantRecord> list = new ArrayList<RestaurantRecord>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "KEYID = ?  and  ";
          
          sql+=  "SOURCE = ?  and  ";
          
          sql+=  "REPORTDATE = ?  ";
           
      LogService.sql(Base_RestaurantRecordSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setString(ps, ++col, keyID); DBUtil.setString(ps, ++col, source); DBUtil.setLong(ps, ++col, reportDate);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       RestaurantRecord  obj = new RestaurantRecord();
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
  RestaurantRecord obj,
  
	int col) throws SQLException{
         
         String keyID= rs.getString(++col) ;
         
     
         String source= rs.getString(++col) ;
         
    
         long  reportDate=rs.getLong(++col);
          
    
         int recent_order_num=  rs.getInt(++col);
         
    
         int month_sales=  rs.getInt(++col);
         
    
         int minimum_order_amount=  rs.getInt(++col);
         
    
         int rating_count=  rs.getInt(++col);
         
    
         int delivery_fee=  rs.getInt(++col);
         
    
				obj.setKeyID	(keyID);
				
    
				obj.setSource	(source);
				
    
				obj.setReportDate	(reportDate);
				
    
				obj.setRecent_order_num	(recent_order_num);
				
    
				obj.setMonth_sales	(month_sales);
				
    
				obj.setMinimum_order_amount	(minimum_order_amount);
				
    
				obj.setRating_count	(rating_count);
				
    
				obj.setDelivery_fee	(delivery_fee);
				

    
   
     return col;
  }  
  
  /**
   * code auto write. ready only file. don't change any code. 
   */  

  public static int setValues(PreparedStatement ps, RestaurantRecord obj, int col) throws SQLException{
     
         DBUtil.setInt(ps, ++col,obj.getRecent_order_num());
     DBUtil.setInt(ps, ++col,obj.getMonth_sales());
     DBUtil.setInt(ps, ++col,obj.getMinimum_order_amount());
     DBUtil.setInt(ps, ++col,obj.getRating_count());
     DBUtil.setInt(ps, ++col,obj.getDelivery_fee());

     
     return col;
  }  
  
  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }
}


