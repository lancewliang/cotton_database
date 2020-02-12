package model.entity.custom.country;

import model.constant.Country;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.MonthlyRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

//进口总量

@DB_TABLE(name = "ImportExportMonth", alias = "erd", tablespace = "ccdata")
public class ImportExportMonth extends CommodityRecord implements ResetUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymm
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", refObjectField = "country", primary = true)
  private Country toCountry;// 进口
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", refObjectField = "country", primary = true)
  private Country fromCountry;// 进口
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double value = DBUtil.NULLDOUBLE;
  @DB_FIELD(type = WeightUnit.DB_TYPE, allowNull = true)
  private WeightUnit weightUnit;// 进口量 计量单位

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

  public void setValue(double value) {
    this.value = value;
  }

  public WeightUnit getWeightUnit() {
    return weightUnit;
  }

  public void setWeightUnit(WeightUnit weightUnit) {
    this.weightUnit = weightUnit;
  }

  public Country getToCountry() {
    return toCountry;
  }

  public void setToCountry(Country toCountry) {
    this.toCountry = toCountry;
  }

  public Country getFromCountry() {
    return fromCountry;
  }

  public void setFromCountry(Country fromCountry) {
    this.fromCountry = fromCountry;
  }

  //
  //
  public void reSetUnit(QueryUnit unit) {

    if (unit.weightUnit != null && this.weightUnit != null) {

      value = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, value);
      this.weightUnit = unit.weightUnit;
    }
  }

  @Override
  public boolean ignoreSave() {

    return false;
  }
}
