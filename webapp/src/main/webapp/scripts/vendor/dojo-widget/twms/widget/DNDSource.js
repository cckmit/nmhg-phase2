/* 
 *  This widget is just to recognize the left mouse button click for source move
 *  In FF & Chrome, even though dom-quirks is enabled the mouse event still generates the following values for button
 *  leftButton : 0, middleButton:1,rightButton:2 because of which DND was not working in FF & Chrome
 */

dojo.require("dojo.dnd.Source");

dojo.provide("twms.widget.DNDSource");

dojo.declare("twms.widget.DNDSource", dojo.dnd.Source, {
    _legalMouseDown: function(e){
		// summary:
		//		checks if user clicked on "approved" items
		// e: Event
		//		mouse event

		// accept only the left mouse button
		if(!dojo.mouseButtons.isLeft(e)){ 
            if(e.button != 0){ // second chance for FF & Chrome to identify the left button click
                return false; 
            }
                
        }

		if(!this.withHandles){ return true; }

		// check for handles
		for(var node = e.target; node && node !== this.node; node = node.parentNode){
			if(dojo.hasClass(node, "dojoDndHandle")){ return true; }
			if(dojo.hasClass(node, "dojoDndItem") || dojo.hasClass(node, "dojoDndIgnore")){ break; }
		}
		return false;	// Boolean
	}
});
