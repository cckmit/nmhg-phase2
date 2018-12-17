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

import java.util.List;

import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

/**
 * @author pradipta.a
 */
public class AdditionalAttributesServiceImpl implements AdditionalAttributesService {

    private AdditionalAttributesRepository additionalAttributesRepository;

    public List<AdditionalAttributes> findAddAttributeByPurpose(AttributePurpose purpose)
	{
		return this.additionalAttributesRepository.findAddAttributeByPurpose(purpose);
	}
    
    public PageResult<AdditionalAttributes> findAttributesForPurpose(String purpose,
            ListCriteria criteria) {
        return additionalAttributesRepository.findAdditionalAttributes(purpose, criteria);
    }

    public AdditionalAttributes findAdditionalAttributes(long id) {
        return additionalAttributesRepository.findById(id);
    }
    
    public AdditionalAttributes findAdditionalAttributeByNameForPurpose(String name,AttributePurpose purpose) {
        return additionalAttributesRepository.findAdditionalAttributeByNameForPurpose(name,purpose);
    }

    public void setAdditionalAttributesRepository(
            AdditionalAttributesRepository additionalAttributesRepository) {
        this.additionalAttributesRepository = additionalAttributesRepository;
    }

    public void saveAdditionalAttribute(AdditionalAttributes additionalAttributes) {
        this.additionalAttributesRepository.save(additionalAttributes);
    }

    public void updateAdditionalAttribute(AdditionalAttributes additionalAttributes) {
        this.additionalAttributesRepository.update(additionalAttributes);
    }

    
    public List<AdditionalAttributes> findAttributesForEquipment (long id) {
    	return this.additionalAttributesRepository.findAttributesForEquipment(id);
    	
    }

}
