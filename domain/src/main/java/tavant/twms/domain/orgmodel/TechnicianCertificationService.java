package tavant.twms.domain.orgmodel;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

@Transactional(readOnly = true)
public interface TechnicianCertificationService extends GenericService<TechnicianCertification, Long, Exception>{
	public List<TechnicianCertification> getCertificationForTechnician(AttributeValue attr);

	@Transactional(readOnly = false)
	public void saveAll(List<TechnicianCertification> certificationList);
	
	public PageResult<TechnicianCertification> findAllTechnicianCertificates(ListCriteria listCriteria);
}
