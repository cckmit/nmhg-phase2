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
<%--
 @author: Jhulfikar Ali
--%>


<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ include file="/i18N_javascript_vars.jsp" %>
<%@ taglib prefix="authz" uri="authz" %>
<script type="text/javascript" src="scripts/vendor/dojo-widget/dojo/dojo.js"></script>
<s:head theme="twms" />
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="adminPayment.css"/>

<u:body>
<s:form name="baseForm" id="baseForm" method="post" action="updateMiscellaneousPart.action">
    <u:actionResults/>
    <div class="admin_section_div" style="margin:5px;width:99%">
        <div class="admin_section_heading">
            <s:text name="label.miscellaneous.part"/>
        </div>
        <table  cellspacing="0" cellpadding="0" class="grid">
            <s:hidden name="miscellaneousItem" value="%{miscellaneousItem.id}"/>
            
            <tr>
                <td width="20%" class="labelStyle"><s:text name="label.miscellaneous.partNumber"/>:</td>
                <td width="30%" class="label"><s:textfield name="miscellaneousItem.partNumber"
                             value="%{miscellaneousItem.partNumber}"/></td>
            </tr>
            </table>
               
                <div class="mainTitle">
                        <s:text name="label.miscellaneous.partDescription"/>:
                    </div>
                    <div class="borderTable">&nbsp;</div>
             
             <table  cellspacing="0" cellpadding="0" class="grid" style="margin-top:-10px;">
           
            <tr width="100%">
                <td>
                    <table class="grid">
                        <s:iterator value="locales" status="itemLocaleIter">
                            <tr>
                                <td class="labelStyle" width="20%" nowrap="nowrap"><s:property value="description"/></td>
                                <td><s:textfield
                                        name="miscellaneousItem.i18nMiscTexts[%{#itemLocaleIter.index}].description"
                                        value="%{miscellaneousItem.i18nMiscTexts[#itemLocaleIter.index].description}"/></td>
                                <s:hidden name="miscellaneousItem.i18nMiscTexts[%{#itemLocaleIter.index}].locale"
                                          value="%{locales[#itemLocaleIter.index].locale}"/>
                            </tr>
                        </s:iterator>
                    </table>
                </td>
            </tr>
        </table>
     
    </div>
       <div align="center" class="spacingAtTop">
                    <s:submit value="%{getText('button.common.update')}" cssClass="buttonGeneric"/>
                </div>
</s:form>
<authz:ifPermitted resource="warrantyAdminManageMiscellaneousPartsReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>
