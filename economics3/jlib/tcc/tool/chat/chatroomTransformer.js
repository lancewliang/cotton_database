(function() {
  window.chatroomTransformer = {

    CommonRequestObj : function(type, cmd, body, option) {
      this.type = type;
      this.cmd = cmd;
      this.body = body;
      this.option = option;
    },

    currentCommand : null,
    isFilled : false,

    toXMPPRequest : function(request) {
      var type = request.type;
      if (type === undefined) {
        type = Controller.TYPE.WB;
      }
      var ret = {};
      switch (type) {
      case Controller.TYPE.WB:
        ret = this._encodeWBRequest(request);
        break;
      case Controller.TYPE.CHAT:
        ret = this._encodeCHATRequest(request);
        break;
      case Controller.TYPE.FILE:
        ret = this._encodeFILERequest(request);
        break;
      }
      return ret;
    },

    _encodeWBRequest : function(request) {
      var cmd = request.cmd;
      var ret = new this.CommonRequestObj(Controller.TYPE.WB, cmd, "", null);
      switch (cmd) {
      case wbRequests.CMD.BEGIN_DRAW:
        ret["body"] = request.startX + "," + request.startY;
        this.currentCommand = wbRequests.CMD.DRAWING;
        break;
      case wbRequests.CMD.SET_COLOR:
        ret["body"] = request.color;
        break;
      case wbRequests.CMD.SET_LINE_WIDTH:
        ret["body"] = request.lineWidth;
        break;
      case wbRequests.CMD.BEGIN_DRAW_LINE:
        ret["body"] = request.startX + "," + request.startY;
        this.currentCommand = wbRequests.CMD.DRAWING_LINE;
        break;
      case wbRequests.CMD.BEGIN_DRAW_RECT:
        ret["body"] = request.startX + "," + request.startY;
        ret["option"] = "isFilled=" + request.isFilled;
        this.currentCommand = wbRequests.CMD.DRAWING_RECT;
        this.isFilled = request.isFilled;
        break
      case wbRequests.CMD.BEGIN_DRAW_OVAL:
        ret["body"] = request.startX + "," + request.startY;
        ret["option"] = "isFilled=" + request.isFilled;
        this.currentCommand = wbRequests.CMD.DRAWING_OVAL;
        this.isFilled = request.isFilled;
        break;
      case wbRequests.CMD.DRAW_IMG:
        ret["body"] = request.image.src;
        break;
      case wbRequests.CMD.MOVE_IMG:
        ret["body"] = request.mousePrevX + "," + request.mousePrevY + "," + request.mouseX + "," + request.mouseY
        break;
      case wbRequests.CMD.DRAW_TEXT:
        ret["body"] = request.text;
        ret["option"] = [ "startX=", request.startX, ";startY=", request.startY, ";fontSize=", request.fontSize, ";fontFamily=", request.fontFamily, ";isBold=", request.isBold, ";isUnderLine=", request.isUnderLine, ";isItalic=", request.isItalic, ";color=" + request.color ]
            .join('');
        break;
      case wbRequests.CMD.MOVING:
        ret["cmd"] = this.currentCommand;
        ret["body"] = request.body;
        if (request.isFilled !== undefined) {
          ret["option"] = "isFilled=" + request.isFilled;
        }
        break;
      case wbRequests.CMD.CLEAR:
      case wbRequests.CMD.UNDO:
      case wbRequests.CMD.REDO:
      case wbRequests.CMD.END_DRAW:
        break;
      }
      return ret;
    },

    _encodeCHATRequest : function(request) {
      var message = request.message;
      var isBold = request.isBold;
      var isItalic = request.isItalic;
      var isUnderLine = request.isUnderLine;
      var color = request.color;
      var option = [ "isBold=", isBold, ";isItalic=", isItalic, ";isUnderLine=", isUnderLine, ";color=", color ].join("");
      return new this.CommonRequestObj(Controller.TYPE.CHAT, "", message, option);
    },

    _encodeFILERequest : function(request) {

    },

    toClientRequest : function(type, cmd, body, option, stamp) {
      if (type === undefined) {
        type = Controller.TYPE.WB;
      }
      switch (type) {
      case Controller.TYPE.WB:
        return this._decodeWBRequest(cmd, body, option);
        break;
      case Controller.TYPE.CHAT:
        return this._decodeCHATRequest(body, option, stamp);
        break;
      case Controller.TYPE.FILE:
        return this._decodeFILERequest(cmd, body, option);
        break;
      }
    },

    _decodeWBRequest : function(cmd, body, option) {
      var ret = [];
      switch (cmd) {
      case wbRequests.CMD.BEGIN_DRAW:
        var p = body.split(",");
        ret.push(new wbRequests.BeginDrawRequest(parseInt(p[0]), parseInt(p[1])));
        break;
      case wbRequests.CMD.SET_COLOR:
        ret.push(new wbRequests.SetColorRequest(body));
        break;
      case wbRequests.CMD.SET_LINE_WIDTH:
        ret.push(new wbRequests.SetLineWidthRequest(parseInt(body)));
        break;
      case wbRequests.CMD.DRAWING:
        var positions = body.split(",");
        for ( var i = 0, len = positions.length; i < len; i += 2) {
          ret.push(new wbRequests.DrawingRequest(null, null, parseInt(positions[i]), parseInt(positions[i + 1])));
        }
        break;
      case wbRequests.CMD.BEGIN_DRAW_LINE:
        var p = body.split(",");
        ret.push(new wbRequests.BeginDrawLineRequest(parseInt(p[0]), parseInt(p[1])));
        break;
      case wbRequests.CMD.DRAWING_LINE:
        var positions = body.split(",");
        for ( var i = 0, len = positions.length; i < len; i += 2) {
          ret.push(new wbRequests.DrawingLineRequest(null, null, parseInt(positions[i]), parseInt(positions[i + 1])));
        }
        break;
      case wbRequests.CMD.BEGIN_DRAW_RECT:
        var p = body.split(",");
        var isFilled = this._decodeOption(option)["isFilled"] === "true" ? true : false;
        ret.push(new wbRequests.BeginDrawRectRequest(parseInt(p[0]), parseInt(p[1]), isFilled));
        break;
      case wbRequests.CMD.DRAWING_RECT:
        var positions = body.split(",");
        var isFilled = this._decodeOption(option)["isFilled"] === "true" ? true : false;
        for ( var i = 0, len = positions.length; i < len; i += 2) {
          ret.push(new wbRequests.DrawingRectRequest(null, null, parseInt(positions[i]), parseInt(positions[i + 1]), isFilled));
        }
        break;
      case wbRequests.CMD.BEGIN_DRAW_OVAL:
        var p = body.split(",");
        var isFilled = this._decodeOption(option)["isFilled"] === "true" ? true : false;
        ret.push(new wbRequests.BeginDrawOvalRequest(parseInt(p[0]), parseInt(p[1]), isFilled));
        break;
      case wbRequests.CMD.DRAWING_OVAL:
        var positions = body.split(",");
        var isFilled = this._decodeOption(option)["isFilled"] === "true" ? true : false;
        for ( var i = 0, len = positions.length; i < len; i += 2) {
          ret.push(new wbRequests.DrawingOvalRequest(null, null, parseInt(positions[i]), parseInt(positions[i + 1]), isFilled));
        }
        break;
      case wbRequests.CMD.DRAW_IMG:
        ret.push(new wbRequests.DrawImageRequest(body));
        break;
      case wbRequests.CMD.MOVE_IMG:
        var p = body.split(",");
        ret.push(new wbRequests.MoveImageRequest(parseInt(p[0]), parseInt(p[1]), parseInt(p[2]), parseInt(p[3])));
        break;
      case wbRequests.CMD.DRAW_TEXT:
        var text = body;
        var options = this._decodeOption(option);
        var startX = parseInt(options["startX"]);
        var startY = parseInt(options["startY"]);
        var fontSize = parseInt(options["fontSize"]);
        var fontFamily = options["fontFamily"];
        var isBold = options["isBold"] === "true" ? true : false;
        var isItalic = options["isItalic"] === "true" ? true : false;
        var isUnderLine = options["isUnderLine"] === "true" ? true : false;
        var color = options["color"];
        ret.push(new wbRequests.DrawTextRequest(startX, startY, text, fontSize, fontFamily, isItalic, isBold, isUnderLine, color));
        break;
      case wbRequests.CMD.CLEAR:
        ret.push(new wbRequests.ClearRequest());
        break;
      case wbRequests.CMD.UNDO:
        ret.push(new wbRequests.UndoRequest());
        break;
      case wbRequests.CMD.REDO:
        ret.push(new wbRequests.RedoRequest());
        break;
      case wbRequests.CMD.END_DRAW:
        ret.push(new wbRequests.EndDrawRequest());
        break;
      }
      return ret;
    },

    _decodeCHATRequest : function(body, option, stamp) {
      var message = body;
      var options = this._decodeOption(option);
      var isBold = options["isBold"] === "true" ? true : false;
      var isItalic = options["isItalic"] === "true" ? true : false;
      var isUnderLine = options["isUnderLine"] === "true" ? true : false;
      var color = options["color"];
      return new messageList.ChatRequest(message, isBold, isItalic, isUnderLine, color, stamp);
    },

    _decodeFILERequest : function(cmd, body, option) {

    },

    _decodeOption : function(option) {
      var ret = {};
      if (option != undefined && option != null) {
        var p1 = option.split(";");
        for ( var i = 0, len = p1.length; i < len; i++) {
          var p2 = p1[i].split("=");
          ret[p2[0]] = p2[1];
        }
      }
      return ret;
    }
  }
})();