package global.fileupload.page;

import javax.servlet.http.HttpServletRequest;

import tcc.utils.session.SessionObj;

public class UploadBrowserInfo {
  boolean isFlashSupport = false;

  boolean isAppletSupport = false;

  public UploadBrowserInfo(HttpServletRequest req) {
    isAppletSupport = "true".equals(req.getParameter("isAppletSupport"));

    isFlashSupport = "true".equals(req.getParameter("isFlashSupport"));

  }

  public static void setIntoUserSession(SessionObj session, UploadBrowserInfo info) {
    session.setUserSessionValue("UploadBrowserInfo", info);
  }

  public static UploadBrowserInfo getIntoUserSession(SessionObj session) {
    return (UploadBrowserInfo) session.getUserSessionValue("UploadBrowserInfo");
  }

  public static String Default = "Default";

  public static String Flash = "Flash";

  public static String Applet = "Applet";

}
