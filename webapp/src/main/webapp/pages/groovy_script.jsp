<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<head>
    <s:head theme="twms"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="claimForm.css"/>
</head>
<u:body>
	<u:actionResults />
	<s:form action="groovyScript">
		<div style="width: 98%; background: #FCFCFC; border: #CCCCCC 1px solid; overflow-x: hidden">

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td nowrap="nowrap" class="sectionTitle" colspan="2">Groovy
				Shell</td>
			</tr>
			<tr>
				<td colspan="2"><s:text name="label.groovyScript.typeScript" />
				Press submit to execute the script. Two default variables are
				available to you 'context' which is the web application context and
				'out' which is basically console whose output is directed to the
				bottom pane :)</td>
			</tr>
			<tr>
				<td nowrap="nowrap" colspan="2" align="center"><t:textarea
					name="script" cols="180" rows="10"/></td>
			</tr>
			<tr>
				<td nowrap="nowrap" colspan="2">
				<div class="buttonWrapperPrimary"><input type="Submit"
					value="<s:text name="button.common.submit" />" class="buttonGeneric" /></div>
				</td>
			</tr>
		</table>
		</div>
	</s:form>
	<div id="separator" /><s:if test="%{output!=null}">
		<div style="width: 98%; background: #FCFCFC; border: #CCCCCC 1px solid; overflow-x: hidden">

		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td nowrap="nowrap" class="sectionTitle" colspan="2"><s:text name="label.groovyScript.outputScript" /></td>
			</tr>
			<tr>
				<td nowrap="nowrap" colspan="2"><t:textarea
					value="%{output}" cols="180" rows="10"/></td>
			</tr>
		</table>
		</div>
	</s:if>
</u:body>
