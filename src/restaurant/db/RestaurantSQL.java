package restaurant.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;
import tcc.utils.None;
import restaurant.obj.Restaurant;
import restaurant.db.base.Base_RestaurantSQL;

public class RestaurantSQL extends Base_RestaurantSQL {

  public static List<Restaurant> getObjsByID(List<String> keyID) throws SQLException {
    Connection conn = null;
    try {
      conn = getConnection();
      List<Restaurant> list = new ArrayList<Restaurant>();

      PreparedStatement ps = null;
      ResultSet rs = null;
      try {

        String sql = "SELECT " + SQL_COLUMS + " FROM " + SQL_TABLE + " WHERE sd.KEYID in (0";
        for (int i = 0; i < keyID.size(); i++) {
          sql += ",?";
        }
        sql += ")";
        LogService.sql(Base_RestaurantSQL.class, "SQL", sql);
        ps = conn.prepareStatement(sql);
        int col = 0;
        for (int i = 0; i < keyID.size(); i++) {
          DBUtil.setString(ps, ++col, keyID.get(i));
        }
        ps.executeQuery();
        rs = ps.getResultSet();

        while (rs.next()) {
          Restaurant obj = new Restaurant();
          int rcol = getValues(rs, obj, 0);
          List<String> ids = getMappingIds(obj.getKeyID());
          long salesvalue = setSalesValue(ids);
          obj.setSalesvalue(salesvalue);
          list.add(obj);
        }
        return list;
      } finally {
        DBUtil.cleanup(rs, ps, null);
      }
    } finally {
      DBUtil.cleanup(null, null, conn);
    }
  }

  public static List<String> getMappingIds(String keyID) throws SQLException {
    Connection conn = null;
    try {
      conn = getConnection();
      List<String> list = new ArrayList<String>();
      list.add(keyID);
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {

        String sql = "SELECT sdr.ele,sdr.meituan,sdr.baidu FROM restaurantmapping sdr where sdr.ele= ? or sdr.meituan= ? or sdr.baidu = ?";
 
        LogService.sql(Base_RestaurantSQL.class, "SQL", sql);
        ps = conn.prepareStatement(sql);
        int col = 0;

        DBUtil.setString(ps, ++col, keyID);
        DBUtil.setString(ps, ++col, keyID);
        DBUtil.setString(ps, ++col, keyID);

        ps.executeQuery();
        rs = ps.getResultSet();

        while (rs.next()) {
          String r1 = rs.getString(1);
          String r2 = rs.getString(2);
          String r3 = rs.getString(3);
          if (None.isNonBlank(r1))
            list.add(r1);
          if (None.isNonBlank(r2))
            list.add(r2);
          if (None.isNonBlank(r3))
            list.add(r3);

        }
        return list;
      } finally {
        DBUtil.cleanup(rs, ps, null);
      }
    } finally {
      DBUtil.cleanup(null, null, conn);
    }
  }

  public static long setSalesValue(List<String> keyID) throws SQLException {
    Connection conn = null;
    try {
      conn = getConnection();

      PreparedStatement ps = null;
      ResultSet rs = null;
      try {

        String sql = "SELECT sdr.recent_order_num,sdr.minimum_order_amount FROM restaurantrecordlast sdr where sdr.KEYID in (0";
        for (int i = 0; i < keyID.size(); i++) {
          sql += ",?";
        }
        sql += ")";
        LogService.sql(Base_RestaurantSQL.class, "SQL", sql);
        ps = conn.prepareStatement(sql);
        int col = 0;
        for (int i = 0; i < keyID.size(); i++) {
          DBUtil.setString(ps, ++col, keyID.get(i));
        }

        ps.executeQuery();
        rs = ps.getResultSet();
        int ret = 0;
        while (rs.next()) {

          int recent_order_num = rs.getInt(1);

          int minimum_order_amount = rs.getInt(2);
          if (minimum_order_amount == 0)
            minimum_order_amount = 20;
          ret += recent_order_num * minimum_order_amount;

        }
        return ret;
      } finally {
        DBUtil.cleanup(rs, ps, null);
      }
    } finally {
      DBUtil.cleanup(null, null, conn);
    }
  }

  public static List<Restaurant> getObjsBySQL(String sql) throws SQLException {
    Connection conn = null;
    try {
      conn = getConnection();
      List<Restaurant> list = new ArrayList<Restaurant>();

      PreparedStatement ps = null;
      ResultSet rs = null;
      try {

        LogService.sql(Base_RestaurantSQL.class, "SQL", sql);
        ps = conn.prepareStatement(sql);

        ps.executeQuery();
        rs = ps.getResultSet();

        while (rs.next()) {
          Restaurant obj = new Restaurant();
          getValues(rs, obj, 0);

          list.add(obj);
        }
        return list;
      } finally {
        DBUtil.cleanup(rs, ps, null);
      }
    } finally {
      DBUtil.cleanup(null, null, conn);
    }
  }
}
