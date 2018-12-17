<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="sd" uri="/struts-dojo-tags"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>

<u:stylePicker fileName="base.css" />
<u:stylePicker fileName="actionResult.css"/>
<style type="text/css">
	.labelFooter {
		margin:0;
	    border-top:1px solid #EFEBF7;
        padding: 4px;
    }

    .labelEditSection {
        padding: 5px;
    }

    td.label {
        padding-left:2px;
        font-weight: bold;
	}

	td.data {
        padding:0;
	}
	
	td.heading {
        font-size:10pt;
        color: #636363;
        font-weight: bold;
        font-style:  normal;
    }

    #labelsDialog {
        width: 50%;
    }
</style>

<div style="display:none;">
<div dojoType="twms.widget.Dialog" title='<s:text name="label.common.addRemoveLabel"/>' id="labelsDialog"
     bgColor="white" bgOpacity="0.5" toggle="fade" toggleDuration="250" style="z-index:1000;">
   		<div dojoType="dijit.layout.ContentPane" layoutAlign="client" id="labels_div" class="labelEditSection">
   		<div id="noSearchParamsErrorSection" class="twmsActionResults" style="display:none">
            <link href="css/theme/official/ui-ext/actionResult/actionResult.css" type="text/css" rel="stylesheet"/>
            <div class="twmsActionResultsSectionWrapper twmsActionResultsErrors">
				<h4 class="twmsActionResultActionHead"><s:text name="label.common.errors"/></h4>
					<ol>
						<li><s:text name="error.Labels.create" /></li>
					</ol>
				<hr/>
			</div>
		</div>
     		<div id="forErrors"></div>
     			<table class="form" cellpadding="0" cellspacing="0" id="labelsTable" style="margin-top: 10px;">
     			<tr>
     				<td><span style="margin-left:-10px;"><input type="radio" checked="checked" id="forAutoComplete" name="level"/><s:text name="label.common.searchLabel"/></span></td><td>
     				<input type="radio" id="fortextField" name="level" /><s:text name="button.common.create" /></td>
     			</tr>
				<tr>
			        <td width="30%" class="labelStyle"><s:text name="label.common.searchALabel"/>:</td>
			        <td id="tagLevel">		     
			        <script type="text/javascript">
               					 dojo.addOnLoad(function() {               					 	
                       			 	var url= "list_labels.action";                       			 	
                       				 dojo.publish("listLabels", [{
                           						 url: url,
                           						 params: {
                       								labelType: document.getElementById("labelType").value
                            					}
                        					}]);
               								 });
                    </script>
					<sd:autocompleter id='labelAutoComplete' required='false' showDownArrow='false' listenTopics='listLabels' />
				    <s:textfield name="label" id="textfieldForLabel"/>
					</td>
				  	<td>
                          <button class="buttonGeneric" onclick="addLabel()">
                            <s:text name="button.common.addLabel"/>
                          </button>
				  	</td>
				</tr>
						<tr>
							<td valign="top" class="labelStyle"><s:text name="label.common.selectedLabels"/>:</td>
							<td valign="top" class="data"><div id="listOfLabels"></div></td>
							<td valign="top">
                                <button class="buttonGeneric" onclick="clearList()">
                                    <s:text name="button.common.clearList"/>
                                </button>
						</tr>
					</table>
			</div>
			<div dojoType="dijit.layout.ContentPane" layoutAlign="bottom" class="labelFooter">
				<div align="center" id="submitButtonsDiv">
				  <table width="50%" class="buttonWrapperPrimary">
                    <tr>                    
                        <td>
							<button class="buttonGeneric" id="applyLabel" onclick="applyLabel()">
		                        <s:text name="button.common.applyLabel"/>
		                    </button>
		                </td>
	                    <td>
							<button class="buttonGeneric" id="removeLabel" onclick="removeLabel()">
		                        <s:text name="button.common.removeLabel"/>
		                    </button>
	                    </td>
                    	</tr>
                  </table>
			   </div>
			</div>
</div>
</div>
