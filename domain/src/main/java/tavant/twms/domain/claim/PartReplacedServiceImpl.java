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
package tavant.twms.domain.claim;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.domain.partreturn.PartReturnAudit;
import tavant.twms.domain.uom.UomMappings;

public class PartReplacedServiceImpl extends HibernateDaoSupport implements PartReplacedService {

    public NonOEMPartReplaced findNonOEMPartReplacedById(Long id) {
        return (NonOEMPartReplaced) getHibernateTemplate().load(NonOEMPartReplaced.class, id);
    }

    public OEMPartReplaced findOEMPartReplacedById(Long id) {
        return (OEMPartReplaced) getHibernateTemplate().load(OEMPartReplaced.class, id);
    }

    public Claim getClaimForOEMPartReplaced(final OEMPartReplaced oemPartReplaced) {
        return (Claim) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "from Claim claim where :part in elements(claim.activeClaimAudit.serviceInformation.serviceDetail.oemPartsReplaced)")
                        .setParameter("part", oemPartReplaced).uniqueResult();
            }
        });
    }

    /*
     * Since RecoveryClaim is now made only for causal part in club car its not
     * required For club car
     * 
     * 
     */
    public void updateOEMPartReplaced(OEMPartReplaced oemPartReplaced) {
        getHibernateTemplate().update(oemPartReplaced);

    }
    
    /**
     * API to set UOM Mappings of replaced parts to null when an UOM mapping is getting deleted
     */
    @SuppressWarnings("unchecked")
	public void updateUOMMappingsOfOEMReplacedParts(final UomMappings uomMapping) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.createQuery("update OEMPartReplaced set uomMapping = null  "
								+ " where uomMapping = :uomMapping ");
				query.setParameter("uomMapping", uomMapping);
				return query.executeUpdate();
			}
		});
	}

    public void updatePartAudit(PartReturnAudit audit) {
        getHibernateTemplate().saveOrUpdate(audit);

    }
}
