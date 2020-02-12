// ----------------------------------------------------------------------
// Copyright (c) 1998-1999 Timecruiser Computing Corportaion
// All Rights Reserved.
//
// Use, copy, modify, and distribute of this software and its documentation
// without WebMan Technologies Inc. written permission can result in the
// violation of U.S. Copyright and Patent laws.  Violators will be prosecuted
// to the highest extend of the applicable laws.
//
// WEBMAN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
// THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
// TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
// PARTICULAR PURPOSE, OR NON-INFRINGEMENT. WEBMAN SHALL NOT BE LIABLE FOR
// ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
// DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
//
// Anthony Ma
// @10/24/2002
// ---------------------------------------------------------------------

package global.fileupload.page;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tcc.utils.session.SessionObj;
import tcc.webfw.page.Page;
import tcc.webfw.page.PagePrefixString;

public class MultiFileUploadDialogAjax extends  Page {

  @Override
  public String processCommand(String command, SessionObj session, HttpServletRequest req, HttpServletResponse response) {

    String pagePrefix = PagePrefixString.getPagePrefix(req);
    String ppfx = req.getParameter("ppfx");

    if (command == null) {
      // noop
    } else if (command.equalsIgnoreCase("addUploadedFile")) {
      String filename = req.getParameter("uploadFileName");
      String avStatus = req.getParameter("avStatus");
      if (filename != null) {
        String tempfold = MultiFileUploadManager.getTempPath(session, ppfx);
        File file = new File(tempfold, filename);
        if (file.exists()) {
          FileInfo info = MultiFileUploadManager.addFile(session, ppfx, file);
          try {
            info.avStatus = Integer.parseInt(avStatus);
          } catch (Exception e) {}
        }
      }

    } else if (command.equalsIgnoreCase("remove")) {

      String filename = req.getParameter("filename");
      if (filename != null) {

        MultiFileUploadManager.removeFile(session, ppfx, filename);
      }

    } else if (command.equalsIgnoreCase("convert2OnlineViewing")) {
      String quantityStr = req.getParameter("quantity");
      int quantity = 0;
      if (quantityStr == null) {
        quantity = 0;
      }
      try {
        quantity = Integer.parseInt(quantityStr);
      } catch (Exception e) {
        quantity = 0;
      }
      String filename = req.getParameter("filename");
      if (filename != null) {

        MultiFileUploadManager.convert2OnlineViewingFile(session, ppfx, filename, quantity);
      }

    }
    return super.processCommand(command, session, req, response);
  }

}
