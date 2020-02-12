package model.constant;

import java.util.HashMap;

import model.constant.dao.Constant;
import model.constant.dao.ConstantBase;

public class AreaUnit implements Constant {
  protected static HashMap<String, Constant> map = new HashMap<String, Constant>();

  static {
    ConstantBase.init(AreaUnit.class, map);
  }
  private String name = null;
  private long Weight = 0;

  public String getAreaUnit() {
    return name;
  }

  public long getArea() {
    return Weight;
  }

  public static AreaUnit getAreaUnit(String str) {
    return (AreaUnit) map.get(str);
  }

  public void parse(String key, String value) {
    name = key;
    Weight = Long.parseLong(value);
  }

  @Override
  public String getDisplay() {
    
    return name;
  }
}
