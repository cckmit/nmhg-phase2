<%-- 
    Document   : jobDetails
    Created on : 19 Dec, 2011, 6:38:42 PM
    Author     : prasad.r
--%>

<%@page contentType="text/html" pageEncoding="windows-1252"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=windows-1252">
        <title>Integration Server :: Quartz Scheduler</title>
		<link href="<s:url includeParams='none' value='/stylesheet/master.css' encode='false'/>" rel="stylesheet" type="text/css">
		<link href="<s:url includeParams='none' value='/stylesheet/style.css' encode='false'/>" rel="stylesheet" type="text/css">
    <link rel="stylesheet" type="text/css" media="all" href="<s:url includeParams='none' value='/scripts/jqGrid/css/ui.jqgrid.css' encode='false'/>"/>
    <link rel="stylesheet" type="text/css" media="all"	href="<s:url includeParams='none' value='/scripts/jqGrid/css/cupertino/jquery-ui-1.7.2.custom.css' encode='false'/>"/>

    <script src="<s:url includeParams='none' value='/scripts/jqGrid/js/jquery-1.5.2.min.js' encode='false'/>" type="text/javascript"></script>
    <script src="<s:url includeParams='none' value='/scripts/jqGrid/js/i18n/grid.locale-en.js' encode='false'/>" type="text/javascript"></script>
    <script src="<s:url includeParams='none' value='/scripts/jqGrid/js/jquery.jqGrid.min.js' encode='false'/>" type="text/javascript"></script>
    </head>
    <body>
		<div align="center"><b>Integration Server</b> </div>
		<div align="left"><a href='<s:url action="Home"/>' style="color: navy;"> Home </a> &gt; <a href='<s:url value="/quartzadmin/index.jsp"/>'>Scheduler List</a> &gt; Quartz Job Details</div>
		<div align="right" style="padding-right: 25px"><a href='<s:url action="Home!logout"/>' style="color: navy;"> Logout </a></div>
        <hr>
        <s:actionmessage/>
        <div>
            <a href="showCreateJob.action?id=<s:property value="id"/>">Create Job</a> | 
            <a href="schedulerDetail.action?id=<s:property value="id"/>">Refresh</a>
        </div>
        <table id="list" style="width:90%;"></table>
        <div id="pager"></div>
		<form name="actionForm" id="actionForm"/>
    </body>
    <script lang="javascript">
			function mouseOver(a){
				jQuery(a).addClass("ui-state-hover");
			}
			function mouseOut(a){
				jQuery(a).removeClass("ui-state-hover");
			}
			
			function replaceAll(source,stringToFind,stringToReplace){
			  var temp = source;
				var index = temp.indexOf(stringToFind);
					while(index != -1){
						temp = temp.replace(stringToFind,stringToReplace);
						index = temp.indexOf(stringToFind);
					}
					return temp;
			}

			function handleClick(action, param){
				param = replaceAll(param, '#SPACE#', ' ');
				var form = document.getElementById('actionForm');
				form.action = 'handleJobAction.action?action=' + action + param;
				form.method = 'POST';
				form.submit();
			}
		jQuery(document).ready(function() {
            function customFormatter(cellvalue, options, rowObject){
				cellvalue = replaceAll(cellvalue, ' ', '#SPACE#');
                var buttons = '<div style="margin-left:15px;">' + 
				'<div title="Edit Job" style="float:left;cursor:pointer;" class="ui-pg-div ui-corner-all" onclick=handleClick("EDIT","' + cellvalue + '") onmouseover="mouseOver(this)" onmouseout="mouseOut(this)"><span class="ui-icon ui-icon-pencil"></span></div>' +
				'<div title="Run Job Now" style="float:left;margin-left:15px;" class="ui-pg-div ui-corner-all" onclick=handleClick("RUN","' + cellvalue + '") onmouseover="mouseOver(this)" onmouseout="mouseOut(this)"><span class="ui-icon ui-icon-seek-next"></span></div>';
                if(rowObject.status != 'PAUSED'){
                    buttons +='<div title="Pause Job Now" style="float:left;margin-left:15px;" class="ui-pg-div ui-corner-all" onclick=handleClick("PAUSE","' + cellvalue + '") onmouseover="mouseOver(this)" onmouseout="mouseOut(this)"><span class="ui-icon ui-icon-pause"></span></div>';
                }else{
                    buttons +='<div title="Resume Job Now" style="float:left;margin-left:15px;" class="ui-pg-div ui-corner-all" onclick=handleClick("RESUME","' + cellvalue + '") onmouseover="mouseOver(this)" onmouseout="mouseOut(this)"><span class="ui-icon ui-icon-play"></span></div>';
                }                
                if(rowObject.groupName === 'Reporting Job'){
                    buttons += '<div title="Delete Job" style="float:left;margin-left:15px;" class="ui-pg-div ui-corner-all" onclick=handleClick("DELETE","' + cellvalue + '") onmouseover="mouseOver(this)" onmouseout="mouseOut(this)"><span class="ui-icon ui-icon-trash"></span></div>' +
                     		   '<div title="Un-Schedule Job" style="float:left;margin-left:15px;" class="ui-pg-div ui-corner-all" onclick=handleClick("UNSCHEDULE","' + cellvalue + '") onmouseover="mouseOver(this)" onmouseout="mouseOut(this)"><span class="ui-icon ui-icon-cancel"></span></div>';
                }
//                buttons += '<div title="Add Trigger" style="float:left;margin-left:15px;" class="ui-pg-div ui-corner-all" onclick=handleClick("ADDTRIGGER","' + cellvalue + '") onmouseover="mouseOver(this)" onmouseout="mouseOut(this)"><span class="ui-icon ui-icon-plusthick"></span></div>';
                buttons += "</div>";
                return buttons;
            }
			jQuery("#list").jqGrid({
                    datatype: 'json',
                    url:"listJobDetails.action?id=<s:property escape="true" value="id"/>",
                    mtype: 'GET',
                    colNames:['Job Name','Group Name', 'Status', 'Started on', 'Completed On', 'Time(s)','Next Fire Time', 'No. of Triggers', 'Actions'],
                    colModel:[
                        {name:'jobName',label:'jobName', editable: false, sortable:true, search:false, width:'195px'},
                        {name:'groupName',label:'groupName', editable: false, sortable:false, search:false, width:'195px'},
                        {name:'status',label:'status', editable: false, sortable:false, search:false, width:'115px'},
                        {name:'started.on',label:'started.on', editable: false, sortable:false, search:false, width:'215px'},
                        {name:'completed.on',label:'completed.on', editable: false, sortable:false, search:false, width:'215px'},
                        {name:'time.taken',label:'time.taken', editable: false, sortable:false, search:false, width:'75px'},
                        {name:'nextFireTime',label:'nextFireTime', editable: false, sortable:false, search:false, width:'205px'},
                        {name:'numTriggers',label:'numTriggers', editable: false, sortable:false, search:false, width:'140px'},
                        {name:'actions',label:'actions', editable: true, sortable:false, search:false, formatter:customFormatter, width:'225px'}                
                    ],
                    pager: '',
                    height:'250px',
                    imgpath: 'css/smoothness/images',
                    sortname: 'scheduler.name',
                    sortorder: 'asc',
					hoverrows:false,
                    jsonReader: {
                        repeatitems : false,
                        cell:"",
                        id: "0"
                    },
                    viewrecords: true,
                    autowidth:true,
                    caption: 'Jobs List',
                    rowNum: -1,
					beforeSelectRow: function(rowid, e) {
						return false;
					}
				});
                //jQuery("#list").filterToolbar({useparammap:true,searchOnEnter:false});
            });
    </script>
    
</html>
