package model.entity.custom.country;

import model.constant.Country;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.DaylyRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "ImportExportDay", alias = "erd", tablespace = "ccdata")
public class ImportExportDay extends CommodityRecord implements ResetUnit, DaylyRecord {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymmdd

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", refObjectField = "country", primary = true)
  private Country toCountry;// 进口
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", refObjectField = "country", primary = true)
  private Country fromCountry;// 进口
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double total = DBUtil.NULLDOUBLE;// 累计量

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

  public Country getFromCountry() {
    return fromCountry;
  }

  public void setFromCountry(Country fromCountry) {
    this.fromCountry = fromCountry;
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

  //
  public void reSetUnit(QueryUnit unit) {
    if (unit.weightUnit != null) {
      total = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, total);
      this.weightUnit = unit.weightUnit;
    }
  }

  @Override
  public boolean ignoreSave() {

    return DBUtil.NULLDOUBLE == this.total;
  }
}
