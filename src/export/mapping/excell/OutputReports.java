package export.mapping.excell;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import model.constant.Commodity;

import tcc.utils.None;
import tcc.utils.StringUtil;
import tcc.utils.property.PropertyManager;
import ui.util.DeleteFile;

public class OutputReports {
  private static File root = new File(PropertyManager.getString("export.output.root", ""));

  public static List<File> getReports(Commodity commodityObj, String format) {
    format = StringUtil.replaceString(format, ".xml", "");
    List<File> list = new ArrayList<File>();
    File folder = new File(root, commodityObj.getCommodity() + File.separator + format);
    if (folder.exists()) {
      File[] fs = folder.listFiles();
      if (!None.isEmpty(fs))
        for (File file : fs) {
          if (file.isFile())
            list.add(file);
        }
    }
    return list;
  }

  public static File getReport(Commodity commodityObj, String format, String fileName) {
    format = StringUtil.replaceString(format, ".xml", "");
    File folder = new File(root, commodityObj.getCommodity() + File.separator + format);
    return new File(folder, fileName);
  }

  public static File saveReport(Commodity commodityObj, String format, String date) {
    format = StringUtil.replaceString(format, ".xml", "");
    File folder = new File(root, commodityObj.getCommodity() + File.separator + format);

    return new File(folder, format + "-" + date + ".xlsm");
  }

  public static void clearReports(Commodity commodityObj) {
    File folder = new File(root, commodityObj.getCommodity());
    DeleteFile.delete(folder);

  }

  public static void clearReports(String format, Commodity commodityObj) {
    format = StringUtil.replaceString(format, ".xml", "");
    File folder = new File(root, commodityObj.getCommodity() + File.separator + format);
    DeleteFile.delete(folder);
    folder.delete();
  }
}
