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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<html>
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.miscellaneous.parts"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="yui/reset.css" common="true"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="base.css"/>
    <u:stylePicker fileName="adminPayment.css"/>    
    <script type="text/javascript" src="scripts/RepeatTable.js"></script>
    <script type="text/javascript" src="scripts/AdminToggle.js"></script>
    <script type="text/javascript" src="scripts/adminAutocompleterValidation.js"></script>
</head>
<u:body>
<s:form name="baseForm" id="baseFormId" validate="true" method="post" >
<u:actionResults/>

<div class="admin_section_div">
    
    
    <div class="admin_section_heading"><s:text name="title.miscellaneous.parts"/></div>
        <u:repeatTable id="myTable" cssClass="grid borderForTable" width="96%" theme="simple">
            <thead>
               <tr class="row_head">
                    <th width="33%" class="colHeader"><s:text name="label.miscellaneousPart.partNumber"/></th>
                    <th width="33%" class="colHeader"><s:text name="label.miscellaneousPart.description"/></th>                    
                    <th width="2%" class="colHeader" >
                        <u:repeatAdd id="adder" theme="simple">
                            <img id="addPrice" src="image/addRow_new.gif" border="0" style="cursor: pointer; padding-right:4px;" title="<s:text name="label.miscellaneousParts.AddPart" />" />
                        </u:repeatAdd>
                    </th>
                </tr>
            </thead>
            <u:repeatTemplate id="mybody" value="items" index="myindex" theme="twms">
                <tr index="#myindex">
                    <td >
                        <s:textfield id="items[#myindex]_partNumber" name="items[#myindex].partNumber" />
                    </td>
                    <td >
                        <table>
                            <s:iterator value="locales" status="itemLocaleIter">
                                <tr>
                                    <td><s:property value="description"/></td>
                                    <td><s:textfield
                                            name="items[#myindex].i18nMiscTexts[%{#itemLocaleIter.index}].description"
                                            value="%{items[#myindex].i18nMiscTexts[#itemLocaleIter.index].description}"/></td>
                                    <s:hidden name="items[#myindex].i18nMiscTexts[%{#itemLocaleIter.index}].locale"
                                              value="%{locales[#itemLocaleIter.index].locale}"/>
                                </tr>
                            </s:iterator>
                        </table>
                            <%--<s:textfield id="items[#myindex]_description" name="items[#myindex].description" />--%>
                    </td>
                    <td >
                        <u:repeatDelete id="deleter_#myindex" theme="simple">
                            <img id="deletePrice" src="image/remove.gif" border="0" style="cursor: pointer;padding-right:4px;" title="<s:text name="label.miscellaneousParts.deletePart" />"/>
                        </u:repeatDelete>
                    </td>
                </tr>
            </u:repeatTemplate>
        </u:repeatTable>
    
   </div>
      <div class="spacingAtTop" align="center"><s:submit id="closeTab" value="Cancel" cssClass="buttonGeneric" action=""/>
			<script type="text/javascript">
			    dojo.addOnLoad(function() {
			        dojo.connect(dojo.byId("closeTab"), "onclick", function() {
			            closeMyTab();
			        });
			    });
			</script>
        <s:submit value="Create" cssClass="buttonGeneric" action="saveMiscellaneousParts"/></div>
  


</s:form>

</u:body>
</html>
