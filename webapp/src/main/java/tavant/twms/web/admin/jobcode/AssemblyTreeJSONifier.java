package tavant.twms.web.admin.jobcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.failurestruct.*;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.infra.InstanceOfUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author janmejay.singh
 *         Date: Apr 24, 2007
 *         Time: 12:36:08 PM
 */
/**
 * The serialized tree's Assembly JSON node will have this structure... { id :
 * "assemblyId", definition : { id : "definitionId", label : "label", code :
 * "code" }, nodeType : "node", instanceOf : "Assembly", treadBucket : { id :
 * "some id", label : "Some Label" }, asmChildren : [...more asm nodes...],
 * spChildren : [...more Action nodes...] } Action Nodes will have this
 * structure... { definition : { code : "Job1", id : "job1", label :
 * "JOB_LEVEL_1_1" }, nodeType : "leaf", instanceOf : "ActionNode", forCampaigns :
 * true, id : "id_job1", completeCode : "AA1 - Job1", suggestedLabourHours : 10,
 * serviceProcedureId : 1 }
 */
@SuppressWarnings("JavaDoc")
public class AssemblyTreeJSONifier {

	public static final String NODE_TYPE = "nodeType",
			INSTANCE_OF = "instanceOf", FOR_CAMPAIGNS = "forCampaigns",
			COMPLETE_CODE = "completeCode", CODE = "code", JOB_CODE_DESCRIPTION = "jobCodeDescription",
			LABOUR_HRS = "suggestedLabourHours", ID = "id",
			DEFINITION = "definition", ASM_CHILDREN = "asmChildren",
			ITEMS = "items", LAST_UPDATED = "lastUpdated",
			SP_CHILDREN = "spChildren", TREAD_BUCKET = "treadBucket",
			LABEL = "label", FAULT_CODE = "faultCode", FAULT_CODE_DESCRIPTION = "faultCodeDescription",
			SERVICE_PROCEDURE_ID = "serviceProcedureId";

	public static final String NODE_TYPE_BRANCH = "node",
			NODE_TYPE_ROOT = "root", NODE_TYPE_LEAF = "leaf";

	private FailureStructureService failureStructureService;
	private ConfigParamService configParamService;
    private ThreadLocal<Boolean> standardLaborHoursEnabled = new ThreadLocal<Boolean>();

	/**
	 * This interface can be implemented for applying some filtering logic on
	 * nodes. Please make sure you don't try to protect any state in this test
	 * object. The methods implementation shd have zero side effects. When a
	 * test returns true, that means the node will appear in the Serialized
	 * JSON, If it returns false, it will not appear there.
	 */
	/**
	 * @author janmejay.singh Date: May 2, 2007 Time: 6:45:49 PM
	 */
	public static interface Filter {

		public boolean preTestNode(Assembly assembly);

		public boolean preTestNode(ActionNode action);

		public boolean postTestNode(JSONObject node, Assembly assembly)
				throws JSONException;

		public boolean postTestNode(JSONObject node, ActionNode action)
				throws JSONException;

		public boolean includeFaultCodeInfo();

	}

	@Required
	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	@Required
	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	/**
	 * Takes a failure structure and a JSONString, and populates the
	 * failurestructures assemblies with the data held by the serialized JSON.
	 * 
	 * @param serializedJSONString
	 * @param failureStructure
	 * @throws JSONException
	 */
	public void updateFailureStructure(String serializedJSONString,
			FailureStructure failureStructure) throws JSONException {
		JSONTokener toknizer = new JSONTokener(serializedJSONString);
		JSONObject root = new JSONObject(toknizer);
		assert root.getString(NODE_TYPE).equals(NODE_TYPE_ROOT);// must have a
		// root node
		assert root.getString(LABEL).equals(getItemGroupName(failureStructure));// label
		// shd
		// be
		// same
		// as
		// failureStructure
		// given
		assert root.has(ASM_CHILDREN);// should have children, the JSONArray
		// can be empty if there are none...
		JSONArray children = root.getJSONArray(ASM_CHILDREN);
		Set<Assembly> assemblies = failureStructure.getAssemblies();
		updateDeletedAssemblies(children, assemblies);
		for (int i = 0; i < children.length(); i++) {
			updateAddedAssemblies(children.getJSONObject(i), assemblies);
		}
	}

	/**
	 * Deletes the nodes that are not there in the JSON tree. This method shd be
	 * used only by the FailureStructure, asm nodes shd use the method
	 * updateDeletedAssemblies(JSONArray, Set<Assembly>, Assembly)
	 * 
	 * @param nodes
	 * @param assemblies
	 * @throws JSONException
	 */
	private void updateDeletedAssemblies(JSONArray nodes,
			Set<Assembly> assemblies) throws JSONException {
		Set<Assembly> deletables = findDeletableAsmNodes(assemblies, nodes);
	}

	/**
	 * finds asm objects that are not found in the JSONArray passed, and returns
	 * a set containing them all.
	 * 
	 * @param assemblies
	 * @param nodes
	 * @return
	 * @throws JSONException
	 */
	private Set<Assembly> findDeletableAsmNodes(Set<Assembly> assemblies,
			JSONArray nodes) throws JSONException {
		Set<Assembly> deletables = new HashSet<Assembly>();
		for (Assembly asm : assemblies) {
			JSONObject node = getJSONObjectMatching(nodes, asm);
			if (node == null) {
				asm.setActive(false);
				deletables.add(asm);
			} else {
				Set<Assembly> childAssemblies = asm.getComposedOfAssemblies();
				if (childAssemblies != null) {
					updateDeletedAssemblies(node.getJSONArray(ASM_CHILDREN),
							childAssemblies, asm);
				}
				Set<ActionNode> childActions = asm.getActions();
				if (childActions != null) {
					updateDeletedActions(node.getJSONArray(SP_CHILDREN),
							childActions);
				}
			}
		}
		return deletables;
	}

	/**
	 * Finds and deletes all the action nodes that are no longer there in the
	 * JSONArray passed.
	 * 
	 * @param nodes
	 * @param actions
	 * @throws JSONException
	 */
	private void updateDeletedActions(JSONArray nodes, Set<ActionNode> actions)
			throws JSONException {
		Set<ActionNode> deletables = new HashSet<ActionNode>();
		for (ActionNode procedure : actions) {
			JSONObject node = getJSONObjectMatching(nodes, procedure);
			if (node == null) {
				deletables.add(procedure);
			}
		}
		for (ActionNode deletable : deletables) {
			deletable.setActive(false);
		}
	}

	/**
	 * Finds and deletes all the assemblies that are no longer there in the
	 * given JSONArray.
	 * 
	 * @param nodes
	 * @param assemblies
	 * @param parentAsm
	 * @throws JSONException
	 */
	private void updateDeletedAssemblies(JSONArray nodes,
			Set<Assembly> assemblies, Assembly parentAsm) throws JSONException {
		Set<Assembly> deletables = findDeletableAsmNodes(assemblies, nodes);
		for (Assembly deletable : deletables) {
			deletable.setActive(false);
		}
	}

	/**
	 * Returns the JSONObject that has id same as the object passed...
	 * 
	 * @param nodes
	 * @param object
	 * @return JSONObject(null if not found)
	 * @throws JSONException
	 */
	private JSONObject getJSONObjectMatching(JSONArray nodes, Object object)
			throws JSONException {
		long id;
		Class instance;
		if (InstanceOfUtil.isInstanceOfClass(Assembly.class, object)) {
			id = ((Assembly) object).getId();
			instance = Assembly.class;
		} else {
			id = ((ActionNode) object).getId();
			instance = ActionNode.class;
		}
		for (int i = 0; i < nodes.length(); i++) {
			JSONObject node = nodes.getJSONObject(i);
			if (isThisInstance(node, instance)) {
				Long nodeId = getNodeId(node);
				if (nodeId != null && nodeId == id) {
					return node;
				}
			}
		}
		return null;
	}

	/**
	 * Returns true if the node represents the node of the given class, else
	 * returns false.
	 * 
	 * @param node
	 * @param clazz
	 * @return
	 * @throws JSONException
	 */
	private boolean isThisInstance(JSONObject node, Class clazz)
			throws JSONException {
		return node.getString(INSTANCE_OF).equals(clazz.getSimpleName());
	}

	/**
	 * returns true if the given JSONObject represents an assembly node, else
	 * returns false.
	 * 
	 * @param node
	 * @return
	 * @throws JSONException
	 */
	private boolean isAssembly(JSONObject node) throws JSONException {
		return isThisInstance(node, Assembly.class);
	}

	/**
	 * returns true if the given JSONObject represents a ServiceProcedure node,
	 * else returns false.
	 * 
	 * @param node
	 * @return
	 * @throws JSONException
	 */
	private boolean isServiceProcedure(JSONObject node) throws JSONException {
		return isThisInstance(node, ServiceProcedure.class);
	}

	/**
	 * This method is called only at the failure structure level, where failure
	 * structure doesn't hold a ref to the asm node... (@ asm node level, the
	 * updateAddedAssemblies(JSONObject jsonObject, Set<Assembly> assemblies,
	 * Assembly parentAsm) should be called.)
	 * 
	 * @param jsonObject
	 * @param assemblies
	 * @throws JSONException
	 */
	private void updateAddedAssemblies(JSONObject jsonObject,
			Set<Assembly> assemblies) throws JSONException {
		Assembly asm = findAssemblyFor(assemblies, jsonObject);
		if (asm == null) {// if asm is not found... create a new one...
			asm = new Assembly();
			assemblies.add(asm);
		}
		updateAddedAsmNode(jsonObject, asm);
	}

	/**
	 * This method updates the Set of assemblies with the data held by the JSON
	 * object passed. it updates an extsting node Asm object in the set(matching
	 * is done based on ID) and creates new Asm node if none is there... It also
	 * manages the creation/updation of the action nodes...
	 * 
	 * @param jsonObject
	 * @param assemblies
	 * @throws JSONException
	 */
	private void updateAddedAssemblies(JSONObject jsonObject,
			Set<Assembly> assemblies, Assembly parentAsm) throws JSONException {
		Assembly asm = findAssemblyFor(assemblies, jsonObject);
		if (asm == null) {// if asm is not found... create a new one...
			asm = new Assembly();
			assert parentAsm != null;
			parentAsm.addChildAssembly(asm);
		}
		updateAddedAsmNode(jsonObject, asm);
	}

	/**
	 * This function updates a given Assembly object with the data provided by
	 * the JSONObject.
	 * 
	 * @param jsonObject
	 * @param asm
	 * @throws JSONException
	 */
	private void updateAddedAsmNode(JSONObject jsonObject, Assembly asm)
			throws JSONException {
		populateAsmNode(jsonObject, asm);// populate the asm node
		JSONArray asmChildren = jsonObject.getJSONArray(ASM_CHILDREN);
		JSONArray spChildren = jsonObject.getJSONArray(SP_CHILDREN);

		if (asm.getComposedOfAssemblies() == null && asmChildren.length() > 0) {
			asm.setComposedOfAssemblies(new HashSet<Assembly>());
		}
		if (asm.getActions() == null && spChildren.length() > 0) {
			asm.setActions(new HashSet<ActionNode>());
		}
		for (int i = 0; i < asmChildren.length(); i++) {
			updateAddedAssemblies(asmChildren.getJSONObject(i), asm
					.getComposedOfAssemblies(), asm);
		}
		for (int i = 0; i < spChildren.length(); i++) {
			updateAddedActions(spChildren.getJSONObject(i), asm.getActions(),
					asm);
		}
	}

	/**
	 * This function updates a action node(if it exists in the set... or else it
	 * creates a new one...
	 * 
	 * @param jsonObject
	 * @param actions
	 * @throws JSONException
	 */
	private void updateAddedActions(JSONObject jsonObject,
			Set<ActionNode> actions, Assembly parentAsm) throws JSONException {
		assert jsonObject.has(ID);
		ActionNode action = findActionFor(actions, jsonObject);
		if (action == null) {// if procedure is not found... create a new
			// one...
			action = new ActionNode();
			action.setDefinedFor(parentAsm);
			ServiceProcedure serviceProcedure = new ServiceProcedure();
			action.setServiceProcedure(serviceProcedure);
			populateActionNode(jsonObject, action);// populate the procedure
			// node
			serviceProcedure.setDefinedFor(action);
			serviceProcedure.setDefinition(failureStructureService
					.findOrCreateServiceProcedureDefinition(action
							.getFullCode()));
			actions.add(action);
		} else {
			populateActionNode(jsonObject, action);// populate the procedure
			// node
		}
	}

	/**
	 * This method populates the action node given(raises
	 * AssemblyTreeIllegalDataException if something goes wrong)
	 * 
	 * @param jsonObject
	 * @param action
	 */
	private void populateActionNode(JSONObject jsonObject, ActionNode action) {
		try {
			assert jsonObject.has(DEFINITION);
			JSONObject definition = jsonObject.getJSONObject(DEFINITION);
			assert definition.has(ID);
			ActionDefinition actionDefinition = failureStructureService
					.findActionDefinition(definition.getLong(ID));
			assert actionDefinition != null;
			action.setDefinition(actionDefinition);
		} catch (Exception ex) {
			throw new AssemblyTreeIllegalDataException(
					AssemblyTreeIllegalDataException.BAD_SERVICE_PROCEDURE_DEFINITION,
					ex);
		} catch (AssertionError error) {
			throw new AssemblyTreeIllegalDataException(
					AssemblyTreeIllegalDataException.BAD_SERVICE_PROCEDURE_DEFINITION,
					error);
		}
		try {
			action.getServiceProcedure().setForCampaigns(
					jsonObject.getBoolean(FOR_CAMPAIGNS));
		} catch (JSONException ex) {
			throw new AssemblyTreeIllegalDataException(
					AssemblyTreeIllegalDataException.BAD_FOR_CAMPAIGN_VALUE, ex);
		}
		try {
			action.getServiceProcedure().setSuggestedLabourHours(
					jsonObject.getDouble(LABOUR_HRS));
		} catch (JSONException ex) {
			throw new AssemblyTreeIllegalDataException(
					AssemblyTreeIllegalDataException.BAD_LABOUR_HRS_VALUE, ex);
		}
	}

	/**
	 * finds action object that has the same ID as the JSON object given.
	 * returns null if not found.
	 * 
	 * @param actions
	 * @param object
	 * @return serviceProcedure(null if not found)
	 * @throws JSONException
	 */
	private ActionNode findActionFor(Set<ActionNode> actions, JSONObject object)
			throws JSONException {
		Long id = getNodeId(object);
		if (id != null) {
			for (ActionNode action : actions) {
				Long actionId = action.getId();
				if (actionId != null && actionId.equals(id)) {
					return action;
				}
			}
		}
		return null;
	}

	private Long getNodeId(JSONObject object) {
		try {
			if (nodeHasId(object)) {
				return object.getLong(ID);
			}
			return null;
		} catch (JSONException e) {
			throw new AssemblyTreeIllegalDataException(
					AssemblyTreeIllegalDataException.ID_IS_NOT_LEGAL, e);
		}
	}

	private boolean nodeHasId(JSONObject object) throws JSONException {
		assert object.has(ID);
		return StringUtils.hasText(object.getString(ID));
	}

	/**
	 * finds assembly object that has the same ID as the JSON object given.
	 * returns null if not found.
	 * 
	 * @param assemblies
	 * @param object
	 * @return assembly(null if not found)
	 * @throws JSONException
	 */
	private Assembly findAssemblyFor(Set<Assembly> assemblies, JSONObject object)
			throws JSONException {
		Long id = getNodeId(object);
		if (id != null) {
			for (Assembly asm : assemblies) {
				Long asmId = asm.getId();
				if (asmId != null && asmId.equals(id)) {
					return asm;
				}
			}
		}
		return null;
	}

	/**
	 * populates a given assembly object with the data of given jsonObject.
	 * Throws AssemblyTreeIllegalDataException if something oes wrong...
	 * 
	 * @param jsonObject
	 * @param asm
	 */
	private void populateAsmNode(JSONObject jsonObject, Assembly asm) {
		try {
			assert jsonObject.has(DEFINITION);
			JSONObject definition = jsonObject.getJSONObject(DEFINITION);
			assert definition.has(ID);
			AssemblyDefinition assemblyDefinition = failureStructureService
					.findAssemblyDefinition(definition.getLong(ID));
			assert assemblyDefinition != null;
			asm.setDefinition(assemblyDefinition);
		} catch (Exception ex) {
			throw new AssemblyTreeIllegalDataException(
					AssemblyTreeIllegalDataException.BAD_ASSEMBLY_DEFINITION,
					ex);
		} catch (AssertionError error) {
			throw new AssemblyTreeIllegalDataException(
					AssemblyTreeIllegalDataException.BAD_ASSEMBLY_DEFINITION,
					error);
		}
		try {
			FaultCode faultCode = null;
			if (jsonObject.getJSONArray(SP_CHILDREN).length() > 0) {
				if (asm.getFaultCode() == null) {
					faultCode = new FaultCode();
				} else {
					faultCode = asm.getFaultCode();
					assert faultCode != null;
				}
				faultCode.setDefinition(failureStructureService
						.findOrCreateFaultCodeDefinition(asm.getFullCode()));
				if (StringUtils.hasText(jsonObject.getJSONObject(TREAD_BUCKET)
						.getString(ID))) {
					TreadBucket tread = failureStructureService
							.findTreadBucket(jsonObject.getJSONObject(
									TREAD_BUCKET).getString(ID));
					faultCode.setTreadBucket(tread);
				}
			}
			asm.setFaultCode(faultCode);
		} catch (JSONException ex) {
			throw new AssemblyTreeIllegalDataException(
					AssemblyTreeIllegalDataException.BAD_TREAD_BUCKET, ex);
		}
	}

	// serialization code...
	/**
	 * Takes a failure structure, and returns the serialized JSON for that,
	 * based on a filter that allows everything.
	 * 
	 * @param failureStructure
	 * @return
	 * @throws JSONException
	 */
	public String getSerializedJSONString(FailureStructure failureStructure)
			throws JSONException {
		return getSerializedJSONStringForCampaign(failureStructure, new Filter() { // getSerializedJSONStringForCampaign -- load JSON String with all nodes ; getSerializedJSONString -- load JSON String with levels (used in claim page2)
			public boolean preTestNode(Assembly assembly) {
				return true;
			}

			public boolean preTestNode(ActionNode action) {
				return true;
			}

			public boolean postTestNode(JSONObject node, Assembly assembly) {
				return true;
			}

			public boolean postTestNode(JSONObject node, ActionNode action) {
				return true;
			}

			public boolean includeFaultCodeInfo() {
				return false;
			}
		}, null);
	}

	/**
	 * Takes a failure structure, and returns the serialized JSON for that,
	 * based on the filter.
	 * 
	 * @param failureStructure
	 * @param filter
	 *            (the filter tells the jsonifier weather to include the node or
	 *            not... by returning true or false.)
	 * @param claim
     * @return
	 * @throws JSONException
	 */
	public String getSerializedJSONString(FailureStructure failureStructure,
                                          Filter filter, Claim claim) throws JSONException {
        standardLaborHoursEnabled.set(configParamService.getBooleanValue(ConfigName.ENABLE_STANDARD_LABOR_HOURS.getName()));
		return createTree(failureStructure, filter, claim).toString();
	}
	
	public String getSerializedJSONStringForCampaign(
			FailureStructure failureStructure, Filter filter, Claim claim)
			throws JSONException {
		standardLaborHoursEnabled.set(configParamService
				.getBooleanValue(ConfigName.ENABLE_STANDARD_LABOR_HOURS
						.getName()));
		return createTreeForCampaign(failureStructure, filter, claim)
				.toString();
	}

	private JSONObject createTreeForCampaign(FailureStructure failureStructure,
			Filter filter, Claim claim) throws JSONException {
		Set<JSONObject> children = new HashSet<JSONObject>();
		if (failureStructure != null
				&& failureStructure.getAssemblies() != null) {
			Set<Assembly> assemblies = new TreeSet<Assembly>(
					failureStructure.getAssemblies());
			failureStructure.setAssemblies(assemblies);
			for (Assembly asm : failureStructure.getAssemblies()) {
				if (asm.getActive()) {
					JSONObject node = getJSONObjectForCampaign(asm, filter,
							claim);
					if (node != null) {
						children.add(node);
					}
				}
			}
		}
		Set<JSONObject> sortedChildren = new TreeSet<JSONObject>(
				new AssemblyJsonComparator());
		sortedChildren.addAll(children);

		return (new JSONObject()).put(NODE_TYPE, NODE_TYPE_ROOT)
				.put(LABEL, getItemGroupName(failureStructure))
				.put(ASM_CHILDREN, sortedChildren);
	}
	
	protected JSONObject getJSONObjectForCampaign(Assembly asm, Filter filter,
			Claim claim) throws JSONException {
		if (!filter.preTestNode(asm))
			return null;
		JSONObject treeFragment = new JSONObject();
		// creating children...
		Set<JSONObject> asmNodes = new HashSet<JSONObject>();
		if (asm.getComposedOfAssemblies() != null) {
			Set<Assembly> assemblies = new TreeSet<Assembly>(
					asm.getComposedOfAssemblies());
			asm.setComposedOfAssemblies(assemblies);
			for (Assembly asmChild : asm.getComposedOfAssemblies()) {
				if (asmChild.getActive()) {
					JSONObject node = getJSONObjectForCampaign(asmChild,
							filter, claim);
					if (node != null) {
						asmNodes.add(node);
					}
				}
			}
		}
		Set<JSONObject> sortedNodes = new TreeSet<JSONObject>(
				new AssemblyJsonComparator());
		sortedNodes.addAll(asmNodes);
		treeFragment.put(ASM_CHILDREN, sortedNodes);
		Set<JSONObject> spNodes = new HashSet<JSONObject>();
		if (asm.getActions() != null) {
			Set<ActionNode> actions = new TreeSet<ActionNode>(asm.getActions());
			asm.setActions(actions);
			for (ActionNode action : asm.getActions()) {
				if (action.getActive() && isEligible(action, claim)) {
					JSONObject node = getJSONObject(action, filter);
					if (node != null) {
						spNodes.add(node);
					}
				}
			}
		}
		Set<JSONObject> sortedSPNodes = new TreeSet<JSONObject>(
				new AssemblyJsonComparator());
		sortedSPNodes.addAll(spNodes);
		treeFragment.put(SP_CHILDREN, spNodes);
		// creating definition...
		JSONObject definition = new JSONObject();
		definition.put(ID, asm.getDefinition().getId());
		definition.put(LABEL, getFormattedLabel(asm.getDefinition()));
		definition.put(CODE, asm.getDefinition().getCode());
		treeFragment.put(COMPLETE_CODE, asm.getFullCode());
		treeFragment.put(DEFINITION, definition);
		// putting some other attributes
		treeFragment.put(ID, asm.getId());
		if (claim == null) {
			// These values are required only in failure structure edit screen
			treeFragment.put(TREAD_BUCKET, serializeTreadBucket(asm));
			treeFragment.put(LAST_UPDATED, getLastUpdatedDate(asm));
		}
		treeFragment.put(INSTANCE_OF, asm.getClass().getSimpleName());
		treeFragment.put(NODE_TYPE,
				((asmNodes.size() + spNodes.size()) > 0) ? NODE_TYPE_BRANCH
						: NODE_TYPE_LEAF);
		if (!filter.postTestNode(treeFragment, asm))
			return null;
		if (filter.includeFaultCodeInfo()) {
			treeFragment.put(FAULT_CODE,
					serializeFaultCodeInfo(asm.getFaultCode()));
		}
		return treeFragment;
	}

	public String getSerializedJSONString(Assembly assembly, Filter filter,
			Claim claim) throws JSONException {
		standardLaborHoursEnabled.set(configParamService
				.getBooleanValue(ConfigName.ENABLE_STANDARD_LABOR_HOURS
						.getName()));
		Set<JSONObject> children = new HashSet<JSONObject>();
		if (assembly != null) {
			if (assembly.getComposedOfAssemblies() != null) {
				for (Assembly asm : assembly.getComposedOfAssemblies()) {
					if (asm.getActive()) {
						JSONObject node = getJSONObject(asm, filter, claim);
						if (node != null) {
							children.add(node);
						}
					}
				}
			}
			if (assembly.getActions() != null) {
				for (ActionNode action : assembly.getActions()) {
					if (action.getActive() && isEligible(action, claim)) {
						JSONObject node = getJSONObject(action, filter);
						if (node != null) {
							children.add(node);
						}
					}
				}
			}
		}
		Set<JSONObject> sortedChildren = new TreeSet<JSONObject>(
				new AssemblyJsonComparator());
		sortedChildren.addAll(children);
		return sortedChildren.toString();
	}

	/**
	 * creates JSON object from a given assembly object.
	 * 
	 * @param asm
	 * @param claim
     * @return jsonObject(tree fragment)
	 * @throws JSONException
	 */
	protected JSONObject getJSONObject(Assembly asm, Filter filter, Claim claim)
			throws JSONException {
		if (!filter.preTestNode(asm))
			return null;
		JSONObject treeFragment = new JSONObject();
		Set<JSONObject> spNodes = new HashSet<JSONObject>();
		if (asm.getActions() != null) {
			Set<ActionNode> actions = new TreeSet<ActionNode>(asm.getActions());
			asm.setActions(actions);
			for (ActionNode action : asm.getActions()) {
                if (action.getActive() && isEligible(action,claim)) {
                    JSONObject node = getJSONObject(action, filter);
					if (node != null) {
						spNodes.add(node);
					}
                }
			}
		}
		Set<JSONObject> sortedSPNodes = new TreeSet<JSONObject>(
				new AssemblyJsonComparator());
		sortedSPNodes.addAll(spNodes);
		treeFragment.put(SP_CHILDREN, spNodes);
		// creating definition...
		JSONObject definition = new JSONObject();
		definition.put(ID, asm.getDefinition().getId());
		definition.put(LABEL, getFormattedLabel(asm.getDefinition()));
		definition.put(CODE, asm.getDefinition().getCode());
		treeFragment.put(COMPLETE_CODE, asm.getFullCode());
		treeFragment.put(DEFINITION, definition);
		// putting some other attributes
		treeFragment.put(ID, asm.getId());
        if (claim == null) {
            // These values are required only in failure structure edit screen
            treeFragment.put(TREAD_BUCKET, serializeTreadBucket(asm));
            treeFragment.put(LAST_UPDATED, getLastUpdatedDate(asm));
        }
		treeFragment.put(INSTANCE_OF, asm.getClass().getSimpleName());
		treeFragment.put(NODE_TYPE,
				((asm.getComposedOfAssemblies().size()) > 0 || !spNodes.isEmpty()) ? NODE_TYPE_BRANCH
						: NODE_TYPE_LEAF);
		if (!filter.postTestNode(treeFragment, asm))
			return null;
		if (filter.includeFaultCodeInfo()) {
			treeFragment.put(FAULT_CODE, serializeFaultCodeInfo(asm
					.getFaultCode()));
			treeFragment.put(FAULT_CODE_DESCRIPTION, asm.getDefinition().getName());
		}
		return treeFragment;
	}

	private String getLastUpdatedDate(Assembly asm) {
		FaultCode faultCode = asm.getFaultCode();
		if (faultCode != null && faultCode.getLastUpdatedDate() != null) {
			return faultCode.getLastUpdatedDate().toString("MM/dd/yyyy");
		}
		return "";
	}

	private JSONObject serializeFaultCodeInfo(FaultCode faultCode)
			throws JSONException {
		JSONObject node = new JSONObject();
		// will be null when the asm has more asm children... which have sp
		// children... but the node doesn't have direct
		// sp children...
		if (faultCode != null)
			node.put(ID, faultCode.getId());
		return node;
	}

	private JSONObject serializeTreadBucket(Assembly asm) throws JSONException {
		JSONObject treadBucket = new JSONObject();
		if (asm.getFaultCode() != null
				&& asm.getFaultCode().getTreadBucket() != null) {
			treadBucket.put(ID, asm.getFaultCode().getTreadBucket().getCode());
			treadBucket.put(LABEL, asm.getFaultCode().getTreadBucket()
					.getDescription());
		} else {
			treadBucket.put(ID, "");
			treadBucket.put(LABEL, "");
		}
		return treadBucket;
	}

	/**
	 * creates jsonObject representing the ServiceProcedure node given.
	 * 
	 * @param action
	 * @return jsonObject
	 * @throws JSONException
	 */
	protected JSONObject getJSONObject(ActionNode action, Filter filter)
			throws JSONException {
		if (!filter.preTestNode(action))
			return null;
		JSONObject treeFragment = new JSONObject();
		// creating definition...
		JSONObject definition = new JSONObject();
		definition.put(ID, action.getDefinition().getId());
		if (standardLaborHoursEnabled.get()) {
			definition.put(LABEL, getFormattedLabel(action.getDefinition(),
					action.getServiceProcedure()));
		} else {
			definition.put(LABEL, getFormattedLabel(action.getDefinition()));
		}		
		definition.put(CODE, action.getDefinition().getCode());
		treeFragment.put(DEFINITION, definition);
		// putting some other attributes
		treeFragment.put(ID, action.getId());
		treeFragment.put(FOR_CAMPAIGNS, action.getServiceProcedure()
				.getForCampaigns());
		treeFragment.put(INSTANCE_OF, action.getClass().getSimpleName());
		treeFragment.put(LABOUR_HRS, action.getServiceProcedure()
				.getSuggestedLabourHours());
		treeFragment.put(COMPLETE_CODE, action.getFullCode());
		treeFragment.put(JOB_CODE_DESCRIPTION, action.getJobCodeDescription());
		treeFragment.put(SERVICE_PROCEDURE_ID, action.getServiceProcedure()
				.getId());
		treeFragment.put(NODE_TYPE, NODE_TYPE_LEAF);
		if (!filter.postTestNode(treeFragment, action))
			return null;
		return treeFragment;
	}

	/**
	 * Creates the tree out of given failure structure.
	 * 
	 * @param failureStructure
	 * @param claim
     * @return
	 * @throws JSONException
	 */
	private JSONObject createTree(FailureStructure failureStructure,
                                  Filter filter, Claim claim) throws JSONException {
		Set<JSONObject> children = new HashSet<JSONObject>();
		if (failureStructure != null
				&& failureStructure.getAssemblies() != null) {
			//Set<Assembly> assemblies = new TreeSet<Assembly>(failureStructure
					//.getAssemblies());
			failureStructure.setAssemblies(failureStructure.getAssemblies());
			for (Assembly asm : failureStructure.getAssemblies()) {
				if (asm.getActive()) {
					JSONObject node = getJSONObject(asm, filter, claim);
					if (node != null) {
						children.add(node);
					}
				}
			}
		}
		Set<JSONObject> sortedChildren = new TreeSet<JSONObject>(
				new AssemblyJsonComparator());
		sortedChildren.addAll(children);

		return (new JSONObject()).put(NODE_TYPE, NODE_TYPE_ROOT).put(LABEL,
				getItemGroupName(failureStructure)).put(ASM_CHILDREN,
				sortedChildren);
	}

	// utilities

	public static String getFormattedLabel(AssemblyDefinition asmDef) {
		return getFormattedLabel(asmDef.getName(), asmDef.getCode());
	}

	public static String getFormattedLabel(ActionDefinition actionDef) {
		return getFormattedLabel(actionDef.getName(), actionDef.getCode());
	}

	public static String getFormattedLabel(ActionDefinition actionDef,
			ServiceProcedure servicePro) {
		return getFormattedLabel(actionDef.getName(), actionDef.getCode(),
				servicePro.getSuggestedLabourHours());
	}

	public static String getFormattedLabel(String name, String code,
			Double laborHours) {
		String label = name + " (" + code + ")";
		if(laborHours != null){
			label = label + " (" + laborHours + ")";
		}
		return label;
	}

	public static String getFormattedLabel(String name, String code) {
		return name + " (" + code + ")";
	}

	// TODO: im i @ the right place???
	private String getItemGroupName(FailureStructure failureStructure) {
		if (failureStructure != null) {
			ItemGroup group = failureStructure.getForItemGroup();
			if (group != null)
				return group.getName();
		}
		return "Undefined";
	}

    private boolean isEligible(ActionNode action, Claim claim) {
        boolean isEligible = true;
        if (claim != null) {
            if (ClaimType.MACHINE.getType().equals(claim.getType().getType())
                    && action.getServiceProcedure().getForCampaigns()) {
                isEligible = false;
                for (LaborDetail laborDetail : claim.getServiceInformation().getServiceDetail().getLaborPerformed()) {
                    if (laborDetail.getServiceProcedure().getId().longValue()
                            == action.getServiceProcedure().getId().longValue()) {
                        isEligible = true;
                    }
                }
            }
        }
        return isEligible;
    }

}

/**
 * This exception is raised by AssemblyTreeJSONSerializer, when something goes
 * wrong while unserializing a tree... The message is a i18N key, which can be
 * put as an error in the action.
 */
class AssemblyTreeIllegalDataException extends RuntimeException {

	public static final String BAD_ASSEMBLY_DEFINITION = "error.manageFailureStructure.invalidAsmDefinition",
			BAD_TREAD_BUCKET = "error.manageFailureStructure.invalidTreadBucket",
			ILLEGAL_TREE_NODE_STATE = "error.manageFailureStructure.invalidTreeNodeState",
			BAD_FOR_CAMPAIGN_VALUE = "error.manageFailureStructure.invalidForCampaignsFragValue",
			BAD_SERVICE_PROCEDURE_DEFINITION = "error.manageFailureStructure.invalidServiceProcedureDefinition",
			BAD_LABOUR_HRS_VALUE = "error.manageFailureStructure.invalidSuggestedLabourHoursValue",
			ID_IS_NOT_LEGAL = "error.manageFailureStructure.invalidNodeID";

	// this exception gets raised whenever tree passed is found to have some
	// validatio errors.
	public AssemblyTreeIllegalDataException(String messageKey, Throwable e) {
		super(messageKey, e);
	}

	public AssemblyTreeIllegalDataException(String messageKey) {
		super(messageKey);
	}
}
