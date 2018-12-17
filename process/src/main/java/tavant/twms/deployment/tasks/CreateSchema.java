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
package tavant.twms.deployment.tasks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hibernate.dialect.Dialect;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

/**
 * @author radhakrishnan.j
 * 
 */
public class CreateSchema extends DefaultTask {

	private static final String CREATE_SCHEMA_FILE = "target/create-schema.sql";

	private String baseDirectory;
	
	public void setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public void perform() {
		try {
			LocalSessionFactoryBean sessionFactory = (LocalSessionFactoryBean) applicationContext
					.getBean("&sessionFactory");
			String[] sqls = getSQLStatementsForSessionFactory(sessionFactory);
			writeSQLStatementsToFile(sqls);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 
	}
	
	String[] getSQLStatementsForSessionFactory(LocalSessionFactoryBean sessionFactory) {
		Dialect dialect = Dialect.getDialect(sessionFactory
				.getConfiguration().getProperties());
		return sessionFactory.getConfiguration()
				.generateSchemaCreationScript(dialect);
	}
	
	void writeSQLStatementsToFile(String[] sqls) throws IOException {
		File file = getSchemaCreationFile();
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		for(String s : sqls) {
			out.write(s);
			out.write(";");
			out.write("\n");
		}
		out.flush();
		out.close();
	}
	
	File getSchemaCreationFile() {
		//create schema file under the base directory
		return new File(baseDirectory, CREATE_SCHEMA_FILE);
	}
}
