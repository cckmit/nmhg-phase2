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

<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<script type="text/javascript" src="scripts/thirdParty/thirdPartySearch.js"></script>
<table class="form" border="0" cellpadding="0" cellspacing="0">
    <tbody>
        <tr>
            <td align="center">
                <label for="isThirdParty">
                    <s:text name="label.viewClaim.thirdPartyClaim"/>:
                </label>
                <s:checkbox name="claim.isThirdPartyClaim" id="thirdPartyCheck" cssClass="checkbox"/>
            </td>
            <td>
            	<span id="thirdPartySelectedName" style="display:none">
	              	<a id="thirdPartyAddLinkId" class="link" >
		            	<s:text name="label.viewClaim.thirdPartyAddLink"/>
					</a>
           			<s:textfield name="selectedThirdPartyText" id="selectedThirdParty" readonly="true" theme="simple"/>
           		</span>
            </td>
            
            
    <script type="text/javascript">
            dojo.addOnLoad(function() {
                var check = dojo.byId("thirdPartyCheck");
                dojo.connect(check, "onclick", function(evt) {
                    if (evt.target.checked) {
                        showThirdPartyLink();
                    } else {
                        hideThirdPartyLink();
                    }
                });
            });
            
             dojo.connect(dojo.byId("thirdPartyAddLinkId"), "onclick",
            	function() {
            		dojo.publish("/thirdPartySearch/show");
				});
            
    </script>
        </tr>
    </tbody>
 </table>
   

