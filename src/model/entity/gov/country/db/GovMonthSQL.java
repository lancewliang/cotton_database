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
import model.db.DeleteTable;
import model.db.MonthlyQuery;
import model.db.QuerySQLUtil;
import model.db.SaveDB;
import model.db.YearlyQuery;
import model.entity.MonthlyRecord;
import model.entity.Record;
import model.entity.YearlyRecord;
import model.entity.gov.country.GovMonth;
import model.entity.gov.country.GovYear;
import model.entity.gov.country.db.base.Base_GovMonthSQL;
import model.entity.weather.db.base.Base_WeatherDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class GovMonthSQL extends Base_GovMonthSQL implements MonthlyQuery, YearlyQuery, SaveDB, DateListQuery {

  @Override
  public List<DateList> queryDays(Commodity commodity, Country country, String source, String condition, long reportStartDate, long reportEndDate) {
    try {
      return DateListQueryUtil.queryDays(getConnection(), SQL_TABLE, commodity, country, source, condition, reportStartDate, reportEndDate);
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
    return null;
  }

  public static int getBuyLastMonth(Commodity commodity, Country country, String source) throws SQLException {

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

  public static GovMonth queryMonthlyObj(long month, Commodity commodity, Country country, String source, String condition) {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String D_SQL = SQL_QUERY + " where reportDate=?";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, country, source, condition);

      LogService.sql(Base_GovMonthSQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;
      DBUtil.setLong(ps, ++col, month);

      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        GovMonth obj = new GovMonth();
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
  public MonthlyRecord queryMonthly(long month, Commodity commodity, Country country, String source, String condition) {
    return queryMonthlyObj(month, commodity, country, source, condition);
  }

  @Override
  public YearlyRecord queryYearly(String year, Commodity commodity, Country country, String source, String condition) {
    List<GovMonth> list = new ArrayList<GovMonth>();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String D_SQL = SQL_QUERY + " where reportDate>=? and reportDate<=?  ";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, country, source, condition);
      D_SQL += " order by reportDate asc";
      LogService.sql(GovMonthSQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;
      DBUtil.setLong(ps, ++col, QuerySQLUtil.getStartMonth(year, commodity));
      DBUtil.setLong(ps, ++col, QuerySQLUtil.getEndMonth(year, commodity));

      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {
        GovMonth obj = new GovMonth();
        getValues(rs, obj, 0);
        list.add(obj);
      }

    } catch (Exception e) {
      e.printStackTrace();

    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    GovYear yearly = null;
    if (!None.isEmpty(list)) {
      yearly = new GovYear();
      double buyValue = DBUtil.NULLDOUBLE;
      double sellValue = DBUtil.NULLDOUBLE;
      for (GovMonth im : list) {
        if (im.getBuyValue() != DBUtil.NULLDOUBLE)
          buyValue += im.getBuyValue();
        if (im.getSellValue() != DBUtil.NULLDOUBLE)
          sellValue += im.getSellValue();
      }

      yearly.setCommodity(commodity);
      yearly.setReportDate(year);
      yearly.setCountry(country);
      yearly.setBuyValue(buyValue);
      yearly.setSellValue(sellValue);
      yearly.setReserveValue(list.get(list.size() - 1).getReserveValue());
      yearly.setWeightUnit(list.get(0).getWeightUnit());
    }
    return yearly;
  }

  @Override
  public boolean save(List<Record> objs) throws SQLException {
    List<GovMonth> list = new ArrayList<GovMonth>();
    for (Record ob : objs) {
      list.add((GovMonth) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<GovMonth> list = new ArrayList<GovMonth>();
    for (Record ob : objs) {
      list.add((GovMonth) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }
}
