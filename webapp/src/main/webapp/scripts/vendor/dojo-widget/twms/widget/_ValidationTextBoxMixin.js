/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["twms.widget._ValidationTextBoxMixin"]){
    dojo._hasResource["twms.widget._ValidationTextBoxMixin"]=true;
    dojo.require("twms.widget.Tooltip");
    dojo.provide("twms.widget._ValidationTextBoxMixin");
    dojo.declare("twms.widget._ValidationTextBoxMixin",null,{
        required:true,
        cssClassForReadOnly:"dijitTextBoxReadOnly",
        validationNotificationTopics:"",
        _validationNotificationTopicsArray:[],
        _validationTopicsProcessingDone:false,
        _validationTooltip:null,
        _lastValidatedValue:null,
        validate:function(_1){
            if(!this._validationTopicsProcessingDone){
                this._initValidationNotificationTopics();
                this._validationTopicsProcessingDone=true;
            }
            if(!this.required&&dojo.string.isBlank(this.textbox.value)){
                this._clearValidationErrors();
            }else{
                this.inherited("validate",arguments);
            }
            var _2=this.isValid();
            this._adjustValidationStyle(_2);
            var _3=this.get('displayedValue');
            if(_3!==this._lastValidatedValue){
                this._notifyValidationTopics(_2);
            }
            this._lastValidatedValue=_3;
            return _2;
        },
        isValid:function(_4){
            if(dojo.string.isBlank(this.textbox.value)){
                return !this.required;
            }else{
                return this.inherited("isValid",arguments);
            }
        },
    setDisabled:function(_5,_6){
        this.inherited("setDisabled",arguments);
        this._disableValueSubmission(arguments.length>1?_6:_5);
    },
    setReadOnly:function(_7){
        this.setDisabled(_7,false);
        var _8=_7?dojo.addClass:dojo.removeClass;
        _8(this.domNode,this.cssClassForReadOnly);
    },
    setValidationNotificationTopics:function(_9){
        this.validationNotificationTopics=_9;
        this._initValidationNotificationTopics();
    },
    displayMessage:function(_a){
        this._clearTooltip();
        if(_a){
            this._showTooltip(_a);
        }
    },
    destroyRecursive:function(_b){
        if(this._validationTooltip){
            this._validationTooltip.destroyRecursive(_b);
        }
        this.inherited("destroyRecursive",arguments);
    },
    _disableValueSubmission:function(_c){
        this.domNode.disabled=_c;
        if(this.valueNode){
            this.valueNode.disabled=_c;
        }
    },
    _clearValidationErrors:function(){
        this.displayMessage("");
        this.state="";
        this._setStateClass();
        dijit.setWaiState(this.focusNode,"invalid","false");
    },
    _adjustValidationStyle:function(_d){
        var _e=_d?dojo.addClass:dojo.removeClass;
        var _f=this.iconNode;
        if(typeof _f != 'undefined'){
            _e(_f,"dijitValidationIconHidden");
            _e(_f.parentNode,"dijitValidationIconFieldNarrow");
        }
    },
    _initValidationNotificationTopics:function(){
        if(!dojo.string.isBlank(this.validationNotificationTopics)){
            this._validationNotificationTopicsArray=this.validationNotificationTopics.split(",");
        }
    },
_notifyValidationTopics:function(_10){
    dojo.forEach(this._validationNotificationTopicsArray,function(_11){
        dojo.publish(_11,[{
            sourceId:this.id,
            isValid:_10,
            value:this.getValue(),
            displayedValue:this.get('displayedValue')
            }]);
    },this);
},
_showTooltip:function(_12){
    if(!this._validationTooltip){
        this._validationTooltip=new twms.widget.Tooltip({
            connectId:[this.domNode],
            label: _12
            });
    }
//    this._validationTooltip.open(this.domNode); // need not open the tool tip as it will be openen on mouse over 
},
_clearTooltip:function(){
    if(this._validationTooltip){
        this._validationTooltip.close();
        if(this.isValid()){
            this._validationTooltip.label="";
        }
    }
}
});
}