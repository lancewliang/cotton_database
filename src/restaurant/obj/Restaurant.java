package restaurant.obj;

import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "Restaurant", alias = "sd", tablespace = "ccdata")
public class Restaurant {
  public static String SCOURSE_ele = "ele";
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(60)", primary = true)
  String keyID;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(10)", primary = true)
  String source;
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  double latitude;
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  double longitude;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(100)")
  String name;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(200)")
  String adress;
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  private long reportDate;// yyyymmdd

  private long salesvalue;

  public String getKeyID() {
    return keyID;
  }

  public void setKeyID(String keyID) {
    this.keyID = keyID;
  }

  public Restaurant() {

  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public long getReportDate() {
    return reportDate;
  }

  public void setReportDate(long reportDate) {
    this.reportDate = reportDate;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getName() {
    return name;
  }

  public long getSalesvalue() {
    return salesvalue;
  }

  public void addSalesvalue(long nsalesvalue) {
    this.salesvalue += nsalesvalue;

  }

  public void setSalesvalue(long salesvalue) {
    this.salesvalue = salesvalue;

  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAdress() {
    return adress;
  }

  public void setAdress(String adress) {
    this.adress = adress;
  }

}
