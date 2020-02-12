package model.entity.production.country;

import model.constant.Country;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.MonthlyRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;

public class YieldMonth extends CommodityRecord implements MonthlyRecord, ResetUnit {

  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;//

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 国家，主要生产国和出口国
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double value = DBUtil.NULLDOUBLE;// 当月产量
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double movingAnnualTotal = DBUtil.NULLDOUBLE;// 年度累计 总量
  @DB_FIELD(type = WeightUnit.DB_TYPE)
  private WeightUnit weightUnit;// 计量单位

  //
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

  public double getMovingAnnualTotal() {
    return movingAnnualTotal;
  }

  public void setMovingAnnualTotal(double movingAnnualTotal) {
    this.movingAnnualTotal = movingAnnualTotal;
  }

  //
  public void reSetUnit(QueryUnit unit) {
    if (unit.weightUnit != null) {
      value = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, value);
      movingAnnualTotal = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, movingAnnualTotal);
      this.weightUnit = unit.weightUnit;
    }
  }

  @Override
  public boolean ignoreSave() { 
    return false;
  }
}
