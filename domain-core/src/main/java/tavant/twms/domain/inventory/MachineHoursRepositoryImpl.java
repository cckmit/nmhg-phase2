package tavant.twms.domain.inventory;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class MachineHoursRepositoryImpl extends GenericRepositoryImpl<MachineHours, Long> implements MachineHoursRepository {

    @SuppressWarnings("unchecked")
    public MachineHours findByDateAndSourceType(final MachineHours machineHours) {
        return (MachineHours) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                List<MachineHours> machineHoursList = session
                        .createQuery(
                                "from MachineHours machineHours  where machineHours.sourceType = :sourceType and (machineHours.fleetInventoryItemId = :fleetInventoryItemId or machineHours.inventoryItemId = :inventoryItemId) and (machineHours.fleetInventoryItemId is not null or machineHours.inventoryItemId is not null)  and machineHours.mtrReadingDate = :mtrReadingDate")
                        .setParameter("sourceType", machineHours.getSourceType()).setParameter("mtrReadingDate", machineHours.getMtrReadingDate())
                        .setParameter("fleetInventoryItemId", machineHours.getFleetInventoryItemId())
                        .setParameter("inventoryItemId", machineHours.getInventoryItemId()).list();
                if (machineHoursList != null && !machineHoursList.isEmpty()) {
                    return machineHoursList.get(0);
                } else {
                    return null;
                }
            };
        });
    }
    
    @SuppressWarnings("unchecked")
    public MachineHours findByMonthAndSourceType(final MachineHours machineHours) {
        return (MachineHours) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                List<MachineHours> machineHoursList = session
                        .createQuery(
                                "from MachineHours machineHours  where machineHours.sourceType = :sourceType and (machineHours.fleetInventoryItemId = :fleetInventoryItemId or machineHours.inventoryItemId = :inventoryItemId) and (machineHours.fleetInventoryItemId is not null or machineHours.inventoryItemId is not null)  and month(machineHours.mtrReadingDate) = :currentMonth")
                        .setParameter("sourceType", machineHours.getSourceType())
                        .setParameter("currentMonth", machineHours.getMtrReadingDate().getMonth()+1)
                        .setParameter("fleetInventoryItemId", machineHours.getFleetInventoryItemId())
                        .setParameter("inventoryItemId", machineHours.getInventoryItemId()).list();
                if (machineHoursList != null && !machineHoursList.isEmpty()) {
                    return machineHoursList.get(0);
                } else {
                    return null;
                }
            };
        });
    }
    
    public PageResult<MachineHours> findByFleetInventoryItemIdForTelemetry(final ListCriteria listCriteria, final Long fleetInventoryItemId){
        Map<String, Object> params = new HashMap<String, Object>();
       final StringBuilder baseQuery = new StringBuilder("from MachineHours machineHours where machineHours.fleetInventoryItemId = :fleetInventoryItemId and machineHours.sourceType = :sourceType");
           params.put("fleetInventoryItemId", fleetInventoryItemId);
           params.put("sourceType", SourceType.TELEMETRY);
           params.putAll(listCriteria.getParameterMap());
           if (listCriteria.isFilterCriteriaSpecified())
               baseQuery.append("and ").append(listCriteria.getParamterizedFilterCriteria());
           return findPageUsingQuery(baseQuery.toString(), listCriteria.getSortCriteriaString(), listCriteria.getPageSpecification(), params);
    }
    
    @SuppressWarnings("unchecked")
    public MachineHours findByFleetInventoryItemIdAndSourceType(final Long fleetInventoryItemId, final SourceType sourceType){
    	return (MachineHours) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                       	List<MachineHours> machineHours= session.createQuery("from MachineHours machineHours where machineHours.fleetInventoryItemId = :fleetInventoryItemId and machineHours.sourceType = :sourceType order by mtr_reading_date desc")
                    	 .setParameter("fleetInventoryItemId", fleetInventoryItemId).setParameter("sourceType", sourceType).list();
                    	return (machineHours!=null && !machineHours.isEmpty())?machineHours.get(0):null;
					};
                });
    }

}
