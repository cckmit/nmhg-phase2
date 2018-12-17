package tavant.twms.domain.common;

import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.policy.Customer;
import tavant.twms.infra.HibernateCast;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ListOfValuesXMLConverter implements Converter{
	
	private LovRepository lovRepository;

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		if(source!=null)
		{
			ListOfValues lov=new HibernateCast<ListOfValues>().cast(source);
			String value=lov.getId().toString();			
			if(value!=null)
			{
				writer.startNode("listOfValueId");
				writer.setValue(value);
				writer.endNode();				
			}
		}

	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		reader.moveDown();
		String value=reader.getValue();		
		reader.moveUp();
		ListOfValues lov=lovRepository.findById(context.getRequiredType().getSimpleName(), Long.parseLong(value));
		return lov;		
	}

	public boolean canConvert(Class type) {		
		 return (ListOfValues.class.equals(type.getSuperclass()) || ListOfValues.class.equals(type));
	}

	public LovRepository getLovRepository() {
		return lovRepository;
	}

	public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}
	
	
}
