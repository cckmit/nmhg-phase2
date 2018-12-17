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

import java.io.OutputStream;
import java.sql.Blob;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.infra.GenericService;

/**
 * @author anshul.khare
 * 
 */
@Transactional(readOnly = false)
public interface DocumentService extends
		GenericService<Document, Long, Exception> {
	public void updateDocument(Document document);

	public Blob getDocumentContentById(Long id);

	public Document findByDocumentType(String documentType);

	public void downLoadDocument(OutputStream downloadStream);

	public void updatePartsCatalogDocument();

	public Blob findContentByDocumentType(String documentType);

}