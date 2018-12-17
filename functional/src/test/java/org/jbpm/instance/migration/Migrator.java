/*
 * *##% 
 * jBPM Instance Migrator
 * Copyright (C) null - 2009 JBoss Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * ##%*
 */
package org.jbpm.instance.migration;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.JbpmContext;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.context.exe.TokenVariableMap;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.SuperState;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.Fork;
import org.jbpm.graph.node.Join;
import org.jbpm.graph.node.ProcessState;
import org.jbpm.graph.node.StartState;
import org.jbpm.graph.node.State;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.instance.migration.util.JbpmInstanceMigratorLogger;
import org.jbpm.scheduler.exe.Timer;
import org.jbpm.taskmgmt.def.Swimlane;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskInstanceUtil;



/**
 * An instance of this class is responsible for migrating a process instance to te latest version. 
 * @author Caleb Powell <caleb.powell@intelliware.ca> 
 * @author David Harcombe <david.harcombe@intelliware.ca> 
 */
public class Migrator {

	private static Logger logger = Logger.getLogger(JbpmInstanceMigratorLogger.class);
	private static final String ROOT_TOKEN_NAME = "Root token";
	private final SortedSet migrations = new TreeSet(new MigrationComparator());
	private final Map subProcessMigrators = new HashMap();
	private final JbpmContext jbpmContext;
	private final String processDefinitionName;
	private final StateNodeMap compositeNodeMap = new StateNodeMap();
	private final List migrationHandlers = new ArrayList();
	private final Map<Token, Token> tokenMap = new HashMap<Token, Token>();
	private final Map<TaskInstance, TaskInstance> taskInstancesMap = new HashMap<TaskInstance, TaskInstance>();
	public static Set SUPPORTED_WAIT_STATE_NODE_TYPES = new HashSet(){
		private static final long serialVersionUID = 9100798202825510066L;
	{
		add(StartState.class);
		add(EndState.class);
		add(State.class);
		add(Fork.class);
		add(Join.class);
		add(ProcessState.class);
		add(SuperState.class);
		add(TaskNode.class);
	}};

	/**
	 * 
	 * @param processDefinitionName The name of the ProcessDefinition that this migrator is responsible for.
	 * @param jbpmContext A JBPMContext that the migrator can use to look up the latest ProcessDefinition (among other things).
	 * @param migrations An arrray of migration instances.
	 * @param subProcessMigrators An array of migrator classes for any subprocesses that this Migrator may encounter.
	 * @throws InvalidMigrationException
	 */
	public Migrator(String processDefinitionName, JbpmContext jbpmContext, Migration[] migrations, Migrator[] subProcessMigrators) throws InvalidMigrationException {
		this.processDefinitionName = processDefinitionName;
		this.jbpmContext = jbpmContext;

		if(!ArrayUtils.isEmpty(migrations)) {
			CollectionUtils.addAll(this.migrations, migrations);
		}
		
		for (int i = 0; subProcessMigrators != null && i < subProcessMigrators.length; i++) {
			this.subProcessMigrators.put(subProcessMigrators[i].getProcessDefinitionName(), subProcessMigrators[i]);
		}

		populateCompositeNodeMap();
	}

	/**
	 * 
	 * @param processDefinitionName The name of the ProcessDefinition that this migrator is responsible for.
	 * @param jbpmContext A JBPMContext that the migrator can use to look up the latest ProcessDefinition (among other things).
	 * @param baseClassName The base name for the migration classes. For example, if given 'ca.intelliware.foo.FooProcess' as the baseClassName, the migrator
	 * will attempt load 'ca.intelliware.foo.FooProcess001', ''ca.intelliware.foo.FooProcess002', etc.
	 * @param subProcessMigrators An array of migrator classes for any subprocesses that this Migrator may encounter.
	 * @throws InvalidMigrationException
	 */
	public Migrator(String processDefinitionName, JbpmContext jbpmContext, String baseClassName, Migrator[] subProcessMigrators) throws InvalidMigrationException {
		this(processDefinitionName, jbpmContext, MigrationUtils.lookupMigrationsFor(baseClassName), subProcessMigrators);
	}

	/**
	 * 
	 * @param processDefinitionName The name of the ProcessDefinition that this migrator is responsible for.
	 * @param jbpmContext A JBPMContext that the migrator can use to look up the latest ProcessDefinition (among other things).
	 * @param baseClassName The base name for the migration classes. For example, if given 'ca.intelliware.foo.FooProcess' as the baseClassName, the migrator
	 * will attempt load 'ca.intelliware.foo.FooProcess001', ''ca.intelliware.foo.FooProcess002', etc.
	 * @throws InvalidMigrationException
	 */
	public Migrator(String processDefinitionName, JbpmContext jbpmContext, String baseClassName) throws InvalidMigrationException {
		this(processDefinitionName, jbpmContext, MigrationUtils.lookupMigrationsFor(baseClassName), null);
	}
	
	/**
	 * Migrates the ProcessInstance instance to the latest version.
	 * @param processInstance The processInstance that you wish to migrate.
	 * @return A migrated processInstance based on the latest version of this Migrator's ProcessDefinition. If the processInstance does not require migration,
	 * this method will return the provided processInstance object.
	 * @throws InvalidMigrationException
	 */
	public ProcessInstance migrate(ProcessInstance processInstance) {
		if(!willMigrate(processInstance.getProcessDefinition())){
			String errorMessage = "The "+getProcessDefinitionName()+" migrator cannot migrate a processInstance of the "+processInstance.getProcessDefinition().getName()+" ProcessDefinition!";
			logger.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}
		
		ProcessInstance newProcessInstance = null;
		if(processRequiresMigration(processInstance, jbpmContext)) {
			logger.info(getProcessDefinitionName()+" Migrator attempting to migrate processInstance[@id="+processInstance.getId()+"].");
			newProcessInstance = migrateOldProcessInstance(processInstance);
			for (Iterator iterator = migrationHandlers.iterator(); iterator.hasNext();) {
				MigrationHandler migrationHandler = (MigrationHandler) iterator.next();
				migrationHandler.migrateInstance(processInstance, newProcessInstance);
			}
			logger.info(getProcessDefinitionName()+" Migrator finished migration of processInstance[@id="+processInstance.getId()+"].");
		} else {
			newProcessInstance = processInstance;
		}
		return newProcessInstance;
	}
	
	public List<Timer> migrate(Session session , ProcessInstance oldProcessInstance) {
		
		Query query = session.createQuery("select t from Timer t where t.processInstance.id = :processInstanceId").setParameter("processInstanceId", oldProcessInstance.getId());
    	List<Timer> oldTimers = query.list();
		List<Timer> newTimers = new ArrayList<Timer>();
		
		for(Timer oldTimer : oldTimers){
			TaskInstance newTaskInstance = taskInstancesMap.get(oldTimer.getTaskInstance());
			Timer newTimer = new Timer(newTaskInstance.getToken());
			newTimer.setTaskInstance(newTaskInstance);
			newTimer.setName(oldTimer.getName());
			newTimer.setDueDate(oldTimer.getDueDate());
			newTimer.setRepeat(oldTimer.getRepeat());
			newTimer.setTransitionName(oldTimer.getTransitionName());
			newTimer.setException(oldTimer.getException());
			newTimer.setSuspended(oldTimer.isSuspended());
			if( oldTimer.getGraphElement() instanceof Task){
				newTimer.setGraphElement(newTaskInstance.getTask());
			}
			Action action = getNewActionForTimer(session, oldTimer,newTaskInstance.getTask().getProcessDefinition());
			newTimer.setAction(action);
			newTimers.add(newTimer);
			
		}
		
		
		return newTimers;
	}
	
	private Action getNewActionForTimer(Session session , Timer oldTimer, ProcessDefinition newProcessDefinition){

		String queryString = "select action from Delegation delegation , Action action " +
							 "where delegation.configuration = action.actionDelegation.configuration " +
							 "and delegation.className = action.actionDelegation.className " +
							 "and action.actionDelegation.processDefinition = :newProcessDefinition " +
							 "and delegation = :oldDelegation " ;
		Query query = session.createQuery(queryString);
		query.setParameter("newProcessDefinition", newProcessDefinition).setParameter("oldDelegation", oldTimer.getAction().getActionDelegation());
		Action newAction = (Action)query.uniqueResult();
		return newAction;
							 
	}

	/**
	 * This method iterates through 'migrations' list and populates the compositeNodeMap with each Migration mappings.
	 * @throws InvalidMigrationException
	 */
	private void populateCompositeNodeMap() {
		for (Iterator iterator = this.migrations.iterator(); iterator.hasNext();) {
			Migration currentMigration = (Migration) iterator.next();
			logger.debug("Adding the " + currentMigration.getClass().getName()+" migration node mappings to the composite node map.");
			this.compositeNodeMap.addMigration(currentMigration);
		}
	}
	
	private ProcessInstance migrateOldProcessInstance(ProcessInstance processInstance) {
		ProcessInstance newProcessInstance = MigrationUtils.findLatestProcessDefinition(processInstance.getProcessDefinition().getName(), jbpmContext).createProcessInstance();
		migrateContextInstance(processInstance, newProcessInstance);
		mapAllTokens(null, processInstance.getRootToken(), newProcessInstance);
		migrateTaskInstances( processInstance,  newProcessInstance);
		return newProcessInstance;
	}

	public void migrateTaskInstances(ProcessInstance oldProcessInstance, ProcessInstance newProcessInstance) {
		Collection<TaskInstance> taskInstances = oldProcessInstance.getTaskMgmtInstance().getTaskInstances();
		Map<SwimlaneInstance, SwimlaneInstance> swimlaneInstancesMap = new HashMap<SwimlaneInstance, SwimlaneInstance>();
		for (Iterator<TaskInstance> iterator = taskInstances.iterator(); iterator.hasNext();) {
			TaskInstance taskInstance = iterator.next();
			Node oldNode = taskInstance.getTask().getTaskNode();
			Node newNode = findCurrentNode(oldNode,newProcessInstance.getProcessDefinition());
			
			TaskInstance newTaskInstance = TaskInstanceUtil.createTaskInstance(taskInstance, (Task)((TaskNode)newNode).getTasks().iterator().next());
			newTaskInstance.setTaskMgmtInstance(newProcessInstance.getTaskMgmtInstance());
			newTaskInstance.setToken(tokenMap.get(taskInstance.getToken()));
			if(taskInstance.getSwimlaneInstance()!=null){
				SwimlaneInstance newSwimlaneInstance = getNewSwimlaneInstance(swimlaneInstancesMap,taskInstance.getSwimlaneInstance(),newProcessInstance);
				newTaskInstance.copySwimlaneInstanceAssignment(newSwimlaneInstance);	
			}
			newProcessInstance.getTaskMgmtInstance().addTaskInstance(newTaskInstance);
			taskInstancesMap.put(taskInstance, newTaskInstance);
		}
		tokenMap.clear();// clear all token mappings so that the map object can be used again while migrating the next ProcessInstance
	}
	
	private SwimlaneInstance getNewSwimlaneInstance(Map<SwimlaneInstance, SwimlaneInstance> swimlaneInstancesMap,SwimlaneInstance oldSwimlaneInstance,ProcessInstance newProcessInstance){
		SwimlaneInstance newSwimlaneInstance;
		if(swimlaneInstancesMap.containsKey(oldSwimlaneInstance))
				newSwimlaneInstance = swimlaneInstancesMap.get(oldSwimlaneInstance); 
		else{
			newSwimlaneInstance = createSwimlaneInstance(oldSwimlaneInstance,newProcessInstance);
			swimlaneInstancesMap.put(oldSwimlaneInstance, newSwimlaneInstance);
		}
		return newSwimlaneInstance;
	}
	
	private SwimlaneInstance createSwimlaneInstance (SwimlaneInstance oldSwimlaneInstance,ProcessInstance newProcessInstance){
		Swimlane newSwimlane = (Swimlane)newProcessInstance.getProcessDefinition().getTaskMgmtDefinition().getSwimlanes().get(oldSwimlaneInstance.getSwimlane().getName());
		SwimlaneInstance newSwimlaneInstance = new SwimlaneInstance(newSwimlane);
		newProcessInstance.getTaskMgmtInstance().addSwimlaneInstance(newSwimlaneInstance);
		newSwimlaneInstance.setActorId(oldSwimlaneInstance.getActorId());
		return newSwimlaneInstance;
	}


	private boolean processRequiresMigration(ProcessInstance processInstance, JbpmContext jbpmContext) {
		return MigrationUtils.requiresMigration(processInstance, jbpmContext);
	}

	private void migrateContextInstance(ProcessInstance oldProcessInstance, ProcessInstance newProcessInstance) {
		ContextInstance oldContextInstance = oldProcessInstance.getContextInstance();
		ContextInstance newContextInstance = newProcessInstance.getContextInstance();
		migratePersistedVariables(oldContextInstance, newContextInstance);
		migrateTransientVariables(oldContextInstance, newContextInstance);
		addMigrationMemo(newContextInstance, oldProcessInstance, newProcessInstance);
	}

	private void addMigrationMemo(ContextInstance newContextInstance, ProcessInstance oldProcessInstance,
			ProcessInstance newProcessInstance) {
		int oldVersion = oldProcessInstance.getProcessDefinition().getVersion();
		int newVersion = newProcessInstance.getProcessDefinition().getVersion();
		Date today = Calendar.getInstance().getTime();
		newContextInstance.createVariable("migrationMemo", createMigrationMemo(oldVersion, newVersion, today, oldProcessInstance.getId()));
	}

	private String createMigrationMemo(int oldVersion, int newVersion, Date today, long predecessorProcessId) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Process migrated from version [");
		buffer.append(oldVersion);
		buffer.append("] to [");
		buffer.append(newVersion);
		buffer.append("] on ");
		buffer.append(today.toString());
		buffer.append(". Predecessor process instance id => ");
		buffer.append(predecessorProcessId);
		return buffer.toString();
	}

	private void migrateTransientVariables(ContextInstance contextInstance, ContextInstance newContextInstance) {
		if (MapUtils.isNotEmpty(contextInstance.getTransientVariables())) {
			logger.info(getProcessDefinitionName()+" Migrator is migrating the TransientVariables map");
			newContextInstance.setTransientVariables(contextInstance.getTransientVariables());
		}
	}

	private void migratePersistedVariables(ContextInstance contextInstance, ContextInstance newContextInstance) {
		if (MapUtils.isNotEmpty(contextInstance.getVariables())) {
			logger.info(getProcessDefinitionName()+" Migrator is migrating the PersistedVariables map");
			
			setVariables(newContextInstance,contextInstance.getVariables());
		}
	}
	
	private void setVariables(ContextInstance newContextInstance , Map<String, Object> variables){
		TokenVariableMap tokenVariableMap = newContextInstance.getOrCreateTokenVariableMap(newContextInstance.getProcessInstance().getRootToken());
		for (Map.Entry<String, Object> entry : variables.entrySet()) {
			String name = entry.getKey();
			Object value = entry.getValue();
			tokenVariableMap.setVariable(name, value);      
		}
	}

	
	
	

	private void mapAllTokens(Token parentToken, Token oldProcessToken, ProcessInstance newProcessInstance) {
		mapProcessToken(parentToken, oldProcessToken, newProcessInstance);
		mapChildTokens(oldProcessToken, newProcessInstance);
	}

	private void mapProcessToken(Token parentToken, Token oldToken, ProcessInstance newInstance) {
		Node toNode = findCurrentNode(oldToken);
		Token newToken = createNewToken(parentToken, oldToken, newInstance, toNode);
		if(oldToken.getSubProcessInstance() != null) {
			mapSubProcess(oldToken, newToken);
		}
	}

	private Token createNewToken(Token parentToken, Token oldToken, ProcessInstance newInstance, Node toNode) {
		Token newToken;
		logger.info(getProcessDefinitionName()+" Migrator creating new Token named '"+getTokenName(oldToken)+"'");
		if (oldToken.isRoot()) {
			newToken = newInstance.getRootToken();
		} else {
			newToken = new Token(parentToken, getTokenName(oldToken));
		}
		newToken.setNodeEnter(new Date());
		newToken.setNode(toNode);
		tokenMap.put(oldToken, newToken);
		logger.info(getProcessDefinitionName()+" Migrator has converted node token ["+ getTokenName(oldToken) +"] from ["+ oldToken.getNode().getFullyQualifiedName()+"] to ["+toNode.getFullyQualifiedName()+"] for process ["+newInstance.getProcessDefinition().getName()+"]");
		return newToken;
	}

	private String getTokenName(Token token) {
		return token.getName() == null ? ROOT_TOKEN_NAME : token.getName();
	}

	private void mapSubProcess(Token oldSuperProcessToken, Token newSuperProcessToken) {
		ProcessInstance oldSubProcess = oldSuperProcessToken.getSubProcessInstance();
		logger.info(getProcessDefinitionName() + " migrator is attempting to migrate a "+oldSubProcess.getProcessDefinition().getName()+ " sub-process instance.");
		ProcessDefinition newSubProcessDefinition = this.jbpmContext.getGraphSession().findLatestProcessDefinition(oldSubProcess.getProcessDefinition().getName());
		
		ProcessInstance newSubProcessInstance =	getSubProcessMigrator(newSubProcessDefinition.getName()).migrateOldProcessInstance(oldSubProcess);
		newSubProcessInstance.setSuperProcessToken(newSuperProcessToken);
		newSuperProcessToken.setSubProcessInstance(newSubProcessInstance);
	}

	private Migrator getSubProcessMigrator(String processDefinitionName) {
		if(!this.subProcessMigrators.containsKey(processDefinitionName)){
			//create a default migrator for the subprocess definition
			Migrator subProcessMigrator = new Migrator(processDefinitionName, jbpmContext, new Migration[]{}, new Migrator[]{});
			subProcessMigrators.put(processDefinitionName, subProcessMigrator);
		}
		return (Migrator) this.subProcessMigrators.get(processDefinitionName);
	}

	private void mapChildTokens(Token oldProcessToken, ProcessInstance newProcessInstance) {
		if (oldProcessToken.getChildren() != null) {
			Iterator childTokenIter = oldProcessToken.getChildren().values().iterator();
			while (childTokenIter.hasNext()) {
				Token childToken = (Token) childTokenIter.next();
				mapAllTokens(newProcessInstance.getRootToken(), childToken, newProcessInstance);
			}
		}
	}

	private Node findCurrentNode(Token oldProcessToken) {
		String nodeName = oldProcessToken.getNode().getFullyQualifiedName();
		String currentNodeName = this.compositeNodeMap.containsDeprecatedNodeName(nodeName) ? this.compositeNodeMap.getCurrentNodeName(nodeName) : nodeName;
		logger.debug(getProcessDefinitionName()+" Migrator.findCurrentNode: mapping '"+nodeName+"' => '"+currentNodeName+"'");
		ProcessDefinition targetDefinition = MigrationUtils.findLatestProcessDefinition(oldProcessToken.getProcessInstance().getProcessDefinition().getName(), jbpmContext);
		return targetDefinition.findNode(currentNodeName);
	}
	
	private Node findCurrentNode(Node oldNode , ProcessDefinition targetDefinition) {
		String nodeName = oldNode.getFullyQualifiedName();
		String currentNodeName = this.compositeNodeMap.containsDeprecatedNodeName(nodeName) ? this.compositeNodeMap.getCurrentNodeName(nodeName) : nodeName;
		return targetDefinition.findNode(currentNodeName);
	}

	private String getProcessDefinitionName() {
		return this.processDefinitionName;
	}

	/**
	 * 
	 * @param processDefinition
	 * @return true if the processDefinition parameter's name attribute is equal to the 
	 * processDefinitionName attribute of this Migrator instance.
	 */
	public boolean willMigrate(ProcessDefinition processDefinition){
		return this.processDefinitionName.equals(processDefinition.getName());
	}
	
	/**
	 * Returns this Migrator's {@link StateNodeMap}, which is a composite of all the 
	 * Migrations' {@link StateNodeMap}'s.
	 * @return
	 */
	public StateNodeMap getStateNodeMap() {
		return this.compositeNodeMap;
	}

	public void addMigrationHandler(MigrationHandler migrationHandler) {
		this.migrationHandlers.add(migrationHandler);
	}

}
