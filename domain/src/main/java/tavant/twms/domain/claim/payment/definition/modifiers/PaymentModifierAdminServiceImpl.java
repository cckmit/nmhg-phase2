package tavant.twms.domain.claim.payment.definition.modifiers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.common.CriteriaEvaluationPrecedence;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.infra.BitSetValueComputer;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.RelevanceScoreComputerService;

public class PaymentModifierAdminServiceImpl extends GenericServiceImpl<PaymentModifier, Long, Exception> implements
        PaymentModifierAdminService {

    private PaymentModifierRepository paymentModifierRepository;
    
    private CriteriaBasedValueRepository criteriaBasedValueRepository;

    private RelevanceScoreComputerService relevanceScoreComputerService;
    
    private static final String PAYMENT_MODIFIER_WEIGHTS ="PAYMENT MODIFIER WEIGHTS";
    
    @Override
    public GenericRepository<PaymentModifier, Long> getRepository() {
        return paymentModifierRepository;
    }
    
    public void createEvaluationPrecedence(CriteriaEvaluationPrecedence newEvalPrecedence) {
        criteriaBasedValueRepository.save(newEvalPrecedence);
    }

    public void createPaymentVariable(PaymentVariable newPaymentVariable) {
        paymentModifierRepository.savePaymentVariable(newPaymentVariable);
    }
    public void updatePaymentVariable(PaymentVariable newPaymentVariable){
    	paymentModifierRepository.updatePaymentVariable(newPaymentVariable);
    }

    public boolean isUnique(PaymentModifier definition) {
        boolean isUnique = false;
        Criteria forCriteria = definition.getForCriteria();
        PaymentVariable paymentVariable = definition.getForPaymentVariable();
        PaymentModifier example = null;
        example = paymentModifierRepository.findExactForCriteria(forCriteria, paymentVariable,definition.getCustomerType());
        if (example == null || same(definition, example)) {
            isUnique = true;
        }
        return isUnique;
    }

    @Required
    public void setPaymentModifierRepository(PaymentModifierRepository paymentModifierRepository) {
        this.paymentModifierRepository = paymentModifierRepository;
    }
    
    @Required
    public void setCriteriaBasedValueRepository(CriteriaBasedValueRepository criteriaBasedValueRepository) {
        this.criteriaBasedValueRepository = criteriaBasedValueRepository;
    }

    private boolean same(PaymentModifier source, PaymentModifier target) {
        return source.getId() != null && target.getId() != null && source.getId().compareTo(target.getId()) == 0;
    }

    public List<PaymentVariable> findAllPaymentVariables() {
        return paymentModifierRepository.findAllPaymentVariables();
    }

    public PaymentVariable findPaymentVariableById(Long paymentVariableId) {
        return paymentModifierRepository.findPaymentVariableByPK(paymentVariableId);
    }

    public PaymentVariable findPaymentVariableByName(String newVariableName) {
        return paymentModifierRepository.findPaymentVariableByName(newVariableName);
    }

	@Override
	public void save(PaymentModifier entity) {
		long score =  relevanceScoreComputerService.computeRelevanceScore(PAYMENT_MODIFIER_WEIGHTS, entity);
		//updateRelevanceScore(entity);
		entity.getForCriteria().setRelevanceScore(score);
		super.save(entity);
	}

	@Override
	public void update(PaymentModifier entity) {
		long score =  relevanceScoreComputerService.computeRelevanceScore(PAYMENT_MODIFIER_WEIGHTS, entity);
		//updateRelevanceScore(entity);
		entity.getForCriteria().setRelevanceScore(score);
		super.update(entity);
	}
	
	void updateRelevanceScore(PaymentModifier entity) {
    	BitSetValueComputer bitSetValueComputer = new BitSetValueComputer();
		Criteria forCriteria = entity.getForCriteria();
		DealerCriterion dealerCriterion = forCriteria.getDealerCriterion();
		boolean[] bits = new boolean[] {
    			dealerCriterion!=null && dealerCriterion.getDealer()!=null,
    			dealerCriterion!=null && dealerCriterion.getDealerGroup()!=null,
    			forCriteria.getClaimType()!=null,
    			forCriteria.getWarrantyType()!=null,
    			forCriteria.getProductType()!=null
    	};
		forCriteria.setRelevanceScore( bitSetValueComputer.compute(bits) );
    }

	public List<PaymentVariable> sortModifiersBasedOnName(List<PaymentVariable> paymentVariables){
		 if(CollectionUtils.isNotEmpty(paymentVariables)){
		   Collections.sort(paymentVariables, SORT_BY_COMPLETE_NAME);
		 }
		 return paymentVariables;
	}
	
	public static Comparator<PaymentVariable> SORT_BY_COMPLETE_NAME = new Comparator<PaymentVariable>() {
		public int compare(PaymentVariable arg0, PaymentVariable arg1) {
			return arg0.getName().compareToIgnoreCase(
					arg1.getName());
		}
	};

	public void setRelevanceScoreComputerService(
			RelevanceScoreComputerService relevanceScoreComputerService) {
		this.relevanceScoreComputerService = relevanceScoreComputerService;
	}
    
    public void deactivatePaymentModifierForVariable(
			final Long paymentVariableId) {
		paymentModifierRepository
				.deactivatePaymentModifierForVariable(paymentVariableId);
	}
    
    public void deactivatePaymentVariableLevelForVariable(final Long paymentVariableId) {
		paymentModifierRepository
				.deactivatePaymentVariableLevelForVariable(paymentVariableId);
	}
    
    public void deactivateCriteriaEvaluationPrecedence(final PaymentVariable paymentVariable) {
		paymentModifierRepository.deactivateCriteriaEvaluationPrecedence(paymentVariable);
	}
    
}