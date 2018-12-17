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
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>

<html>
	<head>
	  <title><s:text name="title.manageFailureStructure.editableTree" /> </title>
	  <u:stylePicker fileName="base.css"/>
	  <u:stylePicker fileName="layout.css" common="true"/>
	  <u:stylePicker fileName="yui/reset.css" common="true"/>
	  <s:head theme="twms"/>
	  <jsp:include page="/i18N_javascript_vars.jsp"/>
      <script type="text/javascript">
			dojo.require("twms.widget.Select");
			dojo.require("twms.widget.Dialog");
            dojo.require("dijit.layout.LayoutContainer");
            dojo.require("dijit.layout.ContentPane");

      var itemGroupIdVariable = null;
      var isReadOnly = null;
      dojo.addOnLoad(function() {
    	  
    	  isReadOnly = false;
    	  <s:if test = "%{!isCreateModifyAllowed()}">
			  dojo.byId('saveButton').disabled = true; 
			  isReadOnly = true;
    	  </s:if>

          var treeLoadingLid = dojo.byId("treeLoadingLid");

          //var binding
          FAILURE_TREE_VARS.errorParams = {
              messageWrapper : errorMessageWrapperMarkup,
              message : errorMessageMarkup
          };
          
          //binding the comboxbox stuff... to reload the tree... when the value is changed
          var itemGrpIdBox = dijit.byId("itemGroupId");
          var loadTree = function (itemGrpId) {
              dojo.html.show(treeLoadingLid);
              twms.ajax.fireJsonRequest("populate_failure_tree_data.action", {itemGroupId : itemGrpId, failureContext : "failureCause"}, function(data) {
                  publishEvent("/failureTree/load", data);
                  itemGroupIdVariable = itemGrpId;
                  dojo.html.hide(treeLoadingLid);
              });
          }

          dojo.connect(itemGrpIdBox, "onChange", function(itemGrpId) {
              loadTree(itemGrpId);
          });
          
          //on click of save button... firing the event to make the tree serialize.
          dojo.connect(dojo.byId("saveButton"), "onclick", function(event) {
              dojo.stopEvent(event);
              publishEvent("/failureTree/serialize");
          });

          //on return event getting published... submit the form
          dojo.subscribe("/failureTree/return", null, function(tree) {
              dojo.byId("updatedTree").value = tree;
              dojo.byId("failureTreeForm").submit();
          });

          //in case... there is a validation error.... show error dialog... insteed of submiting.
          var errorDialog = dijit.byId("saveErrorDialog");
          dojo.subscribe("/failureTree/invalid", null, function() {
              errorDialog.show();
          });
          //on dismiss button click... hide error dialog.
          dojo.connect(dojo.byId("dismissErrorDialog"), "onclick", function() {
              errorDialog.hide();
          });
      });
		</script>
	  <u:stylePicker fileName="spTreeEditor.css"/>
	   <u:stylePicker fileName="base.css"/>
	    <u:stylePicker fileName="common.css"/>
	<style>
		  .grid{
		  	width:99%;
		  }
		  .grid tr td{
		  	color:#545454;
			font-family:Arial, Helvetica, sans-serif;
			font-size:9pt;
		  }
  </style>
  </head>
	<u:body>
		<u:actionResults/>
		<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
			<div dojoType="dijit.layout.ContentPane" layoutAlign="top">
			    <form action="save_fault_found_association.action" method="POST" id="failureTreeForm">
			    	<s:hidden name="failureContext" value="failureCause" />
			       <div style="width:100%;background:#F3FBFE;border:1px solid #EFEBF7;padding:10px 0px 10px 5px;margin:5px;">
			        <span><s:text name="label.manageFailureStructure.productModelItemgroup" />:</span>
			        <span>
			            <select dojoType="twms.widget.Select" name="itemGroupId" id="itemGroupId" >
			                <s:iterator value="itemGroups">
			                    <option value="<s:property value="id"/>"
			                       <s:if test="id == itemGroupId">selected</s:if>><s:property value="itemGroupDescription"/></option>
			                </s:iterator>
			            </select>
			        </span>
			        <s:hidden id="updatedTree" name="updatedTree"/>
			        <span><button type="submit" id="saveButton" class="buttonGeneric"><s:text name="button.common.save"/></button></span>
			    </div>
			    </form>
			</div>
			<div dojoType="dijit.layout.ContentPane" layoutAlign="client">
				<center>
                    <div class="startupLid" id="treeLoadingLid">
                        <div class="startupLidIndication">
                            <div class="startupLidMessage"><s:text name="label.common.startUpMessage" /> </div>
                        </div>
                    </div>
                    
                    <u:treeTable id="failureTypeEditortable" loadOn="/failureTree/load" serializeOn="/failureTree/serialize" returnBy="/failureTree/return"
	          nodeAgent="twms.failureTree.NodeAgent" headTemplateVar="headTemplate" cssClass="grid borderForTable"
	          headCssClass="treeTableHead" rootRowClass="twms.failureTree.RootRow" onValidationErrors="/failureTree/invalid" />
				</center>
				<script type="text/javascript">
			    dojo.addOnLoad(function() {
			        FAILURE_TREE_VARS.treeDataManager = new twms.failureTree.TreeDataManager("/failureTree/load");
			    });
				</script>
				<u:jsVar varName="headTemplate">
			    <tr class="dummyRow">
			        <th class="asmAdditionColumn dummyColumn"></th>
			        <th class="asmLabelColumn dummyColumn"></th>
			        <th class="spAdditionColumn dummyColumn"></th>
			    </tr>
			    <tr>
			        <th class="dottedRightBorder">
			        	<s:if test = "%{isCreateModifyAllowed()}">
				            <u:treeAddButton rowType="FailureType" identifierCssClass="addNewFailureTypeButton">
				                <img src="image/addRow_new.gif" title="<s:text name='message.manageFailureAssoc.addFaultFound' />" class="addAsmChild"/>
				            </u:treeAddButton>
			            </s:if>
			        </th>
			        <th class="dottedLeftBorder">
			
			        </th>
			        <th>
			        </th>
			    </tr>
				</u:jsVar>
			</div>
		</div>
		<u:jsVar varName="failureTypeRowTemplate">
	    <tr>
        <td class="dottedRightBorder">
        	<s:if test = "%{isCreateModifyAllowed()}">
	            <u:treeAddButton rowType="FailureCause" identifierCssClass="addNewFailureCauseButton">
	                <img src="image/addRow_new.gif" title="<s:text name='message.manageFailureAssoc.addCausedBy'/>" class="addAsmChild"/>
	            </u:treeAddButton>
            </s:if>
        </td>
        <td class="dottedLeftBorder hilightable" style="readOnly">
            <span class="unfoldButton"><img src="image/icon_expand.gif" title="<s:text name='label.manageFailureAssoc.unfold'/>"/></span>
            <span class="foldButton"><img src="image/icon_collapse.gif" title="<s:text name='label.manageFailureAssoc.fold'/> "/></span>
            <span class="dummyFoldButton"><img src="image/icon_expand_dummy.gif"/></span>
            <span class="failureTypeHolder">${label} </span>
        </td>
        <td class="dropRowColumn">
        	<s:if test = "%{isCreateModifyAllowed()}">
	            <u:treeDropButton identifierCssClass="dropRowButton">
	                <img src="image/remove.gif" class="dropRow" title="<s:text name='label.manageFailureAssoc.dropNode' /> "/>
	            </u:treeDropButton>
            </s:if>
        </td>
	    </tr>
		</u:jsVar>
		<u:jsVar varName="failureCauseRowTemplate">
    	<tr>
       	<td class="dottedRightBorder">
        </td>
        <td class="dottedLeftBorder hilightable indentable">
            <span class="failureCauseHolder" >${label}</span>
        </td>
        <td class="dropRowColumn">
        	<s:if test = "%{isCreateModifyAllowed()}">
	            <u:treeDropButton identifierCssClass="dropRowButton">
	                <img src="image/remove.gif" class="dropRow" title="<s:text name='label.manageFailureAssoc.dropNode'/>"/>
	            </u:treeDropButton>
            </s:if>
        </td>
	    </tr>
		</u:jsVar>
		<u:jsVar varName="errorMessageMarkup">
	    <li class="errorMessage">${message}</li>
		</u:jsVar>
		<u:jsVar varName="errorMessageWrapperMarkup">
	    <span>
	        <ol class="errorToolTip">
	            ${messages}
	        </ol>
	    </span>
		</u:jsVar>
        <div style="display:none">
        <div dojoType="twms.widget.Dialog" id="createDialog" bgColor="#FFF"
             title="<s:text name="label.manageFailureAssoc.createDefinition" />">
		    <div dojoType="dijit.layout.LayoutContainer">
		        <div dojoType="dijit.layout.ContentPane" layoutAlign="client" class="dialogContentPane">
		            <table align="center">
                        <tbody>
                            <tr>
                                <td><s:text name="label.common.label"/> : </td>
                                <td><input type="text" id="label"/><input type="hidden" id="depth"/></td>
                            </tr>
                            <tr>
                                <td><s:text name="label.common.description"/> : </td>
                                <td><input type="text" id="descr"/></td>
                            </tr>
                            <tr>
                                <td colspan="2" align="center">
                                    <button id="createDefinition" class="buttonGeneric">
                                        <s:text name="button.common.create"/>
                                    </button>
                                    <button id="cancelDefinitionCreation" class="buttonGeneric">
                                        <s:text name="button.common.cancel"/>
                                    </button>
                                </td>
                            </tr>
                    </tbody>
                </table>
		    </div>
		</div>
        <script type="text/javascript">
	    dojo.addOnLoad(function() {
        FAILURE_TREE_VARS.definitionCreator = new twms.failureTree.DefinitionCreator({
            dialog : dijit.byId("createDialog"),
            labelInput : dojo.byId("label"),
            depthInput : dojo.byId("depth"),
            descInput : dojo.byId("descr"),
            createButton : dojo.byId("createDefinition"),
            cancelButton : dojo.byId("cancelDefinitionCreation")
        });
	    });
		</script>
        <div style="display:none">
            <div dojoType="twms.widget.Dialog" id="saveErrorDialog" bgColor="#FFF">
            <div dojoType="dijit.layout.LayoutContainer" style="height:90px;width:350px;">
            <div dojoType="dijit.layout.ContentPane" layoutAlign="client" class="dialogContentPane" style="height:150px;width:150px;">
                <table align="center">
                    <tbody>
                        <tr>
                            <td><s:text name="error.manageFailureAssoc.validationErrors" /></td>
                        </tr>
                        <tr>
                            <td align="center">
                                <button id="dismissErrorDialog" class="buttonGeneric">
                                    <s:text name="button.common.dismiss"/>
                                </button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            </div>
            </div>
        </div>
        <script type="text/javascript" src="scripts/failureTypeTreeEditor.js"></script>
<authz:ifPermitted resource="warrantyAdminFaultFound/CausedByAssociationReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('failureTreeForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('failureTreeForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
    </u:body>
</html>