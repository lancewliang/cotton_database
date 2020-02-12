package model.entity.production.country;

import model.constant.AreaUnit;
import model.constant.Country;
import model.entity.CommodityRecord;
import model.entity.DaylyRecord;
import model.entity.YearlyRecord;
import model.entity.wasde.obj.WorldSupplyDemandMonthlyHistory;
import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

//种植面积
@DB_TABLE(name = "GrowAreaYear", alias = "gay", tablespace = "ccdata")
public class GrowAreaYear extends CommodityRecord implements YearlyRecord, DaylyRecord {
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private String year;// 年度
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;//
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 国家，主要生产国和出口国
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(1)", primary = true)
  int reportStatus = 0;
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double value = DBUtil.NULLDOUBLE;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(25)")
  private AreaUnit areaUnit;// 面积单位

  // //0 proj, 1 est
  //
  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
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

  public AreaUnit getAreaUnit() {
    return areaUnit;
  }

  public void setAreaUnit(AreaUnit areaUnit) {
    this.areaUnit = areaUnit;
  }

  @Override
  public boolean ignoreSave() {
    return DBUtil.NULLDOUBLE == getValue();
  }

  public int getReportStatus() {
    return reportStatus;
  }

  public void setReportStatus(int reportStatus) {
    this.reportStatus = reportStatus;
  }

  public long getReportDate() {
    return reportDate;
  }

  public void setReportDate(long reportDate) {
    this.reportDate = reportDate;
  }

  public boolean isDataSame(GrowAreaYear one) {

    if (value != one.value) {
      return false;
    }

    if (reportStatus != one.reportStatus) {
      return false;
    }
    return true;
  }
}
