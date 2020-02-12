(function(TCC) {
  var itemMargin = 10;

  TCC.widget("slider", {
    // These options will be used as defaults
    options : {
      items : [],
      itemWrap : null, // replace $(item)
      itemSize : {
        width : null,
        height : null
      },
      sliderSize : {
        width : null,  // sliderSize must be the integral multiple of itemSize width + itemMargin, e.g. item size is 100, item margin is 10, the slider size is (100+10*2) * 8 = 960
        height : null
      },
      loadData: false // need load data from  
    },
    _init : function() {
      var self = this;
      self.itemWidth, self.itemCount, self.currentItem = 0;
      var htmlArray = [];

      htmlArray.push("<ul class='tccBaseSliderItemWrap'></ul>");
      self.find(".tccBaseSliderWrap").html(htmlArray.join(""));
      self._refreshData();
    },
    _create : function() {
      this._createHtml();
      this._bindEvent();
    },

    _createHtml : function() {
      var tag = "";
      if(this.attr("id")){
        tag = "#" + this.attr("id") + " ";
      }
       
      var node = TCC.find("<style type='text/css'>" + tag + " .tccBaseSliderItem{ width:" + this.options.itemSize.width + "px; height:" + this.options.itemSize.height + "px;} " + tag + " .tccBaseSliderWrap{ width:" + this.options.sliderSize.width + "px; height:" + this.options.sliderSize.height
          + "px;}</style>");
      var head = TCC.find("head");
      head.appendChild(node);
    },

    _refreshData : function() {
      var self = this;
      var items = self.options.items;
      var len = items.length;
      var itemWrap = self.options.itemWrap;
      var htmlArray = [];
      for ( var i = 0; i < len; i++) {
        var temp = itemWrap == null ? items[i] : itemWrap.replace('$(item)', items[i]);
        htmlArray.push("<li class='tccBaseSliderItem' index='" + i + "'>" + temp + "</li>");
      }

      var obj1 = self.find(".tccBaseSliderItemWrap");
      obj1.html(htmlArray.join(""));
      self.itemWidth = obj1.find(".tccBaseSliderItem[index = 0]").width() + 2 * itemMargin;
      self.itemCount = parseInt(self.find(".tccBaseSliderWrap").width() / self.itemWidth);
      self.find(".tccBaseSliderItemWrap").css({
        width : self.itemWidth * len,
        height : 200
      });
    },
    // replace the data with the new items
    // direction, next, prev
    replaceData: function(newItems, direction){
      var self = this;
      var items = self.options.items;
      if(direction == "prev"){
        items = newItems.concat(items);
      }else if(direction == "next"){
        items = items.concat(newItems);
      }
      self.options.items = items;
      var len = newItems.length;
      var itemWrap = self.options.itemWrap;
      var htmlArray = [];
      for ( var i = 0; i < len; i++) {
        var temp = itemWrap == null ? newItems[i] : itemWrap.replace('$(item)', newItems[i]);
        htmlArray.push("<li class='tccBaseSliderItem' index='" + i + "'>" + temp + "</li>");
      }
      var obj1 = self.find(".tccBaseSliderItemWrap");
      var needDeleteItems = obj1.find(".tccBaseSliderItem");
      if(direction == "prev"){
        obj1.prepend(TCC.find(htmlArray.join("")));
        obj1.css("margin-left", -self.itemCount * self.itemWidth);
      }else if(direction == "next"){
        obj1.appendChild(TCC.find(htmlArray.join("")));
      }
      
      this._reCalSliderValue();
      var marginLeftValue = 0;
      if (direction == "prev") {
        marginLeftValue = 0;
      } else if (direction == "next") {
        marginLeftValue = self.itemCount * self.itemWidth;
      }
      obj1.animate({
        "margin-left" : -marginLeftValue
        }, 800, function(){
          self.options.items = newItems;
          needDeleteItems.remove();
          self._reCalSliderValue();
          obj1.css("margin-left", 0);
        }
      );
    },
    
    addItem: function(item){
      var self = this;
      self.options.items.push(item);
      var items = self.options.items; 
      var itemWrap = self.options.itemWrap;
      var temp = itemWrap == null ? item : itemWrap.replace('$(item)', item);
      self._reCalSliderValue();
      self.find(".tccBaseSliderItemWrap").appendChild("<li class='tccBaseSliderItem' index='" + (items.length - 1) + "'>" + temp + "</li>");
      self.goLastPage();
    },
    
    _reCalSliderValue: function(){
      var self = this;
      var obj1 = self.find(".tccBaseSliderItemWrap");
      self.itemWidth = obj1.find(".tccBaseSliderItem[index = 0]").width() + 2 * itemMargin;
      self.itemCount = parseInt(self.find(".tccBaseSliderWrap").width() / self.itemWidth);
      self.find(".tccBaseSliderItemWrap").css({
        width : self.itemWidth * self.options.items.length,
        height : 200
      });
    },

    _bindEvent : function() {
      var self = this;
      var obj1 = self.find(".tccBaseSliderPrev");
      obj1.on("click", function() {
        self.goPrevPage();
      });

      var obj2 = self.find(".tccBaseSliderNext");
      obj2.on("click", function() {
        self.goNextPage();
      });
    },

    goItem : function(index, duration, callbackFn) {
      var self = this;
      if(!duration && duration!==0){
        duration = 800;
      }
      if (index < 0 || index >= self.options.items.length) {

      } else {
        var obj1 = self.find(".tccBaseSliderItemWrap");
        obj1.animate({
          "margin-left" : -index * self.itemWidth
        }, duration, callbackFn);
        self.currentItem = index;
      }
    },
    
    getCurrentPageNum: function(){
      var self = this;
      return parseInt(self.currentItem / self.itemCount);
    },

    goPage : function(pageNum) {
      var self = this;
      self.goItem(parseInt(pageNum * self.itemCount));
    },

    goNextPage : function(e) {
      var self = this;
      self._trigger("beforeNextPage", e, self);
      self.goPage(self.getCurrentPageNum() + 1);
    },

    goPrevPage : function(e) {
      var self = this;
      self._trigger("beforePrevPage", e, self);
      self.goPage(self.getCurrentPageNum() - 1);
    },
    
    goFirstPage: function(e){
      this.goPage(0);
    },
    
    goLastPage: function(e){
      this.goPage(parseInt(this.options.items.length/this.itemCount));
    },

    close : function(callbackFn) {
    },

    destroy : function() {
      TCC.superWidget.prototype.destroy.call(this);
    }
  });
})(TCC);