package model.db;

import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.entity.MonthlyRecord;

public interface MonthlyQuery {
  public MonthlyRecord queryMonthly(long month, Commodity commodity, Country country, String source, String condition);
  
}
