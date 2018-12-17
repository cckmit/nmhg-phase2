/**
 * Same as RuleSearch.js, except that it declares one more
 * RuleRenderer, which dumps ids for every row chosen.
 * Useful when you are doing direct object manipulation 
 *
 * The _hiddenInput variable is just used as a "name" attribute
 */
dojo.declare("tavant.twms.MultipleHiddenValuesRuleRenderer", tavant.twms.RuleRenderer, {
	
	_rowCounter : 0,
	
	_createNewRow : function(/*rule object*/ ruleNode) {
        var tr = document.createElement("tr");
        var td = document.createElement("td");
        //Now add the hidden element tag
        var imgDom = dojo.html.createNodesFromText("<img src=\"image/remove.gif\" style=\"float:right\" id=\"delete_" + ruleNode.id + "\" associatedRuleId=\"" + ruleNode.id + "\"/>", true);
        var spanDom = dojo.html.createNodesFromText("<span style=\"float: left\">" + ruleNode.name + "</span>", true);
        var hiddenInputName = this._hiddenInput.id.replace("?", this._rowCounter++);
        var hiddenFieldHTML = "<input type=\"hidden\" name=\"" + hiddenInputName + "\" value=\"" + ruleNode.id + "\"/>";
        var hiddenFieldDom = dojo.html.createNodesFromText(hiddenFieldHTML, true);
        td.appendChild(spanDom);
        td.appendChild(imgDom);
		this._hiddenInput.appendChild(hiddenFieldDom);
        tr.appendChild(td);
        tr.hiddenFieldDOM = hiddenFieldDom;
        this._parentTable.appendChild(tr);
        var deleteButton = dojo.byId("delete_" + ruleNode.id);
        dojo.connect(deleteButton, "onclick", this, "dropRule");
    },
    
    dropRule : function(/*event will be a click on the image*/event) {
        var ruleId = event.target.getAttribute("associatedRuleId");
        this._deleteRuleHavingId(ruleId);
        //TODO: please use the utility.js's method here insteed of doing it again.
        var trNode = getExpectedParent(event.target, "tr");
        var associatedHiddenDOM = trNode.hiddenFieldDOM;
        associatedHiddenDOM.name = "__remove."+associatedHiddenDOM.name;
        dojo.dom.destroyNode(getExpectedParent(event.target, "tr"));
        delete ruleId;
        this._updateHiddenInput();
    },
    
    _updateHiddenInput : function() { }
});