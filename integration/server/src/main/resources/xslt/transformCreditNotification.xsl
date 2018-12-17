<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns="http://www.tavant.com/globalsync/warrantyclaimcreditnotification" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:template match="/">
		<xsl:call-template name="CreateSyncInvoice"/>
	</xsl:template>
	<xsl:template name="CreateSyncInvoice">
		<SyncInvoice>
			<ApplicationArea>
				<Sender>
					<Task>
						<xsl:value-of select="/SyncInvoice/ApplicationArea/Sender/Task"/>
					</Task>
					<LogicalId>
						<xsl:value-of select="/SyncInvoice/ApplicationArea/Sender/LogicalId"/>
					</LogicalId>
					<ReferenceId>
						<xsl:value-of select="/SyncInvoice/ApplicationArea/Sender/ReferenceId"/>
					</ReferenceId>
				</Sender>
				<BODId>
					<xsl:value-of select="/SyncInvoice/ApplicationArea/BODId"/>
				</BODId>
				<InterfaceNumber>
					<xsl:value-of select="/SyncInvoice/ApplicationArea/InterfaceNumber"/>
				</InterfaceNumber>
				<CreationDateTime>
					<xsl:value-of select="/SyncInvoice/ApplicationArea/CreationDateTime"/>
				</CreationDateTime>
			</ApplicationArea>
			<DataArea>
				<Invoice>
					<Header>
						<UserArea>
							<ClaimNumber>
								<xsl:value-of select="/SyncInvoice/DataArea/Invoice/Header/UserArea/ClaimNumber"/>
							</ClaimNumber>
						</UserArea>
						<DocumentIds>
							<DocumentId>
								<Id>
									<xsl:value-of select="/SyncInvoice/DataArea/Invoice/Header/DocumentIds/DocumentId/Id"/>
								</Id>
							</DocumentId>
						</DocumentIds>
						<TotalAmount>
							<xsl:value-of select="/SyncInvoice/DataArea/Invoice/Header/TotalAmount"/>
						</TotalAmount>
						<DocumentDateTime>
							<xsl:value-of select="/SyncInvoice/DataArea/Invoice/Header/DocumentDateTime"/>
						</DocumentDateTime>
					</Header>
					<Line>
						<Tax>
							<TaxAmount>
								<xsl:value-of select="/SyncInvoice/DataArea/Invoice/Line/Tax/TaxAmount"/>
							</TaxAmount>
						</Tax>
						<TotalAmount>
							<xsl:attribute name="currency"><xsl:value-of select="/SyncInvoice/DataArea/Invoice/Line/TotalAmount/@currency"/></xsl:attribute>
						</TotalAmount>
					</Line>
				</Invoice>
			</DataArea>
		</SyncInvoice>
	</xsl:template>
</xsl:stylesheet>
