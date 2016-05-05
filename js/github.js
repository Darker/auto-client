function GitHubSimple() {
  this.cache = {};
  if(localStorage["gh-cache"]) {
    try {
      var cache = JSON.parse(localStorage["gh-cache"]);
      for(var i in cache) {
        this.cache[i] = Cache.fromJSON(cache[i]);
        console.log("Cache loaded from storage: ",this.cache[i]);
      }
    }
    catch(e) {console.warn("Error loading cache: "+e);}
  }
  window.addEventListener("unload", ()=>{
    localStorage["gh-cache"] = JSON.stringify(this.cache);
  });
}

GitHubSimple.prototype.getURL = function(url, cb) {
  var cache = this.cache[url];
  if(cache==null)
    cache = this.cache[url] = new Cache(url);
  cache.getData(cb);
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
      .done((result)=>{
          this.data = result;
          this._last_update = new Date().getTime();
          this.loading = false;
          this.emitLoad();
      })
      .fail((result)=>{
          this.data = null;
          this.loading = false;
          console.warn("Failed to load data.");
          this.discardLoad();
      });
  }
}
Cache.prototype.emitLoad = function() {
  this._onload.forEach((x)=>{
    try {
      x(this.data);
    }
    catch(e) {
      if(console)
        console.error(e);
    } 
  });
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

function SimplePromise() {
  
}
SimplePromise.prototype.then = function(cb) {
  this._success = cb;
  return this;
}
SimplePromise.prototype.fail = function(cb) {
  this._fail = cb;
  return this;
}
SimplePromise.prototype.doCallback = function(cbname, arguments) {
  if(typeof this[cbname]=="function")
    cbname.apply(null, arguments);
}
// Default callbacks for convenience
SimplePromise.prototype._success = 
  SimplePromise.prototype._fail = 
  ()=>{};


function time() {
  return new Date().getTime();
}
function dt(t) {
  return time()-t;
}
function cache_is_outdated(x, limit) {
  return x==null || x.outdated(limit);
}
