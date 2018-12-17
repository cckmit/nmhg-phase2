<%--

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

--%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<div class="admin_section_div">
<div class="admin_section_heading"><s:text name="accordionLabel.miscellaneousParts"/>
</div>
<u:repeatTable id="misc_parts_replaced_table" cssClass="repeat borderForTable"
	width="97%">
	<thead>
		<tr class="row_head">
			<th><s:text name="label.common.partNumber"/></th>
			<th ><s:text name="columnTitle.common.description"/></th>
			<th><s:text name="label.common.quantity" /></th>
			<th><s:text name="label.newClaim.unitPrice" /></th>
			<th><s:text name="label.miscPart.partUom" /></th>
			<th width="9%"><u:repeatAdd id="misc_parts_adder" theme="twms">
				<div align="center"><img src="image/addRow_new.gif" border="0"
					style="cursor: pointer;" title="<s:text name="label.common.addRow" />" /></div>
			</u:repeatAdd></th>
		</tr>
	</thead>
	<u:repeatTemplate id="misc_parts_replaced_body" value="campaign.miscPartsToReplace">
		<tr index="#index">
			<td><s:hidden name="campaign.miscPartsToReplace[#index]" /> 
			<s:hidden id="miscParts_miscitemconfig_#index" name="campaign.miscPartsToReplace[#index].miscItemConfig" />
			<sd:autocompleter id='miscParts_#index_partNo' size='5' href='getAllMiscParts.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='campaign.miscPartsToReplace[#index].miscItem' value='%{campaign.miscPartsToReplace[#index].miscItem.partNumber}' loadOnTextChange='true' showDownArrow='false' notifyTopics='/misc_part/partNo/changed/#index' /></td>
			<td><span id="desc_for_miscpart_#index"> </span> </td>
			<td><s:textfield name="campaign.miscPartsToReplace[#index].noOfUnits"
				id="miscParts_numberOfUnits_#index" /></td>
			<td><span id="price_for_miscpart_#index"> </span> </td>
			<td><span id="uom_for_miscpart_#index"> </span> </td>
			<script type="text/javascript">                      	
	   			dojo.addOnLoad( function() {
	               dojo.subscribe("/misc_part/partNo/changed/#index", null, function(data, type, request) {						                
	                   fillMiscPartDetails(#index, data, type);
	              	   });
	              });
			</script>
			<td><u:repeatDelete
				id="misc_parts_deleter_#indx">
				<div align="center"><img id="deleteConfiguration"
					src="image/remove.gif" border="0" style="cursor: pointer;"
					title="<s:text name="label.common.deleteRow" />" /></div>
			</u:repeatDelete>
			</td>
		</tr>
	</u:repeatTemplate>
</u:repeatTable>
</div>
<script type="text/javascript">                      	
    function fillMiscPartDetails(index, number, type) {
	    if (type != "valuechanged") {
	        return;
	    }
	    twms.ajax.fireJavaScriptRequest("getMiscellaneousItemDetailsForCampaign.action",{
	        number:number
	        }, function(details) {
	            var tr = findMiscPartRow(index);
	           	fillMiscPartDetailForARow(tr, details, index);
	            delete tr;
	        }
	    );
	} 
     	
    function findMiscPartRow(index) {
		    var tbody = dojo.byId("misc_parts_replaced_body");
		    var matches = dojo.query("> tr[index=" + index + "]", tbody);
		    return (matches.length == 1) ? matches[0] : null;
	}
     	 
     	function fillMiscPartDetailForARow(tr, details, index) {
		if(tr != null){
	        var descriptionField = dojo.query("> td:nth-child(2) > span", tr)[0];
	        descriptionField.innerHTML = details[1]; //description
	        var costPriceField = dojo.query("> td:nth-child(4) > span", tr)[0];
	        var costPrice = details[6] + details[5];
	        costPriceField.innerHTML = costPrice;
	        var uom = dojo.query("> td:nth-child(5) > span", tr)[0];
	        uom.innerHTML = details[2];	        
	        if (details[3]=='-') {
	        	dojo.byId("miscParts_miscitemconfig_"+index).value="";
	        } else {		           
	        	dojo.byId("miscParts_miscitemconfig_"+index).value=details[3];
	        }				        
	    }
	 }

</script>

