package model.entity;

import java.util.Date;

import model.constant.Commodity;
import tcc.utils.obj.generation.annotation.DB_FIELD;

public abstract class CommodityRecord extends Record {
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Commodity commodity;// …Ã∆∑
  @DB_FIELD(type = DB_FIELD.TYPE_VARCHAR + "(25)", primary = true)
  private String source;
  @DB_FIELD(type = DB_FIELD.TYPE_VARCHAR + "(255)", allowNull = true)
  private String comment;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(10)", allowNull = true)
  private String updatedBy;
  @DB_FIELD(type = DB_FIELD.TYPE_DATE)
  private Date updatedAt;

  public Commodity getCommodity() {
    return commodity;
  }

  public void setCommodity(Commodity commodity) {
    this.commodity = commodity;
  }

}
