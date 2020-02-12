(function(TCC, undefined) {
  if (!TCC.widgetUuid) {
    TCC.widgetUuid = 0;
  }
  TCC.widgets = {};
  // TCC.widget = function(name, options1) {
  // TCC.fn[name] = function(options){
  // this.extend(new TCC.superWidget());
  // this.extend(options1);
  // // TCC.superWidget.call(this);
  // this._createWidget(options);
  // }
  // };
  TCC.widget = function(name, options1) {
    TCC.fn[name] = function(options) {
      if (!this.attr("widgetId") && typeof (options) !== 'string') {
        this.attr("widgetId", "w" + name + (++TCC.widgetUuid));
        TCC.widgets[this.attr("widgetId")] = this;
        var superObj = new TCC.superWidget();
        this.options = null;
        this.extend(superObj);
        this.extend(options1);
        this.options = {};
        for ( var p in options1) {
          if (p !== 'options' && p.indexOf("_") < 0) {
            this.options[p] = options1[p];
          }
        }
        for ( var p in options1.options) {
          this.options[p] = options1.options[p];
        }
        for ( var property in options) {
          this.options[property] = options[property];
        }
        this._createWidget(this.options);
      } else {
        var widTemp = TCC.widgets[this.attr("widgetId")];
        if (widTemp !== undefined){
          if(typeof (options) === 'string') {
            if (arguments[1] !== undefined) {
              return widTemp._trigger(arguments[0], null, arguments[1]);
            } else {
              return widTemp._trigger(options);
            }
          }else if(options === null || typeof (options) === 'object'){
            if(options!==null){
              widTemp._setOptions(options);
            }
            widTemp._init();
            widTemp._trigger("init");
          }else if(options === undefined){
            return widTemp;
          }
        }
      }
    }
  };
  
  TCC.getContainWidgets = function(selector){
    return TCC.find(selector).find("*[widgetid!='']");
  };

  TCC.superWidget = function() {

  };

  TCC.superWidget.prototype = {
    widgetName : "widget",
    widgetEventPrefix : "",
    options : {
      disabled : false
    },

    getWId : function() {
      return this.attr("widgetId");
    },

    _createWidget : function(options) {
      this.options = options;
      var self = this;

      this.originalObj.on("remove." + this.widgetName, function() {
        self.destroy();
      });

      this._create();
      this._bind();
      this._trigger("create");
      this._init();
      this._trigger("init");
    },

    _create : function() {

    },

    _bind : function() {

    },

    _init : function() {
    },

    destroy : function() {
      var wId = this.getWId();
      TCC.widgets[wId].attr("widgetId", "");
      TCC.widgets[wId] = null;
    },

    widget : function() {
      return this;
    },

    option : function(key, value) {
      var options = key;

      if (arguments.length === 0) {
        // don't return a reference to the internal hash
        return this.options;
      }

      if (typeof key === "string") {
        if (value === undefined) {
          return this.options[key];
        }
        options = {};
        options[key] = value;
      }

      this._setOptions(options);
      return this;
    },

    _setOptions : function(options) {
      var self = this;
      TCC.each(options, function(key, value) {
        self._setOption(key, value);
      });
      return this;
    },

    _setOption : function(key, value) {
      this.options[key] = value;

      if (key === "disabled") {
        this.widget()[value ? "addClass" : "removeClass"](this.widgetBaseClass + "-disabled" + " " + "ui-state-disabled").attr("aria-disabled", value);
      }
      return this;
    },

    enable : function() {
      return this._setOption("disabled", false);
    },

    disable : function() {
      return this._setOption("disabled", true);
    },

    _trigger : function(type, event, data) {
      var prop, orig, callback = this.options[type];

      if(data === 0){
        data = 0;
      }else{
        data = data || {};
      }
      // event = TCC.Event(event);
      if (!event) {
        event = {};
      }
      event.type = (type === this.widgetEventPrefix ? type : this.widgetEventPrefix + type).toLowerCase();
      // the original event may come from any element
      // so we need to reset the target on the new event
      event.target = this;

      // copy original event properties over to the new event
      orig = event.originalEvent;
      if (orig) {
        for (prop in orig) {
          if (!(prop in event)) {
            event[prop] = orig[prop];
          }
        }
      }
      var ret = null;
      if (typeof (callback) !== "function") {
        if (typeof (this[event.type]) === 'function') {
          ret = this[event.type].call(this, event, data);
        } else {
          this.trigger(event, data);
        }
      }
      return typeof (callback) === "function" && callback.call(this, data);
    }
  };

})(TCC);
