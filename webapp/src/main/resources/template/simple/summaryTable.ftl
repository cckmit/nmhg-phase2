<script type="text/javascript">
    dojo.require("dijit.layout.ContentPane");
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.SplitContainer");
    dojo.require("twms.widget.Select");
</script>
<div dojoType="dijit.layout.LayoutContainer" id="${parameters.id}_tableLayoutContainer"
 <#if parameters.cssClass?exists> class="${parameters.cssClass}"</#if> layoutAlign="client">
 <div dojoType="dijit.layout.ContentPane" layoutAlign="top" id="${parameters.id}_summaryTable_headerPane" class="summaryTableHeaderPane <#rt/>
 <#if (parameters.isIE)>
 ie<#rt/>
 </#if>">
  <#if !parameters.tagWasUsedBefore>
   <script type="text/javascript" src="scripts/ui-ext/tst/SummaryTableTag.js"></script>
   <script type="text/javascript" src="scripts/ui-ext/tst/SummaryTableTagColumn.js"></script>
   <script type="text/javascript" src="scripts/ui-ext/tst/SummaryTableTagGrabber.js"></script>
   <script type="text/javascript" src="scripts/ui-ext/tst/SummaryTableTagEventHandlers.js"></script>
   <link rel="stylesheet" type="text/css" href="css/theme/official/ui-ext/tst/SummaryTableTag.css"/>
   <link rel="stylesheet" type="text/css" href="css/theme/official/ui-ext/tst/SummaryTableTagButtons.css"/>
   <#if parameters.injectThemeCss>
    <link rel="stylesheet" type="text/css" href="css/theme/official/ui-ext/tst/SummaryTableTheme.css"/>
   </#if>
  </#if>
  <script type="text/javascript">
   <#--the default namespace... that is used by all the variables related to summary table.-->
   if(!summaryTableVars) var summaryTableVars = {};
   summaryTableVars.${parameters.id} = {};
   summaryTableVars.${parameters.id}.prefixForTableHeadId = "${parameters.id}_summaryTable_header_";
   summaryTableVars.${parameters.id}.prefixForTableBodyId = "${parameters.id}_summaryTable_dummy_";
   summaryTableVars.${parameters.id}.prefixForTableFilterId = "${parameters.id}_summaryTable_filter_";
   summaryTableVars.${parameters.id}.prefixForSortImageDiv = "${parameters.id}_summaryTable_sortImageBox_";
   summaryTableVars.${parameters.id}.headTableId = "${parameters.id}_summaryTable_tableHeader";
   summaryTableVars.${parameters.id}.bodyTableId = "${parameters.id}_summaryTable_tableContents";
   summaryTableVars.${parameters.id}.bodyUrl = "${parameters.bodyUrl}"
   summaryTableVars.${parameters.id}.previewUrl = <#if parameters.previewUrl?exists> "${parameters.previewUrl}" <#else> "" </#if>;
   summaryTableVars.${parameters.id}.detailUrl = <#if parameters.detailUrl?exists> "${parameters.detailUrl}" <#else> "" </#if>;
   summaryTableVars.${parameters.id}.totalRecordSpanId = "${parameters.id}_totalRecords";
   summaryTableVars.${parameters.id}.pageNumberSpanId = "${parameters.id}_pageNumber";
   summaryTableVars.${parameters.id}.totalPagesSpanId = "${parameters.id}_totalPages";
   summaryTableVars.${parameters.id}.folder = "${parameters.folderName}";
   summaryTableVars.${parameters.id}.dummyRowId = "${parameters.id}_summaryTable_dummyRow";
   <#if parameters.previewPaneId?exists>
    summaryTableVars.${parameters.id}.previewPaneId = "${parameters.previewPaneId}";
   </#if>
   summaryTableVars.${parameters.id}.minColumnSize = "5";
   summaryTableVars.${parameters.id}.nextPageButtonId = "${parameters.id}_summaryTable_nextPage";
   summaryTableVars.${parameters.id}.previousPageButtonId = "${parameters.id}_summaryTable_previousPage";
   summaryTableVars.${parameters.id}.firstPageButtonId = "${parameters.id}_summaryTable_firstPage";
   summaryTableVars.${parameters.id}.lastPageButtonId = "${parameters.id}_summaryTable_lastPage";
   summaryTableVars.${parameters.id}.pageSelectorId = "${parameters.id}_summaryTable_pageSelector";
   summaryTableVars.${parameters.id}.showHidePreviewButtonId = "${parameters.id}_summaryTable_showHidePreview";
   summaryTableVars.${parameters.id}.quickPaginatorTBodyId = "${parameters.id}_summaryTable_quickPaginatorBody";
   summaryTableVars.${parameters.id}.layoutContainerId = "${parameters.id}_tableLayoutContainer";
   summaryTableVars.${parameters.id}.paginatorBarId = "${parameters.id}_summaryTable_paginator";
   summaryTableVars.${parameters.id}.parentSplitContainerId = "${parameters.parentSplitContainerId}";
   summaryTableVars.${parameters.id}.extraParamsVar = ${parameters.extraParamsVar};
   summaryTableVars.${parameters.id}.extraParamsFunctions = ${parameters.extraParamsFunctions};
   <#if parameters.enableTableMinimize>
    summaryTableVars.${parameters.id}.enableTableMinimize = true;
    summaryTableVars.${parameters.id}.rootLayoutContainerId = "${parameters.rootLayoutContainerId}";
    summaryTableVars.${parameters.id}.buttonContainerId = "${parameters.buttonContainerId}";
    <#else>
    summaryTableVars.${parameters.id}.enableTableMinimize = false;
   </#if>
   summaryTableVars.${parameters.id}.table = null;<#-- holds the SummaryTable object itself -->
   summaryTableVars.${parameters.id}.eventHandler = null;<#-- holds the reference to the event handler -->
  </script>
  <div dojoType="tavant.twms.summaryTable.PaginatorDataStore" data-dojo-id="__paginatorStoreFor${parameters.id}"></div>
  <script type="text/javascript">
      dojo.subscribe("/summaryTable/${parameters.id}/totalPages", function(message) {
          __paginatorStoreFor${parameters.id}.setTotalPages(message.totalPageCount);
      });
  </script>
  <table id="${parameters.id}_summaryTable_tableHeader" class="tables tableHeader" border="0">
   <thead>
    <tr>
