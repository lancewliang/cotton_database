package model.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import model.constant.dao.Constant;
import model.constant.dao.ConstantBase;

public class WeatherRegion implements Constant {
  String name;
  String key;
  String contry;
  int number1;
  int number2;

  //
  public String getWeatherRegion() {
    return name;
  }

  public String getKey() {
    return key;
  }

  public int getNumber1() {
    return number1;
  }

  public int getNumber2() {
    return number2;
  }

  @Override
  public void parse(String key, String value) {
    // TODO Auto-generated method stub
    name = key;
    String[] values = value.split(",");

    this.key = values[0];

    contry = values[1];
    number1 = Integer.parseInt(values[2]);
    number2 = Integer.parseInt(values[3]);
  }

  public String getContry() {
    return contry;
  }

  @Override
  public String getDisplay() {
    return name;
  }

  protected static HashMap<String, Constant> map = new HashMap<String, Constant>();

  static {
    ConstantBase.init(WeatherRegion.class, map);
  }

  public static List<WeatherRegion> getWeatherRegions() {
    List rets = new ArrayList<WeatherRegion>();
    rets.addAll(map.values());
    return rets;
  }

  public static WeatherRegion getWeatherRegion(String str) {
    if (str == null)
      return null;
    return (WeatherRegion) map.get(str);
  }
}
