/*

   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

*/
/**
 * @author: janmejay.singh
 * @author: vikas.sasidharan
 */
/**
 * This file holds javascript functions that are more of a common utility, rather than being tightly
 * associated with anything.
 * The functions here may be used in multiple places in different contexts.
 */
/**
 * This variable is for checking from any page or free marker template, if utility.js is included.
 */
var TWMS_UTILITY_JS = true;

// dojo 0.4 compatibility functions.

_undefSafeMixin("dojo.dom", {
    getFirstChildElement : function(/*domNode*/ node, /*string*/ childType) {
        var childrenOfType = dojo.query(childType, node);
        return (childrenOfType.length > 0) ? childrenOfType[0] : null;
    },

    replaceNode : function(/*Element*/node, /*Element*/newNode) {
		//	summary:
		//		replaces node with newNode and returns a reference to the removed node.
		//		To prevent IE memory leak, call destroyNode on the returned node when
		//		it is no longer needed.
		return node.parentNode.replaceChild(newNode, node); // Node
    },

    destroyNode : function(/*Node*/node){
		// summary:
		//		destroy a node (it can not be used any more). For IE, this is the
		//		right function to call to prevent memory leaks. While for other
		//		browsers, this is identical to dojo.dom.removeNode
		if(node.parentNode){
			node = dojo.dom.removeNode(node);
		}

        if(node.nodeType != 3){ // ignore TEXT_NODE
			if(dojo.isIE){
				node.outerHTML=''; //prevent ugly IE mem leak associated with Node.removeChild (ticket #1727)
			}
            // Destroy all the widgets, if any, that are the children of this node.
            dojo.query("[widgetId]", node).forEach(function(widgetDomNode) {
                var widget = dijit.byNode(widgetDomNode);

                if(widget) { // since it might have automatically got destroyed when we destroyed its parent.
                    widget.destroyRecursive();
                }
            });
        }
    },

    removeNode : function(/*Node*/node) {
        // summary:
        //		if node has a parent, removes node from parent and returns a
        //		reference to the removed child.
        //		To prevent IE memory leak, call destroyNode on the returned node when
        //		it is no longer needed.
        //	node: the node to remove from its parent.

        if (node && node.parentNode) {
            // return a ref to the removed child
            return node.parentNode.removeChild(node);//Node
        }
    },

    removeChildren : function(/*Element*/node, /*boolean*/ destroy){
        //	summary:
        //		removes all children from node and returns the count of children removed.
        //		The children nodes are also destroyed if the destroy flag is set.
        var count = node.childNodes.length;
        while(node.hasChildNodes()){
            var removedNode = this.removeNode(node.firstChild);
            if(destroy) {
                dojo.dom.destroyNode(removedNode);
            }
        }
        return count; // int
    },

    getAncestors : function(/*Node*/node, /*function?*/filterFunction, /*boolean?*/returnFirstHit) {
        //	summary:
        //		returns all ancestors matching optional filterFunction; will return
        //		only the first if returnFirstHit
        var ancestors = [];
        var isFunction = (filterFunction && (filterFunction instanceof Function || typeof filterFunction == "function"));
        while (node) {
            if (!isFunction || filterFunction(node)) {
                ancestors.push(node);
                if (returnFirstHit) {
                    return node;
                }
            }

            node = node.parentNode;
        }
        if (returnFirstHit) {
            return null;
        } else {
            return ancestors;
            //	array
        }
    },

    getAncestorsByTag : function(/*Node*/node, /*String*/tag, /*boolean?*/returnFirstHit) {
        //	summary:
        //		returns all ancestors matching tag (as tagName), will only return
        //		first one if returnFirstHit
        tag = tag.toLowerCase();
        return dojo.dom.getAncestors(node, function(el) {
            return ((el.tagName) && (el.tagName.toLowerCase() == tag));
        }, returnFirstHit);
        //	Node || array
    },

    getFirstAncestorByTag : function(/*Node*/node, /*string*/tag) {
        //	summary:
        //		Returns first ancestor of node with tag tagName
        return dojo.dom.getAncestorsByTag(node, tag, true);
        //	Node
    },
    
    insertBefore : function(/*Node*/node, /*Node*/ref, /*boolean?*/force){
        //	summary:
        //		Try to insert node before ref
        if(	(force)&&
               (node === ref || node.nextSibling === ref)){return false;}
        var parent = ref.parentNode;
        parent.insertBefore(node, ref);
        return true;	//	boolean
    },

    insertAfter : function(/*Node*/node, /*Node*/ref, /*boolean?*/force){
        //	summary:
        //		Try to insert node after ref
        var pn = ref.parentNode;
        if(ref == pn.lastChild){
            if((force)&&(node === ref)){
                return false;	//	boolean
            }
            pn.appendChild(node);
        }else{
            return this.insertBefore(node, ref.nextSibling, force);	//	boolean
        }
        return true;	//	boolean
    },

    insertAtIndex : function(/*Node*/node, /*Element*/containingNode, /*number*/insertionIndex){
        //	summary:
        //		insert node into child nodes nodelist of containingNode at
        //		insertionIndex. insertionIndex should be between 0 and
        //		the number of the childNodes in containingNode. insertionIndex
        //		specifys after how many childNodes in containingNode the node
        //		shall be inserted. If 0 is given, node will be appended to
        //		containingNode.
        var siblingNodes = containingNode.childNodes;

        // if there aren't any kids yet, just add it to the beginning

        if (!siblingNodes.length || siblingNodes.length == insertionIndex){
            containingNode.appendChild(node);
            return true;	//	boolean
        }

        if(insertionIndex == 0){
            return dojo.dom.prependChild(node, containingNode);	//	boolean
        }
        // otherwise we need to walk the childNodes
        // and find our spot

        return this.insertAfter(node, siblingNodes[insertionIndex-1]);	//	boolean
    },

    prependChild : function(/*Element*/node, /*Element*/parent){
        //	summary:
        //		prepends node to parent's children nodes
        if(parent.firstChild) {
            parent.insertBefore(node, parent.firstChild);
        } else {
            parent.appendChild(node);
        }
        return true;	//	boolean
    }
});

_undefSafeMixin("dojo.lang", {

    toArray: function(/*Object*/arrayLike, /*Number*/startOffset){
		// summary:
		//		Converts an array-like object (i.e. arguments, DOMCollection)
		//		to an array. Returns a new Array object.
		var array = [];
		for(var i = startOffset||0; i < arrayLike.length; i++){
			array.push(arrayLike[i]);
		}
		return array; // Array
	},

    isArray : function(/*anything*/ it){
        // summary:	Return true if it is an Array.
        return (it && it instanceof Array || typeof it == "array"); // Boolean
    },

    setTimeout : function(/*Function*/func, /*int*/delay /*, ...*/){
        // summary:
        //		Sets a timeout in milliseconds to execute a function in a given
        //		context with optional arguments.
        // usage:
        //		dojo.lang.setTimeout(Object context, function func, number delay[, arg1[, ...]]);
        //		dojo.lang.setTimeout(function func, number delay[, arg1[, ...]]);

        var context = window, argsStart = 2;
        if(!dojo.isFunction(func)){
            context = func;
            func = delay;
            delay = arguments[2];
            argsStart++;
        }

        if(dojo.isString(func)){
            func = context[func];
        }

        var args = [];
        for (var i = argsStart; i < arguments.length; i++){
            args.push(arguments[i]);
        }
        return dojo.global().setTimeout(function(){func.apply(context, args);}, delay); // int
    }
});

_undefSafeMixin("dojo.string", {
    startsWith : function(/*string*/str, /*string*/start, /*boolean*/ignoreCase){
        // summary:
        //      Returns true if 'str' starts with 'start'

        if(ignoreCase) {
            str = str.toLowerCase();
            start = start.toLowerCase();
        }
 	    return str.indexOf(start) == 0; // boolean
    },
    
    isBlank : function(/*string*/str){
        // summary:
        //	Return true if the input is not a string or it is a string made up entirely of whitespace characters
	    if(!dojo.isString(str)){
            return true;
        }
	    return (dojo.string.trim(str).length == 0); // boolean
    },

    escapeXml : function(/*string*/str, /*boolean*/noSingleQuotes) {
        //summary:
        //	Adds escape sequences for special characters in XML: &<>"'
        //  Optionally skips escapes for single quotes

        str = str.replace(/&/gm, "&amp;").replace(/</gm, "&lt;")
                .replace(/>/gm, "&gt;").replace(/"/gm, "&quot;");
        if (!noSingleQuotes) {
            str = str.replace(/'/gm, "&#39;");
        }
        return str;
        // string
    },
    escapeJavaScript : function(/*string*/str){
    //summary:
    //	Adds escape sequences for single and double quotes as well
    //	as non-visible characters in JavaScript string literal expressions

	    return str.replace(/(["'\f\b\n\t\r])/gm, "\\$1"); // string
    },
    escape: function(/*string*/type,/*string*/str){
        //summary:
        //Forward ported from .4
        var args=dojo.lang.toArray(arguments,1);
        switch(type.toLowerCase()){
            case "xml":
            case "html":
            case "xhtml":
                return this.escapeXml.apply(this,args);
            case "javascript":
            case "jscript":
            case "js":
                return dojo.string.escapeJavaScript.apply(this,args);
            default:
                return str;
        }
    }
});

dojo.require("dijit._Templated");
_undefSafeMixin("dojo.html", {
    createNodesFromText : function(/* string */txt, /* boolean? */trim) {
        txt = (trim) ? dojo.trim(txt) : txt;
        return dojo.toDom(txt);
    },

    show : function(/*dom node*/ node){
        if(node)
            dojo.style(node, "display", "");
    },

    hide : function(/*dom node*/ node){
        if(node)
            dojo.style(node, "display", "none");
    },

    setDisplay : function(/*dom node*/ node, /*boolean*/show){
        if(show) {
            this.show(node);
        } else {
            this.hide(node);
        }
    },

    isDisplayed : function(/* HTMLElement */node){
        // 	Is true if the the computed display style for node is not 'none'
        return (dojo.style(node, "display") != "none");	//	boolean
    },

    isShowing : function (node) {
	    return (this.isDisplayed(node));
    },

    setVisibility: function(/*node*/ domNode, /*boolean*/ visible) {
      dojo.style(domNode, "visibility", visible ? "visible": "hidden");
    },

    isVisible : function(/* HTMLElement */node){
        // 	Is true if the the computed visibility style for node is 'visible'
        return (dojo.style(node, "visibility") == "visible");	//	boolean
    },

    getElementsByClass: function(/*string (comma separated)*/ className, domNode) {
        var classNamesArr = this._prependDotToClassNames(className).split(",");
        var initialNodes = dojo.query("." + classNamesArr[0], domNode);

        var classNamesCount = classNamesArr.length;
        for(var i = 1; i < classNamesCount; i++) {
            initialNodes = initialNodes.filter(classNamesArr[i]);
        }

        return initialNodes;
    },

    _QUERY_CSS_MODIFIER_REGEX : /\,[\s]*/g,

    _prependDotToClassNames: function(/*string (comma separated)*/ cssNames) {
        return cssNames.replace(this._QUERY_CSS_MODIFIER_REGEX, ",.");
    }
});

dojo.declare("dojo.io.FormBind",null, {
	form: null,

	bindArgs: null,

	clickedButton: null,

	constructor: function(/*DOMNode or Object*/args) {
        // This shudn't be done while defining the property, since then the same bindArgs instance would get shared by
        // all FormBind instances! Prototype inheritance at work! ;-)
        this.bindArgs = {
            content: {},
            preventCache: true
        }
        
        var form = dojo.byId(args.formNode);

        if(!form || !form.tagName || form.tagName.toLowerCase() != "form") {
			throw new Error("FormBind: Couldn't apply, invalid form");
		} else if(form.__formBindApplied) {
			throw new Error("Detected attempt to do multiple FormBinds on the same form : " + form.id);
		}

        form.__formBindApplied = true;

        this.form = form;
        dojo.mixin(this.bindArgs, args);
        this.bindArgs.form = this.form;

        dojo.connect(form, "onsubmit",this, "submit");
        var nodeSelectorQuery = "input:[type=submit], input:[type=button]";
        dojo.query(nodeSelectorQuery, form).forEach(function(node) {
            dojo.connect(node, "onclick", this, "click");
        }, this);
    },

	onSubmit: function(/*DOMNode*/form) {
        //summary: Function used to verify that the form is OK to submit.
		//Override this function if you want specific form validation done.
		return true; //boolean
	},

	submit: function(/*Event*/e) {
        var result=null;
        //summary: internal function that is connected as a listener to the
		//form's onsubmit event.		
        e.preventDefault();
        if(this.onSubmit(this.form)) {
            var buttonName = this.clickedButton.name;
            if(buttonName) {
                this.bindArgs.content[buttonName] = this.clickedButton.value;
            }

            result=dojo.xhrPost(this.bindArgs);
        }
	},

	click: function(/*Event*/e) {
        //summary: internal method that is connected as a listener to the
		//form's elements whose click event can submit a form.
		var node = e.currentTarget;
		if(node.disabled) {
            return;
        }
		this.clickedButton = node;
	}
});

/* Internal APIs used by the porting code */

/*
 * Would mixin methods to the specified object expression, even if the root object or any of if its inner properties
 * are undefined. For eg., if the expression is "dojo.a.b", it will automatically create a & b if they are not defined.
 */
function _undefSafeMixin(/*string*/ objectName, /*object*/ mixin) {
    dojo.mixin(dojo.getObject(objectName, true), mixin);
}
// end porting related section

function getExpectedParent(node, expectedTag) {
    if(node.tagName.toLowerCase() == expectedTag.toLowerCase()) {
        return node;
    }

    return dojo.dom.getFirstAncestorByTag(node, expectedTag);
}

/**
 * This method will be used by the jsp's loaded in the iFrames to publish an event in the parent context.
 * Its is used by other items as well, that publish events to open new tabs etc...
 */
function publishEvent(topic, message) {
    dojo.publish(topic, [message]);
}

/**
 * returns true if keyCode given is a ignorable key(for things where only text matters).
 */
function isIgnorableKeyStroke(keyCode, ignoreBackspaceAndDelete) {
    // ignore keys:
    //     F1 - F12 i.e. 112 - 123
    //     PageUp - Delete i.e. 33 - 46
    // Delete i.e. 46 is NOT ignored   \
    // SpaceBar i.e. 32 is NOT ignored  - unless ignoreBackspaceAndDelete is true;
    // Backspace i.e. 8 is NOT ignored /
    // everything else below 32 is ignored
    if(keyCode == null) {
        return true;
    }
    if(ignoreBackspaceAndDelete && (keyCode == 8 || keyCode == 46)) {
        return true;
    }
    return (keyCode != 8 && keyCode < 32) ||
           (keyCode >= 33 && keyCode < 46) ||
           (keyCode >= 112 && keyCode <= 123);
}

function getTabHavingId(/*String*/ id) {
    return top.tabManager.getTabHavingId(id);
}

function getMyTab() {
    return getTabHavingId(getTabDetailsForIframe().tabId);
}

function getMyTabLabel() {
    return getMyTab().title;
}

function closeTab(/*tab reference*/ tab) {
    top.publishEvent("/tab/close", {tab : tab});
}

function closeMyTab() {
    closeTab(parent.getTabHavingLabel(getMyTabLabel()));
}

function getDetailsOfTab(/*tab reference*/ tab) {
    var iframe = getIFrameForTab(tab);
    return parseIframeDetails(iframe.name);
}

function getIFrameForTab(tab) {
    return dojo.query("iframe", tab.domNode)[0];
}

function getTabDetailsForIframe() {
     return parseIframeDetails(window.name);
}

function parseIframeDetails(/*String*/ name) {
    var suffix = "_iframe";
    var folderName = null, tabId = null;
    var indexOfHash = name.indexOf("#");
    if(indexOfHash == -1) {
           tabId = name.substring(0, name.length - suffix.length);
    } else {
        folderName = name.substring(indexOfHash + 1, name.length - suffix.length).replace(/_/g, " ");
        tabId = name.substring(0, indexOfHash);
    }
    return {tabId: tabId,
            folderName: folderName};//FIXME: this was named as folderName by mistake.. but now its too difficult to fix.
}

function checkIfLimitExceeded(textControl, limit, event) {
    if(textControl && textControl.value.length == limit) {
        dojo.stopEvent(event);
    }
}

function getTabHavingLabel(label) {
    return top.tabManager.getTabHavingLabel(label);
}

function refreshSummaryTableInTabHavinglabel(tabLabel) {
    var tab = getTabHavingLabel(tabLabel);
    if(tab != null) {
        var tabId = tab.id;
        var iframeName = tabId + "_iframe";
        window.frames[iframeName].summaryTable.refreshTable();
        delete tabId, iframeName;
    }
    delete tab;
}

function setTooltip(/*DomNode*/ domNode, /*String*/ title) {
    domNode.title = title;
}

function removeTooltip(/*DomNode*/ domNode) {
    domNode.removeAttribute("title");
}

/*
    Helper methods for detecting various mouse click events.
*/
function isLeftClickEvent(event) {
    if(dojo.isIE <= 8 )
        return event.button == 1;
    return event.button == 0;
}

function isMiddleClickEvent(event) {
    if(dojo.isIE <= 8 )
        return event.button == 4;
    return event.button == 1;
}

function isRightClickEvent(event) {
    return event.button == 2;
}

function __substituteParams(template, hash){
    var map = (typeof hash == 'object') ? hash : dojo.lang.toArray(arguments, 1);

    return template.replace(/#(\w+)/g, function(match, key){
        if(typeof(map[key]) != "undefined" && map[key] != null){
            return map[key];
        }
    });
};

function __substituteIndex(template, key, val) {
    return template.replace(new RegExp("#" + key, "g"), val);
}

function requestDeletion(/*TR*/ofRow, /*Ognl collection name*/fromCollection) {	
    var col = fromCollection.replace(/\./g, "\\.");	
    col = col.replace(/\[/g,"\\[");
    col = col.replace(/\]/g,"\\]");
    var reg = new RegExp("^" + col + "\\[.*\\]$");   
    var inputs = ofRow.getElementsByTagName("input");
    for (var i = 0; i < inputs.length; i++) {
        var input = inputs[i];   
        if ( (input.type == "hidden") && (reg.test(input.name)) ) {
            var tbody = ofRow.parentNode;
            __appendDeleterFormElem(fromCollection, input.name, tbody);
            delete tbody;
        }
        delete input;
    }
    delete reg, inputs;
}

function __appendDeleterFormElem(fromCollection, withId, toTbody) {
	var deleter = document.createElement("input");
    deleter.type = "hidden";
    deleter.name = "__remove." + fromCollection;
    deleter.value = withId;
    toTbody.appendChild(deleter);
    delete deleter;
}

function unescapeThisHtml(/*String*/ str) {
    return (new String(str)).replace(/&amp;/g,"&").replace(/&lt;/g, "<").replace(/&gt;/g, ">");
}

function executeIncludedJS(/*domNode*/ domNode) {
    var scriptElements = domNode.getElementsByTagName("script");
    var scriptContent = "";
    for(var i = 0; i < scriptElements.length; i++) {
        scriptContent += scriptElements[i].innerHTML;
    }
    if((scriptContent != null) && (dojo.string.trim(new String(scriptContent)).length > 0)) {
        eval(scriptContent);
    }
}

function publishOnParentTab(/*String*/ topic, /*assoc array(event message)*/ message) {
    var parentWindow = _getParentIFrame();
    if(!parentWindow.publishEvent)
        throw new Error("'publishEvent' method not found in the parent. Please include utility.js in the parent page.");
    parentWindow.publishEvent(topic, message);
}
/**
 * The function invocation string is something like ... "myFunction(parentVar1, \"and something\", parentVar2)"
 */
function invokeInParentTab(/*String (function invocation string)*/ func) {
   var parentWindow = _getParentIFrame();
    try {
        return parentWindow.eval(func);
    } catch(ex) {
        throw new Error("invokeInParentTab : the function invocation string raised an exception... " +
            "please check the variable/function names and make sure they exist in the tab.", ex);
    }
}

function _getParentIFrame() {
    var tabDetails = getTabDetailsForIframe();
    if(!tabDetails.folderName)
        throw new Error("'decendentOf not set : Please set the 'decendentOf' attribute correctly to use the " +
                   "publishOnParentTab function.");
    var parentTab = getTabHavingLabel(tabDetails.folderName);
    if(!parentTab) return;//it seems it is closed
    var iframeName = dojo.query("iframe", parentTab.domNode)[0].name;
    return top.window.frames[iframeName];
}

function manageRowHide(/*String (summaryTableId)*/ summaryTableId,/*String (dataId)*/ dataId) {
    if(getFrameAttribute("TST_IS_PREVIEW")) {// this means we are in preview
    	parent.publishEvent(parent.SUMMARY_TABLE_UTIL.getHideCompletedRowTopic(summaryTableId), {dataId : dataId});
    } else {// this means we are in detail page. So, we can use decendentOf.
        publishOnParentTab(invokeInParentTab("SUMMARY_TABLE_UTIL.getHideCompletedRowTopic(\"" + summaryTableId + "\")"),
                           {dataId : dataId});
    }
}

function manageMultipleRowHide(/*String (summaryTableId)*/ summaryTableId,/*Array (of dataIds)*/ dataIds) {
    if(getFrameAttribute("TST_IS_PREVIEW")) {// this means we are in preview view
        parent.publishEvent(parent.SUMMARY_TABLE_UTIL.getHideCompletedRowsTopic(summaryTableId), {dataIds : dataIds});

    } else {// this means we are in detail page. So, we can use decendentOf.
        publishOnParentTab(invokeInParentTab("SUMMARY_TABLE_UTIL.getHideCompletedRowsTopic(\"" + summaryTableId + "\")"),
                           {dataIds : dataIds});
    }
}

function manageTableRefresh(/*String (summaryTableId)*/ summaryTableId, /*boolean */ forceFullRefresh) {
    if(getFrameAttribute("TST_IS_PREVIEW")) {// this means we are in preview view
    	parent.publishEvent(parent.SUMMARY_TABLE_UTIL.getRefreshListingTopic(summaryTableId));
    } else {// this means we are in detail page. So, we can use decendentOf.
        var parentTabLabel = getTabDetailsForIframe().folderName;
        top.tabManager.markDirty(parentTabLabel, forceFullRefresh);
    }
}

function manageTableMinimize(/*String (summaryTableId)*/ summaryTableId) {
    if(getFrameAttribute("TST_IS_PREVIEW")) {// this means we are in preview view
    	var topic = parent.SUMMARY_TABLE_UTIL.getMinimizeTopic(summaryTableId);
        parent.publishEvent(topic);
    } else {// this means we are in detail page. So, we can use decendentOf.
        publishOnParentTab(invokeInParentTab("SUMMARY_TABLE_UTIL.getMinimizeTopic(\"" + summaryTableId + "\")"));
    }
}

function manageTableRestore(/*String (summaryTableId)*/ summaryTableId) {
    if(getFrameAttribute("TST_IS_PREVIEW")) {// this means we are in preview view
        parent.publishEvent(parent.SUMMARY_TABLE_UTIL.getRestoreTopic(summaryTableId));
    } else {// this means we are in detail page. So, we can use decendentOf.
        publishOnParentTab(invokeInParentTab("SUMMARY_TABLE_UTIL.getRestoreTopic(\"" + summaryTableId + "\")"));
    }
}

function getFrameAttribute(/*String (name of attribute)*/ attribute) {
    return window.frameElement ? window.frameElement[attribute] : null;
}

// TWMS namespace starts here...

_undefSafeMixin("twms.ajax", {

    fireJsonRequest : function(/*String (url)*/ url,/*Object (param map)*/ params,/*Function*/ onSuccess,
                                            /*Function?*/ onError, /*boolean*/ useGet) {
        this.fireRequestForMimeType(url, params, "json", onSuccess, onError,useGet);
    },

    fireHtmlRequest : function(/*String (url)*/ url,/*Object (param map)*/ params,/*Function*/ onSuccess,
                                            /*Function?*/ onError, /*boolean*/ useGet) {
        this.fireRequestForMimeType(url, params, "text", onSuccess, onError,useGet);
    },

    fireJavaScriptRequest : function(/*String (url)*/ url,/*Object (param map)*/ params,/*Function*/ onSuccess,
                                            /*Function?*/ onError, /*boolean*/ useGet) {
        this.fireRequestForMimeType(url, params, "javascript", onSuccess, onError,useGet);
    },

    fireRequestForMimeType : function(/*String (url)*/ url,/*Object (param map)*/ params, /*string*/ mimeType,
                                             /*Function*/ loadCallback, /*Function?*/ errorCallback,
                                             /*boolean*/ useGet) {
        var xhrFunc = useGet ? dojo.xhrGet : dojo.xhrPost;
        xhrFunc({
            url : url,
            content: params,
            load : function(response) {
                loadCallback(response);
                return response;
            },
            error : function(response) {
                if (errorCallback) {
                	errorCallback('error', response);
                } else {
                	throw new Error("Ajax request failed : ", response);
                }
                return response;
                //FIXME: do something better here...
            },
            handleAs: mimeType
        });
    }
});

_undefSafeMixin("twms.string", {
    removeAllWhiteSpace: function(/*string*/ aString) {
        return aString ? aString.replace(/\s+/g, '') : null;
    },

    toArray: function(/*string*/stringToBeConverted, /*string*/separator, /*boolean*/removeAllWhiteSpaces) {
        var inputString = removeAllWhiteSpaces ? this.removeAllWhiteSpace(stringToBeConverted) : stringToBeConverted;
        return inputString.split(separator);
    },

    summarize: function(/*string*/ text, /*number*/ numCharsToShow) {
        var textLength = text.length;

        return (textLength > numCharsToShow) ? text.substring(0, numCharsToShow) + "..." : text;
    }
});

_undefSafeMixin("twms.util", {
    // Disables all widgets and form fields and hides all buttons in the whole page, except for the ones which have the
    // class name passed as the argument.
    makePageReadOnly: function(/*optional string*/classNameForBypass) {
        var byPassFragment = dojo.string.isBlank(classNameForBypass) ?
                             "" : ":not([class~=" + classNameForBypass + "])";

        // First disable all the widgets
        dojo.query("[widgetId]" + byPassFragment).forEach(function(widgetDomNode) {
        	if (dojo.isIE){
        		widgetDomNode.disabled = false;}
        	else{
        	widgetDomNode.disabled = true;}
        	
        });
        
        // Now work on the plain ol' input fields and buttons. As a perf optimization, we skip already disabled
        // elements.

        var classAndDisabledByPassFragment = ":not([disabled])" + byPassFragment;

        // Disable all input fields but keep them visible, since the user would want to see the values inside them.
        dojo.query("input" + classAndDisabledByPassFragment).forEach(function(inputField) {
            inputField.disabled = true;
        });

        // Needn't bother to disable the buttons. Just hide them.
        dojo.query("button" + classAndDisabledByPassFragment).forEach(function(buttonNode) {
            dojo.html.hide(buttonNode);
        });
    },

    // This would show/hide the given node and also enable/disable its children of type "input" respectively. It
    // correctly handles input elements that were already disabled (even before calling this API). i.e., for such
    // elements, even if the "show" argument is true, it wont enable them.
    adjustVisibilityAndSubmission: function(/*dom node*/ node, /*boolean*/ show) {
        dojo.html.setDisplay(node, show);

        dojo.query("input, textarea", node).forEach(function(input) {
            if(show) {
                if(input._disabledViaTwms) {
                    input.disabled = input._disabledViaTwms = false;
                }
            } else {
                input._disabledViaTwms = true;  /* Was initially input._disabledViaTwms=!input.disabled.
                                                   Have modified this because, if the input node is enabled,then disabled , then again disabled,the disabledViaTwms was set to false
                                                   and input.disabled never becomes true .
                                                 */ 
                input.disabled =  true;
            }
        });
    },

    putLidOn: function(/*domNode*/ node, /*string*/ message) {
        if(node && !this.hasLid(node)) {
            var lid = new twms.widget.LoadingLid({targetNode:node});
            if(!dojo.string.isBlank(message)) {
                lid.setMessage(message);
            }

            lid.show();
            node._twmsLoadingLid = lid;
        }
    },

    removeLidFrom: function(/*domNode*/ node) {
        var domNode = dojo.byId(node);

        var lid = domNode._twmsLoadingLid;
        if(lid) {
            lid.dispose();
            domNode._twmsLoadingLid = null;
        }
    },

    changeLidMessageFor: function(/*domNode*/ node, /*string*/ message) {
        var domNode = dojo.byId(node);
        
        var lid = domNode._twmsLoadingLid;
        if(lid) {
            lid.setMessage(message);
        }
    },

    hasLid: function(/*domNode*/ node) {
        return node._twmsLoadingLid != null; 
    },

    /*preventMultipleFormSubmissions: function(*//*string*//* message) {
            // The ":not(.lidOnSubmit)" check is to prevent runnning into infinite loops for cases where this files gets
            // included (thru head.ftl) multiple times (such as when we add content thru Ajax).
            dojo.query("form:not(.lidOnSubmit)").forEach(function(formNode) {

                // For pages loaded from iframes embedded inside a tab (such as File Upload dialogs), we put the lid
                // manually.
                var lidShower = (parent === top) ?
                        dojo.hitch(twms.util, twms.util.putLidOnCurrentPage, message)
                        :
                        function() {
                            twms.util.putLidOn(dojo.body());
                        };

                dojo.connect(formNode, "onsubmit", lidShower);

                // This bit is for handling programmatic submissions.
                formNode._twmsSubmit = formNode.submit;
                formNode._twmssubmit = function() {
                    twms.util.putLidOnCurrentPage(message);
                    return formNode._twmsSubmit();
                };

                dojo.addClass(formNode, "lidOnSubmit");
            });
        },

        putLidOnCurrentPage: function(*//*string*//* message) {
            if (top.tabManager) {
                top.tabManager.putLidOnTab(getMyTab(), message);
            } else {
                this.putLidOn(dojo.body(), message);
            }
        },*/

    restrictTextEntry: function(/*domNode*/ node, /*integer*/ maxLimit) {
        this._limitNodeValue(node, maxLimit);
        var _self = this;

        dojo.forEach(["onkeypress", "paste"], function(event) {
            dojo.connect(node, event, function() {
                // Need to use setTimeout, since the value is set only *after* the event happens.
                setTimeout(function() {
                    _self._limitNodeValue(node, maxLimit);
                }, 5);
            });
        });
    },

    _limitNodeValue: function(/*domNode*/ node, /*integer*/ maxLimit) {
        var textInNode = node.value;
        var numCharsLeft = maxLimit - textInNode.length;

        if (numCharsLeft <= 0) {
            node.value = textInNode.substring(0, maxLimit);
            numCharsLeft = 0;
        }

        dojo.byId(node.id + "_remainingCharsCount").innerHTML = numCharsLeft;
    },

    updateSessionAccessTime: function() {
        if(top.sessionTimeOutNotifier) {
            top.sessionTimeOutNotifier.setSessionLastAccessTimeToNow();
        }
    },
    
    disconnectAll: function(/*array*/ connectHandlers) {
        dojo.forEach(connectHandlers, function(connectHandler) {
           dojo.disconnect(connectHandler); 
        });
    }
});

_undefSafeMixin("twms.widget", {
    isAChildWidgetOf: function(/*dijit widget*/ parentWidget, /*string*/ childWidgetId) {
        var matchingNodes = dojo.query("[widgetId='" + childWidgetId + "']", parentWidget.domNode);
        return matchingNodes.length == 1;
    }
});

function getElementByClass(cssClassName, domNode) {
    var matchingNodes = dojo.html.getElementsByClass(cssClassName, domNode);
    return (matchingNodes.length > 0) ? matchingNodes[0] : null;
}

var VALIDATOR_MODE = {
    EXPRESSION : "EXP",
    FUNCTION : "FN"
};

dojo.declare("twms.utility.Validatable", null, {

    STATIC_VARS : {
            COUNTER : 0
    },

    _id : 0,
    _shortCircuit : false,

    constructor : function(/*boolean*/ shortCircuit) {
        this._id = this.STATIC_VARS.COUNTER++;
        this._shortCircuit = shortCircuit;
    },

    /**
     * Will return an array of validation messages.
     */
    validate : function(/*Target Object*/ object) {
        try {
            return this._validate(object);
        } catch(ex) {
            throw new Error("twms.utility.Validator : Exception raised while validating by validator having id : " + this._id, ex);
        }
    },

    getId : function() {
        return this._id;
    },

    getMessages : function() {
        return this._message;
    },

    isShortCircuited : function() {
        return this._shortCircuit;
    },

    /**
     * @return array of validation messages.
     */
    _validate : function(/*Target Object*/ object) {
        //doing nothing here.... classes entending this will override this function.
    }
});

/**
 * This is the class that does the validation logic... It can be instantiated in following two ways...
 * new twms.utility.Validator(VALIDATOR_MODE.EXPRESSION, "left hand side value", "operator for ex. === ", "right hand side");
 * or
 * new twms.utility.Validator(VALIDATOR_MODE.FUNCTION, "functionName", "argument1", "argument2"...);
 * and then calling validate(targetObject).. tires to validate and returns null or validationMessage...
 * depending on weather the validation was successful or not....
 */
dojo.declare("twms.utility.Validator", twms.utility.Validatable, {

    _lhs : "",
    _rhs : "",
    _operator : "",

    _function : null,
    _arguments : null,

    _mode : "",

    constructor : function(/*boolean*/ shortCircuit, /*String (validation mode)["EXP","FN"]*/ mode, message) {
        this._mode = mode;
        this._message = message;
        if(this._mode === VALIDATOR_MODE.EXPRESSION) {
            this._lhs = arguments[3];
            this._operator = arguments[4];
            this._rhs = arguments[5];
            this._validate = this._validateExp;
        } else if(this._mode === VALIDATOR_MODE.FUNCTION){
            this._function = arguments[3];
            this._arguments = new Array();
            for(var i = 4; i < arguments.length; i++) {
                this._arguments.push(arguments[i]);
            }
            this._validate = this._validateFn;
        } else {
            throw new Error("twms.utility.Validator : Illegal value of validation mode.");
        }
    },

    _validate : function(/*Target Object*/ object) {/*Will be overridden in the constructor*/},

    _validateFn : function(/*Target Object*/ object) {
        with(object) {
            var args = new Array();
            for(var i in this._arguments) {
                args.push(eval(this._arguments[i]));
            }
            if(eval(this._function).apply(object, args)) {
                return [];
            } else {
                return [this._message];
            }
        }
    },

    _validateExp : function(/*Target Object*/ object) {
        with(object) {
            if(eval(this._lhs + " " + this._operator + " " + this._rhs)) {
                return [];
            } else {
                return [this._message];
            }
        }
    }
});

/**
 * This acts like a series circuit, if one fails... the whole circuit fails.
 */
dojo.declare("twms.utility.ValidatorChain", twms.utility.Validatable, {

    _validatorChain : null,

    constructor : function() {
        this._validatorChain = new Array();
    },

    add : function(/*Object [Validatable]*/ validator) {
        for(var i in this._validatorChain) {
            if(this._validatorChain[i].getId() === validator.getId()) {
                return this;
            }
        }
        this._validatorChain.push(validator);
        return this;
    },

    drop : function(/*Object [Validatable]*/ validator) {
        for(var i in this._validatorChain) {
            if(this._validatorChain[i].getId() === validator.getId()) {
                this._validatorChain.splice(i, 1);
                return this;
            }
        }
        return this;
    },

    _validate : function(/*Target Object*/ object) {
        var messages = new Array();
        for(var i in this._validatorChain) {
            var validationResult = this._validatorChain[i].validate(object);
            if(validationResult.length > 0) {
                for(var j in validationResult) {
                    messages.push(validationResult[j]);
                }
                if(this._validatorChain[i].isShortCircuited()) return messages;
            }
        }
        return messages;
    }
});

var __fileDownloader = null;
/**
 * FileDownloader is an expensive resource... please make sure there is only one instance of this in a page. don't instantiate
 * the class yourself... use the getFileDownloader method below.
 */

function getFileDownloader(/*boolean*/ lazyInitialization) {
    if(__fileDownloader == null) {
        __fileDownloader = new twms.utility._FileDownloader(lazyInitialization);
    }
    return __fileDownloader;
}
dojo.declare("twms.utility._FileDownloader", null, {

    _dummyIframe : null,

    constructor : function(/*boolean*/ lazyInitialization) {
        if(!lazyInitialization) {
            this._initDummyIframe();
        }
    },

    _initDummyIframe : function() {
        this._dummyIframe = document.createElement("iframe");
		dojo.style(this._dummyIframe, "display", "none");
		dojo.body().appendChild(this._dummyIframe);
    },

    download : function(/*String URL*/ url) {
        if(this._dummyIframe == null) {
            this._initDummyIframe();
        }
        this._dummyIframe.src = url;
    }
});

function bindKeyboardAction(/*domNode (on or below node)*/ node, /*int (keyCode)*/ keyCode,
                            /*boolean (with/without Ctrl)*/ ctrlOn, /*boolean (with/without Alt)*/ altOn,
                            /*Function (function to call)*/ callback) {
    dojo.connect(node, "onkeyup", function(event) {
        if(event.ctrlKey === ctrlOn && event.altKey === altOn && event.keyCode === keyCode) {
            callback(event);
        }
    });
}

/**
 * Remove button is assumed to be inside a row.
 * Add new row : 'Ctrl' + 'insert'
 * Remove this row : 'Ctrl' + 'delete'
 */
function bindRepeatTableKeyboardActions(/*domNode [table]*/ table, /*domNode [addButton icon]*/ addButton,
                                        /*domNode [remove button cssClass]*/ removeButtonClass) {
    var tBody = dojo.query("tbody", table)[0];
    //binding 'Ctrl' + 'insert'
    bindKeyboardAction(table, 45, true, false, function(event) {//45 is 'insert'
        dojo.stopEvent(event);
        addButton.onclick(event);
        __focusRepeatTableLastRow(tBody);
    });
    //binding 'Ctrl' + 'delete'
    bindKeyboardAction(table, 46, true, false, function(event) {//46 is 'delete'
        dojo.stopEvent(event);
        var tr = getExpectedParent(event.target, "tr");
        if(tr) {
            var deleteButton = getElementByClass(removeButtonClass, tr);
            if(deleteButton) {
                deleteButton.onclick(event);
            }
        }
        __focusRepeatTableLastRow(tBody);
    });
}

function __focusRepeatTableLastRow(/*domNode (tbody)*/ tBody) {
    var lastTr = dojo.query("tr", tBody)[0];
    if (lastTr) {
        var inputs = lastTr.getElementsByTagName("input");
        for (var i in inputs) {
            if (dojo.html.isDisplayed(inputs[i])) {
                inputs[i].focus();
                return;
            }
        }
    }
}

function getMapLength(/*javascript map*/ map) {
    var i = 0;
    for (var j in map) i++;
    return i;
}

function cloneEvenIfIe(/*event*/event) {
    if (dojo.isIE) {
        for (i in event) {
            this[i] = event[i];
        }
        return this;
    } else {
        return event;
    }
}

/**
Returns reference to focused tab 
*/
function getFocusedTab(){
	return top.tabManager.tabPane.selectedChildWidget;
}