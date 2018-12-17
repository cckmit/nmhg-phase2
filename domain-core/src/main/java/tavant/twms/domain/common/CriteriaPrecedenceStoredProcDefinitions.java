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
package tavant.twms.domain.common;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.Mapping;
import org.hibernate.mapping.AuxiliaryDatabaseObject;

/**
 * @author radhakrishnan.j
 *
 */
@SuppressWarnings("serial")
public class CriteriaPrecedenceStoredProcDefinitions implements
		AuxiliaryDatabaseObject {
	private Set<String> classNamesOfSupportedDialects = new HashSet<String>();
	private static Logger logger = LogManager.getLogger(CriteriaPrecedenceStoredProcDefinitions.class);
	
	public void addDialectScope(String dialectName) {
		if (dialectName!=null) {
			classNamesOfSupportedDialects.add(dialectName);
		}		
	}

	public boolean appliesToDialect(Dialect dialect) {
		String dialectClassName = dialect.getClass().getName();
		return dialect!=null && classNamesOfSupportedDialects.contains(dialectClassName);
	}

	public String sqlCreateString(Dialect dialect, Mapping p,
			String defaultCatalog, String defaultSchema)
			throws HibernateException {
		String alias1 = "create alias criteriaRelevanceDealerClaimWarrantyProduct for \""+CriteriaPrecedenceStoredProcDefinitions.class.getName()+".criteriaRelevanceDealerClaimWarrantyProduct\"";
		String alias2 = "create alias criteriaRelevanceDealerWarrantyClaimProduct for \""+CriteriaPrecedenceStoredProcDefinitions.class.getName()+".criteriaRelevanceDealerWarrantyClaimProduct\"";
		StringBuffer buf = new StringBuffer();
		buf.append(alias1).append(";\n").append(alias2);
		return buf.toString();
	}

	public String sqlDropString(Dialect dialect, String defaultCatalog,
			String defaultSchema) {
		String alias1 = "drop alias criteriaRelevanceDealerClaimWarrantyProduct";
		String alias2 = "drop alias criteriaRelevanceDealerWarrantyClaimProduct";
		StringBuffer buf = new StringBuffer();		
		buf.append(alias1).append(";\n").append(alias2);
		return buf.toString();
	}
	
	
	public static int criteriaRelevanceDealerClaimWarrantyProduct(Long dealer,String warrantyType,String claimType,Long productType) {
		int score = 0;
		int weight = 16;
		if( dealer!=null ) {
			score = score + weight;
		}
		
		weight = weight / 2;
		if( claimType!=null ) {
			score = score + weight;
		}

		weight = weight / 2;
		if( warrantyType!=null ) {
			score = score + weight;
		}
		
		
		weight = weight / 2;
		if( productType!=null ) {
			score = score + weight;
		}
		
		if( logger.isDebugEnabled() ) {
			logger.debug(" match score for ("+dealer+","+claimType+","+warrantyType+","+productType+") is ["+score+"]");
		}
		
		return score;
	}
	
	
	public static int criteriaRelevanceDealerWarrantyClaimProduct(Long dealer,String warrantyType,String claimType,Long productType) {
		int score = 0;
		int weight = 16;
		if( dealer!=null ) {
			score = score + weight;
		}
		
		weight = weight / 2;
		if( warrantyType!=null ) {
			score = score + weight;
		}

		weight = weight / 2;
		if( claimType!=null ) {
			score = score + weight;
		}
		
		weight = weight / 2;
		if( productType!=null ) {
			score = score + weight;
		}
		
		if( logger.isDebugEnabled() ) {
			logger.debug(" match score for  ("+dealer+","+warrantyType+","+claimType+","+productType+") is ["+score+"]");
		}		
		return score;
	}	
}
