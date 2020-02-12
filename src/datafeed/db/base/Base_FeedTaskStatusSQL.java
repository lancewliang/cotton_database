


package datafeed.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import datafeed.excell.FeedTaskStatus;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_FeedTaskStatusSQL{

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
  private static final String SQL_INSERT = "insert into FeedTaskStatus (type,filename,sheetname,status) values (?,?,?,?)";
  private static final String SQL_UPDATE = "update FeedTaskStatus set status=? where type=? and filename=? and sheetname=?";
  private static final String SQL_DELETE = "delete from FeedTaskStatus where type=? and filename=? and sheetname=?";
    
       private static final String SQL_DELETES = "";
    
  public static final String SQL_COLUMS = "bsm.type,bsm.filename,bsm.sheetname,bsm.status";
  public static final String SQL_TABLE = "FeedTaskStatus bsm";
  public static final String SQL_ALIAS = "bsm";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(FeedTaskStatus obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,FeedTaskStatus obj) throws SQLException{
   
     PreparedStatement  ps = null;
    try{     
       ps = con.prepareStatement(SQL_INSERT);
       int col =0;
         DBUtil.setString(ps, ++col,obj.getType()); DBUtil.setString(ps, ++col,obj.getFilename()); DBUtil.setString(ps, ++col,obj.getSheetname());      
     col = setValues(ps,obj,col); 
      ps.executeUpdate();    
    }finally{     
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
 
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(FeedTaskStatus obj) throws SQLException{
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
  public static boolean update( Connection con ,FeedTaskStatus obj) throws SQLException{
    PreparedStatement ps = null;
    try{ 
      ps= con.prepareStatement(SQL_UPDATE);
      
      int col= setValues(ps,obj,0);
        DBUtil.setString(ps, ++col, obj.getType()); DBUtil.setString(ps, ++col, obj.getFilename()); DBUtil.setString(ps, ++col, obj.getSheetname());      
      ps.executeUpdate();
     
    }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  
  
     
     public static boolean save(FeedTaskStatus obj  ) throws SQLException{
       FeedTaskStatus   _obj =  getObj( 
        obj.getType()  , 
        obj.getFilename()  , 
        obj.getSheetname()  );
       if(_obj==null){
          return  insert(obj  );
       }else{
         return update(obj);
       }
    }
     
    /**
   * code auto write. ready only file. don't change any code.
   */      
    
  
   public static boolean delete(
     java.lang.String
type , java.lang.String
filename , java.lang.String
sheetname ) throws SQLException{
     Connection con = null;
     boolean ret = false;
     try{  
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, type , filename , sheetname);
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
  
    public static boolean delete(Connection con,
     java.lang.String
type , java.lang.String
filename , java.lang.String
sheetname ) throws SQLException{
    PreparedStatement ps = null;
    try{
      LogService.msg(Base_FeedTaskStatusSQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
      int col = 0;
       DBUtil.setString(ps, ++col, type); DBUtil.setString(ps, ++col, filename); DBUtil.setString(ps, ++col, sheetname);      
     }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
  

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
    public static FeedTaskStatus getObj(
          java.lang.String
type , java.lang.String
filename , java.lang.String
sheetname
   ) throws SQLException{
      List<FeedTaskStatus>  list = getObjs(
          type , filename , sheetname
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (FeedTaskStatus)list.get(0);  
      }
   } 
     
   public static List<FeedTaskStatus> getObjs(
      java.lang.String
type , java.lang.String
filename , java.lang.String
sheetname
   ) throws SQLException{
    List<FeedTaskStatus> list = new ArrayList<FeedTaskStatus>();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
      conn = getConnection();
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "TYPE = ?  and  ";
          
          sql+=  "FILENAME = ?  and  ";
          
          sql+=  "SHEETNAME = ?  ";
           
      LogService.msg(Base_FeedTaskStatusSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setString(ps, ++col, type); DBUtil.setString(ps, ++col, filename); DBUtil.setString(ps, ++col, sheetname);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       FeedTaskStatus  obj = new FeedTaskStatus();
        getValues( rs,obj, 0);
       
    
        list.add(obj);
      }
      return list;
    }finally{
      DBUtil.cleanup(rs, ps, conn);
    }
  }

  /**
   * code auto write. ready only file. don't change any code. 
   */  
  public static int getValues(ResultSet rs, 
  FeedTaskStatus obj,
  
	int col) throws SQLException{
         
         String type= rs.getString(++col) ;
         
     
         String filename= rs.getString(++col) ;
         
     
         String sheetname= rs.getString(++col) ;
         
     
         String status= rs.getString(++col) ;
         
    
				obj.setType	(type);
				
    
				obj.setFilename	(filename);
				
    
				obj.setSheetname	(sheetname);
				
    
				obj.setStatus	(status);
				

    
   
     return col;
  }  
  
  /**
   * code auto write. ready only file. don't change any code. 
   */  

  public static int setValues(PreparedStatement ps, FeedTaskStatus obj, int col) throws SQLException{
     
         DBUtil.setString(ps, ++col,obj.getStatus());

     
     return col;
  }  
  
  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }
}


