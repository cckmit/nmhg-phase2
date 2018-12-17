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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<u:stylePicker fileName="reportDesign.css"/>

<%--NOTE : This file is meant to work only when included in another file, which has all the javascript functions that r used here--%>
<div id="dialogBoxContainer" style="display:none">
	<div dojoType="twms.widget.Dialog" id="searchDealersDialog" bgColor="#FFF" bgOpacity="0.5" toggle="fade" toggleDuration="250">
		
		<div dojoType="dijit.layout.LayoutContainer" style="width:500px;height:400px; background: #F3FBFE; border: 1px solid #EFEBF7">
			<div style="overflow:auto;">
			<div  dojoType="dijit.layout.ContentPane" layoutAlign="top" style="border-bottom:1px solid #EFEBF7">
				<table width="100%">
					<tr>
					    <td width="15%" class="label1"><s:text name="label.report.dealer" /></td>
						<td width="25%"><t:text id="dealerName" name="dealerName"/></td>
						<td  width="60%">
						<t:submit onclick="requestDealers" id="startSearch1" label="button.common.search" cssClass="buttonGeneric"/>
						</td>
					</tr>
				</table>
			</div>
			<div dojoType="dijit.layout.ContentPane" layoutAlign="client" id="searchResultsDealer" style="background:#F3FBFE">
			</div>
			<div align="center" dojoType="dijit.layout.ContentPane" layoutAlign="bottom" style="border-top:1px solid #EFEBF7">
				<table width="100%">
					<tr>
					    <td width="5%">&nbsp;</td>
						<td>
						<t:submit onclick="addDealersToForm" id="addTerms1" label="button.common.add" cssClass="buttonGeneric"/>
						</td>
						<td><div align="right">
						<t:submit onclick="closeDialogDealer" id="closeTerms1" label="button.common.close" cssClass="buttonGeneric"/>
	         			</div>
	             		</td>
	             		<td width="5%">&nbsp;</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
	</div>
</div>