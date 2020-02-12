package model.entity.wasde.db;

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
import model.db.DaylyQuery;
import model.db.QuerySQLUtil;
import model.entity.DaylyRecord;
import model.entity.production.country.db.base.Base_YieldDaySQL;
import model.entity.sale.country.db.base.Base_SaleDaySQL;
import model.entity.wasde.db.base.Base_WorldSupplyDemandMonthlyHistorySQL;
import model.entity.wasde.obj.WorldSupplyDemandMonthlyHistory;
import model.entity.weather.db.base.Base_WeatherDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class WorldSupplyDemandMonthlyHistorySQL extends Base_WorldSupplyDemandMonthlyHistorySQL implements DaylyQuery, DateListQuery {
  public static boolean isSameWithLast(WorldSupplyDemandMonthlyHistory newOne) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + " WHERE  ";
      sql += "commodity = ?  and  ";
      sql += "country = ?  and  ";
      sql += "year = ?  and reportStatus=? and source=? ";
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
      DBUtil.setInt(ps, ++col, newOne.getReportStatus());
      DBUtil.setString(ps, ++col, newOne.getSource());
      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        WorldSupplyDemandMonthlyHistory obj = new WorldSupplyDemandMonthlyHistory();
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

  public static int getLastDay(Commodity commodity, String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from WorldSupplyDemandMonthlyHistory WHERE commodity = ?  and source =? ";

      LogService.sql(Base_WeatherDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, commodity.getCommodity());

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
  public List<DateList> queryDays(Commodity commodity, Country country, String source, String condition, long reportStartDate, long reportEndDate) {
    List<String> years = getYears(commodity, country, source, condition);
    List<DateList> ret = new ArrayList<DateList>();
    try {
      for (String year : years) {
        String _condition = condition;
        if (None.isNonBlank(_condition)) {
          _condition += " and ";
        }
        String contract_condition = " year='" + year + "' ";

        _condition += contract_condition;
        List<Long> dates = QuerySQLUtil.queryDays(getConnection(), SQL_TABLE, commodity, country, source, _condition, reportStartDate, reportEndDate);
        if (!None.isEmpty(dates)) {
          ret.add(new DateList(dates, contract_condition, year));
        }
      }
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
    return ret;
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
  public DaylyRecord queryDayly(long day, Commodity commodity, Country country, String source, String condition) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String D_SQL = SQL_QUERY + " where reportDate=?   ";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, country, source, condition);
      LogService.sql(Base_SaleDaySQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;

      DBUtil.setLong(ps, ++col, day);

      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        WorldSupplyDemandMonthlyHistory obj = new WorldSupplyDemandMonthlyHistory();
        getValues(rs, obj, 0);
        return obj;
      }
    } catch (SQLException e) {
      LogService.trace(e, null);
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return null;
  }
}
