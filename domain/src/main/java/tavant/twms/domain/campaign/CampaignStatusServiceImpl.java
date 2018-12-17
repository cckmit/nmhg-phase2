package tavant.twms.domain.campaign;

import java.util.Collection;
import java.util.List;

import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public class CampaignStatusServiceImpl implements CampaignStatusService {

	private CampaignStatusRepository campaignStatusRepository;

	public void delete(CampaignStatus campaignStatus) {
		campaignStatusRepository.delete(campaignStatus);
	}

	public List<CampaignStatus> findAll() {
		return campaignStatusRepository.findAll();
	}

	public PageResult<CampaignStatus> findAll(
			PageSpecification pageSpecification) {
		return campaignStatusRepository.findAll(pageSpecification);
	}

	public CampaignStatus findByCode(String code) {
		return campaignStatusRepository.findByCode(code);
	}

	public List<CampaignStatus> findByIds(Collection<Long> collectionOfIds) {
		return campaignStatusRepository.findByIds(collectionOfIds);
	}

	public void save(CampaignStatus campaignStatus) {
		campaignStatusRepository.save(campaignStatus);
	}

	public void update(CampaignStatus campaignStatus) {
		campaignStatusRepository.update(campaignStatus);
	}

	public void setCampaignStatusRepository(
			CampaignStatusRepository campaignStatusRepository) {
		this.campaignStatusRepository = campaignStatusRepository;
	}

	public CampaignStatus findById(Long id) {
		return campaignStatusRepository.findById(id);
	}

	public void deleteAll(List<CampaignStatus> entitiesToDelete) {
		this.campaignStatusRepository.deleteAll(entitiesToDelete);
	}

}
