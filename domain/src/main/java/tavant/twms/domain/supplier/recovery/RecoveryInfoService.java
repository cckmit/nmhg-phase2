package tavant.twms.domain.supplier.recovery;

import org.springframework.transaction.annotation.Transactional;/*
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

/**
 * @author  kaustubhshobhan.b
 *
 */

public interface RecoveryInfoService {

    @Transactional(readOnly=false)
    public void saveUpdate(RecoveryInfo recoveryInfo);
    
    public RecoveryInfo findRecoveryInfoForClaim(Long claimId);
}
