    </tr>
    <tr>
    <#list parameters.columns as col>
     <#if (!col.hidden && !col.cssColumn)>
       <th <#if col.disableFiltering>class="unfilterableColumn" ><#else> >
        <input type="text" id="${parameters.id}_summaryTable_filter_${col.id}" class="filteringInputs"
               field="${col.id}" />
        </#if>
       </th>
      </#if>
    </#list>
    </tr>
   </thead>
  </table>
 </div>
 <div dojoType="dijit.layout.ContentPane" layoutAlign="bottom" id="${parameters.id}_summaryTable_paginator" class="paginator">
  <#include "/${parameters.templateDir}/simple/summaryTablePaginator.ftl" />
 </div>
 <div dojoType="dijit.layout.ContentPane" layoutAlign="client" id="${parameters.id}_summaryTable_bodyPane" class="summaryTableBodyPane">
  <table id="${parameters.id}_summaryTable_tableContents" class="tables">
   <tbody>
    <tr id="${parameters.id}_summaryTable_dummyRow">
     <#list parameters.columns as col>
      <#if (!col.hidden && !col.cssColumn)>
       <td id="${parameters.id}_summaryTable_dummy_${col.id}" class="dummyRowTd"></td>
      </#if>
     </#list>
    </tr>
   </tbody>
  </table>
 </div>
</div>
<script type="text/javascript">
var masterCheckBoxId = '${parameters.id}_summaryTable_header_masterCheckBox';
 summaryTableVars.${parameters.id}.columnData = new Array();
 <#list parameters.columns as col>
  <#if (!col.cssColumn && !col.hidden)>
   summaryTableVars.${parameters.id}.columnData.push({ id:"${col.id}",
    width:${col.width}/100,
    title:"${col.label}",
    alignment:"<#if (col.dataType == "CheckBox")>center<#else>${col.alignment}</#if>",
	rendererClass:${col.rendererClass},
    disableSorting: <#if col.disableSorting>true<#else>false</#if>});
  </#if>
  <#if col.cssColumn>
   summaryTableVars.${parameters.id}.cssColumnId = "${col.id}";
  <#else><#--because the cssColumn can not be id or label column-->
   <#if col.idColumn>
    summaryTableVars.${parameters.id}.idColumn = {
     id:"${col.id}",
     width:${col.width}/100,
     title:"${col.label}",
     alignment:"<#if (col.dataType == "CheckBox")>center<#else>${col.alignment}</#if>",
	 rendererClass:${col.rendererClass},
     hidden:<#if col.hidden> true <#else> false </#if>
    };
   </#if>
   <#if col.labelColumn>
    summaryTableVars.${parameters.id}.labelColumn = {
     id:"${col.id}",
     width:${col.width}/100,
     title:"${col.label}",
     alignment:"<#if (col.dataType == "CheckBox")>center<#else>${col.alignment}</#if>",
	 rendererClass:${col.rendererClass},
     hidden:<#if col.hidden> true <#else> false </#if>
    };
   </#if>
  </#if>
 </#list>
 var valueReader = new tavant.twms.summaryTable.ValueReader("${parameters.id}");
 summaryTableVars.${parameters.id}.table = new tavant.twms.SummaryTable(valueReader, "${parameters.id}");
 function plugEventHandler() {
  summaryTableVars.${parameters.id}.eventHandler = new ${parameters.eventHandlerClass}(summaryTableVars.${parameters.id}.table, valueReader);
  <#if parameters.multiSelect>
   summaryTableVars.${parameters.id}.table.enableMultipleSelection();
   summaryTableVars.${parameters.id}.table.ctrlClickHandler = summaryTableVars.${parameters.id}.eventHandler.ctrlClickHandler;
  </#if>
 }  
 dojo.addOnLoad( function() {
  plugEventHandler();
  summaryTableVars.${parameters.id}.table.onLoad();
  summaryTableVars.${parameters.id}.table.onResize();
  if(dojo.isFF) {<#--HACK: In FF the top of the grabbers don't show up at the right place by default. So we force it.-->
   summaryTableVars.${parameters.id}.table.fixTopOfAllGrabbers();
  }
  dojo.subscribe("/refresh/inboxView#${parameters.id}/listing", summaryTableVars.${parameters.id}.table, "refreshTable");
  dojo.subscribe("/refresh/inboxView#${parameters.id}/full", summaryTableVars.${parameters.id}.table, "refreshTableAndPreview");
  dojo.subscribe("/refresh/inboxView#${parameters.id}/hideCompletedRow", summaryTableVars.${parameters.id}.table, "hideCompletedRow");
  dojo.subscribe("/refresh/inboxView#${parameters.id}/hideCompletedRows", summaryTableVars.${parameters.id}.table, "hideCompletedRows");
  dojo.subscribe("/listing/inboxView#${parameters.id}/minimize", summaryTableVars.${parameters.id}.table, "minimize");
  dojo.subscribe("/listing/inboxView#${parameters.id}/restore", summaryTableVars.${parameters.id}.table, "restore");
  dojo.subscribe("/tab/focused", summaryTableVars.${parameters.id}.table, "manageRenderingOnFocus");
  dojo.subscribe("/masterCheckBox/${parameters.id}/stateChanged",null,function(isChecked){
  	dojo.query('input[name='+childCheckBoxName+']').forEach(function(child){
  		if(!child.disabled)
  			child.checked = isChecked;
  	});
  });
  dojo.subscribe("/childCheckBox/${parameters.id}/stateChanged",null,function(isChecked){
  	var allChildsInSameState = true;
  	dojo.query('input[name='+childCheckBoxName+']').forEach(function(child){
  		if(!child.disabled && child.checked != isChecked){
  			allChildsInSameState = false;
  		}
  			
  	});
  	if(allChildsInSameState){
  		dojo.byId('${parameters.id}_summaryTable_header_masterCheckBox').checked = isChecked;
  	}else{
  		dojo.byId('${parameters.id}_summaryTable_header_masterCheckBox').checked = false;
  	}
  });
  <#if parameters.populateCriteriaDataOn?exists>
    <#--
        the listner needs a map... which has : {url : "a string", returnTo: javascriptFunction(will recive url as 1st arg)}
    -->
    dojo.subscribe("${parameters.populateCriteriaDataOn}", null, function(message) {
        message.returnTo(summaryTableVars.${parameters.id}.table.state.populateCriteriaOnUrl(message.url).toGet());
    });
  </#if>
 });
  function masterCheckBoxChanged(masterCheckBox){
 	dojo.publish("/masterCheckBox/${parameters.id}/stateChanged",[masterCheckBox.checked]);
 }
 
 function childCheckBoxChanged(childCheckBox){
 	dojo.publish("/childCheckBox/${parameters.id}/stateChanged",[childCheckBox.checked]);
 }
</script>