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
import java.util.Collection;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author kamal.govindraj
 */
public class JobCodeRepositoryImpl extends HibernateDaoSupport implements JobCodeRepository {

    public Job findJob(final Long jobId) {
        return (Job) getHibernateTemplate().get(Job.class, jobId);
    }

    public Job findJob(final String jobCode) {
        return (Job) getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from Job j where j.definition.code = :code")
                        .setParameter("code", jobCode).uniqueResult();
            }

        });
    }

    @SuppressWarnings("unchecked")
    public Collection<Job> findJobsStartingWith(final String jobCodePrefix) {
        return getHibernateTemplate().find("from Job j where j.definition.code like ?",
                                           jobCodePrefix + "%");
    }

    public Long saveJob(final Job job) {
        return (Long) getHibernateTemplate().save(job);
    }

    public void updateJob(final Job job) {
        getHibernateTemplate().update(job);
    }

    public void deleteJobById(final Long id) {
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                session.createQuery("delete from Job j where j.id = :id").setParameter("id", id)
                        .executeUpdate();

                return null;
            }
        });
    }

    public boolean isJobCodeAssignedToAnyClaims(final String jobCode) {
        return (Boolean) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {

                String query = "select count(*) from Claim claim "
                        + "join claim.activeClaimAudit.serviceInformation.serviceDetail.laborPerformed as laborPerformed "
                        + "where laborPerformed.jobPerformed.definition.code = :code";

                Long numJobCodesFound = (Long) session.createQuery(query).setParameter("code",
                                                                                       jobCode)
                        .uniqueResult();

                return numJobCodesFound > 0;
            }
        });
    }
}
