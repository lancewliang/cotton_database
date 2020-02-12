package model.entity.sale.country.db;

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
import model.entity.sale.country.SaleDay;
import model.entity.sale.country.db.base.Base_SaleDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class SaleDaySQL extends Base_SaleDaySQL implements SaveDB, DaylyQuery, DateListQuery {

  @Override
  public List<DateList> queryDays(Commodity commodity, Country country, String source, String condition, long reportStartDate, long reportEndDate) {
    try {
      return DateListQueryUtil.queryDays(getConnection(), SQL_TABLE, commodity, country, source, condition, reportStartDate, reportEndDate);
    } catch (SQLException e) {
      LogService.trace(e, null);
    }
    return null;
  }

  public static SaleDay getObjSaleDay(long day, Commodity commodity, Country country, String source, String condition) throws SQLException {
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
        SaleDay obj = new SaleDay();
        getValues(rs, obj, 0);
        return obj;
      }
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return null;
  }

  public static SaleDay getObjSaleDayByMonth(long month, Commodity commodity, Country country, String source, String condition, boolean asc) throws SQLException {
    condition = " and total>0 " + (None.isNonBlank(condition) ? (" and " + condition) : "");
    return getObjSaleDayByRange(Long.parseLong(month + "00"), Long.parseLong(month + "31"), commodity, country, source, condition, asc);

  }

  public static SaleDay getObjSaleDayByRange(long startDay, long endDay, Commodity commodity, Country country, String source, String condition, boolean asc) throws SQLException {

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
        SaleDay obj = new SaleDay();
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
    List<SaleDay> list = new ArrayList<SaleDay>();
    for (Record ob : objs) {
      list.add((SaleDay) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<SaleDay> list = new ArrayList<SaleDay>();
    for (Record ob : objs) {
      list.add((SaleDay) ob);
    }
    return super.delete(list);
  }

  @Override
  public DaylyRecord queryDayly(long day, Commodity commodity, Country country, String source, String condition) {

    try {
      return getObjSaleDay(day, commodity, country, source, condition);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }
}
