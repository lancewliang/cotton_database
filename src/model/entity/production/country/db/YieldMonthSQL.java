package model.entity.production.country.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.db.MonthlyQuery;
import model.db.QueryUnit;
import model.entity.MonthlyRecord;
import model.entity.production.country.YieldDay;
import model.entity.production.country.YieldMonth;

public class YieldMonthSQL implements MonthlyQuery {

  @Override
  public MonthlyRecord queryMonthly(long month, Commodity commodity, Country country, String source, String condition) {
    YieldMonth monthObj = null;
    try {
      QueryUnit unit = new QueryUnit();
        unit.weightUnit = WeightUnit.getWeightUnit("ถึ");
      long nextMonth = getNextMonth("" + month);
      YieldDay day1 = YieldDaySQL.getObjYieldDayByMonth(month, commodity, country, source, condition, true);
      YieldDay day2 = YieldDaySQL.getObjYieldDayByMonth(nextMonth, commodity, country, source, condition, true);
      if (day1 != null && !(day2 != null && day2.getTotal() > 0)) {

        day2 = YieldDaySQL.getObjYieldDayByMonth(month, commodity, country, source, condition, false);

      }
      if (day1 != null && day2 != null && day1.getReportDate() != day2.getReportDate() && day2.getTotal() > 0) {
        day1.reSetUnit(unit);
        day2.reSetUnit(unit);
        monthObj = new YieldMonth();
        monthObj.setReportDate(month);
        monthObj.setCommodity(commodity);
        monthObj.setCountry(country);
        monthObj.setSource(source);
        monthObj.setWeightUnit(unit.weightUnit);
        double d1 = day1.getTotal() <= 0 ? 0 : day1.getTotal();
        double d2 = day2.getTotal() <= 0 ? 0 : day2.getTotal();

        monthObj.setValue(d2 - d1);
        monthObj.setMovingAnnualTotal(d2);

      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return monthObj;
  }

  private long getLastMonth(String m) throws ParseException {
    SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
    Date d = df.parse(m);
    Calendar cal = df.getCalendar();
    cal.setTime(d);
    cal.add(Calendar.MONTH, -1);

    return Long.parseLong(df.format(cal.getTime()));
  }

  private long getNextMonth(String m) throws ParseException {
    SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
    Date d = df.parse(m);
    Calendar cal = df.getCalendar();
    cal.setTime(d);
    cal.add(Calendar.MONTH, 1);

    return Long.parseLong(df.format(cal.getTime()));
  }

}
