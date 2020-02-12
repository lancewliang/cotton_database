package ant.cotton.wasde;

import java.io.File;
import java.util.Date;

import model.constant.Commodity;
import model.constant.Country;
import model.constant.WeightUnit;
import model.entity.ReportStatus;
import model.entity.wasde.db.WorldSupplyDemandMonthlyHistorySQL;
import model.entity.wasde.obj.WorldSupplyDemandMonthlyHistory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ant.server.AntManger;

import tcc.utils.log.LogService;
import tcc.utils.xml.dom.DOMUtil;
import tcc.utils.xml.xpath.XmlOperator;

public class ParseXMLMotherProjReport {
  private String XML_matrix;
  private String XML_region_header;
  private String XML_mx_refion_group;
  private String XML_att_regions;
  private String XML_regions_col_begin;
  private String XML_regions_month_col_begin;
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
          ParseXMLMotherProjReport parse = new ParseXMLMotherProjReport();
          String reportDate = parse.getReportDate(f.getName());
          String monthShortName = parse.getMonthShortName(f.getName());
          // parse.doParseXML(doc, reportDate, monthShortName);
        }
      }

    } catch (Exception e) {

      e.printStackTrace();
    }
  }

  public void doParseXML(Document doc, long reportDate, String monthShortName) {
    Element reportEl = (Element) XmlOperator.selectSingleNode(doc, "/Report/sr27/Report[contains(@sub_report_title,'World Cotton Supply and Use')]");
    this.XML_matrix = "matrix2";
    this.XML_region_header = "region_header2";
    this.XML_mx_refion_group = "m1_region_group2_Collection/m1_region_group2";
    this.XML_att_regions = "region2";
    this.XML_regions_month_col_begin = "m1_month_group2_Collection/m1_month_group2";
    this.XML_regions_col_begin = "m1_attribute_group2_Collection/m1_attribute_group2";
    this.XML_regions_col_attribute = "attribute2";
    this.XML_regions_col_end = "FormatFiller5/Cell";
    this.cellvalue = "cell_value2";
    doParseYearReport(reportEl, Commodity.getCommodity("棉花"), reportDate, monthShortName);

  }

  private void doParseYearReport(Element reportEl, Commodity commodity, long reportDate, String monthShortName) {

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
    }
    years = years.replaceAll("Proj.", "").trim();
    years = years.replaceAll("Est.", "").trim();
    years = years.replaceAll("Est", "").trim();
    doParseYearReport(matrixEl, commodity, years, reportStatus, reportDate, monthShortName);
    //
  }

  private void doParseYearReport(Element matrixEl, Commodity commodity, String years, int reportStatus, long reportDate, String monthShortName) {
    Date now = new Date();
    NodeList region_group = XmlOperator.selectNodeList(matrixEl, XML_mx_refion_group);
    for (int i = 0; i < region_group.getLength(); i++) {
      Element region = (Element) region_group.item(i);
      Element monthgroup = (Element) XmlOperator.selectSingleNode(region, XML_regions_month_col_begin + "[@forecast_month2='" + monthShortName + "']");
      String areas = getAreas(region.getAttribute(XML_att_regions));
      double beginstock = getColumNumber(monthgroup, "Beginning Stocks");
      double production = getColumNumber(monthgroup, "Production");
      double imports = getColumNumber(monthgroup, "Imports");
      double uses = getColumNumber(monthgroup, "Domestic Use");
      double exports = getColumNumber(monthgroup, "Exports");
      double loss = getColumNumber(monthgroup, "Loss");
      double endstock = getColumNumber(monthgroup, "Ending Stocks");
      areas = areas.replaceAll("5/", "").trim();
      areas = areas.replaceAll("3/", "").trim();
      areas = areas.replaceAll("4/", "").trim();
      areas = areas.replaceAll("6/", "").trim();
      areas = areas.replaceAll("7/", "").trim();
      areas = areas.replaceAll("9/", "").trim();
      Country conutry = Country.getCountry(areas);
      if (conutry == null) {
        LogService.warn(years + "|" + reportDate + " no areas:" + areas);
        continue;
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

  private Element getColumElement(Element monthgroup, String name) {
    NodeList list = XmlOperator.selectNodeList(monthgroup, XML_regions_col_begin);
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
      String value = cell.getAttribute(cellvalue);
      try {

        ret = Double.parseDouble(value);
      } catch (Exception e) {
      }
    }
    return ret;
  }

  public String getReportDate(String filename) {
    String st = filename;
    st = st.replaceAll("wasde-", "");
    st = st.replaceAll(".xml", "");
    String[] strs = st.split("-");
    st = strs[2] + strs[0] + strs[1];

    return st;
  }

  public String getMonthShortName(String fileName) {
    String st = fileName;
    st = st.replaceAll("wasde-", "");
    st = st.replaceAll(".xml", "");
    String[] strs = st.split("-");
    String monthstr = strs[0];
    if (monthstr.equals("01")) {
      return "Jan";
    }
    if (monthstr.equals("02")) {
      return "Feb";
    }
    if (monthstr.equals("03")) {
      return "Mar";
    }
    if (monthstr.equals("04")) {
      return "Apr";
    }

    if (monthstr.equals("05")) {
      return "May";
    }
    if (monthstr.equals("06")) {
      return "Jun";
    }
    if (monthstr.equals("07")) {
      return "Jul";
    }
    if (monthstr.equals("08")) {
      return "Aug";
    }
    if (monthstr.equals("09")) {
      return "Sep";
    }
    if (monthstr.equals("10")) {
      return "Oct";
    }
    if (monthstr.equals("11")) {
      return "Nov";
    }
    if (monthstr.equals("12")) {
      return "Dec";
    }
    return null;
  }

  public String getSource() {
    return "USDA";
  }
}
