package tavant.twms.web.partsreturn;

import com.domainlanguage.time.CalendarDate;
import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.StringUtils;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.partreturn.*;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.partreturn.PartReturnWorkList;
import tavant.twms.domain.configuration.ConfigOptionConstants;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 21/2/13
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class CEVATrackingInfo extends PartReturnInboxAction implements ConfigOptionConstants{

    private static final Logger logger = Logger.getLogger(CEVATrackingInfo.class);

    private Long carrierId;

    private String trackingNumber;

    private TaskViewService taskViewService;

    private Claim claim;

    private PartReturnProcessingService partReturnProcessingService;

    
    private String cevaComments;

	public HttpServletRequest request;

    private List<ClaimWithPartBeans> claimWithPartBeansList = new ArrayList<ClaimWithPartBeans>();

    private List<Shipment> shipments = new ArrayList<Shipment>();

    private WpraService wpraService;

    private Wpra wpra;

    private Date shipmentDate;

    private Integer hour;

    private Integer minute;

    private ShipmentRepository shipmentRepository;

    private CarrierRepository carrierRepository;

    /* Todo: This is a temporary quick patch.
       We need to figure out how the datetimepicker should be configured so that the date format will be unique for all requests.
       Tried with putting displayFormat attribute but that is not working properly.
     */
    private CalendarDate shipmentCalenderDate;

    @Override
    public void validate() {
        if ("Submit".equals(transitionTaken)) {
           //Validate for tracking information

            if (!StringUtils.hasText(getCevaComments())) {
                addActionError("error.manageFleetCoverage.commentsMandatory");
            }

            //validate for shipment date
            if (!StringUtils.hasText(getTrackingNumber())) {
                addActionError("error.partReturn.trackingInfo.mandatory");
            }

            if (shipmentCalenderDate == null ) {
                addActionError("error.partReturnConfiguration.shipmentDateIsRequired");
            }

            if(carrierId == null || carrierId.equals("")){
                addActionError("error.sra.contract.carrier");
            }

            if (shipmentDate != null) {
                if(hour == -1){
                    addActionError("error.field.hour");
                }
                if(minute == -1){
                    addActionError("error.field.minute");
                }

                if(hour!=null && hour != -1){
                    this.shipmentDate.setHours(hour);
                }else{
                    this.shipmentDate.setHours(00);
                }
                if(minute!=null && minute != -1){
                    this.shipmentDate.setMinutes(minute);
                }else{
                    this.shipmentDate.setMinutes(00);
                }

                if(shipmentDate.getTime() < new Date().getTime()){
                    addActionError("Shipment Date cannot be in Past");
                }
            }
        }
        if (hasActionErrors() || hasFieldErrors()) {
            generateView();
        }
    }

    public CEVATrackingInfo() {
        super();
        setActionUrl("provideCEVATrackingInfo");
    }

    public void prepare() throws Exception {
        if(shipmentCalenderDate != null){
            shipmentDate =  new Date(shipmentCalenderDate.breachEncapsulationOf_year()-1900,shipmentCalenderDate.breachEncapsulationOf_month()-1,shipmentCalenderDate.breachEncapsulationOf_day());
        }
    }

    private Wpra getWpra(String id){
        return wpraService.findWpraById(id);
    }

    public String submitCevaTrackingInfo() {
        List<PartTaskBean> partTaskBeans = getSelectedPartTaskBeans();
        List<PartReturn> parts = getPartReturnsFromPartTaskBeans(partTaskBeans);
        List<TaskInstance> listOfInstances = findAllPartTasksForId(id);
        for(Shipment shipment : shipments ){
            shipment.setCevaComments(cevaComments);
            shipment.setTrackingId(trackingNumber);
            shipment.setShipmentDate(shipmentDate);
            Carrier carrier = carrierRepository.findCarrierById(carrierId);
            shipment.setCarrier(carrier);
           // shipmentRepository.updateShipment(shipment);
        }
        taskViewService.submitAllTaskInstances(listOfInstances, transitionTaken);
        //get all the parts from shipment
        for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
            if(partReplacedBean.isSelected()){
                OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();

                part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_SHIPPED.getStatus()
                            ,partReplacedBean.getCevaTracking()));
                updatePartStatus(part);
                getPartReplacedService().updateOEMPartReplaced(part);

            }
        }
        shipmentRepository.updateShipments(shipments);
        updatePartReceivedCount(getPartReplacedBeans());
        return resultingView();
    }

    private void updatePartReceivedCount(List<OEMPartReplacedBean> oemPartReplacedBeans) {
        if (ON_PART_SHIPPED.equalsIgnoreCase(
                getConfigParamService().getStringValue(ConfigName.PART_RETURN_STATUS_TO_BE_CONSIDERED_FOR_PRC_MAX_QTY.getName()))) {
            for (OEMPartReplacedBean oemPartReplacedBean : oemPartReplacedBeans) {
                if (oemPartReplacedBean.getOemPartReplaced() != null
                        && oemPartReplacedBean.getOemPartReplaced().getItemReference() != null) {
                    PartReturnConfiguration partReturnConfiguration = oemPartReplacedBean.getOemPartReplaced().getPartReturnConfiguration();
                    if (partReturnConfiguration!=null &&
                            oemPartReplacedBean.getOemPartReplaced().getPartReturns() != null
                            && !oemPartReplacedBean.getOemPartReplaced().getPartReturns().isEmpty()
                            && partReturnConfiguration.getMaxQuantity() != null) {
                        int quantityReceived = partReturnConfiguration.getQuantityReceived();
                        for(PartTaskBean partTaskBean : oemPartReplacedBean.getPartReturnTasks()) {
                            if(partTaskBean.isSelected())
                                quantityReceived ++;
                        }
                        partReturnConfiguration.setQuantityReceived(quantityReceived);
                        getPartReturnService().updatePartReturnConfiguration(partReturnConfiguration);
                    }
                }
            }
        }
    }

    public String cancelReturnProcess(){
        // This should end the return process.But since we have moved the parts or we can call it as tasks to CEVA_TRACKING inbox just
        //getting tasks for this inbox will do.
        WorkListCriteria criteria = createCriteria();
        criteria.setIdentifier(id);
        //this should end the wpra generated inbox task too
        List<TaskInstance> instancesToEnd = getPartReturnWorkListItemService().findShipmentGeneratedTasksForWPRA(criteria);
        taskViewService.submitAllTaskInstances(instancesToEnd, transitionTaken);
        List<Long> partReturnIds = new ArrayList<Long>();
        for(TaskInstance instance : instancesToEnd){
            PartReturn prtRet = (PartReturn) instance.getVariable("partReturn");
            if(prtRet != null){
                partReturnIds.add(prtRet.getId());
            }
        }
        if(partReturnIds.size() > 0){
            //end wpra generated task too
            partReturnProcessingService.endTasksForParts(partReturnIds, WorkflowConstants.GENERATED_WPRA);
        }
        for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
            if(partReplacedBean.isSelected()){
                OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                part.setPartAction1(new PartReturnAction(PartReturnStatus.REMOVED_BY_PROCESSOR.getStatus()
                        ,partReplacedBean.getCevaTracking()));
                part.setPartToBeReturned(false);
                partReplacedBean.partReturnTasks.get(0).getPartReturn().setTriggerStatus(PartReturnTaskTriggerStatus.ENDED);
                updatePartStatus(part);
                part.setWpra(null);
                part.setShipment(null);
                part.setPartReturns(new ArrayList<PartReturn>());
                getPartReplacedService().updateOEMPartReplaced(part);

            }
        }
        addActionMessage("message.itemStatus.updated");
        return SUCCESS;
    }

    public void setPartReturnProcessingService(PartReturnProcessingService partReturnProcessingService) {
        this.partReturnProcessingService = partReturnProcessingService;
    }


    // This is only for the parts receipt preview. Need a better way ?
    public Shipment getShipmentFromPartBeans() {
        return getClaimWithPartBeans().get(0).getPartReplacedBeans().get(0).getPartTaskBean().getPartReturn().getShipment();
    }

    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    public List<ClaimWithPartBeans> getClaimWithPartBeansList() {
        return claimWithPartBeansList;
    }

    public void setClaimWithPartBeansList(List<ClaimWithPartBeans> claimWithPartBeansList) {
        this.claimWithPartBeansList = claimWithPartBeansList;
    }

    //My code goes here.
    //let's make the work list then we will proceed next action calls
    //ok, so worklist method should pick all task for shipment generated and group by their WPRA number
    //probably we don't need BU or wpra check sice this action will never be called for other bu
    @Override
    protected PartReturnWorkList getWorkList() {
        return getPartReturnWorkListService().getCEVAWorkListByWpra(createCriteria());
    }

    public List<Carrier> getShipperCompanies(){
        List<Carrier> shippers = new ArrayList<Carrier>();
        shippers.addAll(carrierRepository.findAllCarriers());
        return shippers;
    }

    @Override
    protected List<TaskInstance> findAllPartTasksForId(String id) {
        logger.debug("Find Shipment Generated Tasks for WPRA[" + id + "]");
        WorkListCriteria criteria = createCriteria();
        criteria.setIdentifier(id);
        //it should return all the shipment having the same wpra number...
        //Set the shipments also, that would be easy to deal with the jsps.
        setShipments(getPartReturnWorkListItemService().findShipmentsForWPRA(id));
        wpra = wpraService.findWpraById(id);
        return getPartReturnWorkListItemService().findShipmentGeneratedTasksForWPRA(criteria);
    }

    @Override
    protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.wpra",
                "wpra.wpraNumber", 15, "string","wpra.wpraNumber", true, false, false, false));
        tableHeadData.add(new SummaryTableColumn("columnTitle.common.wpra", "wpra.id", 10,
                "number", "wpra.id", false, true, true, false));
        return tableHeadData;
    }

    public void setTaskViewService(TaskViewService taskViewService) {
        this.taskViewService = taskViewService;
    }

    public List<Shipment> getShipments() {
        return shipments;
    }

    public void setShipments(List<Shipment> shipments) {
        this.shipments = shipments;
    }

    public WpraService getWpraService() {
        return wpraService;
    }

    public void setWpraService(WpraService wpraService) {
        this.wpraService = wpraService;
    }

    public Wpra getWpra() {
        return wpra;
    }

    public void setWpra(Wpra wpra) {
        this.wpra = wpra;
    }

    public Date getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Date shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public CalendarDate getShipmentCalenderDate() {
        return shipmentCalenderDate;
    }

    public void setShipmentCalenderDate(CalendarDate shipmentCalenderDate) {
        this.shipmentCalenderDate = shipmentCalenderDate;
    }

    public Integer getHour() {
        return hour;
    }

    public void setHour(Integer hour) {
        this.hour = hour;
    }

    public Integer getMinute() {
        return minute;
    }

    public void setMinute(Integer minute) {
        this.minute = minute;
    }

    public Long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(Long carrierId) {
        this.carrierId = carrierId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public ShipmentRepository getShipmentRepository() {
        return shipmentRepository;
    }

    public void setShipmentRepository(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }

    public CarrierRepository getCarrierRepository() {
        return carrierRepository;
    }

    public void setCarrierRepository(CarrierRepository carrierRepository) {
        this.carrierRepository = carrierRepository;
    }
    
    public String getCevaComments() {
		return cevaComments;
	}

	public void setCevaComments(String cevaComments) {
		this.cevaComments = cevaComments;
	}
}
