package model.entity.stock;

import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_INDEX1;
import tcc.utils.obj.generation.annotation.DB_TABLE;
import model.constant.Bourse;
import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.CommodityRecord;
import model.entity.Record;

//��������� �ܱ�
@DB_TABLE(name = "BourseStockWeek", alias = "bsm", tablespace = "ccdata") 
public class BourseStockWeek extends CommodityRecord  {

  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymmdd
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// ����

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Bourse bourse;// ������
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double value = DBUtil.NULLDOUBLE;// ��ǰ�ܿ��
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double predictedValue = DBUtil.NULLDOUBLE;// Ԥ��ֵ
  @DB_FIELD(type = WeightUnit.DB_TYPE)
  private WeightUnit weightUnit;// ������λ

  //
  public long getReportDate() {
    return reportDate;
  }

  public void setReportDate(long reportDate) {
    this.reportDate = reportDate;
  }

  public Bourse getBourse() {
    return bourse;
  }

  public void setBourse(Bourse bourse) {
    this.bourse = bourse;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  public double getPredictedValue() {
    return predictedValue;
  }

  public void setPredictedValue(double predictedValue) {
    this.predictedValue = predictedValue;
  }

  public WeightUnit getWeightUnit() {
    return weightUnit;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public void setWeightUnit(WeightUnit weightUnit) {
    this.weightUnit = weightUnit;
  }

  @Override
  public boolean ignoreSave() {

    return DBUtil.NULLDOUBLE == getValue();
  }

}
