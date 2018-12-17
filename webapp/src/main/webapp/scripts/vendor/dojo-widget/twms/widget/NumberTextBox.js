/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["twms.widget.NumberTextBox"]){dojo._hasResource["twms.widget.NumberTextBox"]=true;dojo.require("dijit.form.NumberTextBox");dojo.require("twms.widget._ValidationTextBoxMixin");dojo.provide("twms.widget.NumberTextBox");dojo.declare("twms.widget.NumberTextBox",[dijit.form.NumberTextBox,twms.widget._ValidationTextBoxMixin],{invalidMessage:"Please enter a valid number.",lessThanMinMessage:"Please enter a number greater than or equal to ${0}.",moreThanMaxMessage:"Please enter a number lesser than or equal to ${0}.",betweenMessage:"Please enter a number between ${0} and ${1}, both inclusive.",rangeCheck:function(_1,_2){this.inherited("rangeCheck",arguments);var _3=(typeof _2.min!="undefined");var _4=(typeof _2.max!="undefined");if(_3||_4){var _5=_3&&(_1<_2.min);var _6=_4&&(_1>_2.max);var _7=_5||_6;if(_7){if(_3&&_4){this.rangeMessage=dojo.string.substitute(this.betweenMessage,[_2.min,_2.max]);}else{var _8=_5?_2.min:_2.max;var _9=_5?this.lessThanMinMessage:this.moreThanMaxMessage;this.rangeMessage=dojo.string.substitute(_9,[_8]);}return false;}}return true;}});}