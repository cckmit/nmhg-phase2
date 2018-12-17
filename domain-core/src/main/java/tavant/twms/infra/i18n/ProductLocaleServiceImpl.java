/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.infra.i18n;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class ProductLocaleServiceImpl extends
		GenericServiceImpl<ProductLocale, String, Exception> implements
		ProductLocaleService {

	private ProductLocaleRepository productLocaleRepository;

	public ProductLocaleRepository getProductLocaleRepository() {
		return productLocaleRepository;
	}

	public void setProductLocaleRepository(
			ProductLocaleRepository productLocaleRepository) {
		this.productLocaleRepository = productLocaleRepository;
	}

	@Override
	public GenericRepository getRepository() {
		return productLocaleRepository;
	}

}
