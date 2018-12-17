package tavant.twms.domain.claim.foc;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class FocServiceImpl  extends GenericServiceImpl<FocOrder, Long, Exception> implements FocService {

	private FocRepository focRepository;
	
	public GenericRepository<FocOrder, Long> getRepository(){
	  return focRepository;	
	}

	public void setFocRepository(FocRepository focRepository) {
		this.focRepository = focRepository;
	}
	
	public String syncFoc(String claimXml){
		XStream xstream = new XStream(new DomDriver());
		  xstream.fromXML(claimXml);
		return "";
	}

	public FocOrder fetchFOCOrderDetails(String orderNo) {
		return focRepository.fetchFOCOrderDetails(orderNo);
	}
	
	
}
