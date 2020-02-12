// File: ccAttachment_fileupload.js
// for use by MultiFileUploadDialog.html
//const variable ---
var FILEVIEWNAME_SUBSTRING_LENGTH = 17;
//const variable ---

/**
 * Collect the file name before activating the file upload.
 */
function escapeFileName(fileName){
  fileName = encodeURIComponent(fileName);
  // Latin letters and the characters  + - * / . _ @ remain unchanged
  // so we manually replace '+' here
  return fileName.split("+").join("%2B");
}


/**
 * close the dialog window
 */
function isLeave(){
  if (isUploading()) {
    return confirm(uploadingI18N);
  }
  return true;
}
 
function dismiss(){
  var fileListformObj = document.attachment_list;
  var length = fileinfolist.length;
  if (length == 0) {
    self.close();
  } else {
    if(!isLeave()){
      return;
    }
    if (opener && opener.mfudCallback) {
      opener.mfudCallback(fileListformObj.ppfx.value, getAllUploadedFileName());
    }
    self.close();
  }
}

function isUploading(){
  for (var i = 0; i < fileinfolist.length; i++) {
    var fileinfo = fileinfolist[i];
    if (fileinfo.status == STATUS_START || fileinfo.status == STATUS_PROCESS) {
    
      return true;
    }
  }
  return false;
}

function closeuploadwindow(){
  if(!isLeave()){
      return;
  }  
  self.close();
}

function showSelectUploadModePicker(){
  ccShowPickerById("selectUploadMethodDIV", "selectUploadMethodBtn", '');
}

function doRememberSelectionUploadMethod(currentView){
  var saveURL = returnWorkerURL() + "&cmd=saveUploadmethod&method=" + currentView + "&isRemember=" + CC.$("rememberSelectionUploadMethod").checked;
  var dosomething = function(){
    showRightMsg(RememberI18N);
  }
  $.ajax({url:saveURL,async:false}).done(dosomething);
 
}

function switchToView(n){
  if (CC.$("rememberSelectionUploadMethod").checked) {
    doRememberSelectionUploadMethod(n);
  }
  switchTo2(n);
}

function switchTo2(n){
  if(!isLeave()){
      return;
  }
  var ppfx = document.attachment_list.ppfx.value;
  var mf = document.attachment_list.mf.value;
  var url = null;
  if (n == "fromFlash") {
    url = "PageServlet?cx=u&pg=MultiFileUploadDialog-Flash&ppfx=" + ppfx + "&mf=" + mf;
  } else if (n == "fromMyFiles") {
    url = "PageServlet?cx=u&pg=MultiFileUploadDialog-MyFiles&ppfx=" + ppfx + "&mf=" + mf;
  } else if (n == "fromHTML") {
    url = "PageServlet?cx=u&pg=MultiFileUploadDialog-HTML&ppfx=" + ppfx + "&mf=" + mf;
  } else if (n == "fromApplet") {
    url = "PageServlet?cx=u&pg=MultiFileUploadDialog-Applet&ppfx=" + ppfx + "&mf=" + mf;
  }
  window.location.href = url;
}


function uploadFileOnLoad(){
  if (opener && opener.uploadFileOnLoad) {
    opener.uploadFileOnLoad();
  }
}

function getSize(fileSize){

  if (fileSize <= 0)     
    return "0";
  if (fileSize < 1) {
    return "<1";
  } else if (fileSize < 1024) {
    return "<1";
  } else {
    fileSize = Math.round(fileSize / 1024);
    try {
      fileSize = fileSize.toString();
      var len = fileSize.length;
      var str = "";
      if (len < 4) {
        return fileSize;
      } else {
        var i = 0;
        var iCount = 1;
        for (i = (len - 1); i >= 0; i--) {
          if (iCount++ < 3 || i == 0) {
            str = fileSize.charAt(i) + str;
          } else {
            str = "," + fileSize.charAt(i) + str;
            iCount = 1;
          }
        }
        return str;
      }
    } catch (e) {
      //alert(e);
      return fileSize;
    }
  }
}


function turnGray(obj){
  obj.className = "tr_bg_gray";
}

function recover(obj){
  obj.className = "tr_bg_white";
}

var fileinfolist = new Array();
var STATUS_FINISHED = 4;
var STATUS_START = 1;
var STATUS_PROCESS = 2;
var STATUS_FAILURE = 3;

function getCurrentFileUploadedSize(){
  var i = 0, uploadedsize = 0;
  for (i = 0; i < fileinfolist.length; i++) {
    var fileInfo = fileinfolist[i];
    uploadedsize += parseInt(fileInfo.size);
  }
  return uploadedsize;
}

function getCurrentFileLen(){
  return fileinfolist.length;
}


function updateAllSizeInfo(){
  var uploadedsize = getCurrentFileUploadedSize();
  var uploadedlen = getCurrentFileLen();
  $("#mfud_file_size").html(getSize(uploadedsize) + "KB");
  $("#mfud_file_len").html(uploadedlen);
  
  if (fileinfolist.length > 0) {
    $("#uploadFileInfo_nodata").hide();
    $("#uploadFileInfo").show();
  } else {
    $("#uploadFileInfo").hide();
    $("#uploadFileInfo_nodata").show();
  }
  var totalsize = parseInt(document.attachment_list.allowableTotalSize.value);
  var totallen = parseInt(document.attachment_list.maxCount.value);
  if (uploadedsize >= totalsize || uploadedlen >= totallen) {
     refusedUpload();
  } else {
     allowUpload();
  }
  
}

function addFileInfoList(fileId, fileName, fileSize, avStatus){
  debug("showUploadFile ->startUpload true fileName:" + fileName + "|fileSize" + fileSize);
  var newRecord = {
    "fileId": fileId,
    "filename": fileName,
    "size": fileSize,
    "avStatus": avStatus,
    "isOnlineViewing": "N",
    "status": STATUS_START
  };
  fileinfolist.push(newRecord);
  var fileList = document.getElementById("filelist");
  insertTableRecord(fileList, fileId, fileName, fileSize, "0", "N", false);
  debug("after insertTableRecord:" + fileinfolist.length);
  updateAllSizeInfo();
  
}

function insertTableRecord(fileList, fileId, fileName, fileSize, avStatus, onlineViewing, isFinished){
  var tr = fileList.insertRow(fileList.rows.length);
  var j = 0;
  tr.setAttribute("id", "fileId_" + fileId);
  
  tr.setAttribute("onMouseOver", "turnGray(this);");
  tr.setAttribute("onMouseOut", "recover(this);");
  var td0 = tr.insertCell(0);//fileViewName
  var td1 = tr.insertCell(1);//status
  var td2 = tr.insertCell(2);//fileViewSize
  var td3 = tr.insertCell(3);//Conversion Request
  var td4 = tr.insertCell(4);//button
  //ccHidden
  var td5 = tr.insertCell(5);//fileSize
  var td6 = tr.insertCell(6);//fileName
  var td7 = tr.insertCell(7);//fileEncode
  td0.setAttribute("valign", "baseline");
  td1.setAttribute("valign", "baseline");
  td2.setAttribute("valign", "baseline");
  td2.setAttribute("align", "right");
  td3.setAttribute("align", "right");
  td3.setAttribute("valign", "baseline");
  td4.setAttribute("valign", "baseline");
  td4.setAttribute("align", "right");
  
  
  var fileViewName = fileName;
  var fileViewSize = getSize(parseInt(fileSize));
  
  fileViewName = fileName;
  var encodeFileName = encodeURIComponent(fileName);
  td0.innerHTML = "<LABEL TITLE='" + fileName + "'><DIV style='clear:both'>" + fileViewName + "</DIV></LABEL>";
  if (isFinished) {
    td1.innerHTML = showFileStatusDiv(avStatus);
  } else {
    td1.innerHTML = "Waiting";
  }
  td2.innerHTML = fileViewSize + "&nbsp;KB";
  if (isFinished) {
    var conversionResult = -1;
    if (onlineViewing == "Y") {
      conversionResult = -2;
    } else {
      conversionResult = canOnlineViewing(fileName);
    }
    if (conversionResult == -2) {
      td3.innerHTML = "<span class='ccMsg'>" + onlineViewingI18N + "</span>";
    } else if (conversionResult == 0) {
      td3.innerHTML = "<A class='ccIcon ccRIcon'  HREF=\"javascript:convertATfile('" + fileId + "');\" >" + Convert2ViewingI18N + "</A>";
    } else if (conversionResult == 1) {
      td3.innerHTML = "<A class='ccIcon ccRIcon'  ID='selectQuantityButton_" + fileId + "' HREF=\"javascript:showQuantity('quantityPicker','selectQuantityButton_" + fileId + "','" + fileId + "');\" >" + Convert2ViewingI18N + "</A>";
    } else {
      td3.innerHTML = "";
    }
    td4.innerHTML = "<a href='#' onclick='removeAttachment(" + fileId + ");' class='ccIcon ccRIcon' role='button'><img src='i/delete.png' alt='" + removeI18n + "' /></a>";
  }
  td5.innerHTML = fileSize;
  td6.innerHTML = fileName;
  td7.innerHTML = encodeFileName;
  
  td5.className = "ccHidden";
  td6.className = "ccHidden";
  td7.className = "ccHidden";
}

 

function parseInitFileContent(){
  if (fileinfolist.length > 0) {
    var i = 0;
    var fileList = document.getElementById("filelist");
    var jCount = 0;
    
    for (i = 0; i < fileinfolist.length; i++) {
      var fileInfo = fileinfolist[i];
      fileInfo.status = STATUS_FINISHED;
      var fileId = --jCount;
      fileInfo.fileId = fileId;
      insertTableRecord(fileList, fileId, fileInfo.filename, fileInfo.size, fileInfo.avStatus, fileInfo.isOnlineViewing, true);
    }
  }
 
  updateAllSizeInfo();
}


function showProgress(fileId, progress){
  if (progress < 100) {
    var remain = 100 - progress;
    var row = document.getElementById("fileId_" + fileId);
    var cells = row.cells;
    //var uploadImg = "<img src='img/loading.gif' />&nbsp;";
    var line1Img = "<img src='img/multiupload/line1.jpg' width='" + progress + "' height='16' alt=''>";
    var line2Img = "<img src='img/multiupload/line2.jpg' width='" + remain + "' height='16' alt=''>";
    var percentHTML = "(" + progress + "%)";
    //cells[1].innerHTML = uploadImg + line1Img + line2Img + percentHTML;
    cells[1].innerHTML = line1Img + line2Img + percentHTML;
    var fileInfo = getFileInfoFromFileListByFileID(fileId);
    if (fileInfo != null) {
      fileInfo.status = STATUS_PROCESS;
    }
  }
  CC.$("ShowMsgDIV").innerHTML = "";
}



function showCompeted(fileId){
  try {
    var row = document.getElementById("fileId_" + fileId);
    var cells = row.cells;
    var fileName = cells[6].innerHTML;
    var encodedName = cells[7].innerHTML;
    var fileInfo = getFileInfoFromFileListByFileID(fileId);
    var dosomething = function(){
      fileInfo.status = STATUS_FINISHED;
      cells[1].innerHTML = showFileStatusDiv(fileInfo.avStatus);
      var fileSize = cells[5].innerHTML;
      var result = canOnlineViewing(fileName);
      if (result == 0) {
        cells[3].innerHTML = "<A class='ccIcon ccRIcon'   HREF=\"javascript:convertATfile('" + fileId + "');\"  >" + Convert2ViewingI18N + "</A>";
      } else if (result == 1) {
        cells[3].innerHTML = "<A class='ccIcon ccRIcon'  ID='selectQuantityButton_" + fileId + "'  HREF=\"javascript:showQuantity('quantityPicker','selectQuantityButton_" + fileId + "','" + fileId + "');\" >" + Convert2ViewingI18N + "</A>";
      }
      cells[4].innerHTML = "<a href='#' onclick='removeAttachment(" + fileId + ");' class='ccIcon ccRIcon' role='button'><img src='i/delete.png' alt='" + removeI18n + "' /></a>";
      updateAllSizeInfo();
    }
    if (fileInfo != null) {
      var strurl = returnWorkerURL() + "&cmd=addUploadedFile&uploadFileName=" + encodedName + "&avStatus=" + fileInfo.avStatus;
      debug(strurl);
      $.ajax({url:strurl,async:false}).done(dosomething);
      
       $("#ShowMsgDIV").html("");
    }
  } catch (updateE) {
    alert(updateE);
  }
}

function showFileStatusDiv(avStatus){
  var color = "black";
  if (avStatus == "2") {
    color = "green";
  } else if (avStatus == "3") {
    color = "red";
  } else if (avStatus == "4") {
    color = "orange";
  }
  var title = noScanI18N;
  if (avStatus == "0") {
  } else if (avStatus == "1") {
    title = "Scanning";
  } else if (avStatus == "2") {
    title = "Clean";
  } else if (avStatus == "3") {
    title = "Virus Detected";
  } else if (avStatus == "4") {
    title = "Suspicious";
  }
  return "<FONT COLOR='" + color + "'>" + title + "</FONT>";
}


function uploadedResponse(fileId, rs){
  rs = rs.trim();
  var row = document.getElementById("fileId_" + fileId);
  var cells = row.cells;
  var fileName = cells[6].innerHTML;
  var i = rs.lastIndexOf("TCCUPLOADRS=");
  var rs = rs.substring(i + "TCCUPLOADRS=".length, rs.length);
  var rs_code = rs.split("|")[0];
  var av_code = rs.split("|")[1];
  if (rs_code == "1") {
    var fileInfo = getFileInfoFromFileListByFileID(fileId);
    fileInfo.avStatus = av_code;
    showCompeted(fileId);
  } else {
    debug("CCUploader upload file fileId: failure,errorcode rs:" + rs);
    showErrorMsg(fileName, rs_code);
    exceptionUpload(fileId);
  }
}


function cancelUpload(fileId){
  var row = document.getElementById("fileId_" + fileId);
  var cells = row.cells;
  cells[1].innerHTML = "canceled";
  cells[4].innerHTML = "<a href='#' onclick='removeAttachment(" + fileId + ");' class='ccIcon ccRIcon' role='button'>Delete</a>";
  var fileinfo = getFileInfoFromFileListByFileID(fileId);
  if (fileinfo != null) {
    fileinfo.status = STATUS_FAILURE;
    
  }
}

function exceptionUpload(fileId){
  var row = document.getElementById("fileId_" + fileId);
  var cells = row.cells;
  cells[1].innerHTML = "exception";
  cells[4].innerHTML = "<a href='#' onclick='removeAttachment(" + fileId + ");' class='ccIcon ccRIcon' role='button'>Delete</a>";
  var fileinfo = getFileInfoFromFileListByFileID(fileId);
  if (fileinfo != null) {
    fileinfo.status = STATUS_FAILURE;
    
  }
}

function returnWorkerURL(){
  return "q?cx=u&pg=MultiFileUploadDialog-Ajax&ppfx=" + document.attachment_list.ppfx.value;
}

function getFileInfoFromFileListByFileID(fileId){
  var i = 0;
  for (i = 0; i < fileinfolist.length; i++) {
    var fileInfo = fileinfolist[i];
    if (fileInfo.fileId == fileId) {
      return fileInfo;
      
    }
  }
  return null;
}

function getFileInfoFromFileListByFileName(filename){
  var i = 0;
  for (i = 0; i < fileinfolist.length; i++) {
    var fileInfo = fileinfolist[i];
    if (fileInfo.filename == filename) {
      return fileInfo;
      
    }
  }
  return null;
}

function removeFileInfoFromFileList(filename){
  var i = 0;
  for (i = 0; i < fileinfolist.length; i++) {
    var fileInfo = fileinfolist[i];
    if (fileInfo.filename == filename) {
      break;
    }
  }
  fileinfolist.splice(i, 1);
}

function getAllUploadedFileName(){
  var returnstr = "";
  var i = 0;
  for (i = 0; i < fileinfolist.length; i++) {
    var fileInfo = fileinfolist[i];
    if (i == 0) {
      returnstr += fileInfo.filename;
    } else {
      returnstr += ";" + fileInfo.filename;
    }
    
    
  }
  return returnstr;
}

function removeAttachment(fileId){
  var fileList = document.getElementById("filelist");
  var row = document.getElementById("fileId_" + fileId);
  var cells = row.cells;
  var fileName = cells[6].innerHTML;
  var encodedName = cells[7].innerHTML;
  var fileSize = parseInt(cells[5].innerHTML);
  var dosomething = function(){
    if (typeof(beforeRemoveAttachment) == "function") {
      beforeRemoveAttachment(fileId);
    }
    removeFileInfoFromFileList(fileName);
    var index = row.rowIndex;
    fileList.deleteRow(index);
    updateAllSizeInfo();
  }
	//escapeFileName
  var removeUrl = returnWorkerURL() + "&cmd=remove&filename=" + encodedName;
  debug(removeUrl);
  $.ajax({url:removeUrl,async:false}).done(dosomething);
 
}

function showQuantity(qtyDivId, aId, fileId){
  fileListformObj.conversionFileId.value = fileId;
  ccShowPickerById(qtyDivId, aId, '');
}

function submitVideoQuantity(){
  var fileId = fileListformObj.conversionFileId.value;
  convertATfile(fileId);
  
}

function convertATfile(fileId){
  var row = document.getElementById("fileId_" + fileId);
  var cells = row.cells;
  var fileName = cells[6].innerHTML;
  var encodedName = cells[7].innerHTML;
  var fileInfo = getFileInfoFromFileListByFileID(fileId);
  if (fileInfo != null) {
    var multiAllowSize = document.attachment_list.allowableTotalSize.value;
    var fileSize = cells[5].innerHTML;
    if (checkOnlineViewingSize(multiAllowSize, fileSize, "KB")) {
      var quantity = 0;
      var qtyBtns = document.attachment_list.quantity;
      var i = 0;
      for (i = 0; i < qtyBtns.length; i++) {
        if (qtyBtns[i].checked == true) {
          quantity = i;
          break;
        }
      }
      if (quantity == null) {
        quantity = 0;
      }
      
      var conversionUrl = returnWorkerURL() + "&cmd=convert2OnlineViewing&quantity=" + quantity + "&filename=" + encodedName + "&ppfx=" + document.attachment_list.ppfx.value + "&allowConvert=" + document.attachment_list.allowConvert.value;
      var dosomething = function(){
        cells[3].innerHTML = "<span class='ccMsg'>" + onlineViewingI18N + "</span>";
      }
      $.ajax({url:conversionUrl,async:false}).done(dosomething);
    }
  }
  ccClosePicker();
}

function checkOnlineViewingSize(calAllSize, filesize, unit){
  var remainSize = calAllSize - filesize * 2;
  //alert("calAllSize:"+calAllSize+"\n"+"filesize:"+filesize+"\n");
  if (remainSize > 0) {
    return true;
  } else {
    alert(warnNotEnoughStorageMessageI18N + (filesize * 2) + " " + unit + warnNotEnoughStorageMessageI18N2);
    return false;
  }
}

//return result
//-2 already Convert
//-1 no
//0 view
//1 video
function canOnlineViewing(fileName){
  var allowConvert = document.attachment_list.allowConvert.value;
  var result = -1;
  var viewExt = document.attachment_list.viewConversionExt.value.toLowerCase();
  var videoExt = document.attachment_list.videoConversionExt.value.toLowerCase();
  if ('Y' == allowConvert) {
    try {
      var viewExtArr = viewExt.split(",");
      var videoExtArr = videoExt.split(",");
      var dotPost = fileName.lastIndexOf(".");
      var len = fileName.length;
      var ext = fileName.substring((dotPost + 1), len);
      //alert(ext);
      var i = 0;
      if (ext && ext.length > 0) {
        ext = ext.toLowerCase();
        for (i = 0; i < viewExtArr.length; i++) {
          if (ext == viewExtArr[i]) {
            result = 0;
            break;
          }
        }
        if (result == -1) {
          for (i = 0; i < videoExtArr.length; i++) {
            if (ext == videoExtArr[i]) {
              result = 1;
              break;
            }
          }
        }
      }
    } catch (onlineE) {
      alert(onlineE)
    }
  }
  return result;
}


function checkFile(fileName, fileSize){
  if (fileName == null || fileName == '') {
    return -6;
  }
  try{
  var _max_file_count = parseInt(document.attachment_list.maxCount.value);
  var _max_allowed_size = parseInt(document.attachment_list.allowableTotalSize.value);
  var currentFileLen = getCurrentFileLen();
  var currentFileSize = getCurrentFileUploadedSize();
  var fileCount = currentFileLen;
  var uploadedFileSize = parseInt(currentFileSize) + parseInt(fileSize);
  var fileInfo = getFileInfoFromFileListByFileName(fileName);
  if (fileSize == 0) {
    return -1;
  } else if (uploadedFileSize >= _max_allowed_size) {
    return -2;
  } else if (fileCount >= _max_file_count) {
    return -3;
  } else if (fileInfo != null) {
    return -4;
  } else if (checkUploadedFileName(fileName)) {
    return -5;
  } else {
    return 0;
  }
  }catch(e){
    alert(e);
  }
  return -9;
 
}

var unvalidateString = new Array("#", "%");

var unvalidateStartWithString = new Array(".", "_", "!", "`", "$", "&");
String.prototype.startsWith = function(str){
  return (this.match("^" + str) == str)
}
function checkUploadedFileName(filename){
  var result = false;
  for (var j = 0; j < unvalidateString.length; j++) {
    if (filename.indexOf(unvalidateString[j]) > -1) {
      result = true;
      break;
    }
  }
  for (var k = 0; k < unvalidateStartWithString.length; k++) {
    if (filename.startsWith(unvalidateStartWithString[k])) {
      result = true;
      break;
    }
  }
  return result;
}

function showRightMsg(msg){
  var error_div = $("#ShowMsgDIV");
  error_div.className = "ccMsg";
  error_div.html(msg);
}

function showErrorMsg(fileName, errorCode){
  var error_div = $("#ShowMsgDIV");
  error_div.className = "ccErr";
  var errorMsg = "";
  if (errorCode == -1) {
    errorMsg = fileName + warnFileZeroI18n;
  } else if (errorCode == -2) {
    errorMsg = warnMaxSizeI18N;
  } else if (errorCode == -3) {
    errorMsg = "";
  } else if (errorCode == -4) {
    errorMsg = fileName + warnReuploadingFileNameI18N;
  } else if (errorCode == -5) {
    errorMsg = warnUnvalidateFileNameI18N;
  } else if (errorCode == -6) {
    errorMsg = watchNewFileI18n;
  } else {
  
    errorMsg = errorCode;
  }
  error_div.html(errorMsg);
}

