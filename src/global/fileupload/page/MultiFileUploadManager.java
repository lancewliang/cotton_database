// ----------------------------------------------------------------------
// Copyright (c) 1995-2002 Timecruiser Computing Corporation ("TCC")
// All Rights Reserved.
//
// Use of, copying, modifications to, and distribution of this software
// and its documentation without Timecruiser Computing Corporation's
// written permission can result in the violation of U.S. Copyright
// and Patent laws.  Violators will be prosecuted to the highest 
// extent of the applicable laws.
//
// TCC MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
// THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
// TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
// PARTICULAR PURPOSE, OR NON-INFRINGEMENT.
//
// Anthony Ma
// 10/25/02
// ----------------------------------------------------------------------

package global.fileupload.page;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;

import tcc.account.obj.UserObj;
import tcc.account.session.UserSessionUtil;
import tcc.utils.av.AVScanner;
import tcc.utils.base64.Base64;
import tcc.utils.log.LogService;
import tcc.utils.property.PropertyManager;
import tcc.utils.session.SessionObj;

/**
 * The purpose of this class is to manage multiple file upload requests from user
 * by managing the template/GUI interface and dialogs, along with the location
 * of the files and AV scanning.
 * The user's session object is used for managing transition variables used
 * by the interface and to avoid conflict in multiple pages situation.
 */

public class MultiFileUploadManager {
  /**
   * access key for storing/retrieving info used by the manager.
   */

  // KEY for storing path info in user session object
  //private static final String TMP_FILE_PATH = "mfum_TempFilePath";
  // KEY for storing info of files within the path
  private static final String FILE_INFO_CACHE = "mfum_FileInfoCache";

  // KEY for specifying total allowable file size
  private static final String ALLOWABLE_TOTAL_SIZE = "mfum_maxTotalSize";

  public static long DEFAULT_ALLOWABLE_TOTAL_SIZE = 3 * 1024 * 1024;

  // KEY for specifying total allowable file count
  private static final String ALLOWABLE_FILE_COUNT = "mfum_maxFileCount";

  private static final String ALLOWABLE_CONVERT = "mfum_allowConvert";

  // KEY for specifying class name
  private static final String FROM_CLASS_NAME = "mfum_from_classname";

  public static int DEFAULT_ALLOWABLE_FILE_COUNT = 6;

  public static long SOFT_LIMIT_ALLOWABLE_SIZE = 500 * 1024 * 1024; // 500 MB

  // special mode switch flag (for non-CampusCruiser mode)

  public static String defaultTempDir = "/tmp";

  static {

    //move it back to /tmp
    defaultTempDir = PropertyManager.getString("mfud.defaultTempDir", "/tmp");
    DEFAULT_ALLOWABLE_TOTAL_SIZE = PropertyManager.getLong("mfud.maxTotalSize", 3 * 1024 * 1024);
    DEFAULT_ALLOWABLE_FILE_COUNT = PropertyManager.getInt("mfud.maxFileCount", 6);
  }

  //-----------------------------------------------------------------------------------

  public static String getNewTransactionKey() {
    return Base64.base64Encode(String.valueOf(System.currentTimeMillis() + Math.random() * 100000));
  }

  //-----------------------------------------------------------------------------------

  protected static Object getValue(SessionObj session, String transactionKey, String key) {
    String _key = "_" + transactionKey + "." + key;
    return session.getUserSessionValue(_key);
  }

  protected static void setValue(SessionObj session, String transactionKey, String key, Object value) {
    session.setUserSessionValue("_" + transactionKey + "." + key, value);
  }

  //-----------------------------------------------------------------------------------
  public static String getDefaultTempPath(SessionObj session) {
    UserObj user = UserSessionUtil.getUserObj(session);
    if (user != null) {
      return getDefaultTempPath(user.getUserId());
    } else {
      return getDefaultTempPath(-1);
    }

  }

  public static String getDefaultTempPath(long userId) {
    String path = defaultTempDir + File.separator + (userId / 1000) + File.separator + userId;
    File file = new File(path);
    if (!file.exists()) {
      file.mkdirs();
    }
    return path;
  }

  public static String getTempPath(SessionObj session, String transactionKey) {
    UserObj user = UserSessionUtil.getUserObj(session);
    if (user != null) {
      return getTempPath(user.getUserId(), transactionKey);
    } else {
      return getTempPath(-1, transactionKey);
    }
  }

  public static String getTempPath(long userId, String transactionKey) {

    String s = getDefaultTempPath(userId);

    File file = new File(s);
    if (!file.exists()) {
      file.mkdirs();
    }

    return s;
  }

  public static void setFromClassName(SessionObj session, String transactionKey, String fromClassName) {
    setValue(session, transactionKey, FROM_CLASS_NAME, fromClassName);
  }

  public static String getFromClassName(SessionObj session, String transactionKey) {
    String fromClassName = (String) getValue(session, transactionKey, FROM_CLASS_NAME);
    return fromClassName == null ? "" : fromClassName;
  }

  public static void setAllowableTotalSize(SessionObj session, String transactionKey, long size) {

    setValue(session, transactionKey, ALLOWABLE_TOTAL_SIZE, "" + size);
  }

  public static long getAllowableTotalSize(SessionObj session, String transactionKey) {
    long size = 0;
    String s = (String) getValue(session, transactionKey, ALLOWABLE_TOTAL_SIZE);
    if (s == null) {
      size = DEFAULT_ALLOWABLE_TOTAL_SIZE;
      setAllowableTotalSize(session, transactionKey, size);
    } else {
      size = Long.parseLong(s);
    }
    return size;
  }

  public static void setAllowConvert(SessionObj session, String transactionKey, boolean allow) {
    setValue(session, transactionKey, ALLOWABLE_CONVERT, allow ? "Y" : "N");
  }

  public static boolean getAllowConvert(SessionObj session, String transactionKey) {
    boolean isAllow = false;
    String s = (String) getValue(session, transactionKey, ALLOWABLE_CONVERT);
    if ("Y".equals(s)) {
      isAllow = true;
    }
    return isAllow;
  }

  public static void setAllowableFileCount(SessionObj session, String transactionKey, int count) {
    setValue(session, transactionKey, ALLOWABLE_FILE_COUNT, "" + count);
  }

  public static int getAllowableFileCount(SessionObj session, String transactionKey) {
    int count = 0;
    String s = (String) getValue(session, transactionKey, ALLOWABLE_FILE_COUNT);
    if (s == null) {
      count = DEFAULT_ALLOWABLE_FILE_COUNT;
      setAllowableFileCount(session, transactionKey, count);
    } else {
      count = Integer.parseInt(s);
    }
    return count;
  }

  /**
   * And yes, reminent files may not be cleaned up.
   * (Hopefully, system operator will have a batch script to periodically clean up.)
   */
  protected static void clearFileInfoCache(SessionObj session, String transactionKey) {
    Vector cache = (Vector) getValue(session, transactionKey, FILE_INFO_CACHE);
    if (cache != null) {
      cache.removeAllElements();
    }
    setValue(session, transactionKey, FILE_INFO_CACHE, null);
  }

  /**
   * Retrieve file info from cache.  And then sync up with current location data.
   */
  protected static Vector getFileInfoCache(SessionObj session, String transactionKey) {
    // prepare cache
    Vector cache = null;

    cache = (Vector) getValue(session, transactionKey, FILE_INFO_CACHE);
    if (cache == null) {
      cache = new Vector();
      setValue(session, transactionKey, FILE_INFO_CACHE, cache);
    }

    return cache;
  }

  public static FileInfo addFile(SessionObj session, String transactionKey, File file) {

    Vector cache = getFileInfoCache(session, transactionKey);

    //String name = file.getName();
    FileInfo info = null;
    boolean found = false;
    int foundIndx = 0;
    for (Enumeration en = cache.elements(); en.hasMoreElements();) {
      info = (FileInfo) en.nextElement();
      if (info.fullPathInfo.equals(file.getAbsolutePath())) {
        found = true;
        try {
          cache.remove(foundIndx);
        } catch (Exception e) {
          ;
        }

        break;
      }
      foundIndx++;
    }
    if (found) {
      if (info.timestamp != file.lastModified()) {
        cache.remove(info);
        info = null;
      }
    }
    if (!found || info == null) {
      info = new FileInfo();
      info.fullPathInfo = file.getAbsolutePath();
      info.name = file.getName();
      info.size = file.length();
      info.timestamp = file.lastModified();

      if (info.avStatus == AVScanner.INFECTED) {
        file.delete();
        return info;
      }

    }
    LogService.msg("MFUD Manager (addFile) adding file " + info.name + " into cache.");
    cache.addElement(info);
    return info;
  }

  public static FileInfo[] getFilesFromCache(SessionObj session, String transactionKey) {
    Vector cache = getFileInfoCache(session, transactionKey);
    FileInfo list[] = new FileInfo[cache.size()];
    cache.copyInto(list);
    return list;
  }

  /**
   * Get all the files that are set in the cache.
   */
  public static FileInfo[] getFiles(SessionObj session, String transactionKey) {
    //    return getFileInfoCache(session,transactionKey);

    return getFilesFromCache(session, transactionKey);

  }

  public static void removeFile(SessionObj session, String transactionKey, String file) {
    Vector cache = getFileInfoCache(session, transactionKey);
    for (Enumeration en = cache.elements(); en.hasMoreElements();) {
      FileInfo info = (FileInfo) en.nextElement();
      if (info.name.equals(file)) {
        new File(info.fullPathInfo).delete();
        cache.removeElement(info);
        break;
      }
    }

  }

  public static void scanFile(SessionObj session, String transactionKey, String file) {
    Vector cache = getFileInfoCache(session, transactionKey);
    for (Enumeration en = cache.elements(); en.hasMoreElements();) {
      FileInfo info = (FileInfo) en.nextElement();
      if (info.avStatus == AVScanner.INFECTED) {
        cache.remove(info);
      }
    }

  }

  public static void convert2OnlineViewingFile(SessionObj session, String transactionKey, String file, int quantity) {
    Vector cache = getFileInfoCache(session, transactionKey);
    for (Enumeration en = cache.elements(); en.hasMoreElements();) {
      FileInfo info = (FileInfo) en.nextElement();
      if (info.name.equals(file)) {
        info.convert2OnlineViewing = true;

        break;
      }
    }

  }

  /**
   * Remove all file information from cache.
   * Optionally remove all physical files.
   */
  public static void clearFiles(SessionObj session, String transactionKey, boolean deleteFile) {
    Vector cache = getFileInfoCache(session, transactionKey);
    if (deleteFile) {
      for (Enumeration en = cache.elements(); en.hasMoreElements();) {
        FileInfo info = (FileInfo) en.nextElement();
        File f = new File(info.fullPathInfo);
        f.delete();
      }
    }

    clearFileInfoCache(session, transactionKey);

  }

}
