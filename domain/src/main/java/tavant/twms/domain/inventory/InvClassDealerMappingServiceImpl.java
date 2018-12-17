package tavant.twms.domain.inventory;

import java.util.List;

import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

/**
 * Service interface implementation for {@link InvClassDealerMapping}
 * 
 * @author ravi.sinha
 */
public class InvClassDealerMappingServiceImpl extends GenericServiceImpl<InvClassDealerMapping, Long, Exception> 
	implements InvClassDealerMappingService {
	
	private InvClassDealerMappingRepository invClassDlrMappingRepository;
	
	public List<InvClassDealerMapping> findInvClassDealerMappings(ServiceProvider serviceProvider) {
		
		List<InvClassDealerMapping> returnedMappings = 
				invClassDlrMappingRepository.findInvClassDealerMappings(serviceProvider);
		
		return returnedMappings;
	}
	
	@Override
	public GenericRepository<InvClassDealerMapping, Long> getRepository() {
		return invClassDlrMappingRepository;
	}
	
	public void setInvClassDlrMappingRepository(InvClassDealerMappingRepository invClassDlrMappingRepository) {
		this.invClassDlrMappingRepository = invClassDlrMappingRepository;
	}
	
	public InvClassDealerMappingRepository getInvClassDlrMappingRepository() {
		return invClassDlrMappingRepository;
	}
}