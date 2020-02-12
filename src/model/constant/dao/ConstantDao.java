package model.constant.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import tcc.utils.file.FileStreamUtil;
import tcc.utils.file.FileUtil;

public class ConstantDao {
  public static File basePaht = new File(tcc.utils.property.PropertyManager.getString("constant.path", ""));

  public static Properties getProprity(String type) throws IOException {
    File f = new File(basePaht, type + ".properties");
    return getProprity(f);
  }

  public static Properties getProprity(File f) throws IOException {

    Properties p = new Properties();
    if (f.exists()) {

      FileInputStream fos = new FileInputStream(f);
      String content = FileStreamUtil.getFileContent(fos, "UTF-8");
      fos.close();
      StringReader r = new StringReader(content);
      p.load(r);
      r.close();
    }
    return p;
  }

  public static void saveProprity(String type, Properties p) throws IOException {
    File f = new File(basePaht, type + ".properties");
    if (!f.exists()) {
      f.createNewFile();
    }
    FileOutputStream fos = new FileOutputStream(f);
    p.store(fos, "");
    fos.close();

  }
}