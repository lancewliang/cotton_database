var ccCommunity;
if(typeof(CC)=="undefined"){CC={ver:"",scripts:[],callBack:[],
  $:function(id,o){return o?o.getElementById(id):document.getElementById(id)},
  log:function(s){var c=arguments.callee.caller;console.log("CC log"+(c&&!document.all?" "+c.name+"()":"")+" > "+s);},
  delay:function(fn){CC.on(window,'load',fn);},
  ready:function(o,fn){
    var _loaded=function(){if(o.readyState=="complete"){fn}}
    if(o.readyState=="complete"){n} 
    else if(o.addEventListener){o.addEventListener("DOMContentLoaded",fn,false);}
    else if(o.attachEvent){o.attachEvent("onreadystatechange",_loaded);}
    else{setTimeout(fn,2000);}
  },
  link:function(url,text){//url,text,attr
    if(!url||!text){return}if(text.indexOf("&lt;/")!=-1){text=text.replace(/&lt;/g,"<").replace(/&gt;/g,">");}
    var a=arguments;return "<A HREF='"+url+"'"+((a.length==3)?" "+a[2]:"")+">"+text+"</A>";
  },
  head:document.getElementsByTagName("head")[0],
  on:function(o,e,fn){if(o.attachEvent){o.attachEvent("on"+e,fn)}else{o.addEventListener(e,fn,false)}},
  script:function(src,callback){
    if(CC.ver){
      var idx=src.indexOf('?'),url=src,tag="";
      if(idx!=-1){url=src.substring(0,idx);tag=src.substring(idx + 1).trim();if(tag!=""){tag+="+";}}
      src=url+'?'+tag+CC.ver;
    }
    function _loaded(k2,isCallback){
      CC.scripts[k2]=2;if(!isCallback&&!CC.callBack[k2]){return;}
      var f;while(CC.callBack[k2].length>0){f=CC.callBack[k2].shift();if(d.all){setTimeout(f,0);}else{f();}} delete CC.callBack[k2];
    }
    function _loading(o,k,isCallback){
      if(d.all){o.onreadystatechange=function(){var r=this.readyState;if(r==4||r=='complete'||r=='loaded'){_loaded(k,isCallback);}}}
      else{o.onload=function(){_loaded(k,isCallback);}}
    }
    var d=document,k=src.replace(/\//g,"").replace(/\./g,"")+"";
    var isCallback=typeof(callback)=="function",i=k.indexOf("?");if(i>0){k=k.substring(0,i);}
    if(!CC.scripts[k]){
      CC.scripts[k]=1;if(isCallback){CC.callBack[k]=[];CC.callBack[k].push(callback);}
      var o=d.createElement('script');o.type='text/javascript';o.src=src;o.id=k;
      CC.head.appendChild(o);_loading(o,k,isCallback);
    }else if(CC.scripts[k]==1){if(isCallback){if(!CC.callBack[k]){CC.callBack[k]=[];} _loading(CC.$(k),k,isCallback); CC.callBack[k].push(callback);}
    }else if(isCallback){if(d.all){setTimeout(callback,0);}else{callback();}}
  },
  offsetX:function(o){var l=0;while(1){l+=o.offsetLeft;if(!o.offsetParent){break;}o=o.offsetParent;}return l;},
  offsetY:function(o){var t=0;while(1){t+=o.offsetTop;if(!o.offsetParent){break;}o=o.offsetParent;}return t;},
  x:function(o){
    function _left(o){return (o.parentNode)?o.scrollLeft+_left(o.parentNode):0}
    var bodyOffset=(navigator.userAgent.toLowerCase().indexOf('firefox')!=-1)?CC.style.size(document.body,"margin-left"):0;
    return CC.offsetX(o)-_left(o)+bodyOffset;
  },
  y:function(o){
    function _top(o){return (o.parentNode)?o.scrollTop+_top(o.parentNode):0}
    var parentTop=0; if(CC.isTabSlidable()){parentTop=o.scrollTop+_top(o.parentNode);}
    var bodyOffset=(navigator.userAgent.toLowerCase().indexOf('firefox')!=-1)?CC.style.size(document.body,"margin-top"):0;
    return CC.offsetY(o)-parentTop+bodyOffset;
  }
}
CC.style={
  value:function(o,s){
    if(o.currentStyle){
      var r=/(\-([a-z]){1})/g,p=s; if(p=='float'){p='styleFloat';}
      if(r.test(p)){p=p.replace(r,function(){return arguments[2].toUpperCase();});}
      return o.currentStyle[p]?o.currentStyle[p]:null;
    }else{try{return window.getComputedStyle(o,null).getPropertyValue(s);}catch(e){}}
  },
  size:function(o,s){var r=(CC.style.value(o,s)+"").toLowerCase(),ret=(r.indexOf("em")>0)?r.replace(/em/,"")*12:parseInt(r);return isNaN(ret)?0:ret;},
  link:function(s){
    var idx=s.indexOf('?'),id=(idx==-1)?s:s.substring(0,idx);
    if(CC.ver){
      var url=s, tag="";
      if(idx!=-1){url=id;tag=s.substring(idx+1).trim();}
      s=url+"?"+((tag == "")?CC.ver:tag+"+"+CC.ver);
    }
    id="css"+id.replace(/\//g,"_");
    var o=CC.$(id);
    if(!o){o=document.createElement("LINK");o.rel="stylesheet";o.type="text/css";o.href=s;o.id=id;CC.head.appendChild(o);}
    return o;
  },
  add:function(s){
    var d=document,o=d.createElement("STYLE");o.setAttribute("type","text/css");
    if(o.styleSheet){o.styleSheet.cssText=s;}else{o.appendChild(d.createTextNode(s));}
    CC.head.appendChild(o);return o;
  },
  showBlock:function(s){s.visibility="visible";s.display="block";},
  hideBlock:function(s){s.visibility="hidden";s.display="none";}
}
CC.tab={
  isPreventOverrun:function(){return CC.ui&&CC.ui.t=="P";},
  isSlidable:function(){return CC.ui&&CC.ui.m=="S";},
  isFitInWinwdow:function(){return CC.tab.isPreventOverrun()||CC.tab.isSlidable();}
}
CC.stab={}
CC.ready(document,CC.script("/js/tcc/ccLoaded.js"));
String.prototype.trim=function(){return this.replace(/^\s+/,'').replace(/\s+$/,'')}
}
function ccAreaExpanded(id){return ccObjExpanded(CC.$(id))}
function ccObjExpanded(o){var s=o.style;if(s.display){return!(s.display=="none"||s.display=="")}else{return(o.className!="ccHidden")}}
function ccToggleArea(id){//id:block Id
  var o=CC.$(id);if(o){var s=CC.style.value(o,"display");if(s=="none"||s==""){ccExpandObj(o)}else{ccCollapseObj(o)}}
  return false;
}
function ccExpandArea(id){ccExpandObj(CC.$(id))}
function ccExpandObj(o){ //obj
  if (o==null){return false;}
  var t=o.tagName,s=o.style;o.style.visibility="visible";
  if(t=="DIV"){s.display="block"}else if(t=="SPAN"||t=="INPUT"||t=="A"||t=="BUTTON"||t=="NOBR"||t=="IMG"){s.display="inline"}
  else{
    if(document.all){s.display="block"}else{
      if(t=="TABLE"){s.display="table"}else if(t=="TR"){s.display="table-row"}else if(t=="THEAD"||t=="TBODY"){s.display="table-row-group"}
      else if(t=="TD"||t=="TH"){s.display="table-cell"}else if(t=="LI"){s.display="list-item"}else{s.display="block"}
    }
  }
  return false;
}
function ccCollapseArea(id){return ccCollapseObj(CC.$(id));}
function ccCollapseObj(o){if(o==null){return false;}o.style.display="none";o.style.visibility="hidden";return false;}
function ccGetCookie(k){
  var c=document.cookie;if(c.length==0){return null;}
  var i=c.indexOf(k+"=");if(i!=-1){i+=k.length+1;var j=c.indexOf(";",i);if(j==-1){j=c.length;}return decodeURIComponent(c.substring(i,j));}
  return null;
}
function ccSetCookie(k,v,e,path){//key, value, expired, path
  var s=k+"="+encodeURIComponent(v);
  if(e){var d=new Date();d.setTime(d.getTime()+(e*24*3600*1000));s=s+"; expires="+d.toGMTString();}
  if(path){s=s+"; path="+path;}
  document.cookie=s;
} 
function ccDelCookie(k){if(ccGetCookie(k)){document.cookie=k+"=" + "; expires=Thu, 01-Jan-70 00:00:01 GMT";}}
function ccSetHiddenStyle(id){CC.style.add("#"+id+" {display:none;visibility:hidden;}")}
function ccUseAccessibleProfile(){return CC.access.is();}
CC.access={
  _cookie:function(){return ccGetCookie("CC_accessible")},
  is:function(){return CC.access._cookie()!=null},
  screenReader:function(){return "screenReader"==CC.access._cookie()},
  keyboard:function(){return "keyboard"==CC.access._cookie()}
}
CC.isTabSlidable=function(){return CC.ui&&CC.ui.m=="S";}
CC.isMenuOverlay=function(){return CC.ui&&CC.ui.m=="O";}
CC.isContentHeightFixed=function(){return CC.ui&&CC.ui.m=="S";}
CC.isLeftNav=function(){return CC.ui&&CC.ui.t=="L";}
function ccTag(o){var a="";if(o.attr){a=" "+attr;}return "<"+o.tag+a+">"+o.lb+"</"+o.tag+">";}
function ccTrackingClick(linkObj,id){ 
  if(linkObj != null){
    var url=linkObj.getAttribute("linkurl");if(url!=null&&url.toLowerCase().indexOf("://")==-1){url ="http://"+url; linkObj.linkurl=url;}
    linkObj.target = "_blank";
    linkObj.href = "PageServlet?pg=Bookmark-worker&tg=Bookmark-worker&cmd=clickLink&bookmarkid="+id+"&link="+encodeURIComponent(url);
  }
}