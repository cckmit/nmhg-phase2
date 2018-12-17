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

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<script type="text/javascript" src="scripts/ui-ext/common/tabs.js"></script>

<div class="policy_section_div">
 <div class="section_header"><s:text name="label.contractAdmin.itemsCovered" /> </div>
       
     <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid borderForTable">
    
            <tr class="row_head">
        	<th width="10%" ><s:text name="label.contractAdmin.partNumber" /></th>
        	<th width="25%" ><s:text name="label.common.description"/></th>
            <th width="15%" ><s:text name="columnTitle.duePartsInspection.supplier_part_no" /></th>
            <th width="25%"><s:text name="label.sra.partSouce.supPartDescription" /></th>
            <th width="10%" ><s:text name="label.sra.partSource.fromDate" /></th>
            <th width="10%" ><s:text name="label.sra.partSource.toDate" /></th>
            </tr>
            <s:iterator value="supplierItems" status="status">
              <tr>
	        	<td >
					<s:property value="itemMapping.fromItem.number"/>	
	            </td>
	            <td ><span>
	                	<s:property value='itemMapping.fromItem.description' />
					</span>	
	            </td >
	            <td >
	            	<s:property value="itemMapping.toItem.number"/>
	            </td>
	            <td >
	                <s:property value="itemMapping.toItem.description"/>
	            </td>
	            <td >
	                <s:property value="fromDate"/>
	            </td>
	            <td >
	                <s:property value="toDate"/>
	            </td>
	           </tr> 	
            </s:iterator>            
        
    </table>
</div>