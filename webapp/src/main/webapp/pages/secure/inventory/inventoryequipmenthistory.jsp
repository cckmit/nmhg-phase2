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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%@ taglib prefix="tda" uri="twmsDomainAware" %>



<html>
<head>
<u:stylePicker fileName="yui/reset.css" common="true"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">


<script type="text/javascript" src="scripts/jscalendar/calendar.js"></script>
<script type="text/javascript"
	src="scripts/jscalendar/lang/calendar-en.js"></script>
<script type="text/javascript"
	src="scripts/jscalendar/calendar-setup.js"></script>
<link href="scripts/jscalendar/calendar-brown.css" rel="stylesheet"
	type="text/css">
<script type="text/javascript" src="scripts/domainUtility.js"></script>
<script type="text/javascript">
        dojo.require("twms.widget.TitlePane");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.TabContainer");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.form.Button");
        dojo.require("dojox.layout.ContentPane");
        dojo.require("twms.widget.Dialog");
    </script>
<%@ include file="/i18N_javascript_vars.jsp"%>
<u:stylePicker fileName="inboxLikeButton.css" />
<u:stylePicker fileName="base.css" />
<title><s:text name="FIXME" /></title>
	<style type="text/css">
		body {
			overflow: hidden;
		}
	</style>    
    
</head>
<u:body>

	<s:hidden name="selectedBusinessUnit"
		value="%{inventoryItem.businessUnitInfo.name}" />


	<div dojoType="dijit.layout.LayoutContainer"
		style="width: 100%; height: 100%; " id="baseDiv">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="top" id="buttonsDiv"><authz:ifUserInRole
		roles="inventoryAdmin,dealerSalesAdministration,dealer">
		<s:if test="isStockInventory() || !inventoryItem.warranty.draft">
			<s:if test="!inventoryItem.pendingWarranty">
				<s:if test="isStockInventory() && wntyRegAllowed">
				
					<div class="inboxLikeButtonWrapper" style="float: left;">
					<button dojoType="dijit.form.Button" id="actionButton"
						activeImg="../../../image/inbox_button/twmsActive-"
						inactiveImg="../../../image/inbox_button/twmsButton-"
						pressedImg="../../../image/inbox_button/twmsPressed-"
						disabledImg="../../../image/inbox_button/twmsDisabled-">
                            <s:text name="inventory_equipment_history_jsp.buttonLabel.new_warranty_registration" />
					</button>
					</div>
				</s:if>
				<s:elseif test="isRetailInventory() && (isInternalUser() || isLoggedInDealerCurrentOwnerOrParentDealer() ||isLoggedInDealerShipToDealer())">
					<div class="inboxLikeButtonWrapper" style="float: left;">
					<button dojoType="dijit.form.Button" id="actionButton">
                            <s:text name="inventory_equipment_history_jsp.buttonLabel.warranty_transfer" />
					</button>
					</div>
				</s:elseif>
				<script type="text/javascript">
                dojo.addOnLoad(function() {
                    var actionButton = dijit.byId("actionButton");
                    if(actionButton)
                    {
	                    <s:if test="stockInventory">                    
	                        actionButton.onClick = function() {
	                            launchNewWarranyRegistration("<s:property value="id"/>",getMyTabLabel(),"<s:property value="inventoryItem.businessUnitInfo.name"/>");
	                        };
	                    </s:if>
	                    <s:else>
	                        actionButton.onClick = function() {
	                            launchEquipmentTransfer("<s:property value="id"/>",getMyTabLabel(),"<s:property value="inventoryItem.businessUnitInfo.name"/>");
	                        };
	                    </s:else>
                    }
                });
            </script>
			</s:if>
		</s:if>
	</authz:ifUserInRole> <s:if test="retailInventory">
		<authz:ifUserInRole roles="inventoryAdmin,dealer,enterpriseDealership">
				<s:if test="!isScrap()  && !inventoryItem.pendingWarranty &&
                    ((dealerEligibleToPerformRMT && loggedInUserADealer &&
                      (getLoggedInUsersDealership().getId().longValue() == inventoryItem
						.getCurrentOwner().getId().longValue() || isLoggedInDealerParentDealer() ||isLoggedInDealerShipToDealer())) ||
                    (loggedInUserAnInvAdmin || isEnterpriseDealer()))">
				<div class="inboxLikeButtonWrapper" style="float: left">
				<button dojoType="dijit.form.Button" id="RMTButton">
				<s:text name="summaryTable.inboxButton.retail_machine_transfer" />
				</button>
				</div>
			</s:if>
		</authz:ifUserInRole>
		<authz:ifUserInRole roles="inventoryAdmin,inventorylisting">
			<s:if test="eligibleForExtendedWarrantyPurchase && !isIri_StockInventory()  && (isInternalUser() || isLoggedinDealerOwner() || isLoggedInDealerParentDealer() ||isLoggedInDealerShipToDealer())">
				<div class="inboxLikeButtonWrapper" style="float: left">
				<button dojoType="dijit.form.Button" id="extendedWarrantyPurchase">
				<s:text name="summaryTable.inboxButton.purchase_warranty" />
				</button>
				</div>
			</s:if>
		</authz:ifUserInRole>
	</s:if> <s:if test="isScrap()">
		<authz:ifUserInRole roles="inventoryAdmin">
			<div class="inboxLikeButtonWrapper" style="float: left">
			<button dojoType="dijit.form.Button" id="unScrapInventoryButton">
			<s:text name="button.unScrap.markAsUnScrap" />
			</button>
			</div>
		</authz:ifUserInRole>
	</s:if> <s:if test="!isScrap() ">
		<authz:ifUserInRole roles="inventoryAdmin">
			<div class="inboxLikeButtonWrapper" style="float: left">
			<button dojoType="dijit.form.Button" id="scrapInventoryButton">
                <s:text name="button.scrap.markAsScrap" />
			</button>
			</div>

		</authz:ifUserInRole>
	</s:if>
	<s:if test="isStolen()">
		<authz:ifUserInRole roles="inventoryAdmin">
			<div class="inboxLikeButtonWrapper" style="float: left">
			<button dojoType="dijit.form.Button" id="unStolenInventoryButton">
			<s:text name="button.unStolen.markAsUnStolen" />
			</button>
			</div>
		</authz:ifUserInRole>
	</s:if> <s:if test="!isStolen()">
		<authz:ifUserInRole roles="inventoryAdmin">
			<div class="inboxLikeButtonWrapper" style="float: left">
			<button dojoType="dijit.form.Button" id="stolenInventoryButton">
                <s:text name="button.stolen.markAsStolen" />
			</button>
			</div>

		</authz:ifUserInRole>
	</s:if>  
	<authz:ifUserInRole roles="dealerWarrantyAdmin,processor">
		<s:if test="claimSubmissionAllowed && isStockClaimAllowed() && isStockInventory() ">
			<div class="inboxLikeButtonWrapper" style="float: left">
			<button dojoType="dijit.form.Button" id="CreateClaimButton">
			<s:text name="button.newClaim.createClaim" />
			</button>
			</div>
		</s:if>
		<s:elseif test="claimSubmissionAllowed && isRetailInventory() && (isInternalUser() || isLoggedinDealerOwner() || isLoggedInDealerParentDealer() ||isLoggedInDealerShipToDealer() || isCreateClaimAllowed())">
	 		<div class="inboxLikeButtonWrapper" style="float: left">
			<button dojoType="dijit.form.Button" id="CreateClaimButton">
			<s:text name="button.newClaim.createClaim" />
			</button>
			</div>
	 		</s:elseif>
		</authz:ifUserInRole>
	 <script type="text/javascript">
	            dojo.addOnLoad(function() {
	               if(dijit.byId("scrapInventoryButton"))
	               {
		               dijit.byId("scrapInventoryButton").onClick = function(){
		                	var thisTabLabel = getMyTabLabel();
					        var url = "inventoryScrap.action?inventoryItem=<s:property value='inventoryItem.id'/>" ;
		                    parent.publishEvent("/tab/open", {label: i18N.scrap_inventory, 
		            								         url: url, 
		            								         decendentOf: thisTabLabel,
		            								         forceNewTab: true }); 
		          	    }	
                   }	    
	               if(dijit.byId("stolenInventoryButton"))
	               {
		               dijit.byId("stolenInventoryButton").onClick = function(){
		                	var thisTabLabel = getMyTabLabel();
					        var url = "inventoryStolen.action?inventoryItem=<s:property value='inventoryItem.id'/>" ;
		                    parent.publishEvent("/tab/open", {label: i18N.stolen_inventory, 
		            								         url: url, 
		            								         decendentOf: thisTabLabel,
		            								         forceNewTab: true }); 
		          	    }	
                   }
	               
	               if(dijit.byId("extendedWarrantyPurchase"))
	          	     {
		          	     dijit.byId("extendedWarrantyPurchase").onClick = function(){
		                	var thisTabLabel = getMyTabLabel();
					        var url = "show_extended_warrantyplans.action?inventoryItems=<s:property value='inventoryItem.id'/>&inventoryItems=" ;
		                    parent.publishEvent("/tab/open", {label: i18N.extended_warranty_purchase, 
		            								         url: url, 
		            								         decendentOf: thisTabLabel,
		            								         forceNewTab: true }); 
		          	    }  
	          	    }
	          	    if(dijit.byId("RMTButton"))
	          	    {
		          	    dijit.byId("RMTButton").onClick = function(){
		                	var thisTabLabel = getMyTabLabel();
		                	var tabLabel = "Retail Machine Transfer";
					        var url = "show_retail_machine_transfer.action?inventoryItems=<s:property value='inventoryItem.id'/>";
		                    parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : thisTabLabel});		
								delete url, tabLabel;
		          	    }
	          	    }
	          	    
	          	    if(dijit.byId("CreateClaimButton"))
	          	    {
	          	         dijit.byId("CreateClaimButton").onClick = function createClaim(event, dataId){
		                	var thisTabLabel = getMyTabLabel();
		                	var tabLabel = "Create Claim";		                		                	
		                	var url = "chooseClaimTypeFromInventoryInbox.action?claim.itemReference.referredInventoryItem=<s:property value='inventoryItem.id'/>" +
		                	"&inventoryItem=<s:property value='inventoryItem.id'/>" +
					                "&selectedBusinessUnit=<s:property value='inventoryItem.businessUnitInfo'/>&claim.forDealer=<s:property value='%{@tavant.twms.web.common.SessionUtil@getDealerFromSession(#session).name}'/>";
		                    parent.publishEvent("/tab/open", {label: tabLabel, url: url, decendentOf : thisTabLabel});		
								delete url, tabLabel;
		          	    }
	          	    }
	          	    	          	    
	          	    if( dijit.byId("unScrapInventoryButton"))
	          	    {
                          dojo.connect(dojo.byId("unScrapInventoryButton"), "onclick", function(event){
                          var thisTabLabel = getMyTabLabel();
                          console.debug = (thisTabLabel);
                          var url = "inventoryUnScrap.action?inventoryItem=<s:property value='inventoryItem.id'/>" ;
                          parent.publishEvent("/tab/open", {label: i18N.unScrap_inventory,
                              url: url,
                              decendentOf: thisTabLabel,
                              forceNewTab: true });
                      }  );

                      }
	          	  if( dijit.byId("unStolenInventoryButton"))
	          	    {
                        dojo.connect(dojo.byId("unStolenInventoryButton"), "onclick", function(event){
                        var thisTabLabel = getMyTabLabel();
                        console.debug = (thisTabLabel);
                        var url = "inventoryUnStolen.action?inventoryItem=<s:property value='inventoryItem.id'/>" ;
                        parent.publishEvent("/tab/open", {label: i18N.unStolen_inventory,
                            url: url,
                            decendentOf: thisTabLabel,
                            forceNewTab: true });
                    }  );

                    }

                });
	            


        </script> <s:if test="isScrap()== false">
		<s:if test="stockInventory ">
			<authz:ifUserInRole roles="inventoryAdmin">
				<s:if test="isLoggedinDealerOwner()|| isInternalUser()|| isEnterpriseDealer()">		
					<div class="inboxLikeButtonWrapper" style="float: left">
					<button dojoType="dijit.form.Button" id="dealertodealer">
					<s:text name="title.dealertodealer.dealertodealertransfer" />
					</button>
					</div>
				</s:if>
			</authz:ifUserInRole>
			<authz:ifUserInRole roles="inventorylisting">
			<authz:ifUserNotInRole roles="inventoryAdmin">
			<s:if test="isLoggedInDealerCurrentOwnerOrParentDealer()|| isInternalUser()|| isEnterpriseDealer() || isLoggedInDealerShipToDealer() ">	
			<s:if test="isD2DAllowed()">			
			<div class="inboxLikeButtonWrapper" style="float: left">
					<button dojoType="dijit.form.Button" id="dealertodealer">
					<s:text name="title.dealertodealer.dealertodealertransfer" />
					</button>
					</div>
			</s:if>
			</s:if>
			</authz:ifUserNotInRole>
			</authz:ifUserInRole>
		</s:if>
		<script type="text/javascript">
	            dojo.addOnLoad(function() {
	              if( dijit.byId("unScrapInventoryButton"))
	          	    {
	                	dijit.byId("unScrapInventoryButton").onClick = function(){
	                	var thisTabLabel = getMyTabLabel();
				        var url = "inventoryUnScrap.action?inventoryItem=<s:property value='inventoryItem.id'/>" ;
	                    parent.publishEvent("/tab/open", {label: i18N.unScrap_inventory, 
	            								         url: url, 
	            								         decendentOf: thisTabLabel,
	            								         forceNewTab: true }); 
	          	    	}   
	          	    }  
	              if( dijit.byId("unStolenInventoryButton"))
	          	    {
	                	dijit.byId("unStolenInventoryButton").onClick = function(){
	                	var thisTabLabel = getMyTabLabel();
				        var url = "inventoryUnStolen.action?inventoryItem=<s:property value='inventoryItem.id'/>" ;
	                    parent.publishEvent("/tab/open", {label: i18N.unStolen_inventory, 
	            								         url: url, 
	            								         decendentOf: thisTabLabel,
	            								         forceNewTab: true }); 
	          	    	}   
	          	    }  
	          	    
	          	    if(dijit.byId("dealertodealer")){
		          	    dijit.byId("dealertodealer").onClick = function(){
		                	var thisTabLabel = getMyTabLabel();
					        var url = "show_D2D_transfer_inventory.action?inventoryItem=<s:property value='inventoryItem.id'/>" ;
		                    parent.publishEvent("/tab/open", {label: i18N.dealer_to_dealer, 
		            								         url: url, 
		            								         decendentOf: thisTabLabel,
		            								         forceNewTab: true }); 
		          	    } 
	          	    } 
	            });
	        </script>
	</s:if> <s:if test="reportAvailable">
		<div class="inboxLikeButtonWrapper" style="float: left">
		<button dojoType="dijit.form.Button" id="reportButton">
		<div class="inboxLikeButtonWithoutPadding"><span
			class="inboxLikeButtonText"> <s:text
			name="summaryTable.inboxButton.reports" /> </span></div>
		</button>
		</div>
		<script type="text/javascript">
	            dojo.addOnLoad(function() {
	          	    if(dijit.byId("reportButton")){
		          	    dijit.byId("reportButton").onClick = function(){
		                	var thisTabLabel = getMyTabLabel();
					        var url = "show_reports.action?inventoryItem=<s:property value='inventoryItem.id'/>" ;
		                    parent.publishEvent("/tab/open", {label: "Report",
		            								         url: url,
		            								         decendentOf: thisTabLabel,
		            								         forceNewTab: true });
		          	    }
	          	    }
	            });
	        </script>
	</s:if></div>
	 <jsp:include page="../claims/forms/common/write/fileUploadDialog.jsp"/>
                
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client"
		style="border-top: 1px solid #D1D1D1;"><u:actionResults
		wipeMessages="false" /> <authz:ifUserNotInRole roles="supplier">

		<!--  Inserted for Major Components -->

		<div>
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="title.common.equipmenInfo" />"
			labelNodeClass="section_header"><jsp:include flush="true"
			page="inventory_equipmentinfo.jsp" /></div>
		</div>

		<div>
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="Major Components" />"
			labelNodeClass="section_header"><jsp:include flush="true"
			page="inventory_majorcomponents.jsp" /></div>
		</div>
		
		
		<div>		
		<s:if test="buConfigAMER">
			<div dojoType="twms.widget.TitlePane"
			title="<s:text name="Additional Components Installed" />"
			labelNodeClass="section_header"><jsp:include flush="true"
			page="inventory_additionalcomponents.jsp" /></div>
			</s:if>
		</div>
		<div>
	
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="title.common.transactionHistory" />"
			labelNodeClass="section_header"><jsp:include flush="true"
			page="inventory_transactionhistory.jsp" /></div>
		</div>
		
		<div>
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.inventory.options" />"
				labelNodeClass="section_header"><jsp:include flush="true"
				page="inventory_options.jsp" />
			</div>
		</div>
		
		<div>
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.inventory.partgroups" />"
				labelNodeClass="section_header"><jsp:include flush="true"
				page="inventory_part_groups.jsp" />
			</div>
		</div>

		<s:if test="%{isEverScrapped()}">
			<div>
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="title.common.scrapHistory" />"
				labelNodeClass="section_header"><jsp:include flush="true"
				page="scrap_details.jsp" /></div>
			</div>
		</s:if>

		<s:if test="%{isEverStolen()}">
			<div>
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="title.common.stolenHistory" />"
				labelNodeClass="section_header"><jsp:include flush="true"
				page="stolen_details.jsp" /></div>
			</div>
		</s:if>
		<s:if test="isInternalUser()">
			<div>
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="title.viewClaim.RecoveryClaimHistory" />"
				labelNodeClass="section_header"><jsp:include flush="true"
				page="../supplier/detail/inventory_recovery_claim_history.jsp" /></div>
			</div>
		</s:if>

		<div>
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="title.common.claimHistory" />"
			labelNodeClass="section_header"><jsp:include flush="true"
			page="inventory_warrantyclaimhistroy.jsp" /></div>
		</div>

		<div>
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="title.common.fieldModification" />"
			labelNodeClass="section_header"><jsp:include flush="true"
			page="inventory_campaigns.jsp" /></div>
		</div>

	</authz:ifUserNotInRole> <authz:ifUserInRole roles="supplier">


		<div>
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="title.common.equipmenInfo" />"
			labelNodeClass="section_header"><jsp:include flush="true"
			page="inventory_equipmentinfo.jsp" /></div>
		</div>
		
		<!-- Inserted for Major Components  -->

		<div>
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="Major Components" />"
			labelNodeClass="section_header"><jsp:include flush="true"
			page="inventory_majorcomponents.jsp" /></div>
		</div>

		<div>
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="title.common.transactionHistory" />"
			labelNodeClass="section_header"><jsp:include flush="true"
			page="inventory_transactionhistory.jsp" /></div>
		</div>

		<s:if test="%{isEverScrapped()}">
			<div>
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="title.common.scrapHistory" />"
				labelNodeClass="section_header"><jsp:include flush="true"
				page="scrap_details.jsp" /></div>
			</div>
		</s:if>
		<s:if test="%{isEverStolen()}">
			<div>
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="title.common.stolenHistory" />"
				labelNodeClass="section_header"><jsp:include flush="true"
				page="stolen_details.jsp" /></div>
			</div>
		</s:if>
		<div>
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="title.viewClaim.RecoveryClaimHistory" />"
			labelNodeClass="section_header"><jsp:include flush="true"
			page="../supplier/detail/recovery_claim_history.jsp" /></div>
		</div>
	</authz:ifUserInRole>
	<s:if test="isRetailInventory() && invisiblePolicies.size()>0 && isInternalUser()">
	<div dojoType="twms.widget.TitlePane"
			title="<s:text name="label.common.invisiblePolicies" />"
			labelNodeClass="section_header">
            <div style="width:100%; padding-bottom:10px;">
            <table width="100%" cellpadding="0" cellspacing="0" class="grid borderForTable">
		<thead>
								<tr>

									<th width="33%" class="warColHeader"><s:text
											name="label.warrantyAdmin.policyCode" /></th>
									<th width="33%" class="warColHeader"><s:text
											name="label.warrantyAdmin.policyName" /></th>
									<th width="33%" class="warColHeader"><s:text
											name="label.warrantyAdmin.type" /></th>

								</tr>
							</thead>
		
            <s:iterator value="invisiblePolicies" status = "invisiblePolicies">
							<tbody id="policy_list_invisible">
								<tr>
									<td width="33%"><s:property
													value="code" /></td>
									<td width="33%"><s:property value="description" /></td>
									<td width="33%"><s:property value="%{getText(warrantyType.displayValue)}" /></td>
								</tr>
							</tbody>

						</s:iterator></table>
            </div>
		</div>
	</s:if>	
	  <s:if test="isRetailInventory()">
		<div>
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="label.common.warrantyCoverages" />"
			labelNodeClass="section_header">
            <div style="width:100%; padding-bottom:10px;"><jsp:include flush="true" page="includedPolicies.jsp" /></div>
		</div>
		<div dojoType="twms.widget.Dialog" id="policy_audits"
			style="width:80%; overflow: hidden;"
			title="<s:text name="label.policyAudit.policyAuditHistory"/>" >
		<div id="policy_audits_div" dojoType="dojox.layout.ContentPane"
			executeScripts="true" style="padding-bottom: 3px;"></div>
		</div>


		<div dojoType="twms.widget.Dialog" id="policy_details"
			style="width: 80%; overflow: hidden"
			title="<s:text name="label.managePolicy.policyDetails" />"
			bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250">
		<div id="policy_details_div" dojoType="dojox.layout.ContentPane"
			executeScripts="true"
			style="padding-bottom: 3px; overflow: auto; width: 100%; height: 336px"></div>
		</div>
		
		
		<div id="dialogBoxContainer" style="display: none">
		<div dojoType="twms.widget.Dialog" id="addPoliciesDialog"
			bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250" style="height:90%;"><jsp:include
			flush="true" page="availablePolicies.jsp"></jsp:include></div>
		</div>
		</div>
	</s:if>
	
	<s:if test="isAttachmentEditable">
	<div dojoType="twms.widget.TitlePane" title="<s:text name="label.inventory.internalTruckRecords"/>" labelNodeClass="section_header">
            <jsp:include flush="true" page="inventory_upload_internalTruckDocuments.jsp"/>
        </div>
        </s:if>
       <s:else>
       <s:if test="isLoggedInUserAnInternalUser()">
     <authz:ifUserInRole roles="processor, dsm,recoveryProcessor">
     <div dojoType="twms.widget.TitlePane" title="<s:text name="label.inventory.internalTruckRecords"/>" labelNodeClass="section_header">
            <jsp:include flush="true" page="inventory_view_internalTruckDocuments.jsp"/>
        </div>
        </authz:ifUserInRole>
     </s:if>   
       </s:else> 
	<s:if test="isLoggedInUserAnInternalUser()">
		<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.inventory.NEMISComments" />"
				labelNodeClass="section_header">
			<div style="width:100%; padding-bottom:10px;"><jsp:include flush="true" page="truckComments.jsp" /></div>
		</div>
		
	</s:if>
	</div>
	</div>
</u:body>
<authz:ifPermitted resource="inventoryRetailedReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="inventoryRetailedReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseDiv')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseDiv'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="inventoryStockReadOnlyView">
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			document.getElementById("buttonsDiv").style.display="none";
		});
	</script>
</authz:ifPermitted>
<authz:ifPermitted resource="inventoryStockReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseDiv')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseDiv'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>
