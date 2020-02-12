(function(TCC) {
  var POPIN_INSTANCES = {};
  var BACKDROP_TPL = '<div class="tccWidget-popin-backdrop"/>';
  var LOADING_TPL = '<div class="tccWidget-popin-body-loading"><span class="tccWidget-popin-body-loadingIcon"></span>${loadingLabel}</div>';
  var SAVING_TPL = '<div class="tccWidget-popin-body-saving"><span class="tccWidget-popin-body-loadingIcon"></span><span class="savingLabel">${savingLabel}</span></div>';
  function Popin(popinEL, options) {
    this.options = options;
    this.popinEL = popinEL;
    this.selector = popinEL.selector;
    // bind close event.
    this.popinEL.find('[data-dismiss="popin"]').on("click", TCC.proxy(this.hide, this));
    this.bodyEL = this.popinEL.find('.tccWidget-popin-body');
    // load content from remote url.
    if (this.options.remote) {
      if (this.bodyEL) {
        this.bodyEL.clear();
      }
      // add loading.
      this.bodyEL.appendChild(LOADING_TPL.replace("${loadingLabel}", this.options.i18n.loading));
      // append remote content to 'tccWidget-popin-body' and remove loading.
      TCC.load(this.bodyEL.get(0), this.options.remote, this.options.onShow);
    }
    for(var i=0,len = this.options.actions.length;i<len;i++){
      var that = this;
      var actionObj = this.options.actions[i];
      this.popinEL.find(actionObj.selector).on("click", function(){
        actionObj.fn.call(this, actionObj.data);
        if(actionObj.isClose){
          that.hide();
        }
      });
    }
  }

  Popin.prototype = {

    show : function() {
      if (this.isShown) {
        return;
      }
      if (this.options.backdrop) {
        this.backdropEL = TCC.find(BACKDROP_TPL);
        document.body.appendChild(this.backdropEL.get(0));
      }
      if (this.options.parentIsPopin) {
        document.body.appendChild(this.popinEL.get(0));
      }
      this.popinEL.show();
      if (!this.options.remote) {
        this.options.onShow && this.options.onShow();
      }
      this.isShown = true;
    },

    showProgress : function() {
      this.hasProgress = true;
      this.bodyEL.clear();
      // add saving progress
      this.bodyEL.appendChild(SAVING_TPL.replace("${savingLabel}", this.options.i18n.saving));
    },

    hide : function() {
      var that = this;
      var closeDialog = function() {
        if (that.options.reload) {
          delete POPIN_INSTANCES[that.selector];
        }
        that.popinEL.hide();
        if (that.options.backdrop) {
          that.backdropEL.remove();
        }
        that.options.onClose && that.options.onClose();
        that.isShown = false;
        that.hasProgress = false;
      };
      if (that.hasProgress) {
        this.bodyEL.find(".savingLabel").get(0).innerHTML = that.options.i18n.saveOk;
        setTimeout(closeDialog, 200);
      } else {
        closeDialog();
      }
      return false;
    }
  }

  TCC.fn.popin = function(option) {
    if (this.length == 0) {
      throw new Error('Popin target element is not exist.');
    }

    var popin = POPIN_INSTANCES[this.selector], options = TCC.extend({}, TCC.fn.popin.defaults, typeof option == 'object' && option);
    if (!popin)
      POPIN_INSTANCES[this.selector] = (popin = new Popin(this, options))
    if (typeof option == 'string')
      popin[option]()
    else
      popin.show()
    return this;
  }

  TCC.fn.popin.defaults = {
    backdrop : true,
    reload : false,
    hasProgress : false,
    parentIsPopin : false,
    actions : [],
    onShow : null,
    onClose : null,
    i18n : {
      loading : TCC.i18n.widget.popin.loading,
      saving : TCC.i18n.widget.popin.saving,
      saveOk : TCC.i18n.widget.popin.saveOk
    }
  }

  TCC.fn.popin.Constructor = Popin;
})(window.TCC);