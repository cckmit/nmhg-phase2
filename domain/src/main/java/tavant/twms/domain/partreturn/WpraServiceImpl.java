package tavant.twms.domain.partreturn;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParam;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.ServiceProvider;

import java.util.List;
import java.util.Random;

public class WpraServiceImpl implements WpraService{

    private final static String SEPARATOR = "-";

	private ConfigParamService configService;

    private WpraRepository wpraRepository;
    
    private WpraNumberPatternService wpraNumberPatternService;

	public WpraNumberPatternService getWpraNumberPatternService() {
		return wpraNumberPatternService;
	}

	public void setWpraNumberPatternService(
			WpraNumberPatternService wpraNumberPatternService) {
		this.wpraNumberPatternService = wpraNumberPatternService;
	}

	public String getProcessorRoleBUSpecific(Claim claim) {
		if(configService.getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())){
			return claim.getAssignToUser().getAssignToUserName();
		}
		
		return null;
	}

	public void setConfigService(ConfigParamService configService) {
		this.configService = configService;
	}
	
	 public Wpra createWpraForParts(List<PartReturn> parts) {
        validateParts(parts);
        PartReturn aPart = parts.get(0);
        Wpra wpra = new Wpra(aPart.getReturnLocation(), aPart.getReturnedBy());
	    wpra.addParts(parts);
	    wpra.setShippedBy(aPart.getReturnedBy());
        wpra.setDestination(aPart.getReturnLocation());
        wpra.setWpraNumber(generateUniqueRandomNumbers(aPart.getReturnLocation(), aPart.getReturnedBy()));
	    wpraRepository.save(wpra);
	    return wpra;
     }

     public Wpra createWpraForParts(List<PartReturn> parts, Claim claim) {
        validateParts(parts);
        PartReturn aPart = parts.get(0);
        Wpra wpra = new Wpra(aPart.getReturnLocation(), aPart.getReturnedBy());
	    wpra.addParts(parts);
	    wpra.setShippedBy(aPart.getReturnedBy());
        wpra.setDestination(aPart.getReturnLocation());
        StringBuffer sf = new StringBuffer();
         if(aPart.getReturnedBy() != null && aPart.getReturnedBy().getName().length() > 2){
             sf.append(aPart.getReturnedBy().getName().substring(0,2).toUpperCase());
             sf.append(SEPARATOR);
         }
         if(aPart.getReturnLocation() != null && aPart.getReturnLocation().getCode().length() > 2 ){
             sf.append(aPart.getReturnLocation().getCode().substring(0,2).toUpperCase());
         }
         wpra.setWpraNumber(wpraNumberPatternService.generateNextWpraNumber(claim, wpra));
	    wpraRepository.save(wpra);
	    return wpra;
     }

     public String generateUniqueRandomNumbers(Location returnLocation, ServiceProvider forDealer){

         StringBuffer sf = new StringBuffer();
         if(forDealer != null && forDealer.getName().length() > 2){
             sf.append(forDealer.getName().substring(0,2).toUpperCase());
             sf.append(SEPARATOR);
         }
         if(forDealer != null && returnLocation.getCode().length() > 2 ){
             sf.append(returnLocation.getCode().substring(0,2).toUpperCase());
         }
         long randomNumber = new Random().nextLong();
         return sf.append(SEPARATOR).append((String.valueOf(randomNumber))).toString();
     }

     private void validateParts(List<PartReturn> parts) {
        if(parts == null || parts.size() == 0) {
            throw new IllegalArgumentException("There are no parts selected to update shipment info.");
        }
     }

    public WpraRepository getWpraRepository() {
        return wpraRepository;
    }

    public void setWpraRepository(WpraRepository wpraRepository) {
        this.wpraRepository = wpraRepository;
    }

    public void reloadWpras(List<Wpra> wpras){
        this.wpraRepository.reloadWpras(wpras);
    }

    public Wpra findWpraById(String id){
        return this.wpraRepository.findById(new Long(id));
    }
}
