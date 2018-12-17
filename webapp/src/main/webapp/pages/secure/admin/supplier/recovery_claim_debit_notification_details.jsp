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
<%@taglib prefix="t" uri="twms"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>

<script type="text/javascript">

	/**
	 * Rounds the currency amount entered based on the currenly selected currency.
	 * Different currencies have different fraction digits allowed. If no
	 * currency is selected, then default of 2 fraction digits is considered
	 */
	function roundCurrencyValue(fieldId) {
		
		var field = document.getElementById(fieldId);
		
		var zeroFracArray = new Array("JPY", "ESP", "ITL", "BEF");
		var threeFracArray = new Array("BHD");
		
		var fieldValue = parseFloat(field.value);
		
		if (isNaN(field.value.trim())) {
			field.value = ""; // Making it blank, after checking if this is a number
		}		
		else if (isNaN(fieldValue)) {
			field.value = ""; // Making it blank after parsing it as a float
		} 
		else {
			
			var currencyOption = document.getElementById("debitMemoCurrencyStr");
			alert("Currency Option" + currencyOption);
			var currencyCode = currencyOption.options[currencyOption.selectedIndex].text;
			alert("Currency Code" + currencyCode);
			
			var scale = 2; // Default as 2, as it is fraction digit for maximum number of currencies
			
			if (zeroFracArray.indexOf(currencyCode) > -1) {
				alert("Index in zero frac:" + zeroFracArray.indexOf(currencyCode));
				scale = 0;
			}
			else if (threeFracArray.indexOf(currencyCode) > -1) {
				alert("Index in three frac:" + threeFracArray.indexOf(currencyCode));
				scale = 3;
			}
			else {
				scale = 2; // Default fraction digits is 2
			}
			
			field.value = parseFloat(fieldValue).toFixed(scale);
		}
	}	
</script>

<div dojoType="twms.widget.TitlePane" title="Debit Notification Details" labelNodeClass="section_header">
<table>
<tr>
		  <td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.debitMemoNumber"/>:</td>
		  <td width="30%">
		    <s:textfield name="debitMemoNumber"/>
		  </td>
		   
		  <td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.debitMemoDate"/>:</td> 			  
          <td width="30%">
            <sd:datetimepicker name='debitMemoDate' value='%{debitMemoDate}' id='debitDate' />
          </td>
</tr>
<tr>
		  <td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.debitMemoAmount"/>:</td>
		  <td width="30%">
		    <s:textfield id="debitMemoAmountStr" name="debitMemoAmountStr" onchange="roundCurrencyValue('debitMemoAmountStr')"/>
		  </td>
		   
		  <td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.common.debitMemoCurrency"/>:</td> 			  
          <td width="30%">
            <s:select id="debitMemoCurrencyStr" name="debitMemoCurrencyStr" cssStyle="width:180px;"
            		headerKey="-1" headerValue="-- Select a currency --"
                	list="currencies" onchange="roundCurrencyValue('debitMemoAmountStr')" />
          </td>
</tr>

<tr>

			<td width="20%" class="labelStyle" nowrap="nowrap"><s:text name="label.supplierRecovery.memoComments"/>:</td>
		  	<td width="30%" style="padding-top: 15px;">
		    	<t:textarea rows="4" cols="80" name="debitMemoComments"
              	wrap="physical" cssClass="bodyText" maxLength="4000" value=""/>
		  	</td>
</tr>
</table>
</div>