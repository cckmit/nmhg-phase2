<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
	<title><s:text name="label.uploads.invoiceDetails"/></title>
    <u:stylePicker fileName="common.css"/>
    <s:head theme="twms"/>
</head>
</script>
<u:body>
	<div >
		<div class="section_header" id="invoiceUploaded"><s:text name="label.uploads.invoiceUploaded" /></div>
		<table class="grid borderForTable"  width="100%" >
			<tr class="row_head">
				<th><s:text name="label.newClaim.fileName"/> </th>
				<th><s:text name="label.uploads.contentType"/> </th>
				<th><s:text name="label.uploads.invoiceId"/> </th>
				<th><s:text name="label.common.action"/> </th>
			</tr>	
			<tr>
				<td>
					<s:url action="downloadDocument" id="url"><s:param name="id" value="%{document.id}"/></s:url>
					<a href="<s:property value="#url" escape="false"/>"><s:property value="document.fileName"/></a>
				</td>
				<td><s:property value="document.contentType"/></td>
				<td><s:property value="document.id"/></td>
				<td>
					<s:url action="deleteInvoice" id="url"><s:param name="id" value="%{document.id}"/></s:url>
					<a href="<s:property value="#url" escape="false"/>"><s:text name="label.common.delete"/></a>
				</td>
			</tr>
		</table>		
	</div>
	
	<div>
	    <s:submit type="button" value='<s:text name="label.common.close"/>' id="close"/>
	</div>
	<script type="text/javascript">
	dojo.addOnLoad(function() { 
	    var docid = <s:property value="document.id"/>;
	    var index = <s:property value="index"/>;
	    var hidden = parent.dojo.byId("hiddenNonOemInvoice_" + index);
	    if (hidden != null) {
	        hidden.value = docid;
	    } 
	        
	    dojo.connect(dojo.byId("close"), "onclick", function () {
	        parent.dojo.publish("/invoices/hide");
	    });
	});
	</script>
</u:body>
</html>