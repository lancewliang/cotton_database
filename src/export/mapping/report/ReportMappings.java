package export.mapping.report;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import tcc.utils.None;
import tcc.utils.log.LogService;
import tcc.utils.property.PropertyManager;
import tcc.utils.xml.dom.DOMUtil;

public class ReportMappings {
  private static File root = new File(PropertyManager.getString("export.format.root", ""));
  public static String TYPE_COMMON = "common";
  public static String TYPE_commodity = "commodity";

  public static List<String> getReports(String type) {
    List<String> list = new ArrayList<String>();
    File folder = new File(root, type);
    if (folder.exists()) {
      File[] fs = folder.listFiles();
      if (!None.isEmpty(fs)) {
        for (File file : fs) {
          if (file.getName().equals("CVS"))
            continue;
          list.add(file.getName());
        }
      }
    }
    return list;
  }

  public static void saveReport(String type, DimensionReportMapping reportMapping) {
    try {
      File folder = new File(root, type);
      if (!folder.exists())
        folder.mkdirs();
      File file = new File(folder, reportMapping.label + ".xml");
      if (!file.exists())
        file.createNewFile();
      FileOutputStream out = new FileOutputStream(file);
      Document doc = DimensionReportMappingDumper.dump(reportMapping);
      DOMUtil.writeDoc(doc, out);
      out.close();
    } catch (Exception e) {
      LogService.trace(e, null);
    }
  }

  public static void removeReportFile(String type, String format) {
    File f = getReportFile(type, format);
    f.delete();
  }

  public static File getReportFile(String type, String format) {
    return new File(root, type + File.separator + format);
  }

  public static List<ReportMapping> getReport(String type, String format) {
    Document doc;
    try {
      doc = DOMUtil.file2Doc(getReportFile(type, format));
      return DimensionReportMappingParser.load(doc);
    } catch (Exception e) {
      LogService.trace(e, null);
    }
    return null;
  }

}
