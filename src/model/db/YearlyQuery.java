package model.db;

import model.constant.Commodity;
import model.constant.Country;
import model.entity.YearlyRecord;

public interface YearlyQuery {
  public YearlyRecord queryYearly(String year, Commodity commodity, Country country, String source, String condition);
 
}
