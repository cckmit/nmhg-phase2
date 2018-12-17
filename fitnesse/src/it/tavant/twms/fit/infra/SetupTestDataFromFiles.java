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
package tavant.twms.fit.infra;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.excel.XlsDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceUtils;

import tavant.twms.infra.HsqlDataTypeFactory;
import fitnesse.fixtures.TableFixture;

/**
 * @author vineeth.varghese
 * @date Oct 25, 2006
 */
public class SetupTestDataFromFiles extends TableFixture {
	
	private DataSource dataSource;	

	public SetupTestDataFromFiles() {
		ApplicationContextHolder.getApplicationContextHolder().autowireBeanProperties(this);
	}

	/**
	 * @return the dataSource
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(@SuppressWarnings("hiding")
    DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/* (non-Javadoc)
	 * @see fitnesse.fixtures.TableFixture#doStaticTable(int)
	 */
	@Override
	protected void doStaticTable(int rows) {
		String[] dataFiles = new String[rows];		
        for (int i=0; i<rows; i++) {
        	dataFiles[i]=getText(i,0);
        }
        try {
			loadTestData(dataFiles);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
	}
	
	public void loadTestData(String[] dataFiles) throws DatabaseUnitException, SQLException, IOException, 
	        DataSetException {
        Connection jdbcConnection = DataSourceUtils.getConnection(dataSource);
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        // Set the HsqlDataTypeFactory wrapper to accomodate the HSQL BOOLEAN
        // data type
        // in DBUnit.
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                new HsqlDataTypeFactory());
		if (dataFiles.length > 0) {
			DatabaseOperation.CLEAN_INSERT.execute(connection, getDataSet(dataFiles));
		}        
    }
	
	private IDataSet getDataSet(String[] dataFiles) throws IOException, DataSetException {        
        IDataSet dataSets[] = new IDataSet[dataFiles.length];        
        int i = 0;
        for (String fileName : dataFiles) {            
            XlsDataSet xlsDataSet = new XlsDataSet(getResourceAsStream(fileName));
            dataSets[i++] = xlsDataSet;
        }
        return new CompositeDataSet(dataSets);
    }
	
	InputStream getResourceAsStream(String file) throws IOException {
		Resource resource = new ClassPathResource(file);
		return resource.getInputStream();
	}
}
