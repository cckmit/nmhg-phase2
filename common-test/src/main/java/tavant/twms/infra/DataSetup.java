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
package tavant.twms.infra;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceUtils;

/**
 * @author radhakrishnan.j
 *
 */
public class DataSetup implements ResourceLoaderAware,InitializingBean {
	private ResourceLoader resourceLoader;
	
	private List<String> seedDataResources = new ArrayList<String>();
	
	private DataSource dataSource;
	
	private static Logger logger = LogManager.getLogger(DataSetup.class);
	
	private boolean populateData = true;
	
	
	/**
	 * @param populateData the populateData to set
	 */
	public void setPopulateData(boolean populateData) {
		this.populateData = populateData;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	@Required
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @param seedDataResources the seedDataResources to set
	 */
	@Required
	public void setSeedDataResources(List<String> seedDataResources) {
		this.seedDataResources = seedDataResources;
	}

	@Required
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	
	public void afterPropertiesSet() throws Exception {
		if (populateData) {
			Connection jdbcConnection = DataSourceUtils
					.getConnection(dataSource);
			IDatabaseConnection connection = new DatabaseConnection(
					jdbcConnection);
			// Set the HsqlDataTypeFactory wrapper to accomodate the HSQL BOOLEAN
			// data type
			// in DBUnit.
			connection.getConfig().setProperty(
					DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
					new HsqlDataTypeFactory());
			IDataSet dataSets[] = new IDataSet[seedDataResources.size()];
			int i = 0;
			for (String resourceName : seedDataResources) {
				Resource resource = resourceLoader.getResource(resourceName);
				if (logger.isInfoEnabled()) {
					logger.info("Loading data from resource " + resource);
				}
				InputStream resourceAsStream = resource.getInputStream();
				XlsDataSet xlsDataSet = new XlsDataSet(resourceAsStream);
				dataSets[i++] = xlsDataSet;
			}
			CompositeDataSet dataSet = new CompositeDataSet(dataSets);
			DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
		}        
	}
	
	
	
}
