package ant.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.db.InstructionStatus;
import tcc.utils.db.DBUtil;
import ant.server.AntInstruction;
import ant.server.db.base.Base_AntInstructionSQL;

public class AntInstructionSQL extends Base_AntInstructionSQL {
  public static boolean insert(AntInstruction obj) throws SQLException {
    obj.setUpdateAt(new Date(System.currentTimeMillis()));
    return Base_AntInstructionSQL.insert(obj);
  }

  public static boolean update(AntInstruction obj) throws SQLException {
    obj.setUpdateAt(new Date(System.currentTimeMillis()));
    return Base_AntInstructionSQL.update(obj);
  }

  public static List<AntInstruction> getStartedObjs() throws SQLException {
    return getObjs(InstructionStatus.STATUS_START);
  }

  public static AntInstruction getObjByName(String name) throws SQLException {
    AntInstruction obj = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + "  WHERE name=? ";

      ps = conn.prepareStatement(sql);
      int col = 0;

      DBUtil.setString(ps, ++col, name);
      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {
        obj = new AntInstruction();
        getValues(rs, obj, 0);

      }
      return obj;
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
  }

  public static List<AntInstruction> getObjs(String status) throws SQLException {
    List<AntInstruction> ret = new ArrayList<AntInstruction>();
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
        AntInstruction obj = new AntInstruction();
        getValues(rs, obj, 0);
        ret.add(obj);
      }
      return ret;
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
  }

  public static boolean reset() throws SQLException {
    return reset(InstructionStatus.STATUS_SUCCESS, "STATUS_RESET");
  }

  public static boolean sch() throws SQLException {
    return reset(InstructionStatus.STATUS_PROCESS, InstructionStatus.STATUS_START);
  }

  public static boolean reset(String fromStatus, String toStatus) throws SQLException {
    Connection con = null;
    PreparedStatement ps = null;
    boolean ret = false;
    try {
      con = getConnection();
      con.setAutoCommit(false);
      ps = con.prepareStatement("update AntInstruction set status=? where status!=?");
      int col = 0;
      DBUtil.setString(ps, ++col, toStatus);
      DBUtil.setString(ps, ++col, fromStatus);
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
