package tavant.twms.domain.inventory;

import java.util.List;

import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.GenericRepository;

/**
 * Repository interface implementation for {@link InvClassDealerMapping}
 * 
 * @author ravi.sinha
 */
public interface InvClassDealerMappingRepository extends GenericRepository<InvClassDealerMapping, Long> {
	
	List<InvClassDealerMapping> findInvClassDealerMappings(ServiceProvider serviceProvider);

}
