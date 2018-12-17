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
<!--  Used the File name as an included page in the detail.jsp of /draft_claim/detail.jsp and /search_result/detail.jsp -->
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<u:stylePicker fileName="batterytestsheet.css" />
<u:stylePicker fileName="official.css" />
<head>

</head>

<table width="100%">
	<thead>
		<tr class="title" width="100%">
            <th align="center" width="50%"><s:text name="label.newClaim.hussmanPartReplaced"/></th>
            <th align="center" width="50%"><s:text name="label.newClaim.hussmanPartsInstalled"/></th>            
        </tr>
		
		<tr class="row_head">
			<th align="center" width="50%">
				<table width="100%" height="100%" >
				  <tr class="repeatTable_row_head">
					<th width="50%">
						<s:text	name="label.newClaim.partNumber" />
					</th>
					<th width="50%">
						<s:text name="label.common.quantity" />
					</th>					
				 </tr>
				</table>
			</th>
			<th align="center" width="50%">
				<table width="100%" height="100%" >
				  <tr class="repeatTable_row_head">
					<th width="50%">
						<s:text	name="label.newClaim.partNumber" />
					</th>
					<th width="50%">
						<s:text name="label.common.quantity" />
					</th>					
				 </tr>
				</table>
			</th>
		</tr>
	</thead>
	<tbody id="readAddRepeatBody" >	
		<s:if test="hussmanPartsReplacedInstalledRead != null && !hussmanPartsReplacedInstalledRead.isEmpty()">		                        
					<s:iterator	value="hussmanPartsReplacedInstalledRead" status="readMIndex">
						<tr>
																				
							<s:if test="hussmanPartsReplacedInstalledRead[#readMIndex.index].replacedParts != null && ! hussmanPartsReplacedInstalledRead[#readMIndex.index].replacedParts.isEmpty()">

										<td>
										<s:hidden name="hussmanPartsReplacedInstalledRead[%{#readMIndex.index}].readOnly" value="true"/>
											<table width="100%" height="100%">
											<s:iterator	value="hussmanPartsReplacedInstalledRead[#readMIndex.index].replacedParts"
													status="replacedRdIndx">
												<tr>
													<td width="50%">
														<s:property value="hussmanPartsReplacedInstalledRead[#readMIndex.index].replacedParts[#replacedRdIndx.index].itemReference.referredItem.number"/>
														<s:hidden name="hussmanPartsReplacedInstalledRead[%{#readMIndex.index}].replacedParts[%{#replacedRdIndx.index}].itemReference.referredItem" 
														value="%{hussmanPartsReplacedInstalledRead[#readMIndex.index].replacedParts[#replacedRdIndx.index].itemReference.referredItem.number}" />
														<s:hidden name="hussmanPartsReplacedInstalledRead[%{#readMIndex.index}].replacedParts[%{#replacedRdIndx.index}].readOnly" 
														  value="true"/>
 
														
													</td>
													<td width="50%">
														<s:property value="hussmanPartsReplacedInstalledRead[#readMIndex.index].replacedParts[#replacedRdIndx.index].numberOfUnits" />
														<s:hidden name="hussmanPartsReplacedInstalledRead[%{#readMIndex.index}].replacedParts[%{#replacedRdIndx.index}].numberOfUnits" 
														value="%{hussmanPartsReplacedInstalledRead[#readMIndex.index].replacedParts[#replacedRdIndx.index].numberOfUnits}" />
													</td>																											

												</tr>
									        </s:iterator>	
											</table>
										</td>

							</s:if>
							<s:else>
								<td>
								<table width="100%" height="100%">
									<tr>
										<td width="50%">
										</td>
										<td width="50%">
										</td>
									</tr>
								</table>
								</td>
							</s:else>
							<s:if test="hussmanPartsReplacedInstalledRead[#readMIndex.index].hussmanInstalledParts != null && !hussmanPartsReplacedInstalledRead[#readMIndex.index].hussmanInstalledParts.isEmpty()">
										<td>
											<table width="100%" height="100%">
											<s:iterator	value="hussmanPartsReplacedInstalledRead[#readMIndex.index].hussmanInstalledParts"
													status="replacedRdIndx">
											
												<tr>
													<td width="50%">
														<s:property value="hussmanPartsReplacedInstalledRead[#readMIndex.index].hussmanInstalledParts[#replacedRdIndx.index].item.number"/>
														<s:hidden name="hussmanPartsReplacedInstalledRead[%{#readMIndex.index}].hussmanInstalledParts[%{#replacedRdIndx.index}].item" 
														value="%{hussmanPartsReplacedInstalledRead[#readMIndex.index].hussmanInstalledParts[#replacedRdIndx.index].item.number}" />
														<s:hidden name="hussmanPartsReplacedInstalledRead[%{#readMIndex.index}].hussmanInstalledParts[%{#replacedRdIndx.index}].readOnly"
														value="true" />
													</td>
													<td width="50%">
														<s:property value="hussmanPartsReplacedInstalledRead[#readMIndex.index].hussmanInstalledParts[#replacedRdIndx.index].numberOfUnits"/>
														<s:hidden name="hussmanPartsReplacedInstalledRead[%{#readMIndex.index}].hussmanInstalledParts[%{#replacedRdIndx.index}].numberOfUnits" 
														value="%{hussmanPartsReplacedInstalledRead[#readMIndex.index].hussmanInstalledParts[#replacedRdIndx.index].numberOfUnits}" />
													</td>

												</tr>
											</s:iterator>	
											</table>
										</td>
								
							</s:if>
							<s:else>
								<td>
								<table width="100%" height="100%">
									<tr>
										<td width="50%">
										</td>
										<td width="50%">
										</td>
									</tr>
								</table>
								</td>
							</s:else>
					</tr>					
					
					</s:iterator>			
		</s:if>		
	</tbody>
</table>