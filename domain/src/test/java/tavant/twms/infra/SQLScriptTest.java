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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;

public class SQLScriptTest extends TestCase {

    public void testExtractSqlStatements() throws Exception {
        SQLScript fixture = new SQLScript();
        ClassPathResource testSqlScript = new ClassPathResource(getClass().getSimpleName()+".sql",getClass());
        InputStream is = testSqlScript.getInputStream();
        InputStreamReader scriptReader = new InputStreamReader(is);
        List<String> extractSqlStatements = Arrays.asList(fixture.extractSqlStatements(scriptReader));
        List<String> expectedStatements = Arrays.asList(new String[]{"delete from some_table where 1=2","delete from another_table where 1=2","delete from some_table"});
        assertEquals(expectedStatements,extractSqlStatements);
    }

}
