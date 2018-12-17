
var autoSuggestElements = new Array();

function bindAutoSuggest(formId, action) {
	var formElement = document.getElementById(formId);
	if( typeof(formElement)!=undefined && formElement!=null ) {
				formElementsArrayId = 0;
				for(j=0;;j++) {
					if(handlingDetails[j] == null) {
						formElementsArrayId = j;
						break;
					}
				}
				arrayOfElements = new Array();
				j=0;
				
				if( typeof(formElement.elements)!=undefined && formElement.elements!=null ) {
						for(i=0; i<formElement.elements.length; i++) {
							var myName = formElement.elements[i];
							for(j=0; j < elementsForAutoSuggest.length;j++) {
								if(myName == elementsForAutoSuggest[j]) {
									arrayOfElements[j] = myName;
									j++;
									autoSuggestElements[i] = new AutoSuggestControl( myName, new Suggester(formElementsArrayId));
								}
							}
						}
						handlingDetails[formElementsArrayId] = new FormHandlingDetails(arrayOfElements, action);	
				}
		}
}

function AutoSuggestControl(oTextbox, oProvider) {
    this.cur /*:int*/ = -1;
    this.layer = null;
    this.provider = oProvider;
    this.textbox = oTextbox;
    this.init();   
}

AutoSuggestControl.prototype.autosuggest = function (aSuggestions, bTypeAhead) {
    if (aSuggestions.length > 0) {
        if (bTypeAhead) {
           this.typeAhead(aSuggestions[0]);
        }
        this.showSuggestions(aSuggestions);
    } 
    else {
        this.hideSuggestions();
    }
};

AutoSuggestControl.prototype.createDropDown = function () {
    var oThis = this;
    this.layer = document.createElement("div");
    this.layer.className = "suggestions";
    this.layer.style.visibility = "hidden";
    this.layer.style.width = this.textbox.offsetWidth;
    this.layer.onmousedown = 
    this.layer.onmouseup = 
    this.layer.onmouseover = function (oEvent) {
        oEvent = oEvent || window.event;
        oTarget = oEvent.target || oEvent.srcElement;
        if (oEvent.type == "mousedown") {
            oThis.textbox.value = oTarget.firstChild.nodeValue;
            oThis.hideSuggestions();
        } 
        else if (oEvent.type == "mouseover") {
            oThis.highlightSuggestion(oTarget);
        } 
        else {
            oThis.textbox.focus();
        }
    };
    document.body.appendChild(this.layer);
};

AutoSuggestControl.prototype.getLeft = function () /*:int*/ {
    var oNode = this.textbox;
    var iLeft = 0;
    while(oNode.tagName != "HTML"/*HACK: this shd also work with body, it does in FF, doesn't in IE....*/ && oNode.tagName != "BODY") {
        iLeft += oNode.offsetLeft;
        oNode = oNode.offsetParent;
    }
    return iLeft;
};

AutoSuggestControl.prototype.getTop = function () /*:int*/ {
    var oNode = this.textbox;
    var iTop = 0;
    while(oNode.tagName != "HTML"/*HACK: this shd also work with body, it does in FF, doesn't in IE....*/  && oNode.tagName != "BODY") {
        iTop += oNode.offsetTop;
        oNode = oNode.offsetParent;
    }
    return iTop;
};

AutoSuggestControl.prototype.handleKeyDown = function (oEvent /*:Event*/) {
    switch(oEvent.keyCode) {
        case 38: //up arrow
            this.previousSuggestion();
            break;
        case 40: //down arrow 
            this.nextSuggestion();
            break;
        case 13: //enter
            this.hideSuggestions();
            oEvent.returnValue = false;//HACK: To make IE Happy.....(IE doesn't prevent default action on calling the preventDefault())
            oEvent.preventDefault();
            break;
    }
};

AutoSuggestControl.prototype.handleKeyUp = function (oEvent /*:Event*/) {
    var iKeyCode = oEvent.keyCode;
    if(iKeyCode == 13) {
    	oEvent.returnValue = false;//HACK: To make IE Happy.....(IE doesn't prevent default action on calling the preventDefault())
    	oEvent.preventDefault();
    }
    //for backspace (8) and delete (46), shows suggestions without typeahead
    if (iKeyCode == 8 || iKeyCode == 46) {
        this.provider.requestSuggestions(this, false);
    } 
    else if (iKeyCode < 32 || (iKeyCode >= 33 && iKeyCode < 46) || (iKeyCode >= 112 && iKeyCode <= 123)) {
        //ignore
    } 
    else {
        this.provider.requestSuggestions(this, true);
    }
};

AutoSuggestControl.prototype.hideSuggestions = function () {
    this.layer.style.visibility = "hidden";
};

AutoSuggestControl.prototype.highlightSuggestion = function (oSuggestionNode) {    
    for (var i=0; i < this.layer.childNodes.length; i++) {
        var oNode = this.layer.childNodes[i];
        if (oNode == oSuggestionNode) {
            oNode.className = "current"
        } else if (oNode.className == "current") {
            oNode.className = "";
        }
    }
};

AutoSuggestControl.prototype.init = function () {
    var oThis = this;
    this.textbox.onkeyup = function (oEvent) {
        if (!oEvent) {
            oEvent = window.event;
        }
        oThis.handleKeyUp(oEvent);
    };
    this.textbox.onkeydown = function (oEvent) {
        if (!oEvent) {
            oEvent = window.event;
        }
        oThis.handleKeyDown(oEvent);
    };
    this.textbox.onblur = function () {
        oThis.hideSuggestions();
    };
    this.createDropDown();
};

AutoSuggestControl.prototype.nextSuggestion = function () {
    var cSuggestionNodes = this.layer.childNodes;
    if (cSuggestionNodes.length > 0 && this.cur < cSuggestionNodes.length-1) {
        var oNode = cSuggestionNodes[++this.cur];
        this.highlightSuggestion(oNode);
        this.textbox.value = oNode.firstChild.nodeValue; 
    }
};

AutoSuggestControl.prototype.previousSuggestion = function () {
    var cSuggestionNodes = this.layer.childNodes;
    if (cSuggestionNodes.length > 0 && this.cur > 0) {
        var oNode = cSuggestionNodes[--this.cur];
        this.highlightSuggestion(oNode);
        this.textbox.value = oNode.firstChild.nodeValue;   
    }
};

AutoSuggestControl.prototype.selectRange = function (iStart /*:int*/, iLength /*:int*/) {
    if (this.textbox.createTextRange) {
        var oRange = this.textbox.createTextRange(); 
        oRange.moveStart("character", iStart); 
        oRange.moveEnd("character", iLength - this.textbox.value.length);      
        oRange.select();
    } 
    else if (this.textbox.setSelectionRange) {
        this.textbox.setSelectionRange(iStart, iLength);
    }     
    this.textbox.focus();      
}; 

AutoSuggestControl.prototype.showSuggestions = function (aSuggestions /*:Array*/) {
    var oDiv = null;
    this.layer.innerHTML = "";  //clear contents of the layer
    for (var i=0; i < aSuggestions.length; i++) {
        oDiv = document.createElement("div");
        oDiv.appendChild(document.createTextNode(aSuggestions[i]));
        this.layer.appendChild(oDiv);
    }
    this.cur = 0;
    this.layer.style.left = this.getLeft() + "px";
    var top = this.getTop();
    if(this.textbox.className.indexOf("showSuggestionsOnTop") != -1) {
    	top -= this.layer.offsetHeight;
    } else {
    	top += this.textbox.offsetHeight;
    }
    this.layer.style.top = top + "px";
    this.layer.style.visibility = "visible";
};

AutoSuggestControl.prototype.typeAhead = function (sSuggestion /*:String*/) {
    if (this.textbox.createTextRange || this.textbox.setSelectionRange){
        var iLen = this.textbox.value.length; 
        
        this.selectRange(iLen, sSuggestion.length);
    }
};