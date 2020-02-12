


package ant.server.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import ant.server.AntInstruction;

/**
  * code auto write. ready only file. don't change any code.
  */
public class Base_AntInstructionSQL{

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
  private static final String SQL_INSERT = "insert into AntInstruction (name,status,log,updateAt) values (?,?,?,?)";
  private static final String SQL_UPDATE = "update AntInstruction set status=?,log=?,updateAt=? where name=?";
  private static final String SQL_DELETE = "delete from AntInstruction where name=?";
    
  public static final String SQL_COLUMS = "bsm.name,bsm.status,bsm.log,bsm.updateAt";
  public static final String SQL_TABLE = "AntInstruction bsm";
  public static final String SQL_ALIAS = "bsm";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
    * code auto write. ready only file. don't change any code. 
    */
 public  static boolean insert(AntInstruction obj) throws SQLException{
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
    
 public  static boolean insert(Connection con,AntInstruction obj) throws SQLException{
   
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
   public static void setInsertValues(PreparedStatement  ps,AntInstruction obj)throws SQLException{
      int col =0;
         DBUtil.setString(ps, ++col,obj.getName());      
     col = setValues(ps,obj,col); 
   
   }
  /**
   * code auto write. ready only file. don't change any code. 
   */  
   public static boolean update(AntInstruction obj) throws SQLException{
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
  public static boolean update( Connection con ,AntInstruction obj) throws SQLException{
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
    public static void setUpdateValues(PreparedStatement  ps,AntInstruction obj)throws SQLException{
    int col= setValues(ps,obj,0);
        DBUtil.setString(ps, ++col, obj.getName());      
  }
  
  
  /**
   * code auto write. ready only file. don't change any code. 
  */ 
   public static boolean delete(
   String   
   [] ids) throws SQLException{
    Connection con = null;
    boolean ret = false;
    try{  
      con = getConnection();
      con.setAutoCommit(false);
     
      
       ret = delete(con, ids);
        con.commit();
    } catch (Exception e) {
      try {
        con.rollback();
      } catch (Exception ex) {
      }
      throw new SQLException(e);
    } finally {
      try {
        con.setAutoCommit(true);
      } catch (Exception e) {
      }
      DBUtil.cleanup(null, con);
    }
    return ret;
    
   }
   
   
  public static boolean delete(Connection con ,   String[] ids) throws SQLException{
    if(None.isEmpty(ids)){
      return true;
    }     
    PreparedStatement ps = null;
    try{
      LogService.sql(Base_AntInstructionSQL.class, "SQL", SQL_DELETE); 
      ps = con.prepareStatement(SQL_DELETE);
      for(int i=0;i<ids.length;i++){
        int col = 0;
          DBUtil.setString(ps, ++col, ids[i]);               
        ps.addBatch();
      }
      ps.executeBatch();
     
    }finally{
      DBUtil.cleanup(ps, null);
    }
    return true;
  }
   

    /**
   * code auto write. ready only file. don't change any code. 
 
   */    
     public static AntInstruction getObj(
          java.lang.String
name
   ) throws SQLException{
   Connection conn=null;
   try{
    conn = getConnection();
  return getObj(conn,
          name); 
   }finally{
   DBUtil.cleanup(null, null, conn);
   }
   }
   
   
   
    public static AntInstruction getObj(Connection conn,
          java.lang.String
name
   ) throws SQLException{
      List<AntInstruction>  list = getObjs(conn,
          name
      );  
      if(list.size()==0){
      return null;
      }else{
      
      return (AntInstruction)list.get(0);  
      }
   } 
     
   public static List<AntInstruction> getObjs( Connection conn,
      java.lang.String
name
   ) throws SQLException{
    List<AntInstruction> list = new ArrayList<AntInstruction>();
    
    PreparedStatement ps = null;
    ResultSet rs = null;
    try{
 
      
      String sql = SQL_QUERY + " WHERE  ";
      
          sql+=  "NAME = ?  ";
           
      LogService.sql(Base_AntInstructionSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
         DBUtil.setString(ps, ++col, name);
      ps.executeQuery();
      rs = ps.getResultSet();
      
      while(rs.next()){
       AntInstruction  obj = new AntInstruction();
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
  AntInstruction obj,
  
	int col) throws SQLException{
         
         String name= rs.getString(++col) ;
         
     
         String status= rs.getString(++col) ;
         
     
         String log= rs.getString(++col) ;
         
    java.util.Date updateAt =DBUtil.getDate(rs, ++col);
    
				obj.setName	(name);
				
    
				obj.setStatus	(status);
				
    
				obj.setLog	(log);
				
    
				obj.setUpdateAt	(updateAt);
				

    
   
     return col;
  }  
  
  /**
   * code auto write. ready only file. don't change any code. 
   */  

  public static int setValues(PreparedStatement ps, AntInstruction obj, int col) throws SQLException{
     
         DBUtil.setString(ps, ++col,obj.getStatus());
     DBUtil.setString(ps, ++col,obj.getLog());
    DBUtil.setTimestamp(ps, ++col, obj.getUpdateAt());

     
     return col;
  }  
  
  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }
}


