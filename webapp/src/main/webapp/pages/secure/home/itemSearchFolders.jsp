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
<authz:ifPermitted resource="itemsDefinedSearchQuery">
	<ol>
		<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
			id="CreateItemSearch" tagType="li" cssClass="items_folder folder"
			tabLabel="%{getText('accordion_jsp.accordionPane.myClaims.defineSearch.item')}"
			url="new_search_expression.action?context=ItemSearches" catagory="item" helpCategory="Items/Search_Items.htm">
			<span style="color:blue">
			<s:text name="accordion_jsp.accordionPane.myClaims.defineSearch" />
			</span>
		</u:openTab>
	</ol>
	

	<ol>
		<s:iterator value="savedQueriesForItem" status="savedQueriesForItemIter">
			<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				id="manage_item_business_search_subs[%{#savedQueriesForItemIter.index}]"
				tagType="li" cssClass="item_folder folder"
				tabLabel="%{getDomainPredicate().getName()}"
				url="dynamicItemSearchResult.action?domainPredicateId=%{getDomainPredicate().getId()}&savedQueryId=%{getId()}"
				catagory="item" helpCategory="Items/Search_Items.htm">
				<s:text name="%{getDomainPredicate().getName()}" />
			</u:openTab>
		</s:iterator>
	</ol>
</authz:ifPermitted>
</u:jsVar>
