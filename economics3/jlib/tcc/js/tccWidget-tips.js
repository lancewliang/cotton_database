(function(TCC) {
  TCC.widget("tips", {
    // These options will be used as defaults
    options : {
      tips : null,
      width : null,
      heigth : null,
      alignTo : []
    // [top, left] default, [top, right], [bottom, left], [bottom,
    // right]
    },

    _create : function() {
      TCC.log("create");
      var self = this;
      // self.i =0,self.j=0;
      var borderObj = TCC.find("." + "tccTips-"+this.getWId()+"-border");
      if(borderObj.size() == 0){
        borderObj = TCC.create("<div class='tccTipsBorder tccTips-"+this.getWId()+"-border'><div class='tccTipsWrapper'><div class='tccTipsArrow'></div><div class='tccTipsContent'></div></div></div>");
        TCC.find(document.body).appendChild(borderObj);
      }
      self.borderObj = borderObj;

      this.on("mouseenter", (TCC.proxy(self.show, self)));
      this.on("mouseout", (TCC.proxy(self.close, self)));
      this.borderObj.on("mouseout", (TCC.proxy(self.close, self)));
    },

    _init : function() {
      TCC.log("init");
      var self = this;
      if (self.options.width) {
        var a = self.borderObj.find(".tccTipsWrapper");
        a.css("width", self.options.width);
      }
      if(self.options.tips!==null){
        if(self.options.tips.charAt(0) == '#' || self.options.tips.charAt(0) == '.'){
          self.borderObj.find(".tccTipsContent").html(TCC.find(self.options.tips).html());
        }else{
          self.borderObj.find(".tccTipsContent").html(self.options.tips);
        }
      }else{
        console.log("tips is null");
      }

      if (self.options.width) {
        self.borderObj.width(self.options.width);
      }
      self._setPosition();
    },

    _setPosition : function() {
      // set position
      var self = this;
      var referOfferSet = self.offset();
      self.borderObj.offset({
        'top' : referOfferSet.top + self.height() - 1 + 8,
        'left' : referOfferSet.left
      });
      self.targetTop = referOfferSet.top + self.height() - 1;
      self.targetLeft = referOfferSet.left;
      TCC.find(".tccTipsArrow").css({
        'top' : -15,
        'left' : 0
      });
    },

    _isInElementRange : function(eX, eY, el, aa) {
      var elOffset = el.offset();
      if (el.css('display') != 'none' && (eY > elOffset.top) && eY < (elOffset.top + el.height()) && (eX > elOffset.left) && (eX < elOffset.left + el.width())) {
        return true;
      } else {
        return false;
      }
    },

    show : function(event) {
      event.stopPropagation();
      var self = this;
      self.isActive = true;
      setTimeout(function() {
        if (self.isActive == true) {
          self._setPosition();
          self.borderObj.css({
            display : 'block',
            opacity : 0,
            left : self.targetLeft,
            top : self.targetTop + 8
          });
          self.borderObj.animate({
            opacity : 100,
            left : self.targetLeft,
            top : self.targetTop
          }, 200, function(){
            // if the tips still does not exist, please change this condition, add the range check--_isInElementRange
            if(self.isActive == false){
              self.borderObj.css({
                display : 'none'
              });
            }
          });
        }
      }, 500);
    },
    
    forceHide: function(){
      this.borderObj.css({
        display : 'none'
      });
    },
    
    close : function(event) {
      event.stopPropagation();
      var self = this;
      if (self.isActive == true) {
        var eY = event.pageY;
        var eX = event.pageX;
        var c1 = self._isInElementRange(eX, eY, self, 1);
        var c2 = self._isInElementRange(eX, eY, self.borderObj, 2);
        if (c1 === true || c2 === true) {
          // do nothing
        } else {
          if (self.isActive == true) {
            self.isActive = false;
            self.borderObj.animate({
              opacity : 0,
              left : self.targetLeft,
              top : self.targetTop + 8
            }, 200, function() {
              self.borderObj.css({
                display : 'none'
              });
            });
          }
        }
      }
    },

    destroy : function() {
      TCC.log("des");
      this.unbind("mouseenter");
      this.unbind("mouseout");
      this.borderObj.unbind("mouseout");
      this.borderObj.remove();
      // Use the destroy method to reverse everything your plugin has
      // applied
      TCC.superWidget.prototype.destroy.call(this);
    }
  });
})(TCC);
