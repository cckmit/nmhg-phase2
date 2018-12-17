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
import java.util.List;
import java.util.Set;

import tavant.twms.infra.GenericRepository;

/**
 * @author anshul.khare
 * 
 */
public interface DocumentRepository extends GenericRepository<Document, Long> {
	public List<Document> findPartsInvDocuments(Set<String> properties,
			Document document);

	public Blob getDocumentContentById(final Long id);

	public List<Document> findByDocumentType(String documentType);

	public Blob findContentByDocumentType(final String documentType);

}
