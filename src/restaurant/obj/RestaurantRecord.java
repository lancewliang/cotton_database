package restaurant.obj;

import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "RestaurantRecord", alias = "sdr", tablespace = "ccdata")
public class RestaurantRecord {
  public static String SCOURSE_ele = "ele";
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(60)", primary = true)
  String keyID;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(10)", primary = true)
  String source;
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  private long reportDate;// yyyymmdd

  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  int recent_order_num;// 当月订单数
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  int month_sales;// 月销售数
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  int minimum_order_amount;// 起送价格
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  int rating_count;// 评价数目
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  int delivery_fee;// 配送费

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getKeyID() {
    return keyID;
  }

  public void setKeyID(String keyID) {
    this.keyID = keyID;
  }

  public long getReportDate() {
    return reportDate;
  }

  public void setReportDate(long reportDate) {
    this.reportDate = reportDate;
  }

  public int getRecent_order_num() {
    return recent_order_num;
  }

  public void setRecent_order_num(int recent_order_num) {
    this.recent_order_num = recent_order_num;
  }

  public int getMonth_sales() {
    return month_sales;
  }

  public void setMonth_sales(int month_sales) {
    this.month_sales = month_sales;
  }

  public int getMinimum_order_amount() {
    return minimum_order_amount;
  }

  public void setMinimum_order_amount(int minimum_order_amount) {
    this.minimum_order_amount = minimum_order_amount;
  }

  public int getRating_count() {
    return rating_count;
  }

  public void setRating_count(int rating_count) {
    this.rating_count = rating_count;
  }

  public int getDelivery_fee() {
    return delivery_fee;
  }

  public void setDelivery_fee(int delivery_fee) {
    this.delivery_fee = delivery_fee;
  }

}
