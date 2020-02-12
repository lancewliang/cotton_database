package model.entity.price.country;

import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;

public class ExchangeRateUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;//
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportHour;//
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private String fromCurreny;// USD 货币转换 ，USDCNY 美元 换 人民币
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private String toCurreny;// CNY 货币转换 ，USDCNY 美元 换 人民币
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double openingValue = DBUtil.NULLFLOAT;
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double topValue = DBUtil.NULLFLOAT;
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double minimumValue = DBUtil.NULLFLOAT;
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double closingValue = DBUtil.NULLFLOAT;
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, allowNull = true)
  private long volumes;
 
  public long getReportDate() {
    return reportDate;
  }

  public void setReportDate(long reportDate) {
    this.reportDate = reportDate;
  }

  public long getReportHour() {
    return reportHour;
  }

  public void setReportHour(long reportHour) {
    this.reportHour = reportHour;
  }

  public String getFromCurreny() {
    return fromCurreny;
  }

  public void setFromCurreny(String fromCurreny) {
    this.fromCurreny = fromCurreny;
  }

  public String getToCurreny() {
    return toCurreny;
  }

  public void setToCurreny(String toCurreny) {
    this.toCurreny = toCurreny;
  }

  public double getOpeningValue() {
    return openingValue;
  }

  public void setOpeningValue(double openingValue) {
    this.openingValue = openingValue;
  }

  public double getTopValue() {
    return topValue;
  }

  public void setTopValue(double topValue) {
    this.topValue = topValue;
  }

  public double getMinimumValue() {
    return minimumValue;
  }

  public void setMinimumValue(double minimumValue) {
    this.minimumValue = minimumValue;
  }

  public double getClosingValue() {
    return closingValue;
  }

  public void setClosingValue(double closingValue) {
    this.closingValue = closingValue;
  }

  public long getVolumes() {
    return volumes;
  }

  public void setVolumes(long volumes) {
    this.volumes = volumes;
  }

}
