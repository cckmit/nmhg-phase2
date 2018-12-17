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

<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<html>
<head>
    
    <u:stylePicker fileName="adminPayment.css"/>
	<u:stylePicker fileName="common.css"/>
	<u:stylePicker fileName="form.css"/>
    <s:head theme="twms"/>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
    </script>
    
</head>


<u:body>
<div style="display:none;" id="errorDiv">
<u:actionResults/>
</div>

<s:form name="buForm" id="buForm"  theme="twms" action="updateSupplierBUMapping">

  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow-x:hidden; overflow-y:auto;" id="root" >
    <div class="policy_section_div" style="width:100%;margin-right:5px;">
        <div id="dcap_pricing_title" class="section_header">
            <s:text name="title.bu.supplier.search"/>
        </div>
        <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid borderForTable" style="width:99%" align="center"> 
            <tr class="row_head">
                <th colspan="2"><s:label  value="%{getText('label.user.name')}" cssClass="hederTxt"/></th>
                <th><s:label value="%{getText('label.availableBu')}" cssClass="hederTxt"/></th>
            </tr>
           	<tr>
               <td>
                  <s:property value='supplier.name' />
               </td>
               <td></td>
               <td>
               		<s:hidden name="supplier" />
					<s:iterator value="allBusinessUnits" status="buResults" id="bu"  >
						<s:property value='#bu.displayName' />
						<s:if test="%{isBUAlreadyMapped(#bu)}">
							<s:checkbox name="buListSelected%{#buResults.index}_checkbox" disabled="true" value="true" 
									id="buListCheck[%{#buResults.index}]" />
                            <s:hidden name="buListSelected[%{#buResults.index}]" value="%{#bu}"/> 
						</s:if>
						<s:else>                 	  		
                 	  		<s:checkbox name="buListSelected[%{#buResults.index}]" 
									id="buListCheck[%{#buResults.index}]" fieldValue="%{#bu}"/>                            
						</s:else>
		                <script type="text/javascript">
		                    dojo.addOnLoad(function() {
		                       
		                        dojo.connect(dojo.byId("buListCheck<s:property value='{#buResults.index}'/>"), "onchange", function() {
		                            if (dojo.byId(("buListCheck<s:property value='{#buResults.index}'/>")).checked) {
		                                countForCheckBox = countForCheckBox + 1;
		
		                            } else {
		                                countForCheckBox = countForCheckBox - 1;
		
		                            }
		                        });
		                    });
		                </script>
                     </s:iterator>
                  </td>
             </tr>             
        </table>
    </div>
    <authz:ifNotPermitted resource="readOnlyAccesstoSLMS">
    <div align="center" class="buttons spacingAtTop">
    
                <%--  <input type="button" class="searchButton" id="updateButton" value="Update"/> --%>
                    

                   <s:submit cssClass="buttonGeneric" value="Update" id="updateButton"/>

                    <s:submit cssClass="buttonGeneric" value="%{getText('button.common.cancel')}" type="input" id="cancelButton"/>
                    <script type="text/javascript">
                        dojo.addOnLoad(function() {
                            dojo.connect(dojo.byId("cancelButton"), "onclick", closeMyTab);
                        });
                    </script>
      </div>
      </authz:ifNotPermitted>
  </div>
</s:form>
</u:body>
</html>