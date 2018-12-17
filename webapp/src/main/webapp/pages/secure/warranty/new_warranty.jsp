<%@ page contentType="text/html" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><s:text name="summaryTable.inboxButton.new_warranty_registration"/></title>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <s:head theme="twms"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="base.css"/>  
    <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
    <script type="text/javascript" src="scripts/WarrantyReg.js"></script>
    <script type="text/javascript" src="scripts/commonWarranty.js"></script>
   	<%@ include file="/i18N_javascript_vars.jsp" %>
    <script type="text/javascript" xml:space="preserve">
        dojo.require("twms.widget.Dialog");
           
        dojo.addOnLoad(function() {
            dojo.subscribe("/setPrePopulatedValues", null, function() {
               
                if (dojo.byId("searchDealerName")) {
                    dojo.byId("searchDealerName").value = prePopulatedDealerName;
                }
                if (dojo.byId("searchDealerNumber")) {
                    dojo.byId("searchDealerNumber").value = prePopulatedDealerNumber;
                }
            });       
        });
    </script>
</head>
<body onkeypress="return disableKeyPress(event)">
<script type="text/javascript">
    function disableKeyPress(e)
    {
        var key;
        if (window.event)
            key = window.event.keyCode; //IE
        else
            key = e.which; //firefox
        return (key != 13);
    }
</script>
<u:body>
<s:hidden name="preOrderBooking" id="preOrderBooking"/>
<div dojoType="dijit.layout.LayoutContainer"
     style="width: 100%; height: 100%; background: white; overflow-y:auto;">
<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
<u:actionResults/>
<s:form action="confirmRegistration" id="register_warranty" theme="twms" validate="true" method="POST">
<s:hidden name="actualInventoryForDR" />
<s:hidden name="warranty" value="%{warranty.id}" />
<s:hidden id="selectedBusinessUnit" name="selectedBusinessUnit"/>
<s:hidden id="dealerRentalAllowed" value="%{isCustomerDetailsNeededForDR_Rental()}"/>
<authz:ifUserInRole roles="inventoryAdmin, enterpriseDealership">
<s:if test="allowInventorySelection">
<script type="text/javascript" src="scripts/warrantyRegForInternalUser.js"></script>
<script type="text/javascript">
    var dealerSelect;
    var isDealerNameSelected = false;
    dojo.addOnLoad(function() {
    <s:if test="inventoryItemMappings.empty">
       // dijit.byId("selectedInventoriesPane").destroyDescendants();
       // dijit.byId("selectedInventoriesPane").setContent("");
    </s:if>
    <s:if test="dealerNameSelected">       
            dojo.html.hide(dijit.byId("dealerNumberAutoComplete").domNode);
            dijit.byId("dealerNumberAutoComplete").setDisabled(true);
            dealerSelect = dijit.byId("dealerNameAutoComplete");        
        dojo.html.hide(dojo.byId("dealerNumberText"));
        dojo.html.hide(dojo.byId("toggleToDealerName"));
        isDealerNameSelected = true;
    </s:if>
    <s:else>
        dojo.html.hide(dijit.byId("dealerNameAutoComplete").domNode);
        dijit.byId("dealerNameAutoComplete").setDisabled(true);
        dealerSelect = dijit.byId("dealerNumberAutoComplete");       
        dojo.html.hide(dojo.byId("dealerNameText"));
        dojo.html.hide(dojo.byId("toggleToDealerNumber"));
        isDealerNameSelected = false;
    </s:else>
    //console.debug("test" + "<s:property value="enterpriseDealer()"/>");
    <s:if test="forDealer==null">// ||enterpriseDealer">
	dojo.html.hide(dojo.byId("warrantyRegDiv"));
	</s:if>
	<s:else>
	dojo.html.show(dojo.byId("warrantyRegDiv"));
	</s:else>
    <s:if test="forDealer!=null && warranty.id!=null">
        dojo.html.show(dojo.byId("warrantyRegDiv"));
        dojo.byId("dealerName").value = '<s:property value="forDealer.name"/>';
        dojo.byId("dealerId").value = '<s:property value="forDealer.Id"/>';
        <s:if test="dealerNameSelected">
            isDealerNameSelected = true;
            dojo.html.hide(dojo.byId("toggleToDealerNumber"));
            dojo.html.hide(dijit.byId("dealerNumberAutoComplete").domNode);
            dijit.byId("dealerNumberAutoComplete").setDisabled(true);
        </s:if>
        <s:else>
            isDealerNameSelected = false;
            dojo.html.hide(dojo.byId("toggleToDealerName"));
        </s:else>
    </s:if>
    });
</script>
<div class="policy_section_div" style="border:none; padding:5px;" >
<table class="form" cellpadding="0" cellspacing="0" border="0">
    <input type="hidden" name="dealerNameSelected" id="dealerNameOrNumberSelected"
           value="<s:property value="dealerNameSelected"/>"/>
    <tr style="width:50%">
        <td id="dealerNameText" class="labelStyle">
            <s:text name="label.common.dealerName"/>:
        </td>
        <td id="dealerNumberText" class="labelStyle">
            <s:text name="label.common.dealerNumber"/>:
        </td>
        
        <td>
            <s:if test="forDealer!=null && warranty.id!=null">
                <s:if test="dealerNameSelected">
                    <s:text name="%{forDealer.name}"/>
                </s:if>
                <s:else>
                    <s:text name="%{forDealer.dealerNumber}"/>
                </s:else>
                <s:hidden name="forDealer" value="%{forDealer.id}" id="dealer"/>
            </s:if>
            <s:else>
            <sd:autocompleter id='dealerNameAutoComplete' href='list_warranty_reg_dealer_name_value_id.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='forDealer' loadOnTextChange='true' showDownArrow='false' indicator='indicator' delay='500' key='%{forDealer.id}' keyName='forDealer' value='%{forDealer.name}' />
            <sd:autocompleter id='dealerNumberAutoComplete' href='list_warranty_reg_dealer_number_value_id.action?selectedBusinessUnit=%{selectedBusinessUnit}' name='forDealer' loadOnTextChange='true' showDownArrow='false' indicator='indicator' delay='500' key='%{forDealer.id}' keyName='forDealer' keyValue="%{forDealer.id}" value='%{forDealer.dealerNumber}' />
           <img style="display: none;" id="indicator" class="indicator"
                 src="image/indicator.gif" alt="Loading..."/>

            <script type="text/javascript">
                dojo.addOnLoad(function() {   
                	dojo.byId("dealerId").value = '<s:property value="forDealer.Id"/>';             	
                	var dealerNameAutoCompleter = dijit.byId("dealerNameAutoComplete");
                	dealerNameAutoCompleter.fireOnLoadOnChange=false;
                	var dealerNumberAutoCompleter = dijit.byId("dealerNumberAutoComplete");
                	dealerNumberAutoCompleter.fireOnLoadOnChange=false;                	
                    dojo.connect(dealerNameAutoCompleter, "onChange", function(value) {
                        if (!dijit.byId("dealerNameAutoComplete").isValid()) {
                            setUiForInvalidDealerName();
                        } else {
                        <s:if test="forDealer!=null && warranty.id!=null">
                            var dealerName = "<s:property value="forDealer.name"/>";
                            if (dealerName != value) {
                                //dijit.byId("selectedInventoriesPane").destroyDescendants();
                               // dijit.byId("selectedInventoriesPane").setContent("");
                               if(dijit.byId("searchResultsNode"))
                                dijit.byId("searchResultsNode").setContent("");
                                dojo.byId("customerDetailsDiv").innerHTML = "";
                                dojo.byId("dealerNameOrNumberSelected").value = true;
                                
                            }                             
                        </s:if>
                        <s:else>
                            //dojo.byId("dealerId").value = value;
                            dojo.byId("dealerName").value = dealerSelect.getDisplayedValue();
                        </s:else>
                            isDealerNameSelected = true;  
                            <s:if test="inventoryItem==null">
                            dojo.byId("draft_btn").form.reset();     
                            var hiddenList = dojo.query("input[id $= 'inventoryIemSN']");
                        	for ( var i = 0; i < hiddenList.length; i++) {
                        		hiddenList[i].value = "";
                        	}
            				dojo.byId("draft_btn").form.action="create_warranty.action?forDealer="+value+"&dealerNameSelected=true";    
                            dojo.byId("draft_btn").form.submit();   
                            </s:if>
                            <s:else>
                            dojo.html.show(dojo.byId("warrantyRegDiv"));
                            </s:else>
                        }
                    });
                    dojo.connect(dealerNumberAutoCompleter, "onChange", function(value) {                       
                        if (!dijit.byId("dealerNumberAutoComplete").isValid()) {
                            setUiForInvalidDealerNumber();
                        } else {
                        <s:if test="forDealer!=null && warranty.id!=null">
                            var dealerName = '<s:property value="forDealer.name"/>';
                            if (dealerName != value) {
                                //dijit.byId("selectedInventoriesPane").destroyDescendants();
                                //dijit.byId("selectedInventoriesPane").setContent("");
                                if(dijit.byId("searchResultsNode"))
                                dijit.byId("searchResultsNode").setContent("");
                                dojo.byId("customerDetailsDiv").innerHTML = "";
                                dojo.byId("dealerNameOrNumberSelected").value = true;
                            }
                        </s:if>
                        <s:else>
                            dojo.byId("dealerId").value = value;
                            dojo.byId("dealerName").value = "";
                        </s:else>
                            isDealerNameSelected = false;
                            <s:if test="inventoryItem==null">
                            dojo.byId("draft_btn").form.reset();  
                            var hiddenList = dojo.query("input[id $= 'inventoryIemSN']");
                        	for ( var i = 0; i < hiddenList.length; i++) {
                        		hiddenList[i].value = "";
                        	}         
                            dojo.byId("draft_btn").form.action="create_warranty.action?forDealer="+value+"&dealerNameSelected=false";    
                            dojo.byId("draft_btn").form.submit();   
                            </s:if>
                            <s:else>
                            dojo.html.show(dojo.byId("warrantyRegDiv"));
                            </s:else>
                        }
                    });                   
                });
                </script>
            </s:else>
            <script type="text/javascript">
                dojo.addOnLoad(function() {
                    dojo.subscribe("/multipleInventorySearch/setSelectedParams", null, function() {
                        dojo.html.show(dojo.byId("warrantyRegDiv"));
                            <s:if test="forDealer!=null">
                                dojo.byId("searchDealerNumber").value = '<s:property value="forDealer.dealerNumber"/>';
                            </s:if>
                            <s:else>
                                if (isDealerNameSelected) {
                                    dojo.byId("searchDealerName").value = dealerSelect.getDisplayedValue();
                                    dojo.byId("searchDealerNumber").value = "";
                                } else {
                                    dojo.byId("searchDealerName").value = "";
                                    dojo.byId("searchDealerNumber").value = dealerSelect.getDisplayedValue();
                                }
                            </s:else>
                        dojo.byId("inventorySearchCriteria.dealerId").value = dojo.byId("dealerId").value;
                        dojo.byId("searchDealerNumber").readOnly = true;
                        dojo.byId("searchDealerName").readOnly = true;
                    });
                });
            </script>
        </td>
    </tr>
</table></div>

<div id="toggle" style="cursor:pointer;">
    <span id="toggleToDealerNumber" class="clickable">
        <s:text name="toggle.common.toDealerNumber"/>
    </span>
    <span id="toggleToDealerName" class="clickable">
        <s:text name="toggle.common.toDealerName"/>
    </span>
</div>
</s:if>
<s:else>
    <input type="hidden" name="forDealer" value="<s:property value="forDealer.name"/>"/>
    <script type="text/javascript">
        dojo.addOnLoad(function() {
            dojo.byId("dealerName").value = '<s:property value="forDealer.name"/>';
            dojo.byId("dealerId").value = '<s:property value="forDealer.id"/>';
        });
    </script>
</s:else>
</authz:ifUserInRole>
<s:if test="loggedInUserADealer">
    <script type="text/javascript">
        dojo.addOnLoad(function() {
        	 <s:if test="!loggedInUserAParentDealer">
            dojo.byId("dealerName").value = "<s:property value="%{loggedInUsersDealership.name}"/>";
            dojo.byId("dealerId").value = "<s:property value="%{loggedInUsersDealership.id}"/>";

            if (dojo.byId("dealerName").value.indexOf("&amp;") > 0)
            {
                dojo.byId("dealerName").value = dojo.byId("dealerName").value.replace("&amp;", "&");
            }
            </s:if>
        });
    </script>
    <jsp:include flush="true" page="parentDealer_selection.jsp"/> 
    
</s:if>
<s:elseif test="inventoryItemMappings.size!=0">
    <script type="text/javascript">
        dojo.addOnLoad(function() {
            dojo.byId("dealerName").value = "<s:property value="inventoryItemMappings[0].inventoryItem.currentOwner.name"/>";
            dojo.byId("dealerId").value = "<s:property value="inventoryItemMappings[0].inventoryItem.currentOwner.id"/>";
            if (dojo.byId("dealerName").value.indexOf("&amp;") > 0)
            {
                dojo.byId("dealerName").value = dojo.byId("dealerName").value.replace("&amp;", "&");
            }
        });
    </script>
</s:elseif>
<div id="warrantyRegDiv" class="policy_section_div" style="border:0">
    <s:hidden name="allowInventorySelection"/>   
  
    <jsp:include flush="true" page="warranty_customerinfo.jsp"/> 
  
    <%-- <jsp:include flush="true" page="warranty_operatorinfo.jsp"/> --%>     
     <div dojoType="dijit.layout.ContentPane">
     <s:if test="isAdditionalInformationDetailsApplicable() || isMarketingInfoEntered()">
      <div dojoType="twms.widget.TitlePane" title="<s:text name="label.marketingInformation"/>"
     		id="marketing_info" labelNodeClass="section_header" open="true">
		         <jsp:include flush="true" page="warranty_marketinginfo.jsp"/> 
         </div>
      
         
          
    </s:if> 
     <s:if test="isInstallingDealerEnabled()">
        <div dojoType="twms.widget.TitlePane"
            title="<s:text name="title.common.serviceDetails"/>"
            id="servicedetails" labelNodeClass="section_header" open="true">
               <jsp:include flush="true" page="installingDealer.jsp" />
        </div>   
    </s:if>
 	 
			  
    <jsp:include flush="true" page="warranty_machineinfo.jsp"/>
    

        <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.comments"/>"
             id="comments" labelNodeClass="section_header" open="true">
            <table width="80%">
                <tr>
                    <td width="40%" nowrap="nowrap" class="labelNormalTop labelStyle" style="padding-left:3px;">
                        <s:text name="label.common.comments"/>:
                    </td>
                    <td width="60%" class="labelNormalTop">
                        <t:textarea name="registrationComments"
                                    cols="80" rows="4" cssStyle="width:70%"></t:textarea>
                    </td>
                </tr>
            </table>
        </div>

    
    <div class="buttonWrapperPrimary" style="background:#fff">
        <script>
            function submitForm(btn, val) {
            	 if(dojo.byId('isForPDI')){
						dojo.byId('isForPDI').value="false";
						dojo.byId('printPDI').disabled="true";
            	 }
                dojo.byId('warrantyDraft').value = val;
                btn.form.action="save_draft_warranty.action";
                btn.form.submit();
            }
        </script>
        

	 <s:if test="pdiGeneration">
								<script type="text/javascript">
			dojo.addOnLoad(function() {
				 var inventoryItem = '<s:property value="inventoryItemMappings[0].inventoryItem"/>';
						var thisTabLabel = getMyTabLabel();
                    	parent.publishEvent("/tab/open", {
					                    label: "Print PDI",
					                    url: "printTransfer.action?forETR=false&inventoryItems[0]="+inventoryItem, 
					                    decendentOf: thisTabLabel,
					                    forceNewTab: true
                                       });
                		});
			</script>
							</s:if>
        <s:if test="confirmRegistration">
            <script type="text/javascript" xml:space="preserve">
                dojo.addOnLoad(function() {
                    dijit.byId("confirmRegistration").show();
                    dojo.connect(dojo.byId("btnEdit"), "onclick", function() {
                        dijit.byId("confirmRegistration").hide();
                        bindOnChangeForDDateHrs();
                        checkForDemoOrDealerRental();
                    });
                });
            </script>
            <s:iterator value="inventoryItemMappings" status="inventoryItemMappings">
               
                <s:iterator value="commonAttachments"
                            status="commonAttachments">
                    <s:hidden
                            name="commonAttachments[%{#commonAttachments.index}]"
                            value="%{commonAttachments[#commonAttachments.index].id}"/>
                </s:iterator>
            </s:iterator>
        </s:if>
		
			<s:if test="printPdf">		
	       <input type="button" class="buttonGeneric" value='<s:text name="button.label.printPDI"/>'
								id="printPDI" />
							<s:hidden name="forPrintPDI" id="isForPDI" />
							<script type="text/javascript">
			dojo.addOnLoad(function() {
				var printPDIButton = dojo.byId("printPDI");
				if(printPDIButton){
					dojo.connect(printPDIButton, "onclick", function(event){
						 if(dojo.byId('isForPDI')){
						dojo.byId('isForPDI').value="true";
					    dojo.byId('warrantyDraft').value = "true";
					    if(dojo.byId('draft_btn')){
						    dojo.byId('draft_btn').disabled="true";
					    }
					    var form = dojo.byId("register_warranty");
		                form.action="save_draft_warranty.action";
		                form.submit();
						 }
					});
				}
			});	
		</script>
		</s:if>
        <s:hidden name="saveAsDraft" id="warrantyDraft"/>
        <s:if test="displaySaveAsDraftButton()">
        <button class="buttonGeneric" id="draft_btn" name="warranty.draft"
                onclick="submitForm(this,'true')"><s:text
                name="button.label.saveAsDraft"/></button>
        </s:if>

        <s:submit cssClass="buttonGeneric" value="%{getText('button.common.submit')}"></s:submit>

        
        <s:if test="warrantyTaskInstance!=null && warrantyTaskInstance.id!=null && !inventoryItemMappings.empty">
            <s:hidden name="warrantyTaskInstance"/>
            <input type="button" class="buttonGeneric" value='<s:text name="button.common.delete"/>'
                   id="deleteDRDraft"/>
            <script type="text/javascript">
                dojo.addOnLoad(function() {
                    dojo.connect(dojo.byId("deleteDRDraft"), "onclick", function() {
                        var form = dojo.byId("register_warranty");
                        form.action = "delete_draft_warranty.action";
                        form.submit();
                    });
                });
            </script>
        </s:if>
    </div></div>
    </s:form></div>
</div>

    <div dojoType="twms.widget.Dialog" id="confirmRegistration" title="<s:text name="label.common.confirmDeliveryReport" />"  closable="false" style="width:90%;height:90%; ">
        <div dojoType="dijit.layout.LayoutContainer" style="height:450px;overflow-y: auto; ">
            <div dojoType="dijit.layout.ContentPane" layoutAlign="top" 
                 id="confirmRegistrationContent">
                <jsp:include page="register_warranty_conf.jsp"/>
            </div>
            <div dojoType="dijit.layout.ContentPane" id="actionButtons" layoutAlign="client"
                 style="padding:10px 25px 10px 0;">
                <center class="buttons">
                    <s:submit value="%{getText('button.common.edit')}" id="btnEdit" type="button"/>
                    <s:submit value="%{getText('button.common.submit')}" id="btnSubmit"/>
                </center>
            </div>
        </div>
    </div>
    <div dojoType="twms.widget.Dialog"
     id="dialogBoxTermsAndCondition"
     title="<s:text name="label.common.termsAndConditions" />">
    <div dojoType="dijit.layout.LayoutContainer" style="height:280px;overflow: auto;">
        <div dojoType="dijit.layout.ContentPane" style="margin:5px;"> 

        </div>
    </div>
</div>
</div>
</u:body>
</body>
</html>
<script type="text/javascript">
	dojo.addOnLoad (function() {
		<s:if test = "noPolicyForDemoTruckWithMoreThan80Hours()">
			var contractCodeElement = dijit.byId("contractCode");
			if(contractCodeElement){
				contractCodeElement.fireOnLoadOnChange=false;
				dojo.connect(contractCodeElement, "onChange", function() {
				var indexList =  dojo.query("input[id $= 'indexFlag']"); 
				var nameList =  dojo.query("input[id $= 'nameFlag']");
				for(var i=0;i<indexList.length;i++){
					getAllPolicies(indexList[i].value,nameList[i].value);
				}
				});
			}
		</s:if>
	});
</script>