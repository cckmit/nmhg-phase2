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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@page pageEncoding="UTF-8" %>
<html>
<head>
    <title><s:text name="title.newClaim.campaignClaim"/></title>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <script type="text/javascript">
      dojo.require("dijit.layout.ContentPane");
    </script>


    <script type="text/javascript" src="scripts/ClaimForm.js"></script>
    <script type="text/javascript">
    dojo.require("dojox.layout.ContentPane");
    dojo.require("dijit.layout.LayoutContainer");
    </script>

    <style type="text/css">
        table.form {
            margin-bottom: 5px;
            border: 1px solid #EFEBF7;
			background-color:#F3FBFE;
			margin-left:5px;
			width:99%;
        }

        label {
            color: #000000;
            font-weight: 400;
        }
    </style>
</head>
	
<u:body>


	
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow:auto" >
<div dojoType="dojox.layout.ContentPane" layoutAlign="client">
<s:if test="!hasActionMessages()">
<u:actionResults/>
</s:if>
	<h3><s:text name="label.viewClaim.page1of2"/></h3>
	<s:form method="post" theme="twms" validate="true" id="campaignForm"
		name="saveCampaignClaimDraft" action="saveCampaignClaimDraft.action">		
		<s:hidden name="claim.type.type" value="Campaign"/>	
		<s:hidden name="dealerNumberSelected" id="isDealerNumber"/>
		<s:hidden name="fromPendingCampaign" id="true"/>
		<s:if test = "selectedBusinessUnit == null || selectedBusinessUnit.trim().length() == 0">
			<s:if test="fromPendingCampaign && (currentBusinessUnit == null || 
										currentBusinessUnit.name == null )">
				<s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit" value="%{campaign.businessUnitInfo.name}"></s:hidden>
			</s:if>
			<s:else>
				<s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit" value = "%{currentBusinessUnit.name}"/>
			</s:else>
		</s:if>
		<s:else>
			<s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit"/>
		</s:else>
	<div style="border:1px solid #EFEBF7;background:#F3FBFE;margin:5px">
		<table class="form" cellpadding="0" cellspacing="0" style="border:none">
			<tr><td style="padding:0;margin:0" colspan="4">&nbsp;</td></tr>
			<tr>
	   			<td width="26%">
	               <label for="BU" class="labelStyle"> <s:text name="label.common.businessUnit"/>:</label>
	            </td>
	            <td colspan="3" style="padding-left:5px;">
					<s:if test = "selectedBusinessUnit == null || selectedBusinessUnit.trim().length() == 0">
						<s:if test="fromPendingCampaign && (currentBusinessUnit == null || 
													currentBusinessUnit.name == null )">							
							<s:property value="campaign.businessUnitInfo.name"/>							
						</s:if>
						<s:else>
						    <s:property value="currentBusinessUnit.name"/>							
						</s:else>
					</s:if>
					<s:else>
						<s:property value="selectedBusinessUnit"/>							
					</s:else>
	            </td>
   		    </tr>
			<tr>
				<td class="labelStyle" width="26%" nowrap="nowrap">
					<s:if test="isLoggedInUserADealer()">
                		<label for="dealer" class="labelStyle"><s:text name="label.common.dealer"/>:</label>
               		</s:if>
               		<s:else>
               		<s:if test="campaign!=null && (campaign.id!=null ||(campaign.id!=null &&
					(fromPendingCampaign.equals(true) || campaign.claim!=null)))  && (claim==null || claim.itemReference.referredInventoryItem==null)">	
					   		<label for="dealer" class="labelStyle"><s:text name="label.common.dealer"/>:</label>
					   </s:if>
					<s:else>
                		<div id="dealerNameLabel" class="labelStyle">
                    		<s:text name="label.common.dealerName"/>:
                 		</div>
                 		<s:if test="claim == null || claim.id == null">
                  			<div id="dealerNumberLabel" class="labelStyle">
                     			<s:text name="label.common.dealerNumber"/>:
                 			</div> 
                 			<div id="toggle" style="cursor:pointer;">
                     			<div id="toggleToDealerNumber" class="clickable">
                         			<s:text name="toggle.common.toDealerNumber" />
                     			</div>
                     			<div id="toggleToDealerName" class="clickable">
                        			<s:text name="toggle.common.toDealerName"/>
                     			</div>
                 			</div>
						</s:if>   
						</s:else>
                	</s:else>
				</td>
				<td style="text-transform:uppercase" width="35%">
                <s:if test="claim == null || claim.id == null">                               	
                    <s:if test="isLoggedInUserADealer()">
                        <s:property value="getLoggedInUsersDealership().name"/>
                        <s:hidden name="dealer" value="%{loggedInUsersDealership}"/>
                        <s:hidden name="claim.forDealer" value="%{loggedInUsersDealership.id}"/>
                        <s:hidden name="claim.filedBy" value="%{loggedInUsersDealership}"/>
                    </s:if>
                    <s:elseif test="(campaign.id!=null ||(campaign.id!=null &&
					(fromPendingCampaign.equals(true) || campaign.claim!=null))) && (claim==null || claim.itemReference.referredInventoryItem==null)">
						<s:property value="campaignNotification.item.currentOwner.name"/>
                        <s:hidden name="dealer" value="%{campaignNotification.item.currentOwner}"/>
                        <s:hidden name="claim.forDealer" value="%{campaignNotification.item.currentOwner.id}"/>
                        <s:hidden name="claim.filedBy" value="%{campaignNotification.item.currentOwner}"/>
						</s:elseif>
                    <s:else>
                    <div id="dealerName">
                        <sd:autocompleter id='dealerNameAutoComplete' href='list_claim_dealers.action' name='claim.forDealer' value='%{claim.forDealer.name}' keyName="claim.forDealer" key="%{claim.forDealer.id}" keyValue="%{claim.forDealer.id}" cssStyle='width:145px;' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicator' />
                        <img style="display: none;" id="indicator" class="indicator"
                            src="image/indicator.gif" alt="Loading..."/>
                            <script type="text/javascript">
					        dojo.addOnLoad(function() {
                                var dealerSelect = dijit.byId("dealerNameAutoComplete");
                                dealerSelect.sendDisplayedValueOnChange = false;                                
                                });
							});
    					</script> 
    				</div>
    				<div id="dealerNumber">
    				    <sd:autocompleter id='dealerNumberAutoComplete' href='list_claim_dealer_numbers.action' name='claim.forDealer' keyName="claim.forDealer" key="%{claim.forDealer.id}" keyValue="%{claim.forDealer.id}" value='%{claim.forDealer.serviceProviderNumber}' cssStyle='width:145px;' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' indicator='indicator' />
                        <img style="display: none;" id="indicator" class="indicator"
                            src="image/indicator.gif" alt="Loading..."/>
                            <script type="text/javascript">
					        dojo.addOnLoad(function() {
                                var dealerSelect = dijit.byId("dealerNumberAutoComplete");
                                dealerSelect.sendDisplayedValueOnChange = false;
                                });
							});
    					</script> 
    				</div> 
    				<script type="text/javascript">
					    dojo.addOnLoad(function() {
					        <s:if test="dealerNumberSelected">
					          showDealerNumber();
					      </s:if>
					      <s:else>
					          showDealerName();
					      </s:else>
					
					        dojo.connect(dojo.byId("toggleToDealerName"), "onclick", function() {
					            dijit.byId('dealerNumberAutoComplete').setValue("");
					            showDealerName();
					        });
					        dojo.connect(dojo.byId("toggleToDealerNumber"), "onclick", function() {
					            dijit.byId('dealerNameAutoComplete').setValue("");
					            showDealerNumber();
					        });
					    });
					</script>				
                    </s:else>
                </s:if>
                <s:else>
                    <s:hidden name="claim.forDealer" value="%{claim.forDealer.id}" id="dealer"/>
                    <s:hidden name="claim.filedBy"/>
                    <s:property value="claim.forDealer.name"/>
                </s:else>
           	  </td>
				<td width="15%"  style="width: 230px;"><label for="type" class="labelStyle"><s:text name="label.common.claimType"/>:</label></td>
				<td style="padding-top:5px;" valign="top">
				<s:select id="type" name="claimType" list="claimTypes" cssStyle="width:145px;"
					listKey="type" listValue="%{getText(displayType)}" disabled="%{claim.id != null}" />	
					  				   	
                <script type="text/javascript">                
                dojo.addOnLoad(function() {                	
                    var form1 = dojo.byId("campaignForm");
                    dojo.connect(dijit.byId("type"),"onChange",function(){
                    	var claimType=dijit.byId("type").getValue();                    	
                    	if(claimType == "Parts" || claimType == "Machine" || value == "Attachment"){
                    		form1.action = "chooseClaimTypeAndDealer.action";                             
                            form1.submit();
                    	}
                    });
					var fromPendingCampaign='<s:property value="fromPendingCampaign"/>';
                	if(fromPendingCampaign=="true"){
                		dijit.byId("type").setDisabled(true);
                	}                  
                });
				</script></td>
			</tr>
			<tr><td style="padding:0;margin:0" colspan="4">&nbsp;</td></tr>
		</table>

		<jsp:include flush="true" page="../forms/common/write/campaign_header.jsp" />
	</div>
		<div class="spacer5"></div>
		<table align="center" border="0" cellpadding="0" cellspacing="0" class="buttons">
			<tbody>
				<tr>
                <td align="center">
                    <s:submit value="%{getText('button.common.continue')}" onclick="this.disabled=true; this.form.submit();"/>
                </td>
            </tr>
			</tbody>
		</table>
		<s:hidden name="pageOne" value="true" />
	</s:form>
</div>
</div>
</u:body>
</html>