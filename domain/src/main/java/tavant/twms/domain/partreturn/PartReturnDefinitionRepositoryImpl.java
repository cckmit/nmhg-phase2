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
package tavant.twms.domain.partreturn;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.util.Assert;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemCriterion;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.CriteriaEvaluationPrecedence;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.BitSetValueComputer;
import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author vineeth.varghese
 * 
 */
public class PartReturnDefinitionRepositoryImpl extends
        GenericRepositoryImpl<PartReturnDefinition, Long> implements PartReturnDefinitionRepository {

    @Override
    public void save(PartReturnDefinition entity) {
        updateRelevanceScore(entity);
        super.save(entity);
    }

    @Override
    public void update(PartReturnDefinition entity) {
        updateRelevanceScore(entity);
        super.update(entity);
    }

    public List findPartReturnDefinitions(final Item forItem, final Criteria criteria) {
        Assert.notNull(forItem, "Part not specified to find part return configuration");
        return getHibernateTemplate()
                .executeFind(new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException,
                            SQLException {
                        Map<String, Object> params = new HashMap<String, Object>();
                        params.put("item", forItem);
                        ItemGroup dummyGroup = new ItemGroup();
                        dummyGroup.setId(-1L);
                        params.put("productType", criteria.getProductType() != null ? criteria
                                .getProductType() : dummyGroup);
                        DealerCriterion dealerCriterion = criteria.getDealerCriterion();

                        ServiceProvider dummyDealer = new ServiceProvider();
                        dummyDealer.setId(-1L);
                        params.put("dealer", (dealerCriterion != null)
                                && (dealerCriterion.getDealer() != null) ? dealerCriterion
                                .getDealer() : dummyDealer);

                        params.put("warrantyType", criteria.getWarrantyType() != null ? criteria
                                .getWarrantyType() : "");
                        params.put("claimType", criteria.getClaimType() != null ? criteria
                                .getClaimType() : "");
                        Query query = session.getNamedQuery("partReturnDefinitionLookupQuery");
                        if (logger.isDebugEnabled()) {
                            logger.debug(" setting parameters " + params
                                    + " for named query 'partReturnDefinitionLookupQuery' ");
                        }
                        query.setProperties(params);
                        return query.list();
                    }
                });


    }

    public PartReturnDefinition findPartReturnDefinition(final Item forItem, final Criteria criteria) {
        Assert.notNull(forItem, "Part not specified to find part return configuration");
        PartReturnDefinition partReturnDefinition = (PartReturnDefinition) getHibernateTemplate()
                .execute(new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException,
                            SQLException {
                    	Map<String, Object> params = new HashMap<String, Object>();
                        params.put("item", forItem);
                        ItemGroup dummyGroup = new ItemGroup();
                        dummyGroup.setId(-1L);
                        params.put("productType", criteria.getProductType() != null ? criteria
                                .getProductType() : dummyGroup);
                        DealerCriterion dealerCriterion = criteria.getDealerCriterion();

                        ServiceProvider dummyDealer = new ServiceProvider();
                        dummyDealer.setId(-1L);
                        params.put("dealer", (dealerCriterion != null)
                                && (dealerCriterion.getDealer() != null) ? dealerCriterion
                                .getDealer() : dummyDealer);

                        params.put("warrantyType", criteria.getWarrantyType() != null ? criteria
                                .getWarrantyType() : "");
                        params.put("claimType", criteria.getClaimType() != null ? criteria
                                .getClaimType() : "");
                        Query query = session.getNamedQuery("partReturnDefinitionLookupQuery");
                        if (logger.isDebugEnabled()) {
                            logger.debug(" setting parameters " + params
                                    + " for named query 'partReturnDefinitionLookupQuery' ");
                        }
                        query.setProperties(params);
                        query.setMaxResults(1);
                        Object uniqueResult = query.uniqueResult();
                        return uniqueResult;
                    }
                });
        return partReturnDefinition;
    }

    public PaymentCondition findPaymentConditionForCode(String code) {
        return (PaymentCondition) getHibernateTemplate().load(PaymentCondition.class, code);
    }

    @SuppressWarnings("unchecked")
    public List<PaymentCondition> findAllPaymentConditions() {
        return (List<PaymentCondition>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createCriteria(PaymentCondition.class).setCacheable(true).list();
            }
        });
    }

    public CriteriaEvaluationPrecedence findEvaluationPrecedence(final String forData) {
        if (logger.isDebugEnabled()) {
            logger.debug("findEvaluationPrecedence(" + forData + ")");
        }
        return (CriteriaEvaluationPrecedence) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException,
                            SQLException {
                        return session.createCriteria(CriteriaEvaluationPrecedence.class).add(
                                Restrictions.eq("forData", forData)).uniqueResult();
                    }
                });
    }

    public boolean isUnique(PartReturnDefinition partReturnDefinition) {
        final ItemCriterion itemCriterion = partReturnDefinition.getItemCriterion();
        final Criteria criteria = partReturnDefinition.getCriteria();
        PartReturnDefinition result = (PartReturnDefinition) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session) throws HibernateException,
                            SQLException {
                        org.hibernate.Criteria hbmCriteria = session
                                .createCriteria(PartReturnDefinition.class);
                        if (itemCriterion.isGroupCriterion()) {
                            hbmCriteria.add(Restrictions.isNull("itemCriterion.item"));
                            hbmCriteria.add(Restrictions.eq("itemCriterion.itemGroup",
                                    itemCriterion.getItemGroup()));
                        } else {
                            hbmCriteria.add(Restrictions.isNull("itemCriterion.itemGroup"));
                            hbmCriteria.add(Restrictions.eq("itemCriterion.item", itemCriterion
                                    .getItem()));
                        }

                        if (criteria.isClaimTypeSpecified()) {
                            hbmCriteria.add(Restrictions.eq("forCriteria.claimType", criteria
                                    .getClaimType()));
                        } else {
                            hbmCriteria.add(Restrictions.isNull("forCriteria.claimType"));
                        }

                        if (criteria.isWarrantyTypeSpecified()) {
                            hbmCriteria.add(Restrictions.eq("forCriteria.warrantyType", criteria
                                    .getWarrantyType()));
                        } else {
                            hbmCriteria.add(Restrictions.isNull("forCriteria.warrantyType"));
                        }

                        if (criteria.isProductTypeSpecified()) {
                            hbmCriteria.add(Restrictions.eq("forCriteria.productType", criteria
                                    .getProductType()));
                        } else {
                            hbmCriteria.add(Restrictions.isNull("forCriteria.productType"));
                        }

                        if (criteria.isDealerGroupSpecified()) {
                            hbmCriteria.add(Restrictions.eq(
                                    "forCriteria.dealerCriterion.dealerGroup", criteria
                                            .getDealerCriterion().getDealerGroup()));
                        } else {
                            hbmCriteria.add(Restrictions
                                    .isNull("forCriteria.dealerCriterion.dealerGroup"));
                        }

                        if (criteria.isDealerSpecified()) {
                            hbmCriteria.add(Restrictions.eq("forCriteria.dealerCriterion.dealer",
                                    criteria.getDealerCriterion().getDealer()));
                        } else {
                            hbmCriteria.add(Restrictions
                                    .isNull("forCriteria.dealerCriterion.dealer"));
                        }
                        // Avoiding a session flush at this point.
                        hbmCriteria.setFlushMode(FlushMode.COMMIT);
                        return hbmCriteria.uniqueResult();
                    }
                });
        return result == null ? true : (partReturnDefinition.getId() == result.getId());
    }

    void updateRelevanceScore(PartReturnDefinition entity) {
        BitSetValueComputer bitSetValueComputer = new BitSetValueComputer();
        ItemCriterion itemCriterion = entity.getItemCriterion();
        Criteria forCriteria = entity.getForCriteria();
        DealerCriterion dealerCriterion = forCriteria.getDealerCriterion();
        boolean[] bits = new boolean[] { itemCriterion.getItem() != null,
                itemCriterion.getItemGroup() != null,
                (dealerCriterion != null) && (dealerCriterion.getDealer() != null),
                (dealerCriterion != null) && (dealerCriterion.getDealerGroup() != null),
                forCriteria.getClaimType() != null, forCriteria.getWarrantyType() != null,
                forCriteria.getProductType() != null };
        forCriteria.setRelevanceScore(bitSetValueComputer.compute(bits));
    }

}