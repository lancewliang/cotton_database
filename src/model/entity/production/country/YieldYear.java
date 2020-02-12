package model.entity.production.country;

import model.constant.Country;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.DaylyRecord;
import model.entity.YearlyRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

//产量年表
@DB_TABLE(name = "YieldYear", alias = "yy", tablespace = "ccdata")
public class YieldYear extends CommodityRecord implements DaylyRecord, YearlyRecord, ResetUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private String year;// 年度
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;//
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 国家，主要生产国和出口国
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double value = DBUtil.NULLDOUBLE;
  @DB_FIELD(type = WeightUnit.DB_TYPE)
  private WeightUnit weightUnit;// 计量单位
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(1)", primary = true)
  int reportStatus = 0;

  // 0 proj, 1 est
  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public WeightUnit getWeightUnit() {
    return weightUnit;
  }

  public void setWeightUnit(WeightUnit weightUnit) {
    this.weightUnit = weightUnit;
  }

  //
  //
  public void reSetUnit(QueryUnit unit) {
    if (unit.weightUnit != null) {
      value = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, value);
      this.weightUnit = unit.weightUnit;
    }
  }

  @Override
  public boolean ignoreSave() {
    return DBUtil.NULLDOUBLE == getValue();
  }

  public long getReportDate() {
    return reportDate;
  }

  public void setReportDate(long reportDate) {
    this.reportDate = reportDate;
  }

  public int getReportStatus() {
    return reportStatus;
  }

  public void setReportStatus(int reportStatus) {
    this.reportStatus = reportStatus;
  }

  public boolean isDataSame(YieldYear one) {

    if (value != one.value) {
      return false;
    }

    if (reportStatus != one.reportStatus) {
      return false;
    }
    return true;
  }
}
