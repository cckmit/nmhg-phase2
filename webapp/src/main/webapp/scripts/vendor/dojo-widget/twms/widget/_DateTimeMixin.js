/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["twms.widget._DateTimeMixin"]){
    dojo._hasResource["twms.widget._DateTimeMixin"]=true;
    dojo.require("twms.widget._ValidationTextBoxMixin");
    dojo.require("dojo.date.stamp");
    dojo.provide("twms.widget._DateTimeMixin");
    dojo.declare("twms.widget._DateTimeMixin",twms.widget._ValidationTextBoxMixin,{
        formattedValue:"",
        constructor:function(){
            this.constraints.datePattern="MM/dd/yyyy";
        },
        postCreate:function(){
            if(!dojo.string.isBlank(this.formattedValue)){
                this.fixFormattedValue();
                this.set('value', this.formattedValue, false);
            }
            this.inherited("postCreate",arguments);
        },
        setValue:function(_1,_2){
            if(dojo.isString(arguments[0])){
                arguments[0]=this.parse(_1,this.constraints);
            }
            this.inherited("setValue",arguments);
        },
        serialize:function(_3,_4){
            return dojo.date.locale.format(_3,_4).toLowerCase();
        },
        fixFormattedValue:function(){
            var arr = this.formattedValue.split('-');
            if(arr.length == 3){
                if(arr[1].length === 1) arr[1]="0"+arr[1];
                if(arr[2].length === 1) arr[2]="0"+arr[2];
                this.formattedValue = arr.join("-");
            }
        }
    });
}