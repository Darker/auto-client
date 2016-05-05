var cache = {releases:{latest: {_last_update: 0}, all: {_last_update: 0}}};
function GetLatestReleaseInfo(cb) {
    if(cache_is_outdated(cache.releases.latest)) {
      $.getJSON(SETTINGS.ghapi+"releases/latest").done(function (release) {
          cache.releases.latest = release;
          cache.releases.latest._last_update = new Date().getTime();
          cb(release);
      });
    }
    else
      cb(cache.releases.latest);
}
function time() {
  return new Date().getTime();
}
function dt(t) {
  return time()-t;
}
function cache_is_outdated(x, limit) {
  if(typeof limit!="number")
    limit = 60*60*1000;
  return x==null || x._last_update==null || dt(x._last_update)>limit;
}

function timeAgo(timestamp) {
  var second = 1000;
  var minute = 60 * second;
  var hour = 60*minute;
  var day = 24*hour;
  

  var dateDiff = new Date() - new Date(timestamp);
  var timeAgo = "never";
  if (dateDiff < 5*minute) {
      timeAgo = "just now"; 
  }
  else if (dateDiff < hour) {
      timeAgo = Math.round(dateDiff/minute)+" minutes ago"; 
  }
  else if (dateDiff < day) {
      var diff = Math.round(dateDiff/hour);
      timeAgo = diff+" hour"+(diff>1?"s":"")+" ago"; 
  }
  else
  {
      var diff = Math.round(dateDiff/day);
      timeAgo = diff+" day"+(diff>1?"s":"")+" ago"; 
  }
  return timeAgo;
}


/** unrelated shit **/

// Our countdown plugin takes a callback, a duration, and an optional message
$.fn.countdown = function (callback, duration, message) {
    // If no message is provided, we use an empty string
    message = message || "";
    // Get reference to container, and set initial content
    var container = $(this[0]).html(duration + message);
    // Get reference to the interval doing the countdown
    var countdown = setInterval(function () {
        // If seconds remain
        if (--duration) {
            // Update our container's message
            container.html(duration + message);
        // Otherwise
        } else {
            // Clear the countdown interval
            clearInterval(countdown);
            // And fire the callback passing our container as `this`
            callback.call(container);   
        }
    // Run interval every 1000ms (1 second)
    }, 1000);

};

function distribute_text(text) {
  for(var i in text) {
    $("."+i).text(text[i]);
  }
}