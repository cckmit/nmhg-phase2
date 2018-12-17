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
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<html>
<head>
    <u:stylePicker fileName="adminPayment.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <s:head theme="twms"/>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>

    <script type="text/javascript">
        dojo.require("dijit.layout.LayoutContainer");
        function submitForm() {
        	var form=document.getElementById('costCatForm');        	
        	for (i=0;i<form.ap.length;i++) {            	
            	if (form.ap[i].value=='') {
                	var maxIndex = form.ap[i].length;            		
            		form.ap[i].options[maxIndex] = new Option('newOption', '');
            		form.ap[i].options.selectedIndex = maxIndex;           		
            	}			
        	}       	        	
        	form.submit();        	
        }
    </script>
</head>


<u:body>
    <u:actionResults/>
    <s:form id="costCatForm" name="costCategory" action="updateCostCategoryConfiguration">
        <div class="policy_section_div">
            <div id="dcap_pricing_title" class="section_header">
                <s:text name="accordionLabel.costCategoryConfiguration"/>
            </div>
            <div class="spacer10"></div>
             <div id="categories" style="display: block;">
            <table  border="0" cellspacing="0" cellpadding="0" width="60%" class="borderForTable">
                <thead>
                    <tr class="row_head">
                        <th align="center"><s:text name="label.common.select" /></th>
                        <th align="center"><s:text name="label.costCategory"/></th>
                        <th align="center"><s:text name="label.productCovered"/></th>
                    </tr>
                </thead>
                <s:iterator value="costCategoriesMap.entrySet()" status="iter">
                    <tr height="60">
                        <td align="center">
                            <input type="checkbox" name="costCategories[<s:property value="#iter.index"/>]"
                                   id="costCategoriesChkBx_<s:property value="#iter.index"/>"
                                   value="<s:property value="%{key.id}"/>"
                                    <s:if test="%{key.code.equals('OEM_PARTS') || key.code.equals('NON_OEM_PARTS')|| key.code.equals('TRAVEL')}  ">
                                         disabled="disabled"
                                    </s:if>
                            />
                            <script type="text/javascript">
                                <s:if test="%{value}">
                                var index = <s:property value="%{#iter.index}"/>;
                                dojo.byId("costCategoriesChkBx_" + index).checked = true;
                                </s:if>
                            </script>
                        </td>
                        <td align="center">
                           <s:text name="%{getMessageKey(key.name)}"/>                           
                        </td>
                        <td align="center">
	                        <s:if test="%{key.code.equals('OEM_PARTS') || key.code.equals('NON_OEM_PARTS') || key.code.equals('TRANSPORTATION_COST')|| key.code.equals('TRAVEL')}">
	                            
	                             <s:text name="label.common.allProductTypes"/>
	                        </s:if>
	                        <s:else>
		                           <s:select id="ap" name="costCategories[%{#iter.index}].applicableProducts" 
		                            value="%{key.applicableProducts.{id.toString()}}"  size="5"
					                 multiple="true" 
					                 list="products"  cssClass="selectboxWidth" listKey="id" listValue="itemGroupDescription"  />
				           </s:else> 
			             </td>    
                    </tr>
                    <s:if test="%{key.code.equals('OEM_PARTS') || key.code.equals('NON_OEM_PARTS')|| key.code.equals('TRAVEL')}">
                         <s:hidden name="costCategories[%{#iter.index}]" value="%{key.id}"/>
                    </s:if>
                </s:iterator>
            </table>              
            </div>
            <div align="center" style="width:60%; margin-top:10px;">
            
            <span style="margin-left: 5px;" >
                            <img style="display: none;position:relative;left:40%;margin-top:15%" id="indicator" class="indicator" src="image/throbber.gif" alt="Loading..."></img>
                            <p id="textMsg" style="display: none;position:relative;left:30%"> <s:text name="message.costCategory.update.wait"/> </p>                      
                        <%--TODO: i18N me--%>
                <input class="buttonGeneric" type="button" value="<s:text name='button.common.update'/>" id="updateButton" onclick="submitForm(this);test()"/>
            </span><%--TODO: i18N me--%>
            </div>
        </div>
    </s:form>
       <script type="text/javascript">
    	    function test(){	    	
	    	 dojo.style(dojo.byId("categories"),"display","none");
		     dojo.style(dojo.byId("updateButton"),"display","none");
             dojo.style(dojo.byId("indicator"),"display","block");
             dojo.style(dojo.byId("textMsg"),"display","block");
	    }
	</script>
</u:body>
<authz:ifPermitted resource="warrantyAdminCostCategoryConfigurationReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('costCatForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('costCatForm'))[i].disabled=true;
	        }
	    });	    

	</script>
</authz:ifPermitted>
</html>