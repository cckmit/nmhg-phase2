dojo.require("twms.widget.RuleBuilderSelect");

dojo.declare("tavant.twms.ruleBuilder.Controller", null, {

    tBody : null,
    rows : null,//will hold an array of Row objects.
    currentIndexVal : 0,//stores the id that is to be allocated to the row getting added next.
    jsonField : null,//the field to be populated with the serialized tree before submission.

    expressionNameField : null,

    referredPredicatesField : null,

    errorDialog : null,

    ruleSearchInput : null,

    operatorValue : "",

    lhsNameField : null,

    _invalidFields : [], // array of widgetIds
    _invalidRows: [],
    _errorDialogContentPane: null,
    _conditionNameSetByUser: null,
    
    constructor : function(/*tBody domNode*/ rowEditorBody,
                                             /*JSON tree's hidden field*/ hiddenJSONField,
                                             /*expression name field*/ expressionNameField,
                                             /*referred predicates field*/ referredPredicatesField,
                                             /*search key input's domNode*/ searchInput,
                                             /*error dialog widget*/ errorDialog) {

        this.tBody = rowEditorBody;
        this.jsonField = hiddenJSONField;
        this.referredPredicatesField = referredPredicatesField;
        this.rows = [];
        this._errorDialogContentPane = dijit.byId("errorDialogContent");
        if (!globalObj.model.readOnly) {
            this.expressionNameField = expressionNameField;
            this.ruleSearchInput = searchInput;
            this.errorDialog = errorDialog;
        }
        if(this.expressionNameField){
	        dojo.connect(this.expressionNameField.domNode, "onchange", dojo.hitch(this, function() {
	        	this._conditionNameSetByUser = true;
	        }));
        }
    },

    addInvalidField : function(/* widgetId */ fieldId) {
        if (dojo.indexOf(this._invalidFields, fieldId) == -1) {
            this._invalidFields.push(fieldId);
        }
    },

    removeInvalidField : function(/* widgetId */ fieldId) {
        var indexOfId = dojo.indexOf(this._invalidFields, fieldId);

        if (indexOfId >= 0) {
            this._invalidFields.splice(indexOfId, 1);
        }
    },

    isExpressionFullyBuilt : function() {
        return this.rows.length > 0 && this._invalidFields.length == 0;
    },

    createRows : function() {
        var nodes = globalObj.model.getNodes();
        for (var i in nodes) {
            if (nodes[i].type === "EXPRESSION") {
                this.addNewRow("initialization", nodes[i]);
            } else if (nodes[i].type === "RULE_FRAGMENT") {
                this._addNewRuleRow(nodes[i]);
            } else {
                throw new Error("The controller doesn't know how to handle node " +
                           "type : " + nodes[i].type);
            }
        }
    },

    getNewNodes : function() {
        var foo=[];
        var nodes = [];
        dojo.forEach(this.rows, function(row) {
            try {
                nodes.push(row.getNode());
            } catch(exception) {
                this._invalidRows.push(row.domNode.id);
            }
        }, this);

        return nodes;
    },

    getReferredPredicates : function() {
        var referredPredicates = [];
        for (var i in this.rows) {
            var node = this.rows[i].getNode();
            if (node.id) {
                referredPredicates.push(node.id);
            }
        }

        return referredPredicates;
    },

    addNewRow : function(event, node) {
        this.currentIndexVal++;
        var row = new tavant.twms.ruleBuilder.Row(this.tBody, node,
                this.currentIndexVal);
        this.rows.push(row);

        globalObj.validateSubmission();

        dojo.forEach(row.subRows, function(subRow) {
            subRow.indentAsPerNesting();
        });
    },

    _addNewRuleRow : function(node) {
        if (this._isExisting(node)) return;
        this.currentIndexVal++;
        var row = new tavant.twms.ruleBuilder.PrebuiltRuleRow(node, this.currentIndexVal);
        this.rows.push(row);
        this.tBody.appendChild(row.domNode);
    },

    _isExisting : function(node) {
        for (var i = 0; i < this.rows.length; i++) {
            var row = this.rows[i];
            if (row.expression == null) continue;
            if (row.expression.id == node.id) return true;
            delete row;
        }
        return false;
    },

    requestSave : function(event) {
        dojo.forEach(this._invalidRows, function(rowNodeId) {
            dojo.removeClass(dojo.byId(rowNodeId), "invalidRow");
        });
        this._invalidRows = [];

        try {
            globalObj.model.refreshTree();
        } catch(ex) {
            dojo.stopEvent(event);

            var invalidRowsIdentified = this._invalidRows.length > 0;

            var errorMessage = invalidRowsIdentified ? i18N.invalidConditionsWhileSaving : i18n.saveFailed;

            if(invalidRowsIdentified) {
                dojo.forEach(this._invalidRows, function(rowNodeId) {
                   dojo.addClass(dojo.byId(rowNodeId), "invalidRow");
                });

            }

            this._errorDialogContentPane.setContent(errorMessage);
            this.errorDialog.show();
        }

        var model = globalObj.model;

        this.jsonField.value = dojo.toJson(model.tree);
        this.referredPredicatesField.value = dojo.toJson(model.referredPredicates);
    },

    destroyAllRows : function() {
        var copyOfRows = this.rows.slice();
        for (var i in copyOfRows) {
            copyOfRows[i].destroyRow();
        }
    },

    clearRowRecordFor : function(/*int*/ rowId) {
        for (var i in this.rows) {
            if (this.rows[i].id == rowId) {
                this.rows.splice(i, 1);
                return;
            }
        }
    },

    launchSearchDialog : function() {
        globalObj.searchRulesDialog.show();
    },

    closeSearchDialog : function() {
        globalObj.searchRulesDialog.hide();
    },

    startRuleSearch : function() {
    	globalObj.ruleSearchWizard.startSearch(this.ruleSearchInput.value);
    },

    addSelectedRules : function() {
        var rules = globalObj.ruleSearchWizard.getSelectedRules();
        for (var i in rules) {
            this._addNewRuleRow(rules[i]);
        }
        this.closeSearchDialog();
    },
    _hideErrorDialog : function() {
        this.errorDialog.hide();
    }
});

dojo.declare("tavant.twms.ruleBuilder.Row", null, {

    id : 0,

    domNode : null,//row's domNode
    tdIndentIndicator : null,
    tdFoldingToggler : null,
    tdCollectionSelector : null,
    tdLhs : null,
    tdOp : null,
    tdRhs : null,//td element holding rhsElement(input/datePicker, or whatever)
    loadingIndicator : null,
    tdRowAdd : null,
    tdRowDelete : null,
    deleteButton : null,
    addButton : null,
    lhsComboBox : null,//dojo combo box
    operatorComboBox : null,//dojo combo box
    collectionSelectorComboBox : null,
    foldingToggler : null,
    rhsElementValueSetter : null,//this will be a function obj, which will set a given value to the element.

    baseLhsValue : "",
    collectionSelectorValue : "",
    lhsValue : "",
    rhsValue : "",
    operatorValue : "",
    subConditionConjunction : "all",

    isASimpleRow : false,
    isACollectionRow : false,
    isAnEntityRow : false,
    isSubRow : false,
    subRows : [],
    subRowIndex : 0,
    parentRow : null,
    nestingLevel : 0,
    fullLhsType : null,
    isBeingInitialized : false,
    isFolded: false,
    collapseNodeClass: "collapseSubRows",
    expandNodeClass: "expandSubRows",

    _operatorComboListenTopic: null,
    _collapseTooltip: "",
    _expandTooltip: "",

    constructor : function(/* Node */ parentDomNode,
                                      /*Expression(the whole object, having left, right and all..)*/ expression,
                                      /*int*/ id,
                                      /* boolean */ isSubRow,
                                      /* Parent Row */ parentRow) {

        this.isBeingInitialized = (expression != null);
        if(globalObj.controller._conditionNameSetByUser == null ){
        	globalObj.controller._conditionNameSetByUser = (expression != null);
        }
        this.id = id;
        
        if (isSubRow) {
            this.isSubRow = isSubRow;
            this.parentRow = parentRow;
            this.nestingLevel = parentRow.nestingLevel + 1;
        }

        this._generateRow(parentDomNode);
        this.subRows = [];

        if (expression == null) {
            this.lhsComboBox.selectFirstValueIfAny();
            return;
        }

        //else we start setting the existing values
        this.baseLhsValue = expression.baseName;
        if(!isSubRow){
            this.lhsComboBox.set("value",expression.left.baseName,false,expression.left.name, {key:expression.left.baseName,label:expression.left.name});
        }            
        else{
            var label = globalObj.model.getDataElementFor(expression.left.baseName).name;
            this.lhsComboBox.set("value",expression.left.baseName,false,label, {key:expression.left.baseName,label:label});
        }
        // Are you wondering why we are saying don't fire onChange event, and call it manually..?
        // Answer is hitched functions are on stack, so last change get's fired first which is not we want :)
        this._lhsChanged(expression.left.baseName);
        var subConditions = expression.subConditions;
        var subConditionsCount = subConditions ? subConditions.length : 0;

        if (subConditionsCount > 0) {

            this.subConditionConjunction = expression.subConditionConjunction;

            dojo.forEach(subConditions, function(subCondition) {
                this._generateSubRow(subCondition);
            }, this);

            if (this.isACollectionRow) {
                this.collectionSelectorComboBox.setValue(expression.collectionSelector);
            }

            var toolTipSuffix = subConditionsCount + " Sub Condition(s).";
            this._collapseTooltip = "Hide All " + toolTipSuffix;
            this._expandTooltip = "View All " + toolTipSuffix;

            this.foldingToggler.setAttribute("title", this._collapseTooltip);

            if (globalObj.model.readOnly) {
                this._addReadOnlyModeSummarySection();
            }

        } else {
            this.operatorComboBox.set('value',dojo.string.trim(expression.name),false);
            this._operatorChanged(dojo.string.trim(expression.name));
            var rhsValue = (expression.right) ? expression.right.value : "";
            this.rhsElementValueSetter(rhsValue);

        }

        this.isBeingInitialized = false;
    },

    _addReadOnlyModeSummarySection: function() {
        var lhsNode = this.tdLhs;
        var summarySpan = document.createElement("span");
        // *Do Not* remove this line even if the class is not found in css! It is used for dojo.query lookup.
        dojo.addClass(summarySpan, "readOnlySummarySection");
        summarySpan.innerHTML = "{...}";
        dojo.connect(summarySpan, "onclick", this, "_toggleFolding");
        summarySpan.setAttribute("title", this._expandTooltip);

        //TODO: See why putting "display:none" in the css doesn't work...
        this._showOrHideElement(summarySpan, false);
        lhsNode.appendChild(summarySpan);
    },

    _generateSubRow : function(expression) {

        if (this.subRowIndex == 0) {
            this._adjustTogglerVisibility(true);
        } else if (this.subRowIndex == 1) {
            this._showOrMakeInvisibleElement(this.subRows[0].deleteButton, true);
        }

        var row = new tavant.twms.ruleBuilder.Row(this.domNode, expression,
                this.id + "_" + this.subRowIndex, true, this);

        this.subRows.push(row);
        this.subRowIndex++;

        this._adjustTogglerVisibility(true);

        if (!expression) {
            row.indentAsPerNesting();
        }
        return row;
    },

    _toggleFolding : function(evt) {
        this._expandOrCollapseSubRows(this.isFolded);

        if(globalObj.model.readOnly && this.subRows.length > 0) {
            var summarySpanNode = dojo.query("span.readOnlySummarySection", this.tdLhs)[0];
            this._showOrHideElement(summarySpanNode, this.isFolded);
        }
    },

    _expandOrCollapseSubRows : function(expand) {
        dojo.forEach(this.subRows, function(subRow) {
            subRow._expandOrCollapseSubRows(expand);
            subRow._showOrHide(expand);
        });

        this.foldingToggler.setAttribute("title", (expand ? this._collapseTooltip : this._expandTooltip));

        var cssClassToBeAdded  = (expand) ? this.collapseNodeClass : this.expandNodeClass;
        var cssClassToBeRemoved = (expand) ? this.expandNodeClass : this.collapseNodeClass;

        dojo.removeClass(this.foldingToggler, cssClassToBeRemoved);
        dojo.addClass(this.foldingToggler, cssClassToBeAdded);

        this.isFolded = !expand;
    },

    _findLastAnyLevelChild : function() {
        var lastChild = this;
        var subRowsUnderScan = lastChild.subRows;

        while (subRowsUnderScan.length > 0) {
            lastChild = subRowsUnderScan[subRowsUnderScan.length - 1];
            subRowsUnderScan = lastChild.subRows;
        }

        return lastChild;
    },

    indentAsPerNesting : function() {
        // The logic in the "else" block is the best and safest one, but it
        // doesn't work if the combo boxes are hidden. Hence, for read only
        // scenario, we use a less efficient approach.
        if (globalObj.model.readOnly) {
            var indentationScale = (this.parentRow.isSubRow) ? 35 : 20;
            this.tdIndentIndicator.style.paddingLeft =
            (this.nestingLevel * indentationScale) + "px";
        } else {
            var parentRow = this.parentRow;
            var parentAlignmentRef = (parentRow.isACollectionRow) ?
                                     parentRow.collectionSelectorComboBox :
                                     parentRow.lhsComboBox;
            var alignPos = dojo.coords(parentAlignmentRef.domNode);
            dojo.style(this.tdIndentIndicator, "paddingLeft", (alignPos.x - 15) + "px");
        }

        dojo.forEach(this.subRows, function(subRow) {
            subRow.indentAsPerNesting();
        });
    },

    _generateRow : function(/* Dom Node */ parentDomNode,/*String initialValue*/ initialValue) {
        /* The fullest possible component structure of a row is:
           *
           * [Collection Selector V  ] [LHS V ]
           *                               |
           *                               |
           *                               [Collection Element V  ] [Op V  ] [RHS]
           *
           * And the more frequent structure, i.e. for a non-collection LHS, is:
           *
           *                 [LHS V ]      [Op V ]     [RHS]
           *
           */

        /* The UI structure of a Row is:
         *
         *  <tr class="expressionRow">
         *      <td>
         *          <table>
         *              <tr>
         *                  <td>...</td> --> Collection Selector (hidden)
         *                  <td>...</td> --> LHS
         *                  <td>...</td> --> Operator
         *                  <td>...</td> --> RHS
         *              </tr>
         *          </table>
         *      </td>
         *      <td>
         *          <div class="addButton" />   --> "Add Row" button
         *      </td>
         *      <td>
         *          <div class="deleteButton" />   --> "Delete Row" button
         *      </td>
         *  </tr>
         *
         * We are using the inner table to ensure that when a Row is indented,
         * the ones above/below it are not stretched by the browser to align
         * them with the indented one. Having them inside the table makes them
         * independent of each other, thereby avoiding the problem.
         */

        var model = globalObj.model;

        // Root node (TR)
        this.domNode = document.createElement("tr");
        this.domNode.id = "__ruleBuilderRow_" + this.id;
        dojo.addClass(this.domNode, "expressionRow");

        // Indent Indicator row (TD->DIV->IMG).
        this.tdIndentIndicator = globalObj.createStyledTd();
        if (this.isSubRow) {
            var indentIndicator = document.createElement("div");
            dojo.addClass(indentIndicator, "indentIndicator");
            this.tdIndentIndicator.appendChild(indentIndicator);
        }
        dojo.addClass(this.tdIndentIndicator, "indentIndicatorTd");
        this._showOrHideElement(this.tdIndentIndicator, this.isSubRow);

        // "Expand/Collapse" button (TD->DIV->IMG).
        this.tdFoldingToggler = globalObj.createStyledTd();
        this.foldingToggler = document.createElement("div");
        dojo.addClass(this.foldingToggler, "collapseSubRows");
        dojo.connect(this.foldingToggler, "onclick", dojo.hitch(this, this._toggleFolding));
        this.tdFoldingToggler.appendChild(this.foldingToggler);
        dojo.addClass(this.tdFoldingToggler, "foldingTogglerTd");
        this._adjustTogglerVisibility(false);

        //  Collection Selector (TD->SELECT).
        this.tdCollectionSelector = globalObj.createStyledTd();
        var collectionSelector = document.createElement("select");
        this.tdCollectionSelector.appendChild(collectionSelector);
        this._showOrHideCollectionSelector(false);
        dojo.addClass(this.tdCollectionSelector, "collectionSelectorTd");

        //  LHS  (TD->SELECT).
        this.tdLhs = globalObj.createStyledTd();
        var lhs = document.createElement("select");
        this.tdLhs.appendChild(lhs);
        dojo.addClass(this.tdLhs, "lhsComboBoxTd");

        //  Operator (TD->SELECT).
        this.tdOp = globalObj.createStyledTd();
        var operator = document.createElement("select");
        this.tdOp.appendChild(operator);
        dojo.addClass(this.tdOp, "operatorComboBoxTd");

        //  RHS (TD->SELECT).
        this.tdRhs = globalObj.createStyledTd();
        dojo.addClass(this.tdRhs, "rhsTd");

        // "Loading" indicator.
        var tdLoadingIndicator = globalObj.createStyledTd();
        this.loadingIndicator = document.createElement("div");
        dojo.addClass(this.loadingIndicator, "loadingIndicator");
        tdLoadingIndicator.appendChild(this.loadingIndicator);

        if (!globalObj.model.readOnly) {
            //  "Delete Row" button (TD->DIV->IMG).
            this.tdRowDelete = globalObj.createStyledTd();
            this.deleteButton = document.createElement("div");
            this.deleteButton.title = (this.isSubRow) ?
                                      i18N.delete_condition_subRow_title :
                                      i18N.delete_condition_row_title;
            dojo.connect(this.deleteButton, "onclick",
                    dojo.hitch(this, this._processDelete));
            // Hide the delete button, if this is the lone subrow of parent.
            this._showOrMakeInvisibleElement(this.deleteButton, !this._isPrimarySubRow());
            dojo.addClass(this.deleteButton, "deleteButton");
            this.tdRowDelete.appendChild(this.deleteButton);

            //  "Add Row" button (TD->DIV->IMG).
            this.tdRowAdd = globalObj.createStyledTd();
            this.addButton = document.createElement("div");
            this.addButton.title = i18N.add_condition_subRow_title;
            dojo.connect(this.addButton, "onclick",
                    dojo.hitch(this, function() {
                        this._generateSubRow(null).indentAsPerNesting();
                    }));
            this._showOrMakeInvisibleElement(this.addButton, false);
            dojo.addClass(this.addButton, "addButton");
            this.tdRowAdd.appendChild(this.addButton);
        }

        // Build the final Row, using these components.
        var containerRow = document.createElement("tr");
        containerRow.appendChild(this.tdIndentIndicator);
        containerRow.appendChild(this.tdFoldingToggler);
        containerRow.appendChild(this.tdCollectionSelector);
        containerRow.appendChild(this.tdLhs);
        containerRow.appendChild(this.tdOp);
        containerRow.appendChild(this.tdRhs);
        containerRow.appendChild(tdLoadingIndicator);

        var containerTable = document.createElement("table");
        var containerTBody = document.createElement("tbody");
        containerTBody.appendChild(containerRow);
        containerTable.appendChild(containerTBody);
        var containerTableHolder = globalObj.createStyledTd();
        containerTableHolder.appendChild(containerTable);
        this.domNode.appendChild(containerTableHolder);

        if (!globalObj.model.readOnly) {
            this.domNode.appendChild(this.tdRowAdd);
            this.domNode.appendChild(this.tdRowDelete);
        }

        if (this.isSubRow) {
            if (this.parentRow.subRowIndex == 0) {
                dojo.dom.insertAfter(this.domNode, parentDomNode);
            } else {
                dojo.dom.insertAfter(this.domNode,
                        this.parentRow._findLastAnyLevelChild().domNode);
            }
        } else {
            parentDomNode.appendChild(this.domNode);
        }

        var selfObj = this;

        this.collectionSelectorComboBox = globalObj.createComboBox(collectionSelector,
                model.getAllCollectionSelectors(),
                dojo.hitch(this, function() {
                    selfObj.collectionSelectorValue =
                    selfObj.collectionSelectorComboBox.getValue();
                }));

        this.collectionSelectorComboBox.selectFirstValueIfAny();
        dojo.addClass(this.collectionSelectorComboBox.domNode,
                "collectionSelectorComboBoxSpan");

        var data = this.isSubRow ? model.getAttributesForProperty(this.parentRow.lhsValue) : model.getAllLhsNames();
        this.lhsComboBox = globalObj.createComboBox(lhs, data, dojo.hitch(this, this._lhsChanged));
        dojo.addClass(this.lhsComboBox.domNode, "lhsComboBoxSpan");

        this._operatorComboListenTopic = "/ruleBuilder/operator/" + this.id;
        this.operatorComboBox = globalObj.createComboBox(operator, null, 
        dojo.hitch(this, this._operatorChanged), 
        {
            handleEmptyValues : false,
            required: false,
            listenTopics: this._operatorComboListenTopic
        });
    },

    _lhsChanged : function(/*new value*/ value) {
        var model = globalObj.model;

        if(value && value.indexOf(model.SEPARATOR_TYPE) != -1) {
            this.lhsComboBox.revertToValidValue(this.lhsValue);
            return;
        }
        
        if(!value || value === this.lhsValue) return;

        this.lhsValue = value;


        var fieldsInNewLhs = model.getAttributesForProperty(value);
        var doesNewLhsHaveFields = fieldsInNewLhs && fieldsInNewLhs.length > 0;

        var isNewLhsACollection = doesNewLhsHaveFields &&
                                  model.isDataElementACollection(value);
        var isNewLhsAnEntity = doesNewLhsHaveFields &&
                               model.isDataElementAnEntity(value);

        this.isASimpleRow = !isNewLhsACollection && !isNewLhsAnEntity;
        this.isACollectionRow = isNewLhsACollection;
        this.isAnEntityRow = isNewLhsAnEntity;

        this._adjustForSimple2CompoundOrBack();

        if (this.isSubRow && this.parentRow.isAnEntityRow) {
            var parentLhsType = this.parentRow.fullLhsType;
            var subLhsType = model.getIdFor(value);
            var indexOfHash = subLhsType.indexOf("#");

            var baseField = subLhsType.substring(indexOfHash + 1);
            this.fullLhsType = parentLhsType + "." + baseField;
        } else {
            this.fullLhsType = model.getIdFor(value);
        }

        this.operatorValue = null;

        dojo.publish(this._operatorComboListenTopic, [{
            items: model.getApplicableOperatorsFor(value),
            makeLocal: true
        }]);
        
        if (this.isASimpleRow) {
            this.operatorComboBox.selectFirstValueIfAny();
        } else {
            this.operatorComboBox.setValue("");
            this._operatorChanged(""); // since onChange won't be fired if the earlier value was also "".
        }
    },

    _adjustForSimple2CompoundOrBack : function() {
        this._showOrHideCollectionSelector(this.isACollectionRow);
        this._showOrHideRhs(this.isASimpleRow);
        this._showOrMakeInvisibleElement(this.addButton, !this.isASimpleRow);
    },

    _operatorChanged : function(/*new value*/ value) {
        if(value === this.operatorValue)return;
        var operatorIsEmpty = (value === "");

        if (this.isASimpleRow && operatorIsEmpty) {
            this.operatorComboBox.revertToValidValue(this.operatorValue);
            return;
        }

        this._adjustUIForOperatorChanged(operatorIsEmpty);

        this.operatorValue = value;

        if (operatorIsEmpty) {
            return;
        }

        var rhsType = globalObj.model.getRhsTypeAdjustedForOperatorAndLhs(this.lhsValue, value);

        this._changeRhsTd(rhsType);

    },

// if this is a collection or entity row, show/hide sub rows and hide/show
// rhs. Else, cancel the selection (i.e. select the previously set value,
// or the first value if there was nothing set ).
    _adjustUIForOperatorChanged : function(operatorIsEmpty) {
        if (this.isBeingInitialized) {
            return;
        }

        this._showOrHideRhs(!operatorIsEmpty);
        this._showOrHideCollectionSelector(this.isACollectionRow &&
                                           operatorIsEmpty);
        //            this._showOrHideSubRows(operatorIsEmpty);
        this._destroyAllSubRows();

        if (operatorIsEmpty) {
            this._expandOrCollapseSubRows(true);
            this._generateSubRow();
        }
    },

    _rhsChanged : function(/*new value*/ value,
                                         /* meaningful name for suggest */ rhsNameForSuggest) {
    	if(value == 0 ){
            this.rhsValue = value;
    	}
        if(!value  || value === this.rhsValue)return;
        this.rhsValue = value;

        if(this._shouldAutoSuggestExpressionName()) {
            this._suggestExpressionNameFromChosenValues(rhsNameForSuggest);
        }
    },

    _shouldAutoSuggestExpressionName : function() {
    	return	((dojo.string.isBlank(globalObj.controller.expressionNameField.getValue()))
	     			   ||
	     		(!globalObj.controller._conditionNameSetByUser));
    },

    _changeRhsTd : function(/*String*/ dataType) {
        this._removeInvalidFieldsForExRhs();

        if (this.subRowIndex == 0 && !this.isBeingInitialized) {
        	if (this.lhsValue && this.operatorValue) {
		        var operatorLabel = this.operatorComboBox.getDisplayedValue(this.operatorValue);
		        operatorLabel=dojo.string.trim(operatorLabel);
		        var lhsLabel = this.lhsComboBox.getDisplayedValue(this.lhsValue);
		        lhsNameField = dojo.string.trim(lhsLabel+" "+operatorLabel);
		        if(this._shouldAutoSuggestExpressionName()) {
		        	globalObj.controller.expressionNameField.setValue(lhsNameField);
		        	globalObj.controller._conditionNameSetByUser = false;
		        }
        	}
        }
        
        var rhsObj = globalObj.rhsManager.getRhs(dataType,
                dojo.hitch(this, this._rhsChanged), 
                this.lhsValue,
                this.operatorValue, this.rhsValue);
        var oldTdNode = dojo.dom.replaceNode(this.tdRhs, rhsObj.tdNode);
        dojo.dom.destroyNode(oldTdNode);
        
        this.tdRhs = rhsObj.tdNode;

        this.rhsElementValueSetter = rhsObj.valueSetter;
    },

    _showOrHideCollectionSelector : function(show) {
        this._showOrHideElement(this.tdCollectionSelector, show);
    },

    _showOrHideRhs : function(show) {
        this._removeInvalidFieldsForExRhs();

        this._showOrHideElement(this.tdRhs, show);
    },

    _removeInvalidFieldsForExRhs : function() {		
			this._removeInvalidFieldsForTds(this.tdRhs);
    },
    
    _removeInvalidFieldsForTds : function(rowElement) { 
    	if(!globalObj.model.readOnly) {   	
			dojo.query("[widgetId]", rowElement).forEach(function(widgetNode) {
				globalObj.controller.removeInvalidField(widgetNode.getAttribute("widgetId"));
			});
			globalObj.validateSubmission();
    	}
    },

    _showOrHide : function(show) {
        var fadeOperation = show ? dojo.fadeIn : dojo.fadeOut;
        var currentOpacity = show ? 0 : 1;

        var fadeAnim = fadeOperation({
                node: this.domNode,
                start: currentOpacity
        });

        var connectEvent = show ? "beforeBegin" : "onEnd";

        dojo.connect(fadeAnim, connectEvent, this, function(propValues) {
            this._showOrHideElement(this.domNode, show);
        });

        fadeAnim.play();
    },

    _showOrHideElement : function(element, show) {
        if(element) {
            dojo.html.setDisplay(element, show);
        }
    },

    _showOrMakeInvisibleElement : function(element, show) {
        if(element) {
            dojo.html.setVisibility(element, show);
        }
    },

    _showOrHideSubRows : function(show) {
        for (var i in this.subRows) {
            this.subRows[i]._showOrHide(show);
        }
    },

    _adjustTogglerVisibility: function(show) {
        var visibilityOperation = this.isSubRow ? this._showOrHideElement : this._showOrMakeInvisibleElement;
        visibilityOperation(this.tdFoldingToggler, show);
    },

    _suggestExpressionNameFromChosenValues : function(meaningfulRhsName) {
        if (this.subRowIndex == 0 && !this.isBeingInitialized) {

            if (this.lhsValue && this.operatorValue) {
            	var suggestedName = dojo.string.trim(lhsNameField);
                var rhsValue = "";
                if (globalObj.model.isDuplicateCheck || this.rhsValue) {
                    rhsValue = " ";

                    if (meaningfulRhsName) {
                        rhsValue += meaningfulRhsName;
                    } else {
                        if (dojo.lang.isArray(this.rhsValue)) {
                            var descriptor = " ";
                            if (dojo.indexOf(globalObj.intervalOperators,
                                    this.operatorValue) != -1) {
                                descriptor = " and ";
                            }

                            // We *have* to do an explicit null check, otherwise
                            // a value of "0" would be construed as false.
                            if (!dojo.string.isBlank(this.rhsValue[0]) &&
                                this.rhsValue[1] != null) {
                                rhsValue += this.rhsValue[0] + descriptor +
                                            this.rhsValue[1];
                            }
                        } else {
                            rhsValue += this.rhsValue;
                        }
                    }

                    suggestedName += rhsValue;

                }

            	globalObj.controller.expressionNameField.setValue(suggestedName);	
                globalObj.controller._conditionNameSetByUser = false;
            }
        }
    },

    getNode : function() {
        if (this.isAnEntityRow && this.subRowIndex > 0) {
            var nodes = [];

            for (var i in this.subRows) {
                nodes.push(this.subRows[i].getNode());
            }

            return {
                type: "OPERATOR",//FIXME: this should not be hardcoded(atleast not here)
                name: this.subConditionConjunction,
                oneToOneVariable : globalObj.model.getIdFor(this.lhsValue),
                nodes: nodes,
                isForOneToOne: true
            };
        } else {
            return globalObj.model.makeNodeFromValues({
                "lhsValue" : this.lhsValue,
                "baseLhsValue" : this.baseLhsValue,
                "operatorValue" : this.operatorValue,
                "rhsValue" : this.rhsValue,
                "collectionSelectorValue" : this.collectionSelectorValue,
                "subRows" : this.subRows,
                "subConditionConjunction": this.subConditionConjunction,
                "fullLhsType" : this.fullLhsType
            });
        }
    }
    ,

    _isPrimarySubRow : function() {
        return this.isSubRow && this.parentRow.subRowIndex == 0;
    }
    ,

    _processDelete : function() {
        if (this.isSubRow) {
            this.parentRow._destroySubRow(this.id);
            if (this.parentRow.subRowIndex == 1) {
                this._showOrMakeInvisibleElement(this.parentRow.subRows[0].deleteButton,
                        false);
            }
        } else {
            this.destroyRow();
        }
    },

    _destroyAllSubRows : function() {
        var copyOfSubRows = this.subRows.slice();
        for (var i in copyOfSubRows) {
            this._destroySubRow(copyOfSubRows[i].id);
        }

        this._adjustTogglerVisibility(false);
    }
    ,

    _destroySubRow : function(subRowId) {
        var subRowToBeDeleted = this._clearSubRowRecordFor(subRowId);
        subRowToBeDeleted._removeInvalidFieldsForExRhs();
        this._removeInvalidFieldsForTds(this.tdRhs);
        this._removeInvalidFieldsForTds(this.tdLhs);
        this._removeInvalidFieldsForTds(this.tdOp);
        subRowToBeDeleted._destroyAllSubRows();
        dojo.dom.destroyNode(subRowToBeDeleted.domNode);
        this.subRowIndex--;
    }
    ,

    _clearSubRowRecordFor : function(/*int*/ subRowId) {
        for (var i in this.subRows) {
            if (this.subRows[i].id == subRowId) {
                var subRowToBeDeleted = this.subRows[i];
                this.subRows.splice(i, 1);
                return subRowToBeDeleted;
            }
        }
    }
    ,

    destroyRow : function() {
        this._destroyAllSubRows();
        globalObj.controller.clearRowRecordFor(this.id);
        this._removeInvalidFieldsForTds(this.tdRhs);
        this._removeInvalidFieldsForTds(this.tdLhs);
        this._removeInvalidFieldsForTds(this.tdOp);
        this._removeInvalidFieldsForExRhs();
        dojo.dom.destroyNode(this.domNode);
    }

});

dojo.declare("tavant.twms.ruleBuilder.PrebuiltRuleRow", null, {

    id : 0,

    tdMain : null,
    domNode : null,//row's domNode
    expression : null,//the rule object(getting it frm the constructor).

    constructor : function(/*Expression(the whole object, having left, right and all..)*/ expression, /*int*/ id) {
        this._generateRow();
        this.id = id;
        if (expression == null) {
            throw "A PrebuiltRuleRow can not be instantiated without expression. In this case it is null!!!";
        } else if (expression.type != "RULE_FRAGMENT") {
            throw "A PrebuiltRuleRow can only be used only for expression type RULE_FRAGMENT. In this case, expression type is '" + expression.type + "'";
        }
        this.expression = expression;
        //else we start setting the existing values
        this.tdMain.innerHTML = expression.name;
    },

    _generateRow : function() {
        this.domNode = document.createElement("tr");
        var tdDelete = globalObj.createStyledTd();
        this.tdMain = globalObj.createStyledTd();
        this.tdMain.setAttribute("colSpan", 2);
        //HACK: attributes are case insensitive, but because IE is DUMBO!!! this is needed to get it going!!!
        this.domNode.appendChild(this.tdMain);
        this.domNode.appendChild(tdDelete);
        dojo.addClass(this.domNode, "expressionRow");

        if (!globalObj.model.readOnly) {
            var deleteButton = document.createElement("div");
            deleteButton.title = i18N.delete_condition_row_title;
            dojo.addClass(deleteButton, "deleteButton");
            tdDelete.appendChild(deleteButton);
            dojo.connect(deleteButton, "onclick", dojo.hitch(this,
                    this.destroyRow));
        }
    },

    getNode : function() {
        return this.expression;
    },

    destroyRow : function() {
        globalObj.controller.clearRowRecordFor(this.id);
        dojo.dom.destroyNode(this.domNode);
        delete this;
        //i don't know if this will do something extra... but it doesn't harm
    }
});