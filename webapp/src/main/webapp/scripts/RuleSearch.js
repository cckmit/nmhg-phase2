dojo.require("twms.widget.TitlePane");

/**
 * 
 */

dojo.declare("tavant.twms.RuleSearchWizard", null, {
    _resultPane : null,
    _checkboxes : null,
    _url : "",
    _idToFilter : "",
    _lastLoadedData : null,
    _params:null,
    _searchKeyName:null,
    constructor : function(/*ContentPane*/searchResult, /*url (String) to ping for results*/ baseUrl, /*search key name*/ searchKeyName,
                                          /* id to be filtered (prevent cyclic dep)*/_idToFilter,
                                          /* rule context*/ context) {
        this._idToFilter = _idToFilter;
        this._resultPane = searchResult;
        this._params={
            context:context
        };
        this._searchKeyName=searchKeyName;
        this._url = baseUrl;
    },

    startSearch : function(/*string*/ searchString) {
        if(searchString) {
            this._params[this._searchKeyName]=searchString;
        }
        var self = this;
        this._adjustDisplayForSearch(true);
        twms.ajax.fireJsonRequest(this._url, this._params, function(data) {
                self._lastLoadedData = eval(data);
                self._avoidCyclicDependency();
                self._adjustDisplayForSearch(false);
                self._displaySearchResults();
            },
            function(error) {
                self._adjustDisplayForSearch(false);
            });
    },

    _avoidCyclicDependency : function() {
        if (this._idToFilter !== null && this._idToFilter != "" &&
            this._lastLoadedData != null) {
            for (var i = 0; i < this._lastLoadedData.length; i++) {
                if (this._lastLoadedData[i].id == this._idToFilter) {
                    this._lastLoadedData.splice(i, 1);
                    break;
                }
            }
        }
    },

    _adjustDisplayForSearch : function(searchInProgress) {
        var mouseStyle = searchInProgress ? "wait" : "auto";

        this._resultPane.domNode.innerHTML = 
            "<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'>Loading...</div></div>";
        dojo.byId("ruleSearchStringInput").readOnly = searchInProgress ? "readonly" : "";
        dojo.style(dijit.byId("searchRulesDialog").domNode, "cursor",mouseStyle);
        dojo.byId("startSearch").disabled = searchInProgress;
        dojo.byId("addTerms").disabled = searchInProgress;
    },

    getSelectedRules : function() {
        var selectedRules = new Array();
        for (var i in this._checkboxes) {
            if (this._checkboxes[i].checked) {
                selectedRules.push(this._findRuleById(this._checkboxes[i].id));
            }
        }
        return selectedRules;
    },

/**
 * array elements are assumed to have structure like this...
 * {
 *  value: "rule name",
 *  description: "rule description",
 *  id: "rule id"
 * }
 */
    _displaySearchResults : function() {
        this._clearResults();
        this._resultPane.domNode.innerHTML = "";
        
        var dataCount = this._lastLoadedData.length;

        for (var i = 0; i < dataCount; i++) {
            var labelMarkup = "<input type=\"checkbox\" id=\"" + this._lastLoadedData[i].id + "\"/>&nbsp;" +
                              this._lastLoadedData[i].name;

            var result = new twms.widget.TitlePane();
            //result.setContent(this._lastLoadedData[i].description)
            result.setTitle(labelMarkup);

            if(i > 0) { // Collapse all but first.
                result.toggle();
            }

            this._resultPane.domNode.appendChild(result.domNode);

            var checkBox = dojo.byId(String(this._lastLoadedData[i].id));
            this._checkboxes.push(checkBox);

            delete result;
        }
    },

    _clearResults : function() {
        this._resultPane.destroyDescendants();
        this._resultPane.setContent("");
        this._checkboxes = new Array();
    },

    _findRuleById : function(/*String*/ id) {
        for (var i in this._lastLoadedData) {
            if (this._lastLoadedData[i].id == id) {
                return this._lastLoadedData[i];
            }
        }
    }
});

dojo.declare("tavant.twms.RuleRenderer", null, {

    _parentTable : null,
    _rulesAlreadyPresent : null,
    _hiddenInput : null,

    constructor : function(/*div to be populated*/ parentTable, /*array of already present rules*/ preConfiguredRules,
        /*input domNode which has to be updated about rules selected*/ hiddenInput) {
        this._rulesAlreadyPresent = new Array();
        this._parentTable = parentTable;
        this._hiddenInput = hiddenInput;
        this.addRows(preConfiguredRules);
    },

/**
 * array elements are assumed to have structure like this...
 * {
 *  value: "rule name",
 *  description: "rule description",
 *  id: "rule id"
 * }
 */
    addRows : function(/*array of rules*/ rules) {
        for (var i in rules) {
            if (!this._isRulePresent(rules[i])) {
                this._createNewRow(rules[i]);
                this._rulesAlreadyPresent.push(rules[i]);
            }
        }
        this._updateHiddenInput();
    },

    _createNewRow : function(/*rule object*/ ruleNode) {
        var tr = document.createElement("tr");
        var td = document.createElement("td");
        td.innerHTML = "<span style='float: left'>" + ruleNode.name +
                       "</span><img src='image/remove.gif' style='float:right' id='delete_" + ruleNode.id +
                       "' associatedRuleId='" + ruleNode.id + "'/>";
        tr.appendChild(td);
        this._parentTable.appendChild(tr);
        var deleteButton = dojo.byId("delete_" + ruleNode.id);
        dojo.connect(deleteButton, "onclick", this, "dropRule");
    },

/**
 * Returns true or false depending on weather the rule is already there or not!!!
 */
    _isRulePresent : function(/*rule object*/ rule) {
        for (var i in this._rulesAlreadyPresent) {
            if (rule.id == this._rulesAlreadyPresent[i].id) {
                return true;
            }
        }
        return false;
    },

    dropRule : function(/*event will be a click on the image*/event) {
        var ruleId = event.target.getAttribute("associatedRuleId");
        this._deleteRuleHavingId(ruleId);
        dojo.dom.destroyNode(getExpectedParent(event.target, "tr"));
        delete ruleId;
        this._updateHiddenInput();
    },

    _deleteRuleHavingId : function(/*String*/ ruleId) {
        for (var i in this._rulesAlreadyPresent) {
            if (this._rulesAlreadyPresent[i].id == ruleId) {
                this._rulesAlreadyPresent.splice(i, 1);
            }
        }
    },

    _updateHiddenInput : function() {
        var ruleIdsSelected = new Array();
        for (var i in this._rulesAlreadyPresent) {
            ruleIdsSelected.push(this._rulesAlreadyPresent[i].id);
        }
        this._hiddenInput.value = ruleIdsSelected;
        delete ruleIdsSelected;
    }
});