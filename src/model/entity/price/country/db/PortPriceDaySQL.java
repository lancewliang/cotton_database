package model.entity.price.country.db;

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
import model.db.DateListQueryUtil;
import model.db.DaylyQuery;
import model.db.DeleteTable;
import model.db.FieldQuery;
import model.db.QuerySQLUtil;
import model.db.QueryTable;
import model.db.SaveDB;
import model.entity.DaylyRecord;
import model.entity.Record;
import model.entity.price.country.PortPriceDay;
import model.entity.price.country.db.base.Base_PortPriceDaySQL;
import model.entity.production.country.db.base.Base_YieldDaySQL;
import model.entity.weather.db.base.Base_WeatherDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class PortPriceDaySQL extends Base_PortPriceDaySQL implements SaveDB, DaylyQuery, QueryTable, DateListQuery, FieldQuery {

  public static int getLastDay(Commodity c, String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from PortPriceDay  Where  source = ? and  Commodity=? ";

      LogService.sql(Base_WeatherDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;

      DBUtil.setString(ps, ++col, source);
      DBUtil.setString(ps, ++col, c.getCommodity());
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
    List<PortPriceDay> list = new ArrayList<PortPriceDay>();
    for (Record ob : objs) {
      PortPriceDay obj = (PortPriceDay) ob;
      PortPriceDay old = super.getObj(obj.getReportDate(), obj.getCountry(), obj.getFromCountry(), obj.getStandard(), obj.getTerm(), obj.getPortPriceType(), obj.getCommodity(), obj.getSource());

      if (old == null) {
        super.insert(obj);
      } else {
        if (obj.getValue1() <= 0) {
          obj.setValue1(old.getValue1());
          obj.setPriceUnit1(old.getPriceUnit1());
          obj.setWeightUnit1(old.getWeightUnit1());
        }
        if (obj.getValue2() <= 0) {
          obj.setValue2(old.getValue2());
          obj.setPriceUnit2(old.getPriceUnit2());
          obj.setWeightUnit2(old.getWeightUnit2());
        }
        super.update(obj);
      }

    }
    return true;
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<PortPriceDay> list = new ArrayList<PortPriceDay>();
    for (Record ob : objs) {
      list.add((PortPriceDay) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
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
  public List<ReportObjectField> queryReportObjectFields(Commodity commodity, Country country, String source, String condition) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    List<ReportObjectField> dlist = new ArrayList<ReportObjectField>();
    try {
      conn = getConnection();
      String D_SQL = "select distinct fromCountry,standard,portPriceType from " + SQL_TABLE + " where 1=1 ";
      D_SQL += QuerySQLUtil.QuerySQLString(commodity, source, condition);
      D_SQL += " order by fromCountry desc,standard desc,portPriceType desc";
      LogService.sql(Base_YieldDaySQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;

      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {
        String fromCountry = rs.getString(1);
        String standard = rs.getString(2);
        String portPriceType = rs.getString(3);

        String conditionS = condition + " fromCountry='" + fromCountry + "' and standard='" + standard + "' and ( term is null or term='') and portPriceType='" + portPriceType + "'";

        ReportObjectField field = new ReportObjectField(null, fromCountry + "-" + standard, null, conditionS, Country.getCountry(fromCountry).getDisplay() + "-" + standard + "-" + portPriceType, null, commodity.getDisplay(), country.getCountry(), source);
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
  public DaylyRecord queryDayly(long day, Commodity commodity, Country country, String source, String condition) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
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
        PortPriceDay obj = new PortPriceDay();
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

  @Override
  public String[] getTables() {

    return new String[] { super.SQL_TABLE };
  }

  @Override
  public boolean hasCommodity() {

    return true;
  }

}
