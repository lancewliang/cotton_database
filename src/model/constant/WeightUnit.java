package model.constant;

import java.math.BigDecimal;
import java.util.HashMap;

import model.constant.dao.Constant;
import model.constant.dao.ConstantBase;
import model.constant.dao.Unit;
import tcc.utils.db.DBUtil;
import tcc.utils.log.LogService;
import tcc.utils.obj.generation.annotation.DB_FIELD;

public class WeightUnit implements Unit {
  /*
   * Thousands_of_Bales, // 1000°ü Ten_Thousands_of_Tons, // Íò¶Ö Ton, // ¶Ö
   * Hundred_of_Kilogram, // 100¹«½ï Pound;// °õ
   */

  protected static HashMap<String, Constant> map = new HashMap<String, Constant>();
  public final static String DB_TYPE = DB_FIELD.TYPE_CHAR + "(25)";
  static {
    ConstantBase.init(WeightUnit.class, map);

  }
  private String name = null;
  private double WeightKG = 0;

  public String getWeightUnit() {
    return name;
  }

  public double getWeightKG() {
    return WeightKG;
  }

  public static WeightUnit getWeightUnit(String str) {
    return (WeightUnit) map.get(str);

  }

  @Override
  public void parse(String key, String value) {
    name = key;
    WeightKG = Double.parseDouble(value);
  }

  public static double reSetWeightUnit(WeightUnit oldweightUnit, WeightUnit newweightUnit, double value) {
    if (value == DBUtil.NULLDOUBLE)
      return value;
    try {
      double v = oldweightUnit.getWeightKG() * value / newweightUnit.getWeightKG();

      BigDecimal b = new BigDecimal(v);

      return b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return 0;
  }

  public static double reSetPriceWeightUnit(WeightUnit oldweightUnit, WeightUnit newweightUnit, double value) {
    if (value == DBUtil.NULLDOUBLE)
      return value;
    if (oldweightUnit == null)
      return value;
    try {
      double v = (1 / oldweightUnit.getWeightKG()) * value * newweightUnit.getWeightKG();

      BigDecimal b = new BigDecimal(v);

      return b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return 0;
  }
  @Override
  public String getUnit() {
    return name;
  }

  @Override
  public String getDisplay() {

    return name;
  }
}
