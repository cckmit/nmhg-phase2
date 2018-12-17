package tavant.twms.integration.layer.component;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import tavant.twms.integration.layer.IntegrationRepositoryTestCase;
import tavant.twms.integration.layer.transformer.ExtWarrantyPriceCheckResponseTransformer;

public class ExtWarrantyPriceCheckTest extends IntegrationRepositoryTestCase {

	private ExtWarrantyPriceCheckResponseTransformer extWarrantyPriceCheckResponseTransformer;

	public void testExtWarrantyPriceCheck() {

		InputStream stream = ExtWarrantyPriceCheckTest.class
				.getResourceAsStream("/ExtWarrantyPriceCheck/Ext-Warranty-Price-Check-Response.xml");

		try {
			String str = IOUtils.toString(stream);
			extWarrantyPriceCheckResponseTransformer.transform(str);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setExtWarrantyPriceCheckResponseTransformer(
			ExtWarrantyPriceCheckResponseTransformer extWarrantyPriceCheckResponseTransformer) {
		this.extWarrantyPriceCheckResponseTransformer = extWarrantyPriceCheckResponseTransformer;
	}

}
