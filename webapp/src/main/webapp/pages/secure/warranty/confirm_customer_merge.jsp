<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<s:head theme="twms"/>
<style  type="text/css">
.searchButton {
		vertical-align:middle;
		cursor:pointer;
		width:18px;
		overflow:visible;
		background:url(../../../image/searchIcon.gif);
		background-repeat:no-repeat;
		background-position:center;
}
</style>
<script lang="javascript">
    dojo.require("dijit.layout.ContentPane");
</script>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="warrantyForm.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="base.css"/>
</head>
<u:body>
<u:actionResults/>
<s:form action="confirm_merge.action" method="POST" id="updateCustomerForm" name="create" validate="true">
    <div dojoType="dijit.layout.LayoutContainer" style="background: white;overflow-y:auto;overflow-x:hidden">
        <div dojoType="dijit.layout.ContentPane">
            <jsp:include flush="true" page="selected_merged_customers.jsp"/>
            <jsp:include flush="true" page="new_merge_customer.jsp"/>
        </div>
    </div>
</s:form> 
</u:body>
</html>
