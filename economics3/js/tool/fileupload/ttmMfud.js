// File: ccMultiFileUpload.js
// for invoking multiple file upload dialog

/**
 * Popup the dialog for uploading multiple files. Usually is invoked by user
 * clicking on a link or button.
 */
function showMultiFileUploadDialog(ppfx, mf, params) { // ppfx : transaction
  // key, mf:show MyFiles,
  // params:
  // {p_hasFileParams:"true",p_pagePrefix:"`pagePrefix`",p_transactionKey:"`transactionKey`"}
  var url = "q?pg=MultiFileUploadDialog-Flash&ppfx=" + ppfx;

  if (typeof (mf) != 'undefined' && mf != null) {
    url += "&mf=" + mf;
  }
  if (typeof (params) != 'undefined' && params) {
    for ( var p_key in params) {
      url += "&" + p_key + "=" + params[p_key];
    }
  }
  var showWin = openNewWin('attachWin', url, 500, 465, "resizable,scrollbars,status");
  return showWin;
}

/**
 * Externally set the values in the template make use of data(attList) and
 * selection list(attachmentList)
 */
function prepareAttachmentListOnScreen(list, fileList) {
  var selectList = list;
  selectList.length = 0;
  if (fileList == null) {
    return;
  }
  var files = fileList.split(";");
  for ( var i = 0; i < files.length; i++) {
    selectList[i] = new Option(files[i]);
    selectList[i].text = files[i];
    selectList[i].value = files[i];
  }
}