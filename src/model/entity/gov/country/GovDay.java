package model.entity.gov.country;

import model.constant.Country;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.DaylyRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "GovDay", alias = "erd", tablespace = "ccdata")
public class GovDay extends CommodityRecord implements DaylyRecord, ResetUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymm
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 进口
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double buyValue = DBUtil.NULLDOUBLE;// 收储量
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double sellValue = DBUtil.NULLDOUBLE;// 抛储量
 
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double totalBuyValue = DBUtil.NULLDOUBLE;// 收储量
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double totalSellValue = DBUtil.NULLDOUBLE;// 抛储量
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

  public WeightUnit getWeightUnit() {
    return weightUnit;
  }

  public void setWeightUnit(WeightUnit weightUnit) {
    this.weightUnit = weightUnit;
  }

  public double getTotalBuyValue() {
    return totalBuyValue;
  }

  public void setTotalBuyValue(double totalBuyValue) {
    this.totalBuyValue = totalBuyValue;
  }

  public double getTotalSellValue() {
    return totalSellValue;
  }

  public void setTotalSellValue(double totalSellValue) {
    this.totalSellValue = totalSellValue;
  }

  //
  public void reSetUnit(QueryUnit unit) {
    if (unit.weightUnit != null) {
      sellValue = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, sellValue);
      buyValue = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, buyValue);
      this.weightUnit = unit.weightUnit;
    }
  }

  @Override
  public boolean ignoreSave() {

    return false;
  }
}
