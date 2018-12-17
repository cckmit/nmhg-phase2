<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		<link href="<s:url includeParams='none' value='/stylesheet/master.css' encode='false'/>" rel="stylesheet" type="text/css">
		<link href="<s:url includeParams='none' value='/stylesheet/style.css' encode='false'/>" rel="stylesheet" type="text/css">
        <link rel="stylesheet" type="text/css" media="all" href="<s:url includeParams='none' value='/scripts/jqGrid/css/ui.jqgrid.css' encode='false'/>"/>
        <link rel="stylesheet" type="text/css" media="all"	href="<s:url includeParams='none' value='/scripts/jqGrid/css/cupertino/jquery-ui-1.7.2.custom.css' encode='false'/>"/>

        <script src="<s:url includeParams='none' value='/scripts/jqGrid/js/jquery-1.5.2.min.js' encode='false'/>" type="text/javascript"></script>
        <script src="<s:url includeParams='none' value='/scripts/jqGrid/js/i18n/grid.locale-en.js' encode='false'/>" type="text/javascript"></script>
        <script src="<s:url includeParams='none' value='/scripts/jqGrid/js/jquery.jqGrid.min.js' encode='false'/>" type="text/javascript"></script>
		<title>Integration Server :: Listing Sync Trackers</title>
		<s:head calendarcss="calendar-blue"/>
		<SCRIPT language="JavaScript">
			function showDetails(methodName, trackerId) {
				window.open('<%request.getContextPath();%>'+"SyncTracker!"+methodName+".action?trackerId="+trackerId,'Payload','width=650,height=350,left=150,top=150,scrollbars=yes,menubar=no,status=no,toolbar=no,resizable=yes');
			}
		</SCRIPT>
	<head>
	<body>
		<div align="center"><b>Integration Server</b> </div>
		<div align="left"><a href='<s:url action="Home"/>' style="color: navy;"> Home </a> &gt; Sync Tracker Page</div>
        <div align="right" style="padding-right: 25px"><a href='<s:url action="Home!logout"/>' style="color: navy;"> Logout </a></div>
        <hr>
		
		<s:form action="SyncTracker!search" method="post" id="searchForm">
			<div align="left">
				Enter search criteria
				<s:actionerror/>
                <s:textfield name="uniqueIdValue" label="Unique Id Value" id="uniqueIdValue"/>
				<s:select label="Sync Type"  name="syncType" headerKey="" 
                          headerValue="Select Sync Type" list='syncTypeOptions' listKey="type" listValue="type" id="syncType"/>
				<s:select label="Status"  name="status" headerKey="" 
					headerValue="Select Status"  list='statusOptions' id="status"/>
                <s:datetimepicker name="fromDate" label="Start Date" displayFormat="dd/MM/yyyy" id="fromDate"/>
				<s:datetimepicker name="toDate" label="End date" displayFormat="dd/MM/yyyy" id="toDate"/>
				<s:submit label="Search" align="left"/>
				
			</div>
		</s:form>
		<hr>
		
		
        <table id="list" style="width:90%;"></table>
        <div id="pager"></div>
		<s:include value="copyright_footer.jsp"></s:include>	
	</body>
    <script lang="javascript">
		$("#searchForm").submit(function(evt){
            jQuery("#list").clearGridData(true);
			var fromDate = $('[name="dojo.fromDate"]').val();
			var toDate = $('[name="dojo.toDate"]').val();
			jQuery("#list").jqGrid('setGridParam',{url:"SyncTracker!search.action?uniqueIdValue="+$("#uniqueIdValue").val()+
			"&syncType="+$("#syncType").val()+"&status="+$("#status").val()+"&fromDate="+format(fromDate)+"&toDate="+format(toDate)+
			"&dojo.fromDate="+fromDate+"&dojo.toDate="+toDate,page:1})
            .trigger("reloadGrid");
            evt.preventDefault();
			return false;
		});
		function format(date){ // date in yyyy-MM-dd format
			if(!date && date.length != 10)return"";
			arr = date.split("-");
			return arr[2]+"/"+arr[1]+"/"+arr[0]; //return date in dd/MM/yyyy format
		}
		jQuery(document).ready(function() {
			function openWindow(cellvalue, options, rowObject){
				if(options.colModel.name === 'payLoad')
					return '<div title="Show Payload" style="float:left;cursor:pointer;" onclick=showDetails("fetchPayload","' + rowObject.id + '")>Show Payload</div>';
				else if(options.colModel.name === 'showError')
					return '<div title="Show Error" style="float:left;cursor:pointer;" onclick=showDetails("fetchError","' + rowObject.id + '")>Show Error</div>';
				else if(options.colModel.name === 'reprocess'){
                    if(rowObject.syncType === 'CreditNotification' && rowObject.status === 'Failed')
                        return '<div title="Reprocess..?" style="float:left;cursor:pointer;" onclick=showDetails("reprocess","' + rowObject.id + '")>Reprocess</div>';
                    else
                        return '<div title="Reprocess..?">---</div>';
                }
					
            }
			jQuery("#list").jqGrid({
                    datatype: 'json',
                    url: "",
                    mtype: 'GET',
                    colNames:['Sync type','Unique Id Name', 'Unique Id Value', 'Status', 'Pay Load', 'Error message','Number of attempts', 'Create date', 'Update date',''],
                    colModel:[
                        {name:'syncType',label:'syncType', editable: false, sortable:true, search:false},
                        {name:'uniqueIdName',label:'uniqueIdName', editable: false, sortable:false, search:false},
						{name:'uniqueIdValue',label:'uniqueIdValue', editable: false, sortable:false, search:false},
                        {name:'status',label:'status', editable: false, sortable:false, search:false},
                        {name:'payLoad',label:'payLoad', editable: false, sortable:false, search:false,formatter:openWindow},
                        {name:'showError',label:'showError', editable: false, sortable:false, search:false,formatter:openWindow},
                        {name:'noOfAttempts',label:'noOfAttempts', editable: false, sortable:false, search:false},
                        {name:'createDate',label:'createDate', editable: false, sortable:false, search:false},
                        {name:'updateDate',label:'updateDate', editable: false, sortable:false, search:false},
                        {name:'reprocess',label:'reprocess', editable: true, sortable:false, search:false,formatter:openWindow}                

                    ],
                    pager: '#pager',
                    height:'250px',
                    imgpath: 'css/smoothness/images',
                    sortname: 'id',
                    sortorder: 'asc',
					hoverrows:false,
                    jsonReader: {
                        repeatitems : false,
                        cell:"",
                        id: "id"
                    },
                    viewrecords: true,
                    autowidth:true,
                    caption: 'Listing sync trackers',
                    rowNum: 10,
					beforeSelectRow: function(rowid, e) {
						return false;
					}
				});
                jQuery("#list").filterToolbar({useparammap:true,searchOnEnter:false});
            });
    </script>

</html>