package tavant.twms.domain.orgmodel;

public interface BrandRepository {

	/**
	 * 
	 * @param brandCode
	 * @return Brand object
	 */
	public Brand findByBrandCode(String brandCode);
}
