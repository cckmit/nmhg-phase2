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
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>


<style type="text/css">
    .servicePrecedureNode {
        font-family:Verdana,sans-serif,Arial,Helvetica;
        font-size:7.5pt;
        color:#000;
        padding-left : 20px;
        background-repeat: no-repeat;
    }
    .newFileIcon {
        background: url(image/newFile.gif) no-repeat 2px 2px;
    }
    .directoryIcon {
        background-image: url(image/directory.gif);
    }
</style>
<script type="text/javascript" src="scripts/ServiceProcedureTree.js"></script>
<script type="text/javascript">
        dojo.require("twms.data.EmptyFileWriteStore");
        dojo.require("twms.widget.Tree");
        dojo.require("twms.widget.TreeModel");
        dojo.require("dijit.form.Button");
        dojo.require("dojox.layout.ContentPane");
        function LTrim(str)
	    {
			if (str==null){return null;}
			for(var i=0;str.charAt(i)==" ";i++);
			return str.substring(i,str.length);
		}
	
		function RTrim(str)
		{
			if (str==null){return null;}
			for(var i=str.length-1;str.charAt(i)==" ";i--);
			return str.substring(0,i+1);
		}
	
		function Trim(str){return LTrim(RTrim(str));}

    var targetCollectionName = "task.claim.serviceInformation.serviceDetail.laborPerformed";
    function hideShowReason(hours,reason,checkHour) {
             if(hours.value!='') {
 				dojo.html.show(reason);
 				if(checkHour){
 				checkHour.value="false";
 				}
 			} else {
 				reason.value="";
 				if(checkHour){
 				checkHour.value="true";
 				}
 				dojo.html.hide(reason);
 			}
 		}
        var isEnableStandardLaborhrs = <s:property value="%{isStdLaborHrsToDisplay(task.claim)}"/>;
        var isProcessorReview = <s:property value="%{isProcessorReview()}"/>;
        var isEnableAdditionalLaborDetails = <s:property value="isEligibleForAdditionalLaborDetails(task.claim)"/>;       
</script>

<script type="text/javascript">
    dojo.addOnLoad(function(){
        var tree = '';
        var isMultipleJobCodeAllowed = <s:property value="%{isMultipleJobCodeAllowed()}"/>;

        var alreadySelectedNodes = <s:property value="selectedJobsJsonString" escape="false"/>;
    	var serviceProcId;
    	var claimId = '<s:property value="task.claim.id"/>';
    	var businessUnitInfo = "<s:property value="task.claim.businessUnitInfo"/>";

        var casualPartChanged = false;
        var spFirstTime = true;
        var spTreeWidget = null;
        var spTreeManager = null;
        var dialog = dijit.byId("treeBrowser");
        var spRenderer = null;

        if (dijit.byId("causalBrandPart") == null || dijit.byId("causalBrandPart").getValue()) {
            destroyAndRecreateTree();
            initSpTree(tree, alreadySelectedNodes);
        } else {
            spFirstTime = false;
        }

        function initSpTree(tree, selectedNodes) {
            spRenderer = new tavant.twms.ServiceProcedureRenderer(dojo.byId("serviceProcedureTBody"), spTreeWidget, selectedNodes,
            rowTemplateText.markup, rowTemplateText.script, targetCollectionName,null,isMultipleJobCodeAllowed);
            spRenderer.renderSelectedRows(selectedNodes);
        }
        
        function renderTree(data, alreadySelectedNodes){
            var div = document.createElement("div")            
            dojo.byId("serviceProcedureTreeParent").appendChild(div);
            var spStoreId = new twms.data.EmptyFileWriteStore({});
            spStoreId.data = data;
            var spModel = new twms.widget.TreeModel({
            	store:spStoreId,
            	treeType:'JOBCODE',
            	 claimDetail: claimId,
                 causalPart: (dijit.byId("causalPart")) ? dijit.byId("causalPart").getValue() : null,
                 selectedBusinessUnit : businessUnitInfo
            	});
            spTreeWidget = new twms.widget.Tree(
                        {
                            id : "serviceProcedureTreeWidget",
                            labelAttr : "name",
                            model : spModel,
                            multipleSelectionAllowed: isMultipleJobCodeAllowed,
                            label : "Service Procedures Codes",
                            defaultSelectedNodes:alreadySelectedNodes,
                            rootId: 'spRoot',
                            persist: false
                        }, div);
            casualPartChanged = false;
            if(spRenderer){ // set the treewidget to the old instance of 
                spRenderer.treeWidget = spTreeWidget;
            }
            spRenderer = new tavant.twms.ServiceProcedureRenderer(dojo.byId("serviceProcedureTBody"), spTreeWidget, alreadySelectedNodes,
                rowTemplateText.markup, rowTemplateText.script, targetCollectionName,null,isMultipleJobCodeAllowed);
        }
        refreshDiagWidget();
        dojo.subscribe("/causalPart/changed", null, function() {
        	//below conditions are added for avoiding auto closing of service information part after copying causal part number
        	//if(dojo.byId("causalPartTemp").value==""){
        	//	dojo.byId("causalPartTemp").value = dojo.byId("causalPart").value;
        	//}
        	//if(!(dojo.byId("causalPart").value == dojo.byId("causalPartTemp").value)){
        			 if (!spFirstTime) {
        	                casualPartChanged = true;
        	                if (spRenderer) {
        	                    spRenderer.deleteSelectedNodes();
        	                }
        	            }
        	//		dojo.byId("causalPartTemp").value = dojo.byId("causalPart").value;
        	//	}
        });

        function destroyAndRecreateTree() {
            if (spTreeWidget) {
                rowCount = spRenderer.index;
                spTreeWidget.destroyRecursive();
            }
        }
        dojo.connect(dojo.byId("browseTreeButton"), "onclick", function() {
            if (casualPartChanged || spFirstTime) {
                destroyAndRecreateTree();
                spFirstTime = false;
                if (dijit.byId("causalBrandPart") == null || dijit.byId("causalBrandPart").getValue()) {
                	var causalPart="";
                	if(dojo.byId("causalPart")){
                		causalPart = dojo.byId("causalPart").value;
                	}                  
                    var params = { claimDetail : claimId,
                                   causalPart : causalPart ,
                                   selectedBusinessUnit : businessUnitInfo };
                    twms.util.putLidOn(dijit.byId("spTreeContainer").domNode);
                    dojo.html.setDisplay(dojo.byId("errorDivForServiceProcedureTree"), false);
                    var previouslySelectedNodes = new Array();
                    if(spRenderer){
                        dojo.forEach(spRenderer.selectedRows, function(row){
                            if(row.nodeObject.listed)
                                previouslySelectedNodes.push(row.nodeObject);
                        });
                    }
                    twms.ajax.fireJsonRequest("get_service_procedure_json.action", params, function(data) {
                    	 dojo.cookie("serviceProcedureTreeWidgetSaveStateCookie",null); // delete the old cookie so that tree widget doesn't open based on previous storage
                        renderTree(data,previouslySelectedNodes);
                        dialog.containerNode.scrollTop = 0; //scroll to Top
                        twms.util.removeLidFrom(dijit.byId("spTreeContainer").domNode);
                    });
                }
            }
            if (dijit.byId("causalBrandPart") && !dijit.byId("causalBrandPart").getValue()) {
               dojo.html.setDisplay(dojo.byId("errorDivForServiceProcedureTree"), true);
            }
            dialog.show();
        });

        dojo.connect(dojo.byId("addServiceProcedures"),"onclick", function() {
            spRenderer.renderSelectedRows();
            dialog.hide();
            refreshDiagWidget();
        });
        bindRepeatTableKeyboardActions(dojo.byId("serviceProcedureRepeatTable"), dojo.byId("browseTreeButton"), "repeat_del");
    });
</script>
<table width="96%" class="grid">
	<s:if test= " isThirdParty " >
		<tr>
			<td align="center">
				<s:text name="label.laorRate.thirdPartyLaborRate" />
			</td>
			<td></td>
			<td align="left">
				<t:money id="thirdPartyLaborRate" 
	                  	name="task.claim.serviceInformation.thirdPartyLaborRate" defaultSymbol="$"
	                  	 value="%{task.claim.serviceInformation.thirdPartyLaborRate}"/>
			</td>
		</tr>
	</s:if>
</table>
<s:if test="task.claim.state.state=='draft'">
    <s:hidden name="task.claim.serviceInformation.serviceDetail.stdLaborEnabled" value="%{isEnableStandardLaborHours()}"/>
</s:if>
  <div class="mainTitle" style="margin-bottom:5px;">
            <s:text name="label.common.serviceProcedure"/></div>
<table class="grid borderForTable" id="serviceProcedureRepeatTable" width="97%">

    <thead>
      

        <tr class="row_head">
            <th><s:text name="label.campaign.jobCode"/></th>            
            <th><s:text name="label.campaign.jobCodeDescription"/></th>            
            <s:if test="%{!isStdLaborHrsToDisplay(task.claim)}">
            <th ><s:text name="label.campaign.laborHrsEntered"/></th>
            </s:if>
            <s:if test="%{isStdLaborHrsToDisplay(task.claim) || isProcessorReview()}">
            <th><s:text name="label.newClaim.suggestedLaborHours"/></th>
            </s:if>
            <s:if test="isEligibleForAdditionalLaborDetails(task.claim)">
            <th><s:text name="label.newClaim.additionalLaborHours"/></th>
            <th><s:text name="label.newClaim.reasonAdditionalLaborHrs"/></th>
            </s:if>
            <th ><s:text name="label.common.additionalAttributes"/></th>
            <th width="9%"><div class="repeat_add" id="browseTreeButton"></div></th>
        </tr>
    </thead>
    <tbody id="serviceProcedureTBody"></tbody>
</table>
<u:jsVar varName="rowTemplateText">
    <tr index="${index}">    	
        <td>
        	
            <input type="hidden" name="task.claim.serviceInformation.serviceDetail.laborPerformed[${index}]"
                value="${wrapperId}"/>
            <input type="hidden" name="task.claim.serviceInformation.serviceDetail.laborPerformed[${index}].serviceProcedure"
                value="${id}"/>
            <span title="${fullNameTooltip}">${completeCode}</span>
        </td>
        <td>    		
            <span title="${fullNameTooltip}">${jobCodeDescription}</span>            
								
    	</td>
        <td id="laborHrsEnteredSection[${index}]">
             <input type="text" size="5" class="numeric" id="laborHrsEntered[${index}]"
                       name="task.claim.serviceInformation.serviceDetail.laborPerformed[${index}].laborHrsEntered"
                       value="${laborHrsEntered}"/>
        </td>
       
       <td id="stdLaborHrsSection[${index}]">          
                <s:if test="task.claim.type.type == 'Campaign'">
                <input type="hidden"
                   name="task.claim.serviceInformation.serviceDetail.laborPerformed[${index}].hoursSpent"
                   value="${specifiedHoursInCampaign}"/>
                    <span>${specifiedHoursInCampaign}</span>
                </s:if>
                <s:else>
                <input type="hidden"
                   name="task.claim.serviceInformation.serviceDetail.laborPerformed[${index}].hoursSpent"
                   value="${suggestedLabourHours}"/>
                    <span>${suggestedLabourHours}</span>	
                </s:else>
        </td>
        
        <td id="addLaborHrsSection[${index}]">

            <input type="text" size="3" class="numeric" id="hours[${index}]" 
                name="task.claim.serviceInformation.serviceDetail.laborPerformed[${index}].additionalLaborHours"
                value="${additionalLaborHours}" onblur="this.value=Trim(this.value);"/>

            <input type="hidden" name="task.claim.serviceInformation.serviceDetail.laborPerformed[${index}].emptyAdditionalHours"
                value="${emptyAdditionalHours}" id="checkHour[${index}]"/>
            
        </td>
        <td id="reasonForAddLaborHrsSection[${index}]">
            <input type="text" name="task.claim.serviceInformation.serviceDetail.laborPerformed[${index}].reasonForAdditionalHours"
                value="${reasonForAdditionalHours}" size="50" id="reason[${index}]"/>
        </td>
        <td>
        	  <span>
			  <input type="hidden" id="test" name="counter" value="${index}"/>
			  </span>
			  <input type="hidden" id="laborDetailsAttributesPresent_${index}" value="${hasAdditionalAttributes}"/>	
			<s:a cssStyle="cursor:pointer;color:blue;text-decoration:underline" cssClass="alinkclickable" href="#">
        		<span id="laborDetailsAttributes_${index}" class="alinkclickable">
        			<div id="jobAttributeLinkNotPrepared_${index}"></div>
        		</span>
        	</s:a>

            <div id ="dialogBoxContainer" style="display: none" style="overflow-X: auto; overflow-Y: auto" >
			<div dojoType="twms.widget.Dialog" id="jobCodeAtrribute_${index}" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
				toggleDuration="250" title="<s:text name="label.common.additionalAttributes"/>" style="width:500px; height:300px;">
                <div dojoType="dojox.layout.ContentPane" id="jobAttributeContentPane_${index}"
                        style="width: 95%; margin: 5px 0 5px 15px">

                </div>
				</div>
			</div>
       <%--<s:if test="%{!isMultipleJobCodeAllowed()}">
        <div id = "dialogBoxContainer" style="display: none" style="overflow-X: auto; overflow-Y: auto" >
			<div dojoType="twms.widget.Dialog" id="jobCodeAtrribute_0" bgColor="#FFF" bgOpacity="0.5" toggle="fade"
				toggleDuration="250" title="<s:text name="label.common.additionalAttributes"/>">
				<div dojoType="dijit.layout.LayoutContainer" style="background: #F3FBFE; border: 1px solid #EFEBF7">
				 	<div dojoType="dojox.layout.ContentPane" id="jobAttributeContentPane_0}"
                          style="width: 95%; margin: 5px 0 5px 15px">
						<jsp:include flush="true" page="job-code-claim_attribute.jsp" />
				  </div>
				 </div>
				</div>
			</div>
       </s:if>            
       <s:else>

        </s:else>--%>
        </td>
        <td>
            <div class="repeat_del"></div>
        </td>
    </tr>
    <script type="text/javascript">
    	dojo.addOnLoad(function() {            
    		var hours = dojo.byId('hours[${index}]');
    		var reason = dojo.byId('reason[${index}]');
    		var checkHour=dojo.byId('checkHour[${index}]');
            if(!isEnableStandardLaborhrs){
                dojo.html.show(dojo.byId('laborHrsEnteredSection[${index}]'));
                dojo.html.hide(dojo.byId('hours[${index}]'));
                if (!isProcessorReview) {
                    dojo.html.hide(dojo.byId('stdLaborHrsSection[${index}]'));
                }
            }else{
                dojo.html.hide(dojo.byId('laborHrsEnteredSection[${index}]'));
                dojo.html.show(dojo.byId('hours[${index}]'));
                dojo.html.show(dojo.byId('stdLaborHrsSection[${index}]'));
            }
            if(isProcessorReview){
               dojo.html.show(dojo.byId('stdLaborHrsSection[${index}]'));
            }
            if( hours.value == '') {    		
    			dojo.html.hide(reason);
    			if(checkHour){
    			checkHour.value="true";
    			}
    		}
            if(isEnableAdditionalLaborDetails){
                dojo.html.show(dojo.byId('addLaborHrsSection[${index}]'));
                dojo.html.show(dojo.byId('reasonForAddLaborHrsSection[${index}]'));                             
            }else{
                dojo.html.hide(dojo.byId('addLaborHrsSection[${index}]'));
                dojo.html.hide(dojo.byId('reasonForAddLaborHrsSection[${index}]'));               
            }
    		dojo.connect(hours, "onblur", function() {
                hideShowReason(hours,reason,checkHour);
    		});

    		var claim='<s:property value="task.claim.id"/>';
            if(dojo.byId("jobAttributeLinkNotPrepared_"+${index})) {
	            twms.ajax.fireJsonRequest("findAttributesForJobCode.action", {
	                    serProcedureId: ${id},
	                    claimDetails: claim
	                }, function(data) {
	                	var attrInfo = eval(data)[0];
	                	var content= '' ;
	                    if(attrInfo["Claim"] != '-') {
	                        prepareAttributeLink(${index},${id}, true);
	                    }else {
	                    	prepareAttributeLink(${index},${id}, false);
	                    }
	                });
            }
            
    	}); 
    </script>
</u:jsVar>
<s:hidden id="causalPartTemp" value=""></s:hidden>
<div style="display: none;">
    <div dojoType="twms.widget.Dialog" id="treeBrowser" 
            title="<s:text name="title.newClaim.selectServiceProcedure"/>" style="width:80%;">
            <div id="errorDivForServiceProcedureTree" style="display:none" class="twmsActionResultsErrors">
                <s:text name="error.newClaim.causalPartNoRequired"/>
            </div>
            <div dojoType="dijit.layout.ContentPane" layoutAlign="top" style="width:99%;height:380px;overflow:auto " id="spTreeContainer">
                <div id="serviceProcedureTreeParent">
                </div>
            </div>
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="border-top:1px solid #EFEBF7;" align="center">
            <button id="addServiceProcedures" style="margin:1px;"><s:text name="label.newClaim.addServiceProcedure"/></button>
        </div>
    </div>
</div>    
<s:if test="isLaborSplitEnabled()">
  <div class="mainTitle" ><s:text name="label.laborType.laborType"/></div>
<u:repeatTable id="labor_type_table" cssClass="grid borderForTable" width="97%">
    <thead>
      
        <tr class="row_head">
            <th ><s:text name="lable.labor.type.additional"/></th>
            <th><s:text name="lable.labor.type.labor.hrs"/></th>
            <th><s:text name="lable.labor.type.reason"/></th>
            <s:if test="getLaborSplitOption() == 'OPTIONAL'">
           	 	<th><s:text name="lable.labor.type.inclusive"/></th>
            	<th><u:repeatAdd id="labor_type_adder"><div class="repeat_add"/></u:repeatAdd></th>
            </s:if>
            <s:else>
				<th width="9%"><u:repeatAdd id="labor_type_adder"><div class="repeat_add"/></u:repeatAdd></th>
			</s:else>	
        </tr>
    </thead>
    <u:repeatTemplate id="labor_type_body"
          value="task.claim.serviceInformation.serviceDetail.laborSplit">
    	<tr index="#index">
    		<s:hidden name="task.claim.serviceInformation.serviceDetail.laborSplit[#index]"></s:hidden>
     		<td width="23%" class="labelNormalTop">     		  
         		<s:select name="task.claim.serviceInformation.serviceDetail.laborSplit[#index].laborType"
              		 list="prepareLaborTypeList"
               		 listKey="id" listValue="laborType"
              		 headerKey="" headerValue="--Select--"
              	     value="%{task.claim.serviceInformation.serviceDetail.laborSplit[#index].laborType.id.toString()}"/>
    	    </td>
    	    <td>
                  <s:textfield id="labor_split_hrs_#index"  size="5"
                  	name="task.claim.serviceInformation.serviceDetail.laborSplit[#index].hoursSpent"/>
    		</td>
     		<td >
                  <s:textfield size="35" name="task.claim.serviceInformation.serviceDetail.laborSplit[#index].reason"/>
    		</td>
     		
     		<s:if test="getLaborSplitOption() == 'ALWAYSINCLUSIVE'">
     		   <input type="hidden" name="task.claim.serviceInformation.serviceDetail.laborSplit[#index].inclusive"
                		   value="true"/>
     		</s:if>	
     		<s:if test="getLaborSplitOption() == 'ALWAYSEXCLUSIVE'">
   				<input type="hidden" name="task.claim.serviceInformation.serviceDetail.laborSplit[#index].inclusive"
                		  value="false"/>
     		</s:if>	
     		<s:if test="getLaborSplitOption() == 'OPTIONAL'">
     		    <td align="center">
   					<s:checkbox id="inclusive_#index" name="task.claim.serviceInformation.serviceDetail.laborSplit[#index].inclusive"  
     					  value="%{task.claim.serviceInformation.serviceDetail.laborSplit[#index].inclusive}" />
     		  	</td>
     		</s:if>				 
    		<td>           
		        <u:repeatDelete id="labor_type_deleter_#index">
            		<div class="repeat_del"/>
        		</u:repeatDelete>
     		</td>
  		</tr> 
 	</u:repeatTemplate>                           
</u:repeatTable>
</s:if>






<s:if test="roundUpLaborDetail!=null">
		  <s:hidden name="roundUpLaborDetail" value="%{roundUpLaborDetail}"/>
<div style="width: 100%" style="padding-left:2px">
		<div id="roundup_labor_details" class="mainTitle"
			style="margin-top: 5px;"><s:text name="label.common.roundUpLabor" />
		<table width="100%" class="grid borderForTable">
			<tr class="row_head">
				<th><s:text name="label.campaign.jobCode" /></th>
				<th><s:text name="label.campaign.laborHrsEntered" /></th>
			</tr>
			<tr>
				<td class="partReplacedClass"><s:property
					value="roundUpLaborDetail.serviceProcedure.definition.code" />
				</td>
				<td class="partReplacedClass"><s:property
					value="roundUpLaborDetail.hoursSpent" />
				</td>
			</tr>
		</table>
		</div>
		</div>
</s:if>
<script type="text/javascript">
     function prepareAttributeLink(index,serviceProcId,attrPresent){
         if(attrPresent) {
	        dijit.byId('jobCodeAtrribute_'+index).formNode=document.getElementById("claim_form");
	        dojo.byId("laborDetailsAttributes_"+index).innerHTML='<s:text name="label.additionalAttribute.enterAttribute"/>'
	        dojo.connect(dojo.byId("laborDetailsAttributes_"+index),"onclick",function(){
	            dojo.publish("/job_"+index+"/attribute/show");
	        });
	
	        dojo.subscribe("/job_"+index+"/attribute/hide", null, function() {
				dijit.byId("jobCodeAtrribute_"+index).hide();
			});
	
	        dojo.subscribe("/job_"+index+"/attribute/show", null, function() {
	            var dlg = dijit.byId("jobCodeAtrribute_"+index);
	            
	            if (!dojo.byId("jobCodeAttrPopupClose_"+index))
	            {
	            	var claim='<s:property value="task.claim.id"/>';
	            	var toFetchJobCode = 'false';
	
	            	toFetchJobCode = 'true';
	                var params = {
	                    claimDetails: claim,
	                    indexId: index,
	                    serProcedureId: serviceProcId,
	                    fetchJobCode: toFetchJobCode
	                }
	                var url = "getAttributesForJobCode.action";
	                twms.ajax.fireHtmlRequest(url,params, function(data) {
	                        var parentContentPane = dijit.byId("jobAttributeContentPane_"+index);
	                        parentContentPane.setContent(data);
	                        dojo.connect(dojo.byId("jobCodeAttrPopupClose_"+index),"onclick",function() {
	                            dojo.publish("/job_"+index+"/attribute/hide");
							});
	                    }
	                );
	            }
	            dlg.show();
	        });
         }else {
        	 dojo.byId("laborDetailsAttributes_"+index).innerHTML=''
         }
      }
</script>
