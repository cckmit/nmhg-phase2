package tavant.twms.domain.claim;

import java.util.List;

public interface ReplacedInstalledPartsService {
	public Long generateSequence();
	
	public List<String> findItemNumbers(List<String> listOfPartNumbers);
}
