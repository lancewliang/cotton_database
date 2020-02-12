package model.entity.production.country.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import model.entity.production.country.db.base.Base_GrowAreaYearSQL;
import model.entity.production.country.db.base.Base_YieldDaySQL;
import model.entity.production.country.db.base.Base_YieldYearSQL;
import model.entity.wasde.db.WorldSupplyDemandMonthlyHistorySQL;
import model.entity.wasde.obj.WorldSupplyDemandMonthlyHistory;
import model.entity.weather.db.base.Base_WeatherDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class GrowAreaYearSQL extends Base_GrowAreaYearSQL implements YearlyQuery, SaveDB, DaylyQuery, DateListQuery {
  public static boolean isSameWithLast(GrowAreaYear newOne) throws SQLException {

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
        GrowAreaYear obj = new GrowAreaYear();
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
  public List<DateList> queryDays(Commodity commodity, Country country, String source, String condition, long reportStartDate, long reportEndDate) {
    try {
      if ("GrowWithProduction".equals(condition)) {

        List<String> years = getYears(commodity, country, source, null);
        if (None.isEmpty(years)) {
          years = YieldYearSQL.getYears(commodity, country, source, null);
        }
        List<DateList> ret = new ArrayList<DateList>();
        for (String year : years) {
          String _condition = "";
          if (None.isNonBlank(_condition)) {
            _condition += " and ";
          }
          String contract_condition = " year='" + year + "' ";

          _condition += contract_condition;
          List<Long> datesall = new ArrayList<Long>();
          List<Long> dates1 = QuerySQLUtil.queryDays(getConnection(), SQL_TABLE, commodity, country, source, _condition, reportStartDate, reportEndDate);
          List<Long> dates2 = QuerySQLUtil.queryDays(getConnection(), YieldYearSQL.SQL_TABLE, commodity, country, source, _condition, reportStartDate, reportEndDate);

          for (Long d : dates1) {
            if (!datesall.contains(d)) {
              datesall.add(d);
            }
          }
          for (Long d : dates2) {
            if (!datesall.contains(d)) {
              datesall.add(d);
            }
          }
          Collections.sort(datesall, new Comparator<Long>() {
            public int compare(Long o1, Long o2) {
              return o2.compareTo(o1);
            }
          });
          ret.add(new DateList(datesall, _condition, year));
        }
        return ret;
      } else
        return DateListQueryUtil.queryDays(getConnection(), SQL_TABLE, commodity, country, source, condition, reportStartDate, reportEndDate);
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
    return null;
  }

  public List<String> getYears(Commodity commodity, Country country, String source, String condition) {

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
        GrowAreaYear obj = new GrowAreaYear();
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
      String sql = "select max(reportDate) from GrowAreaYear WHERE commodity = ? and country = ? and source =? ";

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

  @Override
  public YearlyRecord queryYearly(String year, Commodity commodity, Country country, String source, String condition) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + " WHERE YEAR = ? ";
      sql += QuerySQLUtil.QuerySQLString(commodity, country, source, condition);

      LogService.sql(Base_GrowAreaYearSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, year);

      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        GrowAreaYear obj = new GrowAreaYear();
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
    List<GrowAreaYear> list = new ArrayList<GrowAreaYear>();
    for (Record ob : objs) {
      list.add((GrowAreaYear) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<GrowAreaYear> list = new ArrayList<GrowAreaYear>();
    for (Record ob : objs) {
      list.add((GrowAreaYear) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }
}
