var GITHUB = new GitHubSimple();
function GetLatestReleaseInfo(cb) {
  GITHUB.getURL(SETTINGS.ghapi+"releases/latest", cb, SETTINGS.gh_cache_max_age);
}
function GetLatestReleaseFromAll(cb, betas) {

  GITHUB.getURL(SETTINGS.ghapi+"releases", FindLatestAsync, SETTINGS.gh_cache_max_age);
  function FindLatestAsync(releases) {
    cb(FindLatest(releases, betas));
  }
}


function FindLatest(releases, betas) {
  var latest = new Tag("v0");
  var latest_release = null;
  for(var i=0,l=releases.length; i<l; i++) {
    var r = releases[i];
    console.log("Comparing ", r, latest_release);
    var tag = new Tag(r.tag_name);
    if(tag.beta && !betas)
      continue;
    
    if(tag.compare(latest)>0) {
      latest = tag;
      latest_release = r;
    }
  }
  return latest_release;
}

function clearcache() {
  GITHUB.cache = {};
  localStorage.removeItem(GITHUB.cacheName);
}


if(typeof String.prototype.endsWith!="function") {
  String.prototype.endsWith = function(suffix) {
      return this.indexOf(suffix, this.length - suffix.length) !== -1;
  };
}

function findFirstZipAsset(assets) {
  for(var i=0,l=assets.length; i<l; i++) {
    if(assets[i].name.endsWith(".zip")) {
      return assets[i];
    }
  }
  return null;
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

// Our countdown plugin takes a duration, and an optional message
$.fn.countdown = function (duration, message) {
    // If no message is provided, we use an empty string
    message = message || "";
    // Get reference to container, and set initial content
    var container = $(this[0]).html(duration + message);
    // Control of the coundown
    var control = new CountdownControl(null, container);
    // Get reference to the interval doing the countdown
    var countdown = setInterval(function () {
        if(!control.enabled)
          return;
        // If seconds remain
        if (--duration) {
            // Update our container's message
            container.html(duration + message);
        // Otherwise
        } else {
            // Clear the countdown interval
            clearInterval(countdown);
            // And fire the callback passing our container as `this`
            control.success();
        }
    // Run interval every 1000ms (1 second)
    }, 1000);
    control.id = countdown;
    return control;
};
function CountdownControl(id, element) {
  if(typeof id=="number") {
    this.id = id;
  }
  this.enabled = true;
  this.element = element;
}
CountdownControl.prototype.id = null;
CountdownControl.prototype._done = 
  CountdownControl.prototype._fail =
  CountdownControl.prototype._always =
  function (){};

CountdownControl.prototype.stop = function() {
  if(this.id) {
    clearInterval(this.id);
    this.id = null;
    this.enabled = false;
    this._fail.call(this.element);
    this._always.call(this.element);
  }
}
CountdownControl.prototype.success = function() {
  if(this.id) {
    clearInterval(this.id);
    this.id = null;
    this.enabled = false;
  }
  this._done.call(this.element);
  this._always.call(this.element);
}

CountdownControl.prototype.done = function(f) {
  this._done = f;
  return this;
}
CountdownControl.prototype.fail = function(f) {
  this._fail = f;
  return this;
}
CountdownControl.prototype.always = function(f) {
  this._always = f;
  return this;
}

function distribute_text(text) {
  for(var i in text) {
    $("."+i).text(text[i]);
  }
}