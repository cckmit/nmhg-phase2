package tavant.twms.domain.inventory;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class MachineHoursServiceImpl extends GenericServiceImpl<MachineHours, Long, Exception> implements MachineHoursService {

    private MachineHoursRepository machineHoursRepository;
    
    public void saveOrUpdate(MachineHours machineHours){
        MachineHours existingMachineHours = null;
        if(machineHours.getSourceType().equals(SourceType.TELEMETRY)){
            existingMachineHours= machineHoursRepository.findByMonthAndSourceType(machineHours);
        }
        if(machineHours.getSourceType().equals(SourceType.EXTERNAL_UPLOAD) || machineHours.getSourceType().equals(SourceType.INTERNAL_UPLOAD)){
            existingMachineHours= machineHoursRepository.findByDateAndSourceType(machineHours);
        }
        
        if(existingMachineHours!=null){
            existingMachineHours.setMtrReading(machineHours.getMtrReading());
            existingMachineHours.setMtrReadingDate(machineHours.getMtrReadingDate());
            machineHoursRepository.update(existingMachineHours);
        }else{
            machineHoursRepository.save(machineHours);
        }
    }
    
    public PageResult<MachineHours> findByFleetInventoryItemIdForTelemetry(ListCriteria listingCriteria,Long fleetInventoryItemId){
        return machineHoursRepository.findByFleetInventoryItemIdForTelemetry(listingCriteria,fleetInventoryItemId);
    }
    
    @Override
    public GenericRepository<MachineHours, Long> getRepository() {
        return machineHoursRepository;
    }

    public MachineHoursRepository getMachineHoursRepository() {
        return machineHoursRepository;
    }

    public void setMachineHoursRepository(MachineHoursRepository machineHoursRepository) {
        this.machineHoursRepository = machineHoursRepository;
    }
    
	public MachineHours findByFleetInventoryItemIdAndSourceType(Long fleetInventoryItemId, SourceType sourceType) {
		return machineHoursRepository.findByFleetInventoryItemIdAndSourceType(fleetInventoryItemId, sourceType);
	}

}
