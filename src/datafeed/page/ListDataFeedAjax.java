package datafeed.page;

import global.fileupload.page.FileInfo;
import global.fileupload.page.MultiFileUploadManager;

import java.io.File;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.constant.Commodity;
import model.db.InstructionStatus;
import model.db.SQLFactory;
import model.db.SaveDB;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import tcc.utils.None;
import tcc.utils.log.LogService;
import tcc.utils.session.SessionObj;
import tcc.utils.xml.dom.DOMUtil;
import tcc.webfw.page.Page;
import datafeed.db.DataFeedSQL;

public class ListDataFeedAjax extends Page {
  @Override
  public String processCommand(String command, SessionObj session, HttpServletRequest request, HttpServletResponse response) {
    try {
      String pfx = Page.getPagePrefix(request);
      if ("doUploadFeedFile".equals(command)) {
        String type = request.getParameter("type");
        String mfudKey = request.getParameter("key");
        if (None.isNonBlank(mfudKey)) {
          FileInfo fileInfos[] = MultiFileUploadManager.getFiles(session, mfudKey);
          try {
            for (FileInfo fi : fileInfos) {
              File srcFile = new File(fi.fullPathInfo);
              if (!srcFile.exists() || !srcFile.isFile()) {
                throw new Exception("Feed file can't be found! file: " + fi.fullPathInfo);
              } else {
                FeedLogic.copyFeedFileByType(type, srcFile);
              }
            }
          } finally {
            MultiFileUploadManager.clearFiles(session, mfudKey, true);
          }
        }
      } else if ("doFeedByType".equals(command)) {
        String type = request.getParameter("type");
        FeedLogic.doFeed(type);
      } else if ("doFeedByFile".equals(command)) {
        String type = request.getParameter("type");
        String filename = request.getParameter("filename");
        FeedLogic.doFeed(type, filename);
      } else if ("clearAllData".equals(command)) {
        for (SaveDB db : SQLFactory.getAllSaveDBs()) {
          for (Commodity c : Commodity.getCommoditys()) {
            db.deleteAll(c);
          }
        }
      }

      print(pfx, session);
    } catch (Exception e) {
      LogService.trace(e, "");
    }
    return super.processCommand(command, session, request, response);
  }

  private void print(String pfx, SessionObj session) throws Exception {
    List<File> types = FeedLogic.getFeedTypes();

    Document doc = DOMUtil.createDocument();

    Element rootEl = doc.createElement("root");

    doc.appendChild(rootEl);

    for (File type : types) {
      String typename = type.getName();
      Element typeEl = doc.createElement("type");
      typeEl.setAttribute("name", typename);

      rootEl.appendChild(typeEl);
      List<File> files = FeedLogic.getFeedTypeFiles(type);
      for (File file : files) {
        String filename = file.getName();
        Element fileEl = doc.createElement("file");
        typeEl.appendChild(fileEl);
        fileEl.setAttribute("name", filename);

        Set<String> statuses = DataFeedSQL.getStatus(typename, filename);
        if (None.isEmpty(statuses)) {
          fileEl.setAttribute("status", "NONE");
        } else if (statuses.contains(InstructionStatus.STATUS_FAILED)) {
          fileEl.setAttribute("status", "FAILED");
        } else if (statuses.contains(InstructionStatus.STATUS_START) || statuses.contains(InstructionStatus.STATUS_PROCESS)) {
          fileEl.setAttribute("status", "PROCESS");
        } else {
          fileEl.setAttribute("status", "SUCCESS");
        }

      }
    }
    // System.out.println(DOMUtil.doc2XML(doc));
    session.setPageSessionValue(pfx + ".pfdoc", doc);
  }
}
