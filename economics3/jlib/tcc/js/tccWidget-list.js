(function(TCC) {
  TCC.widget("list", {
    // These options will be used as defaults
    options : {
      id : null,
      listContent : [], // array
      position : {
        top : -999,
        left : -999,
        right : -999,
        bottom : -999
      }, // top, left, right, default top, left is 0
      width : 200, // if is number, set it, else if element, set the element's
      // width
      triggerType : 'click',
      callBackFn : null
    // when click on li, will trigger this, and the argument is the index of li,
    // the context is li, After click, the list will hidden, if return false
    // list will still display
    },
    _init : function() {

    },
    _create : function() {
      var self = this;
      self.isShow = false;
      var content = [];
      var list = self.options.listContent;
      var length = list.length;
      var listWrapObj = TCC.create("<div class='tccListWrap'/>");
      if (self.options.id) {
        listWrapObj.attr("id", self.options.id);
      }
      var parentObj = self.parent();
      parentObj.appendChild(listWrapObj);
      listWrapObj.appendChild(this);

      for ( var i = 0; i < length; i++) {
        if (i == 0) {
          content.push("<div style='display:none;' class='tccListBorder'><ul class='tccListContent'><li class='tccListItem tccListItem" + i + "'>");
          content.push(list[i]);
          content.push("</li>");
        } else if (i == (length - 1)) {
          content.push("<li class='tccListItem tccListItem" + i + "'>");
          content.push(list[i]);
          content.push("</li></ul></div>");
        } else {
          content.push("<li class='tccListItem tccListItem" + i + "'>");
          content.push(list[i]);
          content.push("</li>");
        }
      }
      var relativeBorderObj = TCC.create("<div class='tccListRelativeBorder'>" + content.join("") + "</div>");
      listWrapObj.appendChild(relativeBorderObj);
      self.borderObj = relativeBorderObj.find(".tccListBorder");
      self._setPosition();
      // set position and width
      self.borderObj.css("width", self.options.width + "px");
      // start bind event
      if (self.options.triggerType == "hover") {
        self.hover(function(e) {
          self.borderObj.show();
        }, function(e) {
          var borderObj = self.borderObj;
          borderObj.hover(function() {
            borderObj.show();
          }, function() {
            borderObj.hide();
          });
          if (!self.isShow) {
            borderObj.hide();
          }
        });
      } else {
        self.on("click", function(e) {
          e.e.stopImmediatePropagation();
          self.expand(self);
          LIST_SHOW = true;
        });
      }
      var liObjs = self.borderObj.find("li");
      self.borderObj.delegate("click", "li", function(e) {
        var res = -1;
        liObjs.each(function(node, index) {
          if (node === e.currentTarget.originalObj[0]) {
            res = index;
          }
        });
        if (!self.options.callBackFn || self.options.callBackFn(res) != false) {
          self.collapse(self);
        }
      });
      var overFn = function(e) {
        if (e.currentTarget.hasClass("tccListOver")) {
          e.currentTarget.removeClass("tccListOver");
        } else {
          e.currentTarget.addClass("tccListOver");
        }
      }
      liObjs.on("mouseover", overFn);
      liObjs.on("mouseout", overFn);

      TCC.find(document).on("click", this._documentClick, this);
    },

    _documentClick : function(event) {
      var flag = false;// self.borderObj.has(event.target).length ? true :
      // false;
      if (flag == true || event.data == event.currentTarget) {
        // do nothing
      } else {
        if (event.data == null) {
          TCC.find(document).unbind("click", this._documentClick);
        } else {
          event.data.collapse(event.data);
        }
      }
    },

    _setPosition : function() {
      var self = this;
      var positionCss = {};
      if (self.options.position.top == null && self.options.position.bottom == null) {
        positionCss.top = 0;
      } else if (self.options.position.top == null && self.options.position.bottom != null) {
        positionCss.bottom = self.options.position.bottom;
      } else if (self.options.position.top !== -999) {
        positionCss.top = self.options.position.top;
      }
      if (self.options.position.left == null && self.options.position.right == null) {
        positionCss.left = 0;
      } else if (self.options.position.left == null && self.options.position.right != null) {
        positionCss.right = self.options.position.right;
      } else if (self.options.position.left != null) {
        positionCss.left = self.options.position.left;
      }
      self.borderObj.css(positionCss);
    },

    expand : function(self) {
      if (self.isShow == true) {
        self.collapse(self);
      } else {
        for ( var w in TCC.widgets) {
          if (w.indexOf("wlist") != -1) {
            if (TCC.widgets[w] != null && TCC.widgets[w].isShow == true) {
              TCC.widgets[w].collapse(TCC.widgets[w]);
            }
          }
        }
        self.borderObj.show();
        self.isShow = true;
        self.addClass("over");
      }
    },
    collapse : function(self) {
      self._trigger("onCollapse");
      self.borderObj.hide();
      self.isShow = false;
      self.removeClass("over");
    },

    destroy : function() {
      this.collapse(this);
      TCC.find(document).unbind("click", this._documentClick);
      // Use the destroy method to reverse everything your plugin has applied
      TCC.superWidget.prototype.destroy.call(this);
    }
  });
})(TCC);