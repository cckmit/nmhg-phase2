package tavant.twms.domain.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.infra.GenericRepositoryImpl;

public class AdditionalMarketingInfoRepositoryImpl extends GenericRepositoryImpl<AdditionalMarketingInfo, Long>
		implements AdditionalMarketingInfoRepository {

	public List<AdditionalMarketingInfo> getAdditionalMarketingInfoByAppProduct(ItemGroup itemGroup) {
		// TODO Auto-generated method stub
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("itemGroupParam", itemGroup.getId());
		String query = "from AdditionalMarketingInfo marketingInfo where marketingInfo.applItemGroup.id = :itemGroupParam";
		List<AdditionalMarketingInfo> additionalMarketingInfo = findUsingQuery(query, params);
		return additionalMarketingInfo;
	}
}
