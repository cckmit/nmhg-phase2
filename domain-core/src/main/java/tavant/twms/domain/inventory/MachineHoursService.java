package tavant.twms.domain.inventory;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.infra.GenericService;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public interface MachineHoursService extends GenericService<MachineHours, Long, Exception> {

    @Transactional(readOnly = false)
    void saveOrUpdate(MachineHours machineHours);
    
    PageResult<MachineHours> findByFleetInventoryItemIdForTelemetry(ListCriteria listingCriteria,Long fleetInventoryItemId);
    
    MachineHours findByFleetInventoryItemIdAndSourceType(Long fleetInventoryItemId, SourceType sourceType);
    
}
