if(location.href.indexOf("127.0.0.1")==-1 && !SETTINGS.debug) {
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-77379209-1', 'auto');
  ga('send', 'pageview');
  
  $("a").click(function() {
    ga('send', 'event', 'outbound', 'click', this.href, {
         'transport': 'beacon',
         //'hitCallback': function(){document.location = url;}
       });
  });
}