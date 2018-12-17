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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>


<table class="form" cellspacing="0" cellpadding="0" id="claim_description_table" style="width:95%;">
    <tr>
        <td>
            <label for="condition_found"  class="labelStyle"> <s:text name="label.newClaim.conditionFound"/>:</label><br/>
            <t:textarea id="condition_found" rows="4" cols="105" maxLength="3990"
                name="task.claim.conditionFound"/>
        </td>
        
       
</tr>
<tr>
     <td>
            <label for="work_performed" class="labelStyle"><s:text name="label.newClaim.workPerformed"/>:</label><br/>
            <t:textarea id="work_performed" rows="4" cols="105" maxLength="3990"
                name="task.claim.workPerformed"/>
        </td>
    </tr>
<tr>
        <td>
            <label for="additional_details" class="labelStyle"><s:text name="label.newClaim.additionalDetails"/>:</label><br/>
            <t:textarea id="additional_details" rows="4" cols="105" maxLength="3990"
                name="task.claim.otherComments"/>
        </td>
    </tr>
<tr>
<%--         <td> 
             <label for="probable_cause" class="labelStyle"><s:text name="label.newClaim.probableCause"/>:</label><br/> 
             <t:textarea id="probable_cause" rows="4" cols="105" maxLength="3990" 
                 name="task.claim.probableCause"/> 
        </td> --%>
    </tr>
    
</table>
