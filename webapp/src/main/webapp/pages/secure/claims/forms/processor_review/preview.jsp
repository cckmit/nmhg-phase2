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
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<s:head theme="twms" />

<script type="text/javascript">
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.TabContainer");
</script>

<u:stylePicker fileName="layout.css" common="true"/>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="preview.css"/>
<u:stylePicker fileName="paymentSection.css"/>

<div class="official maxSize" dojoType="dijit.layout.LayoutContainer">
       <s:if test="task.claim.appealed.booleanValue() == 0 && (task.claim.getNotificationsToProcessor().size()> 0)">
            <div dojoType="twms.widget.TitlePane" title="<s:text name="Notifications"/>"
	            labelNodeClass="section_header" open="true">
                <table class="borderForTable" id="notificationTable" width="96%" >
                    <s:iterator value="task.claim.notificationsToProcessor">
                            <tr>
                                <td width="90%"><s:property/></td>                              
                            </tr>
                        </s:iterator>
                </table>
        </div>
		</s:if>    
    <s:if test="!task.claim.appealed.booleanValue() && (task.claim.ruleFailures.size > 0)">
        <div dojoType="dijit.layout.ContentPane" id="errors" layoutAlign="top" class="section scrollYNotX"
             style="padding: 0 ; margin: 3px; height: 70px; overflow:auto">
                <div class="section_header"><s:text name="label.common.errors"/></div>
                <table class="borderForTable" id="errorTable" width="96%">
                    <tr class="row_head">
                          <th width="10%"><s:text name="columnTitle.manageBusinessRule.ruleNumber"/></th>
                          <th width="90%"><s:text name="columnTitle.manageBusinessRule.history.ruleDescription"/></th>
                    </tr>
                    <s:iterator value="task.claim.ruleFailures">
                        <s:iterator value="failedRules">
                            <tr>
                                <td width="10%"><s:property value="ruleNumber"/></td>
                                <s:if test="ruleMsg != null">
                                    <td width="90%"><s:property value="ruleMsg"/></td>
                                </s:if>
                                <s:else>
                                     <td width="90%"><s:property value="defaultRuleMsgInUS"/></td>
                                </s:else>
                            </tr>
                        </s:iterator>
                    </s:iterator>
                </table>
        </div>
    </s:if>
    <div  dojoType="dijit.layout.TabContainer" tabPosition="bottom" layoutAlign="client" style="width: 100%;">
        <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.header" />"
            class="scrollYNotX"
            id="header">
            <div  class="policy_section_div">
                <div class="section_header"><s:text name="label.viewClaim.claimDetails" /></div>
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
            class="scrollYNotX"
            id="equipment">
            <div class="policy_section_div">
                <div class="section_header"><s:text name="label.viewClaim.equipmentDetails"/></div>
                <s:push value="task">
                    <jsp:include flush="true" page="../common/read/equipment.jsp"/>
                </s:push>
            </div>
        </div>

        <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.failure" />"
            class="scrollYNotX" id="failure">
            <div class="policy_section_div">
                <div class="section_header"><s:text name="label.claim.failureDetails" /></div>
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
	<s:if test="task.claim.laborConfig">
		<s:if test="task.partsClaim && (!task.claim.partInstalled || (task.claim.partInstalled && task.claim.competitorModelBrand!=null))">
		 	<div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.laborInformation" />" class="scrollYNotX">
		        <div class="policy_section_div">
		            <div class="section_header"><s:text name="title.viewClaim.laborInformation" /></div>
			            <s:push value="task">
			               <jsp:include flush="true" page="../common/read/labor_detail_part.jsp"/>
			            </s:push>
		        </div>
		    </div>
		</s:if>
		<s:elseif test="!task.partsClaim || (task.claim.partInstalled && (task.claim.competitorModelBrand == null || task.claim.competitorModelBrand.isEmpty()))">
            <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.serviceDetails"/>"
                class="scrollYNotX" id="service_detail">
                <div class="policy_section_div">
                    <div class="section_header"> <s:text name="label.viewClaim.serviceDetails"></s:text></div>
                    <s:push value="task">
                        <jsp:include flush="true" page="../common/read/service_detail.jsp"/>
                    </s:push>
                </div>
            </div>
        </s:elseif>
	</s:if>
		
        <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.components"/>"
            class="scrollYNotX" id="components">
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
             class="scrollYNotX">
            <div class="policy_section_div">
               <s:push value="task">
                    <jsp:include flush="true" page="../common/read/otherIncidentals.jsp"/>
                </s:push>
            </div>
        </div>
        </s:if>


        <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.comments"/>"
            class="scrollYNotX" id="comments">
            <div class="policy_section_div">
                <div class="section_header"><s:text name="label.viewClaim.comments"></s:text> </div>
                <s:push value="task">
                    <jsp:include flush="true" page="../common/read/comment.jsp"/>
                </s:push>
            </div>
        </div>

        <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.history"/>"
            class="scrollYNotX" id="history">
            <div class="policy_section_div">
                <div class="section_header"><s:text name="label.viewClaim.history"></s:text></div>
                <s:push value="task">
                    <jsp:include flush="true" page="../common/read/commentHistory.jsp"/>
                </s:push>
            </div>
        </div>

        <div dojoType="dijit.layout.ContentPane" title="<s:text name="label.partReturnAudit.PartHistory"/>" style="overflow-X: hidden; overflow-Y: auto">
                <div class="policy_section_div">
                    <div class="section_header"><s:text name="label.partReturnAudit.PartHistory"/></div>
                    <s:push value="task">
                    <jsp:include flush="true" page="../common/read/partsAuditHistory.jsp"/>
                    </s:push>
                </div>
        </div>

        <div dojoType="dijit.layout.ContentPane" title="<s:text name="title.viewClaim.amount"/>"
           style="overflow-Y:auto;">
            <div class="policy_section_div">
                <div class="section_header"><s:text name="label.viewClaim.paymentDetails"></s:text></div>
                <s:push value="task">
                    <jsp:include flush="true" page="../common/read/payment.jsp"/>
                </s:push>
            </div>
        </div>

    </div>
</div>