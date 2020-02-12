package model.entity.stock;

import model.constant.Bourse;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.CommodityRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "BoursesStockDayDetail", alias = "bsm", tablespace = "ccdata")
public class BoursesStockDayDetail extends CommodityRecord{

  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymmdd
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 国家

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Bourse bourse;// 交易所

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private String wHId;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(30)", primary = true)
  private String wHName;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private String annual;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private String grade;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private String producingArea;

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

  public String getWHId() {
    return wHId;
  }

  public void setWHId(String wHId) {
    this.wHId = wHId;
  }

  public String getWHName() {
    return wHName;
  }

  public void setWHName(String wHName) {
    this.wHName = wHName;
  }

  public String getAnnual() {
    return annual;
  }

  public void setAnnual(String annual) {
    this.annual = annual;
  }

  public String getGrade() {
    return grade;
  }

  public void setGrade(String grade) {
    this.grade = grade;
  }

  public String getProducingArea() {
    return producingArea;
  }

  public void setProducingArea(String producingArea) {
    this.producingArea = producingArea;
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
