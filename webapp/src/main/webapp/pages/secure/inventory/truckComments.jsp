<%--

   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

--%>

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="authz" uri="authz"%>
<form id="truckCommentForm" name="truckCommentForm" method="POST" action="newTruckComment">
<s:hidden name="inventoryItem" />
<table class="grid borderForTable"  cellpadding="0" cellspacing="0">
	<thead>
		<tr class="row_head">
			<th width="10%"><s:text name="columnTitle.common.machineSerialNo"/></th>
		    <th width="10%"><s:text name="label.inventoryComment.commentSequence"/></th>
		    <th width="20%"><s:text name="label.userId"/></th>
		    <th width="10%"><s:text name="label.inventoryComment.dateOfComment"/></th>
		    <th width="50%"><div style="width:50%;word-wrap: break-word;margin-left: 40px;"> <s:text name="label.inventoryComment.comment"/></div></th>
        </tr>
	</thead>
	<tbody id="truckCommentsBody">
   		<s:iterator value="inventoryItem.inventoryComments">
   			<tr>
				<td><s:text name="inventoryItem.serialNumber" /></td>
				<td><s:text name="sequenceNumber" /></td>
				<td><s:text name="userId" /></td>
				<td><s:text name="dateOfComment" /></td>
				<td><div style="width:50%; word-wrap: break-word;margin-left: 40px;"><s:text name="comment" /></div></td>
			</tr>
   		</s:iterator>
	</tbody>
</table>
<authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
<s:if test="isAttachmentEditable">
<div align="center" id="comment_submit_section">
			<s:submit align="middle" cssClass="button" value="%{getText('label.inventoryComment.addComment')}" action="newTruckComment"></s:submit>
</div></s:if>
</authz:ifNotPermitted>
</form>