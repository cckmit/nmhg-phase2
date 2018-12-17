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
<%@taglib prefix="s" uri="/struts-tags"%>

<div >
	<div class="section_header">
		<s:text name="label.common.users"></s:text>
	</div>
	<table width="100%" cellpadding="0" cellspacing="0" class="grid borderForTable" align="center">
		<tbody>
			<tr class="row_head">
				<th>
					<s:text name="label.common.reciever"/>
				</th>
				<th>
					<s:text name="label.common.inspector"/>
				</th>
				<th>
					<s:text name="label.manageWarehouse.partShipper"/>
				</th>
			</tr>
			<tr>
				<td width="35%">
					<table width="100%" cellpadding="0" cellspacing="0" class="gridSub" >
						<TBODY>
							<s:iterator value="warehouse.recievers">
								<tr>
									<td>
										<s:property value="name"/>
									</td>
								</tr>
							</s:iterator>
						</TBODY>
					</table>
				</td>
				<td width="30%">
					<table width="100%" cellpadding="0" cellspacing="0" class="gridSub" >
						<TBODY>
						<s:iterator value="warehouse.inspectors">
							<tr>
								<td>
									<s:property value="name"/>
								</td>
							</tr>
						</s:iterator>
					</TBODY>
					</table>
				</td>
				<td width="35%">
					<table width="100%" cellpadding="0" cellspacing="0" class="gridSub" >
						<TBODY>
							<s:iterator value="warehouse.partShippers">
								<tr>
									<td>
										<s:property value="name"/>
									</td>
								</tr>
							</s:iterator>
						</TBODY>
					</table>
				</td>
			</tr>
		</tbody>
	</table>
</div>