<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
	<title><s:text name="title.uploadDocumentForRecoveryClaim" /></title>
    <u:stylePicker fileName="common.css"/>
    <s:head theme="twms"/>
    <style type="text/css">
        p.uploadFormRootElement{
            padding : 5px;
        }
        p.uploadFormRootElement span{
		font-family: Verdana, Arial, Helvetica, sans-serif;
		font-size:8pt;
		font-weight:400;
        margin : 2px;
        }
	   .labelStyle{
       color:#545454;
       font-family:Verdana,Arial,Helvetica,sans-serif;
       font-size:7pt;
       font-weight:600;
	   }
    </style>
    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
    </script>
</head>
 
<u:body>
<u:actionResults/>
	  	<s:form action="uploadDocumentForRecoveryClaim.action" method="post" enctype="multipart/form-data" id="uploadform">
            <div dojoType="dijit.layout.LayoutContainer">
                <div dojoType="dijit.layout.ContentPane" style="width: 100%;height: 100%;background:#F3FBFE" layoutAlign="center">
                    <div style="background:#F3FBFE">
                        <s:hidden name="batchSize"/>
                        <u:loop repeat="batchSize" status="status">
                            <p class="uploadFormRootElement"><%--TODO: i18N me--%>
                                <span class="labelStyle"><s:text name="label.newClaim.document"/> : </span>
                                <span><s:file name="upload[%{#status}]" label="File"/></span>
                            </p>
                        </u:loop>
                        <span style="margin-left: 5px;" ><s:submit type="button" value="%{getText('button.newClaim.attach')}" id="uploadbtn"/></span><%--TODO: i18N me--%>
                    </div>
                </div>
            </div>
		</s:form>
<s:if test="documents.size() > 0"><%--means something was uploaded--%>
    <script type="text/javascript">
        dojo.addOnLoad(function() {
            <s:set name="firstTime" value="true"/>
            var message = <s:property value="JSONifiedDocumentListForRecoveryClaim" escape="false"/>;
            parent.publishEvent("/uploadDocumentForRecoveryClaim/uploaded", message);
        });
    </script>
</s:if>
</u:body>
</html>