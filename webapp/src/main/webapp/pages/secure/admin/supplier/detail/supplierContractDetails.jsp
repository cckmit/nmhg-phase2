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


     <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid borderForTable" align="center">
     	 <thead>
            <tr class="row_head">
                <th align="left">Contract Code</th>
                <th align="left">Contract Name</th>
            </tr>
           </thead>
        
       		<tbody>
            <s:iterator value="supplierContracts" status="status">
              <tr>
                <td align="left" >
                    <s:property value="id"/>
                </td>
            	<td width="40%" >
          			 <u:openTab tabLabel="Contract %{name}"
	      		   url="contract_view.action?id=%{id}"
	      		   id="Contract[%{id}]"
	      		   cssClass="Inboxlink">	      
	      	<s:property value="name" />
	    </u:openTab>
           </td>
	           </tr> 	
            </s:iterator>  
          </tbody>          
    </table>


