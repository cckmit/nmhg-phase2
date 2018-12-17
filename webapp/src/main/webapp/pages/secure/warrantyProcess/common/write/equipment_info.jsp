<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<table id="equipments_table" class="grid borderForTable"
	style="margin-top: 15px;width:95%;">
	<thead>
	<s:if test="!buConfigAMER">
		<tr class="row_head">		
			<th width="95%">
			<div dojoType="twms.widget.TitlePane"
				title="<s:text name="label.majorComponent.unitSerialNos"/>" id="unit_serial_nos"
				labelNodeClass="section_header" open="false"></div>
			</th>	
		</tr>
	</s:if>
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
            <div dojoType="dojox.layout.ContentPane">
                <div align="center"  style="padding-top: 20px">
                    <s:text name="label.warranty.waitMessageForPolicy" />
                </div>
            </div>
        </div>
    </div>
</div>
