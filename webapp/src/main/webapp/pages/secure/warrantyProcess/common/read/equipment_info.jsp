<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<style>
div.divAlign{width:100%; width:95%\9; margin-left:10px;}
</style>
<table id="equipments_table" class="grid" width="100%">
	<thead>
		<tr>
			<th>
			
			</th>
		</tr>
	</thead>
	<tbody>
		<u:nList value="inventoryItemMappings"
			rowTemplateUrl="getUnitRegistrationTemplate.action" >
			<jsp:include flush="true" page="unitRegistrationTemplate.jsp" />
		</u:nList>
	</tbody>
</table>
<div id="policyFetchSection" style="display:none;">
    <div dojoType="twms.widget.Dialog" id="pop_up_for_policy_fetching"
         bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250"
         title="<s:text name="label.customReport.pleaseWait" />" style="width: 40%">
        <div class="dialogContent" dojoType="dijit.layout.LayoutContainer"
             style="background: #F3FBFE; width: 100%; height: 130px; border: 1px solid #EFEBF7">
            <div dojoType="dojox.layout.ContentPane" layoutAlign="center">
                <div align="center"  style="padding-top: 20px">
                    <s:text name="label.warranty.waitMessageForPolicy" />
                </div>
            </div>
        </div>
    </div>
</div>
