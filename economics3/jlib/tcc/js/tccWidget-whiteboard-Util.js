/**
 * http://bloggingmath.wordpress.com/2009/05/29/line-segment-intersection/
 */
function Vector(x, y) {
  this.x = x;
  this.y = y;

  this.scalarMult = function(scalar) {
    return new Vector(this.x * scalar, this.y * scalar);
  }
  this.dot = function(v2) {
    return this.x * v2.x + this.y * v2.y;
  };
  this.perp = function() {
    return new Vector(-1 * this.y, this.x);
  };
  this.subtract = function(v2) {
    return this.add(v2.scalarMult(-1));
  };
  this.add = function(v2) {
    return new Vector(this.x + v2.x, this.y + v2.y);
  }
}

function Square(a, b, c, d) {
  this.a = a;
  this.b = b;
  this.c = c;
  this.d = d;
  this.origin = centerSquareOrigin(a, b, c, d);

  this.intersect = function(mouse) {
    return (!intersectWithLine(this.origin, mouse, this.a, this.b) && !intersectWithLine(this.origin, mouse, this.b, this.c) && !intersectWithLine(this.origin, mouse, this.c, this.d) && !intersectWithLine(this.origin, mouse, this.d, this.a));
  }

  this.rotate = function(angle) {
    var radius = Math.sqrt(Math.pow(this.origin.x - this.a.x, 2) + Math.pow(this.origin.y - this.a.y, 2));

    var aAngle = Math.atan2((this.a.y - this.origin.y), (this.a.x - this.origin.x));
    var bAngle = Math.atan2((this.b.y - this.origin.y), (this.b.x - this.origin.x));
    var cAngle = Math.atan2((this.c.y - this.origin.y), (this.c.x - this.origin.x));
    var dAngle = Math.atan2((this.d.y - this.origin.y), (this.d.x - this.origin.x));

    this.a.x = this.origin.x + radius * Math.cos(angle + aAngle);
    this.a.y = this.origin.y + radius * Math.sin(angle + aAngle);
    this.b.x = this.origin.x + radius * Math.cos(angle + bAngle);
    this.b.y = this.origin.y + radius * Math.sin(angle + bAngle);
    this.c.x = this.origin.x + radius * Math.cos(angle + cAngle);
    this.c.y = this.origin.y + radius * Math.sin(angle + cAngle);
    this.d.x = this.origin.x + radius * Math.cos(angle + dAngle);
    this.d.y = this.origin.y + radius * Math.sin(angle + dAngle);
  }

  this.alignBottomRight = function(alignPoint) {
    var diff = new Vector(alignPoint.x - this.c.x, alignPoint.y - this.c.y);

    this.a = this.a.add(diff);
    this.b = this.b.add(diff);
    this.c = this.c.add(diff);
    this.d = this.d.add(diff);
    this.origin = centerSquareOrigin(this.a, this.b, this.c, this.d);
  }

  this.alignTopRight = function(alignPoint) {
    var diff = new Vector(alignPoint.x - this.b.x, alignPoint.y - this.b.y);

    this.a = this.a.add(diff);
    this.b = this.b.add(diff);
    this.c = this.c.add(diff);
    this.d = this.d.add(diff);
    this.origin = centerSquareOrigin(this.a, this.b, this.c, this.d);
  }

  var epsilon = 10e-6;

  function centerSquareOrigin(a, b, c, d) {
    p = a;
    r = c.subtract(a);
    q = b;
    s = d.subtract(b);

    rCrossS = cross(r, s);
    if (rCrossS <= epsilon && rCrossS >= -1 * epsilon) {
      return;
    }
    t = cross(q.subtract(p), s) / rCrossS;
    u = cross(q.subtract(p), r) / rCrossS;
    if (0 <= u && u <= 1 && 0 <= t && t <= 1) {
      intPoint = p.add(r.scalarMult(t));
      return new Vector(intPoint.x, intPoint.y);
    }

    return null;
  }

  function cross(v1, v2) {
    return v1.x * v2.y - v2.x * v1.y;
  }

  function intersectWithLine(l1p1, l1p2, l2p1, l2p2) {
    p = l1p1;
    r = l1p2.subtract(l1p1);
    q = l2p1;
    s = l2p2.subtract(l2p1);

    rCrossS = cross(r, s);
    if (rCrossS <= epsilon && rCrossS >= -1 * epsilon) {
      return false;
    }

    t = cross(q.subtract(p), s) / rCrossS;
    u = cross(q.subtract(p), r) / rCrossS;
    if (0 <= u && u <= 1 && 0 <= t && t <= 1) {
      return true;
    } else {
      return false;
    }
  }
}

function intersect(width, height, left, top, mouseX, mouseY) {
  var square = new Square(new Vector(left, top), new Vector(left + width, top), new Vector(left + width, top + height), new Vector(left, top + height));
  return square.intersect(new Vector(mouseX, mouseY));
}