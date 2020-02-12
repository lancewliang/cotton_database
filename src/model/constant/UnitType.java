package model.constant;

import model.constant.dao.Unit;

public enum UnitType {
  LENGTH("���ȵ�λ"), WEIGHT("������λ");
  String type;

  UnitType(String type) {
    this.type = type;
  }

  public String getUnitType() {
    return type;
  }

  public static UnitType getUnitType(String str) {
    if (str.equals("���ȵ�λ")) {
      return LENGTH;
    } else if (str.equals("������λ")) {
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
