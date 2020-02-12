package export.mapping.excell;

import java.util.List;

import model.constant.Commodity;
import model.constant.Country;
import model.db.DateList;
import model.db.DaylyQuery;
import model.db.MonthlyQuery;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.db.SQLFactory;
import model.db.YearlyQuery;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import tcc.utils.None;
import tcc.utils.collections.UnguardedHashtable;
import tcc.utils.log.LogService;
import export.mapping.report.DimensionReportMapping;
import export.mapping.report.dimension.ModeDimension;
import export.mapping.report.dimension.TimeDimension;
import export.mapping.report.dimension.TimeDimension.Dimension;
import export.mapping.report.field.Chart;
import export.mapping.report.field.ReportExpressionField;
import export.mapping.report.field.ReportField;
import export.mapping.report.field.ReportModeDimensionField;
import export.mapping.report.field.ReportObjectField;
import export.mapping.report.field.ReportTimeDimensionField;

public class FormatExcellSheet {

  public static void doPage(DimensionReportMapping mapping, Sheet sheet, Commodity gCommodity, Country gCountry, List<ReportField> fileds, List<Chart> charts, TimeDimension timeDimension, List<DateList> timeLists, QueryUnit unit, UnguardedHashtable cachepool) throws Exception {

    int rowIndex = 0;

    if (rowIndex == 0) {
      Row row = sheet.createRow(rowIndex);
      int x = 0;
      for (Chart chart : charts) {
        Cell cell = row.createCell(x);
        cell.setCellValue(chart.getOutputValue());
        x++;
      }
    }
    rowIndex++;
    if (rowIndex == 1) {
      Row row = sheet.createRow(rowIndex);
      for (ReportField field : fileds) {
        if (field instanceof ReportModeDimensionField) {
          ReportModeDimensionField modeField = (ReportModeDimensionField) field;
          ModeDimension modleDimension = mapping.modeDimensionMap.get(modeField.getModel());
          List<ReportObjectField> sfileds = modleDimension.getReportObjectFields(modeField.getCol());
          for (ReportField sfield : sfileds) {
            try {
              Cell cell = row.createCell(sfield.getColIndex());
              cell.setCellValue(sfield.getColDisplayLabel(timeDimension.time_dimension));
            } catch (Exception e) {
              throw e;
            }
          }
        } else {
          Cell cell = row.createCell(field.getColIndex());
          cell.setCellValue(field.getColDisplayLabel(timeDimension.time_dimension));
        }
      }
    }
    rowIndex++;
    for (DateList timeList : timeLists) {
      if (timeLists.size() > 1 && None.isNonBlank(timeList.getCondition())) {
        Row row = sheet.createRow(rowIndex);
        Cell cell = row.createCell(0);
        cell.setCellValue(timeList.getConditionlabel());
        rowIndex++;
      }

      for (Long time : timeList.getDates()) {
        Row row = sheet.createRow(rowIndex);
        for (ReportField field : fileds) {
          if (field instanceof ReportModeDimensionField) {
            ReportModeDimensionField modeField = (ReportModeDimensionField) field;
            ModeDimension modleDimension = mapping.modeDimensionMap.get(modeField.getModel());
            List<ReportObjectField> sfileds = modleDimension.getReportObjectFields(modeField.getCol());
            for (ReportField sfield : sfileds) {
              doPageField(sheet, gCommodity, gCountry, sfield, row, timeDimension, timeList, time, unit, cachepool);
            }

          } else {
            doPageField(sheet, gCommodity, gCountry, field, row, timeDimension, timeList, time, unit, cachepool);
          }
        }
        rowIndex++;
      }
    }
  }

  private static void doPageField(Sheet sheet, Commodity gCommodity, Country gCountry, ReportField field, Row row, TimeDimension timeDimension, DateList timeList, Long time, QueryUnit unit, UnguardedHashtable cachepool) {
    Cell cell = null;
    if (field instanceof ReportTimeDimensionField) {
      cell = row.createCell(field.getColIndex());
      cell.setCellValue(timeDimension.getDateString(time));
    } else if (field instanceof ReportExpressionField) {
      cell = row.createCell(field.getColIndex());
      String value = ((ReportExpressionField) field).getRTMValue(row);
      if (None.isNonBlank(value))
        cell.setCellFormula(value);
    } else if (field instanceof ReportObjectField) {
      cell = row.createCell(field.getColIndex());
      ReportObjectField objField = (ReportObjectField) field;
      Object obj = null;
      String condition = objField.condition;
      if (None.isNonBlank(timeList.getCondition())) {
        if (!timeList.getCondition().trim().startsWith("and") && None.isNonBlank(condition)) {
          condition += " and ";
        }
        condition += timeList.getCondition();
      }
      String source = objField.source;
      Commodity fCommodity = objField.getCommodity() != null ? objField.getCommodity() : gCommodity;
      Country fCountry = objField.getCountry() != null ? objField.getCountry() : gCountry;
      if (timeDimension.time_dimension.equals(Dimension.MONTH)) {
        MonthlyQuery query = SQLFactory.getMonthlyQuery(objField.model);
        if (query == null) {
          LogService.err("query is null:" + objField.model);
        } else {
          obj = query.queryMonthly(time, fCommodity, fCountry, source, condition);
        }
      } else if (timeDimension.time_dimension.equals(Dimension.YEAR)) {
        YearlyQuery query = SQLFactory.getYearlyQuery(objField.model);
        if (query == null) {
          LogService.err("query is null:" + objField.model);
        } else {
          obj = query.queryYearly(time.toString(), fCommodity, fCountry, source, condition);
        }
      } else if (timeDimension.time_dimension.equals(Dimension.DAY)) {
        DaylyQuery query = SQLFactory.getDaylyQuery(objField.model);
        if (query == null) {
          LogService.err("query is null:" + objField.model);
        } else {
          String key = keyCacheKey(query, time, fCommodity, fCountry, source, condition);
          obj = cachepool.get(key);
          if (obj == null) {
            obj = query.queryDayly(time, fCommodity, fCountry, source, condition);
            if (obj != null) {
              cachepool.put(key, obj);
            }
          }
        }
      }
      if (obj != null && obj instanceof ResetUnit) {
        ((ResetUnit) obj).reSetUnit(unit);
      }

      objField.getObjectAttr(obj, cell);

    }
  }

  private static String keyCacheKey(DaylyQuery query, long day, Commodity commodity, Country country, String source, String condition) {
    return query.toString() + "|" + day + "|" + (commodity != null ? commodity.getCommodity() : "") + "|" + (country != null ? country.getCountry() : "") + "|" + source + "|" + condition;
  }

}
