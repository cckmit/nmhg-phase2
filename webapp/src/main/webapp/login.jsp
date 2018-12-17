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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title><s:text name="title.loginPage" /></title>
<s:head theme="twms" /><u:stylePicker fileName="master.css"/>
<script type="text/javascript" src="scripts/login/login.js"></script>
<script type="text/javascript">
    dojo.addOnLoad(function() {
        new twms.util.CapsLockOnDetector("password", '<s:text name="warning.loginPage.capsLockOn" />');
        dojo.byId("userName").focus();
    });
</script>
</head>

<u:body smudgeAlert="false"> <s:form action="/authenticateUser" method="post" id="loginForm">
  <div id="container">
    <div class="leftCurve"></div>
    <div class="middleCurve">
      <div class="loginHead"></div>
      <!--<div class="hrule"></div>-->
      <div>
        <div class="leftSec"></div>
        <div class="midSpace"></div>
        <div class="rightSec">
          <!--Login Related Check and error message-->
          <div class="txtLabels"><s:text name="label.userId" /></div>
          <div>
            <input class="txtinput" id="userName" name="user" type="text" accesskey="U"  />
          </div>
          <div class="txtLabels2"><s:text	name="label.password" /></div>
          <div>
            <input class="txtinput" id="password" name="j_password" type="password" accesskey="P"/>
          </div>
          <div></div>
          <div align="center" class="alert"> <s:property value="userMessage" /> </div>
          <s:if test="#session['ACEGI_SECURITY_LAST_EXCEPTION'] != null"><s:set name="ACEGI_SECURITY_LAST_EXCEPTION" value=""	scope="session" /> <br />
          <div class="alert" id="alerts">
            <div align="center"> <s:text name="error.loginFailed" /> </div>
          </div>
          </s:if>
						<div>
							<input type="image" src="image/loginBtn.jpg" id="logMeInButton"
								border="0" class="button" alt="Login" title="Login" />
						</div>
			<div class="alert" id="alerts">
							<div align="center">
								<s:iterator value="#session['INTEGRATION_ERRORS']" var="key">
									<s:text name="%{#key}" />
								</s:iterator>
								<s:set name="INTEGRATION_ERRORS" value="" scope="session"/>
							</div>
						</div>
					</div>
        <div class="rightCurveBG"></div>
      </div>
      <div style="clear:both;float:left;margin-top:-72px;margin-bottom:-12px;">
        
        <s:if test="bannerMsg != null">
        <div style="margin-top: -311px; margin-right: -130px; float: right;"> <span ><img src="image/tavant.jpg" alt="Tavant" title="Tavant"  /></span> </div>
        	<br></br><p style=" font-family:Verdana, Arial, Helvetica, sans-serif; font-size:11pt; font-weight:500; color:#FF0000; padding-left:7px;margin-top:6px;"> <s:property value="bannerMsg" /></p>
        	
        </s:if>
      </div>
      <s:if test="bannerMsg == null">      
        <div style="margin-top: -390px; float: right;"> <span ><img src="image/tavant.jpg" alt="Tavant" title="Tavant"  /></span> </div>
      </s:if>
    </div>
    <div class="rightCurve"></div>
  <div style="clear:both;"></div>
  </div>
<s:hidden name="%{@tavant.twms.web.security.authn.filter.OrgAwareAuthenticationProcessingFilter@PASSWD_BASED_AUTHN_INDICATOR}" value="true" /></s:form>
</u:body>
</html>