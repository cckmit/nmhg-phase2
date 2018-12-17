<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<html>
<head>
	<title><s:text name="title.uploadDocument" /></title>
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
	  	<s:form action="uploadDocument.action" method="post" enctype="multipart/form-data" id="uploadform">
            <div dojoType="dijit.layout.LayoutContainer">
                <div dojoType="dijit.layout.ContentPane" style="width: 100%;height: 100%;background:#F3FBFE" layoutAlign="center">
                    <div style="background:#F3FBFE">
                        <s:hidden name="batchSize"/>
                        <div id="files" style="display: block;">
                            <u:loop repeat="batchSize" status="status">
                                <p class="uploadFormRootElement"><%--TODO: i18N me--%>
                                    <span class="labelStyle"><s:text name="label.newClaim.document"/> : </span>
                                    <span><s:file name="upload[%{#status}]" label="File"/></span>
                                </p>
                            </u:loop>
                        </div>
                        <s:hidden name="attachementListIndex" value="%{#nListIndex}"/>
                        <span style="margin-left: 5px;" >
                            <img style="display: none;position:relative;left:40%;margin-top:15%" id="indicator" class="indicator" src="image/throbber.gif" alt="Loading..."></img>
                            <p id="textMsg" style="display: none;position:relative;left:30%"> <s:text name="message.upload.document.wait"/> </p>
                        	<input class="labelStyle" type="button" value="<s:text name='button.newClaim.attach'/>" id="uploadbtn" onclick="uploadform.submit();test();">
                        </span><%--TODO: i18N me--%>
                    </div>
                </div>
            </div>
		</s:form>

		 <script type="text/javascript">
		    function test(){
		        dojo.style(dojo.byId("files"),"display","none");
		        dojo.style(dojo.byId("uploadbtn"),"display","none");
                dojo.style(dojo.byId("indicator"),"display","block");
                dojo.style(dojo.byId("textMsg"),"display","block");
		    }


		  </script>

<s:if test="documents.size() > 0"><%--means something was uploaded--%>
    <script type="text/javascript">
        dojo.addOnLoad(function() {
            <s:set name="firstTime" value="true"/>
            <s:set name="documentAttached" value="true"/>
            var message = <s:property value="JSONifiedDocumentList" escape="false"/>;
            <s:if test="attachementListIndex != null && attachementListIndex != ''">
                parent.publishEvent("/uploadDocument/uploaded_<s:property value="attachementListIndex"/>", message);
            </s:if>
            <s:else>
                parent.publishEvent("/uploadDocument/uploaded", message);
            </s:else>
        });
    </script>
</s:if>
</u:body>
</html>