<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<%
	response.setHeader("Pragma", "no-cache");
	response.addHeader("Cache-Control", "must-revalidate");
	response.addHeader("Cache-Control", "no-cache");
	response.addHeader("Cache-Control", "no-store");
	response.setDateHeader("Expires", 0);
%>

                 <table width="98%" cellpadding="0" cellspacing="0"  class="grid borderForTable">
			        <thead>
			            <tr>
			                <th class="warColHeader" align="left"><s:text name="label.planName"/></th>			                
			            </tr>
			        </thead>
			        <tbody id="policy_lists">
			        <s:if test="isModifyDRorETR()">
			            <s:iterator value="availablePolicies" status="availablePolicies">
			            <tr>
                            <td> 
                                <u:openTab autoPickDecendentOf="true"
                                           id="policy_%{policyDefinition.id}"
                                           tabLabel="Policy %{policyDefinition.code}"
                                           url="get_policy_detail.action?policyId=%{policyDefinition.id}">
                                    <u style="cursor: pointer;"><s:property value="code"/></u></u:openTab>
                            </td>
			            </tr>
			            </s:iterator>
			            </s:if>
			            <s:else>
			             <s:iterator value="warranty.policies" status="availablePolicies">
			            <tr>
                            <td>
                                <u:openTab autoPickDecendentOf="true"
                                           id="policy_%{policyDefinition.id}"
                                           tabLabel="Policy %{policyDefinition.code}"
                                           url="get_policy_detail.action?policyId=%{policyDefinition.id}">
                                    <u style="cursor: pointer;"><s:property value="code"/></u></u:openTab>
                            </td>
			            </tr>
			            </s:iterator>
			           </s:else>

			   			<authz:ifUserInRole roles="admin">
			           <s:if test="!isDeliveryReport()">
			           	<s:iterator value="getTransferablePoliciesFromPrevWarranty()" status="missedOutCoverages">
			            <tr>
			                <td>
                                <u:openTab autoPickDecendentOf="true"
                                           id="policy_%{policyDefinition.id}"
                                           tabLabel="Policy %{policyDefinition.code}"
                                           url="get_policy_detail.action?policyId=%{policyDefinition.id}">
                                    <u style="cursor: pointer;"><s:property value="code"/></u></u:openTab>
                            </td>			                
			            </tr>
			            </s:iterator>
			           </s:if> 
			           </authz:ifUserInRole>
			        </tbody>
			    </table>
