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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<style type="text/css">
.separator {
	margin-left: 17px;
}
</style>

<script type="text/javascript">
	dojo.require("twms.widget.Dialog");
	 dojo.require("twms.widget.TitlePane");
</script>


<table cellspacing="0" cellpadding="0" class="grid borderForTable"
	align="center" width="100%">
	<thead>
		<tr class="row_head">
			<th><s:text name="label.common.serialNumber" /></th>
			<th><s:text name="label.common.itemNumber" /></th>
			<th><s:text name="label.common.itemDescription" /></th>
			<authz:ifUserNotInRole roles="supplier">                              
             <th ><s:text name="label.common.overallWarrantyStartDate"/></th>             
             <th><s:text name="label.common.overallWarrantyEndDate"/></th>
            </authz:ifUserNotInRole>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>
			 <authz:ifUserInRole roles="supplier, receiverLimitedView, inspectorLimitedView, partShipperLimitedView">
						<span style="color:black">
							<s:property value="claim.partItemReference.referredInventoryItem.serialNumber" />
						</span>
	        	    </authz:ifUserInRole>
    	    	    <authz:else>
    	    	      <span style="color:blue;cursor:pointer;text-decoration:underline">
			          <s:if test="(claim.partItemReference.referredInventoryItem.d.isActive())"> 
 	    	    		<u:openTab cssClass="link" url="majorComponentInventoryDetail.action?id=%{claim.partItemReference.referredInventoryItem.id}"
		        			id="serialLink_%{#claimedItems.index}" tabLabel="Serial Number %{claim.partItemReference.referredInventoryItem.serialNumber}"
	                		autoPickDecendentOf="true" catagory="majorComponents">
							<s:property value="claim.partItemReference.referredInventoryItem.serialNumber" />
			         	</u:openTab>
			         	</s:if>			         
			         </span>
			         	<s:else>
			         	<s:property value="claim.partItemReference.referredInventoryItem.serialNumber" />
			         	</s:else>
			    
        		    </authz:else>
			</td>
			<td><s:property
				value="claim.partItemReference.referredInventoryItem.ofType.number" /></td>
			<td><s:property
				value="claim.partItemReference.referredItem.description" /></td>
			<authz:ifUserNotInRole roles="supplier">				
					<td><s:property
						value="claim.partItemReference.referredInventoryItem.wntyStartDate" /></td>
					<td><s:property
						value="claim.partItemReference.referredInventoryItem.wntyEndDate" /></td>				
			</authz:ifUserNotInRole>
		</tr>

	</tbody>
</table>


