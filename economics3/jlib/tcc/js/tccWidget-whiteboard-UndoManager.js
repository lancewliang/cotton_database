(function() {

  window.wbUndoManager = {

    init : function(undoSteps) {
      this.steps = undoSteps;
      this.undoQueue = [];
      this.redoQueue = [];
    },

    undo : function() {
      if (this.undoQueue.length == 0) {
        return;
      }
      this.removeState();
      var restoreState = this.undoQueue.pop();
      var image = new Image();
      image.onload = function() {
        wbProcessor.context_O.drawImage(image, 0, 0);
      };
      image.src = restoreState;
    },

    redo : function() {
      if (this.redoQueue.length == 0) {
        return;
      }
      this.saveState();
      var restoreState = this.redoQueue.pop();
      var image = new Image();
      image.onload = function() {
        wbProcessor.context_O.drawImage(image, 0, 0);
      };
      image.src = restoreState;
    },

    saveState : function() {
      if (this.undoQueue.length == this.steps) {
        this.undoQueue.shift();
      }
      var state = wbProcessor.canvas_O.toDataURL("image/png");
      this.undoQueue.push(state);
    },

    removeState : function() {
      if (this.redoQueue.length == this.steps) {
        this.redoQueue.shift();
      }
      var state = wbProcessor.canvas_O.toDataURL("image/png");
      this.redoQueue.push(state);
    },

    clearHistory : function() {
      this.undoQueue.splice(0, this.undoQueue.length);
      this.redoQueue.splice(0, this.undoQueue.length);
    }
  }
})();