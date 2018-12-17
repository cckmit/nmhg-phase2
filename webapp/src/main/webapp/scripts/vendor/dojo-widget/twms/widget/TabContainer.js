if(!dojo._hasResource["twms.widget.TabContainer"]){
    dojo._hasResource["twms.widget.TabContainer"]=true;
    dojo.require("dijit.layout.TabContainer");
    dojo.provide("twms.widget.TabContainer");
    dojo.declare("twms.widget.TabContainer",dijit.layout.TabContainer,{
        _makeController: function(/*DomNode*/ srcNode){
            // copied from dijit.layout.TabContainer to customize the tab labels
            var cls = this.baseClass + "-tabs" + (this.doLayout ? "" : " dijitTabNoLayout"),
            TabController = dojo.getObject(this.controllerWidget);
            
            return new TabController({
                buttonWidget: "twms.widget.TabButton",
                id: this.id + "_tablist",
                dir: this.dir,
                lang: dojo.lang,
                textDir: this.textDir,
                tabPosition: this.tabPosition,
                doLayout: this.doLayout,
                containerId: this.id,
                "class": cls,
                nested: this.nested,
                useMenu: this.useMenu,
                useSlider: this.useSlider,
                tabStripClass: this.tabStrip ? this.baseClass + (this.tabStrip ? "":"No") + "Strip": null
            }, srcNode);
        }
    });
    dojo.declare("twms.widget.TabButton",dijit.layout._TabButton,{
        STATIC:{
            MAX_LABEL_LENGTH:13,
            APPEND_STRING:"..."
        },
        postCreate:function(){
            twms.widget.TabButton.superclass.postCreate.apply(this,arguments);
            this.setLabel(this.label);
        },
        setLabel:function(_1){
            this.label=_1;
            this.containerNode.title=_1;
            this.containerNode.innerHTML=this._getTruncateLabel();
        },
        _getTruncateLabel:function(){
            var _2=this.label;
            if(_2.length>this.STATIC.MAX_LABEL_LENGTH){
                return _2.substring(0,10)+this.STATIC.APPEND_STRING;
            }
            return _2;
        }
    });
}
