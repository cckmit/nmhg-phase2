package tavant.twms.domain.claim.foc;

import tavant.twms.infra.GenericRepository;

public interface FocRepository extends GenericRepository<FocOrder,Long> {

	public FocOrder fetchFOCOrderDetails(String orderNo);
	
}
