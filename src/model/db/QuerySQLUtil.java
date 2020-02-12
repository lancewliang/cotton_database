package model.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class QuerySQLUtil {

  public static List<Long> queryDays(Connection conn, String SQL_TABLE, Commodity commodity, Country country, String source, String condition, long reportStartDate, long reportEndDate) {
    List<Long> list = new ArrayList<Long>();

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {

      String D_SQL = "select distinct reportDate from " + SQL_TABLE + " where 1=1 ";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, country, source, condition);

      if (reportStartDate > 0) {
        D_SQL += " and reportDate>=" + reportStartDate + " ";
      }
      if (reportEndDate > 0) {
        D_SQL += " and reportDate<=" + reportEndDate + " ";
      }

      D_SQL += DaylyQuery.QUERYDATE_ORDER;
      LogService.sql(QuerySQLUtil.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);

      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {
        list.add(rs.getLong(1));
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();

    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return list;
  }

  public static String QuerySQLString(Commodity commodity, String source, String condition) {
    String D_SQL = " and commodity = '" + commodity.getCommodity() + "'";
    if (None.isNonBlank(source)) {
      D_SQL += " and source ='" + source + "'";
    }
    if (None.isNonBlank(condition)) {
      if (!condition.trim().startsWith("and")) {
        D_SQL += " and ";
      }
      D_SQL += condition;
    }
    return D_SQL;
  }

  public static String QuerySQLString(String source, String condition) {
    String D_SQL = "";
    if (None.isNonBlank(source)) {
      D_SQL += " and source ='" + source + "'";
    }
    if (None.isNonBlank(condition)) {
      if (!condition.trim().startsWith("and")) {
        D_SQL += " and ";
      }
      D_SQL += condition;
    }
    return D_SQL;
  }

  public static String QuerySQLString(Commodity commodity, Country country, String source, String condition) {
    String D_SQL = " and commodity = '" + commodity.getCommodity() + "' ";
    if (country != null)
      D_SQL += " and COUNTRY ='" + country.getCountry() + "'";
    if (None.isNonBlank(source)) {
      D_SQL += " and source ='" + source + "'";
    }
    if (None.isNonBlank(condition)) {
      if (!condition.trim().startsWith("and")) {
        D_SQL += " and ";
      }
      D_SQL += condition;
    }
    return D_SQL;
  }

  public static long getStartMonth(String year, Commodity commodity) {
    return Long.parseLong(year + "08");
  }

  public static long getEndMonth(String year, Commodity commodity) {
    return Long.parseLong((Long.parseLong(year) + 1) + "07");
  }

}
