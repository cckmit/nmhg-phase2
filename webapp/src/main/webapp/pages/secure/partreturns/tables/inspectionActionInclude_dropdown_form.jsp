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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<style type="text/css">
</style>
<script type="text/javascript">
	dojo.require("twms.widget.TitlePane");
	dojo.require("twms.widget.Select");
</script>

		<tr>
			<td width="10%">
			 <s:select name="partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].inspectionStatus" list="inspectionStatusList"
			                  required="true"    id="inspectionStatus_%{#partsCounter}_%{#taskIterator.index}"  value="%{inspectionStatus.name()}"
			                  cssStyle="width: 130px" />
			                  </td>
			
	        <td id="rowAcceptId_<s:property value="#partsCounter"/>_<s:property value="#taskIterator.index"/>" width="90%" id="test" align="center">
	                    <tda:lov name="partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].acceptanceCause"
	                 className="PartAcceptanceReason"
	                 id="acceptReasons_%{#partsCounter}_%{#taskIterator.index}"
	                 businessUnitName="claim.businessUnitInfo.name"></tda:lov>
	                   
	         </td>
	         <td class="rowFailures" id ="rowFailureId_<s:property value="#partsCounter"/>_<s:property value="#taskIterator.index"/>" >
	         <tda:lov name="partReplacedBeans[%{#partsCounter}].partReturnTasks[%{#taskIterator.index}].failureCause"
	                                 className="FailureReason" id="failureReasons_%{#partsCounter}_%{#taskIterator.index}"
	                                 businessUnitName="claim.businessUnitInfo.name"></tda:lov>
	         </td>
         </tr>
<script type="text/javascript">
	dojo.addOnLoad(function() {
		 setVisibility();
	     var index = '<s:property value="%{#partsCounter}"/>' + "_" + '<s:property value="%{#taskIterator.index}"/>';
	     dojo.connect(
	          dojo.byId("inspectionStatus_"+index), "onchange",function(evt)
	            {updateForm(index);
	            });
	                 
	});
	
	
	function setVisibility(){
		
	 dojo.query(".rowFailures").forEach(function(node){
			twms.util.adjustVisibilityAndSubmission(dojo.byId(node),false);
		 });
		
	}
	
	function updateForm(index){
		var value= dojo.byId("inspectionStatus_"+index).value;
		if(value=='ACCEPT'){
			twms.util.adjustVisibilityAndSubmission(dojo.byId("rowFailureId_"+index), false);
			twms.util.adjustVisibilityAndSubmission(dojo.byId("rowAcceptId_"+index), true);
		}else if(value=='REJECT'){
			twms.util.adjustVisibilityAndSubmission(dojo.byId("rowAcceptId_"+index), false);
			twms.util.adjustVisibilityAndSubmission(dojo.byId("rowFailureId_"+index), true);
	}};
                         
</script>
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
                        
 
