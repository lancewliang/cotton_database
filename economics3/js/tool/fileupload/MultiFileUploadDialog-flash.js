
function showUploadFile(fileId, fileName,fileSize){
  
    addFileInfoList(fileId, fileName, fileSize, "0");
   
}

function beforeRemoveAttachment(fileId){

  var CCFileUploader = document.getElementById("CCFileUploader");
  if (CCFileUploader) {
   try{
    CCFileUploader.deleteUpload(fileId);
   }catch(e){}
  }
}
function allowUpload(){
  var CCFileUploader = document.getElementById("CCFileUploader");
  if (CCFileUploader) {
    try{
    CCFileUploader.btnDisplay(true);
    }catch(e){}
  }
  $("#suppendUploadMainDIV").hide();
  $("#uploadMainDIV").show();
  
  
}
function refusedUpload(){  
  var CCFileUploader = document.getElementById("CCFileUploader");
  if (CCFileUploader) {
    try{
      CCFileUploader.btnDisplay(false);
    }catch(e){
    alert(e.message);}
  }
  $("#uploadMainDIV").hide();
  $("#suppendUploadMainDIV").show();
}