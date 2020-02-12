(function(TCC) {

  if (typeof TCC.Timer !== "undefined") {
    return false;
  }
  // All of the time unit is based on second
  var defOptions = {
    usedTime : 0,
    totalTime : 10,
    interval : 1,
    timeoutFN : null,
    timerID : "#TCC_Timer",
    // show expired info in last 3 minutes
    expired : {
      count : 3,
      interval : 60
    },
    listeners : {
      timeout : null,
      showExpiredInfo : null
    },
    i18n : {
      timer : "Time",
      hour : "hr ",
      minute : "min ",
      second : "sec "
    }
  };

  function Timer(options) {
    this.options = TCC.extend(defOptions, options);
    this.listeners = this.options.listeners;
    this.usedTime = this.options.usedTime;
    this.totalTime = this.options.totalTime;
    this._printTimer();
    this.timerInterval = null;
    this.isActive = false;
  }

  Timer.prototype = {

    _printTimer : function() {
      var time = this._getTime();
      if (!this.isActive) {
        this.timerEL = TCC.find(this.options.timerID);
        if(time.hour > 0){
          this.timerEL.appendChild("<span>" + this.options.i18n.timer + " : " + "<span id='tccTimerHour' style='width:20px;display:inline-block;text-align:center;'>" + time.hour + "</span>" + this.options.i18n.hour + "</span");          
        }else{
          this.timerEL.appendChild("<span>" + this.options.i18n.timer + " : " + "</span>");
        }
        this.timerEL.appendChild("<span><span id='tccTimerMin' style='width:20px;display:inline-block;text-align:center;'>" + +time.min + "</span>" + this.options.i18n.minute + "</span");
        this.timerEL.appendChild("<span><span id='tccTimerSec' style='width:20px;display:inline-block;text-align:center;'>" + +time.sec + "</span>" + this.options.i18n.second + "</span");
      } else {
        if(time.hour > 0){
          TCC.find("#tccTimerHour").html(time.hour);
        }
        TCC.find("#tccTimerMin").html(time.min);
        TCC.find("#tccTimerSec").html(time.sec);
      }
    },

    /**
     * return {hour:1,min:1,sec:10}
     */
    _getTime : function() {
      var totalTime = this.totalTime;
      var usedTime = this.usedTime || 0;
      var leftTime = totalTime - usedTime;
      if (leftTime <= 0) {
        return {
          hour : 0,
          min : 0,
          sec : 0
        };
      } else {
        var hour = Math.floor(leftTime / 3600);
        var min = Math.floor((leftTime - hour * 3600) / 60);
        var sec = (leftTime - hour * 3600) % 60;
        return {
          hour : hour,
          min : min,
          sec : sec
        };
      }
    },

    start : function() {
      var that = this, expiredCount = 0;
      that.isActive = true;
      this.timerInterval = setInterval(function() {
        that.usedTime++;
        that._printTimer();
        var leftTime = that.totalTime - that.usedTime;
        if (leftTime !== 0 && leftTime == (that.options.expired.count - expiredCount) * that.options.expired.interval) {
          expiredCount++;
          that.listeners.showExpiredInfo && that.listeners.showExpiredInfo(leftTime);
        } else if (leftTime === 0) {
          that.timeout();
        }
      }, this.options.interval * 1000);
    },

    pause : function() {
      if (this.timerInterval) {
        clearInterval(this.timerInterval);
        this.timerInterval = null;
        this.isActive = false;
      }
    },

    stop : function() {
      if (this.timerInterval) {
        clearInterval(this.timerInterval);
        this.timerInterval = null;
        this.isActive = false;
        this.usedTime = 0;
      }
    },

    resume : function() {
      this.start();
    },

    timeout : function() {
      this.pause();
      if (this.listeners.timeout) {
        this.listeners.timeout();
      }
    },

    getUsedTime : function() {
      return this.usedTime;
    }
  }

  TCC.Timer = Timer;
})(window.TCC);