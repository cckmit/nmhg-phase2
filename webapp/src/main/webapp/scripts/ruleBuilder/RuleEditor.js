dojo.require("twms.widget.RuleBuilderSelect");
dojo.require("twms.widget.ValidationTextBox");
dojo.require("twms.widget.NumberTextBox");
dojo.require("twms.widget.DateTextBox");
dojo.subscribe("/validation",function(message){
	globalObj.commonValidationCallBack(message);
});
var globalObj = {
    model : null,
    controller : null,
    rhsManager : null,
    ruleSearchWizard : null,
    searchRulesDialog : null,
    searchResultsPane : null,     
    
    operatorsWithoutRhs : ["label.operators.isSet", "label.operators.isNotSet", 
    						"label.operators.isInPartsReturnWatchList","label.operators.isInPartsReviewWatchList",
        "label.operators.isInDealerWatchList"],
    intervalOperators : ["label.operators.between", "label.operators.notbetween"],
    comparisonOperators : ["label.operators.isGreaterThan", "label.operators.isLessThan"],
    datePlainDurationOperators : ["label.operators.isWithinLast", "label.operators.isNotWithinLast",
            "label.operators.isExactlyGreaterBy", "label.operators.isAtleastGreaterBy", "label.operators.isAtmostGreaterBy",
            "label.operators.isExactlyLesserBy", "label.operators.isAtleastLesserBy", "label.operators.isAtmostLesserBy",
            "label.operators.isGreaterBy", "label.operators.isLesserBy",
            "label.operators.isWithinNext", "label.operators.isNotWithinNext"],
    dateCalendarDurationOperators : ["label.operators.isDuringTheLast", "label.operators.isNotDuringTheLast",
            "label.operators.isDuringTheNext", "label.operators.isNotDuringTheNext"],
    dateComparisonDurationOperators : ["label.operators.isGreaterBy", "label.operators.isLesserBy","label.operators.isNotLesserBy","label.operators.isNotGreaterBy"],
    watchedListOperators : ["label.operators.belongsTo","label.operators.doesNotBelongTo","label.operators.equalsEnum","label.operators.notEqualsEnum"],
    watchListsFetchUrl : "get_all_categories_of_a_kind.action",
    dealerGroupsFetchUrl : "get_all_dcap_groups.action",
    allCurrenciesJSON : null,

// Hack for bug HUSS-251
    labelDateComparisonOptionsMap : [
        ["label.deliveryDate","Delivery"],
        ["label.shipmentDate","Shipment"],
        ["label.claim.claimDate","Claim"],
        ["label.common.dateFailure","Failure"],
        ["label.common.dateInstall","Installation"],
        ["label.common.dateRepair","Repair"],
        ["label.inventory.dateOfShipment","Shipment"]
    ],

    _getRenderingMode : function() {
        return this.model.readOnly ? "readOnly" : "normal";
    },

    // Hack for bug HUSS-251
    //  return's the display name for the label passed else same base name will be returned
    // Can we handle this in FieldTraversal ..?
    _getDisplayNameForDateComparisonComboBox : function(/*Base name for the ComparisonComboBox*/ baseName){
          for(var i in globalObj.labelDateComparisonOptionsMap){
              if(globalObj.labelDateComparisonOptionsMap[i][0] == baseName)
                  return globalObj.labelDateComparisonOptionsMap[i][1];
          }
          return baseName;
    },

    createStyledTd : function() {
        var newTd = document.createElement("td");
        dojo.addClass(newTd, "ruleBuilder");

        return newTd;
    },

    createComboBox : function(/*domNode*/ node,/*data providing function*/ initialData,
        /*onChange callback*/ callbackFnObj, /* other options */ options) {  	
    	
        var comboOptions = {
            required: true,
            validationNotificationTopics: "/validation",
            renderingMode : globalObj._getRenderingMode(),
            node: node,
            initialData: initialData
        }
        
        dojo.mixin(comboOptions, options);

        var comboBox = new twms.widget.RuleBuilderSelect(comboOptions, node);

        if(callbackFnObj) {
            dojo.connect(comboBox, "onChange", function(newValue) {            	
                callbackFnObj(comboBox.getValue(),comboBox.getDisplayedValue()) // To handle the case that newValue would be undefined if combo
                // value is set to empty.
            });
        }

        return comboBox;
    },

    createValidationTextBox : function(input, options) {
		var type = options ? options.type : "";		
        var props = {
            trim: true,
            required: true,
            validationNotificationTopics: "/validation"
        };

        var inputAttributes =
                ["id", "name", "value", "maxlength", "size", "className"];

		for (var j in inputAttributes) {
            var property = inputAttributes[j];
            if (input[property]) {
				props[property] = input[property];
            }
        }

        dojo.mixin(props, options);

        var textBox;

        if ("integer" === type) {
            textBox = new twms.widget.NumberTextBox(props, input);
        } else if ("date" === type) {
            textBox = new twms.widget.DateTextBox(props, input);
            
            textBox.setDatePattern(userDateFormat);	
            
            if(!options.loneElementInRhs) {
 	 			dojo.style(textBox.domNode, "width", "35%");
 	 		}
 	 		                         
 	 		if(props.onChange) {
 	 	 		var onChangeHandler = props.onChange;
 	 			props.onChange = function(newValue) {
 	 			onChangeHandler(this.getDisplayedValue());
 	 		}
 	 	}
        } else {
            props.type = "input";
            textBox = new twms.widget.ValidationTextBox(props, input);
        }

        if(props.onChange) {
            dojo.connect(textBox, "onChange", props.onChange);
                /*globalObj.commonValidationCallBack(validationResult, this.id);

                if (!validationResult.validationFailed && onChange) {
                    onChange(validationResult.value);
                }*/
        }

        return textBox;
    },

    commonValidationCallBack : function(validationResult) {
        if (globalObj.controller) {
            if (validationResult.isValid) {
				globalObj.controller.removeInvalidField(validationResult.sourceId);                
            } else {
                globalObj.controller.addInvalidField(validationResult.sourceId);
            }

            globalObj.validateSubmission();
        }
    },

    validateSubmission : function() {
		if(!globalObj.model.readOnly) {
			var expressionName = globalObj.controller.expressionNameField.getValue();
			var allowSave = !dojo.string.isBlank(expressionName) &&
							globalObj.controller.isExpressionFullyBuilt();
			this._toggleSaveCapability(allowSave);
		}
    },

    _toggleSaveCapability : function(allowSave) {
        if (allowSave) {
            $("saveButton").removeAttribute("disabled");
        } else {
            $("saveButton").setAttribute("disabled", "disabled");
        }
    }
}

dojo.declare("tavant.twms.ruleBuilder.Model", null, {

    tree : null,
    referredPredicates : null,
    topLevelDataElements : null,
    topLevelDuplicacyDataElements : null,
    allDataElements : null,
    readOnly : false,
    isDuplicateCheck : false,
    isDcapContext : false,
    isInventoryContext : false,
    allDuplicacyDataElements : null,
    SEPARATOR_TYPE: "_SEPARATOR_",

    constructor : function(/*initial tree*/ tree,
                                            /*Array of possible top level LHSs*/ topLevelDataElements,
                                            /*Array of possible any level LHSs*/ allDataElements,
                                            /*Array of possible claim duplicacy check LHSs*/ topLevelDuplicacyDataElements,
                                            /** render as read only? */ readOnly,
                                            /** claim duplicacte check? */ isDuplicateCheck,
                                            /**is context DCAP? */isDcapContext,
                                            /**is context INVENTORY */isInventoryContext,
                                            /**Array of possible any Duplicacy Level LHSs*/ allDuplicacyDataElements) {
	    this.tree = tree;
        this.topLevelDataElements = topLevelDataElements;
        this.allDataElements = this.topLevelDataElements;
        this.readOnly = readOnly;
        this.topLevelDuplicacyDataElements = topLevelDuplicacyDataElements;
        this.isDuplicateCheck = isDuplicateCheck;
        this.allDuplicacyDataElements = allDuplicacyDataElements;

        if (allDataElements && allDataElements.length > 0) {
            this.allDataElements = allDataElements;
        }

    },

    getAllRootOperators : function() {
        var model = globalObj.model;
        return [
                [model.geti18NLableForOperator(" all "), " all "],
                [model.geti18NLableForOperator(" any "), " any "]
        ];
    },

    getCurrentOperatorValue : function() {
        return this.getOperator();
    },

	_getTopLevelElementsList : function() {
		return this.isDuplicateCheck ?
			this.topLevelDuplicacyDataElements :
			this.topLevelDataElements;
	},

    getAllLhsNames : function() {
        var names = new Array();
		var elementsList = this._getTopLevelElementsList();
        var index = 0; // Just a dummy var used to make sure that all the separators have unique keys.

        dojo.forEach(elementsList, function(element) {
            var keyValPair = new Array();

            if (element.type === this.SEPARATOR_TYPE) {
                // Separator.
                var separatorLabel = "------------------------------------------------";
                keyValPair.push(separatorLabel, this.SEPARATOR_TYPE + index);
                index++;
            } else {
                keyValPair.push(element.name);
                keyValPair.push(element.baseName);
            }

            names.push(keyValPair);
        }, this);

        return names;
    },

    getAllCollectionSelectors : function() {
        return [["For Any", "for any"], ["For Each", "for each"]];
    },

    getAttributesForProperty : function(/*String(name)*/ name) {
        return this.getDataElementFor(name).fields;
    },

    getApplicableOperatorsFor : function(/*String(name)*/ name) {
        return this.getDataElementFor(name).allowedOperators;
    },

    getDataTypeFor : function(/*String(name)*/ name) {
		return this.getDataElementFor(name).datatype;
    },

    getBaseNameFor : function(/*String(name)*/ name) {
        return this.getDataElementFor(name).baseName;
    },

    getIdFor : function(/*String(name)*/ name) {
        return this.getDataElementFor(name).id;
    },

    getDataElementFor : function(/*String(name)*/ name) {
        if(dojo.string.isBlank(name))return "";
		var elementsList = this.isDuplicateCheck ?
			this.allDuplicacyDataElements :
			this.allDataElements;

        for (var i in elementsList) {
            if (elementsList[i].baseName === name) {
                return elementsList[i];
            }
        }
    },

    isDataElementACollection : function(/*String(name)*/ name) {
        return this.getDataElementFor(name).isCollection;
    },

    isDataElementAnEntity : function(/*String(name)*/ name) {
        return this.getDataElementFor(name).isEntity;
    },

    isDataElementSimpleVariable : function(/*String(name)*/ name) {
        return this.getDataElementFor(name).isSimpleVariable;
    },

    getNodes : function() {
        return this.tree.nodes;
    },

    getOperator : function() {
        return this.tree.name;
    },

    getRhsTypeAdjustedForOperatorAndLhs : function(lhsValue, operatorValue) {
        var opVal = dojo.string.trim(operatorValue);
        if (dojo.indexOf(globalObj.operatorsWithoutRhs, opVal) != -1) {
            return "boolean";
        } else if (dojo.indexOf(globalObj.watchedListOperators, opVal) != -1) {
            return "watchList";
        } else if (dojo.indexOf(globalObj.dateComparisonDurationOperators, opVal) != -1
                 && !this.isDuplicateCheck) {
            return "dateComparisonDuration";
        } else if (dojo.indexOf(globalObj.datePlainDurationOperators, opVal) != -1) {
            return "datePlainDuration";
        } else if (dojo.indexOf(globalObj.dateCalendarDurationOperators, opVal) != -1) {
            return "dateCalendarDuration";
        } else if (dojo.indexOf(globalObj.intervalOperators, opVal) != -1) {
            var type = this.getDataTypeFor(lhsValue);
            return ("date" === type) ? "dateInterval" : "numberInterval";
        } else {
            return this.getDataTypeFor(lhsValue);
        }
    },

    makeNodeFromValues : function(expression) {
        var dataElement = this.getDataElementFor(expression.lhsValue);
        if (dataElement == null) {
            throw "Data element having name '" + expression.lhsValue +
                  "' was not found.";
        }

        var right = "";
        var name = expression.collectionSelectorValue;

        if (expression.subRows.length == 0) {
            var operatorsWithMultiValuedRhs = [" is one of"," is not one of","label.operators.equalsEnum","label.operators.notEqualsEnum" ];
            var rhsType = "CONSTANT";

            for (var i in operatorsWithMultiValuedRhs) {
                if (operatorsWithMultiValuedRhs[i] ==
                    expression.operatorValue) {
                    rhsType = "CONSTANTS";
                    break;
                }
            }

            var rhsValue = expression.rhsValue;
            var dataType = dataElement.datatype;
            //TODO: Refactor
            var trimmedOpVal = dojo.string.trim(expression.operatorValue);
            if ((dojo.indexOf(globalObj.watchedListOperators, trimmedOpVal) != -1) ||
                (dojo.indexOf(globalObj.intervalOperators, trimmedOpVal) != -1) ||
                (dojo.indexOf(globalObj.datePlainDurationOperators, trimmedOpVal) != -1) ||
                (dojo.indexOf(globalObj.dateCalendarDurationOperators, trimmedOpVal) != -1) ||
                (dojo.indexOf(globalObj.dateComparisonDurationOperators, trimmedOpVal) != -1)) {
                dataType = "string";
            } else if(rhsValue && (dataType === "string")) {
                rhsValue = dojo.string.trim(rhsValue);
            }

            right = {
                value: rhsValue,
                type: dojo.string.trim(rhsType),
                datatype: dataType
            };

            name = expression.operatorValue;
        }

        var dataId = (expression.fullLhsType) ? expression.fullLhsType :
                     dataElement.id;

        var left = {
            type: dojo.string.trim(dataElement.type),
            datatype: dojo.string.trim(dataElement.datatype),
            expression: dataElement.expression,
            name: expression.lhsValue,
            baseName: expression.lhsValue,
            id: dataId
        };

        var subConditions = new Array();
        dojo.forEach(expression.subRows, function(subRow) {
            subConditions.push(subRow.getNode());
        }, this);

        return {
            type: "EXPRESSION",//FIXME: this should not be hardcoded(atleast not here)
            name: dojo.string.trim(name),
            collectionSelector: expression.collectionSelectorValue,
            left: left,
            right: right,
            subConditions : subConditions,
            subConditionConjunction : expression.subConditionConjunction
        };
    },

    refreshTree : function() {
        this.tree = {
            type: "OPERATOR",//FIXME: this should not be hardcoded(atleast not here)
            name: globalObj.controller.operatorValue,
            isDuplicateCheck: this.isDuplicateCheck,
            nodes: globalObj.controller.getNewNodes()
        }

        this.referredPredicates = globalObj.controller.getReferredPredicates();
    },

    geti18NLableForOperator : function(/*String*/ labelValue) {
        return (labelValue === " any ") ?
               i18N.expressionEditor_operator_any :
               i18N.expressionEditor_operator_all;
    }
});
dojo.declare("tavant.twms.ruleBuilder.RhsManager", null, {

    durations : [
                ["days", 0],
                ["weeks", 1],
                ["months", 2]
                ],

    calendarDurations : [
        ["calendar week(s)", 1],
        ["calendar month(s)", 2]
        ],
        
    dateComparisonOptions : [
                            ["Claim",
                             "{'type':'VARIABLE','datatype':'date','expression':'undefined','name':'Claim Date','baseName':'Claim Date','id':'Claim#claim.filedOnDate'}"],
                            ["Failure",
                             "{'type':'VARIABLE','datatype':'date','expression':'undefined','name':'Date Of Failure','baseName':'Date Of Failure','id':'Claim#claim.failureDate'}"],
                            ["Installation",
                             "{'type':'VARIABLE','datatype':'date','expression':'undefined','name':'Date Of Installation','baseName':'Date Of Installation','id':'Claim#claim.installationDate'}"],
                            ["Repair",
                             "{'type':'VARIABLE','datatype':'date','expression':'undefined','name':'Date Of Repair','baseName':'Date Of Repair','id':'Claim#claim.repairDate'}"]                            
                            ],
	dateComparisonOptionsForDcap : [
                             ["Claim",
                             "{'type':'VARIABLE','datatype':'date','expression':'undefined','name':'Claim Date','baseName':'Claim Date','id':'DcapClaim#dcapClaim.filedOn'}"],
                             ["Shipment",
                             "{'type':'VARIABLE','datatype':'date','expression':'undefined','name':'Shipment Date','baseName':'Shipment Date','id':'DcapClaim#dcapClaim.inventoryItem.shipmentDate'}"],
                             ["Delivery",
                             "{'type':'VARIABLE','datatype':'date','expression':'undefined','name':'Delivery Date','baseName':'Delivery Date','id':'DcapClaim#dcapClaim.inventoryItem.deliveryDate'}"]
                             ],
    dateComparisonOptionsForinventorySearch : [
                             ["Installation",
                             "{'type':'VARIABLE','datatype':'date','expression':'undefined','name':'Date Of Installation','baseName':'Date Of Installation','id':'InventoryItem#inventoryItem.builtOn'}"],
                             ["Shipment",
                             "{'type':'VARIABLE','datatype':'date','expression':'undefined','name':'Date Of Shipment','baseName':'Date Of Shipment','id':'InventoryItem#inventoryItem.shipmentDate'}"]
                             ],                         
   
	
    dataTypeValueComponentMap : [],
    _categoryToNamedListsCache : [],
    claimDuplicacyOperatorsRhsMap : [],
    

    constructor : function() {		
        this.dataTypeValueComponentMap["money"] = dojo.hitch(this, this._getMoneyRhs);
        this.dataTypeValueComponentMap["date"] = dojo.hitch(this, this._getDateRhs);
        this.dataTypeValueComponentMap["boolean"] = dojo.hitch(this, this._getBooleanRhs);
        this.dataTypeValueComponentMap["watchList"] = dojo.hitch(this, this._getWatchedListsRhs);
        this.dataTypeValueComponentMap["numberInterval"] = dojo.hitch(this, this._getNumberIntervalRhs);
        this.dataTypeValueComponentMap["dateInterval"] = dojo.hitch(this, this._getDateIntervalRhs);
        this.dataTypeValueComponentMap["datePlainDuration"] = dojo.hitch(this, this._getDatePlainDurationRhs);
        this.dataTypeValueComponentMap["dateCalendarDuration"] = dojo.hitch(this, this._getDateCalendarDurationRhs);
        this.dataTypeValueComponentMap["dateComparisonDuration"] = dojo.hitch(this, this._getDateComparisonDurationRhs);
        this.dataTypeValueComponentMap["others"] = dojo.hitch(this, this._getFreeTextRhs);

        this.claimDuplicacyOperatorsRhsMap["label.operators.isSameAs"] = dojo.hitch(this,
                this._getClaimDuplicacyBlankRhs);
        this.claimDuplicacyOperatorsRhsMap["label.operators.isExactlyGreaterBy"] = dojo.hitch(
                this, this._getClaimDuplicacyComparisonRhs);
        this.claimDuplicacyOperatorsRhsMap["label.operators.isAtleastGreaterBy"] = dojo.hitch(
                this, this._getClaimDuplicacyComparisonRhs);
        this.claimDuplicacyOperatorsRhsMap["label.operators.isAtmostGreaterBy"] = dojo.hitch(
                this, this._getClaimDuplicacyComparisonRhs);
        this.claimDuplicacyOperatorsRhsMap["label.operators.isExactlyLesserBy"] = dojo.hitch(
                this, this._getClaimDuplicacyComparisonRhs);
        this.claimDuplicacyOperatorsRhsMap["label.operators.isAtleastLesserBy"] = dojo.hitch(
                this, this._getClaimDuplicacyComparisonRhs);
        this.claimDuplicacyOperatorsRhsMap["label.operators.isAtmostLesserBy"] = dojo.hitch(
                this, this._getClaimDuplicacyComparisonRhs);
        this.claimDuplicacyOperatorsRhsMap["label.operators.isGreaterBy"] = dojo.hitch(
                this, this._getClaimDuplicacyComparisonRhs);
        this.claimDuplicacyOperatorsRhsMap["label.operators.isLesserBy"] = dojo.hitch(
                this, this._getClaimDuplicacyComparisonRhs);
        this.claimDuplicacyOperatorsRhsMap["label.operators.isGreaterThan"] = dojo.hitch(
                this, this._getClaimDuplicacyComparisonRhs);
        this.claimDuplicacyOperatorsRhsMap["label.operators.isLessThan"] = dojo.hitch(
                this, this._getClaimDuplicacyComparisonRhs);
        
        if(isDcapContext){
	        this.dateComparisonOptions=this.dateComparisonOptionsForDcap;
        }else if(isInventoryContext){
	    	this.dateComparisonOptions=this.dateComparisonOptionsForinventorySearch;
	    }
    },

    getRhs : function(/*String (dataType)*/ dataType,
                                            /*function Object(onChange)*/ onChange,
                                            lhs, operator) {
        var trimmedOpVal = (operator) ? dojo.string.trim(operator) : "";

        if (globalObj.model.readOnly) {
            return this._getReadOnlyRhs(onChange, lhs, trimmedOpVal);
        }

        if(globalObj.model.isDuplicateCheck) {
            for (var j in this.claimDuplicacyOperatorsRhsMap) {
                if (j == trimmedOpVal) {
                    return this.claimDuplicacyOperatorsRhsMap[j](dataType,
                            onChange, lhs, operator);
                }
            }

            throw new Error("Unsupported claim duplicate check operator : " +
                       operator + ", for lhs : " + lhs + " of datatype : " +
                       dataType);
        }

        for (var i in this.dataTypeValueComponentMap) {
            if (i == dataType) {
                return this.dataTypeValueComponentMap[i](onChange,
                        lhs, operator);
            }
        }

        return this.dataTypeValueComponentMap.others(dataType, onChange, lhs,
                operator);
    },

    _isAComparisonOperator : function(operator) {
        return dojo.indexOf(globalObj.comparisonOperators, operator) != -1;
    },

    _isADateDurationOperator : function(operator) {

       return  this._isADatePlainDurationOperator(operator) ||
           this._isADateCalendarDurationOperator(operator);
    },

    _isADatePlainDurationOperator : function(operator) {
        return dojo.indexOf(globalObj.datePlainDurationOperators, operator) != -1;
    },

   _isADateCalendarDurationOperator : function(operator) {
        return dojo.indexOf(globalObj.dateCalendarDurationOperators,
             operator) != -1;
    },

    _isADateComparisonDurationOperators : function(operator) {
        return dojo.indexOf(globalObj.dateComparisonDurationOperators,
             operator) != -1;
    },

    _getReadOnlyRhs : function(onChange, lhs, operator) {
        var model = globalObj.model;
        var selfObj = this;

        var tdNode = globalObj.createStyledTd();
        tdNode.appendChild(document.createElement("span"));

        return {tdNode : tdNode,
            valueSetter : function(/*String */ newValue) {

                var valueToBeSet = newValue;

                var prefix = "";
                var isDateOperator = selfObj._isADateDurationOperator(operator);
                var suffix = "";

                if(model.isDuplicateCheck) {
                   prefix = (isDateOperator || selfObj._isAComparisonOperator(operator))  ?
                         selfObj._getComparisonPrefixForClaimDuplicacyRhs() :
                         selfObj._getBasePrefixForClaimDuplicacyRhs();
                }
                else if(isDateOperator &&
                       (selfObj._isADateComparisonDurationOperators(operator)&&
                            !model.isDuplicateCheck)){
                   prefix = globalObj._getDisplayNameForDateComparisonComboBox(newValue[2].baseName) +
                                       selfObj._getComparisonPrefixForDateComparisonRhs();
                 }

                if (dojo.indexOf(globalObj.intervalOperators, operator) != -1) {
                    valueToBeSet = newValue[0] + " and " + newValue[1];
                } else if(isDateOperator) {
                       suffix =
                         (selfObj._isADatePlainDurationOperator(operator) ||
                          (selfObj._isADateComparisonDurationOperators(operator)&&
                            !model.isDuplicateCheck)) ?
                             selfObj._getDescriptiveDuration(newValue[1]) :
                             selfObj._getDescriptiveCalendarDuration(newValue[1]);

                       valueToBeSet = newValue[0] +  " " + suffix;
                }

                tdNode.childNodes[0].innerHTML = prefix + valueToBeSet;

            },
            focusSetter : function() {
                // Nothing to do, since there is no RHS
            }};
    },
/**
 * This _get*Datatype*Rhs function will return a assoc array of this format...
 * {
 * 	tdNode : domNode of tdHolding the whole thing, which has to be appended to the row,
 *  valueSetter : function, that can be used to set the value of the rhs(will take new val as param).
 * }
 */
    _getFreeTextRhs : function(dataType,
                               /*function Object(onChange)*/ onChange,
                               lhs, operator) {
        var tdNode = globalObj.createStyledTd();
        var input = document.createElement("input");

        tdNode.appendChild(input);        
        var validationTextbox = globalObj.createValidationTextBox(input, {
            type: dataType,
            onChange : onChange
        });

        tdNode.validationTextboxIds = [validationTextbox.id];
        return {
            tdNode : tdNode,
            valueSetter : function(/*String*/ newValue) {
                validationTextbox.setValue(newValue);
                onChange(newValue);
            },
            focusSetter : function() {
                if (dojo.html.isVisible(input)) {
                    input.focus();
                }
            }};
    },

    _getMoneyRhs : function(/*function Object(onChange)*/ onChange) {
        var tdNode = globalObj.createStyledTd();

        // We have to define these variables upfront, along with a dummy
        // implementation, since they have a circular dependency.
        var dummy = {getValue : function() {
        }};

        var currencySelector = dummy;
        var amountTextBox = dummy;
    
        var select = document.createElement("select");
        tdNode.appendChild(select);

        currencySelector = globalObj.createComboBox(select, 
            globalObj.allCurrencies,
            function(newValue) {
                var amountVal = amountTextBox.getValue();
                onChange(newValue + " " + (amountVal ? amountVal : ""));
            }, {
                legacyDataMode: false
            });
        currencySelector.selectFirstValueIfAny();

        var input = document.createElement("input");

        tdNode.appendChild(input);

        amountTextBox = globalObj.createValidationTextBox(input, {
            onChange : function(newValue) {
                onChange(currencySelector.getValue() + " " + newValue);
            }
        });

        tdNode.validationTextboxIds = [amountTextBox.id];

        return {
            tdNode: tdNode,
            valueSetter: function(/*space separated value*/ currencyAndAmount) {
                if(!dojo.string.isBlank(currencyAndAmount)) {
                    currencyAndAmount = currencyAndAmount.split(/\s/ig);
                }
                
                if(currencyAndAmount[0]) {
                    currencySelector.setValue(currencyAndAmount[0]);
                }

                if(currencyAndAmount[1]) {
                    amountTextBox.setValue(currencyAndAmount[1]);
                }
            },

            focusSetter : function() {
                if (dojo.html.isVisible(input)) {
                    input.focus();
                }
            }};
    },
    
    _getDateRhs : function(/*function Object(onChange)*/ onChange) {
        var tdNode = globalObj.createStyledTd();
        var input = document.createElement("input");

        tdNode.appendChild(input);
        var datePicker = this._createValidateableDatePicker(tdNode, input, onChange);

        return {
            tdNode : tdNode,
            valueSetter : function(/*String*/ newValue) {
                if (newValue) {
                    datePicker.setValue(newValue);
                }
            },
            focusSetter : function() {
                if (dojo.html.isVisible(input)) {
                    input.focus();
                }
            }};
    },

    _createValidateableDatePicker : function(tdNode, input, onChange) {
        return globalObj.createValidationTextBox(input, {
        	type: "date",
        	onChange: onChange,
        	loneElementInRhs: true
        });
    },

    _getBooleanRhs : function(onChange) {//onChange is used only for suggest
        var tdNode = globalObj.createStyledTd();
        tdNode.innerHTML = "&nbsp";

        onChange();

        return {tdNode : tdNode,
            valueSetter : function() {
            },
            focusSetter : function() {
                // Nothing to do, since there is no RHS
            }};
    },

    _getWatchedListsRhs : function(/*function Object(onChange)*/ onChange,
                                                                 lhs, operator) {
        var tdNode = globalObj.createStyledTd();
        var watchListSelector = document.createElement("select");
        var urlForFetch = globalObj.watchListsFetchUrl; 
        tdNode.appendChild(watchListSelector);
   		
   		if(isDcapContext){	
        	urlForFetch = globalObj.dealerGroupsFetchUrl;
		}
		
		var comboBox = globalObj.createComboBox(watchListSelector, null, dojo.hitch(this, onChange));
		this._populateWatchListsForCategory(comboBox, globalObj.model.getBaseNameFor(lhs), urlForFetch);

        return {
            tdNode : tdNode,
            valueSetter : function(/*String*/ newValue) {  
            	comboBox.initialValue = newValue;
                comboBox.setValue(newValue);
                onChange(newValue);
            },
            focusSetter : function() {
            }};
    },

    _populateWatchListsForCategory : function(comboBox, category, urlForFetch) {

        var cachedWatchLists = this._categoryToNamedListsCache[category];
        if (cachedWatchLists) {
        	comboBox._reloadUsingItems(cachedWatchLists);
            comboBox.selectFirstValueIfAny();
        } else {
        
            twms.ajax.fireJsonRequest(urlForFetch, {
                categoryKind : category
            }, dojo.hitch(this, function(watchLists, evt) {
            	// Put the data in cache.
                    this._categoryToNamedListsCache[category] = watchLists;
                    comboBox._reloadUsingItems(watchLists);
                    if(!comboBox.getDisplayedValue()) {
                    	comboBox.selectFirstValueIfAny();
                    }else {
                    	var i=0;
                    	for(i=0;i<watchLists.length;i++) {
                    		if(watchLists[i][1]==comboBox.getDisplayedValue()) {
                    			comboBox.setDisplayedValue(watchLists[i][0]);
                    			break;
                    		}
                    	}
                    }
               }),
               function(error) {
                    alert("Error : " + error.message);
               });
        }
    },

    _getNumberIntervalRhs : function(/*function Object(onChange)*/ onChange) {
        var tdNode = globalObj.createStyledTd();
        var startingInput = document.createElement("input");
        var separator = document.createElement("span");
        dojo.addClass(separator, "intervalSeparator");
        separator.innerHTML = "and";
        var endingInput = document.createElement("input");

        tdNode.appendChild(startingInput);
        tdNode.appendChild(separator);
        tdNode.appendChild(endingInput);

        // We have to define these variables upfront, along with a dummy
        // implementation, since they have a circular dependency.
        var dummy = {getValue : function() {
        }};
        var startValidationTextbox = dummy;
        var endValidationTextbox = dummy;

        startValidationTextbox = globalObj.createValidationTextBox(startingInput, {
            type: "integer",
            onChange : function(value) {
                onChange([value, endValidationTextbox.getValue()]);
            }
        });

        endValidationTextbox = globalObj.createValidationTextBox(endingInput, {
            type: "integer",
            onChange : function(value) {
                onChange([startValidationTextbox.getValue(), value]);
            }
        });

        tdNode.validationTextboxIds = [startValidationTextbox.id,
                endValidationTextbox.id];

        return {
            tdNode : tdNode,
            valueSetter : function(/*2-el array*/ newStartingAndEndingValue) {
                if (newStartingAndEndingValue[0] &&
                    newStartingAndEndingValue[1]) {
                    startValidationTextbox.setValue(
                            newStartingAndEndingValue[0]);

                    endValidationTextbox.setValue(
                            newStartingAndEndingValue[1]);

                    onChange([newStartingAndEndingValue[0],
                            newStartingAndEndingValue[1]]);
                }
            },
            focusSetter : function() {
                if (dojo.html.isVisible(startingInput)) {
                    startingInput.focus();
                }
            }};
    },

    _getDateIntervalRhs : function(/*function Object(onChange)*/ onChange) {
        var tdNode = globalObj.createStyledTd();
        var startingInput = document.createElement("input");
        var separator = document.createElement("span");
        dojo.addClass(separator, "intervalSeparator");
        separator.innerHTML = "and";
        var endingInput = document.createElement("input");

        // We have to define these variables upfront, along with a dummy
        // implementation, since they have a circular dependency.
        var dummy = {getValue : function() {
        }};
        var startingDatePicker = dummy;
        var endingDatePicker = dummy;

        tdNode.appendChild(startingInput);
        startingDatePicker =
        this._createValidateableDatePicker(tdNode, startingInput,
                function(value) {
                    onChange([value, endingDatePicker.getDisplayedValue()])
                });

        tdNode.appendChild(separator);

        tdNode.appendChild(endingInput);
        endingDatePicker =
        this._createValidateableDatePicker(tdNode, endingInput,
                function(value) {
                    onChange([startingDatePicker.getDisplayedValue(), value])
                });

        return {
            tdNode : tdNode,
            valueSetter : function(/*2-el array*/ newStartingAndEndingValue) {
                if (newStartingAndEndingValue[0] &&
                    newStartingAndEndingValue[1]) {
                    startingDatePicker.setValue(newStartingAndEndingValue[0]);
                    endingDatePicker.setValue(newStartingAndEndingValue[1]);
                }
            },
            focusSetter : function() {
                if (dojo.html.isVisible(startingInput)) {
                    startingInput.focus();
                }
            }};
    },

    _getDescriptiveDuration : function(/* int */durationType) {
        return this.durations[durationType][0];
    },

    _getDescriptiveCalendarDuration : function(/* int */durationType) {
        return this.calendarDurations[durationType-1][0];
    },
   
    _getDatePlainDurationRhs : function(/*function Object(onChange)*/ onChange) {
        var selfObj = this;

        var tdNode = globalObj.createStyledTd();
        var input = document.createElement("input");
        tdNode.appendChild(input);

        var separator = document.createElement("span");
        dojo.addClass(separator, "durationSeparator");
        tdNode.appendChild(separator);

        var durationTypeInput = document.createElement("input");
        tdNode.appendChild(durationTypeInput);
        
        //dummy
        var comboBox = {getValue : function() {
        }};

       var validationTextbox = globalObj.createValidationTextBox(input, {
            type : "integer",
            onChange : function(value) {
                var durationType = comboBox.getValue();
                var isComplete = (durationType != null && value > 0);
                var rhsNameForSuggest =
                        isComplete ?
                        value + " " + selfObj._getDescriptiveDuration(durationType) : null;

                onChange([value, durationType], rhsNameForSuggest);
            },
            size:5
        });       
       
        comboBox = globalObj.createComboBox(durationTypeInput, selfObj.durations,
                dojo.hitch(this, function(newValue) {
                    var duration = validationTextbox.getValue();
                    var isComplete = (newValue != null && duration > 0);

                    var rhsNameForSuggest =
                            isComplete ?
                            duration + " " + selfObj._getDescriptiveDuration(newValue) : null;

                   onChange([duration, newValue], rhsNameForSuggest);
                }));

        dojo.addClass(comboBox.domNode, "datePlainDurationComboBoxSpan");
        comboBox.selectFirstValueIfAny();

        tdNode.validationTextboxIds = [validationTextbox.id];

        return {
            tdNode : tdNode,
            valueSetter : function(/*2-el array*/ newDurationAndType) {
                // We *have* to do an explicit null check, otherwise a value of
                // "0" would be construed as false.
                if (newDurationAndType[0] != null &&
                    newDurationAndType[1] != null) {
                    validationTextbox.setValue(newDurationAndType[0]);
                    comboBox.setValue(newDurationAndType[1]);
                    onChange([newDurationAndType[0], newDurationAndType[1]]);
                }
            },
            focusSetter : function() {
            }};
    },

    _getDateCalendarDurationRhs : function(/*function Object(onChange)*/ onChange) {
        var selfObj = this;

        var tdNode = globalObj.createStyledTd();
        var input = document.createElement("input");
        tdNode.appendChild(input);

        var separator = document.createElement("span");
        dojo.addClass(separator, "durationSeparator");
        tdNode.appendChild(separator);

        var durationTypeInput = document.createElement("input");
        tdNode.appendChild(durationTypeInput);

        //dummy
        var comboBox = {getValue : function() {
        }};

        var validationTextbox = globalObj.createValidationTextBox(input, {
            type : "integer",
            onChange : function(value) {
                var durationType = comboBox.getValue();
                var isComplete = (durationType != null && value > 0);
                var rhsNameForSuggest =
                        isComplete ?
                        value + " " + selfObj._getDescriptiveCalendarDuration(durationType) : null;

                onChange([value, durationType], rhsNameForSuggest);
            },
            size:5
        });

        comboBox = globalObj.createComboBox(durationTypeInput, selfObj.calendarDurations,
                dojo.hitch(this, function(newValue) {
                    var duration = validationTextbox.getValue();
                    var isComplete = (newValue != null && duration > 0);
                    var rhsNameForSuggest =
                            isComplete ?
                            duration + " " + selfObj._getDescriptiveCalendarDuration(newValue) : null;

                    onChange([duration, newValue], rhsNameForSuggest);
                }));

        comboBox.selectFirstValueIfAny();

        tdNode.validationTextboxIds = [validationTextbox.id];

        return {
            tdNode : tdNode,
            valueSetter : function(/*2-el aray*/ newDurationAndType) {
                // We *have* to do an explicit null check, otherwise a value of
                // "0" would be construed as false.
                if (newDurationAndType[0] != null &&
                    newDurationAndType[1] != null) {
                    validationTextbox.setValue(newDurationAndType[0]);
                    comboBox.setValue(newDurationAndType[1]);
                    onChange(newDurationAndType[0], newDurationAndType[1]);
                }
            },
            focusSetter : function() {
            }};
    },

      // Claim Duplicacy RHS'es
    _getClaimDuplicacyBlankRhs : function(dataType, onChange, lhs, operator) {
        var tdNode = globalObj.createStyledTd();
        var textNode = this._getBasePrefixNodeForClaimDuplicacyRhs();
        tdNode.appendChild(textNode);

        // Sets the suggested expression, nothing else.
        onChange(null, textNode.innerHTML);

        return {
            tdNode : tdNode,
            valueSetter : function() {
            },
            focusSetter : function() {
            }};
    },

    _getClaimDuplicacyComparisonRhs : function(dataType, onChange, lhs,
                                                 operator) {
        var textNode = this._getComparisonPrefixNodeForClaimDuplicacyRhs();
        var durationNode;

        var modifiedOnChange = function(value, rhsNameForSuggest) {
            if(!rhsNameForSuggest) {
                rhsNameForSuggest = value;
            }

            onChange(value, textNode.innerHTML + rhsNameForSuggest);
        }

        if("datePlainDuration" == dataType) {
            durationNode = this._getDatePlainDurationRhs(modifiedOnChange);
        } else {
            durationNode = this._getFreeTextRhs(dataType, modifiedOnChange, lhs,
                    operator);
        }

        dojo.dom.prependChild(textNode, durationNode.tdNode); // Prepend the prefix.

        return durationNode;
    },

    _getBasePrefixNodeForClaimDuplicacyRhs : function() {
        var textNode = this._getTextNodeForClaimDuplicacyRhs();
        textNode.innerHTML = this._getBasePrefixForClaimDuplicacyRhs();

        return textNode;
    },

    _getBasePrefixForClaimDuplicacyRhs : function() {
        return i18N.duplicateClaimCheck_rhsPrefix;
    },

    _getComparisonPrefixNodeForClaimDuplicacyRhs : function() {
        var textNode = this._getTextNodeForClaimDuplicacyRhs();
        textNode.innerHTML = this._getComparisonPrefixForClaimDuplicacyRhs();

        return textNode;
    },

    _getComparisonPrefixForClaimDuplicacyRhs : function() {
        return this._getBasePrefixForClaimDuplicacyRhs() + ", " +
              i18N.common_by + " ";
    },

    _getTextNodeForClaimDuplicacyRhs : function() {
        var textNode = document.createElement("span");
        dojo.addClass(textNode, "claimDuplicacyRhsPrefix");

        return textNode;
    },

  //Added For Club Car rules
  _getDateComparisonDurationRhs : function(onChange, lhs, operator) {    	
      var selfObj = this;

        var tdNode = globalObj.createStyledTd();
        var input = document.createElement("input");
        tdNode.appendChild(input);

        var separator = document.createElement("span");
        dojo.addClass(separator, "durationSeparator");
        tdNode.appendChild(separator);

        var durationTypeInput = document.createElement("input");
        tdNode.appendChild(durationTypeInput);

        //dummy
        var comboBox = {getValue : function() {
        }};

        var dateComparisonComboBox = {
            getValue : function() {},
            getDisplayedValue : function() {}
        };        
       var validationTextbox = globalObj.createValidationTextBox(input, {
            type : "integer",
            onChange : function(value) {
                var durationType = comboBox.getValue();
                var isComplete = (durationType != null);
                var rhsDate = dateComparisonComboBox.getValue();
                var rhsDateLabel = dateComparisonComboBox.getDisplayedValue();
                var rhsNameForSuggest =
                        isComplete ? rhsDateLabel + prefixNode.innerHTML + value + " " +
                                     selfObj._getDescriptiveDuration(durationType) : null;
                onChange([value, durationType, rhsDate], rhsNameForSuggest);
            },
            size:5
        });

        comboBox = globalObj.createComboBox(durationTypeInput, selfObj.durations,
                function(newValue) {
                    var duration = validationTextbox.getValue();
                    var isComplete = (newValue != null && duration > 0);
                    var rhsDate = dateComparisonComboBox.getValue();
                    var rhsDateLabel = dateComparisonComboBox.getDisplayedValue();

                    var rhsNameForSuggest =
                        isComplete ? rhsDateLabel + prefixNode.innerHTML + duration + " " +
                                     selfObj._getDescriptiveDuration(newValue) : null;
                                     
                   onChange([duration, newValue, rhsDate], rhsNameForSuggest);
                });

        dojo.addClass(comboBox.domNode, "datePlainDurationComboBoxSpan");
        comboBox.selectFirstValueIfAny();

        tdNode.validationTextboxIds = [validationTextbox.id];

        var prefixNode = document.createElement("span");
        prefixNode.innerHTML = this._getComparisonPrefixForDateComparisonRhs();

		var dateComparisonSelector = document.createElement("select");

        dojo.dom.prependChild(prefixNode, tdNode); // Prepend the prefix.
        dojo.dom.prependChild(dateComparisonSelector, tdNode); // Prepend the date.

        dateComparisonComboBox  = globalObj.createComboBox(dateComparisonSelector, selfObj.dateComparisonOptions,
            dojo.hitch(this, function(newValue) {
                var duration = validationTextbox.getValue();
                var durationType = comboBox.getValue();
                var isComplete = (newValue != null && duration > 0);
                var rhsDateLabel = dateComparisonComboBox.getDisplayedValue();

                var rhsNameForSuggest = isComplete ? rhsDateLabel + prefixNode.innerHTML + duration + " " +
                                                     selfObj._getDescriptiveDuration(durationType) : null;               
               onChange([duration, durationType , newValue], rhsNameForSuggest);
        }));        
        dateComparisonComboBox.selectFirstValueIfAny();
        return {
            tdNode : tdNode,
            valueSetter : function(/*2-el array*/ newCompareDateDurationAndType) {
                // We *have* to do an explicit null check, otherwise a value of
                // "0" would be construed as false.        		
                if (newCompareDateDurationAndType[0] != null &&
                    newCompareDateDurationAndType[1] != null &&
                    newCompareDateDurationAndType[2] != null) {
                    validationTextbox.setValue(newCompareDateDurationAndType[0]);
                    comboBox.setValue(newCompareDateDurationAndType[1]);
                    dateComparisonComboBox.setDisplayedValue(globalObj._getDisplayNameForDateComparisonComboBox(newCompareDateDurationAndType[2].baseName));
                    onChange([newCompareDateDurationAndType[0], newCompareDateDurationAndType[1],
                        newCompareDateDurationAndType[2]]);
                }
            },
            focusSetter : function() {
            }};
    },

  _getComparisonPrefixForDateComparisonRhs : function() {
        return  ", " + i18N.common_by + " ";
    }
});
