/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/


if(!dojo._hasResource["twms.widget.Tree"]){
    dojo._hasResource["twms.widget.Tree"]=true;
    dojo.provide("twms.widget.Tree");
    dojo.require("dijit.Tree");
    dojo.require("twms.widget.ServiceProcedureNode");
    dojo.declare("twms.widget.Tree",dijit.Tree,{
        
        selectedNodesList : [],
        multipleSelectionAllowed : true,
        selectedNode : null,
        defaultSelectedNodes: [],
        defaultSelectedRadio: null,
        isTreeModified: false,

        getLabel: function(item) {
            return item.definition.label;
        },
        _createTreeNode: function(childParams) {
            if(childParams.item.root)
                return this.inherited(arguments);
            return this._getChild(childParams);
        },
        _onClick: function(/*TreeNode*/ nodeWidget, /*Event*/ evt)  {
            this._clickTarget = evt.target;
            //If the target was a checkbox, ignore focusing the widget
            if(typeof this._clickTarget !== 'undefined' && this._clickTarget.nodeName=='INPUT'){
                var item = nodeWidget.item;
                item.completeName = this._getCompleteName(nodeWidget, item);
                if(this._clickTarget.checked){
                    this._addSelectedNode(nodeWidget);
                }else{ // user has unchecked it, remove it from nodes list
                    this.selectedNodesList = dojo.filter(this.selectedNodesList, function(node){
                        return nodeWidget.item.id != node.item.id;
                    });
                }
                this.isTreeModified = true;
                evt.stopPropagation();
                return;
            }
            nodeWidget.isClicked = true; // to indicate the + sign is clicked
            return this.inherited(arguments);
        },
        _expandNode: function(/*_TreeNode*/ node, /*Boolean?*/ recursive){ // overriding this private function to aviod recurise call to load children 
            var model = this.model,
                item = node.item,
                _this = this;
            switch(node.state){
                case "UNCHECKED":
                    // need to load all the children, and then expand
                    node.markProcessing();

                    // Setup deferred to signal when the load and expand are finished.
                    // Save that deferred in this._expandDeferred as a flag that operation is in progress.
                    var def = (node._expandNodeDeferred = new dojo.Deferred());
                    // Get the children
                    model.getChildren(
                        item,
                        function(items){
                            node.unmarkProcessing();
                            var scid = node.setChildItems(items);
                            if(node.item.root || node.expandOnLoad || node.isClicked){
                                    delete node.isClicked;
                                    var ed = _this._expandNode(node, true);
                                    
                                    // After the above two tasks (setChildItems() and recursive _expandNode()) finish,
                                    // signal that I am done.
                                    scid.addCallback(function(){
                                        ed.addCallback(function(){
                                            def.callback();
                                        })
                                    });
                            }
                        },
                        function(err){
                            console.error(_this, ": error loading root children: ", err);
                        }
                    );
                    if(node.item.root){ // need to call onLoad once the root is loaded
                        _this.onLoad();
                    }
                    break;
                default:	// "LOADED"
                    // data is already loaded; just expand node
                    def = (node._expandNodeDeferred = node.expand());

                    this.onOpen(node.item, node);

                    this._state(node, true);
                }
            return def;	// dojo.Deferred
            
        },
        _getCompleteName: function(node, item){
            var name;
            while(!item.root){
                if(name)
                    name = node.label.replace(" (" + item.definition.code + ")", "") + " - " + name;
                else
                    name = node.label.replace(" (" + item.definition.code + ")", "");
                node = node.getParent();
                item = node.item;
            }  
            return name;
        },
        _addSelectedNode : function(/*dataStructure node [nodeType : leaf]*/ node) {
            if(this.model.treeType === 'JOBCODE'){
                if(this.multipleSelectionAllowed)
                    this.selectedNodesList.push(node);      
                else {
                    this._removeSelectedNode(this.selectedNodesList[0]);
                    this.selectedNodesList.push(node); 
                }
            }else{
                this.selectedNode = node;
            }                
        },      
        onLoad:function(){ // call back function once tree is loaded, here we will set the val for nodes            
            if(this.defaultSelectedRadio || this.defaultSelectedNodes.length > 0){
                this._expandSelectedNodes(this.rootNode);
            }
            this.treeLoaded();
        },
        _expandSelectedNodes: function(node){
            var children = node.getChildren();
            var _this = this;
            dojo.forEach(children, function(node){
                if(node.expandOnLoad && !node.isExpanded && node.item.nodeType == 'node'){
                    this._expandNode(node).addCallback(function(){
                        _this._expandSelectedNodes(node);
                    });
                }else {
                    this._collapseNode(node);
                }
            }, this);
        },
        treeLoaded : function(){
//            callBack function for renderer to say tree is loadded so the rows can be set up properly on UI
        },
        _findNodeByItemId: function(item, node){
            if(item.id === node.item.id) {
                return node;
            }
            var children = node.getChildren();
            if(children){
                var treeNode = null;
                for(var i = 0; i < children.length;i++){
                    if(item.id === children[i].item.id){
                        treeNode = children[i];
                    }else{
                        treeNode = this._findNodeByItemId(item, children[i]);
                    }
                    if(treeNode != null){
                        break;
                    }
                }
                return treeNode;
            }
            return null;
        },
        _findNodeByCompleteCode: function(completeCode, node){
            if(completeCode === node.item.completeCode) {
                return node;
            }
            var children = node.getChildren();
            if(children){
                var treeNode = null;
                for(var i = 0; i < children.length;i++){
                    if(completeCode === children[i].item.completeCode){
                        treeNode = children[i];
                    }else{
                        treeNode = this._findNodeByCompleteCode(completeCode, children[i]);
                    }
                    if(treeNode != null){
                        break;
                    }
                }
                return treeNode;
            }
            return null;
        },
        _removeSelectedNode : function(/*dataStructure node [nodeType : leaf]*/ node) {
            var indexOfNode = dojo.indexOf(this.selectedNodesList, node);
            if(indexOfNode != -1) {
                this.selectedNodesList.splice(indexOfNode, 1);
            }
        },
        _isDefaultSelectedOrExpanded: function(currentItem){
            var selected = false;
            var expanded = false;
            if(this.defaultSelectedRadio){
                expanded = (this.defaultSelectedRadio.indexOf(currentItem.completeCode) === 0);
                selected = currentItem.completeCode === this.defaultSelectedRadio;
            }
            if(this.defaultSelectedNodes.length > 0){
                selected = dojo.filter(this.defaultSelectedNodes, function(item){
                    if(!expanded){
                        expanded = (item.completeCode.indexOf(currentItem.completeCode) === 0);
                    }
                    return currentItem.completeCode === item.completeCode;
                }).length > 0;
            }
            
            
            return {isDefaultSelected:selected,isExpanded:expanded};
        },
        _getChild: function(/*assoc array*/childParams) {
            var child;
            var itemNode = childParams.item;
            var obj = this._isDefaultSelectedOrExpanded(itemNode);
            var isSelected = obj.isDefaultSelected;
            if(itemNode.faultCode && itemNode.faultCode.id){
                child = new twms.widget.FaultCodeNode(
                    dojo.mixin({
                        tree: this.tree,
                        label: this.tree.getLabel(childParams.item),
                        expandOnLoad: obj.isExpanded,
                        defaultSelected: isSelected
                    }, childParams)
                    );
            } else if(itemNode.nodeType === 'leaf'){
                child = new twms.widget.ServiceProcedureLeaf(
                    dojo.mixin({
                        tree: this.tree,
                        label: this.tree.getLabel(childParams.item),
                        expandOnLoad: obj.isExpanded,
                        multipleSelectionAllowed: this.multipleSelectionAllowed,
                        defaultSelected: isSelected
                    }, childParams)
                    );
            } else{
                child = new twms.widget.ServiceProcedureBranch(
                    dojo.mixin({
                        tree: this.tree,
                        label: this.tree.getLabel(childParams.item),
                        expandOnLoad: obj.isExpanded,
                        defaultSelected: isSelected
                    }, childParams)
                    );
            }
            return child;
        }
    });
    
}

