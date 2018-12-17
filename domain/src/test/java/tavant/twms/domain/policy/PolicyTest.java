package tavant.twms.domain.policy;

import java.util.TimeZone;

import junit.framework.TestCase;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryType;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class PolicyTest extends TestCase {
    static {
        Clock.setDefaultTimeZone( TimeZone.getDefault() );
        Clock.timeSource();
    }
    
    public void testIsStillAvailableFor_Yes() throws PolicyException {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setType( InventoryType.RETAIL );
        inventoryItem.setHoursOnMachine(9);
        RegisteredPolicy policy = new RegisteredPolicy();
        CalendarDate today = Clock.today();
        CalendarDate tomorrow = today.nextDay();
        CalendarDate yesterday = today.previousDay();
        CalendarDuration duration = new CalendarDuration(yesterday,tomorrow);
        policy.setWarrantyPeriod(duration);

        inventoryItem.setDeliveryDate(today);
        inventoryItem.setShipmentDate(yesterday);
        
        PolicyDefinition policyDefinition =new PolicyDefinition();
        CoverageTerms coverageTerms = new CoverageTerms();
        coverageTerms.setServiceHoursCovered(10);
        policyDefinition.setCoverageTerms(coverageTerms);
        
        policy.setPolicyDefinition(policyDefinition);
        
        assertTrue(policy.isStillAvailableFor(inventoryItem));
    }

    public void testIsStillAvailableFor_No_HoursExpired() throws PolicyException {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setType( InventoryType.RETAIL );
        inventoryItem.setHoursOnMachine(12);
        RegisteredPolicy policy = new RegisteredPolicy();
        CalendarDate today = Clock.today();
        CalendarDate tomorrow = today.nextDay();
        CalendarDate yesterday = today.previousDay();
        CalendarDuration duration = new CalendarDuration(yesterday,tomorrow);
        policy.setWarrantyPeriod(duration);
        
        PolicyDefinition policyDefinition =new PolicyDefinition();
        CoverageTerms coverageTerms = new CoverageTerms();
        coverageTerms.setServiceHoursCovered(10);
        policyDefinition.setCoverageTerms(coverageTerms);
        
        policy.setPolicyDefinition(policyDefinition);
        
        assertFalse(policy.isStillAvailableFor(inventoryItem));
    }    
    
    public void testIsStillAvailableFor_No_TimeExpired() throws PolicyException {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setType( InventoryType.RETAIL );
        inventoryItem.setHoursOnMachine(9);
        RegisteredPolicy policy = new RegisteredPolicy();
        CalendarDate today = Clock.today();
        CalendarDate yesterday = today.previousDay();
        CalendarDuration duration = new CalendarDuration(yesterday.previousDay(),yesterday);
        policy.setWarrantyPeriod(duration);
        
        PolicyDefinition policyDefinition =new PolicyDefinition();
        CoverageTerms coverageTerms = new CoverageTerms();
        coverageTerms.setServiceHoursCovered(10);
        policyDefinition.setCoverageTerms(coverageTerms);
        
        policy.setPolicyDefinition(policyDefinition);
        
        assertFalse(policy.isStillAvailableFor(inventoryItem));
    }    
}
