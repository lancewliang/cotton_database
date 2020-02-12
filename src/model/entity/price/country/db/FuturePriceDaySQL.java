package model.entity.price.country.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import export.mapping.report.field.ReportObjectField;

import model.constant.Commodity;
import model.constant.Country;
import model.db.DateList;
import model.db.DateListQuery;
import model.db.DateListQueryUtil;
import model.db.DaylyQuery;
import model.db.DeleteTable;
import model.db.QuerySQLUtil;
import model.db.QueryTable;
import model.db.SaveDB;
import model.entity.DaylyRecord;
import model.entity.Record;
import model.entity.price.country.FuturePriceDay;
import model.entity.price.country.db.base.Base_FuturePriceDaySQL;
import model.entity.production.country.db.base.Base_YieldDaySQL;
import model.entity.weather.db.base.Base_WeatherDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class FuturePriceDaySQL extends Base_FuturePriceDaySQL implements SaveDB, DaylyQuery, QueryTable, DateListQuery {
  public static int getLastDay(String contract, String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from FuturePriceDay  Where contract=? and source = ?  ";

      LogService.sql(Base_WeatherDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, contract);
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

  public static int getLastDay(String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from FuturePriceDay  Where source = ?";

      LogService.sql(Base_WeatherDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
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
  public boolean save(List<Record> objs) throws SQLException {
    List<FuturePriceDay> list = new ArrayList<FuturePriceDay>();
    for (Record ob : objs) {
      list.add((FuturePriceDay) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<FuturePriceDay> list = new ArrayList<FuturePriceDay>();
    for (Record ob : objs) {
      list.add((FuturePriceDay) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }

  @Override
  public List<DateList> queryDays(Commodity commodity, Country country, String source, String condition, long reportStartDate, long reportEndDate) {
    List<DateList> ret = new ArrayList<DateList>();
    try {
      String cmd = "";
      if ("continuecontract".equals(condition)) {
        condition = null;
        cmd = "continuecontract";
      }

      if ("continuecontract".equals(cmd)) {
        List<Long> dates = QuerySQLUtil.queryDays(getConnection(), SQL_TABLE, commodity, country, source, null, reportStartDate, reportEndDate);
        if (!None.isEmpty(dates)) {
          ret.add(new DateList(dates, null, null));
        }
      } else {
        List<String> Contracts = getContract(commodity, country, source, condition);
        for (String contract : Contracts) {
          String _condition = condition;
          if (None.isNonBlank(_condition)) {
            _condition += " and ";
          }
          String contract_condition = " contract='" + contract + "' ";

          _condition += contract_condition;
          List<Long> dates = QuerySQLUtil.queryDays(getConnection(), SQL_TABLE, commodity, country, source, _condition, reportStartDate, reportEndDate);
          if (!None.isEmpty(dates)) {
            ret.add(new DateList(dates, contract_condition, contract));
          }
        }
      }
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
    return ret;
  }

  public List<String> getContract(Commodity commodity, Country country, String source, String condition) {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    List<String> dlist = new ArrayList<String>();
    try {
      conn = getConnection();
      String D_SQL = "select contract,MAX(reportDate) X from " + SQL_TABLE + " where 1=1 ";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, source, condition);
      D_SQL += " GROUP BY contract order by contract desc";
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

  private String getContractByReportDate(Country country, long reportDate) throws ParseException {
    if (country.getCountry().equals(Country.getCountry("CHN").getCountry())) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(sdf.parse("" + reportDate));
      int year = calendar.get(Calendar.YEAR);
      if (Long.parseLong(year + "0116") <= reportDate && reportDate <= Long.parseLong(year + "0515")) {
        return "CF" + String.valueOf(year).substring(3) + "05";
      } else if (Long.parseLong(year + "0516") <= reportDate && Long.parseLong(year + "0915") >= reportDate) {
        return "CF" + String.valueOf(year).substring(3) + "09";
      } else if (Long.parseLong((year - 1) + "0916") <= reportDate && Long.parseLong(year + "0115") >= reportDate) {
        return "CF" + String.valueOf(year).substring(3) + "01";
      } else if (Long.parseLong(year + "0916") <= reportDate && Long.parseLong((year + 1) + "0115") >= reportDate) {
        return "CF" + String.valueOf((year + 1)).substring(3) + "01";
      }
    } else if (country.getCountry().equals(Country.getCountry("USA").getCountry())) {

      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(sdf.parse("" + reportDate));
      int year = calendar.get(Calendar.YEAR);
      if (Long.parseLong(year + "0310") <= reportDate && reportDate <= Long.parseLong(year + "0509")) {
        return "May " + (year);
      } else if (Long.parseLong(year + "0510") <= reportDate && Long.parseLong(year + "0709") >= reportDate) {
        return "Jul " + (year);
      } else if (Long.parseLong(year + "0710") <= reportDate && Long.parseLong(year + "1009") >= reportDate) {
        return "Oct " + (year);
      } else if (Long.parseLong(year + "1010") <= reportDate && Long.parseLong(year + "1209") >= reportDate) {
        return "Dec " + (year);
      } else if (Long.parseLong((year - 1) + "1210") <= reportDate && Long.parseLong(year + "0309") >= reportDate) {
        return "Mar " + (year);
      } else if (Long.parseLong(year + "1210") <= reportDate && Long.parseLong((year + 1) + "0309") >= reportDate) {
        return "Mar " + (year + 1);
      }
    } else if (country.getCountry().equals(Country.getCountry("IND").getCountry())) {

      SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(sdf.parse("" + reportDate));
      int year = calendar.get(Calendar.YEAR);
      if (Long.parseLong(year + "0101") <= reportDate && reportDate <= Long.parseLong(year + "0131")) {
        return "CTF" + (year);
      } else if (Long.parseLong(year + "0201") <= reportDate && Long.parseLong(year + "0231") >= reportDate) {
        return "CTG" + (year);
      } else if (Long.parseLong(year + "0301") <= reportDate && Long.parseLong(year + "0331") >= reportDate) {
        return "CTH" + (year);
      } else if (Long.parseLong(year + "0401") <= reportDate && Long.parseLong(year + "0431") >= reportDate) {
        return "CTJ" + (year);
      } else if (Long.parseLong(year + "0501") <= reportDate && Long.parseLong(year + "0531") >= reportDate) {
        return "CTK" + (year);
      } else if (Long.parseLong(year + "0601") <= reportDate && Long.parseLong(year + "0631") >= reportDate) {
        return "CTM" + (year);
      } else if (Long.parseLong(year + "0701") <= reportDate && Long.parseLong(year + "0731") >= reportDate) {
        return "CTN" + (year);
      } else if (Long.parseLong(year + "0801") <= reportDate && Long.parseLong(year + "1031") >= reportDate) {
        return "CTV" + (year);
      } else if (Long.parseLong(year + "1101") <= reportDate && Long.parseLong(year + "1131") >= reportDate) {
        return "CTX" + (year);
      } else if (Long.parseLong(year + "1201") <= reportDate && Long.parseLong(year + "1231") >= reportDate) {
        return "CTZ" + (year);
      }
    }
    return null;
  }

  @Override
  public DaylyRecord queryDayly(long day, Commodity commodity, Country country, String source, String condition) {

    String cmd = "";
    if ("continuecontract".equals(condition)) {
      condition = null;
      cmd = "continuecontract";
    }

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      if ("continuecontract".equals(cmd)) {
        String contract = getContractByReportDate(country, day);

        condition = "contract='" + contract + "'";
      }
      conn = getConnection();
      String D_SQL = SQL_QUERY + " where reportDate=?   ";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, country, source, condition);
      LogService.sql(Base_YieldDaySQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;

      DBUtil.setLong(ps, ++col, day);

      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        FuturePriceDay obj = new FuturePriceDay();
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
  public String[] getTables() {

    return new String[] { super.SQL_TABLE };
  }

  @Override
  public boolean hasCommodity() {

    return true;
  }

}
