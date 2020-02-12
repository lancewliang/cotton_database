package model.constant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import model.constant.dao.Constant;
import model.constant.dao.ConstantBase;

public class Country implements Constant {
  public static final String WHOLE = "WHOLE";
  protected static HashMap<String, Constant> map1 = new HashMap<String, Constant>();
  protected static HashMap<String, Constant> map2 = new HashMap<String, Constant>();
  static {
    ConstantBase.init(Country.class, map1);
    for (Constant obj1 : map1.values()) {
      Country obj = (Country) obj1;
      for (String name : obj.names) {
        map2.put(name.toUpperCase(), obj);
      }
    }

  }
  private String name = null;
  private String[] names = null;

  public String getCountry() {
    return name;
  }

  public static Set<Country> getCountrys() {
    Set set = new HashSet<Country>();
    set.addAll(map1.values());
    return set;
  }

  public static Country getCountry(String str) {
    if (str == null)
      return null;
    Country ret = (Country) map1.get(str);
    if (ret != null)
      return ret;
    ret = (Country) map2.get(str.toUpperCase());
    return ret;
  }

  @Override
  public void parse(String key, String value) {
    name = key;
    names = value.split(",");

  }

  @Override
  public String getDisplay() {

    return names[0];
  }
}
