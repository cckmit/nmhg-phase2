/**
 * 
 */
package  tavant.twms.domain.alarmcode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author surajdeo.prasad
 * 
 */

@SuppressWarnings("unchecked")
public class AlarmCodeRepositoryImpl extends
		GenericRepositoryImpl<AlarmCode, Long> implements AlarmCodeRepository {

	public List<AlarmCode> findAllAlarmCodeOfProductWithNameLike(
			final String partialAlarmCode, final ItemGroup itemGroup,
			final int pageNumber, final int pageSize) {
		 List<AlarmCode> appAlarmCodes= new ArrayList<AlarmCode>();
         for(AlarmCode alarmCode:getAllAlarmCodes(partialAlarmCode,pageNumber, pageSize)){
        	if(alarmCode.getApplicableProducts()==null || alarmCode.getApplicableProducts().isEmpty()){
        	appAlarmCodes.add(alarmCode);	
        	}
         }
		 if (isNotNull(itemGroup) && isNotNull(itemGroup.getId())) {
			 appAlarmCodes.addAll
			 (getAlarmCodeFromProductId(partialAlarmCode, itemGroup.getId(), pageNumber, pageSize));
			 
		} else {
			appAlarmCodes = getAllAlarmCodes(partialAlarmCode,
							pageNumber, pageSize);
		}
		Collections.sort(appAlarmCodes);
		return appAlarmCodes;
	}
	
	public List<AlarmCode> findAlarmCodesOfProductByCodes(
			final List<String> alarmCodeList, final ItemGroup itemGroup) {
		List<AlarmCode> alarmCodes = null;
		if (isNotNull(itemGroup)) {
			alarmCodes = getAlarmCodesFromProductId(alarmCodeList, itemGroup.getId());
		} else {
			alarmCodes = getAlarmCodesByCodes(alarmCodeList);
		}
		return alarmCodes;
	}

	private List<AlarmCode> getAlarmCodeFromProductId(
			final String partialAlarmCode, final Long productId,
			final int pageNumber,
			final int pageSize) {
		return (List<AlarmCode>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select alarmCode from AlarmCode alarmCode "
												+ "join alarmCode.applicableProducts itemGroup"
												+ " where upper(alarmCode.code) like :name"
												+ " and itemGroup.id = :id order by alarmCode.code")
								.setParameter("name", partialAlarmCode.toUpperCase() + "%")
								.setParameter("id", productId)
								.setFirstResult(pageNumber * pageSize)
								.setMaxResults(pageSize).list();
					}
				});
	}
	
	public List<AlarmCode> getACListFromAlarmCodes(final List<String> alramCodeList) {
		return (List<AlarmCode>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
						.createQuery(
								"select alarmCode from AlarmCode alarmCode "
										+ " where alarmCode.code in (:alarmCodeList)").setParameterList("alarmCodeList", alramCodeList).list();
					}
				});
	}
	
	private List<AlarmCode> getAlarmCodesFromProductId (
			final List<String> alramCodeList, final Long productId) {
		return (List<AlarmCode>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
						.createQuery(
								"select alarmCode from AlarmCode alarmCode "
										+ "join alarmCode.applicableProducts itemGroup"
										+ " where upper(alarmCode.code) in (:alarmCodeList)"
										+ " and itemGroup.id = :id").setParameterList("alarmCodeList", alramCodeList)
								.setParameter("id", productId).list();
					}
				});
	}
	
	private List<AlarmCode> getAllAlarmCodes(
			final String partialAlarmCode, final int pageNumber,
			final int pageSize) {
		return (List<AlarmCode>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"select alarmCode from AlarmCode alarmCode "
												+ " where upper(alarmCode.code) like :name order by alarmCode.code")
								.setParameter("name", partialAlarmCode.toUpperCase() + "%")
								.setFirstResult(pageNumber * pageSize)
								.setMaxResults(pageSize).list();
					}
				});
	}

	private boolean isNotNull(Object object) {
		return object == null ? false : true;
	}

	public AlarmCode alarmCodeByCode(final String code) {
		List<AlarmCode> codeList = (List<AlarmCode>) getHibernateTemplate()
				.execute(new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createCriteria(AlarmCode.class).add(
								Restrictions.eq("code", code)).list();
					}
				});
		return codeList == null || codeList.size() == 0 ? null
				: codeList.get(0);
	}
	
	private List<AlarmCode> getAlarmCodesByCodes(final List<String> alramCodeList) {
		return (List<AlarmCode>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery("select alarmCode from AlarmCode alarmCode "
										+ " where upper(alarmCode.code) in (:alarmCodeList)")
								.setParameterList("alarmCodeList", alramCodeList).list();
					}
				});
	}
	
	
	   public List<AlarmCode> findFaultcodes( final String partialAlarmCode) {
	        return (List<AlarmCode>) getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session)
	                            throws HibernateException, SQLException {
	                        return session
	                                .createQuery(
	                                        "select alarmCode from AlarmCode alarmCode "
	                                                + " where upper(alarmCode.code) like :name order by alarmCode.code")
	                                .setParameter("name", partialAlarmCode.toUpperCase() + "%").list() ;
	                    }
	                });
	    }
	   
	    public List<ItemGroup> ListAllProductCodesMatchingName(final String partialProductName){
	        return ListAllProductsOrModelsMatchingName(partialProductName,"PRODUCT");
	    }
	    
	    
	   @SuppressWarnings("unchecked")
	    public List<ItemGroup> ListAllProductsOrModelsMatchingName(final String partialProductName,
	            final String itemGroupType){
	        return (List<ItemGroup>) getHibernateTemplate().execute(
	                new HibernateCallback() {
	                    public Object doInHibernate(Session session)
	                            throws HibernateException, SQLException {       
	                                return session.createQuery(
	                                        "select itemGroup from ItemGroup itemGroup join itemGroup.scheme.purposes purpose " +
	                                        "where upper(itemGroup.name) like :name " +
	                                        "and itemGroup.itemGroupType =:itemGroupType "+
	                                        "and purpose.name = 'PRODUCT STRUCTURE' ")
	                                .setParameter("name", partialProductName + "%")
	                                .setParameter("itemGroupType",itemGroupType)
	                                .list();
	                    }
	                });
	    
	        
	    }
	   
	   public List<AlarmCode> getFaultCodeListUsingProductIdAndCode(final String code,
				final Long productId){
		   return (List<AlarmCode>) getHibernateTemplate().execute(
					new HibernateCallback() {
						public Object doInHibernate(Session session)
								throws HibernateException, SQLException {
							return session
									.createQuery(
											"select alarmCode from AlarmCode alarmCode "
													+ "join alarmCode.applicableProducts itemGroup"
													+ " where upper(alarmCode.code) like :name"
													+ " and itemGroup.id = :id order by alarmCode.code")
									.setParameter("name", code.toUpperCase() + "%")
									.setParameter("id", productId)
									.list();
						}
					});
	   }

}
