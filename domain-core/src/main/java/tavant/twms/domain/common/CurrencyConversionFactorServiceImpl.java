package tavant.twms.domain.common;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class CurrencyConversionFactorServiceImpl extends
	GenericServiceImpl<CurrencyConversionFactor, Long, Exception> 
	implements CurrencyConversionFactorService {

	CurrencyConversionFactorRepository CurrencyConversionFactorRepository;

	@Override
	public GenericRepository<CurrencyConversionFactor, Long> getRepository() {

		return CurrencyConversionFactorRepository;
	}

	public CurrencyConversionFactorRepository getCurrencyConversionFactorRepository() {
		return CurrencyConversionFactorRepository;
	}

	public void setCurrencyConversionFactorRepository(
			CurrencyConversionFactorRepository currencyConversionFactorRepository) {
		CurrencyConversionFactorRepository = currencyConversionFactorRepository;
	}

	
}
