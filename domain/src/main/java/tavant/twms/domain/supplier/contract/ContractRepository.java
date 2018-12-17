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
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.infra.GenericRepository;

/**
 * @author kannan.ekanath
 */
@Transactional(readOnly = true)
public interface ContractRepository extends GenericRepository<Contract, Long> {

    List<Contract> findContractsForItem(Item supplierItem);

    List<Contract> findContractsForItemAndSuppiler(Item supplierItem);
    
    public List<Contract> findContractsForSuppiler(Supplier supplier);

    @Transactional(readOnly = false)
    void updateContract(Contract c);
    
    public List<Contract> findAllContracts(final String name, final int pageNumber,final int pageSize);

    public List<Contract> findContractsForItem(Item item,Boolean isCausalPartRecovery);
}
