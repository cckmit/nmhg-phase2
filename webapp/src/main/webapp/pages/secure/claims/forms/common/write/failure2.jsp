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

<table class="grid" cellspacing="0" cellpadding="0" id="failure_details_table">
    <tr>
        <td>
            <label for="causalPart"><s:text name="label.common.causalPartNumber"/>:</label>
        </td>
        <td>
            <s:if test="task.partsClaim">
                <s:property value='%{task.claim.serviceInformation.causalBrandPart.itemNumber}' />
            </s:if>
        </td>
        <td class="labelStyle">
			<s:text name="label.common.causalPartDescription" />:
		</td>
        <td colspan="2">
            <span id="causalPartDescription">
                <s:property value="task.claim.serviceInformation.causalPart.description"/>
            </span>
        </td>
		<td nowrap="nowrap"><s:a cssStyle="cursor:pointer" href="#">
			<span id="causalPartAttributeLink"> </span>
		</s:a></td>
    </tr>
</table>

<div id = "dialogBoxContainer" style="display: none">
	<div dojoType="twms.widget.Dialog" id="causalPartAtrribute" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
		toggleDuration="250" title="<s:text name="label.common.additionalAttributes"/>" style="width:550px; height:300px;" >
		<div style="background: #F3FBFE; border: 1px solid #EFEBF7">
		 	<div dojoType="dojox.layout.ContentPane" id="causalPartAttributeContentPane"
                  layoutAlign="top" style="height: 250px;">
				<jsp:include flush="true" page="causal-part-claim_attribute.jsp" />
		  </div>
	    </div>
	</div>
</div>


<script type="text/javascript" src="scripts/ServiceProcedureTree.js"></script>
<script type="text/javascript">
var isCausalDialogDisplayed=false;
dojo.addOnLoad(function(){	
	 var causalAttributePresent= '<s:property value="task.claim.serviceInformation.partClaimAttributes.size"/>' >0;
     
     dijit.byId('causalPartAtrribute').formNode=document.getElementById("claim_form");
     if(causalAttributePresent){
         dojo.byId("causalPartAttributeLink").innerHTML='<s:text name="label.additionalAttribute.enterAttribute"/>';
     }
     dojo.connect(dojo.byId("causalPartAttributeLink"),"onclick",function(){	            
         dojo.publish("/causal/attribute/show");
     });
     dojo.subscribe("/causal/attribute/show", null, function() {
         var dlg = dijit.byId("causalPartAtrribute");
        	var partNumber = '<s:property value="task.claim.serviceInformation.causalPart.number"/>'
             if(! isCausalDialogDisplayed){
                 var claim='<s:property value="task.claim.id"/>';
                 var params = {
                     claimDetails: claim,
                     partNumber: partNumber
                 };
                 twms.ajax.fireHtmlRequest("getAttributesForCausalPart.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />", params,
                     function(data) {
                         var parentContentPane = dijit.byId("causalPartAttributeContentPane");
                         parentContentPane.setContent(data);
                         isCausalDialogDisplayed = true;
                     }
                 );
             }
             dlg.show();
     });
				    
})	;
	       
</script>
    