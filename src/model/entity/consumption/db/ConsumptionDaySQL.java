package model.entity.consumption.db;

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
import model.entity.DaylyRecord;
import model.entity.Record;
import model.entity.consumption.ConsumptionDay;
import model.entity.consumption.db.base.Base_ConsumptionDaySQL;
import model.entity.production.country.db.base.Base_YieldDaySQL;
import model.entity.sale.country.SaleDay;
import model.entity.sale.country.db.base.Base_SaleDaySQL;
import model.entity.weather.db.base.Base_WeatherDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class ConsumptionDaySQL extends Base_ConsumptionDaySQL implements SaveDB, DaylyQuery, DateListQuery {

  public static int getLastDay(Commodity commodity, Country country, String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from ConsumptionDay WHERE commodity = ? and country = ? and source =? ";

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
  public DaylyRecord queryDayly(long day, Commodity commodity, Country country, String source, String condition) {
    try {
      return getObjConsumptionDay(day, commodity, country, source, condition);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  public static ConsumptionDay getObjConsumptionDay(long day, Commodity commodity, Country country, String source, String condition) throws SQLException {

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
        ConsumptionDay obj = new ConsumptionDay();
        getValues(rs, obj, 0);
        return obj;
      }
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return null;
  }

  public static ConsumptionDay getObjConsumptionDayMonth(long month, Commodity commodity, Country country, String source, String condition, boolean asc) throws SQLException {
    condition = " and total>0 " + (None.isNonBlank(condition) ? (" and " + condition) : "");
    return getObjConsumptionDayByRange(Long.parseLong(month + "00"), Long.parseLong(month + "31"), commodity, country, source, condition, asc);

  }

  public static ConsumptionDay getObjConsumptionDayByRange(long startDay, long endDay, Commodity commodity, Country country, String source, String condition, boolean asc) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String D_SQL = SQL_QUERY + " where reportDate>=? and reportDate<=? ";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, country, source, condition);
      if (!asc) {
        D_SQL += "order by reportDate desc";
      }
      LogService.sql(Base_SaleDaySQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;
      DBUtil.setLong(ps, ++col, startDay);
      DBUtil.setLong(ps, ++col, endDay);

      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        ConsumptionDay obj = new ConsumptionDay();
        getValues(rs, obj, 0);
        return obj;
      }
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return null;
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
  public boolean save(List<Record> objs) throws SQLException {
    List<ConsumptionDay> list = new ArrayList<ConsumptionDay>();
    for (Record ob : objs) {
      list.add((ConsumptionDay) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<ConsumptionDay> list = new ArrayList<ConsumptionDay>();
    for (Record ob : objs) {
      list.add((ConsumptionDay) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {
    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }

}
