package model.entity.price.country;

import model.constant.Country;
import model.constant.PriceUnit;
import model.constant.UnitType;
import model.constant.WeightUnit;
import model.constant.dao.Unit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.DaylyRecord;
import model.entity.DifferentUnitType;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

//国内价格
@DB_TABLE(name = "CountryPriceDay", alias = "sad", tablespace = "ccdata")
public class CountryPriceDay extends CommodityRecord implements DaylyRecord, DifferentUnitType, ResetUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(25)", primary = true)
  private long reportDate;//

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 国家
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(25)", primary = true)
  private String state;// 区域，或者全国均值
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(225)", primary = true)
  private String standard;// 规格
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double value = DBUtil.NULLFLOAT;
  @DB_FIELD(type = PriceUnit.DB_TYPE)
  private PriceUnit priceUnit;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(10)")
  private UnitType unitType;
  @DB_FIELD(type = WeightUnit.DB_TYPE)
  private Unit unit; // 计量单位

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

  public String getStandard() {
    return standard;
  }

  public void setStandard(String standard) {
    this.standard = standard;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public PriceUnit getPriceUnit() {
    return priceUnit;
  }

  public void setPriceUnit(PriceUnit priceUnit) {
    this.priceUnit = priceUnit;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public UnitType getUnitType() {
    return unitType;
  }

  public void setUnitType(UnitType unitType) {
    this.unitType = unitType;
  }

  public Unit getUnit() {
    return unit;
  }

  public void setUnit(Unit unit) {
    this.unit = unit;
  }

  @Override
  public boolean ignoreSave() {

    return DBUtil.NULLDOUBLE == getValue();
  }

  public void reSetUnit(QueryUnit unit) {
    if (unit.weightUnit != null) {
      if (unitType.getUnitType().equals(UnitType.WEIGHT.getUnitType())) {
        value = WeightUnit.reSetPriceWeightUnit((WeightUnit) this.unit, unit.weightUnit, value);
        this.unit = unit.weightUnit;
      }
    }
    if (unit.priceUnit != null) {
      value = PriceUnit.reSetPriceUnit(this.priceUnit, unit.priceUnit, value);
      this.priceUnit = unit.priceUnit;

    }
  }

}
