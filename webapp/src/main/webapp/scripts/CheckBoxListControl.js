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

/*
	This control uses dojo;s even library APIs to react to user actions.
*/


function CheckBoxListControl(masterCheckBox) {
	//See [1] and [2] to understand what all conditions are checked in this if statement.
	if(!masterCheckBox) {
		throw "Null or undefined argument in CheckBoxListControl<init>(masterCheckBox) ";
	}
	this.master = masterCheckBox;
	this.listElements = [];
	this.init();
}

CheckBoxListControl.prototype.addListElement= function(checkBox) {
 	var listControl = this;
	if( typeof(checkBox)==undefined ) {
		throw "Null or undefined argument in CheckBoxListControl.addListElement(checkBox)";
	}
	listControl.listElements.push(checkBox);

    dojo.connect(checkBox,"onclick",function(onClickEvent) {
  	    if(onClickEvent.target!=null && !onClickEvent.target.checked) {
            listControl.master.checked = false;
        }
    });
};

CheckBoxListControl.prototype.init = function() {
	dojo.connect(this.master,"onclick", this, function(onClickEvent) {
            this.updateChildrenState(onClickEvent.target.checked);
	});
}

CheckBoxListControl.prototype.updateChildrenState = function(/*boolean*/ checked) {
    for(var i=0; i < this.listElements.length ; i++ ) {
        var childCheckBox = this.listElements[i];
        if(childCheckBox.disabled) {
            return;
        }

        childCheckBox.checked = checked;

        if(childCheckBox.onclick == undefined) {
            childCheckBox.onclick = function(/*event*/event){};
        }

        childCheckBox.onclick({
            target : childCheckBox
        });
    }
}

// Programmatically simulate a state change.
CheckBoxListControl.prototype.forceStateChange = function(/*boolean*/ checked) {
    this.master.checked = checked;
    this.updateChildrenState(checked);
}

/*
References:
==========
[1] http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Global_Properties:undefined
[2] http://developer.mozilla.org/en/docs/Core_JavaScript_1.5_Reference:Operators:Comparison_Operators
*/
