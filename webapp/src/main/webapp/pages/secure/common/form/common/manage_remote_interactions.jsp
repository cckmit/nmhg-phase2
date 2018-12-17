
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<html>
<head>
<u:stylePicker fileName="yui/reset.css" common="true" />
<s:head theme="twms" />
<u:stylePicker fileName="common.css" />
<u:stylePicker fileName="form.css" />
<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="adminPayment.css" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
</head>
<u:actionResults/>
<u:body>

		<div class="admin_section_div" style="width:100%;margin-right:0px;"/>
	<div id="accounts.remote_interactions_logs.title"
		class="section_header"><s:text
		name="title.accounts.remote_interactions_logs" /></div>
		<br/>
		<b>* Please select desired Interaction Name , Status as Failed to reprocess/hide.</b>
		<br/>
		<br/>
		<s:form method="post" theme="twms" id="form" action="RemoteInteractionLogsAction.action">
		<table   border="0" cellpadding="0" cellspacing="0" class="grid">
		<tbody>
		  
			<tr>
				<td width="20%" nowrap="nowrap">
					<label id="InteractionNameLabel" class="labelStyle"   >
						<s:text name="label.manageRemoteInteractions.interactionName" />:
					</label>
				</td>
				<td>
					<s:select name="syncType" headerKey="Select" headerValue="Select" list="remoteSystemNameList"/>
				</td>
			</tr>
			<tr>
				<td nowrap="nowrap">
					<label id="statusLabel" class="labelStyle">
						<s:text name="label.manageRemoteInteractions.status" />:
					</label>
				</td>
				<td>
					<s:select name="statusSelected" headerKey="Select" headerValue="Select" list="#{'Completed':'Completed','Failed':'Failed'}"/>
				</td>
			</tr>
            <tr>
            	<td nowrap="nowrap">
                	<label id="LogicalIdLabel" class="labelStyle">
                    	<s:text name="label.manageRemoteInteractions.logicalId"/>:
                    </label>
                </td>
                <td ><s:textfield id="businessId" name="businessId" /></td>
            </tr>
			<tr>
				<td nowrap="nowrap">
	            	<label id="TransactionIdLabel" class="labelStyle">
	                    <s:text name="label.manageRemoteInteractions.transactionId"/>:
	                </label>
	            </td>
	            <td >
	            	<s:textfield id="TransactionId" name="transactionId"/>
	            </td>
            </tr>
            <tr>
	            <td nowrap="nowrap">
	                <label id="fromDateLabel" class="labelStyle">
	                    <s:text name="label.manageRemoteInteractions.fromDate" />:
	                </label>
	            </td>
	            
	            <td>
	                <sd:datetimepicker name='fromDate' value='%{fromDate}' id='fromDateId' />
	            </td>
            </tr>
            
             <tr>
	            <td nowrap="nowrap">
	                <label id="toDateLabel" class="labelStyle">
	                    <s:text name="label.manageRemoteInteractions.toDate" />:
	                </label>
	            </td>
	            
	            <td>
	                <sd:datetimepicker name='toDate' value='%{toDate}' id='toDateId' />
	            </td>
			</tr>
            <tr>
            	<td nowrap="nowrap">
                    <label id="includeDeleted" class="labelStyle">
                        <s:text name="label.manageRemoteInteractions.includeHidden" />:
                    </label>
                </td>

                <td title="If checked, Search is performed on hidden records only!!">
                	<s:checkbox name="includeDeleted" id="includeDeleted" theme="twms"/>
                </td>
            </tr>
		</tbody>
	</table>
	
	</div>
	<div align="center" class="spacingAtTop" id="buttonsDiv">
       
                    <s:submit value="%{getText('button.common.search')}" cssClass="buttonGeneric" />
              </div>
	</s:form>
</u:body>
<authz:ifPermitted resource="accountsRemoteInteractionsLogsReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('form')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('form'))[i].disabled=true;
	        }
	        document.getElementById("buttonsDiv").style.display="none";
	    });
	</script>
</authz:ifPermitted>
</html>