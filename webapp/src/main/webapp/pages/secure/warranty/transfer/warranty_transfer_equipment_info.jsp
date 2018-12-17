<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<table id="equipments_table" class="grid borderForTable"
	style="margin-top: 15px;width:95%;">
	<thead>
		<tr class="row_head">
			<th width="95%">
			<div class="section_heading" style="background:none;"><s:text
				name="label.majorComponent.unitSerialNos" /></div>
			</th>
			<th width="5%" style="font-weight: 700">
			<div class="nList_add" id="nListAdd_id" />
			</th>
		</tr>
	</thead>
	<tbody >
		<u:nList value="inventoryItemMappings"
			rowTemplateUrl="getEquipmentTransferTemplate.action" >
			<jsp:include flush="true" page="equipmentTransferTemplate.jsp" />
		</u:nList>
	</tbody>
</table>
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

<script type = "text/javascript">
	<s:if test = "buConfigAMER">
		var addRowButton = dojo.byId("nListAdd_id");
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