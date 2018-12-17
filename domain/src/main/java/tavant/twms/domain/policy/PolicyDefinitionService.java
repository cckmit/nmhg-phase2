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

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.common.Label;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 * 
 */
@Transactional(readOnly=true)
public interface PolicyDefinitionService {
    
    public List<PolicyDefinition> findByIds(Collection<Long> collectionOfIds);
    
    @Transactional(readOnly=false)
    public void save(PolicyDefinition policyDefinition);

    @Transactional(readOnly=false)
    public void update(PolicyDefinition policyDefinition);

    @Transactional(readOnly=false)
    public void delete(PolicyDefinition policyDefinition);
    
    
    public boolean isCodeUnique(PolicyDefinition policyDefinition);
    
    public PolicyDefinition findPolicyDefinitionById(Long policyDefinitionId);
    
    public PageResult<PolicyDefinition> findAllPolicyDefinitions(ListCriteria listCriteria);
    
    public PolicyDefinition findPolicyDefinitionsWithPriority(Long priority);
    
    public List<PolicyDefinition> findExistingPoliciesForUsedItem
                                    (InventoryItem inventoryItem,CalendarDate asOfDate)throws PolicyException;
    
    public List<PolicyDefinition> findTransferablePoliciesForInventoryItem
                                    (InventoryItem inventoryItem, CalendarDate asOfDate)throws PolicyException;
    
    public List<PolicyDefinition> findPolicyDefinitionsForLabel(Label label);
}
