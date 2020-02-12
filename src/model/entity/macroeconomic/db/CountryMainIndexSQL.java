package model.entity.macroeconomic.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entity.macroeconomic.CountryMainIndex;
import model.entity.macroeconomic.db.base.Base_CountryMainIndexSQL;
import model.entity.weather.db.base.Base_WeatherDaySQL;
import tcc.utils.None;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;

public class CountryMainIndexSQL extends Base_CountryMainIndexSQL {

  public static List<CountryMainIndex> getNULLinferenceObjs(String country1, String country2, String source) throws SQLException {
    List<CountryMainIndex> list = new ArrayList<CountryMainIndex>();
    Connection conn = null;

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + " WHERE  ";
      sql += "inference is null  and  ";
      sql += "currency in (?, ?)  and  ";
      sql += "importance =3 order by reportDate asc,reportHour asc ";

      LogService.sql(Base_CountryMainIndexSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0; 
      DBUtil.setString(ps, ++col, country1);
      DBUtil.setString(ps, ++col, country2);
      ps.executeQuery();
      rs = ps.getResultSet();

      while (rs.next()) {
        CountryMainIndex obj = new CountryMainIndex();
        getValues(rs, obj, 0);

        list.add(obj);
      }
      return list;
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
  }

  public static List<CountryMainIndex> getObjs(String country1, String country2, String source, int date, int hour) throws SQLException{
	    List<CountryMainIndex> list = new ArrayList<CountryMainIndex>();
	    Connection conn = null;

	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    try {
	      conn = getConnection();
	      String sql = SQL_QUERY + " WHERE  ";
	      sql += "SOURCE = ?  and inference is not null and ";
	      sql += "currency in (?, ?)  and  ";
	      sql += "importance =3 ";
	      sql += " and reportDate = ? and reportHour = ? ";
	      
	      LogService.sql(Base_CountryMainIndexSQL.class, "SQL", sql);
	      ps = conn.prepareStatement(sql);
	      int col = 0;
	      DBUtil.setString(ps, ++col, source);
	      DBUtil.setString(ps, ++col, country1);
	      DBUtil.setString(ps, ++col, country2);
	      DBUtil.setLong(ps, ++col, date);
	      DBUtil.setLong(ps, ++col, hour);
	      ps.executeQuery();
	      rs = ps.getResultSet();

	      while (rs.next()) {
	        CountryMainIndex obj = new CountryMainIndex();
	        getValues(rs, obj, 0);

	        list.add(obj);
	      }
	      return list;
	    } finally {
	      DBUtil.cleanup(rs, ps, conn);
	    }
  }
  
  public static List<CountryMainIndex> getObjs(String country1, String country2, String source) throws SQLException {
    List<CountryMainIndex> list = new ArrayList<CountryMainIndex>();
    Connection conn = null;

    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = SQL_QUERY + " WHERE  ";
      sql += "SOURCE = ?  and inference is not null and ";
      sql += "currency in (?, ?)  and  ";
      sql += "importance =3 order by reportDate asc,reportHour asc ";

      LogService.sql(Base_CountryMainIndexSQL.class, "SQL", sql);
      ps = conn.prepareStatement(sql);
      int col = 0;
      DBUtil.setString(ps, ++col, source);
      DBUtil.setString(ps, ++col, country1);
      DBUtil.setString(ps, ++col, country2);
      ps.executeQuery();
      rs = ps.getResultSet();

      while (rs.next()) {
        CountryMainIndex obj = new CountryMainIndex();
        getValues(rs, obj, 0);

        list.add(obj);
      }
      return list;
    } finally {
      DBUtil.cleanup(rs, ps, conn);
    }
  }

  public static int getLastDay(String source) throws SQLException {

    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      conn = getConnection();
      String sql = "select max(reportDate) from CountryMainIndex  Where  source = ?  ";

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
