function jsjacManager(server, username, pwd) {
  this.server = server;
  this.username = username;
  this.pwd = pwd;
  this.initConnection();
  this.listener = {};
}

jsjacManager.prototype = {

  initConnection : function() {
    try {
      var options = new Object();
      options.httpbase = this.server;
      options.timerval = 2000;

      this.conn = new JSJaCHttpBindingConnection(options);
      this.registerHandler();
      options = new Object();
      options.username = this.username;
      options.pass = this.pwd;
      this.conn.connect(options);
    } catch (e) {
      console.log(e);
    }
  },

  registerHandler : function() {
    this.conn.registerHandler("message", $.proxy(this.handleMessage, this));
    this.conn.registerHandler("presence", $.proxy(this.handlePresence, this));
    this.conn.registerHandler("onconnect", $.proxy(this.handleConnected, this));
    this.conn.registerHandler("ondisconnect", $.proxy(this.handleDisconnected, this));
  },

  handleMessage : function(packet) {
    var from = packet.getFrom();
    var body = packet.getBody();
    var type = packet.getChild("type").textContent;
    var cmd = packet.getChild("cmd").textContent;
    var option = null;
    if (packet.getChild("option") !== null) {
      option = packet.getChild("option").textContent;
    }
    this.notifyListener(Controller.LISTENER.MESSAGE, this.getUserName(from), type, cmd, body, option);
  },

  handlePresence : function(packet) {
    var from = packet.getFrom();
    this.notifyListener(Controller.LISTENER.JOINED_ROOM, this.getUserName(from));
  },

  handleConnected : function() {
    try {
      this.joinRoom();
    } catch (e) {
      console.log(e);
    }
  },

  joinRoom : function(roomId) {
		this.conn.send(new JSJaCPresence());
		
//    if (roomId === undefined) {
//      this.conn.send(new JSJaCPresence());
//    } else {
//      var joinPacket = new JSJaCPresence();
//      var roomId = "chatter@tcc.tccchinacruiser.com/" + this.username.substring(0, 7);
//      joinPacket.setTo(roomId);
//      var xnode = joinPacket.buildNode("x");
//      xnode.setAttribute("xmlns", "http://jabber.org/protocol/muc");
//      joinPacket.appendNode(xnode);
//      joinPacket.setStatus("available");
//      this.conn.send(joinPacket);
//    }
  },

  handleDisconnected : function() {
    console.log("handleDisconnected");
  },

  broadcastRequest : function(request, to) {
    var msg = new JSJaCMessage();
    msg.setTo(to);
    msg.setBody(request.body);
    msg._setChildNode("type", request.type);
    msg._setChildNode("cmd", request.cmd);
    var option = request.option;
    if (option !== undefined && option !== null) {
      msg._setChildNode("option", option);
    }
    this.conn.send(msg);
  },

  addListener : function(name, fn, scope) {
    this.listener[name] = {
      fn : fn,
      scope : scope
    };
  },

  notifyListener : function(listenerName) {
    var listener = this.listener[listenerName];
    if (listener !== undefined) {
      listener.fn.apply(listener.scope, Array.prototype.slice.call(arguments).slice(1));
    }
  },

  getUserName : function(from) {
    var index = from.indexOf('@');
    if (index > -1) {
      return from.substring(0, index);
    } else {
      return from;
    }
  }
}
