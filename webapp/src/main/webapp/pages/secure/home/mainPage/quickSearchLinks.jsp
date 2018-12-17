<%--
  Created by IntelliJ IDEA.
  User: pradyot.rout
  Date: Nov 20, 2008
  Time: 7:37:42 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>
  <!-- This section displays the quick search Links based on the roles-->
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr><td>&nbsp;</td></tr>
			<tr><td>&nbsp;</td></tr>
			<s:if test="buConfigAMER"> 
		        <authz:ifUserNotInRole roles="supplier">
            <tr>
                <td class="ItemsHdrQuickSearch"><s:text name="label.common.quickSearch"/></td> 
            </tr>
            </authz:ifUserNotInRole>
            </s:if>
             <s:else>
            	<td class="ItemsHdrQuickSearch"><s:text name="label.common.quickSearch"/></td>  
            </s:else> 
            <s:if test="isInternalUser()">  
                <tr>
	                <td class="ItemsLabels">
						<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
	                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                       id="quickInventorySearch" tagType="a" forceNewTab="true"
                                       tabLabel="%{getText('label.common.quickSearches')}" catagory="inventoryQuickSearch"
                                       url="displayQuickSearch.action?context=InventorySearches">
	                        <s:text name="accordion_jsp.accordionPane.inventory"/>
	                    </u:openTab>
	                </td>
	            </tr> 
	       </s:if>
	       <s:else>
	         <authz:ifUserNotInRole roles="supplier">
	            <tr>
	                <td class="ItemsLabels">
						<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
	                    <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                       id="quickInventorySearch" tagType="a" forceNewTab="true"
                                       tabLabel="%{getText('label.common.quickSearches')}" catagory="inventoryQuickSearch"
                                       url="displayQuickSearch.action?context=InventorySearches">
	                        <s:text name="accordion_jsp.accordionPane.inventory"/>
	                    </u:openTab>
	                </td>
	            </tr>
	          </authz:ifUserNotInRole>    
	        </s:else>  
	        <s:if test="buConfigAMER"> 
		        <authz:ifUserNotInRole roles="supplier">
		        	<tr>
	                    <td class="ItemsLabels">
						<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
	                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
	                                   id="quickClaimSearch" tagType="a" forceNewTab="true"
	                                   tabLabel="%{getText('label.common.quickSearches')}" catagory="claimQuickSearch"
	                                   url="displayQuickSearch.action?context=ClaimSearches">
	                            <s:text name="claim.prieview.ContentPane.claim"/>
	                        </u:openTab>
	                    </td>
	                </tr>
		        </authz:ifUserNotInRole>
	        </s:if>
	        <s:else>
                <tr>
                    <td class="ItemsLabels">
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                   id="quickClaimSearch" tagType="a" forceNewTab="true"
                                   tabLabel="%{getText('label.common.quickSearches')}" catagory="claimQuickSearch"
                                   url="displayQuickSearch.action?context=ClaimSearches">
                            <s:text name="claim.prieview.ContentPane.claim"/>
                        </u:openTab>
                    </td>
                </tr>
             </s:else>   
              <s:if test="isInternalUser()">   
                 <tr>
                    <td class="ItemsLabels">
						<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                   id="quickMajorComponentSearch" tagType="a" forceNewTab="true"
                                   tabLabel="%{getText('label.common.quickSearches')}" catagory="majorCompQuickSearch"
                                   url="displayQuickSearch.action?context=MajorComponentSearches">
                            <s:text name="label.majorComponent.majorComponent"/>
                        </u:openTab>
                    </td>
                </tr>
              </s:if>
              <s:else>
                 <authz:ifUserNotInRole roles="supplier">
                  <tr>
                    <td class="ItemsLabels">
						<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                   id="quickMajorComponentSearch" tagType="a" forceNewTab="true"
                                   tabLabel="%{getText('label.common.quickSearches')}" catagory="majorCompQuickSearch"
                                   url="displayQuickSearch.action?context=MajorComponentSearches">
                            <s:text name="label.majorComponent.majorComponent"/>
                        </u:openTab>
                    </td>
                   </tr>
                 </authz:ifUserNotInRole>
               </s:else>
                <%-- <authz:ifUserInRole roles="admin">
                <tr>
                    <td class="ItemsLabels">
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                   id="wpraSearch" tagType="a" forceNewTab="true"
                                   tabLabel="%{getText('label.common.quickSearches')}" catagory="wpraSearch"
                                   url="displayQuickSearch.action?context=WpraSearches">
                            <s:text name="columnTitle.common.wpra"/>
                        </u:openTab>
                    </td>
                </tr>	
                </authz:ifUserInRole>	 --%>	
                <s:if test="buConfigAMER"> 
		        <authz:ifUserNotInRole roles="supplier">
		        	<tr>
                    <td class="ItemsLabels">
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                   id="historicalClaimSearch" tagType="a" forceNewTab="true"
                                   tabLabel="%{getText('label.common.quickSearches')}" catagory="wpraSearch"
                                   url="displayQuickSearch.action?context=HistoricalClaimSearches">
                            <s:text name="claim.prieview.ContentPane.historicalClaim"/>
                        </u:openTab>
                    </td>
                </tr>	
		        </authz:ifUserNotInRole>
	        </s:if>
	         <s:else>
                <tr>
                    <td class="ItemsLabels">
					<img src="image/bullets_newDesign.gif" border="0" align="absmiddle"/>
                        <u:openTab decendentOf="%{getText('home_jsp.tabs.home')}"
                                   id="historicalClaimSearch" tagType="a" forceNewTab="true"
                                   tabLabel="%{getText('label.common.quickSearches')}" catagory="wpraSearch"
                                   url="displayQuickSearch.action?context=HistoricalClaimSearches">
                            <s:text name="claim.prieview.ContentPane.historicalClaim"/>
                        </u:openTab>
                    </td>
                </tr>	
             </s:else>     
                
        </table>