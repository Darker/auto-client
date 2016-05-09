if(location.href.indexOf("127.0.0.1")==-1 && (typeof SETTINGS=="undefined" || !SETTINGS.debug)) {
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-77379209-1', 'auto');
  ga('send', 'pageview');
  
  try {
    var links = document.getElementsByTagName("a");
    var link_callback = function() {
      ga('send', 'event', 'outbound', 'click', this.href, {
           'transport': 'beacon',
           //'hitCallback': function(){document.location = url;}
         });
    };
    for(var i=0,l=links.length; i<l; i++) {
      links[i].addEventListener(link_callback);
    } 
  }
  catch(e) {
    console.warn("Failed to add link callbacks.");
  }
}
try {
  window.addEventListener("error", function(e) {
    var file = e.filename.substr(e.filename.lastIndexOf("/")+1);
    if(file.length==0)
      file = e.filename;
    var msg = file+":"+e.lineno+":"+e.colno+" \""+e.message+"\" Browser: "+navigator.userAgent;
    
    if(typeof ga=="function") {
      ga('send', 'event', 'jserror', 'click', msg, {'transport': 'beacon'});
    }
    else {
      console.error(msg);
    }
  });
}
catch(e) {
  console.warn("Failed to add error callback.");
}