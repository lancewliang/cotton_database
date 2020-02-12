package model.constant;

import model.constant.dao.Unit;

public enum UnitType {
  LENGTH("长度单位"), WEIGHT("重量单位");
  String type;

  UnitType(String type) {
    this.type = type;
  }

  public String getUnitType() {
    return type;
  }

  public static UnitType getUnitType(String str) {
    if (str.equals("长度单位")) {
      return LENGTH;
    } else if (str.equals("重量单位")) {
      return WEIGHT;
    }
    return null;
  }

  public Unit getUnit(String unit) {
    if (this.equals(LENGTH)) {
      return LengthUnit.getLengthUnit(unit);
    } else if (this.equals(WEIGHT)) {
      return WeightUnit.getWeightUnit(unit);
    }
    return null;
  }
}
