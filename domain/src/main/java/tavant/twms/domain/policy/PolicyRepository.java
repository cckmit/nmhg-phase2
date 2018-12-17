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

import java.util.List;

import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface PolicyRepository {
    
    public void create(Policy aNewPolicy);
    
    public void update(Policy aPolicy);    

    public void delete(Policy aPolicy);    
    
    public Policy findBy(Long id);
    
    @SuppressWarnings("unchecked")
    public List<RegisteredPolicy> findPoliciesForInventoryItem(InventoryItem inventoryItem);
    
    public PageResult<RegisteredPolicy> findAllPolicies(ListCriteria forCriteria);
    
    public List<RegisteredPolicy> findPoliciesForWarranty(Warranty warranty);
    
    public List<RegisteredPolicy> filterPolicyByServiceProvider(List<RegisteredPolicy> policys,ServiceProvider serviceProvider);
}