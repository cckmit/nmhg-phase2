/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["twms.widget.RuleBuilderSelect"]){
    dojo._hasResource["twms.widget.RuleBuilderSelect"]=true;
    dojo.require("dojo.data.ItemFileReadStore");
    dojo.require("twms.widget.Select");
    dojo.provide("twms.widget.RuleBuilderSelect");
    dojo.declare("twms.widget.RuleBuilderSelect",twms.widget.Select,{
        useCallBackForPopulation:false,
        handleEmptyValues:true,
        renderingMode:"",
        searchAttr:"label",
        legacyDataMode:true,
        _alternateRenderer:null,
        _lastSetValue:null,
        postCreate:function(){
            this.activateAlternateRendering();
            this.inherited("postCreate",arguments);
        },
        onChange:function(_1){
            if(this.handleEmptyValues&&!_1){
                this.revertToValidValue();
                return;
            }else{
                this._lastSetValue=_1;
            }
            this.inherited("onChange",arguments);
        },
        revertToValidValue:function(_2){
            var _3=_2||this._lastSetValue;
            if(_3){
                this.set("value",_3,true);
            }else{
                this.selectFirstValueIfAny();
            }
        },
    selectFirstValueIfAny:function(){
        var _4=this.store; // since this will be an mem store no need of below check
//        if(!_4._loadFinished){
//            _4._fetchItems({},function(_5,_6){},function(e,_8){});
//        }
        var _9=_4.data;
        if(_9.length>0){
            var _a=_9[0].key;
            this.set("value",_a,true);
        }
    },
    activateAlternateRendering:function(){
        if("readOnly"==this.renderingMode){
            var _b=document.createElement("span");
            this._alternateRenderer={
                domNode:_b,
                setValue:function(_c){
                    _b.innerHTML=(_c)?dojo.string.trim(_c):"";
                }
            };
            
        dojo.dom.insertAfter(this._alternateRenderer.domNode,this.domNode);
        this.showOrHideAlternateRenderer(true);
    }
    },
showOrHideAlternateRenderer:function(_d){
    dojo.html.setDisplay(this._alternateRenderer.domNode,_d);
    dojo.html.setDisplay(this.domNode,!_d);
},
_hideResultList:function(){
    this.inherited("_hideResultList",arguments);
    if(this._alternateRenderer){
        this.showOrHideAlternateRenderer(true);
    }
},
setValue:function(_e){
    this.inherited("setValue",arguments);
    if(this._alternateRenderer){
        this._alternateRenderer.setValue(this.getDisplayedValue());
    }
}
});
}