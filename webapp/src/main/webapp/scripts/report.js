/*

   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

*/

dojo.require("twms.widget.TitlePane");
function invokeReportPopup(action,
                           dealers,
                           suppliers,
                           dealerGroups,
                           startDate,
                           endDate,
                           orderBy,
                           order,taskName,
                           showReport){
	var win = "";
	if(!showReport){
		return;
	}
	var url=action+'?reportSearchCriteria.startDate='+startDate+'&reportSearchCriteria.endDate='+endDate+
	    '&reportSearchCriteria.orderBy='+orderBy+'&reportSearchCriteria.order='+order+'&taskName='+taskName+
        '&reportSearchCriteria.dealers='+dealers+'&reportSearchCriteria.dealerGroups='+dealerGroups+'&reportSearchCriteria.suppliers='+suppliers;
	win = window.open(url,'win','toolbar=yes,location=no,directories=no,status=no,menubar=yes,scrollbars=yes,resizable=yes,copyhistory=no,Width=680,height=400,top=115,left=230');
	return false;
}

function deleteData(){
	if(document.getElementById("reportSearchCriteria.dealers")!=null){
	document.getElementById("reportSearchCriteria.dealers").value="";
	}
	if(document.getElementById("reportSearchCriteria.suppliers")!=null){
	document.getElementById("reportSearchCriteria.suppliers").value="";
	}
	if(document.getElementById("reportSearchCriteria.dealerGroups")!=null){
	document.getElementById("reportSearchCriteria.dealerGroups").value="";
	}
	if(document.getElementById("existingDealers")!=null){
	document.getElementById("existingDealers").value="";
	}
	if(document.getElementById("existingSuppliers")!=null){
	document.getElementById("existingSuppliers").value="";
	}
	if(document.getElementById("existingDealerGroups")!=null){
	document.getElementById("existingDealerGroups").value="";
	}
	if(document.getElementById("startDate")!=null){
	document.getElementById("startDate").value="";
	document.getElementById("endDate").value="";
	}
}

/*search*/
dojo.declare("tavant.twms.DealerSearchWizard", null, {

    _resultPane : null,
    _checkboxes : null,
    _url : "",
    _idToFilter : "",
    searchKeyName : "",
    _lastLoadedData : null,
    _param : null,
    constructor : function(/*ContentPane*/searchResult, /*url (String) to ping for results*/ baseUrl, /*search key name*/ searchKeyName,
                                          /* id to be filtered (prevent cyclic dep)*/_idToFilter,
                                          /* rule context*/ context) {
        this._idToFilter = _idToFilter;
        this._resultPane = searchResult;
        this._url = baseUrl;
        this.searchKeyName = searchKeyName;
        this._checkboxes = new Array();
        this._params={
            context:context
        }
    },

    startSearch : function(/*string*/ searchString) {
        this._params[this.searchKeyName]=searchString;
        console.dir(this._params);        
        var self = this;

        twms.ajax.fireJsonRequest(this._url,this._params,function(data) {
                self._lastLoadedData = eval(data);
                self._avoidCyclicDependency();
                self._displaySearchResults();
            },function(error) {
                alert("Error : " + error.message);
            }
        );

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

    getSelectedDealers : function() {
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
        this._checkboxes = new Array();
        this._clearResults();
         if(this._lastLoadedData[0]==null){
            var descriptionDiv = document.createElement("div");
           // var labelMarkup = "<ul><li><span class=\"errorMessage\">"+ this._lastLoadedData[0].name+"</span></li></ul>";
            var labelMarkup ="<ul><li><span class=\"errorMessage\">"+i18N.search_results+"</span></li></ul>";
            var result = new twms.widget.TitlePane(
            {open:false,
                labelNodeClass: "collapsableLabel",
                containerNodeClass: "collapsableContent"
            });
            result.setTitle(labelMarkup);            
            this._resultPane.domNode.appendChild(result.domNode)
            delete result,descriptionDiv;
        
        }else{
        if( this._lastLoadedData[0].id >=1){
        for (var i in this._lastLoadedData) {
            var descriptionDiv = document.createElement("div");
            //descriptionDiv.innerHTML = this._lastLoadedData[i].id;
            var labelMarkup = "<input type=\"checkbox\" id=\"" + this._lastLoadedData[i].id + "\"/>&nbsp;" + this._lastLoadedData[i].name;
            var result = new twms.widget.TitlePane(
            {open:false,
                labelNodeClass: "collapsableLabel",
                containerNodeClass: "collapsableContent"
            });
            //result.setContent(this._lastLoadedData[i].id)
            result.setTitle(labelMarkup);           
            this._resultPane.domNode.appendChild(result.domNode)
            var checkbox = dojo.byId("" + this._lastLoadedData[i].id);
            //this "" is required because id is actually a number.
            dojo.connect(checkbox, "onclick", function(event) {
                event.stopPropagation();
            });
            this._checkboxes.push(checkbox);
            delete result,descriptionDiv;
        }}
		}
    },

    _clearResults : function() {
        //HACK: this 'this._resultPane.destroyChildren();' should have worked... but it didn't...
        //it checks if widget(child) is an instance of Widget, don't know why it failed!!! will figure out later!!!
        //i know for sure that it is a widget... so removing it without the check.
        this._resultPane.domNode.innerHTML= "<SPAN></SPAN>";
       /* while (this._resultPane.children.length > 0) {
            var widget = this._resultPane.children[0];
            this._resultPane.removeChild(widget);
            widget.destroy();
        }
        this._checkboxes = new Array();*/
    },

    _findRuleById : function(/*String*/ id) {
        for (var i in this._lastLoadedData) {
            if (this._lastLoadedData[i].id == id) {
                return this._lastLoadedData[i];
            }
        }
    }
});

dojo.declare("tavant.twms.DealerRenderer", null, {

    _parentTable : null,
    _rulesAlreadyPresent : null,
    _hiddenInput : null,
    _hiddenInput_2:null,
    _realDom:null,
    _dummyDom:null,
    _flag:false,
    

    constructor : function(/*div to be populated*/ parentTable, /*array of already present rules*/ preConfiguredRules, /*input domNode which has to be updated about rules selected*/ hiddenInput,hiddenInput_2,realDom,dummyDom,
    	validationNodeId) {
        this._rulesAlreadyPresent = new Array();
        this._parentTable = parentTable;
        this._hiddenInput = hiddenInput;
        this._hiddenInput_2=hiddenInput_2;
        this._realDom=realDom;
        this._dummyDom=dummyDom;
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
    	
        var tr1 = document.createElement("tr");
        tr1.id=ruleNode.id;
        var td1 = document.createElement("td");
        td1.innerHTML = "<td width=\"5%\">&nbsp;</td>";
        var td2 = document.createElement("td");
        td2.innerHTML = "<td width=\"90%\">";
        td2.innerHTML = "<span style=\"float: left\">" + ruleNode.name + "</span></td>";
        var td3 = document.createElement("td");
        td3.innerHTML ="<td width=\"5%\"><img src=\"image/remove.gif\" style=\"float:left\"  alt=\"Remove Row\" width=\"11\" height=\"11\" id=\"delete_" + ruleNode.id + "\" associatedRuleId=\"" + ruleNode.id + "\"/></td>";
        tr1.appendChild(td1);
        tr1.appendChild(td2);
        tr1.appendChild(td3);
        this._parentTable.appendChild(tr1);
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
        this._hiddenInput_2.value="";
        for(var i in ruleIdsSelected){
        this._hiddenInput_2.value=this._hiddenInput_2.value+"{'name':'"+this._rulesAlreadyPresent[i].name+"','id':'"+this._rulesAlreadyPresent[i].id+"'},";
        }
        if(this._hiddenInput.value!=null && this._hiddenInput.value=="") {
        	this.hideIt();
        }
		if(this._hiddenInput.value!=null && this._hiddenInput.value!="") {
        	this.showIt();
        }
        delete ruleIdsSelected;
    },
   	showIt :function() {
		if(!this._flag) return;
		if(this._dummyDom!=null){
		dojo.dom.replaceNode(this._dummyDom, this._realDom);
		}
		this._flag = false;
	},
	
	hideIt :function() {
		if(this._flag) return;
		if(this._dummyDom!=null){
		dojo.dom.replaceNode(this._realDom,this._dummyDom);
		}
		this._flag = true;
	},
	dropDealers: function() {
		this._rulesAlreadyPresent = [];
		var tbody =this._parentTable;
		var trs = tbody.getElementsByTagName("tr");

           while (trs.length > 1) {
               dojo.dom.destroyNode(trs[1]);
           }
		
    	this._updateHiddenInput();
    	
		  	
    }
});

	function ltrim(argvalue) {
	  while (1) {
	    if (argvalue.substring(0, 1) != " ")
	      break;
	    argvalue = argvalue.substring(1, argvalue.length);
	  }
	  return argvalue;
	}


	function removeDealers() {
		dealerRenderer.dropDealers();
		}
	function removeDealerGroups() {
		dealerGroupRenderer.dropDealers();
		}
	function showSearchDealersDialog() {
		 if(GLOBAL_VARS.dealerCheckBox.checked){
		    searchDealersDialog.show();
		    }
		}
	function showSearchDealerGroupsDialog() {
		 if(GLOBAL_VARS.dealerGroupCheckBox.checked){
	    	searchDealerGroupsDialog.show();
		    }
		}

    function requestDealers() {
		if(ltrim(dealerName.value).length<1){
            errorInDealerSearching();
		}else{
		dealerSearchWizard.startSearch(dealerName.value);}
		}

    function requestSuppliers() {
        if(ltrim(supplierName.value).length<1){
	         errorInSearching();
		}else{
		dealerSearchWizard.startSearch(supplierName.value);
		}
	}

    function errorInSearching(){
         this.searchResultsPaneDealer.domNode.innerHTML= "<SPAN></SPAN>";
          var labelMarkup ="<ul><li><span class=\"errorMessage\">"+i18N.supplier_Name+"</span></li></ul>";
          var result = new twms.widget.TitlePane(
            {open:false,labelNodeClass: "collapsableLabel",containerNodeClass: "collapsableContent"
            });
            result.setTitle(labelMarkup);
            this.searchResultsPaneDealer.domNode.appendChild(result.domNode);
            delete result;
    }
    
    function errorInDealerSearching(){
         this.searchResultsPaneDealer.domNode.innerHTML= "<SPAN></SPAN>";
          var labelMarkup ="<ul><li><span class=\"errorMessage\">"+"Som test value"+"</span></li></ul>";
          var result = new twms.widget.TitlePane(
            {open:false,labelNodeClass: "collapsableLabel",containerNodeClass: "collapsableContent"
            });
            result.setTitle(labelMarkup);
            this.searchResultsPaneDealer.domNode.appendChild(result.domNode);
            delete result;
    }
    
    function errorInDealerGroupSearching(){
         this.searchResultsPaneDealerGroups.domNode.innerHTML= "<SPAN></SPAN>";
          var labelMarkup ="<ul><li><span class=\"errorMessage\">"+i18N.dealer_GroupName+"</span></li></ul>";
          var result = new twms.widget.TitlePane(
            {open:false,labelNodeClass: "collapsableLabel",containerNodeClass: "collapsableContent"
            });
            result.setTitle(labelMarkup);
            this.searchResultsPaneDealerGroups.domNode.appendChild(result.domNode);
            delete result;
    }

    function requestDealerGroups() {
		if(ltrim(dealerGroupName.value).length<1)
		{  	        
		errorInDealerGroupSearching();		 
		}else{
		dealerGroupSearchWizard.startSearch(dealerGroupName.value);}
		}
	function addDealersToForm() {
		dealerRenderer.addRows(dealerSearchWizard.getSelectedDealers());
		closeDialogDealer();
		}
	function addDealerGroupsToForm() {
		dealerGroupRenderer.addRows(dealerGroupSearchWizard.getSelectedDealers());
		closeDialogDealerGroup();
		}
	function closeDialogDealer() {
		searchDealersDialog.hide();
		}
	function closeDialogDealerGroup() {
		searchDealerGroupsDialog.hide();
		}	
	var	dealersTable =null;
	var	dealerGroupsTable =null;
	var	dealerName =null;
	var	supplierName =null;
	var	dealerGroupName =null;
	var	dealersTableDiv1 =null;
	var	dealerGroupsTableDiv1 =null;
	var	dealersTableDiv2 =null;
	var	dealerGroupsTableDiv2 =null;
	var	deleteAllDealers =null;
	var	deleteAllDealerGroups =null;
	var	dealersSelectedHiddenInput =null;
	var	dealerGroupsSelectedHiddenInput =null;
	var	existingDealersHiddenInput =null;
	var	existingDealerGroupsHiddenInput =null;
	var	dealerSearchWizard =null;
	var	dealerGroupSearchWizard =null;
	var	selectedDealers =null;
	var	selectedDealerGroups =null;
	var	dealerRenderer =null;
	var	dealerGroupRenderer =null;
