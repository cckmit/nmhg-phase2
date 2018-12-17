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
package tavant.twms.domain.policy;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;

import tavant.twms.domain.common.GlobalConfiguration;

import com.domainlanguage.money.Money;

/**
 * @author radhakrishnan.j
 * 
 */
@Embeddable
public class TransferDetails {
    private Boolean transferable = Boolean.FALSE;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "transfer_fee_amt"), @Column(name = "transfer_fee_curr") })
    private Money transferFee;
   
    @Column(name="window_period")
    private Long windowPeriod;
    
    @Column(name="max_transfer")
    private Long maxTransfer;

    public TransferDetails() {
        GlobalConfiguration config = GlobalConfiguration.getInstance();
        this.transferFee = config.zeroInBaseCurrency();
    }

    public Boolean isTransferable() {
        return this.transferable;
    }

    public void setTransferable(Boolean transferable) {
        this.transferable = transferable;
    }

    public Money getTransferFee() {
        return this.transferFee;
    }

    public void setTransferFee(Money transferFee) {
        this.transferFee = transferFee;
    }

	public Long getWindowPeriod() {
		return this.windowPeriod;
	}

	public void setWindowPeriod(Long windowPeriod) {
		this.windowPeriod = windowPeriod;
	}

	public Long getMaxTransfer() {
		return this.maxTransfer;
	}

	public void setMaxTransfer(Long maxTransfer) {
		this.maxTransfer = maxTransfer;
	}

    public Boolean getTransferable() {
        return transferable;
    }
}