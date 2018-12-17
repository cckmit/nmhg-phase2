package tavant.twms.domain.policy;

import java.util.TimeZone;

import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

public class WarrantyTest extends DomainRepositoryTestCase {

    private WarrantyService warrantyService;

    static {
        Clock.setDefaultTimeZone(TimeZone.getDefault());
        Clock.timeSource();
    }

    public WarrantyService getWarrantyService() {
        return this.warrantyService;
    }

    public void setWarrantyService(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    public void testGetEndDate() {
        Warranty fixture = new Warranty();

        CalendarDate today = Clock.today();
        CalendarDate tomorrow = today.nextDay();
        CalendarDate dayAfterTomorrow = tomorrow.nextDay();

        CalendarDuration warrantyPeriod1 = new CalendarDuration(today, today);
        RegisteredPolicy policy = new RegisteredPolicy() {

            @Override
            public boolean isStillAvailableFor(InventoryItem inventoryItem) {
                return true;
            }

        };
        PolicyDefinition policyDefinition = new PolicyDefinition();
        policy.setPolicyDefinition(policyDefinition);
        policy.setWarrantyPeriod(warrantyPeriod1);
        fixture.getPolicies().add(policy);

        CalendarDuration warrantyPeriod2 = new CalendarDuration(today, tomorrow);
        policy = new RegisteredPolicy() {

            @Override
            public boolean isStillAvailableFor(InventoryItem inventoryItem) {
                return true;
            }

        };

        policyDefinition = new PolicyDefinition();
        policy.setPolicyDefinition(policyDefinition);
        policy.setWarrantyPeriod(warrantyPeriod2);
        fixture.getPolicies().add(policy);

        CalendarDuration warrantyPeriod3 = new CalendarDuration(today, dayAfterTomorrow);
        policy = new RegisteredPolicy() {

            @Override
            public boolean isStillAvailableFor(InventoryItem inventoryItem) {
                return true;
            }

        };

        policyDefinition = new PolicyDefinition();
        policy.setPolicyDefinition(policyDefinition);
        policy.setWarrantyPeriod(warrantyPeriod3);
        fixture.getPolicies().add(policy);

        CalendarDuration warrantyPeriod4 = new CalendarDuration(today, dayAfterTomorrow.plusDays(4));
        policy = new RegisteredPolicy() {

            @Override
            public boolean isStillAvailableFor(InventoryItem inventoryItem) {
                return false;
            }

        };
        policyDefinition = new PolicyDefinition();
        policy.setPolicyDefinition(policyDefinition);
        policy.setWarrantyPeriod(warrantyPeriod4);
        fixture.getPolicies().add(policy);

        assertEquals(dayAfterTomorrow.plusDays(4), fixture.getEndDate());
    }

    public void testRegister() {
        Warranty fixture = new Warranty();
        PolicyDefinition aPolicyDefinition = new PolicyDefinition();
        CalendarDuration calendarDuration = new CalendarDuration(Clock.today(), Clock.today());
        RegisteredPolicy registeredPolicy = this.warrantyService.register(fixture,
                aPolicyDefinition, calendarDuration, Money.dollars(200), null, null, null);
        assertNotNull(registeredPolicy);

        assertEquals(registeredPolicy, fixture.getPolicies().iterator().next());
        assertEquals(calendarDuration, registeredPolicy.getWarrantyPeriod());
    }
}
