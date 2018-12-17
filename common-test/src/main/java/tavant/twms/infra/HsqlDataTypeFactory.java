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
package tavant.twms.infra;

import java.sql.Types;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;

/**
 * Wrapper class to accomodate the HSQL BOOLEAN data type when used 
 * with DBUnit. DBUnit doesn't support the HSQL BOOLEAN data type.
 * @author subin.p
 *
 */
public class HsqlDataTypeFactory extends DefaultDataTypeFactory {
	
        @Override
	public DataType createDataType(int sqlType, String sqlTypeName)
			throws DataTypeException {
	    if (sqlType == Types.BOOLEAN){    
	       return DataType.BOOLEAN;
	    }
	    return super.createDataType(sqlType, sqlTypeName);
	}
}