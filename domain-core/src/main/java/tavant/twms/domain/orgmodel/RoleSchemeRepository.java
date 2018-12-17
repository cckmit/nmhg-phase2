package tavant.twms.domain.orgmodel;

import java.util.List;

import tavant.twms.domain.common.Purpose;
import tavant.twms.infra.GenericRepository;

public interface RoleSchemeRepository extends
		GenericRepository<RoleScheme, Long> {

	public List<Purpose> findEmployedPurposes();

	public RoleScheme findSchemeForPurpose(Purpose purpose);

}
