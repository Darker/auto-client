function Script(source) {
  this.execute = this.execute.bind(this);
  this.parse(source);
}
Script.commands = {
  s: CommandSay,
  d: CommandDelay
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
    var params = splitByUnescaped(commandsStr[i], ",");
    console.log("Command: ", commandsStr[i], params);
    var command_ctor = Script.commands[params[0]]||Command;
    this.commands.push(new command_ctor(params));
  }
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
    
    for(var i=0,l=this.commands.length; i<l; i++) {
      var html = this.commands[i].html();
      html.setAttribute("x-command-index", i);
      main.appendChild(html);
    }
  }
  return this.html_;
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
Command.prototype.html = function() {
  if(!this.html_main) {
    var main = this.html_main = document.createElement("div");
    main.className = "script-command";
    
    var name = document.createElement("p");
    name.className = "name";
    name.appendChild(new Text(this.name));
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
  
  this.text = this.args[0];
  this.delay = this.args[1]*1>0?this.args[1]*1:200;
  this.repeat = this.args[2]*1>0?this.args[2]*1:1;
}
CommandSay.prototype = Object.create(Command.prototype);
CommandSay.prototype.constructor = CommandSay;
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
}
CommandDelay.prototype = Object.create(Command.prototype);
CommandDelay.constructor = CommandDelay;

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

function makeEnum(names) {
  var enu = {};
  for(var i=0,l=names.length; i<l; i++) {
    // new string operator ensures there is no possible identity between names
    enu[names[i]] = new String(names[i]);
  }
  return Object.freeze(enu);
}