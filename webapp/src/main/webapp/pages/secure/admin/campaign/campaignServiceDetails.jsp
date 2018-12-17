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

<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>
<script type="text/javascript" src="scripts/ServiceProcedureTree.js"></script>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="form.css"/>
<u:stylePicker fileName="claimForm.css"/>
<script type="text/javascript">
        dojo.require("twms.data.EmptyFileWriteStore");
        dojo.require("twms.widget.Tree");
        dojo.require("twms.widget.TreeModel");
</script>        

<style type="text/css">
    .servicePrecedureNode {
        font-family: Verdana, sans-serif, Arial, Helvetica;
        font-size: 7.5pt;
        color: #000;
        padding-left: 20px;
        background-repeat: no-repeat;
    }

    .newFileIcon {
        background: url(image/newFile.gif) no-repeat 2px 2px;
    }

    .directoryIcon {
        background-image: url( image/directory.gif );
    }
</style>
<script type="text/javascript">
    var targetCollectionName = "campaign.campaignServiceDetail.campaignLaborLimits";
    function hideShowSpecifyHours(hours, standards) {
        if (standards.checked == true) {
            hours.value = "";
            dojo.html.hide(hours);
        } else {
            dojo.html.show(hours);
        }
    }
</script>

<script type="text/javascript">
    dojo.addOnLoad(function() {
        var tree = <s:property value="jsonServiceProcedureTree" escape="false" />;
        var alreadySelectedNodes = <s:property value="selectedJobsJsonString" escape="false"/>;
		var firsttime=true;
        var spTreeWidget = null;
//        dojo.setSelectable(treeWidget.domNode, false);
//        var spTreeManager = new twms.campaignServiceDetail.MergedTreeManager(tree, treeWidget, treeWidget.store,"true", null);
        var spRenderer = new twms.campaignServiceDetail.MergedTreeRenderer(dojo.byId("serviceProcedureTBody"), spTreeWidget,
                alreadySelectedNodes, rowTemplateText.markup, rowTemplateText.script,
                targetCollectionName,null,true);

        if(alreadySelectedNodes){
            spRenderer.renderSelectedRows(alreadySelectedNodes);
        }
        var dialog = dijit.byId("treeBrowser");
        dojo.connect(dojo.byId("browseTreeButton"), "onclick", function() {
             if(firsttime)
                {
                    firsttime=false
                    var previouslySelectedNodes = new Array();
                    dojo.forEach(spRenderer.selectedRows, function(row){
                        if(row.nodeObject.listed)
                            previouslySelectedNodes.push(row.nodeObject); // this is done not show the removed row as selected on first load
                    });
                    renderTree(tree,previouslySelectedNodes);
                }
            dialog.show();
        });
        dojo.connect(dojo.byId("addServiceProcedures"),"onclick", function() {
            spRenderer.renderSelectedRows();
            dialog.hide();
        });
        bindRepeatTableKeyboardActions(dojo.byId("serviceProcedureRepeatTable"), dojo.byId("browseTreeButton"), "repeat_del");

    function renderTree(data, alreadySelectedNodes){
            var div = document.createElement("div")
            dojo.byId("serviceProcedureTreeParent").appendChild(div);
            var spStoreId = new twms.data.EmptyFileWriteStore({});
            spStoreId.data = data;
            var spModel = new twms.widget.TreeModel({store:spStoreId,treeType:'JOBCODE',isAsync:false});
            spTreeWidget = new twms.widget.Tree(
                        {
                            id : "serviceProcedureTreeWidget",
                            labelAttr : "name",
                            model : spModel,
                            multipleSelectionAllowed: true,
                            label : "Service Procedures Codes",
                            defaultSelectedNodes:alreadySelectedNodes,
                            persist: false
                        }, div);
            if(spRenderer){ // set the treewidget to the old instance of 
                spRenderer.treeWidget = spTreeWidget;
            }
            spRenderer = new twms.campaignServiceDetail.MergedTreeRenderer(dojo.byId("serviceProcedureTBody"), spTreeWidget,
                alreadySelectedNodes, rowTemplateText.markup, rowTemplateText.script,
                targetCollectionName,null,true);
        }

    });

</script>
<div class="admin_section_div">
    <div class="admin_section_heading"><s:text name="label.campaign.serviceProcedure"/></div>

    <table class="repeat" width="96%" id="serviceProcedureRepeatTable">

        <thead>
            <tr class="admin_table_header">
                <th width="23%" class="admin_table_header"><s:text name="label.campaign.jobCode"/></th>
                <th width="23%" class="admin_table_header"><s:text name="label.campaign.jobCodeDescription"/></th> 
                <th width="23%" class="admin_table_header"><s:text name="label.campaign.laborStandards"/></th>
                <th width="23%" class="admin_table_header"><s:text name="label.campaign.specifiedLaborHours"/></th>
                <th width="12%" class="admin_table_header" align="left">
                    <div class="repeat_add" id="browseTreeButton"></div>
                </th>
            </tr>
        </thead>
        <tbody id="serviceProcedureTBody" class="admin_data_table"></tbody>
    </table>
    <%--
    NODE: In the template we normally shouldn't use the jsp tags, because the syntax %{someValue} tries to resolve the value
    against the OGNL stack... and not finding anything... it replaces it with empty string. So using normal HTML tags here.
    --%> <u:jsVar varName="rowTemplateText">
    <tr index="${index}">
        <td><input type="hidden"
                   name="campaign.campaignServiceDetail.campaignLaborLimits[${index}]"
                   value="${wrapperId}"/> <input type="hidden"
                                                 name="campaign.campaignServiceDetail.campaignLaborLimits[${index}].serviceProcedureDefinition"
                                                 value="${id}"/> <span title="${fullNameTooltip}">${completeCode}</span>
        </td>
        <script type="text/javascript">
            dojo.addOnLoad(function() {
                var actualField = dojo.byId("use_suggested_hrs_field_${index}");
                var checkBox = dojo.byId("standards[${index}]");
                dojo.connect(checkBox, "onclick", function(event) {
                    actualField.value = event.target.checked;
                });
                checkBox.checked =${useSuggestedHours};
                actualField.value = ${useSuggestedHours};
            });
        </script>
         <td>    		
            <span title="${fullNameTooltip}">${jobCodeDescription}</span>            
								
    	</td> 
        <td align="center"><input type="checkbox" id="standards[${index}]"></td>
        <td align="center"><input type="text" size="5" id="hours[${index}]" value="${specifiedLaborHours}"
                                  name="campaign.campaignServiceDetail.campaignLaborLimits[${index}].specifiedLaborHours"/>
            <input type="hidden" name="campaign.campaignServiceDetail.campaignLaborLimits[${index}].laborStandardsUsed"
                   id="use_suggested_hrs_field_${index}"/>
        </td>
        <td>
            <div class="repeat_del"></div>
        </td>
    </tr>

    <script type="text/javascript">
        dojo.addOnLoad(function() {
            var hours = dojo.byId('hours[${index}]');
            var standards = dojo.byId('standards[${index}]');
            if (standards.checked == true) {
                dojo.html.hide(hours);
            } else {
                dojo.html.show(hours);
            }

            dojo.connect(standards, "onclick", function() {
                hideShowSpecifyHours(hours, standards);
            });

        });
    </script>

</u:jsVar>

</div>

    <div dojoType="twms.widget.Dialog" id="treeBrowser"
    title="<s:text name="title.newClaim.selectServiceProcedure"/>"     
     style="width:95%;height:95%;">
            <div dojoType="dijit.layout.ContentPane" layoutAlign="client" style="font-size: 9pt;height:408px;">
                <div id="serviceProcedureTreeParent">
                </div>
            </div>

            <div dojoType="dijit.layout.ContentPane" layoutALign="bottom" style="border-top:1px solid #EFEBF7">
                <button id="addServiceProcedures"><s:text name="label.campaign.addServiceProcedure"/></button>
            </div>

    </div>
