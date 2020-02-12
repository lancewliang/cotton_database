package ant.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import tcc.utils.file.FileStreamUtil;
import tcc.utils.property.PropertyManager;

public class AntFTPLogic {
  private static File AntFTPFileRoot = new File(PropertyManager.getString("ant.ftp.root", ""));

  public static File getFolder(String module) {
    File folder = new File(AntFTPFileRoot, module);

    return folder;
  }

  public static File getFile(String module, String filename) {
    File folder = new File(AntFTPFileRoot, module);
    folder.mkdirs();
    File file = new File(folder, filename);
    return file;
  }

  public static void saveFile(String module, String filename, String content) {
    File folder = new File(AntFTPFileRoot, module);
    folder.mkdirs();
    File file = new File(folder, filename);
    OutputStream out = null;
    try {
      file.createNewFile();
      out = new FileOutputStream(file);
      FileStreamUtil.outputString(out, content, "UTF-8");

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } finally {
      try {
        if (out != null)
          out.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

}
