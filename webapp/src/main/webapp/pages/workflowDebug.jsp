<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<head>
    <s:head theme="twms"/>
    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="base.css"/>
</head>
<u:body>
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client"
		style="width: 98%; background: #FCFCFC; border: #CCCCCC 1px solid;  overflow-x: hidden">

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td nowrap="nowrap" class="sectionTitle" colspan="2">
					<s:text name="label.claim.claimDetails" />
				</td>
			</tr>
			<tr>
				<td width="16%" nowrap="nowrap" class="labelNormal">
					<s:text name="label.claim.claimNumber" />:
				</td>
				<td width="34%" class="label">
					<s:property value="id" />
				</td>
			</tr>
		</table>
	</div>
	<div id="separator" />
		<div dojoType="dijit.layout.ContentPane" layoutAlign="client"
			style="width: 98%; background: #FCFCFC; border: #CCCCCC 1px solid;  overflow-x: hidden">

			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td nowrap="nowrap" class="sectionTitle" colspan="2">
						<s:text name="label.workFlow.taskDetails" />
					</td>
				</tr>
				<tr>
					<td width="16%" nowrap="nowrap" class="labelNormal">
						<s:text name="label.workFlow.tasksPending" />
					</td>
					<td width="34%" class="label">
						<s:property value="tasks.size()" />
					</td>
				</tr>
				<tr>
					<td width="50%" colspan="2" nowrap="nowrap" class="labelNormal">
						<s:text name="label.workFlow.taskNames" />
					</td>
				</tr>
				<s:iterator value="tasks">
					<tr>
						<td width="16%" nowrap="nowrap" class="labelNormal"></td>
						<td width="34%" class="label">
							[
							<s:property value="name" />
							] with Token [
							<s:property value="token.id" />
							]
						</td>
					</tr>
				</s:iterator>
			</table>
		</div>
		<s:iterator value="tasks">
			<div id="separator" />
				<div dojoType="dijit.layout.ContentPane" layoutAlign="client"
					style="width: 98%; background: #FCFCFC; border: #CCCCCC 1px solid;  overflow-x: hidden">

					<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid borderForTable">
						<tr>
							<td nowrap="nowrap" class="sectionTitle" colspan="3">
								Task [
								<s:property value="name" />
								] details, Token [
								<s:property value="token.id" />
								] actor [
								<s:property value="actorId" />
								] part [
								<s:property value="variables.get('part').itemReference.unserializedItem.number"/>
								]
							</td>
						</tr>
						<tr>
							<td width="34%" nowrap="nowrap" class="colHeader">
								<s:text name="label.workFlow.sourceNode" />
							</td>
							<td width="34%" nowrap="nowrap" class="colHeader">
								<s:text name="label.workFlow.destinationNode" />
							</td>
							<td width="32%" class="colHeader">
								<s:text name="label.transition.transition" />
							</td>
						</tr>
						<s:iterator value="getTransitionLogsForToken(token.id)">
							<tr>
								<td width="34%" nowrap="nowrap" class="labelNormal">
									<s:property value="sourceNode.name" />
								</td>
								<td width="34%" nowrap="nowrap" class="labelNormal">
									<s:property value="destinationNode.name" />
								</td>
								<td width="32%" nowrap="nowrap" class="labelNormal">
									<s:property value="transition.name" />
								</td>
							</tr>
						</s:iterator>
					</table>
				</div>
		</s:iterator>
</u:body>
