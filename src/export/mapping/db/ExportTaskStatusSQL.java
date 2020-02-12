package export.mapping.db;

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
import tcc.utils.log.LogService;

import export.mapping.db.base.Base_ExportTaskStatusSQL;
import export.mapping.excell.ExportTaskStatus;

public class ExportTaskStatusSQL extends Base_ExportTaskStatusSQL {
  public static List<ExportTaskStatus> getObjs(String status) throws SQLException {
    List<ExportTaskStatus> list = new ArrayList<ExportTaskStatus>();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();

      String sql = SQL_QUERY + " WHERE status = ?  ";

      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, status);
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

  public static Set<String> getStatus(String type, String commodity, String format) throws SQLException {
    Connection conn = null;
    Set<String> ret = new HashSet<String>();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select distinct status from ExportTaskStatus WHERE type=? and commodity=? and format=? ";

      ps = conn.prepareStatement(sql);
      int col = 0;

      DBUtil.setString(ps, ++col, type);
      DBUtil.setString(ps, ++col, commodity);
      DBUtil.setString(ps, ++col, format);

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

  public static void reset() throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    try {
      conn = getConnection();
      ps = conn.prepareStatement("update ExportTaskStatus set status=? where status=?");
      int col = 0;
      DBUtil.setString(ps, ++col, InstructionStatus.STATUS_START);
      DBUtil.setString(ps, ++col, InstructionStatus.STATUS_PROCESS);
      ps.execute();
    } finally {
      DBUtil.cleanup(ps, conn);
    }
   
  }

  public static void deletes(String status) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    try {
      conn = getConnection();
      ps = conn.prepareStatement("delete from ExportTaskStatus where status=?");
      int col = 0;
      DBUtil.setString(ps, ++col, status);
      ps.execute();
    } finally {
      DBUtil.cleanup(ps, conn);
    }

  }
}
