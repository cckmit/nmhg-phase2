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

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>


<html>
    <body>
    	<s:text name="form"/>     
	     <s:form action="uploadDocument.action" method="post" enctype="multipart/form-data" id="uploadform">
				<p style="padding:5px;">
					<span class="label"><s:text name="label.uploads.documentToUploaded"/></span>
                 <s:file cssClass="border:1px solid #666666;" name="upload" label="File" id="documentOperations"/><br/><br/>
                 <span class="label"><s:text name="label.uploads.documentTitle"/></span>
                 <s:textfield cssClass="border:1px solid #666666;" name="fileName" id="uploadfilename"/><br/>
                 <s:submit type="button" value='<s:text name="label.common.upload"/>' id="uploadbtn"/>
				</p>			
		</s:form>
	</body>
</html>