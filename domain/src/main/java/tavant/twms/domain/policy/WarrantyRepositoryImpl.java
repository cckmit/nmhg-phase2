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

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.inventory.ContractCode;
import tavant.twms.domain.inventory.IndustryCode;
import tavant.twms.domain.inventory.InternalInstallType;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryListCriteria;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionType;
import tavant.twms.domain.inventory.MaintenanceContract;
import tavant.twms.domain.inventory.TransactionType;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author radhakrishnan.j
 * 
 */
public class WarrantyRepositoryImpl extends GenericRepositoryImpl implements
		WarrantyRepository {

	public void delete(Warranty warranty) {
		getHibernateTemplate().delete(warranty);
	}

	public Warranty findById(Long id) {
		return (Warranty) getHibernateTemplate().get(Warranty.class, id);
	}

	// TODO- Please explain what is the need for this API....
	// Y cant we directly use inventoryItem.getWarranty().....
	// Am i missing something here????? - Aniruddha...
	public Warranty findBy(final InventoryItem inventoryItem) {
		return (Warranty) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from Warranty warranty where warranty.forItem.id=:inventoryItemId "
												+ "and warranty.draft = 0 and warranty.status = 'ACCEPTED' order by list_index desc")
								.setParameter("inventoryItemId",
										inventoryItem.getId())
								.setFirstResult(0).setMaxResults(1)
								.uniqueResult();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public PageResult<Warranty> findWarranties(ServiceProvider dealer,
			final PageSpecification pageSpec) {
		Long totalRowCount = (Long) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createQuery(
								"select count(*) from Warranty").uniqueResult();
					}
				});

		int totalPages = pageSpec.convertRowsToPages(totalRowCount);

		List<Warranty> rowsInPage = getHibernateTemplate().executeFind(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session.createQuery(" from Warranty ");
						Integer from = pageSpec.offSet();
						int rowsPerPage = pageSpec.getPageSize();
						return query.setFirstResult(from).setMaxResults(
								rowsPerPage).list();
					}
				});

		return new PageResult<Warranty>(rowsInPage, pageSpec, totalPages);
	}
    
    @SuppressWarnings("unchecked")
    public PageResult<Warranty> listDraftWarrantiesForDealer(final InventoryListCriteria inventoryListCriteria) {
    	return (PageResult<Warranty>) getHibernateTemplate().execute(new HibernateCallback() {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException, SQLException {

            	String baseQuery = "from Warranty warranty "
                    + "where warranty.draft=1 and  warranty.filedBy= :user " 
                    + getFilterCriteriaString(inventoryListCriteria)
                    + " exists "
                    + "(from InventoryItem inventoryItem "
                    + "where (inventoryItem.id = warranty.forItem) and inventoryItem.type = :type "
                    + " and exists "
                    + " (from InventoryTransaction inventoryTransaction "
                    + " where inventoryTransaction in elements(inventoryItem.transactionHistory) "
                    + " and (inventoryTransaction.seller = :dealer or inventoryTransaction.buyer = :dealer )))";
            	Map<String, Object> params = new HashMap<String, Object>();
                params.put("type", inventoryListCriteria.getType());
                params.put("dealer", inventoryListCriteria.getDealer());
                params.put("user", inventoryListCriteria.getUser());
                return findPageUsingQuery(baseQuery, "forItem.serialNumber asc", inventoryListCriteria.getPageSpecification(), params);
            }
    	});
    }
    
    
    @SuppressWarnings("unchecked")
    public PageResult<Warranty> listDraftWarrantiesForInternalUser(final InventoryListCriteria inventoryListCriteria) {
    	return (PageResult<Warranty>) getHibernateTemplate().execute(new HibernateCallback() {
            @SuppressWarnings("unchecked")
            public Object doInHibernate(Session session) throws HibernateException, SQLException {

            	String baseQuery = "from Warranty warranty "
                    + "where warranty.draft=1 and  warranty.filedBy= :user "
                    + getFilterCriteriaString(inventoryListCriteria)
                    + " exists "
                    + "(from InventoryItem inventoryItem "
                    + "where (inventoryItem.id = warranty.forItem) and inventoryItem.type = :type)";
            	Map<String, Object> params = new HashMap<String, Object>();
                params.put("type", inventoryListCriteria.getType());
                params.put("user", inventoryListCriteria.getUser());
                return findPageUsingQuery(baseQuery, "forItem.serialNumber asc", inventoryListCriteria.getPageSpecification(), params);                
            }
    	});
    }

	@SuppressWarnings("unchecked")
	public PageResult<Warranty> listMatchingWarrantiesForDealer(
			final WarrantyListCriteria warrantyListCriteria) {
		return (PageResult<Warranty>) getHibernateTemplate().execute(
				new HibernateCallback() {
					@SuppressWarnings("unchecked")
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {

						String baseQuery = "from Warranty warranty"
								+ getFilterCriteriaString(warrantyListCriteria);

						Long count = (Long) session.createQuery(
								"select count(*) " + baseQuery).uniqueResult();
						List<Warranty> results = new ArrayList<Warranty>();
						if (count > 0
								&& count > warrantyListCriteria
										.getPageSpecification().offSet()) {
							results = session
									.createQuery(
											baseQuery
													+ getSortCriteriaString(warrantyListCriteria))
									.setFirstResult(
											warrantyListCriteria
													.getPageSpecification()
													.offSet()).setMaxResults(
											warrantyListCriteria
													.getPageSpecification()
													.getPageSize()).list();
						}
						return new PageResult<Warranty>(results,
								warrantyListCriteria.getPageSpecification(),
								warrantyListCriteria.getPageSpecification()
										.convertRowsToPages(count));
					}
				});
	}

	private String getFilterCriteriaString(WarrantyListCriteria criteria) {
		if (criteria.getFilterCriteria().size() > 0) {
			StringBuffer dynamicQuery = new StringBuffer(" where ");
			for (String expression : criteria.getFilterCriteria().keySet()) {
				dynamicQuery.append(expression);
				dynamicQuery.append(" like ");
				dynamicQuery.append("'");
				dynamicQuery.append(criteria.getFilterCriteria()
						.get(expression));
				dynamicQuery.append("%'");
				dynamicQuery.append(" and ");
			}
			String returnQuery = dynamicQuery.toString();
			return returnQuery.substring(0, returnQuery.length() - 5); // to
																		// remove
																		// last
																		// and
		}
		return "";
	}

	private String getSortCriteriaString(WarrantyListCriteria criteria) {
		final String sortCriteriaAsString = criteria.getSortCriteriaString();
		if ("".equals(sortCriteriaAsString)) {
			return sortCriteriaAsString;
		} else {
			return " order by " + sortCriteriaAsString;
		}
	}

	public void save(Warranty warranty) {
		getHibernateTemplate().save(warranty);
	}

	public void update(Warranty warranty) {
		getHibernateTemplate().update(warranty);
	}

	@SuppressWarnings("unchecked")
	public List<TransactionType> listTransactionTypes() {

		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(" from TransactionType ").setCacheable(true);
				return query.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<MarketType> listMarketTypes() {

		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(" from MarketType marketType order by marketType.title").setCacheable(true);
				return query.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<CompetitionType> listCompetitionTypes() {

		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(" from CompetitionType competitionType order by competitionType.type").setCacheable(true);
				return query.list();
			}
		});
	}

	@SuppressWarnings("unchecked")
	public List<WarrantyType> listWarrantyTypes() {
		return getHibernateTemplate().find("from WarrantyType");
	}
	
	@SuppressWarnings("unchecked")
	public List<CompetitorMake> listCompetitorMake(){
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(" from CompetitorMake competitorMake order by competitorMake.make").setCacheable(true);
				return query.list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<CompetitorModel> listCompetitorModel(){
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(" from CompetitorModel competitorModel order by competitorModel.model").setCacheable(true);
				return query.list();
			}
		});
	}


	public Warranty findByTransactionId(final Long invTrnxId) {
		return (Warranty) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from Warranty warranty where warranty.forTransaction.id=:invTrnxId ")
								.setParameter("invTrnxId", invTrnxId)
								.uniqueResult();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public InventoryTransaction findWarrantyBySerialNumberAndDealer(
			final String serialNumber, final Party dealer,
			final InventoryTransactionType inventoryTransactionType) {
	       String query = "select invTrx from InventoryTransaction invTrx join invTrx.transactedItem as inventoryItem "
               + " where inventoryItem.serialNumber =:serialNumber and invTrx.invTransactionType.id =:invTrxTypeId";
       Map<String, Object> params = new HashMap<String, Object>(2);
       params.put("serialNumber", serialNumber);
       params.put("invTrxTypeId", inventoryTransactionType.getId());
       return (InventoryTransaction)findUniqueUsingQuery(query, params);		
	}
	
	private String getFilterCriteriaString(InventoryListCriteria criteria) {
        if (criteria.getFilterCriteria().size() > 0) {
            StringBuffer dynamicQuery = new StringBuffer("and ");
            for (String expression : criteria.getFilterCriteria().keySet()) {
                dynamicQuery.append(expression);
                dynamicQuery.append(" like ");
                dynamicQuery.append("'" + criteria.getFilterCriteria().get(expression) + "%'");
                dynamicQuery.append(" and ");
            }
            return dynamicQuery.toString();
        }
        return " and ";
    }

    @SuppressWarnings("unchecked")
	public String getWarrantyMultiDRETRNumber(){
		return getSession().createSQLQuery("select WARRANTY_MULTIDRETR_NUMBER_SEQ.nextVal from dual d").uniqueResult().toString();
	}

    @SuppressWarnings("unchecked")
    public WarrantyType findWarrantyTypeByType(final String type) {
        return (WarrantyType) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        return session
                                .createQuery(
                                        "from WarrantyType where type =:type ")
                                .setParameter("type", type)
                                .uniqueResult();
                    }
                });
    }

	public CompetitionType findCompetitionType(final String competitionType) {
		
		return (CompetitionType) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from CompetitionType where type=:competitionType")
								.setParameter("competitionType", competitionType)
								.uniqueResult();
					}
				});
	}

	public CompetitorMake findCompetitorMake(final String competitorMake) {
		
		return (CompetitorMake) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from CompetitorMake where make=:competitorMake")
								.setParameter("competitorMake", competitorMake)
								.uniqueResult();
					}
				});
	}

	public CompetitorModel findCompetitorModel(final String competitorModel) {
		
		return (CompetitorModel) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from CompetitorModel where model=:competitorModel")
								.setParameter("competitorModel", competitorModel)
								.uniqueResult();
					}
				});
	}

	public MarketType findMarketType(final String marketType) {
		
		return (MarketType) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from MarketType where title=:marketType")
								.setParameter("marketType", marketType)
								.uniqueResult();
					}
				});
	}

	public TransactionType findTransactionType(final String trxType) {		
		
		return (TransactionType) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from TransactionType where type=:trxType")
								.setParameter("trxType", trxType)
								.uniqueResult();
					}
				});
	}
	
public ContractCode findCCode(final String cCode) {		
		
		return (ContractCode) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from ContractCode where contractCode=:cCode")
								.setParameter("cCode", cCode)
								.uniqueResult();
					}
				});
	}
	

public ContractCode findContractCode(final String contractId) {

	return (ContractCode) getHibernateTemplate().execute(
	new HibernateCallback() {
	public Object doInHibernate(Session session)
	throws HibernateException, SQLException {
	return session
	.createQuery(
	"from ContractCode where id=:contractId")
	.setParameter("contractId", new Long(contractId))
	.uniqueResult();
	}
	});
	}

	public InternalInstallType findInternalInstallType(
			final Long internalInstallId) {

		return (InternalInstallType) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from InternalInstallType where id=:internalInstallId")
								.setParameter("internalInstallId",internalInstallId)
								.uniqueResult();
					}
				});
	}

	public InternalInstallType findInternalInstallTypeByName(
			final String internalInstallType) {

		return (InternalInstallType) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from InternalInstallType where internalInstallType=:internalInstallId")
								.setParameter("internalInstallId",internalInstallType)
								.uniqueResult();
					}
				});
	}
	
	
	

public IndustryCode findIndustryCode(final String industryId) {		
	
	return (IndustryCode) getHibernateTemplate().execute(
			new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					return session
							.createQuery(
									"from IndustryCode where id=:industryId")
							.setParameter("industryId", new Long(industryId))
							.uniqueResult();
				}
			});
}

public IndustryCode findIndustryCodeByIndustryCode(final String industryCode) {		
	
	return (IndustryCode) getHibernateTemplate().execute(
			new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					return session
							.createQuery(
									"from IndustryCode where industryCode=:industryId")
							.setParameter("industryId", industryCode)
							.uniqueResult();
				}
			});
}

public MaintenanceContract findMaintenanceContract(final String maintenanceId) {		
	
	return (MaintenanceContract) getHibernateTemplate().execute(
			new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					return session
							.createQuery(
									"from MaintenanceContract where id=:maintenanceId")
							.setParameter("maintenanceId", new Long(maintenanceId))
							.uniqueResult();
				}
			});
}

public MaintenanceContract findMaintenanceContractByName(final String maintenanceContract) {		
	
	return (MaintenanceContract) getHibernateTemplate().execute(
			new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
					return session
							.createQuery(
									"from MaintenanceContract where maintenanceContract=:maintenanceContract")
							.setParameter("maintenanceContract", maintenanceContract)
							.uniqueResult();
				}
			});
}

	public BigDecimal findWRCount(final String businessUnit) {
		return (BigDecimal) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createSQLQuery(
										"select count(w.id) from warranty w join inventory_item ii on w.for_item = ii.id join inventory_transaction_type itt on w.transaction_type = itt.id where w.draft = 0 and ii.business_unit_info =:businessUnit and itt.trnx_type_key = 'DR' ")
										.setParameter("businessUnit",businessUnit).uniqueResult();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<IndustryCode> listIndustryCode() {

		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(" from IndustryCode industryCode order by industryCode.industryCode").setCacheable(true);
				return query.list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<MaintenanceContract> listMaintenanceContract() {

		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(" from MaintenanceContract mContract order by mContract.maintenanceContract").setCacheable(true);
				return query.list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public List<ContractCode> listContractCode() {

		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(" from ContractCode contractCode order by contractCode.contractCode").setCacheable(true);
				return query.list();
			}
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<InternalInstallType> listInternalInstallType() {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.createQuery(
								" from InternalInstallType internalInstallType order by internalInstallType.internalInstallType")
						.setCacheable(true);
				return query.list();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public Long getIndustryCode(final String siCode){
		return (Long) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select industryCode.id from IndustryCode industryCode  where industryCode.industryCode=:sicCode")
								.setParameter("sicCode",siCode).uniqueResult();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public List<Warranty> getwarrantyesByUpdateDateTime(final Date lastupdate,
			final String buName) {
		return (List<Warranty>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String query = "select warranty from Warranty warranty ,WarrantyAudit warrantyAudit,InventoryItem invnetoryItem where" +
								" warranty.forItem=invnetoryItem.id and warrantyAudit.forWarranty=warranty.id "
								+ " and warrantyAudit.d.updatedTime > :lastupdate and warrantyAudit.status = :status and invnetoryItem.businessUnitInfo=:businessUnit " +
										"order by warrantyAudit.d.updatedTime desc ";

						Query q = session.createQuery(query).setParameter(
								"lastupdate", lastupdate).setParameter(
								"status", WarrantyStatus.SUBMITTED)
								.setParameter("businessUnit", buName);

						return q.list();
					}
				});
	}

	/*public CountyCodeMapping findCountyCode(
			final String countyName) {

		return (CountyCodeMapping) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from CountyCodeMapping ccm where lower(ccm.countyName) = :countyName ")
								.setParameter("countyName",countyName.toLowerCase())
								.uniqueResult();
					}
				});
	}
	*/
	
    public WarrantyAudit findWarrantyAuditFromWarranty(final Warranty warranty,final Date lastupdate) {       
        
        return (WarrantyAudit) getHibernateTemplate().execute(
                    new HibernateCallback() {
                          public Object doInHibernate(Session session)
                                      throws HibernateException, SQLException {
                                Query q = session
                                            .createQuery(
                                                        "from WarrantyAudit warrantyAudit where warrantyAudit.forWarranty=:forWarranty and warrantyAudit.d.updatedTime > :lastupdate and warrantyAudit.status = :status order by warrantyAudit.d.updatedTime desc")
                                            .setParameter("forWarranty", warranty)
                                            .setParameter("lastupdate", lastupdate)
                                            .setParameter("status", WarrantyStatus.SUBMITTED);
                                return q.list().isEmpty() ? null : q.list().get(0);
                          }
                    });
  }

	
	
	
}