package model.constant.dao;

import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import tcc.utils.log.LogService;

import model.constant.AreaUnit;
import model.constant.Bourse;
import model.constant.Commodity;
import model.constant.Country;
import model.constant.PortPriceType;
import model.constant.PriceUnit;
import model.constant.WeightUnit;

public class ConstantBase {

  public static void init(Class c, HashMap<String, Constant> map) {
    try {
      Properties p = ConstantDao.getProprity(c.getSimpleName());
      Set<Object> keys = p.keySet();
      for (Object k : keys) {
        String s = (String) k;
        String v = (String) p.get(s);
        try {

          Constant newOne = (Constant) c.newInstance();
          newOne.parse(s, v);
          map.put(s, newOne);
        } catch (Exception e) {
          LogService.trace(e, s + "|" + v);
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public static Constant getConstant(Class c, String str) {
    if (c.isInstance(AreaUnit.class) || c.equals(AreaUnit.class)) {
      return AreaUnit.getAreaUnit(str);
    } else if (c.isInstance(Bourse.class) || c.equals(Bourse.class)) {
      return Bourse.getBourse(str);
    } else if (c.isInstance(Commodity.class) || c.equals(Commodity.class)) {
      return Commodity.getCommodity(str);
    } else if (c.isInstance(Country.class) || c.equals(Country.class)) {
      return Country.getCountry(str);
    } else if (c.isInstance(PriceUnit.class) || c.equals(PriceUnit.class)) {
      return PriceUnit.getPriceUnit(str);
    } else if (c.isInstance(WeightUnit.class) || c.equals(WeightUnit.class)) {
      return WeightUnit.getWeightUnit(str);
    } else if (c.isInstance(PortPriceType.class) || c.equals(PortPriceType.class)) {
      return PortPriceType.getPortPriceType(str);
    }
    return null;
  }
}
