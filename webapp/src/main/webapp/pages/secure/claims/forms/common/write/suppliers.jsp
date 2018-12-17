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
<%--
  @author Sushma.manthale
--%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<table class="grid" width="100%">
	<tr>
		<td width="20%" class="labelStyle">
			<s:text name="label.common.causalPart" /> : 
		</td>
		<td class="labelStyle">
			<s:property value="task.claim.serviceInformation.causalPart" />
		</td>
	</tr>
</table>
<table  width="100%" cellpadding="2" cellspacing="0" class="grid borderForTable" align="center">
	<thead>
	<tr class="row_head">
		<th><s:text name="label.supplier.supplierNumber" /></th>
		<th><s:text name="label.common.supplierName" /></th>
		<th><s:text name="label.supplierPartNumber" /></th>
		<th><s:text name="label.sra.partSouce.supPartDescription" /></th>
	</tr>
	</thead>
	<tbody>
	<s:iterator value="supplierItems" status="supplierItemStatus" id="supplierItem">
		<s:set name="partSupplier" value="%{getSupplierById(ownedBy.id)}" />
		<!-- Part supplier can be passed as Null if supplier is INACTIVE  -->
		<s:if test="#partSupplier!=null">
			<tr>
				<td><s:property value="%{getSupplierById(ownedBy.id).supplierNumber}" /></td>
				<td><s:property value="ownedBy.name" /></td>
				<td><s:property value="number" /></td>
				<td width="40%"><s:property value="name" /></td>
	        </tr>
        </s:if>
    </s:iterator>
   	</tbody>
</table>