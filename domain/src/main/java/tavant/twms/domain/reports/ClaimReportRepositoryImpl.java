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
package tavant.twms.domain.reports;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.domain.claim.Claim;

import com.domainlanguage.timeutil.Clock;

/**
 * @author bibin.jacob
 *
 */
public class ClaimReportRepositoryImpl extends HibernateDaoSupport implements ClaimReportRepository {
    // TODO:Replace Transformers.aliasToBean to Map
    private static final String FIND_ALL_DUE_PART_RETURNS_QUERY = "findAllDuePartReturnsQuery";

    private static final String FIND_PART_RETURNS_QUERY = "findPartReturnsQuery";

    private static final String FIND_DEALERS_COUNT_QUERY = "findDealersCountQuery";

    private static final String FIND_CLAIMS_FOR_PROCESSING_EFFICIENCY_QUERY = "findClaimsForProcessingEfficiencyQuery";

    private static final String FIND_SUPPLIER_RECOVERY_QUERY = "findSupplierRecoveryQuery";

    private static final String FIND_CLAIMS_PER_PRODUCT_QUERY = "findClaimsPerProductQuery";

    private static final String WARRANTY_PAYOUT_MONTH_QUERY = "warrantyPayoutForMonthQuery";

    private static final String WARRANTY_PAYOUT_QUARTER_QUERY = "warrantyPayoutForQuarterQuery";

    private static final String TAX_AMOUNT_QUERY = "findTaxAmountQuery";

    /**
     * Find all claims given dealer, startDate,endDate
     *
     * @param ReportSearchCriteria
     * @return List Claim
     */
    @SuppressWarnings("unchecked")
    public List<Claim> findAllClaimsForCriteria(final ReportSearchCriteria reportSearchCriteria) {
        return (List<Claim>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                StringBuffer query = new StringBuffer(
                        "from Claim claim where claim.forDealer.id in (:ids)");
                query
                        .append(" and claim.filedOnDate >= :startDate and claim.filedOnDate <= :endDate ");
                return session.createQuery(query.toString()).setParameter("startDate",
                        reportSearchCriteria.getStartDate()).setParameter("endDate",
                        reportSearchCriteria.getEndDate()).setParameterList("ids",
                        reportSearchCriteria.getSelectedDealers()).list();
            }

        });
    }

    /**
     * Find all claims between system date and systemdate-12months
     *
     * @return List Claim
     */

    @SuppressWarnings("unchecked")
    public List findClaimsForProcessingEfficiency() {
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(FIND_CLAIMS_FOR_PROCESSING_EFFICIENCY_QUERY);
                query.setParameter("startDate", Clock.today().plusMonths(-12));
                query.setParameter("endDate", Clock.today());
                query.setResultTransformer(Transformers.aliasToBean(ReportVO.class));
                return query.list();
            }

        });
    }

    /**
     * Find dealers count
     *
     * @return Long count
     */

    @SuppressWarnings("unchecked")
    public List findDealersCount(final ReportSearchCriteria reportSearchCriteria) {
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(FIND_DEALERS_COUNT_QUERY);
                query.setParameter("startDate", reportSearchCriteria.getStartDate());
                query.setParameter("endDate", reportSearchCriteria.getEndDate());
                query.setParameterList("ids", reportSearchCriteria.getSelectedDealers());
                query.setResultTransformer(Transformers.aliasToBean(SubReportVO.class));
                return query.list();
            }
        });
    }

    /**
     * Find all PartReturns
     *
     * @return List PartReturns
     */

    @SuppressWarnings("unchecked")
    public List findPartReturns(final ReportSearchCriteria reportSearchCriteria) {
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(FIND_PART_RETURNS_QUERY);
                query.setParameter("startDate", reportSearchCriteria.getStartDate());
                query.setParameter("endDate", reportSearchCriteria.getEndDate());
                query.setParameterList("ids", reportSearchCriteria.getSelectedDealers());
                query.setResultTransformer(Transformers.aliasToBean(SubReportVO.class));
                return query.list();
            }

        });
    }

    /**
     * Find all Due PartReturns
     *
     * @return List PartReturns
     */

    @SuppressWarnings("unchecked")
    public List findAllDuePartReturns(final ReportSearchCriteria reportSearchCriteria) {
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(FIND_ALL_DUE_PART_RETURNS_QUERY);
                query.setParameterList("ids", reportSearchCriteria.getSelectedDealers());
                query.setResultTransformer(Transformers.aliasToBean(SubReportVO.class));
                return query.list();
            }
        });
    }

    /**
     * Find supplier recovery
     *
     * @return List CostLineItem
     *
     * TBD
     */

     @SuppressWarnings("unchecked")
     public List findSupplierRecovery(final ReportSearchCriteria reportSearchCriteria) {
    	 return (List) getHibernateTemplate().execute(new HibernateCallback() {
	    	public Object doInHibernate(Session session) throws HibernateException, SQLException {
			     Query query = session.getNamedQuery(FIND_SUPPLIER_RECOVERY_QUERY);
			     query.setParameter("startDate", reportSearchCriteria.getStartDate());
			     query.setParameter("endDate", reportSearchCriteria.getEndDate());
			     query.setParameterList("ids", reportSearchCriteria.getSelectedSuppliers());
		         query.setResultTransformer(Transformers.aliasToBean(SubReportVO.class));
		         return query.list();
	         }
    	   });
	    }

    /**
     * Find Claims By Product
     *
     * @return List claims
     */
    @SuppressWarnings("unchecked")
    public List findClaimsByProduct(final ReportSearchCriteria reportSearchCriteria) {
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(FIND_CLAIMS_PER_PRODUCT_QUERY);
                query.setParameter("startDate", reportSearchCriteria.getStartDate());
                query.setParameter("endDate", reportSearchCriteria.getEndDate());
                query.setResultTransformer(Transformers.aliasToBean(SubReportVO.class));
                return query.list();
            }
        });
    }

    /**
     * Find Claims By Fault
     *
     * @return List claims
     */
    // TODO:Remove later
    @SuppressWarnings("unchecked")
    public List findClaimsByFaultNEW(final ReportSearchCriteria reportSearchCriteria) {
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                String modelFault = "child.name,claim.activeClaimAudit.serviceInformation.faultCode";
                String faultModel = "claim.activeClaimAudit.serviceInformation.faultCode,child.name";
                String orderBy = "modelFault".equalsIgnoreCase(reportSearchCriteria.getOrderBy()) ? modelFault
                        : faultModel;
                StringBuffer query = new StringBuffer("select");
                query
                        .append(" child.id,child.name as modelName,claim.activeClaimAudit.serviceInformation.faultCode as faultName,count(item) as modelCount,");
                query
                        .append(" sum(claim.activeClaimAudit.payment.totalAmount.amount) as modelSum	from Claim claim ,Item item,ItemGroup parent,");
                query
                        .append(" ItemGroup child where item.product = child and parent.itemGroupType ='PRODUCT'");
                query
                        .append(" and child.itemGroupType ='MODEL' and parent.nodeInfo.lft < child.nodeInfo.lft and");
                query.append(" child.nodeInfo.rgt < parent.nodeInfo.rgt");
                query
                        .append(" and claim.filedOnDate >= :startDate and claim.filedOnDate <= :endDate ");
                query.append(" and UPPER(claim.activeClaimAudit.state.state) !='DRAFT' ");
                query.append(" group by child.id,child.name,claim.activeClaimAudit.serviceInformation.faultCode ");
                query.append(" order by ");
                query.append(orderBy);
                return session.createQuery(query.toString()).setParameter("startDate",
                        reportSearchCriteria.getStartDate()).setParameter("endDate",
                        reportSearchCriteria.getEndDate()).setResultTransformer(
                        Transformers.aliasToBean(SubReportVO.class)).list();
            }
        });
    }

    public List findClaimsByFault(final ReportSearchCriteria reportSearchCriteria) {
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                String modelFault = "item.model.name,claim.activeClaimAudit.serviceInformation.faultCodeRef.definition.code";
                String faultModel = "claim.activeClaimAudit.serviceInformation.faultCodeRef.definition.code,item.model.name";
                String orderBy = "modelFault".equalsIgnoreCase(reportSearchCriteria.getOrderBy()) ? modelFault
                        : faultModel;
                StringBuffer query = new StringBuffer("select");
                query
                        .append(" item.model.name as modelName,claim.activeClaimAudit.serviceInformation.faultCodeRef.definition.code as faultName,count(item) as modelCount,");
                query
                        .append(" sum(claim.payment.totalAmount.amount) as modelSum from Claim claim left outer join claim.claimedItems as claimedItem "
                                + " left outer join claimedItem.itemReference.unserializedItem as item");
                query
                        .append(" where claim.filedOnDate >= :startDate and claim.filedOnDate <= :endDate ");
                query.append(" and UPPER(claim.activeClaimAudit.state.state) !='DRAFT' ");
                query
                        .append(" group by item.model.name,claim.activeClaimAudit.serviceInformation.faultCodeRef.definition.code ");
                query.append(" order by ");
                query.append(orderBy);
                return session.createQuery(query.toString()).setParameter("startDate",
                        reportSearchCriteria.getStartDate()).setParameter("endDate",
                        reportSearchCriteria.getEndDate()).setResultTransformer(
                        Transformers.aliasToBean(SubReportVO.class)).list();
            }
        });
    }

    /**
     * Find Warranty Payout
     *
     * @return List claims
     */
    @SuppressWarnings("unchecked")
    public List findWarrantyPayout(final ReportSearchCriteria reportSearchCriteria) {
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery("month".equalsIgnoreCase(reportSearchCriteria
                        .getGroupBy()) ? WARRANTY_PAYOUT_MONTH_QUERY
                        : WARRANTY_PAYOUT_QUARTER_QUERY);
                query.setParameter("curYear", Clock.today().breachEncapsulationOf_year());
                query.setParameter("lastYear", Clock.today().plusMonths(-12)
                        .breachEncapsulationOf_year());
                query.setResultTransformer(Transformers.aliasToBean(SubReportVO.class));
                return query.list();
            }
        });
    }

    /**
     * Find Tax Amount
     *
     * @return List claims
     */
    @SuppressWarnings("unchecked")
    public List findTaxAmount() {
        return (List) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                Query query = session.getNamedQuery(TAX_AMOUNT_QUERY);
                query.setParameter("curYear", Clock.today().breachEncapsulationOf_year());
                query.setParameter("lastYear", Clock.today().plusMonths(-12)
                        .breachEncapsulationOf_year());
                query.setResultTransformer(Transformers.aliasToBean(SubReportVO.class));
                return query.list();
            }
        });
    }
}
