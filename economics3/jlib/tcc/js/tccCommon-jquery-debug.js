//======================================================================
(function() {
// jquery-1.7.2.js
// from line 6006
  var rcleanScript = /^\s*<!(?:\[CDATA\[|\-\-)/; // from line 5748
  $.fn.extend({
    domManip: function( args, table, callback ) {
      var results, first, fragment, parent,
        value = args[0],
        scripts = [];

      // We can't cloneNode fragments that contain checked, in WebKit
      if ( !$.support.checkClone && arguments.length === 3 && typeof value === "string" && rchecked.test( value ) ) {
        return this.each(function() {
          jQuery(this).domManip( args, table, callback, true );
        });
      }

      if ( $.isFunction(value) ) {
        return this.each(function(i) {
          var self = jQuery(this);
          args[0] = value.call(this, i, table ? self.html() : undefined);
          self.domManip( args, table, callback );
        });
      }

      if ( this[0] ) {
        parent = value && value.parentNode;

        // If we're in a fragment, just use that instead of building a new one
        if ( $.support.parentNode && parent && parent.nodeType === 11 && parent.childNodes.length === this.length ) {
          results = { fragment: parent };

        } else {
          results = $.buildFragment( args, this, scripts );
        }

        fragment = results.fragment;

        if ( fragment.childNodes.length === 1 ) {
          first = fragment = fragment.firstChild;
        } else {
          first = fragment.firstChild;
        }

        if ( first ) {
          table = table && jQuery.nodeName( first, "tr" );

          for ( var i = 0, l = this.length, lastIndex = l - 1; i < l; i++ ) {
            callback.call(
              table ?
                root(this[i], first) :
                this[i],
              // Make sure that we do not leak memory by inadvertently discarding
              // the original fragment (which might have attached data) instead of
              // using it; in addition, use the original fragment object for the last
              // item instead of first because it can end up being emptied incorrectly
              // in certain situations (Bug #8070).
              // Fragments from the fragment cache must always be cloned and never used
              // in place.
              results.cacheable || ( l > 1 && i < lastIndex ) ?
                $.clone( fragment, true, true ) :
                fragment
            );
          }
        }

        if ( scripts.length ) {
          $.each( scripts, function( i, elem ) {
            if ( elem.src ) {
              // replace here source code is line 6070
//              jQuery.ajax({
//                type: "GET",
//                global: false,
//                url: elem.src,
//                async: false,
//                dataType: "script"
//              });
              
              var oHead = document.head || document.getElementsByTagName( "head" )[0] || document.documentElement;
              var oScript= document.createElement("script");
              oScript.type = "text/javascript";
              oScript.src=elem.src;
              oHead.appendChild( oScript);
          
            } else {
              $.globalEval( ( elem.text || elem.textContent || elem.innerHTML || "" ).replace( rcleanScript, "/*$0*/" ) );
            }

            if ( elem.parentNode ) {
              elem.parentNode.removeChild( elem );
            }
          });
        }
      }

      return this;
    }
    
  });
//======================================================================
  
})();
