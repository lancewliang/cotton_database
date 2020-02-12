package model.entity.gov.country.db;

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
import model.db.QuerySQLUtil;
import model.entity.DaylyRecord;
import model.entity.gov.country.GovDay;
import model.entity.gov.country.GovMonth;
import model.entity.gov.country.db.base.Base_GovDaySQL;
import model.entity.production.country.db.base.Base_YieldDaySQL;
import model.entity.sale.country.db.base.Base_SaleDaySQL;
import model.entity.weather.db.base.Base_WeatherDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class GovDaySQL extends Base_GovDaySQL implements DaylyQuery, DateListQuery {
  public static void getTotalByMonth(GovMonth lGovMonth, GovMonth cGovMonth) throws SQLException {
    GovDay lastMonthMaxDay = getMonthMaxDay(lGovMonth.getReportDate(), lGovMonth.getCommodity(), lGovMonth.getCountry(), lGovMonth.getSource());
    GovDay currrentMonthMaxDay = getMonthMaxDay(cGovMonth.getReportDate(), cGovMonth.getCommodity(), cGovMonth.getCountry(), cGovMonth.getSource());
    double totalSell = 0;
    double totalBuy = 0;

    if (currrentMonthMaxDay != null) {
      double lastTotalSell = (lastMonthMaxDay != null ? lastMonthMaxDay.getTotalSellValue() : 0);
      if (currrentMonthMaxDay.getTotalSellValue() > 0 && currrentMonthMaxDay.getTotalSellValue() > lastTotalSell) {
        totalSell = currrentMonthMaxDay.getTotalSellValue() - lastTotalSell;
      }

      double lastTotalBuy = (lastMonthMaxDay != null ? lastMonthMaxDay.getTotalBuyValue() : 0);
      if (currrentMonthMaxDay.getTotalBuyValue() > 0 && currrentMonthMaxDay.getTotalBuyValue() > lastTotalBuy) {
        totalBuy = currrentMonthMaxDay.getTotalBuyValue() - lastTotalBuy;
      }
    }
    cGovMonth.setBuyValue(totalBuy);
    cGovMonth.setSellValue(totalSell);

  }

  public static GovDay getMonthMaxDay(long month, Commodity commodity, Country country, String source) throws SQLException {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + "  WHERE reportDate>=? and reportDate<=? and commodity = ? and country = ? and source =? order by reportDate desc";

      LogService.sql(Base_WeatherDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setLong(ps, ++col, Long.parseLong(month + "00"));
      DBUtil.setLong(ps, ++col, Long.parseLong(month + "31"));
      DBUtil.setString(ps, ++col, commodity.getCommodity());

      DBUtil.setString(ps, ++col, country.getCountry());
      DBUtil.setString(ps, ++col, source);

      ps.executeQuery();
      rs = ps.getResultSet();

      if (rs.next()) {
        GovDay obj = new GovDay();
        getValues(rs, obj, 0);
        return obj;
      }
      return null;
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
  }

  public static int getSellLastDay(Commodity commodity, Country country, String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from GovDay WHERE commodity = ? and country = ? and source =? and sellValue>0";

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

  public static int getBuyLastDay(Commodity commodity, Country country, String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from GovDay WHERE commodity = ? and country = ? and source =? and buyValue>0";

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
  public List<DateList> queryDays(Commodity commodity, Country country, String source, String condition, long reportStartDate, long reportEndDate) {
    try {
      return DateListQueryUtil.queryDays(getConnection(), SQL_TABLE, commodity, country, source, condition, reportStartDate, reportEndDate);
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
    return null;
  }

  public static GovDay getObjGovDay(long day, Commodity commodity, Country country, String source, String condition) throws SQLException {
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
        GovDay obj = new GovDay();
        getValues(rs, obj, 0);
        return obj;
      }
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return null;
  }

  @Override
  public DaylyRecord queryDayly(long day, Commodity commodity, Country country, String source, String condition) {
    try {
      return getObjGovDay(day, commodity, country, source, condition);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

}
