<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css" />
 </head>
  <script type="text/javascript">  	  	
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
  .bgColor tr td{
  padding-bottom:10px;
  }
  </style>
<u:body smudgeAlert="false">
	
    <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%;" id="root">
    <form action="validatePreDefinedPartRecoverySearchFields.action?context=PartRecoverySearches" method="POST" >
	<s:hidden name="savedQueryId" />
        <div>
          <s:fielderror/>
         <u:actionResults/>
        </div>
        <div class="policy_section_div" style="width:100%">
          <div  class="section_header">        
        	  		<s:text name="label.common.partRecoverySearch"/>
       			</div>	
		<table width="100%"  border="0" cellspacing="0" cellpadding="0" class="bgColor" style="margin:5px;">
		
      		<tr>
      		    <s:if test="(getLoggedInUser().businessUnits).size>1">
			 	 <td class="labelStyle"><s:text name="label.common.businessUnit" />:</td>
			 	  <td>
		 	 	    <s:iterator value="businessUnits" status="buItr">
		 	 	        <s:if test="partRecoverySearchCriteria.selectedBusinessUnits[#buItr.index] != null" >
		 	 	       	 	<input type="checkbox" 
			 				       name="partRecoverySearchCriteria.selectedBusinessUnits[<s:property value="#buItr.index"/>]"
				 	 		        value="<s:property value="name" />" checked="true"/>
				 	 		        <s:property value="name"/>
			 				 		    
			 			 </s:if>
		 				 <s:else> 				 
			 				<input type="checkbox" 
			 				       name="partRecoverySearchCriteria.selectedBusinessUnits[<s:property value="#buItr.index"/>]"
				 	 		       value="<s:property value="name" />" /> 
				 	 		       <s:property value="name"/>
			 			</s:else>
		 	 	   </s:iterator>	 
	   	 	 	 </td>
	   	 	    </s:if>     		    	
	   		</tr>
			<tr>
			 	 <td  width="20%" class="labelStyle"><s:text name="label.partReturn.supplierName" />:</td>
       	 	 	 <td>
       	 	 	 	<s:textfield name="partRecoverySearchCriteria.supplierName" id="supplierName"/>
       	 	 	 </td>	
			</tr>
			<tr>
			 	 <td  width="20%" class="labelStyle"><s:text name="label.supplier.supplierNumber" />:</td>
       	 	 	 <td>
       	 	 	 	<s:textfield name="partRecoverySearchCriteria.supplierNumber" id="supplierNumber"/>
       	 	 	 </td>	
			</tr>
			<tr>
			 	 <td  width="20%" class="labelStyle"><s:text name="label.common.partNumber" />:</td>
       	 	 	 <td>
       	 	 	 	<s:textfield name="partRecoverySearchCriteria.partNumber" id="partNumber"/>
       	 	 	 </td>	
			</tr>
			<tr>
			 	 <td class="labelStyle"><s:text name="label.common.claimNumber" />:</td>
       	 	 	 <td>
       	 	 	 	<s:textfield name="partRecoverySearchCriteria.claimNumber" id="claimNumber"/>
       	 	 	 </td>	
			</tr>
			<tr>
			 	 <td class="labelStyle"><s:text name="label.partReturnConfiguration.trackingNumber" />:</td>
       	 	 	 <td>
       	 	 	 	<s:textfield name="partRecoverySearchCriteria.trackingNumber" id="trackingNumber"/>
       	 	 	 </td>	
			</tr>
			<tr>
						<td class="labelStyle"><s:text name="label.common.status"/>:</td>
						<td class="labelStyle">
						<s:select name="partRecoverySearchCriteria.status" list="partRecoveryStatus" 
							listKey="getPartReturnStatus(status)" listValue="status" value="%{partRecoverySearchCriteria.status}"
							emptyOption="true" id="partRecoverySearchCriteriaStatus"/></td>
						<td colspan="2"/>
			</tr>			
			<tr>
			 	 <td  class="labelStyle"><s:text name="columnTitle.recoveryClaim.rgaNumber" />:</td>
       	 	 	 <td>
       	 	 	 	<s:textfield name="partRecoverySearchCriteria.rgaNumber" id="rgaNumber"/>
       	 	 	 </td>	
			</tr>
			
			
			<tr>
			<td  colspan="2" style="padding-left:90px;" class="labelStyle">
			<s:text name="button.common.saveSearch" />
					<s:checkbox	cssClass="buttonGeneric" name="notATemporaryQuery" id="notATemporaryQuery"
							value="notATemporaryQuery" onclick="enableText()"
							>
					</s:checkbox>
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
				
				<td class="label" valign="bottom"  colspan="2" style="padding-left:160px;">
					<s:reset label="reset" cssClass="buttonGeneric"></s:reset>
					<s:submit cssClass="buttonGeneric" value="%{getText('button.common.search')}"  />
					
				</td>
		</tr>			
		</table>
		</div>
	</form>
	</div>
	</u:body>
</html>