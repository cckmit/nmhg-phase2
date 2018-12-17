/*
 *   Copyright (c) 2008 Tavant Technologies
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
package tavant.twms.domain.additionalAttributes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.util.StringUtils;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.ClaimType;

/**
 * @author pradipta.a
 */
public class AttributeAssociationServiceImpl implements AttributeAssociationService {

    private AttributeAssociationRepository attributeAssociationRepository;

    public List<AdditionalAttributes> findAttributesForItemGroups(List<ItemGroup> itemGroups, ClaimType claimType,AttributePurpose attributePurpose) {
        List<AttributeAssociation> attrAssociations = this.attributeAssociationRepository
                .findAttributesForItemGroups(itemGroups,attributePurpose);
        return getAttributeListFromAssociations(attrAssociations,claimType);
    }
    
    public Boolean isAnyAttributeConfiguredForBU()
    {
    	return this.attributeAssociationRepository.isAnyAttributeConfiguredForBU();
    }

    public List<AdditionalAttributes> findAttributesForItem(long itemId, ClaimType claimType) {
        List<AttributeAssociation> attrAssociations = this.attributeAssociationRepository
                .findAttributesForPart(itemId);
        return getAttributeListFromAssociations(attrAssociations,claimType);
    }

    public List<AdditionalAttributes> findAttributesForSupplier(long supplierId, ClaimType claimType) {
        List<AttributeAssociation> attrAssociations = this.attributeAssociationRepository
                .findAttributesForSupplier(supplierId);
        return getAttributeListFromAssociations(attrAssociations,claimType);
    }

    public List<AdditionalAttributes> findAttributesForFaultCode(long faultCodeId,long modelId,ClaimType claimType) {
        List<AttributeAssociation> attrAssociations = this.attributeAssociationRepository
                .findAttributesForFaultCode(faultCodeId, modelId);
        return getAttributeListFromAssociations(attrAssociations,claimType);
    }

    public List<AdditionalAttributes> findAttributesForJobCode(long serProcedureId, ClaimType claimType) {
        List<AttributeAssociation> attrAssociations = this.attributeAssociationRepository
                .findAttributesForJobCode(serProcedureId);
        return getAttributeListFromAssociations(attrAssociations,claimType);
    }
    
    public List<AdditionalAttributes> findAttributesForClaim(long smrReasonId, ClaimType claimType) {
        List<AdditionalAttributes> additionalAttributes = this.attributeAssociationRepository
                .findAttributesForClaim(smrReasonId);
        return getAttributeListByClaimType(additionalAttributes,claimType);
    }
    
    private List<AdditionalAttributes> getAttributeListByClaimType(
            List<AdditionalAttributes> additionalAttributes,ClaimType claimType) {
        List<AdditionalAttributes> attrList = new ArrayList<AdditionalAttributes>();
        for (AdditionalAttributes additionalAttribute : additionalAttributes) {
        	StringTokenizer token =new StringTokenizer(additionalAttribute.getClaimTypes(),",");
        	while(token.hasMoreTokens()){
        		String str= token.nextToken().trim();
        		if(str.equals(claimType.getType())){
        			
        				attrList.add(additionalAttribute);
        			
        		}
        	}
        }
        return attrList;
    }

    private List<AdditionalAttributes> getAttributeListFromAssociations(
            List<AttributeAssociation> attrAssociations,ClaimType claimType) {
        List<AdditionalAttributes> attrList = new ArrayList<AdditionalAttributes>();
      //if(!attrList.isEmpty()){ //Wrongly fixed (Revision 35592).Here no need to add any check as attrList is just initialized.
        for (AttributeAssociation attrAssociation : attrAssociations) {
        	StringTokenizer token =new StringTokenizer(attrAssociation.getForAttribute().getClaimTypes(),",");
        	while(token.hasMoreTokens()){
        		String str= token.nextToken().trim();
        		if(str.equals(claimType.getType())){
        			if(attrAssociation.getForAttribute().getAttributeAssociations().contains(attrAssociation)){
        				attrList.add(attrAssociation.getForAttribute());
        			}
        		}
        	}
        }
        //}
        return attrList;
    }

    public void setAttributeAssociationRepository(
            AttributeAssociationRepository attributeAssociationRepository) {
        this.attributeAssociationRepository = attributeAssociationRepository;
    }

	public void deleteExistingAssociation(
			AttributeAssociation attributeAssociation) {
		this.attributeAssociationRepository.delete(attributeAssociation);
		
	}
	
	public void saveAssociation(
			AttributeAssociation attributeAssociation) {
		this.attributeAssociationRepository.save(attributeAssociation);
		
	}
	
	public Boolean isAnyAttributeConfiguredForSupplier(){
		 return this.attributeAssociationRepository.isAnyAttributeConfiguredForSupplier();
	}
}
