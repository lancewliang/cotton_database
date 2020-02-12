package model.entity.custom.country.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import export.mapping.report.field.ReportObjectField;

import model.constant.Commodity;
import model.constant.Country;
import model.db.DateList;
import model.db.DaylyQuery;
import model.db.DeleteTable;
import model.db.QuerySQLUtil;
import model.db.SaveDB;
import model.entity.Record;
import model.entity.custom.country.ImportExportMonth;
import model.entity.custom.country.db.base.Base_ImportExportMonthSQL;
import model.entity.production.country.db.base.Base_YieldDaySQL;
import model.entity.weather.db.base.Base_WeatherDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class ImportExportMonthSQL extends Base_ImportExportMonthSQL implements SaveDB {

  public static List<ReportObjectField> queryReportObjectFields(Commodity commodity, Country country, String source, String colname, String countrycondition, String condition) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    List<ReportObjectField> dlist = new ArrayList<ReportObjectField>();
    try {
      conn = getConnection();
      String D_SQL = "select distinct " + colname + ",sum(value) x from " + SQL_TABLE + " where " + countrycondition;
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, source, condition);
      D_SQL += "group by " + colname + " order by x desc";
      LogService.sql(Base_YieldDaySQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;
      DBUtil.setString(ps, ++col, country.getCountry());
      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {
        String conditionS = condition + " " + colname + "='" + rs.getString(1) + "'";
        Country toCObj = Country.getCountry(rs.getString(1));
        ReportObjectField field = new ReportObjectField(null, toCObj.getDisplay(), null, conditionS, toCObj.getDisplay(), null, commodity.getDisplay(), country.getCountry(), source);
        dlist.add(field);
      }

    } catch (SQLException e) {
      LogService.trace(e, null);
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }

    return dlist;
  }

  public static List<DateList> queryDays(Commodity commodity, Country country, String source, String countrycondition, String condition, long reportStartDate, long reportEndDate) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    List<DateList> dlist = new ArrayList<DateList>();
    List<Long> list = new ArrayList<Long>();
    try {
      conn = getConnection();
      String D_SQL = "select distinct reportDate from " + SQL_TABLE + " where " + countrycondition;
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, source, condition);
      if (reportStartDate > 0) {
        D_SQL += " and reportDate>=" + reportStartDate + " ";
      }
      if (reportEndDate > 0) {
        D_SQL += " and reportDate<=" + reportEndDate + " ";
      }
      D_SQL += DaylyQuery.QUERYDATE_ORDER;
      LogService.sql(Base_YieldDaySQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;
      DBUtil.setString(ps, ++col, country.getCountry());
      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {

        list.add(rs.getLong(1));
      }
      dlist.add(new DateList(list));
    } catch (SQLException e) {
      LogService.trace(e, null);
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return dlist;
  }

  public static ImportExportMonth getObj(long reportDate, model.constant.Country toCountry, model.constant.Country fromCountry, model.constant.Commodity commodity, java.lang.String source) throws SQLException {
    return Base_ImportExportMonthSQL.getObj(reportDate, toCountry, fromCountry, commodity, source);
  }

  public static int getTOCountryLastDay(Commodity commodity, Country TOCOUNTRY, String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from ImportExportMonth WHERE commodity = ? and TOCOUNTRY = ?  and source =? ";

      LogService.sql(Base_WeatherDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, commodity.getCommodity());

      DBUtil.setString(ps, ++col, TOCOUNTRY.getCountry());
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

  public static int getFROMCountryLastDay(Commodity commodity, Country fromCountry, String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from ImportExportMonth WHERE commodity = ? and FROMCOUNTRY = ?  and source =? ";

      LogService.sql(Base_WeatherDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, commodity.getCommodity());

      DBUtil.setString(ps, ++col, fromCountry.getCountry());
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

  public static ImportExportMonth queryMonthly(long month, Commodity commodity, String countrycondition, Country country, String source, String condition) {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String D_SQL = SQL_QUERY + " where reportDate=? and " + countrycondition + " ";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, source, condition);
      LogService.sql(ImportExportMonthSQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;
      DBUtil.setLong(ps, ++col, month);
      DBUtil.setString(ps, ++col, country.getCountry());
      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        ImportExportMonth obj = new ImportExportMonth();
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

  public static List<ImportExportMonth> queryYearly(String year, Commodity commodity, String countrycondition, Country country, String source, String condition) {
    List<ImportExportMonth> list = new ArrayList<ImportExportMonth>();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String D_SQL = SQL_QUERY + " where reportDate>=? and reportDate<=? and " + countrycondition + " ";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, source, condition);
      D_SQL += " order by reportDate asc";
      LogService.sql(ImportExportMonthSQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;
      DBUtil.setLong(ps, ++col, (QuerySQLUtil.getStartMonth(year, commodity)));
      DBUtil.setLong(ps, ++col, QuerySQLUtil.getEndMonth(year, commodity));
      DBUtil.setString(ps, ++col, country.getCountry());

      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {
        ImportExportMonth obj = new ImportExportMonth();
        getValues(rs, obj, 0);
        list.add(obj);
      }
    } catch (Exception e) {
      e.printStackTrace();

    } finally {
      DBUtil.cleanup(rs, ps, conn);

    }
    return list;
  }

  @Override
  public boolean save(List<Record> objs) throws SQLException {
    List<ImportExportMonth> list = new ArrayList<ImportExportMonth>();
    for (Record ob : objs) {
      list.add((ImportExportMonth) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<ImportExportMonth> list = new ArrayList<ImportExportMonth>();
    for (Record ob : objs) {
      list.add((ImportExportMonth) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }

}
