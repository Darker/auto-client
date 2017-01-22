(function() {
  var tooltip = document.createElement("div");
  //tooltip.style.position = "fixed";
  tooltip.style.position = "fixed";
  //tooltip.style.backgroundColor = "rgba(0,0,0,0.8)";
  //tooltip.style.border = "1px solid black";
  //tooltip.style.color = "white";
  //tooltip.style.minWidth = "100px";
  //tooltip.style.minHeight = "40px";
  tooltip.style.zIndex = "9999999";
  tooltip.style.display = "none";
  
  tooltip.className = "js_tooltip";
  tooltip.innerHTML = "Tooltip";  
  window.TOOLTIP_ELEMENT = tooltip;
  
  //document.body.appendChild(tooltip);
  
  function assignTooltip(element, callback, cache) {
      element.tooltipEvents = {
        "move": function(event){
            showTooltip(event, cache?this.cachedTooltip:(typeof element.tooltipCallback!="function"&&false?callback:callback.apply(element, [event])))
            
        },
        "in":   function(event){this.cachedTooltip=(typeof callback=="string"?callback:callback.apply(element, [event]))},
        "out":  function(event){tooltip.style.display="none";}
      }
      if(typeof element.addEventListener=="function") {
        element.addEventListener("mousemove",element.tooltipEvents.move);
        element.addEventListener("mouseout",element.tooltipEvents.out);
      }   
      else {
        //Nahrada za addEventListener:
        element.onmousemove = element.tooltipEvents.move;
        element.onmouseout = element.tooltipEvents.out;
      }
      //element.setAttribute("onmouseout", element.tooltipEvents.out.toString());
      if(cache==true) {
        element.cachedTooltip=(typeof callback=="string"?callback:callback(element));
        if(typeof element.addEventListener=="function") {
          element.addEventListener("mouseover", element.tooltipEvents['in']);
        }
        else {
          element.onmouseover = element.tooltipEvents['in'];
        }
      }
  }
  HTMLElement.prototype.setTooltip = HTMLElement.prototype.assignTooltip = function(callback, cache) {
    assignTooltip(this, callback, cache);
  }
  window.assignTooltip = assignTooltip;
  
  var lastcontent = null;
  function showTooltip(event, content) {
     //Assign tooltip to the document tree on the first call
     if(tooltip.parentNode==null)
       document.body.appendChild(tooltip);
     if(content==false||content==null||content=="") {
       if(tooltip.style.display!="none")
         tooltip.style.display = "none";
       return;
     }
     var x, y;
     x = event.clientX;
     y = event.clientY;
     
     if(content!=lastcontent) {
       tooltip.innerHTML = content;
       lastcontent = content;
     }
     //Default position relative to mouse
     var tx, ty;
     tx = (x*1+10);
     ty = (y*1+15);
     
     //Show tooltip
     if(tooltip.style.display!="block") {
       tooltip.style.display = "block";
       //tooltip.style.maxWidth = (window.innerWidth)+"px";
     }
     //Cap the position to the window
     //var rect = tooltip.getBoundingClientRect();
     if(tx+tooltip.offsetWidth>(window.innerWidth-17)) {
       tx = (window.innerWidth-tooltip.offsetWidth-17);
     }
     if(ty+tooltip.offsetHeight>(window.innerHeight-17)) {
       ty = (window.innerHeight-tooltip.offsetHeight-17);
     }
     //Set the position
     tooltip.style.top = ty+"px";
     tooltip.style.left = tx+"px";
  }
})();