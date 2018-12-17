/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Jun 25, 2007
 * Time: 2:58:26 PM
 */

package tavant.twms.domain.businessobject;

import java.util.HashSet;
import java.util.Set;

import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.rules.DomainType;
import tavant.twms.domain.rules.DomainTypeSystem;
import tavant.twms.domain.rules.Type;

public class ClaimDuplicacyRulesBusinessObjectModel extends
		AbstractRulesBusinessObjectModel {

	protected DomainType domainType;

	@Override
	public String getExpressionForDomainType(String typeName) {
		return ("ClaimDuplicacy".equals(typeName)) ? "claim" : null;
	}

	@Override
	public Set<DomainType> getDomainTypes() {
		Set<DomainType> set = new HashSet<DomainType>();
		set.add(domainType);
		return set;
	}

	public ClaimDuplicacyRulesBusinessObjectModel() {
		domainTypeSystem = new DomainTypeSystem();

		domainType = claimDuplicacy();
		discoverPathsToFields(domainType, "claim");
	}

	private DomainType claimDuplicacy() {
		String typeName = "ClaimDuplicacy";
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType claimDuplicacy = new DomainType(
			/* Domain term */"Claim",
			/* Unique Type* Name */typeName);

			// Simple Fields.
			claimDuplicacy.simpleField("label.common.causedby",
					"activeClaimAudit.serviceInformation.causedBy.name", Type.STRING);
			claimDuplicacy.simpleField("label.common.dateFailure", "activeClaimAudit.failureDate",
					Type.DATE);
			claimDuplicacy.simpleField("label.common.dateRepair", "activeClaimAudit.repairDate",
					Type.DATE);
			 claimDuplicacy.simpleField("label.common.repairStartDate", "repairStartDate", Type.DATE);
			claimDuplicacy.simpleField("label.common.faultFound",
					"activeClaimAudit.serviceInformation.faultFound.nameInEnglish", Type.STRING);
			claimDuplicacy.simpleField("label.common.workOrderNumber", "activeClaimAudit.workOrderNumber",
					Type.STRING);
            claimDuplicacy.simpleField("label.claim.partSerialNumber", "partSerialNumber",
                    Type.STRING);
            claimDuplicacy.simpleField("label.common.historicalClaimNumber", "histClmNo",
                    Type.STRING);
            claimDuplicacy.simpleField("label.rules.technicianID", "activeClaimAudit.serviceInformation.serviceDetail.serviceTechnician",
                    Type.STRING);
            
			// One To One Associations.             
			claimDuplicacy.oneToOne("label.common.serviceProvider", "forDealer",
					dealership());
			claimDuplicacy.oneToOne("label.failure.failureCode",
					"activeClaimAudit.serviceInformation.faultCodeRef", failureCode());
			claimDuplicacy.oneToOne("label.common.causalPart",
					"activeClaimAudit.serviceInformation.causalPart", item());
			claimDuplicacy.oneToMany("label.recoveryClaim.serviceDetails",
					"activeClaimAudit.serviceInformation.serviceDetail.laborPerformed",
					laborDetail());			
			claimDuplicacy.oneToMany("label.common.incidentals","activeClaimAudit.serviceInformation.serviceDetail",
					travelDetail());
			// One To Many Associations.
			claimDuplicacy.oneToMany("label.claim.claimedItems", "claimedItems",
					claimedItem());

			domainTypeSystem.registerDomainType(claimDuplicacy);
			return claimDuplicacy;
		}

		return domainTypeSystem.getDomainType(typeName);
	}
	
	public String getTopLevelTypeName() {
		return "Claim";
	}

	public String getTopLevelAlias() {
		return "claim";
	}
}
