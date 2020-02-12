package model.entity.stock.db;

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
import model.entity.stock.IndustrialStockMonth;
import model.entity.stock.db.base.Base_IndustrialStockMonthSQL;
import model.entity.weather.db.base.Base_WeatherDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class IndustrialStockMonthSQL extends Base_IndustrialStockMonthSQL implements YearlyQuery, MonthlyQuery, SaveDB, DateListQuery {

  public static int getLastDay(Commodity commodity, Country country, String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from IndustrialStockMonth WHERE commodity = ? and country = ? and source =? ";

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

  @Override
  public MonthlyRecord queryMonthly(long month, Commodity commodity, Country country, String source, String condition) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + " WHERE REPORTDATE = ? ";
      sql += QuerySQLUtil.QuerySQLString(commodity, country, source, condition);

      LogService.sql(IndustrialStockMonthSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setLong(ps, ++col, month);
      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        IndustrialStockMonth obj = new IndustrialStockMonth();
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
  public YearlyRecord queryYearly(String year, Commodity commodity, Country country, String source, String condition) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + " WHERE REPORTDATE = ? ";
      sql += QuerySQLUtil.QuerySQLString(commodity, country, source, condition);

      LogService.sql(IndustrialStockMonthSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setLong(ps, ++col, QuerySQLUtil.getStartMonth(year, commodity));
      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        IndustrialStockMonth obj = new IndustrialStockMonth();
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
    List<IndustrialStockMonth> list = new ArrayList<IndustrialStockMonth>();
    for (Record ob : objs) {
      list.add((IndustrialStockMonth) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<IndustrialStockMonth> list = new ArrayList<IndustrialStockMonth>();
    for (Record ob : objs) {
      list.add((IndustrialStockMonth) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }
}
