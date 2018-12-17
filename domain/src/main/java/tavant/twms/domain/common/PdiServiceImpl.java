package tavant.twms.domain.common;

import java.util.List;

public class PdiServiceImpl implements PdiService {
	
	private PdiRepository pdiRepository;

	public PdiRepository getPdiRepository() {
		return pdiRepository;
	}

	public void setPdiRepository(PdiRepository pdiRepository) {
		this.pdiRepository = pdiRepository;
	}

	public List<DeliveryCheckList> populateDeliveryCheckList() {
		return this.pdiRepository.populateDeliveryCheckList();
	}
}
