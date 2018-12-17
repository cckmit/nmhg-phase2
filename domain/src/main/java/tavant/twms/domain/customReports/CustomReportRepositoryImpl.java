package tavant.twms.domain.customReports;

import tavant.twms.infra.CriteriaHelper;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.ListCriteria;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupRepository;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.common.ReportType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.query.PartReturnClaimSummary;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.PartsClaim;

import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.sql.SQLException;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.Type;


/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Dec 24, 2008
 * Time: 2:50:24 PM
 * To change this template use File | Settings | File Templates.
 */

public class CustomReportRepositoryImpl extends GenericRepositoryImpl<CustomReport, Long> implements
        CustomReportRepository{
	
	private CriteriaHelper criteriaHelper;
	
	private ItemGroupService itemGroupService;

    public ReportSection createReportSection(ReportSection section){
        getHibernateTemplate().save(section);
        return section;
    }

    public ReportSection updateReportSection(ReportSection section){
        getHibernateTemplate().saveOrUpdate(section);
        return section;
    }

     public ReportFormQuestion createReportFormQuestion(ReportFormQuestion formQuestion){
        getHibernateTemplate().save(formQuestion);
        return formQuestion;
    }

    public ReportFormQuestion updateReportFormQuestion(ReportFormQuestion formQuestion){
        getHibernateTemplate().saveOrUpdate(formQuestion);
        return formQuestion;
    }
    
    

    public ItemGroupService getItemGroupService() {
		return itemGroupService;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	public PageResult<CustomReport> findReports(ListCriteria criteria){
       String baseQuery = "from CustomReport report ";
       return findPage(baseQuery,criteria);
    }
    
    @SuppressWarnings("unchecked")
    public List<CustomReport> findPublishedReportsForProducts(final List<Long> itemGroupIds,
    		final List<String> inventoryTypeNames
    		,final Boolean published, final ReportType reportType){
    	return (List<CustomReport>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createCriteria(CustomReport.class,"c").
											createAlias("forItemGroups", "itemGrps").
											createAlias("forInventoryTypes", "invTypes").
											add(Restrictions.in("itemGrps.id", itemGroupIds)).
											add(Restrictions.in("invTypes.type", inventoryTypeNames)).
											add(Restrictions.eq("c.published", Boolean.TRUE)).
											add(Restrictions.eq("c.reportType.id",reportType.getId())).
											list();																	
					}
					
				});

    }
    
    @SuppressWarnings("unchecked")
    public List<CustomReport> findConflictingReports(final List<String> inventoryTypeNames
    		,final Boolean published, final ReportType reportType){
    	return (List<CustomReport>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createCriteria(CustomReport.class,"c").
											createAlias("forInventoryTypes", "invTypes").
											add(Restrictions.in("invTypes.type", inventoryTypeNames)).
											add(Restrictions.eq("c.published", Boolean.TRUE)).
											add(Restrictions.eq("c.reportType.id",reportType.getId())).
											setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY).
											list();																	
					}
					
				});

    }
    
    
    @SuppressWarnings("unchecked")
	public List<CustomReport> findConflictingReportsForStandAlonePartsClaim(final Boolean published, final ReportType reportType) {
        return (List<CustomReport>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session
                                .createQuery(
                                        "select distinct customReport from CustomReport customReport  "
                                                + "where customReport.published = :published "
                                                + "and customReport.reportType.id = :reportTypeId "
                                                + "and customReport.forItemGroups.size = 0 ")
                                .setBoolean("published",Boolean.TRUE)
                                .setParameter("reportTypeId",reportType.getId())
                                .list();
                    }
                });
    }


    public CustomReportAnswer createCustomReportAmswer(CustomReportAnswer reportAnswer){
        getHibernateTemplate().saveOrUpdate(reportAnswer);
        return reportAnswer;
    }
    
    public CustomReportAnswer updateCustomReportAnswer(CustomReportAnswer reportAnswer){
        getHibernateTemplate().saveOrUpdate(reportAnswer);
        return reportAnswer;
    }
    
    @SuppressWarnings("unchecked")
	public List<CustomReport> findReportsForInventory(final InventoryItem inventoryItem) {
        return (List<CustomReport>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session
                                .createQuery(
                                        "select customReport from CustomReport customReport "
                                                + "where customReport.businessUnitInfo = :businessUnitInfo "
                                                + "and (:productType in elements (customReport.forItemGroups) " 
                                                + " or :model in elements (customReport.forItemGroups))"
                                                + "and :inventoryType in elements (customReport.forInventoryTypes) "
                                                + "and customReport.published = :published "
                                                + "order by customReport.name")
                                .setParameter("businessUnitInfo", inventoryItem.getBusinessUnitInfo())
                                .setParameter("productType",inventoryItem.getOfType().getProduct())
                                .setParameter("model",inventoryItem.getOfType().getModel())
                                .setParameter("inventoryType", inventoryItem.getType())
                                .setBoolean("published", Boolean.TRUE).list();
                    }
                });
    }

	public CriteriaHelper getCriteriaHelper() {
		return criteriaHelper;
	}

	public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
		this.criteriaHelper = criteriaHelper;
	}

	@SuppressWarnings("unchecked")
	public List<CustomReport> findReportsForParts(final Collection<Item> items, final Claim claim) {
        return (List<CustomReport>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        ItemGroup model = null;
                        InventoryItem inventoryItem = null;
                        PartsClaim partsClaim = null;
                        StringBuffer query = new StringBuffer(
                                  "select distinct customReport from CustomReport customReport join  "
                                + " customReport.applicableParts as applicableParts "
                                + " left join applicableParts.itemCriterion.itemGroup as itemGroup," 
                                + " ItemGroup itemGroup1 join itemGroup1.includedItems as items1"
                                + " where customReport.published = true "
                                + " and customReport.reportType.id in (select id from ReportType where code='FAILURE REPORT') "
                                + " and items1 in (:items)"
                                + " and " +
                                " ( " +
                                " applicableParts.itemCriterion.item in (:items) " +
                                " or " +
                                " (itemGroup1.scheme in (select itemScheme from ItemScheme itemScheme join itemScheme.purposes " +
                                " as purpose where purpose.name='Failure Reports') and " +
                                " (itemGroup1.nodeInfo.lft >= itemGroup.nodeInfo.lft " +
                                " and itemGroup1.nodeInfo.rgt <= itemGroup.nodeInfo.rgt ))" +
                                " ) ");
                        if (ClaimType.PARTS.getType().equals(claim.getType().getType())) {
                            partsClaim = new HibernateCast<PartsClaim>().cast(claim);
                        }
                        if (partsClaim != null
                                && (!partsClaim.getPartInstalled() || (partsClaim.getCompetitorModelBrand()!=null && (!partsClaim.getCompetitorModelBrand().isEmpty()
        								|| !partsClaim.getCompetitorModelDescription().isEmpty()|| !partsClaim
        								.getCompetitorModelTruckSerialnumber().isEmpty())))) {
                            query.append(" and customReport.forItemGroups is empty ");
                        }else{
                            if (claim.getClaimedItems().get(0).getItemReference().getModel() != null) {
                                model = claim.getClaimedItems().get(0).getItemReference().getModel();
                            }
                            if (claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem() != null) {
                                inventoryItem = claim.getClaimedItems().get(0).getItemReference().getReferredInventoryItem();
                            }
                            if (inventoryItem != null) {
                                query.append("and ( :productType in elements (customReport.forItemGroups) or :model in elements (customReport.forItemGroups)) "
                                        + "and :inventoryType in elements (customReport.forInventoryTypes)");
                            }
                            if (model != null && inventoryItem == null) {
                                query.append("and ( :model in elements (customReport.forItemGroups) or :product in elements  (customReport.forItemGroups)) ");
                            }
				}
				query.append(" order by customReport.name");
				Query finalQuery = session.createQuery(query.toString());
				finalQuery.setParameterList("items", items);
					if (inventoryItem != null) {
						finalQuery.setParameter("productType", inventoryItem.getOfType().getProduct());
						finalQuery.setParameter("model", inventoryItem.getOfType().getModel());
						finalQuery.setParameter("inventoryType", inventoryItem.getType());
					}
					if (model != null && inventoryItem == null) {
						ItemGroup product = itemGroupService.findProductOfModel(model);
						finalQuery.setParameter("model", model);
						finalQuery.setParameter("product",product);
					}
				return finalQuery.list();
			}
		});
	}
    
	
	@SuppressWarnings("unchecked")
	public List<CustomReportResultSet> findConflictingReportsBasedOnItems(final List<Item> items,
			final List<CustomReport> conflictingCustomReports) {
		return (List<CustomReportResultSet>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("customReportFindConflictingPartsQuery").setParameterList("customReports",
						conflictingCustomReports).setParameterList("items", items)
						.setResultTransformer(Transformers.aliasToBean(CustomReportResultSet.class))
						.setFirstResult(0).setMaxResults(10)
						.list();
			}
		});
	}
	
	

	@SuppressWarnings("unchecked")
	public List<CustomReportResultSet> findConflictingReportsBasedOnItemGroups(final List<ItemGroup> itemGroups,
			final List<CustomReport> conflictingCustomReports) {
		return (List<CustomReportResultSet>) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery("customReportFindConflictingPartGroupsQuery").setParameterList("customReports",
						conflictingCustomReports).setParameterList("itemGroups", itemGroups).setFirstResult(0).setMaxResults(10)
						.setResultTransformer(Transformers.aliasToBean(CustomReportResultSet.class))
						.list();
			}
		});
	}
	
	public boolean isReportNameDuplicate(final String name, final Long id) {
		return (Boolean) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				String query = null;
				if (id != null) {
					query = "select count(*) from CustomReport customReport "
							+ "where ( customReport.name  = :name and customReport.id != :id ) ";
				} else {
					query = "select count(*) from CustomReport customReport " + "where ( customReport.name  = :name )";
				}
				Query finalQuery = session.createQuery(query.toString());
				finalQuery.setParameter("name", name);
				if (id != null) {
					finalQuery.setParameter("id", id);
				}
				Long noOfrows = (Long) finalQuery.uniqueResult();
				return (noOfrows > 0);
			}
		});
	}

}
    

