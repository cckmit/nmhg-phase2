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
package tavant.twms.domain.common;

import java.util.List;

import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author aniruddha.chaturvedi
 *
 */
public interface LabelService extends GenericService<Label, String, Exception> {
	public List<String> findLabelsWithNameLike(String label, int pageNumber, int pageSize);
	
	public List<String> findLabelsWithNameAndTypeLike(String label, String labelType, int pageNumber, int pageSize);
	
	public Label findLabelWithName(String label);
	
	public PageResult<?> findAllPolicyDefintionLabels(final ListCriteria listCriteria);
	
	public PageResult<?> findAllFaultCodeDefinitionLabels(final ListCriteria listCriteria);
	
	public PageResult<?> findAllSupplierLabels(final ListCriteria listCriteria);

    public PageResult<?> findAllJobCodeDefinitionLabels(final ListCriteria listCriteria);

    public PageResult<?> findAllInventoryLabels(final ListCriteria listCriteria);

    public PageResult<?> findAllModelLabels(final ListCriteria listCriteria);

    public List<Label> findLabelsForType(String type);
    
    public PageResult<?> findAllCampaignLabels(final ListCriteria listCriteria);
    
    public PageResult<?> findAllWarehouseLabels(final ListCriteria listCriteria);
    
    public PageResult<?> findAllFleetInventoryLabels(final ListCriteria listCriteria);
    
    public PageResult<?>  findAllContractLabels(final ListCriteria listCriteria);
    
    public PageResult<?>  findAllFleetCustomerLabels(final ListCriteria listCriteria);
}
