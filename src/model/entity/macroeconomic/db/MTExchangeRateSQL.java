package model.entity.macroeconomic.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entity.macroeconomic.db.base.Base_CountryMainIndexSQL;
import model.entity.price.country.ExchangeRateUnit;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;
import tutami.fw.DB;

public class MTExchangeRateSQL {
  protected static javax.sql.DataSource TTDB;
  static {
    TTDB = DB.getDBPoolADMIN();
  }

  public static List<ExchangeRateUnit> getObjs(long date, long hour, String table, String fromCurreny, String toCurreny, int r) throws SQLException {
    List<ExchangeRateUnit> list = new ArrayList<ExchangeRateUnit>();
    Connection conn = null;

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = TTDB.getConnection();
      String sql = "select date,time,open,high,low,close,volume from " + table + " WHERE  ";
      sql += "date >= ?  and  ";
      sql += "time >= ?    ";
      sql += " order by date asc,time asc ";

      LogService.sql(Base_CountryMainIndexSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setLong(ps, ++col, date);
      DBUtil.setLong(ps, ++col, hour);
      ps.executeQuery();
      rs = ps.getResultSet();
      int i = 0;
      while (rs.next()) {
        ExchangeRateUnit obj = new ExchangeRateUnit();
        obj.setFromCurreny(fromCurreny);
        obj.setReportDate(rs.getLong(1));
        obj.setReportHour(rs.getLong(2));
        obj.setOpeningValue(rs.getLong(3));
        obj.setTopValue(rs.getLong(4));
        obj.setMinimumValue(rs.getLong(5));
        obj.setClosingValue(rs.getLong(6));
        obj.setVolumes(rs.getLong(7));
        obj.setToCurreny(toCurreny);

        if (i > r)
          break;
        list.add(obj);
        i++;
      }
      return list;
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
  }
}
