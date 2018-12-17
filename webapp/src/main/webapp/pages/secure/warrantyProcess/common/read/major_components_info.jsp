<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Sep 1, 2008
  Time: 2:54:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<u:stylePicker fileName="common.css"/>

<table  cellspacing="0" cellpadding="0" id="major_components_details_preview_table"  class="grid borderForTable" style="width: 96%;">
    <thead>
		<tr class="row_head">
				<th><s:text name="label.component.sequenceNumber" /></th>
				<th><s:text name="label.component.serialNumber" /></th>
				<th><s:text name="label.common.partNumber" /></th>
				<th><s:text name="columnTitle.common.description" /></th>
				<th><s:text name="label.installDate" /></th>
			
		</tr>
	</thead>
    <tbody>
        <s:iterator value="inventoryItemMappings[0].inventoryItem.composedOf" status="index">
        <tr>
          	<td>
          		<s:property value="sequenceNumber" />
          	</td>
            <td>
                <s:property value="part.serialNumber"/>
            </td>
            <td>
                <s:property value="part.ofType.number" />
            </td>
            <td>
                <s:property value="part.ofType.description" />
            </td>
           <td>
	              <s:property value="part.installationDate" />
            </td>
            
        </tr>
        </s:iterator>
   </tbody>
</table>