<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Sep 1, 2008
  Time: 4:39:01 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ page pageEncoding="UTF-8"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <s:head theme="twms"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="preview.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>
    <script type="text/javascript" src="scripts/WarrantyProcess.js"></script>
    <script type="text/javascript" src="scripts/commonWarranty.js"></script>
    <script type="text/javascript">
        dojo.require("twms.widget.Dialog");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dojox.layout.ContentPane");
        dojo.require("twms.widget.TitlePane");
        dojo.require("twms.widget.Select");
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

   
<div dojoType="dijit.layout.LayoutContainer"
     style="width: 100%; height: 100%; background: white; overflow-y:auto;">
                <div dojoType="dijit.layout.ContentPane" >               
                    <u:actionResults/>
    <s:form name="warranty_processing" action="warranty_transition_admin" method="POST" id="warrantyForm" theme="twms">
        <s:hidden id="warrantyTaskInstanceId" name="warrantyTaskInstance" value="%{warrantyTaskInstance.id}"></s:hidden>
        <s:hidden name="status" id="actionDecider"/>
        <s:hidden name="forPrintPDI" id="forPdi"/>
        <s:hidden name="selectedBusinessUnit"/>
        <s:hidden name="warranty"/>
        <s:hidden name="transactionType" id="typeOfTransaction" value="%{transactionType}"/>
        <s:hidden id="dealerId" value="%{warranty.forDealer}"/>
        <s:hidden id="dealerRentalAllowed" value="%{isCustomerDetailsNeededForDR_Rental()}"/>
        <s:hidden name="warrantyTaskInstanceId" />

		<div style="background: #F3FBFE; border: 1px solid #EFEBF7; margin: 5px; padding-bottom: 10px;">
		<table class="form" cellpadding="0" cellspacing="0">
			<tr style="width: 50%">
				<td id="dealerNameText" class="labelStyle"><s:text
					name="label.common.dealerName" />:</td>
				<td><s:property value="%{warranty.forDealer.name}" /></td>
			</tr>
		</table>
		</div>
		<div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.customerDetails"/>"
                         id="customer_info" labelNodeClass="section_header" open="true" >
                        <s:set name="siCode" value="%{operator.siCode}" /> 
                		<s:set name="corporateName" value="%{operator.corporateName}" /> 
                        <s:push value="warranty">                        
                            <jsp:include flush="true" page="customer_info.jsp"/>
                        </s:push>
                    </div>
                     <%-- <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.operatorInformation"/>"
                         id="operator_info" labelNodeClass="section_header" open="true" >
                        <s:push value="warranty">
                            <jsp:include flush="true" page="operator_info.jsp"/>
                        </s:push>
                    </div> --%>
                    	<s:if test="isAdditionalInformationDetailsApplicable() || marketingInformation!=null">
                    	<div dojoType="twms.widget.TitlePane" title="<s:text name="label.marketingInformation"/>"
     		id="marketing_info" labelNodeClass="section_header" open="true">
		         <jsp:include flush="true" page="../../../warranty/warranty_marketinginfo.jsp"/> 
         </div>
               
               </s:if>  
                     <s:if test="isInstallingDealerEnabled()">
                    <div dojoType="twms.widget.TitlePane" title="<s:text name="title.common.serviceDetails"/>"
             id="servicedetails" labelNodeClass="section_header" open="true">             
           <jsp:include flush="true" page="../../../warranty/installingDealer.jsp" />
        
        </div>
        </s:if>
         	 
		<div dojoType="twms.widget.TitlePane"
			title="<s:text name="inventory.preview.equipmentInformation"/>"
			id="equipment_info" labelNodeClass="section_header" open="true">
			  <s:if test="isInstallingDealerEnabled()">
		 <jsp:include flush="true" page="copyInstallDate.jsp" />
		 </s:if> <jsp:include flush="true" page="equipment_info.jsp" /></div>

	 

                
                    <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.WarrantyAuditHistory"/>"
                         id="warranty_audit_info" labelNodeClass="section_header" open="true" >
                        <s:push value="warrantyTaskInstance.warrantyAudit">
                            <jsp:include flush="true" page="../read/warranty_audit_history.jsp"/>
                        </s:push>
                    </div>
               

               
                    <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.comments"/>"
                         id="comments" labelNodeClass="section_header" open="true" >
                        <table width="90%">
                            <tr>
                                <td width="20%" nowrap="nowrap" class="labelNormalTop labelStyle">
                                    <s:text name="label.common.comments"/>:
                                </td>
                                <td width="60%" class="labelNormalTop">
                                    <t:textarea name="warrantyAudit.externalComments"
                                    id="warrantyAudit.externalComments"
                                                cols="80" rows="4" cssStyle="width:70%" 
                                                onchange="updateWarrantyComments(this.value)"></t:textarea>
                                    <s:hidden name="warrantyComments" id="warrantyComments"/>                                    
                                </td>
                            </tr>
                        </table>
                    </div>

                 
                    <div align="center" class="spacingAtTop">
                           <s:if test="printPdf"> 
                         <input type="button" value='<s:text name="button.label.printPDI"/>'
                                   id="printPdi" class="buttonGeneric" />
                                    </s:if> 
                        <s:if test="warranty.status.status=='Rejected'">
                            <s:if test="%{warranty.filedBy.id.longValue()==getLoggedInUser().getId().longValue() ||
                                            warranty.forDealer.id.longValue()==getLoggedInUser().getBelongsToOrganization().getId().longValue()}">
                                <input type="button" value='<s:text name="label.common.resubmit"/>'
                                       id="resubmitWarranty" class="buttonGeneric" />
                            </s:if>
                        </s:if>
                        <s:else>
                            <input type="button" value='<s:text name="label.common.accept"/>'
                                   id="acceptWarranty" class="buttonGeneric" />
                            <input type="button" value='<s:text name="label.common.reject"/>'
                                   id="rejectWarranty" class="buttonGeneric"/>
                            <input type="button" value='<s:text name="label.common.forwardToDealer"/>'
                                   id="forwardWarranty" class="buttonGeneric"/>
                        </s:else>
                        <input type="button" value='<s:text name="button.common.delete"/>'
                               id="deleteWarranty" class="buttonGeneric"/>
                               
                               <s:if test="pdiGeneration">
								<script type="text/javascript">
			dojo.addOnLoad(function() {
				 var inventoryItem = '<s:property value="inventoryItemMappings[0].inventoryItem"/>';
				 var transactionType = '<s:property value="transactionType"/>';
				 var isETR;
				 if(transactionType == 'ETR')
					isETR = true;
				 else
					 isETR = false
						var thisTabLabel = getMyTabLabel();
                    	parent.publishEvent("/tab/open", {
					                    label: "Print PDI",
					                    url: "printTransfer.action?forETR="+isETR+"&inventoryItems[0]="+inventoryItem, 
					                    decendentOf: thisTabLabel,
					                    forceNewTab: true
                                       });
                		});
			</script>
							</s:if>
                        <s:hidden name="folderName" />
						<s:if
							test="getLoggedInUser().isInternalUser() && (folderName=='Submitted' || folderName=='Resubmitted' || folderName=='Replied') 
								&& ( warranty.transactionType.trnxTypeKey=='DR' || warranty.transactionType.trnxTypeKey=='DR_RENTAL')">
							<s:checkbox name="warranty.invalidItdrAttachment" />
							<s:text name="label.warrantyAdmin.invalidItdr" />
						</s:if>
					</div>
                <script type="text/javascript">
	                function updateWarrantyComments(val){		
	            		dojo.byId("warrantyComments").value = val;
	            	}
	                dojo.addOnLoad(function() {
	                	var comments = dojo.byId("warrantyAudit.externalComments");
	                	if(comments){
		                	if(comments.value == null){
		                		comments.value=dojo.byId("warrantyComments").value;
		                	}
	                	}
                    });
                    dojo.addOnLoad(function() {
                        if(dojo.byId("acceptWarranty")){
                        dojo.connect(dojo.byId("acceptWarranty"), "onclick", function() {
                            dojo.byId("actionDecider").value = 'ACCEPTED';
                            dojo.byId("forPdi").value= false;
                            if(dojo.byId("printPdi")){
                                dojo.byId("printPdi").disabled="true";
                                }
                            dojo.byId("warrantyForm").submit();
                        });
                        }
                        if(dojo.byId("printPdi")){
                            dojo.connect(dojo.byId("printPdi"), "onclick", function() {
                            	 if(dojo.byId("acceptWarranty")){
                            		 dojo.byId("acceptWarranty").disabled="true";
                            	 }
                            	  if(dojo.byId("rejectWarranty")){
                            		  dojo.byId("rejectWarranty").disabled="true";
                            	  }
                                dojo.byId("forPdi").value= true;
                                dojo.byId("warrantyForm").submit();
                            });
                            }
                        if(dojo.byId("rejectWarranty")){
                        dojo.connect(dojo.byId("rejectWarranty"), "onclick", function() {
                            dojo.byId("actionDecider").value = 'REJECTED';
                            dojo.byId("forPdi").value= false;
                            if(dojo.byId("printPdi")){
                                dojo.byId("printPdi").disabled="true";
                                }
                            dojo.byId("warrantyForm").submit();
                        });
                        }
                        if(dojo.byId("forwardWarranty")){
                        dojo.connect(dojo.byId("forwardWarranty"), "onclick", function() {
                            dojo.byId("actionDecider").value = 'FORWARDED';
                            dojo.byId("forPdi").value= false;
                            if(dojo.byId("printPdi")){
                            dojo.byId("printPdi").disabled="true";
                            }
                            dojo.byId("warrantyForm").submit();
                        });
                        }
                        if (dojo.byId("resubmitWarranty")) {
                            dojo.connect(dojo.byId("resubmitWarranty"), "onclick", function() {
                                dojo.byId("actionDecider").value = 'RESUBMITTED';
                                dojo.byId("forPdi").value= false;
                                if(dojo.byId("printPdi")){
                                    dojo.byId("printPdi").disabled="true";
                                }
                                dojo.byId("warrantyForm").submit();
                            });
                        }
                        dojo.connect(dojo.byId("deleteWarranty"), "onclick", function() {
                            dojo.byId("actionDecider").value = 'DELETED';
                            dojo.byId("forPdi").value= false;
                            if(dojo.byId("printPdi")){
                                dojo.byId("printPdi").disabled="true";
                                }
                            dojo.byId("warrantyForm").submit();
                        });
                    });
                    dojo.addOnLoad(function() {
                    	var indexList =  dojo.query("input[id $= 'indexFlag']"); 
 						var nameList =  dojo.query("input[id $= 'nameFlag']");  
 	 					for(var i=0;i<indexList.length;i++){ 	 	 					
                           	getAllPolicies(indexList[i].value,nameList[i].value);
                        }			
                        dojo.byId("dealerName").value = "<s:property value="warranty.forDealer.name"/>";
                    });
                </script>

    </s:form>
    </div>
</div>
    <div dojoType="twms.widget.Dialog"
     id="dialogBoxTermsAndCondition"
     title="<s:text name="label.common.termsAndConditions" />">
    <div dojoType="dijit.layout.LayoutContainer" style="height:280px;overflow: auto;">
        <div dojoType="dijit.layout.ContentPane" style="margin:5px;"> 

        </div>
    </div>
</div></div>
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