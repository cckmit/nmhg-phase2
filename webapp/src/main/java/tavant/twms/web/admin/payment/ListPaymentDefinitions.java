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
package tavant.twms.web.admin.payment;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import tavant.twms.domain.claim.payment.definition.PaymentDefinition;
import tavant.twms.domain.claim.payment.definition.PaymentDefinitionAdminService;
import tavant.twms.domain.claim.payment.definition.PaymentDefinitionRepository;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.inbox.DefaultPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListPaymentDefinitions extends SummaryTableAction {
    protected static Logger logger = LogManager.getLogger(ListPaymentDefinitions.class); 
    
    private PaymentDefinitionAdminService paymentDefinitionAdminService;

    private PaymentDefinitionRepository paymentDefinitionRepository;

    private Map<String, String> defaultValues = new HashMap<String, String>(5);
    
    @Override
    public List<SummaryTableColumn> getHeader(){
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.managePayment.policyCategory",
        		"criteria.identifier", 50, "String"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.managePayment.claimType",
        		"criteria.clmTypeName", 50, "String"));
        tableHeadData.add(new SummaryTableColumn("columnTitle.managePayment.paymentDefiniton",
        		"id",0, "String", true, true, true, false));
        
        defaultValues.put("criteria.claimType", 
        		getText("label.common.allClaimTypes"));
        defaultValues.put("criteria.label.name", 
        		getText("label.common.allPolicyTypes"));
        
        return tableHeadData;
    }
    
   @Override
	protected BeanProvider getBeanProvider() {
		return new DefaultPropertyResolver() {

			@Override
			public Object getProperty(String propertyPath, Object root) {
				
				Object columnValue = super.getProperty(propertyPath, root);
				
				if(columnValue == null &&
						"criteria.label.name".equals(propertyPath)) {
					columnValue = super.getProperty("criteria.policyDefinition.code", root);
				}
				
				if(columnValue == null) {
					columnValue = defaultValues.get(propertyPath);
				}
				
				return columnValue;
			}
			
		};
	}

   @Override
    public PageResult<?> getBody() {
        PageSpecification pageSpecification = new PageSpecification();
        PageResult<PaymentDefinition> paymentDefinitionsPage = paymentDefinitionRepository.findPage("from PaymentDefinition ",getCriteria());
        return paymentDefinitionsPage;
    }
   
   public String detail() throws Exception {
       return SUCCESS;
   }
        
    public String preview() {
        return SUCCESS;
    }
    
    public void setPaymentDefinitionAdminService(PaymentDefinitionAdminService paymentDefinitionAdminService) {
        this.paymentDefinitionAdminService = paymentDefinitionAdminService;
    }

    public void setPaymentDefinitionRepository(PaymentDefinitionRepository paymentDefinitionRepository) {
        this.paymentDefinitionRepository = paymentDefinitionRepository;
    }

    public Object getProperty(Object item, String propertyName) {
        try {
            return PropertyUtils.getProperty(item, propertyName);
        } catch (IllegalAccessException e) {
            logger.error("Error encountered while fetching value of property"
                    + propertyName + " of object" + item, e);
        } catch (InvocationTargetException e) {
            logger.error("Error encountered while fetching value of property"
                    + propertyName + " of object" + item, e);
        } catch (NoSuchMethodException e) {
            logger.error("Error encountered while fetching value of property"
                    + propertyName + " of object" + item, e);
        }
        return null;
    }
}
