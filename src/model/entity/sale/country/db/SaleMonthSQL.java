package model.entity.sale.country.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.db.DeleteTable;
import model.db.MonthlyQuery;
import model.db.QuerySQLUtil;
import model.db.SaveDB;
import model.entity.MonthlyRecord;
import model.entity.Record;
import model.entity.sale.country.SaleDay;
import model.entity.sale.country.SaleMonth;
import model.entity.sale.country.db.base.Base_SaleMonthSQL;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class SaleMonthSQL extends Base_SaleMonthSQL implements SaveDB, MonthlyQuery {
  @Override
  public boolean save(List<Record> objs) throws SQLException {
    List<SaleMonth> list = new ArrayList<SaleMonth>();
    for (Record ob : objs) {
      list.add((SaleMonth) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<SaleMonth> list = new ArrayList<SaleMonth>();
    for (Record ob : objs) {
      list.add((SaleMonth) ob);
    }
    return super.delete(list);
  }

  @Override
  public MonthlyRecord queryMonthly(long month, Commodity commodity, Country country, String source, String condition) {
    SaleMonth monthObj = queryMonthlyObj(month, commodity, country, source, condition);
    try {
      if (month == 201109) {
        System.out.println("asdf");
      }
      if (monthObj == null) {
        long nextMonth = getNextMonth("" + month);
        SaleDay day1 = SaleDaySQL.getObjSaleDay(Long.parseLong(month + "01"), commodity, country, source, condition);
        SaleDay day2 = SaleDaySQL.getObjSaleDay(Long.parseLong(nextMonth + "01"), commodity, country, source, condition);
        if (day1 != null && day2 != null) {
          if (day2.getTotal() <= 0) {
            day2 = SaleDaySQL.getObjSaleDayByMonth(month, commodity, country, source, condition, false);
          }
        }
        if (day1 != null && day2 != null && day1.getReportDate() != day2.getReportDate() && day2.getTotal() >= 0) {

          monthObj = new SaleMonth();
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
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return monthObj;
  }

  private long getNextMonth(String m) throws ParseException {
    SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
    Date d = df.parse(m);
    Calendar cal = df.getCalendar();
    cal.setTime(d);
    cal.add(Calendar.MONTH, 1);

    return Long.parseLong(df.format(cal.getTime()));
  }

  public SaleMonth queryMonthlyObj(long month, Commodity commodity, Country country, String source, String condition) {

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
        SaleMonth obj = new SaleMonth();
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
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }
}
