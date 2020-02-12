package model.entity.consumption.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.db.DeleteTable;
import model.db.QuerySQLUtil;
import model.db.SaveDB;
import model.db.YearlyQuery;
import model.entity.Record;
import model.entity.YearlyRecord;
import model.entity.consumption.ConsumptionYear;
import model.entity.consumption.db.base.Base_ConsumptionYearSQL;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class ConsumptionYearSQL extends Base_ConsumptionYearSQL implements YearlyQuery, SaveDB {
  public static List<Country> getCountrys(Commodity c) {
    List<Country> list = new ArrayList<Country>();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select distinct country from " + SQL_TABLE + " where commodity = ?";

      LogService.sql(Base_ConsumptionYearSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, c.getCommodity());
      ps.executeQuery();
      rs = ps.getResultSet();
      while (rs.next()) {
        list.add(Country.getCountry(rs.getString(1)));
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
    return list;
  }

  @Override
  public YearlyRecord queryYearly(String year, Commodity commodity, Country country, String source, String condition) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + " WHERE YEAR = ? ";
      sql += QuerySQLUtil.QuerySQLString(commodity, country, source, condition);

      LogService.sql(Base_ConsumptionYearSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, year);
      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        ConsumptionYear obj = new ConsumptionYear();
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
  public boolean save(List<Record> objs) throws SQLException {
    List<ConsumptionYear> list = new ArrayList<ConsumptionYear>();
    for (Record ob : objs) {
      list.add((ConsumptionYear) ob);
    }
    return super.save(list);
  }

  @Override
  public boolean delete(List<Record> objs) throws SQLException {
    List<ConsumptionYear> list = new ArrayList<ConsumptionYear>();
    for (Record ob : objs) {
      list.add((ConsumptionYear) ob);
    }
    return super.delete(list);
  }

  @Override
  public boolean deleteAll(Commodity commodity) throws SQLException {

    return DeleteTable.deleteAll(commodity, super.SQL_TABLE);
  }
}
