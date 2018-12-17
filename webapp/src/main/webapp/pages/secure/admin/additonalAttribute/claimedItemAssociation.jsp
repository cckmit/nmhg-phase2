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
  <script type="text/javascript">  
		dojo.require("twms.widget.Tree");
        dojo.require("twms.widget.TreeModel");
		dojo.require("twms.data.EmptyFileWriteStore");
      	dojo.require("dijit.form.Button");
      	dojo.require("twms.widget.Dialog");
      	dojo.require("dojox.layout.ContentPane");   
    </script>

<script type="text/javascript" src="scripts/ServiceProcedureTree.js"></script>
<u:repeatTable id="inv_table" cssClass="grid borderForTable" width="99%" 
cssStyle="margin:5px;" cellspacing="0" cellpadding="0">
    <thead>
        <tr class="row_head">
        	<th width="15%"><s:text name="columnTitle.common.product"/></th>
        	<th width="15%"><s:text name="columnTitle.common.model"/></th>
        	<th width="30%"><s:text name="label.common.jobCode"/></th>
        	<th width="30%"><s:text name="columnTitle.duePartsInspection.faultcode"/></th>
            <th width="9%"><u:repeatAdd id="inv_adder"><div class="repeat_add"/></u:repeatAdd></th>
        </tr>
    </thead>
    
    <u:repeatTemplate id="inv_body"
        value="claimedItemAttrList">  
		<tr index="#index">
		  <s:hidden name="claimedItemAttrList[#index]" value="%{claimedItemAttrList[#index].id}" id="claimedItemAttrList#index"/>     
			<td style="border:1px solid #EFEBF7">
			<s:hidden id ="association_#index" name="claimedItemAttrList[#index].associated" value="true" ></s:hidden>
			 	   <sd:autocompleter name='claimedItemAttrList[#index].product.name' keyName='claimedItemAttrList[#index].product' href='list_product_for_association.action' showDownArrow='false' loadMinimumCount='3' value='%{claimedItemAttrList[#index].product.name}' key='%{claimedItemAttrList[#index].product.id}' id='product_#index' />
		    </td>
            <td style="border:1px solid #EFEBF7">
            	<sd:autocompleter name='claimedItemAttrList[#index].itemGroup' keyName='claimedItemAttrList[#index].itemGroup' href='list_model_for_association.action' loadMinimumCount='0' value='%{claimedItemAttrList[#index].itemGroup.name}' key='%{claimedItemAttrList[#index].itemGroup.id}' id='model_#index' listenTopics='/product/queryAddParams/#index' />
				
				<script type="text/javascript">
				  	dojo.addOnLoad(function() {
				  		var url= "list_model_for_association.action";
                        var productName = '<s:property value="%{claimedItemAttrList[#index].product.name}"/>';
                        var productId = '<s:property value="%{claimedItemAttrList[#index].product.id}"/>';
                        var product= dijit.byId("product_#index");
                        dojo.connect(dijit.byId("product_#index"), "onChange", function() {
                            dojo.publish("/product/queryAddParams/#index", [{
                                url: url,
                                params: {
                                    productId: product.getValue()  ? product.getValue() : productId 
                                }
                            }]);
                        });
                        dojo.connect(dojo.byId("browseTreeButton_#index"), "onclick", function(event) {
				        		var dialog = dijit.byId("treeBrowser_#index");
				            	dojo.stopEvent(event);
				            	generateJobCodeTree(#index);
				            	dialog.show();
		           			 });
		            	dojo.connect(dojo.byId("addServiceProcedures_#index"),"onclick", function() {
		            		var dialog = dijit.byId("treeBrowser_#index");
			            	if(renderer.treeWidget.selectedNodesList[0]){
			            		
			            		if ( ( dojo.byId("jobCodeId_#index") == null) || 
			            				( dojo.byId("jobCodeId_#index") == 'undefined') )									
			            			createHiddenElement("serviceProcedure","jobCodeId_","job_code_", #index);
				                var code=renderer.treeWidget.selectedNodesList[0].item.completeCode;
				                var id=renderer.treeWidget.selectedNodesList[0].item.serviceProcedureId;
				                dojo.byId("job_code_#index").innerHTML=code;
				                dojo.byId("jobCodeId_#index").value=id;
				                
				                }
			                dialog.hide();
			                delete renderer.treeWidget.model;
			                renderer.treeWidget.model= null;
			            });
			            
			            dojo.connect(dojo.byId("launchFaultCodeTree_#index"), "onclick", function(event) {
			            	var dialog = dijit.byId("browserForFaultCode_#index");
			            	dojo.stopEvent(event);
							generateFaultCodeTree(#index);
							dialog.show();
	 					});
					 	dojo.connect(dojo.byId("addFaultCode_#index"),"onclick", function() {
					 		var dialog = dijit.byId("browserForFaultCode_#index");
							faultRenderer.renderSelectedCode();
							if(faultRenderer.treeWidget.selectedNode){
								if ( (dojo.byId("faultCodeID_#index")==null) || 
										(dojo.byId("faultCodeID_#index")== 'undefined'))
									createHiddenElement("faultCode","faultCodeID_","fault_code", #index);
								var code=faultRenderer.treeWidget.selectedNode.item.completeCode;
								var id=faultRenderer.treeWidget.selectedNode.item.faultCode.id;
								dojo.byId("faultCodeID_#index").value=id;
								dojo.byId("fault_code#index").innerHTML=code;
							}
							dialog.hide();
							delete faultRenderer.treeWidget.model;
			                faultRenderer.treeWidget.model= null;
						});
	               });
			  </script>									        	
            </td>
            <td style="border:1px solid #EFEBF7">
            	<label><s:text name="label.common.jobCode"/>:</label>
            	<span id="job_code_#index">
            	    <s:property value="%{claimedItemAttrList[#index].serviceProcedure.definition.code}"/>
            	    <s:if test="claimedItemAttrList[#index].serviceProcedure!=null && claimedItemAttrList[#index].serviceProcedure.definition!=null && claimedItemAttrList[#index].serviceProcedure.definition.code!=null">
        	    		<s:hidden id="jobCodeId_#index" name="claimedItemAttrList[#index].serviceProcedure" />
        	    	</s:if>
            	</span>
            	 
					    <div dojoType="twms.widget.Dialog" id="treeBrowser_#index" 
					            title="<s:text name="title.newClaim.selectServiceProcedure"/>" 
					            style="width:60%;height:70%;">
					    	<span class="erasableErrorMessage" id="jobCodeMsg_#index">
					    	</span>
                            <div dojoType="dojox.layout.ContentPane">
                                <div id="serviceProcedureTreeParent_#index">
                                </div>
                            </div>
					        <div dojoType="dijit.layout.ContentPane" style="border-top:1px solid #EFEBF7">
					            <button id="addServiceProcedures_#index"><s:text name="label.newClaim.addServiceProcedure"/></button>
					        </div>
					    </div>
				
            	
            	
            	<button id="browseTreeButton_#index" style="background:#F3FBFE url(image/searchIcon.gif) no-repeat;
            	width : 16px;height : 16px;margin : 0px;padding : 0px;border : 0px;">
            	<div id="job_#index">
            	</div>
            	
            	</button>
            </td>
          	
            <td style="border:1px solid #EFEBF7"> 
            	<label><s:text name="label.common.faultCode"/>:</label>           	
            	<span id="fault_code#index">
                    <s:property value="claimedItemAttrList[#index].faultCode.definition.code"/>
            	    <s:if test="claimedItemAttrList[#index].faultCode!=null && claimedItemAttrList[#index].faultCode.definition!=null && claimedItemAttrList[#index].faultCode.definition.code!=null">
    	    			<s:hidden id="faultCodeID_#index" name="claimedItemAttrList[#index].faultCode" />
    	    		</s:if>
                </span>
            	 
                <div dojoType="twms.widget.Dialog" id="browserForFaultCode_#index"
                        title="<s:text name="title.newClaim.selectFaultCode"/>" style="width:60%;height:70%;">
                    <span class="erasableErrorMessage" id="faultCodeMsg_#index">
                    </span>
                        <div dojoType="dojox.layout.ContentPane" style="overflow:auto">
                            <div id="faultLocationTreeParent_#index">
                            </div>
                        </div>
                    <div dojoType="dijit.layout.ContentPane" style="border-top:1px solid #EFEBF7">
                        <button id="addFaultCode_#index"><s:text name="button.newClaim.setFaultCode"/></button>
                    </div>
                </div>
				
    			 	
            		<button id="launchFaultCodeTree_#index" style="background:#F3FBFE url(image/searchIcon.gif) no-repeat;
        				width : 16px;height : 16px;margin : 0px;padding : 0px;border : 0px;">
            		<div id="faultCode_#index">
            		 </div>
            		 </button>
            </td>
           
            <td style="border:1px solid #EFEBF7">
                <u:repeatDelete id="inv_tableDeleter_#index">
                    <div class="repeat_del"/>
                </u:repeatDelete>
            </td>
      </tr>
       <s:hidden name="claimedItemAttrList[#index]" value="%{claimedItemAttrList[#index].id}" />        
    </u:repeatTemplate>
</u:repeatTable>
<script type="text/javascript">
var spTreeManager;
var renderer;
var faultRenderer;
var treeWidget;
var faultCodeTreeManager;
function generateFaultCodeTree(index) {
	var faultSelected = '<s:property value="claimedItemAttrList['+index+'].faultCode.definition.code"/>';
	var modelId =dijit.byId("model_"+index).getValue();
	if (modelId=='') {
		dojo.byId("faultCodeMsg_"+index).innerHTML = '<s:text name="error.additionalAttribute.selectModel" />';
	}
	else {
		dojo.byId("faultCodeMsg_"+index).innerHTML = '';		
		var faultSpanTemplateText = {
		 markup : "",
		 script : ""
		};
        var treeWidgetId = "faultCodeTreeWidget" + index;
        if(dijit.byId(treeWidgetId)){
            dijit.byId(treeWidgetId).destroyRecursive();
        }
		twms.ajax.fireJsonRequest("list_faultCode_tree_for_association.action", {
		            modelId: modelId
		        }, function(data) {
		        	 if(data){
                            var divElement = document.createElement("div")
                            divElement.id = "faultCodeTreeWidgetDiv" +index;
                            dojo.byId("faultLocationTreeParent_"+index).appendChild(divElement);
                            var storeId = new twms.data.EmptyFileWriteStore({});
                            storeId.data = data;
                            var model = new twms.widget.TreeModel({store:storeId,treeType:'FAULTCODE'});
                            treeWidget = new twms.widget.Tree(
                                {
                                    id : "faultCodeTreeWidget"+index,
                                    labelAttr : "name",
                                    model : model,
                                    label : "Fault Location",
                                    defaultSelectedRadio:faultSelected
                                }, divElement);
                            dojo.setSelectable(treeWidget.domNode, false);
                            faultRenderer = new tavant.twms.FaultCodeRenderer(dojo.byId("faultCode_"+index), treeWidget, faultSelected, faultSpanTemplateText.markup);
			          }
		        	 else {
		        		 dojo.byId(faultCodeDialog)
		        	 }
		        }
		      );
	}
}

function generateJobCodeTree(index) {
	var alreadySelectedNode = '<s:property value="claimedItemAttrList['+index+'].serviceProcedure.definition.code"/>';
	var modelId =dijit.byId("model_"+index).getValue();
	if (modelId=='') {
		dojo.byId("jobCodeMsg_"+index).innerHTML = '<s:text name="error.additionalAttribute.selectModel" />';
	}
	else {
		dojo.byId("jobCodeMsg_"+index).innerHTML = '';
		var jobSpanTemplateText={
		 markup : "",
		 script : ""
		}
        var treeWidgetId = "serviceProcedureTreeWidget" + index;
        if(dijit.byId(treeWidgetId)){
            dijit.byId(treeWidgetId).destroyRecursive();
        }
		twms.ajax.fireJsonRequest("list_jobCode_tree_for_association.action", {
	            modelId: modelId
	        }, function(data) {
	        	if(data){
                    var div = document.createElement("div")
                    dojo.byId("serviceProcedureTreeParent_"+index).appendChild(div);
                    var spStoreId = new twms.data.EmptyFileWriteStore({});
                    spStoreId.data = data;
                    var spModel = new twms.widget.TreeModel({store:spStoreId,treeType:'JOBCODE'});
                    spTreeWidget = new twms.widget.Tree(
                                {
                                    id : "serviceProcedureTreeWidget"+index,
                                    labelAttr : "name",
                                    model : spModel,
                                    multipleSelectionAllowed: false,
                                    label : "Service Procedures Codes",
                                    defaultSelectedNodes: alreadySelectedNode
                                }, div);
                    renderer = new tavant.twms.ServiceProcedureRenderer(dojo.byId("job_"+index), spTreeWidget, alreadySelectedNode,jobSpanTemplateText.markup);
	            }
	        }
	    );
	}
  }
  
 function  createHiddenElement(labelSuffix,idSuffix,tdSuffix,index){
 	var parentTD= dojo.byId(tdSuffix +index);
 	var td = getExpectedParent(parentTD, "td");
    var hiddenInput = document.createElement("input");
    hiddenInput.type = "hidden";
    hiddenInput.name="claimedItemAttrList["+ index +"]." + labelSuffix;		   	   		   
    hiddenInput.id = idSuffix + index;
    td.appendChild(hiddenInput);
  }
  
  function setHiddenValue(index){
  	dojo.byId("association_"+ index).value = false;
  }
</script>
