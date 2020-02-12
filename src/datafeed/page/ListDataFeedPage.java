package datafeed.page;

import java.io.PrintWriter;

import tcc.utils.session.SessionObj;
import tcc.webfw.page.Page;
import export.mapping.page.CreateReportFormatS2;
import global.fileupload.page.MultiFileUploadManager;

public class ListDataFeedPage extends Page {

  @Override
  public void print(PrintWriter arg0, SessionObj session, String pfx, String arg3, String arg4) {

    try {
      String transactionKey = MultiFileUploadManager.getNewTransactionKey();
      MultiFileUploadManager.setAllowableFileCount(session, transactionKey, 10);
      session.setPageSessionValue(pfx + ".mfudppfx", transactionKey);
      CreateReportFormatS2.printConstant(session, pfx);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    super.print(arg0, session, pfx, arg3, arg4);
  }

}
