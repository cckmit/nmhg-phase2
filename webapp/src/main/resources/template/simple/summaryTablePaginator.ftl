<div class="paginatorInnerDiv">
 <div style="float:left;" class="showHidePreviewWrapper">
  <button id="${parameters.id}_summaryTable_showHidePreview" dojoType="dijit.form.Button">
    <div class="paginatorShowHideButtonDiv">
     <span class="labeledPaginatorButton"></span>
    </div>
   </button>
 </div>
 <!-- Do not remove this class! It is used by SummaryTableTag.js for dojo.query lookup. Also, dont move the display
 style to css class, since then dojo.html.hide wont work. -->
 <div class="paginationButtonsHolder" style="display:none">
     <div class="pagination_button pageSelector" >
      <select dojoType="twms.widget.Select" id="${parameters.id}_summaryTable_pageSelector" autoComplete="false" <#rt/>
        class="paginatorComboBox" store="__paginatorStoreFor${parameters.id}" searchAttr='label'>
      </select>
     </div>
     <div class="pagination_button" style="float:right;<#if parameters.isIE>margin-top : 2px;</#if>">
      <div id="${parameters.id}_summaryTable_lastPage" dojoType="dijit.form.Button" <#rt/>
      iconClass="paginateToLast" onclick="summaryTableVars.${parameters.id}.table.lastPage()"
      title="Go To Last Page" ></div>
     </div>
     <div class="pagination_button" style="float:right;<#if parameters.isIE>margin-top : 2px;</#if>">
      <div id="${parameters.id}_summaryTable_nextPage" dojoType="dijit.form.Button" <#rt/>
       iconClass="paginateToNext" onclick="summaryTableVars.${parameters.id}.table.nextPage()" title="Go To Next Page">
       </div>
     </div>
     <div class="quick_page_switch" style="float:right">
      <table cellpadding="4">
       <tbody id="${parameters.id}_summaryTable_quickPaginatorBody">
        <tr/>
       </tbody>
      </table>
     </div>
     <div class="pagination_button" style="float:right;<#if parameters.isIE>margin-top : 2px;</#if>">
      <div id="${parameters.id}_summaryTable_previousPage" dojoType="dijit.form.Button" <#rt/>
       iconClass="paginateToPrevious" onclick="summaryTableVars.${parameters.id}.table.previousPage()"
       title="Go To Previous Page"></div>
     </div>
     <div class="pagination_button" style="float:right;<#if parameters.isIE>margin-top : 2px;</#if>">
      <div id="${parameters.id}_summaryTable_firstPage" dojoType="dijit.form.Button" <#rt/>
       iconClass="paginateToFirst"onclick="summaryTableVars.${parameters.id}.table.firstPage()"
       title="Go To First Page"></div>
     </div>
     <div class="pagination_button_text" style="float:right;<#if parameters.isIE>margin-top : 2px;</#if>">
      <@s.text name="label.common.pageNumberLabel">
       <@s.param name="value" value="parameters.pageNoSpan"/><#--HACK: this markup comes frm tag's bean... struts-tags's param doesn't work with static markup as its value-->
       <@s.param name="value" value="parameters.totalPagesSpan"/>
      </@s.text>
     </div>
     <div class="pagination_button_text" style="float:right;<#if parameters.isIE>margin-top : 2px;</#if>">
      <@s.text name="label.common.totalRecords">
       <@s.param name="value" value="parameters.totalRecords"/>
      </@s.text>
     </div>
   </div>
</div>