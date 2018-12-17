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
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@ taglib prefix="authz" uri="authz" %>
<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1" />
<title>
<s:text name="title.common.warranty" />
</title>
<s:head theme="twms" />
<u:stylePicker fileName="adminPayment.css" />

<script type="text/javascript" src="scripts/RepeatTable.js"></script>
<script type="text/javascript" src="scripts/AdminToggle.js"></script>
<script type="text/javascript"
	src="scripts/adminAutocompleterValidation.js"></script>
</head>
<u:body>
  <s:form name="baseForm" id="baseFormId"  validate="true" method="post">
    <u:actionResults />
    <s:hidden name="id" />
    <s:hidden name="relateCampaign" value="%{relateCampaign.id}" />
    </td>
    
    <s:iterator value="relateCampaign.includedCampaigns" status="status">
      <s:hidden name="includedcampaignsBeforeModifications[%{#status.index}]" value="%{id}" />
    </s:iterator>
    <div style="background: #F3FBFE; border: 1px solid #EFEBF7; margin-left:6px; margin-top:6px">
      <div class="policy_section_heading" >
        <s:text
			name="title.relatedCampaign.add" />
      </div>
      <table border="0" cellspacing="2" cellpadding="2" class="grid">
        <tr>
          <td class="labelStyle" nowrap="nowrap" width="13%"><s:text
					name="columnTitle.common.FullCode" />
            :
          <td><s:textfield name="relateCampaign.code" cssStyle="width:400px"
					value="%{relateCampaign.code}" /></td>
        </tr>
        <tr>
          <td class="labelStyle" nowrap="nowrap" valign="top"><s:text
					name="columnTitle.common.description" />
            :</td>
          <td><t:textarea name="relateCampaign.description" cssClass="textarea"  cols="100"  maxLength="3999" 
					value="%{relateCampaign.description}" /></td>
        </tr>
      </table>
    </div>
    <div style="background: #F3FBFE; border: 1px solid #EFEBF7; margin-left:6px; margin-top:6px">
      <div class="policy_section_heading">
        <s:text
			name="label.campaign.addCampaign" />
      </div>
      <u:repeatTable id="myTable" cssClass="grid borderForTable"
			cellpadding="0" cellspacing="0" theme="simple"  width="98%" cssStyle="margin:5px;">
        <thead>
          <tr class="row_head">
            <th width="85%" class="colHeader"><s:text
						name="dropdown.common.campaigns" /></th>
            <th width="15%" class="colHeader"><u:repeatAdd id="related_campaign_adder"
						theme="simple">
                <div align="center"><img id="addCampaign" src="image/addRow_new.gif" border="0"
							style="cursor: pointer;"
							title="<s:text name="label.campaign.addCampaign" />" /></div>
              </u:repeatAdd></th>
          </tr>
        </thead>
        <u:repeatTemplate id="related_campaign_mybody" value="relateCampaign.includedCampaigns"
				index="myindex" theme="twms">
          <tr index="#myindex">
            <td><sd:autocompleter id='campaigns_#myindex_Id' showDownArrow='false' href='list_active_campaign_starts_with.action' cssStyle='width:400px' name='relateCampaign.includedCampaigns[#myindex]' value='%{relateCampaign.includedCampaigns[#myindex].code}' listenTopics='/relateCampaign/initial/%{#myindex}' /></td>
            <td><u:repeatDelete
						id="relate_campaign_deleter_#myindex" theme="simple">
                <div align="center"><img id="deletePrice" src="image/remove.gif" border="0"
							style="cursor: pointer;"
							title="<s:text name="label.campaign.deleteCampaign" />" /></div>
              </u:repeatDelete>
              <s:hidden name="relateCampaign.includedCampaigns[#myindex]"></s:hidden>
            </td>
          </tr>
          <script type="text/javascript">
		                    dojo.addOnLoad(function(){
		                        dojo.publish("/relateCampaign/initial/"+'<s:property value="%{#myindex}"/>' , [{
		                            addItem: {
		                                key: '<s:property value="%{relateCampaign.includedCampaigns[#myindex].id}"/>',
		                                label: '<s:property value="%{relateCampaign.includedCampaigns[#myindex].code}"/>'
		                            }
		                        }]);
		                    });
	          			  </script>
        </u:repeatTemplate>
      </u:repeatTable>
      <div class="spacer3"></div>
      <div align="center">
        <s:submit
			id="closeTab" value="Cancel" cssClass="buttonGeneric" action="" />
        <script
			type="text/javascript">
			    dojo.addOnLoad(function() {
			        dojo.connect(dojo.byId("closeTab"), "onclick", function() {
			            closeMyTab();
			        });
			    });
			</script>
        <s:if test="(relateCampaign.id > 0)">
          <s:submit id="modifyRelatedCampaigns" value="Modify" cssClass="buttonGeneric"
			action="modify_related_campaigns" />
          <s:submit id="deleteRelatedCampaigns" value="Delete" cssClass="buttonGeneric"
			action="delete_related_campaigns" />
        </s:if>
        <s:else>
          <s:submit value="Submit" cssClass="buttonGeneric"
			action="saveRelatedCampaign" />
        </s:else>
        <div class="spacer7"></div>
      </div>
    </div>
  </s:form>
</u:body>
<authz:ifPermitted resource="warrantyAdminRelatedFPIsManagementReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('baseFormId'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
</html>
