package model.constant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import model.constant.dao.Constant;
import model.constant.dao.ConstantBase;

public class Commodity implements Constant {
  protected static HashMap<String, Constant> map1 = new HashMap<String, Constant>();
  protected static HashMap<String, Constant> map2 = new HashMap<String, Constant>();
  static {
    ConstantBase.init(Commodity.class, map1);

  }

  private String name = null;

  public String getCommodity() {
    return name;
  }

  public static Commodity getCommodity(String str) {
    if (str == null)
      return null;
    Commodity ret = (Commodity) map1.get(str);
    if (ret != null)
      return ret;

    return ret;
  }

  public static Set<Commodity> getCommoditys() {
    Set set = new HashSet<Commodity>();
    set.addAll(map1.values());
    return set;
  }

  @Override
  public void parse(String key, String value) {
    name = value;

  }

  @Override
  public String getDisplay() {

    return name;
  }
}
