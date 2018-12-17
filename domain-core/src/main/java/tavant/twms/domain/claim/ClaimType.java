/*
 *   Copyright (c) 2007 Tavant Technologies
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
package tavant.twms.domain.claim;

/**
 * @author vineeth.varghese
 */
public enum ClaimType {
    MACHINE("Machine"), PARTS("Parts"), CAMPAIGN("Campaign"), ALL("All"),FIELD_MODIFICATION("Field Modification"),ATTACHMENT("Attachment");

    private String type;

    private ClaimType(String type) {
        this.type = type;
    }

    public static ClaimType typeFor(String type) {
        if (MACHINE.type.equalsIgnoreCase(type)) {
            return MACHINE;
        } else if (PARTS.type.equalsIgnoreCase(type)) {
            return PARTS;
        } else if (CAMPAIGN.type.equalsIgnoreCase(type)) {
            return CAMPAIGN;
        } else if (ATTACHMENT.type.equalsIgnoreCase(type)) {
            return ATTACHMENT;
        } else if (ALL.type.equalsIgnoreCase(type)) {
            return ALL;
        } else {
            throw new IllegalArgumentException("Cannot understand the Claim Type");
        }
    }

    public String getType() {
        return this.type;
    }
    
    /**
     * @return type
     * Updated for NMHGSLMS-577:RTM-94
     * Machine should be displayed as Unit
     */
	public String getDisplayType() {
		if (this.type.equals(CAMPAIGN.getType())) {
			return "label.claimType.campaign";
		}
		 else if (this.type.equals(MACHINE.getType())) {
				return "label.claimType.machine";
			}
		 else if (this.type.equals(PARTS.getType())) {
				return "label.claimType.parts";
			}
		 else {
			return this.type;
		}
	}
    
    @Override
    public String toString() {
        return this.type;
    }

     public static ClaimType getUIDisplayName(String claimType){
		if(MACHINE.getType().equalsIgnoreCase(claimType)){
			return MACHINE;
		}
		if(PARTS.getType().equalsIgnoreCase(claimType)){
			return PARTS;
		}
		if(CAMPAIGN.getType().equalsIgnoreCase(claimType)){
			return CAMPAIGN;
		}
        if(ATTACHMENT.getType().equalsIgnoreCase(claimType)){
			return ATTACHMENT;
		}
        if(ALL.getType().equalsIgnoreCase(claimType)){
			return ALL;
		}
		return null;
	}
}
