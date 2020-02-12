package model.entity.macroeconomic;

import java.util.Date;

import model.constant.Country;

import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "CountryMainIndex", alias = "cmi", tablespace = "ccdata")
public class CountryMainIndex {
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(25)", primary = true)
  private long reportDate;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(8)", primary = true)
  private int reportHour;
  @DB_FIELD(type = DB_FIELD.TYPE_VARCHAR + "(25)", primary = true)
  private String source;
  @DB_FIELD(type = DB_FIELD.TYPE_VARCHAR + "(8)", primary = true)
  private Country country;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(45)", primary = true)
  private String title;
  @DB_FIELD(type = DB_FIELD.TYPE_VARCHAR + "(8)")
  private String currency;
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  private int importance;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", allowNull = true)
  private String inference = null;
  public static final String INFERENCE_UP = "UP";
  public static final String INFERENCE_FLAT = "FLAT";
  public static final String INFERENCE_DOWN = "DOWN";
  //
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(10)", allowNull = true)
  private String forecastValue;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(10)", allowNull = true)
  private String actualValue;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(10)", allowNull = true)
  private String previousValue;
  @DB_FIELD(type = DB_FIELD.TYPE_VARCHAR + "(255)", allowNull = true)
  private String remark;
  @DB_FIELD(type = DB_FIELD.TYPE_VARCHAR + "(255)", allowNull = true)
  private String mark;
  @DB_FIELD(type = DB_FIELD.TYPE_VARCHAR + "(255)", allowNull = true)
  private String description;

  @DB_FIELD(type = DB_FIELD.TYPE_VARCHAR + "(255)", allowNull = true)
  private String comment;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(10)", allowNull = true)
  private String updatedBy;
  @DB_FIELD(type = DB_FIELD.TYPE_DATE)
  private Date updatedAt;

  public long getReportDate() {
    return reportDate;
  }

  public void setReportDate(long reportDate) {
    this.reportDate = reportDate;
  }

  public int getReportHour() {
    return reportHour;
  }

  public void setReportHour(int reportHour) {
    this.reportHour = reportHour;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public int getImportance() {
    return importance;
  }

  public void setImportance(int importance) {
    this.importance = importance;
  }

  public String getForecastValue() {
    return forecastValue;
  }

  public void setForecastValue(String forecastValue) {
    this.forecastValue = forecastValue;
  }

  public String getActualValue() {
    return actualValue;
  }

  public void setActualValue(String actualValue) {
    this.actualValue = actualValue;
  }

  public String getPreviousValue() {
    return previousValue;
  }

  public void setPreviousValue(String previousValue) {
    this.previousValue = previousValue;
  }

  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  public String getMark() {
    return mark;
  }

  public void setMark(String mark) {
    this.mark = mark;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String getInference() {
    return inference;
  }

  public void setInference(String inference) {
    this.inference = inference;
  }

}
