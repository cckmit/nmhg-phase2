package tavant.twms.domain.orgmodel;

import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.Converter;

import tavant.twms.domain.policy.Customer;
import tavant.twms.infra.HibernateCast;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Apr 1, 2009
 * Time: 2:27:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartyXMLConverter implements Converter {
    OrgService orgService;

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		if(source!=null)
		{
			Party party=new HibernateCast<Party>().cast(source);
			String value=party.getId().toString();
			if(value!=null)
			{
				writer.startNode("dealershipId");
				writer.setValue(value);
				writer.endNode();
			}
		}

	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		reader.moveDown();
		String value=reader.getValue();
		reader.moveUp();
		Party party=orgService.getPartyById(Long.parseLong(value));
		return party;
	}

	public boolean canConvert(Class type) {
		return (Party.class.equals(type)  || Customer.class.equals(type));
	}
}
