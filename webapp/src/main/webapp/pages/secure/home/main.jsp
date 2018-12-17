<%--
   User: ramalakshmi.p (Formatted the jsp)
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
response.setDateHeader("Expires", 0); %><head>
    <style type="text/css">
        .CloseIcon {
            background : url("/css/theme/official/dojo/official/images/tabClose.gif") no-repeat center ;
            width: 15%;
        }

    </style>
</head>
<script type="text/javascript" src="scripts/main.js"></script>
<script type="text/javascript" >
        dojo.require("twms.widget.Select");
        dojo.addOnLoad ( function() {
        	<authz:ifUserNotInRole roles="readOnlyDealer">
        	<authz:ifUserInRole roles="dealerWarrantyAdmin">
     			dojo.connect(dojo.byId("homeCreateClaim"), "onclick", "createNewClaim");
     		</authz:ifUserInRole>
     		</authz:ifUserNotInRole>
     		<authz:ifUserInRole roles="thirdPartyPrivilege">
				dojo.connect(dojo.byId("homeCreateThirdPartyClaim"), "onclick", "createNewThirdPartyClaims");
			</authz:ifUserInRole>
	     		var newWarrantyLink = dojo.byId("homeRegisterNewWarranty");
                if (newWarrantyLink) {
                    dojo.connect(newWarrantyLink, "onclick", function() {
                        if (dijit.byId("selectBusinesUnitForDR/ETR")) {
                            dojo.byId("transactionTypeDialog").value='DR';
                            dijit.byId("selectBusinesUnitForDR/ETR").show();
                        }else{
                            var defaultBusinessUnit = '<s:property value="%{getLoggedInUser().getBusinessUnits().iterator().next().name}"/>';
                            deliveryReport(defaultBusinessUnit);
                        }
                    });
                }

                var warrantyTransferLink = dojo.byId("homeWarrantyTransfer");
	     		if(warrantyTransferLink) {
                     dojo.connect(warrantyTransferLink, "onclick", function() {
                        if (dijit.byId("selectBusinesUnitForDR/ETR")!=null) {
                            dojo.byId("transactionTypeDialog").value='ETR';
                            dijit.byId("selectBusinesUnitForDR/ETR").show();
                        }else{
                            var defaultBusinessUnit = '<s:property value="%{getLoggedInUser().getBusinessUnits().iterator().next().name}"/>';
                            equipmentTransfer(defaultBusinessUnit);
                        }
                    });
	     		}
	     	<authz:ifUserInRole roles="admin,processor,dsmAdvisor,recoveryProcessor,recoveryProcessor,dealerSalesAdministration">
	        <authz:ifUserNotInRole roles="readOnlyDealer">
	     	 <authz:ifUserInRole roles="admin,processor,dealerSalesAdministration,dsmAdvisor,recoveryProcessor">
                dojo.connect(dojo.byId("homeCreateCustomer"), "onclick", "createCustomer");
            </authz:ifUserInRole>
            </authz:ifUserNotInRole>
            </authz:ifUserInRole>

        <authz:ifAdmin>
     		dojo.connect(dojo.byId("homeOpenPolicyCreation"), "onclick", "openPolicyCreation");
     		dojo.connect(dojo.byId("homeCreatePaymentDefinition"), "onclick", "createPaymentDefinition");
     		</authz:ifAdmin>
     		if(dojo.byId("homeLogout"))
     		{
     			dojo.connect(dojo.byId("homeLogout"), "onclick", "logout");
     		}
	    });
</script>
<div class="outterBg">
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="header">
  <tr>
      <td>
            <authz:ifAdmin>
            <div id="bu" align="center" style="width:60%;display:none">
                <table width="100%"><tr><td style="padding-left:5px;padding-bottom:2px">
                <s:select id="buNames" label="Business Unit" list="businessUnits"
                          listKey="name" listValue="name" name="Business Unit"
                          value="%{warrantyAdminSelectedBusinessUnit}" emptyOption="false"/>
                    </td>
                <td id="close" class="CloseIcon" style="padding-top:5px"></td>
                </tr>
                </table>
            </div>
            <script type="text/javascript">
                dojo.require("twms.widget.Select");
                dojo.addOnLoad(function() {
                    dojo.connect(dojo.byId("buNames"), "onchange", function() {
                        var bu = dojo.byId("buNames");
                        <s:if test="warrantyAdminSelectedBusinessUnit==null">
                        location.href = "bu.action?warrantyAdminSelectedBusinessUnit=" + bu.options[bu.selectedIndex].text;
                        </s:if>
                    });
                    dojo.connect(dojo.byId("close"), "onclick", function() {
                        dojo.html.hide(dojo.byId("bu"));
                    });
                });
            </script>
        </authz:ifAdmin>
        </td>    
  </tr>
    <tr>
        <td width="49%" valign="top">
            <table width="100%" border="0" cellspacing="0" cellpadding="0">
                <!-- Action Folders Listing For Users based on Roles -->
                <authz:ifUserInRole roles="cevaProcessor,admin,inventoryAdmin,supplier,
                baserole,processor,dsm,dsmAdvisor,recoveryProcessor,cpAdvisor,dealerWarrantyAdmin">
                <jsp:include flush="true" page="mainPage/actionFolders.jsp"/>
                <tr>
                    <td>&nbsp;</td>
                </tr>
                </authz:ifUserInRole>
                <!-- Common Actions Listing For Users based on Roles -->
                <s:if test="!hasOnlyCevaRole()">
                    <jsp:include flush="true" page="mainPage/commonAction.jsp"/>
                </s:if>
            </table>
        </td>

        <td width="2%">&nbsp;
           
        </td>
        <td width="49%" valign="top">
            <!-- Action Folders Listing For Users based on Roles -->
            
            <jsp:include flush="true" page="mainPage/goToLinks.jsp"/>
            <authz:ifUserNotInRole roles="receiverLimitedView, inspectorLimitedView, partShipperLimitedView">
            	 <s:if test="!hasOnlyCevaRole()">
            	     <jsp:include flush="true" page="mainPage/quickSearchLinks.jsp"/>
            	</s:if>
            </authz:ifUserNotInRole>	                        
            <authz:ifUserInRole roles="recoveryProcessor">
            	<jsp:include flush="true" page="mainPage/recoveryClaimOfflineDebit.jsp"/>
            </authz:ifUserInRole>            
        </td>
    </tr>
</table>
</div>