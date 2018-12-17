/**
 * 
 */
package tavant.twms.web.admin.failuretype;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.failurestruct.FailureCause;
import tavant.twms.domain.failurestruct.FailureCauseDefinition;
import tavant.twms.domain.failurestruct.FailureRootCause;
import tavant.twms.domain.failurestruct.FailureRootCauseDefinition;
import tavant.twms.domain.failurestruct.FailureStructureService;
import tavant.twms.domain.failurestruct.FailureType;
import tavant.twms.domain.failurestruct.FailureTypeDefinition;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class FailureTypeAssocSerializer implements FailureTypeAssocConstants {

	private FailureStructureService failureStructureService;

	public void setFailureStructureService(
			FailureStructureService failureStructureService) {
		this.failureStructureService = failureStructureService;
	}

	//Serialization Code ............................
	
	/**
	 * @throws JSONException
	 */
	public String serialize(ItemGroup itemGroup, String failureContext) throws JSONException {
		List<FailureType> failureTypes = failureStructureService
				.findFailureTypesForItemGroup(itemGroup);
		List<JSONObject> children = new ArrayList<JSONObject>();
		for (FailureType type : failureTypes) {
			JSONObject childJsonObject = getJSONObjectFromFailureType(type, failureContext);
			if ( !childJsonObject.isNull(ID) ) {
				children.add(childJsonObject);
			}
		}
		return (new JSONObject()).put(NODE_TYPE, NODE_TYPE_ROOT).put(
				FAILURE_TYPE_CHILDREN, children).toString();
	}

	private JSONObject getJSONObjectFromFailureType(FailureType failureType, String failureContext)
			throws JSONException {
		JSONObject node = new JSONObject();
		// Creating Definition
		JSONObject definition = new JSONObject();
		definition.put(ID, failureType.getDefinition().getId());
		definition.put(LABEL, failureType.getDefinition().getName());
		definition.put(CODE, failureType.getDefinition().getCode());

		// Putting Values
		node.put(DEFINITION, definition);
		node.put(ID, failureType.getId());
		node.put(NODE_TYPE, NODE_TYPE_LEAF);
		node.put(INSTANCE_OF, FailureType.class.getSimpleName());

		if (FAILURE_CONTEXT_CAUSE.equalsIgnoreCase(failureContext))
		{
			List<JSONObject> failureCausesArray = getJSONArrayForCauses(failureType);
			if( failureCausesArray != null && !failureCausesArray.isEmpty() ) {
				node.put(FAILURE_CAUSE_CHILDREN, failureCausesArray);
			} else {
				node.remove(DEFINITION);
				node.remove(ID);
				node.remove(NODE_TYPE);
				node.remove(INSTANCE_OF);
			}
		}
		if (FAILURE_CONTEXT_ROOT_CAUSE.equalsIgnoreCase(failureContext))
		{
			List<JSONObject> failureRootCausesArray = getJSONArrayForRootCauses(failureType);
			if( failureRootCausesArray != null && !failureRootCausesArray.isEmpty() ) {
				node.put(FAILURE_ROOT_CAUSE_CHILDREN, failureRootCausesArray);
			} else {
				node.remove(DEFINITION);
				node.remove(ID);
				node.remove(NODE_TYPE);
				node.remove(INSTANCE_OF);
			}
		}

		return node;
	}

	private List<JSONObject> getJSONArrayForRootCauses(FailureType failureType)
			throws JSONException {
		List<JSONObject> causes = new ArrayList<JSONObject>();
		List<FailureRootCause> failureCauses = failureStructureService
				.findFailureRootCausesForFailureType(failureType);
		for (FailureRootCause rootCause : failureCauses) {
			causes.add(getJSONObjectForFailureRootCause(rootCause));
		}
		return causes;
	}

	private JSONObject getJSONObjectForFailureRootCause(FailureRootCause rootCause)
			throws JSONException {
		JSONObject node = new JSONObject();
		// Creating Definition
		JSONObject definition = new JSONObject();
		definition.put(ID, rootCause.getDefinition().getId());
		definition.put(LABEL, rootCause.getDefinition().getName());
		definition.put(CODE, rootCause.getDefinition().getCode());

		// Putting Values
		node.put(DEFINITION, definition);
		node.put(ID, rootCause.getId());
		node.put(NODE_TYPE, NODE_TYPE_LEAF);
		node.put(INSTANCE_OF, FailureRootCause.class.getSimpleName());
		return node;
	}

	private List<JSONObject> getJSONArrayForCauses(FailureType failureType)
			throws JSONException {
		List<JSONObject> causes = new ArrayList<JSONObject>();
		List<FailureCause> failureCauses = failureStructureService
				.findFailureCausesForFailureType(failureType);
		for (FailureCause cause : failureCauses) {
			causes.add(getJSONObjectForFailureCause(cause));
		}
		return causes;
	}
	
	private JSONObject getJSONObjectForFailureCause(FailureCause cause)
			throws JSONException {
		JSONObject node = new JSONObject();
		// Creating Definition
		JSONObject definition = new JSONObject();
		definition.put(ID, cause.getDefinition().getId());
		definition.put(LABEL, cause.getDefinition().getName());
		definition.put(CODE, cause.getDefinition().getCode());

		// Putting Values
		node.put(DEFINITION, definition);
		node.put(ID, cause.getId());
		node.put(NODE_TYPE, NODE_TYPE_LEAF);
		node.put(INSTANCE_OF, FailureCause.class.getSimpleName());
		return node;
	}

	//deSerialization Code .......................................
	public void deserialize(String updatedTree,
			ItemGroup itemGroup, String failureContext) throws JSONException {
		JSONTokener toknizer = new JSONTokener(updatedTree);
        JSONObject root = new JSONObject(toknizer);
        validateTree(root, failureContext);
        assert root.getString(NODE_TYPE).equals(NODE_TYPE_ROOT);//must have a root node
        JSONArray ftArray = root.getJSONArray(FAILURE_TYPE_CHILDREN);
        List<FailureType> failureTypes = deleteRemovedFailureTypes(itemGroup, ftArray);
        for (FailureType failureType : failureTypes) {
			failureType = manageFailureTypeDefinitionAndCauses(ftArray, failureType, failureContext);
		}
        addNewFailureTypes(itemGroup, ftArray, failureContext);
	}


	private void validateTree(JSONObject root, String failureContext) throws JSONException {
		try {
			assert root.getString(NODE_TYPE).equals(NODE_TYPE_ROOT);//must have a root node
			JSONArray ftArray = root.getJSONArray(FAILURE_TYPE_CHILDREN);
			for (int i = 0; i < ftArray.length(); i++) {
				JSONObject ftObject = ftArray.getJSONObject(i);
				assert ftObject != null;
				assert ftObject.getString(ID) != null;
				JSONObject ftDefObject = ftObject.getJSONObject(DEFINITION);
				assert ftDefObject != null;
				assert ftDefObject.getString(ID) != null;
				JSONArray fcArray = null;
				if (FAILURE_CONTEXT_CAUSE.equalsIgnoreCase(failureContext))
					fcArray = ftObject.getJSONArray(FAILURE_CAUSE_CHILDREN);
				else if (FAILURE_CONTEXT_ROOT_CAUSE.equalsIgnoreCase(failureContext))
					fcArray = ftObject.getJSONArray(FAILURE_ROOT_CAUSE_CHILDREN);
				if (fcArray!=null)
				{
					for (int j = 0; j < fcArray.length(); j++) {
						JSONObject fcObject = fcArray.getJSONObject(j);
						assert fcObject != null;
						assert fcObject.getString(ID) != null;
						JSONObject fcDefObject = fcObject.getJSONObject(DEFINITION);
						assert fcDefObject.getString(ID) != null;
					}
				}
			}
		} catch (AssertionError e) {
			throw new IllegalFailureTypeDataException(e.getMessage());
		}
	}

	/**
	 * Takes care of setting the right definition for the failureType and then
	 * manages the failureCauses for that failureType
	 * 
	 * @param ftArray
	 * @param failureType
	 * @return
	 * @throws JSONException
	 */
	private FailureType manageFailureTypeDefinitionAndCauses(JSONArray ftArray,
			FailureType failureType, String failureContext) throws JSONException {
		JSONObject ftObject = getMatchingFTObject(ftArray, failureType);
		failureType = (FailureType) setCorrectDefinitionAndReturn(ftObject, failureType);
		List<FailureCause> failureCauses = null;
		List<FailureRootCause> failureRootCauses = null;
		JSONArray fcArray = null;
		if (FAILURE_CONTEXT_CAUSE.equalsIgnoreCase(failureContext))
		{
			failureCauses = failureStructureService.findFailureCausesForFailureType(failureType);
			fcArray = ftObject.getJSONArray(FAILURE_CAUSE_CHILDREN);
			if (fcArray!=null)
			{
				failureCauses = deleteRemovedFailureCauses(failureType, failureCauses, fcArray);
				manageFailureCauseDefinitions(failureType, failureCauses, fcArray);
				addNewFailureCauses(failureType, fcArray);
			}
		}
		else if (FAILURE_CONTEXT_ROOT_CAUSE.equalsIgnoreCase(failureContext))
		{
			failureRootCauses = failureStructureService.findFailureRootCausesForFailureType(failureType);
			fcArray = ftObject.getJSONArray(FAILURE_ROOT_CAUSE_CHILDREN);
			if (fcArray!=null)
			{
				failureRootCauses = deleteRemovedFailureRootCauses(failureType, failureRootCauses, fcArray);
				manageFailureRootCauseDefinitions(failureType, failureRootCauses, fcArray);
				addNewFailureRootCauses(failureType, fcArray);
			}
		}
		return failureType;
	}

	/**
	 * Adds the unpersisted FailureRootCauses for a FailureType
	 * @param failureType
	 * @param fcArray
	 * @throws JSONException
	 */
	private void addNewFailureRootCauses(FailureType failureType, JSONArray fcArray) throws JSONException {
		JSONArray failureRootCauseToBeAdded = findEntitiesToBeAdded(fcArray);
		for (int i = 0; i < failureRootCauseToBeAdded.length(); i++) {
			persistFailureRootCause(failureRootCauseToBeAdded.getJSONObject(i), failureType);
		}
	}

	/**
	 * creates a new FailureRootCause object and associates it with the right definition	
	 * and failureType and persists it. 
	 * 
	 * @param fcObject
	 * @param failureType
	 * @throws JSONException
	 */
	private void persistFailureRootCause(JSONObject fcObject, FailureType failureType) throws JSONException {
		Long defId = fcObject.getJSONObject(DEFINITION).getLong(ID);
		FailureRootCauseDefinition def = (FailureRootCauseDefinition) failureStructureService
				.findObjectByPrimaryKey(FailureRootCauseDefinition.class, defId);
		FailureRootCause failureRootCause = new FailureRootCause();
		failureRootCause.setDefinition(def);
		failureRootCause.setFailureType(failureType);
		failureStructureService.saveAndReturnObject(failureRootCause);
	}
	
	/**
	 * Takes care of setting the right definition for the failureRootCauses of a FailureType
	 * @param failureType
	 * @param failureRootCauses
	 * @param fcArray
	 * @throws JSONException
	 */
	private void manageFailureRootCauseDefinitions(FailureType failureType,
			List<FailureRootCause> failureRootCauses, JSONArray fcArray)
			throws JSONException {
		for (FailureRootCause failureRootCause : failureRootCauses) {
			JSONObject fcObject = getMatchingFRCObject(fcArray, failureRootCause);
			setCorrectDefinitionAndReturn(fcObject, failureRootCause);
		}
		failureRootCauses = failureStructureService.findFailureRootCausesForFailureType(failureType);
	}

	/**
	 * Deletes the failureRootCauses that have been removed from a failureType
	 * @param failureType
	 * @param failureRootCauses
	 * @param fcArray
	 * @return
	 * @throws JSONException
	 */
	private List<FailureRootCause> deleteRemovedFailureRootCauses(
			FailureType failureType, List<FailureRootCause> failureRootCauses,
			JSONArray fcArray) throws JSONException {
		List<FailureRootCause> failureRootCausesToBeDeleted = getFailureRootCausesToBeDeleted(
				fcArray, failureRootCauses);
		deleteFailureRootCauses(failureRootCausesToBeDeleted);
		failureRootCauses = failureStructureService.findFailureRootCausesForFailureType(failureType);
		return failureRootCauses;
	}

	/**
	 * Deletes the failureRootCauses in the collection
	 * @param toBeDeleted
	 */
	private void deleteFailureRootCauses(List<FailureRootCause> toBeDeleted) {
		for (FailureRootCause rootCause : toBeDeleted) {
			failureStructureService.deleteObject(rootCause);
		}
	}
	
	/**
	 * Compares the JSONArray to the actual list of failureRootCauses and returns the 
	 * failureCauses that must be deleted
	 * 
	 * @param fcArray
	 * @param failureRootCauses
	 * @return
	 * @throws JSONException
	 */
	private List<FailureRootCause> getFailureRootCausesToBeDeleted(
			JSONArray fcArray, List<FailureRootCause> failureRootCauses)
			throws JSONException {
		List<FailureRootCause> toBeDeleted = new ArrayList<FailureRootCause>();
		for (FailureRootCause failureRootCause : failureRootCauses) {
			if(getMatchingFRCObject(fcArray, failureRootCause) == null) {
				toBeDeleted.add(failureRootCause);
			}
		}
		return toBeDeleted;
	}
	
	/**
	 * Compares the id's of a JSONArray of objects and the FailureRootCause and returns
	 * the JSONObject that matches the id. else returns null.
	 * 
	 * @param ftArray
	 * @param failureType
	 * @return
	 * @throws JSONException
	 */
	private JSONObject getMatchingFRCObject(JSONArray fcArray,
			FailureRootCause failureRootCause) throws JSONException {
		for(int i = 0; i < fcArray.length(); i++) {
			if(getNodeId(fcArray.getJSONObject(i)).equals(failureRootCause.getId())) {
				return fcArray.getJSONObject(i);
			}
		}
		return null;
	}

	/**
	 * Takes care of setting the right definition for the failureCauses of a FailureType
	 * @param failureType
	 * @param failureCauses
	 * @param fcArray
	 * @throws JSONException
	 */
	private void manageFailureCauseDefinitions(FailureType failureType, List<FailureCause> failureCauses, JSONArray fcArray) throws JSONException {
		for (FailureCause failureCause : failureCauses) {
			JSONObject fcObject = getMatchingFCObject(fcArray, failureCause);
			setCorrectDefinitionAndReturn(fcObject, failureCause);
		}
		failureCauses = failureStructureService.findFailureCausesForFailureType(failureType);
	}

	/**
	 * Adds the unpersisted FailureCauses for a FailureType
	 * @param failureType
	 * @param fcArray
	 * @throws JSONException
	 */
	private void addNewFailureCauses(FailureType failureType, JSONArray fcArray) throws JSONException {
		JSONArray failureCauseToBeAdded = findEntitiesToBeAdded(fcArray);
		for (int i = 0; i < failureCauseToBeAdded.length(); i++) {
			persistFailureCause(failureCauseToBeAdded.getJSONObject(i), failureType);
		}
	}

	/**
	 * Deletes the failureCauses that have been removed from a failureType
	 * @param failureType
	 * @param failureCauses
	 * @param fcArray
	 * @return
	 * @throws JSONException
	 */
	private List<FailureCause> deleteRemovedFailureCauses(FailureType failureType, List<FailureCause> failureCauses, JSONArray fcArray) throws JSONException {
		List<FailureCause> failureCausesToBeDeleted = getFailureCausesToBeDeleted(fcArray, failureCauses);
		deleteFailureCauses(failureCausesToBeDeleted);
		failureCauses = failureStructureService.findFailureCausesForFailureType(failureType);
		return failureCauses;
	}

	/**
	 * Adds the New Failure types
	 * 
	 * @param itemGroup
	 * @param ftArray
	 * @throws JSONException
	 */
	private void addNewFailureTypes(ItemGroup itemGroup, JSONArray ftArray, String failureContext) throws JSONException {
		JSONArray failureTypesToBeAdded = findEntitiesToBeAdded(ftArray);
        for (int i = 0; i < failureTypesToBeAdded.length(); i++) {
			persistFailureType(failureTypesToBeAdded.getJSONObject(i), itemGroup, failureContext);
		}
	}

	/**
	 * deletes the failuretypes that have been removed in the UI
	 * 
	 * @param itemGroup
	 * @param ftArray
	 * @return
	 * @throws JSONException
	 */
	private List<FailureType> deleteRemovedFailureTypes(ItemGroup itemGroup, JSONArray ftArray) throws JSONException {
		List<FailureType> failureTypes = failureStructureService.findFailureTypesForItemGroup(itemGroup);
        List<FailureType> failureTypesToBeDeleted = getFailureTypesToBeDeleted(ftArray, failureTypes);
        deleteFailureTypes(failureTypesToBeDeleted);
        failureTypes = failureStructureService.findFailureTypesForItemGroup(itemGroup);
		return failureTypes;
	}
	
	/**
	 * Compares the JSONArray to the actual list of failureTypes and returns the 
	 * failureTypes that must be deleted
	 * 
	 * @param ftArray
	 * @param failureTypes
	 * @return
	 * @throws JSONException
	 */
	private List<FailureType> getFailureTypesToBeDeleted(JSONArray ftArray, List<FailureType> failureTypes) throws JSONException {
		List<FailureType> toBeDeleted = new ArrayList<FailureType>();
		for (FailureType failureType : failureTypes) {
			if(getMatchingFTObject(ftArray, failureType) == null) {
				toBeDeleted.add(failureType);
			}
		}
		return toBeDeleted;
	}
	
	/**
	 * Compares the JSONArray to the actual list of failureCauses and returns the 
	 * failureCauses that must be deleted
	 * 
	 * @param fcArray
	 * @param failureCauses
	 * @return
	 * @throws JSONException
	 */
	private List<FailureCause> getFailureCausesToBeDeleted(JSONArray fcArray, List<FailureCause> failureCauses) throws JSONException {
		List<FailureCause> toBeDeleted = new ArrayList<FailureCause>();
		for (FailureCause failureCause : failureCauses) {
			if(getMatchingFCObject(fcArray, failureCause) == null) {
				toBeDeleted.add(failureCause);
			}
		}
		return toBeDeleted;
	}
	
	/**
	 * Deletes the FailureTypes in the collection.
	 * Also takes care of deleting the failureCauses for the FailureType first.
	 * @param toBeDeleted
	 */
	private void deleteFailureTypes(List<FailureType> toBeDeleted) {
		for (FailureType type : toBeDeleted) {
			List<FailureCause> causes = failureStructureService.findFailureCausesForFailureType(type);
			if(causes != null && !causes.isEmpty()) {
				deleteFailureCauses(causes);
			}
			failureStructureService.deleteObject(type);
		}
	}
	
	/**
	 * Deletes the failureCauses in the collection
	 * @param toBeDeleted
	 */
	private void deleteFailureCauses(List<FailureCause> toBeDeleted) {
		for (FailureCause cause : toBeDeleted) {
			failureStructureService.deleteObject(cause);
		}
	}
	
	/**
	 * Compares the id's of a JSONArray of objects and the FailureType and returns
	 * the JSONObject that matches the id. else returns null.
	 * 
	 * @param ftArray
	 * @param failureType
	 * @return
	 * @throws JSONException
	 */
	private JSONObject getMatchingFTObject(JSONArray ftArray, FailureType failureType) throws JSONException {
		for(int i = 0; i < ftArray.length(); i++) {
			if(getNodeId(ftArray.getJSONObject(i)).equals(failureType.getId())) {
				return ftArray.getJSONObject(i);
			}
		}
		return null;
	}
	
	/**
	 * Compares the id's of a JSONArray of objects and the FailureCause and returns
	 * the JSONObject that matches the id. else returns null.
	 * 
	 * @param ftArray
	 * @param failureType
	 * @return
	 * @throws JSONException
	 */
	private JSONObject getMatchingFCObject(JSONArray fcArray, FailureCause failureCause) throws JSONException {
		for(int i = 0; i < fcArray.length(); i++) {
			if(getNodeId(fcArray.getJSONObject(i)).equals(failureCause.getId())) {
				return fcArray.getJSONObject(i);
			}
		}
		return null;
	}

	/**
	 * Given a JSON object it returns the id.
	 * 
	 * @param object
	 * @return
	 * @throws JSONException
	 */
	private Long getNodeId(JSONObject object) throws JSONException {
		return object.getLong(ID);
	}
	
	/**
	 * Makes sure that the definition is rightly set on a FailureType or a 
	 * FailureCause object. If not so, it sets the correct definition and then 
	 * updates and returns the FailureType/FailureCause object. 
	 *  
	 * @param object
	 * @param actual
	 * @return
	 * @throws JSONException
	 */
	private Object setCorrectDefinitionAndReturn(JSONObject object, Object actual) throws JSONException {
		if(object.getString(INSTANCE_OF).equals(FailureType.class.getSimpleName())) {
			FailureType failureType = (FailureType) actual;
			FailureTypeDefinition def = (FailureTypeDefinition) failureStructureService.findObjectByPrimaryKey(FailureTypeDefinition.class, object.getJSONObject(DEFINITION).getLong(ID));
			if(failureType.getDefinition() == null || !failureType.getDefinition().equals(def)) {
				failureType.setDefinition(def);
				return failureStructureService.updateAndReturnObject(failureType);
			}
		} else if (object.getString(INSTANCE_OF).equals(FailureCause.class.getSimpleName())){
			FailureCause failureCause = (FailureCause) actual;
			FailureCauseDefinition def = (FailureCauseDefinition) failureStructureService.findObjectByPrimaryKey(FailureCauseDefinition.class, object.getJSONObject(DEFINITION).getLong(ID));
			if(failureCause.getDefinition() == null || !failureCause.getDefinition().equals(def)) {
				failureCause.setDefinition(def);
				return failureStructureService.updateAndReturnObject(failureCause);
			}
		}
		else
		{
			FailureRootCause failureRootCause = (FailureRootCause) actual;
			FailureRootCauseDefinition def = (FailureRootCauseDefinition) failureStructureService.findObjectByPrimaryKey(FailureRootCauseDefinition.class, object.getJSONObject(DEFINITION).getLong(ID));
			if(failureRootCause.getDefinition() == null || !failureRootCause.getDefinition().equals(def)) {
				failureRootCause.setDefinition(def);
				return failureStructureService.updateAndReturnObject(failureRootCause);
			}
		}
		return actual;
	}
	
	/**
	 * Finds all objects in an array that have and id 0 and returns their array.
	 * 
	 * @param anArray
	 * @return
	 * @throws JSONException
	 */
	private JSONArray findEntitiesToBeAdded(JSONArray anArray) throws JSONException {
		JSONArray toBeAdded = new JSONArray();
		for (int i = 0; i < anArray.length(); i++) {
			JSONObject anObject = anArray.getJSONObject(i);
			if(anObject.getLong(ID) == 0) {
				toBeAdded.put(anObject);
			}
		}
		return toBeAdded;
	}
	
	/**
	 * creates a new FailureType object and associates it with the right definition	
	 * and itemGroup and persists it. 
	 * Takes care of persisting the child entries(Failure Causes). 
	 * 
	 * @param fcObject
	 * @param failureType
	 * @throws JSONException
	 */
	private void persistFailureType(JSONObject ftObject, ItemGroup itemGroup, String failureContext) throws JSONException {
		Long defId = ftObject.getJSONObject(DEFINITION).getLong(ID);
		FailureTypeDefinition def = (FailureTypeDefinition) failureStructureService.findObjectByPrimaryKey(FailureTypeDefinition.class, defId);
		FailureType failureType = new FailureType();
		failureType.setDefinition(def);
		failureType.setForItemGroup(itemGroup);
		failureType = (FailureType) failureStructureService.saveAndReturnObject(failureType);
		JSONArray fcArray = null;
		if (FAILURE_CONTEXT_CAUSE.equalsIgnoreCase(failureContext))
		{
			fcArray = ftObject.getJSONArray(FAILURE_CAUSE_CHILDREN);
			if (fcArray!=null)
				for (int i = 0; i < fcArray.length(); i++) {
					persistFailureCause(fcArray.getJSONObject(i), failureType);
				}
		}
		else if (FAILURE_CONTEXT_ROOT_CAUSE.equalsIgnoreCase(failureContext))
		{
			fcArray = ftObject.getJSONArray(FAILURE_ROOT_CAUSE_CHILDREN);
			if (fcArray!=null)
				for (int i = 0; i < fcArray.length(); i++) {
					persistFailureRootCause(fcArray.getJSONObject(i), failureType);
				}
		}
	}
	
	/**
	 * creates a new FailureCause object and associates it with the right definition	
	 * and failureType and persists it. 
	 * 
	 * @param fcObject
	 * @param failureType
	 * @throws JSONException
	 */
	private void persistFailureCause(JSONObject fcObject, FailureType failureType) throws JSONException {
		Long defId = fcObject.getJSONObject(DEFINITION).getLong(ID);
		FailureCauseDefinition def = (FailureCauseDefinition) failureStructureService.findObjectByPrimaryKey(FailureCauseDefinition.class, defId);
		FailureCause failureCause = new FailureCause();
		failureCause.setDefinition(def);
		failureCause.setFailureType(failureType);
		failureStructureService.saveAndReturnObject(failureCause);
	}
	
	class IllegalFailureTypeDataException extends RuntimeException {
		
		//this exception gets raised whenever tree passed is found to have some validatio errors.
	    public IllegalFailureTypeDataException(String messageKey, Throwable e) {
	        super(messageKey, e);
	    }
	    public IllegalFailureTypeDataException(String messageKey) {
	        super(messageKey);
	    }
	}

}
