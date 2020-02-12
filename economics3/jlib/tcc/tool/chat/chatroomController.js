Controller = {
  LISTENER : {
    JOINED_ROOM : "joined_room",
    LEAVE_ROOM : "leave_room",
    MESSAGE : "message"
  },

  TYPE : {
    WB : "wb",
    CHAT : "chat",
    FILE : "file"
  }
};
var chatroomController = {
  server : null,
  username : null,
  pwd: null,
  chatwith : null,
  actionList : [],
  listener : [],
  xmppClient : null,
  xmppLib : null,

  init : function(server, username, pwd, lib, chatwith) {
    this.username = username;
    this.pwd = pwd;
    this.server = server;
    this.chatwith = chatwith;
    this.actionList = [];
    this.listener = [];
    this.xmppLib = lib;
    this.xmppClient = this.initClient();

    xmppMsgHandler.init();
  },
  /**
   * send action request to serverside after joined room
   */
  joinedRoom : function(from) {
    joinedRoom(from);
  },
  sendRequest : function() {
    if (this.actionList.length > 0) {
      var request = this.actionList.shift();
      this.xmppClient.broadcastRequest(request, this.chatwith);
    }
  },
  /**
   * receive action requests from whiteboard, chatroom or share file UI widgets
   */
  receiveRequest : function(request) {
    this.actionList.push(chatroomTransformer.toXMPPRequest(request));
  },
  /**
   * handle serverside's response message
   */
  handleMessage : function(from, type, cmd, body, option, stamp) {
    xmppMsgHandler.handle(from, type, chatroomTransformer.toClientRequest(type, cmd, body, option, stamp));
  },

  removeFromRoom : function(from) {
    console.log("remove from room(" + from + ")");
  },

  initClient : function() {
    var xmppClient = null;
    if (this.xmppLib && this.xmppLib == 'jsjac' && typeof (JSJaC) != 'undefined') {
      xmppClient = new jsjacManager(this.server, this.username, this.pwd);
      xmppClient.addListener(Controller.LISTENER.JOINED_ROOM, this.joinedRoom, this);
      xmppClient.addListener(Controller.LISTENER.LEAVE_ROOM, this.removeFromRoom, this);
      xmppClient.addListener(Controller.LISTENER.MESSAGE, this.handleMessage, this);
    } else {
      stropheManager.init(this.server, this.username, this.pwd);
      stropheManager.addListener(Controller.LISTENER.JOINED_ROOM, this.joinedRoom, this);
      stropheManager.addListener(Controller.LISTENER.LEAVE_ROOM, this.removeFromRoom, this);
      stropheManager.addListener(Controller.LISTENER.MESSAGE, this.handleMessage, this);
      xmppClient = stropheManager;
    }

    return xmppClient;
  },

  addListener : function(event, fn, scope) {
    this.listener[event] = {
      fn : fn,
      scope : scope
    };
  },

  notifyListener : function(event) {
    var listener = this.listener[event];
    if (listener !== undefined) {
      listener.fn.apply(listener.scope, Array.prototype.slice.call(arguments).slice(1));
    }
  }
};
function joinedRoom(from) {
  console.log("join room(" + from + ")");
  if (chatroomController.requestInterval === undefined) {
    chatroomController.requestInterval = setInterval($.proxy(chatroomController.sendRequest, chatroomController), 10);
  }
  chatroomController.notifyListener(Controller.LISTENER.JOINED_ROOM, from);
}