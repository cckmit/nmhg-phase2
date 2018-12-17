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
package tavant.twms.fit.policy;

import ognl.Ognl;

import org.springframework.util.StringUtils;

import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.payment.CostCategory;
import tavant.twms.domain.claim.payment.CostCategoryRepository;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionService;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.fit.infra.BeanWiredColumnFixture;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
@SuppressWarnings("hiding")
public class CreatePolicy extends BeanWiredColumnFixture {
    private PolicyDefinitionService policyDefinitionService;
    private CatalogService catalogService;
    private CostCategoryRepository costCategoryRepository;

    public String code;
    public String description;
    public String warrantyType;
    public String priority;
    public String availableFrom;
    public String availableTill;
    public String serviceHoursCovered;
    public String monthsCoveredFromShipment;
    public String monthsCoveredFromRegistration;
    public String costCategoriesCovered;
    public String productsCovered;
    public boolean policyCreated;
    
    public void setPolicyDefinitionService(PolicyDefinitionService policyDefinitionService) {
        this.policyDefinitionService = policyDefinitionService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }
    
    public void setCostCategoryRepository(CostCategoryRepository costCategoryRepository) {
        this.costCategoryRepository = costCategoryRepository;
    }

    public boolean isPolicyCreated() {
        return policyCreated;
    }

    @Override
    public void execute() throws Exception {
        PolicyDefinition policyDefinition = new PolicyDefinition();
        Ognl.setValue("code",policyDefinition,code);
        policyDefinition.setDescription(description);
        Ognl.setValue("priority",policyDefinition,priority);
        policyDefinition.setWarrantyType(new WarrantyType(warrantyType));
        
        CalendarDuration calendarDuration = new CalendarDuration();    
        calendarDuration.setFromDate(CalendarDate.from(availableFrom,"M/d/yyyy"));
        calendarDuration.setTillDate(CalendarDate.from(availableTill,"M/d/yyyy"));
        policyDefinition.getAvailability().setDuration(calendarDuration);
        
        String[] productCodeorNames = StringUtils.commaDelimitedListToStringArray(productsCovered);
        for (int i = 0; i < productCodeorNames.length; i++) {
            String productCodeOrName = productCodeorNames[i];
            ItemGroup productType = catalogService.findItemGroupByName(productCodeOrName);
            policyDefinition.getAvailability().getProducts().add(productType);
        }
        
        
        
        Ognl.setValue("coverageTerms.serviceHoursCovered",policyDefinition,serviceHoursCovered);
        Ognl.setValue("coverageTerms.monthsCoveredFromRegistration",policyDefinition,monthsCoveredFromRegistration);
        Ognl.setValue("coverageTerms.monthsCoveredFromShipment",policyDefinition,monthsCoveredFromShipment);
        
        String[] costCategoryCodes = StringUtils.commaDelimitedListToStringArray(costCategoriesCovered);
        for (int i = 0; i < costCategoryCodes.length; i++) {
            String costCategoryCode = costCategoryCodes[i];
            CostCategory costCategory = costCategoryRepository.findCostCategoryByCode(costCategoryCode);
//            policyDefinition.getCoverageTerms().g
        }
        
        policyDefinitionService.save(policyDefinition);
        policyCreated = policyDefinition.getId()!=null;
    }
}
