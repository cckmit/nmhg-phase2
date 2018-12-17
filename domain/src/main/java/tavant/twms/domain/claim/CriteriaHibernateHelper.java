package tavant.twms.domain.claim;

import java.text.MessageFormat;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import tavant.twms.domain.orgmodel.DealerCriterion;

public class CriteriaHibernateHelper {
    private static MessageFormat claimTypeExpression = new MessageFormat("{0}.claimType");

    private static MessageFormat warrantyTypeExpression = new MessageFormat("{0}.warrantyType");

    private static MessageFormat dealerGroupExpression = new MessageFormat(
            "{0}.dealerCriterion.dealerGroup");

    private static MessageFormat dealerExpression = new MessageFormat("{0}.dealerCriterion.dealer");

    private static MessageFormat productTypeExpression = new MessageFormat("{0}.productType");

    private static MessageFormat orderByProperty = new MessageFormat("{0}.relevanceScore");

    public void addCriteria(org.hibernate.Criteria hbmCriteria, Criteria criteria,
            String propertyNameOfCriteriaComponent) {

        String propertyExpression = claimTypeExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        Criterion claimTypeIsNull = Restrictions.isNull(propertyExpression);
        Criterion claimTypeIsAll = Restrictions.eq(propertyExpression, ClaimType.ALL);
        Criterion claimTypeForAll = Restrictions.or(claimTypeIsNull, claimTypeIsAll);
        if (criteria.getClaimType() != null) {
            Criterion specifiedClaimType = Restrictions.eq(propertyExpression, criteria
                    .getClaimType());
            hbmCriteria.add(Restrictions.or(claimTypeForAll, specifiedClaimType));            
        } else {
            hbmCriteria.add(claimTypeForAll);
        }

        propertyExpression = warrantyTypeExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        Criterion warrantyTypeIsNull = Restrictions.isNull(propertyExpression);
        if (criteria.getWarrantyType() != null) {
            Criterion specifiedWarrantyType = Restrictions.eq(propertyExpression, criteria
                    .getWarrantyType());
            hbmCriteria.add(Restrictions.or(specifiedWarrantyType, warrantyTypeIsNull));
        } else {
            hbmCriteria.add(warrantyTypeIsNull);
        }

        propertyExpression = dealerGroupExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        Criterion dealerGroupIsNull = Restrictions.isNull(propertyExpression);
        DealerCriterion dealerCriterion = criteria.getDealerCriterion();
        Criterion dealerGroupSpecified = Restrictions.eq(propertyExpression, dealerCriterion
                .getDealerGroup());

        propertyExpression = dealerExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        Criterion dealerIsNull = Restrictions.isNull(propertyExpression);
        Criterion dealerSpecified = Restrictions
                .eq(propertyExpression, dealerCriterion.getDealer());

        Criterion noDealerCriterion = Restrictions.and(dealerIsNull, dealerGroupIsNull);

        
        hbmCriteria.add(Restrictions.or(noDealerCriterion, Restrictions.or(
					        		Restrictions.and(dealerSpecified,dealerGroupIsNull),
					        		Restrictions.and(dealerGroupSpecified,dealerIsNull)
        																  )
		        					   )
		        	   );

        propertyExpression = productTypeExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        Criterion productTypeIsNull = Restrictions.isNull(propertyExpression);
        if (criteria.getProductType() != null) {
            Criterion productTypeSpecified = Restrictions.eq(propertyExpression, criteria
                    .getProductType());
            hbmCriteria.add(Restrictions.or(productTypeSpecified, productTypeIsNull));
        } else {
            hbmCriteria.add(productTypeIsNull);
        }
        hbmCriteria.addOrder(Order.desc(orderByProperty
                .format(new Object[] { propertyNameOfCriteriaComponent })));
    }

    public DetachedCriteria createCriteria(Class clazz, Criteria criteria,
            String propertyNameOfCriteriaComponent) {
        DetachedCriteria crit = DetachedCriteria.forClass(clazz);

        String propertyExpression = claimTypeExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        Criterion claimTypeIsNull = Restrictions.isNull(propertyExpression);
        Criterion claimTypeIsAll = Restrictions.eq(propertyExpression, ClaimType.ALL);
        Criterion claimTypeForAll = Restrictions.or(claimTypeIsNull, claimTypeIsAll);
        if (criteria.getClaimType() != null) {
            Criterion specifiedClaimType = Restrictions.eq(propertyExpression, criteria
                    .getClaimType());
            crit.add(Restrictions.or(claimTypeForAll, specifiedClaimType));
        } else {
            crit.add(claimTypeForAll);
        }

        propertyExpression = warrantyTypeExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        Criterion warrantyTypeIsNull = Restrictions.isNull(propertyExpression);
        if (criteria.getWarrantyType() != null) {
            Criterion specifiedWarrantyType = Restrictions.eq(propertyExpression, criteria
                    .getWarrantyType());
            crit.add(Restrictions.or(specifiedWarrantyType, warrantyTypeIsNull));
        } else {
            crit.add(warrantyTypeIsNull);
        }

        propertyExpression = dealerGroupExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        Criterion dealerGroupIsNull = Restrictions.isNull(propertyExpression);
        DealerCriterion dealerCriterion = criteria.getDealerCriterion();
        Criterion dealerGroupSpecified = null;
        if (dealerCriterion.isGroupCriterion()) {
            dealerGroupSpecified = Restrictions.eq(propertyExpression, dealerCriterion
                    .getDealerGroup());
        }

        propertyExpression = dealerExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        Criterion dealerIsNull = Restrictions.isNull(propertyExpression);
        Criterion dealerSpecified = Restrictions
                .eq(propertyExpression, dealerCriterion.getDealer());

        Criterion noDealerCriterion = Restrictions.and(dealerIsNull, dealerGroupIsNull);

        LogicalExpression dealerOrDealerGroupSpecified = null;
        if (dealerGroupSpecified != null) {
            dealerOrDealerGroupSpecified = Restrictions.or(dealerSpecified, dealerGroupSpecified);
        } else {
            dealerOrDealerGroupSpecified = Restrictions.and(dealerSpecified, dealerGroupIsNull);
        }
        crit.add(Restrictions.or(noDealerCriterion, dealerOrDealerGroupSpecified));

        propertyExpression = productTypeExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        Criterion productTypeIsNull = Restrictions.isNull(propertyExpression);
        if (criteria.getProductType() != null) {
            Criterion productTypeSpecified = Restrictions.eq(propertyExpression, criteria
                    .getProductType());
            crit.add(Restrictions.or(productTypeSpecified, productTypeIsNull));
        } else {
            crit.add(productTypeIsNull);
        }
        crit.addOrder(Order.desc(orderByProperty
                .format(new Object[] { propertyNameOfCriteriaComponent })));
        return crit;
    }

    public DetachedCriteria createCriteriaForUniqueCheck(Class clazz, Criteria criteria,
            String propertyNameOfCriteriaComponent) {
        DetachedCriteria crit = DetachedCriteria.forClass(clazz);
        String propertyExpression = claimTypeExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        Criterion claimTypeIsNull = Restrictions.isNull(propertyExpression);
        Criterion claimTypeIsAll = Restrictions.eq(propertyExpression, ClaimType.ALL);
        Criterion claimTypeForAll = Restrictions.or(claimTypeIsNull, claimTypeIsAll);
        if (criteria.getClaimType() == null) {
            crit.add(claimTypeForAll);
        } else {
            crit.add(Restrictions.eq(propertyExpression, criteria.getClaimType()));
        }

        propertyExpression = warrantyTypeExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        if (criteria.getWarrantyType() == null) {
            crit.add(Restrictions.isNull(propertyExpression));
        } else {
            crit.add(Restrictions.eq(propertyExpression, criteria.getWarrantyType()));
        }

        propertyExpression = dealerGroupExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        Criterion dealerGroupIsNull = Restrictions.isNull(propertyExpression);
        DealerCriterion dealerCriterion = criteria.getDealerCriterion();

        propertyExpression = dealerExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        Criterion dealerIsNull = Restrictions.isNull(propertyExpression);
        Criterion noDealerCriterion = Restrictions.and(dealerIsNull, dealerGroupIsNull);

        if (criteria.getDealerCriterion() == null) {
            crit.add(noDealerCriterion);
        } else {
            if (criteria.getDealerCriterion().isGroupCriterion()) {
                Criterion dealerGroupSpecified = Restrictions.eq(propertyExpression,
                                                                 dealerCriterion.getDealerGroup());
                crit.add(Restrictions.and(dealerIsNull, dealerGroupSpecified));
            } else {
                Criterion dealerSpecified = Restrictions.eq(propertyExpression, dealerCriterion
                        .getDealer());
                crit.add(Restrictions.and(dealerSpecified, dealerGroupIsNull));
            }
        }

        propertyExpression = productTypeExpression
                .format(new Object[] { propertyNameOfCriteriaComponent });
        if (criteria.getProductType() == null) {
            crit.add(Restrictions.isNull(propertyExpression));
        } else {
            crit.add(Restrictions.eq(propertyExpression, criteria.getProductType()));
        }
        return crit;
    }
}