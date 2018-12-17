/* 
 * Custom Implimentation of menu to menu to fix the issue with ie8
 */

dojo.provide("twms.widget.Menu");
dojo.require("dijit.Menu");
dojo.require("dijit.popup");
dojo.declare("twms.widget.Menu", dijit.Menu, {
	_openMyself: function(args){
		// summary:
		//		Internal function for opening myself when the user does a right-click or something similar.
		// args:
		//		This is an Object containing:
		//		* target:
		//			The node that is being clicked
		//		* iframe:
		//			If an <iframe> is being clicked, iframe points to that iframe
		//		* coords:
		//			Put menu at specified x/y position in viewport, or if iframe is
		//			specified, then relative to iframe.
		//
		//		_openMyself() formerly took the event object, and since various code references
		//		evt.target (after connecting to _openMyself()), using an Object for parameters
		//		(so that old code still works).

		var coords = args.coords;

		var self=this;
		var prevFocusNode = this._focusManager.get("prevNode");
		var curFocusNode = this._focusManager.get("curNode");
		var savedFocusNode = !curFocusNode || (dojo.isDescendant(curFocusNode, this.domNode)) ? prevFocusNode : curFocusNode;

		function closeAndRestoreFocus(){
			// user has clicked on a menu or popup
			if(self.refocus && savedFocusNode){
                try{
    				savedFocusNode.focus();
                }catch(e){console.log('error');}
			}
			dijit.popup.close(self);
		}
		dijit.popup.open({
			popup: this,
			x: coords.x,
			y: coords.y,
			onExecute: closeAndRestoreFocus,
			onCancel: closeAndRestoreFocus,
			orient: this.isLeftToRight() ? 'L' : 'R'
		});
		this.focus();

		this._onBlur = function(){
			this.inherited('_onBlur', arguments);
			// Usually the parent closes the child widget but if this is a context
			// menu then there is no parent
			dijit.popup.close(this);
			// don't try to restore focus; user has clicked another part of the screen
			// and set focus there
		};
	}    
});

