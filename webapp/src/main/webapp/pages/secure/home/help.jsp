<%@taglib prefix="authz" uri="authz"%>
<%@taglib prefix="s" uri="/struts-tags"%>

	<authz:ifUserInRole roles="admin,internalUserAdmin,inventoryAdmin">	
		<span id="help" onclick="openHelp('Admin')"><s:text
			name="home_jsp.menuBar.help" /></span>
	</authz:ifUserInRole>

<authz:ifUserNotInRole roles="admin,internalUserAdmin,inventoryAdmin">
	<authz:ifProcessor>
		<authz:ifUserInRole roles="processor,warrantyProcessor">
		<span id="help" onclick="openHelp('Processor')"><s:text
			name="home_jsp.menuBar.help" /></span>
		</authz:ifUserInRole>
	</authz:ifProcessor>

	<authz:ifUserNotInRole roles="processor,warrantyProcessor">
		<authz:ifDealer>
			<authz:ifUserInRole roles="dealer,dealerWarrantyAdmin,dealerSiteAdmin,partInventoryDealer,dealerSalesAdministration,dealerAdministrator
				,dealersalesadmin,dealerpartreturn">
			<span id="help" onclick="openHelp('Dealer')"><s:text
				name="home_jsp.menuBar.help" /></span>
			</authz:ifUserInRole>
		</authz:ifDealer>
		<authz:else>
		<authz:ifUserInRole roles="dsm">
			<span id="help" onclick="openHelp('DSM')"><s:text
				name="home_jsp.menuBar.help" /></span>
		</authz:ifUserInRole>
		<authz:else>
		<!--  Currently there is no help for role 'Reciver' so redirect to PRA-->
		<authz:ifUserInRole roles="receiver">
			<span id="help" onclick="openHelp('Receiver')"><s:text
				name="home_jsp.menuBar.help" /></span>
		</authz:ifUserInRole>
		<authz:else>		
		<authz:ifUserInRole roles="inspector">
			<span id="help" onclick="openHelp('Inspector')"><s:text
				name="home_jsp.menuBar.help" /></span>
		</authz:ifUserInRole>
		<authz:else>
		<authz:ifUserInRole roles="supplier">
			<span id="help" onclick="openHelp('Supplier')"><s:text
				name="home_jsp.menuBar.help" /></span>
		</authz:ifUserInRole>
		<authz:else>
		<authz:ifUserInRole roles="sra">
			<span id="help" onclick="openHelp('SRA')"><s:text
				name="home_jsp.menuBar.help" /></span>
		</authz:ifUserInRole>
		<authz:else>
		<authz:ifUserInRole roles="partshipper">
			<span id="help" onclick="openHelp('PartShipper')"><s:text
				name="home_jsp.menuBar.help" /></span>
		</authz:ifUserInRole>
		<%-- <authz:ifUserInRole roles="system">
			<span id="help" onclick="openHelp('Admin')"><s:text
				name="home_jsp.menuBar.help" /></span>
		</authz:ifUserInRole> --%>
		<authz:else>
		<authz:ifUserInRole roles="warrantyregistrationadvisor,dsmAdvisor,cpAdvisor,sysAdvisor">
			<span id="help" onclick="openHelp('Advisor')"><s:text
				name="home_jsp.menuBar.help" /></span>
		</authz:ifUserInRole>
		<authz:else>
		<authz:ifUserInRole roles="recoveryProcessor,supplierRecoveryInitiator">
			<span id="help" onclick="openHelp('Supplier Recovery Processor')"><s:text
				name="home_jsp.menuBar.help" /></span>
		</authz:ifUserInRole>
		</authz:else>
		</authz:else>
		</authz:else>
		</authz:else>
		</authz:else>
		</authz:else>
		</authz:else>
		</authz:else>
	</authz:ifUserNotInRole>
	

	<%-- <authz:ifUserNotInRole roles="customer">
		<s:if test="isLoggedInUserADirectCustomer() == true">
			<span id="help" onclick="openHelp('DCC')"><s:text
				name="home_jsp.menuBar.help" /></span>
		</s:if>
	</authz:ifUserNotInRole> --%>
</authz:ifUserNotInRole>