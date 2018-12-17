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
package tavant.twms.infra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemRepository;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.WarrantyRepository;
import tavant.twms.domain.policy.WarrantyType;

import com.domainlanguage.timeutil.Clock;

@SuppressWarnings("unchecked")
public class GenericRepositoryImplTest extends DomainRepositoryTestCase {

    private GenericRepositoryImpl fixture;

    private InventoryItemRepository inventoryItemRepository;

    private WarrantyRepository warrantyRepository;

    private WarrantyType warrantyType;

    private PolicyDefinition policyDefinition;

    static {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
    }

    public void setInventoryItemRepository(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public void setWarrantyRepository(WarrantyRepository warrantyRepository) {
        this.warrantyRepository = warrantyRepository;
    }

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        this.fixture = new TestRepositoryImpl();
        this.fixture.setSessionFactory(this.sessionFactory);

        this.warrantyType = this.warrantyRepository.listWarrantyTypes().iterator().next();

        this.policyDefinition = new PolicyDefinition();
        this.policyDefinition.setDescription("testSave");
        this.policyDefinition.setCode("someCode");
        this.policyDefinition.setWarrantyType(this.warrantyType);
        this.policyDefinition.getAvailability().setDuration(
                new CalendarDuration(Clock.today(), Clock.today()));
        this.policyDefinition.getCoverageTerms().setServiceHoursCovered(10);
        this.policyDefinition.getCoverageTerms().setMonthsCoveredFromDelivery(10);
        this.policyDefinition.getCoverageTerms().setMonthsCoveredFromShipment(12);
        this.policyDefinition.setPriority(new Long(1));
    }

    public void testGenericRepositoryImpl() {
        assertEquals(PolicyDefinition.class, this.fixture.getEntityType());
    }

    public void testDelete() {
        this.fixture.save(this.policyDefinition);
        flushAndClear();
        assertNotNull(this.policyDefinition.getId());
        assertNotNull(this.fixture.findById(this.policyDefinition.getId()));
        this.fixture.delete(this.fixture.findById(this.policyDefinition.getId()));
        flushAndClear();
        assertNull(this.fixture.findById(this.policyDefinition.getId()));
    }

    public void testFindAll() {
        assertEquals(getSession().createCriteria(PolicyDefinition.class).list(), this.fixture
                .findAll());
    }

    public void testFindAllPageSpecification() {
        PageSpecification pageSpecification = new PageSpecification();
        pageSpecification.setPageSize(4);

        PageResult<PolicyDefinition> pageResult = this.fixture.findAll(pageSpecification);
        assertEquals(2, pageResult.getNumberOfPagesAvailable());
        assertEquals(4, pageResult.getResult().size());
    }

    public void testFindById() {
        assertEquals(getSession().get(PolicyDefinition.class, new Long(1)), this.fixture
                .findById(new Long(1)));
    }

    public void testSave() {
        this.fixture.save(this.policyDefinition);
        assertNotNull(this.policyDefinition.getId());
    }

    public void testUpdate() {
        this.fixture.save(this.policyDefinition);
        assertNotNull(this.policyDefinition.getId());

        flushAndClear();

        PolicyDefinition reloadedEntity = (PolicyDefinition) this.fixture
                .findById(this.policyDefinition.getId());
        reloadedEntity.setDescription("testUpdate");
        this.fixture.update(reloadedEntity);
        flushAndClear();

        reloadedEntity = (PolicyDefinition) this.fixture.findById(reloadedEntity.getId());
        assertEquals("testUpdate", reloadedEntity.getDescription());
    }

    public void testFindUniqueUsingQuery() {
        String query = "select pd from PolicyDefinition pd, Item item,InventoryItem inventoryItem where"
                + " inventoryItem.serialNumber=:serialNumber and"
                + " inventoryItem.ofType = item and  "
                + " item.product in elements(pd.availability.products)";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("serialNumber", "LX3742");

        Exception ex = null;
        try {
            this.fixture.findUniqueUsingQuery(query, params);
        } catch (IncorrectResultSizeDataAccessException e) {
            ex = e;
        }
        assertNotNull(ex);

    }

    public void testFindUsingQuery() {
        String query = "select pd from PolicyDefinition pd, Item item,InventoryItem inventoryItem where"
                + " inventoryItem.serialNumber=:serialNumber and"
                + " inventoryItem.ofType = item and  "
                + " item.product in elements(pd.availability.products)";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("serialNumber", "LX3742");
        assertFalse(this.fixture.findUsingQuery(query, params).isEmpty());
    }

    public void testFindByIds() {
        List<Long> ids = new ArrayList<Long>();
        ids.add(new Long(1));
        ids.add(new Long(1));
        ids.add(new Long(2));
        ids.add(new Long(3));
        assertEquals(3, this.fixture.findByIds(ids).size());
    }

    public void testFindByIds_idPropertyNameSpecified() {
        List<Long> ids = new ArrayList<Long>();
        ids.add(new Long(1));
        ids.add(new Long(1));
        ids.add(new Long(2));
        ids.add(new Long(3));
        assertEquals(3, this.fixture.findByIds("id", ids).size());
    }

    public void testFindPage() {
        PageSpecification pageSpecification = new PageSpecification();
        pageSpecification.setPageSize(4);

        ListCriteria listCriteria = new ListCriteria();
        listCriteria.setPageSpecification(pageSpecification);
        PageResult<PolicyDefinition> pageResult = this.fixture.findPage("from PolicyDefinition",
                listCriteria);
        assertEquals(2, pageResult.getNumberOfPagesAvailable());
        assertEquals(4, pageResult.getResult().size());
    }

    public void testFetchPage() {
        PageSpecification pageSpecification = new PageSpecification();
        pageSpecification.setPageSize(4);

        PageResult<PolicyDefinition> pageResult = this.fixture.fetchPage(pageSpecification,
                getSession().createCriteria(PolicyDefinition.class));
        assertEquals(2, pageResult.getNumberOfPagesAvailable());
        assertEquals(4, pageResult.getResult().size());
    }

    public void testFindEntitiesThatMatchPropertyValue_False() {
        PolicyDefinition policyDefinition = (PolicyDefinition) this.fixture.findById(new Long(1));
        assertFalse(this.fixture.findEntitiesThatMatchPropertyValue("code", policyDefinition)
                .isEmpty());
    }

    public void testFindEntitiesThatMatchPropertyValue_True() {
        PolicyDefinition policyDefinition = new PolicyDefinition();
        policyDefinition.setCode("STD-07");
        assertFalse(this.fixture.findEntitiesThatMatchPropertyValue("code", policyDefinition)
                .isEmpty());
        policyDefinition.setCode("STD-08");
        assertTrue(this.fixture.findEntitiesThatMatchPropertyValue("code", policyDefinition)
                .isEmpty());
    }

    public void testFindEntitiesThatMatchPropertyValues_False() throws ItemNotFoundException {
        GenericRepositoryImpl fixture = new InventoryRepositoryImpl();
        fixture.setSessionFactory(this.sessionFactory);
        InventoryItem inventoryItem = this.inventoryItemRepository.findSerializedItem("LX3742");
        Set<String> properties = new HashSet<String>();
        properties.add("serialNumber");
        properties.add("ofType");
        properties.add("conditionType");
        assertTrue(fixture.findEntitiesThatMatchPropertyValues(properties, inventoryItem).size() == 1);
    }

    public void testAreValuesUnique_True() throws ItemNotFoundException {
        GenericRepositoryImpl fixture = new InventoryRepositoryImpl();
        fixture.setSessionFactory(this.sessionFactory);
        InventoryItem inventoryItem1 = this.inventoryItemRepository.findSerializedItem("LX3742");

        InventoryItem inventoryItem = new InventoryItem();
        Item item = inventoryItem.getOfType();

        inventoryItem.setOfType(item);
        inventoryItem.setSerialNumber("testAreValuesUnique_True");
        inventoryItem.setConditionType(inventoryItem1.getConditionType());

        Set<String> properties = new HashSet<String>();
        properties.add("serialNumber");
        properties.add("ofType");
        properties.add("conditionType");
        List findEntitiesThatMatchPropertyValues = fixture.findEntitiesThatMatchPropertyValues(
                properties, inventoryItem);
        assertTrue(findEntitiesThatMatchPropertyValues.size() == 0);
    }

    static interface TestRepository extends GenericRepository<PolicyDefinition, Long> {
    }

    static class TestRepositoryImpl extends GenericRepositoryImpl<PolicyDefinition, Long> implements
            TestRepository {
    }

    static interface InventoryRepository extends GenericRepository<InventoryItem, Long> {
    }

    static class InventoryRepositoryImpl extends GenericRepositoryImpl<InventoryItem, Long>
            implements InventoryRepository {
    }
}
