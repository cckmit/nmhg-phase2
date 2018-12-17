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

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<%@taglib prefix="authz" uri="authz"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>

<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title><s:text name="title.common.warranty"/></title>
<s:head theme="twms"/>
<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>

	<u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="adminPayment.css"/>
	<u:stylePicker fileName="partreturn.css"/>
</head>
<script type="text/javascript">
	dojo.require("dijit.layout.ContentPane");
	dojo.require("twms.widget.TitlePane");
</script>
<u:body>
	<s:if test="%{isClaimDenied()}">
	<div class="twmsActionResultsSectionWrapper twmsActionResultsWarnings">
		<h4 class="twmsActionResultActionHead">WARNINGS</h4>
			<ol>
				<s:text name="label.parts.deniedClaims"></s:text>				
			</ol>
			<hr/>
	</div>
	</s:if>
    <div class="separatorTop"></div>		
	<s:form action="duepartsinspection_submit.action" id="baseFormId">		 
		<s:hidden id="identifier" name="id"/>
		<u:actionResults/>	
    <div dojoType="dijit.layout.ContentPane" label="Parts List" style="overflow-X: hidden; overflow-Y: auto;">		
	<s:set name="partsCounter" value="0"/>
	<s:hidden name="inboxViewType" value="%{inboxViewType}"/>
	<s:iterator value="claimWithPartBeans" status="claimIterator">	
	  	 <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>" labelNodeClass="section_header" >
      		<div style="width:98%"><%@include file="tables/claim_details.jsp"%></div>
      	 </div>
     <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.partDetails"/>" labelNodeClass="section_header" >
     
		  <table class="grid borderForTable" cellspacing="0" cellpadding="0">
	        <tr>
	          <th width="2%" valign="middle" class="colHeader" align="center">
	            <input id="selectAll_<s:property value="claim.id" />" type="checkbox" 		          		 
	          		   <s:if test="selected"> checked="checked" </s:if> 
	          		   value="checkbox"/>
				<script>
				  var masterCheckBox = new CheckBoxListControl(dojo.byId("selectAll_<s:property value="claim.id" />") );																		
				</script>
			  </th>			
			  <th width="12%"  valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
	          <%--<th width="19%"  valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>--%>
	          <th width="14%" valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.location" /></th>
	          <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.toBeShipped" /></th>
	          <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipped" /></th>
	          <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.cannotShip" /></th>
			  <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.received" /></th>
			  <th width="6%" valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.inspected" /></th>
			  <th valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.total" /></th>
			  <th valign="middle" class="colHeader"><s:text name="label.partReturn.isScrap" /></th>
			  <th valign="middle" class="colHeader">
		        <table>
		        <tr>
		        	<th valign="middle" style="color:#5577B4;"><s:text name="columnTitle.partReturnConfiguration.action" /></th>
	          		<th valign="middle" style="color:#5577B4;"><s:text name="label.partReturn.settlementCode" /></th>
	          		</tr>
	          	</table>
	          	</th>
	        </tr>	
	              
		    <s:iterator value="partReplacedBeans" status="partIterator">
		      <tr index="#index" class="tableDataWhiteText">
		        <td width="2%" valign="middle" align="center">
		           <s:checkbox   
		 		     name="partReplacedBeans[%{#partIterator.index}].selected"
		 		     value="selected" id="%{#claimIterator.index}_%{#partIterator.index}" cssStyle="border:none"/>
				  <script>			
				    var selectElementId = "<s:property value="%{#claimIterator.index}" />_<s:property value="%{#partIterator.index}" />";				
				    var selectElement =	dojo.byId(selectElementId);
					masterCheckBox.addListElement(selectElement);
				  </script>	 
		        </td>
		        
		        <s:iterator value="partReturnTasks" status ="taskIterator">
		         
			  	  <input type="hidden"
	 		     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].partReturnTasks[<s:property value="%{#taskIterator.index}"/>].task" 
			     value="<s:property value="task.id"/>"/>	
				</s:iterator>
				<input type="hidden"
	 		     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].oemPartReplaced" 
			     value="<s:property value="partReplacedBeans[#partIterator.index].oemPartReplaced.id"/>"/> 	
			     <input type="hidden"
	 		     name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].claim" 
			     value="<s:property value="partReplacedBeans[#partIterator.index].claim.id"/>"/>
				
				<td width="12%" >
				<s:property value="oemPartReplaced.itemReference.unserializedItem.alternateNumber" />
	                (<s:property value="oemPartReplaced.itemReference.unserializedItem.description" />)
	            <s:if test="oemPartReplaced.oemDealerPartReplaced!=null && oemPartReplaced.oemDealerPartReplaced.id!=null">
	               <img  src="image/comments.gif" id= "oem_dealer_causal_part"
		            		title="<s:property value="oemPartReplaced.oemDealerPartReplaced.number" />" 
		            		alt="<s:property value="oemPartReplaced.oemDealerPartReplaced.number" />"/>   			    		  		
		        </s:if>  
				</td>
		        <%--<td width="19%" ><s:property value="oemPartReplaced.itemReference.unserializedItem.description" /></td>--%>
		        <td width="14%" ><s:property value="oemPartReplaced.activePartReturn.returnLocation.code" /></td>
		        <td width="6%" ><s:property value="toBeShipped" /></td>
		        <td width="6%" ><s:property value="shipped" /></td>
		        <td width="6%" ><s:property value="cannotBeShipped" /></td>
		        <td width="6%" ><s:property value="received" /></td>
		        <input type="hidden"
	 		      name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].received"
			      value="<s:property value="received"/>"/>
		        <td width="6%" ><s:property value="inspected" /></td>
		        <input type="hidden"
	 		      name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].inspected"
			      value="<s:property value="inspected"/>"/>
		        <td width="6%"><s:property value="totalNoOfParts" /></td>
		        <td>
					<s:checkbox id="scrapped_%{#partsCounter}"
				   				name="partReplacedBeans[%{#partsCounter}].toBeScrapped">
				   	</s:checkbox>
                </td>
		        <td>
		        <div id="includeInspectionAction">
		        <table>
		        	<jsp:include flush="true" page="tables/inspectionActionInclude.jsp" />
		        </table>
		        </div>	
		        </td>        
		     </tr>   
		     <s:set name="partsCounter" value="%{#partsCounter + 1}"/>	
	         </s:iterator>
		  </table>	
	  </div>
	
	<div >
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.newClaim.supportDocs"/>" labelNodeClass="section_header">
            <jsp:include flush="true" page="../partreturns/uploadAttachments.jsp" />
        </div>
    </div>
    <jsp:include page="../partreturns/fileUploadDialog.jsp"/>  
	</s:iterator>	</div>
	
	
	
	    
		<div class="borderComments">
      <div class="detailsHeader">
        <s:text name="title.partReturnConfiguration.comments"/>
      </div>
      <table  cellspacing="0" cellpadding="0" class="grid">
	    		<tr>
	      			  <td width="5%" class="carrierLabel">&nbsp;&nbsp;<s:text name="label.partReturnConfiguration.comments"/>:</td>
					  <td width="95%">
					    <t:textarea name="comments" cols="80" rows="3"/>
					  </td>
	    		</tr>
	  		</table>
		   </div>
		
	<div class="buttonWrapperPrimary">
      <s:submit value="%{getText('button.common.submit')}" cssClass="buttonGeneric"/>
  	</div>
 </s:form>
<authz:ifPermitted resource="partReturnsDuePartsInspectionReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>
</html>