package tavant.twms.external;

import java.io.IOException;
import java.io.InputStream;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.core.io.ClassPathResource;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.payment.CreditMemo;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.process.ClaimProcessService;
import tavant.twms.process.ProcessDefinitionService;

import com.domainlanguage.time.CalendarDate;

public class PaymentAsyncServiceImplTest extends EngineRepositoryTestCase {

    PaymentAsyncService paymentAsyncService;

    ProcessDefinitionService processDefinitionService;

    ClaimProcessService claimProcessService;

    Claim claim;

    ProcessInstance processInstance;

    ProcessDefinition processDefinition;

    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        deployProcess("tavant/twms/worklist/worklist-claimsubmit.xml");
        startProcessInstance();
    }

    public void testAsyncServiceResponse() {
        // to start with the control will be in "ManualClaimAdjudication"
        if(processInstance != null){
            assertEquals(processDefinition.getNode("ManualClaimAdjudication"), processInstance
                    .getRootToken().getNode());
            // Create a dummy credit memo
            CreditMemo memo = new CreditMemo();
            memo.setClaimNumber("1"); // Claim number = ID of claim entity, needs
            // to cleaned up later.
            // now assume that the sync response has come back it should have moved
            // to the node of payment
            // since that is the default node
            paymentAsyncService.syncCreditMemo(memo);
            assertEquals(processDefinition.getNode("end"), processInstance.getRootToken().getNode());
        }
    }

    public void setPaymentAsyncService(PaymentAsyncService paymentAsyncService) {
        this.paymentAsyncService = paymentAsyncService;
    }

    public void setProcessDefinitionService(ProcessDefinitionService processDefinitionService) {
        this.processDefinitionService = processDefinitionService;
    }

    public void setClaimProcessService(ClaimProcessService claimProcessService) {
        this.claimProcessService = claimProcessService;
    }

    private void deployProcess(String resource) throws IOException {
        InputStream processStream = new ClassPathResource(resource).getInputStream();
        assertNotNull(processStream);
        processDefinition = ProcessDefinition.parseXmlInputStream(processStream);
        processDefinitionService.deploy(processDefinition);
    }

    private void startProcessInstance() {
        Claim claim = (Claim) getSession().load(Claim.class, new Long(1));
        claim.setHoursInService(5);
        claim.setState(ClaimState.SUBMITTED);
        claim.setPayment(new Payment());
        claim.setFailureDate(CalendarDate.from("12/12/2004", "MM/dd/yyyy"));
        claim.setRepairDate(CalendarDate.from("12/12/2006", "MM/dd/yyyy"));
        claim.setServiceManagerRequest(false);

        processInstance = claimProcessService.startClaimProcessing(claim);
    }
}
