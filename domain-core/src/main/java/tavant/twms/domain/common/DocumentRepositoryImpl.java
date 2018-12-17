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

package tavant.twms.domain.common;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author anshul.khare
 * 
 */
public class DocumentRepositoryImpl extends
		GenericRepositoryImpl<Document, Long> implements DocumentRepository {

	public List<Document> findPartsInvDocuments(Set<String> properties,
			Document document) {

		return findEntitiesThatMatchPropertyValues(properties, document);

	}

	public Blob getDocumentContentById(final Long id) {
		return (Blob) getHibernateTemplate().execute(new HibernateCallback() {
			public Blob doInHibernate(Session session)
					throws HibernateException, SQLException {
				return (Blob) session.createSQLQuery(
						"SELECT content FROM document WHERE ID =  :id")
						.setLong("id", id).list().get(0);
			}
		});
	}
	public List<Document> findByDocumentType(String documentType) {
		String query = "from Document dm where dm.type = :documentType order by dm.id desc";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("documentType", documentType);
		return findUsingQuery(query, params);
	}

	public Blob findContentByDocumentType(final String documentType) {
		return (Blob) getHibernateTemplate().execute(new HibernateCallback() {
			public Blob doInHibernate(Session session)
					throws HibernateException, SQLException {
				return (Blob) session
						.createSQLQuery(
								"select content from document dm where dm.DOCUMENT_TYPE= :documentType")
								.setParameter("documentType", documentType)
								.list().get(0);
			}
		});
	}
}

