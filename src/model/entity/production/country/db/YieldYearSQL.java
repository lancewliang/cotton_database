package model.entity.production.country.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.db.DateList;
import model.db.DateListQuery;
import model.db.DateListQueryUtil;
import model.db.DaylyQuery;
import model.db.DeleteTable;
import model.db.QuerySQLUtil;
import model.db.SaveDB;
import model.db.YearlyQuery;
import model.entity.DaylyRecord;
import model.entity.Record;
import model.entity.YearlyRecord;
import model.entity.production.country.GrowAreaYear;
import model.entity.production.country.YieldYear;
import model.entity.production.country.db.base.Base_YieldDaySQL;
import model.entity.production.country.db.base.Base_YieldYearSQL;
import model.entity.wasde.db.WorldSupplyDemandMonthlyHistorySQL;
import model.entity.weather.db.base.Base_WeatherDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class YieldYearSQL extends Base_YieldYearSQL implements YearlyQuery, SaveDB, DaylyQuery, DateListQuery {

  @Override
  public List<DateList> queryDays(Commodity commodity, Country country, String source, String condition, long reportStartDate, long reportEndDate) {
    try {
      return DateListQueryUtil.queryDays(getConnection(), SQL_TABLE, commodity, country, source, condition, reportStartDate, reportEndDate);
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
    return null;
  }

  @Override
  public DaylyRecord queryDayly(long reportDate, Commodity commodity, Country country, String source, String condition) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + " WHERE reportDate = ?  ";
      sql += QuerySQLUtil.QuerySQLString(commodity, country, source, condition);

      LogService.sql(Base_YieldYearSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setLong(ps, ++col, reportDate);

      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        YieldYear obj = new YieldYear();
        getValues(rs, obj, 0);
        return obj;
      }

    } catch (Exception e) {
      e.printStackTrace();

    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return null;
  }

  public static int getLastDay(Commodity commodity, Country country, String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from YieldYear WHERE commodity = ? and country = ? and source =? ";

      LogService.sql(Base_WeatherDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, commodity.getCommodity());

      DBUtil.setString(ps, ++col, country.getCountry());
      DBUtil.setString(ps, ++col, source);
      ps.executeQuery();
      rs = ps.getResultSet();

      if (rs.next()) {
        String str = rs.getString(1);
        if (None.isNonBlank(str))
          return Integer.parseInt(str);
      }
      return 0;
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
  }

  public static List<String> getYears(Commodity commodity, Country country, String source, String condition) {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    List<String> dlist = new ArrayList<String>();
    try {
      conn = getConnection();
      String D_SQL = "select distinct year from " + SQL_TABLE + " where 1=1 ";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, source, condition);
      D_SQL += " GROUP BY year asc";
      LogService.sql(Base_YieldDaySQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;
      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {

        dlist.add(rs.getString(1));
      }

    } catch (SQLException e) {
      LogService.trace(e, null);
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }

    return dlist;
  }

  public static boolean isSameWithLast(YieldYear newOne) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + " WHERE  ";
      sql += "commodity = ?  and  ";
      sql += "country = ?  and  ";
      sql += "year = ?    and source=? ";
      sql += "order by REPORTDATE desc";
      if (20130311 == newOne.getReportDate() && newOne.getCountry().equals(Country.getCountry("CHN"))) {

        LogService.msg(WorldSupplyDemandMonthlyHistorySQL.class, "SQL", sql);
      }
      // LogService.msg(WorldSupplyDemandMonthlyHistorySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, newOne.getCommodity().getCommodity());
      DBUtil.setString(ps, ++col, newOne.getCountry().getCountry());
      DBUtil.setString(ps, ++col, newOne.getYear());
      DBUtil.setString(ps, ++col, newOne.getSource());
      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        YieldYear obj = new YieldYear();
        getValues(rs, obj, 0);

        if (obj.isDataSame(newOne)) {

          return true;
        }
      }
      return false;
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
  }

  @Override
  public YearlyRecord queryYearly(String year, Commodity commodity, Country country, String source, String condition) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + " WHERE YEAR = ?  ";
      sql += QuerySQLUtil.QuerySQLString(commodity, country, source, condition);

      LogService.sql(Base_YieldYearSQL.class, "SQL", sql);

      sql += " order by reportDate desc";
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, year);

      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        YieldYear obj = new YieldYear();
        getValues(rs, obj, 0);
        return obj;
      }

    } catch (Exception e) {
      e.printStackTrace();

    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return null;
  }

  @Override
  public boolean save(List<Record> objs) throws SQLException {
    List<YieldYear> list = new ArrayList<YieldYear>();
    for (Record ob : objs) {
      list.add((YieldYear) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<YieldYear> list = new ArrayList<YieldYear>();
    for (Record ob : objs) {
      list.add((YieldYear) ob);
    }
    return super.delete(list);
  }

  public static List<Country> getCountrys(Commodity c) {
    List<Country> list = new ArrayList<Country>();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select distinct country from " + SQL_TABLE + " where commodity = ?";

      LogService.sql(Base_YieldYearSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, c.getCommodity());

      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {
        list.add(Country.getCountry(rs.getString(1)));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return list;
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }
}
