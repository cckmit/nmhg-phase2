dojo.require("twms.widget.Select");

var FAILURE_TREE_VARS = {
    CONSTANTS : {
        FAILURE_TYPE : "FailureType",
        FAILURE_ROOT_CAUSE : "FailureRootCause",
        ROOT : "root",
        MAX_FT_ROW_DEPTH : 2
    },
    /**
     * This array, will hold the names of all the classes, which are used here to point to a particular element
     * The lookup is done based on the css class name.
     */
    ELEMENT_CLASSES : {
        ADD_NEW_FAILURE_TYPE : "addNewFailureTypeButton",
        DROP_ROW : "dropRow",
        FAILURE_TYPE_HOLDER : "failureTypeHolder",
        FAILURE_ROOT_CAUSE_HOLDER : "failureRootCauseHolder",
        
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
    errorParams : null//will hold t:jsVar varables for errorMessages, and wrapper...(late bound)
};

dojo.addOnLoad(function() {
    FAILURE_TREE_VARS.resourceManager = new twms.failureTree.ResourceManager();
});

dojo.declare("twms.failureTree.TreeDataManager", null, {

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
    getPeerNodeDefinitionIds : function(/*Node (treeNode [instanceOf = 'FailureType'/'FailureRootCause'])*/ node) {
        if(node.instanceOf != FAILURE_TREE_VARS.CONSTANTS.FAILURE_ROOT_CAUSE && node.instanceOf != FAILURE_TREE_VARS.CONSTANTS.FAILURE_TYPE) {
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

    _findPeerNodes : function(/*Tree Fragment*/ node, /*tree dataStructure*/ treeFragment) {
        var parentNode = this._findNodesParent(node, treeFragment);
        return (node.instanceOf === FAILURE_TREE_VARS.CONSTANTS.FAILURE_TYPE) ?
               parentNode.failureTypeChildren : parentNode.failureRootCauseChildren;
    },

    _isChildPresent : function(/*Tree Fragment*/ child, /*Tree Fragment*/ testParent) {
        var childrenList = (child.instanceOf === FAILURE_TREE_VARS.CONSTANTS.FAILURE_TYPE) ?
                           testParent.failureTypeChildren : testParent.failureRootCauseChildren;
            return childrenList &&
                   dojo.some(childrenList, function(childInList) {
                        return (child._treeTableKey === childInList._treeTableKey);
                   });
    },

    _findNodesParent : function(/*Tree fragment*/ node, /*Tree Fragment*/ testParent) {
        if(this._isChildPresent(node, testParent)) {
            return testParent;
        }
        for(var i in testParent.failureTypeChildren) {
            var parentNode = this._findNodesParent(node, testParent.failureTypeChildren[i]);
            if(parentNode) return parentNode;
        }
    }
});

dojo.declare("twms.failureTree.DefinitionCreator", null, {

    _dialog : null,
    _processing : false,

    _labelInput : null,
    _depthInput : null,
    _descInput : null,

    /**
     * Expected params format : {dialog : "Widget (dijit.Dialog)",
     *                           labelInput : "HTML input",
     *                           depthInput : "HTML input",
     *                           descInput : "HTML input",
     *                           createButton : "HTML button",
     *                           cancelButton : "HTML button"}
     */
    constructor : function(/*Assoc array*/ params) {
        this._dialog = params.dialog;
        this._labelInput = params.labelInput;
        this._depthInput = params.depthInput;
        this._descInput = params.descInput;
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
        this._descInput.value = "";
        this._dialog.hide();
        this._processing = false;
    },

    _onCompletion : function(type, data) {
        this._processing = false;
        this._hideDialog();
    },

    _requestCreation : function() {/*To be overridden by the using function*/},

    _createNewDef : function(/*String*/ initialLabel, /*int*/ initialDepth, /*String*/ description) {
        if(this._processing)  return;
        this._labelInput.value = initialLabel;
        this._depthInput.value = initialDepth;
        this._descInput.value = description;
        this._showDialog();
    },

    createNewFailureTypeDef : function(/*String*/ label, /*int*/ depth, /*String*/ description, /*Function*/ callback) {
        this._createNewDef(label, depth, description);
        var self = this;
        this._requestCreation = function() {
            twms.failureTree.AJAXer.createFailureTypeDef(this._labelInput.value, this._depthInput.value, this._descInput.value, function(data) {
                self._onCompletion();
                callback(data);
            });
        }
    },

    createNewFailureRootCauseDef : function(/*String*/ label, /*int*/ depth, /*String*/ description, /*Function*/ callback) {
        this._createNewDef(label, depth, description);
        var self = this;
        this._requestCreation = function() {
            twms.failureTree.AJAXer.createFailureRootCauseDef(this._labelInput.value, this._depthInput.value, this._descInput.value, function(data) {
                self._onCompletion();
                callback(data);
            });
        }
    }
});

dojo.declare("twms.failureTree.NodeAgent", tavant.twms.treeTable.DefaultNodeAgent, {

    _controller : null,//controller object

    constructor : function(/*Object (controller)*/ controller) {
        this._controller = controller;
    },

    /**
     * Should return a JSON node with some default values, that the row actually expects.
     */
    getStub : function(/*String (row type)*/ rowType) {
        if (rowType === FAILURE_TREE_VARS.CONSTANTS.FAILURE_TYPE) {
            return {
                "instanceOf":FAILURE_TREE_VARS.CONSTANTS.FAILURE_TYPE,
                "definition":{"code":"","label":"","id":""},
                "nodeType":"leaf",
                "id":0,
                "failureRootCauseChildren":[]
            };
        } else if (rowType === FAILURE_TREE_VARS.CONSTANTS.FAILURE_ROOT_CAUSE) {
            return {
                "definition":{"code":"","label":"","id":""},
                "nodeType":"leaf",
                "instanceOf":FAILURE_TREE_VARS.CONSTANTS.FAILURE_ROOT_CAUSE,
                "id":0
            };
        }
    },

    getRowInstance : function(/*JSON node*/ nodeObject, /*AbstractParentRow*/ parentRow) {
        var className = null;
        var template = null;
        if(nodeObject.instanceOf === FAILURE_TREE_VARS.CONSTANTS.FAILURE_TYPE) {
            className = twms.failureTree.FailureTypeRow;
            template = failureTypeRowTemplate;
        } else if(nodeObject.instanceOf === FAILURE_TREE_VARS.CONSTANTS.FAILURE_ROOT_CAUSE){
            className = twms.failureTree.FailureRootCauseRow;
            template = failureRootCauseRowTemplate;
        }
        if (!className) throw new Error("Row's class was not defined.");
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
        if (nodeObject.instanceOf === FAILURE_TREE_VARS.CONSTANTS.FAILURE_TYPE) {
            return [nodeObject.failureRootCauseChildren];
        } else if(nodeObject.nodeType === FAILURE_TREE_VARS.CONSTANTS.ROOT) {
            return [nodeObject.failureTypeChildren];
        } else {
            return [];
        }
    }
});

dojo.declare("twms.failureTree.FailureRootCauseRow", tavant.twms.treeTable.AbstractRow, {

    _validationHandler : null,

    _getSubstitutionMap : function() {
        return {
            label : this._nodeObject.definition.label
        };
    },

    _postCreationCallback : function() {
        this._bindFailureRootCauseDefEditor();
        this._validationHandler = new twms.failureTree.ValidationHandler(this);
        this.addValidator(new twms.utility.Validator(false, VALIDATOR_MODE.EXPRESSION,
                i18N.definition_not_valid, "_nodeObject.definition.id", "!=", "\"\""));
    },

    _handleValidationErrors : function(errors) {
        twms.failureTree.FailureRootCauseRow.superclass._handleValidationErrors.call(this, errors);
        this._validationHandler.markInvalid(errors);
    },

    _clearValidationErrors : function() {
        twms.failureTree.FailureRootCauseRow.superclass._clearValidationErrors.call(this);
        this._validationHandler.markValid();
    },

    _bindFailureRootCauseDefEditor : function() {
        var failureRootCauseDefSpan = getElementByClass(FAILURE_TREE_VARS.ELEMENT_CLASSES.FAILURE_ROOT_CAUSE_HOLDER, this._domNode);
        var self = this;
        var dataProvider = new twms.failureTree.ComboBoxAjaxDataProvider();
        dataProvider.url = "fetch_failure_root_causes.action";
        
        var updateView = function() {
            failureRootCauseDefSpan.innerHTML = self._nodeObject.definition.label;
        };
        var getNodeDefinition = function() {
            return {label : self._nodeObject.definition.label, value : self._nodeObject.definition.id};
        };
        var setNodeDefinition = function(lvPair) {
            self._nodeObject.definition.id = lvPair.value;
            self._nodeObject.definition.label = lvPair.label;
            self.refresh();
        }
        var createNewFailureRootCauseDefinition = function(/*String (label)*/ label, /*Callback*/ callback) {
            FAILURE_TREE_VARS.definitionCreator.createNewFailureRootCauseDef(label, self.getDepth(), "", function(newDefData) {
                callback(_convertNewDefDataToComboBoxState(newDefData));
            });
        };
        var actionObject = new twms.failureTree.ActionObject(this._nodeObject, FAILURE_TREE_VARS.RESOURCE_TYPE.COMBO_BOX,
                failureRootCauseDefSpan, getNodeDefinition, updateView, setNodeDefinition, createNewFailureRootCauseDefinition, dataProvider);

        dojo.connect(getExpectedParent(failureRootCauseDefSpan, "td"), "onclick", function(event) {
            FAILURE_TREE_VARS.resourceManager.requestEditor(actionObject);
        });
    },

    destroy: function() {
        this._validationHandler.destroy();
    }
    
});

dojo.declare("twms.failureTree.FailureTypeRow", tavant.twms.treeTable.AbstractParentRow, {

    _deleteButton : null,

    _failureRootCauseChildRows : null,

    _validationHandler : null,

    onDeleteButtonClick: function() {
        FAILURE_TREE_VARS.resourceManager.releaseHost();
        this.inherited("onDeleteButtonClick", arguments);
    },
    
    _getSubstitutionMap : function() {
        return {
            label : this._nodeObject.definition.label
        };
    },

    _performModelAdd : function(nodeObject) {
        this._nodeObject.failureRootCauseChildren.push(nodeObject);
    },

    _performModelDrop : function(nodeObject) {
        _findAndRemoveNodeFrm(nodeObject, this._nodeObject.failureRootCauseChildren);
    },

    _postCreationCallback : function() {
        this._deleteButton = getElementByClass(FAILURE_TREE_VARS.ELEMENT_CLASSES.DROP_ROW, this._domNode);
        this._bindFailureTypeDefEditor();
        this._failureRootCauseChildRows = new Array();
        this._validationHandler = new twms.failureTree.ValidationHandler(this);
        this.addValidator(new twms.utility.Validator(false, VALIDATOR_MODE.FUNCTION,
                i18N.ft_row_cannot_be_leaf, "isNotLeafNode"));
        this.addValidator(new twms.utility.Validator(false, VALIDATOR_MODE.EXPRESSION,
                i18N.definition_not_valid, "_nodeObject.definition.id", "!=", "\"\""));
    },

    isNotLeafNode : function() {
        return this._childRows.length > 0;
    },

    _handleValidationErrors : function(errors) {
        twms.failureTree.FailureTypeRow.superclass._handleValidationErrors.call(this, errors);
        this._validationHandler.markInvalid(errors);
    },

    _clearValidationErrors : function() {
        twms.failureTree.FailureTypeRow.superclass._clearValidationErrors.call(this);
        this._validationHandler.markValid();
    },

    _addChildToDomGraph : function(/*Object [FailureTypeRow/FailureRootCauseRow]*/ childRow) {
        this._positionAndAddFailureRootCauseNode(childRow);
    },

    addChild : function(/*Object [FailureTypeRow/FailureRootCauseRow]*/ row) {
        this._failureRootCauseChildRows.push(row);
        twms.failureTree.FailureTypeRow.superclass.addChild.call(this, row);
    },

    removeChild : function(/*Object [FailureTypeRow/FailureRootCauseRow]*/ row) {
        _findAndRemoveRowFrm(row, this._failureRootCauseChildRows);
        FAILURE_TREE_VARS.resourceManager.releaseHost();
        twms.failureTree.FailureTypeRow.superclass.removeChild.call(this, row);
        row.destroy();
    },

    _positionAndAddFailureRootCauseNode : function(/*Object [FailureRootCauseRow]*/ failureRootCauseChild) {
        dojo.dom.insertAfter(failureRootCauseChild.getDomNode(), this.getDomNode());
    },

    _bindFailureTypeDefEditor : function() {
        var failureTypeDefSpan = getElementByClass(FAILURE_TREE_VARS.ELEMENT_CLASSES.FAILURE_TYPE_HOLDER, this._domNode);
        var dataProvider = new twms.failureTree.ComboBoxAjaxDataProvider();
        var self = this;
        dataProvider.url = "fetch_failure_types.action";
        
        var updateView = function() {
            failureTypeDefSpan.innerHTML = self._nodeObject.definition.label;
        };
        var getNodeDefinition = function() {
            return {label : self._nodeObject.definition.label, value : self._nodeObject.definition.id};
        };
        var setNodeDefinition = function(/*object {label: ..., value: ...}*/ lvPair) {
            self._nodeObject.definition.label = lvPair.label;
            self._nodeObject.definition.id = lvPair.value;
            self.refresh();
        };
        var createFailureTypeDef = function(/*String (label)*/ label, /*Callback*/ callback) {
            FAILURE_TREE_VARS.definitionCreator.createNewFailureTypeDef(label, self.getDepth(), "", function(newDefData) {
                callback(_convertNewDefDataToComboBoxState(newDefData));
            });
        };
        var actionObject = new twms.failureTree.ActionObject(this._nodeObject, FAILURE_TREE_VARS.RESOURCE_TYPE.COMBO_BOX,
                failureTypeDefSpan, getNodeDefinition, updateView, setNodeDefinition, createFailureTypeDef, dataProvider);

        dojo.connect(getExpectedParent(failureTypeDefSpan, "td"), "onclick", function(event) {
            FAILURE_TREE_VARS.resourceManager.requestEditor(actionObject);
        });
    },

    refresh : function() {
        twms.failureTree.FailureTypeRow.superclass.refresh.call(this);
        if (this._isDeleteable()) {
            dojo.html.show(this._deleteButton);
        } else {
            dojo.html.hide(this._deleteButton);
        }
    },

    _isDeleteable : function() {
        return !this._isFoldable();
    },

    destroy: function() {
        this._validationHandler.destroy();
    }

});

dojo.require("twms.widget.Tooltip");

dojo.declare("twms.failureTree.ValidationHandler", null, {

    _row : null,//holds the rowObject
    _errorToolTip : null,//will hold the tooltip widget.

    constructor : function(/*Object [FailureTypeRow/FailureRootCauseRow]*/ row) {
        this._row = row;
        this._errorToolTip = new twms.widget.Tooltip({
            connectId : [this._row.getDomNode()],
            showDelay : 100
        });
    },

    _populateErrorTooltipCaption : function(/*Array of Strings(error messages)*/ errors) {
        var errorMessageMarkup = "";
        for (var i in errors) {
            errorMessageMarkup += dojo.string.substitute(FAILURE_TREE_VARS.errorParams.message.markup, {"message" : errors[i]});
        }
        this._errorToolTip.setLabel(dojo.string.substitute(
                FAILURE_TREE_VARS.errorParams.messageWrapper.markup, {"messages" : errorMessageMarkup}));
    },

    markInvalid : function(/*Array of Strings(error messages)*/ errors) {
        dojo.addClass(this._row.getDomNode(), FAILURE_TREE_VARS.ELEMENT_CLASSES.INVALID);
        this._populateErrorTooltipCaption(errors);
    },

    markValid : function() {
        dojo.removeClass(this._row.getDomNode(), FAILURE_TREE_VARS.ELEMENT_CLASSES.INVALID);
        this._errorToolTip.clearLabel();
    },

    destroy: function() {
        this._errorToolTip.destroy();
        this._row = null;
    }
});

dojo.declare("twms.failureTree.RootRow", tavant.twms.treeTable.RootRow, {

    _performModelAdd : function(/*JSON stub*/ nodeObject) {
        this._nodeObject.failureTypeChildren.push(nodeObject);
    },

    _performModelDrop : function(/*JSON stub*/ nodeObject) {
        _findAndRemoveNodeFrm(nodeObject, this._nodeObject.failureTypeChildren);
    }
});

dojo.declare("twms.failureTree.ActionObject", null, {

    COUNTER : {
        count : 0
    },

    id : 0,//just serves the perpose of comparision.

    treeNode : null,

    resourceType : "",

    node : null,

    validValueSet : function(/*value*/ value) {},

    invalidValueSet : function(/*label*/ label) {},

    dataProvider : null,//object<? extends twms.failureTree.AbstractComboBoxDataProvider>

    getValue : function() {return null;},

    preReleaseCallback : function() {},

    constructor : function(/*JSON tree node ("FailureTypw"/"failureRootCause" instance)*/treeNode,
                           /*String (ResourceType)*/ resourceType, /*domNode*/ node, /*Function*/ getValue,
                           /*Function*/ preReleaseCallback, /*Function*/ validValueSet, /*Function?*/ invalidValueSet,
                           /*DataProvider?*/ dataProvider) {
        this.treeNode = treeNode;
        this.resourceType = resourceType;
        this.node = node;
        this.getValue = getValue;
        this.preReleaseCallback = preReleaseCallback;
        this.validValueSet = validValueSet;
        if(invalidValueSet) {
            this.invalidValueSet = invalidValueSet;
        }
        this.dataProvider = dataProvider ? dataProvider : new twms.failureTree.AbstractComboBoxDataProvider();
        this.id = this.COUNTER.count++;
    }
});

dojo.declare("twms.failureTree.AbstractComboBoxDataProvider", null, {

    comboBox : null,
    treeNode : null,//is used for looking up the peer node's definitions and filtering on them... that kinda stuff

    constructor : function() {/*doing nothing*/},

    /**
     * @return array of the format [[label1, id1],[label2, id2]...]
     */
    _filterAndParse : function(/*Definitions (Array having format [{label : "label", id : "id"},...])*/ definitions) {
        this._preFilterAndParse();
        var data = new Array();
        for(var i in definitions) {
            if(this._isAccpetable(definitions[i])) {
                data.push([definitions[i][0], definitions[i][1]]);
            }
        }
        this._postFilterAndParse();
        return data;
    },

    _preFilterAndParse : function() {/*doing nothing*/},

    _postFilterAndParse : function() {/*doing nothing*/},

    _isAccpetable : function(/*Data in the format {label : "label", id : "id"}*/ dataNode) {
        return true;
    }
});

dojo.declare("twms.failureTree.ComboBoxAjaxDataProvider", twms.failureTree.AbstractComboBoxDataProvider, {

    url: null,
    queryParams: null,

    constructor : function() {
        this.queryParams = {
            startingWith: ""
        }
    },

    _preFilterAndParse : function() {
        this.usedIds = FAILURE_TREE_VARS.treeDataManager.getPeerNodeDefinitionIds(this.treeNode);
    },

    _postFilterAndParse : function() {
        this.usedIds = null;
    },

    _isAccpetable : function(/*definition*/ definition) {
        for(var j in this.usedIds) {
            if(definition[1] == this.usedIds[j]) {
                return false;
            }
        }
        return true;
    }
});

dojo.declare("twms.failureTree.ResourceManager", null, {

    _host : null,

    _hostBackup : null,

    _comboBox : null,
    
    _actionObject : null,

    _lockedBy : "",

    _comboBoxListenTopic: null,

    _dummyDataProvider : null,//abstract data provider for the comboBox... so that it doesn't throw a null pointer exception
    // when making some case senstivity checks... doesn't play any other role....

    constructor : function() {
        this._comboBox = new twms.widget.Select({
            id : "commonResource_comboBox",
            autoComplete : false,
            legacyDataMode: true,
            store: new dojo.store.Memory({ data:[]}),
            pageSize:20
        });

        this._dummyDataProvider = this._comboBox.dataProvider;

        var combo = this._comboBox;

        var comboBoxValidationTopic = "/treeTable/failureTypeComboBoxValidation_" + combo.id;

        dojo.connect(combo.textbox, "onblur", dojo.hitch(this, function(event) {
            var displayedValue = combo.getDisplayedValue();

            if(!combo.isValid() && !dojo.string.isBlank(displayedValue)) {
                this._comboBoxInvalidValueSet(displayedValue);
            }
        }));

        combo.setValidationNotificationTopics(comboBoxValidationTopic);

        this._comboBoxListenTopic = "/treeTable/failureTypeComboBoxListen_" + combo.id;

        combo.addListenTopic(this._comboBoxListenTopic);

        dojo.connect(combo, "onChange", this, function(newValue) {
            this._comboBoxValidValueSet({
                value: newValue,
                label: combo.getDisplayedValue()
            });
        });
    },

    supressEvent : function(event) {
        event.stopPropagation();
    },

    requestEditor : function(/*ActionObject*/ action) {
        if(this._actionObject && action.id === this._actionObject.id) {
            return;
        }
        this.releaseHost();
        this._aquireHost(action);
    },

    releaseHost : function() {
        if(this._host) {
            this._releaseHost();
        }
    },

    _aquireHost : function(/*ActionObject*/ action) {
        this._preAquireLock();
        this._host = action.node;
        this._actionObject = action;
        this._lockedBy = action.resourceType;
        this._hostBackup = this._host.innerHTML;

        dojo.dom.removeChildren(this._host, true);

        var actionValue = this._actionObject.getValue();

        if(this._lockedBy === FAILURE_TREE_VARS.RESOURCE_TYPE.COMBO_BOX) {
            var dataProvider = this._actionObject.dataProvider;
            var combo = this._comboBox;

            combo.textbox.value = "";
            combo.dataProvider = dataProvider;
            dataProvider.comboBox = combo;

            combo.filterItems = dojo.hitch(dataProvider, dataProvider._filterAndParse);
            combo.initialValue = actionValue.label;
            combo.dataProvider.treeNode = this._actionObject.treeNode;

            dojo.publish(this._comboBoxListenTopic, [{
                url: dataProvider.url,
                params: dataProvider.queryParams,
                makeLocal: true
            }]);

            this._host.appendChild(combo.domNode);
        } else {
            this._freeText.value = this._actionObject.getValue();
            this._host.appendChild(this._freeText);
        }
    },

    _releaseHost : function() {
        var resourceType = this._actionObject.resourceType;
        var hostHasComboResource = (resourceType === FAILURE_TREE_VARS.RESOURCE_TYPE.COMBO_BOX);

        this._preReleaseLock(this._actionObject.resourceType);

        if(hostHasComboResource) {
            // remove the combobox's domNode, so that it doesnt get destroyed along with the other children of the host.
            dojo.dom.removeNode(this._comboBox.domNode);
        }
        
        dojo.dom.removeChildren(this._host, true);

        this._host.innerHTML = this._hostBackup;
        this._actionObject.preReleaseCallback();

        if(hostHasComboResource) {
            this._actionObject.dataProvider.comboBox = null;
            this._actionObject.dataProvider.treeNode = null;
            this._comboBox.dataProvider = this._dummyDataProvider;
        } else {
            this._freeText.value = "";
        }
        this._actionObject = null;
        this._host = null;
    },

    _preAquireLock : function(/*String*/ resourceType) {
        if(this._host != null || this._lockedBy != "") {
            throw new Error("_aquireHost failed... the _host reference/lock is not free...");
        }
        this._lockedBy = resourceType;
    },

    _preReleaseLock : function() {
        if(this._host == null || this._lockedBy == "") {
            throw new Error("_releaseHost doesn't make any sense... the _host reference/lock is already free...");
        }
        this._lockedBy = "";
    },

    _comboBoxValidValueSet : function(/*object {label: ..., value: ...}*/ lvPair) {
        if(this._actionObject) {
            this._actionObject.validValueSet(lvPair);
        }
    },

    _comboBoxInvalidValueSet : function(/*String*/ label) {
        if(this._actionObject) {
            this._actionObject.invalidValueSet(label,
                dojo.hitch(this, function(/*object{ label: ..., value: ...}*/ lvPair) {
                    dojo.publish(this._comboBoxListenTopic, [{
                        addItem: lvPair
                    }]);
                })
            );
        }
    }

});

_undefSafeMixin("twms.failureTree.AJAXer", {

    createFailureRootCauseDef : function(/*String (label)*/ label, /*int*/ depth, /*String*/ description, /*Function*/ callback) {
        twms.ajax.fireJsonRequest("create_failure_root_cause.action", {"label" : label , description : description }, callback);
    },

    createFailureTypeDef : function(/*String (label)*/ label, /*int*/ depth, /*String*/ description, /*Function*/ callback) {
    	var url = "create_failure_type.action";
        twms.ajax.fireJsonRequest(url, {"label" : label, "depth" : 0, "itemGroupId" : itemGroupIdVariable,
            description : description }, callback);
    }
});

function _findAndRemoveNodeFrm(/*JSON node*/ node, /*failureRootCauseChildren/failureTypeChildren*/ collection) {
    for(var i in collection) {
        if(node._treeTableKey === collection[i]._treeTableKey) {
            collection.splice(i, 1);
            return;
        }
    }
}

function _findAndRemoveRowFrm(/*Object [FailureTypeRow/FailureRootCauseRow]*/ row, /*FailureTypeRow/FailureRootCauseRow Collection*/ collection) {
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