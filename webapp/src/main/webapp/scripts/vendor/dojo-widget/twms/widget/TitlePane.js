/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["twms.widget.TitlePane"]){
    dojo._hasResource["twms.widget.TitlePane"]=true;
    dojo.provide("twms.widget.TitlePane");
    dojo.require("dijit.TitlePane");
    dojo.declare("twms.widget.TitlePane",dijit.TitlePane,{
        postCreate:function(){
            this.inherited("postCreate",arguments);
            this.focusNode.removeAttribute("tabIndex");
        }
    });
}