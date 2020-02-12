package model.db;

import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.constant.Country;

public interface DateListQuery {
  public List<DateList> queryDays(Commodity commodity, Country country, String source, String condition, long reportStartDate, long reportEndDate);

}
