package tavant.twms.domain.orgmodel;

public interface MarketingGroupRepository {
	
	/**
	 *
	 * @param mktGroupCode
	 * @return MarketingGroupCode object
	 */
	public MarketingGroup findByMarketingCode(String mktGrpCode);
}
