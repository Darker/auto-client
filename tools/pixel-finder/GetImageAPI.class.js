    /***********************************************/
    /*********** GETTING THE IMAGE *****************/
    /***********************************************/
function GetImageAPI() {
    // window.addEventListener('paste', ... or

    //When dropping the file

    
    

    
    
    /*document.addEventListener("paste", function() {
      document.getElementById("paste").focus();
      //console.log("Before paste");
    });*/
    

    
    function saveImage(canvas) {
      	var b = canvas.toBlob(function(blob) { saveAs(blob, "transformed.png");});
    }
}
GetImageAPI.setPasteElement = function(element, pastecallback, ignoreinputs) {
  if(typeof element=="string")
    element = document.getElementById(element);
  if(!(element instanceof HTMLElement) && !(element instanceof HTMLDocument)) 
    throw new Error("HTMLElement object or valid id string required for GetImageAPI::setPasteElement.");
  //Create contenteditable div that catches pasted images and image data
  var PASTELEMENT = GetImageAPI.createPasteCatcher(pastecallback);
  //When pasting, focus on the PASTE element
  if(ignoreinputs!=true) {
    element.addEventListener("keydown", function(e) {
     
      //Check if the target isn't some kind of writeable element, such as input or textarea
      //NOT checking for contenteditable elements as of now
      //Obviously, if e.target is the element this function uses, it's not checked
      if(e.target && e.target!=element) {
        var tagName = e.target.tagName.toLowerCase();
        if(tagName=="input"||tagName=="textarea")
          return true;
      }
      //CTRL+V causes the paste element to be focused
      if(e.keyCode==86&&e.ctrlKey) {
        PASTELEMENT.focus();
        //console.log(PASTELEMENT, document.activeElement);
      }
    });
  }
  else {
    //In this case, even input fields will trigger the event
    element.addEventListener("keydown", function(e) {
      if(e.keyCode==86&&e.ctrlKey) {
        PASTELEMENT.focus();
      }
    });
  }
  document.body.appendChild(PASTELEMENT);
}
GetImageAPI.createPasteCatcher = function(pastecallback) {
  var catcher = document.createElement("div");
  catcher.setAttribute("contenteditable", "true");
  /*catcher.setAttribute("type", "text");*/
  catcher.style.position = "absolute";
  catcher.style.top  = "-666px";
  catcher.style.left = "-666px";  
  catcher.style.border = "1px solid red";
  catcher.style.width  = "5px";
  catcher.style.height = "5px";
  catcher.style.overflow = "hidden";/* */
  
  catcher.addEventListener('paste', function(event){
    //I'm not even sure if this returns the same object
    var items = (event.clipboardData || event.originalEvent.clipboardData);

    /**Try to get a file**/
    var files = items.items || items.files;
    //Just simply getting first file right now
    if(files.length>0) {
      event.preventDefault();
      event.cancelBubble = true;
      //Read the file
      GetImageAPI.applyFile(files[0].getAsFile? files[0].getAsFile():files[0], pastecallback);
      return false;
    }
    else
      console.log("No files in clipboard.");   
  });
  //This is where we catch out HTML images
  catcher.addEventListener('input', function(event) {
    var images = this.getElementsByTagName("img");
    if(images.length!=0) {
      //console.log("Image found!");
      var im = images[0];
      //Not loaded - wat until it loads
      if(im.width==0||im.height==0) {
        //console.log("Waiting for onload.");
        im.onload = function() {
          //console.log("Image loaded (event), applying.");
          GetImageAPI.applyImage(this, pastecallback)
        };
      }
      else {
        //console.log("Image loaded, applying.");
        GetImageAPI.applyImage(im, pastecallback);  
      }
    }
    else {
      //console.log("No image in HTML.");
    }  
    //Reset the HTML every time
    this.innerHTML = "";
    //NOTE: the eventual pasting to text fields must be, unfortunatelly, reinvented somehow
    //innerHTML will sometimes contain unwanted data, but you can use it if you feel lazy
  });
  return catcher;
}
GetImageAPI.setDropElement = function(element, callback) {
  if(typeof element=="string")
    element = document.getElementById(element);
  if(!(element instanceof HTMLElement) && !(element instanceof HTMLDocument)) 
    throw new Error("HTMLElement object or valid id string required for GetImageAPI::setDropElement.");
  element.addEventListener("drop", 
    function(e) {
      //document.getElementById("dropzone").style.display = "none";
      //console.log(e.dataTransfer);
      if(e.dataTransfer && e.dataTransfer.files.length>0) {
        var files = e.dataTransfer.files;
        for(var i=0,l=files.length; i<l; i++)
          if(files[i].type.indexOf("image")==0)
            GetImageAPI.applyFile(files[i], callback);    
      }
      e.preventDefault();
      return true;
    }
  );
  
  document.body.addEventListener("dragover", GetImageAPI.FileDragHoverCancelAllow, false);
  document.body.addEventListener("dragleave", GetImageAPI.FileDragHoverCancelAllow, false);
}
GetImageAPI.FileDragHoverCancelAllow = function FileDragHover(e) {
	e.stopPropagation();
	e.preventDefault();
	/*if(e.target&&e.target.style)
  	e.target.style.background = (e.type == "dragover" ? "red" : ""); */
	//document.getElementById("dropzone").style.display = (e.type == "dragover" ? "" : "none")
	return true;
}
GetImageAPI.applyImage = function(img, callback) {
  if(typeof callback=="function")
    callback(img);
  /*var ctx = ORIGINAL.getContext('2d');
  var width = img.width;
  var height = img.height;
  ORIGINAL.width = width;
  ORIGINAL.height = height;
  //Copy given image on canvas
  ctx.drawImage(img,0,0);
  /*if(width>800||height>800) {
    if(confirm("Image is quite big. Do you want to resize it before processing?"))
      canvasResizeTo(800,800,ORIGINAL);
  
  }   */   
}
GetImageAPI.applyFile = function(file, callback) {
  var reader = new FileReader();
  reader.onload = function(event) {
    var img = new Image;
    img.onload = function() {
      GetImageAPI.applyImage(this, callback);
    };
    img.src = event.target.result;
  }; // data url!
  reader.readAsDataURL(file);
}