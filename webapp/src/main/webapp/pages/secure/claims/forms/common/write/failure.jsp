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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>

<style type="text/css">
    td#launchFaultCodeTree {
        background-image: url(image/searchIcon.gif);
        cursor: pointer;
        background-repeat: no-repeat;
        width : 16px;
        height : 16px;
        margin : 0px;
        padding : 0px;
        border : 0px;
    }
#test iframe {
width: 1004px;
height: 790px;
border: none; }

#test {
width:630px;
height: 300px;
padding: 0;
border: inset 1px gray;
overflow: auto; }


	/*The other styling stuff is comming from serviceProcedure.jsp*/
</style>
<script type="text/javascript">
		dojo.require("twms.widget.Tree");
		dojo.require("twms.data.EmptyFileWriteStore");
        dojo.require("twms.widget.TreeModel");
        dojo.require("twms.widget.Select");
        dojo.require("dijit.form.Button");
        dojo.require("twms.widget.Dialog");
        dojo.require("dojox.layout.ContentPane");
</script>
<script type="text/javascript" src="scripts/ServiceProcedureTree.js"></script>
<table class="grid" cellspacing="0" cellpadding="0" id="failure_details_table">
    <tr>
        <td width="20%" nowrap="nowrap">
            <label for="causalBrandPart" class="labelStyle"><s:text name="label.common.causalPartNumber"/>:</label>
        </td>
        <td nowrap="nowrap" >
            <s:if test="task.partsClaim && !task.claim.partInstalled">
                  <s:property value="%{task.claim.serviceInformation.causalBrandPart.itemNumber" />
            </s:if>
            <s:else>
                <sd:autocompleter id='causalBrandPart' href='list_causal_part_nos.action?selectedBusinessUnit=%{selectedBusinessUnit}&claimBrand=%{task.claim.brand}&claim=%{task.claim}' name='task.claim.serviceInformation.causalBrandPart' value='%{task.claim.serviceInformation.causalBrandPart.itemNumber}' keyValue='%{task.claim.serviceInformation.causalBrandPart.id}' loadOnTextChange='true' showDownArrow='false' indicator='causalPart_indicator' notifyTopics='/causalPart/changed' />
                <s:hidden name="task.claim.serviceInformation.causalPart" id="causalPart" value="%{task.claim.serviceInformation.causalPart.number}"/>
                <s:if test="task.claim.serviceInformation.customReportAnswer.id!=''">
                	<s:hidden name="task.claim.serviceInformation.customReportAnswer"
                    	id="causal_part_failure_report"
                    	value="%{task.claim.serviceInformation.customReportAnswer.id}"/>
                </s:if>
                <s:else>
                	<s:hidden name="task.claim.serviceInformation.customReportAnswer"
                  	  	id="causal_part_failure_report" value="null"/>
                </s:else>
                <authz:ifProcessor>
   	                <s:if test="task.claim != null && task.claim.claimNumber !=null && task.claim.serviceInformation.oemDealerCausalPart !=null">
   	                <img  src="image/comments.gif" id= "oem_dealer_causal_part"
   			            		title="<s:property value="task.claim.serviceInformation.oemDealerCausalPart.number" />"
   			            		alt="<s:property value="task.claim.serviceInformation.oemDealerCausalPart.number" />"/>

   			        </s:if>
			   </authz:ifProcessor>
            </s:else>
        </td>
        <td width="22%" class="labelStyle" nowrap="nowrap">
			<s:text name="label.common.causalPartDescription" />:
		</td>
        <td width="20%">
            <span id="causalPartDescription">
                <s:property value="task.claim.serviceInformation.causalPart.description"/>
            </span>
        </td>
        <td nowrap="nowrap" colspan="3">
         <s:a cssStyle="cursor:pointer" href="#">
            <span id="causalPartAttributeLink">
            </span>
          </s:a>
          <script type="text/javascript">
          	dojo.addOnLoad(function(){
          	    generateCausalPartAttributeData(null);
          	});
          </script>
        </td>
    </tr>

    <u:jsVar varName="spanTemplateText">
        <span title="${fullNameTooltip}">${fullNameTooltip}(${completeCode})
            <input type="hidden" value="${faultCodeId}" name="task.claim.serviceInformation.faultCodeRef"/>
            <input type="hidden" value="${completeCode}" name="task.claim.serviceInformation.faultCode"/>
        </span>
    </u:jsVar>
      <tr>
        <td nowrap="nowrap">
 	 	      <s:if test="task.claim != null && !task.claim.foc">
                   <span class="labelStyle" id="launchFaultCodeTree">
 	 	            	<a><s:text name="label.common.faultCode"/> </a>
 	 	  		  </span>
              </s:if>
              <s:elseif test="task.claim != null && task.claim.foc">
              	<s:text name="label.common.faultCode"/>
              </s:elseif>
        </td>
        <td>
        <table>
 	 	<tr>
	     <s:if test="task.claim != null && task.claim.serviceInformation !=null && task.claim.serviceInformation.faultCode !=null">
	        <td id="existingFaultCode">
                <s:set name="faultCodeDescription" value="task.claim.serviceInformation.faultCodeDescription"/>
                <span title="<s:property value="#faultCodeDescription"/>" id="faultCodeDescription">
                <!-- <div style="width:30px;"> -->
                  <s:property value="#faultCodeDescription"/>(<s:property value="task.claim.serviceInformation.faultCode"/>)
                 <!--  </div> -->
                </span>
            </td>
	    </s:if>
		<td align="right"><div id="faultCode"></div> </td>
		</tr>
		</table>
		</td>
        <td nowrap="nowrap" colspan="3">
            <s:a cssStyle="cursor:pointer" href="#">
                <span id ="fautCodeAttributes"></span>
            </s:a>
        </td>
    </tr>
    <tr>
        <td nowrap="nowrap">
            <label for="faultFound"  class="labelStyle"><s:text name="label.common.faultFound"/>:</label>
        </td>
        <td nowrap="nowrap">

            <s:if test="task.claim.itemReference.isSerialized()">
                <s:set name="number" value="task.claim.itemReference.referredInventoryItem.serialNumber"/>
                <s:set name="invItemId" value="task.claim.itemReference.referredInventoryItem.id"/>
                <s:set name="faultFounds" value="prepareFaultFoundList(task.claim.itemReference.referredInventoryItem.id)"/>
                <s:set name="modelNumber" value="task.claim.itemReference.referredInventoryItem.ofType.model.id"/>
                <s:set name="machineUrl" value="task.claim.itemReference.referredInventoryItem.ofType.model.machineUrl"/>

                <s:select id="faultFound"
                list="#faultFounds" name="task.claim.serviceInformation.faultFound" listKey="id" listValue = "name"  emptyOption="true"
                 value="%{task.claim.serviceInformation.faultFound.id.toString()}"
                 />
                <s:hidden name="#machineUrl" value="%{machineUrl}"/>

                <script type="text/javascript">
                    dojo.addOnLoad(function() {
                        var faultFound = dijit.byId("faultFound");
                        faultFound.fireOnLoadOnChange=true;
						 dojo.connect(faultFound, "onChange", function(newValue) {
                            dojo.publish("/causedBy/reload", [{
                                url: "list_caused_by.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
                                params: {
                                    number: '<s:property value="#invItemId"/>',
                                    faultFound: newValue
                                },
                                makeLocal: true
                            }]);

                         <s:if test="isRootCauseAllowed()">
	                            dojo.publish("/rootCause/reload", [{
	                                url: "list_root_cause.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
	                                params: {
	                                    number: '<s:property value="#modelNumber"/>',
	                                    faultFound: newValue
	                                },
	                                makeLocal: true
	                            }]);
                         </s:if>
                        });
                    });
                    function setMachineUrl() {
                        <s:if test="isRootCauseAllowed()">
	                        <s:if test="#machineUrl.trim().length() > 0">
	    	            		document.getElementById('machineDiagramWindow').src = '<s:property value="#machineUrl"/>';
		    	            </s:if>
		    	            <s:else>
		    	            if(dojo.byId('machineUrlUserMessageDiv')!=null){
		    	            	dojo.byId('machineUrlUserMessageDiv').innerHTML = '<s:text name='text.rootCauseFailure.noMachineDiagram' />';
		    	            }
		    	            </s:else>
		    	        </s:if>
                    }
                </script>
            </s:if>
            <s:else>
            	<s:set name="faultFounds" value="prepareFaultFoundListForModels(task.claim.itemReference.model.id)"/>
                <s:set name="number" value="task.claim.itemReference.model.id"/>
                <s:set name="machineUrl" value="task.claim.itemReference.model.machineUrl"/>

               <s:select id="faultFound"
                list="#faultFounds" name="task.claim.serviceInformation.faultFound" listKey="id" listValue = "name"
                 value="%{task.claim.serviceInformation.faultFound.id.toString()}" emptyOption = "true"
                 />

                <script type="text/javascript">

                dojo.addOnLoad(function() {

                    var faultFound = dijit.byId("faultFound");
                    var causedBy = dijit.byId("causedBy");
                    faultFound.fireOnLoadOnChange=false;
                     dojo.connect(faultFound, "onChange",function(newValue) {
                        dojo.publish("/causedBy/reload", [{
                            url: "list_caused_by_for_model.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
                            params: {
                                number: '<s:property value="#number"/>',
                                faultFound: newValue
                            },
                            makeLocal: true
                        }]);
                         <s:if test="isRootCauseAllowed()">
	                            dojo.publish("/rootCause/reload", [{
	                                url: "list_root_cause.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
	                                params: {
	                                    number: '<s:property value="#number"/>',
	                                    faultFound: newValue
	                                },
	                                makeLocal: true
	                            }]);
                         </s:if>
                     });
                });

                function setMachineUrl() {
                    <s:if test="isRootCauseAllowed()">
	                    <s:if test="#machineUrl.trim().length() > 0">
	                		document.getElementById('machineDiagramWindow').src = '<s:property value="#machineUrl"/>';
		                </s:if>
		                <s:else>
			                dojo.byId('machineUrlUserMessageDiv').innerHTML = '<s:text name='text.rootCauseFailure.noMachineDiagram' />';
		                </s:else>
		            </s:if>
                }
                </script>
            </s:else>
        </td>
         <td>
            <label for="causedBy"  class="labelStyle"><s:text name="label.newClaim.causedBy"/>:</label>
        </td>
			 <td>
                <sd:autocompleter id="causedBy"  name="task.claim.serviceInformation.causedBy" keyName="task.claim.serviceInformation.causedBy"  value="%{task.claim.serviceInformation.causedBy.name}" key="%{task.claim.serviceInformation.causedBy.id}" listenTopics="/causedBy/reload" />
                   <script type="text/javascript">

                    </script>

                <img style="display: none;" id="causedBy_indicator" class="indicator"
                     src="image/indicator.gif" alt="Loading..."/>
        </td>
        </tr>
        <tr>
	    <s:if test="isRootCauseAllowed()">

		    <td nowrap="nowrap" >
		    	<label for="rootCause" class="labelStyle"><s:text name="label.newClaim.rootCause"/>:</label>
			</td>
			<td nowrap="nowrap" >
			        <sd:autocompleter id='rootCause'  name='task.claim.serviceInformation.rootCause' value='%{task.claim.serviceInformation.rootCause.name}' indicator='rootCause_indicator' listenTopics='/rootCause/reload' />

			        <img style="display: none;" id="rootCause_indicator" class="indicator"
			             src="image/indicator.gif" alt="Loading..."/>

			        <span id ="viewMachineDiagram" class="alinkclickable"><s:text name="label.rootCauseFailure.viewMachineDiagram"/></span>

					<div id = "dialogBoxContainer" style="display: none">
				        <div dojoType="twms.widget.Dialog" title="<s:text name="title.rootCauseFailure.machineDiagramView"/>"
				        id="browserForFailureRootCauseUrl" >

				                 <div dojoType="dijit.layout.ContentPane" layoutAlign="top"
				                	 id="machineUrlUserMessageDiv" >
									<div id="test">

									<iframe id="machineDiagramWindow"  name="machineDiagramWindow" scrolling="no"></iframe>
									</div></div>


				        </div>
			        </div>
			</td>
	    </s:if>
	    <s:else><td colspan="3"></td></s:else>
     </tr>
    <s:if test="task.claim.serviceInformation.customReportAnswer.id!=null">
        <tr>
            <td class="labelStyle" id="failureReportText"><s:text name="label.common.failureReport"/></td>
            <td>
                   <span style="color:blue;text-decoration:underline;cursor:pointer;"
                         id="report_<s:property value="%{task.claim.serviceInformation.customReportAnswer.id}"/>">
                    <s:text name="home_jsp.menuBar.view"/>
                    <script type="text/javascript">
                        dojo.addOnLoad(function() {
                            var claimId ='<s:property value="%{task.claim.id}"/>';
                            var reportId = '<s:property value="%{task.claim.serviceInformation.customReportAnswer.id}"/>';
                            var itemId = '<s:property value="%{task.claim.serviceInformation.causalPart.id}"/>';
                            var failureReportName = '<s:property value="%{task.claim.serviceInformation.customReportAnswer.customReport.name}"/>';
                            dojo.connect(dojo.byId("report_" + reportId), "onclick", function() {
                                displayFailureReport(
                                        claimId,
                                        reportId,
                                        failureReportName,
                                        itemId,
                                        "", "","");
                            });
                        });
                    </script>
                    </span>
            </td>
        </tr>
    </s:if>
</table>

<div style="display: none;">
    <div dojoType="twms.widget.Dialog" id="browserForFaultCode"
            title="<s:text name="title.newClaim.selectFaultCode"/>" style="width:80%;overflow:hidden;">
        <div id="errorDivForTree" style="display:none" class="twmsActionResultsErrors">
            <s:text name="error.newClaim.causalPartNoRequired"/>
        </div>
        <div dojoType="dijit.layout.ContentPane" layoutAlign="top" style="width:99%;height:380px;overflow:auto " id="faultCodeTreeContainer">
            <div id="treeParent">
            </div>
        </div>
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="border-top:1px solid #EFEBF7;" align="center">
            <button id="addFaultCode" style="margin:1px;"><s:text name="button.newClaim.setFaultCode"/></button>
        </div>
    </div>
</div>

<div id = "dialogBoxContainer" style="display: none">
	<div dojoType="twms.widget.Dialog" id="faultCodeAtrribute" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
		toggleDuration="250" style="width:550px; height:300px;"  title="<s:text name="label.common.additionalAttributes"/>">
		<div  style="background: #F3FBFE; border: 1px solid #EFEBF7">
		 	<div dojoType="dojox.layout.ContentPane" layoutAlign="top"
                  id="faultCodeAttributeContentPane">
			    <jsp:include flush="true" page="fault-code-claim_attribute.jsp" />
		    </div>
	    </div>
	</div>
</div>

<div id = "dialogBoxContainer" style="display: none">
	<div dojoType="twms.widget.Dialog" id="causalPartAtrribute" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
		toggleDuration="250"  style="width:550px; height:300px;" title="<s:text name="label.common.additionalAttributes"/>">
		<div style="background: #F3FBFE; border: 1px solid #EFEBF7">
		 	<div dojoType="dojox.layout.ContentPane" id="causalPartAttributeContentPane"
                  layoutAlign="top" style="height: 320px;">
			    <jsp:include flush="true" page="causal-part-claim_attribute.jsp" />
		    </div>
	    </div>
	</div>
</div>
<s:if test="forSerialized.equals('false') && !displayBrandDropDown()">
    <s:hidden name="claim.brand" id="brandTypeCausal" value="%{claim.brand}"/>
</s:if>
<script type="text/javascript">
var isCausalDialogDisplayed=false;
function fillCausalPartDescription(data, type, request) {
	var claim='<s:property value="task.claim.id"/>';
	var params = {};
	var itemNumber;
	params["number"]=claim;
	var oldBrandPartNumber = '<s:property value="%{task.claim.serviceInformation.causalBrandPart.id}"/>';
	var newBrandPartNumber= dijit.byId("causalBrandPart").getValue();
	if(oldBrandPartNumber == newBrandPartNumber )
	{
	itemNumber='<s:property value="%{task.claim.serviceInformation.causalBrandPart.id}"/>';
	}
	else
	{
	itemNumber=dijit.byId("causalBrandPart").getValue();
	}
	params["causalPart"]= itemNumber;
	var partNumber= itemNumber;
	var updateBrand = '<s:property value="displayBrandDropDown()"/>';
	twms.ajax.fireJsonRequest("findCausalPartDetails.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />", params, function(details) {
                 dojo.byId("causalPartAttributeLink").innerHTML='';
                     if(details){
                         dojo.byId("causalPartDescription").innerHTML=details[0];
                         dojo.byId("causalPart").value=details[2];
                         if(dojo.byId("causalPartTemp").value){
                            dojo.byId("causalPartTemp").value = details[2];
                         }
                            if(dojo.byId("claim_form_forSerialized") && dojo.byId("claim_form_forSerialized").value=="false" && updateBrand == "false"){
                                dojo.byId("brandTypeCausal").value=details[3];
                            }


                     	 if(details[1] && details[1] != '-'){
	                         dojo.byId("causalPartAttributeLink").innerHTML='<s:text name="label.additionalAttribute.enterAttribute"/>';
                             generateCausalPartAttributeData(details[2]);
	                         isCausalDialogDisplayed = false;
                         }
                     }
                 }
             );
     }


dojo.subscribe("/causalPart/changed", null, fillCausalPartDescription);
dojo.addOnLoad(function(){
    dojo.subscribe("/causalPart/changed", null, function() {
        var failureReportId = '<s:property value="%{task.claim.serviceInformation.customReportAnswer.id}"/>';
        var causalPartNumber = '<s:property value="%{task.claim.serviceInformation.causalPart.number}"/>';
        if((causalPartNumber=="" || failureReportId=="")
                || (failureReportId!="" && dojo.byId("causalPart").value != causalPartNumber) ){
           dojo.byId("causal_part_failure_report").value="null";
            if(dojo.byId("report_"+failureReportId) && dojo.byId("failureReportText")){
            dojo.byId("report_"+failureReportId).innerHTML="";
            dojo.html.hide(dojo.byId("failureReportText"));
            }
        }else if (failureReportId!="" && dojo.byId("causalPart").value==causalPartNumber){
            dojo.byId("causal_part_failure_report").value=failureReportId;
            if(dojo.byId("report_"+failureReportId) && dojo.byId("failureReportText")){
            dojo.byId("report_"+failureReportId).innerHTML='<s:text name="home_jsp.menuBar.view"/>';
            dojo.html.show(dojo.byId("failureReportText"));
            }
        }

    });
});
</script>

 <script type="text/javascript">
        var claim="<s:property value="task.claim"/>"
		var faultCodeId = '<s:property value="task.claim.serviceInformation.faultCodeRef"/>';
		var isAttributePresent= '<s:property value="task.claim.serviceInformation.faultClaimAttributes.size"/>' > 0 ;

        var isFaultDialogDisplayed=false;

        dojo.addOnLoad(function(){
            <s:if test="isRootCauseAllowed()">
            var machineDiagramDialog = dijit.byId("browserForFailureRootCauseUrl");
            var machineDiagramFetched = false;
            dojo.connect(dojo.byId("viewMachineDiagram"), "onclick", function(event) {
            	if(machineDiagramDialog)
            	{
            		if(!machineDiagramFetched) {
		            	setMachineUrl();
		            	machineDiagramFetched = true;
	            	}
            		machineDiagramDialog.show();
            	}
            });
            </s:if>

            dijit.byId('faultCodeAtrribute').formNode=document.getElementById("claim_form");
			dojo.connect(dojo.byId("fautCodeAttributes"),"onclick",function(){
			    dojo.publish("/fault/attribute/show");
		    });
			dojo.subscribe("/fault/attribute/show", null, function() {
				var dlg = dijit.byId("faultCodeAtrribute");
				if( ! isFaultDialogDisplayed) {
					var claim='<s:property value="task.claim.id"/>';
					var params = {
                        claimDetails: claim,
                        faultCodeID: faultCodeId
                    }
                    var url = "getAttributesForFaultCode.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />";
                    dijit.byId("faultCodeAttributeContentPane").setContent('');
					twms.ajax.fireHtmlRequest(url, params,
						function(data) {
                            var parentContentPane = dijit.byId("faultCodeAttributeContentPane");
                            parentContentPane.setContent(data);
                            isFaultDialogDisplayed = true;
						}
                    );
			    }
			    dlg.show();
    	    });
        });

     function generateCausalPartAttributeData(partNumber){
	        var causalAttributePresent= '<s:property value="task.claim.serviceInformation.partClaimAttributes.size"/>' >0;

	        dijit.byId('causalPartAtrribute').formNode=document.getElementById("claim_form");
	        if(causalAttributePresent){
	            dojo.byId("causalPartAttributeLink").innerHTML='<s:text name="label.additionalAttribute.enterAttribute"/>';
	        }
	        dojo.connect(dojo.byId("causalPartAttributeLink"),"onclick",function(){
	            dojo.publish("/causal/attribute/show");
	        });
	        dojo.subscribe("/causal/attribute/show", null, function() {
	            var dlg = dijit.byId("causalPartAtrribute");
                if(! causalAttributePresent && ! isCausalDialogDisplayed){
	                    var claim='<s:property value="task.claim.id"/>';
	                    var params = {
	                        claimDetails: claim,
	                        partNumber: partNumber
	                    };
	                    twms.ajax.fireHtmlRequest("getAttributesForCausalPart.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />", params,
	                        function(data) {
	                            var parentContentPane = dijit.byId("causalPartAttributeContentPane");
	                            parentContentPane.setContent(data);
	                            isCausalDialogDisplayed = true;
	                        }
	                    );
	                }
	                dlg.show();
	        });
    }
    </script>
<script type="text/javascript">
    dojo.addOnLoad(function(){
        var faultSelected = "<s:property value="task.claim.serviceInformation.faultCodeRef.definition.code"/>";
        var isAttributePresent = '<s:property value="task.claim.serviceInformation.faultClaimAttributes.size"/>' >0;
        var claimId = '<s:property value="task.claim.id"/>';
        var causalPartNumber = "<s:property value="task.claim.serviceInformation.causalPart.number"/>";
        var causalBrandPartNumber = "<s:property value="task.claim.serviceInformation.causalBrandPart.id"/>";
        var businessUnitInfo = "<s:property value="task.claim.businessUnitInfo"/>";
        var casualPartChanged = true;
        var renderer = null;
        var treeWidget = null;
        var isFirstTime = true;
        var causalValueChanged = false;
        var faultCodeTreeManager = null;
        var dialog = dijit.byId("browserForFaultCode");
        if (isAttributePresent) {
            dojo.byId("fautCodeAttributes").innerHTML = '<s:text name="label.additionalAttribute.enterAttribute"/>';
        }
        dojo.subscribe("/causalPart/changed", null, function() {
            casualPartChanged = true;
             if(causalBrandPartNumber!=dijit.byId("causalBrandPart").getValue())
            	{
            	causalValueChanged = true;
            	}
            causalBrandPartNumber = dijit.byId("causalBrandPart").getValue();

              if (!isFirstTime && causalValueChanged) {
                if (dojo.byId("existingFaultCode")) {
                    dojo.html.hide(dojo.byId("existingFaultCode"));
                }
                var span = dojo.query("span", dojo.byId("faultCode"))[0];

                if (span) {
                    dojo.dom.destroyNode(span);
                    span = null;
                }
                var substitutionMap = {
                    "completeCode" : "",
                    "fullNameTooltip" : "",
                    "faultCodeId" : ""
                };

                var spanMarkup = dojo.string.substitute(unescapeThisHtml(spanTemplateText.markup), substitutionMap);
                span = dojo.html.createNodesFromText(spanMarkup, true);
                if (span) {
                	if(dojo.byId("faultCode").value != null){
                		dojo.byId("faultCode").appendChild(span);
                	}
                }
                dojo.byId("fautCodeAttributes").innerHTML = '';
            }
            isFirstTime = false;
        });
        function destroyAndRecreateTree() {
            if (treeWidget) {
                treeWidget.destroyRecursive();
            }
        }
        dojo.connect(dojo.byId("launchFaultCodeTree"), "onclick", function() {
        var actualPartNumber = dojo.byId("causalPart").value;
            if (casualPartChanged) {
                destroyAndRecreateTree();
                if (actualPartNumber) {
                    var params = { claimDetail : claimId,
                                   causalPart : actualPartNumber,
                                   selectedBusinessUnit : businessUnitInfo };
                    twms.util.putLidOn(dijit.byId("faultCodeTreeContainer").domNode);
                    dojo.html.setDisplay(dojo.byId("errorDivForTree"), false);
                    dialog.show();
                    twms.ajax.fireJsonRequest("get_fault_code_json.action", params, function(data) {

//                        faultCodeTreeManager = new tavant.twms.FaultCodeTreeManager(data, treeWidget, treeWidget.store, "faultCodeNode");
//                        faultCodeTreeManager.renderTree();
                          dojo.cookie("treeWidgetSaveStateCookie",null); // delete the old cookie so that tree widget doesn't open based on previous storage
                            var divElement = document.createElement("div")
                            divElement.id = "faultCodeTreeWidget";
                            dojo.byId("treeParent").appendChild(divElement);
                            var storeId = new twms.data.EmptyFileWriteStore({});
                            storeId.data = data;
                            var model = new twms.widget.TreeModel({
                                store:storeId,
                                treeType:'FAULTCODE',
                                claimDetail: claimId,
                                causalPart: (dijit.byId("causalPart")) ? dijit.byId("causalPart").getValue() : null,
                                selectedBusinessUnit : businessUnitInfo
                            });
                            treeWidget = new twms.widget.Tree(
                                {
                                    id : "treeWidget",
                                    labelAttr : "name",
                                    model : model,
                                    label : "Fault Location",
                                    defaultSelectedRadio:faultSelected,
                                    persist: false
                                }, divElement);
                            dojo.setSelectable(treeWidget.domNode, false);
                            renderer = new tavant.twms.FaultCodeRenderer(dojo.byId("faultCode"), treeWidget, faultSelected, spanTemplateText.markup);
                        casualPartChanged = false;
                        dialog.containerNode.scrollTop = 0; //scroll to Top
                        twms.util.removeLidFrom(dijit.byId("faultCodeTreeContainer").domNode);
                    });
                }
            }
            if (!actualPartNumber) {
               dojo.html.setDisplay(dojo.byId("errorDivForTree"), true);
            }
            dialog.show();
        });
        var causalPartWidget = dojo.byId("causalPart");
        dojo.connect(causalPartWidget, "onChange", function(newValue) {
            dojo.publish("/faultFound/reload", [{
                url: "generate_fault_found_json.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />",
                params: {
                    claimDetail: '<s:property value="task.claim.id"/>',
                    causalPart: newValue
                },
                makeLocal: true
            }]);

        });
        dojo.connect(dojo.byId("addFaultCode"), "onclick", function() {
            if (renderer) {
                renderer.renderSelectedCode();
                if(treeWidget.selectedNode){
                    faultCodeId = treeWidget.selectedNode.item ? treeWidget.selectedNode.item.faultCode.id : treeWidget.selectedNode.faultCode.id;
                    if (dojo.byId("existingFaultCode")) {
                        dojo.html.hide(dojo.byId("existingFaultCode"));
                    }
                    twms.ajax.fireJsonRequest("findAttributesForFaultCode.action?selectedBusinessUnit=<s:property value="%{selectedBusinessUnit}" />", {
                        faultCodeID: treeWidget.selectedNode.item ? treeWidget.selectedNode.item.faultCode.id : treeWidget.selectedNode.faultCode.id,
                        claimDetails: claim
                    }, function(data) {
                        var attrInfo = eval(data)[0];
                        var content = '' ;
                        if (attrInfo["Claim"] != '-') {
                            content = '<s:text name="label.additionalAttribute.enterAttribute"/>';
                        }
                        dojo.byId("fautCodeAttributes").innerHTML = content;
                        var parentContentPane = dijit.byId("faultCodeAttributeContentPane");
                        parentContentPane.setContent('');
                        isFaultDialogDisplayed = false;
                    });
                }
            }
            dialog.hide();
        });
    });
    </script>