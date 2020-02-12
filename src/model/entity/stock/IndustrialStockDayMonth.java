package model.entity.stock;

import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;
import model.constant.Country;
import model.entity.CommodityRecord;
import model.entity.MonthlyRecord;
import model.entity.YearlyRecord;

@DB_TABLE(name = "IndustrialStockDayMonth", alias = "smd", tablespace = "ccdata")
public class IndustrialStockDayMonth extends CommodityRecord implements MonthlyRecord, YearlyRecord {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymm
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// ¹ú¼Ò
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double days = DBUtil.NULLFLOAT;

  @Override
  public boolean ignoreSave() {
    return days==DBUtil.NULLFLOAT;
  }

  public long getReportDate() {
    return reportDate;
  }

  public void setReportDate(long reportDate) {
    this.reportDate = reportDate;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public double getDays() {
    return days;
  }

  public void setDays(double days) {
    this.days = days;
  }

}
