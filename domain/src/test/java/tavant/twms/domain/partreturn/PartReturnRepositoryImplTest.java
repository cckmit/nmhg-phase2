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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemCriterion;
import tavant.twms.domain.catalog.ItemGroupRepository;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.Duration;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealerGroupRepository;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.time.CalendarDate;

public class PartReturnRepositoryImplTest extends DomainRepositoryTestCase {

    PartReturnDefinitionRepository partReturnDefinitionRepository;

    CatalogService catalogService;

    LocationRepository locationRepository;

    ItemGroupRepository itemGroupRepository;

    DealershipRepository dealershipRepository;

    DealerGroupRepository dealerGroupRepository;

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
    }

    public void testSave_ForItem() throws Exception {
        PartReturnDefinition definition = new PartReturnDefinition();
        definition.setItemCriterion(new ItemCriterion(this.catalogService.findItemOwnedByManuf("PRTHOSE1")));
        PartReturnConfiguration partReturnConfiguration = new PartReturnConfiguration();
        partReturnConfiguration.setDueDays(10);
        partReturnConfiguration.setPaymentCondition(new PaymentCondition("PAY_ON_RETURN"));
        partReturnConfiguration.setReturnLocation(this.locationRepository
                .findByLocationCode("Ohio"));
        Duration duration = new Duration(CalendarDate.date(2001, 1, 1), CalendarDate.date(2010, 12,
                31));
        partReturnConfiguration.setDuration(duration);
        definition.addPartReturnConfiguration(partReturnConfiguration);

        this.partReturnDefinitionRepository.save(definition);
        assertNotNull(definition.getId());
        assertEquals(64L, definition.getForCriteria().getRelevanceScore());
    }

    public void testSave_ForItemGroup() throws Exception {
        PartReturnDefinition definition = new PartReturnDefinition();
        definition.setItemCriterion(new ItemCriterion(this.itemGroupRepository.findById(3L)));
        PartReturnConfiguration partReturnConfiguration = new PartReturnConfiguration();
        partReturnConfiguration.setDueDays(10);
        partReturnConfiguration.setPaymentCondition(new PaymentCondition("PAY_ON_RETURN"));
        partReturnConfiguration.setReturnLocation(this.locationRepository
                .findByLocationCode("Ohio"));
        Duration duration = new Duration(CalendarDate.date(2001, 1, 1), CalendarDate.date(2010, 12,
                31));
        partReturnConfiguration.setDuration(duration);
        definition.addPartReturnConfiguration(partReturnConfiguration);
        this.partReturnDefinitionRepository.save(definition);
        assertNotNull(definition.getId());
        assertEquals(32L, definition.getForCriteria().getRelevanceScore());
    }

    public void testSave_ForItemAndDealer() throws Exception {
        PartReturnDefinition definition = new PartReturnDefinition();
        definition.setItemCriterion(new ItemCriterion(this.catalogService.findItemOwnedByManuf("PRTHOSE1")));
        definition.getForCriteria().setDealerCriterion(
                new DealerCriterion(this.dealershipRepository.findByDealerId(7L)));
        PartReturnConfiguration partReturnConfiguration = new PartReturnConfiguration();
        partReturnConfiguration.setDueDays(10);
        partReturnConfiguration.setPaymentCondition(new PaymentCondition("PAY_ON_RETURN"));
        partReturnConfiguration.setReturnLocation(this.locationRepository
                .findByLocationCode("Ohio"));
        Duration duration = new Duration(CalendarDate.date(2001, 1, 1), CalendarDate.date(2010, 12,
                31));
        partReturnConfiguration.setDuration(duration);
        definition.addPartReturnConfiguration(partReturnConfiguration);

        this.partReturnDefinitionRepository.save(definition);
        assertNotNull(definition.getId());
        assertEquals(80L, definition.getForCriteria().getRelevanceScore());
    }

    public void testSave_ForItemAndDealerGroup() throws Exception {
        PartReturnDefinition definition = new PartReturnDefinition();
        definition.setItemCriterion(new ItemCriterion(this.catalogService.findItemOwnedByManuf("PRTHOSE1")));
        definition.getForCriteria().setDealerCriterion(
                new DealerCriterion(this.dealerGroupRepository.findById(2L)));
        PartReturnConfiguration partReturnConfiguration = new PartReturnConfiguration();
        partReturnConfiguration.setDueDays(10);
        partReturnConfiguration.setPaymentCondition(new PaymentCondition("PAY_ON_RETURN"));
        partReturnConfiguration.setReturnLocation(this.locationRepository
                .findByLocationCode("Ohio"));
        Duration duration = new Duration(CalendarDate.date(2001, 1, 1), CalendarDate.date(2010, 12,
                31));
        partReturnConfiguration.setDuration(duration);
        definition.addPartReturnConfiguration(partReturnConfiguration);

        this.partReturnDefinitionRepository.save(definition);
        assertNotNull(definition.getId());
        assertEquals(72L, definition.getForCriteria().getRelevanceScore());
    }

    public void testFindPartReturnDefintionById() {
        PartReturnDefinition definition = this.partReturnDefinitionRepository.findById(new Long(2));
        assertNotNull(definition.getConfigurations());
        assertEquals(definition.getConfigurations().size(), 1);
    }

    public void testDefaultConfiguration_ItemMatch() {
        Item _COUGAR50HZ3 = (Item) getSession().get(Item.class, 39L);
        Criteria criteria = new Criteria();
        criteria.setProductType(_COUGAR50HZ3.getProduct());
        criteria.setClaimType("Parts");
        criteria.setWarrantyType("GOODWILL");
        criteria.setDealerCriterion(new DealerCriterion(this.dealershipRepository
                .findByDealerId(21L)));
        PartReturnDefinition _prd = this.partReturnDefinitionRepository.findPartReturnDefinition(
                _COUGAR50HZ3, criteria);
        PartReturnConfiguration prc = _prd
                .findConfigurationFor(CalendarDate.date(2006, 6, 6), true);
        assertNotNull(prc);
        assertEquals(8L, prc.getId().longValue());
    }

    public void testDefaultConfiguration_BusinessUnitAware(){
    	Item _COUGAR50HZ3 = (Item) getSession().get(Item.class, 39L); 
        Criteria criteria = new Criteria();
        criteria.setProductType(_COUGAR50HZ3.getProduct());
        criteria.setClaimType("Campaign");
        criteria.setWarrantyType("STANDARAD");
        criteria.setDealerCriterion(new DealerCriterion(this.dealershipRepository
                .findByDealerId(21L)));
        PartReturnDefinition prd = new PartReturnDefinition();
        prd.setForCriteria(criteria);
        prd.setItemCriterion(new ItemCriterion(_COUGAR50HZ3));
		PartReturnConfiguration partReturnConfiguration = new PartReturnConfiguration();
        partReturnConfiguration.setDueDays(10);
        partReturnConfiguration.setPaymentCondition(new PaymentCondition("PAY_ON_RETURN"));
        partReturnConfiguration.setReturnLocation(this.locationRepository
                .findByLocationCode("Ohio"));
        Duration duration = new Duration(CalendarDate.date(2001, 1, 1), CalendarDate.date(2010, 12,
                31));
        partReturnConfiguration.setDuration(duration);
        List<PartReturnConfiguration> partReturnConfigurations = new ArrayList<PartReturnConfiguration>();
        prd.setConfigurations(partReturnConfigurations);
        partReturnDefinitionRepository.save(prd);
        assertNotNull(prd.getId());
        assertNotNull(prd.getBusinessUnitInfo());
        assertEquals("IR",prd.getBusinessUnitInfo().getName());
        flushAndClear();
        PartReturnDefinition partReturnDefinition = this.partReturnDefinitionRepository.findPartReturnDefinition(
                _COUGAR50HZ3, criteria);
        assertNotNull(partReturnDefinition.getBusinessUnitInfo());
        assertEquals("IR",partReturnDefinition.getBusinessUnitInfo().getName());
    }

    public void testDefaultConfiguration_BestItemGroupMatch() {
        Item _COUGAR60HZ1 = (Item) getSession().get(Item.class, 39L);
        Criteria criteria = new Criteria();
        criteria.setProductType(_COUGAR60HZ1.getProduct());
        criteria.setClaimType("Parts");
        criteria.setWarrantyType("GOODWILL");
        criteria.setDealerCriterion(new DealerCriterion(this.dealershipRepository
                .findByDealerId(21L)));
        PartReturnDefinition _prd = this.partReturnDefinitionRepository.findPartReturnDefinition(
                _COUGAR60HZ1, criteria);
        PartReturnConfiguration prc = _prd
                .findConfigurationFor(CalendarDate.date(2006, 6, 6), true);
        assertNotNull(prc);
        assertEquals(8L, prc.getId().longValue());
    }

    public void testDefaultConfiguration_NextBestItemGroupMatch() {
        Item _DRYER50HZ1 = (Item) getSession().get(Item.class, 39L);
        Criteria criteria = new Criteria();
        criteria.setProductType(_DRYER50HZ1.getProduct());
        criteria.setClaimType("Parts");
        criteria.setWarrantyType("GOODWILL");
        criteria.setDealerCriterion(new DealerCriterion(this.dealershipRepository
                .findByDealerId(21L)));
        PartReturnDefinition _prd = this.partReturnDefinitionRepository.findPartReturnDefinition(
                _DRYER50HZ1, criteria);
        PartReturnConfiguration prc = _prd
                .findConfigurationFor(CalendarDate.date(2006, 6, 6), true);
        assertNotNull(prc);
        assertEquals(8L, prc.getId().longValue());
    }

    public void testProductConfiguration_ItemMatch() {
        Item _COUGAR50HZ3 = (Item) getSession().get(Item.class, 39L);
        Criteria criteria = new Criteria();
        criteria.setClaimType("Parts");
        criteria.setWarrantyType("GOODWILL");
        criteria.setDealerCriterion(new DealerCriterion(this.dealershipRepository
                .findByDealerId(21L)));
        criteria.setProductType(_COUGAR50HZ3.getProduct());
        PartReturnDefinition _prd = this.partReturnDefinitionRepository.findPartReturnDefinition(
                _COUGAR50HZ3, criteria);
        PartReturnConfiguration prc = _prd
                .findConfigurationFor(CalendarDate.date(2006, 6, 6), true);
        assertNotNull(prc);
        assertEquals(8L, prc.getId().longValue());
    }

    public void testProductMatch_BestItemGroupMatch() {
        Item _COUGAR60HZ1 = (Item) getSession().get(Item.class, 39L);
        Criteria criteria = new Criteria();
        criteria.setClaimType("Parts");
        criteria.setWarrantyType("GOODWILL");
        criteria.setDealerCriterion(new DealerCriterion(this.dealershipRepository
                .findByDealerId(21L)));
        criteria.setProductType(_COUGAR60HZ1.getProduct());
        PartReturnDefinition _prd = this.partReturnDefinitionRepository.findPartReturnDefinition(
                _COUGAR60HZ1, criteria);
        PartReturnConfiguration prc = _prd
                .findConfigurationFor(CalendarDate.date(2006, 6, 6), true);
        assertNotNull(prc);
        assertEquals(8L, prc.getId().longValue());
    }

    public void testProduct_NextBestItemGroupMatch() {
        Item _DRYER50HZ1 = (Item) getSession().get(Item.class, 39L);
        Criteria criteria = new Criteria();
        criteria.setClaimType("Parts");
        criteria.setWarrantyType("GOODWILL");
        criteria.setDealerCriterion(new DealerCriterion(this.dealershipRepository
                .findByDealerId(21L)));
        criteria.setProductType(_DRYER50HZ1.getProduct());
        PartReturnDefinition _prd = this.partReturnDefinitionRepository.findPartReturnDefinition(
                _DRYER50HZ1, criteria);
        PartReturnConfiguration prc = _prd
                .findConfigurationFor(CalendarDate.date(2006, 6, 6), true);
        assertNotNull(prc);
        assertEquals(8L, prc.getId().longValue());
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void setPartReturnDefinitionRepository(
            PartReturnDefinitionRepository partReturnDefinitionRepository) {
        this.partReturnDefinitionRepository = partReturnDefinitionRepository;
    }

    public void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * @param dealerGroupRepository the dealerGroupRepository to set
     */

    @Required
    public void setDealerGroupRepository(DealerGroupRepository dealerGroupRepository) {
        this.dealerGroupRepository = dealerGroupRepository;
    }

    /**
     * @param dealershipRepository the dealershipRepository to set
     */
    @Required
    public void setDealershipRepository(DealershipRepository dealershipRepository) {
        this.dealershipRepository = dealershipRepository;
    }

    /**
     * @param itemGroupRepository the itemGroupRepository to set
     */
    @Required
    public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
        this.itemGroupRepository = itemGroupRepository;
    }
}