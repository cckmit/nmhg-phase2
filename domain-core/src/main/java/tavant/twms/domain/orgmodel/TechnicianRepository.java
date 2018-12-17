package tavant.twms.domain.orgmodel;

import java.util.List;
import java.util.Set;

public interface TechnicianRepository {

	   public void save(Technician technician); 

	    public void update(Technician technician);

	    public Technician findById(Long id);
	    
	    public List<Technician> findTechnicianForDealer(final Long dealerId);
	    
	    public List<Technician> findTechnicianForDealers(final Set<Long> dealerIds);
	    
	    public Technician findByName(String userName);

}
