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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.engine.jdbc.BlobProxy;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.FileCopyUtils;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

import com.domainlanguage.timeutil.Clock;

/**
 * @author anshul.khare
 * 
 */
public class DocumentServiceImpl extends
		GenericServiceImpl<Document, Long, Exception> implements
		DocumentService {

	protected static Logger logger = LogManager
			.getLogger(DocumentServiceImpl.class);
	private DocumentRepository documentRepository;
	private static final String PARTS_CATALOG_DOWNLOAD = "PartsCatalogDownload";

	/**
	 * @param documentRepository
	 * 
	 */
	@Required
	public void setDocumentRepository(DocumentRepository documentRepository) {
		this.documentRepository = documentRepository;
	}

	@Override
	public GenericRepository<Document, Long> getRepository() {
		return documentRepository;
	}

	public Blob getDocumentContentById(Long id) {
		return this.documentRepository.getDocumentContentById(id);
	}

	public void updateDocument(Document document) {
		this.documentRepository.update(document);
	}
	
	public Blob findContentByDocumentType(String documentType) {
		return documentRepository.findContentByDocumentType(documentType);
	}

	public void updatePartsCatalogDocument() {
		try {
			File file = new File("PartCatalogInfo.zip");
			long fileLength = file.length();
			InputStream partsCatalogStream = new FileInputStream(file);
			Blob partsBlob = BlobProxy.generateProxy(partsCatalogStream, (int)fileLength);
			Document partsCatalogDocument = findByDocumentType(PARTS_CATALOG_DOWNLOAD);
			if (partsCatalogDocument == null) {
				Document document = new Document();
				document.setContent(partsBlob);
				document.setFileName("PartsCatalogInfo.zip");
				document.setContentType("application/zip");
				document.setType(PARTS_CATALOG_DOWNLOAD);
				document.setUploadedOn(Clock.now());
				document.setSize((int) fileLength);
				save(document);
			} else {
				partsCatalogDocument.setContent(partsBlob);
				partsCatalogDocument.setUploadedOn(Clock.now());
				partsCatalogDocument.setSize((int) fileLength);
				update(partsCatalogDocument);
			}

		} catch (Exception e) {
			logger.error("Exception in DocumentServiceImpl.updatePartsCatalogDocument()" +e.getMessage(),e);
		}
	}

	public void downLoadDocument(OutputStream downloadStream){
		Blob content = (Blob) findContentByDocumentType(PARTS_CATALOG_DOWNLOAD);
		try {
			FileCopyUtils.copy(content.getBinaryStream(), downloadStream);
		} catch (IOException e) {
			logger.error("IO Exception in DocumentServiceImpl.downLoadDocument()" +e.getMessage(),e);
		} catch (SQLException e) {
			logger.error("SQL Exception in DocumentServiceImpl.downLoadDocument()" +e.getMessage(),e);
		}

	}
	
	public Document findByDocumentType(String documentType) {
		List<Document> documents = documentRepository
				.findByDocumentType(documentType);
		if (documents != null && !documents.isEmpty()) {
			return documents.get(0);
		}
		return null;
	}

}