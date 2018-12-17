<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<head>
    <u:stylePicker fileName="master.css"/>
    <u:stylePicker fileName="detailDesign.css"/>
    <u:stylePicker fileName="base.css"/>


</head>

<div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="width: 97%; background: #FCFCFC; border: #CCCCCC 1px solid; margin: 5px; padding: 5px; overflow-x: hidden">
	<s:form action="task_transition_submit.action">
		<table>
		<tr>
		<td colspan="8" class="sectionTitle">
			<s:text name="label.taskTransition.taskUtility" />
		</td>
		</tr>
		
		<tr>
		<td>
			<s:text name="label.taskTransition.taskID" />
		</td>
		<td>
			<s:textfield name="id"></s:textfield>
		</td>
		</tr>
		<tr>
		<td>
			<s:text name="label.transition.transition" /> 
		</td>
		<td>
			<s:textfield name="transitionTaken"></s:textfield>
		</td>
		</tr>
		</table>
		<div class="buttonWrapperPrimary">
			<s:submit value="button.common.submit"></s:submit>
		</div>
	</s:form>	
</div>