
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
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<s:if test="!(nextLOAProcessor == null)">	
<div style="display: table-row; width: 100%;">
		<div style="display: table-cell; width: 33px; float: left;">
		<s:if test="task != null && task.takenTransition != null && task.takenTransition.equals('ApproveAndTransferToNextUser')">
			<input type="radio" checked="checked" name="task.takenTransition"
				value="ApproveAndTransferToNextUser" class="processor_decesion"
				id="approveAndTransferRadio" onclick="showEnabledFields(this.id)"/>					
		</s:if>
		<s:else>
			<input type="radio" name="task.takenTransition"
				value="ApproveAndTransferToNextUser" class="processor_decesion"
				id="approveAndTransferRadio" onclick="showEnabledFields(this.id)"/>
		</s:else></div>
		<div class="labelStyle" style="display: table-cell; width: 16%; float: left;"><b><s:text
			name="label.newClaim.approveAndTransferToNextUser" /></b></div>
		<div  style="display: table-cell; width: 40%; float: left;" class="labelNormalTop" colspan="2">
		
		<s:hidden id="nextLOAProcessorId" name="task.transferTo" disabled="true"
			value='%{nextLOAProcessor}'/></div>
		<div style="display: table-cell; width: 40%; float: left;"></div></div>
</s:if>
