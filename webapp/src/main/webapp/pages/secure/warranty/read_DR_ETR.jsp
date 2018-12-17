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
  </script>
    <script type="text/javascript" src="scripts/ServiceProcedureTree.js"></script>
</head>

<u:body >

	<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; background: white; overflow-y:auto; ">
		<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
			<div style="margin:5px;" >			
				<div class="section_header">
					<s:if test="!forETR">
						<s:text name="label.view.deliveryReport"/>
					</s:if>
					<s:else>
						<s:text name="label.view.transferReport"/>
					</s:else>		
				</div>	
				
                      <jsp:include flush="true" page="./transfer/view_warranty_machineinfo.jsp" />
                      <div id="seperator"></div>
                      <s:if test="warranty.dieselTierWaiver !=null">
                      <div class="section_header">
                     	 <s:text name="label.disclaimerInfo"/>
                      </div>
                      <jsp:include page="disclaimer_info.jsp" />
                      <div id="seperator"></div>
                      </s:if>
                      <div class="section_header">
                     	 <s:text name="title.common.customerInfo"/>
                      </div>
                      	<jsp:include flush="true" page="customer_details_readonly.jsp" />
                      	<div id="seperator"></div>
				      <s:if test="isAdditionalInformationDetailsApplicable() || warranty.marketingInformation!=null">
					      <div class="section_header">
					      	<s:text name="label.marketingInformation"/>
					      </div>
                          <s:push value="warranty">
                              <jsp:include flush="true" page="/pages/secure/warranty/warranty_marketinginfo_preview.jsp"/>
                          </s:push>
                     </s:if>	      
                          
               </div>
           <div id="seperator"></div>
              <s:if test="!warranty.attachments.empty">
	            
	            <div style="width:100%;background:#F3FBFE;border:1px solid #EFEBF7">
	             <div class="section_header"> <s:text name="title.newClaim.supportDocs"/></div>
                	<div style="padding-top:10px;margin:0px 5px 10px 5px;">
                            <u:uploadDocument name="warranty.attachments"
                                              disabled="true" />
                	</div>
        	    </div>
        	   
              </s:if>
			<div dojoType="twms.widget.TitlePane" title="<s:text name="label.title.stockUnitDiscount"/>" labelNodeClass="section_header" open="true">
				<jsp:include flush="true" page="stockUnitDiscountDetails.jsp"/>
			</div>
           <div id="seperator"></div>                      
		 	<div class="section_div" style="margin:5px; width:100%;">
				<div class="section_heading" ><s:text name="label.modifyDRETR.reason" /></div>
					<div align="left">
						<s:property value="warranty.modifyDeleteComments"/>
					</div>
				</div>
				
				
		</div>	 						
	 </div>	
</u:body>
</html>
