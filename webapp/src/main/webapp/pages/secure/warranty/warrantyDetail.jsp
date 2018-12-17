<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <s:head theme="twms"/>
        <u:stylePicker fileName="yui/reset.css" common="true"/>
        <u:stylePicker fileName="layout.css" common="true"/>
        <u:stylePicker fileName="common.css"/>
        <u:stylePicker fileName="form.css"/>
        <u:stylePicker fileName="warrantyForm.css"/>
        <u:stylePicker fileName="base.css"/>
        <u:stylePicker fileName="claimForm.css"/>
        <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
        <%@ include file="/i18N_javascript_vars.jsp" %>
        <script type="text/javascript" xml:space="preserve">
            dojo.require("twms.widget.Dialog");
            dojo.require("dojox.layout.ContentPane");
            dojo.require("twms.widget.DateTextBox");
            dojo.require("twms.widget.TitlePane");
            dojo.require("dijit.layout.LayoutContainer");
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
        <s:hidden id="dealerRentalAllowed" value="%{isCustomerDetailsNeededForDR_Rental()}"/>
            <div dojoType="dijit.layout.LayoutContainer"
                 style="width: 100%; height: 100%; background: white;">
                <div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="overflow-y:auto">
                    <s:if test="warranty.transactionType.trnxTypeKey=='DR'||warranty.transactionType.trnxTypeKey=='DEMO' || warranty.transactionType.trnxTypeKey=='DR_RENTAL'">
                        <s:form id="register_warranty" theme="twms" validate="true" method="POST" action="confirmRegistration">
                            <u:actionResults/>
                            <s:hidden name="selectedBusinessUnit" id="selectedBusinessUnit"/>
                            <s:hidden name="warrantyTaskInstance"/>
                             <s:hidden name="transactionType" value="%{warranty.transactionType.trnxTypeKey}" />
                            <authz:ifUserInRole roles="inventoryAdmin">
                                <table class="form" cellpadding="0" cellspacing="0"  style="width:80%">
                                    <tr>
                                        <td><s:text name="label.common.dealerName"/>:</td>
                                        <td><s:property value="warranty.forDealer.name"/></td>
                                    </tr>
                                    <tr>
                                        <td><s:text name="label.common.dealerNumber"/>:</td>
                                        <td><s:property value="warranty.forDealer.dealerNumber"/></td>
                                    </tr>
                                </table>
                                <input type="hidden" name="forDealer" value="<s:property value="warranty.forDealer.id"/>"/>
                                <script type="text/javascript">
                                    dojo.addOnLoad(function() {
                                        dojo.byId("dealerName").value='<s:property value="warranty.forDealer.name"/>';
                                        dojo.byId("dealerId").value='<s:property value="warranty.forDealer.id"/>';
                                    });
                                </script>
                            </authz:ifUserInRole>
                            <s:if test="loggedInUserADealer">
                                <input type="hidden" name="forDealer" value="<s:property value="loggedInUsersDealership.id"/>"/>
                                <script type="text/javascript">
                                    dojo.addOnLoad(function() {
                                        dojo.byId("dealerName").value='<s:property value="%{loggedInUsersDealership.name}"/>';
                                        dojo.byId("dealerId").value='<s:property value="%{loggedInUsersDealership.id}"/>';
                                    });
                                </script>
                            </s:if>
                            <s:hidden name="warranty" value="%{warranty.id}" />
                            <%--<s:hidden name="inventoryItem" value="%{warranty.forItem.id}"/>--%>
                            <script type="text/javascript" src="scripts/WarrantyReg.js"></script>
                            <script type="text/javascript" src="scripts/commonWarranty.js"></script>
                            <script type="text/javascript">
                                dojo.addOnLoad(function() {     
                                <s:if test="!confirmRegistration">    	         	         	
                                        var indexList =  dojo.query("input[id $= 'indexFlag']"); 
                                        var nameList =  dojo.query("input[id $= 'nameFlag']");  
                                        for(var i=0;i<indexList.length;i++){ 	 	 					
                                            getAllPolicies(indexList[i].value,nameList[i].value);
                                        }			
                                </s:if>
                                        dojo.connect(dijit.byId("selectedInventoriesPane"),"onLoad","getPolicies");
                                    });
                            </script>
                                <jsp:include flush="true" page="parentDealer_selection.jsp"/> 
                            
                            <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.customerDetails"/>"
                                    id="customer_info" labelNodeClass="section_header" open="true">
                                     <s:set name="siCode" value="%{operator.siCode}" /> 
                					<s:set name="corporateName" value="%{operator.corporateName}" /> 
                                <s:push value="warranty">
                                    <jsp:include flush="true" page="../warrantyProcess/common/write/customer_info.jsp"/>
                                </s:push>
                            </div>
                           
                          <div dojoType="twms.widget.TitlePane" title="<s:text name="label.marketingInformation"/>"
     		id="marketing_info" labelNodeClass="section_header" open="true">
     		     <s:if test="isAdditionalInformationDetailsApplicable() ">  
		         	<jsp:include flush="true" page="warranty_marketinginfo.jsp"/> 
		         </s:if>
         		</div>
      
          
    
                            <s:if test="isInstallingDealerEnabled()">
                                <div dojoType="twms.widget.TitlePane" title="<s:text name="title.common.serviceDetails"/>"
                                     id="servicedetails" labelNodeClass="section_header" open="true">
                                    <jsp:include page="installingDealer.jsp"></jsp:include>
                                </div>
                            </s:if>
                             
                            <div dojoType="twms.widget.TitlePane"
                                    title="<s:text name="inventory.preview.equipmentInformation"/>"
                                    id="equipment_info" labelNodeClass="section_header" open="true">                        
                                <jsp:include flush="true" page="../warranty/warranty_equipment_info_draft.jsp"/>                        
                            </div>
                            <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.comments"/>"
                                    id="comments" labelNodeClass="section_header" open="true">
                                <table width="80%" border="0" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td width="40%" nowrap="nowrap" class="labelNormalTop labelStyle" >
                                            <s:text name="label.common.comments"/>:
                                        </td>                                
                                        <td width="60%" class="labelNormalTop">
                                            <t:textarea name="registrationComments"
                                                        cols="80" rows="4" cssStyle="width:70%" value="%{warranty.warrantyAudits[warranty.warrantyAudits.size()-1].externalComments}"></t:textarea>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            <script>
                                function submitForm(btn, val) {
                                	if(dojo.byId('isForPDI')){
                						dojo.byId('isForPDI').value="false";
                                		 dojo.byId('printPDI').disabled="true";
                                	}
                                    dojo.byId('warrantyDraft').value = val;
                                    btn.form.action = "save_draft_warranty.action";
                                    btn.form.submit();
                                }

                                function deleteDraftWarranty(btn){
                                    var form = dojo.byId('register_warranty'); 
                                    form.action = "delete_draft_warranty.action";
                                    form.submit();
                                }
                            </script>

                            <div class="buttonWrapperPrimary">
                                <s:if test="confirmRegistration">
                                    <script type="text/javascript" xml:space="preserve">
                                        dojo.addOnLoad(function() {
                                            dijit.byId("confirmRegistration").show();                    
                                            dojo.connect(dojo.byId("btnEdit"), "onclick", function() {
                                                dijit.byId("confirmRegistration").hide();
                                            });
                                        });
                                    </script>
                                </s:if>
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
							<s:if test="printPdf">
								<input type="button" value='<s:text name="button.label.printPDI"/>'
                                   id="printPDI" class="buttonGeneric" />
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
                                <button class="buttonGeneric" id="draft_btn"
                                        onclick="submitForm(this,'true')"><s:text
                                        name="button.label.saveAsDraft" /></button>
                                    <s:submit value="%{getText('button.common.submit')}" cssClass="buttonGeneric"/>
                                <input type="button" class="buttonGeneric" value='<s:text name="button.common.delete"/>' id="deleteDRDraft" onclick="deleteDraftWarranty(this)"/>
                            </div>
                        </s:form>
                        <div dojoType="twms.widget.Dialog" id="confirmRegistration" title="Confirm Registration" >
                            <div dojoType="dijit.layout.LayoutContainer" style="width:710px; height:310px;background: #F3FBFE;">
                                <div dojoType="dijit.layout.ContentPane" layoutAlign="top" executeScripts="true" scriptSeparation="false"
                                        id="confirmRegistrationContent" >
                                    <jsp:include page="register_warranty_conf.jsp" />
                                </div>
                            </div>
                            <div dojoType="dijit.layout.ContentPane"  id="actionButtons">
                                <center class="buttons">
                                    <s:submit value="%{getText('button.common.edit')}" id="btnEdit" type="button"/>
                                    <s:submit value="%{getText('button.common.submit')}" id="btnSubmit" />
                                </center>
                            </div>
                        </div>
                    </s:if>
                    <s:else>
                        <s:form id="transfer_warranty" theme="twms" validate="true" method="POST" action="confirmTransferForDraft">
                            <u:actionResults/>
                            <s:hidden name="selectedBusinessUnit"/>
                            <authz:ifUserInRole roles="inventoryAdmin">
                                <table class="form" cellpadding="0" cellspacing="0"  style="width:80%">
                                    <tr>
                                        <td><s:text name="label.common.dealerName"/>:</td>
                                        <td><s:property value="warranty.forItem.latestWarranty.forDealer.name"/></td>
                                    </tr>
                                    <tr>
                                        <td><s:text name="label.common.dealerNumber"/>:</td>
                                        <td><s:property value="warranty.forItem.latestWarranty.forDealer.dealerNumber"/></td>
                                    </tr>
                                </table>
                                <input type="hidden" name="forDealer" value="<s:property value="warranty.forItem.latestWarranty.forDealer.name"/>"/>
                                <script type="text/javascript">
                                    dojo.addOnLoad(function() {		        
                                        dojo.byId("dealerName").value='<s:property value="warranty.forItem.latestWarranty.forDealer.name"/>';
                                        dojo.byId("dealerId").value='<s:property value="warranty.forItem.latestWarranty.forDealer.id"/>';
                                    });
                                </script>         
                            </authz:ifUserInRole>
                            <s:if test="loggedInUserADealer">
                                <script type="text/javascript">
                        dojo.addOnLoad(function() {   
                            dojo.byId("dealerName").value='<s:property value="%{loggedInUsersDealership.name}"/>';
                            dojo.byId("dealerId").value='<s:property value="%{loggedInUsersDealership.id}"/>';
                        });
                                </script>
                            </s:if>
                            <s:hidden name="warranty" value="%{warranty.id}" />
                            <%--<s:hidden name="inventoryItem" value="%{warranty.forItem.id}"/>--%>
                            <script type="text/javascript" src="scripts/WarrantyTransfer.js"></script>
                            <script type="text/javascript" src="scripts/commonWarranty.js"></script>
                            <script type="text/javascript">
                                dojo.addOnLoad(function() {     
                                <s:if test="!confirmTransfer">
                                            var indexList =  dojo.query("input[id $= 'indexFlag']"); 
                                            var nameList =  dojo.query("input[id $= 'nameFlag']");  
                                            for(var i=0;i<indexList.length;i++){ 	 	 					
                                                getAllPolicies(indexList[i].value,nameList[i].value);
                                            }				         		
                                </s:if>
                                            dojo.connect(dijit.byId("selectedInventoriesPane"),"onLoad","getPolicies");	
                                        });
                            </script>
                            <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.customerDetails"/>"
                                 id="customer_info" labelNodeClass="section_header" open="true">
                                <s:push value="warranty">
                                    <jsp:include flush="true" page="../warrantyProcess/common/write/customer_info.jsp"/>
                                </s:push>
                            </div>

                             <div dojoType="twms.widget.TitlePane" title="<s:text name="label.marketingInformation"/>"
     		id="marketing_info" labelNodeClass="section_header" open="true">
     		     <s:if test="isAdditionalInformationDetailsApplicable()"> 
     		   
		         <jsp:include flush="true" page="warranty_marketinginfo.jsp"/> 
		       </s:if>
         </div>
     
         
                            
                            
                            <s:if test="isInstallingDealerEnabled()">
                                <div dojoType="twms.widget.TitlePane" title="<s:text name="title.common.serviceDetails"/>"
                                     id="servicedetails" labelNodeClass="section_header" open="true">
                                    <jsp:include page="installingDealer.jsp"></jsp:include>
                                </div>    </s:if>


                             <jsp:include flush="true" page="transfer/warranty_transfer_machineinfo.jsp" />
                          
                            <script> 
                                function submitForm(btn, val) {
                                	 if(dojo.byId('isForPDI')){
                                		 dojo.byId('isForPDI').value="false";
                                		 dojo.byId('printPDI').disabled="true";
                                	 }
                                    dojo.byId('warrantyDraft').value = val;
                                    btn.form.action = "save_draft_equipmentTransfer.action";
                                    btn.form.submit();
                                }
                            </script>
                            <div class="section_div">
                                <div class="section_heading"><s:text name="label.comments" /></div>
                                <div  style="padding: 2px; padding-left: 0px; padding-right: 10px; margin:5px;">
                                    <t:textarea name="registrationComments" rows="3" value="%{warranty.warrantyAudits[warranty.warrantyAudits.size()-1].externalComments}" 
                                                cssStyle="width:90%; "/>
                                </div>
                            </div>
                            <div class="buttonWrapperPrimary">
                                <s:if test="confirmTransfer">
                                    <script type="text/javascript" xml:space="preserve">
                                        dojo.addOnLoad(function() {
                                            dijit.byId("confirmTransfer").show();                    
                                            dojo.connect(dojo.byId("btnEdit"), "onclick", function() {
                                                dijit.byId("confirmTransfer").hide();
                                            });
                                        });
                                    </script>
                                </s:if>
                                 <s:if test="pdiGeneration">
								<script type="text/javascript">
			dojo.addOnLoad(function() {
				 var inventoryItem = '<s:property value="inventoryItemMappings[0].inventoryItem"/>';
						var thisTabLabel = getMyTabLabel();
                    	parent.publishEvent("/tab/open", {
					                    label: "Print PDI",
					                    url: "printTransfer.action?forETR=true&inventoryItems[0]="+inventoryItem, 
					                    decendentOf: thisTabLabel,
					                    forceNewTab: true
                                       });
                		});
			</script>
							</s:if>
							<s:if test="printPdf">
                                 <input type="button" class="buttonGeneric" value='<s:text name="button.label.printPDI"/>'
								id="printPDI" />
							<s:hidden name="forPrintPDI" id="isForPDI" />
							<script type="text/javascript">
			dojo.addOnLoad(function() {
				var printButton = dojo.byId("printPDI");
				if(printButton){
					dojo.connect(printButton, "onclick", function(event){
						 if(dojo.byId('isForPDI')){
						dojo.byId('isForPDI').value="true";
					    dojo.byId('warrantyDraft').value = "true";
					    var form = dojo.byId("transfer_warranty");
		                form.action="save_draft_equipmentTransfer.action";
		                form.submit();
						 }
					});
				}
			});	
		</script>
		</s:if>
		
                                <s:hidden name="invTransaction.invTransactionType" value="%{transactionType}"/>
                                <s:hidden name="transactionType" value="%{transactionType}" />
                                <s:hidden name="saveAsDraft" id="warrantyDraft"/>
                                <button class="buttonGeneric" id="draft_btn" type="button"
                                        onclick="submitForm(this,'true')"><s:text
                                        name="button.label.saveAsDraft" /></button>					
                                    <s:submit name="label.warranty.transfer" cssClass="buttonGeneric"/>
                                <input type="button" class="buttonGeneric" value='<s:text name="button.common.delete"/>'
                                       id="deleteETRDraft"/>
                                <script type="text/javascript">
                                    dojo.addOnLoad(function() {
                                        dojo.connect(dojo.byId("deleteETRDraft"), "onclick", function() {
                                            dojo.byId("deleteETRDraft").disabled = true;
                                            var form = dojo.byId("transfer_warranty");
                                            this.form.action = "delete_draft_warranty.action";
                                            this.form.submit();
                                        })
                                    })
                                </script>
                            </div>
                        </s:form>
                        <div dojoType="twms.widget.Dialog" id="confirmTransfer" title="Confirm Transfer" closable="false" style="width: 80%;">
                            <div dojoType="dojox.layout.ContentPane" layoutAlign="top" 
                                 id="confirmTransferContent" style="height:380px;">
                                <jsp:include page="../warranty/transfer/transfer_warranty_conf.jsp" />
                                <div dojoType="dojox.layout.ContentPane" layoutAlign="client" style="padding:10px 0 10px 0;">
                                    <center class="buttons">
                                        <s:submit value="%{getText('button.common.edit')}" id="btnEdit" type="button"/>	                
                                        <s:submit value="%{getText('button.common.submit')}" id="btnSubmit" />
                                    </center>
                                </div>
                            </div>
                        </div>
                    </s:else>
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
<authz:ifPermitted resource="dRApproval/TransferDraftReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('register_warranty')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('register_warranty'))[i].disabled=true;
	        }
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('transfer_warranty')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('transfer_warranty'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>