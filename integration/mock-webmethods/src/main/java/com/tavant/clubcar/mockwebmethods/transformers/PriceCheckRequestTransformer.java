package com.tavant.clubcar.mockwebmethods.transformers;

import org.apache.xmlbeans.XmlException;

import tavant.globalsync.pricefetchrequest.PriceCheckRequestTypeDTO;
import tavant.globalsync.pricefetchrequest.PriceFetchDocumentDTO;


public class PriceCheckRequestTransformer {
	
	public PriceCheckRequestTypeDTO transform(String src) {
		PriceFetchDocumentDTO dto = null;
		try {
			dto = PriceFetchDocumentDTO.Factory.parse(src);
		} catch (XmlException e) {
			throw new RuntimeException(e);
		}
		return dto.getPriceFetch().getDataArea().getPriceCheckRequest();
	}

}
