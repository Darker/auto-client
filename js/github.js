function GitHubSimple() {
  this.cache = {};
  if(localStorage[this.cacheName]) {
    try {
      var cache = JSON.parse(localStorage[this.cacheName]);
      for(var i in cache) {
        this.cache[i] = Cache.fromJSON(cache[i]);
        //console.log("Cache loaded from storage: ",this.cache[i]);
      }
    }
    catch(e) {console.warn("Error loading cache: "+e);}
  }
  window.addEventListener("unload", function(){
    localStorage[this.cacheName] = JSON.stringify(this.cache);
  });
}
GitHubSimple.prototype.cacheName = "gh-cache";
GitHubSimple.prototype.getURL = function(url, cb, max_age) {
  var cache = this.cache[url];
  if(cache==null)
    cache = this.cache[url] = new Cache(url);
  cache.getData(cb, max_age);
}
function Cache(url) {
  this.url = url;
  this._onload = [];
  this.data = null;
}
Cache.prototype.setData = function(data) {
  if(typeof data!="undefined") {
    this.data = data;
    this._last_update = new Date().getTime();
  }
}
Cache.prototype.outdated = function(limit) {
  if(this.data==null)
    return true;
  if(typeof limit!="number" || limit<0)
    limit = 60*60*1000;
  //console.log("Cache age: ",dt(this._last_update), this.url); 
  return dt(this._last_update)>limit;
}
Cache.prototype.onload = function(cb) {
  if(typeof cb=="function") {
    this._onload.push(cb);
  }
}
Cache.prototype.getData = function(cb, outdateLimit) {
  this.onload(cb);
  if(this.outdated(outdateLimit)) {
    console.info("Cache "+this.url+" outdated.")
    this.loadData();
  }
  else {
    this.emitLoad();
  }
}
Cache.prototype.loadData = function(cb) {
  this.onload(cb);
  if(!this.loading) {
    this.loading = true;
    $.getJSON(this.url)
      .done(function(result){
          this.data = result;
          this._last_update = new Date().getTime();
          this.loading = false;
          this.emitLoad();
      }.bind(this))
      .fail(function(result){
          this.data = null;
          this.loading = false;
          console.warn("Failed to load data.");
          this.discardLoad();
      }.bind(this));
  }
}
Cache.prototype.emitLoad = function() {
  this._onload.forEach(function(x){
    try {
      x(this.data);
    }
    catch(e) {
      if(console)
        console.error(e);
    } 
  }, this);
  this._onload = [];
}
Cache.prototype.discardLoad = function() {
  this._onload = [];
}
Cache.prototype.toJSON = function() {
  return {data: this.data, timestamp: this._last_update, url: this.url};
}
Cache.prototype._last_update = 0;

Cache.fromJSON = function(x) {
  var c = new Cache(x.url);
  c._last_update = x.timestamp;
  c.data = x.data;
  return c;
}



function time() {
  return new Date().getTime();
}
function dt(t) {
  return time()-t;
}
function cache_is_outdated(x, limit) {
  return x==null || x.outdated(limit);
}


function Tag(text) {
  var matches = text.match(/^v([0-9](?:[0-9\.+]*[0-9])?)(?:\-([a-zA-Z0-9\-]+))?\s*$/);
  var numbers = matches[1].split(".");
  for(var i=0,l=numbers.length; i<l; i++)
    numbers[i]*=1;
  var suffix = matches[2] || "";
  
  this.numbers = numbers;
  this.suffix = suffix;
  this.beta = suffix.indexOf("beta")!=-1;
}
Tag.prototype.compare = function(tag) {
  //if(tag.numbers.length>this.numbers.length)
  //  return !tag.compare(this);
  var n = this.numbers;
  var n2 = tag.numbers;
  console.log("Compare ", n, n2);
  for(var i=0, l=Math.max(n.length, n2.length);i<l; i++) {
    // this is equal but has aditional numbers
    if(i>=n2.length)
      return 1;
    // this is equal but other tag has more numbers
    if(i>=n.length)
      return -1;
    if(n[i]!=n2[i])
      return n[i]-n2[i];
  }
  return 0;
}
Tag.prototype.newer = function(tag) {
  return this.compare(tag)>=0?this:tag;
}
Tag.prototype.toString = function() {
  if(this.suffix.length>0)
    return this.numbers.join(".") + "-" + this.suffix;
  else
    return this.numbers.join(".");
}
Tag.prototype.toJSON = function() {
  return this.toString();
}