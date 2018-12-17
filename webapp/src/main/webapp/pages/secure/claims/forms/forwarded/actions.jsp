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
			<s:text name="title.attributes.claimState" />:
		</td>
		<td width="75%" class="labelNormalTop">
			<s:property value="task.claim.state.state"/>						
		</td>
	</tr>
	<s:iterator value="reasonsList" status="rowCounter">
		<tr>
			<s:if test="#rowCounter.count==1">
				<td width="25%" nowrap="nowrap" class="labelStyle">
					<s:text name='%{claimState}'></s:text>:
				</td>
			</s:if>
			<s:else>
				<td width="25%" nowrap="nowrap" class="labelStyle">
					&nbsp;
				</td>
			</s:else>				
			<td width="75%" class="labelNormalTop">
				<s:property/>				
			</td>
		</tr>			
	</s:iterator>
	<tr>
		<td width="25%" nowrap="nowrap" class="labelStyle">
			<s:text name="label.newClaim.processorComments"/>:
		</td>
		<td width="75%" class="label">
			<s:property value="task.claim.externalComment"/>
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
<authz:ifDealer>
 <s:if test="legalDisclaimerAllowed">
    <div >
    	<div id="disclaimer" class="mainTitle" style="margin:10px 0px 5px 0px">
			 <s:text name="title.viewClaim.legalDisclaimer"/>
			 </div>
			 <div class="borderTable">&nbsp;</div>
            <table border ="0">
            	<tr>
            		<td  align="left" width="60%"><center>
            			<s:text name="message.legalDisclaimer">
            			<s:param ><s:property value="getLoggedInUser().getCompleteName()"/></s:param>
            			</s:text>
            			</center>
            		</td>
            	</tr>
            	<br/>
            	<tr>
            		<td align="center" width="80%">            		
            		<input type="radio" name="checkAcceptance" id="actionDecidedAccept"  />
            		<s:text name="legal.disclaimer.accept"/>					
					<input type="radio" name ="checkAcceptance" id="actionDecidedReject" checked="checked"/>
					<s:text name="legal.disclaimer.reject"/>		
            		</td>             		           		  
            	</tr>
            </table>
        
        <script type="text/javascript">
       
        </script>
    </div>
      </s:if>    
    </authz:ifDealer>
<div class="buttonWrapperPrimary">
	<s:hidden name="task.takenTransition" id="transitionField"/>
	<s:if test="task.claim.type.type == 'Campaign'">
    <s:iterator value="task.transitions" status="status">
    	<s:if test = "!top.equals('DenyOnNoReply')" >
	     	<s:submit id="campaign_submit_%{#status.index}" value="%{top}" type="button" action="campaign_claim_submit"/>
				<script type="text/javascript">
					dojo.addOnLoad(function() {
						dojo.connect(dojo.byId("campaign_submit_<s:property value='#status.index'/>"), "onclick", function() {
							dojo.byId('transitionField').value="<s:property value='top'/>";
							return;
						});
					});
				</script>
		 </s:if>	
		</s:iterator>
    </s:if>
	<s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
		<s:iterator value="task.transitions" status="status">
		<s:if test = "!top.equals('DenyOnNoReply')" >
			<s:submit id="normal_submit_%{#status.index}" value="%{top}" type="button"/>
			<script type="text/javascript">
                dojo.addOnLoad(function() {
                    dojo.connect(dojo.byId("normal_submit_<s:property value='#status.index'/>"), "onclick", function() {
                        dojo.byId('transitionField').value = "<s:property value='top'/>";
                        return;
                    });
                });
                dojo.addOnLoad(function() {
                	<s:if test="legalDisclaimerAllowed">
                    	dojo.html.hide(dojo.byId("normal_submit_<s:property value='#status.index'/>"));
                    </s:if>
                    if (dojo.byId("actionDecidedAccept")) {
                        dojo.connect(dojo.byId("actionDecidedAccept"), "onclick", function() {
                            if (dojo.byId("actionDecidedAccept").checked) {
                                dojo.html.show(dojo.byId("normal_submit_<s:property value='#status.index'/>"));
                            }
                        });
                    }
                    if (dojo.byId("actionDecidedReject")) {
                        dojo.connect(dojo.byId("actionDecidedReject"), "onclick", function() {
                            if (dojo.byId("actionDecidedReject").checked) {
                                dojo.html.hide(dojo.byId("normal_submit_<s:property value='#status.index'/>"));
                            }
                        });
                    }
                });
			</script>
			</s:if>
		</s:iterator>
	</s:elseif>
	<s:else>
		<s:iterator value="task.transitions" status="status">
			<s:if test = "!top.equals('DenyOnNoReply')" >
				<s:submit id="part_submit_%{#status.index}" value="%{top}" type="button" action="parts_claim_submit"/>
				<script type="text/javascript">
					dojo.addOnLoad(function() {
						dojo.connect(dojo.byId("part_submit_<s:property value='#status.index'/>"), "onclick", function() {
							dojo.byId('transitionField').value="<s:property value='top'/>";
							return;
						});
					});
				</script>
			</s:if>
		</s:iterator>
	</s:else>
</div>
