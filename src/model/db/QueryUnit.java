package model.db;

import tcc.utils.None;
import model.constant.LengthUnit;
import model.constant.PriceUnit;
import model.constant.WeightUnit;

public class QueryUnit {
  public WeightUnit weightUnit = null;
  public LengthUnit lengthUnit = null;
  public PriceUnit priceUnit = null;

  //

  public QueryUnit() {
    weightUnit = WeightUnit.getWeightUnit("Íò¶Ö");
    lengthUnit = LengthUnit.getLengthUnit("Ã×");
    priceUnit = PriceUnit.getPriceUnit("USD");
  }

  public QueryUnit(String weightUnitSTR, String lengthUnitSTR, String priceUnitSTR) {
    weightUnit = WeightUnit.getWeightUnit(weightUnitSTR);
    lengthUnit = LengthUnit.getLengthUnit(lengthUnitSTR);
    if (None.isNonBlank(priceUnitSTR))
      priceUnit = PriceUnit.getPriceUnit(priceUnitSTR);
  }
}
