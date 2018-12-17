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

import java.util.List;

import tavant.twms.domain.common.Label;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class RetailItemsServiceImpl extends
		GenericServiceImpl<InventoryItem, Long, Exception> implements
		RetailItemsService {
	
	private RetailItemsRepository retailItemsRepository;

	public void setRetailItemsRepository(RetailItemsRepository retailItemsRepository) {
		this.retailItemsRepository = retailItemsRepository;
	}

	@Override
	public GenericRepository<InventoryItem, Long> getRepository() {
		return retailItemsRepository;
	}

	public PageResult<InventoryItem> findPage(ListCriteria listCriteria) {
		return retailItemsRepository.findPage(listCriteria);
	}
	
	public List<InventoryItem> findRetailItemsForLabel(Label label){
		return retailItemsRepository.findRetailItemsForLabel(label);
	}

}
