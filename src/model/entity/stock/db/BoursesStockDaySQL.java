
package model.entity.stock.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;
import tcc.utils.None;
import model.constant.Commodity;
import model.constant.Country;
import model.entity.stock.BoursesStockDay;
import model.entity.stock.db.base.Base_BoursesStockDaySQL;
import model.entity.weather.db.base.Base_WeatherDaySQL;

public class BoursesStockDaySQL extends Base_BoursesStockDaySQL{

  public static int getLastDay(Commodity commodity, Country country, String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from BoursesStockDay WHERE commodity = ? and country = ? and source =? ";

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
}
