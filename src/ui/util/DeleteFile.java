package ui.util;

import java.io.File;

import tcc.utils.None;

public class DeleteFile {

  public static void delete(File folder) {
    if (folder.exists()) {
      File[] list = folder.listFiles();
      if (!None.isEmpty(list)) {
        for (File file : list) {
          if (file.isFile()) {
            file.delete();
          } else {
            delete(file);
            file.delete();
          }
        }
      }
    }
  }
}
