package tavant.twms.web.admin.loaScheme;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletResponseAware;

import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.web.i18n.I18nActionSupport;

public class ListAllLOAUsers extends I18nActionSupport implements ServletResponseAware {
	private static Logger logger = LogManager.getLogger(ListAllLOAUsers.class);

	private UserRepository userRepository;
	
	// All LOA Users(They will have Processor role).
	public String allLOAUsers() {
		try {
			List<User> users = userRepository.findProcessorUsersWithNameLike(Role.PROCESSOR,getSearchPrefix());
			return generateAndWriteComboboxJson(users, "id", "name");
		} catch (Exception e) {
			logger.error("Error while generating JSON", e);
			throw new RuntimeException("Error while generating JSON", e);
		}
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	

}
