(function(TCC) {

  if (typeof TCC.RequestQueue !== "undefined") {
    return false;
  }

  /**
   * request type. 1:refresh session; 2:save action
   */
  var REQ_TYPE = {
    refreshSession : 1,
    handleAction : 2
  };
  /**
   * request status.
   */
  var REQ_STATUS = {
    ready : 0,
    processing : 1,
    saveOk : 2,
    saveFail : 3
  };
  /**
   * response status
   */
  var RES_STATUS = {
    saveOK : "Y",
    saveFail : "N"
  };
  /**
   * { 'reqId':{ id:1,type:1,status:0,data:questionObj,processAt:0 }, ... }
   */
  var QUEUE = {};

  /**
   * default options
   */
  var defOptions = {
    interval : 2 * 1000,
    timeout : 10 * 1000,
    maxRow : 10,
    listeners : {
      combineRequest : null,
      sendSuccessfully : null,
      getResponseStatus : null,
      getErrorCode : null,
      failedToSend : null,
      isValueChanged : null,
      reportError : null
    },
    requestURL : {
      sessionRefresh : null,
      handleAction : null
    }
  };

  TCC.RequestQueue = {

    init : function(options) {
      var that = this;
      this.options = TCC.extend(defOptions, options);
      this.listeners = this.options.listeners;
      this.lastProcessAt = 0;
      this.isBusy = 0;
      this.timerInterval = setInterval(function() {
        that._checkQueue();
      }, this.options.interval);
    },

    addRequest : function(reqObj) {
      var reqId = reqObj.id;
      var findReq = QUEUE[reqId];
      if (findReq) {
        if (this.listeners.isValueChanged(reqObj, findReq)) {
          reqObj.status = REQ_STATUS.ready;
          reqObj.updateAt = new Date().getTime();
          QUEUE[reqId] = reqObj;
        }
      } else {
        reqObj.status = REQ_STATUS.ready;
        reqObj.updateAt = new Date().getTime();
        QUEUE[reqId] = reqObj;
      }
    },

    clear : function() {
      if (this.timerInterval) {
        clearInterval(this.timerInterval);
        QUEUE = {};
      }
    },

    restart : function() {
      var that = this;
      this.clear();
      this.timerInterval = setInterval(function() {
        that._checkQueue();
      }, this.options.interval);
    },

    _checkQueue : function() {
      var obj, now = new Date().getTime(), row = 0, data = [], result;
      if (this.isBusy && (now - this.lastProcessAt < this.options.timeout)) {
        return;
      }
      for ( var reqId in QUEUE) {
        var reqObj = QUEUE[reqId]
        if (reqObj.status == REQ_STATUS.processing && (now - reqObj.processAt) < this.options.timeout) {
          continue;
        }
        ++row;
        if (row > this.options.maxRow) {
          break;
        }
        reqObj.status = REQ_STATUS.processing;
        reqObj.processAt = new Date().getTime();
        data[row - 1] = reqObj;
      }
      if (data.length > 0) {
        this.isBusy = true;
        result = this.listeners.combineRequest(data);
        this._sendRequest(result);
      }
    },

    _updateObjsInQueue : function(reqIds, status) {
      var ids = reqIds.split(','), reqObj;
      for ( var i = 0; i < ids.length; i++) {
        reqObj = QUEUE[ids[i]];
        if (reqObj) {
          reqObj.status = status;
        }
      }
    },

    _removeObjsFromQueue : function(reqIds) {
      var ids = reqIds.split(','), reqObj;
      for ( var i = 0; i < ids.length; i++) {
        reqObj = QUEUE[ids[i]];
        if (reqObj) {
          if (reqObj.updateAt < reqObj.processAt) {
            delete QUEUE[ids[i]];
          } else {
            reqObj.status = REQ_STATUS.ready;
          }
        }
      }
    },

    _getResponseStatus : function(responseText) {
      if (this.listeners.getResponseStatus) {
        return this.listeners.getResponseStatus(responseText);
      } else {
        var resultEl = TCC.create(responseText);
        return resultEl.attr("status") || RES_STATUS.saveOK;
      }
    },

    _getResponseError : function(responseText) {
      if (this.listeners.getResponseError) {
        return this.listeners.getResponseError(responseText);
      } else {
        var resultEl = TCC.create(responseText);
        return resultEl.attr("errId") || 0;
      }
    },

    _sendRequest : function(actionReq) {
      var that = this;
      that.lastProcessAt = new Date().getTime();
      // url, data, async, callback,error, dataType
      TCC.post(actionReq.url, actionReq.data, true, function(responseText) {
        if (that.listeners.sendSuccessfully) {
          that.listeners.sendSuccessfully();
        }
        var status = that._getResponseStatus(responseText);
        if (status === RES_STATUS.saveOK) {
          that._removeObjsFromQueue(actionReq.id);
          that.isBusy = false;
        } else {
          var errId = that._getResponseError(responseText);
          if (!errId) {
            that._updateObjsInQueue(actionReq.id, REQ_STATUS.saveFail);
          } else {
            if (that.listeners.failedToSend) {
              that.listeners.failedToSend(errId);
            }
          }
        }
      }, function() {
        if (that.listeners.reportError) {
          that.listeners.reportError();
        }
      }, 'html');
    }
  };
})(window.TCC);