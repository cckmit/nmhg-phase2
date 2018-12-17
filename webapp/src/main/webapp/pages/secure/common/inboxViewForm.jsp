<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<script type="text/javascript">
        function changeInboxView(){       	        	
        	var tabName;
            var domainPredicateName = document.getElementById("domainPredicateName");
            if (domainPredicateName)
	        	tabName = domainPredicateName.value;
	        else
	        	tabName = document.getElementById("folderName").value;
        	var obj = document.getElementsByName("inboxViewId")[0];
	        var opts = obj.options;
	        var selectedValue;
            //FIXME: If any one finds break dojo equivaliant just change it to dojo.forEach
            for(var i=0;i<opts.length;i++)
	        {
	        	if(opts[i].selected)
	        	{
	        		selectedValue=opts[i].value;
	        		break;	
	        	}
	        }

        	var thisTabLabel = getMyTabLabel();
        	
        	var url = getPageUrl()+"&inboxViewId="+selectedValue;
		    parent.publishEvent("/tab/reload", {label: thisTabLabel,
												 url: url,decendentOf:thisTabLabel,
												tab: getTabHavingId(getTabDetailsForIframe().tabId)});
        }
        
        function addInboxViewOption(idParam,nameParam){
        	var obj=document.getElementsByName("inboxViewId")[0];
	        var opts=obj.options;
            dojo.forEach(opts,function(option){
                if(option.selected && option.value==idParam)
	        	{
	        			option.text = nameParam;
	        			changeInboxView();
	        			return;
	        	}
            });

		        var optn = document.createElement("OPTION");
			    optn.text = nameParam;
			    optn.value = idParam;
			    opts.add(optn);
			    optn.selected=true;		    
	    		changeInboxView();
		}
		
		function getInboxViewIdExtraParam() {
            dojo.forEach(document.getElementsByName("inboxViewId")[0].options,function(option){
                if(option.selected)
                    {

                        return {
                            "inboxViewId" : option.value
                        };

                    }
            });

		}
</script>
<s:hidden id="folderName" name="folderName" />
<s:hidden id="defaultHeaderSize" name = "defaultHeaderSize"/>
<s:hidden id="inboxViewSortField" name="inboxViewSortField"/>
<s:hidden id="inboxViewSortOrder" name="inboxViewSortOrder"/>		


            <script>
        function inboxViewAction(){
	        var opts=document.getElementsByName("inboxViewAction")[0].options;
	        var selectedValue;
	        for(var i=0;i<opts.length;i++)
	        {
	     
	        	if(opts[i].selected)
	        	{
	        		selectedValue=opts[i].value;
	        		break;
	        	}
	        }
	        if(selectedValue==1)
	        {
	   
                createView();
	        	return;
	        }
	        if(selectedValue==2)
	        {
	     
	        	editView();
	        }
        }
        function createView(){
        	var thisTabLabel = getMyTabLabel();
        	
        	var encodedSelfName=encodeURIComponent(self.name);
            var url = "inboxViewAction.action?context="+document.getElementById("context").value+"&parentFrameId="+encodedSelfName+"&folderName="+document.getElementById("folderName").value+"&defaultHeaderSize="+document.getElementById("defaultHeaderSize").value;
			parent.publishEvent("/tab/open", {
											label: "Create inbox View",
											url: url,
											decendentOf: thisTabLabel,
											forceNewTab: true
									 });
        }

        function editView(){
            var selectedInboxViewId
            if(document.editorForm)
        		selectedInboxViewId=document.editorForm.inboxViewId.value;
        	else
        	{
		        var opts=document.getElementsByName("inboxViewId")[0].options;
		        for(var i=0;i<opts.length;i++)
		        {
		        	if(opts[i].selected)
		        	{
		        		selectedInboxViewId=opts[i].value;
		        		break;
		        	}
	        	}
        	}
        	if ((selectedInboxViewId == "") || (selectedInboxViewId == -1))
        		return;
        	var thisTabLabel = getMyTabLabel();
        	var encodedSelfName=encodeURIComponent(self.name);
            var url = "inboxViewAction.action?context="+document.getElementById("context").value+"&parentFrameId="+encodedSelfName+"&id="+selectedInboxViewId+"&folderName="+document.getElementById("folderName").value+"&defaultHeaderSize="+document.getElementById("defaultHeaderSize").value;
					parent.publishEvent("/tab/open", {
											label: "Edit inbox View",
											url: url,
											decendentOf: thisTabLabel,
											forceNewTab: true
									 });
        }

        </script>
        <table border="0" cellspacing="0" cellpadding="0" width="15%" align="right">
        <tr>        
            <td align="right" style="padding-top:7px">
                <select name="inboxViewAction" id="inboxViewAction" onchange="inboxViewAction()" >
                    <options>
                        <option value="0"><s:text name="label.viewClaim.actions"/></option>
                        <option value="1"><s:text name="label.inboxView.createView"/></option>
                        <option value="2"><s:text name="label.inboxView.editView"/></option>
                    </options>
                </select>
            </td>
            <td></td>
    	    <td align="right" style="padding-top:7px"> 
                <s:select list="inboxViews" listKey="id" listValue="name" headerKey="-1" headerValue="Default View"
                           name="inboxViewId" id="inboxViewsList" onchange="javascript:changeInboxView()"/>
            </td>
            </tr>
            </table>
		