package model.entity.price.country.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

import model.constant.Commodity;
import model.db.DeleteTable;
import model.db.SaveDB;
import model.entity.Record;
import model.entity.price.country.CountryPriceDay;
import model.entity.price.country.ExchangeRateDay;
import model.entity.price.country.db.base.Base_ExchangeRateDaySQL;
import model.entity.weather.db.base.Base_WeatherDaySQL;

public class ExchangeRateDaySQL extends Base_ExchangeRateDaySQL implements SaveDB {
  @Override
  public boolean save(List<Record> objs) throws SQLException {
    List<ExchangeRateDay> list = new ArrayList<ExchangeRateDay>();
    for (Record ob : objs) {
      list.add((ExchangeRateDay) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<ExchangeRateDay> list = new ArrayList<ExchangeRateDay>();
    for (Record ob : objs) {
      list.add((ExchangeRateDay) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(null, super.SQL_TABLE);
  }

  public static int getLastDay(String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from ExchangeRateDay  Where   source = ?  ";

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
}
