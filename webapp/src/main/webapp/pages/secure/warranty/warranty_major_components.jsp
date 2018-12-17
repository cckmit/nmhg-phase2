<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<div id="majorComponents">
<table class="grid borderForTable">
	<thead>
		<tr class="row_head">
			<th><s:text name="label.component.sequenceNumber" /></th>
			<th><s:text name="label.claim.partSerialNumber" /></th>
			<th><s:text name="label.common.partNumber" /></th>
			<th><s:text name="columnTitle.common.description" /></th>
			<th><s:text name="label.installDate" /></th>
			<th width="5%">
                <div class="nList_add" align="center"></div>
			</th>
		</tr>
	</thead>
	<tbody>
		<u:nList value="inventoryItem.composedOf"
			rowTemplateUrl="getMajorComponentsTemplate.action">
			<jsp:include page="majorComponentsTemplate.jsp" />
		</u:nList>
	</tbody>
</table>
</div>