<%@taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>
  
  <div id="customerDetailsDiv">
    <s:if test="addressForTransfer!=null">
      <jsp:include flush="true" page="customer_details.jsp" />
    </s:if>
    <s:else>
      <s:hidden name="addressForTransfer" value="null" id="addressForTransfer"/>
    </s:else>
    <s:if test ="warranty.forDealer !=null">
      <s:push value="warranty"/>
    </s:if>
    <table width="100%" cellpadding="2" cellspacing="2" id="dealer-addr"
		class="form">
      <tbody>
        <tr>
          <td class="non_editable labelStyle" width="15%"><s:text name="label.companyName" /></td>
          <td width="25%" ><s:property value="forDealer.name" /></td>
        </tr>
       
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.common.address.line1" />
            :</td>
          <td colspan="3"><s:property value="forDealer.address.addressLine1" /></td>
        </tr>
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.common.address.line2" />
            :</td>
          <td colspan="3"><s:property value="forDealer.address.addressLine2" /></td>
        </tr>
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.common.address.line3" />
            :</td>
          <td colspan="3"><s:property value="forDealer.address.addressLine3" /></td>
        </tr>
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.city" /></td>
          <td><s:property value="forDealer.address.city" /></td>
          <td class="non_editable labelStyle"><s:text name="label.state" /></td>
          <td><s:property value="forDealer.address.state" /></td>
        </tr>
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.zip" /></td>
          <td><s:property value="forDealer.address.zipCode" /></td>
          <td class="non_editable labelStyle"><s:text name="label.country" /></td>
          <td><s:property value="forDealer.address.country" /></td>
        </tr>
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.phone" /></td>
          <td><s:property value="forDealer.address.phone" /></td>
          <td class="non_editable labelStyle"><s:text name="label.email" /></td>
          <td><s:property value="forDealer.address.email" /></td>
        </tr>
        <tr>
          <td class="non_editable labelStyle"><s:text name="label.fax" /></td>
          <td><s:property value="forDealer.address.fax" /></td>
        </tr>
      </tbody>
    </table>
   <%--  </s:push>
    </s:if> --%>
  </div>