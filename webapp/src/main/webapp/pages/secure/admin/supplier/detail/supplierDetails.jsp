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

<%@taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="u" uri="/ui-ext"%>
<script type="text/javascript">
dojo.addOnLoad(function(){
	dojo.connect(dojo.byId("enter_address"),"onclick",function(){
		dojo.publish("/address/show");
	});

    dojo.subscribe("/address/show", null, function() {
		dijit.byId("supplier_address").show();
    });
    dojo.subscribe("/address/hide", null, function() {
		dijit.byId("supplier_address").hide();
    });
    dojo.connect(dojo.byId("closePopup"),"onclick",function() {
            		dojo.publish("/address/hide");
				});      
});
</script>

<script type="text/javascript">
        dojo.require("twms.widget.TitlePane");
        dojo.require("twms.widget.ValidationTextBox");
        dojo.addOnLoad(function() {
            top.publishEvent("/refresh/folderCount", {})
        });        
        
    </script>
			<table width="100%" border="0" cellspacing="0" cellpadding="0" class="grid">
				<tbody>
					<s:hidden name="supplier"/>
					<tr>
						<td width="20%" nowrap="nowrap" class="labelStyle" ><s:text name="label.supplierNumber"></s:text>:</td>
						
						<td width="34%" class="label">
						<s:if test="supplier == null || supplier.id == null">
						<s:textfield
							name="supplier.supplierNumber" />
						</s:if>
						<s:else>
						<s:property
							value="supplier.supplierNumber" />
						</s:else>
							</td>
						<td width="20%" nowrap="nowrap" class="labelStyle"><s:text name="columnTitle.partSource.supplier_name"/>:</td>
						<td width="32%" class="label">
						<s:if test="supplier == null || supplier.id == null">
						<s:textfield
							name="supplier.name"  />
							</s:if>
							<s:else>
							<s:property
							value="supplier.name"  />
							</s:else>
						</td>
						
					</tr>
					
					<tr>
						<td width="18%" class="labelStyle"><s:text name="label.sra.partSource.prefferedLocationType"/>:</td>
						<td width="32%" class="label">
							<s:select name="supplier.preferredLocationType" list="{'RETAIL','BUSINESS'}"/></td>
						<td width="18%" class="labelStyle"><s:text name="label.common.address"/>:</td>
						<td width="32%" class="label">
						
                            <s:a id ="enter_address" href="#"><s:text name="label.sra.partSource.address"/></s:a>
						
						
						</td>
					</tr>
					<tr>
						<td width="20%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.preferredCurrency"/>:</td>
						<td width="32%" class="label">
						<s:if test="supplier == null || supplier.id == null">
						<s:textfield
							name="inputCurrency"  />
							</s:if>
							<s:else>
							<s:property
							value="supplier.preferredCurrency"  />
							</s:else>
						</td>
					</tr>
					<s:if test="supplier != null && supplier.id != null">
					<tr>
						<td width="20%" nowrap="nowrap" class="labelStyle"><s:text name="label.common.status"/>:</td>
						<td width="32%" class="label">
							<s:property
							value="supplier.status"  />
						</td>
					</tr>
					</s:if>
										
				</tbody>
			</table>


<jsp:include flush="true" page="address.jsp"></jsp:include>	