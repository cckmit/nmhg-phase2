/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["twms.widget.DateTextBox"]){
    dojo._hasResource["twms.widget.DateTextBox"]=true;
    dojo.provide("twms.widget.DateTextBox");
    dojo.require("dijit.form.DateTextBox");
    dojo.require("twms.widget._DateTimeMixin");
    dojo.declare("twms.widget.DateTextBox",[dijit.form.DateTextBox,twms.widget._DateTimeMixin],{
        invalidMessage:"Please specify a date in the format 'mm/dd/yyyy'.",
        postCreate:function(){
            this.inherited(arguments);
            this.formInvalidMessage();
        },
        setDatePattern:function(userDateFormat){
            if(userDateFormat){
                dojo.mixin(this.constraints,{
                    datePattern:userDateFormat
                });
                this.formInvalidMessage();
            }
        },
    formInvalidMessage:function(){
        var datePattern=this.constraints?this.constraints.datePattern:null;
        if(datePattern){
            this.invalidMessage="Please specify date in the format '"+datePattern.toLowerCase()+"'.";
        }
    }
    });
}