package export.mapping.page;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.constant.Commodity;
import model.db.InstructionStatus;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tcc.utils.None;
import tcc.utils.log.LogService;
import tcc.utils.session.SessionObj;
import tcc.utils.xml.dom.DOMUtil;
import tcc.webfw.page.Page;
import datafeed.db.DataFeedSQL;
import export.mapping.db.ExportTaskStatusSQL;
import export.mapping.excell.OutputReports;
import export.mapping.report.ReportMappings;

public class ListReportFormatAjax extends Page {
  String ALLREPORT = "ALLREPORT";

  @Override
  public String processCommand(String command, SessionObj session, HttpServletRequest request, HttpServletResponse response) {
    String pfx = Page.getPagePrefix(request);
    try {

      String commodity = request.getParameter("commodity");
      Commodity commodityObj = Commodity.getCommodity(commodity);
      String scopeSTR = request.getParameter("scope");
      String typeSTR = request.getParameter("type");

      if ("updateReport".equals(command)) {
        if (ALLREPORT.equals(typeSTR) && ALLREPORT.equals(scopeSTR)) {
          ExportTaskLogic.addTasks(ReportMappings.TYPE_COMMON, commodityObj);
          ExportTaskLogic.addTasks(commodity, commodityObj);
        } else {
          if (ALLREPORT.equals(scopeSTR)) {
            ExportTaskLogic.addTasks(typeSTR, commodityObj);
          } else {
            ExportTaskLogic.addTask(typeSTR, scopeSTR, commodityObj);
          }
        }

      } else if ("clearReport".equals(command)) {
        if (ALLREPORT.equals(scopeSTR)) {
          OutputReports.clearReports(commodityObj);
        } else {
          OutputReports.clearReports(scopeSTR, commodityObj);
        }
      } else if ("deleteReportFormat".equals(command)) {

        OutputReports.clearReports(scopeSTR, commodityObj);
        ReportMappings.removeReportFile(commodityObj.getCommodity(), scopeSTR);
      }

      print(pfx, commodityObj, session);
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return super.processCommand(command, session, request, response);
  }

  private void print(String pfx, Commodity commodity, SessionObj session) {
    try {
      Document doc = DOMUtil.createDocument();

      Element rootEl = doc.createElement("root");
      rootEl.setAttribute("reportType", commodity.getCommodity());
      doc.appendChild(rootEl);
      Element commonEl = doc.createElement("common");
      commonEl.setAttribute("type", ReportMappings.TYPE_COMMON);
      rootEl.appendChild(commonEl);
      Element commodityEl = doc.createElement("commodity");
      commodityEl.setAttribute("type", "commodity");
      rootEl.appendChild(commodityEl);
      List<String> c_list = ReportMappings.getReports(ReportMappings.TYPE_COMMON);
      for (String format : c_list) {
        Element formatEl = doc.createElement("format");
        formatEl.setAttribute("name", format);
        commonEl.appendChild(formatEl);
        List<File> com_output_list = OutputReports.getReports(commodity, format);

        for (File co : com_output_list) {
          Element outputEl = doc.createElement("output");
          outputEl.setAttribute("name", co.getName());
          outputEl.setAttribute("createdAt", (new Date(co.lastModified())).toLocaleString());

          formatEl.appendChild(outputEl);
        }
        setStatus(ReportMappings.TYPE_COMMON, commodity, format, formatEl);
      }
      List<String> com_list = ReportMappings.getReports(commodity.getCommodity());
      for (String format : com_list) {
        Element formatEl = doc.createElement("format");
        formatEl.setAttribute("name", format);
        commodityEl.appendChild(formatEl);

        List<File> com_output_list = OutputReports.getReports(commodity, format);
        for (File co : com_output_list) {
          Element outputEl = doc.createElement("output");
          outputEl.setAttribute("name", co.getName());
          outputEl.setAttribute("createdAt", (new Date(co.lastModified())).toLocaleString());

          formatEl.appendChild(outputEl);
        }
        setStatus(ReportMappings.TYPE_commodity, commodity, format, formatEl);
      }
      session.setPageSessionValue(pfx + ".pfdoc", doc);

      // System.out.println(DOMUtil.doc2XML(doc));
      if (DataFeedSQL.hasStatus(InstructionStatus.STATUS_PROCESS) || DataFeedSQL.hasStatus(InstructionStatus.STATUS_START)) {
        session.setPageSessionValue(pfx + ".isDatafeed", "true");
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  //
  private void setStatus(String type, Commodity commodity, String format, Element element) throws SQLException {
    Set<String> statuses = ExportTaskStatusSQL.getStatus(type, commodity.getCommodity(), format);
    if (None.isEmpty(statuses)) {
      element.setAttribute("status", "NONE");
    } else if (statuses.contains(InstructionStatus.STATUS_FAILED)) {
      element.setAttribute("status", "FAILED");
    } else if (statuses.contains(InstructionStatus.STATUS_START) || statuses.contains(InstructionStatus.STATUS_PROCESS)) {
      element.setAttribute("status", "PROCESS");
    } else {
      element.setAttribute("status", "SUCCESS");
    }
  }
}
