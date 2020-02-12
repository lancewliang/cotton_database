package model.db;

import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.entity.DaylyRecord;

public interface DaylyQuery {
  public DaylyRecord queryDayly(long day, Commodity commodity, Country country, String source, String condition);



  public static String QUERYDATE_ORDER = " order by reportDate asc";

}
