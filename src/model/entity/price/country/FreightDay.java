package model.entity.price.country;

import model.constant.Country;
import model.constant.PriceUnit;
import model.entity.Record;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_INDEX1;
import tcc.utils.obj.generation.annotation.DB_TABLE;

//运费 

@DB_TABLE(name = "FreightDay", alias = "fd", tablespace = "ccdata")
 
public class FreightDay extends Record {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;//
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", refObjectField = "country", primary = true)
  private Country from;// 出发 国家
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", refObjectField = "country", primary = true)
  private Country to;// 目的国家

  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double value1 = DBUtil.NULLFLOAT;// 海运
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double value2 = DBUtil.NULLFLOAT;// 路晕
  @DB_FIELD(type = PriceUnit.DB_TYPE)
  private PriceUnit priceUnit;// 计价单位

  //
  public long getReportDate() {
    return reportDate;
  }

  public void setReportDate(long reportDate) {
    this.reportDate = reportDate;
  }

  public Country getFrom() {
    return from;
  }

  public void setFrom(Country from) {
    this.from = from;
  }

  public Country getTo() {
    return to;
  }

  public void setTo(Country to) {
    this.to = to;
  }

  public double getValue1() {
    return value1;
  }

  public void setValue1(double value1) {
    this.value1 = value1;
  }

  public double getValue2() {
    return value2;
  }

  public void setValue2(double value2) {
    this.value2 = value2;
  }

  public PriceUnit getPriceUnit() {
    return priceUnit;
  }

  public void setPriceUnit(PriceUnit priceUnit) {
    this.priceUnit = priceUnit;
  }

  @Override
  public boolean ignoreSave() {

    return false;
  }

}
