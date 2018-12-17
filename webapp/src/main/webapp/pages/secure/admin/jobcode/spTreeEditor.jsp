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
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="authz" uri="authz" %>

<html>
<head>
  <title>EditableTree</title>
  <u:stylePicker fileName="base.css"/>
  <u:stylePicker fileName="layout.css" common="true"/>
  <u:stylePicker fileName="yui/reset.css" common="true"/>
  <s:head theme="twms"/>

  <jsp:include page="/i18N_javascript_vars.jsp"/>
  <script type="text/javascript">
      dojo.require("twms.widget.Select");
      dojo.require("dijit.layout.LayoutContainer");
      dojo.require("dijit.layout.ContentPane");
      dojo.require("twms.widget.Dialog");

      var isReadOnly = null;
       
      dojo.addOnLoad(function() {

    	  isReadOnly = false;
    	  <s:if test = "%{!isCreateModifyFaultCodeAllowed()}">
			  isReadOnly = true;
    	  </s:if>
          var treeLoadingLid = dojo.byId("treeLoadingLid");

          //spTree var binding
          SP_TREE_VARS.errorParams = {
              messageWrapper : errorMessageWrapperMarkup,
              message : errorMessageMarkup
          };
          SP_TREE_VARS.treadData = <s:if test="treadData != null"><s:property value="treadData" escape="false"/></s:if><s:else>[]</s:else>;

          //binding the comboxbox stuff... to reload the tree... when the value is changed
          var itemGrpIdBox = dijit.byId("itemGroupId");
		    var loadTree = function (itemGrpId) {
              itemGrpIdBox.setDisabled(true);
        	  dojo.byId('saveButton').disabled = true;
              dojo.html.show(treeLoadingLid);
              twms.ajax.fireJsonRequest("fetchFailureStructure.action", {itemGroupId : itemGrpId}, function(data) {
                  dojo.byId("asmTreeAddButton").style.display='';
                  publishEvent("/spTree/load", data);
                  dojo.html.hide(treeLoadingLid);
              });
          }

           // To load empty tree when itemGroup Id id null
           if(itemGrpIdBox)  {
            publishEvent("/spTree/load", []);
            dojo.html.hide(treeLoadingLid);
            dojo.byId("asmTreeAddButton").style.display='none';
           }
          dojo.connect(itemGrpIdBox, "onChange", function(itemGrpId) {
              loadTree(itemGrpId);
          });

          //on click of save button... firing the event to make the tree serialize.
          dojo.connect(dojo.byId("saveButton"), "onclick", function(event) {
        	  treeLoadingLid.style.opacity = "0.7";
              dojo.html.show(treeLoadingLid);
              dojo.stopEvent(event);
              publishEvent("/spTree/serialize");
             
          });

          //on return event getting published... submit the form
          dojo.subscribe("/spTree/return", null, function(tree) {
              dojo.byId("updatedTree").value = tree;
              var valueof = dijit.byId("newItemGroupId");
   		   	  dojo.byId("copyToItemGroupId").value = valueof;
              dojo.byId("spTreeForm").submit();
          });

          dojo.subscribe("/spTree/rendered", null, function() {
              itemGrpIdBox.setDisabled(false)
              dojo.byId('saveButton').disabled = false;
          });

          //in case... there is a validation error.... show error dialog... insteed of submiting.
          var errorDialog = dijit.byId("saveErrorDialog");
          dojo.subscribe("/spTree/invalid", null, function() {
              errorDialog.show();
              dojo.html.hide(treeLoadingLid);
          });
          //on dismiss button click... hide error dialog.
          dojo.connect(dojo.byId("dismissErrorDialog"), "onclick", function() {
              errorDialog.hide();
          });
           
          var __saveBtn = dojo.byId("saveButton");
          __saveBtn.disabled=true;
          
	         
      });
     
  </script>
  <jsp:include page="/pages/secure/common/resourceManager.jsp"/>
  <u:stylePicker fileName="spTreeEditor.css"/>
  <u:stylePicker fileName="common.css"/>
  <u:stylePicker fileName="base.css"/>
  <style>
  .grid{
  width:99%;
  }
  .grid tr td{
  color:#545454;
  font-family:Arial, Helvetica, sans-serif;
  font-size:9pt;
  }
  h4 {
      margin-top: 0px;
  }
  </style>
</head>
<u:body>
	<center>
	    <div class="startupLid" id="treeLoadingLid">
	        <div class="startupLidIndication">
	            <div class="startupLidMessage">Please Wait...</div>
	        </div>
	    </div>
    </center>
<u:actionResults wipeMessages="true" wipeOutTime="2000"/>
<div style="height:10%;">
    <form  action="saveFailureStructure.action" method="POST" id="spTreeForm" enctype="multipart/form-data">
       <div style="width:100%;background:#F3FBFE;border:1px solid #EFEBF7;padding:10px 0px 10px 5px;margin:5px;">
	   <div style="float:left;margin-right:20px;">
        <span class="labelStyle"><s:text name="label.manageFailureStructure.productModelItemgroup" />:</span>
        <span>
           <sd:autocompleter id='itemGroupId' name ='itemGroupId' key='%{id}' keyName='itemGroupId' href='list_products_models.action' showDownArrow='false' value='%{itemGroupDescription}' loadMinimumCount="0" />

        </span>
		</div>
		<div style="float:left;padding-top:18px;"><span class="labelStyle"><s:text name="label.copy.to" /></span></div>
		<div style="float:left;margin:0 20px;">
        <span class="labelStyle"><s:text name="label.manageFailureStructure.productModelItemgroup" />:</span>
        <span>
           <sd:autocompleter id='newItemGroupId' name ='newItemGroupId' key='%{id}' keyName='newItemGroupId' href='list_products_models.action' showDownArrow='false'  loadMinimumCount="0" />

        </span>
        </div>
        <s:hidden id="updatedTree" name="updatedTree"/>
		<s:hidden id="copyToItemGroupId" name="copyToItemGroupId"/>
		<div style="margin:5px 0;padding-middle:10px">
		 <button type="button" id="saveButton" class="buttonGeneric" ><s:text name="button.common.save"/></button>
		 </div>
    </div>
        
    </form>
</div>
<div  style="overflow:auto;height:87%" id = "asmTreeAddButton">
<center>
<u:treeTable id="serviceProcedureEditor" loadOn="/spTree/load" serializeOn="/spTree/serialize" returnBy="/spTree/return"
          nodeAgent="twms.spTree.NodeAgent" headTemplateVar="headTemplate" cssClass="grid borderForTable"
          headCssClass="colHeader" rootRowClass="twms.spTree.RootRow" onValidationErrors="/spTree/invalid"
          onTreeRendered="/spTree/rendered" />
</center>
<script type="text/javascript">
    dojo.addOnLoad(function() {
        SP_TREE_VARS.treeDataManager = new twms.spTree.TreeDataManager("/spTree/load");
    });
</script>
<u:jsVar varName="headTemplate">
    <tr class="dummyRow">
        <th class="asmAdditionColumn dummyColumn"></th>
        <th class="asmLabelColumn dummyColumn"></th>
        <th class="spAdditionColumn dummyColumn"></th>
        <th class="spLabelColumn dummyColumn"<u:isIE> style="width : 100px !important;"</u:isIE>></th>
        <th class="treadBucketColumn dummyColumn"<u:isIE> style="width : 100px !important;"</u:isIE>></th>
        <th class="lastUpdateColumn dummyColumn"<u:isIE> style="width : 60px !important;"</u:isIE>></th>
        <th class="dropRowColumn dummyRow"></th>
    </tr>
    <tr>
        <th rowspan="2" class="dottedRightBorder" ><!-- ASM -->

        <s:if test="%{isCreateModifyFaultCodeAllowed()}">
	            <u:treeAddButton rowType="Assembly" identifierCssClass="addNewAsmRowButton">
	                <img src="image/addRow_new.gif" title="<s:text name="message.failureStructureTree.addAsmChild"/>" class="addAsmChild"/>
	            </u:treeAddButton>
            </s:if>
        </th>
        <th rowspan="2" class="dottedLeftBorder"><!-- ASM -->

        </th>
        <th colspan="2" rowspan="2"><!-- SP -->
            <center><s:text name="label.manageFailureStructure.job" /></center>
        </th>
        <th colspan="2"><!--TREAD-->
            <center><s:text name="label.common.tread" /></center>
        </th>
        <th rowspan="2">
        </th>
    </tr>
    <tr>
        <th><!--TREAD-->
            <s:text name="label.manageFailureStructure.treadBucket"/>
        </th>
        <th>
            <s:text name="label.manageFailureStructure.lastUpdated" />
        </th>
    </tr>
</u:jsVar>
</div>

<u:jsVar varName="asmRowTemplate">
    <tr>
        <td class="dottedRightBorder" id="treeAddButton">
        	<s:if test="%{isCreateModifyFaultCodeAllowed()}">
	            <u:treeAddButton rowType="Assembly" identifierCssClass="addNewAsmRowButton">
	                <img src="image/addRow_new.gif" title="<s:text name="message.manageFailureStructure.addAsmChild"/>" class="addAsmChild"/>
	            </u:treeAddButton>
            </s:if>
        </td>
        <td class="dottedLeftBorder hilightable indentable">
            <span class="unfoldButton"><img src="image/icon_expand.gif" title="<s:text name="label.manageFailureStructure.unfold"/>"/></span>
            <span class="foldButton"><img src="image/icon_collapse.gif" title="<s:text name="label.manageFailureStructure.fold"/>"/></span>
            <span class="dummyFoldButton"><img src="image/icon_expand_dummy.gif"/></span>
            <span class="asmDefHolder">${label}</span>
        </td>
        <td class="dottedRightBorder">
            <u:treeAddButton rowType="ActionNode" identifierCssClass="addNewSpRowButton">
                <img src="image/addRow_new.gif" class="addSpChild"
                     title="<s:text name="message.manageFailureStructure.addSpChild"/>"/>
            </u:treeAddButton>
        </td>
        <td class="dottedLeftBorder"></td>
        <td class="hilightable"><span class="treadBucketHolder">${treadBucket}</span></td>
        <td class="hilightable"><span class="lastUpdateHolder">${lastUpdated}</span></td>
        <td class="dropRowColumn">
        	<s:if test="%{isCreateModifyFaultCodeAllowed()}">
	            <u:treeDropButton identifierCssClass="dropRowButton">
	                <img src="image/remove.gif" class="dropRow" title="<s:text name="label.manageFailureStructure.dropNode"/>"/>
	            </u:treeDropButton>
            </s:if>
        </td>
    </tr>
</u:jsVar>
<u:jsVar varName="spRowTemplate">
    <tr>
        <td class="dottedRightBorder"></td>
        <td class="dottedLeftBorder"></td>
        <td class="dottedRightBorder"></td>
        <td class="dottedLeftBorder hilightable">
            <table width="100%" class="spTable treeTableCommon">
                <tr>
                    <td colspan="2" class="hilightable"><span class="spDefHolder">${label}</span>&nbsp</td>
                </tr>
                <tr>
                    <td style="padding-left: 15px"><s:text name="label.manageFailureStructure.forCampaigns" /> : </td>
                    <td style="width: 50px;" class="hilightable" align="center">
                        <input type="checkbox" class="forCampaigns" title="<s:text name="label.manageFailureStructure.forCampaigns" />"/>
                    </td>
                </tr>
                <tr>
                    <td style="padding-left: 15px"><s:text name="label.manageFailureStructure.labourHours" /> : </td>
                    <td class="hilightable" align="right" style="padding-right: 5px">
                        <span class="labourHrsHolder">${suggestedLabourHours}</span>
                    </td>
                </tr>
            </table>
        </td>
        <td></td>
        <td></td>
        <td class="dropRowColumn">
            <u:treeDropButton identifierCssClass="dropRowButton">
                <img src="image/remove.gif" class="dropRow" title="<s:text name="label.manageFailureStructure.dropNode"/>"/>
            </u:treeDropButton>
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
<div dojoType="twms.widget.Dialog" id="createDialog" bgColor="#FFF">
    <div dojoType="dijit.layout.LayoutContainer" class="createDialog"   style="height:90px;width:350px;">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client"   style="height:150px;width:150px;">
            <div class="createFormDiv">
                <span><s:text name="label.manageFailureStructure.createDefinition" /></span>
            </div>
            <div class="createFormDiv">
                <span><s:text name="label.common.label"/> : </span>
                <span><input type="text" id="label"/></span>
                <input type="hidden" id="depth"/>
            </div>
            <div class="createFormDiv">
                <span><button id="createDefinition" class="buttonGeneric"><s:text name="button.common.create"/></button></span>
                <span><button id="cancelDefinitionCreation" class="buttonGeneric"><s:text name="button.common.cancel"/></button></span>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    dojo.addOnLoad(function() {
        SP_TREE_VARS.definitionCreator = new twms.spTree.DefinitionCreator({
              dialog : dijit.byId("createDialog"),
              labelInput : dojo.byId("label"),
              depthInput : dojo.byId("depth"),
              createButton : dojo.byId("createDefinition"),
              cancelButton : dojo.byId("cancelDefinitionCreation")
          });
    });
</script>
<div dojoType="twms.widget.Dialog" id="saveErrorDialog" bgColor="#FFF" >
    <div dojoType="dijit.layout.LayoutContainer" class="saveErrorDialog"  style="height:90px;width:350px;">
        <div dojoType="dijit.layout.ContentPane" layoutAlign="client" class="saveErrorDialogPane"  style="height:150px;width:150px;">
             <table align="center">
              <tbody>
                        <tr>
                <td> <s:text name="error.manageFailureStructure.validationErrors" /> 
            </div>
            </tr>
            <tr> 
             <td align="center">
               <button id="dismissErrorDialog" class="buttonGeneric"><s:text name="button.common.dismiss"/></button>
                </td>
                        </tr>
                    </tbody> 
                     </table>
            </div> 
        </div>
    </div>
</div>
<script type="text/javascript" src="scripts/ServiceProcedureTreeEditor.js"></script>
<authz:ifPermitted resource="warrantyAdminSeriesFailureHierarchyReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	    	document.getElementById("saveButton").style.display="none";
	    });
	</script>
</authz:ifPermitted>
</u:body>
</html>
