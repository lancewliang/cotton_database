package model.entity.custom.country;

import model.entity.MonthlyRecord;

public class ExportMonth extends ImportExportMonth implements MonthlyRecord {
  String s = null;

  public ExportMonth(ImportExportMonth monthinfo) {
    this.setCommodity(monthinfo.getCommodity());
    this.setReportDate(monthinfo.getReportDate());
    this.setToCountry(monthinfo.getToCountry());
    this.setValue(monthinfo.getValue());
    this.setWeightUnit(monthinfo.getWeightUnit());
    s = "1";
  }

  public ExportMonth(ImportExportDay monthinfo, long month) {
    this.setCommodity(monthinfo.getCommodity());
    this.setReportDate(month);
    this.setToCountry(monthinfo.getToCountry());

    this.setWeightUnit(monthinfo.getWeightUnit());
    s = "2";
  }
}
