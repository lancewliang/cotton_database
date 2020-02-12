package export.mapping.report.dimension;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import tcc.utils.log.LogService;

import model.constant.Commodity;
import model.constant.Country;
import model.db.DateList;
import model.db.DateListQuery;
import model.db.SQLFactory;

public class TimeDimension implements ReportDimension {
  public Dimension time_dimension;
  List<DateList> list = new ArrayList<DateList>();
  static TimeZone _tz = TimeZone.getDefault();
  static Locale _lc = Locale.getDefault();

  Calendar cal = null;
  public Date reportStartDate = null;
  public Date reportEndDate = null;
  //
  boolean isQuery = false;
  List<TimeDimensionQueryCondition> conditions = new ArrayList<TimeDimensionQueryCondition>();

  public TimeDimension(Dimension type, List<TimeDimensionQueryCondition> conditionss, Date reportStartDate, Date reportEndDate) throws Exception {

    this.time_dimension = type;
    isQuery = true;
    this.conditions.addAll(conditionss);
    this.reportStartDate = reportStartDate;
    this.reportEndDate = reportEndDate;

  }

  public TimeDimension(String time_dimension, Date reportStartDate, Date reportEndDate) throws Exception {
    if ("year".equals(time_dimension)) {
      this.time_dimension = Dimension.YEAR;
    } else if ("month".equals(time_dimension)) {
      this.time_dimension = Dimension.MONTH;
    } else if ("week".equals(time_dimension)) {
      this.time_dimension = Dimension.MONTH;
    } else if ("day".equals(time_dimension)) {
      this.time_dimension = Dimension.DAY;
    } else {
      throw new Exception("not found time_dimension:" + time_dimension);
    }
    cal = new GregorianCalendar(_tz, _lc);
    this.reportStartDate = reportStartDate;
    if (reportEndDate == null) {
      this.reportEndDate = new Date();
    } else {
      this.reportEndDate = reportEndDate;
    }

  }

  public List<DateList> getTimeList() {
    return list;
  }

  SimpleDateFormat sdfmd1 = new SimpleDateFormat("yyyyMMdd");
  SimpleDateFormat sdfmd2 = new SimpleDateFormat("yyyy/MM/dd");
  SimpleDateFormat sdfmm1 = new SimpleDateFormat("yyyyMM");
  SimpleDateFormat sdfmm2 = new SimpleDateFormat("yyyy/MM");

  public String getDateString(Long date) {
    try {
      if (date == null)
        return null;
      if (time_dimension.equals(Dimension.DAY)) {
        return sdfmd2.format(sdfmd1.parse(date.longValue() + ""));
      } else if (time_dimension.equals(Dimension.YEAR)) {
        return "" + date.longValue();
      } else if (time_dimension.equals(Dimension.MONTH)) {
        return sdfmm2.format(sdfmm1.parse(date.longValue() + ""));
      }
    } catch (Exception e) {
      LogService.trace(e, date.longValue() + "");
    }
    return null;
  }

  public void initTimeList(Commodity fCommodity, Country fCountry) {
    list.clear();
    if (isQuery) {
      if (time_dimension.equals(Dimension.DAY)) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");

        for (TimeDimensionQueryCondition tqc : conditions) {
          DateListQuery query = (DateListQuery) SQLFactory.getDaylyQuery(tqc.qModel);

          List<DateList> dates = query.queryDays(tqc.fCommodity != null ? tqc.fCommodity : fCommodity, fCountry, tqc.qSource, tqc.qCondition, reportStartDate != null ? Long.parseLong(sdf1.format(reportStartDate)) : -1, reportEndDate != null ? Long.parseLong(sdf1.format(reportEndDate)) : -1);
          for (DateList d : dates) {
            boolean isame = false;
            for (DateList l : list) {
              if (l.isSameCondition(d)) {
                isame = true;
                l.addDates(d.getDates());
              }
            }
            if (!isame) {
              list.add(d);
            }
          }

        }
      } else if (time_dimension.equals(Dimension.MONTH)) {
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
        for (TimeDimensionQueryCondition tqc : conditions) {
          DateListQuery query = (DateListQuery) SQLFactory.getMonthlyQuery(tqc.qModel);

          List<DateList> dates = query.queryDays(tqc.fCommodity != null ? tqc.fCommodity : fCommodity, fCountry, tqc.qSource, tqc.qCondition, reportStartDate != null ? Long.parseLong(sdf1.format(reportStartDate)) : -1, reportEndDate != null ? Long.parseLong(sdf1.format(reportEndDate)) : -1);
          for (DateList d : dates) {
            boolean isame = false;
            for (DateList l : list) {
              if (l.isSameCondition(d)) {
                isame = true;
                l.addDates(d.getDates());
              }
            }
            if (!isame) {
              list.add(d);
            }
          }
        }
      }
    } else {
      if (time_dimension.equals(Dimension.YEAR)) {
        List<Long> timelist = new ArrayList<Long>();
        for (int i = 7; i >= 0; i--) {
          cal.setTime(reportEndDate);
          cal.add(Calendar.YEAR, -i);
          timelist.add((long) cal.get(Calendar.YEAR));
        }
        list.add(new DateList(timelist));
      } else if (time_dimension.equals(Dimension.MONTH)) {
        List<Long> timelist = new ArrayList<Long>();
        for (int i = 12 * 4; i >= 0; i--) {
          cal.setTime(reportEndDate);
          cal.add(Calendar.MONTH, -i);
          timelist.add((long) (cal.get(Calendar.YEAR) * 100 + (cal.get(Calendar.MONTH) + 1)));
        }
        list.add(new DateList(timelist));
      } else if (time_dimension.equals(Dimension.WEEK)) {

      } else if (time_dimension.equals(Dimension.DAY)) {
        List<Long> timelist = new ArrayList<Long>();
        if (reportStartDate != null) {
          cal.setTime(reportStartDate);

        } else {
          cal.setTime(reportEndDate);
          cal.add(Calendar.DATE, -(365 * 2));
        }
        for (int i = 0; i <= 365 * 2; i++) {
          cal.add(Calendar.DATE, 1);
          timelist.add((long) (cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DATE)));

          if (reportEndDate.before(cal.getTime())) {
            break;

          }
        }
        list.add(new DateList(timelist));
      }
    }
  }

  public enum Dimension {
    YEAR, MONTH, WEEK, DAY;
  }

}
