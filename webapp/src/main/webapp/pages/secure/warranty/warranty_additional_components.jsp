<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<div id="additionalComponents">
	<script type="text/javascript">
		var extraParams = {
			"inventoryItem" : "<s:property value="inventoryItem"/>"
		};
	</script>
	<table class="grid borderForTable">
		<thead>
			<tr class="row_head">
				<th width="10%"><s:text name="label.additionalComponent.type" /></th>
				<th width="10%"><s:text
						name="label.additionalComponent.subType" /></th>
				<th width="10%"><s:text
						name="label.additionalComponent.serialNumber" /></th>
				<th width="10%"><s:text
						name="label.additionalComponent.partNumber" /></th>
				<th width="10%"><s:text
						name="label.additionalComponent.partDescription" /></th>
				<th width="10%"><s:text
						name="label.additionalComponent.DateCode" /></th>
				<th width="10%"><s:text
						name="label.additionalComponent.manufacturer" /></th>
				<th width="10%"><s:text name="label.additionalComponent.model" /></th>
				<th width="10%">
					<div class="nList_add"></div>
				</th>
			</tr>
		</thead>
		<tbody>
			<u:nList value="inventoryItem.additionalComponents"
				rowTemplateUrl="getAdditionalComponentsTemplate.action"
				paramsVar="extraParams">
				<jsp:include page="additionalComponentsTemplate.jsp" />
			</u:nList>
		</tbody>
	</table>
</div>