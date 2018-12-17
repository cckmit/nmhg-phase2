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
<div class="admin_section_div" align="left" width="100%"/>
 <div style="overflow: auto;width:100%;">
	<table width="100%">
	  <tr>
		<th width="5%" class="admin_table_heading">&nbsp;</th>
	  	<th class="admin_table_heading"><s:text name="label.common.partNumber"/></th>
	    <th class="admin_table_heading"><s:text name="label.common.description"/></th>
	  </tr>
    	<tr>
            <td class="admin_table_subheading" align="center"></td>
			<td colspan="2" align="center" class="admin_table_subheading"><s:text name="label.manageGroup.alreadySelectedItems"/></td>
		</tr>
        <s:iterator value="includedItems">
            <tr>
                <td align="center" class="admin_selections">
                    &nbsp;
                </td>
                <td class="admin_selections" ><s:property value="number"/></td>
              <td class="admin_selections"><s:property value="description"/></td>
            </tr>
		</s:iterator>
      <tr>
			<td width="5%" class="admin_table_subheading" align="center"><input type="checkbox" onclick="toggleOptions('available', this.checked)" /></td>
			<td colspan="2" align="center" class="admin_table_subheading"><s:text name="label.manageGroup.selectMoreItems"/></td>
		</tr>
	  	<s:iterator value="items" status="iter">
		<s:if test="%{includedItems.contains(top)}"></s:if>
	  	<s:else>
  		<tr>
	  		<td align="center">
				<s:if test="%{availableItems.contains(top)}">
                    <input type="checkbox" name="available" 
                           value="{'id':'<s:property value="top.id"/>',
                           'itemNumber':'<s:property value="top.number"/>',
                           'itemDescription':'<s:property value="top.description"/>'}"/>
	  			</s:if>
	  			<s:else>
	  				<input type="checkbox" value="<s:property value="top.number"/>" disabled/>
	  			</s:else>
	  		</td>
	  	  	<td class="admin_selections"><s:property value="number"/></td>
			<td class="admin_selections"><s:property value="description"/></td>
		</tr>
	  	</s:else>
	  	</s:iterator>
	</table>
        <div>
            <center>
                <s:iterator value="pageNoList" status="pageCounter">
                    &nbsp;
                    <s:if test="pageNoList[#pageCounter.index] == (pageNo + 1)">
                        <span id="page_<s:property value="%{#pageCounter.index}"/>"><s:property value="%{intValue()}" /></span>
                    </s:if>	
                    <s:else>
                        <span id="page_<s:property value="%{#pageCounter.index}"/>" style="cursor:pointer;text-decoration:underline">
                            <a href="javascript:searchItems('search_items_for_itemgroup.action',<s:property value="%{intValue()-1}" />)"> <s:property value="%{intValue()}" /></a>
                       </span>	
                    </s:else>
               </s:iterator>
            </center>
        </div>        
	</div>
</div>