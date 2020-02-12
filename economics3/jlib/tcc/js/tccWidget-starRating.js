(function(TCC) {
  var TPL = {
    star : "<span class='star ${starType}' style='margin-right:${starMargin}px'></span>"
  };

  var STYLE = {
    star1 : "star1",
    star2 : "star2",
    star3 : "star3"
  };

  TCC.widget("starRating", {

    options : {
      starCount : 5,
      rating : 0,
      starMargin : 2,
      selectedFn : null
    },

    _init : function() {
      var that = this;

      this.on("mouseout", function() {
        that._selectStar(that.options.rating);
      });

      this.on("click", function() {
        that.isSelected = true;
        if (that.options.selectedFn) {
          var rating = that.find(".star1").length;
          that.options.selectedFn(rating);
        }
      });

      this.delegate("mouseover", ".star", function(evt) {
        var target = evt.currentTarget;
        target.removeClass(STYLE.star2);
        target.removeClass(STYLE.star3);
        target.addClass(STYLE.star1);
        var prevAll = target.prevAll(".star");
        var nextAll = target.nextAll(".star");
        prevAll.removeClass(STYLE.star2);
        prevAll.removeClass(STYLE.star3);
        prevAll.addClass(STYLE.star1);
        nextAll.removeClass(STYLE.star1);
        nextAll.removeClass(STYLE.star3);
        nextAll.addClass(STYLE.star2);
      });
    },

    _create : function() {
      var wId = this.getWId();
      this.addClass("starContainer");
      this.addClass(wId);

      var content = [];
      for ( var i = 0; i < this.options.starCount; i++) {
        content.push(TPL.star.replace("${starType}", STYLE.star1).replace("${starMargin}", this.options.starMargin));
      }
      this.appendChild(content.join(""));
      this._selectStar(this.options.rating);
      delete content;
    },

    _selectStar : function(rating) {
      if (this.isSelected) {
        return false;
      }
      if (rating == Math.ceil(rating)) {
        this.children(".star").each(function(starEL, index) {
          starEL = TCC.create(starEL);
          if (index + 1 > rating) {
            starEL.removeClass(STYLE.star1);
            starEL.removeClass(STYLE.star3);
            starEL.addClass(STYLE.star2);
          } else {
            starEL.removeClass(STYLE.star2);
            starEL.removeClass(STYLE.star3);
            starEL.addClass(STYLE.star1);
          }
          index++;
        })
      } else {
        this.children(".star").each(function(starEL, index) {
          starEL = TCC.create(starEL);
          if (index + 1 > Math.floor(rating)) {
            if (index + 1 == Math.ceil(rating)) {
              starEL.removeClass(STYLE.star1);
              starEL.removeClass(STYLE.star2);
              starEL.addClass(STYLE.star3);
            } else {
              starEL.removeClass(STYLE.star1);
              starEL.removeClass(STYLE.star3);
              starEL.addClass(STYLE.star2);
            }
          } else {
            starEL.removeClass(STYLE.star2);
            starEL.removeClass(STYLE.star3);
            starEL.addClass(STYLE.star1);
          }
        })
      }
    }
  });
})(TCC);