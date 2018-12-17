package tavant.twms.common;

import org.springframework.transaction.annotation.Transactional;

public interface HealthCheckService {
	@Transactional(readOnly = true, timeout = 10)
	public void checkConnectivity();
}
