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

<div class="admin_section_div" style="width: 99%">
  	<div class="admin_section_heading"><s:text name="label.campaign.notify" /></div>
  	
    <table class="admin_selections" width="100%">
	  <tr>
	  	<th width="15%">&nbsp;</th>
	    <th width="15%" class="admin_data_table"><s:text name="label.common.dealer"/></th>
	    <th width="15%" class="admin_data_table"><s:text name="label.common.customer"/></th>
	    <th>&nbsp;</th>
	  </tr>
	  <tr>  
	    <td><s:text name="label.campaign.mail"/></td>
	    <td align="center">
	    	<s:checkbox name="campaign.notifiyDealerByEmail"/>
		</td>
		<td align="center">
			<s:checkbox name="campaign.notifyCustomer"/>
		</td>
		<td>&nbsp;</td>
	  </tr>
	  <tr>  
	    <td><s:text name="label.campaign.inbox"/></td>
	    <td align="center">
	    	<s:checkbox name="campaign.notifyDealer"/>
		</td>
		<td colspan="2">&nbsp;</td>
	  </tr>
	</table>
</div>