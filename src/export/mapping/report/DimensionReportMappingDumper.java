package export.mapping.report;

import java.text.SimpleDateFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tcc.utils.None;
import tcc.utils.xml.dom.DOMUtil;
import export.mapping.report.dimension.CountryDimension;
import export.mapping.report.field.ReportField;

public class DimensionReportMappingDumper {

  public static Document dump(DimensionReportMapping reportMapping) {
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
    Document doc = DOMUtil.createDocument();
    Element reportEl = doc.createElement("report");
    doc.appendChild(reportEl);
    reportEl.setAttribute("label", reportMapping.label);
    if (reportMapping.commodityDimension != null) {
      reportEl.setAttribute("commodity-dimension", reportMapping.commodityDimension.getCommodity().getCommodity());
    }
    if (reportMapping.countryDimension != null) {
      reportEl.setAttribute("country-dimension", reportMapping.countryDimension.getCountry_dimensionSTR());
    }

    Element timeEl = doc.createElement("time-dimension");
    timeEl.setAttribute("time-dimension", reportMapping.timeDimension.time_dimension.toString().toLowerCase());
    if (reportMapping.timeDimension.reportStartDate != null) {
      timeEl.setAttribute("reportStartDate", dateformat.format(reportMapping.timeDimension.reportStartDate));
    }
    if (reportMapping.timeDimension.reportEndDate != null) {
      timeEl.setAttribute("reportEndDate", dateformat.format(reportMapping.timeDimension.reportEndDate));
    }
    reportEl.appendChild(timeEl);
    if (reportMapping.unit != null) {
      Element queryUnitEl = doc.createElement("QueryUnit");
      queryUnitEl.setAttribute("WeightUnit", reportMapping.unit.weightUnit.getWeightUnit());
      queryUnitEl.setAttribute("LengthUnit", reportMapping.unit.lengthUnit.getLengthUnit());
      reportEl.appendChild(queryUnitEl);
    }
    for (ReportField filed : reportMapping.fileds) {
      Element fieldEl = doc.createElement("Field");
      fieldEl.setAttribute("col", filed.getCol());

      fieldEl.setAttribute("type", filed.getType());
      filed.toElement(fieldEl);
      reportEl.appendChild(fieldEl);
    }

    return doc;
  }

  public static void setAttribute(Element ele, String name, String value) {
    if (None.isNonBlank(value)) {
      ele.setAttribute(name, value);
    }
  }
}
