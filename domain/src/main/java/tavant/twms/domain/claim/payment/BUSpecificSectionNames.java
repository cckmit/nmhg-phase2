package tavant.twms.domain.claim.payment;

import java.util.HashMap;
import java.util.Map;

import tavant.twms.domain.claim.payment.definition.Section;

public interface BUSpecificSectionNames {

	public static final Map<String, String> NAMES_AND_KEY = new HashMap<String, String>() {
        {
            put(Section.OEM_PARTS, "label.section.replacedParts");
            put(Section.NON_OEM_PARTS, "label.section.nonReplacedParts");
            put(Section.LABOR, "label.section.labor");
            put(Section.TRAVEL_BY_DISTANCE, "label.section.travelByDistance");
            put(Section.TRAVEL_BY_TRIP, "label.section.travelByTrip");
            put(Section.TRAVEL_BY_HOURS,"label.section.travelByHours");
            put(Section.MEALS, "label.section.meals");
            put(Section.PARKING, "label.section.parking");
            put(Section.ITEM_FREIGHT_DUTY, "label.section.freight");
            put(Section.TOTAL_CLAIM, "label.section.claimAmount");
            put(Section.MISCELLANEOUS_PARTS, "label.section.miscParts");
            put(Section.PER_DIEM, "label.section.perDiem");
            put(Section.RENTAL_CHARGES, "label.section.rentalCharges");
            put(Section.ADDITIONAL_TRAVEL_HRS, "label.section.addnTravelHrs");
            put(Section.LOCAL_PURCHASE, "label.section.localPurchase");
            put(Section.TOLLS, "label.section.tolls");
            put(Section.OTHER_FREIGHT_DUTY, "label.section.otherFreightAndDuty");
            put(Section.OTHERS, "label.section.others");
            put(Section.HANDLING_FEE,"label.section.handlingFee");
            put(Section.TRANSPORTATION_COST, "label.section.transportation");
            put(Section.TRAVEL,"label.section.travel");
            put(Section.LATE_FEE,"label.section.lateFee");
            put(Section.DEDUCTIBLE,"label.common.payment.deductable");
        }
    };
    
   
}
