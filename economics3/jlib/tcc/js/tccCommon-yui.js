if (!window.TCC || typeof (TCC) !== "function") {

  var YUI_config = {
  // filter : 'debug'
  };

  var TCC_Y = new YUI({
  // debug : true,
  // combine : false,
  // filter : 'debug'
  }).use('node', 'event', 'anim', 'io');

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
        TCC_Y.use('node', 'event', function(Y) {
          Y.on("contentready", fn, o);
        });
      },

      create : function(html) {
        return new $tcc(TCC_Y.Node.create(html));
      },

      find : function(selector) {
        return new $tcc(TCC_Y.all(selector));
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
          return TCC_Y.bind(function() {
            fn.apply(context, data);
          }, context);
        } else {
          return TCC_Y.bind(fn, context);
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
        TCC_Y.each(obj, function(idx, o) {
          callback(o, idx);
        })
      },

      trim : function(str) {
        return TCC_Y.Lang.trim(str);
      }
    };

    /**
     * tcc obj constructor, it will hold the original object, which from the thirdpart js lib(such as:jQuery,YUI..)
     */
    var $tcc = function(obj) {
      this.originalObj = obj;
      if (obj._node !== undefined) {
        this.length = 1;
        this.firstItem = this.originalObj;
      } else {
        this.length = obj.size();
        this.firstItem = this.originalObj.item(0);
      }
      return this;
    };

    /**
     * tcc obj prototype functions, we can call by $tcc instance, eg: var ret = TCC.find('div'); ret.methodName();
     */
    tcc.fn = $tcc.prototype = {

      constructor : $tcc,

      find : function(selector) {
        return new $tcc(this.originalObj.all(selector));
      },

      filter : function(selector) {
        return new $tcc(this.originalObj.filter(selector));
      },

      appendChild : function(content) {
        this.originalObj.each(function(o) {
          if (typeof content === "string") {
            o.appendChild(content);
          } else {
            o.appendChild(content.originalObj);
          }
        });
      },

      prepend : function(content) {
        // TODO
      },

      insertAfter : function(target) {
        // TODO
      },

      insertBefore : function(target) {
        // TODO
      },

      val : function(value) {
        if (value === undefined) {
          return this.firstItem.get("value");
        } else {
          this.firstItem.set("value", value);
        }
      },

      html : function(html) {
        if (html !== undefined) {
          this.originalObj.setHTML(html);
        } else {
          return this.originalObj.getHTML()[0];
        }
      },

      attr : function(attr, value) {
        if (value != undefined) {
          this.firstItem.setAttribute(attr, value);
        } else {
          return this.firstItem.getAttribute(attr);
        }
      },

      /** AT HERE* */
      parent : function() {
        return new $tcc(this.firstItem.ancestor());
      },

      ancestor : function(selector) {
        return new $tcc(this.firstItem.ancestor(selector));
      },

      children : function(selector) {
        var list = TCC_Y.all();
        if (this.originalObj != null) {
          this.originalObj.each(function(o1) {
            var sub = o1.get("children");
            if (sub.size() > 0) {
              sub.each(function(o2) {
                list.push(o2);
              });
            }
          });
        }
        return new $tcc(list);
      },

      prev : function(selector) {
        // TODO
      },

      prevAll : function(selector) {
        var that = this;
        var parent = that.firstItem.get('parentNode');
        var index = 0;
        var siblings = parent.all(selector);
        siblings.each(function(el, idx) {
          if (that.firstItem == el) {
            index = idx
            return false;
          }
        });
        return new $tcc(siblings.slice(0, index));
      },

      next : function(selector) {
        // TODO
      },

      nextAll : function(selector) {
        var that = this;
        var parent = that.firstItem.get('parentNode');
        var index = 0;
        var siblings = parent.all(selector);
        siblings.each(function(el, idx) {
          if (that.firstItem == el) {
            index = idx
            return false;
          }
        });
        return new $tcc(siblings.slice(index + 1, siblings.length));
      },

      each : function(fn) {
        if (this.length > 1) {
          this.originalObj.each(function(obj, idx) {
            fn(obj._node, idx);
          });
        } else {
          fn(this.originalObj.item(0)._node, 0);
        }
      },

      size : function() {
        return this.length;
      },

      clear : function() {
        this.originalObj.empty(true);
      },

      remove : function() {
        this.originalObj.remove(true);
      },

      extend : function(target) {
        var destination = this;
        for ( var property in target) {
          destination[property] = target[property];
        }
      },

      get : function(index) {
        // TODO
      }
    };

    /**
     * define js animate related functions
     */
    tcc.extend(tcc.fn, {

      show : function() {
        if (this.length > 1) {
          this.originalObj.each(function(el, index) {
            var tagName = el.get("tagName")[0];
            el.setStyle('display', defDisplay(tagName));
          });
        } else {
          var tagName = this.originalObj.get("tagName")[0];
          this.originalObj.setStyle('display', defDisplay(tagName));
        }
      },

      hide : function() {
        if (this.length > 1) {
          this.originalObj.each(function(el, index) {
            el.setStyle('display', 'none');
          });
        } else {
          this.originalObj.setStyle('display', 'none');
        }
      },

      fadeIn : function(speed, fn) {
        // TODO
      },

      fadeOut : function(speed, fn) {
        // TODO
      },

      fadeTo : function(speed, fn) {
        // TODO
      },

      animate : function(properties, duration, fn, easing) {
        var self = this;
        var animatingParam = {
          xy : []
        };
        for ( var k in properties) {
          if (k === "left") {
            animatingParam.xy[0] = properties[k];
          } else if (k === "top") {
            animatingParam.xy[1] = properties[k];
          } else {
            animatingParam[k] = properties[k];
          }
        }
        console.log(animatingParam);
        var anim = new TCC_Y.Anim({
          node : self.firstItem,
          to : animatingParam,
          duration : duration / 1000
        });
        anim.on('end', fn);
        anim.run();
      }
    });

    /**
     * define css related functions
     */
    tcc.extend(tcc.fn, {

      offset : function() {
        if (this.firstItem != null) {
          var length = arguments.length;
          if (length > 0) {
            this.firstItem.setXY([ arguments[0].left, arguments[0].top ]);
          } else {
            var xy = this.firstItem.getXY();
            return {
              left : xy[0],
              top : xy[1]
            };
          }
        }
        return null;
      },

      position : function() {
        // TODO
      },

      height : function() {
        if (this.firstItem != null) {
          var el = this.firstItem;
          var _getNum = function(el2, param) {
            return parseInt(el2.getComputedStyle(param));
          }
          return _getNum(el, 'height') + _getNum(el, 'paddingTop') + _getNum(el, "paddingBottom") + _getNum(el, "borderTopWidth") + _getNum(el, "borderBottomWidth");
        }
        return null;
      },

      width : function() {
        if (this.firstItem != null) {
          var el = this.firstItem;
          var _getNum = function(el2, param) {
            return parseInt(el2.getComputedStyle(param));
          }
          return _getNum(el, 'width') + _getNum(el, 'paddingLeft') + _getNum(el, "paddingRight") + +_getNum(el, "borderLeftWidth") + _getNum(el, "borderRightWidth");
        }
        return null;
      },

      css : function() {
        if (this.originalObj != null) {
          var length = arguments.length;
          if (length > 1) {
            this.originalObj.setStyle(arguments[0], arguments[1]);
          } else if (typeof (arguments[0]) == 'object') {
            var ps = arguments[0];
            for ( var property in ps) {
              this.originalObj.setStyle(property, ps[property]);
            }
          } else if (typeof (arguments[0]) == 'string') {
            return this.firstItem.getStyle(arguments[0]);
          }
        }
      },

      hasClass : function(name) {
        this.originalObj.hasClass(name);
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
        // TODO
      },

      isVisible : function() {
        // TODO
      }
    });

    /**
     * define js event related functions
     */
    tcc.extend(tcc.fn, {

      _proxyFN : function(fn) {
        return function(evt) {
          var newEvt = {
            e : evt,
            pageY : evt.pageY,
            pageX : evt.pageX,
            offsetX : evt.offsetX,
            offsetY : evt.offsetY,
            currentTarget : new $tcc(evt.currentTarget),
            data : evt.data,
            preventDefault : evt.preventDefault,
            stopPropagation : evt.stopPropagation
          };
          fn(newEvt);
        }
      },

      delegate : function(event, selector, fn) {
        var that = this;
        this.originalObj.each(function(el) {
          el.delegate(event, that._proxyFN(fn), selector);
        });
      },

      on : function(event, fn) {
        var that = this;
        var synth = TCC_Y.Node.DOM_EVENTS[event];
        if (TCC_Y.Lang.isObject(synth) || synth == 1) {
          if (event.indexOf('mouse') >= 0) {
            YUI().use('event-mouseenter', function(Y) {
              that.originalObj.on(event, that._proxyFN(fn));
            });
          } else {
            that.originalObj.on(event, that._proxyFN(fn));
          }

        } else {
          that.originalObj.each(function(el) {
            el.subscribe(event, that._proxyFN(fn));
          });
        }
      },

      hover : function(overFn, outFn) {
        this.originalObj.on("hover", overFn, outFn);
      },

      off : function(event) {
        this.originalObj.detach(event);
      },

      trigger : function(event, data) {
        this.originalObj.each(function(o) {
          o.fire(event, data);
        });
      },

      unbind : function() {
        // TODO
      }
    });

    /**
     * define ajax related functions
     */
    tcc.extend(tcc, {

      load : function(selector, url, callback) {
        TCC_Y.io(url, {
          on : {
            complete : function(id, e) {
              TCC.find(selector).html(e.responseText);
              callback();
            }
          }
        });
      },

      post : function(url, form, async, callback, dataType) {
        YUI().use("io-base", function(Y) {
          var cfg = {
            method : 'POST',
            form : {
              id : form
            },
            on : {
              success : callback
            },
            type : dataType,
            async : async
          };
          var request = Y.io(url, cfg);
        });
      },

      get : function(url, options, callback, async, dataType) {
        // TODO
      }
    });
    return tcc;
  })(window);

  var _elemdisplay = {};

  // Try to restore the default display value of an element
  function defDisplay(nodeName) {
    if (!_elemdisplay[nodeName]) {
      var body = document.body;
      TCC.find(body).appendChild(TCC.create("<" + nodeName + " id='tmp_apd'>"));
      var elem = TCC.find("#tmp_apd");
      var display = elem.css("display");
      elem.remove();
      // Store the correct default display
      _elemdisplay[nodeName] = display;
    }
    return _elemdisplay[nodeName];
  }

  window.TCC = TCC;
};