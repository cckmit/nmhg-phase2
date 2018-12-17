function showPrices(listType, value)
{
	if(listType == 'Parts')
	{
		document.baseForm.criteriaIndex.value = value;
		document.baseForm.action = "get_item_prices_for_criteria.action"
		document.baseForm.submit();
	} 
	else if(listType == 'Dealer')
	{
		document.baseForm.criteriaIndex.value = value;
		document.baseForm.action = "get_labor_and_travel_rate_criteria.action"
		document.baseForm.submit();
	} 
	else if(listType == 'Modifier')
	{
		document.baseForm.criteriaIndex.value = value;
		document.baseForm.action = "get_values_for_criteria.action"
		document.baseForm.submit();
	} 
	else if(listType == 'PartReturn') 
	{
		document.baseForm.criteriaIndex.value = value;
		document.baseForm.action = "show_part_return_definitions.action"
		document.baseForm.submit();
	}
}

function moveUp()
{
	var selectedIndex = document.baseForm.updownlist.selectedIndex;
	if (selectedIndex > 0) 
	{
		var previous = document.baseForm.updownlist.options[selectedIndex - 1];
		var current = document.baseForm.updownlist.options[selectedIndex];
		document.baseForm.updownlist.options[selectedIndex - 1] = new Option("text1", "text1", false);
		document.baseForm.updownlist.options[selectedIndex] = previous;
		document.baseForm.updownlist.options[selectedIndex - 1] = current;
	} 
}

function moveDown() 
{
	var selectedIndex = document.baseForm.updownlist.selectedIndex;
	var size = document.baseForm.updownlist.size;
	if ((selectedIndex > -1) && (selectedIndex < (size-1)))
	{
		var next = document.baseForm.updownlist.options[selectedIndex + 1];
		var current = document.baseForm.updownlist.options[selectedIndex];
		document.baseForm.updownlist.options[selectedIndex + 1] = new Option("text1", "text1", false);
		document.baseForm.updownlist.options[selectedIndex] = next;
		document.baseForm.updownlist.options[selectedIndex + 1] = current;
	} 
}

function selectAllProperties() {
	var size = document.baseForm.updownlist.size;
	for(var i=0; i<size; i++)
	{
		if(document.baseForm.updownlist.options[i] != null) {
			document.baseForm.updownlist.options[i].selected = 'true';
		}
	}
}

function selectAllProps(obj) {
	var size = obj.size;
	for(var i=0; i<size; i++)
	{
		if(obj.options[i] != null) {
			obj.options[i].selected = 'true';
		}
	}
}

function sendAdminRequest(req) {
	document.baseForm.action = req;
	document.baseForm.submit();
}

function sendPaginationRequest(req, value) {
	document.baseForm.action = req;
	document.baseForm.gotoPage.value = value;
	document.baseForm.submit();
}

function nextPage(req, value) {
	document.baseForm.action = req;
	document.baseForm.gotoPage.value = value + 1;
	document.baseForm.submit();
}

function previousPage(req, value) {
	document.baseForm.action = req;
	document.baseForm.gotoPage.value = value - 1;
	document.baseForm.submit();
}

function deleteRateRow(req, value) {
	if(confirm('Are you sure you want to delete?')) {
		document.baseForm.action = req;
		document.baseForm.currencyIndex.value = value;
		document.baseForm.submit();
	}
}

function deleteRateRowFromPrice(req, value1, value2) {
	if(confirm('Are you sure you want to delete?')) {
		document.baseForm.action = req;
		document.baseForm.itemPriceId.value = value1;
		document.baseForm.currencyIndex.value = value2;
		document.baseForm.submit();
	}
}

function deleteLaborAndTravelRateFromPrice(req, value1, value2) {
	if(confirm('Are you sure you want to delete?')) {
		document.baseForm.action = req;
		document.baseForm.laborAndTravelRateId.value = value1;
		document.baseForm.currencyIndex.value = value2;
		document.baseForm.submit();
	}
}

function addItemRateToPrice(req, value) {
	document.baseForm.action = req;
	document.baseForm.itemPriceId.value = value;
	document.baseForm.submit();
}

function addLaborAndTravelRateToPrice(req, value) {
	document.baseForm.action = req;
	document.baseForm.laborAndTravelRateId.value = value;
	document.baseForm.submit();
}

function deleteItemPriceCriteria(req, value) {
	if(confirm('Are you sure you want to delete?')) {
		document.baseForm.action = req;
		document.baseForm.criteriaIndex.value = value;
		document.baseForm.submit();
	}
}

function deletePartReturnCriteria(req, value) {
	if(confirm('Are you sure you want to delete?')) {
		document.baseForm.action = req;
		document.baseForm.criteriaIndex.value = value;
		document.baseForm.submit();
	}
}

function deleteLaborAndTravelRateCriteria(req, value) {
	if(confirm('Are you sure you want to delete?')) {
		document.baseForm.action = req;
		document.baseForm.criteriaIndex.value = value;
		document.baseForm.submit();
	}
}

function deleteItemPrice(req, value) {
	if(confirm('Are you sure you want to delete?')) {
		document.baseForm.action = req;
		document.baseForm.itemPriceId.value = value;
		document.baseForm.submit();
	}
}

function deleteLaborAndTravelRate(req, value) {
	if(confirm('Are you sure you want to delete?')) {
		document.baseForm.action = req;
		document.baseForm.laborAndTravelRateId.value = value;
		document.baseForm.submit();
	}
}

function deleteModifierRow(value) {
	if(confirm('Are you sure you want to delete?')) {
		document.baseForm.action = "delete_modifier_criteria.action";
		document.baseForm.criteriaIndex.value = value;
		document.baseForm.submit();
	}
}

function deleteCriteriaBasedValue(value) {
	if(confirm('Are you sure you want to delete?')) {
		document.baseForm.action="delete_criteria_based_value.action";
		document.baseForm.valueId.value = value;
		document.baseForm.submit();
	}
}

function deletePartReturnDefinition(value) {
	if(confirm('Are you sure you want to delete?')) {
		document.baseForm.action="delete_part_return_definition.action";
		document.baseForm.definitionId.value = value;
		document.baseForm.submit();
	}
}

function sendAdminRequestForPaymentDefinition(req,value,id) {
	document.baseForm.action = req;
	document.baseForm.sectionId.value = value;
	document.baseForm.id.value = id;
	document.baseForm.submit();
}

function sendAdminRequestForUpdatingOrDeletingPaymentDefinition(req,value) {
	document.baseForm.action = req;
	document.baseForm.id.value = value;
	document.baseForm.submit();
}

function sendAdminRequestForDeletingPaymentItem(req,value,sectionId,itemId) {
	if(confirm('Are you sure you want to delete?')) {
		document.baseForm.action = req;
		document.baseForm.id.value = value;
		document.baseForm.sectionId.value = sectionId;
		document.baseForm.itemId.value = itemId;
		document.baseForm.submit();
	}
}

function sendAdminRequestForAddingRowInPaymentDefinition(req,value) {
     document.baseForm.action = req;
     document.baseForm.sectionId.value = value;
     document.baseForm.submit();
}

function setGroupType(req) {
        dojo.byId("baseForm").action = req+"?id="+dojo.byId("schemeId").value;
        dojo.byId("baseForm").submit();
}

function removeCheckedOptions(objName, parentDivId, emptyDivId) {
	var elts = document.getElementsByName(objName);
	var selectedElts = new Array();
	
	// Get the selected elements
	for (var i = 0, j = 0; i < elts.length; i++) {
		if (elts[i].checked) {
			selectedElts[j++] = elts[i];
		}
	}
	var noSelectedElts = selectedElts.length;
	var noElts = elts.length;
	
	var removedNodes = new Array();
	for (var i = 0; i < noSelectedElts; i++) {
		removedNodes[i] = destroyNode(selectedElts[i].parentNode.parentNode);
	}
	
	// If All the elements were deleted, Do not display table.
	if (noSelectedElts == noElts) {
		destroyNode(document.getElementById(parentDivId));
		var emptyDiv = document.getElementById(emptyDivId)
		if (emptyDiv) {
			emptyDiv.style.display = "block";
		}
	}
	return removedNodes;
}

function destroyNode(node) {
	if(node && node.parentNode) {
		return node.parentNode.removeChild(node);
	}
}

function selectAllOptions(objName,form) {
	var elts = document.getElementsByName(objName);
	for (var i = 0; i < elts.length; i++) {
		elts[i].checked = true;
	}
    form.method='post';
}

function toggleOptions(objName, toggleStatus) {
	var elts = document.getElementsByName(objName);
	for (var i = 0; i < elts.length; i++) {
		elts[i].checked = toggleStatus;
	}
}

function show(id)
{
	dojo.byId(id).style.display = '';
}
function hide(id)
{
	dojo.byId(id).style.display = 'none';
}
function addSpecCond(availObj, selObj) {

	var idx = availObj.selectedIndex
	while(idx != -1) {
		var value = availObj.options[idx].value;
		var iText = availObj.options[idx].innerHTML;
                var op = document.createElement("option");
                selObj.options.add(op);
                op.innerHTML = iText;
                op.value = value;

		deleteOption(availObj, availObj.selectedIndex);
		idx = availObj.selectedIndex;
	}
}

function deleteOption(selectObject,optionRank) {
    if (selectObject.options.length!=0) { selectObject.options[optionRank]=null }
}

function sortlist(list1) {
var lb = document.getElementById(list1);
arrTexts = new Array();
arrValue = new Array();

for(i=0; i<lb.length; i++)  {
  arrTexts[i] = lb.options[i].text;
  arrValue[i] = lb.options[i].value;
}

arrTexts.sort();
arrValue.sort();

for(i=0; i<lb.length; i++)  {
  lb.options[i].text = arrTexts[i];
  lb.options[i].value = arrValue[i];
}
}