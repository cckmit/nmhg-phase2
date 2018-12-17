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
package tavant.twms.web.admin;

import tavant.twms.domain.common.CriteriaElement;
import tavant.twms.domain.common.CriteriaEvaluationPrecedence;
import tavant.twms.domain.common.CriteriaEvaluationPrecedenceRepository;
import tavant.twms.infra.BeanProvider;
import tavant.twms.web.inbox.DefaultPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Kiran.Kollipara
 */
public abstract class ListCriteriaAction extends SummaryTableAction {

    private Map<String, SummaryTableColumn> definitionColumnMap;

    private Map<String, String> defaultValues;

    private CriteriaEvaluationPrecedenceRepository criteriaEvaluationPrecedenceRepository;

    public ListCriteriaAction() {
        super();
    }

    private void initializeDefaultColumns() {
        this.definitionColumnMap = new HashMap<String, SummaryTableColumn>();
        this.definitionColumnMap.put("dealerCriterion", new SummaryTableColumn(
                "columnTitle.common.dealerCriterion", "forCriteria.identifier", 20,
                "String"));
       
        this.definitionColumnMap.put("productType", new SummaryTableColumn(
                "columnTitle.common.productType", "forCriteria.productName", 20, "String"));
        if(isBuConfigAMER())
			this.definitionColumnMap.put("warrantyType",
					new SummaryTableColumn("columnTitle.common.warrantyType",
							"forCriteria.wntyTypeName", 20, "String", true));
        else
        	this.definitionColumnMap.put("warrantyType",
					new SummaryTableColumn("columnTitle.common.warrantyType",
							"forCriteria.wntyTypeName", 20, "String"));
        this.definitionColumnMap.put("claimType", new SummaryTableColumn(
                "columnTitle.common.claimType", "forCriteria.clmTypeName", 10, "String",true));

    }

    private void initialiseDefaultColumnValues() {
        this.defaultValues = new HashMap<String, String>();
        this.defaultValues.put("forCriteria.dealerCriterion.identifier",
                getText("label.common.allDealers"));
        this.defaultValues.put("forCriteria.productType.name",
                getText("label.common.allProductTypes"));
        this.defaultValues
                .put("forCriteria.warrantyType", getText("label.common.allWarrantyTypes"));
        this.defaultValues.put("forCriteria.claimType", getText("label.common.allClaimTypes"));
        /**
         * Added for Travel Rates and labor rates
         */
        this.defaultValues.put("customerType", getText("label.common.allCustomers"));
    }

    protected List<SummaryTableColumn> getCriteriaHeader(String criteriaName) {
    	initializeDefaultColumns();
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        CriteriaEvaluationPrecedence precedence = this.criteriaEvaluationPrecedenceRepository
                .findByName(criteriaName);
        List<CriteriaElement> properties = precedence.getProperties();
        for (CriteriaElement element : properties) {
            tableHeadData.add(this.definitionColumnMap.get(element.getPropertyExpression()));
        }
        return tableHeadData;
    }

    @Override
    protected BeanProvider getBeanProvider() {
        if (this.defaultValues == null) {
            initialiseDefaultColumnValues();
        }
        return new DefaultPropertyResolver() {
            @Override
            public Object getProperty(String propertyPath, Object root) {
                Object value = super.getProperty(propertyPath, root);
                if (value == null) {
                    value = ListCriteriaAction.this.defaultValues.get(propertyPath);
                }
                return value;
            }
        };
    }

    public void setCriteriaEvaluationPrecedenceRepository(
            CriteriaEvaluationPrecedenceRepository criteriaEvaluationPrecedenceRepository) {
        this.criteriaEvaluationPrecedenceRepository = criteriaEvaluationPrecedenceRepository;
    }
}
