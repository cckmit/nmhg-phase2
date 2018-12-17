var SP_TREE_VARS = {
    CONSTANTS : {
        ASSEMBLY : "Assembly",
        SERVICE_PROCEDURE : "ActionNode",
        ROOT : "root",
        MAX_ASM_ROW_DEPTH : 4
    },
    /**
     * This array, will hold the names of all the classes, which are used here to point to a particular element
     * The lookup is done based on the css class name.
     */
    ELEMENT_CLASSES : {
        ADD_NEW_ASM_ROW : "addNewAsmRowButton",
        DROP_ROW : "dropRow",
        ASM_DEF : "asmDefHolder",
        TREAD_BUCKET : "treadBucketHolder",
        LAST_UPDATED : "lastUpdateHolder",
        SP_DEF : "spDefHolder",
        LABOUR_HRS : "labourHrsHolder",
        FOR_CAMPAIGN : "forCampaigns",

        HILIGHTABLE : "hilightable",
        HOVERED : "hovered",

        INVALID : "invalidRow"
    },
    RESOURCE_TYPE : {
        COMBO_BOX : "ComboBox",
        FREE_TEXT : "FreeText"
    },
    resourceManager : null,//will hold resource manager
    /**
     * Objects below... Will be bound by the page... late bound!!!
     */
    definitionCreator : null,//will hold the common definition creator.
    treeDataManager : null,//will hold the treeDataManager...
    treadData : null,//will hold the tread data.(will be late bound)
    errorParams : null//will hold t:jsVar varables for errorMessages, and wrapper...(late bound)
};

dojo.addOnLoad(function() {
    SP_TREE_VARS.resourceManager = new twms.resources.ResourceManager();
});

dojo.declare("twms.spTree.TreeDataManager", null, {

    tree : null,

    constructor : function(/*String (event topic to load the new tree on)*/ loadOn) {
        dojo.subscribe(loadOn, this, "loadTree");
    },

    loadTree : function(/*JOSN tree*/ tree) {
        this.tree = tree;
    },

    /**
     * Takes a node, which is part of the tree.. and returns an array of ids, of its peer nodes having same instanceOf value.
     */
    getPeerNodeDefinitionIds : function(/*Node (treeNode [instanceOf = 'ServiceProcedure'/'Assembly'])*/ node) {
        if(node.instanceOf != SP_TREE_VARS.CONSTANTS.SERVICE_PROCEDURE && node.instanceOf != SP_TREE_VARS.CONSTANTS.ASSEMBLY) {
            throw new Error("_getPeerNodeIds has recived instanceOf as '" + node.instanceOf + "' which is not a known value.");
        }
        var peerNodes = this._findPeerNodes(node, this.tree);
        var nodeIds = new Array();
        for(var i in peerNodes) {
            if(peerNodes[i]._treeTableKey != node._treeTableKey) {//makes sure that the node's own id is not passed.
                nodeIds.push(peerNodes[i].definition.id);
            }
        }
        return nodeIds;
    },

    /**
     * This function also gives the current node as it finds peer nodes.
     * and i havn't fixed it here considering the performance. Will do it in the public wrapper.
     */
    _findPeerNodes : function(/*Tree Fragment*/ node, /*tree dataStructure*/ treeFragment) {
        var parentNode = this._findNodesParent(node, treeFragment);
        return (node.instanceOf === SP_TREE_VARS.CONSTANTS.ASSEMBLY) ?
               parentNode.asmChildren : parentNode.spChildren;
    },

    _isChildPresent : function(/*Tree Fragment*/ child, /*Tree Fragment*/ testParent) {
        var childrenList = (child.instanceOf === SP_TREE_VARS.CONSTANTS.ASSEMBLY) ?
                           testParent.asmChildren : testParent.spChildren;
            return childrenList &&
                   dojo.some(childrenList, function(childInList) {
                        return (child._treeTableKey === childInList._treeTableKey);
                   });
    },

    _findNodesParent : function(/*Tree fragment*/ node, /*Tree Fragment*/ testParent) {
        if(this._isChildPresent(node, testParent)) {
            return testParent;
        }
        for(var i in testParent.asmChildren) {
            var parentNode = this._findNodesParent(node, testParent.asmChildren[i]);
            if(parentNode) return parentNode;
        }
    }
});

dojo.declare("twms.spTree.DefinitionCreator", null, {

    _dialog : null,
    _processing : false,

    _labelInput : null,
    _depthInput : null,

    /**
     * Expected params format : {dialog : "Widget (dijit.Dialog)",
     *                           labelInput : "HTML input",
     *                           depthInput : "HTML input",
     *                           createButton : "HTML button",
     *                           cancelButton : "HTML button"}
     */
    constructor : function(/*Assoc array*/ params) {
        this._dialog = params.dialog;
        this._labelInput = params.labelInput;
        this._depthInput = params.depthInput;
        dojo.connect(this._dialog.domNode, "onkeyup", this, "_handleKeyAction");
        dojo.connect(params.createButton, "onclick", this, "_requestCreation");
        dojo.connect(params.cancelButton, "onclick", this, "_hideDialog");
    },

    _handleKeyAction : function(event) {
        if(event.keyCode === 27) {//esc key
            this._hideDialog();
        } else if(event.keyCode === 13) {//enter key
            this._requestCreation();
        }
    },

    _showDialog : function() {
        this._dialog.show();
    },

    _hideDialog : function() {
        this._labelInput.value = "";
        this._depthInput.value = "";
        this._dialog.hide();
        this._processing = false;
    },

    _onCompletion : function(type, data) {
        this._processing = false;
        this._hideDialog();
    },

    _requestCreation : function() {/*To be overridden by the using function*/},

    _createNewDef : function(/*String*/ initialLabel, /*int*/ initialDepth) {
        if(this._processing)  return;
        this._labelInput.value = initialLabel;
        this._depthInput.value = initialDepth;
        this._showDialog();
    },

    createNewAsmDef : function(/*String*/ label, /*int*/ depth, /*Function*/ callback) {
        this._createNewDef(label, depth);
        var self = this;
        this._requestCreation = function() {
            twms.spTree.AJAXer.createAsmDef(this._labelInput.value, this._depthInput.value, function(data) {
                self._onCompletion();
                callback(data);
            });
        }
    },

    createNewSpDef : function(/*String*/ label, /*int*/ depth, /*Function*/ callback) {
        this._createNewDef(label, depth);
        var self = this;
        this._requestCreation = function() {
            twms.spTree.ajaxer.createSpDef(this._labelInput.value, this._depthInput.value, function(data) {
                self._onCompletion();
                callback(data);
            });
        }
    }
});

dojo.declare("twms.spTree.NodeAgent", tavant.twms.treeTable.DefaultNodeAgent, {

    _controller : null,//controller object

    constructor : function(/*Object (controller)*/ controller) {
        this._controller = controller;
    },

    /**
     * Should return a JSON node with some default values, that the row actually expects.
     */
    getStub : function(/*String (row type)*/ rowType) {
        if (rowType === SP_TREE_VARS.CONSTANTS.ASSEMBLY) {
            return {
                "completeCode":"",
                "treadBucket":{"id":"", "label":""},
                "instanceOf":SP_TREE_VARS.CONSTANTS.ASSEMBLY,
                "definition":{"code":"","label":"","id":""},
                "nodeType":"leaf",
                "lastUpdated":"",
                "id":0,
                "asmChildren":[],
                "spChildren":[]
            };
        } else if (rowType === SP_TREE_VARS.CONSTANTS.SERVICE_PROCEDURE) {
            return {
                "completeCode":"",
                "definition":{"code":"","label":"","id":""},
                "nodeType":"leaf",
                "suggestedLabourHours":0,
                "forCampaigns":false,
                "instanceOf":SP_TREE_VARS.CONSTANTS.SERVICE_PROCEDURE,
                "id":0
            };
        }
    },

    getRowInstance : function(/*JSON node*/ nodeObject, /*AbstractParentRow*/ parentRow) {
        var className = null;
        var template = null;
        if(nodeObject.instanceOf === SP_TREE_VARS.CONSTANTS.ASSEMBLY) {
            className = twms.spTree.AsmRow;
            template = asmRowTemplate;
        } else if(nodeObject.instanceOf === SP_TREE_VARS.CONSTANTS.SERVICE_PROCEDURE){
            className = twms.spTree.SpRow;
            template = spRowTemplate;
        }
        if (!className) {
            throw new Error("Row's class was not defined.");
        }
        return new className(this._controller, nodeObject, parentRow, template, parentRow.getDepth() + 1);
    },

    getRowTypeValue : function(/*JSON node*/ nodeObject) {
        return nodeObject.instanceOf;
    },

    /**
     * This function should be overridden by the class extending this,
     * and should return collection or collections of child nodes.
     */
    getChildCollections : function(/*JSON node*/ nodeObject) {
        if (nodeObject.instanceOf === SP_TREE_VARS.CONSTANTS.ASSEMBLY) {
            return [nodeObject.spChildren, nodeObject.asmChildren];
        } else if(nodeObject.nodeType === SP_TREE_VARS.CONSTANTS.ROOT) {
            return [nodeObject.asmChildren];
        } else {
            return [];
        }
    }
});

dojo.declare("twms.spTree.SpRow", tavant.twms.treeTable.AbstractRow, {

    _validationHandler : null,//twms.spTree.ValidationHandler

    _getSubstitutionMap : function() {
        return {
            label : this._nodeObject.definition.label,
            suggestedLabourHours : this._nodeObject.suggestedLabourHours
        };
    },

    _postCreationCallback : function() {
        this._bindSpDefEditor();
        this._bindLabourHrsEditor();
        var forCampaignsCheckBox = getElementByClass(SP_TREE_VARS.ELEMENT_CLASSES.FOR_CAMPAIGN, this._domNode);
        dojo.connect(forCampaignsCheckBox, "onclick", this, function() {
            this._nodeObject.forCampaigns = forCampaignsCheckBox.checked;
        });

        if(this._nodeObject.forCampaigns) {
            forCampaignsCheckBox.checked = true;
        }
        
        this._validationHandler = new twms.spTree.ValidationHandler(this);
        this.addValidator(new twms.utility.Validator(false, VALIDATOR_MODE.EXPRESSION,
                i18N.definition_not_valid, "_nodeObject.definition.id", "!=", "\"\""));
        this.addValidator(new twms.utility.Validator(false, VALIDATOR_MODE.EXPRESSION,
                i18N.zero_lab_hrs_not_allowed, "_nodeObject.suggestedLabourHours", ">=", 0));
    },

    _handleValidationErrors : function(errors) {
        twms.spTree.SpRow.superclass._handleValidationErrors.call(this, errors);
        this._validationHandler.markInvalid(errors);
    },

    _clearValidationErrors : function() {
        twms.spTree.SpRow.superclass._clearValidationErrors.call(this);
        this._validationHandler.markValid();
    },

    _bindSpDefEditor : function() {
        var spDefSpan = getElementByClass(SP_TREE_VARS.ELEMENT_CLASSES.SP_DEF, this._domNode);

        var self = this;
        var dataProvider = new twms.spTree.ComboBoxAjaxDataProvider();
        dataProvider.nodeObject = this._nodeObject;
        dataProvider.url = "fetchServiceProcedureDefinitions.action";
        
        var updateView = function() {
            spDefSpan.innerHTML = self._nodeObject.definition.label;
        };

        var getNodeDefinition = function() {
            return {label : self._nodeObject.definition.label, value : self._nodeObject.definition.id};
        };

        var setNodeDefinition = function(/*object {label: ..., value: ...}*/ lvPair) {
            self._nodeObject.definition.id = lvPair.value;
            self._nodeObject.definition.label = lvPair.label;
            self.refresh();
        }

        var createNewSpDefinition = function(/*String (label)*/ label, /*Callback*/ callback) {
            SP_TREE_VARS.definitionCreator.createNewSpDef(label, self.getDepth(), function(newDefData) {
                callback(_convertNewDefDataToComboBoxState(newDefData));
                updateView();
            });
        };
        
        var actionObject = new twms.resources.ActionObject(this._nodeObject, SP_TREE_VARS.RESOURCE_TYPE.COMBO_BOX,
                spDefSpan, getNodeDefinition, updateView, setNodeDefinition, createNewSpDefinition, dataProvider);

        dojo.connect(getExpectedParent(spDefSpan, "td"), "onclick", function(event) {
            SP_TREE_VARS.resourceManager.requestEditor(actionObject);
        });
    },

    _bindLabourHrsEditor : function() {
        var labourHrsSpan = getElementByClass(SP_TREE_VARS.ELEMENT_CLASSES.LABOUR_HRS, this._domNode);

        var self = this;
        var updateView = function() {
            labourHrsSpan.innerHTML = self._nodeObject.suggestedLabourHours;
        };
        var getLabourHoursValue = function() {
            return self._nodeObject.suggestedLabourHours;
        };
        var setLabourHrsValue = function(value) {
            self._nodeObject.suggestedLabourHours = value;
            self.refresh();
        };
        var actionObject = new twms.resources.ActionObject(this._nodeObject, SP_TREE_VARS.RESOURCE_TYPE.FREE_TEXT,
                labourHrsSpan, getLabourHoursValue, updateView, setLabourHrsValue);

        dojo.connect(getExpectedParent(labourHrsSpan, "td"), "onclick", function(event) {
            SP_TREE_VARS.resourceManager.requestEditor(actionObject);
        });
    },

    destroy: function() {
        this._validationHandler.destroy();
    }
});

dojo.declare("twms.spTree.AsmRow", tavant.twms.treeTable.AbstractParentRow, {

    _treadBucket : null,
    _lastUpdated : null,
    _deleteButton : null,

    _asmChildRows : null,
    _spChildRows : null,

    _validationHandler : null,//twms.spTree.ValidationHandler

    onDeleteButtonClick: function() {
        SP_TREE_VARS.resourceManager.releaseHost();
        this.inherited("onDeleteButtonClick", arguments);
    },

    _getSubstitutionMap : function() {
        return {
            label : this._nodeObject.definition.label,
            treadBucket : this._nodeObject.treadBucket.label,
            lastUpdated : this._nodeObject.lastUpdated
        };
    },

    _performModelAdd : function(nodeObject) {
        if(nodeObject.instanceOf === SP_TREE_VARS.CONSTANTS.ASSEMBLY) {
            this._nodeObject.asmChildren.push(nodeObject);
        } else {
            this._nodeObject.spChildren.push(nodeObject);
        }
    },

    _performModelDrop : function(nodeObject) {
        if(nodeObject.instanceOf === SP_TREE_VARS.CONSTANTS.ASSEMBLY) {
            _findAndRemoveNodeFrm(nodeObject, this._nodeObject.asmChildren);
        } else {
            _findAndRemoveNodeFrm(nodeObject, this._nodeObject.spChildren);
        }
    },

    _postCreationCallback : function() {
        this._treadBucket = getElementByClass(SP_TREE_VARS.ELEMENT_CLASSES.TREAD_BUCKET, this._domNode);
        this._lastUpdated = getElementByClass(SP_TREE_VARS.ELEMENT_CLASSES.LAST_UPDATED, this._domNode);
        this._deleteButton = getElementByClass(SP_TREE_VARS.ELEMENT_CLASSES.DROP_ROW, this._domNode);
        var addAsmRowButton = getElementByClass(SP_TREE_VARS.ELEMENT_CLASSES.ADD_NEW_ASM_ROW, this._domNode);
        if(!this._canHaveChildAsmRow()) {
            dojo.html.hide(addAsmRowButton);
        }
        this._bindAsmDefEditor();
        this._bindTreadBucket();
        this._asmChildRows = new Array();
        this._spChildRows = new Array();
        this._validationHandler = new twms.spTree.ValidationHandler(this);
        this.addValidator(new twms.utility.Validator(false, VALIDATOR_MODE.FUNCTION,
                i18N.asm_row_cannot_be_leaf, "isNotLeafNode"));
        this.addValidator(new twms.utility.Validator(false, VALIDATOR_MODE.EXPRESSION,
                i18N.definition_not_valid, "_nodeObject.definition.id", "!=", "\"\""));
    },

    isNotLeafNode : function() {
        return this._childRows.length > 0;
    },

    _handleValidationErrors : function(errors) {
        twms.spTree.AsmRow.superclass._handleValidationErrors.call(this, errors);
        this._validationHandler.markInvalid(errors);
    },

    _clearValidationErrors : function() {
        twms.spTree.AsmRow.superclass._clearValidationErrors.call(this);
        this._validationHandler.markValid();
    },

    _addChildToDomGraph : function(/*Object [AsmRow/SpRow]*/ childRow) {
        if(childRow._nodeObject.instanceOf === SP_TREE_VARS.CONSTANTS.ASSEMBLY) {
            this._positionAndAddAsmNode(childRow);
        } else {
            this._positionAndAddSpNode(childRow);
        }
    },

    addChild : function(/*Object [AsmRow/SpRow]*/ row) {
        if (row._nodeObject.instanceOf === SP_TREE_VARS.CONSTANTS.ASSEMBLY) {
            this._asmChildRows.push(row);
        } else {
            this._spChildRows.push(row);
        }
        twms.spTree.AsmRow.superclass.addChild.call(this, row);
    },

    removeChild : function(/*Object [AsmRow/SpRow]*/ row) {
        var childRows = (row._nodeObject.instanceOf === SP_TREE_VARS.CONSTANTS.ASSEMBLY) ?
                        this._asmChildRows : this._spChildRows;
        _findAndRemoveRowFrm(row, childRows);

        SP_TREE_VARS.resourceManager.releaseHost();
        twms.spTree.AsmRow.superclass.removeChild.call(this, row);
        row.destroy();
    },

    _positionAndAddAsmNode : function(/*Object [AsmRow]*/ asmChild) {
        var appendAfter = this._spChildRows.length > 0 ? this._spChildRows[0].getDomNode() : this.getDomNode();
        dojo.dom.insertAfter(asmChild.getDomNode(), appendAfter);
    },

    _positionAndAddSpNode : function(/*Object [SpRow]*/ spChild) {
        dojo.dom.insertAfter(spChild.getDomNode(), this.getDomNode());
    },

    _bindAsmDefEditor : function() {
        var asmDefSpan = getElementByClass(SP_TREE_VARS.ELEMENT_CLASSES.ASM_DEF, this._domNode);

        var dataProvider = new twms.spTree.ComboBoxAjaxDataProvider();
        dataProvider.nodeObject = this._nodeObject;
        var self = this;
        dataProvider.url = "fetchAssemblyDefinitions.action";
        dataProvider.queryParams.depth = self.getDepth();

        var updateView = function() {
            asmDefSpan.innerHTML = self._nodeObject.definition.label;
        };

        var getNodeDefinition = function() {
            return {label : self._nodeObject.definition.label, value : self._nodeObject.definition.id};
        };

        var setNodeDefinition = function(/*object {label: ..., value: ...}*/ lvPair) {
            self._nodeObject.definition.label = lvPair.label;
            self._nodeObject.definition.id = lvPair.value;
            self.refresh();
        };

        var createAsmDef = function(/*String (label)*/ label, /*Callback*/ callback) {
            SP_TREE_VARS.definitionCreator.createNewAsmDef(label, self.getDepth(), function(newDefData) {
                callback(_convertNewDefDataToComboBoxState(newDefData));
                updateView();
            });
        };

        var actionObject = new twms.resources.ActionObject(this._nodeObject, SP_TREE_VARS.RESOURCE_TYPE.COMBO_BOX,
                asmDefSpan, getNodeDefinition, updateView, setNodeDefinition, createAsmDef, dataProvider);

        dojo.connect(getExpectedParent(asmDefSpan, "td"), "onclick", function(event) {
        	if(isReadOnly == null || isReadOnly == false){
        		SP_TREE_VARS.resourceManager.requestEditor(actionObject);
        	}
        });
    },

    _bindTreadBucket : function() {
        var treadBucketSpan = getElementByClass(SP_TREE_VARS.ELEMENT_CLASSES.TREAD_BUCKET, this._domNode);

        var self = this;
        var dataProvider = new twms.spTree.ComboBoxStaticDataProvider(SP_TREE_VARS.treadData);
        var illegalValueSupresser = function(label, callback) {
            callback({label : "", value : ""});
        };
        var updateView = function() {
            treadBucketSpan.innerHTML = self._nodeObject.treadBucket.label;
        };
        var getTreadableNodeDefinition= function() {
            return {label : self._nodeObject.treadBucket.label,  value : self._nodeObject.treadBucket.id};
        }
        var setTreadBucket = function(/*object {label: ..., value: ...}*/ lvPair) {
            self._nodeObject.treadBucket.id = lvPair.value;
            self._nodeObject.treadBucket.label = lvPair.label;
            self.refresh();
        }
        var actionObject = new twms.resources.ActionObject(this._nodeObject, SP_TREE_VARS.RESOURCE_TYPE.COMBO_BOX,
                treadBucketSpan, getTreadableNodeDefinition, updateView, setTreadBucket, illegalValueSupresser, dataProvider);

        dojo.connect(getExpectedParent(treadBucketSpan, "td"), "onclick", function(event) {
            SP_TREE_VARS.resourceManager.requestEditor(actionObject);
        });
    },

    refresh : function() {
        twms.spTree.AsmRow.superclass.refresh.call(this);
        if (this._isDeleteable()) {
            dojo.html.show(this._deleteButton);
        } else {
            dojo.html.hide(this._deleteButton);
        }
        if(this._isTreadable()) {
            dojo.html.show(this._treadBucket);
            dojo.html.show(this._lastUpdated);
        } else {
            dojo.html.hide(this._treadBucket);
            dojo.html.hide(this._lastUpdated);
        }
    },

    _isDeleteable : function() {
        return !this._isFoldable();
    },

    _isTreadable : function() {
        return this._nodeObject.spChildren.length > 0;
    },

    _canHaveChildAsmRow : function() {
        return this.getDepth() < SP_TREE_VARS.CONSTANTS.MAX_ASM_ROW_DEPTH;
    },

    destroy: function() {
        this._validationHandler.destroy();
    }
});

dojo.require("twms.widget.Tooltip");

dojo.declare("twms.spTree.ValidationHandler", null, {

    _row : null,//holds the rowObject
    _errorToolTip : null,//will hold the tooltip widget.

    constructor : function(/*Object [AsmRow/SpRow]*/ row) {
        this._row = row;
        this._errorToolTip = new twms.widget.Tooltip({
            connectId : [this._row.getDomNode()],
            showDelay : 100
        });
    },

    _populateErrorTooltipCaption : function(/*Array of Strings(error messages)*/ errors) {
        var errorMessageMarkup = "";
        for (var i=0;i<errors.length;i++) {
            errorMessageMarkup += dojo.string.substitute(SP_TREE_VARS.errorParams.message.markup, {"message" : errors[i]});
        }
        this._errorToolTip.setLabel(dojo.string.substitute(
                SP_TREE_VARS.errorParams.messageWrapper.markup, {"messages" : errorMessageMarkup}));
    },

    markInvalid : function(/*Array of Strings(error messages)*/ errors) {
        dojo.addClass(this._row.getDomNode(), SP_TREE_VARS.ELEMENT_CLASSES.INVALID);
        this._populateErrorTooltipCaption(errors);
    },

    markValid : function() {
        dojo.removeClass(this._row.getDomNode(), SP_TREE_VARS.ELEMENT_CLASSES.INVALID);
        this._errorToolTip.clearLabel();
    },

    destroy: function() {
        this._errorToolTip.destroy();
        this._row = null;
    }
});

dojo.declare("twms.spTree.RootRow", tavant.twms.treeTable.RootRow, {

    _performModelAdd : function(/*JSON stub*/ nodeObject) {
        this._nodeObject.asmChildren.push(nodeObject);
    },

    _performModelDrop : function(/*JSON stub*/ nodeObject) {
        _findAndRemoveNodeFrm(nodeObject, this._nodeObject.asmChildren);
    }
});

dojo.declare("twms.spTree.AbstractComboBoxDataProvider", null, {

    nodeObject : null,//is used for looking up the peer node's definitions and filtering on them... that kinda stuff
    reloadOnBind: null,

    constructor : function() {/*doing nothing*/},

    /**
     * @return array of the format [[label1, id1],[label2, id2]...]
     */
    filterAndParse : function(/*Definitions (Array having format [{label : "label", id : "id"},...])*/ definitions) {
        this.preFilterAndParse();
        var data = new Array();
        for(var i in definitions) {
            if(this.isAccpetable(definitions[i])) {
                data.push([definitions[i][0], definitions[i][1]]);
            }
        }
        this.postFilterAndParse();
        return data;
    },

    preFilterAndParse : function() {/*doing nothing*/},

    postFilterAndParse : function() {/*doing nothing*/},

    isAccpetable : function(/*Data in the format {label : "label", id : "id"}*/ dataNode) {
        return true;
    }
});

dojo.declare("twms.spTree.ComboBoxStaticDataProvider", twms.spTree.AbstractComboBoxDataProvider, {

    initialData : null,
    reloadOnBind: false,

    constructor : function(/*Static data (Array having format [{label : "label", id : "id"},...])*/ data) {
        this.initialData = data;
    }
});

dojo.declare("twms.spTree.ComboBoxAjaxDataProvider", twms.spTree.AbstractComboBoxDataProvider, {

    reloadOnBind: true,
    enableInvalidValueCallback : false,
    url: null,
    queryParams: null,

    constructor : function() {
        this.queryParams = {
            startingWith: ""
        }
    },

    preFilterAndParse : function() {
        this.usedIds = SP_TREE_VARS.treeDataManager.getPeerNodeDefinitionIds(this.nodeObject);
    },

    postFilterAndParse : function() {
        this.usedIds = null;
    },

    isAccpetable : function(/*definition*/ definition) {
        for(var j in this.usedIds) {
            if(definition[1] == this.usedIds[j]) {
                return false;
            }
        }
        return true;
    }
});

_undefSafeMixin("twms.spTree.AJAXer", {

    createSpDef : function(/*String (label)*/ label, /*int*/ depth, /*Function*/ callback) {
        twms.ajax.fireJsonRequest("createServiceProcedureDefinition.action", {"label" : label}, callback);
    },

    createAsmDef : function(/*String (label)*/ label, /*int*/ depth, /*Function*/ callback) {
        twms.ajax.fireJsonRequest("createAssemblyDefinition.action", {"label" : label, "depth" : depth}, callback);
    }
});

function _findAndRemoveNodeFrm(/*JSON node*/ node, /*asmChildren/spChildren*/ collection) {
    for(var i in collection) {
        if(node._treeTableKey === collection[i]._treeTableKey) {
            collection.splice(i, 1);
            return;
        }
    }
}

function _findAndRemoveRowFrm(/*Object [AsmRow/SpRow]*/ row, /*asmRow Collection/spRow Collection*/ collection) {
    for(var i in collection) {
        if(row.getId() === collection[i].getId()) {
            collection.splice(i, 1);
            return;
        }
    }
}

function _convertNewDefDataToComboBoxState(newDefData) {
    return [newDefData.label, newDefData.id];
}