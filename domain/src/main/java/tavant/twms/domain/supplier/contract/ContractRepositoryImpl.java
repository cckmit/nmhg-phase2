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

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author kannan.ekanath
 */
public class ContractRepositoryImpl extends GenericRepositoryImpl<Contract, Long> implements
        ContractRepository {

    @SuppressWarnings("unchecked")
    public List<Contract> findContractsForItem(Item supplierItem) {
        return getHibernateTemplate()
                .find("select c from Contract c join c.itemsCovered as item where item = ?",
                        supplierItem);
    }

    @SuppressWarnings("unchecked")
    public List<Contract> findContractsForItemAndSuppiler(Item supplierItem) {
        return getHibernateTemplate()
                .find(
                        "select c from Contract c join c.itemsCovered as item where item = ? and c.supplier = ?",
                        new Object[] { supplierItem, supplierItem.getOwnedBy() });
    }
    
    @SuppressWarnings("unchecked")
    public List<Contract> findContractsForSuppiler(Supplier supplier) {
		return getHibernateTemplate().find(
				"select c from Contract c where c.supplier = ?", supplier);
	}

    public void updateContract(Contract c) {
        getHibernateTemplate().saveOrUpdate(c);
    }
    
    
    @SuppressWarnings("unchecked")
	public List<Contract> findAllContracts(final String name, final int pageNumber,
            final int pageSize){
    	return (List<Contract>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from Contract where lower(name) like :name").setParameter(
                        "name", name + "%").setFirstResult(pageSize * pageNumber).setMaxResults(
                        pageSize).list();
            };
        });

	}
    @SuppressWarnings("unchecked")
    public List<Contract> findContractsForItem(Item item, Boolean isCausalPartRecovery) {
        return getHibernateTemplate().find("select c from Contract c join c.itemsCovered as item where item = ? " +
                "and c.recoveryBasedOnCausalPart = ?",
                new Object[] {item,isCausalPartRecovery});
    }

}
