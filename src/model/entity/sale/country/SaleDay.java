package model.entity.sale.country;

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

@DB_TABLE(name = "SaleDay", alias = "sd", tablespace = "ccdata")
// ĳһ������ĳһ����ۼ�������
public class SaleDay extends CommodityRecord implements DaylyRecord, ResetUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymmdd

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// ���ң���Ҫ�������ͳ��ڹ�
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT, allowNull = true)
  private double total;// �ۼ���������
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)")
  private WeightUnit weightUnit;// ������λ

  //

  @Override
  public boolean ignoreSave() {

    return DBUtil.NULLDOUBLE == total;
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

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
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
      total = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, total);
      this.weightUnit = unit.weightUnit;
    }
  }
}
