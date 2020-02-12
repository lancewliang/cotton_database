package model.entity.custom.country;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.MonthlyRecord;
import model.entity.Record;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_INDEX1;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "ImportTypeMonth", alias = "erd", tablespace = "ccdata")
public class ImportTypeMonth extends CommodityRecord implements MonthlyRecord, ResetUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymm
 
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", refObjectField = "country", primary = true)
  private Country toCountry;// 进口
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private String type;// 贸易类型
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double value = DBUtil.NULLDOUBLE;
  @DB_FIELD(type = WeightUnit.DB_TYPE, allowNull = true)
  private WeightUnit weightUnit;// 进口量 计量单位

  public long getReportDate() {
    return reportDate;
  }

  public void setReportDate(long reportDate) {
    this.reportDate = reportDate;
  }
 
  public Country getToCountry() {
    return toCountry;
  }

  public void setToCountry(Country toCountry) {
    this.toCountry = toCountry;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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
    if (unit.weightUnit != null) {
      value = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, value);
      this.weightUnit = unit.weightUnit;
    }
  }  @Override
  public boolean ignoreSave() {

    return DBUtil.NULLDOUBLE == getValue();
  }
}
