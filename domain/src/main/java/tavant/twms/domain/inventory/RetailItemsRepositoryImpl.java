/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tavant.twms.domain.common.Label;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class RetailItemsRepositoryImpl extends
		GenericRepositoryImpl<InventoryItem, Long> implements
		RetailItemsRepository {
	
	private static final String queryForRetail = "from InventoryItem inventoryItem where inventoryItem.type.type='RETAIL'";
	
	@Override
	public List<InventoryItem> findAll() {
		return findUsingQuery(queryForRetail, new HashMap<String, Object>());
	}
	
	@Override
	public PageResult<InventoryItem> findAll(PageSpecification pageSpecification) {
		ListCriteria listCriteria = new ListCriteria();
        listCriteria.setPageSpecification(pageSpecification);
        return findPage(queryForRetail, listCriteria);
	}
	
	@SuppressWarnings("unchecked")
    public PageResult<InventoryItem> findPage(final ListCriteria listCriteria) {
        return findPage(queryForRetail, listCriteria);
    }
	
	public List<InventoryItem> findRetailItemsForLabel(Label label) {
		String query="select inv from InventoryItem inv join inv.labels label where label=:label";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("label", label);
		return findUsingQuery(query, params);
	}

}
