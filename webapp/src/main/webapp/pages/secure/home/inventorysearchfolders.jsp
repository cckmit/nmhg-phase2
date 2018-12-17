<%@page contentType="text/html"%>

<%@page pageEncoding="UTF-8"%>

<%@taglib prefix="s" uri="/struts-tags"%>

<%@taglib prefix="t" uri="twms"%><%@taglib prefix="u" uri="/ui-ext"%>

<%@taglib prefix="authz" uri="authz"%>

<%
	response.setHeader("Pragma", "no-cache");

	response.addHeader("Cache-Control", "must-revalidate");

	response.addHeader("Cache-Control", "no-cache");

	response.addHeader("Cache-Control", "no-store");

	response.setDateHeader("Expires", 0);
%>



<u:jsVar ajaxMode="true">
	<ol>
	<authz:ifPermitted resource="inventoryDefineSearchQuery">
		<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
			id="CreateInventorySearch" tagType="li" cssClass="inventory_folder folder"
			tabLabel="%{getText('accordion_jsp.accordionPane.myClaims.defineSearch.inventory')}"
			url="new_search_expression.action?context=InventorySearches" catagory="inventory" helpCategory="Inventory/Search_Inventory.htm">
			<span style="color:blue">
			<s:text name="accordion_jsp.accordionPane.myClaims.defineSearch" />
			</span>
		</u:openTab>
	</authz:ifPermitted>
	<authz:ifPermitted resource="inventoryPredefinedStockSearch">
		<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
			id="PreDefinedStockInventorySearch" tagType="li" cssClass="inventory_folder folder"
			tabLabel="%{getText('label.common.preDefinedSearchStock')}"
			url="preDefined_search_inventory.action?context=InventorySearches&refreshPage=true" catagory="inventory" helpCategory="Inventory/Search_Inventory.htm">
			<span style="color:blue">
				<s:text name="label.common.preDefinedSearchStock" />
			</span>	
		</u:openTab>
	</authz:ifPermitted>
	<authz:ifPermitted resource="inventoryPredefinedRetailSearch">
		<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
			id="PreDefinedRetailInventorySearch" tagType="li" cssClass="inventory_folder folder"
			tabLabel="%{getText('label.common.preDefinedSearchRetail')}"
			url="preDefined_search_inventory_retail.action?context=InventorySearches&refreshPage=true" catagory="inventory" helpCategory="Inventory/Search_Inventory.htm">
			<span style="color:blue">
				<s:text name="label.common.preDefinedSearchRetail" />
			</span>	
		</u:openTab>
	</authz:ifPermitted>
	
	<authz:ifPermitted resource="inventoryDefineSearchQuery">	
		<s:iterator value="savedQueriesForInventory" status="savedQueriesForInventoryIter">
			<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				id="manage_inventory_business_search_subs[%{#savedQueriesForInventoryIter.index}]"
				tagType="li" cssClass="inventory_folder folder"
				tabLabel="%{getDomainPredicate().getName()}"
				url="dynamicInventorySearchResult.action?domainPredicateId=%{getDomainPredicate().getId()}&savedQueryId=%{getId()}"
				catagory="inventory" helpCategory="Inventory/Search_Inventory.htm">
				<s:text name="%{getDomainPredicate().getName()}" />
			</u:openTab>
		</s:iterator>
	</authz:ifPermitted>	
	
	<authz:ifPermitted resource="inventoryPredefinedStockSearch">		
		<s:iterator value="preDefinedInventoryStockSavedQueries" status="savedQueriesForInventoryIter">
			<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				id="manage_preDefined_stock_inventory_business_search_subs[%{#savedQueriesForInventoryIter.index}]"
				tagType="li" cssClass="inventory_folder folder"
				tabLabel="%{getSearchQueryName()}"
				url="preDefined_search_expression_inventory.action?
					queryId=%{preDefinedInventoryStockSavedQueries[#savedQueriesForInventoryIter.index].id}&
					savedQueryName=%{preDefinedInventoryStockSavedQueries[#savedQueriesForInventoryIter.index].searchQueryName}&
					notATemporaryQuery=true"
				catagory="inventory" helpCategory="Inventory/Search_Inventory.htm">
				<s:text name="%{getSearchQueryName()}" />
			</u:openTab>
		</s:iterator>
	</authz:ifPermitted>
		
	<authz:ifPermitted resource="inventoryPredefinedRetailSearch">
		<s:iterator value="preDefinedInventoryRetailSavedQueries" status="savedQueriesForInventoryIter">
			<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				id="manage_preDefined_retail_inventory_business_search_subs[%{#savedQueriesForInventoryIter.index}]"
				tagType="li" cssClass="inventory_folder folder"
				tabLabel="%{getSearchQueryName()}"
				url="preDefined_search_expression_inventory.action?
					queryId=%{preDefinedInventoryRetailSavedQueries[#savedQueriesForInventoryIter.index].id}&
					savedQueryName=%{preDefinedInventoryRetailSavedQueries[#savedQueriesForInventoryIter.index].searchQueryName}&
					notATemporaryQuery=true"
				catagory="inventory" helpCategory="Inventory/Search_Inventory.htm">
				<s:text name="%{getSearchQueryName()}" />
			</u:openTab>
		</s:iterator>
	</authz:ifPermitted>
	</ol>
	
		
</u:jsVar>
