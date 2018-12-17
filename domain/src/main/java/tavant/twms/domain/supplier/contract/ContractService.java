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
package tavant.twms.domain.supplier.contract;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.PartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.Carrier;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;

/**
 * @author kannan.ekanath
 */
@Transactional(readOnly = true)
public interface ContractService {

	@Transactional(readOnly = false)
	public void updateSupplierPartReturn(RecoverablePart recoverablePart,Location location,Carrier carrier , String rgaNumber);
	
	@Transactional(readOnly = false)
	public void updateSupplierPartReturn(RecoverablePart recoverablePart, String rgaNumber);

    @Deprecated
    @Transactional(readOnly = true)
	public List<Contract> findContracts(Claim claim, PartReplaced part,
			Item item, boolean isCausalPartOnly);

	@Transactional(readOnly = false)
	public void updateOEMPartsCostLineItem(RecoveryClaim recClaim) ;

	@Transactional(readOnly = false)
	public void createOrUpdateContract(Contract contract);

	@Transactional(readOnly = true)
	public Contract findContract(Long contractId);

	@Transactional(readOnly = true)
	public void setSupplierContract(Claim claim);

	public List<Contract> findContractsForSuppiler(Supplier supplier);
	
	@Transactional(readOnly = true)
	public List<Contract> findAllContracts(String name,int pageNumber,int pageSize);

    public boolean isRecoverable(Claim claim);

    @Transactional(readOnly = true)
    public List<Contract> findContract(Claim claim,Item item,Boolean isCausalPartRecovery);

    public boolean canAutoInitiateRecovery(Claim claim);

    public void createRecoveryClaims(RecoveryInfo recoveryInfo);

    @Transactional(readOnly = false)
    public void addSupplierCostLineItems(RecoveryClaim recoveryClaim, List<RecoverablePart> recoverablePart);

    public RecoveryInfo createRecoveryInfo(Claim claim);
}
