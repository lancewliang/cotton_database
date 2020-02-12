package model.db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;

public class DateListQueryUtil {
  public static List<DateList> queryDays(Connection conn, String SQL_TABLE, Commodity commodity, Country country, String source, String condition, long reportStartDate, long reportEndDate) {
    List<DateList> ret = new ArrayList<DateList>();
    try {
      List<Long> dates = QuerySQLUtil.queryDays(conn, SQL_TABLE, commodity, country, source, condition, reportStartDate, reportEndDate);
      ret.add(new DateList(dates));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return ret;
  }
}
