dojo.require("dijit.layout.LayoutContainer");
dojo.require("dijit.layout.ContentPane");
dojo.require("twms.widget.TitlePane");
dojo.require("dijit.form.ComboBox");
dojo.require("twms.widget.Dialog");

var dlg;
var allBoxes;

dojo.addOnLoad(function() {
	dlg = dijit.byId("addPoliciesDialog");
	allBoxes = dojo.byId('allBoxes');
	
	if(dlg) { // for stock items no need to show the warranty coverage section
		dojo.connect(allBoxes, "onclick", function() {
			toggleOptions('selectedDefinitionIds', allBoxes.checked);
		});
				
				
		dojo.connect(dojo.byId('closeDialog'), "onclick", function() {
			dlg.hide();
		});
		
		var addPolImage = dojo.byId('addPoliciesImage');
		if(addPolImage) { //for non admin users this button wudnt be there., 
			dojo.connect(addPolImage, "onclick", function() {
				dlg.show();
			});
		}
	}
	
});

function toggleOptions(objName, toggleStatus) {
	var elts = document.getElementsByName(objName);
	for (var i = 0; i < elts.length; i++) {
		elts[i].checked = toggleStatus;
	}
}

function closeTheTab() {
	var thisTabId = getTabDetailsForIframe().tabId;
	var thisTab = getTabHavingId(thisTabId);
	closeTab(thisTab);		
}

/*
 * This class just takes the tbody and a json object and renders a row using the 
 * template. Its soul purpose is to render.
 */
dojo.declare("tavant.twms.PolicyRowRenderer", null, {
	
	tBody : null,//tbody's domNode to which all the selected items are to be appended
  template : "<tr/>",
  script : "",
  index : 0,
  mode : "",

  constructor : function(/*tBody domNode*/ tBody, /*String (template text(tr))*/ template,
  											 index, /*String (template javascript)*/ script,
  											 /* read or edit */ mode) {
      this.tBody = tBody;
      this.template = unescapeThisHtml(template);
      this.script = script;
      this.index = index;
      this.mode = mode;
  },
  
  
	renderRow : function(jsonObject) {		
		var substitutionMap = {
       "index" : this.index,
       "id" : jsonObject.id ? jsonObject.id : "",
       "policy":jsonObject,
       "definitionId" : jsonObject.definitionId,
       "policyCode" : jsonObject.policyCode,
       "policyName" : jsonObject.policyName,
       "type" : jsonObject.type,
       "startDate" : jsonObject.startDate ? jsonObject.startDate : "",
       "endDate" : jsonObject.endDate ? jsonObject.endDate :"",
       "monthsCovered" : jsonObject.monthsCovered ? jsonObject.monthsCovered : "",
       "hoursCovered" : jsonObject.hoursCovered ? jsonObject.hoursCovered : "",
       "policyStatus" : jsonObject.policyStatus ? jsonObject.policyStatus : "",
       "comments" : jsonObject.comments ? jsonObject.comments : "",
       "auditSize" : jsonObject.auditSize ? jsonObject.auditSize : "" ,
       "policyStatusForDisplay" : jsonObject.policyStatusForDisplay ? jsonObject.policyStatusForDisplay : "" ,
	   "purchaseOrdNumber" : jsonObject.purchaseOrdNumber ? jsonObject.purchaseOrdNumber : "",
	   "purchaseDate" : jsonObject.purchaseDate ? jsonObject.purchaseDate : "",
       "currentPolicyStatus" : jsonObject.currentPolicyStatus ? jsonObject.currentPolicyStatus : "",
       "attachments" : jsonObject.attachments ? jsonObject.attachments : "" 
       };
    var rowMarkup = dojo.string.substitute(this.template, substitutionMap);
   	var rowScript = dojo.string.substitute(this.script, substitutionMap);
   	
   	var row = dojo.html.createNodesFromText(rowMarkup, true);
   	row.nodeObject = jsonObject;
   	row.nodeObject.listed = true;
   	row.nodeObject.selected = false;

   	this.tBody.appendChild(row);
   	dojo.parser.parse(row);
   	
   	eval(rowScript);
   	
   	this.renderAttachments(this.mode, jsonObject);
   	
   	this.index++;
  	return row;
	},
	
	/*_getPolicyAuditCell : function(id) {		
	   	var td = document.createElement("td");
	   	var ul = document.createElement("ul");
	   		   	
	   
	   		var li = document.createElement("li");
	   		var linkContent = [];
	   		linkContent.push("<a href='view_policy_audits_detail.action?policyId=");
	   		linkContent.push(id);
	   		linkContent.push("'>");
	   		linkContent.push("Policy Audits");
	   		linkContent.push("</a>");
	   		li.innerHTML = linkContent;
	   		ul.appendChild(li);
	   	
	   	
	   	td.appendChild(ul);
	   	
	   	return td;
	   	
	},*/
	
	renderAttachments : function(mode, jsonObject) {
		var fileUploaderId = mode+"_attachments_"+this.index;
		if(!dijit.byId(fileUploaderId)) 
			return;
	   	if(jsonObject.type=='Extended' &&jsonObject.attachments!=undefined & jsonObject.attachments!='"[]"') {
   			dijit.byId(fileUploaderId).uploadedFilesInfo=jsonObject.attachments;
   			dijit.byId(fileUploaderId)._processAlreadyUploadedFiles();
	   	}else {
	   		dijit.byId(fileUploaderId).destroy();
	   	}
	},
	
	deleteRow : function(row) {
		this.tBody.removeChild(row);
	},
	
	setTemplate : function(template) {
		this.template = template;
	}

});

/**
 * This class controlls how all the rendering and all will be done.
 * Basically 3 important methods are _renderIncludedRows, _renderRegisteredRows
 * and renderAvailableRows. The other methods use these methods indirectly.
 */
dojo.declare("tavant.twms.PolicyRowManager", null, {
	tBody : null,//tbody's domNode to which all the selected items are to be appended
	dlgBody : null, 
  editableTemplate : "<tr/>",
  readonlyTemplate : "<tr/>",
  additionalTemplate : "<tr/>",
  editableTemplateForGoodwill : "<tr/>",
  readonlyTemplateForAdded : "<tr/>",
  script : "",
  renderer : null,
  renderedRows : null,
  registeredRows : null,
  includedPoliciesArray : null,
  availablePoliciesArray : null,
  registeredPoliciesArray : null,
  originalPoliciesSize : 0,
  readFlag : true,

  constructor : function(/*tBody domNode*/ tBody, dlgBody, /*String (template text(tr))*/ editableTemplate,
  											 /*String (template text(tr))*/ readonlyTemplate, additionalTemplate,
  											 editableTemplateForGoodwill, readonlyTemplateForAdded,
  											 /*array of available policies*/ availablePoliciesArray,
  											 /*array of included policies*/ includedPoliciesArray,
  											 /*array of registered policies*/ registeredPoliciesArray,
  											 /*String (template javascript)*/ script, originalPoliciesSize) {
      this.tBody = tBody;
      this.dlgBody = dlgBody;
      this.readonlyTemplate = unescapeThisHtml(readonlyTemplate);
      this.editableTemplate = unescapeThisHtml(editableTemplate);
      this.additionalTemplate = unescapeThisHtml(additionalTemplate);
      this.readonlyTemplateForAdded = unescapeThisHtml(readonlyTemplateForAdded);
      this.editableTemplateForGoodwill = unescapeThisHtml(editableTemplateForGoodwill);
      this.script = script;
      this.includedPoliciesArray = includedPoliciesArray;
      this.availablePoliciesArray = availablePoliciesArray;
      this.registeredPoliciesArray = registeredPoliciesArray;
      this.renderedRows = new Array();
      this.registeredRows = new Array();
      this.originalPoliciesSize = originalPoliciesSize;     
  },
  
  _renderIncludedRows : function(template, template2, script, type) {
  	this.renderedRows = new Array();
  	this.renderer = new tavant.twms.PolicyRowRenderer(this.tBody, template, this.originalPoliciesSize, script, type);
  	for(var i in this.includedPoliciesArray) {
  		if(type == 'read') {
  			this.renderer.setTemplate(template2);
  		} else if(this.includedPoliciesArray[i].type=='POLICY') {
  			this.renderer.setTemplate(template2);
  		} else {
  			this.renderer.setTemplate(template);
  		}
  		var row = this.renderer.renderRow(this.includedPoliciesArray[i]);
  		this.renderedRows.push(row);
  	}
	},
	
	_renderRegisteredRows : function(template, template2, script, type) {
		this.registeredRows = new Array();
		this.renderer = new tavant.twms.PolicyRowRenderer(this.tBody, template, 0, script, type);
	  	for(var i in this.registeredPoliciesArray) {
	  		if(type == 'edit' && this.registeredPoliciesArray[i].type=='POLICY') {
	  			this.renderer.setTemplate(template2);
	  		} else {
	  			this.renderer.setTemplate(template);
	  		}
	  		var row = this.renderer.renderRow(this.registeredPoliciesArray[i]);
	  		this.registeredRows.push(row);
	  	}
	},
	
	edit : function(clearAll) {
		this._clearTbody(clearAll);
		if(clearAll) this._renderRegisteredRows(this.editableTemplate, this.editableTemplateForGoodwill, this.script, "edit");
		this._renderIncludedRows(this.editableTemplate, this.editableTemplateForGoodwill, this.script, "edit");
		this.readFlag= false;
	},
	
	read : function(clearAll) {
		this._clearTbody(clearAll);
		if(clearAll) this._renderRegisteredRows(this.readonlyTemplate, this.readonlyTemplateForAdded, "", "read");
		this._renderIncludedRows(this.readonlyTemplate, this.readonlyTemplateForAdded, "", "read");
		this.readFlag = true;
	},
	
	_clearTbody : function(clearAll) {
		for(var i in this.renderedRows) {
			this.renderer.deleteRow(this.renderedRows[i]);
		}
		this.renderedRows = new Array();
		if(clearAll) { 
			this._clearRegisteredPolicyTbody();
		}
	},
	
	_clearRegisteredPolicyTbody : function() {
		this._destroyAttachments();
		for(var i in this.registeredRows) {
			this.renderer.deleteRow(this.registeredRows[i]);
		}
		this.registeredRows = new Array();
	},
	
	_destroyAttachments: function() {
		var prefix = "edit";
		if(this.readFlag) {
			prefix = "read";
		}
		for ( var i in this.registeredRows) {
			dojo.query('[widgetid^=\"'+prefix+'_attachments_' + i + '\"]',
					dojo.byId(prefix + "_attachments_container_" + i)).forEach( function(w) {
						dijit.byId(w.id).destroy();
			});
		}
	},
	
	renderAvailableRows : function() {
		var aRenderer = new tavant.twms.PolicyRowRenderer(this.dlgBody, this.additionalTemplate, 0, "")
		for(var i in this.availablePoliciesArray) {
			aRenderer.renderRow(this.availablePoliciesArray[i]);
			var check = dojo.byId('check_'+i);
			if(this._contains(this.includedPoliciesArray, this.availablePoliciesArray[i])) {
				//check.checked=true;
			} else {
				//check.checked=false;
			}
		}
	},
	
	/*
	 * Pushes the selected policies from dialog to the main page.
	 */
	pushResults : function() {
		this.includedPoliciesArray = new Array();
		var elts = dojo.query("input.selectedDefinitionIds", this.dlgBody);	
		
		for(var i in elts) {
		  if(elts[i].checked) {
				this.includedPoliciesArray.push(this.availablePoliciesArray[i]);				
			}
		}
		if(this.readFlag) {
			this.read(false);
		} else {
			this.edit(false);
		}
	},
	
	/*
	 * Given a JSON array and a JSON object this api tells if the object is there 
	 * in the array or not.
	 */
	_contains : function(jsonArray, jsonObject) {
		for(var i in jsonArray) {
			if(jsonArray[i].definitionId == jsonObject.definitionId) return true;
		}
		return false;
	}
});

function toggleEndDateSetStatus(index,auditSize)
{   	
	var currentAuditStatus = dojo.byId('registeredPolicies['+index+'].policyAudits['+auditSize+'].status');
	var auditStatusDisplayed = dojo.byId('registeredPolicies['+index+'].policyStatusInput').value;
	if(currentAuditStatus)
	{
		if(auditStatusDisplayed == dojo.byId('activationStatus').value)
		{			
			if(currentAuditStatus.checked)
			{		
				dojo.byId('edit_end_date_'+index).setAttribute('readonly','readonly');		 
				currentAuditStatus.value = dojo.byId('terminationStatus').value;		
			}
			else
			{		
				dojo.byId('edit_end_date_'+index).removeAttribute('readonly');
				currentAuditStatus.value = '';
			}
		}
		else if (auditStatusDisplayed == dojo.byId('terminationStatus').value)
		{						
			if(currentAuditStatus.checked)
			{			 
				dojo.byId('edit_end_date_'+index).removeAttribute('readonly');
				currentAuditStatus.value = dojo.byId('activationStatus').value;		
			}
			else
			{	
				dojo.byId('edit_end_date_'+index).setAttribute('readonly','readonly');
				currentAuditStatus.value = '';
			}		
		}
	}					    	
}

function disableCheckBox(index,auditSize)
{
	var auditStatusDisplayed = dojo.byId('registeredPolicies['+index+'].policyStatusInput').value;
	var terminationChkBox = dojo.byId('registeredPolicies['+index+'].policyAudits['+auditSize+'].status');
	var currentAuditStatus = dojo.byId('registeredPolicies['+index+'].policyAudits['+auditSize+'].status');	
	
	if(terminationChkBox)
	{		
		if(auditStatusDisplayed == dojo.byId('terminationStatus').value)
		{
			terminationChkBox.disabled = false;
			dojo.byId('action_'+index).innerHTML = unescapeThisHtml(activationCaption);
			if(currentAuditStatus.value == dojo.byId('activationStatus').value)
			{
				terminationChkBox.checked = "true";
			}
		}
		else if(auditStatusDisplayed == dojo.byId('activationStatus').value)
		{
			terminationChkBox.disabled = false;
			dojo.byId('action_'+index).innerHTML = unescapeThisHtml(terminationCaption);	
			if(currentAuditStatus.value == dojo.byId('terminationStatus').value)
			{
				terminationChkBox.checked = "true";
			}
		}
		else
		{
			terminationChkBox.disabled = "true";
		}
	}
}

function getPolicyAudits(id) {
        dijit.byId("policy_audits").show();
		var parentDiv = dijit.byId("policy_audits_div");
        parentDiv.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
        twms.ajax.fireHtmlRequest("view_policy_audits_detail.action",{policyId:id
        },function(data) {
			parentDiv.setContent(data);
			delete data;
		}
	);
	delete url;
}
	
function getPolicyDetails(id) {	
		dijit.byId("policy_details").show();
		var parentDiv = dijit.byId("policy_details_div");
        parentDiv.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
        twms.ajax.fireHtmlRequest("policy_history.action",{policyId:id
        },function(data) {
			parentDiv.setContent(data);
			delete data;	
		}
	);		
	delete url;
}
