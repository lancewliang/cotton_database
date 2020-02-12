package model.constant;

import java.math.BigDecimal;
import java.util.HashMap;

import tcc.utils.db.DBUtil;
import tcc.utils.obj.generation.annotation.DB_FIELD;
import model.constant.dao.Constant;
import model.constant.dao.ConstantBase;
import model.constant.dao.Unit;

public class LengthUnit implements Unit {
  protected static HashMap<String, Constant> map = new HashMap<String, Constant>();
  public final static String DB_TYPE = DB_FIELD.TYPE_CHAR + "(25)";
  static {
    ConstantBase.init(LengthUnit.class, map);

  }
  private String name = null;
  private double length = 0;// ºÁÃ×

  public String getLengthUnit() {
    return name;
  }

  public double getLength() {
    return length;
  }

  @Override
  public void parse(String key, String value) {
    name = key;
    length = Double.parseDouble(value);
  }

  @Override
  public String getDisplay() {
    return name;
  }

  public static LengthUnit getLengthUnit(String str) {
    return (LengthUnit) map.get(str);

  }

  public static double reSetLengthUnit(LengthUnit oldLengthUnit, LengthUnit newLengthUnit, double value) {
    if (value == DBUtil.NULLDOUBLE)
      return value;
    double v = oldLengthUnit.getLength() * value / newLengthUnit.getLength();

    BigDecimal b = new BigDecimal(v);
    return b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();

  }

  @Override
  public String getUnit() {
    return name;
  }
}
