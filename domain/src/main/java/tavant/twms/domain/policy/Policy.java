package tavant.twms.domain.policy;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.rules.RuleExecutionTemplate;

import com.domainlanguage.time.CalendarDate;

public interface Policy {
    public abstract boolean isApplicable(Claim claim, RuleExecutionTemplate ruleExecutionTemplate);

    public abstract String getCode();

    public abstract String getDescription();

    public abstract CalendarDuration getWarrantyPeriod();

    public abstract boolean covers(ClaimedItem claimedItem, Integer serviceHoursCovered) throws PolicyException;
    
    public abstract boolean covers(Claim claim, Integer serviceHoursCovered) throws PolicyException;

    public abstract boolean isStillAvailableFor(InventoryItem inventoryItem) throws PolicyException;

    public abstract boolean isAvailable(InventoryItem inventoryItem, CalendarDate asOfDate)
            throws PolicyException;

    public WarrantyType getWarrantyType();

    PolicyDefinition getPolicyDefinition();

    // Added just for convinence to work at the interface level
    Long getId();

	public abstract boolean covers(ClaimedItem claimedItem,
			CalendarDuration warrantyPeriod, Integer serviceHoursCovered);
}
