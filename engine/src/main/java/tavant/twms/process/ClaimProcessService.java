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
package tavant.twms.process;

import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.claim.Claim;

/**
 * @author vineeth.varghese
 * @date Jul 12, 2006
 */
public interface ClaimProcessService {

    @Transactional(readOnly=false)
    public ProcessInstance startClaimProcessing(Claim claim);
    
    @Transactional(readOnly=false)
    public ProcessInstance startClaimProcessingWithTransition(Claim claim, String transition);
    
    @Transactional(readOnly=false)
    public ProcessInstance startClaimProcessingForReopenClaims(Claim claim, String transition);
    
    public ProcessInstance loadClaimProcess(Long processId);

    @Transactional(readOnly=false)
    public void performActionForPartsShippedNotReceieved();

    @Transactional(readOnly=false)
    public void stopClaimProcessing(Claim claim);

    @Transactional(readOnly=false)
    public void reopenClaim(Claim claim);
    
    public void updateBOMOnClaimAcceptance();
}
