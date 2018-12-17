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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>

 <script type="text/javascript">
dojo.require("twms.widget.Dialog");
</script>
<table cellspacing="0" border="0" cellpadding="0" class="grid" width="100%">
  <tr>
    <td class="labelStyle" width="16%"  nowrap="nowrap"><s:text name="label.common.claimNumber" />:</td>
    <td class="labelNormal" width="35%">
    	<authz:ifUserNotInRole roles="receiverLimitedView, inspectorLimitedView, partShipperLimitedView">
	      <u:openTab tabLabel="Claim Number"
	      			 url="view_search_detail.action?id=%{claim.id}"
	      			 id="claimIdForPart%{claim.id}"
	      			 cssClass="link"
	      			 decendentOf="%{getText('label.invTransaction.tabHeading')}">
	    	<s:property value="claim.claimNumber" />
	    	</u:openTab>
    	</authz:ifUserNotInRole>
    	<authz:ifUserInRole roles="receiverLimitedView, inspectorLimitedView, partShipperLimitedView">
    		<s:property value="claim.claimNumber" />
    	</authz:ifUserInRole>
	</td>		    		     
    <td class="labelStyle"  nowrap="nowrap" width="16%"><s:text name="label.common.serialNumber" />:</td>
    <td class="labelNormal" width="35%">
      <s:if test="claim.itemReference.referredInventoryItem.serialNumber != null">    
      	<authz:ifUserNotInRole roles="receiverLimitedView, inspectorLimitedView, partShipperLimitedView">
		    <u:openTab tabLabel="Serial Number %{claim.itemReference.referredInventoryItem.serialNumber}"
		      		   url="inventoryDetail.action?id=%{claim.itemReference.referredInventoryItem.id}"
		      		   id="SerialNoForPart%{claim.itemReference.referredInventoryItem.serialNumber}%{claim.id}"
		      		   cssClass="link"
		      		   decendentOf="%{getText('label.invTransaction.tabHeading')}">	      
		      <s:property value="claim.itemReference.referredInventoryItem.serialNumber" />
		    </u:openTab>
	   </authz:ifUserNotInRole>
	   <authz:ifUserInRole roles="receiverLimitedView, inspectorLimitedView, partShipperLimitedView">
	    	 <s:property value="claim.itemReference.referredInventoryItem.serialNumber" />
	  	</authz:ifUserInRole>
	  </s:if>
	  <s:else>
	    <div>-</div>
	  </s:else> 
	</td>
  </tr>
  <tr>
    <s:if test="claim.type.type.equals('Parts') && claim.partInstalled == false">
      <td class="labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.partNumberWithCol" />:</td>
      <s:if test="isLoggedInUserADealer()">
      <td class="labelNormal"><s:property value="%{claim.brandPartItem.itemNumber}" /></td>
      </s:if>
      <s:else>
      <td class="labelNormal"><s:property value="%{claim.partItemReference.referredItem.alternateNumber}" /></td>
      </s:else>
    </s:if>
    <s:else>  
      <td class="labelStyle"  nowrap="nowrap"><s:text name="label.partReturnConfiguration.modelnumber" />:</td>
      <td class="labelNormal">
      <s:if test="claim.itemReference.isSerialized()">
    	<s:property value="claim.itemReference.unserializedItem.model.name" />
      </s:if>
      <s:else>
    	<s:property value="claim.itemReference.model.name" />
      </s:else>
      </td>
    </s:else>      
  </tr>		    
</table>
