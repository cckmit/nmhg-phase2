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
package tavant.twms.domain.claim.payment;

import org.springframework.core.style.ToStringCreator;

class AmountName {
    String businessTerm;
    String modelTerm;
    
    AmountName(String businessName) {
        super();
        this.businessTerm = businessName;
        modelTerm = businessTerm.replaceAll("\\s", "").toLowerCase();
    }

    String getBusinessTerm() {
        return businessTerm;
    }
    
    String getModelTerm() {
        return modelTerm;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((modelTerm == null) ? 0 : modelTerm.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AmountName other = (AmountName) obj;
        if (modelTerm == null) {
            if (other.modelTerm != null)
                return false;
        } else if (!modelTerm.equals(other.modelTerm))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return new ToStringCreator(this)
            .append("businessTerm", businessTerm)
            .append("modelTerm", modelTerm)
            .toString();
    }
}
