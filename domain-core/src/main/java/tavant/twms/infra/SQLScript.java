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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class SQLScript extends JdbcDaoSupport {


    public void execute(Reader script) {
        String[] sqlStatements =extractSqlStatements(script);
        for (int i = 0; i < sqlStatements.length; i++) {
            final String sqlStatement = sqlStatements[i];
            if(this.logger.isInfoEnabled())
            {
                this.logger.info("About to execute statement ["+sqlStatement+"].... ");
            }
            getJdbcTemplate().execute(new StatementCallback(){
                public Object doInStatement(Statement stmt) throws SQLException, DataAccessException {
                    return stmt.executeUpdate(sqlStatement);
                }

            });
            if(this.logger.isInfoEnabled())
            {
                this.logger.info("Done execution");
            }
        }
    }

    @SuppressWarnings({ "unchecked", "unchecked" })
    String[] extractSqlStatements(Reader inputReader) {
        BufferedReader reader = new BufferedReader(inputReader);
        List<String> statements = new ArrayList<String>();
        StringBuffer statement = new StringBuffer(1024 * 100);

        try {
            String line = null;
            boolean aMultiLineStatementYetToEnd = false;
            for (;(line = reader.readLine())!=null;) {
                line = line.trim();

                //Check if this is a SQL comment. if skip line.
                if( isSqlComment(line)) {
                    //Ignore
                } else if (line.length() > 0 && line.charAt(line.length()-1) == '/') {
                    statement.append(line.substring(0,line.length()-1)).append(' ');
                    aMultiLineStatementYetToEnd = true;
                } else {
                    if( aMultiLineStatementYetToEnd ) {
                        statements.add(statement.toString().trim());
                        statement.setLength(0);
                        aMultiLineStatementYetToEnd = false;
                    } else if( line.length()!=0 ){
                        statements.add(line);
                    }
                }
            }

            //Add the last statement too.
            if (statement.length() > 0) {
                statements.add(statement.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return statements.toArray(new String[statements.size()]);
    }

    private boolean isSqlComment(String line) {
        return line.trim().startsWith("--");
    }
}
