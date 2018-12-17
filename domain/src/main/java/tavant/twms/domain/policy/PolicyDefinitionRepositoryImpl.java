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
package tavant.twms.domain.policy;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.ServiceProviderCertificationStatus;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

/**
 * @author radhakrishnan.j
 *
 */
public class PolicyDefinitionRepositoryImpl extends GenericRepositoryImpl<PolicyDefinition, Long> implements PolicyDefinitionRepository {

    @SuppressWarnings("unchecked")
    public PageResult<PolicyDefinition> findAll(final ListCriteria listCriteria) {
    	return findPage("from PolicyDefinition policyDefinition",listCriteria);
    }

    @SuppressWarnings("unchecked")
    public List<PolicyDefinition> findPoliciesForProduct(ItemGroup product,CalendarDate asOfDate) {
    	return findPoliciesForProductCode(product.getName(),asOfDate);
    }

    public List<PolicyDefinition> findPoliciesFor(Item item,CalendarDate asOfDate) {
        String itemNumber = item.getNumber();
        return findPoliciesForItem(itemNumber,asOfDate);
    }

    @SuppressWarnings("unchecked")
    public List<PolicyDefinition> findPoliciesForInventory(InventoryItem inventoryItem, CalendarDate asOfDate,
    		String customerType, ServiceProvider forDealer, Boolean invisibleFilingDr) {
        Map<String,Object> params = new HashMap<String,Object>();
        ServiceProviderCertificationStatus serviceProviderCertificationStatus;
        params.put("id", inventoryItem.getId());
        params.put("itemCondition", inventoryItem.getConditionType().getItemCondition());
        params.put("asOfDate", asOfDate);
        params.put("serviceProvider", forDealer);
         if(forDealer.getCertified()){
        	 serviceProviderCertificationStatus = ServiceProviderCertificationStatus.CERTIFIED;
         }
        else{
        	serviceProviderCertificationStatus = ServiceProviderCertificationStatus.NOTCERTIFIED;
         }
        params.put("status", serviceProviderCertificationStatus);
        params.put("invisibleFilingDr", invisibleFilingDr);
       
        if(inventoryItem.getBuiltOn()!= null){
        	params.put("buildDate", inventoryItem.getBuiltOn());
        }else{
        	/*
        	 * If build date is null the application would throw an error.
        	 * To avoid we pass 1-Jan-1600.
        	 * */
        	params.put("buildDate", CalendarDate.date(1600, 01, 01));
        }
        if(customerType != null){
        	params.put("type", customerType);
            return findUsingNamedQuery("allActivePoliciesForInventoryItemAsOfDateFilterByInstallingDealer", params);
        }else{
        	return findUsingNamedQuery("allActivePoliciesForInventoryItemAsOfDate", params);
        }
       
    }
    
    public List<PolicyDefinition> findAllExtendedPolicies(boolean isInternal) {
    	Map<String,Object> params = new HashMap<String,Object>();
    	params.put("isInternal", isInternal);
    	return findUsingNamedQuery("allActiveExtendedPolicies", params);
    }
    
    @SuppressWarnings("unchecked")
    public List<PolicyDefinition> findExtendedPoliciesForInventory(String serialNumber,CalendarDate asOfDate) {
    	Map<String,Object> params = new HashMap<String,Object>();
    	params.put("serialNumber", serialNumber);
    	params.put("asOfDate", asOfDate);
    	return findUsingNamedQuery("allActiveExtendedPoliciesForSerialNumberAsOfDate", params);
    }
    
    
    @SuppressWarnings("unchecked")
    public List<PolicyDefinition> findGoodWillPoliciesForInventory(String serialNumber,CalendarDate asOfDate) {
    	Map<String,Object> params = new HashMap<String,Object>();
    	params.put("serialNumber", serialNumber);
    	params.put("asOfDate", asOfDate);
    	return findUsingNamedQuery("allActiveGoodWillPoliciesForSerialNumberAsOfDate", params);
    }

    @SuppressWarnings("unchecked")
    public List<PolicyDefinition> getAllGoodWillPolicies() {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("asOfDate", Clock.today());
        return findUsingNamedQuery("allActiveGoodWillPolicies",params);
    }



    @SuppressWarnings("unchecked")
    public List<PolicyDefinition> findExistingPoliciesForUsedItem(InventoryItem inventoryItem,CalendarDate asOfDate) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("serialNumber", inventoryItem.getSerialNumber());
        //params.put("condition", inventoryItem.getConditionType().getItemCondition());
        params.put("asOfDate", asOfDate);
        return findUsingNamedQuery("allExistingPoliciesForUsedItemAsOfDate", params);
    }

    @SuppressWarnings("unchecked")
    public List<PolicyDefinition> findTransferablePoliciesForInventoryItem(InventoryItem inventoryItem,CalendarDate asOfDate) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("serialNumber", inventoryItem.getSerialNumber());
        params.put("asOfDate", asOfDate);
        return findUsingNamedQuery("allTransferablePoliciesForInventoryItemAsOfDate", params);
    }

    @SuppressWarnings("unchecked")
    public List<PolicyDefinition> findPoliciesForItem(String itemNumber,CalendarDate asOfDate) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("itemNumber", itemNumber);
        params.put("asOfDate", asOfDate);
        return findUsingNamedQuery("allActivePoliciesForItemNumberAsOfDate", params);
    }
    
    @SuppressWarnings("unchecked")
    public List<PolicyDefinition> findInvisiblePoliciesForItem(String itemNumber,CalendarDate asOfDate) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("itemNumber", itemNumber);
        params.put("asOfDate", asOfDate);
        return findUsingNamedQuery("allActiveInvisiblePoliciesForItemNumberAsOfDate", params);
    }
    
    @SuppressWarnings("unchecked")
    public List<PolicyDefinition> findPoliciesForProductCode(String productCode,CalendarDate asOfDate) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("productCode", productCode);
        params.put("asOfDate", asOfDate);
        return findUsingNamedQuery("allActivePoliciesForProductCodeAsOfDate", params);
    }
    
    @SuppressWarnings("unchecked")
    public List<PolicyDefinition> findInvisiblePoliciesForProductCode(String productCode,CalendarDate asOfDate) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("productCode", productCode);
        params.put("asOfDate", asOfDate);
        return findUsingNamedQuery("allActiveInvisiblePoliciesForProductCodeAsOfDate", params);
    }
    
    @SuppressWarnings("unchecked")
    public List<PolicyDefinition> findApplicablePolicyForClaimedPart(String itemNumber,CalendarDate asOfDate) {
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("itemNumber", itemNumber);
        params.put("asOfDate", asOfDate);
        return findUsingNamedQuery("allApplicablePoliciesForClaimedPartAsOfDate", params);
    }

    public boolean isCodeUnique(PolicyDefinition policyDefinition) {
        List<PolicyDefinition> findEntitiesThatMatchPropertyValue = findEntitiesThatMatchPropertyValue("code", policyDefinition);
        Long id = policyDefinition.getId();
        if( id==null ) {
            return findEntitiesThatMatchPropertyValue.isEmpty();
        } else {
            return findEntitiesThatMatchPropertyValue.isEmpty() || ( findEntitiesThatMatchPropertyValue.size()==1 && id.equals(findEntitiesThatMatchPropertyValue.get(0).getId() ) );
        }
    }

	public List<PolicyDefinition> findPoliciesForAdminWarrantyReg(InventoryItem inventoryItem) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("serialNumber", inventoryItem.getSerialNumber());
		params.put("asOfDate", inventoryItem.getDeliveryDate());
		if (!inventoryItem.getSerializedPart()) {
			return findUsingNamedQuery("allActivePoliciesForAdminRegistrationForMachine", params);
		}
		else {
			return findUsingNamedQuery("allActivePoliciesForAdminRegistrationForPart", params);
		}
	}
	
	public List<PolicyDefinition> findInvisiblePoliciesForAdminWarrantyReg(
			InventoryItem inventoryItem) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serialNumber", inventoryItem.getSerialNumber());
		params.put("asOfDate", inventoryItem.getDeliveryDate());
		return findUsingNamedQuery(
				"allActiveInvisiblePoliciesForAdminRegistrationForMachine",
				params);

	}
	

	public List<PolicyDefinition> findPolicyDefinitionsForLabel(Label label) {
		String query="select pd from PolicyDefinition pd join pd.labels label where label=:label";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("label", label);
		return findUsingQuery(query, params);
	}

    @SuppressWarnings("unchecked")
	public List<String> findPolicyDefinitionCodesStartingWith(
			final String codePrefix, final int pageNumber, final int pageSize) {
		return (List<String>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select code from PolicyDefinition where code like :code")
								.setParameter("code", codePrefix + "%")
								.setFirstResult(pageSize * pageNumber)
								.setMaxResults(pageSize).list();
					};
				});
	}

	public PolicyDefinition findPolicyDefinitionByCode(final String code) {
		return (PolicyDefinition) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createQuery(
								"from PolicyDefinition where code = :code")
								.setParameter("code", code).uniqueResult();
					};
				});
	}
	
	
	public PolicyDefinition findPolicyDefinitionWithPriority(final Long priority) {
		return (PolicyDefinition) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from PolicyDefinition where priority =:priority")
								.setParameter("priority", priority).uniqueResult();
								
					};
				});
	}
	
	 @SuppressWarnings("unchecked")
	    public List<PolicyDefinition> findPoliciesAvailableForMajorCompRegistration(InventoryItem inventoryItem, CalendarDate asOfDate,
	    		String customerType, ServiceProvider forDealer) {
	        Map<String,Object> params = new HashMap<String,Object>();
	        ServiceProviderCertificationStatus serviceProviderCertificationStatus;
	        params.put("itemId", inventoryItem.getOfType().getId());
	        params.put("asOfDate", asOfDate);
		if (forDealer != null && forDealer.getId() != null) {
			params.put("serviceProvider", forDealer);
			serviceProviderCertificationStatus = ServiceProviderCertificationStatus.CERTIFIED;

		} else {
			serviceProviderCertificationStatus = ServiceProviderCertificationStatus.NOTCERTIFIED;
		}
	        params.put("status", serviceProviderCertificationStatus);
	       if(customerType != null && !customerType.isEmpty()){
	        	params.put("type", customerType);
	            return findUsingNamedQuery("allActivePoliciesForMajorComponentByCustomerType", params);
	        }else{
	        	return findUsingNamedQuery("allActivePoliciesForMajorComponent", params);
	        }
	       
	    }
	 
	 @SuppressWarnings("unchecked")
	    public List<PolicyDefinition> findGoodWillPoliciesForMajorComponent(String serialNumber,CalendarDate asOfDate) {
	    	Map<String,Object> params = new HashMap<String,Object>();
	    	params.put("serialNumber", serialNumber);
	    	params.put("asOfDate", asOfDate);
	    	return findUsingNamedQuery("allActiveGoodWillPoliciesForMajorComponent", params);
	    } 

	 public List<PolicyDefinition> findPoliciesAvailableForRegistrationUsingOptionCode(InventoryItem item, String optionCode){
		String query="select pd from PolicyDefinition pd where upper(pd.nomsPolicyOptionCode)=:optionCode " +
				"and pd.invisibleFilingDr = 0 and pd.currentlyInactive = 0 " +
				"and (:product in (select prods.product from pd.availability.products prods) or :model in (select prods.product from pd.availability.products prods)) " +
				"and pd.availability.duration.fromDate <= :deliveryDate and :deliveryDate <= pd.availability.duration.tillDate";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("optionCode", optionCode.toUpperCase());
		params.put("product", item.getOfType().getProduct().getId());
		params.put("model", item.getOfType().getModel().getId());
		params.put("deliveryDate", item.getDeliveryDate());
		return findUsingQuery(query, params);
	 }
	 
	 public List<PolicyDefinition> findPoliciesUsingAllOptionCodes(InventoryItem inventoryItem, List<String> optionCodes) {
		 String query = "select pd from PolicyDefinition pd where upper(pd.nomsPolicyOptionCode) in (:optionCodes) " +
		 		"and pd.invisibleFilingDr = 0 and pd.currentlyInactive = 0 " +
		 		"and (:product in (select prods.product from pd.availability.products prods) or :model in (select prods.product from pd.availability.products prods)) " +
		 		"and pd.availability.duration.fromDate <= :deliveryDate and :deliveryDate <= pd.availability.duration.tillDate";
		 Map<String, Object> params = new HashMap<String, Object>();
		 params.put("optionCodes", optionCodes);
		 params.put("product", inventoryItem.getOfType().getProduct().getId());
		 params.put("model", inventoryItem.getOfType().getModel().getId());
		 params.put("deliveryDate", inventoryItem.getDeliveryDate());
		 return findUsingQuery(query, params);
	 }

}
