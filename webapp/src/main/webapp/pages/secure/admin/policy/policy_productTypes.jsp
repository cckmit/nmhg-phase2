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

<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<u:stylePicker fileName="form.css" />
<u:repeatTable id="products_table" cssClass="grid borderForTable"
	width="97%">
	<thead>
		<tr class="row_head">
			<th><s:text name="label.common.productModel"/></th>
			<th id="dedAmt"><s:text name="label.policyDefinition.deductibleAmount"/>
			</th>
			<th width="9%"><u:repeatAdd id="products_adder"><div class="repeat_add"></div></u:repeatAdd></th>
		</tr>
	</thead>
	<u:repeatTemplate id="products_body"
		value="selectedProducts">
		<tr index="#index">
			<td>
			<sd:autocompleter name='selectedProducts[#index].product.description'
					id='productAutocompleter_#index' cssStyle='width:505px;'
					loadOnTextChange='true' showDownArrow='false'
					href='list_products.action' loadMinimumCount='1'
					keyName='selectedProducts[#index].product'
					value='%{selectedProducts[#index].product.getNameAndParentName()}' key='%{selectedProducts[#index].product.id}' /></td>
			
			<td id="deductible_div_#index">
			<t:money id="deductible_#index"
                    name="selectedProducts[#index].deductibleFee"
                    value="%{selectedProducts[#index].deductibleFee}"
                    defaultSymbol="%{baseCurrency}" size="10"/>
                    
                    <script type="text/javascript">
		dojo.addOnLoad(function(){
		deductibleAmountField(deductible_div_#index);
	});
</script>
			</td>
			<td><u:repeatDelete id="products_deleter_#index">
                    <div class="repeat_del" ></div>
			</u:repeatDelete>
			</td>			
		</tr>
	</u:repeatTemplate>
</u:repeatTable>
<script>
dojo.addOnLoad(function() {
	var policyDefWarrantyType='<s:property value="policyDefinition.warrantyType"/>';
	var bu="<s:property value="getCurrentBusinessUnit().getName()"/>"
	if(dijit.byId("warrantyType")!=null){
		if(dijit.byId("warrantyType").getValue() =="POLICY" || dijit.byId("warrantyType").getValue() =="STANDARD" || bu!='AMER'){
			dojo.html.hide(dojo.byId("dedAmt"));
		}
		
		 dojo.connect(dijit.byId("warrantyType"), "onChange", function() {
			 if (dijit.byId("warrantyType").getValue() == "EXTENDED" && bu =='AMER') {
				 dojo.html.show(dojo.byId("dedAmt"));
             }else{
            	 dojo.html.hide(dojo.byId("dedAmt"));
             }
			 
		 });
	}
	if(policyDefWarrantyType!=null){
		if((policyDefWarrantyType =="POLICY" || policyDefWarrantyType =="STANDARD") || bu!='AMER'){
			dojo.html.hide(dojo.byId("dedAmt"));
		}
	}
 	
 });
</script>

<script type="text/javascript">
function deductibleAmountField(index) {
	var policyDefWarrantyType='<s:property value="policyDefinition.warrantyType"/>';
	var bu="<s:property value="getCurrentBusinessUnit().getName()"/>"
	if(dijit.byId("warrantyType")!=null){
		if((dijit.byId("warrantyType").getValue() =="POLICY" || dijit.byId("warrantyType").getValue() =="STANDARD") ||bu!='AMER'){
			dojo.html.hide(dojo.byId("dedAmt"));
			dojo.html.hide(dojo.byId(index));
		}
		 dojo.connect(dijit.byId("warrantyType"), "onChange", function() {
			 if (dijit.byId("warrantyType").getValue() == "EXTENDED" && bu =='AMER') {
				 dojo.html.show(dojo.byId("dedAmt"));
				 dojo.html.show(dojo.byId(index));
             }else{
            	 dojo.html.hide(dojo.byId("dedAmt"));
            	 dojo.html.hide(dojo.byId(index));
             }
			 
		 });
	}
	if(policyDefWarrantyType!=null){
		if((policyDefWarrantyType =="POLICY" || policyDefWarrantyType =="STANDARD") ||bu!='AMER'){
			dojo.html.hide(dojo.byId("dedAmt"));
			dojo.html.hide(dojo.byId(index));
		}
	}
}
</script>