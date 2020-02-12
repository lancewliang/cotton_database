package model.entity.price.country;

import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_INDEX1;
import tcc.utils.obj.generation.annotation.DB_TABLE;
import model.entity.Record;

//汇率 
@DB_TABLE(name = "ExchangeRateDay", alias = "erd", tablespace = "ccdata")
 
public class ExchangeRateDay extends Record {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;//
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private String fromCurreny;// USD 货币转换 ，USDCNY 美元 换 人民币
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private String toCurreny;// CNY 货币转换 ，USDCNY 美元 换 人民币
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double value = DBUtil.NULLFLOAT;

  //
  public long getReportDate() {
    return reportDate;
  }

  public void setReportDate(long reportDate) {
    this.reportDate = reportDate;
  }

  public double getValue() {
    return value;
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

  public void setValue(double value) {
    this.value = value;
  }

  @Override
  public boolean ignoreSave() {

    return DBUtil.NULLDOUBLE == getValue();
  }

}
