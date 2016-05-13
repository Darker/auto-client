function startScriptEditor() {
  console.log("Scripts loaded.");
  var editor = document.getElementById("script-editor");
  var preview = document.getElementById("script_preview");
  var scriptValue = localStorage["edited-script"]||"S>";
  var SCRIPT = new Script(scriptValue);
  var ADD_COMMAND_BUTTON = addCommandButton(startCommandEditor);
  editor.value = scriptValue;
  drawPreview();
  
  var validScript = new RegExp("^S>([a-zA-Z0-9_-]+(,[^;]*)*(;[a-zA-Z0-9_-]+(,[^;]*)*)*;?)?$");
  
  editor.addEventListener("keydown", function(e) {
    if(e.ctrlKey)
      return true;
    var newValue = getNewValue(this, e);
    console.log(this.value, "=>", newValue);
    
    if(this.value.match(validScript) && !newValue.match(validScript)) {    
      e.preventDefault();
      return false;
    }
  });
  editor.addEventListener("paste", function(e) {
    var newValue = insertValue(this, e.clipboardData.getData("text/plain"));
    console.log(this.value, "=>", newValue);
    
    if(this.value.match(validScript) && !newValue.match(validScript)) {    
      e.preventDefault();
      return false;
    }
  });
  
  var debounce = 0;
  editor.addEventListener("input", function() {
    SCRIPT = new Script(this.value);
    localStorage["edited-script"]=this.value;
    clearTimeout(debounce);
    debounce = setTimeout(drawPreview,1);
  });

  function drawPreview() {
    preview.innerHTML = "";
    var html = SCRIPT.html();
    html.appendChild(ADD_COMMAND_BUTTON);
    preview.appendChild(html);
  }
  
  function getNewValue(input, keydown) {
    var newValue;
    var key = keydown.key;
    if(key.length==1) {
      newValue = insertValue(input, key);
    }
    else if(key=="Backspace") {
      if(input.selectionStart==input.selectionEnd) {
        newValue = input.value.substr(0, input.selectionStart-1)+input.value.substr(input.selectionEnd);
      }
      else {
        newValue = insertValue(input, "");
      }
    }
    else if(key=="Delete") {
      if(input.selectionStart==input.selectionEnd) {
        newValue = input.value.substr(0, input.selectionStart)+input.value.substr(input.selectionEnd+1);
      }
      else {
        newValue = insertValue(input, "");
      }
    }
    else {
      newValue = input.value;
    }
    return newValue;
  }
  /** Calculate new value assuming text is added to the input field at the current cursor location: **/
  function insertValue(input, text) {
    var value = input.value;
    return value.substr(0, input.selectionStart)+text+value.substr(input.selectionEnd);
  }
  /** The script editor with dialogs **/
  var dialog;
  var dialog_save;
  onready(function() {
    /** COMMAND WIZATD **/
    var form;
    dialog = $( "#script-edit-select" ).dialog({
      autoOpen: false,
      height: 300,
      width: 350,
      modal: true,
      buttons: {
        Cancel: function() {
          dialog.dialog( "close" );
        }
      },
      close: function() {
        form.hide().each(function(){ 
          this.reset();
        });
        dialog.find("div.select").show();
      }
    });
    /** Process the inputs and convert them to command! **/
    form = dialog.find("form.add-command").on( "submit", function( event ) {
      event.preventDefault();
      var values = Array.prototype.map.call($(this).find(".command-param"), (function(x) {
        return x.value;
      }));
      values.unshift(this.name);
      var command = Script.arrayToCommand(values);
      SCRIPT.addCommand(command);
      localStorage["edited-script"] = editor.value = SCRIPT.toString();
      
      console.log("Add command ", values, command);
      dialog.dialog("close");
    });
    dialog.find( "div.select button" ).on( "click", function( event ) {
      console.log("Add command "+this.value);
      if(dialog.find("form.add-command."+this.value).show().length>0) {
        console.info("Selected form:", this.value);
        dialog.find("div.select").hide();
      }
      else {
        console.error("Invalid command name for dialog: ", this.value);
      }
    });
    
    /** SAVED SCRIPTS **/
    var savedScripts;
    if(localStorage["saved-scripts"]) {
      savedScripts = loadDbFromStorage();
    }
    else {
      savedScripts = new ScriptDb();
      savedScripts.add(new Script("S>s,top,3,800;"), "top lane", "Typical top lane call"); 
      savedScripts.add(new Script("S>s,jungle,3,800;"), "jungle", "Typical jungle lane call");     
      savedScripts.add(new Script("S>s,mid,3,800;"), "mid lane", "Typical mid lane call"); 
      savedScripts.add(new Script("S>s,bot adc,3,800;"), "bot lane adc", "Typical bot lane ADC call");  
    }
    initDbDisplay(savedScripts);
    window.addEventListener("storage", function(e) {
      console.log("Storage changed: ",e);
      if(e.key=="saved-scripts") {
        savedScripts = loadDbFromStorage();
        document.getElementById("saved-scripts").innerHTML = ""; 
        initDbDisplay(savedScripts);
      }
    });
    function loadDbFromStorage() {
      return ScriptDb.fromJSON(JSON.parse(localStorage["saved-scripts"]));
    }
    function initDbDisplay(savedScripts) {
      document.getElementById("saved-scripts").appendChild(savedScripts.html());
      savedScripts.onrequestload = function(script) {
        SCRIPT = script;
        editor.value = script.toString();
        drawPreview();
      }
      savedScripts.onchange = function() {
        localStorage["saved-scripts"] = JSON.stringify(savedScripts);
      }
    }
    /** SAVE new script **/
    dialog_save = $( "#script-edit-save" ).dialog({
      autoOpen: false,
      height: 300,
      width: 350,
      modal: true,
      buttons: {
        Cancel: function() {
          dialog_save.dialog( "close" );
        },
        Save: function() {
          savedScripts.add(SCRIPT, form_save.name.value, form_save.comment.value);
          dialog_save.dialog( "close" );
        }
      },
      close: function() {
        form_save.reset();
      }
    });
    var form_save = dialog_save.find("form")[0];
    $(form_save).on("submit", function(e) {
      e.preventDefault();
      return false;
    });
    var a = document.createElement("a");
    a.href = "javascript: /*save script*/void(0);";
    a.onclick = function() {
      dialog_save.dialog("open");
    }
    a.appendChild(new Text("Click here to save your script."));
    document.getElementById("editor-help").appendChild(a);
  }); 
  function startCommandEditor() {
    dialog.dialog( "open" );
  }
}
function addCommandButton(cb) {
  var div = document.createElement("div");
  div.className = "script-command add-button";
  div.innerHTML = "+";
  div.addEventListener("click", cb);
  return div;
}
