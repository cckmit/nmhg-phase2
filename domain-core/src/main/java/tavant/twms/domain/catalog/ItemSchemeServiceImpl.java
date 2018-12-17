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
package tavant.twms.domain.catalog;

import java.util.List;

import tavant.twms.domain.common.Purpose;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class ItemSchemeServiceImpl extends GenericServiceImpl<ItemScheme, Long, Exception> implements ItemSchemeService {

    private ItemSchemeRepository itemSchemeRepository;
    
    public void setItemSchemeRepository(ItemSchemeRepository itemSchemeRepository) {
        this.itemSchemeRepository = itemSchemeRepository;
    }

    @Override
    public GenericRepository<ItemScheme, Long> getRepository() {
        return itemSchemeRepository;
    }

    public ItemScheme findSchemeForPurpose(Purpose purpose) {
        return itemSchemeRepository.findSchemeForPurpose(purpose);
    }

    public List<Purpose> findEmployedPurposes() {
        return itemSchemeRepository.findEmployedPurposes();
    }

}
