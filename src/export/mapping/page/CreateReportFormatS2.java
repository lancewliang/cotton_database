package export.mapping.page;

import java.io.File;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.constant.dao.ConstantDao;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tcc.utils.json.XML2JSON;
import tcc.utils.session.SessionObj;
import tcc.utils.xml.dom.DOMUtil;
import tcc.webfw.page.Page;
import export.mapping.report.ReportFieldMappingInfo;
import export.mapping.report.ReportMappings;

public class CreateReportFormatS2 extends Page {
  @Override
  public String processCommand(String command, SessionObj session, HttpServletRequest request, HttpServletResponse response) {
    String pfx = Page.getPagePrefix(request);
    session.setPageSessionValue(pfx + ".reportType", request.getParameter("reportType"));
    if ("new".equals(command)) {
      session.setPageSessionValue(pfx + ".reportLabel", request.getParameter("reportLabel"));
      session.setPageSessionValue(pfx + ".reportEndDate", request.getParameter("reportEndDate"));
      session.setPageSessionValue(pfx + ".reportStartDate", request.getParameter("reportStartDate"));
      session.setPageSessionValue(pfx + ".time_dimension", request.getParameter("time_dimension"));
      session.setPageSessionValue(pfx + ".commodity_dimension", request.getParameter("commodity_dimension"));
      session.setPageSessionValue(pfx + ".country_dimension", request.getParameter("country_dimension"));
    }
    return super.processCommand(command, session, request, response);
  }

  @Override
  public void print(PrintWriter arg0, SessionObj session, String pfx, String arg3, String arg4) {

    try {
      CreateReportFormatS2.printReportFormatMapping(session, pfx);
      CreateReportFormatS2.printConstant(session, pfx);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    super.print(arg0, session, pfx, arg3, arg4);
  }

  public static void printReportFormatMapping(SessionObj session, String pfx) throws Exception {
    Document doc = ReportFieldMappingInfo.getDoc();
    String json = XML2JSON.ConvertXMLtoJSON(DOMUtil.doc2XML(doc));
    String js = "var fieldMappingData = " + json + ";";
    session.setPageSessionValue(pfx + ".pfjs", js);

  }

  public static void printConstant(SessionObj session, String pfx) throws Exception {
    File[] files = ConstantDao.basePaht.listFiles();
    for (File f : files) {
      if (f.isFile() && f.getName().endsWith("properties")) {
        Properties p = ConstantDao.getProprity(f);
        Set<Object> keys = p.keySet();
        Document doc = DOMUtil.createDocument();
        Element elementRoot = doc.createElement("root");
        doc.appendChild(elementRoot);
        for (Object k : keys) {
          String s = (String) k;
          String v = (String) p.get(s);
          Element obj = doc.createElement("obj");
          obj.setAttribute("value", v.split(",")[0]);
          obj.setAttribute("label", s);
          elementRoot.appendChild(obj);
        }
        String json = XML2JSON.ConvertXMLtoJSON(DOMUtil.doc2XML(doc));
        String js = "var " + f.getName().split("\\.")[0] + "={};" + f.getName() + "=" + json + ";";
        session.setPageSessionValue(pfx + "." + f.getName(), js);
      }
    }
  }

}
