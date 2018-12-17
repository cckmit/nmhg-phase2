 <%@ page contentType="text/html" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="t" uri="twms" %>
<%@ taglib prefix="u" uri="/ui-ext" %>
<html>
<head>
</head>
<body>

<s:if test="loggedInUserAParentDealer && childDealers.size()!=1">
                           <s:if test="allowInventorySelection">
							<script type="text/javascript"
								src="scripts/warrantyRegForInternalUser.js"></script>
							<script type="text/javascript">
                                    dojo.addOnLoad(function(){                        
                                    <s:if test="forDealer==null">
                                            dojo.html.hide(dojo.byId("warrantyRegDiv"));
                                    </s:if>
                                    <s:else>
                                            dojo.html.show(dojo.byId("warrantyRegDiv"));
                                    </s:else>
                                    <s:if test="forDealer!=null && warranty.id!=null">
                                            dojo.html.show(dojo.byId("warrantyRegDiv"));
                                            dojo.byId("dealerName").value='<s:property value="forDealer.name"/>';
                                            dojo.byId("dealerId").value = '<s:property value="forDealer.Id"/>';
                                        
                                    </s:if>
                                        });
                                </script>
							<div
								style="background: #F3FBFE; border: 1px solid #EFEBF7; margin: 5px; padding-bottom: 10px;">
								<table class="form" cellpadding="0" cellspacing="0">
									
									<tr style="width: 50%">
										<td id="dealerNameText" class="labelStyle"><s:text
												name="label.common.dealerName" />:</td>
										<td><s:if test="forDealer!=null && warranty.id!=null">
												
												<s:hidden name="forDealer" value="%{forDealer.id}"
													id="dealer" />
											</s:if> <s:else>
											<s:hidden id="behalfDealer" name="behalfDealer" value="%{getLoggedInUsersOrganization()}"/>
												<s:text name="%{getLoggedInUsersOrganization().getName()}"/> <!-- NMHGSLMS-1136 only the parent dealer can file the DR , so only the logged in user is selected, which is the parent dealer -->
												<script type="text/javascript">
                                    dojo.addOnLoad(function() {
                                   	 <s:if test="loggedInUserAParentDealer">
                                            dojo.byId("dealerName").value = "<s:property value = '%{getLoggedInUsersOrganization().getName()}'/>"; 
    										dojo.byId("dealerId").value = dojo.byId("behalfDealer").value;
                                            if (dojo.byId("dealerName").value.indexOf("&amp;") > 0)
                                            {
                                                dojo.byId("dealerName").value = dojo.byId("dealerName").value.replace("&amp;", "&");
                                            }
                                        </s:if>
                                    });
                                </script>
											</s:else>
											</td>
									</tr>
								</table>
							</div>
						
						</s:if>
						<s:else>
							<input type="hidden" name="forDealer"
								value="<s:property value="forDealer.name"/>" />
							<script type="text/javascript">
                                    dojo.addOnLoad(function() {
                                        dojo.byId("dealerName").value='<s:property value="forDealer.name"/>';
                                        dojo.byId("dealerId").value = '<s:property value="forDealer.id"/>';
                                    });
                                </script>
						</s:else>
					</s:if>
<%-- 					</s:if> --%>
						<s:else>
							<input type="hidden" name="forDealer"
								value="<s:property value="forDealer.name"/>" />
							<script type="text/javascript">
                                    dojo.addOnLoad(function() {
                                        dojo.byId("dealerName").value='<s:property value="forDealer.name"/>';
                                        dojo.byId("dealerId").value = '<s:property value="forDealer.id"/>';
                                    });
                                </script>
						</s:else>
</body>
</html> 