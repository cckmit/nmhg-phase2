<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  

    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
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
         dojo.require("dijit.layout.LayoutContainer");
         dojo.require("dijit.layout.ContentPane");
         dojo.require("dojox.layout.ContentPane");       
         dojo.require("twms.widget.TitlePane");

         function submitForm(btn,val) {
         		dojo.byId('deleteWarranty').value= val;				
                dojo.byId("modify_warranty").submit();
			}
    </script>
</head>

<u:body>
<s:hidden id="dealerRentalAllowed" value="%{isCustomerDetailsNeededForDR_Rental()}"/>
    <div dojoType="dijit.layout.LayoutContainer"
         style="width: 100%; height: 100%; background: white; overflow-y:auto; ">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
            <u:actionResults/>
<s:form action="modifyWarranty" id="modify_warranty" validate="true" theme="twms" method="POST">
            <div style="margin:5px;border:1px solid #EFEBF7;">
                <div class="section_header">
                    <s:if test="isDeliveryReport()">
                        <s:text name="label.modifyWarranty.deliveryReport"></s:text>
                    </s:if>
                    <s:else>
                        <s:text name="label.modifyWarranty.transferReport"></s:text>
                    </s:else>
                </div>
                <s:hidden id="warrantyId" name="warranty" value="%{warranty.id}"></s:hidden>
                <s:hidden name="deleteWarranty" id="deleteWarranty"/>
                <script type="text/javascript">
                    dojo.addOnLoad(function() {
                    <s:if test="customer!=null">
                        dojo.byId("dealerName").value = '<s:property value="warranty.forDealer.name"/>';
                        dojo.byId("dealerId").value = '<s:property value="warranty.forDealer.id"/>';
                    </s:if>
                    });
                </script>
            </div>
            <authz:ifUserInRole roles="admin">
                <div dojoType="twms.widget.TitlePane" title="<s:text name="label.common.customerDetails"/>"
                         id="customer_info" labelNodeClass="section_header" open="true" >
                        <s:push value="warranty">
                            <jsp:include flush="true" page="../warrantyProcess/common/write/customer_info.jsp"/>
                        </s:push>
                    </div>
            </authz:ifUserInRole>
            <authz:ifUserNotInRole roles="admin">
				<div style="padding-left:10px;"><jsp:include flush="true" page="customer_details_readonly.jsp"/></div>
			</authz:ifUserNotInRole>
			
			        <s:if test="isInstallingDealerEnabled()">
			         <authz:ifUserInRole roles="admin">
        <div dojoType="twms.widget.TitlePane"
            title="<s:text name="title.common.serviceDetails"/>"
            id="servicedetails" labelNodeClass="section_header" open="true">
               <jsp:include flush="true" page="installingDealer.jsp" />
        </div>   
        </authz:ifUserInRole>
    </s:if>
             <div dojoType="twms.widget.TitlePane"
         title="<s:text name="label.machineInfo"/>"
         id="equipment_info" labelNodeClass="section_header" open="true">               
        <div dojoType="dojox.layout.ContentPane" executeScripts="true" id="selectedInventoriesPane">
                <jsp:include flush="true" page="warranty_machineinfo_edit.jsp"/>
</div>
            </div>
            <div id="policyFetchSection" style="display:none;">
    <div dojoType="twms.widget.Dialog" id="pop_up_for_policy_fetching"
         bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250"
         title="<s:text name="label.customReport.pleaseWait" />" style="width: 40%">
        <div class="dialogContent" dojoType="dijit.layout.LayoutContainer"
             style="background: #F3FBFE; width: 100%; height: 130px; border: 1px solid #EFEBF7">
            <div dojoType="dojox.layout.ContentPane">
                <div align="center"  style="padding-top: 20px">
                    <s:text name="label.warranty.waitMessageForPolicy" />
                </div>
            </div>
        </div>
    </div>
</div>
            <s:if test="warranty.dieselTierWaiver !=null">
            <div class="section_header">
            	<s:text name="label.disclaimerInfo"/>
            </div>
             <jsp:include page="disclaimer_info.jsp" />
           <div id="seperator"></div>
           </s:if>
         <div dojoType="twms.widget.TitlePane" title="<s:text name="label.marketingInformation"/>"
     		id="marketing_info" labelNodeClass="section_header" open="true">
            <authz:ifUserInRole roles="admin">            	
                <s:if test="isAdditionalInformationDetailsApplicable()">
                     <s:hidden name="validateMarketingInfo" value="true"></s:hidden>
                    <jsp:include flush="true" page="warranty_marketinginfo.jsp"/>
                </s:if>
             <%--   <s:else>
                	<s:if test="isAdditionalInformationDetailsApplicable() || marketingInformation!=null">                	  
	                    <div dojoType="twms.widget.TitlePane" title="Additional Information"
	                         id="marketing_info_preview" labelNodeClass="section_header" open="true">
	                        <jsp:include flush="true" page="warranty_marketinginfo_preview.jsp"/>
	                    </div>
	                </s:if>
                </s:else>--%>
            </authz:ifUserInRole>
            <authz:ifUserNotInRole roles="admin">
	            <authz:ifUserInRole roles="dealer">
	                <s:if test="isAdditionalInformationDetailsApplicable() || marketingInformation!=null">
	                    <div dojoType="twms.widget.TitlePane" title="Additional Information"
	                         id="marketing_info_preview" labelNodeClass="section_header" open="true">
	                        <jsp:include flush="true" page="warranty_marketinginfo_preview.jsp"/>
	                    </div>
	                </s:if>
	            </authz:ifUserInRole>            	
            </authz:ifUserNotInRole>
            </div>
        
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.warranty.supportDocs"/>"
				labelNodeClass="section_header" open="true">
			<div dojoType="dijit.layout.ContentPane"
				id='uploadDocument' style="border: 0">		
				 <table style="width:100%"> 
  				 <u:uploadDocument name="warranty.attachments" 
                                      canDeleteAlreadyUploadedIf="false" />
				</table>				
				</div>
			</div>
                <div class="section_div" style="margin-left:5px;width:99.2%;">
                    <div class="section_heading"><s:text name="label.modifyDRETR.reason"/></div>
                    <div align="left" style="padding: 2px; padding-left: 7px; padding-right: 10px;">
                        <s:if test="(getLoggedInUser().isInternalUser())">
                        <t:textarea name="warranty.modifyDeleteComments" rows="3" cssStyle="width:90%;"></t:textarea>
                        </s:if>
                        <s:else>
                            <s:property value="warranty.modifyDeleteComments"/>
                        </s:else>
                    </div>
                </div>

            <div class="buttonWrapperPrimary">
                <button class="buttonGeneric" id="cancel_btn"
                        onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"><s:text
                        name="button.common.cancel"/></button>
                <authz:ifUserInRole roles="inventoryAdmin">
                    <s:if test="canModifyDRorETR()">
                        <button class="buttonGeneric" id="delete_btn"
                                onclick="submitForm(this,'true')"><s:text
                                name="button.common.delete"/></button>
                    </s:if>
                </authz:ifUserInRole>
                <button class="buttonGeneric" id="submit_btn" onclick="submitForm(this,'false');return false;">
                    <s:text name="label.invTransaction.actionModify"/></button>
            </div>

            </s:form>
        </div>
    </div>
</u:body>
</html>
<script type="text/javascript">
	dojo.addOnLoad (function() {
		<s:if test = "noPolicyForDemoTruckWithMoreThan80Hours()">
			var contractCodeElement = dijit.byId("contractCode");
			if(contractCodeElement){
				dojo.connect(contractCodeElement, "onChange", function() {
					getAllPoliciesForEdit(0,0);
				});
			}
		</s:if>
	});
</script>