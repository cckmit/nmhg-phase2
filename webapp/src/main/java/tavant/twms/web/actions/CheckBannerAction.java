package tavant.twms.web.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

@SuppressWarnings("serial")
public class CheckBannerAction extends TwmsActionSupport {
	private static final Logger logger = Logger.getLogger(CheckBannerAction.class);
	private String bannerMsg;
	private JdbcTemplate jdbcTemplate;
	public String execute() {
		getBannerMSg();
		return SUCCESS;
	}
	
	private String getBannerMSg() {		
		String msg = null;
		if (logger.isDebugEnabled()) {
			logger.debug("Checking for any banner msg...");
		}
		jdbcTemplate.execute(new ConnectionCallback() {

			public Object doInConnection(Connection conn) throws SQLException,
					DataAccessException {
				PreparedStatement pStmt = null;
				ResultSet rSet = null;
				// String sqlQry = "select banner_msg, main_start_time, main_end_time from login_banner where main_start_time <= ?+hours_before and main_end_time >= ? and active = 1";
				//Timestamp timestamp = new Timestamp(new Date().getTime());
				
				String sqlQry = "select banner_msg from login_banner where active = 1 and rownum = 1";				
				try {
					pStmt = conn.prepareStatement(sqlQry);
//					pStmt.setTimestamp(1, timestamp);
//					pStmt.setTimestamp(2, timestamp);
					rSet = pStmt.executeQuery();
					if (rSet.next()) {
						//setBannerMsg(rSet.getString(1)+rSet.getTimestamp(2)+" and "+rSet.getTimestamp(3));
						setBannerMsg(rSet.getString(1));
						if (logger.isDebugEnabled()) {
							logger.debug("Banner Msg: "+getBannerMsg());
						}
					}
				} catch (SQLException se) {
					logger.error(se.getMessage());					
				} finally {
					//timestamp = null;
					if (rSet != null) {
						rSet.close();
					}
					if (pStmt != null) {
						pStmt.close();
					}
				}				
				return null;
			}
		});
		return msg;
	}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public String getBannerMsg() {
		return bannerMsg;
	}

	public void setBannerMsg(String bannerMsg) {
		this.bannerMsg = bannerMsg;
	}
	
}
