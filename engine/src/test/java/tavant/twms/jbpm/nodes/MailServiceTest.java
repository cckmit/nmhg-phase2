/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.jbpm.nodes;

import java.io.InputStream;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.core.io.ClassPathResource;

import tavant.twms.domain.catalog.ItemReference;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.MachineClaim;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.infra.EngineRepositoryTestCase;
import tavant.twms.jbpm.notification.NotificationAction;
import tavant.twms.process.ProcessDefinitionService;

public class MailServiceTest extends EngineRepositoryTestCase {

    ProcessDefinitionService processDefinitionService;
    
    ProcessDefinition processDefinition;
    
    UserRepository userRepository;
    
    @Override
    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        ClassPathResource resource = new ClassPathResource("mail-service-process.xml",getClass());
        InputStream inputStream = resource.getInputStream();
        processDefinition = ProcessDefinition.parseXmlInputStream(inputStream);
        processDefinitionService.deploy(processDefinition);
    }

    //FIX-ME: Can't figure out why this test is breaking for now.
    public void testCheckEmailProcess() {
        ProcessInstance processInstance = new ProcessInstance(processDefinition);
        User user = userRepository.findByName("sandy");
        user.setEmail("nonexistinguser@tavant.com");
        processInstance.getContextInstance().setVariable("user", user);
        Claim claim = new MachineClaim();
        claim.setId(new Long(1));
        final InventoryItem inventoryItem = new InventoryItem();
        claim.setItemReference(new ItemReference(inventoryItem));
        claim.setFiledBy(user);
        inventoryItem.setSerialNumber("LX3742");
        processInstance.getContextInstance().setVariable("claim", claim);
        processInstance.signal();
        //The process kinda set the result onto the name. so just assert it back
        assertEquals(NotificationAction.SUCCESS, user.getName());
    }
    
    public void setProcessDefinitionService(ProcessDefinitionService processDefinitionService) {
        this.processDefinitionService = processDefinitionService;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    
}
