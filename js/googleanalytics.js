if(location.href.indexOf("127.0.0.1")==-1 && (typeof SETTINGS=="undefined" || !SETTINGS.debug) && (localStorage==null || localStorage["noga"]==null)) {
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-77379209-1', 'auto');
  ga('send', 'pageview');
  

}
else {
  var ga = window.ga = function() {
    console.log("GOOGLE ANALYTICS", arguments);
  };
}

try {
  
  var link_callback = function() {
    ga('send', 'event', 'outbound', 'click', this.href, {
         'transport': 'beacon',
       });
  };
  onready(function() {
    var links = document.getElementsByTagName("a");
    for(var i=0,l=links.length; i<l; i++) {
      links[i].addEventListener("click", link_callback);
    } 
  });
}
catch(e) {
  console.warn("Failed to add link callbacks.");
}

try {
  var starttime = new Date().getTime();
  window.addEventListener("unload", function() {
    ga('send', 'event', "leave", "unload", location.href, Math.round((new Date().getTime()-starttime)/1000), {transport: 'beacon', nonInteraction: true});
  });
}
catch(e) {
  console.warn("Failed to add link callbacks.");
}

try {
  window.addEventListener("error", function(e) {
    var file = e.filename.substr(e.filename.lastIndexOf("/")+1);
    if(file.length==0)
      file = e.filename;
    var msg = file+":"+e.lineno+":"+e.colno+" \""+e.message+"\" Browser: "+navigator.userAgent;
    
    if(typeof ga=="function") {
      ga('send', 'event', 'jserror', 'debug', msg,
        {transport: 'beacon', nonInteraction: true}
      );
    }
    else {
      console.error(msg);
    }
  });
}
catch(e) {
  console.warn("Failed to add error callback.");
}