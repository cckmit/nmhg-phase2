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
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<s:if test="claimWithPartBeansList.size() == 0"> <!-- Need to move out this common style for status messages.  -->
  <div style="border:1px solid #AAAAAA;background:#F5F5F5;padding:1px; width:97%; margin:5px;">
    <div style="background:#EFEBF7;color:#336600;padding:1px;">
	  <table>
	    <tr>
	  	  <td style="color:#336600;"><img src="images/applicationSuccessIcon.gif"/></td>
	  	  <td style="color:#336600;">
	  	    <p style="font-family: Verdana, Arial, Helvetica, sans-serif;font-size: 7.5pt;">
	  	      <s:text name="message.partReturnConfiguration.noDueParts"/>
	  		</p>   
	  	  </td>
	  	</tr>
	  </table>
	</div>
  </div>
</s:if>
<s:else>
	<div dojoType="dijit.layout.ContentPane" label="Parts List" style="overflow-X: hidden; overflow-Y: auto; width:100%;">
	<s:set name="partsCounter" value="0"/>	  
	<s:iterator value="claimWithPartBeansList" status="iterStatus">
        <div dojoType="twms.widget.TitlePane" title="<s:text name="title.partReturnConfiguration.claimDetails"/>(<s:property value="claim.claimNumber"/>)" labelNodeClass="section_header" >
        <%@include file="tables/claim_details.jsp"%>
       
         <%@include file="tables/add_part_details.jsp"%>
        </div>        
		<div class="separator"></div>
	</s:iterator>
    </div>
    <div class="buttonWrapperPrimary">
		<input type="button" name="Submit2232" value="<s:text name="button.partReturnConfiguration.addSelectedParts"/>" class="buttonGeneric" onclick="addPartsToShipment()"/>
  	</div>								
</s:else>

