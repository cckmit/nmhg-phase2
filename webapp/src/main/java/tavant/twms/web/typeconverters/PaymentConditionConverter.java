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
package tavant.twms.web.typeconverters;

import org.springframework.util.StringUtils;

import tavant.twms.domain.partreturn.PartReturnService;
import tavant.twms.domain.partreturn.PaymentCondition;

/**
 * @author vineeth.varghese
 * 
 */
public class PaymentConditionConverter extends
        ValidatableDomainObjectConverter<PartReturnService, PaymentCondition> {

    public PaymentConditionConverter() {
        super("partReturnService");
    }

    @Override
    public String getName(PaymentCondition entity) throws Exception {
        return entity.getCode();
    }

    @Override
    public PaymentCondition fetchByName(String code) throws Exception {
    	if(StringUtils.hasText(code)){
        return getService().findPaymentCondition(code);
    	}else{
    		return null;
    	}
    }

}
