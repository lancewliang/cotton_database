package export.mapping.report.dimension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.constant.Commodity;
import model.constant.Country;
import model.entity.consumption.db.ConsumptionYearSQL;
import model.entity.production.country.db.YieldYearSQL;

public class CountryDimension implements ReportDimension {
  public static String ALL_Country = "ALL";
  private String country_dimensionSTR = null;
  private List<Country> country_dimensions;
  private boolean isAll = false;

  public CountryDimension(String country_dimension) throws Exception {

    if (ALL_Country.equals(country_dimension)) {
      isAll = true;
    } else {
      this.country_dimensions = new ArrayList();
      if (country_dimension.indexOf(",") != -1) {
        String[] ss = country_dimension.split(",");
        for (String s : ss) {
          country_dimensions.add(Country.getCountry(s));
          if (this.country_dimensions == null)
            throw new Exception("not found country_dimension :" + country_dimension);
        }
      } else {
        country_dimensions.add(Country.getCountry(country_dimension));
        if (this.country_dimensions == null)
          throw new Exception("not found country_dimension :" + country_dimension);
      }
    }
    country_dimensionSTR = country_dimension;
  }

  public List<Country> getCountrys() {
    return country_dimensions;
  }

  public boolean isAll() {
    return isAll;
  }

  public String getCountry_dimensionSTR() {
    return country_dimensionSTR;
  }

  public List<Country> getAllCountrys() {
    List<Country> list = new ArrayList<Country>();
    list.addAll(Country.getCountrys());
    return list;
  }

  public List<Country> getCountrys(Commodity c) {
    List<Country> list = new ArrayList<Country>();
    Map<String, Country> map = new HashMap<String, Country>();
    if (isAll) {
      List list1 = YieldYearSQL.getCountrys(c);
      List list2 = ConsumptionYearSQL.getCountrys(c);
      getCountrys(list1, map);
      getCountrys(list2, map);
      list.addAll(map.values());
    } else {
      list.addAll(country_dimensions);
    }
    return list;
  }

  private void getCountrys(List<Country> checkList, Map<String, Country> map) {
    for (Country c : checkList) {
      if (map.get(c.getCountry()) != null) {

      } else {
        map.put(c.getCountry(), c);

      }
    }
  }
}
