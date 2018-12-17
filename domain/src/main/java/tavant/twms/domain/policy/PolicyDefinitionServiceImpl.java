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
package tavant.twms.domain.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.common.Label;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 *
 */
public class PolicyDefinitionServiceImpl implements PolicyDefinitionService {
    private PolicyDefinitionRepository policyDefinitionRepository;
    
    public PageResult<PolicyDefinition> findAllPolicyDefinitions(ListCriteria listCriteria) {
        return this.policyDefinitionRepository.findAll(listCriteria);
    }

    public PolicyDefinition findPolicyDefinitionById(Long policyDefinitionId) {
        return this.policyDefinitionRepository.findById(policyDefinitionId);
    }

    public List<PolicyDefinition> findByIds(Collection<Long> collectionOfIds) {
        return this.policyDefinitionRepository.findByIds(collectionOfIds);
    }
    
    public List<PolicyDefinition> findExistingPoliciesForUsedItem
                (InventoryItem inventoryItem,CalendarDate asOfDate) throws PolicyException {

        List<PolicyDefinition> offerredPolicies = this.policyDefinitionRepository.findExistingPoliciesForUsedItem(inventoryItem,asOfDate);
        CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
        List<PolicyDefinition> availableOnes = policiesAvailableOn(inventoryItem, offerredPolicies,
                deliveryDate);
        return availableOnes;
    }
    
    public List<PolicyDefinition> findTransferablePoliciesForInventoryItem
                (InventoryItem inventoryItem,CalendarDate asOfDate) throws PolicyException {
        List<PolicyDefinition> offerredPolicies = this.policyDefinitionRepository.findTransferablePoliciesForInventoryItem(inventoryItem,asOfDate);
        CalendarDate deliveryDate = inventoryItem.getDeliveryDate();
        List<PolicyDefinition> availableOnes = policiesAvailableOn(inventoryItem, offerredPolicies,
                deliveryDate);
        return availableOnes;
    
    }
    

    public void save(PolicyDefinition policyDefinition) {
    	PolicyDefinitionAudit policyDefinitionAudit = new PolicyDefinitionAudit();
    	policyDefinitionAudit.setActionTaken(policyDefinition.isCurrentlyInactive() ? "INACTIVE":"ACTIVE");
    	policyDefinitionAudit.setComments(policyDefinition.getComments());
    	policyDefinitionAudit.getD().setUpdatedOn(Clock.today());
    	policyDefinitionAudit.setForPolicyDefinition(policyDefinition);
    	policyDefinition.getPolicyDefinitionAudits().add(policyDefinitionAudit); 
        this.policyDefinitionRepository.save(policyDefinition);
    }

    public void update(PolicyDefinition policyDefinition) {
    	PolicyDefinitionAudit policyDefinitionAudit = new PolicyDefinitionAudit();
    	policyDefinitionAudit.setActionTaken(policyDefinition.isCurrentlyInactive() ? "INACTIVE":"ACTIVE");
    	policyDefinitionAudit.setComments(policyDefinition.getComments());
    	policyDefinitionAudit.getD().setUpdatedOn(Clock.today());
    	policyDefinitionAudit.setForPolicyDefinition(policyDefinition);
    	policyDefinition.getPolicyDefinitionAudits().add(policyDefinitionAudit);    	
        this.policyDefinitionRepository.update(policyDefinition);
    }

    public void delete(PolicyDefinition policyDefinition) {
        this.policyDefinitionRepository.delete(policyDefinition);
    }

    public boolean isCodeUnique(PolicyDefinition policyDefinition) {
        return this.policyDefinitionRepository.isCodeUnique(policyDefinition);
    }

    @Required
    public void setPolicyDefinitionRepository(PolicyDefinitionRepository policyDefinitionRepository) {
        this.policyDefinitionRepository = policyDefinitionRepository;
    }

    /**
     * @param inventoryItem
     * @param allPolicies
     * @return
     * @throws PolicyException
     */
    protected List<PolicyDefinition> policiesAvailableOn(InventoryItem inventoryItem,
            List<PolicyDefinition> allPolicies, CalendarDate aDate) throws PolicyException {
        List<PolicyDefinition> availableOnes = new ArrayList<PolicyDefinition>();
        for (PolicyDefinition aPolicyDefinition : allPolicies) {
            if (aPolicyDefinition.isAvailable(inventoryItem, aDate)) {
                availableOnes.add(aPolicyDefinition);
            }
        }
        return availableOnes;
    }
    
    public PolicyDefinition findPolicyDefinitionsWithPriority(Long priority) {
        return this.policyDefinitionRepository.findPolicyDefinitionWithPriority(priority);
    }
    
    public List<PolicyDefinition> findPolicyDefinitionsForLabel(Label label){
    	return this.policyDefinitionRepository.findPolicyDefinitionsForLabel(label);
    }
}
