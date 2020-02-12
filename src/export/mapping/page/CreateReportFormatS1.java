package export.mapping.page;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tcc.utils.session.SessionObj;
import tcc.webfw.page.Page;

public class CreateReportFormatS1 extends Page {
  public String processCommand(String command, SessionObj session, HttpServletRequest request, HttpServletResponse response) {
    String pfx = Page.getPagePrefix(request);

    try {
      session.setPageSessionValue(pfx + ".reportType", request.getParameter("reportType"));
      CreateReportFormatS2.printReportFormatMapping(session, pfx);
      CreateReportFormatS2.printConstant(session, pfx);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return super.processCommand(command, session, request, response);
  }

  @Override
  public void print(PrintWriter arg0, SessionObj session, String pfx, String arg3, String arg4) {

    super.print(arg0, session, pfx, arg3, arg4);
  }
}
