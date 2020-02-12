package model.entity.custom.country;

import model.entity.YearlyRecord;

public class ImportYear extends ImportExportMonth implements YearlyRecord {
  private String year;

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }
 
}
