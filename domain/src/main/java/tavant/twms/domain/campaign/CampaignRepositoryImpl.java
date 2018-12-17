/*
 *   Copyright (c)2007 Tavant Technologies
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
package tavant.twms.domain.campaign;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.annotations.common.DisableDeActivation;
import tavant.twms.domain.campaign.relateCampaigns.RelateCampaign;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

import com.domainlanguage.timeutil.Clock;

/**
 * @author Kiran.Kollipara
 * 
 */
public class CampaignRepositoryImpl extends
		GenericRepositoryImpl<Campaign, Long> implements CampaignRepository {	
	 
	@SuppressWarnings("unchecked")
	public List<CampaignClass> getAllClasses() {
		return getHibernateTemplate().loadAll(CampaignClass.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<FieldModificationInventoryStatus> getFieldModificationInventoryStatus() {
		return getHibernateTemplate().loadAll(FieldModificationInventoryStatus.class);
	}

	public Campaign findByCode(final String code) {
		return (Campaign) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria queryCriteria = session
								.createCriteria(Campaign.class);
						queryCriteria.add(Restrictions.eq("code", code));
						return queryCriteria.uniqueResult();
					}
				});
	}

	@SuppressWarnings("unchecked")
	public Set<Campaign> getCampaignsForInventoryItem(
			final InventoryItem inventoryItem) {

		String query = "select notification.campaign from CampaignNotification notification where notification.item = ?";
		List<Campaign> campaigns = getHibernateTemplate().find(query,
				inventoryItem);
		Set<Campaign> campaignSet = new LinkedHashSet<Campaign>();
		campaignSet.addAll(campaigns);
		return campaignSet;
	}
	
	@SuppressWarnings("unchecked")
	public List<Campaign> getAllCampaignsForInventoryItem(
			final InventoryItem inventoryItem) {

		final String query = "select distinct(c) from CampaignNotification notification, Campaign c where notification.campaign = c.id and notification.item = :inventoryItem";

		return (List<Campaign>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						session.disableFilter("excludeInactive");
						
						return session.createQuery(query).setParameter("inventoryItem", inventoryItem).list();
					}
				});
	}

	public void saveNotifications(List<CampaignNotification> notifications) {
		getHibernateTemplate().saveOrUpdateAll(notifications);

	}
	
	@SuppressWarnings("unchecked")
	public List<InventoryItem> findInvItemsStartWithForCampaignClaims(final String partialSerialNumber, final String dealerNumber){
		return (List<InventoryItem>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("select distinct(item) from InventoryItem item, CampaignNotification notification, ServiceProvider dealer "
										+" where notification.item = item "
										+" and notification.dealership = dealer "
										+" and ((dealer.serviceProviderNumber = :dealerNumber and item.type = 'STOCK') or item.type = 'RETAIL') "
										+" and notification.claim = null "
										+" and notification.notificationStatus='PENDING' "
										+" and notification.campaign.fromDate <= :today "
										+" and upper(item.serialNumber) like :partialSerialNumber "
										+" and item.serializedPart = 0 "
										+ "order by item.serialNumber");
						query.setParameter("partialSerialNumber",
								partialSerialNumber + "%");
						query.setParameter("today", Clock.today());
						query.setParameter("dealerNumber", dealerNumber);
						return query.setFirstResult(0).setMaxResults(10).list();
					}

        });			 
	}
	
	@SuppressWarnings("unchecked")
	public InventoryItem findInvItemForCampaignClaims(final String serialNumber, final String dealerNumber, final Campaign campaign) {
		return (InventoryItem) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("select distinct(item) from InventoryItem item, CampaignNotification notification, ServiceProvider dealer "
										+" where notification.item = item "
										+" and notification.dealership = dealer "
										+" and ((dealer.serviceProviderNumber = :dealerNumber and item.type = 'STOCK') or item.type = 'RETAIL') "
										+" and notification.claim = null "
										+" and notification.campaign= :campaign "
										+" and notification.notificationStatus='PENDING' "
										+" and notification.campaign.fromDate <= :today "
										+" and notification.campaign.tillDate >= :today "
										+" and upper(item.serialNumber) = :serialNumber ");
						query.setParameter("campaign", campaign);
						query.setParameter("serialNumber", serialNumber);
						query.setParameter("today", Clock.today());
						query.setParameter("dealerNumber", dealerNumber);
						return query.uniqueResult();
					}

        });			 
	}
	
	@SuppressWarnings("unchecked")
	public List<InventoryItem> findInvItemsStartWithForCampaignClaimsForAdmin(final String partialSerialNumber){
		return (List<InventoryItem>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("select distinct(item) from InventoryItem item, CampaignNotification notification "
										+" where notification.item = item "
										+" and notification.claim = null "
										+" and notification.notificationStatus='PENDING' "
										+" and notification.campaign.fromDate <= :today "
										+" and item.serialNumber like :partialSerialNumber "
										+" and item.serializedPart = 0 "
										+" order by item.serialNumber");
						query.setParameter("partialSerialNumber",partialSerialNumber + "%");
						query.setParameter("today", Clock.today());
						return query.setFirstResult(0).setMaxResults(10).list();
					}

        });			 
	}

	@SuppressWarnings("unchecked")
	public CampaignNotification getNotificationForItemwithCampaign(
			final InventoryItem inventoryItem,final Campaign campaign) {		
		return (CampaignNotification) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("from CampaignNotification notification where notification.item = :inventoryItem " +
										"and notification.campaign= :campaign");
						query.setParameter("inventoryItem",inventoryItem );
						query.setParameter("campaign", campaign);
						return query.uniqueResult();
					}

        });	
	}
	
	@SuppressWarnings("unchecked")
	public List<CampaignNotification> getNotificationForItemsWithCampaign(final Campaign campaign) {		
		return (List<CampaignNotification>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						        Query query = session
										.createQuery("from CampaignNotification notification where notification.campaign= :campaign");
								query.setParameter("campaign", campaign);
								return query.list();
					}

        });	
	}
	
	@SuppressWarnings("unchecked")
	public List<CampaignNotification> getNotificationForItemWithCampaigns(
			final InventoryItem inventoryItem,final List<Campaign> campaigns) {		
		return (List<CampaignNotification>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("from CampaignNotification notification where notification.item = :inventoryItem " +
										"and notification.campaign in (:campaigns)");
						query.setParameter("inventoryItem",inventoryItem );
						query.setParameterList("campaigns", campaigns);
						session.disableFilter("excludeInactive");
						return query.list();
					}

        });	
	}
	
	@SuppressWarnings("unchecked")
	public List<CampaignNotification> getNotificationForAllRelatedCampaigns(final List<Campaign> campaigns,final List<InventoryItem> items) {		
		return (List<CampaignNotification>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("from CampaignNotification notification where notification.campaign in (:campaigns) " +
										"and notification.notificationStatus='PENDING' and notification.item in (:items)");
						query.setParameterList("campaigns",campaigns );
						query.setParameterList("items",items);
						session.disableFilter("excludeInactive");
						return query.list();
					}

        });	
	}
	
	@SuppressWarnings("unchecked")
	public List<CampaignNotification> findNotificationsForCampaign(final Campaign campaign, final String status) {		
		return (List<CampaignNotification>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("from CampaignNotification notification where notification.notificationStatus= :status and " +
										"notification.campaign = :campaign");
						query.setParameter("campaign",campaign );
						query.setParameter("status",status );
						return query.list();
					}

        });	
	}
	
	@SuppressWarnings("unchecked")
	public List<InventoryItem> findCompletedNotificationsForCampaignsAndItems(final List<Campaign> campaigns,final List<InventoryItem> items) {		
		return (List<InventoryItem>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("select notification.item from CampaignNotification notification where notification.notificationStatus='COMPLETE' and " +
										"notification.campaign in (:campaigns) and notification.item in (:items)");
						query.setParameterList("campaigns",campaigns );
						query.setParameterList("items",items);
						return query.list();
					}

        });	
	}
	
	@SuppressWarnings("unchecked")
	public List<Campaign> getCampaignsForInventoryItemById(
			final String serialNumber) {
		return (List<Campaign>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("select notification.campaign from CampaignNotification notification where notification.item = " +
										" (select item from InventoryItem item where item.serialNumber =:serialNumber and item.serializedPart=0) " +
										" and notification.claim = null "+
										" and notification.notificationStatus='PENDING' "+
										" and notification.campaign.fromDate <= :today ");
						query.setParameter("serialNumber",serialNumber );
						query.setParameter("today", Clock.today());
						return query.list();
					}

        });
	}
	
	@SuppressWarnings("unchecked")
	public List<Campaign> getCampaignsForDealer(final String partialCampaignCode, final String dealerName) {
		return (List<Campaign>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("select distinct(campaign) from CampaignNotification notification ,Campaign campaign ,ServiceProvider dealer " +
										" where notification.dealership = dealer " +
										" and dealer.name =:dealerName "+
										" and notification.campaign = campaign "+
										" and notification.claim = null "+
										" and notification.notificationStatus='PENDING' "+
										" and campaign.fromDate <= :today "+
										" and campaign.tillDate >= :today "+
										" and upper(campaign.code) like :partialCampaignCode " +
										" order by campaign.code ");
						query.setParameter("dealerName",dealerName);
						query.setParameter("partialCampaignCode", partialCampaignCode+"%");	
						query.setParameter("today", Clock.today());
						return query.list();
					}

        });
	}
	
	@SuppressWarnings("unchecked")
	public List<Campaign> getCampaignsByCode(final String partialCampaignCode) {
		return (List<Campaign>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("select distinct(campaign) from CampaignNotification notification ,Campaign campaign ,ServiceProvider dealer " +
										" where notification.dealership = dealer " +
										" and notification.campaign = campaign "+
										" and notification.claim = null "+
										" and notification.notificationStatus='PENDING' "+
										" and campaign.fromDate <= :today "+
										" and campaign.tillDate >= :today "+
										" and upper(campaign.code) like :partialCampaignCode " +
										" order by campaign.code ");
						query.setParameter("partialCampaignCode", partialCampaignCode+"%");	
						query.setParameter("today", Clock.today());
						return query.list();
					}

        });
	}	
	@SuppressWarnings("unchecked")
	public void updateCampaignNotifications(final Collection<InventoryItem> items,final Claim claim,final Campaign campaign,final String campaignNotificationStatus) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.createQuery("update CampaignNotification set claim=:claim , notificationStatus=:campaignNotificationStatus, d.updatedOn=:updatedDate"
								+ " where item in (:items) "
								+ " and campaign=:campaign");
				query.setParameter("claim", claim);
				query.setParameterList("items", items);
				query.setParameter("campaign", campaign);
				query.setParameter("campaignNotificationStatus", campaignNotificationStatus);
				query.setParameter("updatedDate", Clock.today());
				return query.executeUpdate();
			}

		});
	}
	
	
	@SuppressWarnings("unchecked")
	public void deleteDraftCampaignClaim(final Claim claim) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.createQuery("update CampaignNotification set claim=null , notificationStatus='PENDING', d.updatedOn=:updatedDate"
								+ " where claim in (:claim)");
				query.setParameter("claim", claim);
				query.setParameter("updatedDate", Clock.today());
				return query.executeUpdate();
			}

		});
	}
	
	@SuppressWarnings("unchecked")
	public void updateDealershipForCampaignNotification(final InventoryItem item, final Party oldDealership, final Party newDealership) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.createQuery("update CampaignNotification set dealership = :newDealership, d.updatedOn=:updatedDate"
								+ " where item = :item "
								+ " and dealership = :oldDealership "
								+ " and notificationStatus='PENDING'");
				query.setParameter("item", item);
				query.setParameter("oldDealership", oldDealership);
				query.setParameter("newDealership", newDealership);
				query.setParameter("updatedDate", Clock.today());
				return query.executeUpdate();
			}

		});
							
	}

	@SuppressWarnings("unchecked")
	public List<Campaign> findPendingCampaigns(final InventoryItem invItem, final List<String> campaignClasses) {
		return (List<Campaign>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("  select c from Campaign c, CampaignNotification cn where c.id = cn.campaign and "
											 +" c.campaignClass.code in (:campaignClassList) and "
											 +" cn.notificationStatus='PENDING' and cn.item = :invItem and " 
											 +" c.d.active = 1 and c.tillDate >= :today"
											 +" and c.fromDate <= :today"
											 );						
						query.setParameterList("campaignClassList" , campaignClasses );
                        query.setParameter("invItem",invItem);
                        query.setParameter("today",Clock.today());
                        return query.list();
					}

        });
	}

	@SuppressWarnings("unchecked")
	public void deactivateCampaignNotificationsForACampaign(
			final Campaign campaign) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
			              	Query query = session.createQuery("UPDATE CampaignNotification SET d.active = 0, d.updatedOn=:updatedDate WHERE " +
									" campaign = :campaign and notificationStatus='PENDING' ");
				query.setParameter("campaign", campaign);
				query.setParameter("updatedDate", Clock.today());
				
                 			return query.executeUpdate();
			}
		});
	}
	
	@DisableDeActivation
	public void activateCampaignNotificationsForACampaign(
			final Campaign campaign) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.createQuery("UPDATE CampaignNotification SET d.active = 1, d.updatedOn=:updatedDate WHERE " +
								" campaign = :campaign and notificationStatus='PENDING' ");
				query.setParameter("campaign", campaign);
				query.setParameter("updatedDate", Clock.today());
				return query.executeUpdate();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public void activateAllCampaignNotifications(final List<CampaignNotification> campaignNotifications) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				
				Query query = session
						.createSQLQuery("UPDATE campaign_notification cn SET cn.d_active = 1 WHERE " +
								" cn.id in (:campaignNotifications) and cn.d_active = 0 ");
				query.setParameterList("campaignNotifications", campaignNotifications);
				
				return query.executeUpdate();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	public void deactivateAllCampaignNotifications(final List<CampaignNotification> campaignNotifications) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session
						.createSQLQuery("UPDATE campaign_notification cn SET cn.d_active = 0 WHERE " +
								" cn.id in (:campaignNotifications) and cn.d_active = 1 ");
				query.setParameterList("campaignNotifications", campaignNotifications);
				return query.executeUpdate();
			}
		});
	}

	@DisableDeActivation
	public PageResult<Campaign> findAllCampaigns(final String fromClause,
			final ListCriteria listCriteria) {
		return super.findPage(fromClause, listCriteria);
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * We need all completed notifications or pending ACTIVE notifications
	 */
	public List<CampaignNotification> findNotificationsForItem(InventoryItem item) {
		String query = "from CampaignNotification campaignNotification where "
				+ " campaignNotification.item = :item and " 
				+ " (campaignNotification.notificationStatus = 'COMPLETE' or " 
				+ " ((campaignNotification.notificationStatus='PENDING' or " 
				+ "  campaignNotification.notificationStatus='IN PROGRESS') and "
				+ " campaignNotification.campaign.fromDate <= :today  " 
				+ " and campaignNotification.campaign.tillDate >= :today "  
				+ " and campaignNotification.campaign.d.active = 1 ) )";
		return getHibernateTemplate()
				.findByNamedParam(query, new String[] {"item" ,"today"},
						new Object[] {item,Clock.today()});
		
	}

	@SuppressWarnings("unchecked")
	/**
	 * We need all completed notifications or pending ACTIVE notifications
	 */
	public List<CampaignNotification> findPendingNotificationsForItem(InventoryItem item) {
		String query = "from CampaignNotification campaignNotification where "
				+ " campaignNotification.item = :item and " 
				+ " campaignNotification.notificationStatus='PENDING' and "
				+ " campaignNotification.campaign.fromDate <= :today  " 
				+ " and campaignNotification.campaign.tillDate >= :today "  
				+ " and campaignNotification.campaign.d.active = 1 ";
		return getHibernateTemplate()
				.findByNamedParam(query, new String[] {"item" ,"today"},
						new Object[] {item,Clock.today()});
		
	}
		
	@SuppressWarnings("unchecked")
	public List<Campaign> findAllActiveCampaignsWithCodeLike(
			final String partialCampaignCode, final int pageNumber,
			final int pageSize) {
		return (List<Campaign>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {		
								return session.createQuery(
										"select campaign from Campaign campaign where upper(campaign.code) like :code and campaign.status = 'Active' ")
								.setParameter("code", partialCampaignCode.toUpperCase() + "%")
								.setFirstResult(pageNumber * pageSize)
								.setMaxResults(pageSize).list();
					}
				});
	}

	public CampaignNotification findCampaignNotification(final String inventorySerialNumber, final String dealerNumber,
			final String campaignCode) {
		
		return (CampaignNotification) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("select notification " +
										" from InventoryItem invItem, " +
										"      CampaignNotification notification," +
										"      Campaign campaign," +
										"      ServiceProvider dealer " +
										" where upper(invItem.serialNumber) = :serialNumber " +
										" and   notification.dealership = dealer.id " +
										" and   campaign.fromDate <= :today "+
										" and   campaign.tillDate >= :today "+
										" and   ((dealer.serviceProviderNumber = :dealerNumber and invItem.type = 'STOCK') or invItem.type = 'RETAIL') " +
										" and   notification.item = invItem.id " +										
										" and   campaign.id = notification.campaign and  campaign.code = :campaignCode");
						
						query.setParameter("serialNumber", inventorySerialNumber);
						query.setParameter("today", Clock.today());
						query.setParameter("dealerNumber", dealerNumber);
						query.setParameter("campaignCode", campaignCode);
						return query.uniqueResult();
					}

        });
	}
	
	public CampaignNotification findPendingCampaignNotification(
			final InventoryItem item, final Campaign campaign) {
		return (CampaignNotification) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("select notification "
										+ " from CampaignNotification notification "
										+ " where notification.item = :item "
										+ " and notification.campaign = :campaign "
										+ " and notification.notificationStatus = 'PENDING' ");
						query.setParameter("item", item);
						query.setParameter("campaign", campaign);
						return query.uniqueResult();
					}
				});
	}
	
	@SuppressWarnings("unchecked")
	public List<Campaign> findAllCampaigns() {
		return (List<Campaign>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Query query = session
								.createQuery("from Campaign campaign order by upper(campaign.code)");						
                        return query.list();
					}
        });
	}
	
	
	public List<Campaign> findAllCampaignsForLabel(Label label) {
		String query="select camp from Campaign camp join camp.labels label where label=:label";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("label", label);
		return findUsingQuery(query, params);
	}

}
