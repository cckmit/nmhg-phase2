<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%--
  @author aniruddha.chaturvedi
--%>
<html>
<head>
    <title>::<s:text name="title.common.warranty" />::</title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <%@ include file="/i18N_javascript_vars.jsp" %>
</head>
  <u:body>
  	
  	<u:actionResults/>
  	<div >
  		<div class="section_header"><s:text name="label.locale"/></div>
  		<s:iterator value="messages" status="status">
	 			<s:form id="%{#status.index}" method="post" enctype="multipart/form-data" action="upload_properties_file">
 					<table class="form" cellpadding="0" cellspacing="0" width="100%">
	 					<tr>
	 						<td align="left">
	 							<s:property value="locale.displayName"/>
	 						</td>
	 						<td align="left">
	 							<s:url action="download_properties_file" id="url"><s:param name="localeStr" value="%{locale}"/></s:url>
	 							<a href="<s:property value="#url" escape="false"/>"><s:text name="button.download"/></a>
	 						</td>
	 						<td width="70%" align="left">
	 							<span><s:file name="upload" label="File" id="filepath"/></span>
	 							<span>
	 								<s:hidden name="localeStr" value="%{locale}"/>
									<s:submit type="button" value="%{getText('title.uploadDocument')}" id="uploadbtn"/>
								</span>
	 						</td>
	 					</tr>
 					</table>
				</s:form>
			</s:iterator>
  	</div>
  </u:body>
</html>
