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
package tavant.twms.domain.orgmodel;

import java.util.List;

import tavant.twms.domain.common.Purpose;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

/**
 * @author aniruddha.chaturvedi
 *
 */
public class DealerSchemeServiceImpl extends GenericServiceImpl<DealerScheme, Long, Exception> implements DealerSchemeService {
    private DealerSchemeRepository dealerSchemeRepository;

    public void setDealerSchemeRepository(DealerSchemeRepository dealerSchemeRepository) {
        this.dealerSchemeRepository = dealerSchemeRepository;
    }

    @Override
    public GenericRepository<DealerScheme, Long> getRepository() {
        return this.dealerSchemeRepository;
    }

    public List<Purpose> findEmployedPurposes() {
        return dealerSchemeRepository.findEmployedPurposes();
    }

    public DealerScheme findSchemeForPurpose(Purpose purpose) {
        return dealerSchemeRepository.findSchemeForPurpose(purpose);
    }

}
