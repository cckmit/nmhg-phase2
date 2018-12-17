<%--

   Copyright (c)2006 Tavant Technologies
   All Rights Reserved.

   This software is furnished under a license and may be used and copied
   only  in  accordance  with  the  terms  of such  license and with the
   inclusion of the above copyright notice. This software or  any  other
   copies thereof may not be provided or otherwise made available to any
   other person. No title to and ownership of  the  software  is  hereby
   transferred.

   The information in this software is subject to change without  notice
   and  should  not be  construed as a commitment  by Tavant Technologies.

--%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="t" uri="twms" %>
<style>
.linkColor{
color:blue;
cursor:pointer;
text-decoration:underline;
}
td{
padding-bottom:10px;
}
.grid td{
background:none;
}
	.dijitTextBox{width:130px;}

</style>

<script type="text/JavaScript">
    dojo.require("dojox.layout.ContentPane");
    dojo.require("twms.widget.TitlePane");
    dojo.require("twms.widget.MultipleInventoryPicker");
    dojo.require("twms.widget.DateTextBox");
</script>

<script type="text/javascript">

	 function populateDescriptionForSeries() {     	 
   	  twms.ajax.fireHtmlRequest("list_series_description.action", {"description": dijit.byId("series").getValue()}, function(data) {  
   		var seriesDetails = eval(data);                                                                                    
   		document.getElementById('series_description').innerHTML = seriesDetails[0];
   		document.getElementById('series_brand').innerHTML = seriesDetails[1];
   		if(seriesDetails[2]!=null)
   			document.getElementById('sister_series').innerHTML = seriesDetails[2];	   		  	 
   	  });   
     }

</script>

<div dojoType="twms.widget.TitlePane" title="<s:property value="%{getText('title.technician.seriesRefCertification')}"/>"
	 labelNodeClass="section_header" open="true" id="series_ref_certification_details">
<table border="0" cellspacing="0" cellpadding="0">
  <tbody>
			<tr>
				<td nowrap="nowrap" class="labelStyle" width="20%">
				   <s:text name="label.technicianCertification.series"/> :
				</td>
				<td width="30%">
					<sd:autocompleter id="series" href='list_series.action'
						name='seriesAndCertifications.series'
						keyValue="%{seriesAndCertifications.series}"
						key="%{seriesAndCertifications.series.id}"
						loadOnTextChange='true' loadMinimumCount='1'
						value='%{seriesAndCertifications.series.groupCode}'
						showDownArrow='false' autoComplete='false'
						cssClass='admin_selections' /> 
						<script type="text/javascript">
					        dojo.addOnLoad(function() {						       
                                var seriesNumberSelect = dijit.byId("series");
                                if(seriesNumberSelect){
                                seriesNumberSelect.sendDisplayedValueOnChange = false;
                                dojo.connect(seriesNumberSelect, "onChange", function(value){					            	
                                	populateDescriptionForSeries();		
					            });
                                }
                                <s:if test="seriesAndCertifications.getSeries().getGroupCode()!=null">
                                dijit.byId("series").setDisabled(true);
                                </s:if>
							});
    					</script> 
    					<script type="text/javascript">
                                dojo.addOnLoad(function() {                                	 
                                    dojo.connect(dijit.byId("series"), "onChange", function(newValue) {
                                        dojo.publish("/series/modified", [{
                                            params: {
                                                "series" : newValue
                                            }
                                        }]);
                                    });
                                });
                         </script>
				</td>
				<td nowrap="nowrap" class="labelStyle" width="20%">
				   <s:text name="label.technicianCertification.sisterSeries"/> :
				</td>
				<td width="30%">
					<s:label id="sister_series"
                                          value="%{seriesAndCertifications.series.oppositeSeries.groupCode}" />
				</td>				
			</tr>
			<tr>
			<td class="labelStyle" nowrap="nowrap" width="15%" style="width: 210px;"><s:text
					name="label.common.seriesDescription" />:</td>
				<td>
					<s:label id="series_description"
                                          value="%{seriesAndCertifications.series.name}" />
				</td>
			</tr>
			<tr>
				<td nowrap="nowrap" class="labelStyle" width="20%">
				   <s:text name="label.common.startDate"/> :
				</td>
				<td width="30%">
					<sd:datetimepicker cssStyle='width:145px;' name='seriesAndCertifications.startDate' id='startDate' />
				</td>
			</tr>
			<tr>
				<td nowrap="nowrap" class="labelStyle" width="20%">
				   <s:text name="label.common.endDate"/> :
				</td>
				<td width="30%">
					<sd:datetimepicker cssStyle='width:145px;' name='seriesAndCertifications.endDate' id='endDate' />
				</td>
			</tr>
			<tr>
				<td nowrap="nowrap" class="labelStyle" width="20%">
				   <s:text name="label.technician.company"/> :
				</td>
				<td width="30%">
					<s:label id="series_brand"
                                          value="%{seriesAndCertifications.series.brand}" />
				</td>
			</tr>
          <s:hidden name="seriesAndCertifications"/>  

  </tbody>
</table>

<div id="technician_company_details" class="mainTitle" style="margin:10px 0px 0px 0px;">

	<u:repeatTable id="technician_certification_table" theme="twms" cellspacing="4" cellpadding="0" cssStyle="margin:5px;" width="99%">
		<thead>
			<tr class="admin_table_header">
				<th class="colHeader" width="40%"><s:text name="label.technicianCertification.certificationName" /></th>
				<th class="colHeader"  width="10%"><s:text name="label.technicianCertification.categoryLevel" /></th>
				<th class="colHeader"  width="40%"><s:text name="label.technicianCertification.categoryName" /></th>
				<th class="colHeader" width="9%"><u:repeatAdd id="certificationAdder" theme="twms">
					<img id="addCertificationIcon" src="image/addRow_new.gif" border="0"
						style="cursor: pointer;"
						title="<s:text name="label.technician.addCertification" />" />
				</u:repeatAdd></th>
			</tr>
		</thead>
		<u:repeatTemplate id="certicationBody" value="seriesCertification" index="index" theme="twms">
			 <tr index="#index">
			 <s:hidden name="seriesCertification[#index]"/>
				<td valign="top"><s:textfield name="seriesCertification[#index].certificateName" value='%{certificateName}' id='brand_#index' theme="twms" size="100"/></td>
				<td valign="top"><s:select name="seriesCertification[#index].categoryLevel" list="{'CO','PR'}" value='%{categoryLevel}' id='certificationName_#index' theme="twms" headerKey="-1" headerValue="--Select--"/></td>
				<td valign="top"><s:textfield name="seriesCertification[#index].categoryName"  value='%{categoryName}' id='series_#index' theme="twms"/></td>
				<s:hidden name="seriesCertification[#index].seriesRefCertfication" value="%{seriesAndCertifications.id}"/>
				<s:hidden name="seriesCertification[#index].brand" value="%{seriesAndCertifications.series.brandType}"/>
				<td valign="top"><u:repeatDelete id="deleter_#index"
					theme="twms">
					<img id="deleteCertification" src="image/remove.gif" border="0"
						style="cursor: pointer;"
						title="<s:text name="label.technician.deleteCertification" />" />
				</u:repeatDelete></td>
			 </tr>
			 	</u:repeatTemplate>
	</u:repeatTable> 
</div>
</div>
