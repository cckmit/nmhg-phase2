/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["twms.widget.Tooltip"]){
    dojo._hasResource["twms.widget.Tooltip"]=true;
    dojo.require("dijit.Tooltip");
    dojo.provide("twms.widget.Tooltip");
    dojo.declare("twms.widget.Tooltip",dijit.Tooltip,{
        disabled:false,
        open:function(_1){
            if(!this.disabled&&!dojo.string.isBlank(this.label)){
                this.inherited("open",arguments);
            }
        },
    setLabel:function(_2){
        this.label=String(_2);
    },
    clearLabel:function(){
        this.setLabel("");
    },
    setDisabled:function(_3){
        this.disabled=_3;
        if(_3){
            this.close();
        }
    }
    });
}