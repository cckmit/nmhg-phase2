package tavant.twms.domain.policy;

import java.util.List;

import tavant.twms.domain.catalog.ItemGroup;

public class AdditionalMarketingInfoServiceImpl implements AdditionalMarketingInfoService {
	private AdditionalMarketingInfoRepository additionalMarketingInfoRepository;

	public List<AdditionalMarketingInfo> getAdditionalMarketingInfoByAppProduct(ItemGroup itemGroup) {
		// TODO Auto-generated method stub
		return this.additionalMarketingInfoRepository.getAdditionalMarketingInfoByAppProduct(itemGroup);
	}

	public AdditionalMarketingInfoRepository getAdditionalMarketingInfoRepository() {
		return additionalMarketingInfoRepository;
	}

	public void setAdditionalMarketingInfoRepository(AdditionalMarketingInfoRepository additionalMarketingInfoRepository) {
		this.additionalMarketingInfoRepository = additionalMarketingInfoRepository;
	}
	
}
