package model.entity.gov.country;

import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.CommodityRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "GovBatch", alias = "erd", tablespace = "ccdata") 
public class GovBatch extends CommodityRecord {
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(25)", primary = true)
  private String name;// yyyymm

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 国家
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  private long startDate;// yyyymm
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  private long endDate;// yyyymm
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double buyValue = DBUtil.NULLDOUBLE;// 收储量
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double sellValue = DBUtil.NULLDOUBLE;// 抛储量
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double reserveValue = DBUtil.NULLDOUBLE;// 储备量
  @DB_FIELD(type = WeightUnit.DB_TYPE)
  private WeightUnit weightUnit;// 计量单位

  //

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public WeightUnit getWeightUnit() {
    return weightUnit;
  }

  public void setWeightUnit(WeightUnit weightUnit) {
    this.weightUnit = weightUnit;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getStartDate() {
    return startDate;
  }

  public void setStartDate(long startDate) {
    this.startDate = startDate;
  }

  public long getEndDate() {
    return endDate;
  }

  public double getBuyValue() {
    return buyValue;
  }

  public void setBuyValue(double buyValue) {
    this.buyValue = buyValue;
  }

  public double getSellValue() {
    return sellValue;
  }

  public void setSellValue(double sellValue) {
    this.sellValue = sellValue;
  }

  public double getReserveValue() {
    return reserveValue;
  }

  public void setReserveValue(double reserveValue) {
    this.reserveValue = reserveValue;
  }

  public void setEndDate(long endDate) {
    this.endDate = endDate;
  }
  @Override
  public boolean ignoreSave() {

    return false;
  }
}
