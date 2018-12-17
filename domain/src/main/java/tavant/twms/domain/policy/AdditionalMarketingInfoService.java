package tavant.twms.domain.policy;

import java.util.List;

import tavant.twms.domain.catalog.ItemGroup;

public interface AdditionalMarketingInfoService {

	List<AdditionalMarketingInfo> getAdditionalMarketingInfoByAppProduct(ItemGroup itemGroup);
}
