dojo.require("twms.widget.Tree");
dojo.require("dijit._Templated");
dojo.require("dijit.form.CheckBox");
dojo.provide("twms.widget.ServiceProcedureNode");
dojo.provide("twms.widget.ServiceProcedureLeaf");
dojo.provide("twms.widget.ServiceProcedureBranch");
dojo.provide("twms.widget.FaultCodeLeaf");

var _dummySpan = document.createElement("span");

dojo.declare("twms.widget.ServiceProcedureNode", dijit._TreeNode, {
    _item: null,
    _nodeSelector: null,
    _nodeIconClass: null,
    defaultSelected: false,
    
    constructor: function(/*object*/ childParams) {
        this._item = childParams.item;
        this.openOnClick = true;
        this._item._get = function(/*string*/propertyName) {
            return this[propertyName];
        }
    },
//    buildRendering : function() {
//        this.inherited("buildRendering", arguments);
//        var contentSpan = _dummySpan.cloneNode(false);
//        dojo.addClass(contentSpan, "servicePrecedureNode " + this._nodeIconClass);
//        contentSpan.innerHTML=this._item._get("title");
//        dojo.place(nodeSelectorNode, this.labelNode, 'before');
//    },

    setLabelNode : function() {
        // This is intentionally a no-op.
    }
});

dojo.declare("twms.widget.ServiceProcedureLeaf", twms.widget.ServiceProcedureNode, {
    multipleSelectionAllowed:null,
    nameSpace: null,
    _nodeIconClass: "newFileIcon",

    buildRendering : function() {
        //Here is my code to create checkbox button..
        this.inherited("buildRendering", arguments);
        if(this.multipleSelectionAllowed){
		     this._nodeSelector = new dijit.form.CheckBox({checked: this.defaultSelected, disabled:this.defaultSelected});
		}else{
		    this._nodeSelector = new dijit.form.RadioButton({checked: this.defaultSelected});
		}
		var nodeSelectorNode = this._nodeSelector.domNode;
		this._nodeSelector._parentNode = this;
        dojo.style(nodeSelectorNode, "verticalAlign", "middle");
        dojo.place(nodeSelectorNode, this.labelNode, 'before');
    }
});

dojo.declare("twms.widget.ServiceProcedureBranch", twms.widget.ServiceProcedureNode, {
    _nodeIconClass: "directoryIcon",

    buildRendering : function() {
        this.inherited("buildRendering", arguments);
    }
});

dojo.declare("twms.widget.FaultCodeNode", twms.widget.ServiceProcedureBranch, {

    buildRendering : function() {
        //Here is my code to create radio button..
        this.inherited("buildRendering", arguments);
        if(!this._item.root){
            this._nodeSelector = new dijit.form.RadioButton({
                name: "faultCodeNode_radio", // we don't use this. only setting it to ensure
                // that only one radio button gets selected at a time.
                checked: this.defaultSelected
            });
            this._nodeSelector._parentNode = this;
            var nodeSelectorNode = this._nodeSelector.domNode;
            dojo.style(nodeSelectorNode, "verticalAlign", "middle");
            dojo.place(nodeSelectorNode, this.labelNode, 'before');
            
        }
    }
});
