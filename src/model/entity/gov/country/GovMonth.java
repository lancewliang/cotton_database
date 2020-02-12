package model.entity.gov.country;

import model.constant.Country;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.MonthlyRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "GovMonth", alias = "erd", tablespace = "ccdata")
public class GovMonth extends CommodityRecord implements MonthlyRecord, ResetUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymm
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 进口
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double buyValue = DBUtil.NULLDOUBLE;// 收储量
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double sellValue = DBUtil.NULLDOUBLE;// 抛储量
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double reserveValue = DBUtil.NULLDOUBLE;// 储备量
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

  public double getReserveValue() {
    return reserveValue;
  }

  public void setReserveValue(double reserveValue) {
    this.reserveValue = reserveValue;
  }

  public WeightUnit getWeightUnit() {
    return weightUnit;
  }

  public void setWeightUnit(WeightUnit weightUnit) {
    this.weightUnit = weightUnit;
  }

  //
  public void reSetUnit(QueryUnit unit) {
    if (unit.weightUnit != null) {
      sellValue = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, sellValue);
      reserveValue = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, reserveValue);
      buyValue = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, buyValue);
      this.weightUnit = unit.weightUnit;
    }
  }

  @Override
  public boolean ignoreSave() {

    return false;
  }
}
