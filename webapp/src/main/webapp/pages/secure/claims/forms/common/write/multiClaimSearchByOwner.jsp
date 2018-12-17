<%@ taglib prefix="s" uri="/struts-tags" %>	
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<div style="padding: 2px; padding-left: 0px; padding-right: 10px;">	
		<label id="modelNumberLabel" class="labelStyle">
			<s:text name="label.multiClaim.addressBook"></s:text>
        </label>
    	<input type="button" class="searchButton" id="customerSearchButton" style="border:none;" />        
</div>

<div id="dialogBoxContainer" style="display:none">
<div dojoType="twms.widget.Dialog" id="CustomerDialogContent" style="overflow:hidden;width:75%;height:75%;" >
<div dojoType="dijit.layout.LayoutContainer" 
	style="background: #F3FBFE; border: 1px solid #EFEBF7;overflow: auto;">
    <div dojoType="dojox.layout.ContentPane" layoutAlign="top" id="customerSearchResults">
		<jsp:include flush="true" page="customer_search_for_multiClaim.jsp" />
	</div>
	<div class="buttonWrapperPrimary">
        <input type="button" name="Submit2" value="Close" class="buttonGeneric" onclick="javascript:closeCustomerDialog()"/>
        <%--This feature of auto picking up the decendentOf attrubute should not be used. 
        this is an odd case and the tag supports picking up of the attribute as the label of current tab
        but this is highly discouraged. specify the value explicitly whenever possible. --%>
           <s:if test="task.claim.forDealer.id == loggedInUser.belongsToOrganization.id">
	         <u:openTab id="createCustomer" tagType="button" autoPickDecendentOf="true" tabLabel="Create Customer" url="show_customer.action?matchRead=true" cssClass="buttonGeneric">
	        	  <s:text name="home_jsp.fileMenu.createCustomer"/>
	         </u:openTab>
           </s:if>
        
	</div>
</div>
</div>
</div>

