
<%@taglib prefix="s" uri="/struts-tags" %>	
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<script type="text/JavaScript">
    dojo.require("twms.widget.Dialog");
</script>

<div id="warranty_operator_info" class="rela_Width">
<div id="warranty_operator_info_title" class="section_heading">
<s:text name="label.OperatorInfo"/></div>

<div style="margin: 2px 7px 2px 7px;width:100%">
    <table width="100%" cellpadding="2" cellspacing="2" border="0">
		<tr>
			<td class="labelStyle" width="15%" nowrap="nowrap"><s:text name="label.warrantyAdmin.customerType" />:</td>
			<td width="25%">
				<s:if test="isModifyDRorETR()">
					<s:select list="customerTypes" name="addressBookTypeForOperator" disabled="true"
					listKey="key" listValue="value" headerKey="All" headerValue="--Select--" 
					value="addressBookTypeForOperator" id="addressBookTypesForOperator"/>
				</s:if>
				<s:else>
					<s:select list="customerTypes" name="addressBookTypeForOperator" disabled="false"
					listKey="key" listValue="value" headerKey="All" headerValue="--Select--" 
					value="addressBookTypeForOperator" id="addressBookTypesForOperator"/>
				</s:else>
			</td>
			<s:if test="canModifyDRorETR()">
				<td width="15%"><input id="operatorName" type="text" name="name" /></td>
			</s:if>
			<s:else><td width="15%"><input id="name" type="text" name="name" disabled="true"/></td></s:else>
			<s:if test="canModifyDRorETR()">
				<td width="45%"><input type="button" class="searchButton"
					id="operatorSearchButton" style="border: none;" /></td>
			</s:if>
		</tr>
	</table>
</div>


<div id="addressDiv">
<div dojoType="twms.widget.Dialog" id="DialogContentOperator" title="<s:text name="title.common.operatorInfo"/>:" style="width:85%;height:70%;">
<div id="operatorSearchResultTag" dojoType="dojox.layout.ContentPane" layoutAlign="client" executeScripts="true"
     style="padding-bottom: 3px;overflow:auto;width:100%;height:90%;">
</div>
<div class="buttonWrapperPrimary">
        <input type="button" name="Submit2" value="<s:text name='button.common.close' />" class="buttonGeneric" onclick="javascript:operatordlg.hide()"/>
        <%--This feature of auto picking up the decendentOf attrubute should not be used. 
        this is an odd case and the tag supports picking up of the attribute as the label of current tab
        but this is highly discouraged. specify the value explicitly whenever possible. --%>
        <input type="button" name="Submit3" value="<s:text name='home_jsp.fileMenu.createOperator' />" class="buttonGeneric" onclick="createNewOperator()" id="createOperator"/>
        
        <script type="text/javascript">
      		function createNewOperator(){
		        dijit.byId("DialogContentOperator").hide();
		        //IE 7&8 HACK:
		        //Those stupid guys seems to have a problem with the Dialog hiding, before it hides the Dialog its called the open tab method??
		        //is called and that is making the tab hiding impossible, new tab is already here, Dialog seems to be hide to where situation?
		        //Any way for the time being it seems to be working
		        //PS: 500 milli second seems to be the standard time for every IE hack :)
		        if(dojo.isIE){
		            setTimeout(function() {_createOperator();},500);
		        }else{
		            _createOperator();
		        }
			}
			function _createOperator(){
			    var thisTabLabel = getMyTabLabel();
			    var urlForCreateOperator="show_customer.action";
			    <authz:ifUserInRole roles="inventoryAdmin">
				    if(dojo.byId("warrantyId")){
				     	var warrantyId=dojo.byId("warrantyId").value;
				    	urlForCreateOperator="show_customer.action?warrantyId="+warrantyId;
				    }
				    else{
					    var dealerName=dojo.byId("dealerName").value;
                        var dealerId=dojo.byId("dealerId").value;
                        urlForCreateOperator="show_customer.action?dealerId=" + dealerId + "&dealerName=" +dealerName;
				    }			    	
			    </authz:ifUserInRole>
                <s:if test="isInternalUserModifying()">
                if (dojo.byId("warrantyId")) {
                    var warrantyId = dojo.byId("warrantyId").value;
                    urlForCreateOperator = "show_customer.action?warrantyId=" + warrantyId;
                }
                else {
                    var dealerName = dojo.byId("dealerName").value;
                    var dealerId=dojo.byId("dealerId").value;
                    urlForCreateOperator = "show_customer.action?dealerId=" + dealerId + "&dealerName=" +dealerName;
                }
                </s:if>
                <s:else>
                    var dealerName = dojo.byId("dealerName").value;
                    var dealerId=dojo.byId("dealerId").value;
                    urlForCreateOperator = "show_customer.action?dealerId=" + dealerId + "&dealerName=" +dealerName;
                </s:else>
                parent.publishEvent("/tab/open", {
                    label: "Create Operator",
                    url: urlForCreateOperator,
                    decendentOf: thisTabLabel,
                    forceNewTab: true
               });

			}
        </script>
</div>
</div>
<div id="operatorDetailsDiv">
<s:property value="addressForOperator"/>
<s:if test="operatorAddressForTransfer!=null">
	<jsp:include flush="true" page="operator_details.jsp" />
</s:if>
<s:else>
	<s:hidden name="addressForOperator" value="null" id="addressForTransferOperator"/>
</s:else>
</div>
</div>
</div>
