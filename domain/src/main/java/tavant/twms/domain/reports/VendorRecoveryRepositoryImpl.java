package tavant.twms.domain.reports;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.PageResult;

public class VendorRecoveryRepositoryImpl extends GenericRepositoryImpl<VendorRecoveryExtract, Long> implements VendorRecoveryRepository {

	private String getBaseQueryWithoutSelect(VendorRecoveryListCriteria vendorRecoveryListCriteria) {
		String baseQuery = " from VendorRecoveryExtract v "
			+ " where v.recClaimCreatedDate >= :startDate and v.recClaimCreatedDate < :endDate ";
		List<String> bussinesUnitNames = vendorRecoveryListCriteria.getBussinesUnitNames();
		String recClaimState = vendorRecoveryListCriteria.getRecoveryClaimState();
		if(bussinesUnitNames.size() > 0){
			baseQuery = baseQuery + " and v.businessUnitInfo in (:bu) ";
		}
		if(StringUtils.hasText(recClaimState)){
			baseQuery = baseQuery + " and v.recoveryClaimState = :recClaimState ";
		}
		return baseQuery;
	}

	private Map<String, Object> getParametersMap(VendorRecoveryListCriteria vendorRecoveryListCriteria) {
		List<String> bussinesUnitNames = vendorRecoveryListCriteria.getBussinesUnitNames();
		String recClaimState = vendorRecoveryListCriteria.getRecoveryClaimState();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("startDate", vendorRecoveryListCriteria.getStartRepairDate());
		params.put("endDate", vendorRecoveryListCriteria.getEndRepairDate());
		if(bussinesUnitNames.size() > 0){
			params.put("bu", bussinesUnitNames);
		}
		if(StringUtils.hasText(recClaimState)){
			params.put("recClaimState", recClaimState);
		}
		return params;
	}

	public Long findRecoveryClaimsCountForRange(VendorRecoveryListCriteria vendorRecoveryListCriteria) {
		String baseQuery = getBaseQueryWithoutSelect(vendorRecoveryListCriteria);
		Map<String, Object> params = getParametersMap(vendorRecoveryListCriteria);
		return findSizeUsingQuery(baseQuery.toString(), params);
	}

	public PageResult<VendorRecoveryExtract> findAllRecoveryClaimsForRange(VendorRecoveryListCriteria vendorRecoveryListCriteria) {
		String baseQuery = getBaseQueryWithoutSelect(vendorRecoveryListCriteria);
		Map<String, Object> params = getParametersMap(vendorRecoveryListCriteria);
		return findPageUsingQuery(baseQuery.toString(),
				getSortCriteriaString(vendorRecoveryListCriteria),
				vendorRecoveryListCriteria.getPageSpecification(), params);

	}

	private String getSortCriteriaString(VendorRecoveryListCriteria criteria) {
		if (criteria.getSortCriteria().size() > 0) {
			StringBuffer dynamicQuery = new StringBuffer();
			for (String columnName : criteria.getSortCriteria().keySet()) {
				dynamicQuery.append(columnName);
				dynamicQuery.append(" ");
				dynamicQuery.append(criteria.getSortCriteria().get(columnName));
				dynamicQuery.append(",");
			}
			dynamicQuery.deleteCharAt(dynamicQuery.length() - 1);
			return dynamicQuery.toString();
		}
		return "";
	}
}
