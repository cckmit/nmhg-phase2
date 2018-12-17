<#if (!parameters.hidden && !parameters.cssColumn)>
<th id="${parameters.summaryTableId}_summaryTable_header_${parameters.id}"
    class="tableHeaderCell tableCell colapsableColumn <#if parameters.disableSorting>unsortableColumn</#if>" <#rt/>
field="${parameters.id}">
 <table><#--FIXME: is there a better way to put two floating elements togather and keep them from wrapping -->
  <tbody>
   <tr class="tableHeadColumnRow">
    <td>
     <div id="${parameters.summaryTableId}_summaryTable_sortImageBox_${parameters.id}" class="imageBox"></div>
    </td>
    <td class="colapsableColumn">
     <div class="columnTitle">
      <#if (parameters.dataType == "CheckBox")>
     <input type='checkbox' id='${parameters.summaryTableId}_summaryTable_header_masterCheckBox' class='tableHeadColumnRow colapsableColumn columnTitle inputCheckbox' onclick=masterCheckBoxChanged(this)>
     <#else>
      ${parameters.label}
      </#if>
     </div>
    </td>
   </tr>
  </tbody>
 </table>
</th>
</#if>