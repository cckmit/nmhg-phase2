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
<%@taglib prefix="authz" uri="authz"%>

<table class="grid" cellspacing="0" cellpadding="0" >
<s:if test= " isThirdParty " >
	<tr>
	<td  class="labelStyle" width="20%" nowrap="nowrap">
		<s:text name="label.laorRate.thirdPartyLaborRate" />
	</td>
	<td></td>
	<td >
		<t:money id="thirdPartyLaborRate" 
	                	name="claim.serviceInformation.thirdPartyLaborRate" defaultSymbol="$" 
	                	value="%{claim.serviceInformation.thirdPartyLaborRate}"/>
	</td>
	</tr>
</s:if>
	
</table>	
<div dojoType="dijit.layout.ContentPane" >
	
	<table class="grid borderForTable" width="100%" cellspacing="0" cellpadding="0" align="center"  style="margin:5px;width:95%">
		<tr class="row_head"> 
			<th ><s:text name="label.common.jobCode"/></th>
			<th ><s:text name="label.campaign.jobCodeDescription"/></th>
            <s:if test="%{!isStdLaborHrsToDisplay(claim)}">
                <th><s:text name="label.campaign.laborHrsEntered"/></th>
            </s:if>
            <s:if test="%{isStdLaborHrsToDisplay(claim) || isProcessorReview()}">
                <th ><s:text name="label.newClaim.suggestedLaborHours"/></th>
            </s:if>
            <s:if test="isEligibleForAdditionalLaborDetails(claim)">
	            <th><s:text name="label.newClaim.additionalLaborHours"/></th>
				<th ><s:text name="label.newClaim.reasonAdditionalLaborHrs"/></th>
		   </s:if>		
			<th><s:text name="label.common.additionalAttributes"/></th>
		</tr>
		<s:iterator value="claim.serviceInformation.serviceDetail.laborPerformed" status="laborStatus">
		<tr>
			<td>
				<span title="<s:property value="serviceProcedure.serviceProcedureDescription"/>">
					<s:property value="serviceProcedure.definition.code"/>
				</span>
			</td>
			<td>
				<span title="<s:property value="serviceProcedure.serviceProcedureDescription"/>">
					<s:property value="displayJobCodeDescription().get(#laborStatus.index)"/> 
				</span>
			</td>
            <s:if test="%{!isStdLaborHrsToDisplay(claim)}">
                <td class="numeric"><s:property value="laborHrsEntered"/></td>
            </s:if>
            <s:if test="%{isStdLaborHrsToDisplay(claim) || isProcessorReview()}">  
				<td class="numeric"><s:property value="hoursSpent"/></td>
			</s:if>
            <s:if test="isEligibleForAdditionalLaborDetails(claim)">
	            <td class="numeric"><s:property value="additionalLaborHoursUpdated(additionalLaborHours)"/></td>
				<td><s:property value="reasonForAdditionalHours"/></td>
		    </s:if>		
			<td>
			<s:a cssStyle="cursor:pointer;color:blue;text-decoration:underline" cssClass="alinkclickable" href="#">
        		<span id="laborDetailsAttributes_<s:property value="#laborStatus.index"/>" class="alinkclickable"></span>
        	</s:a>
        	<div id = "dialogBoxContainer" style="display: none"> 
                <div dojoType="twms.widget.Dialog" id="jobCodeAtrribute_<s:property value="#laborStatus.index"/>"
                     bgColor="#FFF" bgOpacity="0.5" toggle="fade"
                    toggleDuration="250" style="width:500px; height:300px;"  title="<s:text name="label.common.additionalAttributes"/>" >
                    <div  style="width:450px; height:225px;background: #F3FBFE; border: 1px solid #EFEBF7">
                        <div dojoType="dojox.layout.ContentPane"
                              id="jobAttributeContentPane_<s:property value="#laborStatus.index"/>" layoutAlign="top" >
               
                    <table width="100%">
                        <tbody>
                        <s:iterator value="claimAttributes" >
                            <tr>
                                <td class="labelStyle">
                                     <s:property value="attributes.name" />
                                </td>
                                <td  class="labelNormal">
                                          <s:if test="attrValue.length()>70">
				                   <s:textarea theme="simple" value="%{attrValue}"  readOnly="true" rows="6" cols="70"/>
			                   </s:if> <s:else>
				                 <s:property value="attrValue" />
				                </s:else>
                                </td>
                            </tr>
                        </s:iterator>
                     </tbody>
                  </table>
                  <table width="100%">
                    <tr>
                        <td >&nbsp;</td>
                        <td id="submitSection"  align="left" class="buttons" style="padding-top: 20px;">
                            <input type="button" id="closeJobCodeAttrPopup_<s:property value="#laborStatus.index"/>"
                                      value='<s:text name="button.common.close"/>'/>
                        </td>
                    </tr>
                    </table>
                  </div>
                </div>
              </div>
			</div>
        	</td>
        	<script type="text/javascript">
        	dojo.addOnLoad(function(){	
        		var isAttributeSize="<s:property value="claim.serviceInformation.serviceDetail.laborPerformed[#laborStatus.index].claimAttributes.size"/>";          		
        			if(isAttributeSize > 0){
						dojo.byId("laborDetailsAttributes_<s:property value="#laborStatus.index"/>").innerHTML=
                            '<s:text name="label.additionalAttribute.viewAdditionalAttributes"/>';
						dojo.connect(dojo.byId("laborDetailsAttributes_<s:property value="#laborStatus.index"/>"),
                                "onclick",function(){
						dojo.publish("/job_<s:property value="#laborStatus.index"/>/attribute/show");
					});
						dojo.subscribe("/job_<s:property value="#laborStatus.index"/>/attribute/show", null, function() {
						dlg = dijit.byId("jobCodeAtrribute_<s:property value="#laborStatus.index"/>");
						dlg.show();
							
		    		});
		    			dojo.subscribe("/job_<s:property value="#laborStatus.index"/>/attribute/hide", null, function() {
						 	dijit.byId("jobCodeAtrribute_<s:property value="#laborStatus.index"/>").hide();
						    	}); 
						dojo.connect(dojo.byId("closeJobCodeAttrPopup_<s:property value="#laborStatus.index"/>"),
                                "onclick",function() {
						     	dojo.publish("/job_<s:property value="#laborStatus.index"/>/attribute/hide");
						});  
					}
					});
        	</script>
        	
		</tr>
		</s:iterator>
	</table>
</div>
<s:if test="isLaborSplitEnabled()">
<div dojoType="dijit.layout.ContentPane" class="sub_section">
	<table class="grid borderForTable" width="100%" cellspacing="0" cellpadding="0">
		<tr class="row_head"> 
			<th ><s:text name="lable.labor.type.additional"/></th>
            <th><s:text name="lable.labor.type.labor.hrs"/></th>
            <th><s:text name="lable.labor.type.reason"/></th>
            <s:if test="getLaborSplitOption() == 'OPTIONAL'">
           	<th><s:text name="lable.labor.type.inclusive"/></th>
           	</s:if>
		</tr>
		<s:iterator value="claim.serviceInformation.serviceDetail.laborSplit" status="laborSplit">
		<tr>
			<td class="labelStyle" width="20%" nowrap="nowrap"><s:property value="laborType.laborType"/></td>
			<td class="numeric"><s:property value="hoursSpent"/></td>
			<td class="labelStyle" class="labelStyle"> <s:property value="reason"/></td>
			<s:if test="getLaborSplitOption() == 'OPTIONAL'">
			<td><s:property value="inclusive"/></td>
			</s:if>
			
		</tr>
		</s:iterator>
	</table>		
</div>
</s:if>




	