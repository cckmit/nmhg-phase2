package tavant.twms.web.admin.jobcode;

import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.ASM_CHILDREN;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.CODE;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.COMPLETE_CODE;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.DEFINITION;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.FOR_CAMPAIGNS;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.ID;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.INSTANCE_OF;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.LABEL;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.NODE_TYPE;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.NODE_TYPE_BRANCH;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.NODE_TYPE_LEAF;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.NODE_TYPE_ROOT;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.SP_CHILDREN;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.TREAD_BUCKET;
import static tavant.twms.web.admin.jobcode.AssemblyTreeJSONifier.getFormattedLabel;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Job;
import tavant.twms.domain.failurestruct.ActionDefinition;
import tavant.twms.domain.failurestruct.ActionNode;
import tavant.twms.domain.failurestruct.Assembly;
import tavant.twms.domain.failurestruct.AssemblyDefinition;
import tavant.twms.domain.failurestruct.AssemblyLevel;
import tavant.twms.domain.failurestruct.FailureCause;
import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.domain.failurestruct.FailureStructure;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.FailureType;
import tavant.twms.domain.failurestruct.FailureTypeDefinition;
import tavant.twms.domain.failurestruct.FaultCode;
import tavant.twms.domain.failurestruct.FaultCodeDefinition;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.failurestruct.ServiceProcedureDefinition;
import tavant.twms.domain.failurestruct.TreadBucket;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.WebappRepositoryTestCase;

/**
 * @author janmejay.singh
 *         Date: Apr 25, 2007
 *         Time: 8:32:22 PM
 */
/**
 * @author kamal.govindraj
 * 
 */
public class AssemblyTreeJSONifierTest extends WebappRepositoryTestCase {

	AssemblyTreeJSONifier jsonifier;

	FailureStructure failureStWith2AsmChildren,
			failureStWith1ChildHaving2AsmChildren,
			failureStWith1ChildrenHaving1AsmAnd2ActionChildren;

	// partial ones... will use this to test updation of FailureStructure...
	FailureStructure partialFailureStWith2AsmChildren,
			partialFailureStWith1ChildrenHaving1AsmAnd2ActionChildren;

	AssemblyDefinition asmDefinition1, asmDefinition2;

	ActionDefinition actionDefinition1, actionDefinition2;

	// FailureStructureService mockFailureStService = new
	// FailureStructureService() {
	//
	// public AssemblyDefinition findAssemblyDefinition(Long id) {
	// if(AssemblyTreeJSONifierTest.this.asmDefinition1.getId().equals(id)) {
	// return AssemblyTreeJSONifierTest.this.asmDefinition1;
	// }
	// if(AssemblyTreeJSONifierTest.this.asmDefinition2.getId().equals(id)) {
	// return AssemblyTreeJSONifierTest.this.asmDefinition2;
	// }
	// return null;
	// }
	// public FailureStructure getFailureStructureForItem(Item item) {
	// return null;//doing nothing....
	// }
	//
	// public List<FailureTypeDefinition> findFaultFoundOptions(String
	// serialNumber, String partialNameOrcode)
	// {
	// return null;//doing nothing....
	// }
	//
	// public FailureStructure getFailureStructureForItemGroup(ItemGroup
	// itemGroup) {
	// return null;//doing nothing....
	// }
	//
	// public void update(FailureStructure failureStructure) {
	// //doing nothing....
	// }
	//
	//
	//
	// public List<FailureTypeDefinition> findFaultFoundOptionsForModels(String
	// modelName, String partialNameOrcode) {
	// return null;//doing nothing....
	// }
	//
	// public List<FailureCauseDefinition> findCausedByOptions(String
	// serialNumber, String faultFound, String partialNameOrcode) {
	// return null;//doing nothing....
	// }
	//
	// public List<FailureCauseDefinition> findCausedByOptionsForModel(String
	// modelNumber, String faultFound, String partialNameOrcode) {
	// return null;//doing nothing....
	// }
	//
	// @Transactional(readOnly = false)
	// public AssemblyDefinition createAssemblyDefintion(String name, int level)
	// {
	// return null;//doing nothing....
	// }
	//
	// public PageResult<AssemblyDefinition> findAssemblyDefinitions(String
	// nameStartsWith, int level, PageSpecification page) {
	// return null;//doing nothing....
	// }
	//
	// @Transactional(readOnly = false)
	// public ServiceProcedureDefinition createServiceProcedureDefinition(String
	// name) {
	// return null;//doing nothing....
	// }
	//
	// public PageResult<ServiceProcedureDefinition>
	// findServiceProcedureDefinitions(String nameStartsWith, PageSpecification
	// page) {
	// return null;//doing nothing....
	// }
	//
	// public Collection<Job> findJobsStartingWith(final String jobCodePrefix) {
	// return null;//doing nothing....
	// }
	//
	// public FaultCodeDefinition findOrCreateFaultCodeDefinition(String
	// fullCode) {
	// return null;//doing nothing...
	// }
	//
	// public TreadBucket findTreadBucket(String code) {
	// return null;//doing nothing...
	// }
	//
	// public Collection<TreadBucket> findTreadBuckets() {
	// return null;//doing nothing...
	// }
	//
	// public FaultCode findFaultCode(Item arg0, String arg1) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public ServiceProcedure findServiceProcedure(Item arg0, String arg1) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public List<AssemblyLevel> findAllAssemblyLevels() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public PageResult<FaultCodeDefinition>
	// findAllFaultCodeDefinitions(ListCriteria listCriteria) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public FaultCodeDefinition findFaultCodeDefinitionById(Long id) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public List<FaultCodeDefinition>
	// findFaultCodeDefinitionsByIds(Collection<Long> ids) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public void updateFaultCodeDefinition(FaultCodeDefinition definition) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// public ActionDefinition createActionDefinition(String name) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public ActionDefinition findActionDefinition(Long id) {
	// if(AssemblyTreeJSONifierTest.this.actionDefinition1.getId().equals(id)) {
	// return AssemblyTreeJSONifierTest.this.actionDefinition1;
	// }
	// if(AssemblyTreeJSONifierTest.this.actionDefinition2.getId().equals(id)) {
	// return AssemblyTreeJSONifierTest.this.actionDefinition2;
	// }
	// return null;
	// }
	//
	// public PageResult<ActionDefinition> findActionDefinitions(String
	// nameStartsWith, PageSpecification page) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public ServiceProcedureDefinition
	// findOrCreateServiceProcedureDefinition(String fullCode) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public PageResult<ServiceProcedureDefinition>
	// findAllServiceProcedureDefinitions(ListCriteria listCriteria) {
	// return null;//doing nothing...
	// }
	//
	// public ServiceProcedureDefinition findServiceProcedureDefinitionById(Long
	// id) {
	// return null;//doing nothing...
	// }
	//
	// public List<ServiceProcedureDefinition>
	// findServiceProcedureDefinitionsByIds(Collection<Long> ids) {
	// return null;//doing nothing...
	// }
	//
	// public void updateServiceProcedureDefinition(ServiceProcedureDefinition
	// definition) {
	// //doing nothing...
	// }
	//
	// public PageResult<FailureCauseDefinition>
	// fetchFailureCausesStartingWith(String nameStartsWith, PageSpecification
	// page) {
	// return null;
	// }
	//
	// public PageResult<FailureTypeDefinition>
	// fetchFailureTypesStartingWith(String nameStartsWith, PageSpecification
	// page) {
	// return null;
	// }
	//
	// public Object saveAndReturnObject(Object obj) {
	// return null;
	// }
	//
	// public Object updateAndReturnObject(Object obj) {
	// return null;
	// }
	//
	// public FailureCauseDefinition findFailureCauseDefinitionByName(String
	// name) {
	// return null;
	// }
	//
	// public FailureTypeDefinition findFailureTypeDefinitionByName(String name)
	// {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public Object findObjectByPrimaryKey(Class clazz, Serializable id) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public List<FailureCause> findFailureCausesForFailureType(FailureType
	// failureType) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public List<FailureType> findFailureTypesForItemGroup(ItemGroup
	// itemGroup) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public void deleteObject(Object obj) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	//
	// public FailureStructure getMergedFailureStructure(Collection<ItemGroup>
	// itemGroups) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public ServiceProcedure
	// findServiceProcedureByDefinitionAndItem(ServiceProcedureDefinition
	// serviceProcedureDefinitions, Item item) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// public FailureStructure
	// getMergedFailureStructureForItems(Collection<Item> items) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	//
	// public List<FailureCauseDefinition> findCausedByOptions(
	// String serialNumber, String faultFound) {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// };

	public AssemblyDefinition findAssemblyDefinition(Long id) {
		if (AssemblyTreeJSONifierTest.this.asmDefinition1.getId().equals(id)) {
			return AssemblyTreeJSONifierTest.this.asmDefinition1;
		}
		if (AssemblyTreeJSONifierTest.this.asmDefinition2.getId().equals(id)) {
			return AssemblyTreeJSONifierTest.this.asmDefinition2;
		}
		return null;
	}

	public FailureStructure getFailureStructureForItem(Item item) {
		return null;// doing nothing....
	}

	public FailureStructure getFailureStructureForItemGroup(ItemGroup itemGroup) {
		return null;// doing nothing....
	}

	public void update(FailureStructure failureStructure) {
		// doing nothing....
	}

	public List<FailureTypeDefinition> findFaultFoundOptions(
			String serialNumber, String partialNameOrcode) {
		return null;// doing nothing....
	}

	public List<FailureTypeDefinition> findFaultFoundOptionsForModels(
			String modelName, String partialNameOrcode) {
		return null;// doing nothing....
	}

	public List<FailureCauseDefinition> findCausedByOptions(
			String serialNumber, String faultFound, String partialNameOrcode) {
		return null;// doing nothing....
	}

	public List<FailureCauseDefinition> findCausedByOptionsForModel(
			String modelNumber, String faultFound, String partialNameOrcode) {
		return null;// doing nothing....
	}

	@Transactional(readOnly = false)
	public AssemblyDefinition createAssemblyDefintion(String name, int level) {
		return null;// doing nothing....
	}

	public PageResult<AssemblyDefinition> findAssemblyDefinitions(
			String nameStartsWith, int level, PageSpecification page) {
		return null;// doing nothing....
	}

	@Transactional(readOnly = false)
	public ServiceProcedureDefinition createServiceProcedureDefinition(
			String name) {
		return null;// doing nothing....
	}

	public PageResult<ServiceProcedureDefinition> findServiceProcedureDefinitions(
			String nameStartsWith, PageSpecification page) {
		return null;// doing nothing....
	}

	public Collection<Job> findJobsStartingWith(final String jobCodePrefix) {
		return null;// doing nothing....
	}

	public FaultCodeDefinition findOrCreateFaultCodeDefinition(String fullCode) {
		return null;// doing nothing...
	}

	public TreadBucket findTreadBucket(String code) {
		return null;// doing nothing...
	}

	public Collection<TreadBucket> findTreadBuckets() {
		return null;// doing nothing...
	}

	public FaultCode findFaultCode(Item arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceProcedure findServiceProcedure(Item arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<AssemblyLevel> findAllAssemblyLevels() {
		// TODO Auto-generated method stub
		return null;
	}

	public PageResult<FaultCodeDefinition> findAllFaultCodeDefinitions(
			ListCriteria listCriteria) {
		// TODO Auto-generated method stub
		return null;
	}

	public FaultCodeDefinition findFaultCodeDefinitionById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<FaultCodeDefinition> findFaultCodeDefinitionsByIds(
			Collection<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateFaultCodeDefinition(FaultCodeDefinition definition) {
		// TODO Auto-generated method stub

	}

	public ActionDefinition createActionDefinition(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public ActionDefinition findActionDefinition(Long id) {
		if (AssemblyTreeJSONifierTest.this.actionDefinition1.getId().equals(id)) {
			return AssemblyTreeJSONifierTest.this.actionDefinition1;
		}
		if (AssemblyTreeJSONifierTest.this.actionDefinition2.getId().equals(id)) {
			return AssemblyTreeJSONifierTest.this.actionDefinition2;
		}
		return null;
	}

	public PageResult<ActionDefinition> findActionDefinitions(
			String nameStartsWith, PageSpecification page) {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceProcedureDefinition findOrCreateServiceProcedureDefinition(
			String fullCode) {
		// TODO Auto-generated method stub
		return null;
	}

	public PageResult<ServiceProcedureDefinition> findAllServiceProcedureDefinitions(
			ListCriteria listCriteria) {
		return null;// doing nothing...
	}

	public ServiceProcedureDefinition findServiceProcedureDefinitionById(Long id) {
		return null;// doing nothing...
	}

	public List<ServiceProcedureDefinition> findServiceProcedureDefinitionsByIds(
			Collection<Long> ids) {
		return null;// doing nothing...
	}

	public void updateServiceProcedureDefinition(
			ServiceProcedureDefinition definition) {
		// doing nothing...
	}

	public PageResult<FailureCauseDefinition> fetchFailureCausesStartingWith(
			String nameStartsWith, PageSpecification page) {
		return null;
	}

	public PageResult<FailureTypeDefinition> fetchFailureTypesStartingWith(
			String nameStartsWith, PageSpecification page) {
		return null;
	}

	public Object saveAndReturnObject(Object obj) {
		return null;
	}

	public Object updateAndReturnObject(Object obj) {
		return null;
	}

	public FailureCauseDefinition findFailureCauseDefinitionByName(String name) {
		return null;
	}

	public FailureTypeDefinition findFailureTypeDefinitionByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object findObjectByPrimaryKey(Class clazz, Serializable id) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<FailureCause> findFailureCausesForFailureType(
			FailureType failureType) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<FailureType> findFailureTypesForItemGroup(ItemGroup itemGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	public void deleteObject(Object obj) {
		// TODO Auto-generated method stub

	}

	public FailureStructure getMergedFailureStructure(
			Collection<ItemGroup> itemGroups) {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceProcedure findServiceProcedureByDefinitionAndItem(
			ServiceProcedureDefinition serviceProcedureDefinitions, Item item) {
		// TODO Auto-generated method stub
		return null;
	}

	public FailureStructure getMergedFailureStructureForItems(
			Collection<Item> items) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<FailureCauseDefinition> findCausedByOptions(
			String serialNumber, String faultFound) {
		// TODO Auto-generated method stub
		return null;
	}

	FailureStructureService failureStructureService;

	@Required
	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	@Required
	public void setAsmDefinition1(AssemblyDefinition asmDefinition1) {
		this.asmDefinition1 = asmDefinition1;
	}

	@Required
	public void setAsmDefinition2(AssemblyDefinition asmDefinition2) {
		this.asmDefinition2 = asmDefinition2;
	}

	@Required
	public void setActionDefinition1(ActionDefinition spDefinition1) {
		this.actionDefinition1 = spDefinition1;
	}

	@Required
	public void setActionDefinition2(ActionDefinition spDefinition2) {
		this.actionDefinition2 = spDefinition2;
	}

	@Required
	public void setAssemblyTreeJsonifier(AssemblyTreeJSONifier jsonifier) {
		this.jsonifier = jsonifier;
	}

	@Required
	public void setFailureStWith2AsmChildren(
			FailureStructure failureStWith2AsmChildren) {
		this.failureStWith2AsmChildren = failureStWith2AsmChildren;
	}

	@Required
	public void setFailureStWith1ChildHaving2AsmChildren(
			FailureStructure failureStWith1ChildHaving2AsmChildren) {
		this.failureStWith1ChildHaving2AsmChildren = failureStWith1ChildHaving2AsmChildren;
	}

	@Required
	public void setFailureStWith1ChildrenHaving1AsmAnd2ActionChildren(
			FailureStructure failureStWith1ChildrenHaving1AsmAnd2SPChildren) {
		this.failureStWith1ChildrenHaving1AsmAnd2ActionChildren = failureStWith1ChildrenHaving1AsmAnd2SPChildren;
	}

	@Required
	public void setPartialFailureStWith2AsmChildren(
			FailureStructure partialFailureStWith2AsmChildren) {
		this.partialFailureStWith2AsmChildren = partialFailureStWith2AsmChildren;
	}

	@Required
	public void setPartialFailureStWith1ChildrenHaving1AsmAnd2ActionChildren(
			FailureStructure partialFailureStWith1ChildrenHaving1AsmAnd2SPChildren) {
		this.partialFailureStWith1ChildrenHaving1AsmAnd2ActionChildren = partialFailureStWith1ChildrenHaving1AsmAnd2SPChildren;
	}

	@Override
	protected void onSetUpBeforeTransaction() throws Exception {
		super.onSetUpBeforeTransaction();
		// this.jsonifier.setFailureStructureService(this.mockFailureStService);
	}

	@Override
	protected void onTearDownAfterTransaction() throws Exception {
		super.onTearDownAfterTransaction();
		this.jsonifier.setFailureStructureService(this.failureStructureService);
	}

	// unserializing tests...

	public void _testUpdateFailureStructureWith2AsmChildren()
			throws JSONException {
		String serializedJSON = this.jsonifier
				.getSerializedJSONString(this.failureStWith2AsmChildren);
		this.jsonifier.updateFailureStructure(serializedJSON,
				this.partialFailureStWith2AsmChildren);
		this.failureStWith1ChildHaving2AsmChildren
				.equals(this.partialFailureStWith1ChildrenHaving1AsmAnd2ActionChildren);
	}

	public void _testUpdateFailureStructureWith1ChildrenHaving1AsmAnd2SPChildren()
			throws JSONException {
		String serializedJSON = this.jsonifier
				.getSerializedJSONString(this.failureStWith1ChildrenHaving1AsmAnd2ActionChildren);
		this.jsonifier.updateFailureStructure(serializedJSON,
				this.partialFailureStWith1ChildrenHaving1AsmAnd2ActionChildren);
		this.failureStWith1ChildHaving2AsmChildren
				.equals(this.partialFailureStWith1ChildrenHaving1AsmAnd2ActionChildren);
	}

	// serializing tests...

	public void testGetSerializedJSONStringWith2AsmNodeStructure()
			throws JSONException {
		JSONObject rootNode = getJSONObjectFor(this.failureStWith2AsmChildren);
		testRootNodeAgainst(rootNode, this.failureStWith2AsmChildren);
		JSONArray children = rootNode.getJSONArray(ASM_CHILDREN);
		for (Assembly asmNode : this.failureStWith2AsmChildren.getAssemblies()) {
			JSONObject childNode = getNodeById(children, asmNode.getId());
			assertNotNull(childNode);
			testNodeAgainst(childNode, asmNode, NODE_TYPE_LEAF);
		}
	}

	public void testGetSerializedJSONStringWith1AsmNodeWith2ChildAsmNodesStructure()
			throws JSONException {
		JSONObject rootNode = getJSONObjectFor(this.failureStWith1ChildHaving2AsmChildren);
		testRootNodeAgainst(rootNode,
				this.failureStWith1ChildHaving2AsmChildren);
		JSONArray children = rootNode.getJSONArray(ASM_CHILDREN);
		assertEquals(1, children.length());
		JSONObject childNode = children.getJSONObject(0);
		Assembly firstChild = (Assembly) this.failureStWith1ChildHaving2AsmChildren
				.getAssemblies().toArray()[0];
		assertNotNull(childNode);
		testNodeAgainst(childNode, firstChild, NODE_TYPE_BRANCH);
		children = childNode.getJSONArray(ASM_CHILDREN);
		for (Assembly asmNode : firstChild.getComposedOfAssemblies()) {
			JSONObject node = getNodeById(children, asmNode.getId());
			assertNotNull(node);
			testNodeAgainst(node, asmNode, NODE_TYPE_LEAF);
		}
	}

	public void testGetSerializedJSONStringWith1AsmNodeHaving1AsmAnd2SPNodes()
			throws JSONException {
		JSONObject rootNode = getJSONObjectFor(this.failureStWith1ChildrenHaving1AsmAnd2ActionChildren);
		// going to sp nodes straight, have tested everything else in other
		// cases
		Assembly firstChild = (Assembly) this.failureStWith1ChildrenHaving1AsmAnd2ActionChildren
				.getAssemblies().toArray()[0];
		assertEquals(1, firstChild.getComposedOfAssemblies().size());
		assertEquals(2, firstChild.getActions().size());
		JSONObject firstNode = rootNode.getJSONArray(ASM_CHILDREN)
				.getJSONObject(0);
		JSONArray firstNodeSpChildren = firstNode.getJSONArray(SP_CHILDREN);
		JSONArray firstNodesAsmChildren = firstNode.getJSONArray(ASM_CHILDREN);
		Assembly firstChildsAsmChild = (Assembly) firstChild
				.getComposedOfAssemblies().toArray()[0];
		JSONObject firstNodesAsmNode = getNodeById(firstNodesAsmChildren,
				firstChildsAsmChild.getId());
		assertNotNull(firstNodesAsmNode);
		testNodeAgainst(firstNodesAsmNode, firstChildsAsmChild, NODE_TYPE_LEAF);
		for (ActionNode action : firstChild.getActions()) {
			JSONObject actionNode = getNodeById(firstNodeSpChildren, action
					.getId());
			testNodeAgainst(actionNode, action, NODE_TYPE_LEAF);
		}
	}

	private JSONObject getJSONObjectFor(FailureStructure st)
			throws JSONException {
		String serializedJSON = this.jsonifier.getSerializedJSONString(st);
		JSONTokener toknizer = new JSONTokener(serializedJSON);
		return new JSONObject(toknizer);
	}

	@SuppressWarnings("empty")
	private void testNodeAgainst(JSONObject node, Assembly frag, String nodeType)
			throws JSONException {
		// is node fine???
		assertEquals(frag.getId(), new Long((Integer) node.get(ID)));
		assertEquals(nodeType, node.get(NODE_TYPE));
		testFaultCode(node.getJSONObject(TREAD_BUCKET), frag.getFaultCode());
		assertEquals(frag.getClass().getSimpleName(), node.get(INSTANCE_OF));
		// is the definition fine???
		AssemblyDefinition definition = frag.getDefinition();
		JSONObject nodeDefinition = node.getJSONObject(DEFINITION);
		assertEquals((long) definition.getId(), nodeDefinition.getLong(ID));
		assertEquals(getFormattedLabel(definition), nodeDefinition.get(LABEL));
		assertEquals(definition.getCode(), nodeDefinition.get(CODE));
		assertEquals(frag.getFullCode(), node.get(COMPLETE_CODE));
		// is having right number of children???
		int asmChildren = (frag.getComposedOfAssemblies() != null) ? frag
				.getComposedOfAssemblies().size() : 0;
		int actionChildren = (frag.getActions() != null) ? frag.getActions()
				.size() : 0;
		assertEquals(asmChildren, node.getJSONArray(ASM_CHILDREN).length());
		assertEquals(actionChildren, node.getJSONArray(SP_CHILDREN).length());
	}

	private void testFaultCode(JSONObject treadBucket, FaultCode faultCode)
			throws JSONException {
		if (faultCode != null && faultCode.getTreadBucket() != null) {
			assertEquals(faultCode.getTreadBucket().getCode(), treadBucket
					.getInt(ID));
			assertEquals(faultCode.getTreadBucket().getDescription(),
					treadBucket.get(LABEL));
		}
	}

	private void testNodeAgainst(JSONObject node, ActionNode frag,
			String nodeType) throws JSONException {
		// is node fine???
		assertEquals((long) frag.getId(), node.getLong(ID));
		assertEquals(nodeType, node.get(NODE_TYPE));
		assertEquals(frag.getServiceProcedure().getForCampaigns(), node
				.get(FOR_CAMPAIGNS));
		assertEquals(frag.getClass().getSimpleName(), node.get(INSTANCE_OF));
		assertEquals(frag.getFullCode(), node.get(COMPLETE_CODE));
		// is the definition fine???
		ActionDefinition definition = frag.getDefinition();
		JSONObject nodeDefinition = (JSONObject) node.get(DEFINITION);
		assertEquals((long) definition.getId(), nodeDefinition.getLong(ID));
		assertEquals(getFormattedLabel(definition), nodeDefinition.get(LABEL));
		assertEquals(definition.getCode(), nodeDefinition.get(CODE));
		// make sure it doesn't have any children
		assertFalse(node.has(ASM_CHILDREN));
	}

	private void testRootNodeAgainst(JSONObject rootNode, FailureStructure st)
			throws JSONException {
		assertEquals(st.getForItemGroup().getName(), rootNode.get(LABEL));
		assertEquals(NODE_TYPE_ROOT, rootNode.get(NODE_TYPE));
		assertEquals(st.getAssemblies().size(), rootNode.getJSONArray(
				ASM_CHILDREN).length());
	}

	private JSONObject getNodeById(JSONArray nodes, long id)
			throws JSONException {
		for (int i = 0; i < nodes.length(); i++) {
			JSONObject node = nodes.getJSONObject(i);
			if (node.getLong(ID) == id) {
				return node;
			}
		}
		return null;
	}

}
