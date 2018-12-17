package tavant.twms.domain.policy;

import java.util.List;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.infra.GenericRepository;

public interface AdditionalMarketingInfoRepository extends GenericRepository<AdditionalMarketingInfo, Long>

{
	List<AdditionalMarketingInfo> getAdditionalMarketingInfoByAppProduct(ItemGroup itemGroup);
}
