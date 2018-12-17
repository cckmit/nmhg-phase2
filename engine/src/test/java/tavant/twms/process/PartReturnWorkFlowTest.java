/**
 * 
 */
package tavant.twms.process;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.domain.partreturn.LocationRepository;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.partreturn.PartReturnStatus;
import tavant.twms.domain.partreturn.PaymentCondition;
import tavant.twms.infra.ProcessDeployableTestCase;

import com.domainlanguage.time.CalendarDate;

/**
 * @author pradipta.a
 * 
 */
public class PartReturnWorkFlowTest extends ProcessDeployableTestCase {

    private CatalogService catalogService;

    private static final String PART_RETURN_FLOW = "/PartsReturn/processdefinition.xml";

    ProcessDefinitionService processDefinitionService;

    ProcessInstance processInstance;

    ProcessDefinition processDefinition = null;

    UserRepository userRepository;

    LocationRepository locationRepository;

    public void testPartReturnCompleteFlow() throws CatalogException {

        InputStream processDefinitionStream = this.getClass().getResourceAsStream(PART_RETURN_FLOW);

        processDefinition = ProcessDefinition.parseXmlInputStream(processDefinitionStream);

        processDefinitionService.deploy(processDefinition);

        processInstance = processDefinition.createProcessInstance();
        Claim claim = getPopulatedClaim();
        PaymentCondition paymentCondition = new PaymentCondition("PAY_ON_RETURN");
        claim.getServiceInformation().getServiceDetail().getOemPartsReplaced().get(0)
                .getPartReturns().get(0).setPaymentCondition(paymentCondition);

        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", claim);
        contextInstance.setVariable("partReturn", claim.getServiceInformation().getServiceDetail()
                .getOemPartsReplaced().get(0).getPartReturns().get(0));

        // Check Due Parts
        Token token = processInstance.getRootToken();
        Node node = processDefinition.getNode("Due Parts for Shipment");
        token.setNode(node);
        PartReturn partReturn = (PartReturn) contextInstance.getVariable("partReturn");
        assertEquals(PartReturnStatus.PART_TO_BE_SHIPPED, partReturn.getStatus());

        // check Shipment Generated
        token.signal("Generate Shipment");
        node = processDefinition.getNode("Shipment Generated");
        assertEquals(node.getName(), token.getNode().getName());
        partReturn = (PartReturn) contextInstance.getVariable("partReturn");
        assertEquals(PartReturnStatus.SHIPMENT_GENERATED, partReturn.getStatus());

        // check Part Shipped

        token.signal("Submit");
        node = processDefinition.getNode("PartsShippedFork");
        assertEquals(node.getName(), token.getNode().getName());
        partReturn = (PartReturn) contextInstance.getVariable("partReturn");
        assertEquals(PartReturnStatus.PART_SHIPPED, partReturn.getStatus());

        // check Part Recieved
        Token partReceivedToken = token.findToken("Parts Received");
        assertNotNull(partReceivedToken);
        Token partShippedToken = token.findToken("Parts Shipped");
        partReceivedToken.signal("Received Due Parts");
        node = processDefinition.getNode("JoinAfterPartReceiverResponse");
        assertEquals(node.getName(), partReceivedToken.getNode().getName());
        // check end of part shipped
        assertTrue(partShippedToken.hasEnded());

    }

    /*
     * Kept on Hold public void testPartReturnRemovalFlow() throws
     * CatalogException { InputStream processDefinitionStream =
     * this.getClass().getResourceAsStream(PART_RETURN_FLOW);
     * 
     * processDefinition =
     * ProcessDefinition.parseXmlInputStream(processDefinitionStream);
     * 
     * processDefinitionService.deploy(processDefinition); processInstance =
     * processDefinition.createProcessInstance(); Claim claim =
     * getPopulatedClaim(); ContextInstance contextInstance =
     * processInstance.getContextInstance();
     * contextInstance.setVariable("claim", claim);
     * contextInstance.setVariable("partReturn",
     * claim.getServiceInformation().getServiceDetail()
     * .getOemPartsReplaced().get(0).getPartReturns().get(0)); Token rootToken =
     * processInstance.getRootToken(); Node node =
     * processDefinition.getNode("Due Parts for Shipment");
     * rootToken.setNode(node); processInstance.signal("Generate Shipment"); //
     * before Removal of Part Return PartReturn partReturn = (PartReturn)
     * contextInstance.getVariable("partReturn");
     * assertEquals(PartReturnStatus.SHIPMENT_GENERATED,
     * partReturn.getStatus()); // after removal of part Return
     * 
     * processInstance.signal("Remove Part");
     * assertEquals(PartReturnStatus.PART_TO_BE_SHIPPED,
     * partReturn.getStatus()); }
     */

    @SuppressWarnings("unchecked")
    public void testPartReturnForPayOnReturn() throws CatalogException {
        InputStream processDefinitionStream = this.getClass().getResourceAsStream(PART_RETURN_FLOW);

        processDefinition = ProcessDefinition.parseXmlInputStream(processDefinitionStream);

        processDefinitionService.deploy(processDefinition);
        processInstance = processDefinition.createProcessInstance();
        Claim claim = getPopulatedClaim();
        PaymentCondition paymentCondition = new PaymentCondition("PAY_ON_RETURN");
        PartReturn claimPartReturn = claim.getServiceInformation().getServiceDetail()
                .getOemPartsReplaced().get(0).getPartReturns().get(0);
        claimPartReturn.setPaymentCondition(paymentCondition);
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", claim);
        contextInstance.setVariable("partReturn", claimPartReturn);
        Token rootToken = processInstance.getRootToken();
        Node node = processDefinition.getNode("Due Parts for Shipment");
        rootToken.setNode(node);
        rootToken.signal("Generate Shipment");
        rootToken.signal("Submit");

        Token partReceivedToken = rootToken.findToken("Parts Received");
        TaskMgmtInstance taskMgmtInstance = processInstance.getTaskMgmtInstance();
        Collection unfinishedTasks = taskMgmtInstance.getUnfinishedTasks(partReceivedToken);
        assertEquals(unfinishedTasks.size(), 1);
        TaskInstance partReceivedTask = (TaskInstance) unfinishedTasks.iterator().next();
        partReceivedTask.setVariable("transition", "Send for Inspection");
        PartReturn partReturn = (PartReturn) processInstance.getContextInstance()
                .getVariable("partReturn");
        assertNotNull(partReturn);
        // before transition
        assertEquals(PartReturnStatus.PART_SHIPPED, partReturn.getStatus());
        partReceivedTask.end();
        partReceivedToken.signal("checkReceiverAction");
        // after transition
        assertEquals(PartReturnStatus.PART_RECEIVED, partReturn.getStatus());
        node = processDefinition.getNode("Due Parts For Inspection");
        assertEquals(node.getName(), rootToken.getNode().getName());
        assertTrue(rootToken.hasEnded());

    }

    @SuppressWarnings("unchecked")
    public void testPartReturnForNotPayOnReturn() throws CatalogException {
        InputStream processDefinitionStream = this.getClass().getResourceAsStream(PART_RETURN_FLOW);

        processDefinition = ProcessDefinition.parseXmlInputStream(processDefinitionStream);

        processDefinitionService.deploy(processDefinition);
        processInstance = processDefinition.createProcessInstance();
        Claim claim = getPopulatedClaim();
        PaymentCondition paymentCondition = new PaymentCondition("PAY_ON_ON_INSPECTION");
        PartReturn claimPartReturn = claim.getServiceInformation().getServiceDetail()
                .getOemPartsReplaced().get(0).getPartReturns().get(0);
        claimPartReturn.setPaymentCondition(paymentCondition);
        ContextInstance contextInstance = processInstance.getContextInstance();
        contextInstance.setVariable("claim", claim);
        contextInstance.setVariable("partReturn", claimPartReturn);
        Token rootToken = processInstance.getRootToken();
        Node node = processDefinition.getNode("Due Parts for Shipment");
        rootToken.setNode(node);
        rootToken.signal("Generate Shipment");
        rootToken.signal("Submit");

        Token partReceivedToken = rootToken.findToken("Parts Received");
        TaskMgmtInstance taskMgmtInstance = processInstance.getTaskMgmtInstance();
        Collection<TaskInstance> unfinishedTasks = taskMgmtInstance
                .getUnfinishedTasks(partReceivedToken);
        assertEquals(unfinishedTasks.size(), 1);
        TaskInstance partReceivedTask = unfinishedTasks.iterator().next();
        partReceivedTask.setVariable("transition", "Send for Inspection");
        PartReturn partReturn = (PartReturn) processInstance.getContextInstance()
                .getVariable("partReturn");
        assertNotNull(partReturn);
        // before transition
        assertEquals(PartReturnStatus.PART_SHIPPED, partReturn.getStatus());
        partReceivedTask.end();
        partReceivedToken.signal("checkReceiverAction");
        // after transition
        assertEquals(PartReturnStatus.PART_RECEIVED, partReturn.getStatus());
        node = processDefinition.getNode("Due Parts For Inspection");
        assertEquals(node.getName(), rootToken.getNode().getName());
        assertFalse(rootToken.hasEnded());

    }

    /* Kept on Hold
     * @SuppressWarnings("unchecked") public void testPartReturnForNotReceived()
     * throws CatalogException { InputStream processDefinitionStream =
     * this.getClass().getResourceAsStream(PART_RETURN_FLOW);
     * 
     * processDefinition =
     * ProcessDefinition.parseXmlInputStream(processDefinitionStream);
     * 
     * processDefinitionService.deploy(processDefinition); processInstance =
     * processDefinition.createProcessInstance(); Claim claim =
     * getPopulatedClaim(); PaymentCondition paymentCondition = new
     * PaymentCondition("PAY"); PartReturn claimPartReturn =
     * claim.getServiceInformation().getServiceDetail()
     * .getOemPartsReplaced().get(0).getPartReturns().get(0);
     * claimPartReturn.setPaymentCondition(paymentCondition); ContextInstance
     * contextInstance = processInstance.getContextInstance();
     * contextInstance.setVariable("claim", claim);
     * contextInstance.setVariable("partReturn", claimPartReturn); Token
     * rootToken = processInstance.getRootToken(); Node node =
     * processDefinition.getNode("Due Parts for Shipment");
     * rootToken.setNode(node); rootToken.signal("Generate Shipment");
     * rootToken.signal("Submit");
     * 
     * Token partReceivedToken = rootToken.findToken("Parts Received");
     * TaskMgmtInstance taskMgmtInstance =
     * processInstance.getTaskMgmtInstance(); Collection<TaskInstance>
     * unfinishedTasks = taskMgmtInstance
     * .getUnfinishedTasks(partReceivedToken);
     * assertEquals(unfinishedTasks.size(), 1); TaskInstance partReceivedTask =
     * unfinishedTasks.iterator().next();
     * partReceivedTask.setVariable("transition", "Part Not Received");
     * PartReturn partReturn = (PartReturn) processInstance.getContextInstance()
     * .getVariable("partReturn"); assertNotNull(partReturn); // before
     * transition assertEquals(PartReturnStatus.PART_SHIPPED,
     * partReturn.getStatus()); partReceivedTask.end();
     * partReceivedToken.signal("checkReceiverAction"); // after transition
     * assertEquals(PartReturnStatus.PART_TO_BE_SHIPPED,
     * partReturn.getStatus()); }
     */

    Claim getPopulatedClaim() throws CatalogException {
        Claim claim = getClaim();
        ServiceDetail serviceDetail = claim.getServiceInformation().getServiceDetail();
        Item item = catalogService.findItemOwnedByManuf("MC-COUGAR-50-HZ-1");
        OEMPartReplaced partReplaced = new OEMPartReplaced(new ItemReference(item), 1);
        PartReturn partReturn = new PartReturn();
        partReturn.setReturnLocation(locationRepository.findById(new Long(1)));
        partReturn.setDueDate(CalendarDate.date(2009, 12, 12));
        List<PartReturn> partReturns = new ArrayList<PartReturn>();
        partReturns.add(partReturn);
        partReplaced.setPartReturns(partReturns);
        List<OEMPartReplaced> partReplacedList = serviceDetail.getOemPartsReplaced();
        partReplacedList.add(partReplaced);
        serviceDetail.setOemPartsReplaced(partReplacedList);
        return claim;
    }

    Claim getClaim() {
        Claim claim = new MachineClaim();
        ServiceInformation serviceInformation = new ServiceInformation();
        ServiceDetail serviceDetail = new ServiceDetail();
        claim.setServiceInformation(serviceInformation);
        serviceInformation.setServiceDetail(serviceDetail);
        claim.setFiledBy(getDealer());
        return claim;
    }

    private User getDealer() {
        Set<User> allDealers = userRepository.findAllDealers();
        if (allDealers != null) {
            return (User) allDealers.toArray()[0];
        }
        return null;
    }

    /**
     * @param catalogService the catalogService to set
     */
    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Override
    public void setProcessDefinitionService(ProcessDefinitionService processDefinitionService) {
        this.processDefinitionService = processDefinitionService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setLocationRepository(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

}
