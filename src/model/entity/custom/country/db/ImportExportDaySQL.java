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
import model.db.DateListQuery;
import model.db.DaylyQuery;
import model.db.DeleteTable;
import model.db.FieldQuery;
import model.db.QuerySQLUtil;
import model.db.SaveDB;
import model.entity.DaylyRecord;
import model.entity.Record;
import model.entity.custom.country.ImportExportDay;
import model.entity.custom.country.db.base.Base_ImportExportDaySQL;
import model.entity.production.country.db.base.Base_YieldDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class ImportExportDaySQL extends Base_ImportExportDaySQL implements SaveDB, DaylyQuery, DateListQuery, FieldQuery {

  public static String countrycondition = " fromCountry=?";

  @Override
  public List<ReportObjectField> queryReportObjectFields(Commodity commodity, Country country, String source, String condition) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    List<ReportObjectField> dlist = new ArrayList<ReportObjectField>();
    try {
      conn = getConnection();
      String D_SQL = "select distinct toCountry,sum(total) x from " + SQL_TABLE + " where " + countrycondition;
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, source, condition);
      D_SQL += " group by toCountry order by x desc";
      LogService.sql(Base_YieldDaySQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;
      DBUtil.setString(ps, ++col, country.getCountry());
      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {
        String conditionS = condition + " toCountry='" + rs.getString(1) + "'";
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

  @Override
  public List<DateList> queryDays(Commodity commodity, Country country, String source, String condition, long reportStartDate, long reportEndDate) {
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

  @Override
  public DaylyRecord queryDayly(long day, Commodity commodity, Country country, String source, String condition) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String D_SQL = SQL_QUERY + " where reportDate=? and " + countrycondition + " ";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, source, condition);
      LogService.sql(Base_YieldDaySQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;

      DBUtil.setLong(ps, ++col, day);
      DBUtil.setString(ps, ++col, country.getCountry());
      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        ImportExportDay obj = new ImportExportDay();
        getValues(rs, obj, 0);
        return obj;
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return null;
  }

  public static ImportExportDay getObjImportExportDayByYear(String year, Commodity commodity, String countrycondition, Country country, String source, String condition) throws SQLException {
    condition = (None.isNonBlank(condition) ? (" and " + condition) : "");
    return getObjImportExportDayByRange(Long.parseLong(QuerySQLUtil.getStartMonth(year, commodity) + "00"), Long.parseLong(QuerySQLUtil.getEndMonth(year, commodity) + "31"), commodity, countrycondition, country, source, condition, false);
  }

  public static ImportExportDay getObjImportExportDayByMonth(long month, Commodity commodity, String countrycondition, Country country, String source, String condition, boolean asc) throws SQLException {
    condition = (None.isNonBlank(condition) ? (" and " + condition) : "");
    return getObjImportExportDayByRange(Long.parseLong(month + "00"), Long.parseLong(month + "31"), commodity, countrycondition, country, source, condition, asc);
  }

  public static ImportExportDay getObjImportExportDayByRange(long startDay, long endDay, Commodity commodity, String countrycondition, Country country, String source, String condition, boolean asc) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String D_SQL = SQL_QUERY + " where reportDate>=? and reportDate<=? and " + countrycondition + " ";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, source, condition);
      if (!asc) {
        D_SQL += " order by reportDate desc";
      } else {
        D_SQL += " order by reportDate asc";
      }
      LogService.sql(Base_YieldDaySQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;
      DBUtil.setLong(ps, ++col, startDay);
      DBUtil.setLong(ps, ++col, endDay);
      DBUtil.setString(ps, ++col, country.getCountry());
      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        ImportExportDay obj = new ImportExportDay();
        getValues(rs, obj, 0);
        return obj;
      }
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return null;
  }

  @Override
  public boolean save(List<Record> objs) throws SQLException {
    List<ImportExportDay> list = new ArrayList<ImportExportDay>();
    for (Record ob : objs) {
      list.add((ImportExportDay) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<ImportExportDay> list = new ArrayList<ImportExportDay>();
    for (Record ob : objs) {
      list.add((ImportExportDay) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }

}