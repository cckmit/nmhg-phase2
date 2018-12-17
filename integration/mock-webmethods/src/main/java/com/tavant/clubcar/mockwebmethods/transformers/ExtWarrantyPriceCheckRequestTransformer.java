package com.tavant.clubcar.mockwebmethods.transformers;

import org.apache.xmlbeans.XmlException;

import tavant.extwarranty.ExtendedWarrantyPriceFetchDocumentDTO;
import tavant.extwarranty.ExtendedWarrantyPriceFetchDocumentDTO.ExtendedWarrantyPriceFetch;

public class ExtWarrantyPriceCheckRequestTransformer {
	
	public ExtendedWarrantyPriceFetch transform(String src) {
		ExtendedWarrantyPriceFetchDocumentDTO dto = null;
		try {
			dto = ExtendedWarrantyPriceFetchDocumentDTO.Factory.parse(src);
		} catch (XmlException e) {
			throw new RuntimeException(e);
		}
		return dto.getExtendedWarrantyPriceFetch();
	}
}
