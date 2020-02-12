package model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import model.constant.Commodity;
import tcc.utils.db.DBUtil;
import tutami.fw.DB;

public class DeleteTable {
  /**
   * code auto write. ready only file. don't change any code.
   */
  protected static javax.sql.DataSource TTDB;
  static {
    TTDB = DB.getDBPoolADMIN();
  }

  protected static Connection getConnection() throws SQLException {
    return TTDB.getConnection();
  }

  public static boolean deleteAll(Commodity commodity, String table) throws SQLException {
    Connection con = null;
    PreparedStatement ps = null;
    boolean ret = false;
    try {
      con = getConnection();
      con.setAutoCommit(false);
      String sql = "delete from " + table.split(" ")[0];
      if (commodity != null)
        sql += " where commodity=?";
      ps = con.prepareStatement(sql);
      int col = 0;
      if (commodity != null)
        DBUtil.setString(ps, ++col, commodity.getCommodity());
      ps.execute();
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
      DBUtil.cleanup(ps, con);
    }
    return ret;
  }
}
