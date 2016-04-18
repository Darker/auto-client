define([], ()=>{
  /** A simple class to store images that the user entered **/
  function ImageManager() {
    this.db = {};
  }
  
  ImageManager.prototype.getImg = function(name) {
    return this.db[name];
  }
  ImageManager.prototype.setImg = function(name, img) {
    return this.db[name] = img;
    draw();
  }
  
  var clipboardIndex = -1;
  ImageManager.prototype.clipboardImg = function(img) {
    return this.db["Clipboard "+(++clipboardIndex)] = img;
    draw();
  }
  ImageManager.prototype.removeImg = function(name) {
    delete this.db[name];
    draw();
  }
  
  ImageManager.prototype.draw = function() {
    if(this.elm!=null) {
      this.elm.innerHTML = "";
      this.elm.className = "image_list";
      for(var i in this.db) {
        if(this.db.hasOwnProperty(i)) {
          var row = document.createElement("div");
          row.className = "row";
          var name = document.createElement("span");
          name.appendChild(new Text(i));
          name.className = "name";
          var del = document.createElement("span");
          del.appendChild(new Text("X"));
          del.className = "delete";
          row.appendChild(name);
          row.appendChild(del);
        }
      } 
    }
  }
}
