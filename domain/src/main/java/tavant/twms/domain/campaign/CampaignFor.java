package tavant.twms.domain.campaign;

import org.springframework.core.style.ToStringCreator;

public enum CampaignFor {
    SERIAL_NUMBERS("Serial Numbers"), 
    SERIAL_NUMBER_RANGES("Serial Number Patterns"), 
    PRODUCTS("Products");
    
    private String type;

    private CampaignFor(String compaignFor) {
        this.type = compaignFor;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("Campaign For ", type).toString();
    }
}