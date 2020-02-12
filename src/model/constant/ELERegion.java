package model.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.constant.dao.Constant;
import model.constant.dao.ConstantBase;

public class ELERegion implements Constant {
  protected static HashMap<String, Constant> map = new HashMap<String, Constant>();
  static {
    ConstantBase.init(ELERegion.class, map);

  }
  private String key = null;
  private String name = null;

  public String getKey() {
    return key;
  }

  public String getName() {
    return name;
  }

  @Override
  public void parse(String key, String value) {
    // TODO Auto-generated method stub
    this.key = key;
    name = value;
  }

  @Override
  public String getDisplay() {
    // TODO Auto-generated method stub
    return name;
  }

  public static List<ELERegion> getRegions() {
    List rets = new ArrayList<ELERegion>();
    rets.addAll(map.values());
    return rets;
  }

}
