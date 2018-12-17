package tavant.twms.domain.inventory;

import java.util.List;

import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.GenericService;

/**
 * Service interface for {@link InvClassDealerMapping}
 * 
 * @author ravi.sinha
 */
public interface InvClassDealerMappingService extends GenericService<InvClassDealerMapping, Long, Exception>{
	
	List<InvClassDealerMapping> findInvClassDealerMappings(ServiceProvider serviceProvider);
	
}
