if (!window.TCC || typeof (TCC) !== "function") {

  var TCC = (function(window) {

    /**
     * tcc static functions, we can all that by TCC.methodName().
     */
    var tcc = {

      ns : function(str) {
        var arr = str.split(".");
        var prefix = arr[0];
        if (prefix == "TCC") {
          o = TCC;
        } else {
          if (window[prefix] === undefined) {
            o = window[prefix] = {};
          } else {
            o = window[prefix];
          }
        }
        for ( var i = 1; i < arr.length; i++) {
          o[arr[i]] = o[arr[i]] || {};
          o = o[arr[i]];
        }
        return o;
      },

      ready : function(o, fn) {
        $(o).ready(fn);
      },

      create : function(html) {
        return new $tcc($(html));
      },

      find : function(selector) {
        return new $tcc($(selector));
      },

      log : function(msg) {
        var caller = arguments.callee.caller;
        console.log("TCC log" + (caller && !document.all ? " " + caller.name + "()" : "") + " > " + msg);
      },

      on : function(selector, event, fn) {
        tcc.find(selector).on(event, fn);
      },

      off : function(selector, event) {
        tcc.find(selector).off(event);
      },

      proxy : function(fn, context) {
        var data = Array.prototype.slice.call(arguments, 2);
        if (data && data.length > 0) {
          return $.proxy(function() {
            fn.apply(context, data);
          }, context);
        } else {
          return $.proxy(fn, context);
        }
      },

      extend : function(target) {
        if (target == undefined)
          target = this;
        if (arguments.length === 1) {
          for ( var key in target)
            this[key] = target[key];
          return this;
        } else {
          var arrayObj = Array.prototype.slice.call(arguments, 1);
          for ( var i = 0, len = arrayObj.length; i < len; i++) {
            for ( var key in arrayObj[i]) {
              target[key] = arrayObj[i][key];
            }
          }
        }
        return target;
      },

      each : function(obj, callback) {
        $.each(obj, function(idx, o) {
          return callback(idx, o);
        });
      },

      trim : function(str) {
        return $.trim(str);
      },

      addi18n : function(ns, i18n) {
        if (ns.indexOf("TCC.i18n") == -1) {
          ns = "TCC.i18n." + ns;
        }
        var i18nNS = TCC.ns(ns);
        TCC.extend(i18nNS, i18n);
      }
    };

    /**
     * tcc obj constructor, it will hold the original object, which from the thirdpart js lib(such as:jQuery,YUI..)
     */
    var $tcc = function(obj) {
      this.originalObj = obj;
      this.length = obj.length;
      this.selector = obj.selector;
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
        return new $tcc(this.originalObj.find(selector));
      },

      filter : function(selector) {
        return new $tcc(this.originalObj.filter(selector));
      },

      appendChild : function(content) {
        if (typeof content === "string") {
          this.originalObj.append(content);
        } else {
          this.originalObj.append(content.originalObj);
        }
      },

      prepend : function(content) {
        if (typeof content === "string") {
          this.originalObj.prepend(content);
        } else {
          this.originalObj.prepend(content.originalObj);
        }
      },

      insertAfter : function(target) {
        this.originalObj.insertAfter(target);
      },

      insertBefore : function(target) {
        this.originalObj.insertBefore(target);
      },

      val : function(value) {
        if (value === undefined) {
          return this.originalObj.val();
        } else {
          this.originalObj.val(value);
        }
      },

      html : function(html) {
        if (html === undefined) {
          return this.originalObj.html();
        } else {
          this.originalObj.html(html);
        }
      },

      attr : function(attr, value) {
        if (value === undefined) {
          return this.originalObj.attr(attr);
        } else {
          return this.originalObj.attr(attr, value);
        }
      },

      parent : function() {
        return new $tcc(this.originalObj.parent());
      },

      ancestor : function(selector) {
        return new $tcc(this.originalObj.closest(selector));
      },

      children : function(selector) {
        return new $tcc(this.originalObj.children(selector));
      },

      prev : function(selector) {
        return new $tcc(this.originalObj.prev(selector));
      },

      prevAll : function(selector) {
        return new $tcc(this.originalObj.prevAll(selector));
      },

      next : function(selector) {
        return new $tcc(this.originalObj.next(selector));
      },

      nextAll : function(selector) {
        return new $tcc(this.originalObj.nextAll(selector));
      },

      each : function(fn) {
        this.originalObj.each(function(idx, obj) {
          fn.call(TCC.find(obj), obj, idx);
        });
      },

      size : function() {
        return this.length;
      },

      clear : function() {
        this.originalObj.empty();
      },

      remove : function() {
        this.originalObj.remove();
      },

      extend : function(target) {
        var destination = this;
        for ( var property in target) {
          destination[property] = target[property];
        }
      },

      get : function(index) {
        return this.originalObj[index];
      }
    };

    /**
     * define js animate related functions
     */
    tcc.extend(tcc.fn, {

      show : function() {
        this.originalObj.show();
      },

      hide : function() {
        this.originalObj.hide();
      },

      fadeIn : function(speed, fn) {
        this.originalObj.fadeIn(speed, fn);
      },

      fadeOut : function(speed, fn) {
        this.originalObj.fadeOut(speed, fn);
      },

      fadeTo : function(speed, fn, opacity) {
        this.originalObj.fadeTo(speed, opacity || 0, fn);
      },

      animate : function(properties, duration, fn, easing) {
        $(this.firstItem).animate(properties, parseInt(duration), easing, fn);
      }
    });

    /**
     * define css related functions
     */
    tcc.extend(tcc.fn, {

      offset : function() {
        var length = arguments.length;
        if (length > 0) {
          var param = arguments[0];
          param = {
            top : parseFloat(param["top"]),
            left : parseFloat(param["left"])
          }
          $(this.firstItem).offset(param);
          return null;
        } else {
          var xy = $(this.firstItem).offset();
          return {
            left : xy.left,
            top : xy.top
          };
        }
      },

      position : function() {
        return this.originalObj.position();
      },

      height : function() {
        var value = arguments[0];
        if (value === undefined) {
          return this.originalObj.height();
        } else {
          return this.originalObj.height(value);
        }
      },

      width : function() {
        return this.originalObj.width();
      },

      css : function() {
        var attribute = arguments[0], value = arguments[1];
        if (value === undefined) {
          return this.originalObj.css(attribute);
        } else {
          this.originalObj.css(attribute, value);
        }
      },

      hasClass : function(name) {
        return this.originalObj.hasClass(name);
      },

      addClass : function(name) {
        this.originalObj.addClass(name);
      },

      removeClass : function(name) {
        this.originalObj.removeClass(name);
      },

      replaceClass : function(o, n) {
        this.originalObj.removeClass(o).addClass(n);
      },

      toggleClass : function(name) {
        this.originalObj.toggleClass(name);
      },

      isVisible : function() {
        return this.originalObj.is(":visible");
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
        this.originalObj.delegate(selector, event, this._proxyFN(fn));
      },

      on : function(event, fn, data) {
        this.originalObj.on(event, data, this._proxyFN(fn));
      },

      hover : function(overFn, outFn) {
        this.originalObj.hover(overFn, outFn);
      },

      off : function(event) {
        this.originalObj.off(event);
      },

      trigger : function(event, data) {
        this.originalObj.trigger(event, data);
      },

      unbind : function(event, fn) {
        if (event === undefined) {
          this.originalObj.unbind();
        } else if (event !== undefined && fn === undefined) {
          this.originalObj.unbind(event);
        } else if (event !== undefined && fn !== undefined) {
          this.originalObj.unbind(event, fn);
        }
      }
    });

    /**
     * define ajax related functions
     */
    tcc.extend(tcc, {

      load : function(selector, url, callback) {
        $(selector).load(url, callback);
      },

      post : function(url, forms, async, callback, error, dataType) {
        var params = [];
        if ($.isArray(forms)) {
          TCC.each(forms, function(idx, form) {
            if (form) {
              params.push($(form).serialize());
            }
          });
          params = params.join("&");
        } else {
          if (typeof forms === "string") {
            params = $(forms).serialize();
          } else {
            params = forms;
          }
        }
        $.ajax({
          type : "POST",
          url : url,
          async : async === false ? false : true,
          data : params,
          success : callback,
          error : error,
          dataType : dataType || "html"
        });
      },

      get : function(url, data, callback, error, async, dataType) {
        $.ajax({
          type : "GET",
          url : url,
          data : data,
          success : callback,
          error : error,
          async : async === false ? false : true,
          dataType : dataType || "html"
        });
      }
    });
    return tcc;
  })(window);

  window.TCC = TCC;
};
