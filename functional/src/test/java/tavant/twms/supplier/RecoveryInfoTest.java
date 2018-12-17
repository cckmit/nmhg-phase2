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
package tavant.twms.supplier;

import java.util.List;

import tavant.twms.infra.IntegrationTestCase;
import tavant.twms.domain.supplier.recovery.RecoveryInfoService;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfoRepository;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.domain.supplier.contract.ContractService;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.CostLineItem;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.ClaimNumberPatternService;

/**
 * @author kaustubhshobhan.b
 */
public class RecoveryInfoTest extends IntegrationTestCase {

    RecoveryInfoService recoveryInfoService;

    RecoveryInfoRepository recoveryInfoRepository;

    ContractService contractService;

    ClaimNumberPatternService claimNumberPatternService;

    public void testRecoveryInfoSave() throws Exception {
        login("sedinap");
        RecoveryInfo recoveryInfo = new RecoveryInfo();
        try{
            recoveryInfoService.saveUpdate(recoveryInfo);
            assertNotNull(recoveryInfo.getId());
            assertNotNull(recoveryInfoRepository);
        }catch(Exception ex){
            throw ex;
        }
    }

    public void testCreateRecoveryClaim() throws Exception {
        RecoveryInfo recoveryInfo = recoveryInfoRepository.findById(new Long("1119888271440"));
        assertNotNull(recoveryInfo);
        contractService.createRecoveryClaims(recoveryInfo);
        assertNotNull(recoveryInfo.getCausalPartRecovery());
        RecoveryClaim recClaim = recoveryInfo.getCausalPartRecovery().getRecoveryClaim();
        assertNotNull(recClaim);        
        List<CostLineItem> cLIs = recClaim.getCostLineItems();
        assertFalse(cLIs.isEmpty());
        CostLineItem cli = cLIs.get(0);
        System.out.println(cli.getSection().getDisplayName());
        assertNotNull(cli.getCostAfterApplyingContract());
    }

    public void setContractService(ContractService contractService) {
        this.contractService = contractService;
    }

    public void setRecoveryInfoService(RecoveryInfoService recoveryInfoService) {
        this.recoveryInfoService = recoveryInfoService;
    }

    public void setRecoveryInfoRepository(RecoveryInfoRepository recoveryInfoRepository) {
        this.recoveryInfoRepository = recoveryInfoRepository;
    }

    public void setClaimNumberPatternService(ClaimNumberPatternService claimNumberPatternService) {
        this.claimNumberPatternService = claimNumberPatternService;
    }
}
