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
<%@page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<script type="text/javascript">
    var rowCount =<s:property value = "thirdParties.size" />;  
</script>

<script type="text/javascript" src="scripts/twms-widget/twms.js"></script>

<s:form method="post" theme="twms" id="thirdPartyResultPageForm" name="thirdPartySelection"
        action="selectedThirdParty.action">
<div dojoType="dijit.layout.ContentPane" layoutAlign="top">
<div id="thirdPartyResultSection" dojoType="dojox.layout.ContentPane" class ="multiInventorySearchResults" layoutAlign="client">
<table width="98%" class="repeat borderForTable">
    <thead>        
        <tr class="row_head">
            <th width="12%"><s:text name="label.common.select"/></th>
            <th width="20%"><s:text name="label.claim.thirdPartyName"/></th>
            <th width="20%"><s:text name="label.claim.thirdPartyNumber"/></th>
            <th width="20%"><s:text name="label.address"/></th>
            <th width="20%"><s:text name="label.city"/></th> 
            <th width="8%"><s:text name="label.country"/></th>                     
        </tr>
    </thead>
    <tbody>
    	<s:if test="thirdParties.empty">
    	<tr >
    		<td colspan="6" align ="center">
    			<s:text name="label.claim.noThirdPartyFound"/>
    		</td>
    	</tr>     
    	</s:if>
    	<s:else>
        <s:iterator value="thirdParties" status="thirdParties" id="thirdPartiesList">
            <tr id="listCounter">			
 				<td width="12%">
                	<div align="center"> 
                		<a class="Datalink" 
                			id="thirdParty_<s:property value="%{#thirdParties.index}"/>">
                			<s:text name="label.common.select"/>
                		</a> 
			   		</div>
                </td>
				<script type="text/javascript">
					dojo.connect(dojo.byId("thirdParty_<s:property value="%{#thirdParties.index}"/>"),"onclick",function(){
						//setting escape to false when pass name so that spl. chars like & is passed as is
						selectThirdParty("<s:property value="id"/>","<s:property value="name" escape="false"/>");
					});
				</script>
                <td width="20%"><s:property value="name"/></td>
                <td width="20%"><s:property value="thirdPartyNumber"/></td>                
                <td width="20%"><s:property value="address.addressLine1" /></td>               
                <td width="20%"><s:property value="address.city" /></td>
                <td width="8%"><s:property value="address.country" /></td>                  
            </tr>
        </s:iterator>
        </s:else>
    </tbody>
</table>
<div>
<center>
<s:iterator value="pageNoList" status="pageCounter">
&nbsp;
<s:if test="pageNoList[#pageCounter.index] == (pageNo + 1)">
	<span id="page_<s:property value="%{intValue()-1}"/>" >
</s:if>	
<s:else>
	<span id="page_<s:property value="%{intValue()-1}"/>" style="cursor:pointer;text-decoration:underline" >
</s:else>
<s:property value="%{intValue()}" /></span>
<script type="text/javascript">
	dojo.addOnLoad(function(){	
		var index = '<s:property value="%{intValue()-1}"/>';
		var pageNo='<s:property value="pageNo"/>';
		if(index!=pageNo){
			dojo.connect(dojo.byId("page_"+index),"onclick",function(){
				getThirdParties(index);  
			});
		}	 
	});
	
</script>
</s:iterator>
</center>
</div>
</div>
</div>


<script type="text/javascript">
function getThirdParties(index)
{
	
	var params={
		pageNo:index
	};
	params["thirdPartySearch.thirdPartyName"]="<s:property value="name"/>";
	params["thirdPartySearch.thirdPartyNumber"]="<s:property value="thirdPartyNumber"/>";
	var url = "searchThirdPartiesForClaim.action?";
	var targetContentPane=dijit.byId("thirdPartyResultDiv");
	
	targetContentPane.destroyDescendants();
	targetContentPane.domNode.innerHTML="<div class='loadingLidThrobber'><div class='loadingLidThrobberContent'></div></div>";
	
	twms.ajax.fireHtmlRequest(url, params, function(data) {
				var parentContentPane = dijit.byId("thirdPartyResultDiv");
				parentContentPane.setContent(data);				
			}
	        );
}
</script>

</s:form>