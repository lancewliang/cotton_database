(function() {

  window.wbRequests = {

    CMD : {
      BEGIN_DRAW : "beginDraw",
      DRAWING : "drawing",
      SET_COLOR : "setColor",
      SET_LINE_WIDTH : "setLineWidth",
      BEGIN_DRAW_LINE : "beginDrawLine",
      DRAWING_LINE : "drawingLine",
      BEGIN_DRAW_RECT : "beginDrawRect",
      DRAWING_RECT : "drawingRect",
      BEGIN_DRAW_OVAL : "beginDrawOval",
      DRAWING_OVAL : "drawingOval",
      CLEAR : "clear",
      DRAW_IMG : "drawImg",
      MOVE_IMG : "moveImg",
      DRAW_TEXT : "drawText",
      MOVING : "moving",
      UNDO : "undo",
      REDO : "redo",
      END_DRAW : "endDraw"
    },

    BeginDrawRequest : function(startX, startY) {
      this.cmd = wbRequests.CMD.BEGIN_DRAW;
      this.startX = startX;
      this.startY = startY;
    },

    DrawingRequest : function(startX, startY, lineToX, lineToY, color, lineWidth) {
      this.cmd = wbRequests.CMD.DRAWING;
      this.startX = startX;
      this.startY = startY;
      this.lineToX = lineToX;
      this.lineToY = lineToY;
      this.color = color || wbProcessor.color;
      this.lineWidth = lineWidth || wbProcessor.lineWidth;
    },

    SetColorRequest : function(color) {
      this.cmd = wbRequests.CMD.SET_COLOR;
      this.color = color;
    },

    SetLineWidthRequest : function(lineWidth) {
      this.cmd = wbRequests.CMD.SET_LINE_WIDTH;
      this.lineWidth = lineWidth;
    },

    BeginDrawLineRequest : function(startX, startY) {
      this.cmd = wbRequests.CMD.BEGIN_DRAW_LINE;
      this.startX = startX;
      this.startY = startY;
    },

    DrawingLineRequest : function(startX, startY, lineToX, lineToY, lineWidth, color) {
      this.cmd = wbRequests.CMD.DRAWING_LINE;
      this.startX = startX;
      this.startY = startY;
      this.lineToX = lineToX;
      this.lineToY = lineToY;
      this.lineWidth = lineWidth || wbProcessor.lineWidth;
      this.color = color || wbProcessor.color;
    },

    BeginDrawRectRequest : function(startX, startY, isFilled) {
      this.cmd = wbRequests.CMD.BEGIN_DRAW_RECT;
      this.startX = startX;
      this.startY = startY;
      this.isFilled = isFilled;
    },

    DrawingRectRequest : function(startX, startY, moveToX, moveToY, isFilled, lineWidth, color) {
      this.cmd = wbRequests.CMD.DRAWING_RECT;
      this.startX = startX;
      this.startY = startY;
      this.moveToX = moveToX;
      this.moveToY = moveToY;
      this.isFilled = isFilled;
      this.lineWidth = lineWidth || wbProcessor.lineWidth;
      this.color = color || wbProcessor.color;
    },

    BeginDrawOvalRequest : function(startX, startY, isFilled) {
      this.cmd = wbRequests.CMD.BEGIN_DRAW_OVAL;
      this.startX = startX;
      this.startY = startY;
      this.isFilled = isFilled;
    },

    DrawingOvalRequest : function(startX, startY, width, height, isFilled, lineWidth, color) {
      this.cmd = wbRequests.CMD.DRAWING_OVAL;
      this.startX = startX;
      this.startY = startY;
      this.width = width;
      this.height = height;
      this.isFilled = isFilled;
      this.lineWidth = lineWidth || wbProcessor.lineWidth;
      this.color = color || wbProcessor.color;
    },

    DrawImageRequest : function(image) {
      this.cmd = wbRequests.CMD.DRAW_IMG;
      this.image = image;
    },

    MoveImageRequest : function(mousePrevX, mousePrevY, mouseX, mouseY) {
      this.cmd = wbRequests.CMD.MOVE_IMG;
      this.mousePrevX = mousePrevX;
      this.mousePrevY = mousePrevY;
      this.mouseX = mouseX;
      this.mouseY = mouseY;
    },

    DrawTextRequest : function(startX, startY, text, fontSize, fontFamily, isItalic, isBold, isUnderLine, color) {
      this.cmd = wbRequests.CMD.DRAW_TEXT;
      this.startX = startX;
      this.startY = startY;
      this.text = text;
      this.fontSize = fontSize;
      this.fontFamily = fontFamily;
      this.isItalic = isItalic;
      this.isBold = isBold;
      this.isUnderLine = isUnderLine;
      this.color = color || wbProcessor.color;
    },

    ClearRequest : function() {
      this.cmd = wbRequests.CMD.CLEAR;
    },

    OrbitRequest : function(orbit) {
      this.cmd = wbRequests.CMD.MOVING;
      this.body = orbit;
    },

    UndoRequest : function() {
      this.cmd = wbRequests.CMD.UNDO;
    },

    RedoRequest : function() {
      this.cmd = wbRequests.CMD.REDO;
    },

    EndDrawRequest : function() {
      this.cmd = wbRequests.CMD.END_DRAW;
      this.from = "local";
    }
  };

  window.wbProcessor = {

    lastBufferTime : new Date().getTime(),

    bufferedPosition : [],

    positionRequestIntervalID : null,

    remoteCanvas : {},

    tmpCanvas : {},

    imageObj : {},

    init : function() {
      this.lineWidth = wb.options.lineWidth;
      this.color = wb.options.color;

      this.canvas_O = TCC.find("#canvas_o").get(0);
      this.context_O = this.canvas_O.getContext("2d");
      this.context_O.lineWidth = this.lineWidth;
      this.context_O.lineCap = "round";
      this.context_O.strokeStyle = this.color;

      this.canvas = TCC.find("#canvas_t").get(0);
      this.context = this.canvas.getContext("2d");

      this.context.lineWidth = this.lineWidth;
      this.context.lineCap = "round";
      this.context.strokeStyle = this.color;

      this.canvasWidth = this.canvas.width;
      this.canvasHeight = this.canvas.height;
      this.clearCanvas();
      //if (wbProcessor.controller) {
        setInterval(wbProcessor.gatherPositionRequest, wb.options.requestInterval);
      //}
    },

    createTmpCanvas : function(from) {
      var canvas = document.createElement("canvas");
      var context = canvas.getContext("2d");
      canvas.width = wbProcessor.canvasWidth;
      canvas.height = wbProcessor.canvasHeight;
      context.lineCap = "round";
      wbProcessor.tmpCanvas[from] = {
        canvas : canvas,
        context : context
      };
    },

    beginDraw : function(request) {
      wbProcessor.bufferedPosition.push(wbRequests.CMD.BEGIN_DRAW + "-" + request.startX + "," + request.startY);
    },

    drawing : function(request) {
      wbProcessor.notifyListener(request);
      wbProcessor.context.lineWidth = request.lineWidth;
      wbProcessor.context.strokeStyle = request.color;
      wbProcessor.context.beginPath();
      wbProcessor.context.moveTo(request.startX + 0.5, request.startY + 0.5);
      wbProcessor.context.lineTo(request.lineToX + 0.5, request.lineToY + 0.5);
      wbProcessor.context.stroke();
      if (request.isRemote) {
        var context = wbProcessor.tmpCanvas[request.from].context;
        context.lineWidth = request.lineWidth;
        context.strokeStyle = request.color;
        context.beginPath();
        context.moveTo(request.startX + 0.5, request.startY + 0.5);
        context.lineTo(request.lineToX + 0.5, request.lineToY + 0.5);
        context.stroke();
      }
    },

    drawImg : function(request) {
      wbProcessor.notifyListener(request);
      var image = request.image;
      var imageSize = wbProcessor.adjustImageSize(image);
      wbProcessor.clearCanvas();
      if (wb.options.imagePos === "center") {
        wbProcessor.context_O.drawImage(image, imageSize.left, imageSize.top, imageSize.width, imageSize.height);
      } else {
        wbProcessor.context_O.drawImage(image, 0, 0, imageSize.width, imageSize.height);
      }
      wbUndoManager.clearHistory();
      if (wb.options.moveImage) {
        this.imageObj = {
          "src" : image.src,
          "width" : imageSize.width,
          "height" : imageSize.height,
          "top" : imageSize.top,
          "left" : imageSize.left,
          "image" : image
        };
      }
    },

    adjustImageSize : function(image) {
      var imgWidth = image.width;
      var imgHeight = image.height;
      var imageSize = {
        top : 0,
        left : 0,
        width : imgWidth,
        height : imgHeight
      };
      var canvasWidth = wbProcessor.canvasWidth;
      var canvasHeight = wbProcessor.canvasHeight;
      if (imgWidth > canvasWidth) {
        imageSize.width = canvasWidth;
        if (wb.options.keepImageProportions) {
          imageSize.height = Math.floor((canvasWidth / imgWidth) * imgHeight);
        } else {
          imageSize.height = imgHeight;
        }
      }
      if (imgHeight > canvasHeight) {
        imageSize.height = canvasHeight;
        if (wb.options.keepImageProportions) {
          imageSize.width = Math.floor((canvasHeight / imgHeight) * imgWidth);
        } else {
          imageSize.width = imgWidth;
        }
      }
      if (imageSize.width < canvasWidth) {
        imageSize.left = Math.round((canvasWidth - imageSize.width) / 2);
      }
      if (imageSize.height < canvasHeight) {
        imageSize.top = Math.round((canvasHeight - imageSize.height) / 2);
      }
      return imageSize;
    },

    saveImg : function() {
      return Canvas2Image.saveAsPNG(wbProcessor.canvas_O, true);
    },

    drawText : function(request) {
      wbProcessor.notifyListener(request);
      var fontStyle = "";
      if (request.isItalic == true) {
        fontStyle = "italic "
      }
      if (request.isBold == true) {
        fontStyle = fontStyle + "bold ";
      }
      fontStyle = fontStyle + request.fontSize + "px " + request.fontFamily;
      var fontColor = request.color;
      var lineHeight = wbProcessor.determineFontHeight("font-family:" + request.fontFamily + ";font-size:" + request.fontSize + "px;");
      wbProcessor.paintText(request.startX, request.startY, request.text, fontStyle, fontColor, lineHeight, request.isUnderLine);
    },

    paintText : function(x, y, text, fontStyle, fontColor, lineHeight, isUnderLine) {
      var context = wbProcessor.context_O;
      context.fillStyle = fontColor;
      context.font = fontStyle;
      var textWidth = TCC.find("#font_textarea").width();
      var textLines = wbProcessor.getLines(wbProcessor.context, text, textWidth, fontStyle);
      for ( var i = 0, len = textLines.length; i < len; i++) {
        context.fillText(textLines[i], x, y + i * lineHeight);
        if (isUnderLine) {
          var textWidth = context.measureText(textLines[i]).width;
          context.lineWidth = 1;
          context.strokeStyle = fontColor;
          context.beginPath();
          context.moveTo(x + 0.5, y + 2.5 + i * lineHeight);
          context.lineTo(x + textWidth + 0.5, y + 2.5 + i * lineHeight);
          context.stroke();
          context.closePath();
        }
      }
    },

    getLines : function(ctx, text, maxLength, textStyle) {
      var wa = text.split(" "), textArray = [], lastText = "", measure = 0;
      ctx.font = textStyle;
      for ( var i = 0; i < wa.length; i++) {
        var w = wa[i];
        measure = ctx.measureText(lastText + w).width;
        if (measure < maxLength) {
          lastText += (" " + w);
        } else {
          textArray.push(lastText);
          lastText = w;
        }
        if (i === wa.length - 1) {
          textArray.push(lastText);
          break;
        }
      }
      return textArray;
    },

    determineFontHeight : function(fontStyle) {
      var body = document.getElementsByTagName("body")[0];
      var dummy = document.createElement("div");
      var dummyText = document.createTextNode("M");
      dummy.appendChild(dummyText);
      dummy.setAttribute("style", fontStyle);
      body.appendChild(dummy);
      var result = dummy.offsetHeight;
      body.removeChild(dummy);
      return result;
    },

    beginLine : function(request) {
      wbProcessor.bufferedPosition.push(wbRequests.CMD.BEGIN_DRAW_LINE + "-" + request.startX + "," + request.startY);
    },

    createRemoteCanvas : function(from) {
      var tmp = document.createElement("canvas");
      var tmpContext = tmp.getContext("2d");
      tmp.width = wbProcessor.canvasWidth;
      tmp.height = wbProcessor.canvasHeight;
      tmpContext.lineCap = "round";

      wbProcessor.remoteCanvas[from] = {
        canvas : tmp,
        context : tmpContext
      };
    },

    drawLine : function(request) {
      wbProcessor.notifyListener(request);
      var context = wbProcessor.context;
      if (request.isRemote) {
        context = wbProcessor.remoteCanvas[request.from].context;
      }
      context.clearRect(0, 0, wbProcessor.canvasWidth, wbProcessor.canvasHeight);
      wbProcessor.syncCanvases(request, context);
      context.lineWidth = request.lineWidth;
      context.strokeStyle = request.color;
      context.beginPath();
      context.moveTo(request.startX + 0.5, request.startY + 0.5);
      context.lineTo(request.lineToX + 0.5, request.lineToY + 0.5);
      context.stroke();
      context.closePath();
    },

    beginRectangle : function(request) {
      wbProcessor.bufferedPosition.push(wbRequests.CMD.BEGIN_DRAW_RECT + "-" + request.startX + "," + request.startY + "," + request.isFilled);
    },

    drawRectangle : function(request) {
      wbProcessor.notifyListener(request);
      var context = wbProcessor.context;
      if (request.isRemote) {
        context = wbProcessor.remoteCanvas[request.from].context;
      }
      var sx = request.startX;
      var sy = request.startY;
      var ex = request.moveToX;
      var ey = request.moveToY;
      var tmp = 0;
      if (ex < sx) {
        tmp = sx;
        sx = ex;
        ex = tmp;
      }
      if (ey < sy) {
        tmp = sy;
        sy = ey;
        ey = tmp;
      }
      context.clearRect(0, 0, wbProcessor.canvasWidth, wbProcessor.canvasHeight);
      wbProcessor.syncCanvases(request, context);
      if (!request.isFilled) {
        context.lineWidth = request.lineWidth;
        context.strokeStyle = request.color;
      } else {
        context.fillStyle = request.color;
      }
      context.beginPath();
      context.rect(sx + 0.5, sy + 0.5, ex - sx, ey - sy);
      context.closePath();
      request.isFilled ? context.fill() : context.stroke();
    },

    beginOval : function(request) {
      wbProcessor.bufferedPosition.push(wbRequests.CMD.BEGIN_DRAW_OVAL + "-" + request.startX + "," + request.startY + "," + request.isFilled);
    },

    drawOval : function(request) {
      wbProcessor.notifyListener(request);
      var context = wbProcessor.context;
      if (request.isRemote) {
        context = wbProcessor.remoteCanvas[request.from].context;
      }

      var x = request.startX;
      var y = request.startY;
      var w = request.width;
      var h = request.height;

      var kappa = 0.5522848;
      var ox = (w / 2) * kappa;
      var oy = (h / 2) * kappa;
      var xe = x + w;
      var ye = y + h;
      var xm = x + w / 2;
      var ym = y + h / 2;

      context.clearRect(0, 0, wbProcessor.canvasWidth, wbProcessor.canvasHeight);
      wbProcessor.syncCanvases(request, context);
      if (!request.isFilled) {
        context.lineWidth = request.lineWidth;
        context.strokeStyle = request.color;
      } else {
        context.fillStyle = request.color;
      }
      context.beginPath();
      context.moveTo(x, ym);
      context.bezierCurveTo(x, ym - oy, xm - ox, y, xm, y);
      context.bezierCurveTo(xm + ox, y, xe, ym - oy, xe, ym);
      context.bezierCurveTo(xe, ym + oy, xm + ox, ye, xm, ye);
      context.bezierCurveTo(xm - ox, ye, x, ym + oy, x, ym);
      context.closePath();
      request.isFilled ? context.fill() : context.stroke();
    },

    clear : function(request) {
      wbProcessor.notifyListener(request);
      wbProcessor.clearCanvas();
      wbUndoManager.clearHistory();
    },

    clearCanvas : function() {
      wbProcessor.context.clearRect(0, 0, wbProcessor.canvasWidth, wbProcessor.canvasHeight);
      wbProcessor.context_O.clearRect(0, 0, wbProcessor.canvasWidth, wbProcessor.canvasHeight);
      wbProcessor.setCanvasBackgroud();
    },

    setCanvasBackgroud : function() {
      wbProcessor.context_O.fillStyle = wb.options.backgroundColor;
      wbProcessor.context_O.fillRect(0, 0, wbProcessor.canvasWidth, wbProcessor.canvasHeight);
    },

    updateLineWidth : function(request) {
      wbProcessor.notifyListener(request);
      wbProcessor.lineWidth = request.lineWidth;
    },

    updateColor : function(request) {
      wbProcessor.notifyListener(request);
      wbProcessor.color = request.color;
    },

    undo : function(request) {
      wbProcessor.notifyListener(request);
      wbUndoManager.undo();
    },

    redo : function(request) {
      wbProcessor.notifyListener(request);
      wbUndoManager.redo();
    },

    moveImage : function(request) {
      wbProcessor.notifyListener(request);
      var imageObj = this.imageObj;
      var mouseX = request.mouseX, mouseY = request.mouseY, mousePrevX = request.mousePrevX, mousePrevY = request.mousePrevY;
      wbProcessor.clearCanvas();
      var left = imageObj.left + mouseX - mousePrevX;
      var top = imageObj.top + mouseY - mousePrevY;
      wbProcessor.context_O.drawImage(imageObj.image, left, top, imageObj.width, imageObj.height);
      imageObj.left = left;
      imageObj.top = top;
    },

    endDraw : function(request) {
      if (request.isRemote) {
        var from = request.from, remoteCanvas = wbProcessor.remoteCanvas[from];
        // draw line,rect and oval
        if (remoteCanvas !== undefined) {
          var remoteContext = remoteCanvas.context;
          // copy remote canvas to original canvas
          wbProcessor.context_O.drawImage(remoteCanvas.canvas, 0, 0);
          // clear remote canvas
          remoteContext.clearRect(0, 0, wbProcessor.canvasWidth, wbProcessor.canvasHeight);
        } else {
          var tmpCanvas = wbProcessor.tmpCanvas[from];
          if (tmpCanvas !== undefined) {
            wbProcessor.context_O.drawImage(tmpCanvas.canvas, 0, 0);
            tmpCanvas.context.clearRect(0, 0, wbProcessor.canvasWidth, wbProcessor.canvasWidth);
          }
        }
      } else {
        wbProcessor.bufferedPosition.push(wbRequests.CMD.END_DRAW + "-0");
        wbProcessor.context_O.drawImage(wbProcessor.canvas, 0, 0);
        wbProcessor.context.clearRect(0, 0, wbProcessor.canvasWidth, wbProcessor.canvasHeight);
      }
    },

    gatherPosition : function(x, y, interval) {
      if (interval === undefined) {
        interval = 10;
      }
      if ((new Date().getTime() - wbProcessor.lastBufferTime) > interval) {
        wbProcessor.bufferedPosition.push(x + "," + y);
        wbProcessor.lastBufferTime = new Date().getTime();
      }
    },

    gatherPositionRequest : function() {
      if (wbProcessor.bufferedPosition.length === 0) {
        return;
      } else {
        var requestContent = [];
        for ( var i = 0, len = wbProcessor.bufferedPosition.length; i < len; i++) {
          var position = wbProcessor.bufferedPosition[i];
          var index = position.indexOf("-");
          var prefix = position.substring(0, index);
          if (prefix === wbRequests.CMD.BEGIN_DRAW) {
            var p = position.substring(index + 1).split(",");
            var startX = p[0], startY = p[1];
            wbProcessor.notifyListener(new wbRequests.BeginDrawRequest(startX, startY));
          } else if (prefix === wbRequests.CMD.BEGIN_DRAW_LINE) {
            var p = position.substring(index + 1).split(",");
            var startX = p[0], startY = p[1];
            wbProcessor.notifyListener(new wbRequests.BeginDrawLineRequest(startX, startY));
          } else if (prefix === wbRequests.CMD.BEGIN_DRAW_RECT) {
            var p = position.substring(index + 1).split(",");
            var startX = p[0], startY = p[1];
            var isFilled = p[2] === "true" ? true : false;
            wbProcessor.notifyListener(new wbRequests.BeginDrawRectRequest(startX, startY, isFilled));
          } else if (prefix === wbRequests.CMD.BEGIN_DRAW_OVAL) {
            var p = position.substring(index + 1).split(",");
            var startX = p[0], startY = p[1];
            var isFilled = p[2] === "true" ? true : false;
            wbProcessor.notifyListener(new wbRequests.BeginDrawOvalRequest(startX, startY, isFilled));
          } else {
            if (prefix === wbRequests.CMD.END_DRAW) {
              if (requestContent.length > 0) {
                var request = new wbRequests.OrbitRequest(requestContent.join(","));
                wbProcessor.notifyListener(request);
                requestContent = [];
              }
              wbProcessor.notifyListener(new wbRequests.EndDrawRequest());
            } else {
              requestContent.push(position);
            }
          }
        }
        if (requestContent.length > 0) {
          var request = new wbRequests.OrbitRequest(requestContent.join(","));
          wbProcessor.notifyListener(request);
          requestContent = [];
        }
        wbProcessor.bufferedPosition = [];
      }
    },

    notifyListener : function(request) {
      if (request.isRemote) {
        return;
      }
      var controller = wbProcessor.controller;
      if (controller !== undefined) {
        var cmd = request.cmd;
        switch (cmd) {
        case wbRequests.CMD.DRAWING:
        case wbRequests.CMD.DRAWING_LINE:
          wbProcessor.gatherPosition(request.lineToX, request.lineToY);
          break;
        case wbRequests.CMD.DRAWING_RECT:
          wbProcessor.gatherPosition(request.moveToX, request.moveToY);
          break;
        case wbRequests.CMD.DRAWING_OVAL:
          wbProcessor.gatherPosition(request.width, request.height);
          break;
        default:
          controller.receiveRequest(request);
          break;
        }
      }
    },

    syncCanvases : function(request, context) {
      for ( var from in wbProcessor.tmpCanvas) {
        if (request.from !== from) {
          context.drawImage(wbProcessor.tmpCanvas[from].canvas, 0, 0);
        }
      }
    }
  }
})();