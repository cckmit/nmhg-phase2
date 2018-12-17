/*
 *   Copyright (c)2008 Tavant Technologies
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
package tavant.twms.domain.supplier.recovery;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Required;

/**
 * @author  kaustubhshobhan.b
 *
 */

public class RecoveryInfoServiceImpl implements RecoveryInfoService{

    private RecoveryInfoRepository recoveryInfoRepository;

    @Transactional(readOnly = false)
    public void saveUpdate(RecoveryInfo recoveryInfo) {
      this.recoveryInfoRepository.saveUpdate(recoveryInfo);  
    }

    @Required
    public void setRecoveryInfoRepository(RecoveryInfoRepository recoveryInfoRepository) {
        this.recoveryInfoRepository = recoveryInfoRepository;
    }
    
    public RecoveryInfo findRecoveryInfoForClaim(Long claimId) {
    	return this.recoveryInfoRepository.findRecoveryInfoForClaim(claimId);
    }
    
    
    
}
