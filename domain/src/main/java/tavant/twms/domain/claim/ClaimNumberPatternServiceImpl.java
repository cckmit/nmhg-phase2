package tavant.twms.domain.claim;

import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.domainlanguage.timeutil.Clock;

import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class ClaimNumberPatternServiceImpl extends GenericServiceImpl<ClaimNumberPattern, Long, Exception> implements ClaimNumberPatternService {

	private ClaimNumberPatternRepository claimNumberPatternRepository;
	private ClaimRepository claimRepository;
    private static final Logger logger = Logger.getLogger(ClaimNumberPatternServiceImpl.class);


    public void setClaimNumberPatternRepository(
			ClaimNumberPatternRepository claimNumberPatternRepository) {
		this.claimNumberPatternRepository = claimNumberPatternRepository;
	}
	@Override
	public GenericRepository<ClaimNumberPattern,Long> getRepository() {
		return this.claimNumberPatternRepository;
	}
	
	public List<ClaimNumberPattern> findAllPatterns() {
	    return this.claimNumberPatternRepository.findAllClaimNumberPatterns();
	}
	
	public ClaimRepository getClaimRepository() {
		return claimRepository;
	}
	public void setClaimRepository(ClaimRepository claimRepository) {
		this.claimRepository = claimRepository;
	}
	public ClaimNumberPattern findActivePattern() {
	    return this.claimNumberPatternRepository.findActivePattern();
	}
	
	
	private String getClaimNumberSequenceName(ClaimNumberPattern pattern) {
		String sequenceName = DEFAULT_CLAIM_NUMBER_SEQ;
		if(pattern != null && pattern.getSequenceName() != null)
			sequenceName = pattern.getSequenceName();
		return sequenceName;
	}

	public String generateNextClaimNumber(Claim claim){
		ClaimNumberPattern pattern =findActivePattern();
		String sequenceName = getClaimNumberSequenceName(pattern);
		//resetSequenceNameEveryYear(claim,sequenceName);
		String prefix;
		if(claim.getBusinessUnitInfo().getName().equals(AdminConstants.NMHGAMER)) {
			prefix = "FM0000009";
		} else {
			prefix = "FM000009";
		}
		String nextClaimNumber = this.claimNumberPatternRepository.getNextGeneratedClaimNumber(sequenceName, prefix);
		StringBuffer claimNumber=new StringBuffer();
		String offsets[]=pattern.getTemplate().split("-");
		for (int i = 0; i < offsets.length; i++) {
			/*if(offsets[i].contains("Y") ){
				String yearToBeAppended = String.valueOf(claim.getFiledOnDate().breachEncapsulationOf_year());
				if(offsets[i].length()==2){
					yearToBeAppended = yearToBeAppended.substring(2);
				}
				claimNumber.append(yearToBeAppended);
				if(i<offsets.length-1){
					claimNumber.append("-");
				}
			}
			// Added to get claim prefix from Pattern type column value
			else if(offsets[i].contains("PREFIX")){
				claimNumber.append(pattern.getPatternType());
				if(i<offsets.length-1){
					claimNumber.append("-");
				}
			}
			else if(offsets[i].contains("DLR") ){
				claimNumber.append(claim.getForDealer().getId());
				if(i<offsets.length-1){
					claimNumber.append("-");
				}
			}
			else if(offsets[i].contains("BU")){
				claimNumber.append(claim.getBusinessUnitInfo().getName());
				if(i<offsets.length-1){
					claimNumber.append("-");
				}
			}
			else if(offsets[i].contains("N")){
				if(nextClaimNumber == null || nextClaimNumber == 0){
					nextClaimNumber = (long)Math.pow(10, (offsets[i].length() - 1));
				}
				
				claimNumber.append(nextClaimNumber);
				if(i<offsets.length-1){
					claimNumber.append("-");
				}
			}		
		}*/
		//template Format--YY-NNN
		
		if(offsets[i].contains("Y") ){
			String yearToBeAppended = String.valueOf(claim.getFiledOnDate().breachEncapsulationOf_year());
			if(offsets[i].length()==2){
				yearToBeAppended = yearToBeAppended.substring(2);
			}
			claimNumber.append(yearToBeAppended);
		}
		else if(offsets[i].contains("N")){
			/*if(nextClaimNumber == null || nextClaimNumber == 0){
				nextClaimNumber = (long)Math.pow(10, (offsets[i].length() - 1));
			}
			*/
			claimNumber.append(nextClaimNumber);
		}
			
	  }
		return claimNumber.toString();
	}

    private void resetSequenceNameEveryYear(Claim claim,String sequenceName) {
		
    	String lastFiledClaimYear=String.valueOf(claim.getFiledOnDate().breachEncapsulationOf_year());
		if(Clock.today().breachEncapsulationOf_day()==1 && Clock.today().breachEncapsulationOf_month()==1){
			Claim lastFiledClaim = this.claimRepository.findLastFiledClaim();
			lastFiledClaimYear=String.valueOf(lastFiledClaim.getFiledOnDate().breachEncapsulationOf_year());
		}			
		if(!(lastFiledClaimYear.equals(String.valueOf(claim.getFiledOnDate().breachEncapsulationOf_year())))){
			this.claimNumberPatternRepository.resetSequenceName(sequenceName);
		}
		
	}
	public String generateNextRecoveryClaimNumber(RecoveryInfo recoveryInfo) {
        int recoveryClaimCounter = 1;
        for (RecoveryClaimInfo recClaimInfo : recoveryInfo.getReplacedPartsRecovery()) {
            if (recClaimInfo.getRecoveryClaim() != null)
                ++recoveryClaimCounter;
        }
        return recoveryInfo.getWarrantyClaim().getClaimNumber() + "_" + recoveryClaimCounter;
    }

    private static String DEFAULT_CLAIM_NUMBER_SEQ = "claim_number_seq";

}
