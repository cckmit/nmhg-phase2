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
<%@ taglib prefix="tda" uri="twmsDomainAware" %>
<style type="text/css">
</style>
<script type="text/javascript">
	dojo.require("twms.widget.TitlePane");
	dojo.require("twms.widget.Select");
</script>

		<tr>
		<td width="10%">
            <table>
                <tr>
                    <td>
                        <s:text name="label.partReturn.accept"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <s:textfield size="2" id="accept_%{#partsCounter}"
                                     name='partReplacedBeans[%{#partsCounter}].accepted'
                                     value="%{accepted}"/>
                        <script type="text/javascript">
                            dojo.addOnLoad(function() {
                                dojo.connect(
                                        dojo.byId("accept_" + <s:property value="%{#partsCounter}"/>), "onchange",
                                        function(evt)
                                        {
                                            updateForm(<s:property value="%{#partsCounter}"/>, 'accept');
                                        }
                                        );
                            });
                        </script>
                    </td>
                </tr>
            </table>
        <s:hidden id="index" name ="index" value="%{#partsCounter}"/>
        <td width="90%" id="test" align="center">
            <table width="100%">
                <%-- <tr>
                    <td>
                        <s:checkbox name='partReplacedBeans[%{#partsCounter}].allowMultipleReasons'
                                    value="multipleAcceptanceCauses" id="multipleReasonAccept_%{#partsCounter}"
                                /><s:text name="label.partReturn.multipleCodes"/>
                        <script type="text/javascript">
                            dojo.addOnLoad(function() {
                                dojo.connect(
                                        dojo.byId("multipleReasonAccept_" + <s:property value="%{#partsCounter}"/>), "onchange",
                                        function(evt)
                                        {
                                            updateForm(<s:property value="%{#partsCounter}"/>, 'accept');
                                        }
                                        );
                            });
                        </script>
                    </td>
                </tr> --%>
                <tr>
                    <td>
                    <tda:lov name="partReplacedBeans[%{#partsCounter}].acceptanceCauses[0]"
                 className="PartAcceptanceReason"
                 id="acceptReasons_%{#partsCounter}"
                 businessUnitName="claim.businessUnitInfo.name"></tda:lov></td>
                </tr>
            </table>
        </td>
	    </tr>
	    <tr>
	    <td width="10%">
            <table>
                <tr>
                    <td>
                        <s:text name="label.partReturn.reject"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <s:textfield size="2" id="reject_%{#partsCounter}"
                                     name='partReplacedBeans[%{#partsCounter}].rejected'
                                     value="%{rejected}"
                                ></s:textfield>
                        <script type="text/javascript">
                            dojo.addOnLoad(function() {
                                dojo.connect(
                                        dojo.byId("reject_" + <s:property value="%{#partsCounter}"/>), "onchange",
                                        function(evt)
                                        {
                                            updateForm(<s:property value="%{#partsCounter}"/>);
                                        }
                                        );
                            });
                        </script>
                    </td>
                </tr>
            </table>
     	 </td>

        <td width="90%" align="center">
            <table width="100%">
                <%-- <tr>
                    <td>
                        <s:checkbox name='partReplacedBeans[%{#partsCounter}].allowMultipleReasons'
                                    value="multipleFailureCauses" id="multipleReasonReject_%{#partsCounter}"
                                /><s:text name="label.partReturn.multipleCodes"/>
                        <script type="text/javascript">
                            dojo.addOnLoad(function() {
                                dojo.connect(
                                        dojo.byId("multipleReasonReject_" + <s:property value="%{#partsCounter}"/>), "onchange",
                                        function(evt)
                                        {
                                            updateForm(<s:property value="%{#partsCounter}"/>);
                                        }
                                        );
                            });
                        </script>
                    </td>
                </tr> --%>
                <tr>
                    <td>
                        <tda:lov name="partReplacedBeans[%{#partsCounter}].failureCauses[0]"
                                 className="FailureReason" id="failureReasons_%{#partsCounter}"
                                 businessUnitName="claim.businessUnitInfo.name"></tda:lov>
                    </td>
                </tr>
            </table>
		</td>
		</tr>
<script type="text/javascript">

var acceptSelectBoxCount = [];
var rejectSelectBoxCount = [];
var acceptanceData = dojo.fromJson(getDataForAcceptance());;
var failureDate = dojo.fromJson(getDataForFailure());
function updateForm(index,action){
	
	var accepted = (action != null);
	var check=(accepted) ? dojo.byId("multipleReasonAccept_"+index) : dojo.byId("multipleReasonReject_"+index) ; 
	var parentNodeId = (accepted) ? "acceptReasons_" : "failureReasons_";
	var selectBoxNameSuffix = (accepted) ? "acceptanceCauses" : "failureCauses";
	var valueNodeIdPrefix = (accepted) ? "accept_" : "reject_";
	var reasonNode = dijit.byId(parentNodeId + index).domNode;
    var parentNode = getExpectedParent(reasonNode, "td");
		
	var acceptOrRejectValue = dojo.byId(valueNodeIdPrefix + index).value;
	
	
	if (isNaN(acceptOrRejectValue) || acceptOrRejectValue < 0) {
		acceptOrRejectValue = 0;
		dojo.byId(valueNodeIdPrefix + index).value = 0;
	}
	
	var newSelectBoxCount = (check.checked) ? acceptOrRejectValue : 0;
	
	if(acceptOrRejectValue > 0) {
		newSelectBoxCount--;
	}
	
	var selectBoxCount = (accepted) ? acceptSelectBoxCount : rejectSelectBoxCount;
	var existingSelectBoxCount = selectBoxCount[index];		
	if(existingSelectBoxCount != null && existingSelectBoxCount> 0 ) {	
			newSelectBoxCount -= existingSelectBoxCount;
					
			if(newSelectBoxCount == 0) {
				return;
			}
			var toBeAdded = newSelectBoxCount > 0;
			selectBoxCount[index] += newSelectBoxCount;
			
			newSelectBoxCount = Math.abs(newSelectBoxCount);
			for(var i = 0 ; i < newSelectBoxCount; i++) {
				var suffix = (toBeAdded) ? (existingSelectBoxCount + i + 1 ) : 
					(existingSelectBoxCount - i );
				var id = valueNodeIdPrefix + index + "_" + suffix;
				if(toBeAdded) {
				
					addSelectBox(id, index, selectBoxNameSuffix, parentNode, i, accepted);
				} else {
					removeSelectBox(id);
				}
			}		
	} else {
		for(var i = 1 ; i <= newSelectBoxCount; i++) {
			var id = valueNodeIdPrefix + index + "_" + i;
			addSelectBox(id, index, selectBoxNameSuffix, parentNode, i,accepted);
		}
		
		selectBoxCount[index] = newSelectBoxCount;
	}
}

function addSelectBox(id, index, selectBoxNameSuffix, parentNode, i, accepted) {
    var selectBox = (!accepted) ? new twms.widget.Select({
		initialData : failureDate,
		id : id,
		name: "partReplacedBeans[" + index + "]." +	selectBoxNameSuffix + "["+i+"]"
	}) : new twms.widget.Select({
		initialData : acceptanceData,
		id : id,
		name: "partReplacedBeans[" + index + "]." +	selectBoxNameSuffix + "["+i+"]"
	});

	parentNode.appendChild(selectBox.domNode);
}

function removeSelectBox(id) {
	var selectBox = dijit.byId(id);
	if(selectBox) {
		selectBox.destroyRecursive();
	}
}
function getDataForFailure() {
	  return '<s:property escape="false" value="getFailureReasonArrayForBusinessUnit(claim.businessUnitInfo.name)"/>' ;
}
function getDataForAcceptance(){
	return '<s:property escape="false" value="getAcceptanceReasonArrayForBusinessUnit(claim.businessUnitInfo.name)"/>';
}
</script>
