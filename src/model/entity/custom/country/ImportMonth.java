package model.entity.custom.country;

import model.entity.MonthlyRecord;

public class ImportMonth extends ImportExportMonth implements MonthlyRecord {
  public ImportMonth(ImportExportMonth monthinfo) {
    this.setCommodity(monthinfo.getCommodity());
    this.setReportDate(monthinfo.getReportDate());
    this.setToCountry(monthinfo.getToCountry());
    this.setValue(monthinfo.getValue());
    this.setWeightUnit(monthinfo.getWeightUnit());
  }

  public ImportMonth(ImportExportDay monthinfo, long month) {
    this.setCommodity(monthinfo.getCommodity());
    this.setReportDate(month);
    this.setToCountry(monthinfo.getToCountry());
  
    this.setWeightUnit(monthinfo.getWeightUnit());
  }
}
