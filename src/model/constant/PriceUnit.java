package model.constant;

import java.math.BigDecimal;
import java.util.HashMap;

import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;
import tcc.utils.obj.generation.annotation.DB_FIELD;

import model.constant.dao.Constant;
import model.constant.dao.ConstantBase;

public class PriceUnit implements Constant {

  protected static HashMap<String, Constant> map1 = new HashMap<String, Constant>();
  protected static HashMap<String, Constant> map2 = new HashMap<String, Constant>();
  static {
    ConstantBase.init(PriceUnit.class, map1);
    for (Constant obj1 : map1.values()) {
      PriceUnit obj = (PriceUnit) obj1;
      for (String name : obj.names) {
        map2.put(name, obj);
      }
    }
  }
  private String[] names = null;
  String unit;
  public final static String DB_TYPE = DB_FIELD.TYPE_CHAR + "(20)";

  public String getPriceUnit() {
    return unit;
  }

  public static PriceUnit getPriceUnit(String str) {
    if (str == null)
      return null;
    PriceUnit ret = (PriceUnit) map1.get(str.toUpperCase());
    if (ret != null)
      return ret;
    ret = (PriceUnit) map2.get(str.toUpperCase());
    return ret;
  }

  @Override
  public void parse(String key, String value) {
    unit = key.trim();
    names = value.trim().split(",");
  }

  @Override
  public String getDisplay() {

    return names[0];
  }

  public static double reSetPriceUnit(PriceUnit oldweightUnit, PriceUnit newweightUnit, double value) {
    if (value == DBUtil.NULLDOUBLE)
      return value;
    if (oldweightUnit == null)
      return value;
    try {
      if (oldweightUnit.getPriceUnit().equals("CENTS") && newweightUnit.getPriceUnit().equals("USD")) {
        BigDecimal b = new BigDecimal(value / 100);
        return b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
      } else if (oldweightUnit.getPriceUnit().equals("USD") && newweightUnit.getPriceUnit().equals("CENTS")) {
        return value * 100;
      } else {
        return value;
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return 0;
  }

}
