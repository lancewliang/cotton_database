package ant.cotton.wasde;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.ReportStatus;
import model.entity.wasde.db.WorldSupplyDemandMonthlyHistorySQL;
import model.entity.wasde.db.YearWorldSupplyDemandMonthlySQL;
import model.entity.wasde.obj.WorldSupplyDemandMonthlyHistory;
import model.entity.wasde.obj.YearWorldSupplyDemandMonthly;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ant.server.AntManger;

import tcc.utils.log.LogService;
import tcc.utils.xml.dom.DOMUtil;
import tcc.utils.xml.xpath.XmlOperator;

public class ParseXMLYearReport {

  private String XML_matrix;
  private String XML_region_header;
  private String XML_mx_refion_group;
  private String XML_att_regions;
  private String XML_regions_col_begin;
  private String XML_regions_col_attribute;
  private String XML_regions_col_end;
  private String cellvalue;

  public static void main(String[] args) {
    LogService.setQuiet(true);
    LogService.setQuiet(LogService.ERR, true);
    LogService.setQuiet(LogService.TRACE, true);
    File folder = new File("C:\\Users\\wliang\\Desktop\\期货\\data\\usad\\wasde-xml");
    try {
      File[] yearfs = folder.listFiles();
      for (File yf : yearfs) {
        File[] fs = yf.listFiles();
        for (File f : fs) {
          Document doc = DOMUtil.file2Doc(f);
          ParseXMLYearReport parse = new ParseXMLYearReport();
          long reportDate = Long.parseLong(parse.getReportDate(f.getName()));
          parse.doParseXML(doc, reportDate);
        }
      }

    } catch (Exception e) {

      e.printStackTrace();
    }
  }

  public String getReportDate(String st) {
    st = st.replaceAll("wasde-", "");
    st = st.replaceAll(".xml", "");
    String[] strs = st.split("-");
    st = strs[2] + strs[0] + strs[1];

    return st;
  }

  public void doParseXML(Document doc, long reportDate) {
    Element reportEl = (Element) XmlOperator.selectSingleNode(doc, "/Report/sr26/Report[contains(@sub_report_title,'World Cotton Supply and Use')]");
    this.XML_matrix = "matrix3";
    this.XML_region_header = "region_header3";
    this.XML_mx_refion_group = "m1_region_group2_Collection/m1_region_group2";
    this.XML_att_regions = "region3";
    this.XML_regions_col_begin = "m1_attribute_group2_Collection/m1_attribute_group2";
    this.XML_regions_col_attribute = "attribute3";
    this.XML_regions_col_end = "FormatFiller2/Cell";
    this.cellvalue = "cell_value3";
    doParseYearReport(reportEl, Commodity.getCommodity("棉花"), reportDate, 0);

    this.XML_matrix = "matrix4";
    this.XML_region_header = "region_header4";
    this.XML_mx_refion_group = "m2_region_group2_Collection/m2_region_group2";
    this.XML_att_regions = "region4";
    this.XML_regions_col_attribute = "attribute4";
    this.XML_regions_col_begin = "m2_attribute_group2_Collection/m2_attribute_group2";
    this.XML_regions_col_end = "FormatFiller6/Cell";
    this.cellvalue = "cell_value4";
    doParseYearReport(reportEl, Commodity.getCommodity("棉花"), reportDate, 1);

  }

  private void doParseYearReport(Element reportEl, Commodity commodity, long reportDate, int reportstatus) {

    Element matrixEl = (Element) XmlOperator.selectSingleNode(reportEl, XML_matrix);
    String years = matrixEl.getAttribute(XML_region_header);
    years = years.trim();
    int reportStatus = ReportStatus.NORMAL;
    if (years.indexOf("Proj.") != -1) {
      reportStatus = ReportStatus.PROJ;
      // est
    } else if (years.indexOf("Est") != -1) {
      reportStatus = ReportStatus.EST;
      // est
    } else {
      reportStatus = reportstatus;
      if (reportstatus == ReportStatus.EST) {

      }
    }
    years = years.replaceAll("Proj.", "").trim();
    years = years.replaceAll("Est.", "").trim();
    years = years.replaceAll("Est", "").trim();
    doParseYearReport(matrixEl, commodity, years, reportStatus, reportDate);
    //
  }

  private void doParseYearReport(Element matrixEl, Commodity commodity, String years, int reportStatus, long reportDate) {
    Date now = new Date();
    NodeList region_group = XmlOperator.selectNodeList(matrixEl, XML_mx_refion_group);
    for (int i = 0; i < region_group.getLength(); i++) {
      Element region = (Element) region_group.item(i);
      String areas = getAreas(region.getAttribute(XML_att_regions));
      double beginstock = getColumNumber(region, "Beginning Stocks");
      double production = getColumNumber(region, "Production");
      double imports = getColumNumber(region, "Imports");
      double uses = getColumNumber(region, "Domestic Use");
      double exports = getColumNumber(region, "Exports");
      double loss = getColumNumber(region, "Loss");
      double endstock = getColumNumber(region, "Ending Stocks");
      areas = areas.replaceAll("5/", "").trim();
      areas = areas.replaceAll("3/", "").trim();
      areas = areas.replaceAll("4/", "").trim();
      areas = areas.replaceAll("6/", "").trim();
      areas = areas.replaceAll("7/", "").trim();
      areas = areas.replaceAll("8/", "").trim();
      areas = areas.replaceAll("9/", "").trim();
      Country conutry = Country.getCountry(areas);
      if (conutry == null) {
        LogService.warn(years + "|" + reportDate + " no areas:" + areas);
        continue;
      }
      try {
        boolean todo = true;
        YearWorldSupplyDemandMonthly record = YearWorldSupplyDemandMonthlySQL.getObj(conutry, years, commodity, getSource());
        if (record == null) {
          record = new YearWorldSupplyDemandMonthly();

          record.setCountry(conutry);
          record.setYear(years);
          record.setCommodity(commodity);
          record.setSource(getSource());

        } else {
          if (reportDate < record.getReportDate()) {
            todo = false;
          }
        }
        record.setReportStatus(reportStatus);
        record.setReportDate(reportDate);
        record.setBeginStock(beginstock);
        record.setProduction(production);
        record.setImports(imports);
        record.setExports(exports);
        record.setUses(uses);
        record.setLoss(loss);
        record.setEndStock(endstock);
        record.setUpdatedAt(now);
        record.setUpdatedBy(AntManger.UPDATEBY);
        record.setWeightUnit(WeightUnit.getWeightUnit("百万包,480 pounds"));
        if (todo) {
          YearWorldSupplyDemandMonthlySQL.save(record);
        }
      } catch (SQLException e) {
        LogService.trace(e, years + "|" + reportDate);
      }
      try {
        WorldSupplyDemandMonthlyHistory record = WorldSupplyDemandMonthlyHistorySQL.getObj(conutry, years, reportDate, reportStatus, commodity, getSource());
        if (record == null) {
          record = new WorldSupplyDemandMonthlyHistory();
          record.setReportDate(reportDate);
          record.setCountry(conutry);
          record.setYear(years);
          record.setCommodity(commodity);
          record.setSource(getSource());

        }
        record.setReportStatus(reportStatus);
        record.setBeginStock(beginstock);
        record.setProduction(production);
        record.setImports(imports);
        record.setExports(exports);
        record.setUses(uses);
        record.setLoss(loss);
        record.setEndStock(endstock);
        record.setUpdatedAt(now);
        record.setUpdatedBy(AntManger.UPDATEBY);
        record.setWeightUnit(WeightUnit.getWeightUnit("百万包,480 pounds"));
        if (!WorldSupplyDemandMonthlyHistorySQL.isSameWithLast(record)) {
          WorldSupplyDemandMonthlyHistorySQL.save(record);
        }
      } catch (Exception e) {
        LogService.trace(e, years + "|" + reportDate);
      }
    }
  }

  private String getAreas(String name) {
    name = name.replaceAll("\r\n", " ");
    name = name.trim();
    return name;
  }

  private Element getColumElement(Element region, String name) {
    NodeList list = XmlOperator.selectNodeList(region, XML_regions_col_begin);
    for (int i = 0; i < list.getLength(); i++) {
      Element m2_attribute_group2 = (Element) list.item(i);
      String att = m2_attribute_group2.getAttribute(XML_regions_col_attribute);

      att = att.replaceAll("\r\n", " ");
      if (att.equals(name) || att.indexOf(name) != -1) {
        return m2_attribute_group2;
      }
    }
    return null;
  }

  private double getColumNumber(Element region, String name) {
    Element col = getColumElement(region, name);
    Element cell = (Element) XmlOperator.selectSingleNode(col, XML_regions_col_end);
    double ret = 0;
    if (cell != null) {
      try {
        ret = Double.parseDouble(cell.getAttribute(cellvalue));
      } catch (Exception e) {
      }
    }
    return ret;
  }

  public String getSource() {
    return "USDA";
  }
}
