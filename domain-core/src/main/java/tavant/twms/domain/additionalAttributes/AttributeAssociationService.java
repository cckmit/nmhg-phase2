package tavant.twms.domain.additionalAttributes;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.ClaimType;

public interface AttributeAssociationService {

	public List<AdditionalAttributes> findAttributesForItemGroups(List<ItemGroup> itemGroups, ClaimType claimType,AttributePurpose attributePurpose);

    public List<AdditionalAttributes> findAttributesForSupplier(long supplierId, ClaimType claimType);

    public List<AdditionalAttributes> findAttributesForItem(long itemId, ClaimType claimType);

    public List<AdditionalAttributes> findAttributesForFaultCode(long faultCodeId, long modelId, ClaimType claimType);

    public List<AdditionalAttributes> findAttributesForJobCode(long serProcedureId, ClaimType claimType);
    
    public List<AdditionalAttributes> findAttributesForClaim(long smrReasonId, ClaimType claimType);
    
    @Transactional(readOnly = false)
    public void deleteExistingAssociation(AttributeAssociation attributeAssociation);
    
    @Transactional(readOnly = false)
    public void saveAssociation(AttributeAssociation attributeAssociation);
    
    public Boolean isAnyAttributeConfiguredForBU();
    
    public Boolean isAnyAttributeConfiguredForSupplier();
}
