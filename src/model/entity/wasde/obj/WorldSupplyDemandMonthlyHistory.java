package model.entity.wasde.obj;

import model.constant.Country;
import model.constant.WeightUnit;
import model.db.QueryUnit;
import model.db.ResetUnit;
import model.entity.CommodityRecord;
import model.entity.DaylyRecord;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tcc.utils.obj.generation.annotation.DB_FIELD;
import tcc.utils.obj.generation.annotation.DB_TABLE;

@DB_TABLE(name = "WorldSupplyDemandMonthlyHistory", alias = "wsd", tablespace = "ccdata")
public class WorldSupplyDemandMonthlyHistory extends CommodityRecord implements DaylyRecord, ResetUnit {

  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  private Country country;// 国家，主要生产国和出口国
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  String year;
  @DB_FIELD(type = DB_FIELD.TYPE_CHAR + "(15)", primary = true)
  long reportDate;
  @DB_FIELD(type = DB_FIELD.TYPE_INTEGER, primary = true)
  int reportStatus = 0;
  // 1 est ,2 proj
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

  public WorldSupplyDemandMonthlyHistory() {
  }

  public boolean isDataSame(WorldSupplyDemandMonthlyHistory one) {

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

  public boolean isLaterReportDate(WorldSupplyDemandMonthlyHistory one) {
    if (reportDate > one.reportDate) {
      return true;
    }
    return false;
  }

  public double getBeginStock() {
    return beginStock;
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

  public int getReportStatus() {
    return reportStatus;
  }

  public void setReportStatus(int reportStatus) {
    this.reportStatus = reportStatus;
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
    // TODO Auto-generated method stub
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
