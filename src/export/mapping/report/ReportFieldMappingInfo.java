package export.mapping.report;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tcc.utils.None;
import tcc.utils.log.LogService;
import tcc.utils.xml.dom.DOMUtil;
import tcc.utils.xml.xpath.XmlOperator;

public class ReportFieldMappingInfo {
  static Document doc = null;
  static {

    try {
      doc = DOMUtil.inputStreamDoc(ReportMappings.class.getResourceAsStream("report-field-mapping.xml"));
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static Document getDoc() throws Exception {
    return doc;
  }

  public static String getValueName(String time_dimension, String mode_class, String value) {
    synchronized (doc) {
      try {
        Element modelEl = (Element) XmlOperator.selectSingleNode(doc, "//models[@time_dimension='" + time_dimension + "']/model[@model_class='" + mode_class + "']");
        if (None.isBlank(value) || "value".equals(value)) {
          return modelEl.getAttribute("label");
        } else if (None.isNonBlank(value)) {
          Element fieldEl = (Element) XmlOperator.selectSingleNode(modelEl, "field[@attr='" + value + "']");
          return fieldEl.getAttribute("label");
        }
      } catch (RuntimeException e) {
        LogService.trace(e, time_dimension + "," + mode_class + "," + value);
        throw e;
      }
    }

    return null;
  }
}
