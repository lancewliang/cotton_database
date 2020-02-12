package export.mapping.page;

import java.io.PrintWriter;

import tcc.utils.session.SessionObj;
import tcc.webfw.page.Page;

public class ListReportFormat extends Page {

  @Override
  public void print(PrintWriter arg0, SessionObj session, String pfx, String arg3, String arg4) {

    try {

      CreateReportFormatS2.printConstant(session, pfx);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    super.print(arg0, session, pfx, arg3, arg4);
  }

}
