package model.entity.production.country;

import model.constant.Country;
import model.entity.CommodityRecord;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_INDEX1;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "AgricultureYieldWeek", alias = "ym", tablespace = "ccdata") 
public class AgricultureYieldWeek extends CommodityRecord {
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;//

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// ���ң���Ҫ�������ͳ��ڹ�
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double pickingRate;// ��ժ��
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double sellRate;// ������
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double workingRate;// �ӹ���
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double salesRate;// ������
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double salesProcessRate;// ���۽���

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

  public double getPickingRate() {
    return pickingRate;
  }

  public void setPickingRate(double pickingRate) {
    this.pickingRate = pickingRate;
  }

  public double getSellRate() {
    return sellRate;
  }

  public void setSellRate(double sellRate) {
    this.sellRate = sellRate;
  }

  public double getWorkingRate() {
    return workingRate;
  }

  public void setWorkingRate(double workingRate) {
    this.workingRate = workingRate;
  }

  public double getSalesRate() {
    return salesRate;
  }

  public void setSalesRate(double salesRate) {
    this.salesRate = salesRate;
  }

  public double getSalesProcessRate() {
    return salesProcessRate;
  }

  public void setSalesProcessRate(double salesProcessRate) {
    this.salesProcessRate = salesProcessRate;
  }

  //
  @Override
  public boolean ignoreSave() {
    return false;
  }
}
