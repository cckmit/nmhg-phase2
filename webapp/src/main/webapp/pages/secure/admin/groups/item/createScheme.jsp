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
<div class="admin_section_div" style="width:100%;margin:5px;">

	<div class="admin_section_heading">
		<s:text name="label.manageGroup.itemGroupSchemeDetails"/>
	</div>
	
	<div style="padding:4px;" class="label">
			<s:text name="label.common.name" />:
			<s:textfield name="itemScheme.name" size="50" maxlength="200"/>
	</div>
	
	<s:if test="availablePurposes != null"> 
		
			<div class="mainTitle" style="margin:10px 0px 10px 0px;">
				<s:text name="label.common.purpose"/>
			</div>
			<div class="borderTable">&nbsp;</div>
			<table  width="70%">
				<tr>
					<td class="admin_selections">					
						<s:updownselect id="updownlist3" cssClass="admin_selections" list="availablePurposes" 
								name="purposeNames" size="25" 
								allowMoveDown="false" allowMoveUp="false" allowSelectAll="false" />
					</td>
				</tr>
			</table>
	
	</s:if>
	

</div>
	<div align="center" class="spacingAtTop">
		<s:submit cssClass="buttonGeneric" action="save_item_scheme" value="%{getText('button.common.save')}" />
			<input type="button" id="closeButton" class="buttonGeneric" value="<s:text name='button.common.cancel'/>" />
	</div>
	
	
	<script type="text/javascript">
		dojo.addOnLoad(function() {
			dojo.connect(dojo.byId('closeButton'), "onclick", function() {
				var thisTabId = getTabDetailsForIframe().tabId;
				var thisTab = getTabHavingId(thisTabId);
				closeTab(thisTab);		
			});
		});
	</script>
