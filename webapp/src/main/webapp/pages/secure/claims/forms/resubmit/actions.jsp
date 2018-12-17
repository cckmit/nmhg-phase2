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

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<%@ taglib prefix="t" uri="twms" %>
	<table class="grid">
		<tr>
			<td width="25%" nowrap="nowrap" class="labelStyle">
				<s:text name="title.attributes.claimState" />:
			</td>
			<td width="75%" class="labelNormalTop">
				<s:property value="claim.state.state"/>						
			</td>
		</tr>
	<s:iterator value="reasonsList" status="rowCounter">
		<tr>
			<s:if test="#rowCounter.count==1">
				<td width="25%" nowrap="nowrap" class="labelStyle">
					<s:text name='%{claimState}'></s:text>:
				</td>
			</s:if>
			<s:else>
				<td width="25%" nowrap="nowrap" class="labelStyle">
					&nbsp;
				</td>
			</s:else>				
			<td width="75%" class="labelNormalTop">
				<s:property/>				
			</td>
		</tr>			
	</s:iterator>
		<tr>
			<td width="25%" nowrap="nowrap" class="labelStyle">
				<s:text name="label.newClaim.processorComments"/>:
			</td>
			<td width="75%" class="labelStyle">
				<s:property value="claim.externalComment"/>
				<s:if test="task!=null">
					<s:hidden name="task.claim.decision" value="" />
				</s:if>
				<s:else>
					<s:hidden name="claim.decision" value="" />
				</s:else>				
			</td>
		</tr>
		<tr>
			<td width="25%" nowrap="nowrap" class="labelStyle">
				<s:text name="label.common.comments"/>:
			</td>
			<td width="75%" class="labelNormalTop">
				<t:textarea name="claim.externalComment" rows="4" cssStyle="width:70%" value=""></t:textarea>
				<s:if test="task!=null">
					<s:hidden name="task.claim.internalComment" value=""/>	
				</s:if>
				<s:else>
					<s:hidden name="claim.internalComment" value=""/>	
				</s:else>			
			</td>
		</tr>
	</table>
	<s:hidden name="mandatedComments[0]" value="externalComments"/>
	<div id="separator"/>
	
    	
