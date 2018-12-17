<%--
  Created by IntelliJ IDEA.
  User: vikas.sasidharan
  Date: 20 Nov, 2007
  Time: 2:28:01 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<style type="text/css">
.warrantyCoverageTrigger {
	background-image: url( image/writeWA.gif );
	width: 20px;
	height: 17px;
	background-repeat: no-repeat;
	margin-top: 5px;
	cursor: pointer;
}
</style>

<div style="overflow: auto; width: 100%;">
<table cellspacing="0" cellpadding="0" id="equipment_details_table"
	class="grid borderForTable" style="clear: both; width: 99%">
	<thead>
		<tr class="row_head">
			<th width="95%">
			<div class="section_heading"><s:text
				name="label.majorComponent.unitSerialNos" /></div>
			</th>
			<th width="5%" style="font-weight: 700">

			<div class="nList_add" id="nListAdd_id"/>
			</th>
		</tr>
	</thead>
	<tbody>
	  <s:if test="isInstallingDealerEnabled()"><tr><td width="100%" colspan="2">
		 <jsp:include flush="true" page="../warrantyProcess/common/write/copyInstallDate.jsp" /></td></tr></s:if>
		<u:nList value="inventoryItemMappings"
			rowTemplateUrl="getUnitRegistrationTemplate.action">
			<jsp:include flush="true" page="unitRegistrationTemplate.jsp" />
		</u:nList>
	</tbody>
</table>
</div>
<!-- The dialog box for displaying when the policies are loaded -->
<div id="policyFetchSection" style="display: none;">
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

<script type = "text/javascript">
console.log("here");
	<s:if test = "buConfigAMER">
	console.log("here");
		var addRowButton = dojo.byId("nListAdd_id");
		console.log(addRowButton);
		<s:if test = "inventoryItemMappings.size() > 0">
			dojo.html.hide(addRowButton);
		</s:if>
		dojo.addOnLoad(function() {
			dojo.connect(addRowButton, "onclick", function() {
        		dojo.html.hide(addRowButton);
        	});
		});
	</s:if>
</script>