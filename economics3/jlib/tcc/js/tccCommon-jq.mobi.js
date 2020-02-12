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
        if (typeof o === "function") {
          $(document).ready(o);
        } else {
          $(document).ready(fn);
        }
      },

      create : function(html) {
        return new $tcc($(html));
      },

      find : function(selector) {
        return new $tcc($(selector));
      },

      log : function(msg) {
        var caller = arguments.callee.caller;
        console.log("TCC log" + (caller ? " " + caller.name + "()" : "") + " > " + msg);
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
          Array.prototype.slice.call(arguments, 1).forEach(function(source) {
            for ( var key in source)
              target[key] = source[key];
          });
        }
        return target;
      },

      each : function(obj, callback) {
        $.each(obj, function(idx, o) {
          callback(idx, o);
        });
      },

      trim : function(str) {
        return str.trim();
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
        this.insertBefore(target, true);
      },

      insertBefore : function(target) {
        this.originalObj.insertBefore(target);
      },

      val : function(value) {
        // handle select element
        var options = this.originalObj.find("option");
        if (options && options.length > 0) {
          if (value === undefined) {
            var value = "";
            options.each(function(idx, option) {
              if (option.selected === true) {
                value = option.value;
                return false;
              }
            })
            return value;
          } else {
            options.each(function(idx, option) {
              if (option.value == value) {
                option.selected = true;
                return false;
              }
            });
          }
        } else {
          if (value === undefined) {
            return this.originalObj.val();
          } else {
            this.originalObj.val(value);
          }
        }
      },

      html : function(html) {
        if (html === undefined) {
          return this.originalObj.html();
        } else {
          // filter script content when set html content.
          var content = html;
          if (typeof html === "string") {
            content = html.replace(_rscript, "");
          }
          this.originalObj.html(content, true);
          if (content == html) {
            return;
          }
          var data = $(html);
          var body = $("body");
          var jsSeq = 0;
          TCC._clearAjaxJsNode();
          // append the script tags which in the ajax response content to the end of body.
          data.each(function(idx, el) {
            var nodeName = (el.nodeName && el.nodeName.toLowerCase()) || "";
            if (nodeName === "script") {
              var script = document.createElement("script");
              script.type = "text/javascript";
              script.id = "ajax_js_" + jsSeq++;
              if (el.src) {
                script.src = el.src;
              } else {
                script.textContent = (el.text || el.textContent || el.innerHTML || "").replace(_rcleanScript, "");
              }
              body.append(script);
            }
          });
        }
      },

      attr : function(attr, value) {
        if (value === undefined) {
          return this.originalObj.attr(attr);
        } else {
          if (attr == "disabled" && value == false) {
            this.originalObj.removeAttr(attr);
          } else {
            this.originalObj.attr(attr, value);
          }
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
        var p = this.parent();
        var that = this;
        var index = -1;
        var siblings = p.children(selector);
        siblings.each(function(el, idx) {
          if (that.firstItem == el) {
            index = idx;
            return false;
          }
        });
        if (index <= 0) {
          return null;
        } else {
          return new $tcc($(siblings.originalObj[index - 1]));
        }
      },

      prevAll : function(selector) {
        var p = this.parent();
        var that = this;
        var index = 0;
        var siblings = p.children(selector);
        siblings.each(function(el, idx) {
          if (that.firstItem == el) {
            index = idx;
            return false;
          }
        });
        return TCC.create(siblings.originalObj.slice(0, index));
      },

      next : function(selector) {
        var p = this.parent();
        var that = this;
        var index = -1;
        var siblings = p.children(selector);
        siblings.each(function(el, idx) {
          if (that.firstItem == el) {
            index = idx;
            return false;
          }
        });
        if (index < 0 || index == siblings.length - 1) {
          return null;
        } else {
          return new $tcc($(siblings.originalObj[index + 1]));
        }
      },

      nextAll : function(selector) {
        var p = this.parent();
        var that = this;
        var index = 0;
        var siblings = p.children(selector);
        siblings.each(function(el, idx) {
          if (that.firstItem == el) {
            index = idx
            return false;
          }
        });
        return TCC.create(siblings.originalObj.slice(index + 1, siblings.length));
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

      remove : function(selector) {
        this.originalObj.remove(selector);
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
        this.originalObj.css("opacity", 1);
      },

      hide : function() {
        this.originalObj.hide();
      },

      fadeIn : function(speed, fn) {
        if ($.isFunction(speed)) {
          fn = speed;
          speed = 400;
        }
        var target = this.originalObj;
        var opacity = parseFloat(target.css("opacity")) || 1;
        if (opacity == 1) {
          target.css("opacity", 0);
        }
        target.show();
        var self = this;
        target.animate({
          "opacity" : 1
        }, speed, function() {
          fn && fn.call(self);
        });
      },

      fadeOut : function(speed, fn) {
        if ($.isFunction(speed)) {
          fn = speed;
          speed = 400;
        }
        var target = this.originalObj;
        var self = this;
        target.animate({
          "opacity" : 0
        }, speed, function() {
          target.hide();
          fn && fn.call(self);
        });
      },

      fadeTo : function(speed, fn, opacity) {
        if ($.isFunction(speed)) {
          fn = speed;
          speed = 400;
        }
        var target = this.originalObj;
        var self = this;
        target.animate({
          "opacity" : opacity || 0
        }, speed, function() {
          fn && fn.call(self);
        });
      },

      animate : function(properties, duration, fn, easing) {
        if ($.isObject(properties)) {
          for ( var key in properties) {
            properties[key] = TCC._maybeAddPx(key, properties[key]);
          }
        }
        $(this.firstItem).animate(properties, duration, fn, easing);
      }
    });

    /**
     * define css related functions
     */
    tcc.extend(tcc.fn, {

      offset : function(options) {
        if (arguments.length == 0) {
          return this.originalObj.offset();
        } else {
          var position = this.originalObj.css("position");
          if (position === "static") {
            this.firstItem.style.position = "relative";
          }

          var curOffset = this.originalObj.offset();
          var curCSSTop = this.originalObj.css("top");
          var curCSSLeft = this.originalObj.css("left");
          var calculatePosition = (position === "absolute" || position === "fixed") && (curCSSTop == "auto" || curCSSLeft == "auto");
          var props = {}, curPosition = {}, curTop, curLeft;

          // need to be able to calculate position if either top or left is auto
          // and position is either absolute or fixed
          if (calculatePosition) {
            curPosition = this.position();
            curTop = curPosition.top;
            curLeft = curPosition.left;
          } else {
            curTop = parseFloat(curCSSTop) || 0;
            curLeft = parseFloat(curCSSLeft) || 0;
          }

          if (options.top != null) {
            props.top = (parseFloat(options.top) - curOffset.top) + curTop + "px";
          }
          if (options.left != null) {
            props.left = (parseFloat(options.left) - curOffset.left) + curLeft + "px";
          }

          this.originalObj.css(props);
        }
      },

      position : function() {
        if (!this.firstItem) {
          return null;
        }

        // Get *real* offsetParent
        offsetParent = $(TCC._getOffsetParent()[0][0]),

        // Get correct offsets
        offset = this.offset(), parentOffset = _rroot.test(offsetParent[0].nodeName) ? {
          top : 0,
          left : 0
        } : offsetParent.offset();

        // Subtract element margins
        // note: when an element has margin: auto the offsetLeft and marginLeft
        // are the same in Safari causing offset.left to incorrectly be 0
        offset.top -= parseFloat(this.originalObj.css("marginTop")) || 0;
        offset.left -= parseFloat(this.originalObj.css("marginLeft")) || 0;

        // Add offsetParent borders
        parentOffset.top += parseFloat($(offsetParent[0]).css("borderTopWidth")) || 0;
        parentOffset.left += parseFloat($(offsetParent[0]).css("borderLeftWidth")) || 0;

        // Subtract the two offsets
        return {
          top : offset.top - parentOffset.top,
          left : offset.left - parentOffset.left
        };
      },

      height : function() {
        if (this.length == 0) {
          return null;
        } else {
          var styles = window.getComputedStyle(this.firstItem);
          return parseFloat(styles.height);
        }
      },

      width : function() {
        if (this.length == 0) {
          return null;
        } else {
          var styles = window.getComputedStyle(this.firstItem);
          return parseFloat(styles.width);
        }
      },

      css : function() {
        var attribute = arguments[0], value = arguments[1];
        if ($.isObject(attribute)) {
          for ( var key in attribute) {
            attribute[key] = TCC._maybeAddPx(key, attribute[key]);
          }
        }
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
        this.originalObj.replaceClass(o, n);
      },

      toggleClass : function(name) {
        if (this.originalObj.hasClass(name)) {
          this.originalObj.removeClass(name);
        } else {
          this.originalObj.addClass(name);
        }
      },

      isVisible : function() {
        var display = this.originalObj.css("display");
        if (display === "none") {
          return false;
        } else {
          return true;
        }
      }
    });

    /**
     * define js event related functions
     */
    tcc.extend(tcc.fn, {

      _proxyFN : function(fn, data) {
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
        this.originalObj.on(event, this._proxyFN(fn, data));
      },

      hover : function(overFn, outFn) {
        var that = this;
        var target = this.firstItem;
        if (!target) {
          return false;
        }
        target.addEventListener("mouseover", function(e) {
          var t = e.relatedTarget;
          if (!t || (!(t.compareDocumentPosition(this) & 8) && t !== this)) {
            overFn.call(that, e);
          }
        }, false);
        target.addEventListener("mouseout", function(e) {
          var t = e.relatedTarget;
          if (!t || (!(t.compareDocumentPosition(this) & 8) && t !== this)) {
            outFn.call(that, e);
          }
        }, false);
      },

      off : function(event) {
        this.originalObj.off(event);
      },

      trigger : function(event, data) {
        if (typeof event === "string") {
          this.originalObj.trigger(event, data);
        } else {
          this.originalObj.trigger(event.type, data);
        }
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
        var tmp = this;
        $.get(url, function(result) {
          var target = TCC.find(selector);
          target.html(result);
          callback && callback.call(tmp);
        });
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

      get : function(url, data, callback, async, error, dataType) {
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

    /** other utility funcitons * */
    tcc.extend(tcc, {

      _clearAjaxJsNode : function() {
        var scriptNodes = TCC.find("script");
        scriptNodes.each(function(script) {
          if (script.id.indexOf("ajax_js_") == 0) {
            TCC.find(script).remove();
          }
        });
      },

      _maybeAddPx : function(name, value) {
        return (typeof value == "number" && !_cssNumber[TCC._dasherize(name)]) ? value + "px" : value
      },

      _dasherize : function(str) {
        return str.replace(/::/g, '/').replace(/([A-Z]+)([A-Z][a-z])/g, '$1_$2').replace(/([a-z\d])([A-Z])/g, '$1_$2').replace(/_/g, '-').toLowerCase();
      },

      _getOffsetParent : function() {
        var that = this;
        return this.originalObj.map(function() {
          var offsetParent = this.offsetParent || document.body;
          while (offsetParent && (!_rroot.test(offsetParent.nodeName) && $(offsetParent).css("position") === "static")) {
            offsetParent = offsetParent.offsetParent;
          }
          return offsetParent;
        });
      }
    });

    return tcc;
  })(window);

  var _rroot = /^(?:body|html)$/i;

  var _specailEvt = "mouseenter,mouseleave";

  var _rscript = /<script\b[^<]*(?:(?!<\/script>)<[^<]*)*<\/script>/gi;

  var _rcleanScript = /^\s*<!(?:\[CDATA\[|\-\-)|[\]\-]{2}>\s*$/g;

  var _cssNumber = {
    'column-count' : 1,
    'columns' : 1,
    'font-weight' : 1,
    'line-height' : 1,
    'opacity' : 1,
    'z-index' : 1,
    'zoom' : 1
  };

  window.TCC = TCC;
}
