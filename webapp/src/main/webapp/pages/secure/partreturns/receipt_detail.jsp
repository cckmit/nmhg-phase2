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
<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@taglib prefix="t" uri="twms"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<%@taglib prefix="authz" uri="authz"%>
<%response.setHeader( "Pragma", "no-cache" );
response.addHeader( "Cache-Control", "must-revalidate" );
response.addHeader( "Cache-Control", "no-cache" );
response.addHeader( "Cache-Control", "no-store" );
response.setDateHeader("Expires", 0); %>
<html>
<head>
<meta http-equiv="Context-Type" content="text/html; charset=ISO-8859-1"/>
<title>
<s:text name="title.common.warranty"/>
</title>
<s:head theme="twms"/>
<u:stylePicker fileName="base.css"/>
<u:stylePicker fileName="partreturn.css"/>
<u:stylePicker fileName="yui/reset.css" common="true"/>
<u:stylePicker fileName="common.css"/>
<u:stylePicker fileName="form.css"/>
</head>
<script type="text/javascript">
dojo.require("dijit.layout.ContentPane");
dojo.require("twms.widget.TitlePane");
dojo.require("twms.widget.Dialog");

function setComments() {

var field = dojo.byId("com");
dojo.byId("internalComments").value = field.value;
}	
</script>

<u:body>
  <div class="separatorTop" />
  <s:form id="duePartsRecieved_form" action="duepartsreceived_submit">
    <s:hidden id="identifier" name="id"/>
    <s:hidden id="inboxViewType" name="inboxViewType"/>
    <u:actionResults/>
    <s:if test="%{isClaimDenied()}">
      <div class="twmsActionResultsSectionWrapper twmsActionResultsWarnings">
        <h4 class="twmsActionResultActionHead">WARNINGS</h4>
        <ol>
           <s:property value="denialMessage"/>
        </ol>
        <hr/>
      </div>
    </s:if>
    <div dojoType="dijit.layout.ContentPane" label="Parts List" style="overflow-Y: auto;overflow-x:hidden; width:100%;" id="mainDiv">
      <s:set name="partsCounter" value="0"/>
      <s:iterator value="claimWithPartBeans" status="claimIterator">
      <div dojoType="twms.widget.TitlePane" 
		title="<s:text name="title.partReturnConfiguration.claimDetails"/>
      (
      <s:property value="claim.claimNumber"/>
      )" labelNodeClass="section_header">
      <div style="width:98%">
        <%@include file="tables/claim_details.jsp"%>
      </div>
      <hr/>
      <s:iterator value="shipments" status="ite">
          <table cellspacing="0" border="0" cellpadding="0" width="100%" class="grid">
            <tr>
              <td class="labelStyle" width="16%"  nowrap="nowrap"><s:text name="columnTitle.shipmentGenerated.shipment_no" />
                :</td>
                 <td class="labelNormal"  width="34%"><s:property value="id" />
               </td>
              <td class="labelStyle" width="16%"  nowrap="nowrap"><s:text name="label.partReturn.returnToLocation" />
                :</td>
              <td class="labelNormal"  width="34%"><s:property value="destination.code" />
              </td>
            </tr>
            <tr>
              <td class="labelStyle" width="18%"  nowrap="nowrap"><s:text name="label.partReturnConfiguration.carrier" />
                :</td>
              <td class="labelNormal" width="36%" ><s:property value="carrier.name" />
              </td>
              <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.shipmentDate" />
                :</td>
              <td class="labelNormal" ><s:property value="shipmentDateForDisplay" />
              </td>
            </tr>
            <tr>
              <authz:ifUserNotInRole roles="receiverLimitedView, inspectorLimitedView">
                  <td class="labelStyle" nowrap="nowrap"><s:text name="label.common.dealerName" />
                    :</td>
                  <td class="labelNormal" ><s:property value="claim.forDealer.name" />
                  </td>
              </authz:ifUserNotInRole>

              <td class="labelStyle" nowrap="nowrap"><s:text name="label.partReturnConfiguration.trackingNumber" />
                :</td>
              <td class="labelNormal" ><s:property value="trackingId" />
              </td>
            </tr>
            <s:if test="!claim.commercialPolicy && !'Campaign'.equalsIgnoreCase(claim.type.type) && claim.open && (!claim.reopened || claim.reopenRecoveryClaim)">
              <tr>
                <td colspan="4"><u:openTab tabLabel="Supplier Contracts %{claim.number}"
                                       url="specify_supplier_contract.action?claim=%{claim.id}&folderName=%{folderName}"
                                       id="specifySupplierContract_%{claim.id}"
                                       cssClass="link"
                                       decendentOf="%{getText('label.invTransaction.tabHeading')}">
                    <s:text name="button.supplierRecovery.specifySupplierContract"/>
                  </u:openTab>
                </td>
              </tr>
            </s:if>
          </table>
           <hr/>
      </s:iterator>
      <div class="mainTitle" style="border-bottom:1px solid #E6EAEF; margin-bottom:10px; padding-top:10px; width:99%;">
        <s:text name="title.partReturnConfiguration.partDetails"/>
      </div>
      
        <div style="width:100%; overflow:auto;">
          <table cellspacing="0" cellpadding="0" class="grid borderForTable">
            <tr>
              <th class="colHeader"></th>
              <th  valign="middle" class="colHeader"><s:text name="columnTitle.partReturnConfiguration.partNumber" /></th>
              <th  valign="middle" class="colHeader"><s:text name="columnTitle.common.description" /></th>
              <th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.shipped" /></th>
              <th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.cannotShip" /></th>
              <th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.received" /></th>
              <th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.notReceived" /></th>
              <th valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.total" /></th>
              <th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.receive" /></th>
              <th  valign="middle" class="colHeader" align="right"><s:text name="label.partReturn.didNotReceive" /></th>
              <th width="12%" valign="middle" class="colHeader"><s:text name="label.partReturn.qtyAddedToShipment" /></th>
              <th  valign="middle" class="colHeader"><s:text name="label.partReturnConfiguration.location" /></th>
              <authz:ifUserInRole roles="inspector">
                <th  valign="middle" class="colHeader"><s:text name="label.partReturn.isInspect"/></th>
                <th valign="middle" class="colHeader"><s:text name="label.partReturn.isScrap" /></th>
                <th valign="middle" class="colHeader"> <table>
                    <tr>
                      <th style="color:#5577B4"><s:text name="columnTitle.partReturnConfiguration.action" /></th>
                      <th style="color:#5577B4"><s:text name="label.partReturn.settlementCode" /></th>
                    </tr>
                  </table></th>
              </authz:ifUserInRole>
            </tr>
            <s:iterator value="partReplacedBeans" status="partIterator">
              <tr class="tableDataWhiteText">
                <td align="center"><s:checkbox
 		     		name="partReplacedBeans[%{partsCounter}].selected"
 		     		value="selected" />
                </td>
                <s:iterator value="partReturnTasks" status ="taskIterator">
	                <input type="hidden"  name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].partReturnTasks[<s:property value="%{#taskIterator.index}"/>].task" 
	                  value="<s:property value="task.id"/>"/> 
                </s:iterator>
                <input type="hidden"  name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].oemPartReplaced" 
                value="<s:property value="partReplacedBeans[#partIterator.index].oemPartReplaced.id"/>"/>
                <input type="hidden"  name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].claim" 
                      value="<s:property value="partReplacedBeans[#partIterator.index].claim.id"/>"/>
                <td>
	                <s:property value="oemPartReplaced.itemReference.unserializedItem.alternateNumber" />
	                  <s:if test="oemPartReplaced.oemDealerPartReplaced!=null && oemPartReplaced.oemDealerPartReplaced.id!=null"> <img  src="image/comments.gif" id= "oem_dealer_causal_part"
	   			            		title="<s:property value="oemPartReplaced.oemDealerPartReplaced.number" />" 
	                    alt="<s:property value="oemPartReplaced.oemDealerPartReplaced.number" />"/> 
	                  </s:if>
                </td>
                <td>
                	<s:property value="oemPartReplaced.itemReference.unserializedItem.description" />
                </td>
                <td>
                	<s:property value="shipped" />
                </td>
                <input type="hidden" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>
                ].shipped" value="<s:property value="shipped"/>"/>
                <td>
                	<s:property value="cannotBeShipped" />
                </td>
                <td>
                	<s:property value="received" />
                </td>
                <input type="hidden" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].received"       
                  value="<s:property value="received"/>"/>
                <td>
               		 <s:property value="notReceived" />
                </td>
                <td>
               		 <s:property value="totalNoOfParts" />
                </td>
             	<td>
             		<input type="text"  size="3" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].receive" 
             		value="<s:property value="receive"/>"/>
             	</td>
                <td>
                	<input type="text"  size="3" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].didNotReceive"
                	value="<s:property value="didNotReceive"/>"/>
                </td>
                <td>
                	<s:property value="qtyForShipment"/>
	                <input type="hidden" name="partReplacedBeans[<s:property value="%{#partsCounter}"/>].qtyForShipment"
	                     value="<s:property value="qtyForShipment"/>"/>
                </td>
                <td>
                	<s:select list="%{getWareHouses(shipment.destination.code)}" 
	            	name='partReplacedBeans[%{#partsCounter}].warehouseLocation' size='1'
	            	headerKey="" headerValue="%{getText('dropdown.partReturnConfiguration.location')}"> </s:select>
                </td>
                <authz:ifUserInRole roles="inspector">
	                  <td align="center"><s:checkbox id="inspected_%{#partsCounter}"  
				   			name="partReplacedBeans[%{#partsCounter}].toBeInspected"
	      						value="toBeInspected" > </s:checkbox>
	                  </td>
	            	<td>
						<s:checkbox id="scrapped_%{#partsCounter}"
				   					name="partReplacedBeans[%{#partsCounter}].toBeScrapped">
				   		</s:checkbox>
                	</td>
	                    <script type="text/javascript">
			      			   dojo.addOnLoad(function() {
			      			   setInspectionValue(<s:property value="%{#partsCounter}"/>);
			        		   dojo.connect(
				        		   dojo.byId("inspected_" + <s:property value="%{#partsCounter}"/>), "onchange",
				        		   function(evt)
				        		   {
				        			 setInspectionValue(<s:property value="%{#partsCounter}"/>);
				        		   }              
			        			);	      
			            		});
	        			</script>
	                  <td>
	                     <div id="includeInspectionAction">
	                      <table>
	                        <jsp:include flush="true" page="tables/inspectionActionInclude.jsp" />
	                      </table>
	                    </div>
	                  </td>
                </authz:ifUserInRole>
                <s:set name="partsCounter" value="%{#partsCounter + 1}"/>
              </tr>
            </s:iterator>
          </table>
        </div>
    </div>
    </div>
    <div class="separator" />
    </s:iterator>
    </div>
	<s:if test="%{isNotShipmentView()}">
	    <div >
	      <div dojoType="twms.widget.TitlePane" title="<s:text name="title.newClaim.supportDocs"/>
	      " labelNodeClass="section_header">
	      		<jsp:include flush="true" page="../partreturns/uploadAttachments.jsp" />
	      </div>
	    </div>
	</s:if>
    <jsp:include page="../partreturns/fileUploadDialog.jsp"/>
    <div width="100%" />
    <!-- A hack for IE -->
    <div style="margin-top:10px;">
      <div class="detailsHeader">
        <s:text name="title.partReturnConfiguration.comments"/>
      </div>
      <table  cellspacing="0" cellpadding="0" class="grid">
        <tr>
          <td width="20%" class="carrierLabel" nowrap="nowrap"><s:text name="label.partReturnConfiguration.dealerComments"/>
            : </td>
          <td ><s:property value="comments"/>
          </td>
        </tr>
        <tr>
          <td width="20%" class="carrierLabel" nowrap="nowrap"><s:text name="label.partReturnConfiguration.comments"/>
            :</td>
          <td class="labelNormalTop"><t:textarea cols="80" rows="3" name="newComments" id="com"
                   wrap="physical" cssClass="bodyText" onchange="setComments()"/>
            <s:hidden name ="comments" id ="internalComments"/>
            <script type="text/javascript">
	              dojo.addOnLoad(function() {
	              	setComments();
	              });
              </script>
          </td>
        </tr>
        <tr>
          <td>&nbsp;</td>
        </tr>
      </table>
    </div>
    <div class="separator" />
    <div class="buttonWrapperPrimary spacingAtTop">
      <s:submit value="%{getText('button.common.submit')}" cssClass="buttonGeneric"/>
    </div>
    </div>
  </s:form>
  <script type="text/javascript">
  function setInspectionValue(index)
  {
	  var inspectCheck=dojo.byId("inspected_"+index);
	  if(! inspectCheck.checked)
	  {
		  dojo.byId("accept_"+index).setAttribute("disabled","disabled");
		  dojo.byId("reject_"+index).setAttribute("disabled","disabled");
		  dijit.byId("acceptReasons_" + index).setDisabled(true);
		  dijit.byId("failureReasons_"+index).setDisabled(true);
		  dojo.byId("scrapped_"+index).setAttribute("disabled","disabled");
	  }
	  else{
		  dojo.byId("accept_"+index).removeAttribute("disabled");
		  dojo.byId("reject_"+index).removeAttribute("disabled");
		  dijit.byId("acceptReasons_"+index).setDisabled(false);
		  dijit.byId("failureReasons_"+index).setDisabled(false);
		  dojo.byId("scrapped_"+index).removeAttribute("disabled");
	  }
  }
  </script>
<authz:ifPermitted resource="partReturnsDuePartsReceiptReadOnlyView">
	<script type="text/javascript">
	    dojo.addOnLoad(function() {
	        for ( var i = 0; i < dojo.query("input, button, textarea, select, text", dojo.byId('duePartsRecieved_form')).length; i++) {
	            dojo.query("input, button, textarea, select, text", dojo.byId('duePartsRecieved_form'))[i].disabled=true;
	        }
	    });
	</script>
</authz:ifPermitted>
  </div>
</u:body>
</html>
