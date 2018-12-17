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
package tavant.twms.integration.server.web.action;

import com.opensymphony.xwork2.ActionSupport;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import tavant.twms.integration.server.common.BeanLocator;
import tavant.twms.integration.server.util.PasswordEncoder;
import tavant.twms.integration.server.util.TwmsStringUtil;

public class HomeAction extends ActionSupport implements ServletRequestAware {

    private String login;
    private String password;
    private HttpServletRequest hsr;
    private static final Log logger = LogFactory.getLog("reportingLog"); 

    @Override
    public String execute() throws Exception {
        return SUCCESS;
    }

    @Override
    public void validate() {
        if (hsr.getRequestURI().contains("authenticate")) {
            if (login == null || "".equals(login)) {
                addActionError("Invalid User Login");
            }
            if (password == null || "".equals(password)) {
                addActionError("Invalid Password");
            }
        }
    }

    public String authenticateUser() {
        if (isLoginUserIsValid()) {
            JdbcTemplate jdbctemplate = (JdbcTemplate) new BeanLocator().getBean("jdbcTemplate");
            try {
                String pass = (String) jdbctemplate.execute(new StatementCallback() {

                    public Object doInStatement(Statement stmt) throws SQLException, DataAccessException {
                        ResultSet rs = stmt.executeQuery("select password from org_user where upper(login) = '" + getLogin().toUpperCase() + "'");
                        String ps = null;
                        if(rs.next())
                            ps = rs.getString(1);
                        rs.close();
                        return ps;
                    }
                });
                if(pass != null && !"".equals(pass)){
                    StringTokenizer tokenizer = new StringTokenizer(pass, "|");
                    String encodedPass = tokenizer.nextToken();
                    byte[] salt = TwmsStringUtil.hexStringToBytes(tokenizer.nextToken()) ;
                    if(new PasswordEncoder().isPasswordValid(encodedPass, getPassword(), salt)){
                        hsr.getSession().setAttribute("USER_LOGIN", getLogin());
                        return SUCCESS;
                    }
                }
            } catch (Exception e) {
                logger.error("Error trying to authenticate user : " + getLogin(), e);
            }
        }
        addActionError("Invalid Username/Password !!!");
        return ERROR;
    }

	private boolean isLoginUserIsValid() {
		JdbcTemplate jdbctemplate = (JdbcTemplate) new BeanLocator()
				.getBean("jdbcTemplate");
		try {
			String login = (String) jdbctemplate
					.execute(new StatementCallback() {

						public Object doInStatement(Statement stmt)
								throws SQLException, DataAccessException {
							ResultSet rs = stmt
									.executeQuery("select login from org_user where user_type='INTERNAL' AND upper(login) = '"
											+ getLogin().toUpperCase() + "'");
							String loginUser = null;
							if (rs.next())
								loginUser = rs.getString(1);
							rs.close();
							return loginUser;
						}
					});
			if (login != null && StringUtils.isNotEmpty(login)) {
				return true;
			}
		} catch (Exception e) {
			logger
					.error("Error trying to authenticate user : " + getLogin(),
							e);
		}
		return false;
	}

	public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServletRequest(HttpServletRequest hsr) {
        this.hsr = hsr;
    }

    public String logout() {
        hsr.getSession().invalidate();
        return INPUT;
    }
}
