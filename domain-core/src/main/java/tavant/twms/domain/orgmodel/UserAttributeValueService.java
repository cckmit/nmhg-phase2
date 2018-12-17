package tavant.twms.domain.orgmodel;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface UserAttributeValueService {

	@Transactional(readOnly=false)
	void createUserAttributeValue(UserAttributeValue UserAttributeValue);
    
	@Transactional(readOnly=false)
	void updateUserAttributeValue(UserAttributeValue UserAttributeValue);
	
}
