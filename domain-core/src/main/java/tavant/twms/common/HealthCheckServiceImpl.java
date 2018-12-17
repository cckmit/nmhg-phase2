package tavant.twms.common;

public class HealthCheckServiceImpl implements HealthCheckService {
	HealthCheckRepository healthCheckRepository;

	public HealthCheckRepository getHealthCheckRepository() {
		return healthCheckRepository;
	}

	public void setHealthCheckRepository(
			HealthCheckRepository healthCheckRepository) {
		this.healthCheckRepository = healthCheckRepository;
	}

	public void checkConnectivity() {
		healthCheckRepository.checkConnectivity();
	}
}
