package export.mapping.page;

import java.io.File;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.constant.Commodity;
import tcc.utils.log.LogService;
import tcc.utils.output.ServletOutputFileUtil;
import tcc.utils.session.SessionObj;
import tcc.webfw.page.Page;
import export.mapping.excell.OutputReports;

public class DownloadOutputFile extends Page {
  @Override
  public String processCommand(String command, SessionObj session, HttpServletRequest request, HttpServletResponse response) {
    String pfx = Page.getPagePrefix(request);
    try {

      String reportType = request.getParameter("reportType");
      Commodity commodityObj = Commodity.getCommodity(reportType);
      String format = request.getParameter("format");
      String fileName = request.getParameter("file");
      File report = OutputReports.getReport(commodityObj, format, fileName);
      Locale locale = new Locale("ZH_CN");

      try {
        String contentType = ServletOutputFileUtil.getContentType(fileName);
        ServletOutputFileUtil.outputFile(report, fileName, response, contentType, locale);
      } catch (Exception e) {
        e.printStackTrace();
        throw new ServletException("FileMgrStreamHandler - Failed to output the file = " + report.getAbsolutePath());
      }
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return Page.NO_PRINT;
  }

}
