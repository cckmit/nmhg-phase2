/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.orgmodel;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.springframework.core.style.ToStringCreator;


/**
 * @author aniruddha.chaturvedi
 *
 */
@Embeddable
public class DealerCriterion {

    @ManyToOne(fetch=FetchType.LAZY)
    private ServiceProvider dealer;
    

    @ManyToOne(fetch=FetchType.LAZY)
    private DealerGroup dealerGroup;
    
    public DealerCriterion() {
    }

    public DealerCriterion(DealerGroup dealerGroup) {
        this.dealerGroup = dealerGroup;
    }

    public DealerCriterion(ServiceProvider dealer) {
        this.dealer = dealer;
    }
    
    public boolean isGroupCriterion() {
        return dealerGroup != null;
    }

    /**
     * Gets the dealer name or the group name.
     * @return
     */
    public String getIdentifier() {
        if (isGroupCriterion()) {
            return dealerGroup.getName();
        } else {
            return dealer.getName();
        }
    }

    public Object getIdentifierObject() {
        if (isGroupCriterion()) {
            return dealerGroup;
        } else {
            return dealer;
        }
    }

    public DealerGroup getDealerGroup() {
        return dealerGroup;
    }

    public void setDealerGroup(DealerGroup dealerGroup) {
        this.dealerGroup = dealerGroup;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("dealer", dealer).append("dealer group", dealerGroup).toString();
    }
    
    public boolean isSpecified() {
    	return dealer!=null || dealerGroup!=null;
    }

	public ServiceProvider getDealer() {
		return dealer;
	}

	public void setDealer(ServiceProvider dealer) {
		this.dealer = dealer;
	}
}