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

package tavant.twms.domain.failurestruct;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.I18NAssemblyDefinition;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;
import tavant.twms.infra.Repository;
import tavant.twms.security.SecurityHelper;

public class FailureStructureRepositoryImpl extends GenericRepositoryImpl<FailureStructure, Long> implements FailureStructureRepository {

	private Repository repository;

	private SecurityHelper securityHelper;

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public FailureStructure findFailureStructureForItem(final Item item) {
		return (FailureStructure) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("findFailureStructureForItem").setParameter("item", item).uniqueResult();
			}
		});
	}

	public FailureStructure getFailureStructureForItemGroup(final ItemGroup itemGroup) {
		return (FailureStructure) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from FailureStructure fs where fs.d.active=1 and fs.forItemGroup = :itemGroup").setParameter("itemGroup", itemGroup).uniqueResult();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<FaultCodeDefinition> findFaultCodesForPart(final Item part) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("faultCodesForPart").setParameter("part", part).setParameter("partClass", part.getClassCode()).list();
			}
		});
	}

	public void update(FailureStructure failureStructure) {
		if (failureStructure.getId() != null) {// TODO: Can this be made any
			// better???
			getHibernateTemplate().update(failureStructure);
		} else {
			getHibernateTemplate().merge(failureStructure);
		}
	}

	@SuppressWarnings("unchecked")
	public List<FailureTypeDefinition> findFaultFoundOptions(final String inventoryItemId) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("faultFoundOptions").setParameter("invItemId", inventoryItemId).list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<FailureTypeDefinition> findFaultFoundOptionsForModels(final String modelId) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("faultFoundOptionsForModel").setParameter("modelId", modelId).list();
			}
		});
	}

	
	@SuppressWarnings("unchecked")
	public FailureTypeDefinition findFaultFoundOptionsForModelsByFaultName(final String modelId, final String faultName) {
		return (FailureTypeDefinition) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("faultFoundOptionsForModelByFaultName").setParameter("modelId", modelId).setParameter("faultFoundName", faultName).uniqueResult();

			}
		});
	}

	public AssemblyLevel findAssemblyLevel(final int level) {
		return (AssemblyLevel) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("findAssemblyLevel").setParameter("level", level).uniqueResult();
			}

		});
	}

	public AssemblyDefinition createAssemblyDefinition(String name, int level) {
		AssemblyLevel assemblyLevel = findAssemblyLevel(level);

		AssemblyDefinition definition = assemblyLevel.createNewDefinition(name);
		getHibernateTemplate().save(definition);

		return definition;
	}

	public AssemblyDefinition findAssemblyDefiniton(Long id) {
		return (AssemblyDefinition) getHibernateTemplate().get(AssemblyDefinition.class, id);
	}

	@SuppressWarnings("unchecked")
	public PageResult<AssemblyDefinition> findAssemblyDefinitions(final String nameStartsWith, final int level, final PageSpecification page, final String locale) {

		return (PageResult<AssemblyDefinition>) getHibernateTemplate().execute(new HibernateCallback() {

			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				List<AssemblyDefinition> assemblyDefintions = session.getNamedQuery("findAssemblyDefinitions").setParameter("level", level).list();
				Collections.sort(assemblyDefintions);
				page.setPageSize(assemblyDefintions.size());
				return new PageResult<AssemblyDefinition>(assemblyDefintions, page, 1);
			}
		});
	}

	public ActionDefinition createActionDefintion(String name) {
		ActionLevel actionLevel = (ActionLevel) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from ActionLevel").uniqueResult();
			}
		});

		ActionDefinition definition = actionLevel.create(name);
		getHibernateTemplate().save(definition);
		return definition;
	}

	public ActionDefinition findActionDefinition(Long id) {
		return (ActionDefinition) getHibernateTemplate().get(ActionDefinition.class, id);
	}

	@SuppressWarnings("unchecked")
	public PageResult<ActionDefinition> findActionDefinition(final String nameStartsWith, final PageSpecification page, final String locale) {
		return (PageResult<ActionDefinition>) getHibernateTemplate().execute(new HibernateCallback() {

			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				List<ActionDefinition> serviceProcedureDefintions = session.getNamedQuery("findActionDefinition").list();
				Collections.sort(serviceProcedureDefintions);
				page.setPageSize(serviceProcedureDefintions.size());
				return new PageResult<ActionDefinition>(serviceProcedureDefintions, page, 1);
			}
		});
	}

	public TreadBucket findTreadBucket(String code) {
		return (TreadBucket) getHibernateTemplate().get(TreadBucket.class, code);
	}

	@SuppressWarnings("unchecked")
	public Collection<TreadBucket> findTreadBuckets() {
		return getHibernateTemplate().find("from TreadBucket tb order by tb.code asc");
	}

	public void createFaultCodeDefintion(FaultCodeDefinition faultCodeDefintion) {
		getHibernateTemplate().save(faultCodeDefintion);
	}

	public FaultCodeDefinition findFaultCodeDefintiion(final String faultCode) {
		return (FaultCodeDefinition) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) {
				return session.createQuery("from FaultCodeDefinition faultCodeDefinition" + " where faultCodeDefinition.code = :code").setParameter("code", faultCode).uniqueResult();
			}
		});
	}

	public AssemblyDefinition findAssemblyDefinition(final String code, final int level) {
		return (AssemblyDefinition) getHibernateTemplate().execute(new HibernateCallback() {
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				return session.getNamedQuery("findAssemblyDefinition").setParameter("code", code).setParameter("level", level).uniqueResult();
			}
		});

	}

	public AssemblyDefinition findAssemblyDefinitionByName(final int level, final String code, final String name) {
		return (AssemblyDefinition) getHibernateTemplate().execute(new HibernateCallback() {
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				return session.getNamedQuery("findAssemblyDefinitionByName").setParameter("code", code).setParameter("level", level).setParameter("name", name).uniqueResult();
			}
		});

	}

	@SuppressWarnings("unchecked")
	public PageResult<FaultCodeDefinition> findAllFualtCodeDefinitions(ListCriteria listCriteria) {
		return (PageResult<FaultCodeDefinition>) this.repository.findPage("from FaultCodeDefinition faultCodeDefinition", listCriteria);
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	@SuppressWarnings("unchecked")
	public List<AssemblyLevel> findAllAssemblyLevels() {
		return getHibernateTemplate().find("from AssemblyLevel assemblyLevel order by assemblyLevel.level");
	}

	public FaultCodeDefinition findFaultCodeDefinitionById(Long id) {
		return (FaultCodeDefinition) this.repository.findById(FaultCodeDefinition.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<FaultCodeDefinition> findFaultCodeDefinitionsByIds(Collection<Long> ids) {
		return (List<FaultCodeDefinition>) this.repository.findByIds(FaultCodeDefinition.class, ids);
	}

	public void updateFaultCodeDefinition(FaultCodeDefinition definition) {
		this.repository.update(definition);
	}

	public void createServiceProcedureDefinition(ServiceProcedureDefinition serviceProcedureDefinition) {
		this.repository.save(serviceProcedureDefinition);
	}

	public ActionDefinition findActionDefinition(String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return (ActionDefinition) this.repository.findUniqueUsingQuery("from ActionDefinition actionDefinition " + "	where actionDefinition.code = :code", params);
	}

	public ServiceProcedureDefinition findServiceProcedureDefintiion(String fullCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", fullCode);
		return (ServiceProcedureDefinition) this.repository.findUniqueUsingQuery("from ServiceProcedureDefinition serviceProcedureDefinition " + "	where serviceProcedureDefinition.code = :code", params);
	}

	@SuppressWarnings("unchecked")
	public List<ServiceProcedureDefinition> findServiceProcedureDefinitionWhoseCodeStartsWith(final String code, final int pageNumber, final int pageSize) {
		return (List<ServiceProcedureDefinition>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from ServiceProcedureDefinition serviceProcedureDefinition where upper(serviceProcedureDefinition.code) like :code").setParameter("code", code + "%").setFirstResult(pageSize * pageNumber).setMaxResults(pageSize).list();
			}

			;
		});
	}

	public ServiceProcedure findServiceProcedureForRoundUp(String fullCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", fullCode);
		return (ServiceProcedure) this.repository.findUniqueUsingQuery("from ServiceProcedure serviceProcedure " + "	where serviceProcedure.definition.code = :code", params);
	}

	@SuppressWarnings("unchecked")
	public PageResult<ServiceProcedureDefinition> findAllServiceProcedureDefinitions(ListCriteria listCriteria) {
		return (PageResult<ServiceProcedureDefinition>) this.repository.findPage("from ServiceProcedureDefinition serviceProcedureDefinition", listCriteria);
	}

	public PageResult<?> findParentJobCodes(ListCriteria listCriteria) {

		PageSpecification pageSpecification = listCriteria.getPageSpecification();
		final StringBuffer queryWithoutSelect = new StringBuffer(" from ServiceProcedureDefinition spd join spd.childJobs as childJobCodes where childJobCodes is not null");
		if (listCriteria.isFilterCriteriaSpecified()) {
			queryWithoutSelect.append(" and ");
			queryWithoutSelect.append(listCriteria.getParamterizedFilterCriteria());
		}
		if (listCriteria.isSortCriteriaSpecified()) {
			queryWithoutSelect.append(" order by ");
			queryWithoutSelect.append(listCriteria.getSortCriteriaString());
		}
		final QueryParameters parameters = new QueryParameters();
		final Map<String, Object> parameterMap = listCriteria.getParameterMap();
		parameters.setNamedParameters(parameterMap);

		return findPageUsingQueryForDistinctItems(queryWithoutSelect.toString(), "", "select distinct (spd)", pageSpecification, parameters, "distinct spd");
	}

	@SuppressWarnings("unchecked")
	public List<ServiceProcedureDefinition> findExistParentJobCodes() {
		return (List<ServiceProcedureDefinition>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("select distinct(spd) from ServiceProcedureDefinition spd join spd.childJobs as childJobCodes where childJobCodes is not null").list();
			}

			;
		});
	}

	public ServiceProcedureDefinition findServiceProcedureDefinitionById(Long id) {
		return (ServiceProcedureDefinition) this.repository.findById(ServiceProcedureDefinition.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<ServiceProcedureDefinition> findServiceProcedureDefinitionsByIds(Collection<Long> ids) {
		return (List<ServiceProcedureDefinition>) this.repository.findByIds(ServiceProcedureDefinition.class, ids);
	}

	public void updateServiceProcedureDefinition(ServiceProcedureDefinition definition) {
		this.repository.update(definition);
	}

	@SuppressWarnings("unchecked")
	public PageResult<FailureTypeDefinition> fetchFailureTypesStartingWith(final String nameStartsWith, final PageSpecification page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", nameStartsWith + "%");
		String queryString = "from FailureTypeDefinition failureTypeDefinition where failureTypeDefinition.name like :name";
		return (PageResult<FailureTypeDefinition>) this.repository.findPageUsingQuery(queryString, null, page, params);
	}

	public Object saveAndReturnObject(Object obj) {
		this.repository.save(obj);
		return obj;
	}

	public Object updateAndReturnObject(Object obj) {
		this.repository.update(obj);
		return obj;
	}

	public FailureRootCauseDefinition findFailureRootCauseDefinitionByName(String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		String queryString = "from FailureRootCauseDefinition failureRootCauseDefinition where " + " failureRootCauseDefinition.name =:name";
		return (FailureRootCauseDefinition) this.repository.findUniqueUsingQuery(queryString, params);
	}

	public FailureTypeDefinition findFailureTypeDefinitionByName(String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		String queryString = "from FailureTypeDefinition failureTypeDefinition where failureTypeDefinition.name =:name";
		return (FailureTypeDefinition) this.repository.findUniqueUsingQuery(queryString, params);
	}

	public Object findObjectByPrimaryKey(Class clazz, Serializable id) {
		return this.repository.findById(clazz, id);
	}

	@SuppressWarnings("unchecked")
	public List<FailureCause> findFailureCausesForFailureType(FailureType failureType) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("failureType", failureType);
		String queryString = "from FailureCause failureCause where failureCause.failureType =:failureType";
		return (List<FailureCause>) this.repository.findUsingQuery(queryString, params);
	}

	@SuppressWarnings("unchecked")
	public List<FailureType> findFailureTypesForItemGroup(ItemGroup itemGroup) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("itemGroup", itemGroup);
		String queryString = "from FailureType failureType where failureType.forItemGroup =:itemGroup";
		return (List<FailureType>) this.repository.findUsingQuery(queryString, params);
	}

	public FailureType findFailureTypeForFaultCode(final FaultCode faultCode) {
		return (FailureType) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("from FailureType ft where ft.d.active=1 and ft.forFaultCode = :forFaultCode").setParameter("forFaultCode", faultCode).uniqueResult();
			}
		});
	}

	public void deleteObject(Object obj) {
		this.repository.delete(obj);
	}

	@SuppressWarnings("unchecked")
	public List<FailureStructure> findFailureStructuresForItemGroups(final Collection<ItemGroup> itemGroups) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("findFailureStructuresForItemGroups").setParameterList("itemGroups", itemGroups).list();
			}

		});
	}

	@SuppressWarnings("unchecked")
	public boolean findIfFaiureStructureExistsForAllItems(final Collection<Item> items) {
		Long count = (Long) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("findIfFailureStructuerExistsForAllItems").setParameterList("items", items).uniqueResult();
			}

		});
		return (count == items.size());
	}

	@SuppressWarnings("unchecked")
	public List<FailureStructure> findFailureStructuresForItems(final Collection<Item> items) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("findFailureStructuresForItems").setParameterList("items", items).list();
			}

		});
	}

	@SuppressWarnings("unchecked")
	public List<FaultCodeDefinition> findAllFaultCodeDefinitionsForLabel(Label label) {
		final String queryString = "select fc from FaultCodeDefinition fc join fc.labels label where label=:label";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("label", label);
		return (List<FaultCodeDefinition>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug("findUsingQuery(" + queryString + "," + params + ")");
				}

				Query query = session.createQuery(queryString);
				query.setProperties(params);
				return query.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<ServiceProcedureDefinition> findAllJobCodeForLabel(Label label) {
		final String queryString = "select jc from ServiceProcedureDefinition jc join jc.labels label where label=:label";
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("label", label);
		return (List<ServiceProcedureDefinition>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				if (logger.isDebugEnabled()) {
					logger.debug("findUsingQuery(" + queryString + "," + params + ")");
				}

				Query query = session.createQuery(queryString);
				query.setProperties(params);
				return query.list();
			}
		});
	}

	public FailureRootCauseDefinition findRootCauseById(final Long rootCauseId) {
		return (FailureRootCauseDefinition) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("select failureRootCauseDef from FailureRootCauseDefinition failureRootCauseDef where failureRootCauseDef.id = :rootCauseId").setParameter("causedById", rootCauseId).uniqueResult();
			}
		});
	}

	public FailureTypeDefinition findFaultFoundById(final Long faultFoundId) {
		return (FailureTypeDefinition) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("select failureCauseDef from FailureTypeDefinition failureCauseDef where failureCauseDef.id = :faultFoundId").setParameter("faultFoundId", faultFoundId).uniqueResult();
			}
		});
	}

	public FailureTypeDefinition findFaultFoundByName(final String faultFoundName) {
		return (FailureTypeDefinition) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("select faultFound from FailureTypeDefinition faultFound where faultFound.name = :faultFoundName").setParameter("faultFoundName", faultFoundName).uniqueResult();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public PageResult<FailureRootCauseDefinition> fetchFailureRootCausesStartingWith(String startingWith, PageSpecification page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", startingWith + "%");
		String queryString = "from FailureRootCauseDefinition failureRootCauseDefinition where failureRootCauseDefinition.name like :name";
		return (PageResult<FailureRootCauseDefinition>) this.repository.findPageUsingQuery(queryString, null, page, params);
	}

	@SuppressWarnings("unchecked")
	public List<FailureRootCause> findFailureRootCausesForFailureType(FailureType failureType) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("failureType", failureType);
		String queryString = "from FailureRootCause failureRootCause where failureRootCause.failureType =:failureType";
		return (List<FailureRootCause>) this.repository.findUsingQuery(queryString, params);
	}

	@SuppressWarnings("unchecked")
	public List<FailureRootCauseDefinition> findRootCauseOptionsByModel(final String modelNumber, final String faultFound) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("faultRootCauseByOptionForModel").setParameter("modelNumber", modelNumber).setParameter("faultFound", faultFound).list();

			}
		});
	}

	public FailureRootCauseDefinition findRootCauseOptionsByModelAndFailureDetail(final String number, final String faultFound, final String failureDetail) {
		return (FailureRootCauseDefinition) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("findRootCauseOptionsByModelAndFailureDetail").setParameter("modelNumber", number).setParameter("faultFound", faultFound).setParameter("failureDetail", failureDetail).uniqueResult();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<FailureCauseDefinition> findCausedByOptions(final String serialNumber, final String faultFound) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("faultCausedByOptions").setParameter("serialNumber", serialNumber).setParameter("faultFound", faultFound).setMaxResults(100).list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<FailureCauseDefinition> findCausedByOptionsById(final String inventoryItemId, final String faultFoundId) {

		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<FailureCauseDefinition> causes = new ArrayList<FailureCauseDefinition>();
				causes = session.getNamedQuery("faultCausedByOptionsById").setParameter("inventoryItemId", inventoryItemId).setParameter("faultFoundId", faultFoundId).setMaxResults(100).list();

				if (causes.isEmpty()) {
					causes = session.getNamedQuery("faultCausedByOptionByIdForProduct").setParameter("inventoryItemId", inventoryItemId).setParameter("faultFoundId", faultFoundId).setMaxResults(100).list();
					if (causes.isEmpty()) {
						causes = session.getNamedQuery("faultCausedByOptionsByIdForProduct").setParameter("inventoryItemId", inventoryItemId).setParameter("faultFoundId", faultFoundId).setMaxResults(100).list();
					}
				}
				return causes;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<FailureCauseDefinition> findCausedByOptionsForModel(final String modelNumber, final String faultFound, final String partialNameOrCode) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("faultCausedByOptionsForModel").setParameter("modelNumber", modelNumber).setParameter("faultFound", faultFound).setParameter("partialNameOrCode", partialNameOrCode + "%").setMaxResults(100).list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<FailureCauseDefinition> findCausedByOptionsForModel(final String modelNumber, final String faultFound) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("faultCausedByOptionForModel").setParameter("modelNumber", modelNumber).setParameter("faultFound", faultFound).setMaxResults(100).list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<FailureCauseDefinition> findCausedByOptionsForModelById(final String modelNumber, final String faultFoundId) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<FailureCauseDefinition> causes = new ArrayList<FailureCauseDefinition>();
				causes = session.getNamedQuery("faultCausedByOptionForModelById").setParameter("modelNumber", modelNumber).setParameter("faultFoundId", faultFoundId).setMaxResults(100).list();
				if (causes.isEmpty()) {
					causes = session.getNamedQuery("faultCausedByOptionForProductById").setParameter("modelNumber", modelNumber).setParameter("faultFoundId", faultFoundId).setMaxResults(100).list();
					if (causes.isEmpty()) {
						causes = session.getNamedQuery("faultCausedByOptionsForProductById").setParameter("modelNumber", modelNumber).setParameter("faultFoundId", faultFoundId).setMaxResults(100).list();
					}
				}
				return causes;
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public FailureCauseDefinition findCausedByOptionsForModelByIdAndCausedByName(final String modelNumber, final String faultFoundId, final String causedByName) {
		return (FailureCauseDefinition) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				List<FailureCauseDefinition> causes = new ArrayList<FailureCauseDefinition>();
				causes = session.getNamedQuery("faultCausedByOptionForModelByIdAndCausedByName").setParameter("modelNumber", modelNumber).setParameter("faultFoundId", faultFoundId).setParameter("causedByName", causedByName).setMaxResults(100).list();
				if (causes.isEmpty()) {
					causes = session.getNamedQuery("faultCausedByOptionForProductByIdAndCausedByName").setParameter("modelNumber", modelNumber).setParameter("faultFoundId", faultFoundId).setParameter("causedByName", causedByName).setMaxResults(100).list();
					if (causes.isEmpty()) {
						causes = session.getNamedQuery("faultCausedByOptionsForProductByIdAndCausedByName").setParameter("modelNumber", modelNumber).setParameter("faultFoundId", faultFoundId).setParameter("causedByName", causedByName).setMaxResults(100).list();
					}
				}
				return causes.isEmpty() ? null : causes.get(0);
			}
		});
	}

	// BUG DTH-19
	@SuppressWarnings("unchecked")
	public PageResult<FailureCauseDefinition> fetchFailureCausesStartingWith(final String nameStartsWith, final PageSpecification page) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", nameStartsWith + "%");
		String queryString = "from FailureCauseDefinition failureCauseDefinition where failureCauseDefinition.name like :name";
		return (PageResult<FailureCauseDefinition>) this.repository.findPageUsingQuery(queryString, null, page, params);
	}

	public FailureCauseDefinition findCausedById(final Long causedById) {
		return (FailureCauseDefinition) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.createQuery("select failureCauseDef from FailureCauseDefinition failureCauseDef where failureCauseDef.id = :causedById").setParameter("causedById", causedById).uniqueResult();
			}
		});
	}

	public FailureCauseDefinition findFailureCauseDefinitionByName(String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		String queryString = "from FailureCauseDefinition failureCauseDefinition where failureCauseDefinition.name =:name";
		return (FailureCauseDefinition) this.repository.findUniqueUsingQuery(queryString, params);
	}

	public void createAssemblyDefinition(AssemblyDefinition assemblyDefinition) {
		getHibernateTemplate().save(assemblyDefinition);
	}

	public void updateAssemblyDefinition(AssemblyDefinition assemblyDefinition) {
		getHibernateTemplate().update(assemblyDefinition);
	}

	public FailureTypeDefinition findFailureTypeDefinition(final String code, final String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("code", code);
		String queryString = "from FailureTypeDefinition failureTypeDefinition where failureTypeDefinition.name =:name and failureTypeDefinition.code =:code";
		return (FailureTypeDefinition) this.repository.findUniqueUsingQuery(queryString, params);

	}

	public FailureTypeDefinition findFailureTypeDefinitionByCode(final String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		String queryString = "from FailureTypeDefinition failureTypeDefinition where failureTypeDefinition.code =:code";
		return (FailureTypeDefinition) this.repository.findUniqueUsingQuery(queryString, params);

	}

	public FailureCauseDefinition findFailureCauseDefinitionByCode(final String code) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		String queryString = "from FailureCauseDefinition failureCauseDefinition where failureCauseDefinition.code =:code";
		return (FailureCauseDefinition) this.repository.findUniqueUsingQuery(queryString, params);

	}

	public FailureCauseDefinition findFailureCauseDefinitionById(Long id) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		String queryString = "from FailureCauseDefinition failureCauseDefinition where failureCauseDefinition.id =:id";
		return (FailureCauseDefinition) this.repository.findUniqueUsingQuery(queryString, params);
	}

	public void createFailureTypeDefinition(FailureTypeDefinition failureTypeDefinition) {
		getHibernateTemplate().save(failureTypeDefinition);
	}

	public void updateFailureTypeDefinition(FailureTypeDefinition failureTypeDefinition) {
		getHibernateTemplate().update(failureTypeDefinition);
	}

	public FailureCauseDefinition findFailureCauseDefinition(final String code, final String name) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		params.put("code", code);
		String queryString = "from FailureCauseDefinition failureCauseDefinition where failureCauseDefinition.name =:name and failureCauseDefinition.code =:code";
		return (FailureCauseDefinition) this.repository.findUniqueUsingQuery(queryString, params);

	}

	public void createFailureCauseDefinition(FailureCauseDefinition failureCauseDefinition) {
		getHibernateTemplate().save(failureCauseDefinition);
	}

	public void updateFailureCauseDefinition(FailureCauseDefinition failureCauseDefinition) {
		getHibernateTemplate().update(failureCauseDefinition);
	}

	public ActionDefinition findActionDefinition(final String code, final String name) {
		return (ActionDefinition) getHibernateTemplate().execute(new HibernateCallback() {
			@SuppressWarnings("unchecked")
			public Object doInHibernate(Session session) throws HibernateException, SQLException {

				return session.getNamedQuery("findActionDefinitionByName").setParameter("code", code).setParameter("name", name).uniqueResult();
			}
		});
	}

	public void createActionDefinition(ActionDefinition actionDefinition) {
		getHibernateTemplate().save(actionDefinition);
	}

	public void updateActionDefinition(ActionDefinition actionDefinition) {
		getHibernateTemplate().update(actionDefinition);
	}

	public List<FailureTypeDefinition> findFaultFoundOptionsAtProduct(final String inventoryItemId) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("faultFoundOptionsForProduct").setParameter("invItemId", inventoryItemId).list();
			}
		});
	}

	public FailureTypeDefinition findFaultFoundOptionsAtProduct(
			final String inventoryItemId, final String faultFoundName) {
		return (FailureTypeDefinition) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.getNamedQuery("faultFoundOptionsForProductWithFaultFoundName")
								.setParameter("invItemId", inventoryItemId)
								.setParameter("name", faultFoundName)
								.uniqueResult();
					}
				});
	}

	public Assembly findAssemblyById(Long id) {
		return (Assembly) getHibernateTemplate().get(Assembly.class, id);
	}

	public List<FaultCode> getAnyFaultCodeRefForGivenFaultCode(
			final String faultLocation,final int pageNumber,final int pageSize) {
		return (List<FaultCode>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select faultCode from FaultCode faultCode where faultCode.definition = (select definition.id from FaultCodeDefinition definition where definition.code=:falutCode)")
								.setParameter("falutCode", faultLocation)
								.setFirstResult(pageSize * pageNumber)
								.setMaxResults(pageSize).list();
					}
				});
	}

	@SuppressWarnings({ "rawtypes" })
	public ServiceProcedureDefinition findJobCodeForModelByIdOrProductById(final ItemGroup modelId, final ItemGroup productId, final String jobCode) {
		return (ServiceProcedureDefinition)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ServiceProcedureDefinition spd = null;
				spd = (ServiceProcedureDefinition) session.getNamedQuery("jobCodeForModelByIdOrProductById").setParameter("modelIdOrProductId", modelId.getId()).setParameter("jobCode", jobCode).uniqueResult();
				if (spd==null){
					spd = (ServiceProcedureDefinition) session.getNamedQuery("jobCodeForModelByIdOrProductById").setParameter("modelIdOrProductId", productId.getId()).setParameter("jobCode", jobCode).uniqueResult();
				}
				return spd;
			}
		});
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public FaultCodeDefinition findFaultCodeForModelByIdOrProductById(final ItemGroup modelId, final ItemGroup productId, final String faultCode) {
		return (FaultCodeDefinition)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				FaultCodeDefinition fcd = null;
				fcd = (FaultCodeDefinition) session.getNamedQuery("faultCodeForModelByIdOrProductById").setParameter("modelIdOrProductId", modelId.getId()).setParameter("faultCode", faultCode).uniqueResult();
				if (fcd==null){
					fcd = (FaultCodeDefinition) session.getNamedQuery("faultCodeForModelByIdOrProductById").setParameter("modelIdOrProductId", productId.getId()).setParameter("faultCode", faultCode).uniqueResult();
				}
				return fcd;
			}
		});
	}
	
	@SuppressWarnings({ "rawtypes" })
	public FailureType findFaultFoundForModelByIdOrProductById(final ItemGroup modelId, final ItemGroup productId, final String faultFound) {
		return (FailureType)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				FailureType ft = null;
				ft = (FailureType) session.getNamedQuery("faultFoundForModelByIdOrProductById").setParameter("modelIdOrProductId", modelId.getId()).setParameter("faultFound", faultFound).uniqueResult();
				if (ft==null){
					ft = (FailureType) session.getNamedQuery("faultFoundForModelByIdOrProductById").setParameter("modelIdOrProductId", productId.getId()).setParameter("faultFound", faultFound).uniqueResult();
				}
				return ft;
			}
		});
	}
	
	
	@SuppressWarnings("unchecked")
	public List<FailureTypeDefinition> findAllFaultFoundCodes() {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("findAllFaultFoundCodes").list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<FailureCauseDefinition> findCuasedByUsingFaultFound(final Long faultFoundId){
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("findAllCausedByCodesByUsingFaultFound").setParameter("faultFoundId", faultFoundId).list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public ServiceProcedure getServiceProcedureForModelByIdOrProductById(final ItemGroup modelId, final ItemGroup productId, final String jobCode) {
		return (ServiceProcedure)getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				ServiceProcedure spd = null;
				spd = (ServiceProcedure) session.getNamedQuery("getServiceProcedureForModelByIdOrProductById").setParameter("modelIdOrProductId", modelId.getId()).setParameter("jobCode", jobCode).uniqueResult();
				if (spd==null){
					spd = (ServiceProcedure) session.getNamedQuery("getServiceProcedureForModelByIdOrProductById").setParameter("modelIdOrProductId", productId.getId()).setParameter("jobCode", jobCode).uniqueResult();
				}
				return spd;
			}
		});
	}
	
}
