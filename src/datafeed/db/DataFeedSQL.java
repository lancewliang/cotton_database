package datafeed.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.db.InstructionStatus;
import tcc.utils.db.DBUtil;
import datafeed.db.base.Base_FeedTaskStatusSQL;
import datafeed.excell.FeedTaskStatus;

public class DataFeedSQL extends Base_FeedTaskStatusSQL {

  public static List<FeedTaskStatus> getStartedObjs() throws SQLException {
    return getObjs(InstructionStatus.STATUS_START);
  }

  public static List<FeedTaskStatus> getObjs(String status) throws SQLException {
    List<FeedTaskStatus> ret = new ArrayList<FeedTaskStatus>();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + "  WHERE status=? ";

      ps = conn.prepareStatement(sql);
      int col = 0;

      DBUtil.setString(ps, ++col, status);
      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {
        FeedTaskStatus obj = new FeedTaskStatus();
        getValues(rs, obj, 0);
        ret.add(obj);
      }
      return ret;
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
  }

  public static boolean hasStatus(String status) throws SQLException {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + "  WHERE status=? ";

      ps = conn.prepareStatement(sql);
      int col = 0;

      DBUtil.setString(ps, ++col, status);
      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        return true;
      }
      return false;
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
  }

  public static Set<String> getStatus(String type, String filename) throws SQLException {
    Connection conn = null;
    Set<String> ret = new HashSet<String>();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select distinct status from FeedTaskStatus  WHERE type=? and filename=?";

      ps = conn.prepareStatement(sql);
      int col = 0;

      DBUtil.setString(ps, ++col, type);
      DBUtil.setString(ps, ++col, filename);
      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {
        ret.add(rs.getString(1));
      }
      return ret;
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
  }

  public static boolean reset() throws SQLException {
    Connection con = null;
    PreparedStatement ps = null;
    boolean ret = false;
    try {
      con = getConnection();
      con.setAutoCommit(false);
      ps = con.prepareStatement("update FeedTaskStatus set status=? where status=?");
      int col = 0;
      DBUtil.setString(ps, ++col, InstructionStatus.STATUS_START);
      DBUtil.setString(ps, ++col, InstructionStatus.STATUS_PROCESS);
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
