<%--

   Copyright (c)2006 Tavant   Technologies
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

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<script type="text/javascript">
    dojo.require("dijit.Tooltip");
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("twms.widget.TitlePane");
    dojo.require("dijit.layout.TabContainer");
    
</script>
<style type="text/css">
	.dijitTooltipContainer {
	position:left;
	background-color:#CCD9FF;
	border:1px solid gray;
	font-family:Verdana, sans-serif, Arial, Helvetica;
	max-width:450px;
	
	font-size: 10px;
	display: block; 
	
	}
</style>
<table class="grid borderForTable" cellspacing="0" align="center" cellpadding="0" style="width:95% ">
	<thead>
		<tr class="row_head">
			<th width="20%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="label.policyAudit.createdBy"/></th>
		    <th width="20%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="columnTitle.newClaim.createdOn"/></th>
		    <th width="10%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="columnTitle.laborHistory.LastModifiedDate"/></th>
		    <th width="10%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="columnTitle.manageBusinessRule.history.modifiedBy"/></th>
		    <th width="10%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="label.warrantyAdmin.comments"/></th>
		    <th width="10%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="columnTitle.common.status"/></th>  
			<th width="10%" nowrap="nowrap" class="colHeader" style="border:1px solid #fff"><s:text name="label.common.details"/></th>
		</tr>
		<s:hidden id="size" value="stateMandates.stateMandateAudit.size()"/>
		<s:iterator value="stateMandates.stateMandateAudit" status="stat">  
		 <tr>
		  <td width="10%" style="padding:5px 5px 5px 5px"><s:property value="createdBy.getCompleteNameAndLogin()"/></td>
		  <td width="10%" style="padding:5px 5px 5px 5px">
		  	<s:if test = "#stat.index == stateMandates.stateMandateAudit.size() - 1 ">
		  		<s:property value="d.createdOn"/>
		  	</s:if>
		  </td>
		   <td width="10%" style="padding:5px 5px 5px 5px"><s:property value="d.updatedOn"/></td>
		    <td width="10%" style="padding:5px 5px 5px 5px"><s:property value="d.lastUpdatedBy.getCompleteNameAndLogin()"/></td>
		      <s:if test="comments == null">
             <td  width="10%" style="padding:5px 5px 5px 5px" class="label">-</td>
            </s:if>                          
            <s:else>                             			
		       <td width="10%" style="padding:5px 5px 5px 5px" ><s:property value="comments"/></td>
		    </s:else>
		     <td width="10%" style="padding:5px 5px 5px 5px"> <s:if test="%{#stat.index == 0}">Active </s:if><s:else>InActive</s:else> </td>
		    <td width="10%" style="padding:5px 5px 5px 5px"><s:if test="%{#stat.index == 0}"></s:if><s:else><a id="<s:property value='id' />" >View</a></s:else></td>
		 	 <script type="text/javascript">
                dojo.addOnLoad(
                    function() {
                        var conElemnt= dojo.byId("<s:property value='id' />");
                        if(conElemnt){
                        dojo.connect(conElemnt, "onclick", function(eventOld) {
                        var event= cloneEvenIfIe(eventOld);
                        event.label = "State Mandate Audit History Details";
                        event.url = "history_stateMandates.action?stateMandateAuditID=<s:property value='id' />";
                        event.decendentOf = "Create State Mandate";
                        event.forceNewTab = true;
                        top.requestTab(event);
                        });
                    }
                }
            );
            </script>
		 </tr>
		</s:iterator>
		
	</thead>
	<tbody>
	
	</tbody>

</table>