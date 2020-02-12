package model.entity.production.country;

import model.constant.Country;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.DaylyRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_INDEX1;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "YieldDay", alias = "yd", tablespace = "ccdata")
public class YieldDay extends CommodityRecord implements DaylyRecord, ResetUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;//

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 国家，主要生产国和出口国

  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double total = DBUtil.NULLDOUBLE;// 累计 总量
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

  public WeightUnit getWeightUnit() {
    return weightUnit;
  }

  public void setWeightUnit(WeightUnit weightUnit) {
    this.weightUnit = weightUnit;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  @Override
  public boolean ignoreSave() {
    return DBUtil.NULLDOUBLE == getTotal();
  }

  public void reSetUnit(QueryUnit unit) {
    if (unit.weightUnit != null) {
      total = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, total);
      this.weightUnit = unit.weightUnit;
    }
  }

}
