package model.entity.price.country;

import model.constant.Country;
import model.constant.PortPriceType;
import model.constant.PriceUnit;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.DaylyRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

//港口价格
@DB_TABLE(name = "PortPriceDay", alias = "sad", tablespace = "ccdata")
public class PortPriceDay extends CommodityRecord implements DaylyRecord, ResetUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(25)", primary = true)
  private long reportDate;//

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 港口国家
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", refObjectField = "country", primary = true, allowNull = true)
  private Country fromCountry;// 源国家

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(25)", primary = true)
  private String standard;// 规格
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(25)", primary = true)
  private String term = "";// 商品年度

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(25)", primary = true)
  private PortPriceType portPriceType;// FOB 出港价，CIF 到岸价，POP港口官方报价 PMP港口市场报价
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double value1 = DBUtil.NULLFLOAT;
  @DB_FIELD(type = PriceUnit.DB_TYPE, allowNull = true)
  private PriceUnit priceUnit1;
  @DB_FIELD(type = WeightUnit.DB_TYPE, allowNull = true)
  private WeightUnit weightUnit1; // 计量单位
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double value2 = DBUtil.NULLFLOAT;
  @DB_FIELD(type = PriceUnit.DB_TYPE, allowNull = true)
  private PriceUnit priceUnit2;
  @DB_FIELD(type = WeightUnit.DB_TYPE, allowNull = true)
  private WeightUnit weightUnit2; // 计量单位

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

  public Country getFromCountry() {
    return fromCountry;
  }

  public void setFromCountry(Country fromCountry) {
    this.fromCountry = fromCountry;
  }

  public String getStandard() {
    return standard;
  }

  public void setStandard(String standard) {
    this.standard = standard;
  }

  public PortPriceType getPortPriceType() {
    return portPriceType;
  }

  public void setPortPriceType(PortPriceType portPriceType) {
    this.portPriceType = portPriceType;
  }

  public String getTerm() {
    return term;
  }

  public double getValue1() {
    return value1;
  }

  public void setValue1(double value1) {
    this.value1 = value1;
  }

  public PriceUnit getPriceUnit1() {
    return priceUnit1;
  }

  public void setPriceUnit1(PriceUnit priceUnit1) {
    this.priceUnit1 = priceUnit1;
  }

  public WeightUnit getWeightUnit1() {
    return weightUnit1;
  }

  public void setWeightUnit1(WeightUnit weightUnit1) {
    this.weightUnit1 = weightUnit1;
  }

  public double getValue2() {
    return value2;
  }

  public void setValue2(double value2) {
    this.value2 = value2;
  }

  public PriceUnit getPriceUnit2() {
    return priceUnit2;
  }

  public void setPriceUnit2(PriceUnit priceUnit2) {
    this.priceUnit2 = priceUnit2;
  }

  public WeightUnit getWeightUnit2() {
    return weightUnit2;
  }

  public void setWeightUnit2(WeightUnit weightUnit2) {
    this.weightUnit2 = weightUnit2;
  }

  public void setTerm(String term) {
    this.term = term;
  }

  @Override
  public boolean ignoreSave() {

    return DBUtil.NULLDOUBLE == getValue1() && DBUtil.NULLDOUBLE == getValue2();
  }

  public void reSetUnit(QueryUnit unit) {
    if (unit.weightUnit != null) {
      value1 = WeightUnit.reSetPriceWeightUnit(this.weightUnit1, unit.weightUnit, value1);
      this.weightUnit1 = unit.weightUnit;
      value2 = WeightUnit.reSetPriceWeightUnit(this.weightUnit2, unit.weightUnit, value2);
      this.weightUnit2 = unit.weightUnit;

    }
    if (unit.priceUnit != null) {
      value1 = PriceUnit.reSetPriceUnit(this.priceUnit1, unit.priceUnit, value1);
      this.priceUnit1 = unit.priceUnit;
      value2 = PriceUnit.reSetPriceUnit(this.priceUnit2, unit.priceUnit, value2);
      this.priceUnit2 = unit.priceUnit;
    }
  }

}
