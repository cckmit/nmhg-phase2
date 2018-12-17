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
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>
    <u:stylePicker fileName="adminPayment.css"/>
    <!-- Javascripts and stylesheets for Admin section -->
    <script type="text/javascript" src="scripts/RepeatTable.js"></script>
    <script type="text/javascript" src="scripts/AdminToggle.js"></script>
    <script type="text/javascript" src="scripts/adminAutocompleterValidation.js"></script>
    <script type="text/javascript">
        function validate(inputComponent) {

        }
    </script>
</head>
<u:body>
<s:form name="baseForm" id="baseFormId" validate="true" method="post" >
<u:actionResults/>
<s:hidden name="id" id="blahblah"/>
<div class="admin_section_div" style="margin:5px;width:99%">
    
    
    <div class="policy_section_heading"><s:text name="title.uom.addMappings"/></div>
        <u:repeatTable id="myTable" cssClass="grid borderForTable" cellpadding="0" cellspacing="0" cssStyle="margin:5px;" theme="simple">
            <thead>
               <tr class="title">
                    <th width="33%" class="colHeader"><s:text name="label.uom.baseUom"/></th>
                    <th width="33%" class="colHeader"><s:text name="label.uom.mappedUom"/></th>
                    <th width="32%" class="colHeader"><s:text name="label.uom.mappingFraction"/></th>
                    <th width="2%" class="colHeader">
                        <u:repeatAdd id="adder" theme="simple">
                            <img id="addPrice" src="image/addRow_new.gif" border="0" style="cursor: pointer; padding-right:4px; " title="<s:text name="label.uom.AddUom" />" />
                        </u:repeatAdd>
                    </th>
                </tr>
            </thead>
            <u:repeatTemplate id="mybody" value="mappings" index="myindex" theme="twms">
                <tr index="#myindex">
                    <td style="border:1px solid #EFEBF7;">
                        <s:select list="unMappedUoms" listKey="name" listValue="type" id="mappings_#myindex_baseUom" name="mappings[#myindex].baseUom" 
                        value ="%{mappings[#myindex].baseUom.name}"/>
                       </td>
                    <td style="border:1px solid #EFEBF7;">
                        <s:textfield id="mappings[#myindex].mappedUom" name="mappings[#myindex].mappedUom" />
                    </td>
                    <td style="border:1px solid #EFEBF7;">
                        <s:textfield id="mappings[#myindex].mappingFraction" name="mappings[#myindex].mappingFraction" />
                    </td>
                    <td style="border:1px solid #EFEBF7;">
                        <u:repeatDelete id="deleter_#myindex" theme="simple">
                            <img id="deletePrice" src="image/remove.gif" border="0" style="cursor: pointer;padding-right:4px;" title="<s:text name="label.uom.deleteUom" />"/>
                        </u:repeatDelete>
                    </td>
                </tr>
            </u:repeatTemplate>
        </u:repeatTable>
    
   

</div>
 <div align="center" style="margin:10px 0px 0px 0px;">
      <s:submit id="closeTab" value="Cancel" cssClass="buttonGeneric" action=""/>
			<script type="text/javascript">
			    dojo.addOnLoad(function() {
			        dojo.connect(dojo.byId("closeTab"), "onclick", function() {
			            closeMyTab();
			        });
			    });
			</script>
        <s:submit value="Submit" cssClass="buttonGeneric" action="saveUomMapping"/>
    <div>
</s:form>

</u:body>
</html>
