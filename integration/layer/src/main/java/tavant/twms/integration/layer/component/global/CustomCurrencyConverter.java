package tavant.twms.integration.layer.component.global;

import java.util.Currency;

import net.sf.dozer.util.mapping.converters.CustomConverter;

import org.hibernate.MappingException;
import org.springframework.util.StringUtils;

public class CustomCurrencyConverter implements CustomConverter {

	public Object convert(Object existingDestinationFieldValue,
			Object sourceFieldValue, Class destinationClass, Class sourceClass) {
		// TODO Auto-generated method stub
		if (sourceFieldValue == null) {
		      return null;
		    }
		    if (sourceFieldValue instanceof String) {
		    	try{
		    		return Currency.getInstance(sourceFieldValue.toString());
		    	}catch(Exception e){
		    		if(!StringUtils.hasText(sourceFieldValue.toString())){
		    			throw new RuntimeException("CU0073");
		    		}
		    		throw new RuntimeException("CU0067");
		    	}
		    } 
		    else if (sourceFieldValue instanceof Currency){
		    	return ((Currency)sourceFieldValue).getCurrencyCode();
		    }
		    else {
		      throw new MappingException("Converter CustomCurrencyConverter used incorrectly. Arguments passed in were:"
		          + existingDestinationFieldValue + " and " + sourceFieldValue);
		    }
	}
	
}
