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

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<script type="text/javascript">
    dojo.require("dijit.Tooltip")
</script>
<style type="text/css">
	.dijitTooltipContainer {
	position:absolute;left:0;top:0;
	background-color:#CCD9FF;
	border:1px solid gray;
	font-family:Arial, Helvetica, sans-serif;
	font-size: 9pt;
	display: block;
	
	}
</style>
<div style="padding-bottom:1px;">
<table  class="grid borderForTable" style="margin:5px;" cellspacing="0" cellpadding="0">
	<thead>
		<tr class="row_head">		
			<th width="15%"><s:text name="label.common.date" /></th>
			<th width="15%"><s:text name="label.newClaim.previousState"/></th>
			<th width="10%"><s:text name="label.common.user"/>:</th>
			<th width="30%"><s:text name="label.common.comments"/>:</th>
            <s:if test="loggedInUserAnInternalUser">
				<th width="30%"><s:text name="label.newClaim.internalComments"/>:</th>
            </s:if>
        </tr>
	</thead>
	<tbody>
		<s:iterator value="claimAudits" status="status">
			<s:if test = "(loggedInUserAnInternalUser || (!loggedInUserAnInternalUser && (isCommentViewableByDealer(previousState) || isAllAuditsHistoryShownToDealer())))">
				<tr> 
					<td><s:set name="dateFormat" value="@tavant.twms.dateutil.TWMSDateFormatUtil@getDateFormatForLoggedInUser()"/>
                                            <s:date name="updatedTime" format="%{dateFormat}" /></td>
					<td><s:property value="previousState.state"/> </td>
					<td><s:property value="updatedBy.completeNameAndLogin"/> </td>
					<td>
						<span id="externalComments[<s:property value="#status.index"/>]">
							<s:property value="truncatedExternalComments"/>
						</span>
		
						<span dojoType="dijit.Tooltip" connectId="externalComments[<s:property value="#status.index"/>]" >
							<s:property value="externalComments"/>
						</span>
					</td>
					<s:if test="loggedInUserAnInternalUser">
						<td>
							<span id="internalComments[<s:property value="#status.index"/>]" tabindex="0">
								<s:property value="truncatedInternalComments"/>
							</span>
							
							<span dojoType="dijit.Tooltip" connectId="internalComments[<s:property value="#status.index"/>]" align="right">
								<s:property value="internalComments"/>
							</span>
						</td>
					</s:if>	
                </tr>
			</s:if>
		</s:iterator>
	</tbody>
</table>
</div>