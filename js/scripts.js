function Script(source) {
  this.execute = this.execute.bind(this);
  this.parse(source);
}
Script.commands = {
  s: CommandSay,
  d: CommandDelay
}
Script.parseCommand = function(str) {
  return Script.arrayToCommand(splitByUnescaped(str, ","));
}
Script.arrayToCommand = function(arr) {
  var command_ctor = Script.commands[arr[0]]||Command;
  return new command_ctor(arr);
}

Script.prototype.parse = function(data) {
  if(data.indexOf("S>")!==0)
    throw new Error("Script must begin with S>");
  this.commands = [];
  // Split array contains the ampersands, eg:
  // ["ddd,d", ";", "aaa,a,a", ";"]
  // so I iterate by 2 not one
  var commandsStr = splitByUnescaped(data.substr(2), ";");
  for(var i=0,l=commandsStr.length; i<l; i+=1) {
    this.commands.push(Script.parseCommand(commandsStr[i]));
  }
}
Script.prototype.addCommand = function(command) {
  if(command instanceof Command) 
    this.commands.push(command);
  else if(typeof command=="string")
    this.commands.push(command=Script.parseCommand(command));
  else
    throw new Error("Invalid command!");
  this.addCommandToHtml(command, this.commands.length-1);
}


Script.prototype.execute = function(finalCallback, index) {
  if(typeof index!="number")
    index = 0;
  console.log("EXEC: ", index);
  if(index<this.commands.length) {
    this.commands[index].exec(this.execute, finalCallback, index+1);    
  }
  else {
    finalCallback();
  }
}
Script.prototype.html = function() {
  if(!this.html_) {
    var main = this.html_ = document.createElement("div");
    main.className = "script";
    main.appendChild(this.html_end = elmWithText("div", "", "end"));
    
    
    for(var i=0,l=this.commands.length; i<l; i++) {
      var html = this.commands[i].html();
      html.setAttribute("x-command-index", i);
      main.insertBefore(html, this.html_end);
    }
  }
  return this.html_;
}
Script.prototype.addCommandToHtml = function(command, index) {
  if(this.html_) {
    var html = command.html();
    if(index>0 && index<this.commands.length)
      html.setAttribute("x-command-index", index);
    this.html_.insertBefore(html, this.html_end);
  }
}
Script.prototype.toString = function() {
  return "S>"+this.commands.join(";");
}


function Command(params) {
  console.log("Command(", params,")");
  this.name = params[0];
  this.args = [];
  for(var i=1; i<params.length; i+=1) {
    this.args.push(params[i]);
  }
}
Command.prototype.exec = function(cb) {
  console.warn("Unknown command ",this.name,".");
  if(typeof cb=="function")
    cb.apply(this, Array.prototype.slice.call(arguments, 1));
}
Command.prototype.toString = function() {
  if(this.args.length>0)
    return this.name+","+this.args.join(",");
  else
    return this.name;
}
Command.prototype.html = function() {
  if(!this.html_main) {
    var main = this.html_main = document.createElement("div");
    main.className = "script-command";
    
    var name = document.createElement("div");
    name.className = "heading";
    name.appendChild(elmWithText("div", this.name, "name"));
    if(this.title!=null && this.title.length>0) 
      name.appendChild(elmWithText("div", this.title, "title"));
    else
      name.appendChild(elmWithText("div", "Unknown command", "title error"));
    main.appendChild(name);
    
    main.appendChild(document.createElement("hr"));
    
    var args = document.createElement("div");
    args.className = "arguments";
    main.appendChild(this.html_arguments(args));
  }
  return this.html_main;
}
Command.prototype.html_arguments = function(container) {  
  for(var i=0,l=this.args.length; i<l; i++) {
    var span = document.createElement("span");
    span.className = "argument";
    span.appendChild(new Text(this.args[i]));
    container.appendChild(span);
  }
  return container;
}
Command.namedArgHTML = function(argname, val) {
  var main = document.createElement("span");
  main.className = "argument named";
  
  var name = document.createElement("span")
  name.className = "arg-name";
  name.appendChild(new Text(argname));
  
  var value = document.createElement("span");
  value.className = "arg-value";
  if(typeof val=="number")
    value.className+=" number";
  else if(typeof val=="string") {
    value.className+=" string";
    val = "\""+val+"\"";
  }
  value.appendChild(new Text(val));
  
  main.appendChild(name);
  main.appendChild(value);
  return main;
}

function CommandSay() {
  Command.apply(this, arguments);
  
  this.text = this.args[0]||"";
  this.repeat = this.args[1]*1>0?this.args[1]*1:1;
  this.delay = this.args[2]*1>0?this.args[2]*1:500;
}
CommandSay.prototype = Object.create(Command.prototype);
CommandSay.prototype.constructor = CommandSay;
CommandSay.prototype.title = "Says text in chat."; 

CommandSay.prototype.exec = function(cb) {
  var cbArgs = Array.prototype.slice.call(arguments, 1);
  var repeat = this.repeat;
  
  var interval = setInterval(function() {
    if(repeat>0) {
      console.log("CHAT: ", this.text);
      repeat--;    
    }
    if(repeat<=0) {
      clearInterval(interval);
      cb.apply(this, cbArgs);
    }
  }.bind(this), this.delay);
}
CommandSay.prototype.html_arguments = function(container) {  
  container.appendChild(Command.namedArgHTML("Text", this.text));
  container.appendChild(Command.namedArgHTML("Delay", this.delay));
  container.appendChild(Command.namedArgHTML("Repeat count", this.repeat));
  return container;
}


function CommandDelay() {
  Command.apply(this, arguments);
  this.delay = this.args[0]*1>0?this.args[0]*1:500;
}
CommandDelay.prototype = Object.create(Command.prototype);
CommandDelay.constructor = CommandDelay;
CommandDelay.prototype.title = "Sleep command."; 
CommandDelay.prototype.exec = function(cb) {
  var cbArgs = Array.prototype.slice.call(arguments, 1);
  var _this = this;
  setTimeout(function() {
    cb.apply(this, cbArgs);
  }, this.delay);
}
CommandDelay.prototype.html_arguments = function(container) {  
  container.appendChild(Command.namedArgHTML("Delay", this.delay));
  return container;
}


function ScriptDbEntry(script, name, comment) {
  this.update(script, name, comment);
}
ScriptDbEntry.prototype.update = function(script, name, comment) {
  this.script = script;
  this.name = name||"unnamed";
  this.comment = comment||"";
  
  if(this.html_) {
    this.html_name.data = this.name;
    this.html_comment.data = this.comment;
    this.html_script.data = this.script.toString();
  }
}

ScriptDbEntry.prototype.toJSON = function() {
  return {name:this.name, comment:this.comment, script: this.script.toString()};
}

ScriptDbEntry.prototype.html = function() {
  if(!this.html_) {
    this.html_ = document.createElement("tr");
    var td = this.html_.insertCell();
    td.className = "name";
    td.appendChild(this.html_name = new Text(this.name));
    
    td = this.html_.insertCell();
    td.className = "comment";
    td.appendChild(this.html_comment = new Text(this.comment));
    
    td = this.html_.insertCell();
    td.className = "script";
    td.appendChild(this.html_script = new Text(this.script.toString()));
    
    td = this.html_tools = this.html_.insertCell();
    var load = document.createElement("button");
    load.addEventListener("click", function() {
      if(this.onrequestload) {
        this.onrequestload();
      }
    }.bind(this)); 
    load.appendChild(new Text("Edit"));
    td.appendChild(load);
    
    load = document.createElement("button");
    load.appendChild(new Text("Delete"));
    load.addEventListener("click", function() {
      if(this.onrequestdelete) {
        this.onrequestdelete();
      }
    }.bind(this));
    td.appendChild(load);
     
  }
  return this.html_;
}


function ScriptDb() {
  this.scripts = {};
}

ScriptDb.prototype.html = function() {
  if(!this.html_) {
    this.html_ = document.createElement("table");
    this.html_.className = "saved-scripts";
    this.html_body = document.createElement("tbody");
    this.html_body.innerHTML = "<tr><th>Name</th><th>Comment</th><th>Code</th><th></th></tr>";
    this.html_.appendChild(this.html_body);  
    this.addScriptsToHTML();   
  }
  return this.html_;
}
ScriptDb.prototype.add = function(script, name, comment) {
  if(this.scripts[name]!=null) {
    this.scripts[name].update(script, name, comment);
  }
  else {
    var entry = new ScriptDbEntry(script, name, comment);
    this.scripts[name] = entry;
    
    var db = this;
    entry.onrequestload = function() {
      db.onrequestload(this.script);
    }
    entry.onrequestdelete = function() {
      db.del(this.name);
    }
    
    if(this.html_)
      this.html_body.appendChild(entry.html());
  }
  this.onchange();
}
ScriptDb.prototype.del = function(name) {
  if(this.scripts[name]!=null) {
    var entry = this.scripts[name];
    delete this.scripts[name];
    if(entry.html_) {
      entry.html_.parentNode.removeChild(entry.html_);
    }
    this.onchange();
  }
}

ScriptDb.prototype.toJSON = function() {
  return this.scripts;
}
ScriptDb.fromJSON = function(json) {
  if(typeof json=="string")
    json = JSON.parse(json);
  var db = new ScriptDb();
  for(var i in json) {
    if(json.hasOwnProperty(i)) {
      var entry = json[i];
      db.add(new Script(entry.script), entry.name, entry.comment);
    }
  }
  return db;
}

ScriptDb.prototype.onrequestload = function(script) {};
ScriptDb.prototype.onchange = function() {};

ScriptDb.prototype.addScriptsToHTML = function() {
  for(var i in this.scripts) {
    if(this.scripts.hasOwnProperty(i)) {
      this.html_body.appendChild(this.scripts[i].html());
    }
  }
}



function splitByUnescaped(str, delimiter) {
  //console.log("Input: ", str);
  var reg = new RegExp("(?:^|[^\\\\])(?:\\\\\\\\)*("+delimiter+")", "g");
  var parts = [];
  var lastResult = 0;
  var result;
  while (result = reg.exec(str)) {
    // adding additional chars if regex matched more than just delimiter
    // this assumes delimiter length or 1 char
    var length = result.index+(result[0].length-1)-lastResult;
    var substring = str.substr(lastResult, length);
    if(substring.length>0)
      parts.push(substring);
    //console.log("Match: ", result, " at ", result.index);
    //console.log("Substr: ",lastResult, length, substring);
    lastResult = result.index + result[0].length;
  }                                                  
  if(lastResult<str.length) {
    parts.push(str.substr(lastResult));
  }
  
  //console.log(parts);
  return parts;
}

function elmWithText(elmName, text, className) {
  var span = document.createElement(elmName);
  if(text instanceof HTMLElement)
    span.appendChild(text);
  else
    span.appendChild(new Text(text));
  if(typeof className=="string")
    span.className = className;
  return span;

}

function makeEnum(names) {
  var enu = {};
  for(var i=0,l=names.length; i<l; i++) {
    // new string operator ensures there is no possible identity between names
    enu[names[i]] = new String(names[i]);
  }
  return Object.freeze(enu);
}