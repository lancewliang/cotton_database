CC.popupList=[];CC.win=[];
function openWin(winName,url){//popup window : openWin(winName,url,w,h,options,x,y)
  var w,a=arguments,l=a.length,o=[];o.name=winName;o.url=url;
  if(2<l){o.w=a[2]};if(3<l){o.h=a[3]};if(4<l){o.options=a[4];}if(6<l){o.x=a[5];o.y=a[6];}
  if(CC.win){return CC.win.open(o);}
  return w;
}
CC.win.open=function(o){//popup window : o={name,url,w,h,options,x,y,callback}
  if(typeof(ccSetUsageTrackingCookie)=="function"){ccSetUsageTrackingCookie();}  //for usageTracking
  var w=window,x,y,loc="",winOptions=',resizable,dependent'; if(o.options){winOptions+=","+o.options;}
  if (w.screenX){
    if(w.outerWidth){x=(o.x)?o.x:(w.screenX+(w.outerWidth-o.w)/2);y=(o.y)?o.y:(w.screenY+(w.outerHeight-o.h)/2);}
    else{x=(o.x)?o.x:(w.screenX+50);y=(o.y)?o.y:(w.screenY+50);}
    loc=",screenX="+x+",screenY="+y;
  } else if (w.screenLeft && top) { 
    var b=top.document.body; //IE
    if(b.clientWidth){x=(o.x)?o.x:top.screenLeft+(b.clientWidth-o.w)/2;y=(o.y)?o.y:top.screenTop+(b.clientHeight-o.h)/2;} 
    else {x=(o.x)?o.x:(top.screenLeft+50);y=(o.y)?o.y:(top.screenTop+50);}
    loc=",left="+x+",top="+y;
  }
  var winName=o.name.replace(/\s/g,""), pop=CC.popupList[winName]; //remove any space in window name;
  var feature= "width="+((o.w>screen.width)?(screen.width-20):o.w)+",height="+((o.h>screen.height)?(screen.height-20):o.h)+winOptions+loc;
  if(!pop){pop = w.open(o.url,winName,feature); ccPopList(winName, pop);}else{
    if(pop.closed){pop = w.open(o.url,winName, feature); ccPopList(winName, pop);} //pop up window has been closed.
    else{pop.location.href=o.url;}//pop up window is still open.
  }
  ccPopupFocus(pop); if(o.callback){o.callback();}
  return pop;
}

var ccPrintableAreaId;
function ccPopupFocus(win){try{win.focus();}catch(e){ccPopupAlert();}}
function ccPopList(winName, win){CC.popupList[winName]=win;}
function ccPopupAlert(){CC.script("/js/"+CC.lang+"/ccOpenWin-i18n.js",function(){alert(ccPopupAlertI18n)});}
function ccClosePopups(){for(var i in CC.popupList){var p=CC.popupList[i];try{if(p&&!p.closed) p.close();}catch(e){}}}
function ccDetachWin(winId){delete CC.popupList[winId];}
CC.login=function(){location.href="q?pg=login&tg=LoginEntry&cx=22."+CC.campusId;}
CC.logout=function(){location.href="q?pg=logout&cmd=SignOut&cx=22."+CC.campusId;}
CC.help=function(){openWin("help",'/cc/help.html?'+CC.ver,550,300,"location=false,toolbar=false,menubar=false,resizable,scrollbars,status");}
CC.support=function(){ccPopupFocus(window.open("q?pg=access_feedback&tg=Feedback&cid="+CC.campusId,"feeback","width=630,height=380,location=0,toolbar=0,menubar=0,resizable,scrollbars,status"));}
CC.navigation=function(){CC.script("js/ccSitemap.js",function(){CC.sitemap.show("ccNavLink")});}
function PrintFormat(){if(arguments[0]){ccPrintableAreaId=arguments[0];}else{ccPrintableAreaId="ccTgArea";}ccPrintableWin("/printable_area.html?"+CC.ver);}
function ccPrintableWin(url){openWin("printable", url,780,400,'status,menubar,resizable,scrollbars,toolbar');}
function openNewWin(winName,url,W,H,options){return openWin(winName,url+'&'+(new Date()).getTime(),W,H,options);}
function ccDetachWin(winId){delete CC.popupList[winId];}
function ccUnload(){if(typeof(ccUnloadLocal)=="function"){if(!ccUnloadLocal()){return false;}}if(typeof(ccClosePopups)=='function'){ccClosePopups();}}
if(window.attachEvent){window.attachEvent('onunload', ccUnload);}else if(window.addEventListener){window.addEventListener("unload", ccUnload, true);}
if(ccUseAccessibleProfile()){CC.script("/js/ccTableAsPresentation.js")}