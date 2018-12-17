package tavant.twms.external;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.security.SecurityHelper;
import com.domainlanguage.time.Duration;
import com.domainlanguage.timeutil.Clock;


public class RetryIntegrationService {

	private final Logger logger = Logger
			.getLogger(RetryIntegrationService.class.getName());

	private ClaimService claimService;
	private IntegrationBridge integrationBridge;
	private SecurityHelper securityHelper;
	private int minutesGap;

	@Transactional(readOnly=false)
	public void retryFailedCreditSubmissions() {
		securityHelper.populateIntegrationUser();
		logger.info("Retrying failed credit submissions");
		List<Claim> failedClaims = claimService
				.findClaimsForCreditSubmitRetry();
		logger.info("Total Claims To Retry:" + failedClaims.size());
		for (Claim claim : failedClaims) {
			try {
				boolean sendFlag = Clock.now().minus(Duration.minutes(getMinutesGap())).isAfter(claim.getClaimAudits().get(claim.getClaimAudits().size()-1).getUpdatedOn());
				if(sendFlag){
				    integrationBridge.sendClaim(claim);
		        }
			} catch (RuntimeException e) {
				logger
						.error("Failed retry for claim "
								+ claim.getClaimNumber());
				logger.error(e);
			}
		}
	}

	public ClaimService getClaimService() {
		return claimService;
	}

	public void setClaimService(ClaimService claimService) {
		this.claimService = claimService;
	}

	public IntegrationBridge getIntegrationBridge() {
		return integrationBridge;
	}

	public void setIntegrationBridge(IntegrationBridge integrationBridge) {
		this.integrationBridge = integrationBridge;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public int getMinutesGap() {
		return minutesGap;
	}

	public void setMinutesGap(int minutesGap) {
		this.minutesGap = minutesGap;
	}

}
