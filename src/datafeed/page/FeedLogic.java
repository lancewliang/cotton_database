package datafeed.page;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import tcc.utils.None;
import tcc.utils.file.FileUtil;
import tcc.utils.log.LogService;
import tcc.utils.property.PropertyManager;
import datafeed.db.DataFeedSQL;
import datafeed.excell.ExcellFeed;
import datafeed.excell.FeedTaskStatus;

public class FeedLogic {

  public static File root = new File(PropertyManager.getString("datafeed.root", ""));

  public static void copyFeedFileByType(String type, File fromFile) throws Exception {

    FileUtil.copyFile(fromFile, new File(new File(root, type), fromFile.getName()));

  }

  public static List<File> getFeedTypes() {
    List<File> files = new ArrayList<File>();
    File[] folders = root.listFiles();
    for (File file : folders) {
      if (file.isDirectory()) {
        if (file.getName().equals("CVS")) {
          continue;
        }
        files.add(file);
      }

    }
    return files;
  }

  public static List<File> getFeedTypeFiles(File type) {
    List<File> files = new ArrayList<File>();
    File[] folders = type.listFiles();
    if (!None.isEmpty(folders))
      for (File file : folders) {
        if (file.isFile()) {
          files.add(file);
        }
      }
    return files;
  }

  public static void doFeed(String type) {
    try {
      File folder = new File(root, type);
      LogService.msg("folder===========:" + folder.getName());

      for (File file : folder.listFiles()) {
        if (file.isFile()) {
          parseExcell(file);
          LogService.msg(file.getName());
        }
      }

    } catch (Exception e1) {
      LogService.trace(e1, "");
    }
  }

  public static void doFeed(String type, String filename) {
    try {
      File folder = new File(root, type);
      LogService.msg("folder===========:" + folder.getName());

      File file = new File(folder, filename);
      if (file.isFile() && file.exists()) {
        parseExcell(file);
        LogService.msg(file.getName());
      }

    } catch (Exception e1) {
      LogService.trace(e1, "");
    }
  }

  private static void parseExcell(File excel) {
    FileInputStream fis = null;
    Workbook wb = null;

    try {

      fis = new FileInputStream(excel);
      wb = WorkbookFactory.create(fis);
      for (int s = 0; s < wb.getNumberOfSheets(); s++) {
        Sheet sheet = wb.getSheetAt(s);
        int rowNum = sheet.getPhysicalNumberOfRows();
        if (rowNum < 1) {
          return;
        }
        FeedTaskStatus task = new FeedTaskStatus(excel.getParentFile().getName(), excel.getName(), sheet.getSheetName());

        DataFeedSQL.save(task);
      }
    } catch (Exception e) {
      LogService.trace(e, "excel:" + excel.getAbsolutePath());
    } finally {
      try {
        fis.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static ExcellFeed getFeed(FeedTaskStatus task) {
    FileInputStream fis = null;
    Workbook wb = null;
    File excel = new File(root, task.getType() + File.separator + task.getFilename());
    try {

      fis = new FileInputStream(excel);
      wb = WorkbookFactory.create(fis);

      Sheet sheet = wb.getSheet(task.getSheetname());

      return new ExcellFeed(excel, sheet);

    } catch (Exception e) {
      LogService.trace(e, "excel:" + excel.getAbsolutePath());
    } finally {
      try {
        fis.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

}
