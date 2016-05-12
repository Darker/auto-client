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