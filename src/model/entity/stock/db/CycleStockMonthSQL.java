package model.entity.stock.db;

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
import model.constant.Country;
import model.db.DeleteTable;
import model.db.SaveDB;
import model.entity.Record;
import model.entity.stock.CycleStockMonth;
import model.entity.stock.db.base.Base_CycleStockMonthSQL;
import model.entity.weather.db.base.Base_WeatherDaySQL;

public class CycleStockMonthSQL extends Base_CycleStockMonthSQL implements SaveDB {

  public static int getLastDay(Commodity commodity, Country country, String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from CycleStockMonth WHERE commodity = ? and country = ? and source =? ";

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
  public boolean save(List<Record> objs) throws SQLException {
    List<CycleStockMonth> list = new ArrayList<CycleStockMonth>();
    for (Record ob : objs) {
      list.add((CycleStockMonth) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<CycleStockMonth> list = new ArrayList<CycleStockMonth>();
    for (Record ob : objs) {
      list.add((CycleStockMonth) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }
}
