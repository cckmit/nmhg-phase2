
<%@ taglib prefix="s" uri="/struts-tags" %>	
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<div id="claim_customer_info" class="section_div">
<div id="claim_customer_info_title" class="section_header"><s:text name="label.customer.searchCustomer"/></div>

<div style="padding: 2px; padding-left: 7px; padding-right: 10px;">	
    	<input id="name" type="text" name="name"/> 
    	<input type="button" class="searchButton" id="customerSearchButton" style="border:none;" />
        <s:hidden id="forDealer" name="task.claim.forDealer.id"></s:hidden>	
</div>

<div id="addrDiv"> 
<div dojoType="twms.widget.Dialog" id="CustomerDialogContent" bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250" style="width:85%;height:75%">
<div id="customerSearchResultTag" dojoType="dojox.layout.ContentPane" layoutAlign="client" executeScripts="true"
     style="padding-bottom: 3px;overflow:auto;width:100%;height:100%">
</div>
<div class="buttonWrapperPrimary">
        <input type="button" name="Submit2" value="<s:text name='button.common.close' />" class="buttonGeneric" onclick="javascript:closeCustomerDialog()"/>
        <%--This feature of auto picking up the decendentOf attrubute should not be used. 
        this is an odd case and the tag supports picking up of the attribute as the label of current tab
        but this is highly discouraged. specify the value explicitly whenever possible. --%>
           <s:if test="task.claim.forDealer.id == loggedInUser.belongsToOrganization.id">
	         <u:openTab id="createCustomer" tagType="button" autoPickDecendentOf="true" tabLabel="Create Customer" url="show_customer.action?matchRead=true" cssClass="buttonGeneric">
	        	 <s:text name="home_jsp.fileMenu.createCustomer" />
	         </u:openTab>
           </s:if>
        
</div>
</div>
</div>
</div>
