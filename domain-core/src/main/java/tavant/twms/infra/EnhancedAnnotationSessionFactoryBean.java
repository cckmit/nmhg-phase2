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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate3.HibernateAccessor;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean;

/**
 * @author radhakrishnan.j
 *
 */
public class EnhancedAnnotationSessionFactoryBean extends AnnotationSessionFactoryBean {
	private static Logger logger = LogManager.getLogger(EnhancedAnnotationSessionFactoryBean.class);

	public void setModuleWiseHbmXmls(Map<Integer,Map<Integer,String>> moduleWiseXmls) {
		List<String> mappingResources = new ArrayList<String>();
        List<Integer> parentList = new ArrayList<Integer>(moduleWiseXmls.keySet());
        Collections.sort(parentList); // sorting based on Module
        for (int i = 0; i < parentList.size(); i++) {
            Integer moduleKey = parentList.get(i);
            Map<Integer,String> moduleXmlsMap = moduleWiseXmls.get(moduleKey);
            List<Integer> classesList = new ArrayList<Integer>(moduleXmlsMap.keySet());
            Collections.sort(classesList); // sort as per hbm files
            for (int j = 0; j < classesList.size(); j++) {
                Integer classKey = classesList.get(j);
                String xml = moduleXmlsMap.get(classKey);
                boolean added = mappingResources.add(xml);
				if (added && logger.isDebugEnabled()) {
					logger.debug(" xml -> " + xml);
				}
            }
        }
		setMappingResources(mappingResources.toArray(new String[mappingResources.size()]));
	}

	public void setModuleWiseAnnotatedClasses(Map<Integer,Map<Integer,Class>> moduleWiseClasses) {
		List<Class> annotatedClasses = new ArrayList<Class>();
        List<Integer> parentList = new ArrayList<Integer>(moduleWiseClasses.keySet());
        Collections.sort(parentList); // sorting based on Module
        for (int i = 0; i < parentList.size(); i++) {
            Integer moduleKey = parentList.get(i);
            Map<Integer,Class> moduleClassesMap = moduleWiseClasses.get(moduleKey);
            List<Integer> classesList = new ArrayList<Integer>(moduleClassesMap.keySet());
            Collections.sort(classesList); // sort as per annotated classes
            for (int j = 0; j < classesList.size(); j++) {
                Integer classKey = classesList.get(j);
                Class _class = moduleClassesMap.get(classKey);
                boolean added = annotatedClasses.add(_class);
				if (added && logger.isDebugEnabled()) {
					logger.debug(" class -> " + _class);
				}
            }
        }
		setAnnotatedClasses(annotatedClasses.toArray(new Class[annotatedClasses.size()]));
	}


	public void setModules(String[] modules) throws IOException,ClassNotFoundException {
		List<String> hbmXmlResources = new ArrayList<String>();
		List<Class> annotatedClasses = new ArrayList<Class>();
		Map<String,Class> classNameToClass = new HashMap<String,Class>();
		for(String module : modules ) {
			if( logger.isDebugEnabled() ) {
				logger.debug(" Module -> "+module);
			}
			ClassPathResource classPathResource = new ClassPathResource(module);
			InputStream inputStream = classPathResource.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			LineNumberReader lineNumberReader = new LineNumberReader(bufferedReader);
			String nextLine = null;
			while( (nextLine = lineNumberReader.readLine())!=null ) {
				nextLine = nextLine.trim();
				if( !(nextLine.length()==0 || nextLine.startsWith("#")) ) {
					if( nextLine.endsWith(".hbm.xml")) {
						boolean added = hbmXmlResources.add(nextLine);
						if( added && logger.isDebugEnabled() ) {
							logger.debug("   xml   -> "+nextLine);
						}
					} else if( !classNameToClass.containsKey(nextLine) ) {
						Class<?> newClass = Class.forName(nextLine);
						classNameToClass.put(nextLine, newClass);
						annotatedClasses.add( newClass );
						if( logger.isDebugEnabled() ) {
							logger.debug("   class -> "+nextLine);
						}
					}
				}
			}
		}

		setMappingResources(hbmXmlResources.toArray(new String[hbmXmlResources.size()]));
		setAnnotatedClasses(annotatedClasses.toArray(new Class[annotatedClasses.size()]));
	}

    public String[] generateUpdateSchemaScript() {
        if(logger.isInfoEnabled())
        {
            logger.info("Generating database update schema for Hibernate SessionFactory");
        }

        HibernateTemplate hibernateTemplate = new HibernateTemplate(getSessionFactory());
		hibernateTemplate.setFlushMode(HibernateAccessor.FLUSH_NEVER);

        return (String[]) hibernateTemplate.execute(
			new HibernateCallback() {
				public Object doInHibernate(Session session) throws HibernateException, SQLException {
					Connection con = session.connection();
                    Dialect dialect = Dialect.getDialect(getConfiguration().getProperties());
					DatabaseMetadata metadata = new DatabaseMetadata(con, dialect);
                    return getConfiguration().generateSchemaUpdateScript(dialect, metadata);
				}
			}
		);
    }

}
