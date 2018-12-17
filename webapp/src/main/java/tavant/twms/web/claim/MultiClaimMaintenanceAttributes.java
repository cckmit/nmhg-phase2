package tavant.twms.web.claim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.AcceptanceReason;
import tavant.twms.domain.common.AccountabilityCode;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.domain.common.RecoveryClaimAcceptanceReason;
import tavant.twms.domain.common.RecoveryClaimRejectionReason;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.domain.partreturn.Carrier;
import tavant.twms.domain.partreturn.CarrierRepository;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.LocationRepository;
import tavant.twms.jbpm.infra.BeanLocator;

public class MultiClaimMaintenanceAttributes {
	private  MultiClaimAttributeMapper processingNotes ;
	private  MultiClaimAttributeMapper accountabilityCode ;
	private  MultiClaimAttributeMapper rejectionReason ;
	private  MultiClaimAttributeMapper acceptanceReason ;
	private  MultiClaimAttributeMapper technician;
	private  MultiClaimAttributeMapper recClaimAcceptanceReason;
	private  MultiClaimAttributeMapper recClaimRejectionReason;
	private MultiClaimAttributeMapper rgaNumber;
	private MultiClaimAttributeMapper location;
	private MultiClaimAttributeMapper carrier;
	private  LovRepository lovRepository;
	private  UserRepository userRepository;
	private CarrierRepository carrierRepository;
	private LocationRepository locationRepository;
	private final  List<MultiClaimAttributeMapper> listOfAttributes = new ArrayList<MultiClaimAttributeMapper>(5);
	private static final Logger logger = Logger.getLogger(MultiClaimMaintenanceAttributes.class);
	public MultiClaimMaintenanceAttributes() {
		super();
		this.processingNotes = new MultiClaimAttributeMapper(new String());
		this.accountabilityCode = new MultiClaimAttributeMapper(new AccountabilityCode());
		this.rejectionReason = new MultiClaimAttributeMapper(new RejectionReason());
		this.acceptanceReason =  new MultiClaimAttributeMapper(new AcceptanceReason());
		this.technician = new MultiClaimAttributeMapper(new User());
		this.recClaimAcceptanceReason = new MultiClaimAttributeMapper(new RecoveryClaimAcceptanceReason());
		this.recClaimRejectionReason = new MultiClaimAttributeMapper(new RecoveryClaimRejectionReason());
		this.rgaNumber = new MultiClaimAttributeMapper(new String());
		this.location =new MultiClaimAttributeMapper(new Location());
		this.carrier= new MultiClaimAttributeMapper(new Carrier());
	}
	public MultiClaimAttributeMapper getProcessingNotes() {
		return this.processingNotes;
	}
	public void setProcessingNotes(MultiClaimAttributeMapper processingNotes) {
		this.processingNotes = processingNotes;
	}
	public MultiClaimAttributeMapper getAccountabilityCode() {
		return this.accountabilityCode;
	}
	public void setAccountabilityCode(MultiClaimAttributeMapper accountabilityCode) {
		this.accountabilityCode = accountabilityCode;
	}
	public MultiClaimAttributeMapper getRejectionReason() {
		return this.rejectionReason;
	}
	public void setRejectionReason(MultiClaimAttributeMapper rejectionReason) {
		this.rejectionReason = rejectionReason;
	}
	public MultiClaimAttributeMapper getAcceptanceReason() {
		return this.acceptanceReason;
	}
	public void setAcceptanceReason(MultiClaimAttributeMapper acceptanceReason) {
		this.acceptanceReason = acceptanceReason;
	}

	public MultiClaimAttributeMapper getTechnician() {
		return this.technician;
	}
	public void setTechnician(MultiClaimAttributeMapper technician) {
		this.technician = technician;
	}
	public UserRepository getUserRepository() {
		return this.userRepository;
	}
	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	public List<MultiClaimAttributeMapper> getListOfAttributes() {
		return this.listOfAttributes;
	}
	public void setListOfAttributes() {
		this.listOfAttributes.add(getProcessingNotes());
		this.listOfAttributes.add(getAccountabilityCode());
		this.listOfAttributes.add(getRejectionReason());
		this.listOfAttributes.add(getAcceptanceReason());
		this.listOfAttributes.add(getTechnician());
	}

	public void setListOfRecoveryAttributes() {
		this.listOfAttributes.add(getRecClaimAcceptanceReason());
		this.listOfAttributes.add(getRecClaimRejectionReason());
		this.listOfAttributes.add(getRgaNumber());
		this.listOfAttributes.add(getCarrier());
		this.listOfAttributes.add(getLocation());
	}


	public boolean isAnyAttributeSelected(boolean multiDealerClaimSelected){
		boolean toReturn = false;
		if(getProcessingNotes().isSelected()||getAccountabilityCode().isSelected()
				||getRejectionReason().isSelected() || getAcceptanceReason().isSelected()
				||(!multiDealerClaimSelected && getTechnician().isSelected())){
			toReturn=true;
		}
		return toReturn;
	}

	public boolean isAnySupplierAttributeSelected(boolean multiSuplierClaimSelected){
		boolean toReturn = false;
		if(getRecClaimAcceptanceReason().isSelected()||getRecClaimRejectionReason().isSelected()
			|| (!multiSuplierClaimSelected && (getRgaNumber().isSelected() || getCarrier().isSelected()
					|| getLocation().isSelected()))	){
			toReturn=true;
		}
		return toReturn;
	}
	public boolean isAttributeValueSet(){
		boolean isSet =true;
		if(getProcessingNotes().isSelected() &&
				!StringUtils.hasText(((String[])getProcessingNotes().getAttribute())[0])){
			isSet=false;
		}
		if(getRgaNumber().isSelected() &&
				!StringUtils.hasText(((String[])getRgaNumber().getAttribute())[0])){
			isSet=false;
		}
		return isSet;
	}

	private void initDomainRepository() {
        BeanLocator beanLocator = new BeanLocator();
        this.lovRepository = (LovRepository) beanLocator.lookupBean("lovRepository");
    }
	public Map<String, String> getListOfValues(String className){
		Map<String, String> listOfValues = new HashMap<String, String>();
		if(this.lovRepository == null) {
			initDomainRepository();
		}
		List<ListOfValues> list;
        list = this.lovRepository.findAllActive(className);
        if(list!=null && list.size()>0){
            for (Iterator<ListOfValues> iterator = list.iterator(); iterator.hasNext();) {
                ListOfValues reason = iterator.next();
                listOfValues.put(reason.getCode(), reason.getDescription());
            }
        }
		return listOfValues;
	}

	private void initUserRepository() {
        BeanLocator beanLocator = new BeanLocator();
        this.userRepository = (UserRepository) beanLocator.lookupBean("userRepository");
    }
	public Map<Long, String> getCommonTechnicians(Long dealerId,String businessUnit){
		Map<Long, String> technicians = new HashMap<Long, String>();
		if(this.userRepository == null) {
			initUserRepository();
		}
		technicians = this.userRepository.findTechnicianForDealer(dealerId,businessUnit);	
		return technicians;
	}

	private void initCarrierRepository() {
        BeanLocator beanLocator = new BeanLocator();
        this.carrierRepository = (CarrierRepository) beanLocator.lookupBean("carrierRepository");
    }

	private void initLocationRepository() {
        BeanLocator beanLocator = new BeanLocator();
        this.locationRepository = (LocationRepository) beanLocator.lookupBean("locationRepository");
    }

	public List<Carrier> getCarriers(){
		if(this.carrierRepository == null){
			initCarrierRepository();
		}
		return this.carrierRepository.findAllCarriers();
	}



	public void setLovObjects(){
		if(this.lovRepository == null) {
            initDomainRepository();
        }
		if(((AccountabilityCode)getAccountabilityCode().getAttribute()).getCode()!=null){
		getAccountabilityCode().setAttribute
		(this.lovRepository.findByCode("AccountabilityCode",((AccountabilityCode)getAccountabilityCode().getAttribute()).getCode()));
		}
		if(((RejectionReason)getRejectionReason().getAttribute()).getCode()!=null){
			getRejectionReason().setAttribute(this.lovRepository.findByCode("RejectionReason",((RejectionReason)getRejectionReason().getAttribute()).getCode()));
			}
		if(((AcceptanceReason)getAcceptanceReason().getAttribute()).getCode()!=null){
			getAcceptanceReason().setAttribute(this.lovRepository.findByCode("AcceptanceReason",((AcceptanceReason)getAcceptanceReason().getAttribute()).getCode()));
			}
		if(((RecoveryClaimAcceptanceReason)getRecClaimAcceptanceReason().getAttribute()).getCode()!=null){
			getRecClaimAcceptanceReason().setAttribute(this.lovRepository.findByCode("RecoveryClaimAcceptanceReason",((RecoveryClaimAcceptanceReason)getRecClaimAcceptanceReason().getAttribute()).getCode()));
			}
		if(((RecoveryClaimRejectionReason)getRecClaimRejectionReason().getAttribute()).getCode()!=null){
			getRecClaimRejectionReason().setAttribute(this.lovRepository.findByCode("RecoveryClaimRejectionReason",((RecoveryClaimRejectionReason)getRecClaimRejectionReason().getAttribute()).getCode()));
			}

	}
	
	public void setLovObjects(String bu) {
		if (this.lovRepository == null) {
			initDomainRepository();
		}
		if (((AccountabilityCode) getAccountabilityCode().getAttribute()).getCode() != null) {
			getAccountabilityCode().setAttribute(
					this.lovRepository.findByCode("AccountabilityCode", ((AccountabilityCode) getAccountabilityCode()
							.getAttribute()).getCode(), bu));
		}
		if (((RejectionReason) getRejectionReason().getAttribute()).getCode() != null) {
			getRejectionReason().setAttribute(
					this.lovRepository.findByCode("RejectionReason", ((RejectionReason) getRejectionReason()
							.getAttribute()).getCode(), bu));
		}
		if (((AcceptanceReason) getAcceptanceReason().getAttribute()).getCode() != null) {
			getAcceptanceReason().setAttribute(
					this.lovRepository.findByCode("AcceptanceReason", ((AcceptanceReason) getAcceptanceReason()
							.getAttribute()).getCode(), bu));
		}
		if (((RecoveryClaimAcceptanceReason) getRecClaimAcceptanceReason().getAttribute()).getCode() != null) {
			getRecClaimAcceptanceReason().setAttribute(
					this.lovRepository.findByCode("RecoveryClaimAcceptanceReason",
							((RecoveryClaimAcceptanceReason) getRecClaimAcceptanceReason().getAttribute()).getCode(),
							bu));
		}
		if (((RecoveryClaimRejectionReason) getRecClaimRejectionReason().getAttribute()).getCode() != null) {
			getRecClaimRejectionReason()
					.setAttribute(
							this.lovRepository.findByCode("RecoveryClaimRejectionReason",
									((RecoveryClaimRejectionReason) getRecClaimRejectionReason().getAttribute())
											.getCode(), bu));
		}

	}

	public void setShippingInfo(){
		if(((Location)getLocation().getAttribute()).getCode()!=null){
			if(this.locationRepository == null){
				initLocationRepository();
			}
			getLocation().setAttribute(this.locationRepository.findByLocationCode(((Location)getLocation().getAttribute()).getCode()));
			}
		if(((Carrier)getCarrier().getAttribute()).getName()!=null){
			if(this.carrierRepository == null){
				initCarrierRepository();
			}
			getCarrier().setAttribute(this.carrierRepository.findCarrierByName(((Carrier)getCarrier().getAttribute()).getName()));
			}
	}
	public void setCommonTechnician(){
		if(this.userRepository == null) {
			initUserRepository();
		}
		if(((User)getTechnician().getAttribute()).getId()!=null){
			getTechnician().setAttribute
			(this.userRepository.findById(((User)getTechnician().getAttribute()).getId()));
			}
	}
	public MultiClaimAttributeMapper getRecClaimAcceptanceReason() {
		return recClaimAcceptanceReason;
	}
	public void setRecClaimAcceptanceReason(
			MultiClaimAttributeMapper recClaimAcceptanceReason) {
		this.recClaimAcceptanceReason = recClaimAcceptanceReason;
	}
	public MultiClaimAttributeMapper getRecClaimRejectionReason() {
		return recClaimRejectionReason;
	}
	public void setRecClaimRejectionReason(
			MultiClaimAttributeMapper recClaimRejectionReason) {
		this.recClaimRejectionReason = recClaimRejectionReason;
	}
	public MultiClaimAttributeMapper getRgaNumber() {
		return rgaNumber;
	}
	public void setRgaNumber(MultiClaimAttributeMapper rgaNumber) {
		this.rgaNumber = rgaNumber;
	}
	public MultiClaimAttributeMapper getLocation() {
		return location;
	}
	public void setLocation(MultiClaimAttributeMapper location) {
		this.location = location;
	}
	public MultiClaimAttributeMapper getCarrier() {
		return carrier;
	}
	public void setCarrier(MultiClaimAttributeMapper carrier) {
		this.carrier = carrier;
	}
	public void setCarrierRepository(CarrierRepository carrierRepository) {
		this.carrierRepository = carrierRepository;
	}
	public void setLocationRepository(LocationRepository locationRepository) {
		this.locationRepository = locationRepository;
	}

}

