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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.payment.BUSpecificSectionNames;
import tavant.twms.domain.claim.payment.definition.PaymentDefinitionRepository;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentModifierAdminService;
import tavant.twms.domain.claim.payment.definition.modifiers.PaymentVariable;
import tavant.twms.domain.common.CriteriaElement;
import tavant.twms.domain.common.CriteriaEvaluationPrecedence;
import tavant.twms.domain.common.I18NModifierName;
import tavant.twms.infra.i18n.ProductLocale;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.web.i18n.I18nActionSupport;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;

/**
 * @author Kiran.Kollipara
 *
 */
@SuppressWarnings("serial")
public class ManageVariablesAction extends I18nActionSupport implements Preparable, Validateable, BUSpecificSectionNames {

    private Logger logger = Logger.getLogger(ManageVariablesAction.class);

    private List<PaymentVariable> paymentVariables;

    private PaymentModifierAdminService paymentModifierAdminService;

    /*private String newVariableName;*/
    
    private List<Section> sections = new ArrayList<Section>();
    
    private PaymentVariable paymentVariable; 
    
    private PaymentDefinitionRepository paymentDefinitionRepository;       
    
    private ProductLocaleService productLocaleService;
    
    private List<ProductLocale> locales;
     
    private Long paymentVariableId;
    
    private boolean isDeleted;
    
    
	@Override
    public void validate() {
        if (StringUtils.hasText(paymentVariable.getName())) {
            PaymentVariable variable = paymentModifierAdminService.findPaymentVariableByName(paymentVariable.getName());
            if (variable != null) {
                addActionError("error.managePayment.duplicateModifier");
            }
        } else {
            addActionError("error.managePayment.nonEmptyVariableName");
        }
        if(!StringUtils.hasText(paymentVariable.getSection().getName())){
            addActionError("error.managePayment.sectionRequired");
        }
    }

    public void prepare() throws Exception {
    	locales = productLocaleService.findAll();
        paymentVariables =paymentModifierAdminService.sortModifiersBasedOnName(paymentModifierAdminService.findAllPaymentVariables()) ;
        sections = paymentDefinitionRepository.findAllSections();
    }

    public String listVariables() {
/*        paymentVariables = paymentModifierAdminService.findAllPaymentVariables();
        
        if (logger.isDebugEnabled()) {
            logger.debug("Found the following payment variables [" + paymentVariables + "].");
        }
*/
    	return SUCCESS;
    }
    public void prepareModifierName(){
    	paymentVariable.getI18NModiferNames().clear();
		Map params = ActionContext.getContext().getParameters();
		for (ProductLocale locale : locales) {
			String i18nLocale = locale.getLocale();
			String localizedNames[] = (String[]) params
					.get("localizedNames_" + locale.getLocale());
			if(!StringUtils.hasText(localizedNames[0]) && i18nLocale.equals("en_US")) {
				addActionError("error.common.modifierfailureMessageUS");
				}
			else {
				I18NModifierName i18nName = new I18NModifierName();
				i18nName.setName(localizedNames[0]);
				i18nName.setLocale(i18nLocale);
				paymentVariable.getI18NModiferNames().add(
						i18nName);
			}
			
			}
	}
    public String editModfierName(){
    	paymentVariable = paymentModifierAdminService.findPaymentVariableById(paymentVariableId);
    	return SUCCESS;
    }
    public String updateModifierName(){
    	paymentVariable = paymentModifierAdminService.findPaymentVariableById(paymentVariableId);
    	prepareModifierName();
    	if(!hasActionErrors()){
    		paymentModifierAdminService.updatePaymentVariable(paymentVariable);
    		addActionMessage("message.manageModifier.updateModifierSuccess");
    		return SUCCESS;
       	}
    	return INPUT;
    	
    }
    public String deactivatePaymentVariable() {
		paymentVariable = paymentModifierAdminService
				.findPaymentVariableById(paymentVariableId);
		if (paymentVariable.getD() != null) {
			paymentVariable.getD().setActive(Boolean.FALSE);
		}
		paymentModifierAdminService.updatePaymentVariable(paymentVariable);
		paymentModifierAdminService
				.deactivatePaymentModifierForVariable(paymentVariable.getId());
		
		paymentModifierAdminService.deactivatePaymentVariableLevelForVariable(paymentVariable.getId());
		
		// This was causing data issues as criteria evaluation precedence expects only 1 active record for a given name.
		paymentModifierAdminService.deactivateCriteriaEvaluationPrecedence(paymentVariable);
		isDeleted=true;
		addActionMessage("message.manageModifier.deleteModifierSuccess");
		return SUCCESS;
	}
    
    public boolean isDeleted() {
        return isDeleted;
    }

    public String setupForDeactivatePaymentVariable() {
		paymentVariable = paymentModifierAdminService
				.findPaymentVariableById(paymentVariableId);
		addActionMessage("message.manageModifier.setupForDeleteModifier");
		return SUCCESS;
	}

    public String createVariable() throws Exception {
		/* if(StringUtils.hasText(paymentVariable.getName())){ */
		I18NModifierName i18nName = new I18NModifierName();
		i18nName.setLocale("en_US");
		i18nName.setName(paymentVariable.getName());
		paymentVariable.getI18NModiferNames().add(i18nName);
		/*
		 * } else { addActionError("error.common.modifierfailureMessageUS");
		 * return INPUT; }
		 */
		/*		if (logger.isDebugEnabled()) {
			logger.debug("Creating variable with name [" + newVariableName
					+ "].");
		}*/
		paymentModifierAdminService.createPaymentVariable(paymentVariable);
		CriteriaEvaluationPrecedence newEvalPrecedence = getNewEvalPrecedence(paymentVariable);
		/*		if (logger.isDebugEnabled()) {
			logger.debug("Creating evaluation precedence for the varaible ["
					+ newVariableName + "].");
		}*/
		paymentModifierAdminService
				.createEvaluationPrecedence(newEvalPrecedence);
		addActionMessage("message.managePayment.recordSaveSuccess");
		paymentVariables.add(paymentVariable);
		/*listVariables();*/
		return SUCCESS;
	}
    
    /**
	 * Returns a new instance of CriteriaEvaluationPrecedence for a
	 * PaymentVariable. Essentially this is used at the time of creating of a
	 * new PaymentVariable so that an associated evaluation precedence is
	 * available. By default the order of precedence will be Dealer, Warranty
	 * Type and Claim Type.
	 * 
	 * @param var
	 * @return
	 */
    private CriteriaEvaluationPrecedence getNewEvalPrecedence(PaymentVariable paymentVariable) {
        CriteriaEvaluationPrecedence evaluationPrecedence = new CriteriaEvaluationPrecedence();
        evaluationPrecedence.setForData(paymentVariable.getName());
        List<CriteriaElement> properties = evaluationPrecedence.getProperties();
        properties.add(criteriaElement("Dealer", "dealerCriterion"));
        properties.add(criteriaElement("Warranty Type", "warrantyType"));
        properties.add(criteriaElement("Claim Type", "claimType"));
        properties.add(criteriaElement("Product Type", "productType"));
        return evaluationPrecedence;
    }

    private CriteriaElement criteriaElement(String domainName, String propertyExpression) {
        CriteriaElement aCriteriaElement = new CriteriaElement();
        aCriteriaElement.setDomainName(domainName);
        aCriteriaElement.setPropertyExpression(propertyExpression);
        return aCriteriaElement;
    }

/*    public String getNewVariableName() {
        return newVariableName;
    }

    public void setNewVariableName(String newVariableName) {
        this.newVariableName = newVariableName;
    }*/

    public List<PaymentVariable> getPaymentVariables() {
        return paymentVariables;
    }

    public void setPaymentVariables(List<PaymentVariable> paymentVariables) {
        this.paymentVariables = paymentVariables;
    }
    
    public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

	public void setPaymentDefinitionRepository(
			PaymentDefinitionRepository paymentDefinitionRepository) {
		this.paymentDefinitionRepository = paymentDefinitionRepository;
	}

	public PaymentVariable getPaymentVariable() {
		return paymentVariable;
	}

	public void setPaymentVariable(PaymentVariable paymentVariable) {
		this.paymentVariable = paymentVariable;
	}

	@Required
    public void setPaymentModifierAdminService(PaymentModifierAdminService paymentModifierAdminService) {
        this.paymentModifierAdminService = paymentModifierAdminService;
    }

	public List<ProductLocale> getLocales() {
		return locales;
	}

	public void setLocales(List<ProductLocale> locales) {
		this.locales = locales;
	}

	public void setProductLocaleService(ProductLocaleService productLocaleService) {
		this.productLocaleService = productLocaleService;
	}

	public Long getPaymentVariableId() {
		return paymentVariableId;
	}

	public void setPaymentVariableId(Long paymentVariableId) {
		this.paymentVariableId = paymentVariableId;
	}
	public String getMessageKey(String sectionName){
        return NAMES_AND_KEY.get(sectionName);
    }
	
}