package tavant.twms.domain.WarrantyTask;

import tavant.twms.infra.*;
import tavant.twms.domain.policy.WarrantyStatus;
import tavant.twms.domain.policy.WarrantyListCriteria;
import tavant.twms.domain.orgmodel.User;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.hibernate.Session;
import org.hibernate.Query;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Aug 29, 2008
 * Time: 12:42:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class WarrantyTaskInstanceRepositoryImpl  extends GenericRepositoryImpl<WarrantyTaskInstance, Long>
		implements WarrantyTaskInstanceRepository{

    private static final Logger LOGGER = Logger
			.getLogger(WarrantyTaskInstanceRepositoryImpl.class);

    private CriteriaHelper criteriaHelper;

    @Override
	public void save(WarrantyTaskInstance warrantyTaskInstance) {
		getHibernateTemplate().saveOrUpdate(warrantyTaskInstance);
	}

	@Override
	public void update(WarrantyTaskInstance warrantyTaskInstance) {
		getHibernateTemplate().update(warrantyTaskInstance);
	}

	@Override
	public void delete(WarrantyTaskInstance warrantyTaskInstance) {
		getHibernateTemplate().delete(warrantyTaskInstance);
	}

    public WarrantyTaskInstance findById(Long id) {
		return (WarrantyTaskInstance) getHibernateTemplate().get(WarrantyTaskInstance.class, id);
	}

    @SuppressWarnings("unchecked")
    public PageResult<WarrantyTaskInstance> findWarrantiesForFolder(final WarrantyListCriteria warrantyListCriteria) {
        return (PageResult<WarrantyTaskInstance>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("status", warrantyListCriteria.getStatus());
                params.put("transactionType", warrantyListCriteria.getTransactionType());
                StringBuffer query = new StringBuffer("from WarrantyTaskInstance where status = :status ");
                if(warrantyListCriteria.getTransactionType().equals("DR"))
                	query.append("and warrantyAudit.forWarranty.transactionType.trnxTypeKey in (:transactionType,'DEMO','DR_RENTAL')");
                else
                	query.append(" and warrantyAudit.forWarranty.transactionType.trnxTypeKey = :transactionType");
                if(WarrantyStatus.DELETED.getStatus().equals(warrantyListCriteria.getStatus().getStatus())){
                    query.append(" and active = false ");
                    query.append(" and warrantyAudit.status in ('DELETED') ");
                }else{
                    query.append(" and active = true ");
                }
                if (warrantyListCriteria.getDealer() != null) {
                    query.append(" and warrantyAudit.forWarranty.forDealer = :dealerId ");
                    params.put("dealerId", warrantyListCriteria.getDealer());
                }
                if(warrantyListCriteria.isFilterCriteriaSpecified()){
                    query.append(" and (").append(warrantyListCriteria.getParamterizedFilterCriteria()).append(" )");
                }
                params.putAll(warrantyListCriteria.getParameterMap());
                return findPageUsingQuery(query.toString(),
                        warrantyListCriteria.getSortCriteriaString(),
                        warrantyListCriteria.getPageSpecification(), params);
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> fetchWarrantyFoldersForTransaction(final String transactionType,
               final boolean isAdmin, final boolean isdealer,final User filedBy) {
        return (List<Object[]>) getHibernateTemplate().execute(new HibernateCallback(){
            public Object doInHibernate(Session session) {
                StringBuffer queryString = new StringBuffer("select count(*), ti.status from WarrantyTaskInstance " +
                        " ti join ti.warrantyAudit wa join wa.forWarranty w join w.transactionType tt where " +
                        " tt.trnxTypeKey = :transactionType and ");
                if (isdealer && !isAdmin) {
                    queryString.append(" ( (ti.status in ('FORWARDED','REJECTED','DRAFT') ");
                    queryString.append("  and ti.active = true) " +
                            " or (ti.status = 'DELETED' and ti.active = false and wa.status in ('DELETED')))" +
                            " and (w.forDealer = :dealerId)");
                }
                if (isAdmin && !isdealer) {
                    queryString.append("  ti.status in ('SUBMITTED','REPLIED','RESUBMITTED') and ti.active = true ");
                }
                if (isdealer && isAdmin) {
                    queryString.append(" ((ti.status in ('SUBMITTED','REPLIED','RESUBMITTED') and ti.active = true) or " +
                            " ( (ti.status in ('FORWARDED','REJECTED','DELETED','DRAFT') " +
                            " and (w.filedBy = :filedBy " +
                            " or w.forDealer = :dealerId)) and ti.active = true) )");
                }
                queryString.append("group by ti.status");
                Query query = session.createQuery(queryString.toString());
                query.setParameter("transactionType", transactionType);
                if (isdealer && !isAdmin) {
               	 query.setParameter("dealerId", filedBy.getBelongsToOrganization());
               }
               if (isdealer && isAdmin) {
                   query.setParameter("dealerId", filedBy.getBelongsToOrganization()).
                           setParameter("filedBy", filedBy);
               }
                return query.list();
            }

            ;
        });
    }

    public WarrantyTaskInstance findActiveTaskWarranty(final String multiDRETRNumber) {
        return (WarrantyTaskInstance) getHibernateTemplate().execute(new HibernateCallback(){
           public Object doInHibernate(Session session){
               return session.createQuery("from WarrantyTaskInstance where multiDRETRNumber = :multiDRETRNumber and active = true ").
                       setParameter("multiDRETRNumber",multiDRETRNumber).uniqueResult();
           };
        });
    }

    public CriteriaHelper getCriteriaHelper() {
        return criteriaHelper;
    }

    public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
        this.criteriaHelper = criteriaHelper;
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> fetchWarrantyFoldersForDRTransaction(final String transactionType,
               final boolean isAdmin, final boolean isdealer,final User filedBy) {
        return (List<Object[]>) getHibernateTemplate().execute(new HibernateCallback(){
            public Object doInHibernate(Session session) {
                StringBuffer queryString = new StringBuffer("select count(*), ti.status from WarrantyTaskInstance " +
                        " ti join ti.warrantyAudit wa join wa.forWarranty w join w.transactionType tt where " +
                        " (tt.trnxTypeKey in (:transactionType,'DEMO','DR_RENTAL')) and ");
                if (isdealer && !isAdmin) {
                    queryString.append(" ( (ti.status in ('FORWARDED','REJECTED','DRAFT') ");
                    queryString.append("  and ti.active = true) " +
                            " or (ti.status = 'DELETED' and ti.active = false and wa.status in ('DELETED')))" +
                            " and (w.forDealer = :dealerId)");
                }
                if (isAdmin && !isdealer) {
                    queryString.append("  ti.status in ('SUBMITTED','REPLIED','RESUBMITTED') and ti.active = true ");
                }
                if (isdealer && isAdmin) {
                    queryString.append(" ((ti.status in ('SUBMITTED','REPLIED','RESUBMITTED') and ti.active = true) or " +
                            " ( (ti.status in ('FORWARDED','REJECTED','DELETED','DRAFT') " +
                            " and (w.filedBy = :filedBy " +
                            " or w.forDealer = :dealerId)) and ti.active = true) )");
                }
                queryString.append("group by ti.status");
                Query query = session.createQuery(queryString.toString());
                query.setParameter("transactionType", transactionType);
                if (isdealer && !isAdmin) {
                	 query.setParameter("dealerId", filedBy.getBelongsToOrganization());
                }
                if (isdealer && isAdmin) {
                    query.setParameter("dealerId", filedBy.getBelongsToOrganization()).
                            setParameter("filedBy", filedBy);
                }
                return query.list();
            }

            ;
        });
    }
}
