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
	<authz:ifPermitted resource="claimsDefineSearchQuery">
		<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
			id="CreateClaimSearch" tagType="li" cssClass="claims_folder folder"
			tabLabel="%{getText('accordion_jsp.accordionPane.myClaims.defineSearch.claim')}"
			url="new_search_expression.action?context=ClaimSearches&folderName=Search" catagory="myClaims" helpCategory="Claims/Search_Claims.htm">
			<span style="color:blue">
			   <s:text name="accordion_jsp.accordionPane.myClaims.defineSearch" />
			</span>
		</u:openTab>
	</authz:ifPermitted>
	
	<authz:ifPermitted resource="claimsPredefinedSearch">	
		<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
			id="QuickClaimSearch" tagType="li" cssClass="claims_folder folder"
			tabLabel="%{getText('label.common.preDefinedSearch')}"
			url="showPreDefinedClaimsSearch.action?context=ClaimSearches&folderName=Search" catagory="myClaims" helpCategory="Claims/Search_Claims.htm">
			<span style="color:blue">
				<s:text name="label.common.preDefinedSearch" />
			</span>	
		</u:openTab>
	</authz:ifPermitted>
	
	</ol>
	
	<ol>
	<authz:ifPermitted resource="claimsDefineSearchQuery">
		<s:iterator value="savedQueries" status="savedQueriesIter">
			<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				id="manage_business_search_subs_[%{#savedQueriesIter.index}]"
				tagType="li" cssClass="claims_folder folder"
				tabLabel="%{getDomainPredicate().getName()}"
				url="dynamicClaimSearchResult.action?
					context=ClaimSearches&
					folderName=Search&
					domainPredicateId=%{getDomainPredicate().getId()}&savedQueryId=%{getId()}"
				catagory="myClaims" helpCategory="Claims/Search_Claims.htm">
				<s:text name="%{getDomainPredicate().getName()}" />
			</u:openTab>
		</s:iterator>
	</authz:ifPermitted>
		
	<authz:ifPermitted resource="claimsPredefinedSearch">
		<s:iterator value="preDefinedClaimSavedQueries" status="savedQueriesIter">
			<u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
				id="manage_preDefined_business_search_subs_[%{#savedQueriesIter.index}]"
				tagType="li" cssClass="claims_folder folder"
				tabLabel="%{getSearchQueryName()}"
				url="showPreDefinedClaimSearchResults.action?
					context=ClaimSearches&
					folderName=Search&
					queryId=%{preDefinedClaimSavedQueries[#savedQueriesIter.index].id}&
					savedQueryName=%{preDefinedClaimSavedQueries[#savedQueriesIter.index].searchQueryName}&notATemporaryQuery=true"
				catagory="myClaims" helpCategory="Claims/Search_Claims.htm">
				<s:text name="%{getSearchQueryName()}" />
			</u:openTab>
		</s:iterator>
	</authz:ifPermitted>
		
		

	</ol>

</u:jsVar>
