package model.constant;

import java.util.HashMap;

import model.constant.dao.Constant;
import model.constant.dao.ConstantBase;

//½»Ò×Ëù

public class Bourse implements Constant {
  protected static HashMap<String, Constant> map = new HashMap<String, Constant>();

  static {
    ConstantBase.init(Bourse.class, map);
  }
  private String name = null;

  public String getBourse() {
    return name;
  }

  public static Bourse getBourse(String str) {
    if (str == null)
      return null;
    return (Bourse) map.get(str);
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
