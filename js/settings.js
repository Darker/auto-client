// localStorage["autoclient-settings"] = JSON.stringify({debug: true});
Object.defineProperty(Object.prototype, "addkeys", {
   value: function(other, replace, create) {
      if(typeof create=="undefined")
        create = true;
      if(typeof replace=="undefined")
        replace = true;
      for(var i in other) {
        var defined = this[i]!="undefined";
        if((defined && replace) || (!defined && create))
          this[i] = other[i];
      }
    },
    enumerable: false
});
var SETTINGS = {debug: false, auto_download: true};
var LOCAL_SETTINGS = {};
var LOCAL_SETTINGS_NAME = "autoclient-settings";
// localStorage["autoclient-settings"] = JSON.stringify({debug: true});
if(localStorage[LOCAL_SETTINGS_NAME]) {
  try {
    var LOCAL_SETTINGS = JSON.parse(localStorage[LOCAL_SETTINGS_NAME]);
    SETTINGS.addkeys(saved_settings, true, true);
  }
  catch(e) {
    console.warn("Error parsing localStorage settings: ",e);
  }
}
function changeLocalSetting(name, value) {
  LOCAL_SETTINGS[name] = value;
  SETTINGS[name] = value;
  localStorage[LOCAL_SETTINGS_NAME] = JSON.stringify(LOCAL_SETTINGS);
}

/**
 * Settings from URL can only overwrite, not add. That prevents XSS hopefully.
**/ 
if(location.hash.length>1) {
  try {
    var user_settings = JSON.parse(decodeURIComponent(location.hash.substr(1)));
    console.log("User settings: ", user_settings);
    SETTINGS.addkeys(user_settings, true, false);
  }
  catch(e) {
    console.warn("Error parsing url settings: ",e);
  }
}