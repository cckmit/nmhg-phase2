package tavant.twms.domain.claim;

import java.util.List;


public class ReplacedInstalledPartsServiceImpl implements ReplacedInstalledPartsService{

	private ReplacedInstalledPartsRepository replacedInstalledPartsRepository; 
	
	public Long generateSequence() {
		return replacedInstalledPartsRepository.generateSequence();
	}
	
	public List<String> findItemNumbers(List<String> listOfPartNumbers) {
		return replacedInstalledPartsRepository.findItemNumbers(listOfPartNumbers);
	}
	public ReplacedInstalledPartsRepository getReplacedInstalledPartsRepository() {
		return replacedInstalledPartsRepository;
	}
	public void setReplacedInstalledPartsRepository(
			ReplacedInstalledPartsRepository replacedInstalledPartsRepository) {
		this.replacedInstalledPartsRepository = replacedInstalledPartsRepository;
	}
}
