<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<table id="equipments_table" class="grid borderForTable" style='width:100%;'>
	<thead>	
		<tr>
			<th width="95%" class="section_heading">
			<s:text
				name="label.majorComponent.unitSerialNos" />
			</th>
			<th width="5%" class="section_heading">
			<div class="nList_add" id = "nListAdd_id" style="margin-right:5px"></div>
			</th>
		</tr>		
	</thead>
	<tbody>	
	<tr>
	<td width="5%">
		<u:nList value="inventoryItemMappings"
			rowTemplateUrl="getUnitRegistrationTemplate.action">	
                <jsp:include flush="true" page="unitRegistrationTemplate.jsp" />	
		</u:nList>	
		</td>
	</tr>	
	</tbody>
</table>
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