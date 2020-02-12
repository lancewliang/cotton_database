package export.mapping.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.constant.Commodity;
import model.db.QueryUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import tcc.utils.None;
import tcc.utils.log.LogService;
import tcc.utils.xml.xpath.XmlOperator;
import ant.server.AntManger;
import ant.server.DayAnt;
import export.mapping.report.dimension.CommodityDimension;
import export.mapping.report.dimension.CountryDimension;
import export.mapping.report.dimension.ModeDimension;
import export.mapping.report.dimension.TimeDimension;
import export.mapping.report.dimension.TimeDimensionQueryCondition;
import export.mapping.report.dimension.TimeDimension.Dimension;
import export.mapping.report.field.Chart;
import export.mapping.report.field.ReportExpressionField;
import export.mapping.report.field.ReportField;
import export.mapping.report.field.ReportModeDimensionField;
import export.mapping.report.field.ReportObjectField;
import export.mapping.report.field.ReportTimeDimensionField;

public class DimensionReportMappingParser {
  public static List<ReportMapping> load(Document doc) {
    List<ReportMapping> reports = new ArrayList<ReportMapping>();
    try {
      NodeList reportELs = XmlOperator.selectNodeList(doc, "//report");
      for (int i = 0; i < reportELs.getLength(); i++) {
        ReportMapping reportMapping = load((Element) reportELs.item(i));
        reports.add(reportMapping);
      }

    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return reports;
  }

  private static ReportMapping load(Element reportEL) {
    try {

      String label = reportEL.getAttribute("label");

      DimensionReportMapping reportMapping = new DimensionReportMapping(label, getTimeDimension(reportEL), getQueryUnit(reportEL));
      String commodityDimensionStr = reportEL.getAttribute("commodity-dimension");

      String countryDimensionStr = reportEL.getAttribute("country-dimension");
      if (None.isNonBlank(commodityDimensionStr)) {
        reportMapping.commodityDimension = new CommodityDimension(commodityDimensionStr);
      }
      if (None.isNonBlank(countryDimensionStr)) {
        reportMapping.countryDimension = new CountryDimension(countryDimensionStr);
      }
      load(reportMapping, reportEL);

      reportMapping.ant = getDayAnt(reportEL);
      loadModeDimension(reportMapping, reportEL);
      return reportMapping;

    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return null;
  }

  private static void loadModeDimension(DimensionReportMapping reportMapping, Element reportEL) throws Exception {
    NodeList items = XmlOperator.selectNodeList(reportEL, "model-dimension");
    if (items != null) {
      for (int f = 0; f < items.getLength(); f++) {
        Element fieldEl = (Element) items.item(f);

        String label = fieldEl.getAttribute("label");
        String condition = fieldEl.getAttribute("condition");
        String conditionlabel = fieldEl.getAttribute("conditionlabel");
        String value = fieldEl.getAttribute("value");
        String source = fieldEl.getAttribute("source");
        String model = fieldEl.getAttribute("model");
        String key = fieldEl.getAttribute("key");
        ModeDimension modeDimension = new ModeDimension(key, label, model, condition, conditionlabel, value, source);
        reportMapping.modeDimensionMap.put(model, modeDimension);
      }
    }
  }

  private static TimeDimension getTimeDimension(Element reportEL) throws Exception {
    Element obj = (Element) XmlOperator.selectSingleNode(reportEL, "time-dimension");
    String timeDimensionStr = obj.getAttribute("time-dimension");
    if ("DaylyQuery".equals(timeDimensionStr) || "MonthlyQuery".equals(timeDimensionStr)) {

      List<TimeDimensionQueryCondition> conditions = new ArrayList<TimeDimensionQueryCondition>();

      SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

      Date reportStartDate = null;
      Date reportEndDate = null;
      if (None.isNonBlank(obj.getAttribute("reportStartDate"))) {
        reportStartDate = dateformat.parse(obj.getAttribute("reportStartDate"));
      }

      if (None.isNonBlank(obj.getAttribute("reportEndDate"))) {
        reportEndDate = dateformat.parse(obj.getAttribute("reportEndDate"));
      }

      NodeList csEl = XmlOperator.selectNodeList(obj, "q-condition");
      if (csEl != null && csEl.getLength() > 0) {
        for (int f = 0; f < csEl.getLength(); f++) {

          Element cEl = (Element) csEl.item(f);

          String model = cEl.getAttribute("model");
          String condition = cEl.getAttribute("condition");
          String source = cEl.getAttribute("source");
          String commodity = cEl.getAttribute("commodity");
          TimeDimensionQueryCondition con = new TimeDimensionQueryCondition();
          con.qCondition = condition;
          con.qModel = model;
          con.qSource = source;
          if (None.isNonBlank(commodity)) {
            con.fCommodity = Commodity.getCommodity(commodity);
          }
          conditions.add(con);

        }
      } else {
        String model = obj.getAttribute("model");
        String condition = obj.getAttribute("condition");
        String source = obj.getAttribute("source");
        TimeDimensionQueryCondition con = new TimeDimensionQueryCondition();
        con.qCondition = condition;
        con.qModel = model;
        con.qSource = source;
        conditions.add(con);
      }

      if ("MonthlyQuery".equals(timeDimensionStr)) {
        return new TimeDimension(Dimension.MONTH, conditions, reportStartDate, reportEndDate);
      } else if ("DaylyQuery".equals(timeDimensionStr)) {
        return new TimeDimension(Dimension.DAY, conditions, reportStartDate, reportEndDate);
      }
      return null;

    } else {
      SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");

      Date reportStartDate = null;
      Date reportEndDate = null;
      if (None.isNonBlank(obj.getAttribute("reportStartDate"))) {
        reportStartDate = dateformat.parse(obj.getAttribute("reportStartDate"));
      }

      if (None.isNonBlank(obj.getAttribute("reportEndDate"))) {
        reportEndDate = dateformat.parse(obj.getAttribute("reportEndDate"));
      }
      return new TimeDimension(timeDimensionStr, reportStartDate, reportEndDate);
    }
  }

  private static DayAnt getDayAnt(Element reportEL) {
    Element obj = (Element) XmlOperator.selectSingleNode(reportEL, "ant");
    if (obj != null) {
      return AntManger.getAnt(obj.getAttribute("name"));
    } else {
      return null;
    }
  }

  private static QueryUnit getQueryUnit(Element reportEL) {
    Element obj = (Element) XmlOperator.selectSingleNode(reportEL, "QueryUnit");
    if (obj != null) {
      return new QueryUnit(obj.getAttribute("WeightUnit"), obj.getAttribute("LengthUnit"), obj.getAttribute("PriceUnit"));
    } else {
      return null;
    }
  }

  private static void load(DimensionReportMapping reportMapping, Element reportEL) {
    NodeList fieldEls = XmlOperator.selectNodeList(reportEL, "Field");
    for (int f = 0; f < fieldEls.getLength(); f++) {
      Element fieldEl = (Element) fieldEls.item(f);

      String col = fieldEl.getAttribute("col");
      String type = fieldEl.getAttribute("type");
      String label = fieldEl.getAttribute("label");
      String condition = fieldEl.getAttribute("condition");
      String conditionlabel = fieldEl.getAttribute("conditionlabel");
      String value = fieldEl.getAttribute("value");
      String source = fieldEl.getAttribute("source");
      String model = fieldEl.getAttribute("model");
      ReportField field = null;
      if (ReportTimeDimensionField.TYPE.equals(type)) {
        field = new ReportTimeDimensionField(col, label);
      } else if (ReportExpressionField.TYPE.equals(type)) {
        field = new ReportExpressionField(col, label, value);
      } else if (ReportObjectField.TYPE.equals(type)) {
        String country = fieldEl.getAttribute("country");
        String commodity = fieldEl.getAttribute("commodity");
        field = new ReportObjectField(col, label, model, condition, conditionlabel, value, commodity, country, source);
      } else if (ReportModeDimensionField.TYPE.equals(type)) {
        field = new ReportModeDimensionField(model, col);
      }
      if (field != null)
        reportMapping.fileds.add(field);
    }
    NodeList chartEls = XmlOperator.selectNodeList(reportEL, "Chart");
    for (int f = 0; f < chartEls.getLength(); f++) {
      Element fieldEl = (Element) chartEls.item(f);
      String expression = fieldEl.getAttribute("expression");
      Chart chart = new Chart();
      chart.setValue(expression);
      reportMapping.charts.add(chart);
    }

  }
}
