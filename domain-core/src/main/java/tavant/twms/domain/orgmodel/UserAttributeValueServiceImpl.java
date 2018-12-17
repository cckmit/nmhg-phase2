package tavant.twms.domain.orgmodel;

public class UserAttributeValueServiceImpl implements
		UserAttributeValueService {
	private UserAttributeValueRepository UserAttributeValueRepository;
	
	public void createUserAttributeValue(
			UserAttributeValue UserAttributeValue) {
		this.UserAttributeValueRepository.save(UserAttributeValue);
	}

	public void updateUserAttributeValue(
			UserAttributeValue UserAttributeValue) {
        this.UserAttributeValueRepository.update(UserAttributeValue);
	}

	public void setUserAttributeValueRepository(
			UserAttributeValueRepository UserAttributeValueRepository) {
		this.UserAttributeValueRepository = UserAttributeValueRepository;
	}


}
