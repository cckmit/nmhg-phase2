<%@ page contentType="text/html" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<%@ taglib prefix="authz" uri="authz" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>::
<s:text name="title.common.warranty" />
::</title>
<s:head theme="twms"/>
<u:stylePicker fileName="adminPayment.css"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="form.css"/>
</head>
<style>
.grid1 {
font-family:Arial, Helvetica, sans-serif;
font-size:9pt;
width:100%;
padding-top:10px;
overflow:auto;
}

</style>
<u:body >
<u:actionResults/>
<s:form action="save_part_class_and_job_codes_association" id="baseForm">
<s:hidden name="id" />
<div class="spacer10"></div>
<div style="margin:5px; width:98.5%"> <b>
  <s:text  name="label.mantainPartAndJob.faultLocation"  />
  :</b>
  <s:text  name="%{faultCodeDefinition.code}" />
  <div class="spacer6"></div>
  <u:repeatTable id="part_number_table" cssClass="grid1 borderForTable" cellspacing="0" cellpadding="0">
    <tr>
      <div  class="section_header">
        <s:text name="title.mantainPartAndJob.parts"/>
      </div>
    <thead>
      <tr class="row_head">
        <th width="10%"> <s:text name="label.mantainPartAndJob.PartNumber"/>
        </th>
        <th width="40%"> <s:text name="label.mantainPartAndJob.Description"/>
        </th>
        <th width="5%"><div  align="center">
            <u:repeatAdd id="outside_parts_adder">
              <div class="repeat_add"> </div>
            </u:repeatAdd>
          </div></th>
      </tr>
    </thead>
    <u:repeatTemplate id="outside_parts_replaced_body"
        value="codeDefinitionItem.partClasses">
      <tr index="#index">
        <td width="10%"><sd:autocompleter id='partOffPartNumber_#index' cssStyle='width:75px' href='list_part_return_ItemsStartingWith.action' name='codeDefinitionItem.partClasses[#index].item' loadMinimumCount='3' loadOnTextChange='true' showDownArrow='false' indicator='indicator' />
          <script type="text/javascript">
							        dojo.addOnLoad(function() {
							        	  dojo.byId("partOffPartNumber_#index").value = "<s:property value="codeDefinitionItem.partClasses[#index].item.alternateNumber"/>";
							        	  dojo.byId("partOff_description_#index").value = "<s:property value="codeDefinitionItem.partClasses[#index].item.description"/>";
							        	  dojo.connect(dijit.byId("partOffPartNumber_#index"),"onChange",function() {
							        		  var number = dojo.byId("partOffPartNumber_"+#index).value;
									        	 
							        });
							        	  						    
							        
	   									 	        
       						 </script>
          <img style="display: none;" id="indicator" class="indicator"
                             src="image/indicator.gif" alt="Loading..." /> </td>
        <td align="center"><span id="partOff_description_#index" >&nbsp; </span>
          
        </td>
        <td width="5%" align="center"><u:repeatDelete id="outside_parts_deleter_#index" >
            <div class="repeat_del"> </div>
          </u:repeatDelete>
        </td>
      </tr>
    </u:repeatTemplate>
  </u:repeatTable>
  <div class="spacer10"></div>
  <div class="spacer10"></div>
  <u:repeatTable id="part_class_table" cssClass="grid1 borderForTable"  cellspacing="0" cellpadding="0" >
    <div  class="section_header">
      <s:text name="title.mantainPartAndJob.partClasses"/>
    </div>
    <thead>
      <tr class="row_head">
        <th width="10%"> <s:text name="label.mantainPartAndJob.partClasses"/>
        </th>
        <th width="40%"> <s:text name="label.mantainPartAndJob.Description"/>
        </th>
        <th width="5%"><div align="center">
            <u:repeatAdd id="outside_partClasses_adder">
              <div class="repeat_add"> </div>
            </u:repeatAdd>
          </div></th>
      </tr>
    </thead>
    <u:repeatTemplate id="outside_partClasses_replaced_body"
        value="codeDefinitionItemGroup.partClasses">
      <tr index="#index">
        <td width="10%"><sd:autocompleter id='partOffPartClass_#index' cssStyle='width:150px' href='list_part_class_with_label_id.action?selectedBusinessUnit=%{faultCodeDefinition.businessUnitInfo.name}' name='codeDefinitionItemGroup.partClasses[#index].itemGroup' loadMinimumCount='3' loadOnTextChange='true' showDownArrow='false' indicator='indicator' />
          <script type="text/javascript">
							        dojo.addOnLoad(function() {
								      
							        	  dojo.byId("partOffPartClass_#index").value = "<s:property value="codeDefinitionItemGroup.partClasses[#index].itemGroup.name"/>";
							        	  dojo.byId("partClassOff_description_"+#index).value = "<s:property value="codeDefinitionItem.partClasses[#index].itemGroup.description"/>";
							        	  dojo.connect(dijit.byId("partOffPartClass_"+#index),"onChange",function() {
							        		  dojo.byId("partClassOff_description_"+#index).innerHTML =   
								                   dojo.byId("partOffPartClass_"+#index).value;			    
							        	  			   		           });
							        });
							        	  						    
							         
							       
						    
	   									 	        
       						 </script>
          <img style="display: none;" id="indicator" class="indicator"
                             src="image/indicator.gif" alt="Loading..." /> </td>
        <td><span id="partClassOff_description_#index" > </span>
          
        </td>
        <td width="5%" align="center"><u:repeatDelete id="outside_partClasses_deleter_#index" >
            <div class="repeat_del"> </div>
          </u:repeatDelete>
        </td>
      </tr>
    </u:repeatTemplate>
  </u:repeatTable>
  <div align="center" style="margin-top: 20px;"  >
    <s:submit cssClass="button" align="center" value="%{getText('button.label.save')}"> </s:submit>
    <button class="buttonGeneric" id="btnCancelProductAdd" 
                           onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));">
    <s:text name="button.common.cancel" />
    </button>
  </div>
</div>
</s:form>
<authz:ifPermitted resource="warrantyAdminListFaultLocationsReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseForm')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseForm'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</u:body>
</html>
