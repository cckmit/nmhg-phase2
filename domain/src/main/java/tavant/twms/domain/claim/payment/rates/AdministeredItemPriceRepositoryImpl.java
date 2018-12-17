/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.claim.payment.rates;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemCriterion;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.CriteriaHibernateHelper;
import tavant.twms.domain.common.CriteriaEvaluationPrecedence;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.BitSetValueComputer;
import tavant.twms.infra.GenericRepositoryImpl;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
public class AdministeredItemPriceRepositoryImpl extends GenericRepositoryImpl<AdministeredItemPrice,Long> implements
        AdministeredItemPriceRepository {
     
	private CriteriaHibernateHelper criteriaHibernateHelper;
	
	
	 
     /**
	 * @param criteriaHibernateHelper the criteriaHibernateHelper to set
	 */
	@Required
	public void setCriteriaHibernateHelper(
			CriteriaHibernateHelper criteriaHibernateHelper) {
		this.criteriaHibernateHelper = criteriaHibernateHelper;
	}

	@Override
	public void save(AdministeredItemPrice entity) {
		updateRelevanceScore(entity);
		super.save(entity);
	}

	@Override
	public void update(AdministeredItemPrice entity) {
		updateRelevanceScore(entity);
		super.update(entity);
	}

	@SuppressWarnings("unchecked")
     public ItemPriceModifier findPriceModifier(final Item forItem, final Criteria criteria,
             CriteriaEvaluationPrecedence evalPrecedence, final CalendarDate asOfDate) {
        ItemPriceModifier configurationItem = (ItemPriceModifier)getHibernateTemplate().execute(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query hbmQuery = session.getNamedQuery("itemPriceModifierLookupQuery");
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("item", forItem);
				
				ItemGroup dummyGroup = new ItemGroup();
				dummyGroup.setId(-1L);
				params.put("productType", criteria.getProductType()!=null ? criteria.getProductType() : dummyGroup );
				DealerCriterion dealerCriterion = criteria.getDealerCriterion();
				
				ServiceProvider dummyDealer = new ServiceProvider();
				dummyDealer.setId(-1L);
				params.put("dealer", dealerCriterion!=null ? dealerCriterion.getDealer() : dummyDealer );
				params.put("warrantyType", criteria.getWarrantyType()!=null ? criteria.getWarrantyType() : "" );
				params.put("claimType", criteria.getClaimType()!=null ? criteria.getClaimType() : "" );
				params.put("asOfDate",asOfDate);
				hbmQuery.setProperties(params);
				hbmQuery.setMaxResults(1);
				return hbmQuery.uniqueResult();
			}
    	});
        return configurationItem;
     }
     
     @SuppressWarnings("unchecked")
     public AdministeredItemPrice findItemPrice(final ItemCriterion itemCriterion, final Criteria criteria) {
         return (AdministeredItemPrice) getHibernateTemplate().execute(new HibernateCallback() {
             public Object doInHibernate(Session session) throws HibernateException, SQLException {
 				DetachedCriteria hbmCriteria = criteriaHibernateHelper.createCriteriaForUniqueCheck(AdministeredItemPrice.class,criteria,"forCriteria");            	 
                org.hibernate.Criteria executableCriteria = hbmCriteria.getExecutableCriteria(session);
                
				//Item based match.
				Criterion itemGroupIsNull = Expression.isNull("itemCriterion.itemGroup");
				Criterion itemIsNull = Expression.isNull("itemCriterion.item");
				if (itemCriterion.isGroupCriterion()) {
					executableCriteria.add( Expression.and(itemIsNull,Expression.eq("itemCriterion.itemGroup",itemCriterion.getItemGroup())));
				} else {
					executableCriteria.add( Expression.and(itemGroupIsNull,Expression.eq("itemCriterion.item",itemCriterion.getItem())));
				}

				Object result = executableCriteria.uniqueResult();
				return result;
			}
         });
     }
     
 	void updateRelevanceScore(AdministeredItemPrice entity) {
		BitSetValueComputer bitSetValueComputer = new BitSetValueComputer();
    	ItemCriterion itemCriterion = entity.getItemCriterion();
		Criteria forCriteria = entity.getForCriteria();
		boolean[] bits = new boolean[] {
    			itemCriterion.getItem()!=null,
    			itemCriterion.getItemGroup()!=null,
    			forCriteria.getWarrantyType()!=null
    	};
		forCriteria.setRelevanceScore( bitSetValueComputer.compute(bits) );
	}     
}
