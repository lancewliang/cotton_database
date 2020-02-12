(function(TCC) {
  var LIST_WRAPER = "<div class='tccContentListWrap'><div class='tccContentListDetail'></div></div>";
  var UL_HTML = "<ul class='tccContentListUl'></ul>";
  var CONTENT_TEMPLATE = "<li class='tccContentListItem'>{$item}</li>";

  TCC.widget("contentList", {
    options : {
      rowCountInCol : 3,
      dataSource : "", // please use the selector to define the datasource
      offSet : {
        offSetX : 0,
        offSetY : 0
      }
    },
    _init : function() {
      this._setPosition();
    },
    _create : function() {
      // inner variable
      this.colCount = 0;

      var self = this;
      var wraperObj = TCC.find(LIST_WRAPER);
      this.wraperObj = wraperObj;
      TCC.find(document.body).appendChild(wraperObj);
      var dataObjs = TCC.find(this.options.dataSource).find("div");
      var htmlArray = [];
      var ulObj = null;
      var i = 0;
      var colCount = 0;
      var detailObj = wraperObj.find("div");
      dataObjs.each(function(node) {
        if (i % self.options.rowCountInCol == 0) {
          ulObj = TCC.find(UL_HTML);
          if (colCount == 0) {
            ulObj.addClass("borderFree");
          }
          detailObj.appendChild(ulObj);
          colCount++;
        }
        htmlArray.push(CONTENT_TEMPLATE.replace("{$item}", TCC.find(node).html()));
        if ((i + 1) % self.options.rowCountInCol == 0 || (i + 1) == dataObjs.size()) {
          ulObj.html(htmlArray.join(""));
          htmlArray.length = 0;
        }
        i++;
      });
      this.colCount = colCount;
      detailObj.appendChild("<div style='clear:both'></div>");
      var tccContentListWrapObj = TCC.find(".tccContentListWrap");
      var totalWidth = 0;
      tccContentListWrapObj.each(function(node) {
        totalWidth = totalWidth + TCC.find(node).width();
      });
      tccContentListWrapObj.css("width", totalWidth + 2 * (this.colCount - 1) + "px");
    },

    _setPosition : function() {
      var pObj = this.position();
      this.wraperObj.css({
        top : pObj.top + this.options.offSet.offSetX + "px",
        left : pObj.left + this.width() + this.options.offSet.offSetY + "px"
      });
    },
    destroy : function() {
      this.colCount = null;
      this.wraperObj.remove();
      this.wraperObj = null;
      TCC.superWidget.prototype.destroy.call(this);
    }
  });
})(TCC);