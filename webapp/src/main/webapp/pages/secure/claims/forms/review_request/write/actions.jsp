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
<%@ taglib prefix="t" uri="twms" %>
<table class="grid">
	<tr>
		<td width="25%" nowrap="nowrap" class="labelStyle">
			<s:text name="label.newClaim.processorComments"/>:
		</td>
		<td width="75%" class="label">
			<s:property value="task.claim.internalComment"/>
			     <s:if test="task!=null">
					<s:hidden name="task.claim.decision" value="" />
				</s:if>
				<s:else>
					<s:hidden name="claim.decision" value="" />
				</s:else>
		</td>
	</tr>
	<tr>
		<td width="25%" nowrap="nowrap" class="labelNormalTop labelStyle">
			<s:text name="label.common.comments"/>:
		</td>
		<td width="75%" class="labelNormalTop">
			<t:textarea name="task.claim.internalComment" rows="3" cols="50" cssStyle="bodyText" value=""></t:textarea>
			   <s:if test="task!=null">
					<s:hidden name="task.claim.externalComment" value=""/>	
				</s:if>
				<s:else>
					<s:hidden name="claim.externalComment" value=""/>	
				</s:else>				
		</td>
	</tr>
	<tr>
    	<td width="50%"class="labelStyle">
            <s:checkbox id="radioReviewCP" name="cpReview" onclick="disableButton()"/> 
            <s:text name="label.newClaim.sendToCPAdvisor"/>           
        </td>
       
        <td width="50%" class="labelNormalTop" colspan="2">
        <s:select id="reviewCPAdvisor" list="CPAdvisor" disabled="true" onchange="enableTrasfer()"
                name="task.seekReviewFrom" headerKey="" headerValue="%{getText('label.common.selectHeader')}"/>            
        </td>
        <script type="text/javascript">
        	
        	function disableButton(){
        		if(dojo.byId("radioReviewCP").checked){
 					dijit.byId("reviewCPAdvisor").setDisabled(false);
 					if(dojo.byId("normal_submit_0")!=null){
 						dojo.html.hide(dojo.byId("normal_submit_0"));
 					}else if(dojo.byId("campaign_submit_0")!=null){
 						dojo.html.hide(dojo.byId("campaign_submit_0"));
 					}else if(dojo.byId("part_submit_0")!=null){
 						dojo.html.hide(dojo.byId("part_submit_0"));
 					}	
 				}else {
 					dijit.byId("reviewCPAdvisor").setDisabled(true);
 					if(dojo.byId("normal_submit_0")!=null){
 						dojo.html.show(dojo.byId("normal_submit_0"));
 						dojo.html.hide(dojo.byId("normal_submit_1"));
 						dijit.byId("reviewCPAdvisor").setDisplayedValue("--Select--");
 					}else if(dojo.byId("campaign_submit_0")!=null){
 						dojo.html.show(dojo.byId("campaign_submit_0"));
 						dojo.html.hide(dojo.byId("campaign_submit_1"));
 						dijit.byId("reviewCPAdvisor").setDisplayedValue("--Select--");
 					}else if(dojo.byId("part_submit_0")!=null){
 						dojo.html.show(dojo.byId("part_submit_0"));
 						dojo.html.hide(dojo.byId("part_submit_1"));
 						dijit.byId("reviewCPAdvisor").setDisplayedValue("--Select--");
 					}
 				}
 			}  
 			function enableTrasfer(){   
 				if(dojo.byId("reviewCPAdvisor").value!='--Select--'){
	 				if(dojo.byId("normal_submit_1")!=null){
	 					dojo.html.show(dojo.byId("normal_submit_1"));
	 				}
	 				if(dojo.byId("campaign_submit_1")!=null){
	 					dojo.html.show(dojo.byId("campaign_submit_1"));
	 				}
	 				if(dojo.byId("part_submit_1")!=null){
	 					dojo.html.show(dojo.byId("part_submit_1"));
	 				}	
 				}
 				else{
 					if(dojo.byId("normal_submit_1")!=null){	
	 					dojo.html.hide(dojo.byId("normal_submit_1"));
	 				}
	 				if(dojo.byId("campaign_submit_1")!=null){
 						dojo.html.hide(dojo.byId("campaign_submit_1"));
 					}
 					if(dojo.byId("part_submit_1")!=null){
 						dojo.html.hide(dojo.byId("part_submit_1"));
 					}
 				}
 			}  
        </script>
    </tr>  
</table>
<s:hidden name="mandatedComments[0]" value="internalComments"/>
<div id="separator"></div>
<div class="buttonWrapperPrimary">
	<s:hidden name="task.takenTransition" id="transitionField"/>
	<s:if test="task.claim.type.type == 'Campaign'">
    <s:iterator value="task.transitions" status="status">
     	<s:submit id="campaign_submit_%{#status.index}" value="%{top}" type="button" action="campaign_claim_submit"/>
			<script type="text/javascript">
				dojo.addOnLoad(function() {
					dojo.connect(dojo.byId("campaign_submit_<s:property value='#status.index'/>"), "onclick", function() {
						dojo.byId('transitionField').value="<s:property value='top'/>";
						return;
					});
				});
				if(dojo.byId("campaign_submit_1") != null) {
					dojo.html.hide(dojo.byId("campaign_submit_1"));
				}
			</script>
		</s:iterator>
    </s:if>
	<s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
		<s:iterator value="task.transitions" status="status">
			<s:submit id="normal_submit_%{#status.index}" value="%{top}" type="button"/>
			<script type="text/javascript">
				dojo.addOnLoad(function() {
					dojo.connect(dojo.byId("normal_submit_<s:property value='#status.index'/>"), "onclick", function() {
						dojo.byId('transitionField').value="<s:property value='top'/>";
						return;
					});
				});
				if (dojo.byId("normal_submit_1") != null) {
					dojo.html.hide(dojo.byId("normal_submit_1"));
				}
			</script>
		</s:iterator>
	</s:elseif>
	<s:else>
		<s:iterator value="task.transitions" status="status">
			<s:submit id="part_submit_%{#status.index}" value="%{top}" type="button" action="parts_claim_submit"/>
			<script type="text/javascript">
				dojo.addOnLoad(function() {
					dojo.connect(dojo.byId("part_submit_<s:property value='#status.index'/>"), "onclick", function() {
						dojo.byId('transitionField').value="<s:property value='top'/>";
						return;
					});
				});
				if(dojo.byId("part_submit_1") != null) {
					dojo.html.hide(dojo.byId("part_submit_1"));
				}
			</script>
		</s:iterator>
	</s:else>
</div>