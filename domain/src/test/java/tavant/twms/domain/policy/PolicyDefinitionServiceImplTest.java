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
package tavant.twms.domain.policy;

import java.util.HashSet;
import java.util.Set;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import tavant.twms.infra.ListCriteria;

public class PolicyDefinitionServiceImplTest extends MockObjectTestCase {
    private PolicyDefinitionRepository policyDefinitionRepository;
    
    private Mock policyDefinitionRepositoryMock;
    
    private PolicyDefinitionServiceImpl fixture = new PolicyDefinitionServiceImpl();
    
    @Override
    protected void setUp() throws Exception {
        policyDefinitionRepositoryMock = mock(PolicyDefinitionRepository.class);
        
        policyDefinitionRepository = (PolicyDefinitionRepository)policyDefinitionRepositoryMock.proxy();
        
        fixture.setPolicyDefinitionRepository(policyDefinitionRepository);
        
        super.setUp();
    }
    
    public void testFindByIds() {
        Set<Long> collectionOfIds = new HashSet<Long>();
        collectionOfIds.add(new Long(1));
        collectionOfIds.add(new Long(2));
        policyDefinitionRepositoryMock.expects(once()).method("findByIds").with(eq(collectionOfIds));
        fixture.findByIds(collectionOfIds);
    }

    public void testFindAllPolicyDefinitions() {
        ListCriteria listCriteria = new ListCriteria();
        policyDefinitionRepositoryMock.expects(once()).method("findAll").with(eq(listCriteria));
        fixture.findAllPolicyDefinitions(listCriteria);
    }

    public void testFindPolicyDefinitionById() {
        Long id = new Long(10);
        policyDefinitionRepositoryMock.expects(once()).method("findById").with(eq(id));
        fixture.findPolicyDefinitionById(id);
    }

    public void testSavePolicyDefinition() {
        PolicyDefinition policyDefinition = new PolicyDefinition();
        policyDefinitionRepositoryMock.expects(once()).method("save").with(eq(policyDefinition));
        fixture.save(policyDefinition);
    }

    public void testUpdatePolicyDefinition() {
        PolicyDefinition policyDefinition = new PolicyDefinition();
        policyDefinitionRepositoryMock.expects(once()).method("update").with(eq(policyDefinition));
        fixture.update(policyDefinition);
    }

    public void testDelete() {
        PolicyDefinition policyDefinition = new PolicyDefinition();
        policyDefinitionRepositoryMock.expects(once()).method("delete").with(eq(policyDefinition));
        fixture.delete(policyDefinition);
    }
}
