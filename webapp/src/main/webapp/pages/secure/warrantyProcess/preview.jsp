<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Sep 1, 2008
  Time: 1:28:24 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
<title>:: <s:text name="title.common.warranty" /> ::</title>
<s:head theme="twms"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="layout.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="preview.css"/>
<s:head theme="twms"/>
<script type="text/javascript">
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.TabContainer");
</script>
</head>
<u:body>
    <div dojoType="dijit.layout.LayoutContainer">
        <div dojoType="dijit.layout.TabContainer" layoutAlign="client" tabPosition="bottom" id="tabs">
            <div dojoType="dijit.layout.ContentPane" title="<s:text name="inventory.preview.equipmentInformation"/>"
                 class="scrollXAndY">
                <div class="policy_section_div">
	                 <div class="section_header"><s:text name="label.machineInfo"/></div>
                            <jsp:include flush="true" page="common/read/equipment_info.jsp"></jsp:include>
                </div>
            </div>


            <div dojoType="dijit.layout.ContentPane" title="<s:text name="inventory.preview.majorComponentsInformation"/>">
                <div class="policy_section_div">
	                 <div class="section_header"><s:text name="label.majorComponents"/></div>
                         <jsp:include flush="true" page="common/read/major_components_info.jsp"></jsp:include>
                </div>
            </div>
            
            <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.common.customerInfo"/>">
                <div class="policy_section_div">
	                 <div class="section_header"><s:text name="label.newCustomerInfo"/></div>
                        <jsp:include flush="true" page="/pages/secure/warranty/customer_details_readonly.jsp"></jsp:include>
                </div>
            </div>
            
           <s:if test="isAdditionalInformationDetailsApplicable() || marketingInformation!=null">
	            <div dojoType="dijit.layout.ContentPane" title="<s:text name="label.marketingInformation"/>">
	                <div class="policy_section_div">
	                    <div class="section_header"><s:text name="label.marketingInformation"/></div>
	                        <jsp:include page="/pages/secure/warranty/warranty_marketinginfo_preview.jsp"></jsp:include>
	                </div>
	            </div>
	       </s:if>     

        </div>
    </div>
</u:body>
</html>