package tavant.twms.web.partsreturn;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigOptionConstants;
import tavant.twms.domain.orgmodel.EventState;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.partreturn.*;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.web.common.fop.FopHandler;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.xforms.TaskViewService;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.partreturn.PartReturnWorkList;

import com.opensymphony.xwork2.Preparable;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 13/2/13
 * Time: 11:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class ShipmentGeneratedWpraViewAction extends PartReturnInboxAction implements Preparable, ServletRequestAware, ConfigOptionConstants {

    private TaskViewService taskViewService;

    private WarehouseService warehouseService;

    private Claim claim;

    private PartReturnProcessingService partReturnProcessingService;

    private String comments;

    private List<ShipmentLoadDimension> shipmentLoadDimension = new ArrayList<ShipmentLoadDimension>();

    private ShipmentService shipmentService;

    private ShipmentRepository shipmentRepository;

    public static final String SHIPMENT_GENERATED_TASK_NAME = "Shipment Generated";

    public HttpServletRequest request;

    private List<ClaimWithPartBeans> claimWithPartBeansList = new ArrayList<ClaimWithPartBeans>();

    private List<OEMPartReplacedBean> selectedPartReplacedToAdd = new ArrayList<OEMPartReplacedBean>();

    private boolean isDealer = false;

    private List<Shipment> shipments = new ArrayList<Shipment>();

    private WpraService wpraService;

    private static final Logger logger = Logger.getLogger(ShipmentGeneratedWpraViewAction.class);

    @Override
    public void validate() {
        if (!"Remove Part".equals(transitionTaken)
                && !"Submit".equals(transitionTaken) && !"SubmitShipment".equals(transitionTaken)) {
            List<PartTaskBean> partTasks = getSelectedPartTaskBeans(
                    selectedPartReplacedToAdd, false);
            if (partTasks.size() == 0) {
                addActionError("error.partReturnConfiguration.noPartSelected");
            }
            if (!hasActionErrors()) {
                validateData();
            }
        } else if ("Submit".equals(transitionTaken) || "SubmitShipment".equals(transitionTaken)) {
            List<PartTaskBean> selectedpartTasks = getSelectedPartTaskBeans(
                    getPartReplacedBeans(), false);
            if (selectedpartTasks.size() == 0) {
                addActionError("error.partReturnConfiguration.noPartSelected");
            }
            if (!StringUtils.hasText(getComments())) {
                addActionError("error.manageFleetCoverage.commentsMandatory");
            }

            boolean flagForErrorFound = false;
            boolean isLoadTypeNotSelected = false;
            if(shipmentLoadDimension.size() == 0 && !isBuConfigAMER()){
                addActionError("message.partReturn.shipment.provideLoadDimesion");
            }
            if(shipmentLoadDimension.size() > 0 && !isBuConfigAMER()){
                for(ShipmentLoadDimension loadDimension : shipmentLoadDimension){
                    if(null != loadDimension){
                        if(!StringUtils.hasText(loadDimension.getLoadType())){
                        	isLoadTypeNotSelected  = true;
                        }
                        if(!isPositiveNumber(loadDimension.getBreadth())  
                          	  || !isPositiveNumber(loadDimension.getLength())
                          	  || !isPositiveNumber(loadDimension.getHeight())
                          	  || !isPositiveNumber(loadDimension.getWeight())){
                            flagForErrorFound = true;
                        }
                        if(loadDimension.getShipment() == null || (loadDimension.getShipment() != null && loadDimension.getShipment().getId() == null)){
                            addActionError("message.partReturn.shipment.provideShipment");
                        }

                        }
                        }
                //If a LoadDimension record is getting removed from top or middle it is set as null
                List<ShipmentLoadDimension> nullRemovedList = new ArrayList<ShipmentLoadDimension>();
                for (ShipmentLoadDimension sld : shipmentLoadDimension) {
                	if (sld != null) {
                		nullRemovedList.add(sld);
                        }
                    }
                shipmentLoadDimension.clear();
                shipmentLoadDimension.addAll(nullRemovedList);
            }

            if(flagForErrorFound){
                addActionError("message.partReturn.shipment.provideLoadDimesion");
            }
            if(isLoadTypeNotSelected){
            	addActionError("message.partReturn.shipment.provideLoadType");
            }

            if(!hasActionErrors() && !receiversSetForWarehouse()){
                addActionError("error.NoReceiversPresent.ForWarehouse");
            }

        }
        if (hasActionErrors() || hasFieldErrors()) {
            generateView();

        }
    }

    private boolean receiversSetForWarehouse() {
        boolean receiverSet = false;
        if (!CollectionUtils.isEmpty(getPartReplacedBeans()) && getPartReplacedBeans().get(0) != null) {
            try {
                if(getPartReplacedBeans().get(0).getOemPartReplaced().isReturnDirectlyToSupplier()){
                    receiverSet = true;
                }
                Location location = getPartReplacedBeans().get(0).getPartReturnTasks().get(0).getPartReturn().getReturnLocation();
                if ((warehouseService.getReceiverAtLocation(location)) != null) {
                    receiverSet = true;
                }
            } catch (Exception e) {
                logger.debug("Invalid data" );
            }
        }
        return receiverSet;
    }

    public boolean isPageReadOnly() {
        return false;
    }

    public boolean isPageReadOnlyAdditional() {
        boolean isReadOnlyDealer = false;
        Set<Role> roles = getLoggedInUser().getRoles();
        for (Role role : roles) {
            if (role.getName().equalsIgnoreCase(Role.READ_ONLY_DEALER)) {
                isReadOnlyDealer = true;
                break;
            }
        }
        return isReadOnlyDealer;
    }

    public void validateData() {
        for (OEMPartReplacedBean partReplacedBean : getSelectedPartReplacedToAdd()) {
            if (partReplacedBean.isSelected()) {
                if (partReplacedBean.getPartReturnTasks() != null
                        && (isTaskShipmentGenerated()))
                    if ((partReplacedBean.getCannotShip() == 0 && partReplacedBean
                            .getShip() == 0)) {
                        addActionError("error.partReturnConfiguration.noPartSelected");
                    } else if (partReplacedBean.getCannotShip()
                            + partReplacedBean.getShip() > partReplacedBean.partReturnTasks
                            .size()) {
                        addActionError("error.partReturnConfiguration.excessPartsShipmentGenerate");
                    }
            }
        }
    }

    public ShipmentGeneratedWpraViewAction() {
        super();
        setActionUrl("wpraView_shipmentGenerated");
    }

    public void prepare() throws Exception {
        if (isLoggedInUserADealer()){
            isDealer = true;
        }
    }

   /* public String removeParts() {
        List<PartTaskBean> partTaskBeans = getSelectedPartTaskBeans();
        List<PartReturn> partsList = getPartReturnsFromPartTaskBeans(partTaskBeans);
        Shipment shipment = partsList.get(0).getShipment();
        List<String> parts = new ArrayList<String>();
        partReturnProcessingService.removePartsFromItsShipment(getPartReturnsFromPartTaskBeans(partTaskBeans),
                getTasksFromPartTaskBeans(partTaskBeans), transitionTaken);
        for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
            if(partReplacedBean.isSelected()){
                OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                part.setPartAction1(new PartReturnAction(PartReturnStatus.PART_TO_BE_SHIPPED.getStatus()
                        ,(partReplacedBean.getCountOfToBeShipped())));
                part.setPartAction2(new PartReturnAction(PartReturnStatus.CANNOT_BE_SHIPPED.getStatus()
                        ,(partReplacedBean.getCountOfCannotBeShipped())));
                part.setComments(comments);
                updatePartStatus(part);
                getPartReplacedService().updateOEMPartReplaced(part);
                parts.add(part.getItemReference().getReferredItem().getNumber());
            }
        }
        addActionMessage("label.part.shipmentRemoved", parts);
        // return resultingView();
        return SUCCESS;
    }*/

    private Wpra getWpra(String id){
        return wpraService.findWpraById(id);
    }

    // FIX ME :- Need partReturnProcessingService for this as well ?
    public String shipParts() {

        List<PartTaskBean> partTaskBeans = getSelectedPartTaskBeans();
        List<PartReturn> parts = getPartReturnsFromPartTaskBeans(partTaskBeans);
        setShipments(getPartReturnWorkListItemService().findShipmentsForWPRA(id));
        for(Shipment shipment : shipments ){
            for(ShipmentLoadDimension loadDimension : shipmentLoadDimension){
                if(loadDimension.getShipment().getId().equals(shipment.getId()))  {
                    shipment.getShipmentLoadDimension().add(loadDimension);
                }
            }
            shipment.setComments(comments);
        }

        shipmentService.updateShipmentsWithLoadDimension(shipments);
        taskViewService.submitAllTaskInstances(getTasksFromPartTaskBeans(partTaskBeans), transitionTaken);
        for(OEMPartReplacedBean partReplacedBean : getPartReplacedBeans()){
            if(partReplacedBean.isSelected()){
                OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                if(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER.equals(getTaskName())){
                    part.setPartAction1(new PartReturnAction(PartReturnStatus.NMHG_TO_DEALER_PART_SHIPPED.getStatus()
                            ,partReplacedBean.getCountOfShipmentGenerated()));
                }
                else{
                    part.setPartAction1(new PartReturnAction(PartReturnStatus.WAITING_FOR_CEVA_TRACKING_INFO.getStatus()
                            ,partReplacedBean.getCountOfShipmentGenerated()));
                }
                part.getPartAction1().setWpraNumber(id);
                part.setPartAction2(null);
                part.setComments(comments);
                part.setWpra(getWpra(id));
                updatePartStatus(part);
                getPartReplacedService().updateOEMPartReplaced(part);

            }
        }
        //received count will be updated after tracking info is provided.
        //updatePartReceivedCount(getPartReplacedBeans());

        //create event for shipped parts
       // createEvent(parts);
        //end of call to method to create event

        return resultingView();
    }

   /* public String fetchAllDueAndOverdueParts() {
        WorkListCriteria criteria = createCriteria();
        criteria.setIdentifier(getId());
        claimWithPartBeansList = preparePreviewAndReturn(getPartReturnWorkListItemService()
                .findAllDueAndOverduePartTasksForLocation(criteria), claimWithPartBeansList);
        return SUCCESS;
    }
*/
    /*public String addParts() {
        validateForDueParts();

        if(! hasActionErrors()){
            List<PartTaskBean> selectedPartTasks = getSelectedPartTaskBeans(selectedPartReplacedToAdd);
            List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();
            List<PartReturn> parts = new ArrayList<PartReturn>();
            for (PartTaskBean bean : getBeansForShipmentGeneration(selectedPartTasks)) {
                taskInstances.add(bean.getTask());
                parts.add(bean.getPartReturn());
                bean.getPartReturn().setTriggerStatus(PartReturnTaskTriggerStatus.SHIPMENT_GENERATED);
            }
            partReturnProcessingService.addPartsToShipment(new Long(getId()), parts, taskInstances, transitionTaken);
            List<PartTaskBean> partTasksToEnd = getBeansToBeEnded(selectedPartTasks);
            for (PartTaskBean partTaskBean : partTasksToEnd) {
                getWorkListItemService().endTaskWithTransition(partTaskBean.getTask(), "toEnd");
                partTaskBean.getPartReturn().setTriggerStatus(PartReturnTaskTriggerStatus.ENDED);
            }
            for (OEMPartReplacedBean partReplacedBean : selectedPartReplacedToAdd) {
                if (partReplacedBean.isSelected()) {
                    OEMPartReplaced part = partReplacedBean.partReturnTasks.get(0).getPart();
                    part.setPartAction1(new PartReturnAction(PartReturnStatus.SHIPMENT_GENERATED.getStatus()
                            , partReplacedBean.getCountOfShip()));
                    part.getPartAction1().setShipmentId(getId());
                    part.setPartAction2(null);
                    part.setComments(comments);
                    updatePartStatus(part);
                    getPartReplacedService().updateOEMPartReplaced(part);
                }
            }
        }
        selectedPartReplacedToAdd = new ArrayList<OEMPartReplacedBean>();
        claimWithPartBeansList = new ArrayList<ClaimWithPartBeans>();
        return SUCCESS;
    }*/

  /*  private void validateForDueParts() {
        if (!hasActionErrors()) {
            for (OEMPartReplacedBean partReplacedBean : selectedPartReplacedToAdd) {
                if (partReplacedBean.isSelected()) {
                    if (partReplacedBean.getCannotShip() == 0 && partReplacedBean.getShip() == 0) {
                        addActionError("error.partReturnConfiguration.noPartSelected");
                    } else if (partReplacedBean.getCannotShip() + partReplacedBean.getShip() > partReplacedBean
                            .getToBeShipped()) {
                        addActionError("error.partReturnConfiguration.excessParts");
                    }
                }
            }
        }

    }*/

    public List<OEMPartReplacedBean> getSelectedPartReplacedToAdd() {
        return selectedPartReplacedToAdd;
    }

    public List<OEMPartReplacedBean> getSelectedPartReplacedBeansAfterSetting(){
        for (OEMPartReplacedBean partReplacedBean : this.selectedPartReplacedToAdd) {
            partReplacedBean.setSelected(false);
            for(PartTaskBean partTaskBean : partReplacedBean.getPartReturnTasks()){
                if(partTaskBean.isSelected())
                {   partReplacedBean.setSelected(true);
                    break;
                }
            }
        }
        return this.selectedPartReplacedToAdd;
    }

    public void setSelectedPartReplacedToAdd(List<OEMPartReplacedBean> selectedPartReplacedToAdd) {
        this.selectedPartReplacedToAdd = selectedPartReplacedToAdd;
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


    /**
     * This is the list of part return from which we are going to create an event.
     *
     * @param partReturnList
     */
    private void createEvent(List<PartReturn> partReturnList)
    {
        HashMap<String, Integer> partsShipped = new HashMap<String, Integer>();
        PartReturn currentPartReturn = null;
        String currentItemNumber;
        Set<String> keySet=null;
        StringBuffer finalPartNumberString;
        String finalPartNumberValue;
        HashMap<String,Object> eventHashMap = new HashMap<String, Object>();
        Long partReturnId = null;
        if(partReturnList != null && partReturnList.size() > 0)
        {
            for(Iterator<PartReturn> partReturnIte = partReturnList.iterator(); partReturnIte.hasNext();)
            {
                currentPartReturn = partReturnIte.next();
                if(currentPartReturn != null && currentPartReturn.getOemPartReplaced() != null && currentPartReturn.getOemPartReplaced().getItemReference() != null && currentPartReturn.getOemPartReplaced().getItemReference().getReferredItem() != null)
                {
                    if(partReturnId == null)
                    {
                        partReturnId = currentPartReturn.getId();
                    }
                    currentItemNumber = currentPartReturn.getOemPartReplaced().getItemReference().getReferredItem().getNumber();
                    if(partsShipped.containsKey(currentItemNumber))
                    {
                        //since this part already exist we will merely update quantity
                        partsShipped.put(currentItemNumber, new Integer(partsShipped.get(currentItemNumber) + 1));
                    }
                    else
                    {
                        //since part number doesn't exist we will make an entry with quantity as one.
                        partsShipped.put(currentItemNumber, new Integer(1));
                    }
                }
            }

            //now that we are done with updating part numbers and quantities lets just create a string out of it
            keySet = partsShipped.keySet();
            finalPartNumberString = new StringBuffer();
            for(Iterator<String> ite=keySet.iterator(); ite.hasNext();)
            {
                currentItemNumber = ite.next();
                finalPartNumberString.append(currentItemNumber);
                finalPartNumberString.append(" :: ");
                finalPartNumberString.append(partsShipped.get(currentItemNumber));
                finalPartNumberString.append(", ");
            }

            //remove the last comma from the string buffer
            finalPartNumberValue = finalPartNumberString.substring(0, (finalPartNumberString.length()-2));

            //set the values in hashMap
            eventHashMap.put("claimId",claim.getId().toString());
            eventHashMap.put("partNumberString",finalPartNumberValue);
            eventHashMap.put("taskInstanceId", partReturnId.toString());
            eventHashMap.put("subject", "Claim - " + claim.getClaimNumber() + " needs attention");
            //create the event
            getEventService().createEvent("partReturn", EventState.PART_RETURN_SHIPPED, eventHashMap);
        }
    }
    @Override
    public Map<ShipmentStatus,String> getShipmentStatusList() {
        this.shipmentStatus=new HashMap<ShipmentStatus,String>();
        this.shipmentStatus.put(ShipmentStatus.GENERATE_SHIPMENT,ShipmentStatus.GENERATE_SHIPMENT.getStatus());
        return this.shipmentStatus;
    }

    public void setComments(String remarks) {
        this.comments = remarks;
    }

    public boolean getIsShipmentGeneratedTask() {
        if (SHIPMENT_GENERATED_TASK_NAME.equals(getTaskName()))
            return true;
        else
            return false;
    }

    // getters required for paramsprepareparams
    public String getComments() {
        return comments;
    }

    public void setPartReturnProcessingService(PartReturnProcessingService partReturnProcessingService) {
        this.partReturnProcessingService = partReturnProcessingService;
    }


    // This is only for the parts receipt preview. Need a better way ?
    public Shipment getShipmentFromPartBeans() {
        return getClaimWithPartBeans().get(0).getPartReplacedBeans().get(0).getPartTaskBean().getPartReturn()
                .getShipment();
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

    public WarehouseService getWarehouseService() {
        return warehouseService;
    }

    public void setWarehouseService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public List<String> getLoadTypes(){
        List<String> loadTypes = new ArrayList<String>();
        for(String loadType : LoadType.getAllLoadType()){
            loadTypes.add(getText(loadType));
        }
        return loadTypes;

    }

    public boolean isDealer() {
        return isDealer;
    }

    public void setDealer(boolean isDealer) {
        this.isDealer = isDealer;
    }

    @Override
    public boolean getIsSwitchViewEnabled() {
        return (getSwitchButtonActionName() != null && getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName()));
    }

    //My code goes here.
    //let's make the work list then we will proceed next action calls
    //ok, so worklist method should pick all task for shipment generated and group by their WPRA number
    //probably we don't need BU or wpra check sice this action will never be called for other bu
    @Override
    protected PartReturnWorkList getWorkList() {
        return getPartReturnWorkListService().getShipmentGeneratedWorkListByWpra(createCriteria());
    }

    @Override
    protected List<TaskInstance> findAllPartTasksForId(String id) {
        logger.debug("Find Shipment Generated Tasks for WPRA[" + id + "]");
        WorkListCriteria criteria = createCriteria();
        criteria.setIdentifier(id);
        //it should return all the shipment having the same wpra number...
        //Set the shipments also, that would be easy to deal with the jsps.
        setShipments(getPartReturnWorkListItemService().findShipmentsForWPRA(id));
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

    public String fetchShipmentForTagForWpra() {
        ServletContext context = ServletActionContext.getServletContext();
        //we are getting the wpra id here, get all the shipments for the id
        //setShipments(getPartReturnWorkListItemService().findShipmentsForWPRA(id));
        PartsWithDealerBeans parts = populateShipmentsForPrintTag();
        ShipmentTagVO shipmentTag = getPrintShipmentDetails(parts);
        shipmentTag.setLabelShippedDate(getText("label.partReturnConfiguration.shipmentDate"));
        FopHandler.transformXMLString("/partShipmentTag.xsl", getXMLString(shipmentTag), context, response);
        return null;
    }

    private PartsWithDealerBeans populateShipmentsForPrintTag() {
        List<TaskInstance> taskInstances = findAllPartTasksForId(getShipmentIdString());
        List<PartTaskBean> beans = new ArrayList<PartTaskBean>();
        for (TaskInstance instance : taskInstances) {
            beans.add(new PartTaskBean(instance));
        }

        StringBuilder shipmentId = new StringBuilder();
        for(Shipment sips : shipments){
            shipmentId.append(sips.getId());
        }

        return new PartsWithDealerBeans(beans, shipmentId.toString());
    }

    protected ShipmentTagVO getPrintShipmentDetails(PartsWithDealerBeans shipmentsGeneratedForPrint) {
        ShipmentTagVO shipmentTag = new ShipmentTagVO();
        List<ClaimWithPartVO> partDetails = new ArrayList<ClaimWithPartVO>();
        Map<Claim, List<PartTaskBean>> distinctClmMap = new HashMap<Claim, List<PartTaskBean>>();
        List<PartTaskBean> claimList = null;

        for (PartTaskBean partTaskBean : shipmentsGeneratedForPrint.getPartTaskBeans()) {
            if (distinctClmMap.get(partTaskBean.getClaim()) == null) {
                claimList = new ArrayList<PartTaskBean>();
                claimList.add(partTaskBean);
                distinctClmMap.put(partTaskBean.getClaim(), claimList);
            } else {
                distinctClmMap.get(partTaskBean.getClaim()).add(partTaskBean);
            }
        }

        ClaimWithPartVO claimWithPart;
        PartReturnVO partReturnVO;
        for (Claim claim : distinctClmMap.keySet()) {

            Map<String, PartReturnVO> parts = new HashMap<String, PartReturnVO>();

            for (PartTaskBean partTaskBean : distinctClmMap.get(claim)) {

                if (partTaskBean.getClaim().getId() == claim.getId()) {
                    ClaimVO claimVo = new ClaimVO();
                    populateClaim(claim, claimVo);

                    OEMPartReplaced part = partTaskBean.getPart();
                    String partNumber = part.getItemReference().getReferredItem().getBrandItemNumber(claim.getBrand());
                    if (parts.get(part.getItemReference().getReferredItem().getBrandItemNumber(claim.getBrand())) == null) {
                        claimWithPart = new ClaimWithPartVO();
                        partReturnVO = new PartReturnVO(partNumber);
                        if(StringUtils.hasText(part.getSerialNumber())){
							partReturnVO.setComponentSerialNumber(part.getSerialNumber());
							}
                        partReturnVO.setNumberOfParts(1);
                        partReturnVO.setDescription(part.getItemReference().getReferredItem().getDescription());
                       
                        String dueDate= part.getPartReturns().get(0).getDueDate().toString("dd-MM-yyyy");
                        partReturnVO.setDueDate(dueDate);       
                        partReturnVO.setDueDateMessage(MessageFormat.format(getText("label.partShipmentTag.warrantyPartReturnTagMessages"), dueDate));
                        if(null != part.getPartReturns().get(0).getWpra()){
                            partReturnVO.setWpraNumber(part.getPartReturns().get(0).getWpra().getWpraNumber());
                        }
                        partReturnVO.setRmaNumber(part.getPartReturns().get(0).getRmaNumber());
                        if(null != part.getPartReturnConfiguration() && null != part.getPartReturnConfiguration().getPartReturnDefinition()){
                            partReturnVO.setReceiverInstructions(getStringValue(part.getPartReturnConfiguration().getPartReturnDefinition().getReceiverInstructions()));
                        }
                        partReturnVO.setProblemPartNumber(partNumber);
                        //vendor part Number
                       /* if(null != claim.getRecoveryInfo()){
                            List<RecoveryClaimInfo> recInfo = claim.getRecoveryInfo().getReplacedPartsRecovery();
                            for(RecoveryClaimInfo recIn : recInfo){
                                List<RecoverablePart> recParts = recIn.getRecoverableParts();
                                for(RecoverablePart recPart : recParts){
                                    if(null != recPart.getSupplierItem() && recPart.getOemPart().getItemReference().getReferredItem().getNumber().equals(part.getItemReference().getReferredItem().getNumber())){
                                        partReturnVO.setVendorPartNumber(recPart.getSupplierItem().getNumber());
                                        if(null != recPart.getSupplierPartReturns() && recPart.getSupplierPartReturns().size() >0){
                                            partReturnVO.setVendorRequestedDate(recPart.getSupplierPartReturns().get(0).getD().getCreatedOn());
                                        }
                                    }
                                }
                            }
                        }*/
                        parts.put(partNumber, partReturnVO);
                        claimWithPart.setPart(partReturnVO);
                        claimWithPart.setClaim(claimVo);
                        partDetails.add(claimWithPart);
                    } else {
                        partReturnVO = parts.get(partNumber);
                        if(part.getSerialNumber()!= null && !StringUtils.hasText(part.getSerialNumber())){
							partReturnVO.setComponentSerialNumber(part.getSerialNumber());
							}
                        partReturnVO.setNumberOfParts(partReturnVO.getNumberOfParts() + 1);
                        parts.put(partNumber, partReturnVO);
                    }
                }
            }
        }
        shipmentTag.setPartDetails(partDetails);
        OEMPartReplaced part = shipmentsGeneratedForPrint.getPartTaskBeans().get(0).getPart();
        PartReturn returnedPart = part.getPartReturns().get(0);
        ServiceProvider dealer = returnedPart.getReturnedBy() == null ? shipmentsGeneratedForPrint.getDealer() : returnedPart.getReturnedBy();
        shipmentTag.setShipment(getShipmentDetails());
        Location returnLocation =  returnedPart.getReturnLocation() == null ? shipmentsGeneratedForPrint.getReturnLocation() : returnedPart.getReturnLocation();
       
        ReturnAddressVO returnToAddress = getReturnToAddress(returnLocation.getAddress());
        AddressVO addressVO = returnToAddress.getAddress();
        if(StringUtils.hasText(returnLocation.getCode())){
          	 Warehouse warehouse = this.getWarehouseService().findByWarehouseCode(returnLocation.getCode());
          	 if(warehouse!=null && StringUtils.hasText(warehouse.getBusinessName())){
          		addressVO.setBusinessName(warehouse.getBusinessName());
          	 }
          	 if(warehouse!=null && StringUtils.hasText(warehouse.getContactPersonName())){
          		returnToAddress.setContactPersonName(warehouse.getContactPersonName());
          	 }
        }
        returnToAddress.setAddress(addressVO);
        shipmentTag.setReturnToAddress(returnToAddress);
        shipmentTag.setFrom(populateAddress(getAddressForShipmentTag(dealer)));
        shipmentTag.setDealerNumber(dealer.getServiceProviderNumber());
        shipmentTag.setLanguage(getLoggedInUser().getLocale().getLanguage());
        shipmentTag.setBusinessUnit(shipmentsGeneratedForPrint.getPartTaskBeans().get(0).getClaim().getBusinessUnitInfo().getName());
        if(null != part.getPartReturns().get(0).getWpra()){
            shipmentTag.setWpraNumber(part.getPartReturns().get(0).getWpra().getWpraNumber());
        }

        List<LoadInformation> loadinfos = new ArrayList<LoadInformation>();
        for(Shipment sips: shipments){
            for(ShipmentLoadDimension sipLoadInfo : sips.getShipmentLoadDimension()){
                LoadInformation loadInfo = new LoadInformation();
                loadInfo.setBreadth(sipLoadInfo.getBreadth());
                loadInfo.setHeight(sipLoadInfo.getHeight());
                loadInfo.setLen(sipLoadInfo.getLength());
                loadInfo.setLoadType(sipLoadInfo.getLoadType());
                loadInfo.setWeight(sipLoadInfo.getWeight());
                loadinfos.add(loadInfo);
            }
        }
        shipmentTag.setLoadinfos(loadinfos);
        return shipmentTag;
    }

    protected ShipmentVO getShipmentDetails(){
        ShipmentVO shipmentVO = new ShipmentVO();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
        String shipmentDate=formatter.format(shipments.get(0).getShipmentDate());  
        shipmentVO.setShipmentDate(shipmentDate);
        StringBuilder shipmentId = new StringBuilder();
        for(Shipment sips : shipments){
            shipmentId.append(sips.getId()).append(", ");
        }
        shipmentVO.setShipmentNumber(shipmentId.toString());
        shipmentVO.setTackingNumber(shipments.get(0).getTrackingId());
        //shipmentVO.setBarcode(shipments.get(0).getId().toString());
        shipmentVO.setSenderComments(shipments.get(0).getComments());
        shipmentVO.setPrintDate(clientDate);
      
        //check for the NUll Pointers
        return shipmentVO;
    }


    public List<ShipmentLoadDimension> getShipmentLoadDimension() {
        return shipmentLoadDimension;
    }

    public void setShipmentLoadDimension(List<ShipmentLoadDimension> shipmentLoadDimension) {
        this.shipmentLoadDimension = shipmentLoadDimension;
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

    public ShipmentService getShipmentService() {
        return shipmentService;
    }

    public void setShipmentService(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    public ShipmentRepository getShipmentRepository() {
        return shipmentRepository;
    }

    public void setShipmentRepository(ShipmentRepository shipmentRepository) {
        this.shipmentRepository = shipmentRepository;
    }
}
