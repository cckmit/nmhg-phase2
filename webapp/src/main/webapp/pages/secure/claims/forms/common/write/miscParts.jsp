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
<s:hidden name="task.claim.miscPartsConfig" value="true"/>
<div class="mainTitle" style="margin-top:10px;margin-bottom:5px;">
	<s:text name="accordionLabel.miscellaneousParts"/></div>

<u:repeatTable id="misc_parts_replaced_table" cssClass="grid borderForTable"
	width="97%">
	<thead>

		<tr class="row_head">
			<th><s:text name="label.common.partNumber"/></th>
			<th ><s:text name="columnTitle.common.description"/></th>
			<th><s:text name="label.common.quantity" /></th>
			<th><s:text name="label.newClaim.unitPrice" /></th>
			<th><s:text name="label.miscPart.partUom" /></th>
			<s:if test="loggedInUserAnInternalUser">
					<th width="15%"><s:text name="label.miscpart.thresHold"/></th>
			</s:if>
			<th width="9%"><u:repeatAdd id="misc_parts_adder">
				<div class="repeat_add" ></div>
			</u:repeatAdd></th>
		</tr>
	</thead>
	<u:repeatTemplate id="misc_parts_replaced_body"
		value="task.claim.serviceInformation.serviceDetail.miscPartsReplaced">
		<tr index="#index">
			<td><s:hidden name="task.claim.serviceInformation.serviceDetail.miscPartsReplaced[#index]" /> 
			<s:hidden id="miscParts_miscitemconfig_#index" name="task.claim.serviceInformation.serviceDetail.miscPartsReplaced[#index].miscItemConfig" />
			<sd:autocompleter id='miscParts_#index_partNo' cssStyle='width:65px;' href='getAllMiscPartsForDealer.action?forDealer.id=%{task.claim.forDealer.id}&selectedBusinessUnit=%{selectedBusinessUnit}' name='task.claim.serviceInformation.serviceDetail.miscPartsReplaced[#index].miscItem' value='%{task.claim.serviceInformation.serviceDetail.miscPartsReplaced[#index].miscItem.partNumber}' loadOnTextChange='true' showDownArrow='false' notifyTopics='/misc_part/partNo/changed/#index' /></td>
			<td><span id="desc_for_miscpart_#index"> </span> 
			<script type="text/javascript">                      	
	   			dojo.addOnLoad( function() {
	               dojo.subscribe("/misc_part/partNo/changed/#index", null, function(data, type, request) {						                
	                   fillMiscPartDetails(#index, data, type);
	              });               
	         	});
		    </script>
		    </td>
			<td><s:textfield name="task.claim.serviceInformation.serviceDetail.miscPartsReplaced[#index].numberOfUnits"
				id="miscParts_numberOfUnits_#index" size="3" /></td>
			<td><span id="price_for_miscpart_#index"> </span></td>
			<td><span id="uom_for_miscpart_#index"></span></td>
			<s:if test="loggedInUserAnInternalUser">
					<td width="15%">
						<span id="thresHold_for_miscpart_#index"/>
					</td>
			 </s:if>
			<td><u:repeatDelete id="misc_parts_deleter_#index">
                    <div class="repeat_del" ></div>
			</u:repeatDelete></td>
		</tr>
	</u:repeatTemplate>
</u:repeatTable>




<script type="text/javascript">
			function fillMiscPartDetails(index, number, type) {
		    if (type != "valuechanged") {
		        return;
		    }
		    twms.ajax.fireJavaScriptRequest("getMiscellaneousItemDetails.action",{
		        number:number,
		        'forDealer.Id':'<s:property value="task.claim.forDealer.id" />'
		        },function(details) {
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
      	 
      	function fillMiscPartDetailForARow(tr, details, index,number) {
			if(tr != null){
		        var descriptionField = dojo.query("> td:nth-child(2) > span", tr)[0];
		        descriptionField.innerHTML = details[1]; //description
		        var costPriceField = dojo.query("> td:nth-child(4) > span", tr)[0];
		        var costPrice = details[3] + details[2];
		        costPriceField.innerHTML = costPrice;
		        var uom = dojo.query("> td:nth-child(5) > span", tr)[0];
		        uom.innerHTML = details[4];
		        dojo.byId("miscParts_miscitemconfig_"+index).value=details[5];				        
		        <s:if test="loggedInUserAnInternalUser">
		        	var thresholdQty = dojo.byId("thresHold_for_miscpart_"+index);
		        	if(thresholdQty){		        	
		        		thresholdQty.innerHTML = details[6];
		        	}	
		        </s:if>        
		    }
		 } 
      	 
</script>



