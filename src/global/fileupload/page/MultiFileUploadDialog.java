// ----------------------------------------------------------------------
// Copyright (c) 1998-1999 Timecruiser Computing Corporation
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tcc.account.obj.UserObj;
import tcc.account.session.UserSessionUtil;
import tcc.tools.iostream.IostreamHost;
import tcc.utils.None;
import tcc.utils.log.LogService;
import tcc.utils.session.SessionObj;
import tcc.webfw.page.Page;
import tcc.webfw.page.PagePrefixString;

public class MultiFileUploadDialog extends Page {

  public static final String SEPARATOR = ";";

  public static final int FILE_NAME_LENGTH_LIMIT = 256;

  public static final int FILE_NAME_VIEW_LENGTH = 15;

  public String processCommand(String command, SessionObj session, HttpServletRequest req, HttpServletResponse resp) {

    String userAgent = req.getHeader("User-Agent") == null ? "" : req.getHeader("User-Agent");
    userAgent = userAgent.toLowerCase();
    boolean dragEnabled = !(userAgent.indexOf("mac") > -1 && userAgent.indexOf("firefox") > -1);

    LogService.log("userAgent:" + userAgent);
    LogService.log("dragEnabled:" + dragEnabled);

    String pagePrefix = PagePrefixString.getPagePrefix(req);
    String ppfx = req.getParameter("ppfx");
    session.setPageSessionValue(pagePrefix + ".ppfx", ppfx);

    String k = req.getParameter("k");

    ppfx = None.isBlank(ppfx) ? None.isBlank(k) ? MultiFileUploadManager.getNewTransactionKey() : k : ppfx;
    String allowConvert = req.getParameter("allowConvert");

    boolean isAllowConvert = false;
    allowConvert = "Y".equals(allowConvert) ? "Y" : "N";
    if ("N".equals(allowConvert)) {
      isAllowConvert = MultiFileUploadManager.getAllowConvert(session, ppfx);
      allowConvert = isAllowConvert ? "Y" : "N";
    } else {
      isAllowConvert = true;
    }
    session.setPageSessionValue(pagePrefix + ".allowConvert", allowConvert);

    session.setPageSessionValue(pagePrefix + ".dragEnabled", dragEnabled);

    LogService.log("req.getScheme():" + req.getScheme());
    String requestUrl = req.getRequestURL().toString();
    LogService.log("req.getRequestURL:" + requestUrl);

    session.setPageSessionValue(pagePrefix + ".host", IostreamHost.getHostName());

    String mf = req.getParameter("mf");
    boolean showMyFiles = mf == null || !mf.equalsIgnoreCase("N");
    session.setPageSessionValue(pagePrefix + ".showMyFiles", showMyFiles ? "Y" : "N");
    UserObj user = UserSessionUtil.getUserObj(session);
    long userId = user != null ? user.getUserId() : -1;
    session.setPageSessionValue(pagePrefix + ".userId", userId);
    session.setPageSessionValue(pagePrefix + ".isSV", "N");

    prepareHtml(session, pagePrefix, ppfx);
    return null;
  }

  void prepareHtml(SessionObj session, String pagePrefix, String ppfx) {
    StringBuffer initFileContent = new StringBuffer();
    try {
      FileInfo info[] = MultiFileUploadManager.getFiles(session, ppfx);
      int maxCount = MultiFileUploadManager.getAllowableFileCount(session, ppfx);
      long allowableTotalSize = MultiFileUploadManager.getAllowableTotalSize(session, ppfx);
      session.setPageSessionValue(pagePrefix + ".maxCount", "" + maxCount);
      session.setPageSessionValue(pagePrefix + ".allowableTotalSize", "" + (allowableTotalSize));
      String allowConvert = (String) session.getPageSessionValue(pagePrefix + ".allowConvert");
      boolean isAllowConvert = "Y".equals(allowConvert) ? true : false;
      if (info.length > 0) {
        initFileContent.append("[");
      }
      for (int i = 0; i < info.length; i++) {
        if (i > 0) {
          initFileContent.append(",");
        }
        initFileContent.append(info[i].toJson(FILE_NAME_VIEW_LENGTH, isAllowConvert));
      }
      if (info.length > 0) {
        initFileContent.append("]");
      }
      session.setPageSessionValue(pagePrefix + ".initFileContent", initFileContent.toString());
    } catch (Exception e) {
      LogService.trace(this, "prepareHtml()", e, "Failed to produce MFUD table!");
    }
  }

}
