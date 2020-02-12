package model.entity.price.country;

import model.constant.Bourse;
import model.constant.Country;
import model.constant.PriceUnit;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.DaylyRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

//期货商品价格 

@DB_TABLE(name = "FuturePriceDay", alias = "fpd", tablespace = "ccdata")
public class FuturePriceDay extends CommodityRecord implements DaylyRecord, ResetUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;//

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 国家
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private String contract;// 合约
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", allowNull = true)
  private Bourse bourse;
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
  @DB_FIELD(type = PriceUnit.DB_TYPE)
  private PriceUnit priceUnit;
  @DB_FIELD(type = WeightUnit.DB_TYPE)
  private WeightUnit weightUnit;

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

  public long getVolumes() {
    return volumes;
  }

  public void setVolumes(long volumes) {
    this.volumes = volumes;
  }

  public double getClosingValue() {
    return closingValue;
  }

  public void setClosingValue(double closingValue) {
    this.closingValue = closingValue;
  }

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

  public Bourse getBourse() {
    return bourse;
  }

  public void setBourse(Bourse bourse) {
    this.bourse = bourse;
  }

  public String getContract() {
    return contract;
  }

  public void setContract(String contract) {
    this.contract = contract;
  }

  public PriceUnit getPriceUnit() {
    return priceUnit;
  }

  public void setPriceUnit(PriceUnit priceUnit) {
    this.priceUnit = priceUnit;
  }

  public WeightUnit getWeightUnit() {
    return weightUnit;
  }

  public void setWeightUnit(WeightUnit weightUnit) {
    this.weightUnit = weightUnit;
  }

  //

  @Override
  public boolean ignoreSave() {

    return DBUtil.NULLDOUBLE == closingValue;
  }

  public void reSetUnit(QueryUnit unit) {
    if (unit.weightUnit != null) {
      closingValue = WeightUnit.reSetPriceWeightUnit(this.weightUnit, unit.weightUnit, closingValue);
      this.weightUnit = unit.weightUnit;
    }
  }

}
