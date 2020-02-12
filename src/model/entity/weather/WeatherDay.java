package model.entity.weather;

import model.constant.LengthUnit;
import model.constant.WeatherRegion;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.DaylyRecord;
import model.entity.Record;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "WeatherDay", alias = "sd", tablespace = "ccdata")
public class WeatherDay extends Record implements DaylyRecord {
	//, ResetUnit
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymmdd
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(140)", primary = true)
  private WeatherRegion weatherRegion;
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  private int high = 0;// Temperature
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  private int low = 0;// Temperature
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double precip = 0;// in
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(10)")
  LengthUnit precipUnit = null;
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  private double snow = 0;// in
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(10)")
  LengthUnit snowUnit = null;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(140)")
  private String forecast;
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  private int avgHigh = 0;// Temperature
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  private int avgLow = 0;// Temperature

  //

  @Override
  public boolean ignoreSave() {
    return false;
  }

  public String getForecast() {
    return forecast;
  }

  public void setForecast(String forecast) {
    this.forecast = forecast;
  }

  public long getReportDate() {
    return reportDate;
  }

  public void setReportDate(long reportDate) {
    this.reportDate = reportDate;
  }

  public WeatherRegion getWeatherRegion() {
    return weatherRegion;
  }

  public void setWeatherRegion(WeatherRegion weatherRegion) {
    this.weatherRegion = weatherRegion;
  }

  public int getHigh() {
    return high;
  }

  public void setHigh(int high) {
    this.high = high;
  }

  public LengthUnit getPrecipUnit() {
    return precipUnit;
  }

  public void setPrecipUnit(LengthUnit precipUnit) {
    this.precipUnit = precipUnit;
  }

  public LengthUnit getSnowUnit() {
    return snowUnit;
  }

  public void setSnowUnit(LengthUnit snowUnit) {
    this.snowUnit = snowUnit;
  }

  public int getLow() {
    return low;
  }

  public void setLow(int low) {
    this.low = low;
  }

  public int getAvgHigh() {
    return avgHigh;
  }

  public void setAvgHigh(int avgHigh) {
    this.avgHigh = avgHigh;
  }

  public int getAvgLow() {
    return avgLow;
  }

  public void setAvgLow(int avgLow) {
    this.avgLow = avgLow;
  }

  public double getPrecip() {
    return precip;
  }

  public void setPrecip(double precip) {
    this.precip = precip;
  }

  public double getSnow() {
    return snow;
  }

  public void setSnow(double snow) {
    this.snow = snow;
  } //

  public void reSetUnit(QueryUnit unit) {
    if (unit.lengthUnit != null) {
      if(this.precipUnit!=null){
      precip = LengthUnit.reSetLengthUnit(this.precipUnit, unit.lengthUnit, precip);
      this.precipUnit = unit.lengthUnit;
      }
      
      
      if(this.snowUnit!=null){
        snow = LengthUnit.reSetLengthUnit(this.snowUnit, unit.lengthUnit, snow);
        this.snowUnit = unit.lengthUnit;
        }
    }
  }

}
