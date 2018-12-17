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
package tavant.twms.domain.claim.payment.definition;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.policy.Policy;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

import com.domainlanguage.time.CalendarDate;

/**
 * @author radhakrishnan.j
 *
 */
public class PaymentDefinitionAdminServiceImpl implements PaymentDefinitionAdminService {

    private static final Logger logger = Logger.getLogger(PaymentDefinitionAdminServiceImpl.class);

    private PaymentDefinitionRepository paymentDefinitionRepository;

    public List<Section> findAllSections() {
        return this.paymentDefinitionRepository.findAllSections();
    }

    public PageResult<PaymentDefinition> findAll(PageSpecification pageSpecification) {
        return this.paymentDefinitionRepository.findAll(pageSpecification);
    }

    public PaymentDefinitionRepository getPaymentDefinitionRepository() {
        return this.paymentDefinitionRepository;
    }

    public void setPaymentDefinitionRepository(
            PaymentDefinitionRepository paymentDefinitionRepository) {
        this.paymentDefinitionRepository = paymentDefinitionRepository;
    }

    public void delete(PaymentDefinition paymentDefintion) {
        this.paymentDefinitionRepository.delete(paymentDefintion);
    }

    public PaymentDefinition findById(Long id) {
        return this.paymentDefinitionRepository.findById(id);
    }

    public void save(PaymentDefinition newPaymentDefintion) {
        this.paymentDefinitionRepository.save(newPaymentDefintion);
    }

    public void update(PaymentDefinition paymentDefintion) {
        this.paymentDefinitionRepository.update(paymentDefintion);
    }

    public void saveOrUpdate(PaymentDefinition paymentDefinition) {
        this.paymentDefinitionRepository.saveOrUpdate(paymentDefinition);

    }

    public PaymentDefinition findByDate(CalendarDate forDate) {
        return this.paymentDefinitionRepository.findByDate(forDate);
    }

    public List<PaymentDefinition> findAll() {
        return this.paymentDefinitionRepository.findAll();
    }

    public PaymentDefinition findBestPaymentDefinition(Claim claim, Policy policy) {       
        PaymentDefinition paymentDefinition = this.paymentDefinitionRepository.findPaymentDefinitionForCP();
        List<PaymentDefinition> paymentDefinitions = new ArrayList<PaymentDefinition>();
		if (claim.getCommercialPolicy() && paymentDefinition!=null) {
			paymentDefinitions.add(paymentDefinition);
		} else {
			if(AdminConstants.NMHGAMER.equals(claim.getBusinessUnitInfo().getName()))
			{
				PaymentDefinition paymentDefinitionForGivenPolicy=null;
				if(policy!=null)
				{
					paymentDefinitionForGivenPolicy=this.paymentDefinitionRepository.findByPolicy(policy);
				}
				if(paymentDefinitionForGivenPolicy!=null)
				{
					paymentDefinitions.add(paymentDefinitionForGivenPolicy);
				}
				else
				{
					paymentDefinitions.addAll(this.paymentDefinitionRepository.findAll());	
				}
			}
			else
			{
			paymentDefinitions.addAll(this.paymentDefinitionRepository.findAll());
			}
			if(paymentDefinition!=null)
			paymentDefinitions.remove(paymentDefinition);
		}
        if(logger.isDebugEnabled())
        {
            logger.debug("Found " + paymentDefinitions + " definitions for repair date ["
                    + claim.getRepairDate() + "]");
        }

        int bestScore = -1;
        PaymentDefinition bestPaymentDefinition = null;
        for (PaymentDefinition p : paymentDefinitions) {
            int suitabilityScore = p.getSuitabilityScore(claim, policy);
            if (suitabilityScore > bestScore || bestPaymentDefinition == null) {
                if(logger.isDebugEnabled()) {
                    logger
                            .debug("Payment Definition [" + p
                                    + "] seems to be better than current match");
                }
                bestPaymentDefinition = p;
                bestScore = suitabilityScore;
            }
        }
        if(logger.isDebugEnabled())
        {
            logger.debug("Best payment definition is [" + bestPaymentDefinition + "]");
        }
        return bestPaymentDefinition;
    }

	public void updateAll(List<PaymentDefinition> paymentDefintions) {
		  this.paymentDefinitionRepository.updateAll(paymentDefintions);		
	}

}
