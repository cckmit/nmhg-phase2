<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


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

    <%@ include file="/i18N_javascript_vars.jsp" %>
<script type="text/javascript" src="scripts/twms-widget/widget/ServiceProcedureNode.js"></script>
    <script type="text/javascript" xml:space="preserve">
         dojo.require("twms.widget.Dialog");
         dojo.require("dijit.layout.LayoutContainer");
         dojo.require("dijit.layout.ContentPane");
         dojo.require("dojox.layout.ContentPane");       
         dojo.require("twms.widget.TitlePane");
        function submitForm(btn,val) {
                btn.form.submit();
			}
  </script>
    <script type="text/javascript" src="scripts/ServiceProcedureTree.js"></script>
</head>

<div dojoType="dijit.layout.ContentPane" style="width: 100%; height: 100%; background: white; overflow-y:auto; ">
		<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
		<u:actionResults/>
			<div  >			
				<div class="section_header">
					<s:if test="isDeliveryReport()">
						<s:text name="label.modifyWarranty.deliveryReport"/>
					</s:if>
					<s:else>
						<s:text name="label.modifyWarranty.transferReport"/>
					</s:else>		
				</div>	
				</div>
				<s:form action="modifyAttachments" id="modify_attachments" theme="twms" validate="true" method="POST">
				<s:hidden id="warrantyId" name="warranty" value="%{warranty.id}"></s:hidden>	
                      <jsp:include flush="true" page="./transfer/view_warranty_machineinfo.jsp" />
				      <jsp:include flush="true" page="customer_details_readonly.jsp" />
                      <jsp:include flush="true" page="view_marketInfo.jsp" />    
               
            <div >
			        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.newClaim.supportDocs"/>" labelNodeClass="section_header">
			            <jsp:include flush="true" page="./fileUpload/uploadAttachments.jsp"/>
			        </div>
		  </div>
			    <jsp:include page="../claims/forms/common/write/fileUploadDialog.jsp"/> 
			    
			   
		<div class="section_div">
						<div class="section_heading" style=" font-family:Verdana, Arial, Helvetica, sans-serif; font-size:7.5pt; font-weight:700;"><s:text name="label.modifyDRETRFORDEALER.reason" /></div>
						<div align="left" style="padding: 2px; padding-left: 7px; padding-right: 8px;">
						<t:textarea name="modifyDeleteReason" rows="3" cssStyle="width:90%;"></t:textarea>
						</div>
		</div>	 				
				<div class="buttonWrapperPrimary">	
                     <button class="buttonGeneric"  onclick="submitForm(this,'false')">
						<s:text name="label.invTransaction.actionModify" /></button>
				</div>	
			</s:form>
			</div>
		</div>
</html>
