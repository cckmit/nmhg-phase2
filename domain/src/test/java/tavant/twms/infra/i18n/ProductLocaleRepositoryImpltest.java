package tavant.twms.infra.i18n;

import tavant.twms.infra.GenericRepositoryImplTest;

public class ProductLocaleRepositoryImpltest extends GenericRepositoryImplTest {

	private ProductLocaleRepository productLocaleRepository;
	
	public void testSave()
	{
		ProductLocale productLocale = new ProductLocale();
		productLocale.setLocale("FR_fr");
		productLocaleRepository.save(productLocale);
		flushAndClear();
		productLocale = productLocaleRepository.findById("FR_fr");
		assertNotNull(productLocale);
	}
	

	public ProductLocaleRepository getProductLocaleRepository() {
		return productLocaleRepository;
	}

	public void setProductLocaleRepository(
			ProductLocaleRepository productLocaleRepository) {
		this.productLocaleRepository = productLocaleRepository;
	}

	
}
