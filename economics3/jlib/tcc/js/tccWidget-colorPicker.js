(function(TCC) {

  var TPL = {
    colorPickerContainer : "<div class='colorPicker'></div>",
    colorItem : "<li><a href='javascript:void(0);' style='background:{$color}' color='{$color}'></a></li>"
  };

  TCC.widget("colorPicker", {
    options : {
      colors : [ "rgb(0,0,0)", "rgb(153,153,153)", "rgb(255,255,255)", "rgb(255,0,0)", "rgb(255,153,153)", "rgb(255,153,0)", "rgb(255,204,0)", "rgb(153,153,51)", "rgb(51,255,51)", "rgb(0,102,153)", "rgb(51,153,153)", "rgb(51,204,255)", "rgb(0,0,255)", "rgb(204,0,204)",
          "rgb(153,0,255)", "rgb(204,153,255)" ],
      parentContainer : null,
      clickFn : null,
      colorChangeFn : null
    },

    _init : function() {
      var that = this;
      var wId = this.getWId();
      if (!that.options.parentContainer) {
        this.on("click", function(evt) {
          var cp = TCC.find(".colorPicker." + wId);
          if (typeof that.options.clickFn === "function") {
            that.options.clickFn.call(that, cp, evt.currentTarget);
          } else {
            if (cp.isVisible()) {
              cp.hide();
            } else {
              var target = evt.currentTarget;
              var offset = target.offset();
              cp.css({
                position : "absolute",
                left : offset.left + "px",
                top : offset.top + target.height() + 6 + "px"
              });
              cp.show();
            }
          }
        });
      }
      
      TCC.find(".colorPicker." + wId).delegate("click", "LI A", function(evt) {
        var target = evt.currentTarget;
        var color = target.attr("color");
        var origColor = that.attr("color");
        if (color !== origColor && that.options.colorChangeFn !== null) {
          that.options.colorChangeFn.call(that, color);
        }
        that.css("background-color", color);
        that.attr("color", color);
        that.destroy();
      });
    },

    _create : function() {
      var colorPickerContainer = TCC.create(TPL.colorPickerContainer);
      var colorConent = [];
      for ( var i = 0, len = this.options.colors.length; i < len; i++) {
        var color = this.options.colors[i];
        colorConent.push(TPL.colorItem.replace("{$color}", color).replace("{$color}", color));
      }
      colorConent = colorConent.join("");
      colorConent = "<ul>" + colorConent + "</ul>";
      colorPickerContainer.appendChild(colorConent);
      colorPickerContainer.hide();

      var wId = this.getWId();
      colorPickerContainer.addClass(wId);
      if (this.options.parentContainer) {
        this.options.parentContainer.appendChild(colorPickerContainer);
      } else {
        TCC.find(document.body).appendChild(colorPickerContainer);
      }
    },

    destroy : function() {
      var that = this;
      var wId = that.getWId();
      TCC.find("." + wId).hide();
    }
  });
})(TCC);