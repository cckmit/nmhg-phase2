<%--
  Created by IntelliJ IDEA.
  User: kaustubhshobhan.b
  Date: 15 Feb, 2010
  Time: 10:53:36 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html"%>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib prefix="t" uri="twms"%>
<%@ taglib prefix="u" uri="/ui-ext"%>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <s:head theme="twms" />
    <title><s:text name="Failure Report"/></title>
    <u:stylePicker fileName="common.css"/>
    <u:stylePicker fileName="form.css"/>
    <u:stylePicker fileName="claimForm.css"/>
    <u:stylePicker fileName="multiCar.css"/>
    <u:stylePicker fileName="base.css" />
    <script type="text/javascript" src="scripts/adminAutocompleterValidation.js"></script>
        <script type="text/javascript">
            dojo.require("dijit.form.Button");
            dojo.require("dijit.layout.ContentPane");
            dojo.require("dijit.layout.LayoutContainer");
            dojo.require("twms.widget.TitlePane");
            dojo.require("twms.widget.NumberTextBox");
            function submit(){
                var failureForm = window.document.forms['failureRept'];
                failureForm.submit();
            }

            function submitCreateSectionForm() {
            	dojo.byId("task_Name").value='Save';
                var form = dojo.byId("failureRept");
                form.action="create_section.action";
                form.submit();
            }
           
            function submitCreateSectionFormAndPublish() {
                    dojo.byId("task_Name").value='Publish';
            	    dojo.byId("customReportPublish").value = true;
            	    var form = dojo.byId("failureRept");
                    form.action="publish_report.action";
                    form.submit();
            }

            function viewAllQuestions(){
                var customReportName=dojo.byId("customReportName").value;
            	var reportId = '<s:property value="customReport.id"/>';
    			var actionURL = "view_all_questions.action?customReport="+reportId;
    			var thisTabLabel = getMyTabLabel();
            	parent.publishEvent("/tab/open", {label: customReportName, url: actionURL, decendentOf : thisTabLabel});
            }

            
         
        </script>
        <script type="text/javascript" src="scripts/AdminToggle.js"></script>
</head>
<u:body >
  <div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%; overflow-y: auto;">
     <div dojoType="dijit.layout.ContentPane" layoutAlign="client">
     <s:if
		test="%{internationalizeButtontoBeDisplayed()}">
		<table>
			<tr>
				<td width="20%">
				<div class="inboxLikeButtonWrapper" style="float: left">
				<button dojoType="dijit.form.Button" id="i18nButton">
				<div>
				<div class="inboxLikeButtonWithoutPadding"><span
					class="inboxLikeButtonText"> <s:text
					name="label.report.internationalizeReport" /> </span></div>
				</div>
				</button>
				<script type="text/javascript">
        dojo.addOnLoad(function() {
            dijit.byId("i18nButton").onClick = function() {
                var thisTabLabel = "InternationalizeReport";
                var url = "internationalize_report.action?customReport=<s:property value='customReport.id'/>" ;
                parent.publishEvent("/tab/open", {label:thisTabLabel,
                    url: url,
                    decendentOf: thisTabLabel,
                    forceNewTab: true });
            }
        });
    </script></div>
				</td>
			</tr>
		</table>
	</s:if>
        <u:actionResults wipeMessages="true"/>
        <s:form id="failureRept" theme="twms">
        	<div dojoType="twms.widget.TitlePane" title="<s:text name="label.customReport.ReportDetail"/>"  id="name_productType"
                     labelNodeClass="section_header" open="true">
             
             <div style="width:100%;"><jsp:include page="common/nameAndProductType.jsp" flush="true"/>
             
             <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
                 	<tbody>
	                	<tr>
			                <td  class="labelStyle" nowrap="nowrap" width="22%">
			                	<s:text name="columnTitle.common.productType"/>
			                </td>
	                    </tr>
                    </tbody>
              </table>
              <u:repeatTable id="myTable" cssClass="grid borderForTable" cellpadding="0"
                             cellspacing="0" width="98%" theme="simple" cssStyle="margin:5px;">
              <thead>
                      <tr class="row_head">
                          <th width="26%"><s:text name="label.customReport.add.ProductsAndModels"/></th>
                          <th width="26%"><s:text name="label.customReport.type"/></th>
                          <th width="43%"><s:text name="label.customReport.description"/></th>
                          <th width="5%" ><div align="center">
                             <u:repeatAdd id="adder1" theme="twms">
                                <img id="addPrice" src="image/addRow_new.gif" border="0" style="cursor: pointer;" 
                                    title="<s:text name="label.customReport.addProductModel" />" />
                             </u:repeatAdd>
							 </div>
                          </th>
                       </tr>
              </thead>
              <u:repeatTemplate id="mybody" value="customReport.forItemGroups" index="myindex" theme="twms">
                     <tr index="#myindex" style="border: 1px solid #EFEBF7">
                          <td style="border-left:1px solid #DCD5CC;">
                                  <script type="text/javascript">
                                  	dojo.subscribe("/product_models/details/changed/#myindex",null,function(data, type, request) {
                                       updateProductModelsDetails(data,type,#myindex);
                                  });                                    
	                    		  </script>
                           		  <sd:autocompleter id='customReport_itemGroup_id_#myindex' showDownArrow='false' name='productName' keyName='customReport.forItemGroups[#myindex]' href='list_products_models.action' loadOnTextChange='true' loadMinimumCount='1' listenTopics='/forItemGroups/initial/#myindex' autoComplete='false' notifyTopics='/product_models/details/changed/#myindex' />
                             </td>
                          <script type="text/javascript">
		                  		dojo.addOnLoad(function(){
		                        dojo.publish("/forItemGroups/initial/#myindex", [{
		                            addItem: {
		                                key: '<s:property value="%{id}"/>',
		                                label: "<s:property value="%{name}"/>"
		                            }
		                        }]);
		                     });
	          			  </script>
                             <td style="border-left:1px solid #DCD5CC;">
                             <s:hidden name="customReport.forItemGroups[#myindex]" value="%{customReport.forItemGroups[#myindex].id}"/>
                                <span id="customReport_itemGroupType_id_#myindex">
					  			</span>
                              </td>
                              <td style="border-left:1px solid #DCD5CC;">
                                 <span id="customReport_itemGroupDescription_id_#myindex">
					  				</span>
                              </td>
                              <td align="center">
                                 <u:repeatDelete id="myTableDeleter_#myindex" theme="twms">
								 <img id="delete" src="image/remove.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.deleteEntry" />"/>
								 </u:repeatDelete>
                               </td>
                            </tr>
                      </u:repeatTemplate>
                 </u:repeatTable>    
                 <script type="text/javascript">
                    function updateProductModelsDetails(data,type,index){
                    	var forItemGroupId = dijit.byId("customReport_itemGroup_id_"+index).getValue()
							    if (type != "valuechanged") {
							        return;
							    }
							    twms.ajax.fireJavaScriptRequest("get_itemGroup_details.action",{
							    		forItemGroupId: forItemGroupId
							        }, function(details) {
								        dojo.byId("customReport_itemGroupType_id_"+index).innerHTML=details[0];
								        dojo.byId("customReport_itemGroupDescription_id_"+index).innerHTML=details[1];
							           	}
							        );
							  };
                 </script>            
                 <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
	             	<tbody>
		            	<tr>
			            	<td  class="labelStyle" nowrap="nowrap" width="10%">
			                	<s:text name="Applicable Parts"/>
			                </td>
		                </tr>
	                </tbody>
                  </table>
                  <u:repeatTable id="applicablePart" cssClass="grid borderForTable" cellpadding="0"
                             cellspacing="0" width="98%" theme="simple" cssStyle="margin:5px;">
                        <thead>
                            <tr class="row_head">
                            	 <th width="16%">&nbsp;</th>
                                <th width="14%"><s:text name="label.common.partNumber"/></th>
                                <th width="21%"><s:text name="columnTitle.common.description"/></th>
                                <th width="42%"><s:text name="label.common.applicability"/></th>
                                <th width="7%"><div align="center">
                                 <u:repeatAdd id="adder2" theme="twms">
                                    <img id="addPrice" src="image/addRow_new.gif" border="0" style="cursor: pointer;" title="<s:text name="label.customReport.addPart" />" />
                                 </u:repeatAdd></div>
                                </th>
                            </tr>
                        </thead>
                        <u:repeatTemplate id="applicablePartbody" value="customReport.applicableParts" index="applicablePartindex" theme="twms">
                            <tr index="#applicablePartindex" style="border: 1px solid #EFEBF7">
                            <s:hidden name="customReport.applicableParts[#applicablePartindex].itemCriterionItemGroup" id="isItemGroup_#applicablePartindex"/>
                                 	<td class="admin_data_table">
	   									<div id="itemLabel_#applicablePartindex"><s:text name="label.common.itemLabel" />:</div>
										<div id="itemGroupLabel_#applicablePartindex"><s:text name="label.common.itemGroupLabel" />:</div>
										<div id="toggleToItemGroup_#applicablePartindex" class="clickable"><s:text name="toggle.common.toItemGroup" /></div>
										<div id="toggleToItem_#applicablePartindex" class="clickable"><s:text name="toggle.common.toPart" /></div>
	  								</td>
	 							 	<td>
	 							 	 <s:hidden name="customReport.applicableParts[#applicablePartindex]" value="%{customReport.applicableParts[#applicablePartindex].id}"/>
	 							 	<script type="text/javascript">
		 							 	dojo.subscribe("/applicablePart/itemGroup/value/changed/#applicablePartindex",null,function(data, type, request) {
		                                       updateItemGroupDetails(type,#applicablePartindex);
		                                  });        
	
		 							 	dojo.subscribe("/applicablePart/item/value/changed/#applicablePartindex",null,function(data, type, request) {
		                                       updateItemDetails(type,#applicablePartindex);
		                                  });         
	                    			</script>
	  								  <div id="item_#applicablePartindex">
	     								 <sd:autocompleter href='list_itemsInItemGroup_forFailureReports.action' id='itemAutoComplete_#applicablePartindex' name='itemName' loadOnTextChange='true' loadMinimumCount='1' keyName='customReport.applicableParts[#applicablePartindex].itemCriterion.item' value='%{customReport.applicableParts[#applicablePartindex].itemCriterion.item.number}' showDownArrow='false' autoComplete='false' cssStyle='width:90%;' notifyTopics='/applicablePart/item/value/changed/#applicablePartindex' />
										</div>
										<div id="itemGroup_#applicablePartindex">
										  <sd:autocompleter href='list_ItemGroupsForFailureReports.action' id='itemGroupAutoComplete_#applicablePartindex' name='itemGroupName' keyName='customReport.applicableParts[#applicablePartindex].itemCriterion.itemGroup' value='%{customReport.applicableParts[#applicablePartindex].itemCriterion.itemGroup.name}' loadOnTextChange='true' loadMinimumCount='1' showDownArrow='false' autoComplete='false' cssStyle='width:90%;' notifyTopics='/applicablePart/itemGroup/value/changed/#applicablePartindex' />
	   									 </div>
	 								 </td>
                                      <script type="text/javascript">
	      									dojo.addOnLoad(function() {
										      <s:if test="customReport.applicableParts[#applicablePartindex].itemCriterionItemGroup">
										      showItemGroupWithIndex(#applicablePartindex);
										      updateItemGroupDetails("valuechanged",#applicablePartindex);
										      </s:if>
		    								  <s:else>
										      showItemWithIndex(#applicablePartindex);
										      updateItemDetails("valuechanged",#applicablePartindex);
		   									   </s:else>
										      dojo.connect(dojo.byId("toggleToItem_#applicablePartindex"), "onclick", function() {
										    	  showItemWithIndex(#applicablePartindex);
										    	  updateItemDetails("valuechanged",#applicablePartindex);
										      });
										      dojo.connect(dojo.byId("toggleToItemGroup_#applicablePartindex"), "onclick", function() {
										          showItemGroupWithIndex(#applicablePartindex);
										          updateItemGroupDetails("valuechanged",#applicablePartindex);
										      });	
		 									 });
 								 		</script>
                                <td><span id="applicablePart_description_#applicablePartindex"/></td>
                               <td>
                                  <s:checkboxlist  list="applicabilities" listKey="name" listValue="type"
                                  value="%{customReport.applicableParts[#applicablePartindex].applicabilityList.{name}}"
             					  name="customReport.applicableParts[#applicablePartindex].applicabilityList"/>
                            	</td>
                                <td width="10%" align="center">
                                 <u:repeatDelete id="applicablePartDeleter_#applicablePartindex" theme="twms">
                                    <img id="delete" src="image/remove.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.deleteEntry" />"/>
								</u:repeatDelete>
								</td>
                            </tr>
                        </u:repeatTemplate>
                   </u:repeatTable>
                  <script type="text/javascript">
                  function updateItemGroupDetails(type,index){
                    	var applicablePartItemGroupId = dijit.byId("itemGroupAutoComplete_"+index).getValue()
							    if (type != "valuechanged") {
							        return;
							    }
							    twms.ajax.fireJavaScriptRequest("get_itemGroup_details.action",{
							            applicablePartItemGroupId: applicablePartItemGroupId
							        }, function(details) {
								        dojo.byId("applicablePart_description_"+index).innerHTML=details[0];
							           	}
							        );
							  };

					function updateItemDetails(type,index){
			              var applicablePartItemId = dijit.byId("itemAutoComplete_"+index).getValue()
										    if (type != "valuechanged") {
										        return;
										    }
										    twms.ajax.fireJavaScriptRequest("get_itemGroup_details.action",{
										    	applicablePartItemId: applicablePartItemId
										        }, function(details) {
											        dojo.byId("applicablePart_description_"+index).innerHTML=details[0];
										           	}
										        );
										  };
							  
                 </script> 
             	 <s:if test="%{publishButtontoBeDisplayed()}">
                  <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
	                     <tbody>
		                        <tr>
			                         <td  class="labelStyle" nowrap="nowrap" width="22%">
			                            <s:text name="Sections"/>
			                         </td>
		                         </tr>
	                      </tbody>
                    </table>
                   <u:repeatTable id="sections" cssClass="grid borderForTable" cellpadding="0"
                             cellspacing="0" width="98%" theme="simple" cssStyle="margin:5px;">
                        <thead>
                            <tr class="row_head">
                                <th width="35%"><s:text name="label.common.name"/></th>
                                <th width="25%"><s:text name="label.common.action"/></th>
                                <th width="30%"><s:text name="label.report.order"/></th>
                                <th width="10%" ><div align="center">
                                 <u:repeatAdd id="adder3" theme="twms">
                                    <img id="addPrice" src="image/addRow_new.gif" border="0" style="cursor: pointer; padding-right:4px; " title="<s:text name="label.customReport.addSection" />" />
                                 </u:repeatAdd>
                               </div> </th>
                            </tr>
                        </thead>
                        <u:repeatTemplate id="sectionsbody" value="customReport.sections" index="sectionsindex" theme="twms">
                            <tr index="#sectionsindex" style="border: 1px solid #EFEBF7">
                                <td>
	                                <s:hidden name="customReport.sections[#sectionsindex]" value="%{customReport.sections[#sectionsindex].id}"/>
	                                <s:textfield name="customReport.sections[#sectionsindex].name"  
	                                 id="customReport_sectionName_#sectionsindex" cssStyle="width:95%;" value="%{customReport.sections[#sectionsindex].name}"/>
	                            </td>
                                <td>
	                                <s:if test="customReport.sections[#sectionsindex].id != null">
	                                    <a id="createQuestion_#sectionsindex" class="link" >
	                               			 <s:text name="label.customReport.addQuestion"/>
	                           			 </a>
	                           			   <script type="text/javascript">       
							                dojo.addOnLoad(function() {
							            		dojo.connect(dojo.byId("createQuestion_#sectionsindex"), "onclick", function(event){
							                		var sectionName = dojo.byId("customReport_sectionName_#sectionsindex").value;
							                		var reportId = '<s:property value="customReport.id"/>';
							                		var sectionId = '<s:property value="customReport.sections[#sectionsindex].id"/>';
							            			var actionURL = "add_question.action?customReport="+reportId+"&section="+sectionId;
							            			var thisTabLabel = getMyTabLabel();
							            		    parent.publishEvent("/tab/open", {label: sectionName, url: actionURL, decendentOf : thisTabLabel});
							            		});
							            	});
	        				 			 </script>     
	                                </s:if>
                                </td>
                                <td>
                                <input dojoType="twms.widget.NumberTextBox" name="customReport.sections[#sectionsindex].order"
                                 value="<s:property value='customReport.sections[#sectionsindex].order'/>" cssStyle="width:95%;" /></td>
                                <td align="center">
                                <u:repeatDelete id="sectionsDeleter_#sectionsindex" theme="twms">
                                    <img id="delete" src="image/remove.gif" border="0" style="cursor: pointer;" title="<s:text name="label.common.deleteEntry" />"/>
								</u:repeatDelete>
								</td>
        				  </tr>
                        </u:repeatTemplate>

                    </u:repeatTable>
                   </s:if>
                   <s:else>
                     <table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
	                     <tbody>
		                        <tr>
			                         <td  class="labelStyle" nowrap="nowrap" width="22%">
			                            <s:text name="Sections"/>
			                         </td>
		                         </tr>
	                      </tbody>
                    </table>
                    <u:repeatTable id="sections" cssClass="grid borderForTable" cellpadding="0"
                             cellspacing="0" width="98%" theme="simple" cssStyle="margin:5px;">
                        <thead>
                            <tr class="row_head">
                                <th width="30%"><s:text name="label.common.name"/></th>
                                <th width="20%"><s:text name="label.report.order"/></th>
                            </tr>
                        </thead>
                        <u:repeatTemplate id="sectionsbody" value="customReport.sections" index="sectionsindex" theme="twms">
                            <tr index="#sectionsindex" style="border: 1px solid #EFEBF7">
                                <td width="50%">
	                                <s:property value="%{customReport.sections[#sectionsindex].name}"/>
	                            </td>
                                <td width="50%">
                                <s:property value='customReport.sections[#sectionsindex].order'/>
                                </td>
        				   </tr>
                        </u:repeatTemplate>
                    </u:repeatTable>
                   </s:else>
               </div>
			   </div>
          </s:form>
          <br>
          <div align="center">
                <input  class="buttonGeneric" type="button" id="save_button" value="<s:text name='button.common.save'/>"  onclick="submitCreateSectionForm()" 
                        />
                <s:if test="%{publishButtontoBeDisplayed()}">
                <s:if test="%{customReport.getId()!=null && customReport.getSections().size()>0}">
                  <input class="buttonGeneric" type="button" id="publish_button" value="<s:text name='button.common.publish'/>" onClick="submitCreateSectionFormAndPublish()"/>
                </s:if>
                <s:else>
               	  <input class="buttonGeneric" type="button" id="publish_button" value="<s:text name='button.common.publish'/>" disabled="true"/>
                </s:else>
                </s:if>
                 <script type="text/javascript">
                          dojo.addOnLoad(function() {
                              var isPublish = '<s:property value="publishButtontoBeDisplayed()"/>';
                              if(isPublish=='true'){ 
                            	  dojo.byId("customReportPublish").value = false;
                              }
                          });
                 </script>
                 <s:if test="%{customReport.getId()!=null && customReport.getSections().size()>0}">
                 	 <input  class="buttonGeneric" type="button" id="view_all" value="<s:text name='label.customReport.viewAllQuestions'/>"  onclick="viewAllQuestions()"/>
                 </s:if>
                 <s:else>
                		 <input  class="buttonGeneric" type="button" id="view_all" value="<s:text name='label.customReport.viewAllQuestions'/>"  disabled="true" onClick="viewAllQuestions()"/>
                 </s:else>
                 <input id="cancel_btn" class="buttonGeneric" type="button" value="<s:text name='button.common.cancel'/>"
                             onclick="javascript:closeTab(getTabHavingLabel(getMyTabLabel()));"/>
           </div>
       </div>
 	</div>
</u:body>
</html>