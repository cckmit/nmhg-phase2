/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["twms.widget.Dialog"]){
    dojo._hasResource["twms.widget.Dialog"]=true;
    dojo.require("dijit.Dialog");
    dojo.provide("twms.widget.Dialog");
    dojo.declare("twms.widget.Dialog",dijit.Dialog,{
        templateString:"<div class=\"dijitDialog\">\r\n\t<div dojoAttachPoint=\"titleBar\" class=\"dijitDialogTitleBar\" tabindex=\"0\" waiRole=\"dialog\">\r\n\t<span dojoAttachPoint=\"titleNode\" class=\"dijitDialogTitle\">${title}</span>\r\n\t<span dojoAttachPoint=\"closeButtonNode\" class=\"dijitDialogCloseIcon\" dojoAttachEvent=\"onclick: hide\">\r\n\t\t<span dojoAttachPoint=\"closeText\" class=\"closeText\">x</span>\r\n\t</span>\r\n\t</div>\r\n\t\t<div dojoAttachPoint=\"containerNode\" class=\"dijitDialogPaneContent\"></div>\r\n\t<span dojoAttachPoint=\"tabEnd\" tabindex=\"0\"></span>\r\n</div>\r\n",
        title:"",
        closable:true,
        bgColor:"white",
        bgOpacity:0.5,
        toggle:"fade",
        toggleDuration:250,
        formNode:"",
        closeNode:"",
        _destroyConnects:null,
        constructor:function(){
            this._destroyConnects=[];
        },
        postCreate:function(){
            var _1=dijit.getEnclosingWidget(this.domNode.parentNode);
            if(_1){
                this._destroyConnects.push(dojo.connect(_1,"destroyDescendants",this,"_cleanupAndDestroy"));
                this._destroyConnects.push(dojo.connect(_1,"destroy",this,"_cleanupAndDestroy"));
            }
            this.inherited("postCreate",arguments);
            dojo.connect(this.domNode,"onkeyup",this,"_onKeyAction");
            if(this.closable){
                dojo.connect(this.closeButtonNode,"onclick",this,"hide");
            }else{
                dojo.html.hide(this.closeButtonNode);
            }
            if(!dojo.string.isBlank(this.formNode)){
                this.formNode=dojo.byId(this.formNode);
            }
            if(this.closable&&!dojo.string.isBlank(this.closeNode)){
                this.closeNode=dojo.byId(this.closeNode);
                dojo.connect(this.closeNode,"onclick",this,"hide");
            }
            this._moveInsideForm();
            dojo.connect(window,"resize",this,function(_2){
                this.layout();
            });
        },
        _cleanupAndDestroy:function(_3){
            dojo.forEach(this._destroyConnects,function(_4){
                dojo.disconnect(_4);
            });
            this.destroyRecursive();
        },
        _onKeyAction:function(_5){
            switch(_5.keyCode){
                case 27:
                    this.hide();
                    dojo.stopEvent(_5);
                    break;
                case 13:
                    this.doDefault();
                    dojo.stopEvent(_5);
                    break;
            }
        },
    doDefault:function(){},
        show:function(){
        this._moveToEndOfBody();
        twms.widget.Dialog.superclass.show.apply(this,[]);
    },
    hide:function(){
        this._moveInsideForm();
        if(this.open) // this check is required as the fade is called in deffered 
            twms.widget.Dialog.superclass.hide.apply(this,[]);
    },
    _moveInsideForm:function(){
        this._moveTo(this.formNode);
    },
    _moveToEndOfBody:function(){
        this._moveTo(dojo.body());
    },
    _moveTo:function(_6){
        if(_6){
            _6.appendChild(this.domNode);
        }
    },
    setTitle:function(_7){
        this.titleNode.innerHTML=_7;
    }
    });
}