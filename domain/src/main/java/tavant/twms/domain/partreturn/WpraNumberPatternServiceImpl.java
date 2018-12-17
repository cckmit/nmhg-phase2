
package tavant.twms.domain.partreturn;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;


import tavant.twms.domain.claim.Claim;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

import com.domainlanguage.timeutil.Clock;

public class WpraNumberPatternServiceImpl extends GenericServiceImpl<WpraNumberPattern, Long, Exception> implements WpraNumberPatternService {

	private WpraNumberPatternRepository wpraNumberPatternRepository;
	private WpraRepository wpraRepository;
    private static final Logger logger = Logger.getLogger(WpraNumberPatternServiceImpl.class);


    public void setWpraNumberPatternRepository(
    		WpraNumberPatternRepository wpraNumberPatternRepository) {
		this.wpraNumberPatternRepository = wpraNumberPatternRepository;
	}
	@Override
	public GenericRepository<WpraNumberPattern,Long> getRepository() {
		return this.wpraNumberPatternRepository;
	}
	
	public List<WpraNumberPattern> findAllPatterns() {
	    return this.wpraNumberPatternRepository.findAllWpraNumberPatterns();
	}
	
	public WpraRepository getWpraRepository() {
		return wpraRepository;
	}
	public void setWpraRepository(WpraRepository wpraRepository) {
		this.wpraRepository = wpraRepository;
	}
	public WpraNumberPattern findActivePattern() {
	    return this.wpraNumberPatternRepository.findActivePattern();
	}
	
	
	private String getWpraNumberSequenceName(WpraNumberPattern pattern) {
		String sequenceName = DEFAULT_WPRA_NUMBER_SEQ;
		if(pattern != null && pattern.getSequenceName() != null)
			sequenceName = pattern.getSequenceName();
		return sequenceName;
	}

	public String generateNextWpraNumber(Claim claim,Wpra wpra){
		WpraNumberPattern pattern =findActivePattern();
		String sequenceName = getWpraNumberSequenceName(pattern);
		resetSequenceNameEveryYear(wpra,sequenceName);	
		String nextWpraNumber = this.wpraNumberPatternRepository.getNextGeneratedWpraNumber(sequenceName);
		StringBuffer wpraNumber=new StringBuffer();
		String offsets[]=pattern.getTemplate().split("-");
		SimpleDateFormat formatYear = new SimpleDateFormat("yyyy");
		for (int i = 0; i < offsets.length; i++) {
		//template Format--T-W-YY-NNN
		
		if(offsets[i].contains("Y") ){
			String yearToBeAppended = formatYear.format(wpra.getGenerateDate());
			//String yearToBeAppended = String.valueOf(wpra.getGenerateDate().getYear());
			if(offsets[i].length()==2){
				yearToBeAppended = yearToBeAppended.substring(2);
			}
			wpraNumber.append(yearToBeAppended);
		}
		else if(offsets[i].contains("T")){
			if(claim.getBrand().equals("HYSTER"))
				wpraNumber.append("H");
			else if(claim.getBrand().equals("YALE"))
				wpraNumber.append("Y");
			
		}
		else if(offsets[i].contains("N")){
			
			wpraNumber.append(nextWpraNumber);
		}
		else if(offsets[i].contains("W")){
			wpraNumber.append("W");
		}
			
	  }
		return wpraNumber.toString();
	}

    private void resetSequenceNameEveryYear(Wpra wpra,String sequenceName) {
		
    	//String lastFiledWpraYear=String.valueOf(wpra.getGenerateDate().getYear());
		//if(Clock.today().breachEncapsulationOf_day()==1 && Clock.today().breachEncapsulationOf_month()==1){
			//Wpra lastFiledWpra = this.wpraRepository.findLastFiledWpra();
			//lastFiledWpraYear=String.valueOf(lastFiledWpra.getGenerateDate().getYear());
		//}			
		//if(!(lastFiledWpraYear.equals(String.valueOf(wpra.getGenerateDate().getYear())))){
			this.wpraNumberPatternRepository.resetSequenceName(sequenceName);
		//}
		
	}

    private static String DEFAULT_WPRA_NUMBER_SEQ = "wpra_number_seq";

}
