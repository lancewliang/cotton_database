package model.constant;

import java.util.HashMap;

import model.constant.dao.Constant;
import model.constant.dao.ConstantBase;

public class PortPriceType implements Constant {
  protected static HashMap<String, Constant> map = new HashMap<String, Constant>();

  static {
    ConstantBase.init(PortPriceType.class, map);
  }
  private String name = null;

  public String getPortPriceType() {
    return name;
  }

  public static PortPriceType getPortPriceType(String str) {
    if (str == null)
      return null;
    return (PortPriceType) map.get(str);
  }

  @Override
  public void parse(String key, String value) {
    name = value;

  }

  @Override
  public String getDisplay() {

    return name ;
  }
}
