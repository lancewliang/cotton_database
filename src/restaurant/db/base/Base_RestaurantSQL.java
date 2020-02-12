


package restaurant.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import restaurant.obj.Restaurant;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_RestaurantSQL{

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
  private static final String SQL_INSERT = "insert into Restaurant (keyID,source,latitude,longitude,name,adress,reportDate) values (?,?,?,?,?,?,?)";
  private static final String SQL_UPDATE = "update Restaurant set latitude=?,longitude=?,name=?,adress=?,reportDate=? where keyID=? and source=?";
  private static final String SQL_DELETE = "delete from Restaurant where keyID=? and source=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "sd.keyID,sd.source,sd.latitude,sd.longitude,sd.name,sd.adress,sd.reportDate";
  public static final String SQL_TABLE = "Restaurant sd";
  public static final String SQL_ALIAS = "sd";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(Restaurant obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,Restaurant obj) throws SQLException{
   
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
   public static void setInsertValues(PreparedStatement  ps,Restaurant obj)throws SQLException{
      int col =0;
         DBUtil.setString(ps, ++col,obj.getKeyID()); DBUtil.setString(ps, ++col,obj.getSource());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(Restaurant obj) throws SQLException{
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
  public static boolean update( Connection con ,Restaurant obj) throws SQLException{
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
    public static void setUpdateValues(PreparedStatement  ps,Restaurant obj)throws SQLException{
    int col= setValues(ps,obj,0);
        DBUtil.setString(ps, ++col, obj.getKeyID()); DBUtil.setString(ps, ++col, obj.getSource());      
  }
  
  
  
     
     public static boolean save(Restaurant obj  ) throws SQLException{
       Restaurant   _obj =  getObj( 
        obj.getKeyID()  , 
        obj.getSource()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
      public static boolean save(List<Restaurant >objs  ) throws SQLException{
       Connection con = null;
       PreparedStatement ps_update =null,ps_insert=null;
      try{
      con = getConnection();
      con.setAutoCommit(false);
              ps_update =con.prepareStatement(SQL_UPDATE);
   
       ps_insert =con.prepareStatement(SQL_INSERT);
     
      
      for(Restaurant obj:objs){
      
       Restaurant   _obj =  getObj(con, 
        obj.getKeyID()  , 
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
keyID , java.lang.String
source ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, keyID , source);
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
source
    ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_RestaurantSQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
     setDelete(   ps ,keyID , source
    ) ;
        ps.executeUpdate();    
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
  public static boolean delete(
     List<Restaurant >objs
     ) throws SQLException{
     Connection con = null; PreparedStatement ps = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
       ps = con.prepareStatement(SQL_DELETE);
      
      for(Restaurant obj:objs){
      int col= setDelete(ps, 
          obj.getKeyID()  ,   obj.getSource() );
        
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
source
    ) throws SQLException{
     int col = 0;
       DBUtil.setString(ps, ++col, keyID); DBUtil.setString(ps, ++col, source);  
    return col;
    }
  
  
  
  
  
  
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static Restaurant getObj(
          java.lang.String
keyID , java.lang.String
source
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          keyID , source); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static Restaurant getObj(Connection conn,
          java.lang.String
keyID , java.lang.String
source
   ) throws SQLException{
      List<Restaurant>  list = getObjs(conn,
          keyID , source
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (Restaurant)list.get(0);  
      }
   } 
     
   public static List<Restaurant> getObjs( Connection conn,
      java.lang.String
keyID , java.lang.String
source
   ) throws SQLException{
    List<Restaurant> list = new ArrayList<Restaurant>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "KEYID = ?  and  ";
          
          sql+=  "SOURCE = ?  ";
           
      LogService.sql(Base_RestaurantSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setString(ps, ++col, keyID); DBUtil.setString(ps, ++col, source);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       Restaurant  obj = new Restaurant();
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
  Restaurant obj,
  
	int col) throws SQLException{
         
         String keyID= rs.getString(++col) ;
         
     
         String source= rs.getString(++col) ;
         
    
            double latitude =  rs.getDouble( ++col);
          
    
            double longitude =  rs.getDouble( ++col);
          
     
         String name= rs.getString(++col) ;
         
     
         String adress= rs.getString(++col) ;
         
    
         long  reportDate=rs.getLong(++col);
          
    
				obj.setKeyID	(keyID);
				
    
				obj.setSource	(source);
				
    
				obj.setLatitude	(latitude);
				
    
				obj.setLongitude	(longitude);
				
    
				obj.setName	(name);
				
    
				obj.setAdress	(adress);
				
    
				obj.setReportDate	(reportDate);
				

    
   
     return col;
  }  
  
  /**
   * code auto write. ready only file. don't change any code. 
   */  

  public static int setValues(PreparedStatement ps, Restaurant obj, int col) throws SQLException{
     
         DBUtil.setDouble(ps, ++col, obj.getLatitude());
     DBUtil.setDouble(ps, ++col, obj.getLongitude());
     DBUtil.setString(ps, ++col,obj.getName());
     DBUtil.setString(ps, ++col,obj.getAdress());
     DBUtil.setLong(ps, ++col, obj.getReportDate());

     
     return col;
  }  
  
  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }
}


