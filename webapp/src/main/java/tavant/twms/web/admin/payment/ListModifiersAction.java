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
package tavant.twms.web.admin.payment;

import com.opensymphony.xwork2.Preparable;
import org.springframework.util.Assert;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentModifierRepository;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentVariable;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.web.admin.ListCriteriaAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.List;

/**
 * 
 * @author Kiran.Kollipara
 */
@SuppressWarnings("serial")
public class ListModifiersAction extends ListCriteriaAction implements Preparable {

    private PaymentModifierRepository paymentModifierRepository;

    private String paymentVariableId;
    private PaymentVariable paymentVariable;

    public void prepare() {
        Assert.hasText(paymentVariableId, "Payment Variable cannot be null or empty");
        Long idToBeUsed = new Long(paymentVariableId);
        paymentVariable = paymentModifierRepository.findPaymentVariableByPK(idToBeUsed);
    }
    
    @Override
	protected PageResult<?> getBody() {
        return paymentModifierRepository.findPage(getCriteria(), paymentVariable.getId());
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		List<SummaryTableColumn> header = super.getCriteriaHeader(paymentVariable.getName());
		header.add(new SummaryTableColumn("columnTitle.managePayment.paymentModifier",
				"id", 0, "String", "id", true, true, true, false));
		header.add(new SummaryTableColumn(
                "columnTitle.common.customer", "customerType", 20, "String"));

		return header;
	}

    public void setPaymentModifierRepository(PaymentModifierRepository paymentModifierRepository) {
        this.paymentModifierRepository = paymentModifierRepository;
    }

    public void setPaymentVariableId(String paymentVariableId) {
        this.paymentVariableId = paymentVariableId;
    }

	public String getPaymentVariableId() {
		return paymentVariableId;
	}

	@Override
	protected String getAlias() {
		return "config";
	}
}