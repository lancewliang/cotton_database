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

import java.io.Serializable;

import tcc.utils.StringUtil;
import tcc.utils.av.AVScanner;

public class FileInfo implements Serializable {
  private static final long serialVersionUID = 1L;

  public String fullPathInfo;

  public String name;

  public int avStatus = AVScanner.NOT_SCANNED;

  public String avNote = null;

  public long size;

  public long timestamp;

  public boolean convert2OnlineViewing = false;

  public String toJson(int FILE_NAME_VIEW_LENGTH, boolean isAllowConvert) {
    boolean isOnlineViewing = false;
    String fileViewName = name;
    if (fileViewName.length() > FILE_NAME_VIEW_LENGTH) {
      fileViewName = StringUtil.truncateString(fileViewName, FILE_NAME_VIEW_LENGTH) + "...";
    }

    StringBuffer initFileContent = new StringBuffer();
    initFileContent.append("{");
    initFileContent.append("\"filename\":\"").append(name).append("\",");
    initFileContent.append("\"size\":\"").append(size).append("\",");
    initFileContent.append("\"isOnlineViewing\":\"").append(isOnlineViewing ? "Y" : "N").append("\",");
    initFileContent.append("\"avStatus\":\"").append(avStatus).append("\"");

    initFileContent.append("}");
    return initFileContent.toString();
  }
}
