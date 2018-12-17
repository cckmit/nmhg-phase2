package tavant.twms.domain.claim.foc;

import tavant.twms.infra.GenericService;

public interface FocService extends GenericService<FocOrder,Long,Exception> {

	public String syncFoc(String claimXml);
	
	public FocOrder fetchFOCOrderDetails(String orderNo);
}
