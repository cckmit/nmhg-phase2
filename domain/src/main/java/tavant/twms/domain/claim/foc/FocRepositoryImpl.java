package tavant.twms.domain.claim.foc;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import tavant.twms.infra.GenericRepositoryImpl;

public class FocRepositoryImpl extends GenericRepositoryImpl<FocOrder, Long> implements FocRepository {

	public FocOrder fetchFOCOrderDetails(String orderNo) {
		orderNo = StringUtils.stripToEmpty(orderNo);
		String query = "select foc from FocOrder foc where foc.orderNo = :orderNo";
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("orderNo", orderNo);
		return (FocOrder) findUniqueUsingQuery(query, params);
	}


}
