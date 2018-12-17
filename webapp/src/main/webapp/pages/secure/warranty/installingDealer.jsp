<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<table width="80%">
	<tr>
		<td width="20%" nowrap="nowrap" class="labelNormalTop labelStyle"
			style="padding-left: 3px;"><s:text
			name="label.common.installingDealer" />:</td>
		<td width="80%" class="labelNormalTop"><sd:autocompleter id='installingDealerNameAutoComplete' showDownArrow='false' href='list_warranty_reg_dealer_name_value_id.action' cssStyle='width:200px' name='installingDealer' keyName='installingDealer' value='%{installingDealer.name}' key='%{installingDealer.id}' />
			<script type="text/javascript">
dojo.addOnLoad(function(){	
	var installingDealer =  dijit.byId("installingDealerNameAutoComplete");	
	if(installingDealer)
	{
	installingDealer.fireOnLoadOnChange=false;
	dojo.connect(installingDealer,"onChange",function(){		
			var indexList =  dojo.query("input[id $= 'indexFlag']"); 
			var nameList =  dojo.query("input[id $= 'nameFlag']");  
			for(var i=0;i<indexList.length;i++){				
           		getAllPolicies(indexList[i].value,nameList[i].value);
        }
			try{
					if(dojo.byId("modify_warranty")){
						getAllPoliciesForEdit(0,0);
					}
				}
				catch(e){
					console.debug("Not for Modify Warranty" +e);
				}
	});
	}
});
</script>
			</td>
	</tr>
</table>