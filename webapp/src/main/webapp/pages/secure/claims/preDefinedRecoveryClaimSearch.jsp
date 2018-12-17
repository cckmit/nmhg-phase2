<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />     
     
    <script type="text/javascript">
  	  	dojo.require("dijit.layout.ContentPane");
      	dojo.require("dijit.layout.LayoutContainer");       	      	
      	
      	function enableText()
		{
			document.getElementById("searchQueryName").disabled=!document.getElementById("notATemporaryQuery").checked;
			if(document.getElementById("notATemporaryQuery").checked){
				document.getElementById("searchQueryName").value='';
			}
			return true;
		}    
    </script>
<style>
td{
padding-left:5px;
}
.bgColor td {
padding-bottom:10px;
}
</style>
</head>
<u:body>
	
	 <div dojoType="dijit.layout.LayoutContainer" style="width:100%;
	 height:97.8%; overflow-x:hidden; overflow-y:auto;" id="root" >
	     <div dojoType="dijit.layout.ContentPane">
	         <div class="policy_section_div" style="margin-right:0px;width:100%">
	        <u:actionResults/>
			<s:fielderror theme="xhtml" />
	        <div id="dcap_pricing_title" class="section_header">
	            <s:text name="title.claim.searchClaim"/>
		     </div>
			<form action="validatePreDefinedRecoveryClaimsSearchFields.action?context=RecoveryClaimSearches" method="POST" >			 
	        <s:hidden name="savedQueryId" />
			<table width="100%"  border="0" cellspacing="0" cellpadding="0" class="grid">
			
					<tr>
					   <s:if test="(getLoggedInUser().businessUnits).size>1">
					 	 <td class="labelStyle" width="20%" nowrap="nowrap"><s:text name="label.common.businessUnit" />:</td>
					 	  <td style="padding-left:45px;">
				 	 	    <s:iterator value="businessUnits" status="buItr">
				 	 	        <s:if test="recoveryClaimCriteria.selectedBusinessUnits[#buItr.index] != null" >
				 	 	       	 	<input type="checkbox" 
					 				       name="recoveryClaimCriteria.selectedBusinessUnits[<s:property value="#buItr.index"/>]"
						 	 		        value="<s:property value="name" />" checked="true"/>
						 	 		        <s:property value="name"/>
					 				 		    
					 			 </s:if>
				 				 <s:else> 				 
					 				<input type="checkbox" 
					 				       name="recoveryClaimCriteria.selectedBusinessUnits[<s:property value="#buItr.index"/>]"
						 	 		       value="<s:property value="name" />" /> 
						 	 		       <s:property value="name"/>
					 			</s:else>
				 	 	   </s:iterator>	 
			   	 	 	 </td>
	   	 	          </s:if>					    
			   		</tr>
					<tr>
						<td class="labelStyle" nowrap="nowrap"> <s:text name="label.partReturn.supplierName" /> :</td>
						<td class="labelStyle" style="padding-left:45px;"><s:textfield
							name="recoveryClaimCriteria.supplierName"
							id="recoveryClaimCriteria.supplierName" size="40"/></td>
						<td colspan="2"/>	
					</tr>
					<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.supplier.supplierNumber" />:</td>
						<td class="labelStyle" style="padding-left:45px;"><s:textfield
							name="recoveryClaimCriteria.supplierNumber"
							id="recoveryClaimCriteria.supplierNumber" /></td>
						<td colspan="2"/>	
					</tr>
					<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="columnTitle.supplyRecoveryAdmin.recoveryClaim_no" />:</td>
						<td class="searchLabel" style="padding-left:45px;"><s:textfield
							name="recoveryClaimCriteria.recoveryClaimNumber"
							id="recoveryClaimCriteria.recoveryClaimNumber" /></td>
						<td colspan="2"/>	
					</tr>
					<s:if test="isBuConfigEMEA()">
					<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.recoveryClaim.documentNumber" />:</td>
						<td class="searchLabel" style="padding-left:45px;"><s:textfield
							name="recoveryClaimCriteria.documentNumber"
							id="recoveryClaimCriteria.documentNumber" /></td>
						<td colspan="2"/>
					</tr>
					</s:if>		
					<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.common.claimNumber" />:</td>
						<td class="searchLabel" style="padding-left:45px;"><s:textfield
							name="recoveryClaimCriteria.claimNumber"
							id="recoveryClaimCriteria.claimNumber" /></td>
						<td colspan="2"/>	
					</tr>
					<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.supplier.supplierMemoNumber" />:</td>
						<td class="searchLabel" style="padding-left:45px;"><s:textfield
							name="recoveryClaimCriteria.supplierMemoNumber"
							id="recoveryClaimCriteria.supplierMemoNumber" /></td>
						<td colspan="2"/>	
					</tr>
					
					<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.common.status"/>:</td>
						<!-- fix for 'unable to save processor recovery predefined search with 'status 'as search parameter' -->
						<td class="searchLabel" style="padding-left:45px;"><s:select name="recoveryClaimCriteria.state"
						 id="recoveryClaimCriteria.state" list="allRecoveryClaimStates" multiple="true" size="8"  listValue="state" listKey="state" emptyOption="true"/></td>
						 <td colspan="2"/>
					</tr>
					<tr>
					<td class="labelStyle" nowrap="nowrap" colspan="4"><s:text name="label.claim.claimMarked" />:</td>
					</tr>
					<tr>
						<td class="labelStyle"></td>
						<td class="labelStyle" ><s:text name="label.common.from" /> :
						<sd:datetimepicker name='recoveryClaimCriteria.startWarrantyRequestDate' id='recoveryClaimCriteria.startWarrantyRequestDate' label='From' /></td>
					</tr>
					<tr>
						<td class="labelStyle"></td>
						<td class="labelStyle"><s:text name="label.common.to" />:
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<sd:datetimepicker name='recoveryClaimCriteria.endWarrantyRequestDate' id='recoveryClaimCriteria.endWarrantyRequestDate' label='To' /></td> 						 
					</tr>										
					
					<tr>
						<td class="labelStyle" nowrap="nowrap"><s:text name="label.common.closedDate" />:</td>
						<td class="labelStyle"><s:text name="label.common.from" /> :
						<sd:datetimepicker name='recoveryClaimCriteria.startClosedDate' id='recoveryClaimCriteria.startClosedDate' label='From' /></td>
					</tr>
					<tr>
						<td class="labelStyle"></td>
						<td class="labelStyle"><s:text name="label.common.to" /> :
						&nbsp;&nbsp;&nbsp;&nbsp;<sd:datetimepicker name='recoveryClaimCriteria.endClosedDate' id='recoveryClaimCriteria.endClosedDate' label='To' /></td> 
					</tr>
					<tr>
						<td class="labelStyle"><s:text name="label.claim.claimPayDate" />:</td>
						<td class="labelStyle"><s:text name="label.common.from" /> :
						<sd:datetimepicker name='recoveryClaimCriteria.startClaimPayDate' id='recoveryClaimCriteria.startClaimPayDate' label='From' /></td>
					</tr>
					<tr>
						<td class="labelStyle"></td>
						<td class="labelStyle"><s:text name="label.common.to" /> :
						&nbsp;&nbsp;&nbsp;&nbsp;<sd:datetimepicker name='recoveryClaimCriteria.endClaimPayDate' id='recoveryClaimCriteria.endClaimPayDate' label='To' /></td> 
					</tr>	
					<tr>
					<td  class="labelStyle" colspan="3" style="padding-left:78px" > 
					<s:text name="button.common.saveSearch" />
						<s:checkbox	cssClass="buttonGeneric" name="notATemporaryQuery" id="notATemporaryQuery"
							value="notATemporaryQuery" onclick="enableText()"></s:checkbox>
							<!-- Fix for NMHGSLMS-992 -->
						<s:if test = "searchQueryName!=null" >
							<s:textfield name="searchQueryName" id="searchQueryName" value="%{searchQueryName}"></s:textfield>
						</s:if>
						<s:else>
							<s:textfield name="searchQueryName"  id="searchQueryName" disabled="true" value="Name of the Query" ></s:textfield>
						</s:else>
							
						</td>
					
					</tr>				
					<tr >
						<td> </td>
						<td class="label" valign="bottom"style="padding-left:40px;" >
						<s:reset label="reset" cssClass="buttonGeneric"></s:reset>
						<s:submit cssClass="buttonGeneric" value="%{getText('button.common.search')}"  />
						
						</td>
					</tr>
					<tr><td style="padding:0;margin:0 ">&nbsp;</td></tr>
		
			</table>
    </form>  
	</div>
	</div>
	</div>

	</u:body>
</html>