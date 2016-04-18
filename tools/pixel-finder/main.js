requirejs.config({
    //By default load any module IDs from js/lib
    baseUrl: '.',
    //except, if the module ID starts with "app",
    //load it from the js/app directory. paths
    //config is relative to the baseUrl, and
    //never includes a ".js" extension since
    //the paths config could be for a directory.
    paths: {
        jquery: [
            '//code.jquery.com/jquery-2.1.4.min',
            //If the CDN location fails, load from this location
            'jquery'
        ],
        "socket.io": [
            "//cdn.socket.io/socket.io-1.3.7",
            "socket.io.backup"
        ],
        codemirror: [
            "//cdnjs.cloudflare.com/ajax/libs/codemirror/5.13.4"
        ]
    },
    waitSeconds: 20   
});


requirejs(["ColorPixel", "TestImage"], (ColorPixel, TestImage)=>{
  var ORIGINAL = document.getElementById("target");
  function DisplayImage(img) {
    //console.log("Display image: ", img);
    var ctx = ORIGINAL.getContext('2d');
    var width = img.width;
    var height = img.height;
    ORIGINAL.width = width;
    ORIGINAL.height = height;
    //Copy given image on canvas
    ctx.drawImage(img,0,0);
    //Display canvas and hide everything else
    document.getElementById("select_file").style.display = "none";
    document.getElementById("displayed_image").style.display = "";
    setUpEvents(ORIGINAL);
    
    //Set up HUD
    var hud = document.getElementById("hud");
    hud.width = width;
    hud.height = height;
  }
  GetImageAPI.setDropElement(document.body, DisplayImage);
  GetImageAPI.setPasteElement(document, DisplayImage);
  
  //Set up the tools on right
  var tools = document.getElementById("gui").getElementsByClassName("tool");
  for(var i=0,l=tools.length; i<l; i++) {
    var head = tools[i].getElementsByClassName("head")[0];
    head.addEventListener("click", selectTool);
  }
  /**Event functions**/
  function selectTool() {
    this.parentNode.className = this.parentNode.className.indexOf("active")==-1?"tool active":"tool";
  }
  
  var IMAGE = new TestImage(document.getElementById("target"), document.getElementById("hud"));
  
  
  function moveTestPointRelative() {
    var xo = document.getElementById('x_offset').value*1;
    var yo = document.getElementById('y_offset').value*1;
    
    var canvas = document.getElementById("target");
  
    var test_field = document.getElementById("point_test");
    var px = ColorPixel.fromStringJava(test_field.value, canvas.width, canvas.height);
    px.moveRelative(xo, yo);
    test_field.value = px.toString(ColorPixel.toStr.java);
    
    IMAGE.drawMarker(px);
    
  }
  
  function setUpEvents(ORIGINAL) {
    var div = ORIGINAL.parentNode;
    div.setTooltip(function(event) {
      var pos = ORIGINAL.relativeCoords(event);
      var width = ORIGINAL.width;
      var height = ORIGINAL.height;
      var pos_percent = [(pos[0]/width)*100, (pos[1]/height)*100];
      var str = "<table class=\"tooltipTable\">"+
                "<tr><th>Left</th><th>Top</th></tr>"+
                "<tr><td>"+Math.round(pos[0])+"px</td><td>"+Math.round(pos[1])+"px</td></tr>"+
                "<tr><td>"+Math.round(pos_percent[0])+"%</td><td>"+Math.round(pos_percent[1])+"%</td></tr>";
      if(window.last) {
        var c = window.last;
        var dist = Math.round(Math.sqrt((pos[0]-c[0])*(pos[0]-c[0])+(pos[1]-c[1])*(pos[1]-c[1])));
        str+=  "<tr><th colspan=\"2\">Distance</th></tr>"+
               "<tr><td colspan=\"2\" style=\"text-align:center\">"+dist+"px</td></tr>"+
               "<tr><td>"+Math.round(pos[0]-c[0])+"px</td><td>"+Math.round(pos[1]-c[1])+"px</td></tr>";
      }
      return str+
             "</table>";
    });
    div.addEventListener("click", function(event) {
      var pos = ORIGINAL.relativeCoords(event);
      var width = ORIGINAL.width;
      var height = ORIGINAL.height;
      //var pos_percent = [(pos[0]/width), (pos[1]/height)];
      //Get color
  
      var pos = new ColorPixel(pos[0], pos[1], IMAGE.getCurrentColor(pos[0], pos[1]), width, height);
      
      IMAGE.drawMarker(pos);
      printPoint(pos); 
      window.last = pos;
  
    });
    document.getElementById("last_point").addEventListener("input", function() {
      var point = ColorPixel.fromStringJava(this.value, ORIGINAL.width, ORIGINAL.height);
  
      if(point) {
        //Render color
        if(!point.color) {
          point.color = IMAGE.getCurrentColor(ORIGINAL, point.x, point.y);
        }
        //Update value  
        this.value = point.toString(ColorPixel.toStr.java);
        //Mark position
        IMAGE.drawMarker(point);
      }
      else
        console.log("No match");
    });
    document.getElementById("point_test_button").addEventListener("click", function() {
      drawTestPixel(document.getElementById("point_test").value);
    });
  }
  function drawTestPixel(test) {
    var pixel = ColorPixel.fromStringJava(test, IMAGE.width, IMAGE.height);
    var result = IMAGE.testPixel(pixel);
    console.log(result);
    if(result!=null) {
      var res_container = document.getElementById("point_test_results");
      var text = "<span class=\"tolerance "+(result.max_diff>pixel.tolerance?"error":"ok")+"\">Required tolerance: "+result.max_diff+"</span>";
      text+="\nDifferences: [" +result.differences.join(", ")+"]";
      res_container.innerHTML = text;
      //Show this points color
      document.getElementById("point_test").style.backgroundColor = TestImage.makeCSSColor(result.expectedColor);
      //Update image
      IMAGE.drawMarker(result.pixel);
      //Update color field
      printPoint();
    }
    else
      throw new Error("Invalid pixel: '"+test+"'");
  }
  
  function printPoint(pixel) {
    if(pixel==null) {
      pixel = window.last;
    }
    if(pixel==null)
      return;
    document.getElementById("last_point").value = pixel.toString(ColorPixel.toStr.java);
  }
  function updateColor(pixel) {
    var lastColor = document.getElementById("last_color");
    lastColor.value = "new Color("+pixel.color.join(", ")+")";
    lastColor.style.borderColor = TestImage.makeCSSColor(pixel.color);
  }
  function moveRelative(x, y) {
    var px = window.last;
    console.log("Before move: "+px.toString(ColorPixel.toStr.abs_coords), x+"*"+IMAGE.width, y+"*"+IMAGE.height);
    px.move(x*IMAGE.width, y*IMAGE.height);
    console.log("After move: " +px.toString(ColorPixel.toStr.abs_coords));
  
    px.color = IMAGE.getCurrentColor(px.x, px.y);
    IMAGE.drawMarker(window.last);
    printPoint();
  }
});    




    




/* Returns pixel coordinates according to the pixel that's under the mouse cursor**/
HTMLCanvasElement.prototype.relativeCoords = function(event) {
  var x,y;
  //This is the current screen rectangle of canvas
  var rect = this.getBoundingClientRect();

  //Recalculate mouse offsets to relative offsets
  x = event.clientX - rect.left;
  y = event.clientY - rect.top;
  //Also recalculate offsets if canvas is stretched
  var width = rect.right - rect.left;
  //I use this to reduce number of calculations for images that have normal size 
  if(this.width!=width) {
    var height = rect.bottom - rect.top;
    //changes coordinates by ratio
    x = x*(this.width/width);
    y = y*(this.height/height);
  } 
  //Return as an array
  return [x,y];
}