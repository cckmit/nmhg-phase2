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
package tavant.twms.domain.claim.payment.definition.modifiers;

import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.common.CriteriaEvaluationPrecedence;
import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author radhakrishnan.j
 *
 */
public class CriteriaBasedValueRepositoryImpl extends GenericRepositoryImpl<CriteriaEvaluationPrecedence, Long>
        implements CriteriaBasedValueRepository {
    
    private static Logger logger = LogManager.getLogger(CriteriaBasedValueRepositoryImpl.class);

    public CriteriaEvaluationPrecedence findEvaluationPrecedence(final String forData) {
        if (logger.isDebugEnabled()) {
            logger.debug("findEvaluationPrecedence(" + forData + ")");
        }
        return (CriteriaEvaluationPrecedence) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createCriteria(CriteriaEvaluationPrecedence.class)
                        .add(Expression.eq("forData", forData))
                        .uniqueResult();
            }
        });
    }
}