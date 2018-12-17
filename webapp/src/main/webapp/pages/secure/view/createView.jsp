<%@ page contentType="text/html" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%
    response.setHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "must-revalidate");
    response.addHeader("Cache-Control", "no-cache");
    response.addHeader("Cache-Control", "no-store");
    response.setDateHeader("Expires", 0);
%>


<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9" />
<s:head theme="twms" />
<title>Create Inbox View</title>

<script type="text/javascript">
	var numOfDefaultViewCols = '<s:property value="getDefaultHeaderSize()"/>'; 
</script>
<script type="text/javascript" src="scripts/manageLists.js"></script>
<script type="text/javascript">
    dojo.require("dijit.layout.LayoutContainer");
    dojo.require("dijit.layout.ContentPane");
    

    var sortableFields = [];

    <s:iterator value="fieldsAvailableForSort">
    	sortableFields.push('<s:property value="top"/>');
    </s:iterator>
var fieldsNotAvailableForSort="<s:property value='fieldsNotAvailableForSortString'/>"
function moveSelectedOptionsFromAvailable(avail, selected, sort, defaultSort){
	copySelectedOptions(avail, selected, false, true);
	copySelectedOptionsWithFilter(avail, selected,sort, sortableFields, defaultSort, true, true, false);
	removeSelectedOptions(avail);
}
function moveSelectedOptionsFromSelected(avail, selected, sort, defaultSort){	
	if(sort.selectedIndex >= 0)
		var selectedOption = sort.options[sort.selectedIndex];
	moveSelectedOptions(selected, avail, false);
    removeAllOptions(sort);
	copySelectedOptionsWithFilter(selected, selected,sort, sortableFields, defaultSort, true, true,true);
	for (var i=0; i<sort.options.length; i++) {
		if(fieldsNotAvailableForSort.indexOf(":"+sort.options[i].value+":")!=-1){
			sort.options[i] = null;
			i=i-1;
		}
	}	
	if (selectedOption)
	{
		for (var i=0; i<sort.options.length; i++) {
			if(selectedOption.value == sort.options[i].value){
				sort.options[i].selected = true;
			}
		}
	}
}
function closeTabAfterFinishing() {
        var tabDetails = getTabDetailsForIframe();
        var tab = getTabHavingId(tabDetails.tabId);
        parent.publishEvent("/tab/close", {tab:tab});
    }
</script>
<u:stylePicker fileName="form.css"/>
<u:stylePicker fileName="warrantyForm.css"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="base.css"/>
<%@ include file="/i18N_javascript_vars.jsp" %>
</head>
<style>
.inboxviewradiobtn{float:left;}
.inboxviewradiobtn input{float:left;vertical-align:middle;margin:0;padding:0;}
.inboxviewradiobtn label{display:block;overflow: visible;width:90px;margin-bottom:8px;}
 </style>
<u:body smudgeAlert="false">
    <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; background: white;overflow:auto">
	<div style="height:3px"></div>
    <div dojotype="dijit.layout.ContentPane" style="width: 100%; height: 100%; background: white;overflow:auto">
	<u:actionResults wipeMessages="false"/>
	<s:form action="inboxViewSubmitAction.action" method="post" theme="twms">
	<div id="inbox_view_details" style="border:1px solid #EFEBF7;background:#F3FBFE;margin:5px;width:100%">
	<div id="inbox_view_title" class="section_heading"><s:text name="label.inboxView.detail"/></div>
    <table width="100%" cellpadding="0" cellspacing="0" class="grid">
    <s:hidden name="id"/>
    <s:hidden name="context"/>
    <s:hidden name="folderName"/>
     <s:hidden name="defaultHeaderSize"/>
    <s:hidden name="parentFrameId"/>
   <tr><td style="padding:0;margin:0 ">&nbsp;</td></tr>
    <tr>
        <td class="non_editable labelStyle" width="22%"><s:text name="label.inboxView.viewName"/>:</td>
	    <td><s:textfield name="inboxView.name" id="viewName" cssStyle="width:70%;height:100%;" /></td>
    </tr>
    <tr>
      <td colspan="2" width="100%">
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="20%"><b><s:text name="label.inboxView.availableFields"/></b></td>
              <td rowspan="2" width="10%" valign="middle" align="center">
                  <input type="button" class="buttonGeneric" name="right" value="&gt;&gt;" onclick="moveSelectedOptionsFromAvailable(this.form['updownlist2'],this.form['list2'], this.form['sortByFieldsList'], this.form['defaultSorts'])"><br/>
                  <input type="button" class="buttonGeneric" name="left" value="&lt;&lt;" onclick="moveSelectedOptionsFromSelected(this.form['updownlist2'],this.form['list2'], this.form['sortByFieldsList'], this.form['defaultSorts'])">
              </td>
              <td ><b><s:text name="label.inboxView.selectedFields"/></b></td>
            </tr>
            <tr>
              <td class="non_editable" valign="top">
                  <s:updownselect cssStyle="width:300px;height:200px;" id="updownlist2" cssClass="admin_selections" list="availableFields"
                                        listKey="id" listValue="%{getText(displayName)}" size="4"
                                        allowMoveDown="false" allowMoveUp="false" allowSelectAll="false" theme="simple"/>
              </td>
              <td class="non_editable" valign="top">
                  <s:updownselect cssStyle="width:300px;height:200px;" id="list2" cssClass="admin_selections" list="selectedFieldsList"
                                        listKey="id" listValue="%{getText(displayName)}"
                                        name="selectedFields" size="4"
                                        allowMoveDown="false" allowMoveUp="false" allowSelectAll="false" theme="simple"/>
                  <s:select id="defaultSorts" list="defaultSortableFields"
                                        listKey="id" listValue="%{getText(displayName)}"
                                        name="dummy009" size="4"
                                        theme="simple" cssStyle="display:none;"/>

              </td>
            </tr>
                <tr>
                  <td colspan="2" height="6"></td>
                </tr>
                <tr>
                  <td class="non_editable"><b><s:text name="label.inboxView.defaultSorting"/></b></td>
                  <td class="non_editable" valign="top"><b><s:text name="label.inboxView.sortingOrder"/></b></td>
                </tr>
                <tr>
                  <td class="non_editable">
				  <s:select id="sortByFieldsList" cssClass="admin_selections" list="selectedFieldsListForSort"
                                            listKey="id" listValue="%{getText(displayName)}" name="inboxView.sortByField" theme="simple" cssStyle="width:150px"/></td>
                   <script type="text/javascript">
                      dojo.addOnLoad(function(){
                          copySelectedOptionsWithFilter(dojo.byId("list2"),dojo.byId("list2"), dojo.byId("sortByFieldsList"), sortableFields, dojo.byId("defaultSorts"), true, true);
                      });
                  </script>
                  <td  class="non_editable inboxviewradiobtn" valign="top"><s:radio id="sortOrder" 
				  name="inboxView.sortOrderAscending" list="sortOrderMap" theme="simple" cssStyle="border:none;vertical-align:middle;"/></td> 
                </tr>
        </table>
      </td>
      </tr>
	</table>
   </div>
   <div id="submit" align="center" style="margin-top:-7px;">
	   	<s:submit cssClass="buttonGeneric" value="%{getText('button.common.submit')}" />
	   <s:if test="inboxView.getId() != null">
	  	 <s:submit cssClass="buttonGeneric"
						value="%{getText('button.common.delete')}"
						action="delete_view">
					</s:submit>
	 </s:if>
	</div>
  </s:form>
    </div>
    </div>
</u:body>
</html>
