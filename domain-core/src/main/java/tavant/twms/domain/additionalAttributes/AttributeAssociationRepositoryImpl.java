/*
 *   Copyright (c) 2008 Tavant Technologies
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
package tavant.twms.domain.additionalAttributes;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author pradipta.a
 */
public class AttributeAssociationRepositoryImpl extends
        GenericRepositoryImpl<AttributeAssociation, Long> implements AttributeAssociationRepository {

    @SuppressWarnings("unchecked")
    public List<AttributeAssociation> findAttributesForItemGroups(final List<ItemGroup> itemGroups,AttributePurpose attributePurpose) {
        return (List<AttributeAssociation>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session
                        .createQuery(
                                "from AttributeAssociation attrAssoc "
                                        + "where attrAssoc.forAttribute.d.active=1 and  attrAssoc.itemGroup in (:itemGroupParam) and attrAssoc.faultCode is null and attrAssoc.serviceProcedure is null ")
                        .setParameterList("itemGroupParam", itemGroups).list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<AttributeAssociation> findAttributesForFaultCode(final long id, final long modelId) {
        return (List<AttributeAssociation>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(
                        "from AttributeAssociation attrAssoc "
                                + "where attrAssoc.faultCode.id = :faultCodeId and attrAssoc.itemGroup.id = :ItemGroupId")
                                .setParameter("faultCodeId", id)
                                .setParameter("ItemGroupId", modelId)
                                .list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<AttributeAssociation> findAttributesForJobCode(final long id) {
        return (List<AttributeAssociation>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(
                        "from AttributeAssociation attrAssoc "
                                + "where attrAssoc.serviceProcedure.id = :serviceProcedureId  ")
                        .setParameter("serviceProcedureId", id).list();
            }
        });
    }

    @SuppressWarnings("unchecked")
    public List<AttributeAssociation> findAttributesForSupplier(final long id) {
        return (List<AttributeAssociation>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(
                        "from AttributeAssociation attrAssoc "
                                + "where attrAssoc.supplier.id = :supplierId ").setParameter(
                        "supplierId", id).list();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public List<AdditionalAttributes> findAttributesForClaim(final long id) {
        return (List<AdditionalAttributes>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	String query = "select additionalAttributes from AdditionalAttributes additionalAttributes left outer join additionalAttributes.attributeAssociations attributeAssociation " +
            			"where additionalAttributes.attributePurpose = 'CLAIM_PURPOSE' and (attributeAssociation.smrreason.id is null or attributeAssociation.smrreason.id = :smrreasonId)";
                return session.createQuery(query).setParameter("smrreasonId", id).list();
            }
        });
    }
       
    public Boolean isAnyAttributeConfiguredForSupplier() {
    	return (Boolean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String query = "select count(*) from AdditionalAttributes attr " +
								" join attr.attributeAssociations as attrAssoc where attrAssoc.supplier is not null ";
						Long numOfAttributes = (Long) session.createQuery(query).uniqueResult();
						return numOfAttributes > 0;
					}
				});
    	}

    @SuppressWarnings("unchecked")
    public List<AttributeAssociation> findAttributesForPart(final long id) {
        return (List<AttributeAssociation>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery(
                        "from AttributeAssociation attrAssoc "
                                + "where attrAssoc.item.id = :ItemId ").setParameter("ItemId", id)
                        .list();
            }
        });
    }
    
    @SuppressWarnings("unchecked")
    public Boolean isAnyAttributeConfiguredForBU() {
    	return (Boolean) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String query = "select count(*) from AdditionalAttributes additional_attributes ";
						Long numOfAttributes = (Long) session.createQuery(query).uniqueResult();
						return numOfAttributes > 0;
					}
				});
    	}
    
    @SuppressWarnings("unchecked")
    public List<AdditionalAttributes> findAttributesForEquipment(final long id) {
        return (List<AdditionalAttributes>) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
            	String query = "select additionalAttributes from AdditionalAttributes";
                return session.createQuery(query).setParameter("smrreasonId", id).list();
            }
        });
    }
   
    
}