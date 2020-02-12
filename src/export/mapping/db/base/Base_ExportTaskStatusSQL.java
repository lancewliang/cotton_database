package export.mapping.db.base;

import java.sql.*;
import tutami.fw.*;
import tutami.fw.DB;
import tcc.utils.db.*;
import tcc.utils.log.LogService;
import tcc.utils.None;
import java.util.*;
import tcc.utils.obj.OperatorInfo;

import export.mapping.excell.ExportTaskStatus;

/**
 * code auto write. ready only file. don't change any code.
 */
public class Base_ExportTaskStatusSQL {

  /**
   * code auto write. ready only file. don't change any code.
   */
  protected static javax.sql.DataSource TTDB;
  static {
    TTDB = DB.getDBPoolADMIN();
  }

  /**
   * code auto write. ready only file. don't change any code.
   */
  private static final String SQL_INSERT = "insert into ExportTaskStatus (type,commodity,format,date,status) values (?,?,?,?,?)";
  private static final String SQL_UPDATE = "update ExportTaskStatus set status=? where type=? and commodity=? and format=? and date=?";
  private static final String SQL_DELETE = "delete from ExportTaskStatus where type=? and commodity=? and format=? and date=?";

  private static final String SQL_DELETES = "";

  public static final String SQL_COLUMS = "bsm.type,bsm.commodity,bsm.format,bsm.date,bsm.status";
  public static final String SQL_TABLE = "ExportTaskStatus bsm";
  public static final String SQL_ALIAS = "bsm";
  public static final String SQL_QUERY = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE;

  /**
   * code auto write. ready only file. don't change any code.
   */
  public static boolean insert(ExportTaskStatus obj) throws SQLException {
    Connection con = null;
    boolean ret = false;
    try {
      con = getConnection();
      con.setAutoCommit(false);
      ret = insert(con, obj);
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

  public static boolean insert(Connection con, ExportTaskStatus obj) throws SQLException {

    PreparedStatement ps = null;
    try {
      ps = con.prepareStatement(SQL_INSERT);
      int col = 0;
      DBUtil.setString(ps, ++col, obj.getType());
      DBUtil.setString(ps, ++col, obj.getCommodity());
      DBUtil.setString(ps, ++col, obj.getFormat());
      DBUtil.setString(ps, ++col, obj.getDate());
      col = setValues(ps, obj, col);
      ps.executeUpdate();
    } finally {
      DBUtil.cleanup(ps, null);
    }
    return true;
  }

  /**
   * code auto write. ready only file. don't change any code.
   */
  public static boolean update(ExportTaskStatus obj) throws SQLException {
    Connection con = null;
    boolean ret = false;
    try {
      con = getConnection();
      con.setAutoCommit(false);
      ret = update(con, obj);
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

  public static boolean update(Connection con, ExportTaskStatus obj) throws SQLException {
    PreparedStatement ps = null;
    try {
      ps = con.prepareStatement(SQL_UPDATE);

      int col = setValues(ps, obj, 0);
      DBUtil.setString(ps, ++col, obj.getType());
      DBUtil.setString(ps, ++col, obj.getCommodity());
      DBUtil.setString(ps, ++col, obj.getFormat());
      DBUtil.setString(ps, ++col, obj.getDate());
      ps.executeUpdate();

    } finally {
      DBUtil.cleanup(ps, null);
    }
    return true;
  }

  public static boolean save(ExportTaskStatus obj) throws SQLException {
    ExportTaskStatus _obj = getObj(obj.getType(), obj.getCommodity(), obj.getFormat(), obj.getDate());
    if (_obj == null) {
      return insert(obj);
    } else {
      return update(obj);
    }
  }

  /**
   * code auto write. ready only file. don't change any code.
   */

  public static boolean delete(java.lang.String type, java.lang.String commodity, java.lang.String format, java.lang.String date) throws SQLException {
    Connection con = null;
    boolean ret = false;
    try {
      con = getConnection();
      con.setAutoCommit(false);
      ret = delete(con, type, commodity, format, date);
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

  public static boolean delete(Connection con, java.lang.String type, java.lang.String commodity, java.lang.String format, java.lang.String date) throws SQLException {
    PreparedStatement ps = null;
    try {
      LogService.sql(Base_ExportTaskStatusSQL.class, "SQL", SQL_DELETE);
      ps = con.prepareStatement(SQL_DELETE);
      int col = 0;
      DBUtil.setString(ps, ++col, type);
      DBUtil.setString(ps, ++col, commodity);
      DBUtil.setString(ps, ++col, format);
      DBUtil.setString(ps, ++col, date);
    } finally {
      DBUtil.cleanup(ps, null);
    }
    return true;
  }

  /**
   * code auto write. ready only file. don't change any code.
   */
  public static ExportTaskStatus getObj(java.lang.String type, java.lang.String commodity, java.lang.String format, java.lang.String date) throws SQLException {
    List<ExportTaskStatus> list = getObjs(type, commodity, format, date);
    if (list.size() == 0) {
      return null;
    } else {

      return (ExportTaskStatus) list.get(0);
    }
  }

  public static List<ExportTaskStatus> getObjs(java.lang.String type, java.lang.String commodity, java.lang.String format, java.lang.String date) throws SQLException {
    List<ExportTaskStatus> list = new ArrayList<ExportTaskStatus>();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();

      String sql = SQL_QUERY + " WHERE  ";

      sql += "TYPE = ?  and  ";

      sql += "COMMODITY = ?  and  ";

      sql += "FORMAT = ?  and  ";

      sql += "DATE = ?  ";

      LogService.sql(Base_ExportTaskStatusSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, type);
      DBUtil.setString(ps, ++col, commodity);
      DBUtil.setString(ps, ++col, format);
      DBUtil.setString(ps, ++col, date);
      ps.executeQuery();
      rs = ps.getResultSet();

      while (rs.next()) {
        ExportTaskStatus obj = new ExportTaskStatus();
        getValues(rs, obj, 0);

        list.add(obj);
      }
      return list;
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
  }

  /**
   * code auto write. ready only file. don't change any code.
   */
  public static int getValues(ResultSet rs, ExportTaskStatus obj,

  int col) throws SQLException {

    String type = rs.getString(++col);

    String commodity = rs.getString(++col);

    String format = rs.getString(++col);

    String date = rs.getString(++col);

    String status = rs.getString(++col);

    obj.setType(type);

    obj.setCommodity(commodity);

    obj.setFormat(format);

    obj.setDate(date);

    obj.setStatus(status);

    return col;
  }

  /**
   * code auto write. ready only file. don't change any code.
   */

  public static int setValues(PreparedStatement ps, ExportTaskStatus obj, int col) throws SQLException {

    DBUtil.setString(ps, ++col, obj.getStatus());

    return col;
  }

  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }
}
