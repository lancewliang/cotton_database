var stropheManager = {
  sever : null,
  username : null,
  pwd: null,
  conn : null,
  room : null,
  joined : null,
  listener : {},
  current : null,

  init : function(server, username, pwd) {
    this.server = server;
    this.username = username;
    this.pwd = pwd;
    this.listener = {};
    initConnection({
      server : server,
      username : username,
      pwd : pwd
    });
  },
  handlePresence : function(presence) {
    this.current = $(presence).attr('from');
    this.notifyListener(Controller.LISTENER.JOINED_ROOM, Strophe.getNodeFromJid(this.current));
    return true;
  },
  handleMessage : function(message) {
    var from = Strophe.getNodeFromJid($(message).attr('from'));
    var stamp = null;
    if ($(message).find('delay').length > 0) {
      stamp = Date.parse($(message).find('delay').attr('stamp'));
    }
    var type = $(message).find("type").text();
    var cmd = $(message).find("cmd").text();
    var option = $(message).find('option').text();
    $(message).find("type").empty();
    $(message).find("cmd").empty();
    $(message).find("option").empty();
    var body = $(message).children('body').text();
    if (stamp != null) {
      this.notifyListener(Controller.LISTENER.MESSAGE, from, type, cmd, body, option, stamp);
    } else {
      this.notifyListener(Controller.LISTENER.MESSAGE, from, type, cmd, body, option);
    }

    return true;
  },
  broadcastRequest : function(request, to) {
    // var jid = $(this).parent().data('jid');
    var jid = to;
    var msg = $msg({
      to : jid,
      "type" : "chat"
    }).c('body').t(request.body).c('type').t(request.type).up().c('cmd').t(request.cmd).up();
    var option = request.option;
    if (option !== undefined && option !== null) {
      msg = msg.c('option').t(option);
    }
    msg.toString();
    stropheManager.conn.send(msg);
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
  }
};

function initConnection(data) {
  try {
    var connection = new Strophe.Connection(data.server);
    connection.connect(data.username, data.pwd, function(status) {
      // alert(status)
      if (status === Strophe.Status.CONNECTED) {
        handleConnected();
      } else if (status === Strophe.Status.DISCONNECTED) {
        handleDisconnected();
      }
    });
    stropheManager.conn = connection;
  } catch (e) {
    console.log(e);
  }
}
function handleConnected() {
  console.log("handleConnected");
  // stropheManager.conn.send($pres().c('priority').t('10'));
  stropheManager.conn.send($pres());
  // var iq = $iq({type: 'get'}).c('query', {xmlns: 'jabber:iq:roster'});
  stropheManager.conn.addHandler(TCC.proxy(stropheManager.handlePresence, stropheManager), null, "presence");
  stropheManager.conn.addHandler(TCC.proxy(stropheManager.handleMessage, stropheManager), null, "message");
}
function handleDisconnected() {
  console.log("handleDisconnected");
  stropheManager.conn = null;
}
function room_joined() {
  stropheManager.joined = true;
}
