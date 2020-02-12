if (!window.TCC || typeof (TCC) !== "function") {
  var TCC = (function(window) {

    /**
     * tcc static functions, we can all that by TCC.methodName().
     */
    var tcc = {

      ns : function(str) {
        var arr = str.split("."), o = JTCC;
        for ( var i = (arr[0] == "TCC") ? 1 : 0; i < arr.length; i++) {
          o[arr[i]] = o[arr[i]] || {};
          o = o[arr[i]];
        }
        return o;
      },

      ready : function(o, fn) {

      },

      create : function(html) {

      },

      find : function(selector) {

      },

      log : function(msg) {

      },

      on : function(selector, event, fn) {

      },

      off : function(selector, event) {

      },

      proxy : function(fn, context) {

      },

      extend : function(target) {

      },

      each : function(obj, callback) {

      },

      trim : function(str) {

      },

      addi18n : function(ns, i18n) {
      }
    };

    /**
     * tcc obj constructor, it will hold the orignal object, which from the thirdpart js lib(such as:jQuery,YUI..)
     */
    var $tcc = function(obj) {
      this.originalObj = obj;
      this.length = obj.length;
      if (this.length > 0) {
        this.firstItem = this.originalObj[0];
      }
      return this;
    };

    /**
     * tcc obj prototype functions, we can call by $tcc instance, eg: var ret = TCC.find('div'); ret.methodName();
     */
    tcc.fn = $tcc.prototype = {

      constructor : $tcc,

      find : function(selector) {

      },

      filter : function(selector) {

      },

      appendChild : function(obj) {

      },

      prepend : function(content) {

      },

      insertAfter : function(target) {

      },

      insertBefore : function(target) {

      },

      val : function(value) {

      },

      html : function(html) {

      },

      attr : function(attr, value) {

      },

      parent : function() {

      },

      ancestor : function(selector) {

      },

      children : function(selector) {

      },

      prev : function(selector) {

      },

      prevAll : function(selector) {

      },

      next : function(selector) {

      },

      nextAll : function(selector) {

      },

      each : function(fn) {

      },

      size : function() {

      },

      clear : function() {

      },

      remove : function() {

      },

      extend : function(target) {

      },

      get : function(index) {

      }
    };

    /**
     * define js animate related functions
     */
    tcc.extend(tcc.fn, {

      show : function() {

      },

      hide : function() {

      },

      fadeIn : function(speed, fn) {

      },

      fadeOut : function(speed, fn) {

      },

      fadeTo : function(speed, fn, opacity) {

      },

      animate : function(properties, duration, fn, easing) {

      }
    });

    /**
     * define css related functions
     */
    tcc.extend(tcc.fn, {

      offset : function() {

      },

      position : function() {

      },

      height : function() {

      },

      width : function() {

      },

      css : function() {

      },

      hasClass : function(name) {

      },

      addClass : function(name) {

      },

      removeClass : function(name) {

      },

      replaceClass : function(o, n) {

      },

      toggleClass : function(name) {

      },

      isVisible : function() {

      }

    });

    /**
     * define js event related functions
     */
    tcc.extend(tcc.fn, {

      _proxyFN : function(fn) {
        return function(evt) {
          var target = new $tcc($(evt.currentTarget));
          var newEvt = {
            e : evt,
            originalEvent : evt.originalEvent,
            pageY : evt.pageY,
            pageX : evt.pageX,
            offsetX : evt.offsetX,
            offsetY : evt.offsetY,
            currentTarget : new $tcc($(evt.currentTarget)),
            data : evt.data,
            preventDefault : evt.preventDefault,
            stopPropagation : evt.stopPropagation
          };
          var ret = fn.call(target, newEvt);
          if (ret === false) {
            evt.preventDefault();
            evt.stopPropagation();
          } else {
            evt.result = ret;
          }
        }
      },

      delegate : function(event, selector, fn) {

      },

      on : function(event, fn, data) {

      },

      hover : function(overFn, outFn) {

      },

      off : function(event) {

      },

      trigger : function(event, data) {

      },

      unbind : function() {

      }
    });

    /**
     * define ajax related functions
     */
    tcc.extend(tcc, {

      load : function(selector, url, callback) {

      },

      post : function(url, forms, async, callback, error, dataType) {

      },

      get : function(url, options, callback, error, async, dataType) {

      }
    });
    return tcc;
  })(window);

  window.TCC = TCC;
};
