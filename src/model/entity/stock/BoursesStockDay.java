package model.entity.stock;

import model.constant.Bourse;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.CommodityRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "BoursesStockDay", alias = "bsm", tablespace = "ccdata")
public class BoursesStockDay extends CommodityRecord{

  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymmdd
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 国家

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Bourse bourse;// 交易所

  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double value = DBUtil.NULLDOUBLE;// 当前总库存
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double predictedValue = DBUtil.NULLDOUBLE;// 预报值
  @DB_FIELD(type = WeightUnit.DB_TYPE)
  private WeightUnit weightUnit;// 计量单位

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

  public Bourse getBourse() {
    return bourse;
  }

  public void setBourse(Bourse bourse) {
    this.bourse = bourse;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public double getPredictedValue() {
    return predictedValue;
  }

  public void setPredictedValue(double predictedValue) {
    this.predictedValue = predictedValue;
  }

  public WeightUnit getWeightUnit() {
    return weightUnit;
  }

  public void setWeightUnit(WeightUnit weightUnit) {
    this.weightUnit = weightUnit;
  }

  @Override
  public boolean ignoreSave() {
    // TODO Auto-generated method stub
    return false;
  }

}
