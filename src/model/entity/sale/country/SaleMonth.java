package model.entity.sale.country;

import model.constant.Country;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.MonthlyRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_INDEX1;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "SaleMonth", alias = "sd", tablespace = "ccdata")
// 某一个国家当月 销售量
public class SaleMonth extends CommodityRecord implements MonthlyRecord, ResetUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymm

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 国家，主要生产国和出口国
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double value;// 当月销售总量
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)")
  private WeightUnit weightUnit;// 计量单位

  //

  @Override
  public boolean ignoreSave() {

    return DBUtil.NULLDOUBLE == getValue();
  }

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

  @Override
  public void reSetUnit(QueryUnit unit) {
    // TODO Auto-generated method stub
    if (unit.weightUnit != null) {
      value = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, value);
      this.weightUnit = unit.weightUnit;
    }
  }

}
