<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Aug 4, 2008
  Time: 10:33:57 PM
  To change this template use File | Settings | File Templates.

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

<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="authz" uri="authz" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>


<html>
<head>
    <s:head theme="twms"/>
    <u:stylePicker fileName="preview.css"/>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="paymentSection.css"/>
    <meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1">
    <title><s:text name="title.viewClaim.claimSearchPreview"/></title>

    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.LayoutContainer");
        dojo.require("twms.widget.TitlePane");
        dojo.require("dijit.layout.TabContainer");
    </script>
</head>

<u:body>
    <div dojoType="dijit.layout.LayoutContainer">
        <div dojoType="dijit.layout.TabContainer" tabPosition="bottom" layoutAlign="client">
            <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.header" />"
                 style="overflow-X: hidden; overflow-Y: auto">
                <div class="policy_section_div">
                    <div class="section_header"><s:text name="label.viewClaim.claimDetails"/></div>
                    <s:push value="task">
                        <jsp:include flush="true" page="../common/read/header.jsp"/>
                    </s:push>
                </div>
            </div>
            
            <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.servicingLocation"/>" class="scrollYNotX">
		        <div class="policy_section_div">
		            <div class="section_header"><s:text name="label.viewClaim.servicingLocation"/></div>
		            <s:push value="task">
		                <jsp:include flush="true" page="../common/read/servicingLocation.jsp"/>
		            </s:push>
		        </div>
		    </div>
   <div >
    <s:if test="task.partsClaim && task.claim.partItemReference.serialized">    
        <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.PartDetails"/>" class="scrollYNotX">
        <div class="policy_section_div">
            <div class="section_header"><s:text name="title.viewClaim.PartDetails"/></div>
            <s:push value="task">
                <jsp:include flush="true" page="../common/read/serializedPart.jsp"/>
            </s:push>
        </div>
    </div>
    </s:if>
    </div> 
            <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.equipment"/>"
                 style="overflow-X: hidden; overflow-Y: auto">
                <div class="policy_section_div">
                    <div class="section_header"><s:text name="label.viewClaim.equipmentDetails"/></div>
                    <s:push value="task">
                        <jsp:include flush="true" page="../common/read/equipment.jsp"/>
                    </s:push>
                </div>
            </div>

            <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.failure" />"
                 style="overflow-X: hidden; overflow-Y: auto">
                <div class="policy_section_div">
                    <div class="section_header"><s:text name="label.claim.failureDetails"/></div>
                    <s:push value="task">
                        <jsp:include flush="true" page="../common/read/failure.jsp"/>
                    </s:push>
                </div>
            </div>
            <s:if test="isAlarmCodesSectionVisible()" >
	            <div >
		  	       <div dojoType="twms.widget.TitlePane" title="<s:text name="title.required.alarmCode"/>" id="alarmcode_details"
			           labelNodeClass="section_header">
	                     <s:push value="task">
	                           <jsp:include flush="true" page="../common/read/claimAlarmCodeView.jsp" />
						  </s:push>
						
			      </div>
			    </div>
			</s:if>
			<s:if test="task.partsClaim && (!task.claim.partInstalled || task.claim.competitorModelBrand!=null) ">
			 	<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.laborInformation" />" class="scrollYNotX">
			        <div class="policy_section_div">
			            <div class="section_header"><s:text name="title.viewClaim.laborInformation" /></div>
				            <s:push value="task">
				                <table class="form" id="failure_details_table">
									<tr>
										<td class="labelStyle"><s:text name="label.labor.totalLaborHours" />:</td>					 
										<td><s:property value='claim.serviceInformation.serviceDetail.laborPerformed[0].hoursSpent' /></td>
									</tr>
								</table>
				            </s:push>
			        </div>
			    </div>
			</s:if>
			 <s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
                <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.serviceDetails"/>"
                     style="overflow-X: hidden; overflow-Y: auto">
                    <div class="policy_section_div">
                        <div class="section_header"><s:text name="label.viewClaim.serviceDetails"></s:text></div>
                        <s:push value="task">
                            <jsp:include flush="true" page="../common/read/service_detail.jsp"/>
                        </s:push>
                    </div>
                </div>
            </s:elseif>

             <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.components"/>"
                 style="overflow-X: hidden; overflow-Y: auto">
	            <s:if test="!partsReplacedInstalledSectionVisible" >
		            <div class="policy_section_div">
		                <div class="section_header"><s:text name="label.viewClaim.componentsReplaced"></s:text></div>
		                <s:push value="task">
		                    <jsp:include flush="true" page="../common/read/component.jsp"/>
		                </s:push>
		            </div>
	            </s:if>
	            <s:else>
					<div class="policy_section_div">
				        <div class="section_header"><s:text name="label.claim.partsReplacedInstalled"/></div>
				        <s:push value="task">
				            <jsp:include flush="true" page="../common/read/hussmannPartsReplacedInstalledForPreview.jsp"/>
				        </s:push>           
			 		</div>
				</s:else>
       		 </div>
            <s:if test="incidentalsAvaialable"> 
            <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.miscellaneous"/>"
                 style="overflow-X: hidden; overflow-Y: auto">
                <div class="policy_section_div">
                    <s:push value="task">
                        <jsp:include flush="true" page="../common/read/otherIncidentals.jsp"/>
                    </s:push>
                </div>
            </div>
            </s:if>
            

            <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.comments"/>"
                 style="overflow-X: hidden; overflow-Y: auto">
                <div class="policy_section_div">
                    <div class="section_header"><s:text name="label.viewClaim.comments"></s:text></div>
                    <s:push value="task">
                        <jsp:include flush="true" page="../common/read/comment.jsp"/>
                    </s:push>
                </div>
            </div>

            <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.history"/>"
                 style="overflow-X: hidden; overflow-Y: auto">
                <div class="policy_section_div">
                    <div class="section_header"><s:text name="label.viewClaim.history"></s:text></div>
                    <s:push value="task">
                        <jsp:include flush="true" page="../common/read/commentHistory.jsp"/>
                    </s:push>
                </div>
            </div>

             <s:if test="isLoggedInUserAnInternalUser() || claim.forDealer.id == loggedInUsersDealership.id ">
                <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.amount"/>"
                     style="overflow-X: hidden; overflow-Y: auto">
                    <div class="policy_section_div">
                        <div class="section_header"><s:text name="label.viewClaim.paymentDetails"></s:text></div>
                        <s:push value="task">
                            <jsp:include flush="true" page="../search_result/payment.jsp"/>
                        </s:push>
                    </div>
                </div>
           </s:if>

            <authz:ifUserInRole
                    roles="admin,dsmAdvisor,recoveryProcessor,processor,dsm, receiver,inspector,sra,partshipper,
                    system,salesPerson,technician">
                <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.actions"/>"
                     style="overflow-X: hidden; overflow-Y: auto">
                    <div class="policy_section_div">
                        <div class="section_header"><s:text name="title.viewClaim.actions"></s:text></div>
                        <s:push value="task">
                            <table class="form">
                                <tr>
                                    <td class="label"><s:text name="title.attributes.claimState"/>:</td>
                                    <td><s:property value="claim.state"/></td>
                                    <td class="label"><s:text name="title.attributes.accountabilityCode"/>:</td>
                                    <td>
                                        <s:if test="claim.accountabilityCode!=null">
                                            <s:property value="claim.accountabilityCode.code"/>
                                            (<s:property value="claim.accountabilityCode.description"/>)
                                        </s:if>
                                    </td>
                                </tr>
                            </table>
                        </s:push>
                    </div>
                </div>
            </authz:ifUserInRole>
        </div>
    </div>
</u:body>
</html>