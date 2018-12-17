/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["twms.widget.ValidationTextBox"]){
    dojo._hasResource["twms.widget.ValidationTextBox"]=true;
    dojo.require("dijit.form.ValidationTextBox");
    dojo.require("twms.widget._ValidationTextBoxMixin");
    dojo.provide("twms.widget.ValidationTextBox");
    dojo.declare("twms.widget.ValidationTextBox",[dijit.form.ValidationTextBox,twms.widget._ValidationTextBoxMixin],{
        invalidMessage:"Please enter a valid value.",
        missingMessage:"Please enter a valid value."
    });
}