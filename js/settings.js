function Settings(superSettings, data) {
  this.parent = superSettings;
  if(superSettings instanceof Settings) {
    this.makeGetters(superSettings.data);
  }
  if(typeof data=="object") {
    this.data = data;
    this.makeGetters(data);
  }
  else
    this.data = {};       
}
Settings.prototype.constructor = Settings;
Settings.prototype.set = function(name, value) {
  this.makeGetter(name);
  return this.data[name] = value;
}

Settings.prototype.get = function(name) {
  if(typeof this.data[name]!="undefined")
    return this.data[name];
  else if(this.parent instanceof Settings)
    return this.parent.get(name);
  else
    return undefined;
}
Settings.prototype.del = function(name) {
  delete this.data[name];
}
Settings.prototype.delAll = function() {
  this.data = {};
}

Settings.prototype.makeGetter = function(name) {
  if(!this.hasOwnProperty(name)) {
    if(typeof this.__proto__[name]=="undefined") {
      Object.defineProperty(this, name, {
        get: function() {
          console.log("Getter "+this.constructor.name+"::get(",name,") with ", this, " returns: ", this.get(name));
          return this.get(name);
        }, 
        set: function(value) {
          return this.set(name, value);
        } 
      });
    }
    else {
      console.warn("Attempt to override prototype property "+name+" in settings.");
    }
  }
} 
Settings.prototype.makeGetters = function(obj) {
  for(var i in obj) {
    if(obj.hasOwnProperty(i)) {
      this.makeGetter(i);
    }
  }
}


function SynchronizedSettings(superSettings, name, settings_names) {
  // super constructor
  Settings.call(this, superSettings);
  
  this.name = name;
  this.reloadSettings();
  window.addEventListener("storage", this.reloadSettings = this.reloadSettings.bind(this));
}
SynchronizedSettings.prototype = Object.create(Settings.prototype);
SynchronizedSettings.prototype.constructor = SynchronizedSettings;

SynchronizedSettings.prototype.reloadSettings = function(e) {
  if((typeof e=="undefined" || e.key == this.name) && localStorage[this.name]) {
    try {
      this.data = JSON.parse(localStorage[this.name]);
      console.log("User settings: ", this.data);
      this.makeGetters(this.data);
    }
    catch(e) {
      console.warn("Error parsing localStorage settings: ",e);
    }
  }
}
SynchronizedSettings.prototype.saveSettings = function() {
  localStorage[this.name] = JSON.stringify(this.data);
}

SynchronizedSettings.prototype.set = function(name, value) {
  this.data[name] = value;
  this.makeGetter(name);
  this.saveSettings();
  return value;
}
Settings.prototype.del = function(name) {
  delete this.data[name];
  this.saveSettings();
}
Settings.prototype.delAll = function() {
  this.data = {};
  this.saveSettings();
}


var DEFAULT_SETTINGS = new Settings(null, {
    debug: false,
    auto_download: true,
    gh_cache_max_age: 60*60*1000
});
var LOCAL_SETTINGS = new SynchronizedSettings(DEFAULT_SETTINGS, "autoclient-settings");
var SETTINGS = LOCAL_SETTINGS;

/**
 * Settings from URL can only overwrite, not add. That prevents XSS hopefully.
**/ 
/*if(location.hash.length>1) {
  try {
    var user_settings = JSON.parse(decodeURIComponent(location.hash.substr(1)));
    console.log("User settings: ", user_settings);
    SETTINGS.addkeys(user_settings, true, false);
  }
  catch(e) {
    console.warn("Error parsing url settings: ",e);
  }
}    */