package tavant.twms.domain.orgmodel;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Service;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

@SuppressWarnings("serial")
@Service
public class TechnicianCertificationServiceImpl extends GenericServiceImpl<TechnicianCertification, Long, Exception> implements
		TechnicianCertificationService, Serializable {

	private TechnicianCertificationRepository technicianCertificationRepository;
	
	public List<TechnicianCertification> getCertificationForTechnician(
			AttributeValue attr) {
		return technicianCertificationRepository.getCertificationForTechnician(attr);
	}


	@Override
	public GenericRepository<TechnicianCertification, Long> getRepository() {
		return technicianCertificationRepository;
	}

	public void setTechnicianCertificationRepository(
			TechnicianCertificationRepository technicianCertificationRepository) {
		this.technicianCertificationRepository = technicianCertificationRepository;
	}

	public void saveAll(
			List<TechnicianCertification> certificationList) {
		technicianCertificationRepository.saveAll(certificationList);
	}
	
	public PageResult<TechnicianCertification> findAllTechnicianCertificates(ListCriteria listCriteria) {		
		return this.technicianCertificationRepository.findAllTechnicianCertificates(listCriteria);
	}
	
}
