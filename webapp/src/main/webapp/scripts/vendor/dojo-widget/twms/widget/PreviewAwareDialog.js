/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["twms.widget.PreviewAwareDialog"]){dojo._hasResource["twms.widget.PreviewAwareDialog"]=true;dojo.require("twms.widget.Dialog");dojo.provide("twms.widget.PreviewAwareDialog");dojo.declare("twms.widget.PreviewAwareDialog",twms.widget.Dialog,{show:function(){manageTableMinimize(getFrameAttribute("TST_ID"));twms.widget.PreviewAwareDialog.superclass.show.call(this);},hide:function(){twms.widget.PreviewAwareDialog.superclass.hide.call(this);manageTableRestore(getFrameAttribute("TST_ID"));}});}