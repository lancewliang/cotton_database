package model.entity.custom.country.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import export.mapping.report.field.ReportObjectField;

import model.constant.Commodity;
import model.constant.Country;
import model.db.DateList;
import model.db.DateListQuery;
import model.db.FieldQuery;
import model.db.MonthlyQuery;
import model.db.QueryTable;
import model.db.YearlyQuery;
import model.entity.MonthlyRecord;
import model.entity.YearlyRecord;
import model.entity.custom.country.ImportExportDay;
import model.entity.custom.country.ImportExportMonth;
import model.entity.custom.country.ImportMonth;
import model.entity.custom.country.ImportYear;
import tcc.utils.None;
import tcc.utils.log.LogService;

public class ImportSQL implements YearlyQuery, MonthlyQuery, QueryTable, DateListQuery, FieldQuery {
  String countrycondition = " toCountry=?";

  @Override
  public MonthlyRecord queryMonthly(long month, Commodity commodity, Country country, String source, String condition) {
    ImportMonth monthly = null;
    ImportExportMonth monthinfo = ImportExportMonthSQL.queryMonthly(month, commodity, countrycondition, country, source, condition);
    try {

      if (monthinfo != null) {
        monthly = new ImportMonth(monthinfo);

      } else {

        long nextMonth = getNextMonth("" + month);
        ImportExportDay day1 = ImportExportDaySQL.getObjImportExportDayByMonth(month, commodity, countrycondition, country, source, condition, true);
        ImportExportDay day2 = ImportExportDaySQL.getObjImportExportDayByMonth(nextMonth, commodity, countrycondition, country, source, condition, true);
        if (day1 != null && !(day2 != null && day2.getTotal() > 0)) {

          day2 = ImportExportDaySQL.getObjImportExportDayByMonth(month, commodity, countrycondition, country, source, condition, false);

        }
        if (day1 != null && day2 != null && day1.getReportDate() != day2.getReportDate() && day2.getTotal() > 0) {
          monthly = new ImportMonth(day1, month);

          double d1 = day1.getTotal() <= 0 ? 0 : day1.getTotal();
          double d2 = day2.getTotal() <= 0 ? 0 : day2.getTotal();

          monthly.setValue(d2 - d1);

        }

      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return monthly;

  }

  @Override
  public List<ReportObjectField> queryReportObjectFields(Commodity commodity, Country country, String source, String condition) {
    return ImportExportMonthSQL.queryReportObjectFields(commodity, country, source, "fromCountry", countrycondition, condition);

  }

  @Override
  public List<DateList> queryDays(Commodity commodity, Country country, String source, String condition, long reportStartDate, long reportEndDate) {
    return ImportExportMonthSQL.queryDays(commodity, country, source, countrycondition, condition, reportStartDate, reportEndDate);

  }

  @Override
  public YearlyRecord queryYearly(String year, Commodity commodity, Country country, String source, String condition) {
    List<ImportExportMonth> list1 = ImportExportMonthSQL.queryYearly(year, commodity, countrycondition, country, source, condition);
    double totalValue = 0;
    ImportYear yearly = null;
    try {
      if (!None.isEmpty(list1)) {
        yearly = new ImportYear();
        for (ImportExportMonth im : list1) {
          totalValue += im.getValue();
        }
        yearly.setCommodity(commodity);
        yearly.setYear(year);
        yearly.setToCountry(country);
        yearly.setValue(totalValue);
        yearly.setWeightUnit(list1.get(0).getWeightUnit());
      } else {
        ImportExportDay day1 = ImportExportDaySQL.getObjImportExportDayByYear(year, commodity, countrycondition, country, source, condition);
        if (day1 != null) {
          yearly = new ImportYear();
          yearly.setCommodity(commodity);
          yearly.setYear(year);
          yearly.setToCountry(country);
          yearly.setValue(day1.getTotal());
          yearly.setWeightUnit(day1.getWeightUnit());
        }
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return yearly;
  }

  @Override
  public String[] getTables() {
    return new String[] { ImportExportDaySQL.SQL_TABLE, ImportExportMonthSQL.SQL_TABLE };

  }

  @Override
  public boolean hasCommodity() {

    return true;
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
