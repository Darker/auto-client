var GITHUB = new GitHubSimple();
function GetLatestReleaseInfo(cb) {
  GITHUB.getURL(SETTINGS.ghapi+"releases/latest", cb);
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