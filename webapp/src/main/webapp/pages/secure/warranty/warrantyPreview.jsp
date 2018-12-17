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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<%--
  @author fatima.marneni
--%>
<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title>:: <s:text name="title.common.warranty" /> ::</title>
<s:head theme="twms"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="layout.css" common="true"/>
<u:stylePicker fileName="common.css"/>

<s:head theme="twms"/>

<u:stylePicker fileName="form.css"/>
<u:stylePicker fileName="warrantyForm.css"/>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="preview.css"/>
<script type="text/javascript" src="scripts/CheckBoxListControl.js"></script>

<script type="text/javascript">
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.TabContainer");
</script>
</head>
<u:body>
    <div dojoType="dijit.layout.LayoutContainer">
        <div dojoType="dijit.layout.TabContainer" layoutAlign="client" tabPosition="bottom" id="tabs">
	       

            <div dojoType="dijit.layout.ContentPane" title="Equipment Info" class="scrollXAndY">
                <div >
	                 <div class="section_header"><s:text name="label.machineInfo"/></div>
	                 <jsp:include flush="true" page="warranty_equipment_info_preview.jsp"></jsp:include>
                 </div>
            </div>

            <div dojoType="dijit.layout.ContentPane" title="Customer Information">
                <div >
	                 <div class="section_header"><s:text name="label.newCustomerInfo"/></div>
	                 <jsp:include page="customer_details_readonly.jsp"></jsp:include>
                </div>
            </div>
            <s:if test="isAdditionalInformationDetailsApplicable()">
	            <div dojoType="dijit.layout.ContentPane" title="Marketing Information">
	                <div >                 
	                    <jsp:include page="warranty_marketinginfo_preview.jsp"></jsp:include>
	                </div>
	            </div>
	         </s:if>   

        </div>
    </div>    
</u:body>
</html>
