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

<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
    <title><s:text name="title.common.warranty"/></title>
    <s:head theme="twms"/>

    <STYLE>
    .partsHeadertitle {
		background-color: #DCE9F7;
		font-family: Arial, Helvetica, sans-serif;
		text-transform: uppercase;
		font-size: 8.5pt;
		font-style: normal;
		font-weight: 700;
		padding: 2px;
		text-align: center;
		height: 20px;
		color: #5577B4;
	}
	
	.policy_section_div {
		border:1px solid #EFEBF7;
		background-color:#F3FBFE;
		color:#545454;
		font-family:Arial, Helvetica, sans-serif;
		font-size:9pt;
		font-style:normal;
		font-weight:400;
		text-align:left;
		vertical-align:middle;
		margin:5px;
		width:99%;
	}
	.centresubtitle{
		background-color:#DCE9F7;
		border-bottom:2px solid #EFEBF7;
		font-family:Arial, Helvetica, sans-serif;
		text-transform:uppercase;
		font-size:9pt;
		font-style:normal;
		font-weight:700;
		padding:2px;
		text-align:center;
		height:25px;
		color:#5577B4;
	}
	table.dealertable, table.parttable {
	margin-left:1px;
	margin-right:1px;
	border: 1px solid #c2c2c2;
	padding-left:0px;
	background-color:#F3FBFE;
}
	table.dealertable {
		width:100%;
	}
	.dealertable tr th{
		border:1px solid #ffffff;
	}
	.dealertable tr td {
		border:1px solid #EFEBF7;
	}
	.labelBold {
		color:#545454;
		font-family:Arial, Helvetica, sans-serif;
		font-size:9pt;
		font-weight:bold;
		font-style:normal;
		vertical-align:top;
		padding-left:20px;
		text-align:left;
		line-height:20px
	}
	.labelBoldAndBig {
		color:#545454;
		font-family:Arial, Helvetica, sans-serif;
		font-size:20pt;
		font-weight:bold;
		font-style:normal;
		vertical-align:top;
		padding-left:20px;
		text-align:left;
		line-height:20px
	}
	.labelLargeFont {
		color:#545454;
		font-family:Arial, Helvetica, sans-serif;
		font-size:11pt;
		font-weight:bold;
		font-style:normal;
		vertical-align:top;
		padding-left:20px;
		text-align:left;
		line-height:20px
	}
	.section_header {
		background-color:#dce9f7;
		font-family:Arial, Helvetica, sans-serif;
		font-size:8.5pt;
		font-weight:700;
		color:#5577B4;
		height:28px;
		padding-left:7px;
		padding-top:0px;
		text-transform:uppercase;
		line-height:28px;
	}
	.label {
		color:#545454;
		font-family:Arial, Helvetica, sans-serif;
		font-size:9pt;
		font-style:normal;
		font-weight:bold;
		padding-left:5px;
	
	}
	.labelNormal {
		color:#545454;
		font-family: Arial, Helvetica, sans-serif;
		font-size:9pt;
		font-style:normal;
		font-weight:400;
		padding-left:5px;
		height: 18px;
		line-height: 18px;
		text-align:left;
		vertical-align:middle;
	}
	.grid {
		font-family:Arial, Helvetica, sans-serif;
		font-size:9pt;
		width:97%;
		padding-top:10px;
	}
	.warColDataBg {
		color:#000;
		text-decoration:none;
		padding-left:4px;
		line-height:20px;
		text-align: left;
		font-size:9pt;
		background-color:#FFFFFF;
	}
	.buttonWrapperPrimary {
		padding-top:7px;
		padding-bottom:7px;
		text-align:center
	}
	.buttonGeneric {		
		border:1px solid #EFEBF7;
		background-position:left center;
		background-repeat:repeat-x;
		border:#9B9B9B 1px solid;
		color:#6D7177;
		cursor:pointer;
		font-family: Arial, Helvetica, sans-serif;
		font-size:9pt;
		font-style:normal;
		overflow:visible;
		height:20px;
	}
	.borderForTable{
		border:1px solid #EFEBF7;
	}
	.borderForTable tr td{
		border:1px solid #EFEBF7;
	}
	.borderTable{
		background:url(../../../image/tableBorder.gif) repeat-x;
		width:98%;
		margin-left:5px;
	}
	@media print {
		input.noPrint { display: none; }
	}
    </STYLE>
</head>
<u:body>
<script type="text/javascript">
dojo.require("dijit.layout.LayoutContainer");
</script>
<u:actionResults/>
<br>
<s:if test="!isBuConfigAMER()">
  <div align="left"><img src="image/NMHG_Header.png" alt="tavant"
				    width="300" height="90" /></div>
</s:if>
<s:if test="isBuConfigAMER()">
  <div align="left"><img src="image/logo_NMHG.png" alt="tavant"
				    width="300" height="90" /></div>
</s:if>
<div style="margin:5px;width:99%" class="policy_section_div">
   <table  class="dealertable" width="100%" cellspacing="0" cellpadding="0">	
	    <tr>
	    	<td width="59%" class="centresubtitle"> <s:text name="label.common.internalUseOnly"/></td>
	    	
			<td width="40%" class="centresubtitle"> <s:text name="label.returnToAddress"/></td>
	    </tr>
    </table>	
	<table  class="dealertable" width="100%" cellspacing="0" cellpadding="0">
	    <tr>
	      <td width="59%">
		      <table>
			      <tr>
			      	<td class="labelBold"><s:text name="columnTitle.partShipperPartsShipped.vpraNumber" /> :</td>
			      	<td  class="labelBoldAndBig"><s:property value="%{shipment.id}"/></td>
			      </tr>
			      <tr>
			      	<td class="labelBold">
			      	   <s:text name="columnTitle.partShipperPartsClaimed.location"/>:
			      	</td>
			      	 	<td  class="labelBoldAndBig"><s:property value="%{shipment.destination.code}"/></td>
			      </tr>
			   <s:if test="isBuConfigAMER()">
			       <tr>
			      	<td class="labelBold">
			      	   <s:text name = "label.common.date"/>:
			      	</td> <td class="labelBoldAndBig"><s:property value="shipment.getShipmentDateForPartsCollected()"/></td>
			      </tr>	
			      </s:if>
			    <s:if test="!isBuConfigAMER()">
			    			       <tr>
			      	<td class="labelBold">
			      	   <s:text name = "label.common.date"/>:
			      	</td> <td class="labelBoldAndBig"><s:property value="%{shipment.shipmentDate}"/></td>
			      </tr>	
			    </s:if>     
		      </table>
	      </td>
	      <td width="60%" class="labelLargeFont">
			<s:text name="label.shipment.returnPartTo"/>:<br>
			<s:property value="%{shipment.supplierPartReturns[0].recoverablePart.oemPart.appliedContract.supplier.name}"/><br>
			<s:if test="(shipment.contactPersonName == null ||  shipment.contactPersonName.isEmpty())">
				<s:property value="%{shipment.destination.address.contactPersonName}"/><br>
			</s:if>
			<s:else>
				<s:property value="%{shipment.contactPersonName}"/><br>
			</s:else>
			<s:property value="%{shipment.destination.address.addressLine1}"/><br>
			<s:property value="%{shipment.destination.address.addressLine2}"/> <br>
			<s:property value="%{shipment.destination.address.city}"/>, <s:property value="%{shipment.destination.address.state}"/><br>
			<s:property value="%{shipment.destination.address.country}"/> - <s:property value="%{shipment.destination.address.zipCode}"/><br>
		  </td>
		</tr>	
    </table>		    	
	    
<div class="section_header"><s:text name="label.manageWarehouse.partShipper" /></div>
		<table  border="0" cellspacing="0" cellpadding="0" class="grid" style="margin-top:5px;">
			<tr>
				<td width="16%" valign="top" class="label"><s:text 
					name="label.address" />:</td>
				<td class="bodyText" valign="top">
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td nowrap="nowrap" class="labelNormal"><s:property
							value="shipment.supplierPartReturns[0].recoverablePart.oemPart.activePartReturn.returnLocation.code" /></td>
					</tr>
					<tr>
						<td nowrap="nowrap" class="labelNormal"><s:property
							value="shipment.supplierPartReturns[0].recoverablePart.oemPart.activePartReturn.returnLocation.address.addressLine1" /><br/>
						<s:property value="shipment.supplierPartReturns[0].recoverablePart.oemPart.activePartReturn.returnLocation.address.addressLine2" /></td>
					</tr>
					<tr>
						<td nowrap="nowrap" class="labelNormal"><s:property
							value="shipment.supplierPartReturns[0].recoverablePart.oemPart.activePartReturn.returnLocation.address.city" />, <s:property
							value="shipment.supplierPartReturns[0].recoverablePart.oemPart.activePartReturn.returnLocation.address.state" /> <s:property
							value="shipment.supplierPartReturns[0].recoverablePart.oemPart.activePartReturn.returnLocation.address.zipCode" /></td>
					</tr>
					<tr>
						<td nowrap="nowrap" class="labelNormal"><s:property
							value="shipment.supplierPartReturns[0].recoverablePart.oemPart.activePartReturn.returnLocation.address.country" /></td>
					</tr>
				</table>
				</td>
			</tr>
		</table>
</div>

<div id="separator"></div>
<div style="margin:5px;width:99%" class="policy_section_div">
	<div class="section_header" align="center"><s:text 
					name="label.listOfPartsToBeReturned" /></div>
		<s:iterator value="claimsInShipment" status="status" >
		<table  border="0" cellspacing="0" cellpadding="0" class="grid borderForTable" style="margin:5px;width:99%">
			<tr>
	     		<td class="labelBold" width="20%"><s:text name="label.common.claimNumber"/>:</td>
	  			<td class="labelBold" width="85%">
	  			<s:if test="key.documentNumber !=null && key.documentNumber.length() > 0" >
		            <s:property value="key.recoveryClaimNumber" />-<s:property
				  value="key.documentNumber" />
	             </s:if>
		         <s:else>
		           <s:property value="key.recoveryClaimNumber" />
		          </s:else>	 
	  			</td>
			</tr>
			<tr><td><br></td></tr>
			<tr>
	     		<td class="labelBold" width="20%"><s:text name="columnTitle.listContracts.supplier_name"/>:</td>
	  			<td class="labelNormal" width="85%">
		  			<s:property value="key.contract.supplier.name"/>
		  			-
		  			<s:property value="key.contract.supplier.supplierNumber"/>
	  			</td>
			</tr>
			
			<tr>
     			<td class="labelBold" width="20%"><s:text name="label.common.serialNumber"/>:</td>
                <td height="15"   colspan="5">
                     <table>
                       <s:set name="rowCounter" value="0"/>
                       <s:iterator value="key.claim.claimedItems">
                           <s:if test="#rowCounter==0">
                           	<tr>
                           </s:if>
                           <s:if test="#rowCounter<4">
                               <td  class="labelNormal" width="85%">
                                   <s:property value="itemReference.referredInventoryItem.serialNumber"/>
                               </td>
                               <s:set name="rowCounter" value="#rowCounter+1"/>
                           </s:if>
                           <s:else>
                               <td  class="labelNormal" width="85%">
                                   <s:property value="itemReference.referredInventoryItem.serialNumber"/>
                               </td>
                              <s:set name="rowCounter" value="0"/>
                           </s:else>
                            <s:if test="#rowCounter!=0">
                               </tr>
                           </s:if>
                        </s:iterator>
                      </table>
       			</td>
	   		</tr>
	  	   <tr align="center">
	  		<td class="labelBold" width="20%"><s:text name="label.common.dimensionsComments" />:</td>
	  		<td width="85%" align="left">
			  <s:property value="%{shipment.comments}"/>
	  		</td>
	  		</tr>			
			<tr align="center">
				<td class="labelBold" width="20%"><s:text name="label.supplier.partDetails" /></td>
				<td width="85%">
				<table border="0" cellspacing="0" cellpadding="0" class="grid borderForTable">
					<tr><td width="1%"></td>
					<td class="partsHeadertitle" width="20%"><s:text
							name="label.supplierPartNumber" /></td>
						<td class="partsHeadertitle" width="20%"><s:text
							name="columnTitle.dueParts.part_no" /></td>
						<td class="partsHeadertitle" width="20%"><s:text
							name="label.description" /></td>
						<td class="partsHeadertitle" width="20%"><s:text
								name="label.quantity" /></td>
						<td class="partsHeadertitle" width="20%"><s:text
								name="label.manageWarehouse.bins" /></td>			
						<td class="partsHeadertitle" width="20%"><s:text
							name="columnTitle.recoveryClaim.rgaNumber" /></td>
					</tr>
						<s:iterator value="value">
							<tr><td width="1%"></td>
							 <td align="center"><s:property
									value="recoverablePart.supplierItem.number" />
							 </td>
							<td  align="center"><s:property
								value="recoverablePart.oemPart.itemReference.unserializedItem.number" />
							</td>
							<td  align="center"><s:property
								value="recoverablePart.oemPart.itemReference.unserializedItem.description" />
							</td>
							<td  align="center">
								<s:property value="recoverablePart.receivedFromSupplier" />
							</td>
							<td  align="center">
								<s:property value="recoverablePart.oemPart.activePartReturn.warehouseLocation" />
							</td>
							<td  align="center"><s:property value="rgaNumber" /></td>
							</tr>
						</s:iterator>
				</table>
				</td>
			</tr>
		</table>
</s:iterator>		
</div>
<div class="buttonWrapperPrimary"><input type="button"
	name="Submit223" value='<s:text name="button.common.cancel" />' class="buttonGeneric"
	onclick="window.close()" /> <input type="button" name="Submit2232"
	value='<s:text name="button.common.print" />' class="buttonGeneric" onclick="window.print()" /></div>
	<s:if test="!isBuConfigAMER()">
	<div align="center">
		<img src="image/NMHG_Footer.png" alt="tavant"/>
	</div>	
	</s:if>					

</u:body>
</html>