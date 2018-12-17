package tavant.twms.domain.policy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.catalog.ItemGroupRepository;
import tavant.twms.domain.orgmodel.DealerCriterion;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.infra.DomainRepositoryTestCase;

import com.domainlanguage.time.CalendarDate;

public class PolicyRatesRepositoryImplTest extends DomainRepositoryTestCase {

	private PolicyRatesRepository policyRatesRepository;

	private ItemGroupRepository itemGroupRepository;

	private DealershipRepository dealershipRepository;

	private PolicyDefinitionRepository policyDefinitionRepository;

	public void testFindPolicyRateConfiguration() {
		PolicyRatesCriteria criteria = new PolicyRatesCriteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(7L)));
    	criteria.setWarrantyRegistrationType(WarrantyRegistrationType.ALL);
    	criteria.setCustomerState("Texas");
    	
		PolicyDefinition policyDefinition = policyDefinitionRepository
				.findById(new Long(1));
		BigDecimal rate = policyRatesRepository.findPolicyRateConfiguration(
				criteria, policyDefinition, CalendarDate.date(2007, 1, 1));
		assertNotNull(rate);
		assertEquals(new BigDecimal(10), rate);
	}
	
	public void testFindByCriteria_BusinessUnitFilter()
	{
		PolicyRates entity = new PolicyRates();
		PolicyRatesCriteria criteria = new PolicyRatesCriteria();
    	criteria.setProductType(itemGroupRepository.findById(5L));
    	criteria.setWarrantyType("STANDARD");
    	criteria.setDealerCriterion(new DealerCriterion(dealershipRepository.findByDealerId(7L)));
    	criteria.setWarrantyRegistrationType(WarrantyRegistrationType.ALL);
    	criteria.setCustomerState("Texas");
    	entity.setForCriteria(criteria);
		PolicyDefinition policyDefinition = policyDefinitionRepository
		.findById(new Long(1));
		List<PolicyDefinition> policyDefinitions = new ArrayList<PolicyDefinition>();
		policyDefinitions.add(policyDefinition);
		entity.setPolicyDefinitions(policyDefinitions);
		policyRatesRepository.savePolicyRates(entity);
		PolicyRates policyRates = policyRatesRepository.findByCriteria(criteria);
		assertNotNull(policyRates);
		assertNotNull(policyRates.getBusinessUnitInfo());
		assertEquals("IR", policyRates.getBusinessUnitInfo().getName());
		
	}

	@Required
	public void setPolicyDefinitionRepository(
			PolicyDefinitionRepository policyDefinitionRepository) {
		this.policyDefinitionRepository = policyDefinitionRepository;
	}

	@Required
	public void setPolicyRatesRepository(
			PolicyRatesRepository policyRatesRepository) {
		this.policyRatesRepository = policyRatesRepository;
	}

	/**
	 * @param itemGroupRepository
	 *            the itemGroupRepository to set
	 */
	@Required
	public void setItemGroupRepository(ItemGroupRepository itemGroupRepository) {
		this.itemGroupRepository = itemGroupRepository;
	}

	/**
	 * @param dealershipRepository
	 *            the dealershipRepository to set
	 */
	@Required
	public void setDealershipRepository(
			DealershipRepository dealershipRepository) {
		this.dealershipRepository = dealershipRepository;
	}
}