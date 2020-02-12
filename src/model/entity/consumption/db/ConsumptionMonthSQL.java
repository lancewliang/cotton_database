package model.entity.consumption.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.db.DateList;
import model.db.DateListQuery;
import model.db.DateListQueryUtil;
import model.db.MonthlyQuery;
import model.db.QuerySQLUtil;
import model.entity.MonthlyRecord;
import model.entity.consumption.ConsumptionDay;
import model.entity.consumption.ConsumptionMonth;
import model.entity.consumption.db.base.Base_ConsumptionMonthSQL;
import model.entity.sale.country.db.SaleMonthSQL;
import model.entity.weather.db.base.Base_WeatherDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class ConsumptionMonthSQL extends Base_ConsumptionMonthSQL implements MonthlyQuery, DateListQuery {

  public static int getLastDay(Commodity commodity, Country country, String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from ConsumptionMonth WHERE commodity = ? and country = ? and source =? ";

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
    ConsumptionMonth monthObj = queryMonthlyObj(month, commodity, country, source, condition);
    try {
      if (monthObj == null) {
        long nextMonth = getNextMonth("" + month);
        ConsumptionDay day1 = ConsumptionDaySQL.getObjConsumptionDay(Long.parseLong(month + "01"), commodity, country, source, condition);
        ConsumptionDay day2 = ConsumptionDaySQL.getObjConsumptionDay(Long.parseLong(nextMonth + "01"), commodity, country, source, condition);
        if (day1 != null && day2 != null) {
          if (day2.getTotal() <= 0) {
            day2 = ConsumptionDaySQL.getObjConsumptionDayMonth(month, commodity, country, source, condition, false);
          }
        }
        if (day1 != null && day2 != null && day1.getReportDate() != day2.getReportDate() && day2.getTotal() >= 0) {

          monthObj = new ConsumptionMonth();
          monthObj.setReportDate(month);
          monthObj.setCommodity(commodity);
          monthObj.setCountry(country);
          monthObj.setSource(source);
          monthObj.setWeightUnit(day1.getWeightUnit());
          double d1 = day1.getTotal() <= 0 ? 0 : day1.getTotal();
          double d2 = day2.getTotal() <= 0 ? 0 : day2.getTotal();
          monthObj.setValue(d2 - d1);

        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }

    return monthObj;
  }

  public ConsumptionMonth queryMonthlyObj(long month, Commodity commodity, Country country, String source, String condition) {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String D_SQL = SQL_QUERY + " where reportDate=? ";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, country, source, condition);
      LogService.sql(SaleMonthSQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;
      DBUtil.setLong(ps, ++col, month);
      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        ConsumptionMonth obj = new ConsumptionMonth();
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

  SimpleDateFormat df = new SimpleDateFormat("yyyyMM");

  private long getNextMonth(String m) throws ParseException {

    df.parse(m);
    Calendar cal = df.getCalendar();
    cal.add(Calendar.MONTH, 1);

    return Long.parseLong(df.format(cal.getTime()));
  }

}
