package model.entity.stock;

import model.constant.Country;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.MonthlyRecord;
import model.entity.YearlyRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_INDEX1;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "BusinessStockMonth", alias = "sm", tablespace = "ccdata")
public class BusinessStockMonth extends CommodityRecord implements MonthlyRecord, YearlyRecord, ResetUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymm

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 国家

  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double value = DBUtil.NULLFLOAT;// 库存
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

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
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
      value = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, value);
      this.weightUnit = unit.weightUnit;
    }
  }

  @Override
  public boolean ignoreSave() {

    return DBUtil.NULLDOUBLE == getValue();
  }

}
