<script type="text/javascript">  
	dojo.require("twms.widget.MultipleInventoryPicker");
</script>
<div dojoType="twms.widget.MultipleInventoryPicker" <#rt/>
  <#if parameters.searchHandlerUrl?if_exists != "">
    searchHandlerUrl="${parameters.searchHandlerUrl}" <#rt/>
  </#if>
  <#if parameters.searchHandlerParams?if_exists != "">
    searchHandlerParams="${parameters.searchHandlerParams}" <#rt/>
  </#if> 
  <#if parameters.cssClass?if_exists != "">
    cssClass="${parameters.cssClass}" <#rt/>
  </#if>
  <#if parameters.searchTitle?if_exists != "">
    searchTitle="${parameters.searchTitle?html}" <#rt/>
  </#if>
  <#if parameters.searchInputUrl?if_exists != "">    
    searchInputUrl="${parameters.searchInputUrl}" <#rt/>
  </#if>
  <#if parameters.searchLinkLabel?if_exists != "">
    searchLinkLabel="${parameters.searchLinkLabel?html}" <#rt/>
  </#if>
  <#if parameters.searchLinkClass?exists>
    searchLinkClass="${parameters.searchLinkClass}" <#rt/>
  </#if>
  <#if parameters.selectedItemsContentPane?if_exists != "">
    selectedItemsContentPane="${parameters.selectedItemsContentPane}" <#rt/>
  </#if>
  <#if parameters.useInnerHTML?if_exists != "">
    useInnerHTML="${parameters.useInnerHTML}" <#rt/>
  </#if>  
  <#if parameters.searchInvType?if_exists != "">
    searchInvType="${parameters.searchInvType}" <#rt/>
  </#if>
  <#if parameters.isMultiLineUser?if_exists != "">
    isMultiLineUser="${parameters.isMultiLineUser}" <#rt/>
  </#if>
  <#if parameters.searchActionType?if_exists != "">
    searchActionType="${parameters.searchActionType}" <#rt/>
  </#if>
  <#if parameters.selectionHandlerUrl?if_exists != "">
    selectionHandlerUrl="${parameters.selectionHandlerUrl}" <#rt/>
  </#if>
  <#if parameters.isRestrictedBuListDisplayed?if_exists != "">
    isRestrictedBuListDisplayed="${parameters.isRestrictedBuListDisplayed}" <#rt/>
  </#if>
></div>
