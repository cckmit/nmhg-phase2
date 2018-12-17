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


	<u:actionResults/>
    <jsp:include flush="true" page="../../common/read/validationMessages.jsp"/>
	<s:if test="task.claim.type.type != 'Campaign'">
	    <div dojoType="dijit.layout.ContentPane" id="applicable_policy">
            <table width="95%" class="repeat borderForTable" style="margin-left:0px;">
                <thead>
                    <tr class="row_head">
                        <th>
                            <s:text name="columnTitle.common.serialNo" />
                        </th>
                        <th ><s:text name="label.newClaim.applicablePolicy"/></th>
                        <authz:ifProcessor>
                            <s:if test="task.claim.forMultipleItems">
                                <th ><s:text name="label.common.approved"/></th>
                            </s:if>
                        </authz:ifProcessor>
                    </tr>
                </thead>
                <tbody>
                <s:iterator value="task.claim.claimedItems">
                    <tr>
                        <td style="margin-left: 25px;">
                            <s:property value="itemReference.referredInventoryItem.serialNumber" />
                        </td>
                        <td><s:property value="applicablePolicy.code"/></td>
                        <authz:ifProcessor>
                            <s:if test="task.claim.forMultipleItems">
                                <td>
                                    <s:if test="processorApproved">
                                        <span style="color:green">
                                            <s:text name="label.common.yes" />
                                        </span>
                                    </s:if>
                                    <s:else>
                                        <span style="color:red">
                                            <s:text name="label.common.no" />
                                        </span>
                                    </s:else>
                                </td>
                            </s:if>
                        </authz:ifProcessor>
                    </tr>
                </s:iterator>
                </tbody>
            </table>
        </div>
    </s:if>    
   <s:push value="task">
   		<s:if test="!partsReplacedInstalledSectionVisible">
   			<jsp:include page="../../common/read/oempartreplacedforvalidate.jsp"/>
   		</s:if>
        <s:else>
        	<jsp:include page="../../common/read/hussmannPartsReplacedInstalledforvalidate.jsp" />
        </s:else>
    </s:push>
   <s:push value="task">
        <s:if test="baseFormName == 'service_manager_review'">
        
        <%--
                This is a special case; we need to remove cp related columns
            --%>
        <jsp:include page="complete_payment_detail.jsp"/>
        </s:if>
        <s:else>
        <jsp:include page="../../common/read/payment.jsp"/>
        </s:else>
    </s:push>


    <div dojoType="dijit.layout.ContentPane" layoutAlign="bottom" style="height: 40px; border-top: 1px solid #DCD5CC;">
		<div class="buttonWrapperPrimary">
            <s:submit id="validations_hider" value="%{getText('button.newClaim.editClaim')}" type="button"/>
            <script type="text/javascript">
                dojo.connect(dojo.byId("validations_hider"), "onclick", function() {
                    dijit.byId("validations").hide();
                });
            </script>
            <s:if test="task.claim.type.type == 'Campaign'">
                <s:submit id="campaign_submit" value="Submit" type="button" action="campaign_claim_submit"/>
                <script type="text/javascript">
                    dojo.connect(dojo.byId("campaign_submit"),"onclick",function(){
                        var form = document.forms[0];
                        form.action = "campaign_claim_submit.action";
                        form.submit();
                    });
                </script>
            </s:if>
            <s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
                <s:submit id="normal_submit" value="Submit" type="button"/>
                <script type="text/javascript">
                    dojo.connect(dojo.byId("normal_submit"),"onclick",function(){
                        var form = document.forms[0];
						form.submit();
                    });
                </script>
            </s:elseif>
            <s:else>
                <s:submit id="part_submit" value="Submit" type="button" action="parts_claim_submit"/>
                <script type="text/javascript">
                    dojo.connect(dojo.byId("part_submit"),"onclick",function(){
                        var form = document.forms[0];
                        form.action = "parts_claim_submit.action";
                        form.submit();
                    });
                </script>
            </s:else>
		</div>
	</div>
</div>