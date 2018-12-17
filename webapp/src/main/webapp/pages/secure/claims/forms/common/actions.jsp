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
			<s:text name="label.newClaim.dsmComments"/>:
		</td>
		<td width="75%" class="label">
			<s:hidden name="commentsForDisplay" value="%{commentsForDisplay}"/>
			<s:property value="commentsForDisplay"/>
			<s:if test="task!=null">
					<s:hidden name="task.claim.decision" value="" />
			</s:if>
			<s:else>
					<s:hidden name="claim.decision" value="" />
			</s:else>			
		</td>
	</tr>
	<tr>
		<td width="25%" nowrap="nowrap" class="labelStyle">
			<s:text name="label.common.comments"/>:
		</td>
		<td width="75%" class="labelNormalTop">
			<t:textarea name="task.claim.externalComment" rows="4" cssStyle="width:70%" value=""></t:textarea>
			    <s:if test="task!=null">
					<s:hidden name="task.claim.internalComment" value=""/>	
				</s:if>
				<s:else>
					<s:hidden name="claim.internalComment" value=""/>	
				</s:else>
			
		</td>
	</tr>
</table>
<s:hidden name="mandatedComments[0]" value="externalComments"/>
<div id="separator"></div>
<div class="buttonWrapperPrimary">
	<s:hidden name="task.takenTransition" id="transitionField"/>
	<s:if test="task.claim.type.type == 'Campaign'">
    <s:iterator value="task.transitions" status="status">
     	<s:if test="top=='Re-requests for SMR'">
			<s:if test="showReRequestsForSMR">
                <s:submit id="campaign_submit_%{#status.index}" value="%{getText('label.jbpm.reRequestsForSMR')}" type="button" action="campaign_claim_submit" cssClass="buttonGeneric"/>
			</s:if>	
		</s:if>
		<s:elseif test="top=='Submit'">		
				<s:submit id="campaign_submit_%{#status.index}" value="%{getText('button.common.submit')}" type="button" action="campaign_claim_submit" cssClass="buttonGeneric"/>
		</s:elseif>
		<s:else>
		    <input type="button" class="buttonGeneric" id="campaign_submit_<s:property value='#status.index'/>"
					value="<s:property value='%{getText("viewInbox_jsp.inboxButton.delete")}'/>" />
		</s:else>
			
			<script type="text/javascript">
			 var confirmationDialog = null;
				dojo.addOnLoad(function() {
					<s:if test="top!='Delete'">		
					if(dojo.byId("campaign_submit_<s:property value='#status.index'/>")){
						dojo.connect(dojo.byId("campaign_submit_<s:property value='#status.index'/>"), "onclick", function() {							
							dojo.byId('transitionField').value="<s:property value='top'/>";
							return;
						});
					}
				  </s:if>
				  <s:else> 
				  	confirmationDialog = dijit.byId("deleteDialog");
				  	dojo.byId("dialogBoxContainer").style.display = "block";
				  	
				  	dojo.connect(dojo.byId("campaign_submit_<s:property value='#status.index'/>"), "onclick", function() {					  			  		  
				  			confirmationDialog.show();				  		
				    });
				  	dojo.connect(dojo.byId('confirmationDeleteButton'), "onclick", function(){
				  		dojo.byId('transitionField').value="<s:property value='top'/>";
				  		document.forms[0].action="campaign_claim_submit.action";
                        document.forms[0].submit();				  		
				  	});
				     	  
				  </s:else>
				});
			</script>		
		</s:iterator>
    </s:if>
	<s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
		<s:iterator value="task.transitions" status="status">
			<s:if test="top=='Re-requests for SMR'">
				<s:if test="showReRequestsForSMR">
					<s:submit id="normal_submit_%{#status.index}" value="%{getText('label.jbpm.reRequestsForSMR')}" type="button" cssClass="buttonGeneric"/>
				</s:if>	
			</s:if>
			<s:elseif test="top=='Submit'">		
				<s:submit id="normal_submit_%{#status.index}" value="%{getText('button.common.submit')}" type="button" cssClass="buttonGeneric"/>
			</s:elseif>
			<s:else>
			    <input type="button" class="buttonGeneric" id="normal_submit_<s:property value='#status.index'/>"
					value="<s:property value='%{getText("viewInbox_jsp.inboxButton.delete")}'/>" />
		    </s:else>
			
			<script type="text/javascript">
			 var confirmationDialog = null;
				dojo.addOnLoad(function() {
					<s:if test="top!='Delete'">				
					if(dojo.byId("normal_submit_<s:property value='#status.index'/>")){
						dojo.connect(dojo.byId("normal_submit_<s:property value='#status.index'/>"), "onclick", function() {							
							dojo.byId('transitionField').value="<s:property value='top'/>";
							return;
						});
					}
				  </s:if>
				  <s:else> 
				  	confirmationDialog = dijit.byId("deleteDialog");
				  	dojo.byId("dialogBoxContainer").style.display = "block";
				  	
				  	dojo.connect(dojo.byId("normal_submit_<s:property value='#status.index'/>"), "onclick", function() {					  			  		  
				  			confirmationDialog.show();				  		
				    });
				  	dojo.connect(dojo.byId('confirmationDeleteButton'), "onclick", function(){
				  		dojo.byId('transitionField').value="<s:property value='top'/>";
				  		var form =  document.getElementById('claim_form'); 
						form.submit();
				  	});
				     </s:else>
				});
			</script>
		</s:iterator>
	</s:elseif>
	<s:else>
		<s:iterator value="task.transitions" status="status">
			<s:if test="top=='Re-requests for SMR'">
				<s:if test="showReRequestsForSMR">
					<s:submit id="part_submit_%{#status.index}" value="%{getText('label.jbpm.reRequestsForSMR')}" type="button" action="parts_claim_submit" cssClass="buttonGeneric"/>
				</s:if>	
			</s:if>
			<s:elseif test="top=='Submit'">		
				<s:submit id="part_submit_%{#status.index}" value="%{getText('button.common.submit')}" type="button" action="parts_claim_submit" cssClass="buttonGeneric"/>
			</s:elseif>
			<s:else>
			    <input type="button" class="buttonGeneric" id="part_submit_<s:property value='#status.index'/>"
						value="<s:property value='%{getText("viewInbox_jsp.inboxButton.delete")}'/>" />
			</s:else>
			
			<script type="text/javascript">
			 var confirmationDialog = null;
				dojo.addOnLoad(function() {
					<s:if test="top!='Delete'">	
					if(dojo.byId("part_submit_<s:property value='#status.index'/>")){
						dojo.connect(dojo.byId("part_submit_<s:property value='#status.index'/>"), "onclick", function() {							
							dojo.byId('transitionField').value="<s:property value='top'/>";
							return;
						});
					}
				  </s:if>
				  <s:else> 
				  	confirmationDialog = dijit.byId("deleteDialog");
				  	dojo.byId("dialogBoxContainer").style.display = "block";
				  	
				  	dojo.connect(dojo.byId("part_submit_<s:property value='#status.index'/>"), "onclick", function() {					  			  		  
				  			confirmationDialog.show();				  		
				    });
				  	dojo.connect(dojo.byId('confirmationDeleteButton'), "onclick", function(){
				  		dojo.byId('transitionField').value="<s:property value='top'/>";
				  		document.forms[0].action="parts_claim_submit.action";
                        document.forms[0].submit();				  		
				  	});
				    </s:else>
				});
			</script>		
		</s:iterator>
	</s:else>
	
	 <div id="dialogBoxContainer" style="display: none">
		    <div dojoType="twms.widget.Dialog" id="deleteDialog" bgColor="white"
				bgOpacity="0.5" toggle="fade" toggleDuration="250" style="height: 120px; width: 310px; border: 1px solid #EFEBF7">
				<div class="dialogContent" dojoType="dijit.layout.LayoutContainer"
					style="height: 90px; width: 300px; ">
					<div dojoType="dijit.layout.ContentPane" layoutAlign="top" class="closableDialog" style="background: #F3FBFE;">
						<span class="TitleBar" style="font-size: 9pt; display: inline;background: #F3FBFE;"> 
						    <s:text name="label.delete.confirmation"></s:text>
						</span>
					</div>					
					<div dojoType="dijit.layout.ContentPane" layoutAlign="client" >
					    <br><s:text name="message.delete.confirmation" /> 
					    <br><br>
						<table width="60%" class="buttonWrapperPrimary">				
							<tr>
								<td align="center">
								    <input type="button" id="confirmationDeleteButton" name="Submit2"
										value="<s:text name="label.common.yes"/>" class="buttonGeneric" />				
								</td>
								<td align="center">
								    <input type="button" name="Submit3"	value="<s:text name="label.common.no"/>" class="buttonGeneric"
										onclick="confirmationDialog.hide();" />
								</td>
							</tr>
						</table>
					</div>
					
				</div>
			</div>
       </div>
</div>
