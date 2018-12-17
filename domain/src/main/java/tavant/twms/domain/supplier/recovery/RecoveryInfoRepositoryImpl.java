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

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.claim.Claim;
import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author  kaustubhshobhan.b
 *
 */

public class RecoveryInfoRepositoryImpl extends GenericRepositoryImpl<RecoveryInfo,Long>
        implements RecoveryInfoRepository{

    public void saveUpdate(RecoveryInfo recoveryInfo) {
        getHibernateTemplate().saveOrUpdate(recoveryInfo);

    }
    
    public RecoveryInfo findRecoveryInfoForClaim(final Long claimId)
    {
    	return (RecoveryInfo) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session
						.createQuery(
								"from RecoveryInfo recoveryInfo where recoveryInfo.warrantyClaim.id = :claimId")
						.setParameter("claimId", claimId)
						.uniqueResult();
			}
		});
    }
}
