package model.entity.weather.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.db.DateListQuery;
import model.db.DaylyQuery;
import model.db.QuerySQLUtil;
import model.db.QueryTable;
import model.entity.DaylyRecord;
import model.entity.production.country.db.base.Base_YieldDaySQL;
import model.entity.weather.WeatherDay;
import model.entity.weather.db.base.Base_WeatherDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class WeatherDaySQL extends Base_WeatherDaySQL implements DaylyQuery, QueryTable {

  public static int getLastMonth(model.constant.WeatherRegion weatherRegion) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from WeatherDay WHERE WEATHERREGION = ? and not(forecast is not null and forecast!='') ";

      LogService.sql(Base_WeatherDaySQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, weatherRegion.getWeatherRegion());

      ps.executeQuery();
      rs = ps.getResultSet();

      if (rs.next()) {
        String str = rs.getString(1);
        if (None.isNonBlank(str))
          return Integer.parseInt(str.substring(0, 6));
      }
      return 0;
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
  }

  @Override
  public String[] getTables() {
    // TODO Auto-generated method stub
    return new String[] { super.SQL_TABLE };
  }

  @Override
  public boolean hasCommodity() {

    return false;
  }

  @Override
  public DaylyRecord queryDayly(long day, Commodity commodity, Country country, String source, String condition) {
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String D_SQL = SQL_QUERY + " where reportDate=?   ";
      D_SQL += QuerySQLUtil.QuerySQLString(source, condition);
      LogService.sql(Base_YieldDaySQL.class, "SQL", D_SQL);
      ps = conn.prepareStatement(D_SQL);
      int col = 0;
      DBUtil.setLong(ps, ++col, day);
      ps.executeQuery();
      rs = ps.getResultSet();
      if (rs.next()) {
        WeatherDay obj = new WeatherDay();
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

}
