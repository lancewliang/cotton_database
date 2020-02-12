//depend on ccCommon.js

function ccAddSelected(fromSelect,toSelect) { //add selected option from A (fromSelect) to B (toSelect)
  
  var i=fromSelect.selectedIndex;
  if (i == -1) return;
  var fooValue=fromSelect.options[i].value;
  var fooText=fromSelect.options[i].text;
  var j=toSelect.options.length;
  
  //replace uninitialized item.
  if (j==1 && toSelect.options[0].value=="") {
    toSelect.options[0].value = fooValue;
    toSelect.options[0].text = fooText;
    return toSelect.options[0];
  } else {
    for (i=0;i<j;i++) {
      if (toSelect.options[i].value==fooValue) {
        return;
      }
    }
    var fooOpt = new Option(fooText,fooValue);
    toSelect.options[j] = fooOpt;
    return fooOpt;
  }
}

function ccAddAllSelected(fromSelect,toSelect) { //add selected option from A (fromSelect) to B (toSelect)
  var i=fromSelect.selectedIndex;
  if (i == -1) return;
  var len = fromSelect.options.length;
  for( k=0; k<= len-1; k++) {
    if(fromSelect.options[k].selected == true) {
      var fooValue=fromSelect.options[k].value;
      var fooText=fromSelect.options[k].text;
      var j=toSelect.options.length;
  
      //replace uninitialized item.
      if (j==1 && toSelect.options[0].value=="") {
        toSelect.options[0].value = fooValue;
        toSelect.options[0].text = fooText;
      } else {
        var found = false;
        for (i=0;i<j;i++) {
          if (toSelect.options[i].value==fooValue) {
            found = true;
            break;
          }
        }
        if (!found) {
          var fooOpt = new Option(fooText,fooValue);
          toSelect.options[j] = fooOpt;
        }
      }
    }
  }
}

function ccRemoveSelected(obj) { //remove selected option
  var  force = false;
  if (arguments[1] && arguments[1] == true) { force = true;}
  for(var i=obj.options.length-1; i>=0; i--) {
    if(obj.options[i].selected == true) {
      if (force) {
        obj.options[i]=null;
      } else {
        var value = obj.options[i].value;
        if(value!='') {obj.options[i]=null;}
      }
    }
  }
}

function ccMoveUp(obj) {
  var label;
  if (arguments[1]) {label = arguments[1];}
  else if (typeof(ccLookupLabel) == "function") { label = ccLookupLabel(obj);}
  var i=obj.selectedIndex;
  if (i == -1 ) {
    if (label) {
      alert(ccMoveUpI18n.replace(/{label}/, label) ); //i18n, "Please select an item in the {label}.";
    }
    return;
  }
  if (i == 0) {return;}
  var j=i-1;
  var fooValue=obj.options[i].value;
  var fooText=obj.options[i].text;
  obj.options[i].value=obj.options[j].value;
  obj.options[i].text=obj.options[j].text;
  obj.options[j].value=fooValue;
  obj.options[j].text=fooText;
  if (document.all) { //IE
    obj.selectedIndex = j;
  } else {
    ccWorkingSelect = obj;
    ccWorkingSelectIndex = j;
    setTimeout('ccSetSelectedIndex()', 10);
  }
}

var ccWorkingSelect;
var ccWorkingSelectIndex;
function ccSetSelectedIndex() {ccWorkingSelect.selectedIndex = ccWorkingSelectIndex;}

function ccMoveToTop(obj) {
  var label;
  if (arguments[1]) {label = arguments[1];}
  else if (typeof(ccLookupLabel) == "function") { label = ccLookupLabel(obj);}
  var i=obj.selectedIndex;
  if (i == -1 ) {
    if (label) {
      alert(ccMoveUpI18n.replace(/{label}/, label) ); //i18n, "Please select an item in the {label}.";
    }
    return;
  }
  if (i == 0) {return;}
  var fooValue=obj.options[i].value;
  var fooText=obj.options[i].text;
  for ( var j = i; j > 0; j-- ) {
    obj.options[j].value=obj.options[j-1].value;
    obj.options[j].text=obj.options[j-1].text;
  }
  obj.options[0].value=fooValue;
  obj.options[0].text=fooText;
  obj.selectedIndex = 0;
}

function ccMoveDown(obj) {
  var label;
  if (arguments[1]) {label = arguments[1];}
  else if (typeof(ccLookupLabel) == "function") { label = ccLookupLabel(obj);}
  var i=obj.selectedIndex;
  if (i == -1 ) {
    if (label) {
      alert(ccMoveUpI18n.replace(/{label}/, label) ); //i18n, "Please select an item in the {label}.";
    }
    return;
  }
  var len=obj.options.length - 1;
  if (i == len) {return;}
  var j=i+1;
  var fooValue=obj.options[i].value;
  var fooText=obj.options[i].text;
  obj.options[i].value=obj.options[j].value;
  obj.options[i].text=obj.options[j].text;
  obj.options[j].value=fooValue;
  obj.options[j].text=fooText;
  if (document.all) { //IE
    obj.selectedIndex = j;
  } else {
    ccWorkingSelect = obj;
    ccWorkingSelectIndex = j;
    setTimeout('ccSetSelectedIndex()', 10);
  }
}

function ccMoveToBottom(obj) {
  var label;
  if (arguments[1]) {label = arguments[1];}
  else if (typeof(ccLookupLabel) == "function") { label = ccLookupLabel(obj);}
  var i=obj.selectedIndex;
  if (i == -1 ) {
    if (label) {
      alert(ccMoveUpI18n.replace(/{label}/, label) ); //i18n, "Please select an item in the {label}.";
    }
    return;
  }
  var len=obj.options.length - 1;
  if (i == len) {return;}
  var fooValue=obj.options[i].value;
  var fooText=obj.options[i].text;
  for ( var j=i; j<=len-1; j++ ) {
    obj.options[j].value=obj.options[j+1].value;
    obj.options[j].text=obj.options[j+1].text;
  }
  obj.options[len].value=fooValue;
  obj.options[len].text=fooText;
  if (document.all) { //IE
    obj.selectedIndex = len;
  } else {
    ccWorkingSelect = obj;
    ccWorkingSelectIndex = len;
    setTimeout('ccSetSelectedIndex()', 10);
  }
}

function ccCollectSelected(fooOption) { //append all selected item value delimit with , and return it.
  var dl = ",";
  if (arguments[1]) {dl=arguments[1];}
  var ret=""
  var len=fooOption.options.length;
  if (len ==0 ) {
    return ret;
  } else {
    ret=fooOption.options[0].value;
    for (var i=1;i<len;i++) {
      ret += dl + fooOption.options[i].value;
    }
  }
  return ret;
}

function ccAddOption(ccObj, ccText, ccValue) { // add one option into select object. ccObj : select object, ccText : new option text, ccValue : new option value.
  ccValue += ""; //in case of number
  var ccNewOption = new Option(ccText,ccValue);
  ccObj.options[ccObj.options.length] = ccNewOption;
  return ccNewOption;
}
function ccRemoveAllOptions(ccObj) { // remove option with value (ccValue)
  var len = ccObj.options.length;
  for ( var i = len - 1; i >= 0; i--) {

    ccObj.options[i] = null;

  }
}

function ccRemoveOption(ccObj, ccValue) { //remove option with value (ccValue)
  var len = ccObj.options.length;
  for(var i=len-1; i>=0; i--) {
    if(ccObj.options[i].value == ccValue) {
      ccObj.options[i]=null;
    }
  }
}


function ccSelectOption(ccObj, ccSelectedValue) { //ccObj : a select object, ccSelectedVale : selected value
  for (var j=0;j<ccObj.options.length;j++) {
    if (ccObj.options[j].value==ccSelectedValue) {
      ccObj.selectedIndex=j;
      return;
    }
  }     
}

function ccFindOption(ccObj, ccSelectedValue) { //ccObj : a select object, ccSelectedVale : selected value
  for (var j=0;j<ccObj.options.length;j++) {
    if (ccObj.options[j].value==ccSelectedValue) {
      return ccObj.options[j];
    }
  }
}

function ccUpdateOption(ccObj, ccText, ccValue, addIfNotSelected) {
  var len = ccObj.options.length;
  for(var i=len-1; i>=0; i--) {
    if(ccObj.options[i].selected == true) {
      ccObj.options[i].value = ccValue;
      ccObj.options[i].text = ccText;
      return;
    }
  }
  if ( addIfNotSelected ) {ccAddOption(ccObj,ccText,ccValue);}
}

function ccSetCategoryList(selObj,def,sorted) {
  if ( catList == '' ) {return;}
  var cats = catList.split(catSep);
  if ( sorted ) cats.sort();
  var j = selObj.options.length;
  for ( var i=0; i<cats.length; i++, j++ ) {
    var opt = new Option(cats[i],cats[i]);
    selObj.options[j] = opt;
  }
  var defs = def.split(catSep);
  for ( var i=0; i<defs.length; i++ ) {
    for ( var j=0; j<selObj.options.length; j++ ) {
      if ( defs[i] == selObj.options[j].value ) {
        selObj.options[j].selected = true;
        break;
      }
    }
  }
}

/*
 It needs these global variables: 
   1) opSep, optionList seperator, optional, default is "|";
 About parameters:
   1) selObj, html "select" element;
   2) optionList, team list string, required, of "value|text|value|text ..." format;
   3) def, selected options' value string, of "value|value..." format;
*/
function ccSetOptionList(selObj,optionList, def) {
  if ( optionList == '' ) {return;}
  var sep = "|";
  if(typeof(opSep) != 'undefined') {
    sep = opSep;
  }
  var cats = optionList.split(sep);
  var j = selObj.options.length;
  for ( var i=0; i<cats.length; i+=2, j++ ) {
    var opt = new Option(cats[i+1],cats[i]);
    selObj.options[j] = opt;
  }
  if(sep != ''){
    var defs = def.split(sep);
    for ( var i=0; i<defs.length; i++ ) {
      for ( var j=0; j<selObj.options.length; j++ ) {
        if ( defs[i] == selObj.options[j].value ) {
          selObj.options[j].selected = true;
          break;
        }
      }
    }
  }
}

var ccSelectObj={
  setValue:function(param){//{id:"elementId",label:"",value:"",insert:"true"]}
    var a=document.getElementById(param.id);
    if(typeof(param.insert)=='undefined'){
      param.insert=true;
    }
    if(a){
      if(param.insert){
        a.options[a.length]=new Option(param.label,param.value);
      }else{
        var len=a.options.length;
        for(var i=0;i<len;i++){
          if(a.options[i].value==param.value){
            a.options[i].selected=true;
            break;
          }
        }
      }
    }
  },
  getValue:function(param){//{id:"elementId",,separator:"|",check:"true",hasPrefix:"true"]}
    if(typeof(param.separator)=='undefined'){
      param.separator=",";
    }
    if(typeof(param.hasPrefix)=='undefined'){
      param.hasPrefix="false";
    }
    var a=document.getElementById(param.id),s="";
    if(a){
      var len=a.options.length;
      for(var i=0;i<len;i++){
        if(param.check=="true"){
          if(!a[i].checked){
            continue;
          }
        }else if(param.check=="false"){
          if(a[i].checked){
            continue;
          }
        }
        if(s!=''){
          s+=param.separator;
        }
        s+=a.options[i].value;
      }
      if(s!='' && param.hasPrefix=="true"){
        s=param.separator+s+param.separator;
      }
    }
    return s;
  }
}

/** 
 * sel:select obj
 * Author : Allen Yang;
* */
function ccGetSelectedOption(sel){
	var multipleReturns = [];
	var opts = sel.options;
	for(var i=0;i<opts.length;i++){
		if(opts[i].selected == true){
			if(sel.multiple == true){
				multipleReturns[multipleReturns.length] = opts[i];
			}else
			  	return opts[i];
		}
	}
	return multipleReturns;
}
 
