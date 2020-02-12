package global.fileupload.page;

import javax.servlet.http.HttpServletRequest;

import tcc.tools.iostream.upload.servlet.UploadHelper;

public class UploadHelperImp implements UploadHelper {

  @Override
  public long getCallAllowSize(HttpServletRequest arg0) {
    return -1;
  }

  @Override
  public String getTempFolderPath(HttpServletRequest req) {
    long userId = Long.parseLong(req.getParameter("userId"));
    String ppfx = req.getParameter("ppfx");
    return MultiFileUploadManager.getTempPath(userId, ppfx);
  }

  @Override
  public boolean isAllowCopy(HttpServletRequest req, String arg1) {
    // long userId = Long.parseLong(req.getParameter("userId"));
    // return userId > 0;
    return true;

  }

  @Override
  public boolean isAllowUpload(HttpServletRequest req) {
    // long userId = Long.parseLong(req.getParameter("userId"));
    // return userId > 0;
    return true;
  }

  @Override
  public boolean isSV(HttpServletRequest arg0) {
    return false;
  }

}
