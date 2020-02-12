package model.entity;

import java.util.Date;

import tcc.utils.obj.generation.annotation.DB_FIELD;

public abstract class Record {
  @DB_FIELD(type = DB_FIELD.TYPE_VARCHAR + "(25)", primary = true)
  private String source;
  @DB_FIELD(type = DB_FIELD.TYPE_VARCHAR + "(255)", allowNull = true)
  private String comment;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(10)", allowNull = true)
  private String updatedBy;
  @DB_FIELD(type = DB_FIELD.TYPE_DATE)
  private Date updatedAt;

  //
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

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public abstract boolean ignoreSave();
}
