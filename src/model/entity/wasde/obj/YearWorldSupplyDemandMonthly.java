package model.entity.wasde.obj;

import model.constant.Country;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "YearWorldSupplyDemandMonthly", alias = "wsd", tablespace = "ccdata")
public class YearWorldSupplyDemandMonthly extends CommodityRecord implements ResetUnit {
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 国家，主要生产国和出口国
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  String year;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)")
  long reportDate;
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER)
  int reportStatus = 0;
  // 1 est
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  double beginStock;
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  double production;
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  double imports;
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  double uses;
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  double exports;
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  double loss;
  @DB_FIELD(type = DB_FIELD.TYPE_FLOAT)
  double endStock;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)")
  WeightUnit weightUnit;// 计量单位

  public YearWorldSupplyDemandMonthly() {
  }

  //
  public int getReportStatus() {
    return reportStatus;
  }

  public void setReportStatus(int reportStatus) {
    this.reportStatus = reportStatus;
  }

  public double getBeginStock() {
    return beginStock;
  }

  public void setBeginStock(double beginStock) {
    this.beginStock = beginStock;
  }

  public double getProduction() {
    return production;
  }

  public void setProduction(double production) {
    this.production = production;
  }

  public double getImports() {
    return imports;
  }

  public void setImports(double imports) {
    this.imports = imports;
  }

  public double getUses() {
    return uses;
  }

  public void setUses(double use) {
    this.uses = use;
  }

  public double getExports() {
    return exports;
  }

  public void setExports(double export) {
    this.exports = export;
  }

  public double getLoss() {
    return loss;
  }

  public void setLoss(double loss) {
    this.loss = loss;
  }

  public double getEndStock() {
    return endStock;
  }

  public void setEndStock(double endStock) {
    this.endStock = endStock;
  }

  public boolean isDataSame(YearWorldSupplyDemandMonthly one) {

    if (beginStock != one.beginStock) {
      return false;
    }
    if (production != one.production) {
      return false;
    }
    if (imports != one.imports) {
      return false;
    }
    if (uses != one.uses) {
      return false;
    }
    if (exports != one.exports) {
      return false;
    }
    if (loss != one.loss) {
      return false;
    }
    if (endStock != one.endStock) {
      return false;
    }
    return true;
  }

  public boolean isLaterReportDate(YearWorldSupplyDemandMonthly one) {
    if (reportDate > one.reportDate) {
      return true;
    }
    return false;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  public String getYear() {
    return year;
  }

  public void setYear(String year) {
    this.year = year;
  }

  public long getReportDate() {
    return reportDate;
  }

  public void setReportDate(long reportDate) {
    this.reportDate = reportDate;
  }

  public WeightUnit getWeightUnit() {
    return weightUnit;
  }

  public void setWeightUnit(WeightUnit weightUnit) {
    this.weightUnit = weightUnit;
  }

  public Element toElement(Document doc) {
    Element usadEl = doc.createElement("USAD");
    usadEl.setAttribute("production", "" + production);
    usadEl.setAttribute("reportDate", "" + reportDate);
    usadEl.setAttribute("beginStock", "" + beginStock);
    usadEl.setAttribute("imports", "" + imports);
    usadEl.setAttribute("uses", "" + (long) uses);
    usadEl.setAttribute("exports", "" + exports);
    usadEl.setAttribute("loss", "" + loss);
    usadEl.setAttribute("endStock", "" + endStock);
    usadEl.setAttribute("useRatio", "" + Math.round((endStock / uses) * 100));
    return usadEl;
  }

  @Override
  public boolean ignoreSave() {

    return false;
  }

  @Override
  public void reSetUnit(QueryUnit unit) {
    if (unit.weightUnit != null) {
      production = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, production);
      beginStock = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, beginStock);
      endStock = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, endStock);
      imports = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, imports);
      exports = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, exports);
      uses = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, uses);
      loss = WeightUnit.reSetWeightUnit(this.weightUnit, unit.weightUnit, loss);
      this.weightUnit = unit.weightUnit;
    }
  }
}
