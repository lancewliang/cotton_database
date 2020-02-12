package model.entity.gov.country;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.Record;
import model.entity.YearlyRecord;
import tcc.utils.db.DBUtil;

public class GovYear extends CommodityRecord implements YearlyRecord, ResetUnit {
  private String reportDate;// yyyy 
  private Country country;// 进口
  private double buyValue = DBUtil.NULLDOUBLE;// 收储量
  private double sellValue = DBUtil.NULLDOUBLE;// 抛储量
  private double reserveValue = DBUtil.NULLDOUBLE;// 储备量
  private WeightUnit weightUnit;// 计量单位

  public String getReportDate() {
    return reportDate;
  }

  public void setReportDate(String reportDate) {
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
  }  @Override
  public boolean ignoreSave() {

    return  false;
  }
}
