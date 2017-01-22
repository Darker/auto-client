define(['ColorPixel'], (ColorPixel)=>{
  function TestImage(canvas, overlay) {
    this.canvas = canvas;
    this.ctx = canvas.getContext('2d');
  
    this.overlay = overlay;
    this.octx = overlay.getContext('2d');
  }
  TestImage.prototype.getCurrentColor = function (x, y) {
    if(x instanceof ColorPixel) {
      y = x.y;
      x = x.x;
    }
    x = Math.round(x);
    y = Math.round(y);
    
    var c = this.ctx;
    var p = null;
    try {
      var p = c.getImageData(x, y, 1, 1).data.toArray(); 
    }
    catch(e) {
      throw new Error("No data at ["+[x, y].join(", ")+"].");
    }
    if(p==null)
      throw new Error("No data at ["+[x, y].join(", ")+"].");
    if(p.length>3)
      p[3] = p[3]/255;
    return p;
  }
  TestImage.prototype.drawMarker = function(x, y, color) {
    if(x instanceof ColorPixel) {
      y = x.y;
      color = x.color;
      x = x.x;
    }  
    console.log("Draw mark at ["+x+", "+y+"].");
    var csscolor = TestImage.makeCSSColor(color);
    var ctx = this.octx;
    ctx.restore();
    
    var width = this.overlay.width;
    var height = this.overlay.height;
    
    ctx.clearRect(0,0, width, height);
    //ctx.translate(0.5,0.5);
    ctx.lineWidth = 1;
    bicoloredLine(ctx, [0+0.5, y+0.5], [width+0.5,  y+0.5], "white", "black", 2);
    bicoloredLine(ctx, [x, 0], [x, height], "white", "black", 2);
    
    //Print the color
    var position = x>25 && y>25?[x-20, y-20] : [x+5, y+5];
    bicoloredRect(ctx, position, [15,15], "white", "black", csscolor, 1);
  
  }
  TestImage.prototype.testPixel = function(pixel) {
  
    console.log("Testing point ", pixel.toString(ColorPixel.toStr.abs_coords), " with color ",pixel.color, pixel, ColorPixel.toStr.abs_coords, typeof ColorPixel.toStr.abs_coords!="function");
  
    var p = this.getCurrentColor(pixel.x, pixel.y); 
    //console.log("Color at ",coords," is ",p);
    //Try to match
    var diff = [255,255,255];
    var color = pixel.color;
    for(var i=0; i<3; i++) {
      diff[i] = Math.abs(color[i]-p[i]);
    }
    
    return {
      differences: diff,
      max_diff: Math.max.apply(Math, diff),
      min_diff: Math.min.apply(Math, diff),
      position: [pixel.x, pixel.y],
      expectedColor: color,
      realColor: p,
      pixel: pixel
    }
  }
  
  Object.defineProperty(TestImage.prototype, "width", {
    get: function() {return this.canvas.width;}
  });
  Object.defineProperty(TestImage.prototype, "height", {
    get: function() {return this.canvas.height;}
  });
  
  TestImage.makeCSSColor = function(color) {
    return "rgb"+(color.length==4?"a":"")+"("+color.join(", ")+")";
  }
  Uint8ClampedArray.prototype.toArray = function() {
    var r = [];
    for(var i=0, l=this.length; i<l; i++)
      r.push(this[i]);
    return r;    
  }
  
  
  /** Cretes two-color line using two dashed lines.  
   @param ctx canvas context to draw on
   @param A starting point in format [x1, y1]
   @param B end point in format [x2, y2]
   @param color1 CSS3 string representation of the first color
   @param color2 CSS3 string representation of the second color
   @param fragmentLength length of consequent color fragment
  **/
  
  function bicoloredLine(ctx, A, B, color1, color2, fragmentLength) {
    //Not to mess up with the ctx, we save it here and restore before execution of this
    //function is over
    ctx.save();
  
    ctx.strokeStyle = color1;
    ctx.setLineDash([fragmentLength, fragmentLength]);
    //First dashed line
    ctx.beginPath();
    ctx.moveTo(A[0], A[1]);
    ctx.lineTo(B[0], B[1]);
    ctx.stroke();
    //Move the dash offset by the length of the fragment
    //That will swap non-drawn areas with drawn areas
    ctx.lineDashOffset = fragmentLength+1;
    ctx.strokeStyle = color2;
    ctx.beginPath();
    ctx.moveTo(A[0], A[1]);
    ctx.lineTo(B[0], B[1]);
    ctx.stroke();
    //ctx.closePath();
    //Restore back original ctx settings
    ctx.restore();
  }
  /** Cretes two-color line using two dashed lines.  
   @param ctx canvas context to draw on
   @param A top left point in format [x1, y1]
   @param size dimensions as [width, height]
   @param color1 CSS3 string representation of the first color
   @param color2 CSS3 string representation of the second color
   @param color2 CSS3 string representation of the fill color
   @param fragmentLength length of consequent color fragment
  **/
  
  function bicoloredRect(ctx, S, size, color1, color2, fillColor, fragmentLength) {
    //Not to mess up with the ctx, we save it here and restore before execution of this
    //function is over
    ctx.save();
  
    ctx.strokeStyle = color1;
    ctx.fillStyle = fillColor;
    ctx.lineWidth = 2;
    ctx.setLineDash([fragmentLength, fragmentLength]);
    ctx.strokeRect(S[0],S[1],size[0],size[1]);
  
    ctx.lineDashOffset = fragmentLength+1;
    ctx.strokeStyle = color2;
    ctx.fillRect(S[0],S[1],size[0],size[1]);
    ctx.strokeRect(S[0],S[1],size[0],size[1]);
    //ctx.fill();
    //ctx.stroke();
    //ctx.closePath();
    
    //Restore back original ctx settings
    ctx.restore();
  }
  
  return TestImage;
});