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

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class LabelServiceImpl extends GenericServiceImpl<Label, String, Exception> implements
		LabelService {

	private LabelRepository labelRepository;
	
	public void setLabelRepository(LabelRepository labelRepository) {
		this.labelRepository = labelRepository;
	}
	/* (non-Javadoc)
	 * @see tavant.twms.infra.GenericServiceImpl#getRepository()
	 */
	@Override
	public GenericRepository<Label, String> getRepository() {
		return labelRepository;
	}
	public List<String> findLabelsWithNameLike(String label, int pageNumber, int pageSize) {
		return labelRepository.findLabelsWithNameLike(label, pageNumber, pageSize);
	}
	public List<String> findLabelsWithNameAndTypeLike(String label, String labelType, int pageNumber, int pageSize) {
		return labelRepository.findLabelsWithNameAndTypeLike(label, labelType, pageNumber, pageSize);
	}
	public Label findLabelWithName(String label){
		return labelRepository.findLabelWithName(label);
	}
	public PageResult<?> findAllPolicyDefintionLabels(final ListCriteria listCriteria){
		return labelRepository.findAllPolicyDefintionLabels(listCriteria);
	}
	public PageResult<?> findAllCampaignLabels(final ListCriteria listCriteria){
		return labelRepository.findAllCampaignLabels(listCriteria);
	}
	public PageResult<?> findAllFaultCodeDefinitionLabels(final ListCriteria listCriteria){
		return labelRepository.findAllFaultCodeDefinitionLabels(listCriteria);
	}
	public PageResult<?> findAllSupplierLabels(final ListCriteria listCriteria){
		return labelRepository.findAllSupplierLabels(listCriteria);
	}
	public PageResult<?> findAllJobCodeDefinitionLabels(final ListCriteria listCriteria){
		return labelRepository.findAllJobCodeDefinitionLabels(listCriteria);
	}

    public PageResult<?> findAllInventoryLabels(final ListCriteria listCriteria){
        return labelRepository.findAllInventoryLabels(listCriteria);
    }

    public PageResult<?> findAllModelLabels(final ListCriteria listCriteria){
		return labelRepository.findAllModelLabels(listCriteria);
	}
    
    public PageResult<?> findAllWarehouseLabels(final ListCriteria listCriteria){
		return labelRepository.findAllWarehouseLabels(listCriteria);
	}

    public List<Label> findLabelsForType(String type){
        return labelRepository.findLabelsForType(type);
    }
    
    public PageResult<?> findAllFleetInventoryLabels(final ListCriteria listCriteria) {
        return labelRepository.findAllFleetInventoryLabels(listCriteria);
    }
    
	public PageResult<?> findAllContractLabels(ListCriteria listCriteria) {
		return labelRepository.findAllContractLabels(listCriteria);
	}
	
	public PageResult<?> findAllFleetCustomerLabels(ListCriteria listCriteria) {
        return labelRepository.findAllFleetCustomerLabels(listCriteria);
    }
}
