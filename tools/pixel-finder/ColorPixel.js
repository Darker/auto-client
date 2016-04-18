function ColorPixel(x, y, color, width, height, name, tolerance) {
  this.x = x;
  this.y = y;
  this.color = color;
  
  this.width = width;
  this.height = height;
  if(typeof name=="string" && name.length>0)
    this.name = name;
  else
    this.name = "POINT_NAME";
  if(typeof tolerance == "number" && tolerance >= 0)
    this.tolerance = tolerance;
  else
    this.tolerance = 1;
}
ColorPixel.fromStringJava = function(str, width, height) {
  var point_regex = new RegExp("([A-Za-z0-9_]*) *\\(([0-9\\.]+)D, *([0-9\\.]+)D(, *new Color\\(([0-9 ,\\.]+)\\)(, *([0-9]+))?)?\\) *,?");
  var match = str.match(point_regex);
  if(match) {
    console.log("Parser matches: ", match);
    var coords = [Math.round(width*match[2]), Math.round(height*match[3])];
    var color = [0,0,0,1]; 
    var tolerance = typeof match[7]=="string" && match[7].length>0?1*match[7]:1;
    if(typeof match[5]=="string") {
      color = match[5].split(", ");
    }
    return new ColorPixel(coords[0], coords[1], color, width, height, match[1], tolerance);
  }
  return null;
}

ColorPixel.toStr = {
  java: (px)=>{
    return px.name+" ("+px.x/px.width+"D, "+px.y/px.height+"D, new Color("+px.color.join(", ")+"), "+px.tolerance+")";
  },
  abs_coords: (px)=>{
    return "["+px.x+", "+px.y+"]";
  },

}

ColorPixel.prototype = new Uint16Array(2);
Object.defineProperty(ColorPixel.prototype, "x", {
  get: function()  {return this[0];},
  set: function(v) {return this[0]=v;}
})
Object.defineProperty(ColorPixel.prototype, "y", {
  get: function()  {return this[1];},
  set: function(v) {return this[1]=v;}
})

ColorPixel.prototype.move = function(x, y) {
  this.x+=x;
  this.y+=y;
}
ColorPixel.prototype.moveRelative = function(x, y) {
  this.x+=x*this.width;
  this.y+=y*this.height;
}
ColorPixel.prototype.toString = function(interpreter) {
  if(typeof interpreter!="function") {
    console.warn("No interpreter provided to convert ColorPixel to string. Default is java.");
    interpreter = ColorPixel.toStr.java;
  }
  return interpreter(this);
}
