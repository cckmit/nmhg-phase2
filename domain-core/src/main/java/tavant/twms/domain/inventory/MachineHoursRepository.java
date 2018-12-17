package tavant.twms.domain.inventory;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface MachineHoursRepository extends GenericRepository<MachineHours, Long> {

    MachineHours findByDateAndSourceType(MachineHours machineHours);
    
    MachineHours findByMonthAndSourceType(MachineHours machineHours);
    
    PageResult<MachineHours> findByFleetInventoryItemIdForTelemetry(ListCriteria listingCriteria,Long fleetInventoryItemId);
    
    MachineHours findByFleetInventoryItemIdAndSourceType(Long fleetInventoryItemId, SourceType sourceType);

}
