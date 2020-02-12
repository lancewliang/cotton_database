(function() {

  window.xmppMsgHandler = {

    remoteWBCommands : {},

    remoteUserPosition : {},

    remoteUserColor : {},

    remoteUserLineWidth : {},

    remoteCHATCommands : {},

    init : function() {
      setInterval($.proxy(this.processWBCommands, this), 10);
      setInterval($.proxy(this.processCHATCommands, this), 10);
    },

    handle : function(from, type, requests) {
      var tmp = this;
      switch (type) {
      case Controller.TYPE.WB:
        if (this.remoteWBCommands[from] == undefined) {
          this.remoteWBCommands[from] = [];
        }
        $.each(requests, function() {
          tmp.remoteWBCommands[from].push($(this).get(0));
        });
        break;
      case Controller.TYPE.CHAT:
        if (this.remoteCHATCommands[from] == undefined) {
          this.remoteCHATCommands[from] = [];
        }
        tmp.remoteCHATCommands[from].push(requests);
        break;
      }
    },

    processWBCommands : function() {
      var remoteRequest, commands = this.remoteWBCommands;
      for ( var from in commands) {
        if (commands[from].length == 0) {
          continue;
        }
        remoteRequest = commands[from].shift();
        if (remoteRequest == undefined) {
          continue;
        }
        remoteRequest.isRemote = true;
        remoteRequest.from = from;
        switch (remoteRequest.cmd) {
        case wbRequests.CMD.BEGIN_DRAW:
          this.remoteUserPosition[from] = {
            startX : remoteRequest.startX,
            startY : remoteRequest.startY
          }
          if (this.remoteUserLineWidth[from] === undefined) {
            this.remoteUserLineWidth[from] = wb.options.lineWidth;
          }
          if (this.remoteUserColor[from] === undefined) {
            this.remoteUserColor[from] = wb.options.color;
          }
          wbProcessor.createTmpCanvas(from);
          wbUndoManager.saveState();
          break;
        case wbRequests.CMD.DRAWING:
          var remoteuserPosition = this.remoteUserPosition;
          if (remoteuserPosition[from] === undefined) {
            remoteRequest.startX = remoteRequest.lineToX;
            remoteRequest.startY = remoteRequest.lineToY;
          } else {
            remoteRequest.startX = remoteuserPosition[from].startX;
            remoteRequest.startY = remoteuserPosition[from].startY;
          }
          remoteRequest.color = this.remoteUserColor[from] ? this.remoteUserColor[from] : remoteRequest.color;
          remoteRequest.lineWidth = this.remoteUserLineWidth[from] ? this.remoteUserLineWidth[from] : remoteRequest.lineWidth;
          wbProcessor.drawing(remoteRequest);
          remoteuserPosition[from].startX = remoteRequest.lineToX;
          remoteuserPosition[from].startY = remoteRequest.lineToY;
          break;
        case wbRequests.CMD.SET_COLOR:
          this.remoteUserColor[from] = remoteRequest.color;
          break;
        case wbRequests.CMD.SET_LINE_WIDTH:
          this.remoteUserLineWidth[from] = remoteRequest.lineWidth;
          break;
        case wbRequests.CMD.BEGIN_DRAW_LINE:
          this.remoteUserPosition[from] = {
            startX : remoteRequest.startX,
            startY : remoteRequest.startY
          }
          wbProcessor.createRemoteCanvas(from);
          wbUndoManager.saveState();
          break;
        case wbRequests.CMD.DRAWING_LINE:
          var remoteuserPosition = this.remoteUserPosition;
          if (remoteuserPosition[from] === undefined) {
            remoteRequest.startX = remoteRequest.lineToX;
            remoteRequest.startY = remoteRequest.lineToY;
          } else {
            remoteRequest.startX = remoteuserPosition[from].startX;
            remoteRequest.startY = remoteuserPosition[from].startY;
          }
          remoteRequest.color = this.remoteUserColor[from] ? this.remoteUserColor[from] : remoteRequest.color;
          remoteRequest.lineWidth = this.remoteUserLineWidth[from] ? this.remoteUserLineWidth[from] : remoteRequest.lineWidth;
          wbProcessor.drawLine(remoteRequest);
          break;
        case wbRequests.CMD.BEGIN_DRAW_OVAL:
        case wbRequests.CMD.BEGIN_DRAW_RECT:
          this.remoteUserPosition[from] = {
            startX : remoteRequest.startX,
            startY : remoteRequest.startY,
            isFilled : remoteRequest.isFilled
          }
          if (this.remoteUserLineWidth[from] === undefined) {
            this.remoteUserLineWidth[from] = wb.options.lineWidth;
          }
          if (this.remoteUserColor[from] === undefined) {
            this.remoteUserColor[from] = wb.options.color;
          }
          wbProcessor.createRemoteCanvas(from);
          wbUndoManager.saveState();
          break;
        case wbRequests.CMD.DRAWING_OVAL:
          var remoteuserPosition = this.remoteUserPosition;
          remoteRequest.startX = remoteuserPosition[from].startX;
          remoteRequest.startY = remoteuserPosition[from].startY;
          remoteRequest.isFilled = remoteuserPosition[from].isFilled;
          remoteRequest.color = this.remoteUserColor[from] ? this.remoteUserColor[from] : remoteRequest.color;
          remoteRequest.lineWidth = this.remoteUserLineWidth[from] ? this.remoteUserLineWidth[from] : remoteRequest.lineWidth;
          wbProcessor.drawOval(remoteRequest);
          break;
        case wbRequests.CMD.DRAWING_RECT:
          var remoteuserPosition = this.remoteUserPosition;
          remoteRequest.startX = remoteuserPosition[from].startX;
          remoteRequest.startY = remoteuserPosition[from].startY;
          remoteRequest.isFilled = remoteuserPosition[from].isFilled;
          remoteRequest.color = this.remoteUserColor[from] ? this.remoteUserColor[from] : remoteRequest.color;
          remoteRequest.lineWidth = this.remoteUserLineWidth[from] ? this.remoteUserLineWidth[from] : remoteRequest.lineWidth;
          wbProcessor.drawRectangle(remoteRequest);
          break;
        case wbRequests.CMD.CLEAR:
          wbProcessor.clear(remoteRequest);
          break;
        case wbRequests.CMD.UNDO:
          wbProcessor.undo(remoteRequest);
          break;
        case wbRequests.CMD.REDO:
          wbProcessor.redo(remoteRequest);
          break;
        case wbRequests.CMD.DRAW_IMG:
          wbUndoManager.saveState();
          var imgSrc = remoteRequest.image;
          var image = new Image();
          image.onload = function() {
            remoteRequest.image = image;
            wbProcessor.drawImg(remoteRequest);
          }
          image.src = imgSrc;
          break;
        case wbRequests.CMD.MOVE_IMG:
          wbProcessor.moveImage(remoteRequest);
          break;
        case wbRequests.CMD.DRAW_TEXT:
          wbUndoManager.saveState();
          wbProcessor.drawText(remoteRequest);
          break;
        case wbRequests.CMD.END_DRAW:
          wbProcessor.endDraw(remoteRequest);
          break;
        }
      }
    },

    processCHATCommands : function() {
      var remoteRequest, commands = this.remoteCHATCommands;
      for ( var from in commands) {
        if (commands[from].length == 0) {
          continue;
        }
        remoteRequest = commands[from].shift();
        if (remoteRequest == undefined) {
          continue;
        }
        messageList.handleMessage(from, remoteRequest);
      }
    },

    garbageCollection : function() {
      delete this.remoteWBCommands;
      delete this.remoteCHATCommands;
      delete this.remoteUserPosition;
      delete this.remoteUserColor;
      delete this.remoteUserLineWidth;
    }
  }
})();