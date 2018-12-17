<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
	<title><s:text name="title.newClaim.invoiceUpload"/></title>
    <u:stylePicker fileName="common.css"/>
    <s:head theme="twms"/>
</head>

<u:body>
<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
	<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
	    <u:actionResults/>
	  	<s:form action="uploadNonOEMInvoice.action" method="post" enctype="multipart/form-data" id="uploadform">
			<div>
				<p style="padding:5px;">
					<span class="label"> <s:text name="label.newClaim.documentToUpload"/> :</span>
					<s:file name="upload" label="File" id="filepath"/><br/><br/>
					<span class="label"> <s:text name="label.newClaim.documentName"/> :</span>
					<s:textfield name="fileName" id="filename"/><br/>
					<s:hidden name="index"/> 
					<s:hidden name="docId"/>                                       
					<s:submit type="button" value="%{getText('button.newClaim.attach')}" id="uploadbtn"/>
				</p>
			</div>
		</s:form>		
	</div>
</div>				
<script type="text/javascript">
dojo.connect(dojo.byId("filepath"), "onchange", function() {
    var filepath = dojo.byId("filepath");
    var path = filepath.value;
    var pathSegments = path.split(/\\/); // TODO: This works only on Windows
    dojo.byId("filename").value = pathSegments[pathSegments.length - 1];    
});
</script>
</u:body>
</html>