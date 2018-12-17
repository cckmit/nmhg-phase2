package tavant.twms.domain.orgmodel;

import java.util.List;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface TechnicianCertificationRepository extends GenericRepository<TechnicianCertification, Long> {
	public List<TechnicianCertification> getCertificationForTechnician(AttributeValue attr);
	
	public PageResult<TechnicianCertification> findAllTechnicianCertificates(ListCriteria listCriteria);	
}
